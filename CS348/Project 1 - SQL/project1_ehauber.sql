------Query1
select sch.schoolname, nvl(stu.avgGrade, 0) as averagegrade from school sch left join (select s1.schoolid, trunc(avg(s1.grade), 2) as avgGrade from student s1 where s1.grade >= 60 group by schoolid) stu on sch.schoolid=stu.schoolid order by nvl(stu.avgGrade, 0) desc;
------Query2
select stu.studentid, stu.studentname, count(i.studentid) as NumOfInternships from student stu left join internship i on stu.studentid=i.studentid where stu.birthdate=(select min(birthdate) from student) group by stu.studentid, stu.studentname, stu.birthdate order by stu.studentname;
------Query3
select stu.studentid, stu.studentname, count(i.studentid) as NumOfCompanies from student stu left join jobapplication i on stu.studentid=i.studentid group by stu.studentid, stu.studentname, stu.birthdate order by stu.studentname;
------Query4
select candj.compname, candj.NumberOfJobs from (select company.compname, count(job.compid) as NumberOfJobs from job right join company on job.compid=company.compid where job.offeryear=2015 OR job.offeryear=2016 OR job.offeryear=2014 group by job.compid, company.compname) candj where candj.numberofjobs=(select max(count(job.compid)) from job where job.offeryear=2015 or job.offeryear=2016 or job.offeryear=2014 group by job.compid) order by candj.compname; 
------Query5
select compname, numberofjobs from (select compname, count(case when offeryear in ('2016', '2014', '2015') then 1 else null end) as numberofjobs from (select company.compname, job.compid, job.offeryear from job right join company on job.compid=company.compid) group by compname) where rownum <= 3 order by compname;
------Query6
select a.compname, a.numofjobs, b.numinternships from (select c.compname, count(c.compid) as numofjobs, c.compid from job full outer join company c on job.compid=c.compid group by c.compname, c.compid) a join (select compid, count(compid) as numinternships from internship natural join company group by compid) b on a.compid = b.compid order by a.numofjobs desc;
------Query7
select a.studentid, a.studentname, trunc(months_between(sysdate, a.birthdate)/12) as age, nvl(count(a.offeryear),0) as numofinernships from (select s.studentname, s.birthdate, s.studentid, s.schoolid, i.offeryear from student s left join internship i on s.studentid=i.studentid) a left join (select schoolid, schoolname from school) b on a.schoolid = b.schoolid where lower(b.schoolname)='purdue' group by a.studentid, a.studentname, a.birthdate order by a.studentname;
------Query8
select recid, recname, numofcompanies from (select i.recid, count(i.recid) as numofcompanies, r.recname from internship i left join recruiter r on i.recid=r.recid group by i.recid, r.recname) where numofcompanies >= 2 order by recname;
------Query9
select a.jobnum, a.jobtitle, a.salary, a.compname, b.numofstudents from (select j.jobid, j.jobnum, j.jobtitle, j.salary, c.compname from job j left join company c on j.compid=c.compid where j.offeryear='2017') a join (select jobid, count(jobid) as NumOfStudents from jobapplication where extract(year from to_date(applicationdate, 'dd-mon-rr'))= '2017' group by jobid) b on a.jobid=b.jobid order by a.jobnum, a.jobtitle;
------Query10
select a.schoolname, b.failedcount from school a join (select schoolid, count(schoolid) as failedcount from student where grade < 60 group by schoolid) b on a.schoolid = b.schoolid;