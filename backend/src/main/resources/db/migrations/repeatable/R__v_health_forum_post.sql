drop view if exists v_health_forum_post;
create or replace view v_health_forum_post as
select fp.id           as id,
       fp.post_title   as title,
       fp.post_content as content,
       u.id            as userId,
       u.email         as userEmail,
       fp.created_at   as createdAt,
       fp.updated_at   as updatedAt,
       avg(pr.rating)  as rating,
       count(distinct pl.id)    as likeCount
from forum_posts fp
         join users u on fp.user_id = u.id
         left join post_ratings pr on fp.id = pr.post_id
         left join post_likes pl on fp.id = pl.post_id
where u.is_deleted = false
group by fp.id, u.id, u.email, fp.created_at
order by fp.created_at;
