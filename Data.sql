USE CurriculumManagementDB;
GO

/* =========================
   ROLES
========================= */
INSERT INTO Roles(Role_Name)
VALUES ('Admin'), ('Lecturer'), ('Student');

/* =========================
   USERS
========================= */
INSERT INTO Users(Role_ID, Full_Name, Email, Password_Hash, Is_Active)
VALUES 
(1, 'Admin User', 'admin@school.com', '123', 1),
(2, 'Dr. John Smith', 'john@school.com', '123', 1),
(3, 'Nguyen Van A', 'a@student.com', '123', 1),
(3, 'Tran Thi B', 'b@student.com', '123', 1);

/* =========================
   PROGRAMS
========================= */
INSERT INTO Programs(Program_Code, Program_Name, Description, Is_Active)
VALUES 
('IT2026', 'Information Technology', 'IT Bachelor Program', 1),
('BUS2026', 'Business Administration', 'Business Program', 1);

/* =========================
   SUBJECTS
========================= */
INSERT INTO Subjects(Subject_Code, Subject_Name, English_Name, Credits, Description, Is_Active)
VALUES
('PRN211', 'C# Programming', 'C# Programming', 3, 'Basic C# course', 1),
('DBI202', 'Database Design', 'Database Design', 3, 'SQL Server basics', 1),
('SWE201', 'Software Engineering', 'Software Engineering', 4, 'SE principles', 1),
('WEB101', 'Web Development', 'Web Development', 3, 'HTML CSS JS', 1);

/* =========================
   CURRICULUM
========================= */
INSERT INTO Curriculums
(
    Program_ID, Created_By,
    Curriculum_Code, Curriculum_Name, English_Name,
    Description, Total_Credits, Version,
    Decision_No, Decision_Date,
    Is_Active
)
VALUES
(
    (SELECT TOP 1 Program_ID FROM Programs WHERE Program_Code='IT2026'),
    (SELECT TOP 1 User_ID FROM Users WHERE Email='admin@school.com'),

    'CUR-IT-2026',
    'IT Curriculum 2026',
    'IT Curriculum 2026',

    'Main IT curriculum',
    120,
    'v1',
    'DEC-001',
    '2026-01-01',
    1
);

/* =========================
   CURRICULUM SUBJECTS
========================= */
INSERT INTO Curriculum_Subjects(Curriculum_ID, Subject_ID, Semester_No, Is_Mandatory)
SELECT 
    c.Curriculum_ID,
    s.Subject_ID,
    1,
    1
FROM Curriculums c
JOIN Subjects s ON s.Subject_Code IN ('PRN211','DBI202','SWE201','WEB101');

/* =========================
   SYLLABUS
========================= */
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
    Approved_Date,
    Is_Active
)
VALUES
(
    (SELECT Subject_ID FROM Subjects WHERE Subject_Code='PRN211'),
    'C# Basic Syllabus',
    'C# Basic Syllabus',
    'v1',
    'Learn C# basics',
    '30-30-40',
    'Assignments + Project',
    'Visual Studio',
    'A-F',
    5.0,
    'DEC-SY-001',
    '2026-01-10',
    1
);

/* =========================
   MATERIALS
========================= */
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
VALUES
(
    (SELECT TOP 1 Syllabus_ID FROM Syllabuses),
    'C# Programming Book',
    'Microsoft',
    'MS Press',
    '2023-01-01',
    '3rd',
    'ISBN-12345',
    1,
    1,
    1,
    'Main textbook'
);

/* =========================
   REVIEWS
========================= */
INSERT INTO Reviews
(
    Curriculum_ID,
    Reviewer_ID,
    Is_Approved,
    Comment,
    Review_Date
)
VALUES
(
    (SELECT TOP 1 Curriculum_ID FROM Curriculums),
    (SELECT TOP 1 User_ID FROM Users WHERE Role_ID = 2),
    1,
    'Approved curriculum',
    GETDATE()
);

/* =========================
   DOCUMENTS
========================= */
INSERT INTO Documents
(
    Curriculum_ID,
    Subject_ID,
    Syllabus_ID,
    File_Name,
    File_Path,
    File_Type,
    Uploaded_By
)
VALUES
(
    (SELECT TOP 1 Curriculum_ID FROM Curriculums),
    (SELECT TOP 1 Subject_ID FROM Subjects),
    (SELECT TOP 1 Syllabus_ID FROM Syllabuses),

    'curriculum.pdf',
    '/docs/curriculum.pdf',
    'pdf',
    (SELECT TOP 1 User_ID FROM Users)
);