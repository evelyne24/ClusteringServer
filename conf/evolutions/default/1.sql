# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table location (
  id                            bigint not null,
  latitude                      double,
  longitude                     double,
  quad_key                      varchar(255),
  name                          varchar(255),
  constraint pk_location primary key (id)
);
create sequence location_seq;


# --- !Downs

drop table if exists location;
drop sequence if exists location_seq;

