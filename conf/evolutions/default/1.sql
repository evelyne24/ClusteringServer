# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table location (
  id                        bigint auto_increment not null,
  latitude                  double,
  longitude                 double,
  x                         bigint,
  y                         bigint,
  quad_key                  varchar(255),
  name                      varchar(255),
  constraint pk_location primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table location;

SET FOREIGN_KEY_CHECKS=1;

