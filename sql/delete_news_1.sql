-- Remove article id 1: "The average height of adult males is close to 1.7 meters..."
DELETE FROM `evaluations` WHERE `content_type` = 'NEWS' AND `content_id` = 1;
DELETE FROM `news_save` WHERE `news_id` = 1;
DELETE FROM `news` WHERE `id` = 1;
