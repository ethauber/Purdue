set termout off

-- Admin Tables

create table Users(
	UserId integer,
	Username varchar(32),
	Password varchar(32),
	constraint user_unique unique (Username),
	primary key (UserId));
	
create table Roles(
	RoleId integer,
	RoleName varchar(32),
	EncryptionKey varchar(32),
	constraint role_unique unique (RoleName),
	primary key (RoleId));
	
create table UsersRoles(
	UserId integer,
	RoleId integer,
	primary key (userId, RoleId),
	foreign key (UserId) references Users(UserId),
	foreign key (RoleId) references Roles(RoleId));
	
create table Privileges(
	PrivId integer,
	PrivName varchar(32),
	constraint priv_unique unique (PrivName),
	primary key (PrivId));
	
create table RolesPrivileges(
	RoleId integer,
	PrivId integer,
	TableName varchar(32),
	primary key(RoleId, PrivId, TableName),
	foreign key (RoleId) references Roles(RoleId),
	foreign key (PrivId) references Privileges(PrivId)); 

----------------------------------------------------------------------------------------------

-- User Tables

create table Companies(
	CompId integer,
	CompName varchar(32),
	Address varchar(256),
	EncryptedColumn number(10,0),
	OwnerRole number(10, 0),
	primary key (CompId));
	
create table Schools(
	SchoolId integer,
	SchoolName varchar(32),
	Address varchar(256),
	EncryptedColumn number(10,0),
	OwnerRole number(10, 0),
	primary key (SchoolId));
	
create table Students(
	StudentId integer,
	StudentName varchar(32),
	SchoolId integer,
	BirthDate date,
	Grade number(4,2),
	EncryptedColumn number(10,0),
	OwnerRole number(10, 0),
	primary key (StudentId),
	foreign key (SchoolId) references Schools(SchoolId));
	
create table Internships(
	StudentId integer,
	CompId integer,
	OfferYear number(4),
	EncryptedColumn number(10,0),
	OwnerRole number(10, 0),
	primary key (StudentId, CompId, OfferYear),
	foreign key (StudentId) references Students(StudentId),
	foreign key (CompId) references Companies(CompId));
	
set termout on
