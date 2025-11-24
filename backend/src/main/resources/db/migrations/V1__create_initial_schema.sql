CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       has_selected_diseases BOOLEAN DEFAULT FALSE NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       is_deleted BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE TABLE diseases (
                          id BIGSERIAL PRIMARY KEY,
                          disease_name VARCHAR(255) NOT NULL UNIQUE,
                          disease_category VARCHAR(50) NOT NULL,
                          disease_description VARCHAR(1000) NOT NULL
);

CREATE TABLE forum_posts (
                             id BIGSERIAL PRIMARY KEY,
                             post_title VARCHAR(30) NOT NULL,
                             post_content VARCHAR(1000) NOT NULL,
                             user_id BIGINT NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE post_diseases (
                               post_id BIGINT NOT NULL,
                               disease_id BIGINT NOT NULL,
                               PRIMARY KEY (post_id, disease_id),
                               CONSTRAINT fk_pd_post FOREIGN KEY (post_id) REFERENCES forum_posts(id),
                               CONSTRAINT fk_pd_disease FOREIGN KEY (disease_id) REFERENCES diseases(id)
);

CREATE TABLE forum_comments (
                                id BIGSERIAL PRIMARY KEY,
                                user_id BIGINT,
                                forum_post BIGINT,
                                comment_content VARCHAR(1000) NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(id),
                                CONSTRAINT fk_comment_post FOREIGN KEY (forum_post) REFERENCES forum_posts(id)
);

CREATE TABLE replies (
                         id BIGSERIAL PRIMARY KEY,
                         reply_content VARCHAR(500) NOT NULL,
                         user_id BIGINT NOT NULL,
                         comment_id BIGINT NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_reply_user FOREIGN KEY (user_id) REFERENCES users(id),
                         CONSTRAINT fk_reply_comment FOREIGN KEY (comment_id) REFERENCES forum_comments(id)
);

CREATE TABLE user_diseases (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               disease_id BIGINT NOT NULL,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_ud_user FOREIGN KEY (user_id) REFERENCES users(id),
                               CONSTRAINT fk_ud_disease FOREIGN KEY (disease_id) REFERENCES diseases(id)
);

CREATE TABLE post_likes (
                            id BIGSERIAL PRIMARY KEY,
                            post_id BIGINT NOT NULL,
                            user_id BIGINT NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_like_post FOREIGN KEY (post_id) REFERENCES forum_posts(id),
                            CONSTRAINT fk_like_user FOREIGN KEY (user_id) REFERENCES users(id),
                            CONSTRAINT unique_post_user UNIQUE (post_id, user_id)
);