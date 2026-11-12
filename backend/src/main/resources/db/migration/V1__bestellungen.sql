create table bestellung
(
    id     bigint generated always as identity primary key,
    kunde  varchar(100) not null,
    status varchar(20)  not null
);

create table position
(
    bestellung_id bigint         not null references bestellung (id),
    nr            int            not null,
    artikel       varchar(100)   not null,
    menge         int            not null,
    einzelpreis   numeric(10, 2) not null,
    primary key (bestellung_id, nr)
);