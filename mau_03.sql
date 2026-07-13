/* =========================================================
   SEED DATA — CurriculumManagementDB
   Phạm vi mẫu: 3 ngành (Major) — CNTT, Kinh doanh, Marketing
   Cách dùng: chạy SAU KHI đã chạy script tạo schema (DDL) gốc.
   Script này dùng biến @table để lưu UUID vừa tạo, tránh phải
   đoán GUID khi insert dữ liệu có khóa ngoại.
========================================================= */

USE CurriculumManagementDB;
GO

/* =========================================================
   1) USERS — vài người dùng đại diện cho từng vai trò
========================================================= */
DECLARE @admin1        UNIQUEIDENTIFIER = NEWID();
DECLARE @designerIT    UNIQUEIDENTIFIER = NEWID();
DECLARE @designerBus   UNIQUEIDENTIFIER = NEWID();
DECLARE @designerMkt   UNIQUEIDENTIFIER = NEWID();
DECLARE @reviewer1     UNIQUEIDENTIFIER = NEWID();
DECLARE @reviewer2     UNIQUEIDENTIFIER = NEWID();
DECLARE @teacher1      UNIQUEIDENTIFIER = NEWID();
DECLARE @teacher2      UNIQUEIDENTIFIER = NEWID();
DECLARE @teacher3      UNIQUEIDENTIFIER = NEWID();
DECLARE @student1      UNIQUEIDENTIFIER = NEWID();
DECLARE @student2      UNIQUEIDENTIFIER = NEWID();

INSERT INTO Users (User_ID, Full_Name, Email, Password_Hash, Is_Active)
VALUES
(@admin1,      N'Nguyễn Văn An',      'admin@cms.com', 'admin123', 1),
(@designerIT,  N'Trần Thị Bình',      'designer@cms.com', 'designer123', 1),
(@designerBus, N'Lê Hoàng Cường',     'designer1@cms.com', 'designer123', 1),
(@designerMkt, N'Phạm Thị Dung',      'designer2@cms.com', 'designer123', 1),
(@reviewer1,   N'Hoàng Văn Em',       'reviewer@cms.com', 'reviewer123', 1),
(@reviewer2,   N'Vũ Thị Phương',      'reviewer1@cms.com', 'reviewer123', 1),
(@teacher1,    N'Đặng Văn Giang',     'teacher@cms.com', 'teacher123', 1),
(@teacher2,    N'Bùi Thị Hoa',        'teacher1@cms.com', 'teacher123', 1),
(@teacher3,    N'Ngô Văn Inh',        'teacher2@cms.com', 'teacher123', 1),
(@student1,    N'Lý Thị Kim',         'student@cms.com', 'student123', 1),
(@student2,    N'Phan Văn Long',      'student1@cms.com', 'student123', 1);

/* =========================================================
   2) USER_ROLES — gán vai trò theo Roles đã có sẵn (Admin, Designer, Reviewer, Teacher, Student)
========================================================= */
DECLARE @roleAdmin    INT = (SELECT Role_ID FROM Roles WHERE Role_Name = 'Admin');
DECLARE @roleDesigner INT = (SELECT Role_ID FROM Roles WHERE Role_Name = 'Designer');
DECLARE @roleReviewer INT = (SELECT Role_ID FROM Roles WHERE Role_Name = 'Reviewer');
DECLARE @roleTeacher  INT = (SELECT Role_ID FROM Roles WHERE Role_Name = 'Teacher');
DECLARE @roleStudent  INT = (SELECT Role_ID FROM Roles WHERE Role_Name = 'Student');

INSERT INTO User_Roles (User_ID, Role_ID)
VALUES
(@admin1,      @roleAdmin),
(@designerIT,  @roleDesigner),
(@designerBus, @roleDesigner),
(@designerMkt, @roleDesigner),
(@reviewer1,   @roleReviewer),
(@reviewer2,   @roleReviewer),
(@teacher1,    @roleTeacher),
(@teacher2,    @roleTeacher),
(@teacher3,    @roleTeacher),
(@student1,    @roleStudent),
(@student2,    @roleStudent);

/* =========================================================
   3) MAJORS — 3 ngành: CNTT, Kinh doanh, Marketing
========================================================= */
DECLARE @majorIT  UNIQUEIDENTIFIER = NEWID();
DECLARE @majorBus UNIQUEIDENTIFIER = NEWID();
DECLARE @majorMkt UNIQUEIDENTIFIER = NEWID();

INSERT INTO Majors (Major_ID, Major_Code, Major_Name, Description, Is_Active)
VALUES
(@majorIT,  N'IT',  N'Công nghệ thông tin',
    N'Đào tạo kỹ sư/cử nhân CNTT với kiến thức nền tảng về lập trình, hệ thống, dữ liệu và phát triển phần mềm.', 1),
(@majorBus, N'BUS', N'Quản trị kinh doanh',
    N'Đào tạo cử nhân quản trị kinh doanh với kiến thức về quản lý, tài chính, vận hành doanh nghiệp.', 1),
(@majorMkt, N'MKT', N'Marketing',
    N'Đào tạo cử nhân Marketing với kiến thức về nghiên cứu thị trường, truyền thông, thương hiệu và marketing số.', 1);

/* =========================================================
   4) CURRICULUMS — mỗi ngành 1 chương trình đào tạo (khung CTĐT)
========================================================= */
DECLARE @curIT  UNIQUEIDENTIFIER = NEWID();
DECLARE @curBus UNIQUEIDENTIFIER = NEWID();
DECLARE @curMkt UNIQUEIDENTIFIER = NEWID();

INSERT INTO Curriculums
(Curriculum_ID, Major_ID, Curriculum_Code, Curriculum_Name, English_Name, Description,
 Total_Credits, Version, Decision_No, Decision_Date, Created_By, Updated_Date, Is_Active, Status)
VALUES
(@curIT, @majorIT, N'IT-2024', N'Chương trình đào tạo Công nghệ thông tin 2024',
    N'Information Technology Curriculum 2024',
    N'Khung chương trình đào tạo ngành CNTT áp dụng từ năm học 2024-2025.',
    140, N'1.0', N'QD-1201/QD-DHX', '2024-06-01', @designerIT, NULL, 1, 1),

(@curBus, @majorBus, N'BUS-2024', N'Chương trình đào tạo Quản trị kinh doanh 2024',
    N'Business Administration Curriculum 2024',
    N'Khung chương trình đào tạo ngành Quản trị kinh doanh áp dụng từ năm học 2024-2025.',
    135, N'1.0', N'QD-1202/QD-DHX', '2024-06-01', @designerBus, NULL, 1, 1),

(@curMkt, @majorMkt, N'MKT-2024', N'Chương trình đào tạo Marketing 2024',
    N'Marketing Curriculum 2024',
    N'Khung chương trình đào tạo ngành Marketing áp dụng từ năm học 2024-2025.',
    135, N'1.0', N'QD-1203/QD-DHX', '2024-06-01', @designerMkt, NULL, 1, 1);

/* =========================================================
   5) SUBJECTS — môn học cho từng ngành (đại diện, không đủ toàn khung)
========================================================= */
-- CNTT
DECLARE @subProg1   UNIQUEIDENTIFIER = NEWID(); -- Lập trình căn bản
DECLARE @subProg2   UNIQUEIDENTIFIER = NEWID(); -- Lập trình hướng đối tượng
DECLARE @subDB      UNIQUEIDENTIFIER = NEWID(); -- Cơ sở dữ liệu
DECLARE @subDS      UNIQUEIDENTIFIER = NEWID(); -- Cấu trúc dữ liệu & giải thuật
DECLARE @subWeb     UNIQUEIDENTIFIER = NEWID(); -- Phát triển ứng dụng Web
DECLARE @subNet     UNIQUEIDENTIFIER = NEWID(); -- Mạng máy tính

-- Kinh doanh
DECLARE @subMgmt    UNIQUEIDENTIFIER = NEWID(); -- Quản trị học
DECLARE @subFin     UNIQUEIDENTIFIER = NEWID(); -- Tài chính doanh nghiệp
DECLARE @subAcc     UNIQUEIDENTIFIER = NEWID(); -- Nguyên lý kế toán
DECLARE @subHR      UNIQUEIDENTIFIER = NEWID(); -- Quản trị nhân sự
DECLARE @subBL      UNIQUEIDENTIFIER = NEWID(); -- Luật kinh doanh

-- Marketing
DECLARE @subMktBasic UNIQUEIDENTIFIER = NEWID(); -- Nguyên lý Marketing
DECLARE @subConsumer UNIQUEIDENTIFIER = NEWID(); -- Hành vi người tiêu dùng
DECLARE @subDigMkt   UNIQUEIDENTIFIER = NEWID(); -- Marketing số
DECLARE @subBrand    UNIQUEIDENTIFIER = NEWID(); -- Quản trị thương hiệu
DECLARE @subMktRes   UNIQUEIDENTIFIER = NEWID(); -- Nghiên cứu thị trường

INSERT INTO Subjects (Subject_ID, Major_ID, Subject_Code, Subject_Name, English_Name, Credits, Description, Is_Active)
VALUES
-- CNTT
(@subProg1, @majorIT, N'IT101', N'Lập trình căn bản', N'Fundamentals of Programming', 3,
    N'Giới thiệu các khái niệm lập trình cơ bản: biến, vòng lặp, hàm, cấu trúc điều khiển.', 1),
(@subProg2, @majorIT, N'IT102', N'Lập trình hướng đối tượng', N'Object-Oriented Programming', 3,
    N'Các nguyên lý OOP: đóng gói, kế thừa, đa hình, trừu tượng hóa.', 1),
(@subDS, @majorIT, N'IT201', N'Cấu trúc dữ liệu và giải thuật', N'Data Structures and Algorithms', 4,
    N'Các cấu trúc dữ liệu cơ bản và thuật toán xử lý.', 1),
(@subDB, @majorIT, N'IT202', N'Cơ sở dữ liệu', N'Database Systems', 3,
    N'Thiết kế và truy vấn cơ sở dữ liệu quan hệ, SQL.', 1),
(@subWeb, @majorIT, N'IT301', N'Phát triển ứng dụng Web', N'Web Application Development', 3,
    N'Xây dựng ứng dụng web full-stack.', 1),
(@subNet, @majorIT, N'IT302', N'Mạng máy tính', N'Computer Networks', 3,
    N'Nguyên lý mạng máy tính, mô hình OSI/TCP-IP.', 1),

-- Kinh doanh
(@subMgmt, @majorBus, N'BUS101', N'Quản trị học', N'Principles of Management', 3,
    N'Các nguyên lý cơ bản về quản trị tổ chức.', 1),
(@subFin, @majorBus, N'BUS201', N'Tài chính doanh nghiệp', N'Corporate Finance', 3,
    N'Quản lý tài chính, cấu trúc vốn, ra quyết định đầu tư.', 1),
(@subAcc, @majorBus, N'BUS102', N'Nguyên lý kế toán', N'Principles of Accounting', 3,
    N'Các nguyên lý kế toán tài chính cơ bản.', 1),
(@subHR, @majorBus, N'BUS202', N'Quản trị nhân sự', N'Human Resource Management', 3,
    N'Tuyển dụng, đào tạo, đánh giá và phát triển nhân sự.', 1),
(@subBL, @majorBus, N'BUS203', N'Luật kinh doanh', N'Business Law', 3,
    N'Khung pháp lý liên quan đến hoạt động kinh doanh.', 1),

-- Marketing
(@subMktBasic, @majorMkt, N'MKT101', N'Nguyên lý Marketing', N'Principles of Marketing', 3,
    N'Các khái niệm cơ bản về marketing, 4P, phân khúc thị trường.', 1),
(@subConsumer, @majorMkt, N'MKT201', N'Hành vi người tiêu dùng', N'Consumer Behavior', 3,
    N'Các yếu tố ảnh hưởng đến hành vi mua hàng của người tiêu dùng.', 1),
(@subDigMkt, @majorMkt, N'MKT202', N'Marketing số', N'Digital Marketing', 3,
    N'Marketing trên các nền tảng số: SEO, SEM, social media.', 1),
(@subBrand, @majorMkt, N'MKT301', N'Quản trị thương hiệu', N'Brand Management', 3,
    N'Xây dựng và quản lý giá trị thương hiệu.', 1),
(@subMktRes, @majorMkt, N'MKT302', N'Nghiên cứu thị trường', N'Marketing Research', 3,
    N'Phương pháp thu thập và phân tích dữ liệu thị trường.', 1);

/* =========================================================
   6) CURRICULUM_SUBJECTS — gán môn học vào khung CTĐT theo học kỳ
========================================================= */
INSERT INTO Curriculum_Subjects (Curriculum_ID, Subject_ID, Semester_No, Is_Mandatory)
VALUES
-- CNTT
(@curIT, @subProg1, 1, 1),
(@curIT, @subProg2, 2, 1),
(@curIT, @subDS,    2, 1),
(@curIT, @subDB,    3, 1),
(@curIT, @subWeb,   4, 1),
(@curIT, @subNet,   4, 0),

-- Kinh doanh
(@curBus, @subMgmt, 1, 1),
(@curBus, @subAcc,  1, 1),
(@curBus, @subFin,  2, 1),
(@curBus, @subHR,   3, 1),
(@curBus, @subBL,   3, 0),

-- Marketing
(@curMkt, @subMktBasic, 1, 1),
(@curMkt, @subConsumer, 2, 1),
(@curMkt, @subDigMkt,   3, 1),
(@curMkt, @subBrand,    3, 1),
(@curMkt, @subMktRes,   4, 0);

/* =========================================================
   7) SUBJECT_PREREQUISITES — môn tiên quyết
========================================================= */
INSERT INTO Subject_Prerequisites (Subject_ID, Required_Subject_ID)
VALUES
(@subProg2, @subProg1),   -- OOP yêu cầu Lập trình căn bản
(@subDS,    @subProg1),   -- Cấu trúc dữ liệu yêu cầu Lập trình căn bản
(@subWeb,   @subDB),      -- Web app yêu cầu Cơ sở dữ liệu
(@subFin,   @subAcc),     -- Tài chính DN yêu cầu Nguyên lý kế toán
(@subDigMkt, @subMktBasic), -- Marketing số yêu cầu Nguyên lý Marketing
(@subBrand,  @subConsumer); -- Quản trị thương hiệu yêu cầu Hành vi người tiêu dùng

/* =========================================================
   8) POs (Program Objectives) — theo từng Curriculum
========================================================= */
DECLARE @poIT1 UNIQUEIDENTIFIER = NEWID();
DECLARE @poIT2 UNIQUEIDENTIFIER = NEWID();
DECLARE @poBus1 UNIQUEIDENTIFIER = NEWID();
DECLARE @poBus2 UNIQUEIDENTIFIER = NEWID();
DECLARE @poMkt1 UNIQUEIDENTIFIER = NEWID();
DECLARE @poMkt2 UNIQUEIDENTIFIER = NEWID();

INSERT INTO POs (PO_ID, Curriculum_ID, PO_Code, Description)
VALUES
(@poIT1, @curIT, N'PO1', N'Có khả năng phân tích, thiết kế và phát triển phần mềm đáp ứng yêu cầu thực tế.'),
(@poIT2, @curIT, N'PO2', N'Có khả năng làm việc nhóm và thích nghi với công nghệ mới trong môi trường CNTT.'),
(@poBus1, @curBus, N'PO1', N'Có khả năng phân tích và ra quyết định quản trị trong môi trường kinh doanh.'),
(@poBus2, @curBus, N'PO2', N'Có khả năng quản lý tài chính và vận hành doanh nghiệp hiệu quả.'),
(@poMkt1, @curMkt, N'PO1', N'Có khả năng xây dựng và triển khai chiến lược marketing toàn diện.'),
(@poMkt2, @curMkt, N'PO2', N'Có khả năng nghiên cứu thị trường và phân tích hành vi khách hàng.');

/* =========================================================
   9) PLOs (Program Learning Outcomes) — theo từng Curriculum
========================================================= */
DECLARE @ploIT1 UNIQUEIDENTIFIER = NEWID();
DECLARE @ploIT2 UNIQUEIDENTIFIER = NEWID();
DECLARE @ploBus1 UNIQUEIDENTIFIER = NEWID();
DECLARE @ploBus2 UNIQUEIDENTIFIER = NEWID();
DECLARE @ploMkt1 UNIQUEIDENTIFIER = NEWID();
DECLARE @ploMkt2 UNIQUEIDENTIFIER = NEWID();

INSERT INTO PLOs (PLO_ID, Curriculum_ID, PLO_Code, Description)
VALUES
(@ploIT1, @curIT, N'PLO1', N'Vận dụng kiến thức nền tảng về lập trình và cấu trúc dữ liệu để giải quyết bài toán thực tế.'),
(@ploIT2, @curIT, N'PLO2', N'Thiết kế và xây dựng hệ thống cơ sở dữ liệu và ứng dụng web cơ bản.'),
(@ploBus1, @curBus, N'PLO1', N'Áp dụng các nguyên lý quản trị để hoạch định và tổ chức hoạt động doanh nghiệp.'),
(@ploBus2, @curBus, N'PLO2', N'Phân tích báo cáo tài chính và đưa ra quyết định đầu tư cơ bản.'),
(@ploMkt1, @curMkt, N'PLO1', N'Xây dựng kế hoạch marketing dựa trên phân tích thị trường và khách hàng mục tiêu.'),
(@ploMkt2, @curMkt, N'PLO2', N'Thực hiện các chiến dịch marketing số trên nền tảng đa kênh.');

/* =========================================================
   10) PO_PLO_MAPPINGS
========================================================= */
INSERT INTO PO_PLO_Mappings (PO_ID, PLO_ID)
VALUES
(@poIT1, @ploIT1),
(@poIT1, @ploIT2),
(@poIT2, @ploIT2),
(@poBus1, @ploBus1),
(@poBus2, @ploBus2),
(@poMkt1, @ploMkt1),
(@poMkt2, @ploMkt2);

/* =========================================================
   11) SYLLABUSES — đề cương cho 3 môn đại diện (1 mỗi ngành)
========================================================= */
DECLARE @syIT101  UNIQUEIDENTIFIER = NEWID();
DECLARE @syBUS101 UNIQUEIDENTIFIER = NEWID();
DECLARE @syMKT101 UNIQUEIDENTIFIER = NEWID();

INSERT INTO Syllabuses
(Syllabus_ID, Subject_ID, Syllabus_Name, English_Name, Version, Description,
 Time_Allocation, Student_Tasks, Tools, Scoring_Scale, Min_Avg_Mark_To_Pass,
 Decision_No, Approved_Date, Is_Active)
VALUES
(@syIT101, @subProg1, N'Đề cương Lập trình căn bản', N'Fundamentals of Programming Syllabus', N'1.0',
    N'Môn học cung cấp kiến thức nền tảng về lập trình cho sinh viên năm nhất ngành CNTT.',
    N'45 tiết lý thuyết, 30 tiết thực hành',
    N'Sinh viên hoàn thành bài tập lập trình hàng tuần và 1 đồ án nhỏ cuối kỳ.',
    N'Visual Studio Code, Python 3.x, Git',
    N'10', 5.00, N'QD-2024-IT101', '2024-05-15', 1),

(@syBUS101, @subMgmt, N'Đề cương Quản trị học', N'Principles of Management Syllabus', N'1.0',
    N'Môn học cung cấp các nguyên lý quản trị cơ bản áp dụng trong doanh nghiệp.',
    N'45 tiết lý thuyết, 15 tiết thảo luận',
    N'Sinh viên thực hiện bài tập nhóm phân tích case study doanh nghiệp.',
    N'Microsoft Office, các case study thực tế',
    N'10', 5.00, N'QD-2024-BUS101', '2024-05-15', 1),

(@syMKT101, @subMktBasic, N'Đề cương Nguyên lý Marketing', N'Principles of Marketing Syllabus', N'1.0',
    N'Môn học cung cấp các khái niệm và nguyên lý cơ bản trong marketing hiện đại.',
    N'45 tiết lý thuyết, 15 tiết thực hành',
    N'Sinh viên xây dựng một bản kế hoạch marketing cho sản phẩm/dịch vụ giả định.',
    N'Canva, Google Trends, Meta Business Suite',
    N'10', 5.00, N'QD-2024-MKT101', '2024-05-15', 1);

/* =========================================================
   12) CLOs (Course Learning Outcomes) — cho 3 syllabus trên
========================================================= */
DECLARE @cloIT1 UNIQUEIDENTIFIER = NEWID();
DECLARE @cloIT2 UNIQUEIDENTIFIER = NEWID();
DECLARE @cloBus1 UNIQUEIDENTIFIER = NEWID();
DECLARE @cloBus2 UNIQUEIDENTIFIER = NEWID();
DECLARE @cloMkt1 UNIQUEIDENTIFIER = NEWID();
DECLARE @cloMkt2 UNIQUEIDENTIFIER = NEWID();

INSERT INTO CLOs (CLO_ID, Syllabus_ID, CLO_Code, Description)
VALUES
(@cloIT1, @syIT101, N'CLO1', N'Viết được các chương trình đơn giản sử dụng biến, vòng lặp và hàm.'),
(@cloIT2, @syIT101, N'CLO2', N'Áp dụng được tư duy giải quyết vấn đề để thiết kế thuật toán cơ bản.'),
(@cloBus1, @syBUS101, N'CLO1', N'Trình bày được các chức năng cơ bản của quản trị: lập kế hoạch, tổ chức, điều hành, kiểm soát.'),
(@cloBus2, @syBUS101, N'CLO2', N'Phân tích được case study quản trị thực tế và đề xuất giải pháp.'),
(@cloMkt1, @syMKT101, N'CLO1', N'Trình bày được các thành tố của marketing mix (4P/7P).'),
(@cloMkt2, @syMKT101, N'CLO2', N'Xây dựng được một bản kế hoạch marketing cơ bản cho sản phẩm cụ thể.');

/* =========================================================
   13) PLO_CLO_MAPPINGS
========================================================= */
INSERT INTO PLO_CLO_Mappings (PLO_ID, CLO_ID)
VALUES
(@ploIT1, @cloIT1),
(@ploIT1, @cloIT2),
(@ploBus1, @cloBus1),
(@ploBus1, @cloBus2),
(@ploMkt1, @cloMkt1),
(@ploMkt1, @cloMkt2);

/* =========================================================
   14) SESSIONS — buổi học mẫu (3 buổi cho mỗi syllabus)
========================================================= */
INSERT INTO Sessions
(Syllabus_ID, Session_No, Topic, Learning_Teaching_Type, LO, ITU, Student_Materials, Student_Tasks, URLs)
VALUES
-- IT101
(@syIT101, 1, N'Giới thiệu môn học và môi trường lập trình', N'Lý thuyết + Thực hành',
    N'Hiểu cấu trúc môn học; cài đặt môi trường lập trình', N'Giảng trực tiếp, demo cài đặt',
    N'Slide bài giảng tuần 1', N'Cài đặt Python và VS Code trước buổi học', N'https://example-lms.edu.vn/it101/w1'),
(@syIT101, 2, N'Biến, kiểu dữ liệu và toán tử', N'Lý thuyết + Thực hành',
    N'Sử dụng được biến và toán tử cơ bản', N'Giảng + bài tập tại lớp',
    N'Slide bài giảng tuần 2, file bài tập mẫu', N'Hoàn thành 5 bài tập về biến và toán tử', N'https://example-lms.edu.vn/it101/w2'),
(@syIT101, 3, N'Cấu trúc điều khiển (if, loop)', N'Lý thuyết + Thực hành',
    N'Viết được chương trình có rẽ nhánh và lặp', N'Giảng + thực hành nhóm nhỏ',
    N'Slide bài giảng tuần 3', N'Bài tập về nhà: viết chương trình tính tổng/giải phương trình', N'https://example-lms.edu.vn/it101/w3'),

-- BUS101
(@syBUS101, 1, N'Tổng quan về quản trị', N'Lý thuyết',
    N'Hiểu khái niệm và vai trò của quản trị trong tổ chức', N'Giảng trực tiếp, thảo luận mở đầu',
    N'Slide bài giảng tuần 1', N'Đọc chương 1 giáo trình trước buổi học', N'https://example-lms.edu.vn/bus101/w1'),
(@syBUS101, 2, N'Chức năng lập kế hoạch', N'Lý thuyết + Thảo luận',
    N'Phân tích được quy trình lập kế hoạch chiến lược', N'Giảng + thảo luận nhóm',
    N'Slide bài giảng tuần 2, case study A', N'Chuẩn bị phân tích case study A theo nhóm', N'https://example-lms.edu.vn/bus101/w2'),
(@syBUS101, 3, N'Chức năng tổ chức', N'Lý thuyết + Thảo luận',
    N'Thiết kế được cơ cấu tổ chức cơ bản', N'Giảng + bài tập vẽ sơ đồ tổ chức',
    N'Slide bài giảng tuần 3', N'Vẽ sơ đồ tổ chức cho một doanh nghiệp giả định', N'https://example-lms.edu.vn/bus101/w3'),

-- MKT101
(@syMKT101, 1, N'Tổng quan về Marketing', N'Lý thuyết',
    N'Hiểu khái niệm marketing và vai trò trong doanh nghiệp', N'Giảng trực tiếp',
    N'Slide bài giảng tuần 1', N'Đọc tài liệu giới thiệu marketing hiện đại', N'https://example-lms.edu.vn/mkt101/w1'),
(@syMKT101, 2, N'Phân khúc thị trường và khách hàng mục tiêu', N'Lý thuyết + Thực hành',
    N'Xác định được phân khúc và khách hàng mục tiêu cho sản phẩm cụ thể', N'Giảng + bài tập nhóm',
    N'Slide bài giảng tuần 2', N'Phân tích phân khúc thị trường cho 1 sản phẩm tự chọn', N'https://example-lms.edu.vn/mkt101/w2'),
(@syMKT101, 3, N'Marketing mix (4P)', N'Lý thuyết + Thực hành',
    N'Trình bày và áp dụng được 4 thành tố marketing mix', N'Giảng + thảo luận case study',
    N'Slide bài giảng tuần 3, case study thương hiệu', N'Chuẩn bị thuyết trình nhóm về 1 chiến dịch marketing thực tế', N'https://example-lms.edu.vn/mkt101/w3');

/* =========================================================
   15) MATERIALS — tài liệu học tập chính/tham khảo cho 3 syllabus
========================================================= */
INSERT INTO Materials
(Syllabus_ID, Material_Description, Author, Publisher, Published_Date, Edition, ISBN,
 Link,
 Is_Main_Material, Is_Hard_Copy, Is_Online, Notes)
VALUES
(@syIT101, N'Giáo trình nhập môn lập trình với Python', N'Allen B. Downey', N'O''Reilly Media', '2015-01-01', N'3rd Edition', N'978-1491939369',
    N'https://example.com/python-textbook',
    1, 1, 1, N'Tài liệu chính của môn học, có bản PDF trên hệ thống LMS.'),

(@syIT101, N'Tài liệu thực hành lập trình căn bản (nội bộ)', N'Bộ môn CNTT', N'Đại học Nội bộ', '2024-01-01', N'1.0', NULL,
    N'https://example.com/python-lab-material',
    0, 0, 1, N'Tài liệu tham khảo, cập nhật theo từng học kỳ.'),

(@syBUS101, N'Quản trị học - Lý thuyết và thực hành', N'Stephen P. Robbins', N'Pearson', '2017-01-01', N'14th Edition', N'978-0134237473',
    N'https://example.com/management-book',
    1, 1, 0, N'Tài liệu chính, sinh viên có thể mượn tại thư viện.'),

(@syBUS101, N'Tuyển tập case study quản trị doanh nghiệp Việt Nam', N'Nhiều tác giả', N'NXB Kinh tế', '2022-01-01', N'1.0', NULL,
    N'https://example.com/case-study-vn',
    0, 0, 1, N'Tài liệu tham khảo cho phần thảo luận case study.'),

(@syMKT101, N'Principles of Marketing', N'Philip Kotler, Gary Armstrong', N'Pearson', '2020-01-01', N'18th Edition', N'978-0135243663',
    N'https://example.com/marketing-book',
    1, 1, 1, N'Tài liệu chính của môn học.'),

(@syMKT101, N'Báo cáo xu hướng Digital Marketing Việt Nam', N'We Are Social & Hootsuite', N'We Are Social', '2024-01-01', N'1.0', NULL,
    N'https://example.com/digital-marketing-report',
    0, 0, 1, N'Tài liệu tham khảo, cập nhật số liệu thị trường mới nhất.');

/* =========================================================
   16) TEACHER_MATERIALS — tài liệu riêng do giảng viên upload
========================================================= */
INSERT INTO Teacher_Materials
(User_ID, Syllabus_ID, Material_Type, Material_Name, Material_URL, Description, Is_Active)
VALUES
(@teacher1, @syIT101, N'Slide', N'Slide bài giảng IT101 - Tuần 1-3', 'https://drive.example.com/it101-slides',
    N'Bộ slide bài giảng do giảng viên Đặng Văn Giang biên soạn.', 1),
(@teacher1, @syIT101, N'Video', N'Video hướng dẫn cài đặt môi trường lập trình', 'https://youtube.example.com/it101-setup',
    N'Video hướng dẫn cài Python và VS Code cho sinh viên.', 1),

(@teacher2, @syBUS101, N'Slide', N'Slide bài giảng BUS101 - Chức năng quản trị', 'https://drive.example.com/bus101-slides',
    N'Bộ slide do giảng viên Bùi Thị Hoa biên soạn.', 1),

(@teacher3, @syMKT101, N'Slide', N'Slide bài giảng MKT101 - Marketing Mix', 'https://drive.example.com/mkt101-slides',
    N'Bộ slide do giảng viên Ngô Văn Inh biên soạn.', 1),
(@teacher3, @syMKT101, N'Tài liệu bổ trợ', N'Bộ case study chiến dịch marketing nổi bật 2024', 'https://drive.example.com/mkt101-cases',
    N'Tổng hợp case study thực tế dùng cho thảo luận nhóm.', 1);

/* =========================================================
   17) CURRICULUM_ASSIGNMENTS — phân công Designer/Reviewer cho từng CTĐT
========================================================= */
INSERT INTO Curriculum_Assignments (Curriculum_ID, User_ID, Assignment_Type, Assigned_By)
VALUES
(@curIT,  @designerIT,  N'Designer', @admin1),
(@curIT,  @reviewer1,   N'Reviewer', @admin1),
(@curBus, @designerBus, N'Designer', @admin1),
(@curBus, @reviewer2,   N'Reviewer', @admin1),
(@curMkt, @designerMkt, N'Designer', @admin1),
(@curMkt, @reviewer1,   N'Reviewer', @admin1);

/* =========================================================
   18) REVIEWS — kết quả phản hồi của Reviewer cho từng CTĐT
========================================================= */
INSERT INTO Reviews (Curriculum_ID, Reviewer_ID, Is_Approved, Comment, Review_Date)
VALUES
(@curIT, @reviewer1, 1, N'Chương trình đào tạo CNTT đáp ứng yêu cầu, cấu trúc môn học hợp lý theo từng học kỳ.', '2024-06-10'),
(@curBus, @reviewer2, 1, N'Khung CTĐT Quản trị kinh doanh phù hợp với mục tiêu chuẩn đầu ra, đề nghị bổ sung thêm môn về khởi nghiệp.', '2024-06-11'),
(@curMkt, @reviewer1, 0, N'Cần làm rõ thêm chuẩn đầu ra của môn Marketing số trước khi phê duyệt.', '2024-06-12');

/* =========================================================
   19) AUDIT_LOGS — ví dụ ghi log một số hành động
========================================================= */
INSERT INTO Audit_Logs (User_ID, Entity_Name, Entity_ID, Action, Old_Value, New_Value)
VALUES
(@designerIT, N'Curriculums', @curIT, N'CREATE', NULL, N'Tạo mới chương trình đào tạo CNTT 2024'),
(@designerBus, N'Curriculums', @curBus, N'CREATE', NULL, N'Tạo mới chương trình đào tạo Quản trị kinh doanh 2024'),
(@designerMkt, N'Curriculums', @curMkt, N'CREATE', NULL, N'Tạo mới chương trình đào tạo Marketing 2024'),
(@reviewer1, N'Reviews', @curIT, N'APPROVE', N'Pending', N'Approved'),
(@reviewer2, N'Reviews', @curBus, N'APPROVE', N'Pending', N'Approved'),
(@reviewer1, N'Reviews', @curMkt, N'REJECT', N'Pending', N'Rejected - cần bổ sung chuẩn đầu ra');

GO

PRINT N'Đã thêm dữ liệu mẫu cho 3 ngành: CNTT, Quản trị kinh doanh, Marketing.';