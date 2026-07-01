package util;

import model.Curriculum;
import model.ProgramLearningOutcome;
import model.Subject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Parses the standard Curriculum Excel template.
 * Sheets are looked up BY NAME (case-insensitive) so sheet order in the file
 * doesn't matter; falls back to position (Sheet1/Sheet2/Sheet3) for older
 * 3-sheet templates that don't use named tabs.
 *
 * "Intro" (or Sheet1) — Curriculum info (rows 0-7):
 *   row0 = CurriculumCode
 *   row1 = CurriculumName
 *   row2 = EnglishName
 *   row3 = Description
 *   row4 = DecisionNo                (plain text, e.g. "577/QĐ-ĐHFPT")
 *          (legacy templates may instead embed "<no> dated MM/dd/yyyy" here)
 *   row5 = DecisionDate               (real date cell, e.g. 15/05/2026)
 *   row6 = TotalCredits               (number, e.g. 145)
 *   row7 = Version                    (text/number, e.g. "1" or "K20")
 * "PLO" (or Sheet2)    — rows 1+: col0=index, col1=ploCode, col2=description
 * "PO"                 — rows 1+: col0=index, col1=poCode, col2=description
 * "Mapping"            — row0 header "PLO(s)", "PO1".."POn"; rows 1+ "PLOx" + "✓" marks
 * "Subject" (or Sheet3)— rows 2+: col0=subjectCode, col1=subjectName, col2=semester, col3=noCredit, col4=preRequisite
 */
public class ExcelHelper {

    // ── Result container ──────────────────────────────────────────────────────

    public static class ImportResult {
        public Curriculum curriculum;
        public List<PloRow> plos = new ArrayList<>();
        public List<PoRow> pos = new ArrayList<>();
        public List<String[]> mappingPairs = new ArrayList<>(); // [poCode, ploCode]
        public List<SubjectRow> subjects = new ArrayList<>();
    }

    public static class PloRow {
        public String ploCode;
        public String description;
        public String getPloCode() { return ploCode; }
        public String getDescription() { return description; }
    }

    public static class PoRow {
        public String poCode;
        public String description;
        public String getPoCode() { return poCode; }
        public String getDescription() { return description; }
    }

    public static class SubjectRow {
        public String subjectCode;
        public String subjectName;
        public int semesterNo;
        public int credits;
        public String preRequisite;
        public String getSubjectCode() { return subjectCode; }
        public String getSubjectName() { return subjectName; }
        public int getSemesterNo() { return semesterNo; }
        public int getCredits() { return credits; }
        public String getPreRequisite() { return preRequisite; }
    }

    // ── Main parse entry (full multi-sheet import) ────────────────────────────

    public static ImportResult parseFullExcel(InputStream is) throws Exception {
        ImportResult result = new ImportResult();
        try (Workbook wb = WorkbookFactory.create(is)) {
            // Sheet template hiện tại đặt tên rõ ràng: Intro, PLO, PO, Mapping, Subject.
            // Tra theo TÊN trước (đúng với file CurrDetail.xlsx thật); nếu không thấy
            // tên nào khớp thì fallback theo VỊ TRÍ để tương thích file mẫu cũ
            // (Sheet1=Intro, Sheet2=PLO, Sheet3=Subject — không có PO/Mapping).
            Sheet introSheet = findSheet(wb, "Intro", "Sheet1");
            Sheet ploSheet = findSheet(wb, "PLO", "Sheet2");
            Sheet poSheet = findSheet(wb, "PO");
            Sheet mappingSheet = findSheet(wb, "Mapping");
            Sheet subjectSheet = findSheet(wb, "Subject", "Sheet3");

            if (introSheet == null) introSheet = wb.getNumberOfSheets() > 0 ? wb.getSheetAt(0) : null;
            result.curriculum = parseSheet1(introSheet);

            if (ploSheet != null) result.plos = parsePloSheet(ploSheet);
            if (poSheet != null) result.pos = parsePoSheet(poSheet);
            if (mappingSheet != null) result.mappingPairs = parseMappingSheet(mappingSheet);
            if (subjectSheet != null) result.subjects = parseSubjectSheet(subjectSheet);
        }
        return result;
    }

    private static Sheet findSheet(Workbook wb, String... namesInPreferenceOrder) {
        for (String name : namesInPreferenceOrder) {
            Sheet s = wb.getSheet(name);
            if (s != null) return s;
        }
        // Case-insensitive scan as a last resort
        for (String name : namesInPreferenceOrder) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                if (wb.getSheetName(i).equalsIgnoreCase(name)) return wb.getSheetAt(i);
            }
        }
        return null;
    }

    // ── Legacy entry used by existing import-only-info flow ───────────────────

    public static Curriculum parseCurriculumExcel(InputStream is) {
        try (Workbook wb = WorkbookFactory.create(is)) {
            Sheet introSheet = findSheet(wb, "Intro", "Sheet1");
            if (introSheet == null) introSheet = wb.getNumberOfSheets() > 0 ? wb.getSheetAt(0) : null;
            return parseSheet1(introSheet);
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

        // Row 4: DecisionNo — plain text, e.g. "577/QĐ-ĐHFPT"
        // (kept backward-compatible with legacy "<no> dated MM/dd/yyyy" single-cell format)
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

        // Row 5: Decision Date — real date cell (e.g. 15/05/2026)
        Date decisionDate = dateCell(sheet, 5, 0);
        if (decisionDate != null) {
            c.setDecisionDate(new java.sql.Date(decisionDate.getTime()));
        }

        // Row 6: Total Credits
        c.setTotalCredits(intCellAt(sheet, 6, 0));

        // Row 7: Version
        String version = strCell(sheet, 7, 0);
        if (!version.isEmpty()) {
            c.setVersion(version);
        }

        return c;
    }

    private static List<PloRow> parsePloSheet(Sheet sheet) {
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

    private static List<PoRow> parsePoSheet(Sheet sheet) {
        List<PoRow> list = new ArrayList<>();
        if (sheet == null) return list;
        // Row 0 is header; data starts at row 1
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String code = strCell(row, 1);  // col1 = PO Name
            String desc = strCell(row, 2);  // col2 = PO Description
            if (code.isEmpty() && desc.isEmpty()) continue;
            PoRow pr = new PoRow();
            pr.poCode = code;
            pr.description = desc;
            list.add(pr);
        }
        return list;
    }

    /**
     * Mapping sheet layout:
     *   row0 = header: "PLO(s)", "PO1", "PO2", ...
     *   row1+ = "PLOx", "✓"/blank, "✓"/blank, ...
     * Returns a list of [poCode, ploCode] pairs for every checked cell.
     */
    private static List<String[]> parseMappingSheet(Sheet sheet) {
        List<String[]> pairs = new ArrayList<>();
        if (sheet == null) return pairs;
        Row header = sheet.getRow(0);
        if (header == null) return pairs;

        int lastCol = header.getLastCellNum();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String ploCode = strCell(row, 0);
            if (ploCode.isEmpty()) continue;
            for (int col = 1; col < lastCol; col++) {
                String mark = strCell(row, col);
                if (mark.isEmpty()) continue;
                String poCode = strCell(header, col);
                if (poCode.isEmpty()) continue;
                pairs.add(new String[]{poCode, ploCode});
            }
        }
        return pairs;
    }

    private static List<SubjectRow> parseSubjectSheet(Sheet sheet) {
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

    /**
     * Reads a cell expected to hold a real Excel date value (DATE_TIME cell type).
     * Falls back to parsing a dd/MM/yyyy or MM/dd/yyyy text string if the cell is
     * stored as plain text instead of a date, for robustness against manual edits.
     */
    private static Date dateCell(Sheet sheet, int rowIdx, int colIdx) {
        Row row = sheet.getRow(rowIdx);
        if (row == null) return null;
        Cell cell = row.getCell(colIdx);
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            }
            String raw = getCellStr(cell).trim();
            if (raw.isEmpty()) return null;
            for (String pattern : new String[]{"dd/MM/yyyy", "MM/dd/yyyy", "yyyy-MM-dd"}) {
                try {
                    return new SimpleDateFormat(pattern).parse(raw);
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Reads a numeric cell (Total Credits, etc.) located at sheet/row/col directly,
     * tolerant of the value being stored as text.
     */
    private static int intCellAt(Sheet sheet, int rowIdx, int colIdx) {
        Row row = sheet.getRow(rowIdx);
        if (row == null) return 0;
        Cell cell = row.getCell(colIdx);
        if (cell == null) return 0;
        try {
            if (cell.getCellType() == CellType.NUMERIC) return (int) cell.getNumericCellValue();
            return Integer.parseInt(getCellStr(cell).trim());
        } catch (Exception e) {
            return 0;
        }
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