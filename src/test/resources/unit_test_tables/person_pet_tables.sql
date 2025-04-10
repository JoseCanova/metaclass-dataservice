
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
create table food 
(
food_key varchar(30) primary key,
food_name varchar(200),
food_pet_key varchar(25) references pet(pet_key));

ALTER TABLE public.pet
ADD CONSTRAINT pet_fkey
FOREIGN KEY (pet_person_key)
REFERENCES public.person(person_key)
;
