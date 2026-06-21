package dao;

import dal.DBContext;
import model.Major;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MajorDAO {

    public List<Major> getAllMajors() {
        List<Major> list = new ArrayList<>();

        // Ép kiểu UNIQUEIDENTIFIER thành VARCHAR(36) để Java nhận diện mượt mà
        String sql = """
                     SELECT CAST(Major_ID AS VARCHAR(36)) AS Major_ID_Str,
                            Major_Code,
                            Major_Name,
                            Description
                     FROM Majors
                     WHERE Is_Active = 1
                     ORDER BY Major_Code
                     """;

        try (Connection con = new DBContext().getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Major m = new Major();
                
                // Đọc chuỗi ID đã được chuyển đổi an toàn
                m.setMajorId(rs.getString("Major_ID_Str"));
                m.setMajorCode(rs.getString("Major_Code"));
                m.setMajorName(rs.getString("Major_Name"));
                m.setDescription(rs.getString("Description"));
                
                list.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}