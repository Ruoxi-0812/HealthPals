-- Featured cards: unique local covers (id 4 vs 15 were showing the same image).
-- mysql -h 127.0.0.1 -P 3306 -u root -p personal_health < sql/fix_featured_covers_unique.sql

UPDATE `news` SET `cover` = '/img/articles/principles-health.jpg' WHERE `id` = 4;
UPDATE `news` SET `cover` = '/img/articles/diet-cells.jpg' WHERE `id` = 12;
UPDATE `news` SET `cover` = '/img/articles/heart-healthy.jpg' WHERE `id` = 15;
