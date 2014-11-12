#Update para añadir usuarios

# --- !Ups

-- Nueva tabla con usuarios

CREATE TABLE user_task (
   login varchar(255) NOT NULL,
   first_name varchar(255),
   last_name varchar(255),
   e_mail varchar(255),
   PRIMARY KEY (login)
);

-- Añadimos clave ajena en la tabla de tareas
 
ALTER TABLE task ADD taskOwner varchar(255);
ALTER TABLE task ADD constraint fk_task_user_1 FOREIGN KEY (taskOwner) REFERENCES user_task (login);

-- Añadimos el usuario anonimo

INSERT INTO user_task (login, first_name, last_name, e_mail) VALUES ('anonymous', 'anonymous', 'anonymous', 'anonymous@mail.com');
INSERT INTO user_task (login, first_name, last_name, e_mail) VALUES ('pasquall', 'Pascu', 'Yo', 'pms@ua.es');

# --- !Downs
DROP TABLE user_task;
ALTER TABLE task DROP user_task;