-- Run this if you already created DB from an older SQLQuery1.sql (adds missing columns)
USE CurriculumManagementDB;
GO

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('Users') AND name = 'Phone_Number')
    ALTER TABLE Users ADD Phone_Number NVARCHAR(20);
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('Users') AND name = 'Department')
    ALTER TABLE Users ADD Department NVARCHAR(255);
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('Users') AND name = 'Profile_Image_URL')
    ALTER TABLE Users ADD Profile_Image_URL NVARCHAR(500);

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('Curriculums') AND name = 'Is_Public')
    ALTER TABLE Curriculums ADD Is_Public BIT DEFAULT 0;
IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('Curriculums') AND name = 'Is_Active')
    ALTER TABLE Curriculums ADD Is_Active BIT DEFAULT 1;

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('Subjects') AND name = 'Department')
    ALTER TABLE Subjects ADD Department NVARCHAR(255);

IF NOT EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('Syllabuses') AND name = 'Is_Active')
    ALTER TABLE Syllabuses ADD Is_Active BIT DEFAULT 1;
GO

PRINT 'Schema patch applied.';
GO
