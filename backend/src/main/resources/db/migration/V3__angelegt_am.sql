alter table bestellung
    add column angelegt_am timestamp with time zone not null default now();