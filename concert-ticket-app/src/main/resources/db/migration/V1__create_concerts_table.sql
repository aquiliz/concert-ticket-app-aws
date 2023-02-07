CREATE TABLE IF NOT EXISTS concerts
(
   id bigserial,
   name character varying(100) not null,
   description character varying(5000),
   available_tickets bigserial not null,
   price float not null,
   CONSTRAINT concert_pk PRIMARY KEY (id)
);

INSERT INTO concerts (name, description, available_tickets, price)
VALUES ('The Beatles Tribute Concert',
 'A tribute concert to one of the most famous and influential bands of all time, The Beatles. Featuring talented musicians performing some of the greatest hits of the legendary band.', 100, 50.0);

INSERT INTO concerts (name, description, available_tickets, price)
VALUES ('Taylor Swift: Red Tour', 'Join Taylor Swift for an unforgettable night of music and entertainment as she performs songs from her latest album, "Red."', 200, 75.0);

INSERT INTO concerts (name, description, available_tickets, price)
VALUES ('Michael Jackson Tribute Concert', 'A tribute concert honoring the King of Pop, Michael Jackson. Enjoy a night filled with some of his greatest hits, performed by talented musicians and dancers.', 300, 100.0);

INSERT INTO concerts (name, description, available_tickets, price)
VALUES ('Jazz Night with Billie Holiday', 'Join us for an evening of jazz music, featuring the timeless and soulful voice of Billie Holiday, performed by a talented jazz ensemble.', 400, 125.0);

INSERT INTO concerts (name, description, available_tickets, price)
VALUES ('Rock Legends Concert', 'A concert featuring some of the greatest rock bands and musicians of all time, including The Rolling Stones, Led Zeppelin, and AC/DC.', 500, 150.0);
