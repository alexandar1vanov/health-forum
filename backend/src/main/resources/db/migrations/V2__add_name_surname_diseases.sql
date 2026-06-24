ALTER TABLE users ADD COLUMN name VARCHAR(100) NOT NULL DEFAULT '';
ALTER TABLE users ADD COLUMN surname VARCHAR(100) NOT NULL DEFAULT '';

CREATE TABLE diseases (
                          id   BIGSERIAL PRIMARY KEY,
                          name VARCHAR(200) NOT NULL
);

CREATE TABLE user_diseases (
                               user_id    BIGINT REFERENCES users(id) ON DELETE CASCADE,
                               disease_id BIGINT REFERENCES diseases(id) ON DELETE CASCADE,
                               PRIMARY KEY (user_id, disease_id)
);