package util;

import model.Curriculum;
import model.ProgramLearningOutcome;
import model.Subject;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Parses the standard Curriculum Excel template (3 sheets).
 *
 * Sheet1 — Curriculum info  (rows 0-5: code, name, englishName, description, decisionNo dated MM/dd/yyyy, totalCredits)
 * Sheet2 — PLOs             (rows 1+: col0=index, col1=ploCode, col2=description)
 * Sheet3 — Subjects         (rows 2+: col0=subjectCode, col1=subjectName, col2=semester, col3=noCredit, col4=preRequisite)
 */
public class ExcelHelper {

    // ── Result container ──────────────────────────────────────────────────────

    public static class ImportResult {
        public Curriculum curriculum;
        public List<PloRow> plos = new ArrayList<>();
        public List<SubjectRow> subjects = new ArrayList<>();
    }

    public static class PloRow {
        public String ploCode;
        public String description;
    }

    public static class SubjectRow {
        public String subjectCode;
        public String subjectName;
        public int semesterNo;
        public int credits;
        public String preRequisite;
    }

    // ── Main parse entry (full 3-sheet import) ────────────────────────────────

    public static ImportResult parseFullExcel(InputStream is) throws Exception {
        ImportResult result = new ImportResult();
        try (Workbook wb = WorkbookFactory.create(is)) {
            result.curriculum = parseSheet1(wb.getSheetAt(0));
            if (wb.getNumberOfSheets() > 1) result.plos    = parseSheet2(wb.getSheetAt(1));
            if (wb.getNumberOfSheets() > 2) result.subjects = parseSheet3(wb.getSheetAt(2));
        }
        return result;
    }

    // ── Legacy entry used by existing import-only-info flow ───────────────────

    public static Curriculum parseCurriculumExcel(InputStream is) {
        try (Workbook wb = WorkbookFactory.create(is)) {
            return parseSheet1(wb.getSheetAt(0));
        } catch (Exception e) {
            e.printStackTrace();
            return new Curriculum();
        }
    }

    // ── Sheet parsers ─────────────────────────────────────────────────────────

    private static Curriculum parseSheet1(Sheet sheet) {
        Curriculum c = new Curriculum();
        if (sheet == null) return c;

        // Row 0: CurriculumCode
        c.setCurriculumCode(strCell(sheet, 0, 0));

        // Row 1: Name
        c.setCurriculumName(strCell(sheet, 1, 0));

        // Row 2: English Name
        c.setEnglishName(strCell(sheet, 2, 0));

        // Row 3: Description
        c.setDescription(strCell(sheet, 3, 0));

        // Row 4: DecisionNo dated MM/dd/yyyy
        String rawDecision = strCell(sheet, 4, 0);
        if (rawDecision.contains("dated")) {
            String[] parts = rawDecision.split("dated");
            c.setDecisionNo(parts[0].trim());
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                Date d = sdf.parse(parts[1].trim());
                c.setDecisionDate(new java.sql.Date(d.getTime()));
            } catch (Exception ignored) {}
        } else {
            c.setDecisionNo(rawDecision);
        }

        // Row 5: Total Credits
        Row row5 = sheet.getRow(5);
        if (row5 != null && row5.getCell(0) != null) {
            Cell cell = row5.getCell(0);
            try {
                if (cell.getCellType() == CellType.NUMERIC) {
                    c.setTotalCredits((int) cell.getNumericCellValue());
                } else {
                    c.setTotalCredits(Integer.parseInt(getCellStr(cell).trim()));
                }
            } catch (Exception ex) {
                c.setTotalCredits(0);
            }
        }

        return c;
    }

    private static List<PloRow> parseSheet2(Sheet sheet) {
        List<PloRow> list = new ArrayList<>();
        if (sheet == null) return list;
        // Row 0 is header; data starts at row 1
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String code = strCell(row, 1);  // col1 = PLO Name
            String desc = strCell(row, 2);  // col2 = PLO Description
            if (code.isEmpty() && desc.isEmpty()) continue;
            PloRow pr = new PloRow();
            pr.ploCode = code;
            pr.description = desc;
            list.add(pr);
        }
        return list;
    }

    private static List<SubjectRow> parseSheet3(Sheet sheet) {
        List<SubjectRow> list = new ArrayList<>();
        if (sheet == null) return list;
        // Row 0: "48 subjects, 145 credits" header
        // Row 1: column headers
        // Data starts at row 2
        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String code = strCell(row, 0);
            if (code.isEmpty()) continue;
            SubjectRow sr = new SubjectRow();
            sr.subjectCode = code;
            sr.subjectName = strCell(row, 1);
            sr.semesterNo  = intCell(row, 2);
            sr.credits     = intCell(row, 3);
            sr.preRequisite = strCell(row, 4);
            list.add(sr);
        }
        return list;
    }

    // ── Cell helpers ──────────────────────────────────────────────────────────

    private static String strCell(Sheet sheet, int rowIdx, int colIdx) {
        Row row = sheet.getRow(rowIdx);
        if (row == null) return "";
        return getCellStr(row.getCell(colIdx));
    }

    private static String strCell(Row row, int colIdx) {
        if (row == null) return "";
        return getCellStr(row.getCell(colIdx));
    }

    private static int intCell(Row row, int colIdx) {
        if (row == null) return 0;
        Cell cell = row.getCell(colIdx);
        if (cell == null) return 0;
        try {
            if (cell.getCellType() == CellType.NUMERIC) return (int) cell.getNumericCellValue();
            return Integer.parseInt(getCellStr(cell).trim());
        } catch (Exception e) { return 0; }
    }

    private static String getCellStr(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:  return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default:      return "";
        }
    }
}
