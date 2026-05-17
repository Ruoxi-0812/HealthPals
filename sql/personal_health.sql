/*
 Navicat MySQL Dump SQL

 Source Server         : carol
 Source Server Type    : MySQL
 Source Server Version : 90200 (9.2.0)
 Source Host           : localhost:3306
 Source Schema         : personal_health

 Target Server Type    : MySQL
 Target Server Version : 90200 (9.2.0)
 File Encoding         : 65001

 Date: 24/01/2025 23:55:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for evaluations
-- ----------------------------
DROP TABLE IF EXISTS `evaluations`;
CREATE TABLE `evaluations` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `parent_id` int DEFAULT NULL,
  `commenter_id` int DEFAULT NULL,
  `replier_id` int DEFAULT NULL,
  `content_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `content_id` int DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `upvote_list` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=203 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of evaluations
-- ----------------------------
BEGIN;
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (157, NULL, 3, NULL, 'NEWS', 12, 'Like a cunning hunter lurking in the dark, ready to attack us at any moment. Its presence is silent and undetectable.', '3,7,8', '2025-01-05 18:51:07');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (163, 157, 3, 17, 'NEWS', 12, 'Reply to you', NULL, '2025-01-05 19:00:05');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (166, 158, 3, 17, 'NEWS', 12, 'Go ahead', NULL, '2025-01-05 19:17:10');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (167, 157, 17, NULL, 'NEWS', 12, 'What are you talking about?', '3', '2025-01-05 19:18:14');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (169, 157, 3, 17, 'NEWS', 12, 'Exactly', NULL, '2025-01-05 19:20:38');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (170, 157, 3, 17, 'NEWS', 12, 'A lot of things', NULL, '2025-01-06 13:29:10');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (171, 157, 3, 17, 'NEWS', 12, 'Sending it to you', NULL, '2025-01-06 13:29:57');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (172, NULL, 17, NULL, 'NEWS', 12, 'Peptide supplementation, as the name suggests, is about replenishing the body''s essential peptides.', '3', '2025-01-06 13:31:25');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (173, 172, 3, NULL, 'NEWS', 12, 'First-level comment is done', NULL, '2025-01-06 13:31:47');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (174, 172, 3, NULL, 'NEWS', 12, 'Did you receive it????', NULL, '2025-01-06 14:23:38');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (175, 172, 17, 3, 'NEWS', 12, 'Okay', NULL, '2025-01-06 14:23:54');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (176, 172, 3, 17, 'NEWS', 12, 'Exactly', NULL, '2025-01-06 14:24:11');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (177, 172, 17, 3, 'NEWS', 12, 'Exactly', NULL, '2025-01-06 14:24:22');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (178, 157, 3, 17, 'NEWS', 12, 'Exactly', NULL, '2025-01-06 14:25:00');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (179, 157, 3, 17, 'NEWS', 12, 'Exactly1212', NULL, '2025-01-06 14:25:57');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (180, 157, 3, 17, 'NEWS', 12, '121212', NULL, '2025-01-06 14:31:48');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (181, 157, 3, 17, 'NEWS', 12, '23232344', '3', '2025-01-06 14:34:43');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (182, 157, 3, 17, 'NEWS', 12, 'test ', NULL, '2025-01-06 14:57:37');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (183, 157, 3, NULL, 'NEWS', 12, 'hi', NULL, '2025-01-06 15:01:55');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (184, 172, 3, NULL, 'NEWS', 12, 'hello', '3', '2025-01-06 15:02:29');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (185, 172, 17, 3, 'NEWS', 12, 'hihi', NULL, '2025-01-06 15:02:39');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (186, 157, 3, 17, 'NEWS', 12, '43243243', NULL, '2025-01-06 15:03:28');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (187, 172, 3, 17, 'NEWS', 12, '432432433232', '3', '2025-01-06 15:04:00');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (188, 172, 17, 3, 'NEWS', 12, '432432', NULL, '2025-01-06 15:04:06');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (189, 157, 3, 17, 'NEWS', 12, 'test ', NULL, '2025-01-06 15:04:30');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (190, 157, 3, 17, 'NEWS', 12, 'What about now?', NULL, '2025-01-06 15:05:39');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (191, 172, 3, 17, 'NEWS', 12, 'What about now?', NULL, '2025-01-06 15:05:54');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (192, 157, 3, 17, 'NEWS', 12, 'What about now?', NULL, '2025-01-06 15:06:44');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (193, 157, 17, 3, 'NEWS', 12, '3213123', NULL, '2025-01-06 15:06:51');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (194, 157, 3, 17, 'NEWS', 12, 'too much', NULL, '2025-01-06 15:07:27');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (195, 157, 17, 3, 'NEWS', 12, 'good morning', NULL, '2025-01-06 15:07:37');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (196, 157, 3, 17, 'NEWS', 12, 'thx', '', '2025-01-06 15:07:59');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (197, 157, 17, 3, 'NEWS', 12, 'receive it', NULL, '2025-01-06 15:08:14');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (198, 172, 3, NULL, 'NEWS', 12, 'nice', NULL, '2025-01-06 15:08:28');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (199, 172, 17, 3, 'NEWS', 12, '202020220', '3', '2025-01-06 15:08:38');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (200, 172, 3, 17, 'NEWS', 12, 'great', '3', '2025-01-06 15:10:13');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (201, 157, 17, 3, 'NEWS', 12, 'btw', NULL, '2025-01-06 17:56:46');
INSERT INTO `evaluations` (`id`, `parent_id`, `commenter_id`, `replier_id`, `content_type`, `content_id`, `content`, `upvote_list`, `create_time`) VALUES (202, 157, 3, 17, 'NEWS', 12, 'hahahha', NULL, '2025-01-06 18:26:33');
COMMIT;

-- ----------------------------
-- Table structure for food
-- ----------------------------
DROP TABLE IF EXISTS `food`;
CREATE TABLE `food` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `tag_id` int DEFAULT NULL,
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of food
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for health_model_config
-- ----------------------------
DROP TABLE IF EXISTS `health_model_config`;
CREATE TABLE `health_model_config` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT 'Health model increment primary key',
  `user_id` int DEFAULT NULL COMMENT 'User ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'Configuration name',
  `detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `unit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `symbol` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `is_global` tinyint(1) DEFAULT NULL,
  `value_range` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of health_model_config
-- ----------------------------
BEGIN;
INSERT INTO `health_model_config` (`id`, `user_id`, `name`, `detail`, `cover`, `unit`, `symbol`, `is_global`, `value_range`) VALUES (1, 1, 'Height', 'Height model, selecting this model allows recording of height', 'https://images.unsplash.com/photo-1505751172876-fa1923c5c528?auto=format&fit=crop&w=700&h=525&q=80', 'cm', 'Height', 1, '195,459');
INSERT INTO `health_model_config` (`id`, `user_id`, `name`, `detail`, `cover`, `unit`, `symbol`, `is_global`, `value_range`) VALUES (2, 1, 'Weight', 'This model is for weight parameters, select it when recording', 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?auto=format&fit=crop&w=700&h=525&q=80', 'KG', 'Weight', 1, '126,548');
INSERT INTO `health_model_config` (`id`, `user_id`, `name`, `detail`, `cover`, `unit`, `symbol`, `is_global`, `value_range`) VALUES (4, 1, 'Alanine Aminotransferase (ALT)', 'Exclusive record for ALT enzyme', 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=700&h=525&q=80', 'U/L', 'ALT', 1, '108,258');
INSERT INTO `health_model_config` (`id`, `user_id`, `name`, `detail`, `cover`, `unit`, `symbol`, `is_global`, `value_range`) VALUES (7, 1, 'Exercise Heart Rate', 'Heart rate during exercise', 'https://images.unsplash.com/photo-1505576399279-565b52d4ac71?auto=format&fit=crop&w=700&h=525&q=80', 'Bpm', 'Bpm', 1, '75,197');
INSERT INTO `health_model_config` (`id`, `user_id`, `name`, `detail`, `cover`, `unit`, `symbol`, `is_global`, `value_range`) VALUES (8, 1, 'Nighttime Blood Pressure', 'Nighttime blood pressure measurement', 'https://images.unsplash.com/photo-1576091160550-2173dba999ef?auto=format&fit=crop&w=700&h=525&q=80', 'mmHg', 'mmHg', 1, '72,145');
INSERT INTO `health_model_config` (`id`, `user_id`, `name`, `detail`, `cover`, `unit`, `symbol`, `is_global`, `value_range`) VALUES (9, 1, 'Night Running Steps', 'Records step count during night running', 'https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?auto=format&fit=crop&w=700&h=525&q=80', 'step', 'none', 1, '422,17990');
INSERT INTO `health_model_config` (`id`, `user_id`, `name`, `detail`, `cover`, `unit`, `symbol`, `is_global`, `value_range`) VALUES (10, 1, 'Morning Run Steps', 'Records step count during morning runs', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=700&h=525&q=80', 'step', 'none', 1, '3000,10000');
INSERT INTO `health_model_config` (`id`, `user_id`, `name`, `detail`, `cover`, `unit`, `symbol`, `is_global`, `value_range`) VALUES (11, 3, 'Morning Blood Glucose Concentration', 'test', 'https://images.unsplash.com/photo-1576086213369-97a306d36557?auto=format&fit=crop&w=700&h=525&q=80', 'mol', 'mol/L', 0, '10,50');
INSERT INTO `health_model_config` (`id`, `user_id`, `name`, `detail`, `cover`, `unit`, `symbol`, `is_global`, `value_range`) VALUES (12, 3, 'Nighttime Average Heart Rate', 'test', 'https://images.unsplash.com/photo-1628595351029-c2bf17511435?auto=format&fit=crop&w=700&h=525&q=80', 'times', 'none', 0, '80,110');
COMMIT;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `message_type` int DEFAULT NULL,
  `receiver_id` int DEFAULT NULL,
  `sender_id` int DEFAULT NULL,
  `is_read` tinyint(1) DEFAULT NULL,
  `content_id` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of message
-- ----------------------------
BEGIN;
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (4, '157;12;What are you talking about?', 1, 3, 17, 1, 12, '2025-01-05 19:18:14');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (5, '172;12;First-level comment done', 1, 17, 3, 1, 12, '2025-01-06 13:31:47');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (6, '172;12;Did you receive it????', 1, 17, 3, 1, 12, '2025-01-06 14:23:38');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (7, '172;12;That is to say', 1, 17, 3, 1, 12, '2025-01-06 14:24:11');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (8, '172;12;Just that', 1, 17, 3, 1, 12, '2025-01-06 15:02:29');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (9, '172;12;432432433232', 1, 17, 3, 1, 12, '2025-01-06 15:04:00');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (10, '172;12;This is the effect', 1, 17, 3, 1, 12, '2025-01-06 15:05:55');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (11, '157;12;This is the effect', 1, 17, 3, 1, 12, '2025-01-06 15:06:44');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (12, '157;12;3213123', 1, 3, 17, 1, 12, '2025-01-06 15:06:51');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (13, '157;12;too much things', 1, 17, 3, 1, 12, '2025-01-06 15:07:27');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (14, '157;12;what happened', 1, 3, 17, 1, 12, '2025-01-06 15:07:37');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (15, '157;12;reply to you ', 1, 17, 3, 1, 12, '2025-01-06 15:07:59');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (16, '157;12;I received', 1, 3, 17, 1, 12, '2025-01-06 15:08:14');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (17, '172;12;hello world', 1, 17, 3, 1, 12, '2025-01-06 15:08:28');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (18, '172;12;this is for testing', 1, 3, 17, 1, 12, '2025-01-06 15:08:38');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (19, '172;12;that is great', 1, 17, 3, 1, 12, '2025-01-06 15:10:13');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (38, 'like your comment', 2, 17, 3, 1, 177, '2025-01-06 17:46:00');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (39, 'like your comment', 2, 17, 3, 1, 185, '2025-01-06 17:47:48');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (40, 'A blazing fire that threatens our lives. And peptides, an essential tool for health management, serve as a powerful weapon in combating diseases and maintaining health.', 4, 21, NULL, 0, NULL, '2025-01-06 17:48:40');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (41, 'A blazing fire that threatens our lives. And peptides, an essential tool for health management, serve as a powerful weapon in combating diseases and maintaining health.', 4, 20, NULL, 0, NULL, '2025-01-06 17:48:40');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (42, 'A blazing fire that threatens our lives. And peptides, an essential tool for health management, serve as a powerful weapon in combating diseases and maintaining health.', 4, 19, NULL, 0, NULL, '2025-01-06 17:48:40');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (43, 'A blazing fire that threatens our lives. And peptides, an essential tool for health management, serve as a powerful weapon in combating diseases and maintaining health.', 4, 18, NULL, 0, NULL, '2025-01-06 17:48:40');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (44, 'A blazing fire that threatens our lives. And peptides, an essential tool for health management, serve as a powerful weapon in combating diseases and maintaining health.', 4, 17, NULL, 1, NULL, '2025-01-06 17:48:40');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (45, 'A blazing fire that threatens our lives. And peptides, an essential tool for health management, serve as a powerful weapon in combating diseases and maintaining health.', 4, 15, NULL, 0, NULL, '2025-01-06 17:48:40');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (46, 'A blazing fire that threatens our lives. And peptides, an essential tool for health management, serve as a powerful weapon in combating diseases and maintaining health.', 4, 13, NULL, 0, NULL, '2025-01-06 17:48:40');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (47, 'A blazing fire that threatens our lives. And peptides, an essential tool for health management, serve as a powerful weapon in combating diseases and maintaining health.', 4, 12, NULL, 0, NULL, '2025-01-06 17:48:40');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (48, 'A blazing fire that threatens our lives. And peptides, an essential tool for health management, serve as a powerful weapon in combating diseases and maintaining health.', 4, 10, NULL, 0, NULL, '2025-01-06 17:48:40');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (49, 'A blazing fire that threatens our lives. And peptides, an essential tool for health management, serve as a powerful weapon in combating diseases and maintaining health.', 4, 8, NULL, 0, NULL, '2025-01-06 17:48:40');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (50, 'A blazing fire that threatens our lives. And peptides, an essential tool for health management, serve as a powerful weapon in combating diseases and maintaining health.', 4, 3, NULL, 0, NULL, '2025-01-06 17:48:40');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (51, 'The recorded 【Morning run steps】 exceeded the normal range: [3000,10000]. Please rest accordingly. Seek medical attention if necessary!', 3, 17, NULL, 0, NULL, '2025-01-06 17:49:08');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (52, '157;12;how to say', 1, 3, 17, 0, 12, '2025-01-06 17:56:46');
INSERT INTO `message` (`id`, `content`, `message_type`, `receiver_id`, `sender_id`, `is_read`, `content_id`, `create_time`) VALUES (53, '157;12;hahahha', 1, 17, 3, 0, 12, '2025-01-06 18:26:33');
COMMIT;

-- ----------------------------
-- Table structure for news
-- ----------------------------
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `tag_id` int DEFAULT NULL,
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `reader_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `is_top` tinyint(1) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of news
-- ----------------------------
BEGIN;
INSERT INTO `news` (`id`, `name`, `content`, `tag_id`, `cover`, `reader_ids`, `is_top`, `create_time`) VALUES (2, 'Disease, like a cunning hunter lurking in the shadows, is always ready to strike', '<p><br></p>', 2, 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2024-07-03 15:48:00');
INSERT INTO `news` (`id`, `name`, `content`, `tag_id`, `cover`, `reader_ids`, `is_top`, `create_time`) VALUES (3, 'Ten Indicators of Physical Health: How Many Do You Know?', '<p>As people\'s awareness of health increases, topics related to health are receiving more attention. Does being free of illness mean you are healthy? This definition is too narrow. A precise definition of health should include physical health, mental health, and good social adaptability. Today, let\'s take a detailed look at ten indicators of physical health.</p>\n\n<h4 style=\"text-align: justify;\"><strong>1. Dietary Indicator</strong></h4>\n<p style=\"text-align: justify;\">In general, adults should consume around 500 grams of food daily, while elderly individuals should limit their intake to no more than 350 grams due to weakened digestive function. If a person experiences excessive eating and drinking along with weight loss, they should consider whether they have diabetes or hyperthyroidism. Conversely, if their food intake drops below 250 grams per day for an extended period, they should check for inflammation or malignancies and seek timely medical examination.</p>\n\n<h4 style=\"text-align: justify;\"><strong>2. Weight Indicator</strong></h4>\n<p style=\"text-align: justify;\">Weight is not necessarily better when lower. Here’s a formula to calculate your ideal weight: \n- **Men**: (Height in cm - 80) × 70% = Standard weight \n- **Women**: (Height in cm - 70) × 60% = Standard weight \nIf weight drops rapidly in a short period, resulting in noticeable emaciation, this may be a sign of diabetes, hyperthyroidism, cancer, or gastrointestinal diseases. On the other hand, rapid weight gain may be linked to unhealthy eating habits, high cholesterol, hypothyroidism, or diabetes. Studies show that 60%–80% of adult diabetes patients were overweight before diagnosis.</p>\n\n<h4 style=\"text-align: justify;\"><strong>3. Body Temperature Indicator</strong></h4>\n<p style=\"text-align: justify;\">Normal body temperature should range between 36°C and 37°C. A temperature exceeding 37°C is considered a fever. Additionally, some individuals may have a lower-than-normal body temperature, known as \"hypothermia,\" which is common in elderly individuals, those with chronic malnutrition, hypothyroidism, or frequent shock episodes.</p>\n\n<h4 style=\"text-align: justify;\"><strong>4. Pulse Indicator (Heart Rate)</strong></h4>\n<p style=\"text-align: justify;\">A normal adult pulse rate is between 60–100 beats per minute (athletes may have lower rates). If the pulse is too fast, too slow, irregular, weak, or fluctuates, it may indicate heart problems that require immediate medical attention. Elderly individuals typically have slightly lower heart rates but should not drop below 55 beats per minute. If a person’s heart rate suddenly increases to 80–90 beats per minute without explanation, they should seek medical evaluation.</p>\n\n<h4 style=\"text-align: justify;\"><strong>5. Breathing Indicator</strong></h4>\n<p style=\"text-align: justify;\">Healthy breathing should be steady and regular, averaging 16–18 breaths per minute. Rapid breathing, gasping, breathlessness, or inconsistent breathing patterns, chest tightness, and suffocation are signs of abnormal conditions. Rapid breathing and shortness of breath are common in asthma, while chest tightness and suffocation may indicate heart disease. For elderly individuals, weakened cardiopulmonary function may cause shortness of breath after physical activity, but symptoms should improve with rest.</p>\n\n<h4 style=\"text-align: justify;\"><strong>6. Bowel Movement Indicator</strong></h4>\n<p style=\"text-align: justify;\">A healthy person should have a bowel movement once daily or every other day. Normal stool is yellow and soft. Elderly individuals with lower food intake and less physical activity may have bowel movements every 2–3 days, which is normal as long as stools are not hard. However, if a person has not had a bowel movement for over three days, experiences difficulty passing stool, incomplete evacuation, or dry stool, they may have constipation. Conversely, more than three bowel movements per day with watery or unformed stool may indicate diarrhea. Colitis can also be diagnosed through stool tests, with symptoms such as blood in the stool, constipation, or diarrhea.</p>\n\n<h4 style=\"text-align: justify;\"><strong>7. Urination Indicator</strong></h4>\n<p style=\"text-align: justify;\">An adult should urinate 1–2 liters per day, typically every 2–4 hours (excluding nighttime). Healthy urine is pale yellow, clear, and slightly foamy. If urine appears red or reddish-yellow, it may indicate a urinary tract infection. Greenish-blue urine is often associated with Pseudomonas aeruginosa infections. Blood in the urine may indicate urogenital diseases, tumors, or kidney stones. Frequent urination and urgency are typical symptoms of prostate disease.</p>\n\n<h4 style=\"text-align: justify;\"><strong>8. Blood Pressure Indicator</strong></h4>\n<p style=\"text-align: justify;\">Most people are familiar with this measure. A normal adult blood pressure reading should not exceed 140/80 mmHg. In elderly individuals, reduced vascular elasticity and accumulated waste in the body may require the heart to exert more pressure, causing elevated blood pressure. However, systolic blood pressure in the elderly should not exceed 160 mmHg. If it does, hypertension is diagnosed, requiring medication even if no symptoms are present. If only diastolic pressure is high, a hospital examination is recommended—self-medication should be avoided.</p>\n\n<h4 style=\"text-align: justify;\"><strong>9. Sleep Indicator</strong></h4>\n<p style=\"text-align: justify;\">Adults generally need 6–8 hours of sleep per night, though quality matters more than quantity. Sleep duration varies by age: \n- **10–18 years old**: 8 hours \n- **18–50 years old**: 7 hours \n- **50–70 years old**: 5–6 hours \nElderly individuals often experience declining sleep quality and should not be forced to sleep for a fixed number of hours. As long as they wake up feeling rested and energetic, their sleep is considered healthy. Napping is also recommended for the elderly as it benefits overall well-being.</p>\n\n<h4 style=\"text-align: justify;\"><strong>10. Mental Health Indicator</strong></h4>\n<p style=\"text-align: justify;\">The World Health Organization defines mental health by three key attributes:</p>\n<p style=\"text-align: justify;\">- **Good personality and temperament**: Stable emotions, mild personality, strong willpower, rich emotions, and an optimistic attitude.</p>\n<p style=\"text-align: justify;\">- **Good adaptability**: Objective observation, strong self-control, and the ability to cope with complex social environments.</p>\n<p style=\"text-align: justify;\">- **Good interpersonal relationships**: Tolerant and kind in dealing with others, helpful, and cooperative.</p>\n<p style=\"text-align: justify;\">A mentally healthy individual should be energetic, emotionally rich, and quick to take action. If they do not meet these criteria, they should consider whether they have underlying health issues.</p>\n\n<p>Check your health indicators today! Identifying minor health issues early can prevent them from developing into major concerns.</p>', 2, 'https://images.unsplash.com/photo-1579684385127-1ef15d508118?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2024-07-14 14:27:51');
INSERT INTO `news` (`id`, `name`, `content`, `tag_id`, `cover`, `reader_ids`, `is_top`, `create_time`) VALUES (4, 'Seven Key Principles of Health: Achieving Just Two Is Impressive', '<p>Some say that all it takes to defeat a young person is a single medical report.</p>\n\n<p style=\"text-align: justify;\">The Youth and Middle-Aged News Agency conducted a survey, revealing that 45.2% of young respondents fear medical checkups. Among them, post-95 generation respondents have the highest fear rate, reaching 52.3%.</p>\n\n<p style=\"text-align: justify;\">The main reason 68.1% of respondents fear medical exams is the worry of discovering health problems.</p>\n\n<p style=\"text-align: justify;\"><strong>Indeed, many diseases are increasingly affecting younger generations. Conditions once thought to occur in people in their sixties or seventies now trouble individuals in their twenties and thirties.</strong></p>\n\n<p style=\"text-align: justify;\">For example, osteoporosis, stroke, and type 2 diabetes are becoming more common. A television program focused on cognitive disorders, <em>Forget Me Not Restaurant</em>, once featured a guest who was diagnosed with Alzheimer\\\'s disease at just 36 years old.</p>\n\n<p style=\"text-align: justify;\">If unhealthy dietary habits, irregular routines, and lack of exercise contribute to these diseases, what is causing us to adopt such unhealthy lifestyles?</p>\n\n<p style=\"text-align: justify;\">John Ratey, a professor at Harvard Medical School and author of <em>Exercise Transforms the Brain</em>, wrote a book titled <em>The Doctor’s Most Wanted Advice</em>.</p>\n\n<p style=\"text-align: justify;\">He argues that while civilization has advanced rapidly, our bodies have not evolved at the same pace. This misalignment affects all aspects of our lives, including physical and emotional health.</p>\n\n<p style=\"text-align: justify;\"><strong>Professor Ratey believes that diseases do not stem from flaws in the human body but rather from self-inflicted harm caused by modern lifestyles.</strong></p>\n\n<p style=\"text-align: justify;\">Over the course of evolution, humans have developed three core advantages. However, modern life is gradually eroding these benefits.</p>\n\n<h4 style=\"text-align: justify;\"><strong>1. Humans Are Exceptionally Capable of Movement</strong></h4>\n\n<p style=\"text-align: justify;\">Primates can run, but they are neither fast nor capable of long-distance running. Humans, however, can do both.</p>\n\n<p style=\"text-align: justify;\">The ability to run is tied to bipedal walking, which requires significant brain coordination and planning. When it comes to physical activity, humans are truly versatile athletes.</p>\n\n<p style=\"text-align: justify;\">However, in today\\\'s world, many people sit for hours, with their only exercise being the short walk from home to the subway station.</p>\n\n<h4 style=\"text-align: justify;\"><strong>2. Humans Have a Diverse Diet</strong></h4>\n\n<p style=\"text-align: justify;\">Humans are omnivores, and our gut microbiome is made up of thousands of different bacterial species. These bacteria help break down food, increasing its nutritional value beyond what we can digest on our own.</p>\n\n<p style=\"text-align: justify;\">A diverse diet ensures that humans receive a wide range of essential micronutrients to support complex bodily functions.</p>\n\n<p style=\"text-align: justify;\">However, modern diets are dominated by processed foods designed to provide quick energy. These foods lack the natural integrity of whole foods.</p>\n\n<h4 style=\"text-align: justify;\"><strong>3. Humans Possess Empathy</strong></h4>\n\n<p style=\"text-align: justify;\">In the 1980s and 1990s, a team of Italian scientists discovered a type of brain cell called \"mirror neurons.\"</p>\n\n<p style=\"text-align: justify;\">In an experiment, researchers observed electrical activity in a monkey’s brain while it ate peanuts. When the monkey simply watched another monkey eat peanuts, the same neurons fired, as if it were eating the peanuts itself. This is the function of mirror neurons.</p>\n\n<p style=\"text-align: justify;\">Humans not only perceive others’ emotions but can also deeply empathize with them. This ability is the foundation of human cooperation and social cohesion.</p>\n\n<p style=\"text-align: justify;\">However, in modern times, many people suffer from social anxiety, preferring solitude over interpersonal interactions.</p>\n\n<p style=\"text-align: justify;\">The Bill & Melinda Gates Foundation conducted a large-scale study across 187 countries to investigate the leading causes of death, physical disability, and declining quality of life.</p>\n\n<p style=\"text-align: justify;\"><strong>The study identified 12 major risk factors contributing to disease and physical deterioration worldwide:</strong> \n- High blood pressure\n- Smoking\n- Alcohol consumption\n- Indoor air pollution from cooking fumes\n- Low fruit intake\n- High body mass index (BMI)\n- High blood sugar levels\n- Low body weight\n- Outdoor air pollution\n- Physical inactivity\n- High salt intake\n- Low consumption of nuts and seeds</p>', 1, '/img/articles/principles-health.jpg', NULL, 0, '2024-07-14 14:29:09');
INSERT INTO `news` (`id`, `name`, `content`, `tag_id`, `cover`, `reader_ids`, `is_top`, `create_time`) VALUES (5, 'Gold Standards\" for Physical Health', '<p>Is a person truly healthy?</p>\n\n<p style=\"text-align: justify;\">The most direct way to assess health is through common physical indicators.</p>\n\n<p style=\"text-align: justify;\">Understanding the normal values of these indicators,</p>\n\n<p style=\"text-align: justify;\">as well as the healthy lifestyle habits needed to maintain them,</p>\n\n<p style=\"text-align: justify;\">is something everyone should know!</p>\n\n<p style=\"text-align: justify;\">Take a moment to check your own health status!</p>\n\n<p style=\"text-align: justify;\">10 \"Gold Standards\" for Physical Health</p>\n\n<h4 style=\"text-align: justify;\"><strong>1. Blood Pressure Standards</strong></h4>\n\n<p style=\"text-align: justify;\">Systolic pressure &lt;120 mmHg, Diastolic pressure &lt;80 mmHg</p>\n\n<p style=\"text-align: justify;\">According to the <em>Chinese Guidelines for the Prevention and Treatment of Hypertension (2018 Revised Edition)</em>, jointly issued by the Chinese Society of Cardiology and other institutions, the normal blood pressure range for Chinese adults is:</p>\n\n<p style=\"text-align: justify;\">Systolic pressure &lt;120 mmHg, Diastolic pressure &lt;80 mmHg.</p>\n\n<p style=\"text-align: justify;\">Abnormal blood pressure can affect not only vascular health but also the heart and brain. The <em>Chinese Hypertension Patient Education Guidelines</em> point out that hypertension is the most significant risk factor for stroke, with 70% of stroke patients in China having high blood pressure.</p>\n\n<p style=\"text-align: justify;\"><img src=\"https://p3-sign.toutiaoimg.com/pgc-image/SUnEYkXADWQXzc~noop.image?_iz=58558&amp;from=article.pc_detail&amp;lk3s=953192f4&amp;x-expires=1721543649&amp;x-signature=RuB5B1ApqxK6xu2fLl%2Bvs%2Bt4m%2BM%3D\" alt=\"\" data-href=\"\" style=\"height: auto;\"></p>\n\n<p>Image source: <em>Chinese Guidelines for the Prevention and Treatment of Hypertension (2018 Revised Edition)</em></p>', 3, 'https://images.unsplash.com/photo-1628595351029-c2bf17511435?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2024-07-14 14:34:58');
INSERT INTO `news` (`id`, `name`, `content`, `tag_id`, `cover`, `reader_ids`, `is_top`, `create_time`) VALUES (6, 'The Impact of Sleep Quality on Health and Ways to Improve It', '<p>30 Years Ago…</p>\n\n<p style=\"text-align: justify;\">Cancer and heart disease seemed far away from us.</p>\n<p style=\"text-align: justify;\">Diabetes was rarely heard of.</p>\n<p style=\"text-align: justify;\">\"Fatty liver\" did not appear in medical textbooks.</p>\n<p style=\"text-align: justify;\">Mental and psychological illnesses were extremely rare.</p>\n<p style=\"text-align: justify;\">Few people drank milk or consumed milk powder.</p>\n<p style=\"text-align: justify;\">No one talked about calcium supplements.</p>\n<p style=\"text-align: justify;\">Alcohol was only consumed during festive celebrations.</p>\n<p style=\"text-align: justify;\">Obese individuals were almost nonexistent.</p>\n\n<p style=\"text-align: justify;\"><br>Now…</p>\n\n<p style=\"text-align: justify;\">One in five people has a chronic disease.</p>\n<p style=\"text-align: justify;\">Each year, 12 million new chronic disease cases arise.</p>\n<p style=\"text-align: justify;\">One in five adults has cardiovascular disease.</p>\n<p style=\"text-align: justify;\">Every 10 seconds, someone dies from cardiovascular disease.</p>\n\n<p style=\"text-align: justify;\"><br>Now…</p>\n\n<p style=\"text-align: justify;\">The lung cancer mortality rate has increased fivefold.</p>\n<p style=\"text-align: justify;\">One-third of the world’s lung cancer cases are in China.</p>\n<p style=\"text-align: justify;\">China ranks first globally in the number of smokers.</p>\n<p style=\"text-align: justify;\">China also ranks first in the number of alcohol consumers.</p>\n<p style=\"text-align: justify;\">Mental and psychological illnesses rank first worldwide.</p>\n<p style=\"text-align: justify;\">China has the highest number of diabetes patients in the world.</p>\n\n<p style=\"text-align: justify;\">Therefore, we have compiled an overview of major health statistics across various age groups!</p>\n\n<h4 style=\"text-align: justify;\"><strong>Overview of China’s Health Data</strong></h4>\n\n<p style=\"text-align: justify;\">- 160-170 million people in China have hypertension.</p>\n<p style=\"text-align: justify;\">- Over 100 million people have high blood lipids.</p>\n<p style=\"text-align: justify;\">- 92.4 million people have diabetes.</p>\n<p style=\"text-align: justify;\">- 70 million to 200 million people are overweight or obese.</p>\n<p style=\"text-align: justify;\">- 160 million people have abnormal blood lipids.</p>\n<p style=\"text-align: justify;\">- 122 million people have fatty liver disease.</p>\n<p style=\"text-align: justify;\">- Every 30 seconds, someone is diagnosed with cancer.</p>\n<p style=\"text-align: justify;\">- Every 30 seconds, someone is diagnosed with diabetes.</p>\n<p style=\"text-align: justify;\">- Every 30 seconds, at least one person dies from cardiovascular diseases.</p>\n\n<h4 style=\"text-align: justify;\"><strong>Health Concerns for China’s Urban White-Collar Workers</strong></h4>\n\n<p style=\"text-align: justify;\">Currently, the sub-health rate among white-collar workers in China’s major cities is as high as 76%.</p>\n<p style=\"text-align: justify;\">Nearly 60% are in an overworked state.</p>\n<p style=\"text-align: justify;\"><strong>Less than 3% of the population can be considered truly healthy.</strong></p>\n<p style=\"text-align: justify;\">White-collar women are more susceptible to gynecological and cardiovascular diseases, while men face risks of sudden death, overwork, and cancer.</p>\n<p style=\"text-align: justify;\">A report from the Chinese Academy of Social Sciences states that seven professions have a high risk of death from overwork.</p>\n<p style=\"text-align: justify;\">If Chinese intellectual workers do not improve their sub-health conditions, two-thirds of them will die from cardiovascular diseases in the near future.</p>\n\n<p style=\"text-align: justify;\"><br><strong>Health is Not Directly Linked to Medical Treatment</strong></p>\n\n<p style=\"text-align: justify;\">A U.S. study confirmed that only 10% of factors affecting lifespan are related to medical care.</p>\n<p style=\"text-align: justify;\">25 of the 30 years of increased lifespan in Americans are unrelated to medical advancements.</p>\n<p style=\"text-align: justify;\">Misdiagnosis rates for common diseases are as high as 27%, while for major diseases, the misdiagnosis rate reaches 40%.</p>\n<p style=\"text-align: justify;\">Another U.S. study found that an advanced modern medical system has little impact on overall health.</p>\n<p style=\"text-align: justify;\">A UK study found that 85% of medications are ineffective and that minimizing medical interventions is the best approach.</p>\n<p style=\"text-align: justify;\">A U.S. study found that 30-40% of surgeries are unnecessary.</p>\n\n<p style=\"text-align: justify;\"><br><strong>Adverse Drug Reactions Are a Major Health Threat</strong></p>\n\n<p style=\"text-align: justify;\">In 1994, more than 2.2 million people in the U.S. required hospitalization due to severe drug side effects.</p>\n<p style=\"text-align: justify;\">Over 106,000 people died from adverse drug reactions that year.</p>\n<p style=\"text-align: justify;\">A shocking report from the <em>Journal of the American Medical Association (JAMA)</em> revealed the severe consequences of drug side effects.</p>\n<p style=\"text-align: justify;\">On September 12, 2006, China’s first “Birth Defects Prevention Day” was observed.</p>\n<p style=\"text-align: justify;\">That same year, monitoring data showed that 720 babies with birth defects were born daily in China, many of which were caused by drug-related side effects.</p>\n\n<h4 style=\"text-align: justify;\"><strong>Results from the Fourth National Health Services Survey</strong></h4>\n\n<p style=\"text-align: justify;\">- In 2008, chronic disease prevalence reached 20%, accounting for 83% of total deaths.</p>\n<p style=\"text-align: justify;\">- Over the past 11 years, the number of new chronic disease cases nearly doubled annually.</p>\n<p style=\"text-align: justify;\">- Heart disease and malignant tumor cases nearly doubled.</p>\n<p style=\"text-align: justify;\">- A report from <em>Health Affairs</em> stated that China\'s waistline expansion rate is set to become the fastest in the world.</p>\n<p style=\"text-align: justify;\">- The number of obese people is expected to reach 325 million, doubling over the next 20 years.</p>\n<p style=\"text-align: justify;\">- An increase of just one inch (2.54 cm) in waist circumference leads to four extra miles of blood vessels and increases cancer risk by eight times.</p>\n\n<h4 style=\"text-align: justify;\"><strong>Health Data of Chinese Adolescents</strong></h4>\n\n<p style=\"text-align: justify;\">- 80% of students have poor breakfast nutrition.</p>\n<p style=\"text-align: justify;\">- The anemia rate among adolescents is 38%.</p>\n<p style=\"text-align: justify;\">- The incidence of fatty liver disease among obese children is 40-50%.</p>\n<p style=\"text-align: justify;\">- Myopia rates among students:</p>\n<p style=\"text-align: justify;\">  - Primary school students: 32.5%</p>\n<p style=\"text-align: justify;\">  - Middle school students: 59.4%</p>\n<p style=\"text-align: justify;\">  - High school students: 77.3%</p>\n<p style=\"text-align: justify;\">  - College students: 80%</p>', 3, 'https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2024-07-16 16:09:06');
INSERT INTO `news` (`id`, `name`, `content`, `tag_id`, `cover`, `reader_ids`, `is_top`, `create_time`) VALUES (7, 'The Fast-Paced Modern Life, Like an Invisible Hand', '<p> </p>\n\n<p style=\"text-align: justify;\">In the past, as China\\\'s economy rapidly developed, many people experienced significant improvements in their living standards and material conditions. However, behind this rapid development, some overlooked issues emerged, with health problems being the most prominent. As economic growth slows and society moves toward stability, people are beginning to realize that health is the most important part of life.</p>\n\n<p style=\"text-align: justify;\"><strong>Shocking Health Data of the Chinese Population!</strong></p>\n\n<p style=\"text-align: justify;\">After careful data collection and analysis, we have obtained a comprehensive set of statistics on the health status of the Chinese population. These numbers are not just cold statistics; they represent real lives, reminding us of the invaluable importance of health.</p>\n\n<p style=\"text-align: justify;\">Hypertension: 160-170 million people</p>\n<p style=\"text-align: justify;\">High blood lipids: Over 100 million people</p>\n<p style=\"text-align: justify;\">Diabetes patients: 92.4 million people</p>\n<p style=\"text-align: justify;\">Overweight or obese individuals: 70 million - 200 million</p>\n<p style=\"text-align: justify;\">Abnormal blood lipids: 160 million people</p>\n<p style=\"text-align: justify;\">Fatty liver disease: 120 million people</p>\n<p style=\"text-align: justify;\">Every 30 seconds, someone is diagnosed with cancer.</p>\n<p style=\"text-align: justify;\">Every 30 seconds, someone is diagnosed with diabetes.</p>\n<p style=\"text-align: justify;\">Every 30 seconds, at least one person dies from cardiovascular diseases.</p>\n\n<p style=\"text-align: justify;\">Behind these cold statistics lie the pain and helplessness of countless families. They silently reveal a cruel reality: In our fast-paced lives, we often chase wealth and material pleasures while neglecting our health. When health issues quietly emerge, we realize—sometimes too late—that nothing is more important than well-being.</p>\n\n<p style=\"text-align: justify;\"><strong>Establishing a Healthy Mindset</strong></p>\n\n<p style=\"text-align: justify;\">The fast-paced modern lifestyle, like an invisible hand, pushes us forward while subtly changing our habits. Irregular sleep schedules, high-fat and high-sodium diets, and other unhealthy habits are eroding our health, gradually increasing the risk of chronic diseases.</p>\n\n<p style=\"text-align: justify;\">Even more concerning is that our awareness of health remains insufficient. In the past, people focused solely on economic growth and material improvement, forgetting that health is the foundation of life. What we need is not just advancements in medical technology but also the deep-rooted awareness of health and the conscious adoption of healthy behaviors.</p>\n\n<p style=\"text-align: justify;\">Health is a priority for everyone. We must cherish our bodies and constantly remind ourselves that health is what truly matters—don’t wait until it’s too late to regret neglecting it.</p>\n\n<p style=\"text-align: justify;\"><strong>Peptide Supplementation is Urgent</strong></p>\n\n<p style=\"text-align: justify;\">Diseases, like cunning hunters lurking in the dark, are always ready to strike. They operate silently yet are incredibly powerful. If we ignore them, they can rapidly spread, becoming a raging fire that threatens our lives. Peptide supplementation is a powerful tool for managing health, helping us combat diseases and maintain well-being.</p>\n\n<p style=\"text-align: justify;\">Peptide supplementation refers to replenishing the body with essential peptides. The human body naturally contains small-molecule peptides, but as we age, these peptides gradually decrease, leading to functional decline. Therefore, timely peptide supplementation is essential for maintaining good health.</p>\n\n<p style=\"text-align: justify;\">Small-molecule peptides act like a guardian, creating a strong barrier to protect our health. They enhance our resistance to external pathogens, improve nutrient absorption, and prevent chronic diseases.</p>\n\n<p style=\"text-align: justify;\">Remember, health is our most valuable asset. Only with a healthy body can we truly enjoy life and pursue our dreams. Without health, we cannot experience the beauty of life, be there for our loved ones, or achieve our aspirations.</p>\n\n<p>So, for yourself, for your family, and for your friends, start prioritizing health today! In this era filled with challenges and opportunities, let us move forward together, using peptide supplementation as a key health management tool to safeguard our well-being.</p>', 3, 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=1600&h=720&q=80', NULL, NULL, '2024-07-16 16:10:35');
INSERT INTO `news` (`id`, `name`, `content`, `tag_id`, `cover`, `reader_ids`, `is_top`, `create_time`) VALUES (8, 'We Still Do Not Pay Enough Attention to Health', '<p> </p>\n\n<p style=\"text-align: justify;\">In the past, as China\\\'s economy rapidly developed, many people experienced significant improvements in their living standards and material conditions. However, behind this rapid development, some overlooked issues emerged, with health problems being the most prominent. As economic growth slows and society moves toward stability, people are beginning to realize that health is the most important part of life.</p>\n\n<p style=\"text-align: justify;\"><strong>Shocking Health Data of the Chinese Population!</strong></p>\n\n<p style=\"text-align: justify;\">After careful data collection and analysis, we have obtained a comprehensive set of statistics on the health status of the Chinese population. These numbers are not just cold statistics; they represent real lives, reminding us of the invaluable importance of health.</p>\n\n<p style=\"text-align: justify;\">Hypertension: 160-170 million people</p>\n<p style=\"text-align: justify;\">High blood lipids: Over 100 million people</p>\n<p style=\"text-align: justify;\">Diabetes patients: 92.4 million people</p>\n<p style=\"text-align: justify;\">Overweight or obese individuals: 70 million - 200 million</p>\n<p style=\"text-align: justify;\">Abnormal blood lipids: 160 million people</p>\n<p style=\"text-align: justify;\">Fatty liver disease: 120 million people</p>\n<p style=\"text-align: justify;\">Every 30 seconds, someone is diagnosed with cancer.</p>\n<p style=\"text-align: justify;\">Every 30 seconds, someone is diagnosed with diabetes.</p>\n<p style=\"text-align: justify;\">Every 30 seconds, at least one person dies from cardiovascular diseases.</p>\n\n<p style=\"text-align: justify;\">Behind these cold statistics lie the pain and helplessness of countless families. They silently reveal a cruel reality: In our fast-paced lives, we often chase wealth and material pleasures while neglecting our health. When health issues quietly emerge, we realize—sometimes too late—that nothing is more important than well-being.</p>\n\n<p style=\"text-align: justify;\"><strong>Establishing a Healthy Mindset</strong></p>\n\n<p style=\"text-align: justify;\">The fast-paced modern lifestyle, like an invisible hand, pushes us forward while subtly changing our habits. Irregular sleep schedules, high-fat and high-sodium diets, and other unhealthy habits are eroding our health, gradually increasing the risk of chronic diseases.</p>\n\n<p style=\"text-align: justify;\">Even more concerning is that our awareness of health remains insufficient. In the past, people focused solely on economic growth and material improvement, forgetting that health is the foundation of life. What we need is not just advancements in medical technology but also the deep-rooted awareness of health and the conscious adoption of healthy behaviors.</p>\n\n<p style=\"text-align: justify;\">Health is a priority for everyone. We must cherish our bodies and constantly remind ourselves that health is what truly matters—don’t wait until it’s too late to regret neglecting it.</p>\n\n<p style=\"text-align: justify;\"><strong>Peptide Supplementation is Urgent</strong></p>\n\n<p style=\"text-align: justify;\">Diseases, like cunning hunters lurking in the dark, are always ready to strike. They operate silently yet are incredibly powerful. If we ignore them, they can rapidly spread, becoming a raging fire that threatens our lives. Peptide supplementation is a powerful tool for managing health, helping us combat diseases and maintain well-being.</p>\n\n<p style=\"text-align: justify;\">Peptide supplementation refers to replenishing the body with essential peptides. The human body naturally contains small-molecule peptides, but as we age, these peptides gradually decrease, leading to functional decline. Therefore, timely peptide supplementation is essential for maintaining good health.</p>\n\n<p style=\"text-align: justify;\">Small-molecule peptides act like a guardian, creating a strong barrier to protect our health. They enhance our resistance to external pathogens, improve nutrient absorption, and prevent chronic diseases.</p>\n\n<p style=\"text-align: justify;\">Remember, health is our most valuable asset. Only with a healthy body can we truly enjoy life and pursue our dreams. Without health, we cannot experience the beauty of life, be there for our loved ones, or achieve our aspirations.</p>\n\n<p>So, for yourself, for your family, and for your friends, start prioritizing health today! In this era filled with challenges and opportunities, let us move forward together, using peptide supplementation as a key health management tool to safeguard our well-being.</p>', 3, 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=1600&h=720&q=80', NULL, NULL, '2024-07-16 16:11:31');
INSERT INTO `news` (`id`, `name`, `content`, `tag_id`, `cover`, `reader_ids`, `is_top`, `create_time`) VALUES (9, 'The Shadow of Chronic Diseases is Gradually Enveloping Our Lives', '<p> </p>\n\n<p style=\"text-align: justify;\">In the past, as China\\\'s economy rapidly developed, many people experienced significant improvements in their living standards and material conditions. However, behind this rapid development, some overlooked issues emerged, with health problems being the most prominent. As economic growth slows and society moves toward stability, people are beginning to realize that health is the most important part of life.</p>\n\n<p style=\"text-align: justify;\"><strong>Shocking Health Data of the Chinese Population!</strong></p>\n\n<p style=\"text-align: justify;\">After careful data collection and analysis, we have obtained a comprehensive set of statistics on the health status of the Chinese population. These numbers are not just cold statistics; they represent real lives, reminding us of the invaluable importance of health.</p>\n\n<p style=\"text-align: justify;\">Hypertension: 160-170 million people</p>\n<p style=\"text-align: justify;\">High blood lipids: Over 100 million people</p>\n<p style=\"text-align: justify;\">Diabetes patients: 92.4 million people</p>\n<p style=\"text-align: justify;\">Overweight or obese individuals: 70 million - 200 million</p>\n<p style=\"text-align: justify;\">Abnormal blood lipids: 160 million people</p>\n<p style=\"text-align: justify;\">Fatty liver disease: 120 million people</p>\n<p style=\"text-align: justify;\">Every 30 seconds, someone is diagnosed with cancer.</p>\n<p style=\"text-align: justify;\">Every 30 seconds, someone is diagnosed with diabetes.</p>\n<p style=\"text-align: justify;\">Every 30 seconds, at least one person dies from cardiovascular diseases.</p>\n\n<p style=\"text-align: justify;\">Behind these cold statistics lie the pain and helplessness of countless families. They silently reveal a cruel reality: In our fast-paced lives, we often chase wealth and material pleasures while neglecting our health. When health issues quietly emerge, we realize—sometimes too late—that nothing is more important than well-being.</p>\n\n<p style=\"text-align: justify;\"><strong>Establishing a Healthy Mindset</strong></p>\n\n<p style=\"text-align: justify;\">The fast-paced modern lifestyle, like an invisible hand, pushes us forward while subtly changing our habits. Irregular sleep schedules, high-fat and high-sodium diets, and other unhealthy habits are eroding our health, gradually increasing the risk of chronic diseases.</p>\n\n<p style=\"text-align: justify;\">Even more concerning is that our awareness of health remains insufficient. In the past, people focused solely on economic growth and material improvement, forgetting that health is the foundation of life. What we need is not just advancements in medical technology but also the deep-rooted awareness of health and the conscious adoption of healthy behaviors.</p>\n\n<p style=\"text-align: justify;\">Health is a priority for everyone. We must cherish our bodies and constantly remind ourselves that health is what truly matters—don’t wait until it’s too late to regret neglecting it.</p>\n\n<p style=\"text-align: justify;\"><strong>Peptide Supplementation is Urgent</strong></p>\n\n<p style=\"text-align: justify;\">Diseases, like cunning hunters lurking in the dark, are always ready to strike. They operate silently yet are incredibly powerful. If we ignore them, they can rapidly spread, becoming a raging fire that threatens our lives. Peptide supplementation is a powerful tool for managing health, helping us combat diseases and maintain well-being.</p>\n\n<p style=\"text-align: justify;\">Peptide supplementation refers to replenishing the body with essential peptides. The human body naturally contains small-molecule peptides, but as we age, these peptides gradually decrease, leading to functional decline. Therefore, timely peptide supplementation is essential for maintaining good health.</p>\n\n<p style=\"text-align: justify;\">Small-molecule peptides act like a guardian, creating a strong barrier to protect our health. They enhance our resistance to external pathogens, improve nutrient absorption, and prevent chronic diseases.</p>\n\n<p style=\"text-align: justify;\">Remember, health is our most valuable asset. Only with a healthy body can we truly enjoy life and pursue our dreams. Without health, we cannot experience the beauty of life, be there for our loved ones, or achieve our aspirations.</p>\n\n<p>So, for yourself, for your family, and for your friends, start prioritizing health today! In this era filled with challenges and opportunities, let us move forward together, using peptide supplementation as a key health management tool to safeguard our well-being.</p>', 2, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?auto=format&fit=crop&w=1600&h=720&q=80', NULL, NULL, '2024-07-16 16:12:23');
INSERT INTO `news` (`id`, `name`, `content`, `tag_id`, `cover`, `reader_ids`, `is_top`, `create_time`) VALUES (10, 'How to Establish a Healthy Lifestyle: Effective Health Management Techniques', '<p> </p>\n\n<p style=\"text-align: justify;\">In the past, as China\\\'s economy rapidly developed, many people experienced significant improvements in their living standards and material conditions. However, behind this rapid development, some overlooked issues emerged, with health problems being the most prominent. As economic growth slows and society moves toward stability, people are beginning to realize that health is the most important part of life.</p>\n\n<p style=\"text-align: justify;\"><strong>Shocking Health Data of the Chinese Population!</strong></p>\n\n<p style=\"text-align: justify;\">After careful data collection and analysis, we have obtained a comprehensive set of statistics on the health status of the Chinese population. These numbers are not just cold statistics; they represent real lives, reminding us of the invaluable importance of health.</p>\n\n<p style=\"text-align: justify;\">Hypertension: 160-170 million people</p>\n<p style=\"text-align: justify;\">High blood lipids: Over 100 million people</p>\n<p style=\"text-align: justify;\">Diabetes patients: 92.4 million people</p>\n<p style=\"text-align: justify;\">Overweight or obese individuals: 70 million - 200 million</p>\n<p style=\"text-align: justify;\">Abnormal blood lipids: 160 million people</p>\n<p style=\"text-align: justify;\">Fatty liver disease: 120 million people</p>\n<p style=\"text-align: justify;\">Every 30 seconds, someone is diagnosed with cancer.</p>\n<p style=\"text-align: justify;\">Every 30 seconds, someone is diagnosed with diabetes.</p>\n<p style=\"text-align: justify;\">Every 30 seconds, at least one person dies from cardiovascular diseases.</p>\n\n<p style=\"text-align: justify;\">Behind these cold statistics lie the pain and helplessness of countless families. They silently reveal a cruel reality: In our fast-paced lives, we often chase wealth and material pleasures while neglecting our health. When health issues quietly emerge, we realize—sometimes too late—that nothing is more important than well-being.</p>\n\n<p style=\"text-align: justify;\"><strong>Establishing a Healthy Mindset</strong></p>\n\n<p style=\"text-align: justify;\">The fast-paced modern lifestyle, like an invisible hand, pushes us forward while subtly changing our habits. Irregular sleep schedules, high-fat and high-sodium diets, and other unhealthy habits are eroding our health, gradually increasing the risk of chronic diseases.</p>\n\n<p style=\"text-align: justify;\">Even more concerning is that our awareness of health remains insufficient. In the past, people focused solely on economic growth and material improvement, forgetting that health is the foundation of life. What we need is not just advancements in medical technology but also the deep-rooted awareness of health and the conscious adoption of healthy behaviors.</p>\n\n<p style=\"text-align: justify;\">Health is a priority for everyone. We must cherish our bodies and constantly remind ourselves that health is what truly matters—don’t wait until it’s too late to regret neglecting it.</p>\n\n<p style=\"text-align: justify;\"><strong>Peptide Supplementation is Urgent</strong></p>\n\n<p style=\"text-align: justify;\">Diseases, like cunning hunters lurking in the dark, are always ready to strike. They operate silently yet are incredibly powerful. If we ignore them, they can rapidly spread, becoming a raging fire that threatens our lives. Peptide supplementation is a powerful tool for managing health, helping us combat diseases and maintain well-being.</p>\n\n<p style=\"text-align: justify;\">Peptide supplementation refers to replenishing the body with essential peptides. The human body naturally contains small-molecule peptides, but as we age, these peptides gradually decrease, leading to functional decline. Therefore, timely peptide supplementation is essential for maintaining good health.</p>\n\n<p style=\"text-align: justify;\">Small-molecule peptides act like a guardian, creating a strong barrier to protect our health. They enhance our resistance to external pathogens, improve nutrient absorption, and prevent chronic diseases.</p>\n\n<p style=\"text-align: justify;\">Remember, health is our most valuable asset. Only with a healthy body can we truly enjoy life and pursue our dreams. Without health, we cannot experience the beauty of life, be there for our loved ones, or achieve our aspirations.</p>\n\n<p>So, for yourself, for your family, and for your friends, start prioritizing health today! In this era filled with challenges and opportunities, let us move forward together, using peptide supplementation as a key health management tool to safeguard our well-being.</p>', 3, 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2024-07-16 16:12:56');
INSERT INTO `news` (`id`, `name`, `content`, `tag_id`, `cover`, `reader_ids`, `is_top`, `create_time`) VALUES (11, 'For Family and Friends, Start Health Maintenance Now!', '<p> </p>\n\n<p style=\"text-align: justify;\">In the past, as China\\\'s economy rapidly developed, many people experienced significant improvements in their living standards and material conditions. However, behind this rapid development, some overlooked issues emerged, with health problems being the most prominent. As economic growth slows and society moves toward stability, people are beginning to realize that health is the most important part of life.</p>\n\n<p style=\"text-align: justify;\"><strong>Shocking Health Data of the Chinese Population!</strong></p>\n\n<p style=\"text-align: justify;\">After careful data collection and analysis, we have obtained a comprehensive set of statistics on the health status of the Chinese population. These numbers are not just cold statistics; they represent real lives, reminding us of the invaluable importance of health.</p>\n\n<p style=\"text-align: justify;\">Hypertension: 160-170 million people</p>\n<p style=\"text-align: justify;\">High blood lipids: Over 100 million people</p>\n<p style=\"text-align: justify;\">Diabetes patients: 92.4 million people</p>\n<p style=\"text-align: justify;\">Overweight or obese individuals: 70 million - 200 million</p>\n<p style=\"text-align: justify;\">Abnormal blood lipids: 160 million people</p>\n<p style=\"text-align: justify;\">Fatty liver disease: 120 million people</p>\n<p style=\"text-align: justify;\">Every 30 seconds, someone is diagnosed with cancer.</p>\n<p style=\"text-align: justify;\">Every 30 seconds, someone is diagnosed with diabetes.</p>\n<p style=\"text-align: justify;\">Every 30 seconds, at least one person dies from cardiovascular diseases.</p>\n\n<p style=\"text-align: justify;\">Behind these cold statistics lie the pain and helplessness of countless families. They silently reveal a cruel reality: In our fast-paced lives, we often chase wealth and material pleasures while neglecting our health. When health issues quietly emerge, we realize—sometimes too late—that nothing is more important than well-being.</p>\n\n<p style=\"text-align: justify;\"><strong>Establishing a Healthy Mindset</strong></p>\n\n<p style=\"text-align: justify;\">The fast-paced modern lifestyle, like an invisible hand, pushes us forward while subtly changing our habits. Irregular sleep schedules, high-fat and high-sodium diets, and other unhealthy habits are eroding our health, gradually increasing the risk of chronic diseases.</p>\n\n<p style=\"text-align: justify;\">Even more concerning is that our awareness of health remains insufficient. In the past, people focused solely on economic growth and material improvement, forgetting that health is the foundation of life. What we need is not just advancements in medical technology but also the deep-rooted awareness of health and the conscious adoption of healthy behaviors.</p>\n\n<p style=\"text-align: justify;\">Health is a priority for everyone. We must cherish our bodies and constantly remind ourselves that health is what truly matters—don’t wait until it’s too late to regret neglecting it.</p>\n\n<p style=\"text-align: justify;\"><strong>Peptide Supplementation is Urgent</strong></p>\n\n<p style=\"text-align: justify;\">Diseases, like cunning hunters lurking in the dark, are always ready to strike. They operate silently yet are incredibly powerful. If we ignore them, they can rapidly spread, becoming a raging fire that threatens our lives. Peptide supplementation is a powerful tool for managing health, helping us combat diseases and maintain well-being.</p>\n\n<p style=\"text-align: justify;\">Peptide supplementation refers to replenishing the body with essential peptides. The human body naturally contains small-molecule peptides, but as we age, these peptides gradually decrease, leading to functional decline. Therefore, timely peptide supplementation is essential for maintaining good health.</p>\n\n<p style=\"text-align: justify;\">Small-molecule peptides act like a guardian, creating a strong barrier to protect our health. They enhance our resistance to external pathogens, improve nutrient absorption, and prevent chronic diseases.</p>\n\n<p style=\"text-align: justify;\">Remember, health is our most valuable asset. Only with a healthy body can we truly enjoy life and pursue our dreams. Without health, we cannot experience the beauty of life, be there for our loved ones, or achieve our aspirations.</p>\n\n<p>So, for yourself, for your family, and for your friends, start prioritizing health today! In this era filled with challenges and opportunities, let us move forward together, using peptide supplementation as a key health management tool to safeguard our well-being.</p>', 2, 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2024-07-16 16:13:40');
INSERT INTO `news` (`id`, `name`, `content`, `tag_id`, `cover`, `reader_ids`, `is_top`, `create_time`) VALUES (12, 'Exploring the Importance of a Healthy Diet for the Body', '<p style=\"text-align: justify;\">What you eat shapes your energy, mood, and long-term risk for chronic conditions. A healthy diet is not about perfection or extreme rules; it is about consistent patterns: enough variety, mostly whole foods, and portions that match your day.</p>\n\n<h3 style=\"text-align: justify;\"><strong>Why balance beats restriction</strong></h3>\n<p style=\"text-align: justify;\">Carbohydrates, protein, and fat all play a role. Cutting out an entire group often backfires. Instead, emphasize vegetables and fruit, choose whole grains when you can, and pick proteins that you enjoy and can sustain.</p>\n\n<h3 style=\"text-align: justify;\"><strong>Practical plate guide</strong></h3>\n<ul style=\"text-align: justify;\">\n<li><strong>Half the plate:</strong> vegetables and fruit for fiber and micronutrients.</li>\n<li><strong>One quarter:</strong> lean protein such as fish, beans, tofu, poultry, or yogurt.</li>\n<li><strong>One quarter:</strong> whole grains or other starchy foods (brown rice, oats, potatoes).</li>\n</ul>\n\n<h3 style=\"text-align: justify;\"><strong>Hydration and eating rhythm</strong></h3>\n<p style=\"text-align: justify;\">Drink water regularly; mild dehydration can feel like fatigue. Regular meals help steady blood sugar for many people. If you snack, pair carbohydrates with protein or fat to stay satisfied longer.</p>\n\n<h3 style=\"text-align: justify;\"><strong>When to get personal guidance</strong></h3>\n<p style=\"text-align: justify;\">Conditions such as diabetes, kidney disease, or food allergies require tailored plans. Use general guidance as a starting point, and work with a qualified clinician or dietitian for medical nutrition therapy.</p>', 2, '/img/articles/diet-cells.jpg', NULL, 1, '2024-07-16 16:14:27');


INSERT INTO `news` (`id`, `name`, `content`, `tag_id`, `cover`, `reader_ids`, `is_top`, `create_time`) VALUES
(13, 'Is 10,000 Steps a Day Right for You?',
'<p style=\"text-align: justify;\">Ten thousand steps became a popular goal because it is easy to remember—not because it is a medical rule for everyone. Movement matters, but the best target depends on your age, fitness, and any health conditions.</p>
<h3 style=\"text-align: justify;\"><strong>What research suggests</strong></h3>
<p style=\"text-align: justify;\">Studies link more daily steps with lower risk of heart disease and better mood. Benefits often appear well below 10,000 steps for people who were previously sedentary. Adding 2,000–3,000 steps per day can be a realistic first goal.</p>
<h3 style=\"text-align: justify;\"><strong>Make it sustainable</strong></h3>
<ul style=\"text-align: justify;\">
<li>Break walks into 10-minute blocks after meals.</li>
<li>Use stairs, park farther away, or walk meetings when possible.</li>
<li>Track trends weekly rather than chasing a perfect daily number.</li>
</ul>
<p style=\"text-align: justify;\">If you have joint pain, heart symptoms, or balance concerns, ask a clinician before sharply increasing activity.</p>',
3, 'https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-03-12 09:15:00'),

(14, 'A Simple Weekly Plan to Manage Stress',
'<p style=\"text-align: justify;\">Stress is not only in your head—it affects sleep, appetite, blood pressure, and focus. You cannot remove every stressor, but you can build habits that help your nervous system recover.</p>
<h3 style=\"text-align: justify;\"><strong>Three anchors that help most people</strong></h3>
<ul style=\"text-align: justify;\">
<li><strong>Sleep window:</strong> keep a consistent wake time, even on weekends.</li>
<li><strong>Movement:</strong> 20–30 minutes of walking most days.</li>
<li><strong>Connection:</strong> one meaningful conversation per week counts.</li>
</ul>
<h3 style=\"text-align: justify;\"><strong>When to seek support</strong></h3>
<p style=\"text-align: justify;\">Talk to a mental health professional if anxiety or low mood lasts more than two weeks, interferes with work or relationships, or includes thoughts of self-harm. Crisis lines are available 24/7 in many regions.</p>',
1, 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 1, '2025-04-02 14:20:00'),

(15, 'Heart-Healthy Habits You Can Start This Week',
'<p style=\"text-align: justify;\">Cardiovascular health is shaped by daily choices: what you eat, how you move, whether you smoke, and how you manage stress. Small changes compound over years.</p>
<h3 style=\"text-align: justify;\"><strong>Evidence-based starting points</strong></h3>
<ul style=\"text-align: justify;\">
<li>Choose unsaturated fats (olive oil, nuts, fish) over trans fats when possible.</li>
<li>Limit added salt in packaged foods and restaurant meals.</li>
<li>Aim for 150 minutes of moderate activity per week.</li>
<li>Know your blood pressure and discuss results with a clinician.</li>
</ul>
<p style=\"text-align: justify;\">Chest pain, sudden shortness of breath, or pain radiating to the arm or jaw need urgent medical evaluation—do not wait.</p>',
5, '/img/articles/heart-healthy.jpg', NULL, 1, '2025-04-18 11:00:00'),

(16, 'Screen Time at Night: How It Affects Sleep',
'<p style=\"text-align: justify;\">Phones and laptops before bed can delay sleep by suppressing melatonin and keeping your brain in alert mode. The issue is not only screen brightness but also stimulating content.</p>
<h3 style=\"text-align: justify;\"><strong>Practical evening routine</strong></h3>
<ul style=\"text-align: justify;\">
<li>Stop intense work or social scrolling 60 minutes before bed.</li>
<li>Use night mode and lower brightness if you must use a device.</li>
<li>Keep the bedroom cool, dark, and quiet.</li>
<li>Wake at the same time daily to stabilize your body clock.</li>
</ul>
<p style=\"text-align: justify;\">Persistent insomnia for more than a month may benefit from cognitive behavioral therapy for insomnia (CBT-I), which is often more effective long term than medication alone.</p>',
2, '/img/articles/screen-sleep.jpg', NULL, 0, '2025-05-01 20:45:00'),

(17, 'Strength Training for Beginners: Where to Start',
'<p style=\"text-align: justify;\">Strength training protects muscle and bone as you age, supports metabolism, and can improve balance. You do not need a gym membership to begin.</p>
<h3 style=\"text-align: justify;\"><strong>A simple twice-weekly template</strong></h3>
<ul style=\"text-align: justify;\">
<li>Squats or sit-to-stand from a chair: 2 sets of 8–12 reps.</li>
<li>Wall push-ups or incline push-ups: 2 sets of 8–12 reps.</li>
<li>Row with resistance band or backpack: 2 sets of 8–12 reps.</li>
<li>Plank or dead bug core exercise: 2 sets of 20–30 seconds.</li>
</ul>
<p style=\"text-align: justify;\">Rest at least one day between sessions. Stop if you feel sharp pain, dizziness, or chest discomfort.</p>',
3, 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-05-20 08:30:00'),

(18, 'Hydration: How Much Water Do You Really Need?',
'<p style=\"text-align: justify;\">Hydration needs vary with climate, activity, body size, and health conditions. Thirst is a useful guide for most healthy adults.</p>
<h3 style=\"text-align: justify;\"><strong>Helpful habits</strong></h3>
<ul style=\"text-align: justify;\">
<li>Drink water with meals and during exercise.</li>
<li>Check urine color: pale yellow usually means adequate hydration.</li>
<li>Increase fluids in heat, fever, or diarrhea—oral rehydration solutions may help.</li>
</ul>
<p style=\"text-align: justify;\">People with heart failure or kidney disease should follow fluid limits set by their care team. Drinking excessive water without medical reason is not healthier.</p>',
2, '/img/articles/hydration.jpg', NULL, 0, '2025-06-08 16:10:00'),

(19, 'Five-Minute Breathing Breaks That Calm the Nervous System',
'<p style=\"text-align: justify;\">Slow breathing can lower heart rate and reduce perceived stress within minutes. It is a tool you can use before meetings, exams, or difficult conversations.</p>
<h3 style=\"text-align: justify;\"><strong>Try box breathing</strong></h3>
<p style=\"text-align: justify;\">Inhale through the nose for 4 seconds, hold 4 seconds, exhale 4 seconds, hold 4 seconds. Repeat for 4–6 cycles. Sit upright, shoulders relaxed, eyes soft or closed.</p>
<p style=\"text-align: justify;\">Breathing exercises complement—but do not replace—therapy or medical care for panic disorder, PTSD, or severe anxiety.</p>',
1, 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-06-22 07:50:00'),

(20, 'Seasonal Allergies and Staying Active Outdoors',
'<p style=\"text-align: justify;\">Pollen seasons can make outdoor exercise uncomfortable. Planning around triggers helps you keep moving without unnecessary suffering.</p>
<h3 style=\"text-align: justify;\"><strong>Tips that often help</strong></h3>
<ul style=\"text-align: justify;\">
<li>Check local pollen forecasts and exercise after rain when counts drop.</li>
<li>Shower and change clothes after outdoor activity.</li>
<li>Use saline nasal rinse as directed by a pharmacist or clinician.</li>
<li>Consider indoor alternatives on high-pollen days.</li>
</ul>
<p style=\"text-align: justify;\">See a clinician if symptoms include wheezing, facial pain, or fever—they may indicate asthma or infection rather than simple allergies.</p>',
6, '/img/articles/allergies.jpg', NULL, 0, '2025-07-05 13:25:00'),

(21, 'Desk Stretches for People Who Sit All Day',
'<p style=\"text-align: justify;\">Long sitting tightens hips, rounds the shoulders, and can contribute to neck and lower-back discomfort. Short movement breaks reduce stiffness more than one long stretch at night.</p>
<h3 style=\"text-align: justify;\"><strong>Every hour, try</strong></h3>
<ul style=\"text-align: justify;\">
<li>Stand and walk for 2–3 minutes.</li>
<li>Neck side bends and shoulder rolls: 30 seconds each.</li>
<li>Hip flexor stretch in a lunge position: 20 seconds per side.</li>
<li>Wrist circles if you type frequently.</li>
</ul>
<p style=\"text-align: justify;\">Ergonomic setup matters: screen at eye level, feet flat, elbows near 90 degrees.</p>',
7, 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-08-14 10:40:00'),

(22, 'Why Social Connection Matters for Health',
'<p style=\"text-align: justify;\">Loneliness and weak social ties are linked to higher risks of depression, heart disease, and early mortality. Connection is a health behavior, not a luxury.</p>
<h3 style=\"text-align: justify;\"><strong>Small steps that count</strong></h3>
<ul style=\"text-align: justify;\">
<li>Schedule a regular call or walk with one friend.</li>
<li>Join a class, volunteer group, or community activity aligned with your interests.</li>
<li>Be specific when asking for help—it makes it easier for others to respond.</li>
</ul>
<p style=\"text-align: justify;\">If social anxiety or depression makes connection hard, professional support can help you rebuild skills at a manageable pace.</p>',
8, 'https://images.unsplash.com/photo-1529156069898-49953e39b3ac?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-09-01 18:00:00'),

(23, 'Understanding Blood Sugar Spikes After Meals',
'<p style=\"text-align: justify;\">After eating, blood glucose rises and insulin helps cells use that energy. Large spikes followed by crashes can leave you tired and hungry again soon.</p>
<h3 style=\"text-align: justify;\"><strong>Steadier glucose strategies</strong></h3>
<ul style=\"text-align: justify;\">
<li>Pair carbohydrates with protein, fiber, or healthy fat.</li>
<li>Choose whole grains over refined flour when practical.</li>
<li>Take a short walk after meals when you can.</li>
</ul>
<p style=\"text-align: justify;\">People with diabetes should follow individualized plans from their care team rather than general articles alone.</p>',
5, 'https://images.unsplash.com/photo-1576086213369-97a306d36557?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-10-10 12:15:00'),

(24, 'Morning Sunlight and Your Body Clock',
'<p style=\"text-align: justify;\">Morning light helps set circadian rhythm, which influences sleep timing, alertness, and hormone patterns. Even cloudy outdoor light is often brighter than indoor lighting.</p>
<h3 style=\"text-align: justify;\"><strong>Easy routine</strong></h3>
<p style=\"text-align: justify;\">Within an hour of waking, spend 10–20 minutes outside or by a bright window. Combine with a short walk if possible. In winter or shift work, discuss light therapy devices with a clinician.</p>',
2, 'https://images.unsplash.com/photo-1470252649378-9c29740c9fa8?auto=format&fit=crop&w=1600&h=720&q=80', NULL, 0, '2025-11-02 07:30:00');

COMMIT;

-- ----------------------------
-- Table structure for news_save
-- ----------------------------
DROP TABLE IF EXISTS `news_save`;
CREATE TABLE `news_save` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `news_id` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of news_save
-- ----------------------------
BEGIN;
INSERT INTO `news_save` (`id`, `user_id`, `news_id`, `create_time`) VALUES (40, 8, 3, '2024-10-05 14:46:48');
INSERT INTO `news_save` (`id`, `user_id`, `news_id`, `create_time`) VALUES (45, 3, 8, '2024-11-18 18:32:57');
INSERT INTO `news_save` (`id`, `user_id`, `news_id`, `create_time`) VALUES (47, 3, 2, '2024-11-18 20:29:04');
COMMIT;

-- ----------------------------
-- Table structure for tags
-- ----------------------------
DROP TABLE IF EXISTS `tags`;
CREATE TABLE `tags` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of tags
-- ----------------------------
BEGIN;
INSERT INTO `tags` (`id`, `name`) VALUES (1, 'Mental Health');
INSERT INTO `tags` (`id`, `name`) VALUES (2, 'Wellness');
INSERT INTO `tags` (`id`, `name`) VALUES (3, 'Exercise');
INSERT INTO `tags` (`id`, `name`) VALUES (4, 'Traditional Medicine');
INSERT INTO `tags` (`id`, `name`) VALUES (5, 'Physical Health');
INSERT INTO `tags` (`id`, `name`) VALUES (6, 'Environmental Health');
INSERT INTO `tags` (`id`, `name`) VALUES (7, 'Occupational Health');
INSERT INTO `tags` (`id`, `name`) VALUES (8, 'Psychological Health');
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_pwd` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `user_role` int DEFAULT NULL,
  `is_login` tinyint(1) DEFAULT NULL,
  `is_word` tinyint(1) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (1, 'admin', 'Carol', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=256&q=80&facepad=2', '1343243@qq.com', 1, 0, 0, '2024-07-09 12:53:05');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (3, 'sky', 'Sky', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=256&q=80&facepad=2', '1134123@qq.com', 2, 0, 0, '2024-10-07 23:59:31');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (8, 'linda', 'Linda', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=256&q=80&facepad=2', '123@qq.coom', 2, 0, 1, '2024-10-07 23:59:31');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (9, 'coco', 'Coco', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=256&q=80&facepad=2', '1243@qq.com', 1, 1, 0, '2024-10-07 23:59:31');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (10, 'doris', 'Doris', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=256&q=80&facepad=2', '156456@qq.com', 2, 0, 0, '2024-10-07 23:59:31');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (11, 'dave', 'Dave', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=256&q=80&facepad=2', '789789@qq.com', 1, 1, 0, '2024-11-18 13:00:14');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (12, 'simon', 'Simon', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=256&q=80&facepad=2', '43443@qq.com', 2, 0, 1, '2024-11-18 13:00:14');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (13, 'johnny', 'Johnny', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&w=256&q=80&facepad=2', '1567766@qq.com', 2, 0, 0, '2024-10-25 16:16:40');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (14, 'justin', 'Justin', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=256&q=80&facepad=2', '32323@qq.com', 1, 0, 0, '2024-11-18 13:00:14');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (15, 'lilly', 'Lilly', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=256&q=80&facepad=2', '2121@qq.com', 2, 0, 0, '2024-11-18 13:00:30');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (16, 'helen', 'Helen', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=256&q=80&facepad=2', '32312@qq.com', 1, 0, 0, '2024-11-18 13:00:50');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (17, 'amy', 'Amy', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=256&q=80&facepad=2', '1212@qq.com', 2, 0, 0, '2024-11-18 13:01:18');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (18, 'lisa', 'Lisa', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=256&q=80&facepad=2', '432432@qq.com', 2, 0, 0, '2024-11-18 13:01:34');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (19, 'zoe', 'Zoe', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=256&q=80&facepad=2', '1232@qq.com', 2, 0, 0, '2024-11-18 14:15:19');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (20, 'angela', 'Angela', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1580489944761-15a19d654956?auto=format&fit=crop&w=256&q=80&facepad=2', '124354@qq.com', 2, 0, 0, '2024-11-18 14:16:24');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (21, 'amy', 'Amy', '14e1b600b1fd579f47433b88e8d85291', 'https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&w=256&q=80&facepad=2', '12432@qq.com', 2, 0, 0, '2024-11-18 14:17:35');
INSERT INTO `user` (`id`, `user_account`, `user_name`, `user_pwd`, `user_avatar`, `user_email`, `user_role`, `is_login`, `is_word`, `create_time`) VALUES (22, 'cici', 'cici', 'cc9916041edf5fbaf9302400f00aefc4', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=256&q=80&facepad=2', NULL, 2, 0, 0, '2025-01-25 12:29:44');
COMMIT;

-- ----------------------------
-- Table structure for user_health
-- ----------------------------
DROP TABLE IF EXISTS `user_health`;
CREATE TABLE `user_health` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `health_model_config_id` int DEFAULT NULL,
  `value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of user_health
-- ----------------------------
BEGIN;
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (1, 3, 1, '23', '2024-07-13 13:13:42');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (2, 3, 2, '200', '2024-07-13 13:13:56');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (3, 3, 1, '43', '2024-07-03 13:14:05');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (5, 3, 2, '110', '2024-07-15 14:15:36');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (6, 3, 1, '167', '2024-07-15 14:15:36');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (8, 3, 4, '99', '2024-07-15 14:15:36');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (11, 3, 7, '76', '2024-07-15 14:43:32');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (12, 3, 4, '110', '2024-07-15 14:43:32');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (13, 3, 2, '110', '2024-07-15 14:43:32');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (14, 3, 1, '170', '2024-07-15 14:43:32');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (17, 3, 8, '71', '2024-07-16 16:15:39');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (18, 3, 7, '70', '2024-07-16 16:15:39');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (19, 3, 8, '188', '2024-07-16 16:16:07');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (20, 3, 7, '188', '2024-07-16 16:16:07');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (21, 3, 8, '66', '2024-07-16 16:16:19');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (22, 3, 7, '66', '2024-07-16 16:16:19');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (23, 3, 8, '73', '2024-07-16 16:16:45');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (24, 3, 7, '34', '2024-07-16 16:16:45');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (25, 3, 8, '201', '2024-07-16 16:17:04');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (26, 3, 7, '111', '2024-07-16 16:17:04');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (27, 3, 8, '88', '2024-07-16 16:17:32');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (28, 3, 7, '88', '2024-07-16 16:17:32');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (30, 3, 7, '70', '2024-07-16 16:30:34');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (31, 3, 8, '32', '2024-10-04 16:42:38');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (32, 3, 7, '32', '2024-10-04 16:42:38');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (33, 3, 8, '132', '2024-10-05 15:19:24');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (34, 3, 7, '111', '2024-10-05 15:19:24');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (35, 3, 9, '309', '2024-11-18 17:16:01');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (36, 3, 10, '4000', '2024-11-10 17:16:01');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (37, 3, 4, '120', '2024-11-18 17:16:01');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (38, 3, 7, '88', '2024-11-18 17:16:01');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (39, 3, 1, '166.3', '2024-11-18 17:16:01');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (40, 3, 11, '66', '2024-11-18 17:17:45');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (41, 3, 10, '4000', '2024-11-16 17:17:45');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (42, 3, 12, '100', '2024-11-18 17:18:45');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (43, 3, 11, '40', '2024-11-18 17:18:45');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (44, 3, 10, '8000', '2024-11-18 17:27:18');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (45, 3, 12, '99', '2024-11-12 17:28:34');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (46, 3, 12, '99', '2024-11-14 17:28:51');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (47, 3, 12, '69', '2024-11-17 17:29:04');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (48, 3, 12, '74', '2024-11-10 17:29:51');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (49, 3, 12, '52', '2024-11-07 17:30:31');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (50, 3, 12, '44', '2024-11-06 17:30:31');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (51, 3, 12, '66', '2024-11-04 17:30:31');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (52, 3, 12, '89', '2024-11-02 17:30:31');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (53, 3, 12, '55', '2024-10-21 17:30:31');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (54, 3, 8, '160', '2024-11-18 20:33:02');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (55, 17, 7, '60', '2024-11-18 20:33:02');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (56, 17, 10, '5000', '2024-11-18 20:33:02');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (57, 17, 11, '5', '2024-11-18 20:33:02');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (58, 17, 12, '45', '2024-11-18 20:34:01');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (60, 17, 12, '80', '2024-11-18 20:58:37');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (61, 17, 10, '5000', '2024-12-15 19:23:34');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (62, 17, 9, '500', '2024-12-15 19:23:34');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (63, 17, 8, '123', '2024-12-15 19:23:34');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (64, 17, 10, '10000', '2024-12-15 19:23:56');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (65, 17, 10, '4554', '2024-12-15 19:39:53');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (66, 17, 9, '4354', '2024-12-15 19:39:53');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (67, 17, 8, '132', '2024-12-15 19:39:53');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (68, 17, 12, '102', '2024-12-15 19:43:41');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (69, 17, 11, '90', '2024-12-15 19:43:41');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (70, 17, 12, '99', '2024-12-15 19:44:01');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (71, 17, 10, '4000', '2024-12-15 19:44:01');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (72, 17, 9, '500', '2024-12-15 19:44:01');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (73, 17, 8, '90', '2024-12-15 19:44:01');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (74, 17, 10, '5000', '2025-01-05 16:46:09');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (75, 17, 9, '500', '2025-01-05 16:46:09');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (76, 17, 8, '80', '2025-01-05 16:46:09');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (77, 17, 10, '7000', '2025-01-05 16:46:27');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (78, 17, 9, '500', '2025-01-05 16:46:27');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (79, 17, 8, '90', '2025-01-05 16:46:27');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (80, 17, 10, '13000', '2025-01-06 17:49:08');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (81, 17, 9, '500', '2025-01-06 17:49:08');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (82, 17, 10, '8000', '2025-01-06 17:53:05');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (83, 3, 8, '75', '2025-01-06 17:53:05');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (84, 3, 10, '7000', '2025-01-06 17:53:15');
INSERT INTO `user_health` (`id`, `user_id`, `health_model_config_id`, `value`, `create_time`) VALUES (85, 3, 8, '80', '2025-01-06 17:53:15');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
