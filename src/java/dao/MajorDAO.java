package dao;

import dal.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Major;

public class MajorDAO {

    public List<Major> getAllMajors() {
        List<Major> list = new ArrayList<>();

        // Kế thừa giải pháp CAST UNIQUEIDENTIFIER của HEAD để tránh lỗi Driver và sắp xếp theo tên của main
        String sql = """
                     SELECT CAST(Major_ID AS VARCHAR(36)) AS Major_ID_Str,
                            Major_Code,
                            Major_Name,
                            Description,
                            Is_Active
                     FROM Majors
                     WHERE Is_Active = 1
                     ORDER BY Major_Name
                     """;

        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Major m = new Major();
                m.setMajorId(rs.getString("Major_ID_Str"));
                m.setMajorCode(rs.getString("Major_Code"));
                m.setMajorName(rs.getString("Major_Name"));
                m.setDescription(rs.getString("Description"));
                
                // Tích hợp logic đọc Status dạng String từ nhánh main một cách an toàn
                // Đọc giá trị dạng boolean từ Database một cách an toàn
            try {
                // rs.getBoolean() tự động chuyển đổi 1 thành true, 0 thành false
                m.setIsActive(rs.getBoolean("Is_Active")); 
            } catch (SQLException ignored) {
                ignored.getSQLState();
            }
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
