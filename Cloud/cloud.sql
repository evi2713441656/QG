/*
SQLyog Enterprise v13.1.1 (64 bit)
MySQL - 8.0.41 : Database - cloud
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`cloud` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `cloud`;

/*Table structure for table `article` */

DROP TABLE IF EXISTS `article`;

CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` longtext NOT NULL COMMENT '内容',
  `knowledge_id` bigint NOT NULL COMMENT '所属知识库ID',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览量',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '评论数',
  `is_pinned` tinyint NOT NULL DEFAULT '0' COMMENT '是否置顶(0-否,1-是)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_knowledge` (`knowledge_id`),
  KEY `idx_author` (`author_id`),
  FULLTEXT KEY `ft_title_content` (`title`,`content`) COMMENT '全文索引'
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章表';

/*Data for the table `article` */

insert  into `article`(`id`,`title`,`content`,`knowledge_id`,`author_id`,`view_count`,`like_count`,`comment_count`,`is_pinned`,`create_time`,`update_time`) values 
(1,'1','schsdbchgffcgvbhnjkkn1',2,1,7,0,2,0,'2025-05-10 13:40:28','2025-05-11 14:03:42'),
(2,'2','2adsdasfdfasdchasbdjadbh',2,1,60,1,3,0,'2025-05-10 14:00:36','2025-05-11 15:51:11');

/*Table structure for table `article_favorite` */

DROP TABLE IF EXISTS `article_favorite`;

CREATE TABLE `article_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`,`user_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='点赞表';

/*Data for the table `article_favorite` */

/*Table structure for table `article_likes` */

DROP TABLE IF EXISTS `article_likes`;

CREATE TABLE `article_likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `article_id` (`article_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `article_likes` */

insert  into `article_likes`(`id`,`article_id`,`user_id`,`create_time`) values 
(21,2,1,'2025-05-11 06:44:14');

/*Table structure for table `browse_history` */

DROP TABLE IF EXISTS `browse_history`;

CREATE TABLE `browse_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `browse_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_article` (`article_id`),
  KEY `idx_browse_time` (`browse_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浏览历史表';

/*Data for the table `browse_history` */

/*Table structure for table `comment` */

DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `content` text NOT NULL COMMENT '内容',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID(为空表示一级评论)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_article` (`article_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_parent` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论表';

/*Data for the table `comment` */

insert  into `comment`(`id`,`content`,`article_id`,`user_id`,`parent_id`,`create_time`) values 
(1,'111',2,1,NULL,'2025-05-10 19:30:01'),
(2,'?',2,1,NULL,'2025-05-10 19:32:20'),
(3,'?ï¼',1,1,NULL,'2025-05-10 19:32:41'),
(4,'ï¼ï¼',1,1,3,'2025-05-10 19:32:48'),
(5,'?!',2,1,NULL,'2025-05-11 04:10:09');

/*Table structure for table `enterprise` */

DROP TABLE IF EXISTS `enterprise`;

CREATE TABLE `enterprise` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '企业ID',
  `name` varchar(100) NOT NULL COMMENT '企业名称',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_creator` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业表';

/*Data for the table `enterprise` */

/*Table structure for table `enterprise_member` */

DROP TABLE IF EXISTS `enterprise_member`;

CREATE TABLE `enterprise_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `enterprise_id` bigint NOT NULL COMMENT '企业ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` tinyint NOT NULL DEFAULT '3' COMMENT '角色(1-所有者,2-管理员,3-成员)',
  `join_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_enterprise_user` (`enterprise_id`,`user_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业成员表';

/*Data for the table `enterprise_member` */

/*Table structure for table `enterprise_notice` */

DROP TABLE IF EXISTS `enterprise_notice`;

CREATE TABLE `enterprise_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `enterprise_id` bigint NOT NULL COMMENT '企业ID',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` text NOT NULL COMMENT '内容',
  `publisher_id` bigint NOT NULL COMMENT '发布者ID',
  `publish_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`),
  KEY `idx_enterprise` (`enterprise_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='企业通知表';

/*Data for the table `enterprise_notice` */

/*Table structure for table `knowledge_base` */

DROP TABLE IF EXISTS `knowledge_base`;

CREATE TABLE `knowledge_base` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '知识库ID',
  `name` varchar(100) NOT NULL COMMENT '知识库名称',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `cover_url` varchar(255) DEFAULT NULL COMMENT '封面URL',
  `is_public` tinyint NOT NULL DEFAULT '0' COMMENT '是否公开(0-私有,1-公开)',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_creator` (`creator_id`),
  KEY `idx_public` (`is_public`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识库表';

/*Data for the table `knowledge_base` */

insert  into `knowledge_base`(`id`,`name`,`description`,`cover_url`,`is_public`,`creator_id`,`create_time`,`update_time`) values 
(2,'1','1','https://cdn.pixabay.com/photo/2015/07/17/22/43/student-849825_960_720.jpg',0,1,'2025-05-10 07:02:38','2025-05-10 12:02:33'),
(3,'2','2','https://cdn.pixabay.com/photo/2015/07/17/22/43/student-849825_960_720.jpg',0,1,'2025-05-10 19:01:58','2025-05-10 19:01:58');

/*Table structure for table `knowledge_member` */

DROP TABLE IF EXISTS `knowledge_member`;

CREATE TABLE `knowledge_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `knowledge_id` bigint NOT NULL COMMENT '知识库ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role` tinyint NOT NULL DEFAULT '2' COMMENT '角色(1-所有者,2-管理员,3-成员)',
  `join_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_knowledge_user` (`knowledge_id`,`user_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识库成员表';

/*Data for the table `knowledge_member` */

insert  into `knowledge_member`(`id`,`knowledge_id`,`user_id`,`role`,`join_time`) values 
(1,1,1,1,'2025-05-10 06:24:17'),
(2,2,1,1,'2025-05-10 07:02:38'),
(3,3,1,1,'2025-05-10 19:01:58');

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码(加密存储)',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态(0-禁用,1-正常)',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`),
  UNIQUE KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

/*Data for the table `user` */

insert  into `user`(`id`,`username`,`password`,`email`,`avatar`,`status`,`last_login_time`,`create_time`,`update_time`) values 
(1,'evievi','$2a$12$ITMr.uOzjKbpEbIIITzgde.WYQyrQ1dX57IYwAvr4EEEejwSEltoW','2713441656@qq.com','D:/code/cat/avatar/36fcc07d-a1c9-4cc6-9107-4f844a6b74f7.JPG',1,'2025-05-11 14:46:27','2025-05-07 14:51:10','2025-05-11 22:08:35'),
(15,'1111','$2a$12$M9tTVKZhzDgy8pK.cVjnQebjQjcpaV0BmfS3I1Ut7iInQDG0a8kKG','2962515141@qq.com','/default-avatar.png',1,'2025-05-11 09:05:18','2025-05-11 09:05:11','2025-05-11 09:05:11');

/*Table structure for table `user_relation` */

DROP TABLE IF EXISTS `user_relation`;

CREATE TABLE `user_relation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `follower_id` bigint NOT NULL COMMENT '关注者ID',
  `following_id` bigint NOT NULL COMMENT '被关注者ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follower_following` (`follower_id`,`following_id`),
  KEY `idx_following` (`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户关系表';

/*Data for the table `user_relation` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
