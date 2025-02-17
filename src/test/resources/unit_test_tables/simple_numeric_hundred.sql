DROP TABLE if exists public .simple_numeric_hundred_table;


CREATE TABLE public.simple_numeric_hundred_table;
(
   simple_key int PRIMARY KEY  NOT NULL,
   simple_hundred numeric(5,2) NOT NULL
)
;
