# LTMS — Learning Tutorial Management System

Hệ thống quản lý khung chương trình đào tạo (Curriculum Management) xây dựng bằng **Java Servlet/JSP** + **SQL Server**, deploy trên **Apache Tomcat 10.1**.

## Yêu cầu

- JDK 17+
- Apache Tomcat 10.1 (Jakarta EE 10)
- Microsoft SQL Server (localhost:1433)
- NetBeans 17+ (khuyến nghị) hoặc Ant build

## Cài đặt Database

1. Mở **SQL Server Management Studio**, chạy lần lượt:

```sql
-- Tạo database + bảng
SQLQuery1.sql

-- Nếu DB đã tạo từ bản cũ, chạy thêm patch cột
SQLPatch.sql

-- Dữ liệu mẫu (user, program, curriculum, subject...)
SQLSeedData.sql
```

2. Kiểm tra kết nối trong `src/java/dal/DBContext.java`:

| Thông số | Mặc định |
|----------|----------|
| Server   | localhost |
| Port     | 1433 |
| Database | CurriculumManagementDB |
| User     | sa |
| Password | 123456 |

## Thêm JDBC Driver (bắt buộc)

Tải `mssql-jdbc-12.x.jre11.jar` từ [Microsoft](https://learn.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server) và đặt vào:

```
web/WEB-INF/lib/mssql-jdbc.jar
```

Trong NetBeans: **Project → Properties → Libraries → Add JAR/Folder** → chọn file jar trên.

## Tài khoản demo

Mật khẩu tất cả tài khoản: **123456**

| Email | Role |
|-------|------|
| admin@fpt.edu.vn | Admin |
| reviewer@fpt.edu.vn | Reviewer |
| designer@fpt.edu.vn | Designer |
| lecturer@fpt.edu.vn | Lecturer |
| student@fpt.edu.vn | Student |

## Chạy ứng dụng

### NetBeans
1. Mở project `CurriculumManagement`
2. Cấu hình Tomcat 10.1 trong Services
3. **Run** (F6) — ứng dụng deploy tại `http://localhost:8080/CurriculumManagement`

### Ant (command line)
```bash
ant clean dist
# Copy dist/CurriculumManagement.war vào Tomcat webapps/
```

## Các module đã triển khai

| Module | URL | Mô tả |
|--------|-----|-------|
| Login | `/login` | Đăng nhập theo role |
| Curriculum | `/curriculum/list` | Danh sách, tạo/sửa, chi tiết, submit review |
| Subject | `/subject/list` | Quản lý môn học |
| Syllabus | `/syllabus/list` | Đề cương môn học |
| Review | `/review/list` | Duyệt curriculum (Reviewer) |
| Admin | `/admin/users` | Quản lý user (Admin) |

## Cấu trúc project

```
src/java/
  controller/   ← Servlet (Login, Curriculum, Subject, Syllabus, Review, Admin)
  dao/          ← Data Access Object
  model/        ← Entity classes
  dal/          ← DBContext (kết nối SQL Server)
web/
  WEB-INF/views/  ← JSP views
  index.jsp       ← Redirect → /curriculum/list
```

## Lưu ý

- Guest/Student chỉ xem curriculum **Approved** và **Is_Public = 1**
- Designer tạo curriculum ở trạng thái **Draft**, submit → **Pending**
- Reviewer approve → **Approved**, reject → quay về **Draft**
