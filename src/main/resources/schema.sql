/* create database graduates_site character set 'utf8mb4_0900_ai_ci'; */
/* Если при коннекте ругается на неверное время */
set global time_zone = '-3:00';

/* Включить планировшик MySQL*/
SET GLOBAL event_scheduler = ON;

create table users (
    id int primary key auto_increment,
    email varchar(100) not null unique,
    name varchar(100) not null,
    surname varchar(100) not null,
    second_name varchar(100) not null,
    password varchar(256) not null,
    phone varchar(50) not null,
    is_accepted_mail_sending bool not null,
    is_confirmed bool not null,
    registration_date timestamp not null default current_timestamp
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
    places int not null default 0,
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
    advisor int not null,
    theme varchar (256),
    foreign key (advisor) references scientific_advisor_data (id) on delete cascade on update cascade
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

create table associated_students (
    id int primary key auto_increment,
    scientific_advisor int not null,
    student int not null unique,
    theme int not null,
    is_accepted bool not null default false,
    foreign key (scientific_advisor) references users (id) on delete cascade on update cascade,
    foreign key (student) references users (id) on delete cascade on update cascade,
    foreign key (theme) references project_theme (id)
);

create table occupied_students (
    id int primary key auto_increment,
    student_id int not null unique,
    project_id int not null,
    foreign key (student_id) references student_data (id) on delete cascade on update cascade,
    foreign key (student_id) references associated_students (student) on delete cascade on update cascade,
    foreign key (project_id) references project (id) on delete cascade on update cascade
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

create table project_document (
    id int primary key key auto_increment,
    project int not null,
    document int not null unique,
    foreign key (project) references project (id) on delete cascade on update cascade,
    foreign key (document) references document (id) on delete cascade on update cascade
);

create table refresh_tokens (
    id int primary key not null,
    refresh_token varchar(1024) not null default 'Не назначен',
    issue long not null,
    expire long not null,
    foreign key (id) references users (id) on delete cascade on update cascade
);

create table order_properties (
    id int primary key not null,
    number varchar(20) not null,
    order_date date not null,
    start_date date not null,
    end_date date not null,
    speciality varchar(16) not null,
    foreign key (id) references document(id) on delete cascade on update cascade
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
    ('КИС'),
    ('ВТ'),
    ('ПИ');

insert into student_type (type) values
    ('Бакалавр'),
    ('Магистр'),
    ('Аспирант');

insert into student_group (student_group) values
    ('ИКБО-12-17'),
    ('ИКБО-07-17'),
    ('ИНБО-13-17'),
    ('ИВБО-06-17');

insert into users values
    ('Alamart1683@gmail.com', 'Андрей', 'Лисовой', 'Анатольевич', '$2a$10$hHGIOI8bYFvC7GdN1tc3tOjnNlswdVIRq.5foa5/v0YG9DVNDqNOy', '+79160571756', '1', '1', '2020-09-08 21:05:16'),
    ('vkgrig490@mail.ru', 'Виктор', 'Григорьев', 'Карлович', '$2a$10$lFlTSE0v19WaLKktY1X0b.xDRVTf/5msJCrIR6SSXoDOx2GRvEG16', '+79652812343', '1', '1', '2020-09-09 13:57:00'),
    ('kakoyetomylo@mirea.ru', 'Иван', 'Иванов', 'Иванович', '$2a$10$YwXR/8QNQ9tO.cfcmA5MMur0yYRdxSS1dznFmrwVYUfCKMBCnClJu', '+79991488228', '1', '1', '2020-09-09 13:57:00'),
    ('korrafox@gmail.com', 'Немарина', 'Некарева', 'Неандреевна', '$2a$10$ok3ph7j8SjxHI1YbCshtDeu9PE2vZOn6s6puMt46dqgIFXKdnsv22', '+79991488228', '1', '1', '2020-09-09 13:57:00'),
    ('s_golovin256@mirea.ru', 'Сергей', 'Головин', 'Анатольевич', '$2a$10$YRMcPsGnMz2QMMa7dEHisueWbvU14VmQMAN9WU3JBR6QiUOFtRviK', '+74954349743', '1', '1', '2020-09-09 13:57:00'),
    ('korra-m@yandex.ru', 'Марина', 'Карева', 'Андреевна', '$2a$10$edTSIRTfoeT23qbuAUvjs.bVL4xRA4ib2WONBubjhSbEf7DUDPd2a', '+79855727332', '1', '1', '2020-09-09 13:57:00'),
    ('Alamart1683@yandex.ru', 'Ганнибал', 'Барка', 'Гамилькарович', '$2a$10$2qq.z.VNoFt8T6Mqkekw/u4QlpKmt4SaLhMv1c6Ql9dkbZzmyIGA2', '+78889992212', '1', '1', '2020-09-09 13:57:00'),
    ('andrey.lis2012@yandex.ru', 'Андрей', 'Лисовой', 'Анатольевич', '$2a$10$bhcj/eTrEjVHNYtCkmTL5uMSskaXjwGCHWf9U8EzT2vpk9MEsenN6', '+79160571756', '1', '1', '2020-09-09 13:57:00'),
    ('Delamart1683@yandex.ru', 'Андрей', 'Лисовой', 'Анатольевич', '$2a$10$lAABmHuK6vsqV187OVUIquNm3fwAcjCGo7J0.rISuXis89boU/Oga', '+79160571756', '1', '1', '2020-09-09 13:57:00');

insert into users_roles values
    ('1', '5'),
    ('2', '2'),
    ('3', '2'),
    ('4', '2'),
    ('5', '3'),
    ('6', '1'),
    ('7', '1'),
    ('8', '4'),
    ('9', '1');

insert into student_data values
    ('6', '1', '1', '1'),
    ('7', '1', '1', '1'),
    ('9', '4', '1', '1');

insert into scientific_advisor_data values
    ('2', '1', '8'),
    ('3', '1', '10'),
    ('4', '1', '15'),
    ('5', '1', '3');

insert into associated_students values
    ('2', '7'),
    ('4', '6');

insert into view_rights (view_right) values
    ('Только я'),
    ('Только научные руководители'),
    ('Только мои студенты'),
    ('Только мои студенты и научные руководители'),
    ('Все пользователи'),
    ('Только для проекта');

insert into document_type (type) values
    ('Научно-исследовательская работа'),
    ('Практика по получению знаний и умений'),
    ('Преддипломная практика'),
    ('ВКР');

insert into document_kind (kind) values
    ('Приказ'),
    ('Задание'),
    ('Отчёт');

insert into project_theme (theme) values
    (2, 'Обучающие программы для портала Госуслуги, на основе технологии опережающего, деятельного, ситуационного обучения'),
    (2, 'Дизайн и исследование интерфейсов клиентских приложений, на основе ментальных моделей пользователя'),
    (2, 'Мобильные и WEB приложения, базирующиеся на клиент-серверной архитектуре'),
    (2, 'Анализ, разработка, внедрение и сопровождение распределенных, гетерогенных систем');

/*
Событие для планировщика, которое будет удалять неподтвердившихся
пользователей через 30 дней после их регистрации
 */
create event delete_not_confirmed_users
on
    schedule every 1 day starts current_timestamp
do
    delete from users
    where is_confirmed = false and to_days(date(current_timestamp)) - to_days(date(registration_date)) > 30