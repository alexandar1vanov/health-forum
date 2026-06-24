create table if not exists post_ratings(
    id      bigserial primary key,
    post_id bigint not null references forum_posts (id),
    user_id bigint not null references users (id),
    rating  bigint not null check ( rating >= 1 and rating <= 5),
    constraint uq_post_user unique (post_id, user_id)
);
