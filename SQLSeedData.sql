-- Seed data for LTMS (password for all demo users: 123456)
-- MD5('123456') = e10adc3949ba59abbe56e057f20f883e
-- Ensure database exists
IF DB_ID('CurriculumManagementDB') IS NULL
BEGIN
    CREATE DATABASE CurriculumManagementDB;
END
GO

USE CurriculumManagementDB;
GO

-- Roles
INSERT INTO Roles (Role_Name) VALUES
('Admin'), ('Reviewer'), ('Designer'), ('Lecturer'), ('Student'), ('Guest');
GO

-- Demo users
DECLARE @Pwd NVARCHAR(500) = 'e10adc3949ba59abbe56e057f20f883e';

INSERT INTO Users (User_ID, Role_ID, Full_Name, Email, Password_Hash, Status, Department)
VALUES
('11111111-1111-1111-1111-111111111101', 1, N'Admin User',    'admin@fpt.edu.vn',    @Pwd, 'Active', N'IT'),
('11111111-1111-1111-1111-111111111102', 2, N'Reviewer User', 'reviewer@fpt.edu.vn', @Pwd, 'Active', N'Academic'),
('11111111-1111-1111-1111-111111111103', 3, N'Designer User', 'designer@fpt.edu.vn', @Pwd, 'Active', N'Curriculum'),
('11111111-1111-1111-1111-111111111104', 4, N'Lecturer User', 'lecturer@fpt.edu.vn', @Pwd, 'Active', N'Computing'),
('11111111-1111-1111-1111-111111111105', 5, N'Student User',  'student@fpt.edu.vn',  @Pwd, 'Active', N'SE');
GO

-- Programs
INSERT INTO Programs (Program_ID, Program_Code, Program_Name, Description, Status)
VALUES
('22222222-2222-2222-2222-222222222201', 'SE',  N'Software Engineering',       N'Chương trình Kỹ sư Phần mềm', 'Active'),
('22222222-2222-2222-2222-222222222202', 'BBA', N'Business Administration',  N'Chương trình Quản trị Kinh doanh', 'Active');
GO

-- Subjects
INSERT INTO Subjects (Subject_ID, Subject_Code, Subject_Name, English_Name, Credits, Description, Department, Status)
VALUES
('33333333-3333-3333-3333-333333333301', 'PRJ301', N'Java Web Application', N'Java Web Application', 3, N'Lập trình web với Java Servlet/JSP', N'Computing', 'Active'),
('33333333-3333-3333-3333-333333333302', 'DBI202', N'Introduction to Databases', N'Introduction to Databases', 3, N'Cơ sở dữ liệu cơ bản', N'Computing', 'Active'),
('33333333-3333-3333-3333-333333333303', 'SWP391', N'Software Project', N'Software Project', 5, N'Dự án tốt nghiệp', N'Computing', 'Active');
GO

-- Curriculums
INSERT INTO Curriculums (Curriculum_ID, Program_ID, Created_By, Curriculum_Code, Curriculum_Name, English_Name,
    Description, Total_Credits, Version, Decision_No, Decision_Date, Status, Is_Public, Is_Active)
VALUES
('44444444-4444-4444-4444-444444444401', '22222222-2222-2222-2222-222222222201',
 '11111111-1111-1111-1111-111111111103', 'SE2024', N'Chương trình đào tạo Kỹ sư Phần mềm 2024',
 N'Software Engineering Curriculum 2024', N'Khung chương trình SE năm 2024', 120, '1.0', 'QD-001', '2024-01-15', 'Approved', 1, 1),
('44444444-4444-4444-4444-444444444402', '22222222-2222-2222-2222-222222222201',
 '11111111-1111-1111-1111-111111111103', 'SE2025', N'Chương trình đào tạo Kỹ sư Phần mềm 2025',
 N'Software Engineering Curriculum 2025', N'Khung chương trình SE năm 2025 (bản nháp)', 120, '0.1', NULL, NULL, 'Draft', 0, 1),
('44444444-4444-4444-4444-444444444403', '22222222-2222-2222-2222-222222222201',
 '11111111-1111-1111-1111-111111111103', 'SE2023', N'Chương trình SE 2023',
 N'SE Curriculum 2023', N'Chờ phê duyệt', 120, '1.0', NULL, NULL, 'Pending', 0, 1);
GO

-- Curriculum subjects
INSERT INTO Curriculum_Subjects (Curriculum_Subject_ID, Curriculum_ID, Subject_ID, Semester_No, Is_Mandatory)
VALUES
(NEWID(), '44444444-4444-4444-4444-444444444401', '33333333-3333-3333-3333-333333333302', 1, 1),
(NEWID(), '44444444-4444-4444-4444-444444444401', '33333333-3333-3333-3333-333333333301', 3, 1),
(NEWID(), '44444444-4444-4444-4444-444444444401', '33333333-3333-3333-3333-333333333303', 5, 1);
GO

-- Syllabuses
INSERT INTO Syllabuses (Syllabus_ID, Subject_ID, Syllabus_Name, English_Name, Version, Description,
    Time_Allocation, Scoring_Scale, Min_Avg_Mark_To_Pass, Status, Is_Active)
VALUES
('55555555-5555-5555-5555-555555555501', '33333333-3333-3333-3333-333333333301',
 N'Đề cương PRJ301', N'PRJ301 Syllabus', '1.0', N'Đề cương môn Java Web', N'45h LT + 45h TH', N'10', 5.0, 'Approved', 1),
('55555555-5555-5555-5555-555555555502', '33333333-3333-3333-3333-333333333302',
 N'Đề cương DBI202', N'DBI202 Syllabus', '1.0', N'Đề cương môn CSDL', N'30h LT + 30h TH', N'10', 5.0, 'Approved', 1);
GO

-- Program Objectives & PLOs (sample)
INSERT INTO Program_Objectives (PO_ID, Curriculum_ID, PO_Code, Description)
VALUES ('66666666-6666-6666-6666-666666666601', '44444444-4444-4444-4444-444444444401', 'PO1', N'Áp dụng kiến thức toán học và khoa học');

INSERT INTO Program_Learning_Outcomes (PLO_ID, Curriculum_ID, PLO_Code, Description)
VALUES ('66666666-6666-6666-6666-666666666602', '44444444-4444-4444-4444-444444444401', 'PLO1', N'Phân tích và giải quyết vấn đề kỹ thuật');
GO

PRINT 'Seed data inserted successfully.';
GO
