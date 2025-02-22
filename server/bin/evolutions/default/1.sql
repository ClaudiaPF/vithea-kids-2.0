# --- !Ups

create table answer (
  answer_id                 bigint auto_increment not null,
  answer_description        varchar(255),
  stimulus_resource_id      bigint,
  constraint pk_answer primary key (answer_id))
;

create table caregiver (
  caregiver_id              bigint auto_increment not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  email                     varchar(255) not null,
  gender                    varchar(255),
  caregiverlogin_id         bigint,
  constraint uq_caregiver_email unique (email),
  constraint uq_caregiver_caregiverlogin_id unique (caregiverlogin_id),
  constraint pk_caregiver primary key (caregiver_id))
;

create table child (
  child_id                  bigint auto_increment not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  birth_date                date,
  gender                    varchar(255),
  childlogin_id             bigint,
  constraint uq_child_childlogin_id unique (childlogin_id),
  constraint pk_child primary key (child_id))
;

create table exercise (
  exercise_id               bigint auto_increment not null,
  topic_topic_id            bigint,
  level_level_id            bigint,
  question_question_id      bigint,
  right_answer_answer_id    bigint,
  author_caregiver_id       bigint,
  constraint uq_exercise_right_answer_answer_id unique (right_answer_answer_id),
  constraint pk_exercise primary key (exercise_id))
;

create table level (
  level_id                  bigint auto_increment not null,
  level_description         varchar(255),
  constraint pk_level primary key (level_id))
;

create table login (
  login_id                  bigint auto_increment not null,
  user_name                 varchar(255),
  password                  varbinary(255),
  enabled                   tinyint(1) default 0,
  user_type                 integer,
  constraint pk_login primary key (login_id))
;

create table question (
  question_id               bigint auto_increment not null,
  question_description      varchar(255),
  stimulus_resource_id      bigint,
  stimulus_text             varchar(255),
  constraint pk_question primary key (question_id))
;

create table resource (
  resource_id               bigint auto_increment not null,
  resource_path             varchar(255),
  resource_type_resource_type_id bigint,
  resource_area_resource_area_id bigint,
  owner_caregiver_id        bigint,
  constraint pk_resource primary key (resource_id))
;

create table resource_area (
  resource_area_id          bigint auto_increment not null,
  resource_area_description varchar(255),
  constraint pk_resource_area primary key (resource_area_id))
;

create table resource_type (
  resource_type_id          bigint auto_increment not null,
  resource_type_description varchar(255),
  constraint pk_resource_type primary key (resource_type_id))
;

create table topic (
  topic_id                  bigint auto_increment not null,
  topic_description         varchar(255),
  constraint pk_topic primary key (topic_id))
;


create table CaregiverChild (
  caregiver_caregiver_id         bigint not null,
  child_child_id                 bigint not null,
  constraint pk_CaregiverChild primary key (caregiver_caregiver_id, child_child_id))
;

create table exercise_answer (
  exercise_exercise_id           bigint not null,
  answer_answer_id               bigint not null,
  constraint pk_exercise_answer primary key (exercise_exercise_id, answer_answer_id))
;
alter table answer add constraint fk_answer_stimulus_1 foreign key (stimulus_resource_id) references resource (resource_id) on delete restrict on update restrict;
create index ix_answer_stimulus_1 on answer (stimulus_resource_id);
alter table caregiver add constraint fk_caregiver_caregiverLogin_2 foreign key (caregiverlogin_id) references login (login_id) on delete restrict on update restrict;
create index ix_caregiver_caregiverLogin_2 on caregiver (caregiverlogin_id);
alter table child add constraint fk_child_childLogin_3 foreign key (childlogin_id) references login (login_id) on delete restrict on update restrict;
create index ix_child_childLogin_3 on child (childlogin_id);
alter table exercise add constraint fk_exercise_topic_4 foreign key (topic_topic_id) references topic (topic_id) on delete restrict on update restrict;
create index ix_exercise_topic_4 on exercise (topic_topic_id);
alter table exercise add constraint fk_exercise_level_5 foreign key (level_level_id) references level (level_id) on delete restrict on update restrict;
create index ix_exercise_level_5 on exercise (level_level_id);
alter table exercise add constraint fk_exercise_question_6 foreign key (question_question_id) references question (question_id) on delete restrict on update restrict;
create index ix_exercise_question_6 on exercise (question_question_id);
alter table exercise add constraint fk_exercise_rightAnswer_7 foreign key (right_answer_answer_id) references answer (answer_id) on delete restrict on update restrict;
create index ix_exercise_rightAnswer_7 on exercise (right_answer_answer_id);
alter table exercise add constraint fk_exercise_author_8 foreign key (author_caregiver_id) references caregiver (caregiver_id) on delete restrict on update restrict;
create index ix_exercise_author_8 on exercise (author_caregiver_id);
alter table question add constraint fk_question_stimulus_9 foreign key (stimulus_resource_id) references resource (resource_id) on delete restrict on update restrict;
create index ix_question_stimulus_9 on question (stimulus_resource_id);
alter table resource add constraint fk_resource_resourceType_10 foreign key (resource_type_resource_type_id) references resource_type (resource_type_id) on delete restrict on update restrict;
create index ix_resource_resourceType_10 on resource (resource_type_resource_type_id);
alter table resource add constraint fk_resource_resourceArea_11 foreign key (resource_area_resource_area_id) references resource_area (resource_area_id) on delete restrict on update restrict;
create index ix_resource_resourceArea_11 on resource (resource_area_resource_area_id);
alter table resource add constraint fk_resource_owner_12 foreign key (owner_caregiver_id) references caregiver (caregiver_id) on delete restrict on update restrict;
create index ix_resource_owner_12 on resource (owner_caregiver_id);



alter table CaregiverChild add constraint fk_CaregiverChild_caregiver_01 foreign key (caregiver_caregiver_id) references caregiver (caregiver_id) on delete restrict on update restrict;

alter table CaregiverChild add constraint fk_CaregiverChild_child_02 foreign key (child_child_id) references child (child_id) on delete restrict on update restrict;

alter table exercise_answer add constraint fk_exercise_answer_exercise_01 foreign key (exercise_exercise_id) references exercise (exercise_id) on delete restrict on update restrict;

alter table exercise_answer add constraint fk_exercise_answer_answer_02 foreign key (answer_answer_id) references answer (answer_id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table answer;

drop table caregiver;

drop table CaregiverChild;

drop table child;

drop table exercise;

drop table exercise_answer;

drop table level;

drop table login;

drop table question;

drop table resource;

drop table resource_area;

drop table resource_type;

drop table topic;

SET FOREIGN_KEY_CHECKS=1;

