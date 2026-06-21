package util;

import model.Curriculum;
import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExcelHelper {

    // Đảm bảo tên hàm CHÍNH XÁC là parseCurriculumExcel và nhận tham số đầu vào là InputStream
    public static Curriculum parseCurriculumExcel(InputStream is) {
        Curriculum c = new Curriculum();
        try (Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            // 1. Dòng 1 (Hàng 0): CurriculumCode
            Row row0 = sheet.getRow(0);
            if (row0 != null && row0.getCell(0) != null) {
                c.setCurriculumCode(getCellValueAsString(row0.getCell(0)).trim());
            }

            // 2. Dòng 2 (Hàng 1): Name
            Row row1 = sheet.getRow(1);
            if (row1 != null && row1.getCell(0) != null) {
                c.setCurriculumName(getCellValueAsString(row1.getCell(0)).trim());
            }

            // 3. Dòng 3 (Hàng 2): English Name
            Row row2 = sheet.getRow(2);
            if (row2 != null && row2.getCell(0) != null) {
                c.setEnglishName(getCellValueAsString(row2.getCell(0)).trim());
            }

            // 4. Dòng 4 (Hàng 3): Description
            Row row3 = sheet.getRow(3);
            if (row3 != null && row3.getCell(0) != null) {
                c.setDescription(getCellValueAsString(row3.getCell(0)).trim());
            }

            // 5. Dòng 5 (Hàng 4): DecisionNo MM/dd/yyyy
            Row row4 = sheet.getRow(4);
            if (row4 != null && row4.getCell(0) != null) {
                String rawDecision = getCellValueAsString(row4.getCell(0)).trim();
                if (rawDecision.contains("dated")) {
                    String[] parts = rawDecision.split("dated");
                    c.setDecisionNo(parts[0].trim()); 
                    try {
                        String dateStr = parts[1].trim();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        Date parsedDate = sdf.parse(dateStr);
                        c.setDecisionDate(new java.sql.Date(parsedDate.getTime()));
                    } catch (Exception ignored) {}
                } else {
                    c.setDecisionNo(rawDecision);
                }
            }

            // 6. Dòng 6 (Hàng 5): Total Credit
            Row row5 = sheet.getRow(5);
            if (row5 != null && row5.getCell(0) != null) {
                try {
                    Cell cell = row5.getCell(0);
                    if (cell.getCellType() == CellType.NUMERIC) {
                        c.setTotalCredits((int) cell.getNumericCellValue());
                    } else {
                        c.setTotalCredits(Integer.parseInt(getCellValueAsString(cell).trim()));
                    }
                } catch (Exception ex) {
                    c.setTotalCredits(0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC: return String.valueOf((int)cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }
}