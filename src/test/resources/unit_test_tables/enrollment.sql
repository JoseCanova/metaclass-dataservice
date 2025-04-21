CREATE TABLE enrollment (
   enrollment_id varchar(30) primary key, 
   student_id varchar(30),
   course_id varchar(30),
   enrollment_date DATE,
   grade VARCHAR(2),
   FOREIGN KEY (student_id) REFERENCES student(student_id),
   FOREIGN KEY (course_id) REFERENCES course(course_id)
);
