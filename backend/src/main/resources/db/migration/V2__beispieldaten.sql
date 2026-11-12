insert into bestellung (kunde, status)
values ('Ada', 'OFFEN'),
       ('Linus', 'BEZAHLT'),
       ('Grace', 'STORNIERT'),
       ('Margaret', 'VERSANDT');

insert into position (bestellung_id, nr, artikel, menge, einzelpreis)
values (1, 0, 'Tastatur', 1, 49.90),
       (2, 0, 'Monitor', 2, 199.00),
       (2, 1, 'Kabel', 3, 4.50),
       (3, 0, 'Maus', 1, 25.00),
       (4, 0, 'Headset', 1, 79.00);