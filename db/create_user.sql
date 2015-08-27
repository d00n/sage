#create database sage;
create user 'sage_rw'@'%' identified by 'sk00bysnack99';
create user 'sage_rw'@'localhost' identified by 'sk00bysnack99';
grant all on sage.* to 'sage_rw'@'%';
grant all on sage.* to 'sage_rw'@'localhost';
