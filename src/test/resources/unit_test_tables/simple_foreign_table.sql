CREATE TABLE public.simple_foreign_table
(
   simple_foreign_key varchar(25) PRIMARY KEY  NOT NULL,
   simple_foreign_column varchar(250) NOT NULL,
   simple_key varchar(200) NOT NULL
)
;
ALTER TABLE public.simple_foreign_table
ADD CONSTRAINT simple_foreign_table_simple_key_fkey
FOREIGN KEY (simple_key)
REFERENCES public.simple_table(simple_key)
;
