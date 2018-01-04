create table Company(CompId integer, CompName varchar(30), Address varchar(200), primary key(CompId));
create table Recruiter(RecId integer, RecName varchar(30), primary key(RecId));
create table School(SchoolId integer, SchoolName varchar(30), Address varchar(200), primary key(SchoolId));
create table Student(StudentId integer, StudentName varchar(30), SchoolId integer, BirthDate date, Grade number(4,2), primary key(StudentId), foreign key(SchoolId) references School(SchoolId));
create table Job(JobId integer, CompId integer, JobNum integer, JobTitle varchar(30), Salary integer,OfferYear number(4),  primary key(JobId), foreign key(CompId) references Company(CompId));
create table Internship(StudentId integer, CompId integer, RecId integer,   OfferYear number(4),  primary key(StudentId, CompId,OfferYear), foreign key(CompId) references Company(CompId), foreign key(RecId) references Recruiter(RecId));
create table JobApplication(JobId integer, StudentId integer, ApplicationDate date, primary key(JobId, StudentId), foreign key(StudentId) references Student(StudentId), foreign key(JobId) references Job(JobId));
