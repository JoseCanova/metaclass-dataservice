drop table if exists simple_numeric_table;

CREATE TABLE public.simple_numeric_table
(
   simple_smallint_column smallint NOT NULL,
   simple_integer_column int NOT NULL,
   simple_numeric_zeroed_column numeric(19) NOT NULL,
   simple_numeric_float float(19) NOT NULL,
   simple_numeric_integer int NOT NULL,
   simple_numeric_smallint smallint NOT NULL,
   simple_numeric_boolean bool NOT NULL,
   simple_numeric_bigint bigint NOT NULL,
   simple_numeric_key int PRIMARY KEY  NOT NULL
)
;
