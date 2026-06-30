package util;

import model.CourseLearningOutcome;
import model.Session;
import model.SyllabusMaterial;
import model.Syllabus;

import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Parse Syllabus Excel file (SWT301 format).
 * Scans sheets for key sections: basic info, CLOs, Sessions, Materials.
 */
public class SyllabusExcelHelper {

    public static String removeAccents(String text) {
        if (text == null) return text;
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").replace("\u0111", "d").replace("\u0110", "D");
    }

    /* ====== wrapper object ====== */
    public static class SyllabusImportData {
        private Syllabus syllabus = new Syllabus();
        private List<CourseLearningOutcome> clos = new ArrayList<>();
        private List<Session> sessions = new ArrayList<>();
        private List<SyllabusMaterial> materials = new ArrayList<>();

        public Syllabus getSyllabus() { return syllabus; }
        public void setSyllabus(Syllabus syllabus) { this.syllabus = syllabus; }
        public List<CourseLearningOutcome> getClos() { return clos; }
        public void setClos(List<CourseLearningOutcome> clos) { this.clos = clos; }
        public List<Session> getSessions() { return sessions; }
        public void setSessions(List<Session> sessions) { this.sessions = sessions; }
        public List<SyllabusMaterial> getMaterials() { return materials; }
        public void setMaterials(List<SyllabusMaterial> materials) { this.materials = materials; }
    }

    /* ====== main entry point ====== */
    public static SyllabusImportData parseSyllabusExcel(InputStream is) {
        SyllabusImportData data = new SyllabusImportData();
        try (Workbook workbook = WorkbookFactory.create(is)) {
            
            // Parse basic info from all sheets (in case it's not on the first sheet)
            for (int sheetIdx = 0; sheetIdx < workbook.getNumberOfSheets(); sheetIdx++) {
                parseBasicInfo(workbook.getSheetAt(sheetIdx), data);
            }

            // Parse other sections
            parseCLOs(workbook, data);
            parseSessions(workbook, data);
            parseMaterials(workbook, data);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /* ====== parse basic syllabus info ====== */
    private static void parseBasicInfo(Sheet sheet, SyllabusImportData data) {
        Syllabus s = data.getSyllabus();

        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            int labelCol = 0;
            String label = removeAccents(cellStr(row, 0)).trim().toLowerCase();
            
            // If col 0 is empty, a number, "no" or "stt", check next columns for the actual label
            if (label.isEmpty() || label.matches("^\\d+$") || label.equals("no") || label.equals("stt")) {
                labelCol = 1;
                label = removeAccents(cellStr(row, 1)).trim().toLowerCase();
                if (label.isEmpty() || label.matches("^\\d+$")) {
                    labelCol = 2;
                    label = removeAccents(cellStr(row, 2)).trim().toLowerCase();
                }
            }
            if (label.isEmpty()) continue;

            // Try label:value pattern
            if (label.contains("subject") && !label.contains("pre")) {
                // Could be "Subject Code" or "Subject"
                String v = cellStr(row, labelCol + 1).trim();
                if (v.isEmpty()) v = cellStr(row, labelCol + 2).trim();
                // Don't set subjectId here – just name for reference
            }
            if (label.contains("syllabus name") || label.contains("ten de cuong") || label.contains("course name")) {
                s.setSyllabusName(getValueFromRow(row, labelCol));
            }
            if (label.contains("english name") || label.contains("ten tieng anh")) {
                s.setEnglishName(getValueFromRow(row, labelCol));
            }
            if (label.contains("version") || label.contains("phien ban")) {
                s.setVersion(getValueFromRow(row, labelCol));
            }
            if (label.contains("description") || label.contains("mo ta")) {
                s.setDescription(getValueFromRow(row, labelCol));
            }
            if (label.contains("time allocation") || label.contains("phan bo thoi gian")) {
                s.setTimeAllocation(getValueFromRow(row, labelCol));
            }
            if (label.contains("student task") || label.contains("student's task") || (label.contains("student") && label.contains("task")) || label.contains("nhiem vu sinh vien")) {
                s.setStudentTasks(getValueFromRow(row, labelCol));
            }
            if ((label.contains("tool") || label.contains("material")) && !label.contains("scoring")) {
                s.setTools(getValueFromRow(row, labelCol));
            }
            if (label.contains("scoring") || label.contains("thang diem") || label.contains("assessment")) {
                s.setScoringScale(getValueFromRow(row, labelCol));
            }
            if (label.contains("min") && label.contains("pass")) {
                String val = getValueFromRow(row, labelCol);
                try { s.setMinAvgMarkToPass(Double.parseDouble(val)); } catch (Exception ignored) {}
            }
            if (label.contains("decision") || label.contains("quyết định")) {
                s.setDecisionNo(getValueFromRow(row, labelCol));
            }
            if (label.contains("approved") || label.contains("ngày duyệt") || label.contains("approved date")) {
                String val = getValueFromRow(row, labelCol);
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    Date d = sdf.parse(val);
                    s.setApprovedDate(new java.sql.Date(d.getTime()));
                } catch (Exception ignored) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date d = sdf.parse(val);
                        s.setApprovedDate(new java.sql.Date(d.getTime()));
                    } catch (Exception ignored2) {}
                }
            }
        }

        // Fallback: if syllabusName is empty, try first non-empty cell as name
        if (s.getSyllabusName() == null || s.getSyllabusName().isEmpty()) {
            for (int i = 0; i <= Math.min(5, sheet.getLastRowNum()); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String val = cellStr(row, 0).trim();
                if (!val.isEmpty() && val.length() > 3) {
                    s.setSyllabusName(val);
                    break;
                }
            }
        }
    }

    /* ====== parse CLOs section ====== */
    private static void parseCLOs(Workbook workbook, SyllabusImportData data) {
        // Look for CLO section in any sheet
        for (int sheetIdx = 0; sheetIdx < workbook.getNumberOfSheets(); sheetIdx++) {
            Sheet sheet = workbook.getSheetAt(sheetIdx);

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Check if any cell starts with LO/CLO followed by a number
                String code = "";
                String desc = "";
                for (int c = 0; c < row.getLastCellNum(); c++) {
                    String cv = cellStr(row, c).trim();
                    if (cv.toUpperCase().matches("^(C?)LO\\s*\\d+.*")) {
                        code = cv;
                        desc = cellStr(row, c + 1).trim();
                        if (desc.isEmpty() && c + 2 < row.getLastCellNum()) {
                            desc = cellStr(row, c + 2).trim();
                        }
                        break;
                    }
                }

                if (!code.isEmpty()) {
                    CourseLearningOutcome clo = new CourseLearningOutcome();
                    clo.setCloCode(code);
                    clo.setDescription(desc);
                    data.getClos().add(clo);
                }
            }
            if (!data.getClos().isEmpty()) {
                return; // Found in this sheet, no need to check others
            }
        }
    }

    /* ====== parse Sessions section ====== */
    private static void parseSessions(Workbook workbook, SyllabusImportData data) {
        for (int sheetIdx = 0; sheetIdx < workbook.getNumberOfSheets(); sheetIdx++) {
            Sheet sheet = workbook.getSheetAt(sheetIdx);
            String sheetName = sheet.getSheetName().toLowerCase();

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Find Session header row
                boolean isSessionHeader = false;
                int sessionCol = -1, topicCol = -1, typeCol = -1, loCol = -1, ituCol = -1;
                int matCol = -1, taskCol = -1, urlCol = -1;

                for (int c = 0; c < row.getLastCellNum(); c++) {
                    String cv = cellStr(row, c).trim().toLowerCase();
                    if (cv.contains("session") && (cv.contains("no") || cv.contains("#") || cv.equals("session"))) {
                        sessionCol = c;
                        isSessionHeader = true;
                    }
                    if (cv.contains("topic") || cv.contains("chu de")) topicCol = c;
                    if (cv.contains("learning") && cv.contains("teaching") || cv.contains("type")) typeCol = c;
                    if (cv.equals("lo") || cv.contains("learning outcome")) loCol = c;
                    if (cv.contains("itu") || cv.contains("i/t/u")) ituCol = c;
                    if (cv.contains("student material") || cv.contains("material")) matCol = c;
                    if (cv.contains("student task") || cv.contains("task")) taskCol = c;
                    if (cv.contains("url") || cv.contains("link")) urlCol = c;
                }

                if (isSessionHeader && topicCol >= 0) {
                    // Parse session data rows
                    for (int j = i + 1; j <= sheet.getLastRowNum(); j++) {
                        Row dataRow = sheet.getRow(j);
                        if (dataRow == null) continue;

                        String sessionNoStr = cellStr(dataRow, sessionCol).trim();
                        String topic = topicCol >= 0 ? cellStr(dataRow, topicCol).trim() : "";

                        if (sessionNoStr.isEmpty() && topic.isEmpty()) break;

                        Session session = new Session();
                        try {
                            session.setSessionNo(Integer.parseInt(sessionNoStr.replaceAll("[^0-9]", "")));
                        } catch (Exception e) {
                            session.setSessionNo(data.getSessions().size() + 1);
                        }
                        session.setTopic(topic);
                        if (typeCol >= 0) session.setLearningTeachingType(cellStr(dataRow, typeCol).trim());
                        if (loCol >= 0) session.setLo(cellStr(dataRow, loCol).trim());
                        if (ituCol >= 0) session.setItu(cellStr(dataRow, ituCol).trim());
                        if (matCol >= 0) session.setStudentMaterials(cellStr(dataRow, matCol).trim());
                        if (taskCol >= 0) session.setStudentTasks(cellStr(dataRow, taskCol).trim());
                        if (urlCol >= 0) session.setUrls(cellStr(dataRow, urlCol).trim());

                        data.getSessions().add(session);
                    }
                    return;
                }
            }
        }
    }

    /* ====== parse Materials section ====== */
    private static void parseMaterials(Workbook workbook, SyllabusImportData data) {
        for (int sheetIdx = 0; sheetIdx < workbook.getNumberOfSheets(); sheetIdx++) {
            Sheet sheet = workbook.getSheetAt(sheetIdx);

            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // Find Materials header row
                boolean isMaterialHeader = false;
                int descCol = -1, authorCol = -1, pubCol = -1, dateCol = -1;
                int editionCol = -1, isbnCol = -1, mainCol = -1, hardCol = -1;
                int onlineCol = -1, linkCol = -1, notesCol = -1;

                for (int c = 0; c < row.getLastCellNum(); c++) {
                    String cv = cellStr(row, c).trim().toLowerCase();
                    if (cv.equals("description") || cv.equals("mô tả") || (cv.contains("material") && (cv.contains("description") || cv.contains("name")))) descCol = c;
                    if (cv.equals("author") || cv.contains("tác giả")) authorCol = c;
                    if (cv.equals("publisher") || cv.contains("nhà xuất bản")) pubCol = c;
                    if (cv.equals("publisheddate") || cv.equals("published date") || cv.contains("published") || (cv.contains("date") && cv.contains("publish"))) dateCol = c;
                    if (cv.equals("edition") || cv.contains("ấn bản")) editionCol = c;
                    if (cv.equals("isbn")) isbnCol = c;
                    if (cv.equals("ismainmaterial") || cv.equals("is main material") || cv.contains("main")) mainCol = c;
                    if (cv.equals("ishardcopy") || cv.equals("is hard copy") || cv.contains("hard")) hardCol = c;
                    if (cv.equals("isonline") || cv.equals("is online") || cv.contains("online")) onlineCol = c;
                    if (cv.equals("link") || cv.contains("url")) linkCol = c;
                    if (cv.equals("note") || cv.equals("notes") || cv.contains("ghi chú")) notesCol = c;
                }

                if (descCol >= 0 && (authorCol >= 0 || pubCol >= 0 || mainCol >= 0 || isbnCol >= 0)) {
                    isMaterialHeader = true;
                }

                if (isMaterialHeader) {
                    for (int j = i + 1; j <= sheet.getLastRowNum(); j++) {
                        Row dataRow = sheet.getRow(j);
                        if (dataRow == null) continue;

                        String desc = descCol >= 0 ? cellStr(dataRow, descCol).trim() : "";
                        if (desc.isEmpty()) break;

                        SyllabusMaterial m = new SyllabusMaterial();
                        m.setMaterialDescription(desc);
                        if (authorCol >= 0) m.setAuthor(cellStr(dataRow, authorCol).trim());
                        if (pubCol >= 0) m.setPublisher(cellStr(dataRow, pubCol).trim());
                        if (dateCol >= 0) {
                            try {
                                Cell cell = dataRow.getCell(dateCol);
                                if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                                    m.setPublishedDate(cell.getDateCellValue());
                                }
                            } catch (Exception ignored) {}
                        }
                        if (editionCol >= 0) m.setEdition(cellStr(dataRow, editionCol).trim());
                        if (isbnCol >= 0) m.setIsbn(cellStr(dataRow, isbnCol).trim());
                        if (mainCol >= 0) m.setMainMaterial(isTruthy(cellStr(dataRow, mainCol)));
                        if (hardCol >= 0) m.setHardCopy(isTruthy(cellStr(dataRow, hardCol)));
                        if (onlineCol >= 0) m.setOnline(isTruthy(cellStr(dataRow, onlineCol)));
                        if (linkCol >= 0) m.setLink(cellStr(dataRow, linkCol).trim());
                        if (notesCol >= 0) m.setNotes(cellStr(dataRow, notesCol).trim());

                        data.getMaterials().add(m);
                    }
                    return;
                }
            }
        }
    }

    /* ====== helpers ====== */
    private static String getValueFromRow(Row row, int labelCol) {
        // Return value from the column after the label (or concatenate remaining columns)
        StringBuilder sb = new StringBuilder();
        for (int c = labelCol + 1; c < row.getLastCellNum(); c++) {
            String v = cellStr(row, c).trim();
            if (!v.isEmpty()) {
                if (sb.length() > 0) sb.append(" ");
                sb.append(v);
            }
        }
        return sb.toString();
    }

    private static String cellStr(Row row, int col) {
        if (row == null || col < 0) return "";
        Cell cell = row.getCell(col);
        if (cell == null) return "";
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        return sdf.format(cell.getDateCellValue());
                    }
                    double d = cell.getNumericCellValue();
                    if (d == Math.floor(d) && !Double.isInfinite(d)) {
                        return String.valueOf((long) d);
                    }
                    return String.valueOf(d);
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return cell.getStringCellValue();
                    } catch (Exception e) {
                        try { return String.valueOf(cell.getNumericCellValue()); }
                        catch (Exception e2) { return ""; }
                    }
                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    private static boolean isTruthy(String val) {
        if (val == null) return false;
        val = val.trim().toLowerCase();
        return val.equals("1") || val.equals("true") || val.equals("yes")
                || val.equals("x") || val.equals("✓") || val.equals("có");
    }
}
