/* =========================================
   DROP DATABASE
========================================= */
USE master;
GO

IF EXISTS (SELECT * FROM sys.databases WHERE name = 'CurriculumManagementDB')
BEGIN
    ALTER DATABASE CurriculumManagementDB
    SET SINGLE_USER WITH ROLLBACK IMMEDIATE;

    DROP DATABASE CurriculumManagementDB;
END
GO

CREATE DATABASE CurriculumManagementDB;
GO

USE CurriculumManagementDB;
GO

/* =========================================
   ROLES
========================================= */
CREATE TABLE Roles
(
    Role_ID INT IDENTITY(1,1) PRIMARY KEY,
    Role_Name NVARCHAR(50) NOT NULL UNIQUE
);

/* =========================================
   USERS
========================================= */
CREATE TABLE Users
(
    User_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Full_Name NVARCHAR(255) NOT NULL,
    Email NVARCHAR(255) NOT NULL UNIQUE,
    Password_Hash NVARCHAR(500),

    Is_Active BIT DEFAULT 1,
    Created_Date DATETIME DEFAULT GETDATE()
);

/* =========================================
   USER ROLES
========================================= */
CREATE TABLE User_Roles
(
    User_Role_ID UNIQUEIDENTIFIER
        PRIMARY KEY DEFAULT NEWID(),

    User_ID UNIQUEIDENTIFIER NOT NULL,
    Role_ID INT NOT NULL,

    Assigned_Date DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_UR_User
        FOREIGN KEY(User_ID)
        REFERENCES Users(User_ID),

    CONSTRAINT FK_UR_Role
        FOREIGN KEY(Role_ID)
        REFERENCES Roles(Role_ID),

    CONSTRAINT UQ_User_Role
        UNIQUE(User_ID, Role_ID)
);
/* =========================================
   MAJORS
========================================= */
CREATE TABLE Majors
(
    Major_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Major_Code NVARCHAR(50) UNIQUE,
    Major_Name NVARCHAR(255) NOT NULL,

    Description NVARCHAR(MAX),

    Is_Active BIT DEFAULT 1
);

/* =========================================
   CURRICULUMS
========================================= */
CREATE TABLE Curriculums
(
    Curriculum_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Major_ID UNIQUEIDENTIFIER NOT NULL,

    Curriculum_Code NVARCHAR(50) UNIQUE NOT NULL,
    Curriculum_Name NVARCHAR(500) NOT NULL,
    English_Name NVARCHAR(500),

    Description NVARCHAR(MAX),

    Total_Credits INT,
    Version NVARCHAR(50),

    Decision_No NVARCHAR(100),
    Decision_Date DATE,

    Created_By UNIQUEIDENTIFIER,

    Created_Date DATETIME DEFAULT GETDATE(),
    Updated_Date DATETIME,

    Is_Active BIT DEFAULT 1,

    CONSTRAINT FK_Curriculum_Major
        FOREIGN KEY(Major_ID)
        REFERENCES Majors(Major_ID),

    CONSTRAINT FK_Curriculum_User
        FOREIGN KEY(Created_By)
        REFERENCES Users(User_ID)
);

/* =========================================
   SUBJECTS
========================================= */
CREATE TABLE Subjects
(
    Subject_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Major_ID UNIQUEIDENTIFIER NOT NULL,

    Subject_Code NVARCHAR(20) UNIQUE NOT NULL,
    Subject_Name NVARCHAR(255) NOT NULL,
    English_Name NVARCHAR(255),

    Credits INT NOT NULL,

    Description NVARCHAR(MAX),

    Is_Active BIT DEFAULT 1,

    CONSTRAINT FK_Subject_Major
        FOREIGN KEY(Major_ID)
        REFERENCES Majors(Major_ID)
);

/* =========================================
   CURRICULUM SUBJECTS
========================================= */
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
        REFERENCES Subjects(Subject_ID),

    CONSTRAINT UQ_CS UNIQUE
        (Curriculum_ID, Subject_ID)
);

/* =========================================
   SUBJECT PREREQUISITES
========================================= */
CREATE TABLE Subject_Prerequisites
(
    Subject_Prerequisite_ID UNIQUEIDENTIFIER
        PRIMARY KEY DEFAULT NEWID(),

    Subject_ID UNIQUEIDENTIFIER NOT NULL,
    Required_Subject_ID UNIQUEIDENTIFIER NOT NULL,

    CONSTRAINT FK_SP_Subject
        FOREIGN KEY(Subject_ID)
        REFERENCES Subjects(Subject_ID),

    CONSTRAINT FK_SP_Required
        FOREIGN KEY(Required_Subject_ID)
        REFERENCES Subjects(Subject_ID)
);

/* =========================================
   POs
========================================= */
CREATE TABLE POs
(
    PO_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Curriculum_ID UNIQUEIDENTIFIER NOT NULL,

    PO_Code NVARCHAR(20) NOT NULL,
    Description NVARCHAR(MAX),

    CONSTRAINT FK_PO_Curriculum
        FOREIGN KEY(Curriculum_ID)
        REFERENCES Curriculums(Curriculum_ID)
);

/* =========================================
   PLOs
========================================= */
CREATE TABLE PLOs
(
    PLO_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Curriculum_ID UNIQUEIDENTIFIER NOT NULL,

    PLO_Code NVARCHAR(20) NOT NULL,
    Description NVARCHAR(MAX),

    CONSTRAINT FK_PLO_Curriculum
        FOREIGN KEY(Curriculum_ID)
        REFERENCES Curriculums(Curriculum_ID)
);

/* =========================================
   PO PLO MAPPING
========================================= */
CREATE TABLE PO_PLO_Mappings
(
    Mapping_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    PO_ID UNIQUEIDENTIFIER NOT NULL,
    PLO_ID UNIQUEIDENTIFIER NOT NULL,

    CONSTRAINT FK_POPLO_PO
        FOREIGN KEY(PO_ID)
        REFERENCES POs(PO_ID),

    CONSTRAINT FK_POPLO_PLO
        FOREIGN KEY(PLO_ID)
        REFERENCES PLOs(PLO_ID)
);

/* =========================================
   SYLLABUSES
========================================= */
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

    Is_Active BIT DEFAULT 1,

    CONSTRAINT FK_Syllabus_Subject
        FOREIGN KEY(Subject_ID)
        REFERENCES Subjects(Subject_ID)
);

/* =========================================
   CLOs
========================================= */
CREATE TABLE CLOs
(
    CLO_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Syllabus_ID UNIQUEIDENTIFIER NOT NULL,

    CLO_Code NVARCHAR(20),
    Description NVARCHAR(MAX),

    CONSTRAINT FK_CLO_Syllabus
        FOREIGN KEY(Syllabus_ID)
        REFERENCES Syllabuses(Syllabus_ID)
);

/* =========================================
   PLO CLO MAPPING
========================================= */
CREATE TABLE PLO_CLO_Mappings
(
    Mapping_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    PLO_ID UNIQUEIDENTIFIER NOT NULL,
    CLO_ID UNIQUEIDENTIFIER NOT NULL,

    CONSTRAINT FK_PC_PLO
        FOREIGN KEY(PLO_ID)
        REFERENCES PLOs(PLO_ID),

    CONSTRAINT FK_PC_CLO
        FOREIGN KEY(CLO_ID)
        REFERENCES CLOs(CLO_ID)
);

/* =========================================
   SESSIONS
========================================= */
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

/* =========================================
   MATERIALS
========================================= */
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

/* =========================================
   TEACHER MATERIALS
========================================= */
CREATE TABLE Teacher_Materials
(
    Teacher_Material_ID UNIQUEIDENTIFIER
        PRIMARY KEY DEFAULT NEWID(),

    User_ID UNIQUEIDENTIFIER NOT NULL,

    Syllabus_ID UNIQUEIDENTIFIER NOT NULL,

    Material_Type NVARCHAR(50) NOT NULL,

    Material_Name NVARCHAR(255) NOT NULL,

    Material_URL NVARCHAR(1000) NOT NULL,

    Description NVARCHAR(MAX),

    Created_Date DATETIME DEFAULT GETDATE(),

    Is_Active BIT DEFAULT 1,

    CONSTRAINT FK_TM_User
        FOREIGN KEY(User_ID)
        REFERENCES Users(User_ID),

    CONSTRAINT FK_TM_Syllabus
        FOREIGN KEY(Syllabus_ID)
        REFERENCES Syllabuses(Syllabus_ID)
);

/* =========================================
   CURRICULUM ASSIGNMENTS
========================================= */
CREATE TABLE Curriculum_Assignments
(
    Assignment_ID UNIQUEIDENTIFIER
        PRIMARY KEY DEFAULT NEWID(),

    Curriculum_ID UNIQUEIDENTIFIER NOT NULL,

    User_ID UNIQUEIDENTIFIER NOT NULL,

    Assignment_Type NVARCHAR(20) NOT NULL,
    -- Designer | Reviewer

    Assigned_By UNIQUEIDENTIFIER NULL,

    Assigned_Date DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_CA_Curriculum
        FOREIGN KEY(Curriculum_ID)
        REFERENCES Curriculums(Curriculum_ID),

    CONSTRAINT FK_CA_User
        FOREIGN KEY(User_ID)
        REFERENCES Users(User_ID),

    CONSTRAINT FK_CA_AssignedBy
        FOREIGN KEY(Assigned_By)
        REFERENCES Users(User_ID)
);

/* =========================================
   REVIEWS
========================================= */
CREATE TABLE Reviews
(
    Review_ID UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),

    Curriculum_ID UNIQUEIDENTIFIER NOT NULL,

    Reviewer_ID UNIQUEIDENTIFIER NOT NULL,

    Is_Approved BIT DEFAULT 0,

    Comment NVARCHAR(MAX),

    Review_Date DATETIME,

    CONSTRAINT FK_Review_Curriculum
        FOREIGN KEY(Curriculum_ID)
        REFERENCES Curriculums(Curriculum_ID),

    CONSTRAINT FK_Review_User
        FOREIGN KEY(Reviewer_ID)
        REFERENCES Users(User_ID)
);

/* =========================================
   AUDIT LOGS
========================================= */
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

INSERT INTO Roles(Role_Name)
VALUES
('Admin'),
('Designer'),
('Reviewer'),
('Teacher'),
('Student');