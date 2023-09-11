alter table newsletter add column owner_name varchar(255) not null;
alter table newsletter add column owner_email varchar(255) not null;

create table newsletter_issue(
  id serial primary key,
  newsletter_id integer not null references newsletter(id),
  content text not null
);

create table newsletter_subscription(
  id serial primary key,
  newsletter_id integer not null references newsletter(id),
  email varchar(255) not null
);

