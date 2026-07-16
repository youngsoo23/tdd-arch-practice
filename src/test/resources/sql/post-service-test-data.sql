insert into `users` (`id`, `email`, `nickname`, `address`, `certification_code`, `status`, `last_login_at`)
values (1, 'oh.youngsoo23@gmail.com', 'ohyoungsoo', 'Seoul, South Korea', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'ACTIVE', 0);

insert into `posts` (`id`, `content`, `user_id`, `created_at`, `modified_at`)
values (1, 'This is the content of the first post.', 1, 1, 1);

ALTER TABLE posts ALTER COLUMN id RESTART WITH 2;