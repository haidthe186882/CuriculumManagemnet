package util;

import model.User;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Parse users from an Excel file.
 *
 * Expected sheet: first sheet (index 0)
 * Expected columns in row 1 (header row, index 0):
 * - Full_Name
 * - Email
 * - Role
 * - Is_Reviewer (optional: values like true/false, yes/no, on/off)
 * - Is_Designer (optional: values like true/false, yes/no, on/off)
 */
public class UserExcelHelper {

    public static class ParseResult {
        private final List<User> users;
        private final List<String> errors;

        public ParseResult(List<User> users, List<String> errors) {
            this.users = users;
            this.errors = errors;
        }

        public List<User> getUsers() {
            return users;
        }

        public List<String> getErrors() {
            return errors;
        }
    }

    public static ParseResult parseUsersExcel(InputStream is) {
        List<User> users = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                errors.add("Excel has no sheets");
                return new ParseResult(users, errors);
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                errors.add("Missing header row (row 1)");
                return new ParseResult(users, errors);
            }

            int lastCell = headerRow.getLastCellNum();
            java.util.Map<String, Integer> colIndex = new java.util.HashMap<>();
            for (int c = 0; c < lastCell; c++) {
                Cell cell = headerRow.getCell(c);
                String name = cell == null ? "" : getString(cell).trim();
                if (name != null && !name.isEmpty()) {
                    colIndex.put(name, c);
                }
            }

            // Cho phép 2 dạng header Full_Name hoặc Name (theo template bạn đang dùng)
            int fullNameCol = -1;
            if (colIndex.containsKey("Full_Name")) fullNameCol = colIndex.get("Full_Name");
            else if (colIndex.containsKey("Name")) fullNameCol = colIndex.get("Name");

            if (!colIndex.containsKey("Email")) errors.add("Missing required column: Email");
            if (!colIndex.containsKey("Role")) errors.add("Missing required column: Role");
            if (fullNameCol == -1) errors.add("Missing required column: Full_Name (or Name)");

            if (!errors.isEmpty()) {
                return new ParseResult(users, errors);
            }

            if (!errors.isEmpty()) {
                return new ParseResult(users, errors);
            }

            boolean hasIsReviewer = colIndex.containsKey("Is_Reviewer");
            boolean hasIsDesigner = colIndex.containsKey("Is_Designer");

            int lastRow = sheet.getLastRowNum();
            for (int r = 1; r <= lastRow; r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                String fullName = getCell(row, colIndex.get("Full_Name"));
                String email = getCell(row, colIndex.get("Email"));
                String roleName = getCell(row, colIndex.get("Role"));

                if (isBlank(fullName) && isBlank(email) && isBlank(roleName)) {
                    continue; // skip empty row
                }

                int rowNoHuman = r + 1;
                if (isBlank(fullName)) {
                    errors.add("Row " + rowNoHuman + ": Full_Name is required");
                    continue;
                }
                if (isBlank(email)) {
                    errors.add("Row " + rowNoHuman + ": Email is required");
                    continue;
                }
                if (isBlank(roleName)) {
                    errors.add("Row " + rowNoHuman + ": Role is required");
                    continue;
                }

                User u = new User();
                u.setFullName(fullName);
                u.setEmail(email);

                // Store role mapping using name logic in servlet/DAO.
                // We reuse existing User model flags for extra roles.
                // u.setRoleId() will be filled later by servlet if you want.

                // Role main: map directly into reviewer/designer flags if primary role is those.
                String roleNormalized = roleName.trim();

                // Default: no extra roles unless explicitly set in columns.
                boolean isReviewer = hasIsReviewer && parseBoolean(getCell(row, colIndex.get("Is_Reviewer")));
                boolean isDesigner = hasIsDesigner && parseBoolean(getCell(row, colIndex.get("Is_Designer")));

                // If primary role is Reviewer/Designer, force respective flag and clear the other.
                if ("Reviewer".equalsIgnoreCase(roleNormalized)) {
                    isReviewer = true;
                    isDesigner = false;
                } else if ("Designer".equalsIgnoreCase(roleNormalized)) {
                    isDesigner = true;
                    isReviewer = false;
                } else if ("Student".equalsIgnoreCase(roleNormalized)) {
                    // Student can't be reviewer/designer.
                    isReviewer = false;
                    isDesigner = false;
                }

                u.setReviewer(isReviewer);
                u.setDesigner(isDesigner);

                // Store primary role name for servlet to resolve Role_ID.
                // (Using phoneNumber field as a transport because it is not used in import/addUser.)
                u.setPhoneNumber(roleNormalized);

                users.add(u);
            }
        } catch (Exception e) {
            errors.add("Failed to parse excel: " + e.getMessage());
        }

        return new ParseResult(users, errors);
    }

    private static boolean parseBoolean(String v) {
        if (v == null) return false;
        String s = v.trim().toLowerCase();
        if (s.isEmpty()) return false;
        return s.equals("true") || s.equals("1") || s.equals("yes") || s.equals("y") || s.equals("on") || s.equals("checked");
    }

    private static String getCell(Row row, int col) {
        if (col < 0) return "";
        Cell cell = row.getCell(col);
        return getString(cell);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String getString(Cell cell) {
        if (cell == null) return "";
        try {
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                case NUMERIC -> {
                    // Avoid scientific notation; Excel IDs are usually text.
                    double d = cell.getNumericCellValue();
                    if (Math.floor(d) == d) yield String.valueOf((long) d);
                    yield String.valueOf(d);
                }
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                case FORMULA -> cell.getCellFormula();
                default -> "";
            };
        } catch (Exception ignored) {
            return "";
        }
    }
}

