set serveroutput on size 32000
SET SERVEROUTPUT ON

-- pro_AddIntern: Write a procedure to add a student's internship. The input parameters: (StudentName, CompName, RecName, OfferYear). You can assume that the company and the recruiter exist in the database. You do not need to consider duplicate names. The result will be a new record in the database.

--have to type fstudentname with ''
--procedure is case sensitive
CREATE OR REPLACE PROCEDURE tester (frecid in number) as
frecname varchar2(32767);
firstschool boolean := true;
fnsi number;
favggrade number;
begin
--output recruiter id
dbms_output.put_line('RecID: ' || frecid);

--get recruiter's name
select recname into frecname from recruiter where recid = frecid;
--output recruiter's name
dbms_output.put_line('RecName: ' || frecname);

--output schools with most interns
dbms_output.put('School with most interns: ');
--loop
for cfmi in (select abcd.schoolname from (select ab.schoolid, ab.schoolname, count(ab.schoolid) as numIfromschool from (select a.recid, a.studentid, a.offeryear, sc.schoolid, sc.schoolname from (select i.recid, i.compid, i.offeryear, st.studentid, st.schoolid from internship i left join student st on i.studentid = st.studentid) a left join school sc on a.schoolid = sc.schoolid where a.recid = frecid) ab group by ab.schoolid, ab.schoolname) abcd where abcd.numIfromschool = (select max(count(abc.schoolid)) from (select a.recid, a.studentid, a.offeryear, sc.schoolid, sc.schoolname from (select i.recid, i.compid, i.offeryear, st.studentid, st.schoolid from internship i left join student st on i.studentid = st.studentid) a left join school sc on a.schoolid = sc.schoolid where a.recid = frecid) abc group by abc.schoolid) order by abcd.schoolname)
loop
	if(firstschool) then
		dbms_output.put(cfmi.schoolname);
		firstschool := false;
	else
		dbms_output.put('/' || cfmi.schoolname);
	end if;
end loop;
dbms_output.new_line();

--setup for the rest of the output
dbms_output.put_line('CompanyName    NumberOfInterns    AverageStudentGrade');
dbms_output.put_line('-----------    ---------------    -------------------');

--get company names and output with concat of / if more than one in alphabetical order
for c in (select distinct i.recid, i.compid, co.compname from internship i left join company co on i.compid = co.compid where i.recid = frecid order by co.compname)
loop
	--output company name
	dbms_output.put(c.compname);

	--get number of student interns per company and output
	select count(studentid) as nsi into fnsi from internship where compid = c.compid;
	dbms_output.put(lpad(fnsi, length(fnsi) + 17 - length(c.compname)));
	
	--get average grades of students per company and output
	select avg(st.grade) as avggrade into favggrade from internship i left join student st on i.studentid = st.studentid where i.compid = c.compid;
	dbms_output.put(lpad(trunc(favggrade, 2), length(trunc(favggrade, 2)) + 17 - length(fnsi)));
	dbms_output.new_line();
end loop;
end;
/
begin
	tester(3);
end;
/

-- select avg(st.grade) from internship i left join student st on i.studentid = st.studentid where i.compid = 4;

-- select distinct i.recid, i.studentid from internship i where i.recid = 2;

-- select abcd.schoolname from (select ab.schoolid, ab.schoolname, count(ab.schoolid) as numIfromschool from (select a.recid, a.studentid, a.offeryear, sc.schoolid, sc.schoolname from (select i.recid, i.compid, i.offeryear, st.studentid, st.schoolid from internship i left join student st on i.studentid = st.studentid) a left join school sc on a.schoolid = sc.schoolid where a.recid = 2) ab group by ab.schoolid, ab.schoolname) abcd where abcd.numIfromschool = (select max(count(abc.schoolid)) from (select a.recid, a.studentid, a.offeryear, sc.schoolid, sc.schoolname from (select i.recid, i.compid, i.offeryear, st.studentid, st.schoolid from internship i left join student st on i.studentid = st.studentid) a left join school sc on a.schoolid = sc.schoolid where a.recid = 2) abc group by abc.schoolid) order by abcd.schoolname;

-- show errors of procedure by: show <procedure name> err

 -- execute pro_AddIntern('student7','Comp2','Rec2',2015);
 -- execute pro_AddIntern('student7','Comp2','Rec2',2016);
 -- execute pro_AddIntern('student5','Comp2','Rec2',2015);
 -- execute pro_AddIntern('student5','Comp2','Rec2',2013);
--websites
-- https://www.techonthenet.com/oracle/functions/lpad.php
-- http://www.oracle.com/technetwork/issue-archive/2013/13-mar/o23plsql-1906474.html
-- https://www.tutorialspoint.com/plsql/plsql_if_then.htm