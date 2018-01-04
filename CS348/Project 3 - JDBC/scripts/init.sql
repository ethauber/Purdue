set termout off

-- Initialization

delete from RolesPrivileges;
delete from Privileges;
delete from UsersRoles;
delete from Roles;
delete from Users;

insert into Users values(1, 'admin', 'pass');
insert into Roles values(1, 'ADMIN', 'AK');
insert into UsersRoles values(1, 1);

insert into Privileges values(1, 'INSERT');
insert into Privileges values(2, 'SELECT');

set termout on