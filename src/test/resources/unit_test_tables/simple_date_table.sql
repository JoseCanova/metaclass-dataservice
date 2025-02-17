DROP TABLE if exists public .simple_date_table;


CREATE TABLE public.simple_date_table
(
   simple_date date NOT NULL,
   simple_timestamp timestamp NOT NULL,
   simple_time time NOT NULL,
   simple_timestamp_zone timestamp NOT NULL,
   simple_times_zone time NOT NULL,
   simple_key int PRIMARY KEY  NOT NULL
)
;
