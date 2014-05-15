# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table cluster (
  quad_key                  varchar(255) not null,
  zoom                      integer,
  size                      integer,
  center_lat                double,
  center_lon                double,
  tl_lat                    double,
  tl_lon                    double,
  tr_lat                    double,
  tr_lon                    double,
  bl_lat                    double,
  bl_lon                    double,
  br_lat                    double,
  br_lon                    double,
  constraint pk_cluster primary key (quad_key))
;

create table location (
  id                        bigint auto_increment not null,
  lat                       double,
  lon                       double,
  quad_key                  varchar(255),
  name                      varchar(255),
  constraint pk_location primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table cluster;

drop table location;

SET FOREIGN_KEY_CHECKS=1;

