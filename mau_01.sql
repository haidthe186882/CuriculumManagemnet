/* ===========================
   ROLES
=========================== */
INSERT INTO Roles (Role_Name)
VALUES
('Admin'),
('Designer'),
('Reviewer'),
('Teacher'),
('Student'),
('Guest');



/* ===========================
   USERS (Password không mã hóa)
=========================== */
INSERT INTO Users (Role_ID, Full_Name, Email, Password_Hash)
VALUES
(1, N'Nguyễn Văn Admin', 'admin@cms.com', 'admin123'),
(2, N'Trần Thị Designer', 'designer@cms.com', 'designer123'),
(3, N'Lê Văn Reviewer', 'reviewer@cms.com', 'reviewer123'),
(4, N'Phạm Minh Teacher', 'teacher@cms.com', 'teacher123'),
(5, N'Nguyễn Quốc Student', 'student@cms.com', 'student123'),
(6, N'Guest User', 'guest@cms.com', 'guest123');


/* ===========================
   PROGRAMS
=========================== */
INSERT INTO Programs (Program_Code, Program_Name, Description)
VALUES
('SE2025', N'Kỹ thuật phần mềm', N'Chương trình đào tạo Software Engineering'),
('AI2025', N'Trí tuệ nhân tạo', N'Chương trình đào tạo Artificial Intelligence');


/* ===========================
   SUBJECTS
=========================== */
INSERT INTO Subjects
(Subject_Code, Subject_Name, English_Name, Credits, Description)
VALUES
('PRJ101', N'Lập trình cơ bản', 'Programming Fundamentals', 3, N'Nhập môn lập trình'),
('DBI202', N'Hệ quản trị cơ sở dữ liệu', 'Database Systems', 3, N'Cơ sở dữ liệu'),
('SWE201', N'Kỹ thuật phần mềm', 'Software Engineering', 3, N'Quy trình phát triển phần mềm'),
('OOP301', N'Lập trình hướng đối tượng', 'Object Oriented Programming', 3, N'OOP bằng Java'),
('WEB302', N'Phát triển Web', 'Web Development', 3, N'HTML CSS JavaScript');


/* ===========================
   CURRICULUMS
=========================== */
INSERT INTO Curriculums
(
    Program_ID,
    Created_By,
    Curriculum_Code,
    Curriculum_Name,
    English_Name,
    Description,
    Total_Credits,
    Version,
    Decision_No,
    Decision_Date
)
SELECT
    p.Program_ID,
    u.User_ID,
    'CURR-SE-2025',
    N'Chương trình Kỹ thuật phần mềm 2025',
    'Software Engineering Curriculum 2025',
    N'Chương trình chính thức',
    120,
    '1.0',
    'QD-2025-001',
    '2025-01-15'
FROM Programs p, Users u
WHERE p.Program_Code='SE2025'
AND u.Email='designer@cms.com';


/* ===========================
   CURRICULUM SUBJECTS
=========================== */
INSERT INTO Curriculum_Subjects
(Curriculum_ID, Subject_ID, Semester_No, Is_Mandatory)
SELECT
    c.Curriculum_ID,
    s.Subject_ID,
    1,
    1
FROM Curriculums c
CROSS JOIN Subjects s
WHERE c.Curriculum_Code='CURR-SE-2025'
AND s.Subject_Code IN ('PRJ101','DBI202');


INSERT INTO Curriculum_Subjects
(Curriculum_ID, Subject_ID, Semester_No, Is_Mandatory)
SELECT
    c.Curriculum_ID,
    s.Subject_ID,
    2,
    1
FROM Curriculums c
CROSS JOIN Subjects s
WHERE c.Curriculum_Code='CURR-SE-2025'
AND s.Subject_Code IN ('OOP301','SWE201');


/* ===========================
   SUBJECT PREREQUISITES
=========================== */
INSERT INTO Subject_Prerequisites
(Subject_ID, Required_Subject_ID)
SELECT
    s1.Subject_ID,
    s2.Subject_ID
FROM Subjects s1, Subjects s2
WHERE s1.Subject_Code='OOP301'
AND s2.Subject_Code='PRJ101';


/* ===========================
   PROGRAM OBJECTIVES (PO)
=========================== */
INSERT INTO Program_Objectives
(Curriculum_ID, PO_Code, Description)
SELECT
    Curriculum_ID,
    'PO1',
    N'Có kiến thức nền tảng CNTT'
FROM Curriculums
WHERE Curriculum_Code='CURR-SE-2025';

INSERT INTO Program_Objectives
(Curriculum_ID, PO_Code, Description)
SELECT
    Curriculum_ID,
    'PO2',
    N'Có kỹ năng phát triển phần mềm'
FROM Curriculums
WHERE Curriculum_Code='CURR-SE-2025';


/* ===========================
   PROGRAM LEARNING OUTCOMES (PLO)
=========================== */
INSERT INTO Program_Learning_Outcomes
(Curriculum_ID, PLO_Code, Description)
SELECT
    Curriculum_ID,
    'PLO1',
    N'Phân tích yêu cầu'
FROM Curriculums
WHERE Curriculum_Code='CURR-SE-2025';

INSERT INTO Program_Learning_Outcomes
(Curriculum_ID, PLO_Code, Description)
SELECT
    Curriculum_ID,
    'PLO2',
    N'Thiết kế và lập trình'
FROM Curriculums
WHERE Curriculum_Code='CURR-SE-2025';


/* ===========================
   PO - PLO MAPPING
=========================== */
INSERT INTO PO_PLO_Mappings (PO_ID, PLO_ID)
SELECT
    po.PO_ID,
    plo.PLO_ID
FROM Program_Objectives po
JOIN Program_Learning_Outcomes plo
ON po.PO_Code='PO1'
AND plo.PLO_Code='PLO1';


/* ===========================
   SYLLABUSES
=========================== */
INSERT INTO Syllabuses
(
    Subject_ID,
    Syllabus_Name,
    English_Name,
    Version,
    Description,
    Time_Allocation,
    Student_Tasks,
    Tools,
    Scoring_Scale,
    Min_Avg_Mark_To_Pass,
    Decision_No,
    Approved_Date
)
SELECT
    Subject_ID,
    N'Đề cương Lập trình cơ bản',
    'Programming Fundamentals Syllabus',
    '1.0',
    N'Dùng cho sinh viên năm nhất',
    '45 hours',
    N'Làm bài tập',
    'Visual Studio Code',
    '10',
    5.0,
    'SY-001',
    '2025-01-20'
FROM Subjects
WHERE Subject_Code='PRJ101';


/* ===========================
   CLO
=========================== */
INSERT INTO Course_Learning_Outcomes
(Syllabus_ID, CLO_Code, Description)
SELECT
    Syllabus_ID,
    'CLO1',
    N'Viết chương trình đơn giản'
FROM Syllabuses;


/* ===========================
   CLO - PLO
=========================== */
INSERT INTO CLO_PLO_Mappings
(CLO_ID, PLO_ID)
SELECT
    c.CLO_ID,
    p.PLO_ID
FROM Course_Learning_Outcomes c
CROSS JOIN Program_Learning_Outcomes p
WHERE c.CLO_Code='CLO1'
AND p.PLO_Code='PLO1';


/* ===========================
   SESSIONS
=========================== */
INSERT INTO Sessions
(
    Syllabus_ID,
    Session_No,
    Topic,
    Learning_Teaching_Type,
    LO,
    ITU,
    Student_Materials,
    Student_Tasks,
    URLs
)
SELECT
    Syllabus_ID,
    1,
    N'Giới thiệu môn học',
    'Lecture',
    'CLO1',
    '',
    N'Slide chương 1',
    N'Đọc tài liệu',
    'https://example.com'
FROM Syllabuses;


/* ===========================
   MATERIALS
=========================== */
INSERT INTO Materials
(
    Syllabus_ID,
    Material_Description,
    Author,
    Publisher,
    Published_Date,
    Edition,
    ISBN,
    Is_Main_Material,
    Is_Hard_Copy,
    Is_Online,
    Notes
)
SELECT
    Syllabus_ID,
    N'Giáo trình chính',
    'John Smith',
    'ABC Publisher',
    '2024-01-01',
    '1st',
    '9781234567890',
    1,
    0,
    1,
    N'Tài liệu bắt buộc'
FROM Syllabuses;


/* ===========================
   DOCUMENTS
=========================== */
INSERT INTO Documents
(
    Curriculum_ID,
    File_Name,
    File_Path,
    File_Type,
    Uploaded_By
)
SELECT
    c.Curriculum_ID,
    'curriculum.pdf',
    '/docs/curriculum.pdf',
    'pdf',
    u.User_ID
FROM Curriculums c
CROSS JOIN Users u
WHERE c.Curriculum_Code='CURR-SE-2025'
AND u.Email='designer@cms.com';


/* ===========================
   CURRICULUM ASSIGNMENTS
=========================== */
INSERT INTO Curriculum_Assignments
(Curriculum_ID, User_ID, Assignment_Type)
SELECT
    c.Curriculum_ID,
    u.User_ID,
    'Designer'
FROM Curriculums c
CROSS JOIN Users u
WHERE c.Curriculum_Code='CURR-SE-2025'
AND u.Email='designer@cms.com';


/* ===========================
   REVIEWS
=========================== */
INSERT INTO Reviews
(
    Curriculum_ID,
    Reviewer_ID,
    Is_Approved,
    Comment,
    Review_Date
)
SELECT
    c.Curriculum_ID,
    u.User_ID,
    1,
    N'Đạt yêu cầu',
    GETDATE()
FROM Curriculums c
CROSS JOIN Users u
WHERE c.Curriculum_Code='CURR-SE-2025'
AND u.Email='reviewer@cms.com';


/* ===========================
   AUDIT LOGS
=========================== */
INSERT INTO Audit_Logs
(
    User_ID,
    Entity_Name,
    Entity_ID,
    Action,
    Old_Value,
    New_Value
)
SELECT
    u.User_ID,
    'Curriculum',
    c.Curriculum_ID,
    'CREATE',
    NULL,
    'Created Curriculum'
FROM Users u
CROSS JOIN Curriculums c
WHERE u.Email='admin@cms.com'
AND c.Curriculum_Code='CURR-SE-2025';