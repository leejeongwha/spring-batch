CREATE TABLE board_user ( 
 id varchar(40) primary key,
 passwd varchar(40) not null,
 user_name varchar(30) not null,
 age integer not null,
 role varchar(15) not null
);
