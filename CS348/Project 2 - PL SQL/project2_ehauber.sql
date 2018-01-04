--run entirety of code first with @project2_ehauber
--after this then "execute <procedure name>(arguements if any)" may be used
set serveroutput on size 32000
SET SERVEROUTPUT ON

/*1- (ctrl+q block comments and uncomments code: notpad++)*/
CREATE OR REPLACE PROCEDURE pro_AvgGrade as
--DECLARE <- don't need (causes compliation error)
/* declarations */
--scount number;

minAvg number := 0;
maxAvg number := 0;
tempAvg number := 0;

begin
/* code */
-- select count(schoolid) into scount from school;
-- dbms_output.put_line('School count is: ' || scount);

--get minAvgGrade and maxAvgGrade to calculate and output for bins
--max
select max(avgGrade) as maxGrade into maxAvg from (select avg(stu.grade) as avgGrade from student stu left join school sc on stu.schoolid = sc.schoolid group by sc.schoolid);
--min
select min(avgGrade) as minGrade into minAvg from (select avg(stu.grade) as avgGrade from student stu left join school sc on stu.schoolid = sc.schoolid group by sc.schoolid);
--initial setup output
dbms_output.put('SCHOOLNAME  AVGGRADE:  ');
--output bins
tempAvg := minAvg;
WHILE tempAvg <= maxAvg LOOP
	dbms_output.put('>' || trunc(tempAvg,-1));
	tempAvg := tempAvg + 10;
	dbms_output.put(',<=' || trunc(tempAvg,-1) || '  ');
END LOOP;
dbms_output.put_line('');
--output dashes underneath schoolname and bins
--initial setup for SCHOOLNAME AVGGRADE:  
dbms_output.put('----------           ');
tempAvg := minAvg;
--ouput lines for bins
WHILE tempAvg <= maxAvg LOOP
	dbms_output.put('  --------');
	tempAvg := tempAvg + 10;
END LOOP;
dbms_output.put_line('');
--prints schools with their average by putting x under bin according to average
--The x is located as if it were on a number line. Smaller within the bin will be farther left and when greater within bin the x will be farther right. 
FOR ic in (select sc.schoolname, avg(st.grade) as avggrade from student st left join school sc on st.schoolid = sc.schoolid group by schoolname order by schoolname)
loop 
	dbms_output.put_line(ic.schoolname || lpad('X', (24 - length(ic.schoolname)) + (ic.avggrade - minAvg) ));
end loop;

end;
/
/* actually run the procedure */
begin
	pro_AvgGrade;
end;
/


-- /*2-*/
CREATE OR REPLACE PROCEDURE pro_DispInternSummary as
maxnumi number := 0;
minnumi number := 0;
median number := 0;
markmedian boolean := false;
padspace number;
begin
--initial setup output
dbms_output.put_line('numberOfInternships | #student');
--calculate median
select max(count(i.studentid)) as maxnum into maxnumi from student stu left join internship i on stu.studentid=i.studentid group by stu.studentid;
select min(count(i.studentid)) as minnum into minnumi from student stu left join internship i on stu.studentid=i.studentid group by stu.studentid;
--dbms_output.put_line('max='||maxnumi||' min='||minnumi);
if mod(maxnumi-minnumi,2) = 0 then
	--even in zero based means just pick middle (aka odd elements)
	median := maxnumi/2;
	markmedian := true;
	--odd in zero based means pick average of middle two (aka even elements)
	--this will always be a decimal and the handout says not to mark this
end if;
--ic = implicit cursor (they are the best)
FOR ic in (select a.numInternships, count(a.studentid) as numstudent from (select stu.studentid, count(i.studentid) as numInternships from student stu left join internship i on stu.studentid=i.studentid group by stu.studentid) a group by a.numInternships order by a.numInternships)
loop
	padspace := 23 - length(ic.numInternships);
	if(padspace <= 0) then
		padspace := 1;
	end if;
	dbms_output.put(ic.numInternships|| lpad(ic.numstudent, padspace));
	if ic.numInternships = median and markmedian then
		dbms_output.put('<--median');
	end if;
	dbms_output.put_line('');
end loop;
end;
/
begin
	pro_DispInternSummary;
end;
/


-- /*3-*/
--procedure is case sensitive and inputs must me of form '<SomeCaseSensitiveArgument>'
--the procedure does not have a default output and must be run by user
--run procedure with execute
--example: execute pro_AddIntern('student7','Comp2','Rec2',2015);
CREATE OR REPLACE PROCEDURE pro_AddIntern (fstudentname in varchar2, fcompname in varchar2, frecname in varchar2, fofferyear in number) as
fstudentid number;
fcompid number;
frecid number;
begin
select studentid into fstudentid from student where studentname = fstudentname;
--dbms_output.put_line('fstudentid=' || fstudentid);
select compid into fcompid from company where compname = fcompname;
select recid into frecid from recruiter where recname = frecname;
--dbms_output.put_line('fcompid=' || fcompid || 'frecid=' || frecid);
Insert Into Internship(StudentId, CompId, RecId, OfferYear) values (fstudentid, fcompid, frecid, fofferyear);
end;
/

-- /*4-*/
CREATE OR REPLACE PROCEDURE pro_DispCompany as
fcompid number;
fstudentid number;
fcompname varchar2(25);
fcompaddr varchar2(25);
fnsi number;
favg number;
padspace number;
lenOfSchools number;
firstschool boolean := true;
begin
--initial setup output
dbms_output.put_line('CompanyName  Address      NumOfStundentInerns      School      AverageGrade');
dbms_output.put_line('-----------  -------      -------------------      ------      ------------');
--retrieve information from the company table
for c in (select * from company)
loop
	--get the company name and address
	padspace := 13 - length(c.compname);
	if(padspace <= 0) then
		padspace := 1;
	end if;
	dbms_output.put(c.compname || lpad(c.address, length(c.address) + padspace));
	
	--get the number of student interns
	select count(studentid) as nsi into fnsi from internship i where c.compid = i.compid;
	padspace := 13 - length(c.address) + 1;
	if(padspace <= 0) then
		padspace := 1;
	end if;
	dbms_output.put(lpad(fnsi, length(fnsi) + padspace));
	
	if(fnsi != 0) then
		--get the schools with the most number of internships with compid
		lenOfSchools := 0;
		for c2 in (select sc.schoolname from school sc right join (select c.schoolid, c.MaxNumStuAtSch from (select a.schoolid, count(a.studentid) as MaxNumStuAtSch from student a right join (select i.studentid from internship i where i.compid = 2) b on a.studentid = b.studentid group by a.schoolid order by MaxNumStuAtSch desc) c where c.MaxNumStuAtSch = (select max(d.MaxNumStuAtSch2) as theMax from (select a.schoolid, count(a.studentid) as MaxNumStuAtSch2 from student a right join (select i.studentid from internship i where i.compid = c.compid) b on a.studentid = b.studentid group by a.schoolid order by MaxNumStuAtSch2 desc) d)) abc on sc.schoolid = abc.schoolid order by sc.schoolname)
		loop
			if(firstschool) then
				lenOfSchools := length(c2.schoolname);
				padspace := 19 - length(fnsi);
				if(padspace <= 0) then
					padspace := 1;
				end if;
				dbms_output.put(lpad(c2.schoolname, length(c2.schoolname) + padspace));
				firstschool := false;
			else
				lenOfSchools := lenOfSchools + length(c2.schoolname) + 1;
				dbms_output.put('/' || c2.schoolname);
			end if;
		end loop;
		firstschool := true;
		
		--get the average to output
		select trunc(avg(grade),2) as avggrade into favg from (select studentid from internship i where i.compid = c.compid) b left join student s on b.studentid = s.studentid;
		padspace := 19 - lenOfSchools;
		if(padspace <= 0) then
			padspace := 1;
		end if;
		dbms_output.put(lpad(favg, length(favg) + padspace)); -- needs changing
	end if;
	dbms_output.new_line();
	--
	
end loop;
end;
/
begin
	pro_DispCompany;
end;
/

-- /*5-*/
CREATE OR REPLACE PROCEDURE pro_SearchStudent (fstudentid in number) as
fstudentname varchar2(32767);
fschoolid number;
fschoolname varchar2(32767);
fgrade number;
fnsi number;
fnsja number;
padspace number;
begin
--initial setup output
dbms_output.put_line('StudentId  StudentName    School    Grade  NumOfInternships  NumOfJobApp');
dbms_output.put_line('---------  -----------    ------    -----  ----------------  -----------');

--output student id
dbms_output.put(fstudentid);
--get student name, schoolid, and grade
select studentname, schoolid, grade into fstudentname, fschoolid, fgrade from student where studentid = fstudentid;

--output student name
padspace := 12 - length(fstudentid);
if(padspace <= 0) then
	padspace := 1;
end if;
dbms_output.put(lpad(fstudentname, length(fstudentname) + padspace));
--get school name with schoolid
select schoolname into fschoolname from school where schoolid = fschoolid;

--output school name
padspace := 14 - length(fstudentname);
if(padspace <= 0) then
	padspace := 1;
end if;
dbms_output.put(lpad(fschoolname, length(fschoolname) + padspace));

--output grade 
padspace := 10 - length(fschoolname);
if(padspace <= 0) then
	padspace := 1;
end if;
dbms_output.put(lpad(fgrade, length(fgrade) + padspace));
--get number of student's internships (nsi)
select count(studentid) as nsi into fnsi from internship where studentid = fstudentid;

--output nsi
padspace := 13 - length(fgrade);
if(padspace <= 0) then
	padspace := 1;
end if;
dbms_output.put(lpad(fnsi, length(fnsi) + padspace));
--get number of student's job applications (nsja)
select count(studentid) as nsja into fnsja from jobapplication where studentid = fstudentid;

--output function's nsja
padspace := 16 - length(fnsi);
if(padspace <= 0) then
	padspace := 1;
end if;
dbms_output.put(lpad(fnsja, length(fnsja) + padspace));
dbms_output.new_line();
end;
/
begin
	--default outputs student7
	pro_SearchStudent(7);
end;
/

-- /*6-*/
CREATE OR REPLACE PROCEDURE pro_SearchRecuiter (frecid in number) as
frecname varchar2(32767);
firstschool boolean := true;
fnsi number;
favggrade number;
padspace number;
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
	padspace := 17 - length(c.compname);
	if(padspace <= 0) then
	padspace := 1;
	end if;
	dbms_output.put(lpad(fnsi, length(fnsi) + padspace));
	
	--get average grades of students per company and output
	select avg(st.grade) as avggrade into favggrade from internship i left join student st on i.studentid = st.studentid where i.compid = c.compid;
	padspace := 17 - length(fnsi);
	if(padspace <= 0) then
	padspace := 1;
	end if;
	dbms_output.put(lpad(trunc(favggrade, 2), length(trunc(favggrade, 2)) + padspace));
	dbms_output.new_line();
end loop;
end;
/
begin
	--default output with recruiter id equal to 3
	pro_SearchRecuiter(3);
end;
/