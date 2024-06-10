CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE "app_user" (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES "app_user"(id),
    FOREIGN KEY (role_id) REFERENCES role(id)
);

INSERT INTO role (name) VALUES ('READ'), ('WRITE');

INSERT INTO "app_user" (username, password) VALUES
('readUser', '$2a$12$8NLncEuQyLNa8dHLjNqg6.nOjTBLA8VGqOqgGSP6SwGB5d1yBsGLG'),
('writeUser', '$2a$12$8NLncEuQyLNa8dHLjNqg6.nOjTBLA8VGqOqgGSP6SwGB5d1yBsGLG');

INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM "app_user" WHERE username = 'readUser'), (SELECT id FROM role WHERE name = 'READ')),
((SELECT id FROM "app_user" WHERE username = 'writeUser'), (SELECT id FROM role WHERE name = 'READ')),
((SELECT id FROM "app_user" WHERE username = 'writeUser'), (SELECT id FROM role WHERE name = 'WRITE'));
