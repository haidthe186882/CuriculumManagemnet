package dal;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBContext {

    public Connection getConnection() throws Exception {
        String url;
        if (instance != null && !instance.trim().isEmpty()) {
            // Use serverName\instance (recommended for named instances like SQLEXPRESS)
            url = "jdbc:sqlserver://" + serverName + "\\" + instance + ";databaseName=" + dbName + ";encrypt=false;trustServerCertificate=true";
        } else {
            // Use serverName:port when instance is not provided
            url = "jdbc:sqlserver://" + serverName + ":" + portNumber + ";databaseName=" + dbName + ";encrypt=false;trustServerCertificate=true";
        }
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(url, userID, password);
    }

    private final String serverName = "localhost";
    private final String dbName = "CurriculumManagementDB";
    private final String portNumber = "1433";
    private final String instance = "";
    private final String userID = "sa";
    private final String password = "123456";

    public static void main(String[] args) {
        try {
            System.out.println(new DBContext().getConnection());
            System.out.println("Connected to CurriculumManagementDB successfully!");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}
