DROP TABLE public.simple_table;

CREATE TABLE public.simple_table
(
   simple_key varchar(200) PRIMARY KEY  NOT NULL,
   simple_column varchar(200) NOT NULL,
   simple_date date,
   simple_timestamp timestamp
)
;
