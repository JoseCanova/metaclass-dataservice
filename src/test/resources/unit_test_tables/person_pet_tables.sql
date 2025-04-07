
CREATE TABLE public.person
(
   person_key varchar(50) PRIMARY KEY  NOT NULL,
   person_name varchar(200) NOT NULL)
;
CREATE TABLE public.pet
(
   pet_key varchar(25) PRIMARY KEY  NOT NULL,
   pet_name varchar(250) NOT NULL,
   pet_person_key varchar(50) NOT NULL
)
;
ALTER TABLE public.pet
ADD CONSTRAINT pet_fkey
FOREIGN KEY (pet_person_key)
REFERENCES public.person(person_key)
;
