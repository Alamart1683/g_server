/* create database graduates_site character set 'utf8mb4_0900_ai_ci'; */
/* Если при коннекте ругается на неверное время */
set global time_zone = '-3:00';

create table users (
    id int primary key auto_increment,
    email varchar(100) not null unique,
    name varchar(100) not null,
    surname varchar(100) not null,
    second_name varchar(100) not null,
    password varchar(256) not null,
    phone varchar(50) not null,
    is_accepted_mail_sending bool not null,
    is_confirmed bool not null
);

create table roles (
    id int primary key auto_increment,
    role varchar(100) not null
);

create table users_roles (
    id int primary key auto_increment,
    user_id int not null unique,
    role_id int not null,
    foreign key (user_id) references users (id) on delete cascade on update cascade,
    foreign key (role_id) references roles (id) on delete cascade on update cascade
);

create table cathedras (
    id int primary key auto_increment,
    cathedra_name varchar (100) not null
);

create table scientific_advisor_data (
    id int primary key not null,
    cathedra int not null,
    foreign key (id) references users (id) on delete cascade on update cascade,
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
    id int primary key not null,
    student_group int not null,
    cathedra int not null,
    type int not null,
    foreign key (id) references users (id) on delete cascade on update cascade,
    foreign key (cathedra) references cathedras (id),
    foreign key (type) references student_type (id),
    foreign key (student_group) references student_group (id)
);

create table project_theme (
    id int primary key auto_increment,
    theme varchar (256)
);

create table project (
    id int primary key auto_increment,
    type int not null,
    name varchar (100),
    scientific_advisor_id int not null,
    description varchar(512) default 'Описание отсутствует',
    foreign key (type) references project_theme (id),
    foreign key (scientific_advisor_id) references scientific_advisor_data (id) on delete cascade on update cascade
);

create table occupied_students (
    id int primary key auto_increment,
    student_id int not null,
    project_id int not null,
    foreign key (student_id) references student_data (id) on delete cascade on update cascade,
    foreign key (project_id) references project (id) on delete cascade on update cascade
);

create table associated_students (
    id int primary key auto_increment,
    scientific_advisor int not null,
    student int not null,
    theme int not null,
    is_accepted bool not null default false,
    foreign key (scientific_advisor) references users (id) on delete cascade on update cascade,
    foreign key (student) references users (id) on delete cascade on update cascade,
    foreign key (theme) references project_theme (id)
);

create table document_type (
    id int primary key auto_increment,
    type varchar (100) not null
);

create table document_kind (
    id int primary key auto_increment,
    kind varchar (100) not null
);

create table view_rights (
    id int primary key auto_increment,
    view_right varchar(100) not null
);

create table document (
    id int primary key auto_increment,
    creator_id int not null,
    name varchar (100) not null,
    document_path varchar (256) not null,
    creation_date date not null,
    type int not null,
    kind int not null,
    description varchar (512) default 'Описание отсутствует',
    view_rights int not null,
    foreign key (view_rights) references view_rights (id) on delete cascade on update cascade,
    foreign key (type) references document_type (id),
    foreign key (kind) references document_kind (id),
    foreign key (creator_id) references users(id) on delete cascade on update cascade
);

create table document_version (
    id int primary key auto_increment,
    editor int not null,
    document int not null,
    edition_date datetime not null,
    edition_description varchar (512) default 'Описание изменения отсутствует',
    this_version_document_path varchar (256) not null,
    foreign key (editor) references users (id) on delete cascade on update cascade,
    foreign key (document) references document (id) on delete cascade on update cascade
);

create table common_chat (
    id int primary key auto_increment,
    sender int not null,
    send_date date not null,
    message varchar (512) not null,
    foreign key (sender) references users(id)
);

insert into roles (role) values
    ('ROLE_STUDENT'),
    ('ROLE_SCIENTIFIC_ADVISOR'),
    ('ROLE_HEAD_OF_CATHEDRA'),
    ('ROLE_ADMIN'),
    ('ROLE_ROOT');

insert into cathedras (cathedra_name) values
    ('МОСИТ'),
    ('ИППО'),
    ('КИС');

insert into student_type (type) values
    ('Бакалавр'),
    ('Магистр'),
    ('Аспирант');

insert into student_group (student_group) values
    ('ИКБО-12-17'),
    ('ИКБО-07-17'),
    ('ИНБО-15-16');

insert into view_rights (view_right) values
    ('is_only_for_me'),
    ('is_only_for_scientific_advisors'),
    ('is_only_for_my_students'),
    ('is_only_for_my_students_and_only_for_scientific_advisors'),
    ('is_for_all');

insert into document_type (type) values
    ('Научно-исследовательская работа'),
    ('Практика по получению знаний и умений'),
    ('Преддипломная практика'),
    ('ВКР');

insert into document_kind (kind) values
    ('Отчёт'),
    ('Приказ'),
    ('Титульный лист'),
    ('Методическое пособие'),
    ('Презентация'),
    ('Пример отчёта');

insert into project_theme (theme) values
    ('Обучающие программы для портала Госуслуги, на основе технологии опережающего, деятельного, ситуационного обучения'),
    ('Дизайн и исследование интерфейсов клиентских приложений, на основе ментальных моделей пользователя'),
    ('Мобильные и WEB приложения, базирующиеся на клиент-серверной архитектуре'),
    ('Анализ, разработка, внедрение и сопровождение распределенных, гетерогенных систем');