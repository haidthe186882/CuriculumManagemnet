/* =========================================================
   SAMPLE DATA - CurriculumManagementDB
   Quy uoc: moi Role co 1 nguoi dung (5 role -> 5 user)
   Mat khau mau: "123456", duoc ma hoa MD5 bang HASHBYTES
   khi luu vao cot Password_Hash (giong luc dang nhap thuc te
   he thong se HASHBYTES('MD5', input_password) roi so sanh
   voi Password_Hash trong bang Users)
========================================================= */

USE CurriculumManagementDB;
GO

/* ============ BIEN GUID DE LIEN KET DU LIEU ============ */
DECLARE @Admin_ID    UNIQUEIDENTIFIER = NEWID();
DECLARE @Designer_ID UNIQUEIDENTIFIER = NEWID();
DECLARE @Reviewer_ID UNIQUEIDENTIFIER = NEWID();
DECLARE @Teacher_ID  UNIQUEIDENTIFIER = NEWID();
DECLARE @Student_ID  UNIQUEIDENTIFIER = NEWID();

DECLARE @Major_ID      UNIQUEIDENTIFIER = NEWID();
DECLARE @Curriculum_ID UNIQUEIDENTIFIER = NEWID();

DECLARE @Subject_PRO192 UNIQUEIDENTIFIER = NEWID();
DECLARE @Subject_CSD201 UNIQUEIDENTIFIER = NEWID();
DECLARE @Subject_DBI202 UNIQUEIDENTIFIER = NEWID();
DECLARE @Subject_WED201 UNIQUEIDENTIFIER = NEWID();
DECLARE @Subject_SWR302 UNIQUEIDENTIFIER = NEWID();

DECLARE @Combo_ID UNIQUEIDENTIFIER = NEWID();

DECLARE @PO1_ID  UNIQUEIDENTIFIER = NEWID();
DECLARE @PO2_ID  UNIQUEIDENTIFIER = NEWID();
DECLARE @PLO1_ID UNIQUEIDENTIFIER = NEWID();
DECLARE @PLO2_ID UNIQUEIDENTIFIER = NEWID();

DECLARE @Syllabus_PRO192 UNIQUEIDENTIFIER = NEWID();

DECLARE @CLO1_ID UNIQUEIDENTIFIER = NEWID();
DECLARE @CLO2_ID UNIQUEIDENTIFIER = NEWID();

DECLARE @Material1_ID UNIQUEIDENTIFIER = NEWID();
DECLARE @Material2_ID UNIQUEIDENTIFIER = NEWID();

DECLARE @Assignment1_ID UNIQUEIDENTIFIER = NEWID();
DECLARE @Assignment2_ID UNIQUEIDENTIFIER = NEWID();

DECLARE @Review_ID UNIQUEIDENTIFIER = NEWID();

/* ============ USERS: 1 role - 1 nguoi ============ */
INSERT INTO Users (User_ID, Full_Name, Email, Password_Hash, Is_Active)
VALUES
(@Admin_ID,    N'Nguyễn Văn Admin',  'admin@fpt.edu.vn',    CONVERT(NVARCHAR(32), HASHBYTES('MD5', '123456'), 2), 1),
(@Designer_ID, N'Trần Thị Designer', 'designer@fpt.edu.vn', CONVERT(NVARCHAR(32), HASHBYTES('MD5', '123456'), 2), 1),
(@Reviewer_ID, N'Lê Văn Reviewer',   'reviewer@fpt.edu.vn', CONVERT(NVARCHAR(32), HASHBYTES('MD5', '123456'), 2), 1),
(@Teacher_ID,  N'Phạm Thị Teacher',  'teacher@fpt.edu.vn',  CONVERT(NVARCHAR(32), HASHBYTES('MD5', '123456'), 2), 1),
(@Student_ID,  N'Hoàng Văn Student', 'student@fpt.edu.vn',  CONVERT(NVARCHAR(32), HASHBYTES('MD5', '123456'), 2), 1);

/* ============ USER ROLES ============ */
INSERT INTO User_Roles (User_ID, Role_ID)
VALUES
(@Admin_ID,    (SELECT Role_ID FROM Roles WHERE Role_Name = 'Admin')),
(@Designer_ID, (SELECT Role_ID FROM Roles WHERE Role_Name = 'Designer')),
(@Reviewer_ID, (SELECT Role_ID FROM Roles WHERE Role_Name = 'Reviewer')),
(@Teacher_ID,  (SELECT Role_ID FROM Roles WHERE Role_Name = 'Teacher')),
(@Student_ID,  (SELECT Role_ID FROM Roles WHERE Role_Name = 'Student'));

/* ============ MAJORS ============ */
INSERT INTO Majors (Major_ID, Major_Code, Major_Name, Description, Is_Active)
VALUES (@Major_ID, 'SE', N'Kỹ thuật phần mềm', N'Ngành đào tạo Kỹ thuật phần mềm', 1);

/* ============ CURRICULUMS ============ */
INSERT INTO Curriculums
(Curriculum_ID, Major_ID, Curriculum_Code, Curriculum_Name, English_Name, Description,
 Total_Credits, Version, Decision_No, Decision_Date, Created_By, Is_Active, Status)
VALUES
(@Curriculum_ID, @Major_ID, 'SE_K18', N'Chương trình đào tạo Kỹ thuật phần mềm K18',
 'Software Engineering Curriculum K18', N'Khung chương trình đào tạo ngành Kỹ thuật phần mềm khóa 18',
 140, 'v1.0', 'QD-2024-001', '2024-06-01', @Designer_ID, 1, 1);

/* ============ SUBJECTS ============ */
INSERT INTO Subjects (Subject_ID, Major_ID, Subject_Code, Subject_Name, English_Name, Credits, Description, Is_Active)
VALUES
(@Subject_PRO192, @Major_ID, 'PRO192', N'Lập trình căn bản', 'Programming Fundamentals', 3, N'Môn học nhập môn lập trình', 1),
(@Subject_CSD201, @Major_ID, 'CSD201', N'Cấu trúc dữ liệu',   'Data Structures',          3, N'Cấu trúc dữ liệu và giải thuật', 1),
(@Subject_DBI202, @Major_ID, 'DBI202', N'Cơ sở dữ liệu',      'Database Systems',         3, N'Thiết kế và quản trị CSDL', 1),
(@Subject_WED201, @Major_ID, 'WED201', N'Phát triển Web',     'Web Development',          3, N'Lập trình ứng dụng Web', 1),
(@Subject_SWR302, @Major_ID, 'SWR302', N'Yêu cầu phần mềm',   'Software Requirement',     3, N'Phân tích yêu cầu phần mềm', 1);

/* ============ CURRICULUM SUBJECTS ============ */
INSERT INTO Curriculum_Subjects (Curriculum_ID, Subject_ID, Semester_No, Is_Mandatory)
VALUES
(@Curriculum_ID, @Subject_PRO192, 1, 1),
(@Curriculum_ID, @Subject_CSD201, 2, 1),
(@Curriculum_ID, @Subject_DBI202, 3, 1),
(@Curriculum_ID, @Subject_WED201, 4, 0),
(@Curriculum_ID, @Subject_SWR302, 5, 1);

/* ============ SUBJECT PREREQUISITES ============ */
INSERT INTO Subject_Prerequisites (Subject_ID, Required_Subject_ID)
VALUES
(@Subject_CSD201, @Subject_PRO192),
(@Subject_WED201, @Subject_PRO192);

/* ============ COMBOS (mon lien quan) ============ */
INSERT INTO Combos (Combo_ID, Curriculum_ID, Combo_Code, Combo_Name, English_Name, Description, Is_Active)
VALUES (@Combo_ID, @Curriculum_ID, 'SE_WEB', N'Combo Phát triển Web', 'Web Development Combo',
        N'Nhóm các môn học chuyên sâu về phát triển Web', 1);

INSERT INTO Combo_Subjects (Combo_ID, Subject_ID, Semester_No)
VALUES
(@Combo_ID, @Subject_WED201, 4),
(@Combo_ID, @Subject_DBI202, 3);

/* ============ POs / PLOs ============ */
INSERT INTO POs (PO_ID, Curriculum_ID, PO_Code, Description)
VALUES
(@PO1_ID, @Curriculum_ID, 'PO1', N'Có kiến thức nền tảng về khoa học máy tính'),
(@PO2_ID, @Curriculum_ID, 'PO2', N'Có khả năng thiết kế và phát triển phần mềm');

INSERT INTO PLOs (PLO_ID, Curriculum_ID, PLO_Code, Description)
VALUES
(@PLO1_ID, @Curriculum_ID, 'PLO1', N'Vận dụng kiến thức lập trình để giải quyết vấn đề thực tế'),
(@PLO2_ID, @Curriculum_ID, 'PLO2', N'Thiết kế cơ sở dữ liệu và ứng dụng web hoàn chỉnh');

INSERT INTO PO_PLO_Mappings (PO_ID, PLO_ID)
VALUES
(@PO1_ID, @PLO1_ID),
(@PO2_ID, @PLO2_ID);

/* ============ SYLLABUSES ============ */
INSERT INTO Syllabuses
(Syllabus_ID, Subject_ID, Syllabus_Name, English_Name, Version, Description,
 Time_Allocation, Student_Tasks, Tools, Scoring_Scale, Min_Avg_Mark_To_Pass,
 Decision_No, Approved_Date, Is_Active)
VALUES
(@Syllabus_PRO192, @Subject_PRO192, N'Đề cương Lập trình căn bản', 'Programming Fundamentals Syllabus',
 'v1.0', N'Đề cương chi tiết môn Lập trình căn bản', N'30 tiết lý thuyết, 15 tiết thực hành',
 N'Làm bài tập, đồ án nhỏ', N'Visual Studio Code, .NET SDK', '10', 5.0,
 'QD-2024-015', '2024-06-10', 1);

/* ============ CLOs ============ */
INSERT INTO CLOs (CLO_ID, Syllabus_ID, CLO_Code, Description)
VALUES
(@CLO1_ID, @Syllabus_PRO192, 'CLO1', N'Hiểu các cấu trúc điều khiển cơ bản trong lập trình'),
(@CLO2_ID, @Syllabus_PRO192, 'CLO2', N'Viết được chương trình sử dụng hàm và mảng');

/* ============ PLO CLO MAPPINGS ============ */
INSERT INTO PLO_CLO_Mappings (PLO_ID, CLO_ID)
VALUES
(@PLO1_ID, @CLO1_ID),
(@PLO1_ID, @CLO2_ID);

/* ============ SESSIONS ============ */
INSERT INTO Sessions
(Syllabus_ID, Session_No, Topic, Learning_Teaching_Type, LO, ITU, Student_Materials, Student_Tasks, URLs)
VALUES
(@Syllabus_PRO192, 1, N'Giới thiệu ngôn ngữ lập trình và môi trường phát triển', N'Lý thuyết + Thực hành',
 N'Cài đặt môi trường, hiểu cú pháp cơ bản', N'Slide, máy chiếu', N'Slide bài giảng chương 1',
 N'Đọc trước tài liệu chương 1', 'https://example.com/slide-1'),
(@Syllabus_PRO192, 2, N'Cấu trúc điều khiển: if, for, while', N'Lý thuyết + Thực hành',
 N'Vận dụng được các cấu trúc điều khiển', N'Slide, máy chiếu', N'Slide bài giảng chương 2',
 N'Làm bài tập chương 2', 'https://example.com/slide-2');

/* ============ MATERIALS (da gop, dung chung Download_Link) ============ */
INSERT INTO Materials
(Material_ID, Syllabus_ID, Material_Type, Material_Name, Author, Publisher, Published_Date,
 Edition, ISBN, Is_Main_Material, Is_Hard_Copy, Is_Online, Download_Link, Description, Notes,
 Uploaded_By, Is_Active)
VALUES
-- Tai lieu chinh thuc: Uploaded_By = NULL
(@Material1_ID, @Syllabus_PRO192, 'Textbook', N'C# Programming Yellow Book', N'Rob Miles',
 N'University of Hull', '2018-01-01', N'2018', '978-0000000000', 1, 0, 1,
 'https://example.com/materials/csharp-yellow-book.pdf', N'Giáo trình chính của môn học', NULL, NULL, 1),
-- Tai lieu giao vien tu upload: Uploaded_By = @Teacher_ID
(@Material2_ID, @Syllabus_PRO192, 'Slide', N'Slide bài giảng tuần 1 - Giáo viên bổ sung', NULL, NULL,
 NULL, NULL, NULL, 0, 0, 1, 'https://example.com/uploads/teacher/pro192-week1.pptx',
 N'Slide bổ sung do giáo viên tự biên soạn', N'Tài liệu tham khảo thêm', @Teacher_ID, 1);

/* ============ SYLLABUS ASSIGNMENTS (Admin gan Designer/Reviewer) ============ */
INSERT INTO Syllabus_Assignments (Assignment_ID, Syllabus_ID, User_ID, Assignment_Type, Assigned_By)
VALUES
(@Assignment1_ID, @Syllabus_PRO192, @Designer_ID, 'Designer', @Admin_ID),
(@Assignment2_ID, @Syllabus_PRO192, @Reviewer_ID, 'Reviewer', @Admin_ID);

/* ============ REVIEWS ============ */
INSERT INTO Reviews (Review_ID, Syllabus_ID, Reviewer_ID, Is_Approved, Comment, Review_Date)
VALUES (@Review_ID, @Syllabus_PRO192, @Reviewer_ID, 1, N'Đề cương đạt yêu cầu, đề nghị phê duyệt', GETDATE());

/* ============ AUDIT LOGS ============ */
INSERT INTO Audit_Logs (User_ID, Entity_Name, Entity_ID, Action, Old_Value, New_Value)
VALUES
(@Admin_ID,    'Curriculums', @Curriculum_ID, 'CREATE', NULL, N'Tạo mới chương trình đào tạo SE_K18'),
(@Reviewer_ID, 'Reviews',     @Review_ID,     'CREATE', NULL, N'Reviewer phê duyệt đề cương PRO192');

GO

/* ============ KIEM TRA NHANH (tuy chon) ============ */
SELECT u.Full_Name, r.Role_Name, u.Password_Hash
FROM Users u
JOIN User_Roles ur ON ur.User_ID = u.User_ID
JOIN Roles r ON r.Role_ID = ur.Role_ID;