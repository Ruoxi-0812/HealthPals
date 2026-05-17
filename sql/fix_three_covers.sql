-- Fix broken covers for articles 16, 18, 20 (local static images, no external CDN).
-- mysql -h 127.0.0.1 -P 3306 -u root -p personal_health < sql/fix_three_covers.sql

UPDATE `news` SET `cover` = '/img/articles/screen-sleep.jpg' WHERE `id` = 16;
UPDATE `news` SET `cover` = '/img/articles/hydration.jpg' WHERE `id` = 18;
UPDATE `news` SET `cover` = '/img/articles/allergies.jpg' WHERE `id` = 20;
