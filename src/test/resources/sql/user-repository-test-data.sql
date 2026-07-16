INSERT INTO users (id,
                   email,
                   nickname,
                   address,
                   certification_code,
                   status,
                   last_login_at)
VALUES (1,
        'oh.youngsoo23@gmail.com',
        'ohyoungsoo',
        'Seoul, South Korea',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'ACTIVE',
        0);

INSERT INTO users (id,
                   email,
                   nickname,
                   address,
                   certification_code,
                   status,
                   last_login_at)
VALUES (2,
        'oh.youngsoo223@gmail.com',
        'ohyoungsoo2',
        'Seoul, South Korea2',
        'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
        'PENDING',
        0);

ALTER TABLE users ALTER COLUMN id RESTART WITH 3;