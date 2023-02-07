CREATE TABLE IF NOT EXISTS ticket_purchases
(
   id bigserial,
   issued_to character varying(100) not null,
   tickets_count smallint not null,
   concert_id bigserial not null,
   CONSTRAINT purchase_pk PRIMARY KEY (id),
   FOREIGN KEY (concert_id) REFERENCES concerts(id)
);