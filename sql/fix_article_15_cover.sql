-- Heart-Healthy Habits (id 15): local cover (healthy food, not laptop/stethoscope)
-- mysql -h 127.0.0.1 -P 3306 -u root -p personal_health < sql/fix_article_15_cover.sql

UPDATE `news` SET `cover` = '/img/articles/heart-healthy.jpg' WHERE `id` = 15;
