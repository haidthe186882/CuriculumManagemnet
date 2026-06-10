CREATE DATABASE CurriculumManagementDB;
GO

USE CurriculumManagementDB;
GO

-- =========================
-- ROLE
-- =========================
CREATE TABLE Roles
(
    Role_ID INT IDENTITY(1,1) PRIMARY KEY,
    Role_Name NVARCHAR(50) NOT NULL UNIQUE
);

-- =========================
-- USER
-- =========================
CREATE TABLE Users
(
    User_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    Role_ID INT NOT NULL,

    Full_Name NVARCHAR(255) NOT NULL,
    Email NVARCHAR(255) NOT NULL UNIQUE,
    Password_Hash NVARCHAR(500),
    Status NVARCHAR(20) DEFAULT 'Active',

    Phone_Number NVARCHAR(20),
    Department NVARCHAR(255),
    Profile_Image_URL NVARCHAR(500),

    Created_Date DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_User_Role
        FOREIGN KEY(Role_ID)
        REFERENCES Roles(Role_ID)
);

-- =========================
-- PROGRAM
-- =========================
CREATE TABLE Programs
(
    Program_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Program_Code NVARCHAR(50) UNIQUE,
    Program_Name NVARCHAR(255) NOT NULL,

    Description NVARCHAR(MAX),

    Status NVARCHAR(20) DEFAULT 'Active'
);

-- =========================
-- CURRICULUM
-- =========================
CREATE TABLE Curriculums
(
    Curriculum_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Program_ID UNIQUEIDENTIFIER NOT NULL,
    Created_By UNIQUEIDENTIFIER NOT NULL,

    Curriculum_Code NVARCHAR(50) NOT NULL UNIQUE,
    Curriculum_Name NVARCHAR(500) NOT NULL,
    English_Name NVARCHAR(500),

    Description NVARCHAR(MAX),

    Total_Credits INT,

    Version NVARCHAR(20),

    Decision_No NVARCHAR(100),
    Decision_Date DATE,

    Status NVARCHAR(30) DEFAULT 'Draft',

    Is_Public BIT DEFAULT 0,
    Is_Active BIT DEFAULT 1,

    Created_Date DATETIME DEFAULT GETDATE(),
    Updated_Date DATETIME,

    CONSTRAINT FK_Curriculum_Program
        FOREIGN KEY(Program_ID)
        REFERENCES Programs(Program_ID),

    CONSTRAINT FK_Curriculum_User
        FOREIGN KEY(Created_By)
        REFERENCES Users(User_ID)
);

-- =========================
-- SUBJECT
-- =========================
CREATE TABLE Subjects
(
    Subject_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Subject_Code NVARCHAR(20) NOT NULL UNIQUE,
    Subject_Name NVARCHAR(255) NOT NULL,
    English_Name NVARCHAR(255),

    Credits INT NOT NULL,

    Description NVARCHAR(MAX),

    Department NVARCHAR(255),

    Status NVARCHAR(20) DEFAULT 'Active'
);

-- =========================
-- CURRICULUM SUBJECT
-- =========================
CREATE TABLE Curriculum_Subjects
(
    Curriculum_Subject_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Curriculum_ID UNIQUEIDENTIFIER NOT NULL,
    Subject_ID UNIQUEIDENTIFIER NOT NULL,

    Semester_No INT,

    Is_Mandatory BIT DEFAULT 1,

    CONSTRAINT FK_CS_Curriculum
        FOREIGN KEY(Curriculum_ID)
        REFERENCES Curriculums(Curriculum_ID),

    CONSTRAINT FK_CS_Subject
        FOREIGN KEY(Subject_ID)
        REFERENCES Subjects(Subject_ID)
);

-- =========================
-- SUBJECT PREREQUISITE
-- =========================
CREATE TABLE Subject_Prerequisites
(
    Subject_Prerequisite_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Subject_ID UNIQUEIDENTIFIER NOT NULL,
    Required_Subject_ID UNIQUEIDENTIFIER NOT NULL,

    CONSTRAINT FK_SP_Subject
        FOREIGN KEY(Subject_ID)
        REFERENCES Subjects(Subject_ID),

    CONSTRAINT FK_SP_Required
        FOREIGN KEY(Required_Subject_ID)
        REFERENCES Subjects(Subject_ID)
);

-- =========================
-- PROGRAM OBJECTIVE (PO)
-- =========================
CREATE TABLE Program_Objectives
(
    PO_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Curriculum_ID UNIQUEIDENTIFIER NOT NULL,

    PO_Code NVARCHAR(20) NOT NULL,
    Description NVARCHAR(MAX),

    CONSTRAINT FK_PO_Curriculum
        FOREIGN KEY(Curriculum_ID)
        REFERENCES Curriculums(Curriculum_ID)
);

-- =========================
-- PROGRAM LEARNING OUTCOME
-- =========================
CREATE TABLE Program_Learning_Outcomes
(
    PLO_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Curriculum_ID UNIQUEIDENTIFIER NOT NULL,

    PLO_Code NVARCHAR(20) NOT NULL,
    Description NVARCHAR(MAX),

    CONSTRAINT FK_PLO_Curriculum
        FOREIGN KEY(Curriculum_ID)
        REFERENCES Curriculums(Curriculum_ID)
);

-- =========================
-- PO PLO MAPPING
-- =========================
CREATE TABLE PO_PLO_Mappings
(
    PO_PLO_Mapping_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    PO_ID UNIQUEIDENTIFIER NOT NULL,
    PLO_ID UNIQUEIDENTIFIER NOT NULL,

    CONSTRAINT FK_POPLO_PO
        FOREIGN KEY(PO_ID)
        REFERENCES Program_Objectives(PO_ID),

    CONSTRAINT FK_POPLO_PLO
        FOREIGN KEY(PLO_ID)
        REFERENCES Program_Learning_Outcomes(PLO_ID)
);

-- =========================
-- SYLLABUS
-- =========================
CREATE TABLE Syllabuses
(
    Syllabus_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Subject_ID UNIQUEIDENTIFIER NOT NULL,

    Syllabus_Name NVARCHAR(500),
    English_Name NVARCHAR(500),

    Version NVARCHAR(20),

    Description NVARCHAR(MAX),

    Time_Allocation NVARCHAR(255),

    Student_Tasks NVARCHAR(MAX),

    Tools NVARCHAR(MAX),

    Scoring_Scale NVARCHAR(100),

    Min_Avg_Mark_To_Pass DECIMAL(5,2),

    Decision_No NVARCHAR(100),
    Approved_Date DATE,

    Status NVARCHAR(30) DEFAULT 'Draft',

    Is_Active BIT DEFAULT 1,

    CONSTRAINT FK_Syllabus_Subject
        FOREIGN KEY(Subject_ID)
        REFERENCES Subjects(Subject_ID)
);

-- =========================
-- COURSE LEARNING OUTCOME
-- =========================
CREATE TABLE Course_Learning_Outcomes
(
    CLO_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Syllabus_ID UNIQUEIDENTIFIER NOT NULL,

    CLO_Code NVARCHAR(20),
    Description NVARCHAR(MAX),

    CONSTRAINT FK_CLO_Syllabus
        FOREIGN KEY(Syllabus_ID)
        REFERENCES Syllabuses(Syllabus_ID)
);

-- =========================
-- CLO PLO MAPPING
-- =========================
CREATE TABLE CLO_PLO_Mappings
(
    CLO_PLO_Mapping_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    CLO_ID UNIQUEIDENTIFIER NOT NULL,
    PLO_ID UNIQUEIDENTIFIER NOT NULL,

    CONSTRAINT FK_CP_CLO
        FOREIGN KEY(CLO_ID)
        REFERENCES Course_Learning_Outcomes(CLO_ID),

    CONSTRAINT FK_CP_PLO
        FOREIGN KEY(PLO_ID)
        REFERENCES Program_Learning_Outcomes(PLO_ID)
);

-- =========================
-- SESSION
-- =========================
CREATE TABLE Sessions
(
    Session_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Syllabus_ID UNIQUEIDENTIFIER NOT NULL,

    Session_No INT,

    Topic NVARCHAR(MAX),

    Learning_Teaching_Type NVARCHAR(255),

    LO NVARCHAR(MAX),

    ITU NVARCHAR(MAX),

    Student_Materials NVARCHAR(MAX),

    Student_Tasks NVARCHAR(MAX),

    URLs NVARCHAR(MAX),

    CONSTRAINT FK_Session_Syllabus
        FOREIGN KEY(Syllabus_ID)
        REFERENCES Syllabuses(Syllabus_ID)
);

-- =========================
-- MATERIAL
-- =========================
CREATE TABLE Materials
(
    Material_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Syllabus_ID UNIQUEIDENTIFIER NOT NULL,

    Material_Description NVARCHAR(MAX),

    Author NVARCHAR(255),

    Publisher NVARCHAR(255),

    Published_Date DATE,

    Edition NVARCHAR(100),

    ISBN NVARCHAR(100),

    Is_Main_Material BIT DEFAULT 0,

    Is_Hard_Copy BIT DEFAULT 0,

    Is_Online BIT DEFAULT 1,

    Notes NVARCHAR(MAX),

    CONSTRAINT FK_Material_Syllabus
        FOREIGN KEY(Syllabus_ID)
        REFERENCES Syllabuses(Syllabus_ID)
);

-- =========================
-- DOCUMENT
-- =========================
CREATE TABLE Documents
(
    Document_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Curriculum_ID UNIQUEIDENTIFIER NULL,
    Subject_ID UNIQUEIDENTIFIER NULL,
    Syllabus_ID UNIQUEIDENTIFIER NULL,

    File_Name NVARCHAR(500),
    File_Path NVARCHAR(1000),
    File_Type NVARCHAR(50),

    Uploaded_By UNIQUEIDENTIFIER,

    Uploaded_Date DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Doc_Curriculum
        FOREIGN KEY(Curriculum_ID)
        REFERENCES Curriculums(Curriculum_ID),

    CONSTRAINT FK_Doc_Subject
        FOREIGN KEY(Subject_ID)
        REFERENCES Subjects(Subject_ID),

    CONSTRAINT FK_Doc_Syllabus
        FOREIGN KEY(Syllabus_ID)
        REFERENCES Syllabuses(Syllabus_ID),

    CONSTRAINT FK_Doc_User
        FOREIGN KEY(Uploaded_By)
        REFERENCES Users(User_ID)
);

-- =========================
-- ASSIGNMENT
-- =========================
CREATE TABLE Curriculum_Assignments
(
    Assignment_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Curriculum_ID UNIQUEIDENTIFIER NOT NULL,
    User_ID UNIQUEIDENTIFIER NOT NULL,

    Assignment_Type NVARCHAR(30),

    Assigned_Date DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Assignment_Curriculum
        FOREIGN KEY(Curriculum_ID)
        REFERENCES Curriculums(Curriculum_ID),

    CONSTRAINT FK_Assignment_User
        FOREIGN KEY(User_ID)
        REFERENCES Users(User_ID)
);

-- =========================
-- REVIEW
-- =========================
CREATE TABLE Reviews
(
    Review_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Curriculum_ID UNIQUEIDENTIFIER NOT NULL,

    Reviewer_ID UNIQUEIDENTIFIER NOT NULL,

    Status NVARCHAR(30),

    Comment NVARCHAR(MAX),

    Review_Date DATETIME,

    CONSTRAINT FK_Review_Curriculum
        FOREIGN KEY(Curriculum_ID)
        REFERENCES Curriculums(Curriculum_ID),

    CONSTRAINT FK_Review_User
        FOREIGN KEY(Reviewer_ID)
        REFERENCES Users(User_ID)
);

-- =========================
-- AUDIT LOG
-- =========================
CREATE TABLE Audit_Logs
(
    Log_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    User_ID UNIQUEIDENTIFIER,

    Entity_Name NVARCHAR(100),

    Entity_ID UNIQUEIDENTIFIER,

    Action NVARCHAR(50),

    Old_Value NVARCHAR(MAX),

    New_Value NVARCHAR(MAX),

    Created_Date DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Audit_User
        FOREIGN KEY(User_ID)
        REFERENCES Users(User_ID)
);
GO
