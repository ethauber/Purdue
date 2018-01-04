--Company
insert into Company(CompId, CompName, Address) values (1, 'Comp1', 'comp address1');
insert into Company(CompId, CompName, Address) values (2, 'Comp2', 'comp address2');
insert into Company(CompId, CompName, Address) values (3, 'Comp3', 'comp address3');
--Recruiter
insert into Recruiter(RecId, RecName) values (1, 'Rec1');
insert into Recruiter(RecId, RecName) values (2, 'Rec2');
insert into Recruiter(RecId, RecName) values (3, 'Rec3');
insert into Recruiter(RecId, RecName) values (4, 'Rec4');
--School
insert into School(SchoolId, SchoolName, Address) values (1, 'purdue', 'school address1');
insert into School(SchoolId, SchoolName, Address) values (2, 'iu', 'school address2');
insert into School(SchoolId, SchoolName, Address) values (3, 'school3', 'school address3');
insert into School(SchoolId, SchoolName, Address) values (4, 'school4', 'school address4');
--Student
insert into Student(StudentId, StudentName, SchoolId, BirthDate, Grade) values (1, 'student1', 1, to_date('19900101','YYYYMMDD'),90.90);
insert into Student(StudentId, StudentName, SchoolId, BirthDate, Grade) values (2, 'student2', 1, to_date('19910101','YYYYMMDD'),80.80);
insert into Student(StudentId, StudentName, SchoolId, BirthDate, Grade) values (3, 'student3', 2, to_date('19920202','YYYYMMDD'),70.70);
insert into Student(StudentId, StudentName, SchoolId, BirthDate, Grade) values (4, 'student4', 3, to_date('19930303','YYYYMMDD'),60.60);
insert into Student(StudentId, StudentName, SchoolId, BirthDate, Grade) values (5, 'student5', 3, to_date('19940404','YYYYMMDD'),50.50);
insert into Student(StudentId, StudentName, SchoolId, BirthDate, Grade) values (6, 'student6', 1, to_date('19950505','YYYYMMDD'),75.75);
insert into Student(StudentId, StudentName, SchoolId, BirthDate, Grade) values (7, 'student7', 4, to_date('19960606','YYYYMMDD'),40.40);
--Job
insert into Job(JobId, CompId, JobNum, JobTitle, Salary, OfferYear) values (1, 1, 11,'job1',150000,2017);
insert into Job(JobId, CompId, JobNum, JobTitle, Salary, OfferYear) values (2, 1, 22,'job2',130000,2011);
insert into Job(JobId, CompId, JobNum, JobTitle, Salary, OfferYear) values (3, 1, 22,'job3',110000,2016);
insert into Job(JobId, CompId, JobNum, JobTitle, Salary, OfferYear) values (4, 2, 22,'job4',100000,2017);
insert into Job(JobId, CompId, JobNum, JobTitle, Salary, OfferYear) values (5, 3, 22,'job5',120000,2015);
insert into Job(JobId, CompId, JobNum, JobTitle, Salary, OfferYear) values (6, 3, 22,'job6',160000,2014);
--Internship
insert into  Internship(StudentId, CompId, RecId, OfferYear) values (1, 1, 1,2015);
insert into  Internship(StudentId, CompId, RecId, OfferYear) values (1, 1, 1,2014);
insert into  Internship(StudentId, CompId, RecId, OfferYear) values (2, 2, 2,2015);
insert into  Internship(StudentId, CompId, RecId, OfferYear) values (3, 3, 3,2014);
--JobApplication
insert into  JobApplication(JobId, StudentId, ApplicationDate) values (1,1,to_date('20170101','YYYYMMDD'));
insert into  JobApplication(JobId, StudentId, ApplicationDate) values (2,2,to_date('20160112','YYYYMMDD'));
insert into  JobApplication(JobId, StudentId, ApplicationDate) values (3,3,to_date('20150111','YYYYMMDD'));
insert into  JobApplication(JobId, StudentId, ApplicationDate) values (1,3,to_date('20150901','YYYYMMDD'));
insert into  JobApplication(JobId, StudentId, ApplicationDate) values (2,4,to_date('20140201','YYYYMMDD'));
