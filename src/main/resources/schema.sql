/* create database graduates_site character set 'utf8mb4_0900_ai_ci'; */
/* Если при коннекте ругается на неверное время */
set global time_zone = '-3:00';

create table users (
    id int primary key auto_increment,
    email varchar(100) not null,
    name varchar(100) not null,
    surname varchar(100) not null,
    second_name varchar(100) not null,
    password varchar(256) not null,
    phone varchar(50) not null
);

create table user_role (
    id int primary key auto_increment,
    is_student bool not null,
    is_scientific_advisor bool not null,
    is_admin bool not null,
    is_head_of_cathedra bool not null,
    foreign key (id) references users (id)
);

create table cathedras (
    id int primary key auto_increment,
    cathedra_name varchar (100) not null
);

create table scientific_advisor_data (
    id int primary key auto_increment,
    cathedra int not null,
    foreign key (id) references user_role (id),
    foreign key (cathedra) references cathedras (id)
);

create table student_type (
    id int primary key auto_increment,
    type varchar(100) not null
);

create table student_group (
    id int primary key auto_increment,
    student_group varchar (100) not null
);

create table student_data (
    id int primary key auto_increment,
    student_group int not null,
    cathedra int not null,
    type int not null,
    foreign key (cathedra) references cathedras (id),
    foreign key (type) references student_type (id),
    foreign key (student_group) references student_group (id)
);

create table project_type (
    id int primary key auto_increment,
    type varchar (100)
);

create table project (
    id int primary key auto_increment,
    type int not null,
    name varchar (100),
    scientific_advisor_id int not null,
    description varchar(512) default 'Описание отсутствует',
    foreign key (type) references project_type (id),
    foreign key (scientific_advisor_id) references scientific_advisor_data (id)
);

create table occupied_students (
    id int primary key auto_increment,
    student_id int not null,
    project_id int not null,
    foreign key (student_id) references student_data (id),
    foreign key (project_id) references project (id)
);

create table document_type (
    id int primary key auto_increment,
    type varchar (100) not null
);

create table view_rights (
    id int primary key auto_increment,
    is_only_for_me bool not null,
    is_only_for_scientific_advisors bool not null,
    is_only_for_my_students bool not null,
    is_for_all_students bool not null,
    is_for_all bool not null
);

create table document (
    id int primary key auto_increment,
    creator_id int not null,
    name varchar (100) not null,
    document_path varchar (256) not null,
    creation_date date not null,
    type int not null,
    description varchar (512) default 'Описание отсутствует',
    foreign key (id) references view_rights (id),
    foreign key (type) references document_type (id),
    foreign key (creator_id) references users(id)
);

create table document_version (
    id int primary key auto_increment,
    editor int not null,
    document int not null,
    edition_date date not null,
    edition_description varchar (512) default 'Описание изменения отсутствует',
    this_version_document_path varchar (256) not null,
    foreign key (editor) references users (id),
    foreign key (document) references document (id)
);

create table common_chat (
    id int primary key auto_increment,
    sender int not null,
    send_date date not null,
    message varchar (512) not null,
    foreign key (sender) references users(id)
);