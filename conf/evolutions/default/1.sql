# --- First database schema

# --- !Ups

set ignorecase true;


create table book (
  id                        bigint not null,
  name                      varchar(255) not null,
  author                    varchar(255) not null,
  description               varchar(255) not null,
  version					bigint not null,
  constraint pk_company primary key (id))
;


create table history (
  version					bigint not null,
  id                        bigint not null,
  name                      varchar(255) not null,
  author                    varchar(255) not null,
  description               varchar(255) not null,
)
;

insert into book (id,name,author,description,version) values (  12131,'Test','User1','Test Book',1);
insert into book (id,name,author,description,version) values (  22131,'Scala','User1','Scala Book',1);


insert into history (id,name,author,description,version) values (  12131,'Test','User1','Test Book',1);
insert into history (id,name,author,description,version) values (  22131,'Scala','User1','Scala Book',1);