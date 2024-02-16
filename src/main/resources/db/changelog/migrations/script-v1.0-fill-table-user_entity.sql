insert into user_entity (login, password, role)
values ('Admin', '$2a$12$r7xMVd69qrcro2y08lCfcuGCcALKFwpM11Y8CTslU6excYVc24eE2', 'ROLE_ADMIN'),
       ('User', '$2a$12$L/60e15uj8ZFqTldPHFJo.v3Tlyf4MXtTmPesF0rvWCRnrpiE5ggq', 'ROLE_USER');

-- login = Admin; password = admin; role = ROLE_ADMIN
-- login = User; password = user1234; role = ROLE_USER
