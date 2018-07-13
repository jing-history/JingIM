/*
SQLyog Trial v12.2.1 (64 bit)
MySQL - 5.7.17-log : Database - websocket
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`websocket` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `websocket`;

/*Table structure for table `t_add_message` */

DROP TABLE IF EXISTS `t_add_message`;

CREATE TABLE `t_add_message` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `from_uid` int(10) NOT NULL COMMENT '谁发起的请求',
  `to_uid` int(10) NOT NULL COMMENT '发送给谁的申请,可能是群，那么就是创建该群组的用户',
  `group_id` int(10) NOT NULL COMMENT '如果是添加好友则为from_id的分组id，如果为群组则为群组id',
  `remark` varchar(255) DEFAULT NULL COMMENT '附言',
  `agree` tinyint(2) NOT NULL DEFAULT '0' COMMENT '0未处理，1同意，2拒绝',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '类型，可能是添加好友或群组',
  `time` datetime NOT NULL COMMENT '申请时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `add_friend_unique` (`from_uid`,`to_uid`,`type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_add_message` */

insert  into `t_add_message`(`id`,`from_uid`,`to_uid`,`group_id`,`remark`,`agree`,`type`,`time`) values 
(1,106,19,1,'',1,0,'2017-04-16 12:00:14'),
(2,106,21,3,'',0,0,'2017-04-16 12:00:48'),
(3,106,18,3,'我是王培坤',0,0,'2017-04-16 12:01:01'),
(4,106,1,1,'',0,0,'2017-04-17 09:04:16'),
(13,106,8,1,'1fdfd',0,0,'2017-04-16 15:27:06'),
(14,1,102,6,'我是123456@qq.com ',0,0,'2017-04-16 15:38:18'),
(15,102,100,7,'我是silence',0,0,'2017-04-16 16:22:56'),
(16,45,106,1,'hello you are',2,0,'2017-04-20 16:58:31'),
(17,46,106,2,'can you ',1,0,'2017-04-07 16:58:52'),
(18,56,106,2,'fdfddfdfd',0,0,'2017-04-14 16:59:10'),
(19,57,106,2,'fdfdfdfdf',1,0,'2017-04-20 16:59:26'),
(20,69,106,3,'我是谁',2,0,'2017-04-12 16:59:47'),
(21,95,106,8,'我是id95 思月',1,0,'2017-04-16 19:49:28'),
(22,35,19,10,'我是id 35',1,0,'2017-04-16 21:51:44'),
(23,35,1,10,'我是谁',0,0,'2017-04-16 21:54:02'),
(24,106,53,3,'我是王培坤',1,0,'2017-04-16 22:18:06'),
(25,110,111,13,'',0,0,'2018-07-11 15:34:42'),
(26,111,110,14,'',1,0,'2018-07-12 11:43:17'),
(30,112,110,15,'sss',2,0,'2018-07-12 14:33:14'),
(31,110,106,1,'123',0,1,'2018-07-12 14:10:48'),
(32,112,110,5,'aaaaa',1,1,'2018-07-12 15:26:58'),
(35,111,110,5,'bbbbb',1,1,'2018-07-12 15:20:17'),
(36,110,110,5,'sss',1,1,'2018-07-12 15:25:36'),
(38,110,112,13,'xxxx',1,0,'2018-07-12 15:28:40'),
(39,115,110,18,'',0,0,'2018-07-12 18:17:13');

/*Table structure for table `t_friend_group` */

DROP TABLE IF EXISTS `t_friend_group`;

CREATE TABLE `t_friend_group` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `uid` int(10) NOT NULL COMMENT '该分组所属的用户ID',
  `group_name` varchar(64) NOT NULL COMMENT '分组名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_friend_group` */

insert  into `t_friend_group`(`id`,`uid`,`group_name`) values 
(1,106,'前端小组'),
(2,106,'大数据小组'),
(3,106,'策划小组'),
(4,106,'产品小组'),
(5,100,'调研小组'),
(6,1,'UI小组'),
(7,102,'我的好友'),
(8,95,'我的好友'),
(9,19,'我的好友'),
(10,35,'我的好友'),
(11,53,'我的好友'),
(12,57,'我的好友'),
(13,110,'我的好友'),
(14,111,'我的好友'),
(15,112,'我的好友'),
(17,114,'我的好友'),
(18,115,'我的好友');

/*Table structure for table `t_friend_group_friends` */

DROP TABLE IF EXISTS `t_friend_group_friends`;

CREATE TABLE `t_friend_group_friends` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `fgid` int(10) NOT NULL COMMENT '分组id',
  `uid` int(10) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `g_uid_unique` (`fgid`,`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_friend_group_friends` */

insert  into `t_friend_group_friends`(`id`,`fgid`,`uid`) values 
(75,13,111),
(80,13,112),
(76,14,110),
(79,15,110);

/*Table structure for table `t_group` */

DROP TABLE IF EXISTS `t_group`;

CREATE TABLE `t_group` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(64) NOT NULL COMMENT '群组名称',
  `avatar` varchar(255) NOT NULL DEFAULT '' COMMENT '群组图标',
  `create_id` int(20) NOT NULL COMMENT '创建者id',
  `create_time` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_group` */

insert  into `t_group`(`id`,`group_name`,`avatar`,`create_id`,`create_time`) values 
(1,'Java群','/static/image/group/group_1.gif',106,'2017-04-10 20:39:11'),
(2,'Scala群','/static/image/group/group_2.gif',106,'2017-04-10 20:39:22'),
(3,'SpringBoot群','/static/image/group/group_3.jpg',106,'2017-04-10 20:40:44'),
(4,'Redis群','/static/image/group/group_4.jpg',1,'2017-04-10 20:40:47'),
(5,'SpringIo','/static/image/group/group_3.jpg',110,'2018-07-12 14:17:54');

/*Table structure for table `t_group_members` */

DROP TABLE IF EXISTS `t_group_members`;

CREATE TABLE `t_group_members` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `gid` int(20) NOT NULL COMMENT '群组ID',
  `uid` int(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_group_members` */

insert  into `t_group_members`(`id`,`gid`,`uid`) values 
(20,5,111),
(21,5,110),
(22,5,112);

/*Table structure for table `t_message` */

DROP TABLE IF EXISTS `t_message`;

CREATE TABLE `t_message` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `toid` int(10) NOT NULL COMMENT '发送给哪个用户或者组id',
  `mid` int(10) NOT NULL COMMENT '消息的来源ID（如果是私聊，则是用户id，如果是群聊，则是群组id）',
  `fromid` int(10) NOT NULL COMMENT '消息的发送者id（比如群组中的某个消息发送者）',
  `content` varchar(255) NOT NULL COMMENT '消息内容',
  `type` varchar(10) NOT NULL DEFAULT '' COMMENT '聊天窗口来源类型',
  `timestamp` bigint(25) NOT NULL COMMENT '服务器动态时间',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否已读',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=458 DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_message` */

insert  into `t_message`(`id`,`toid`,`mid`,`fromid`,`content`,`type`,`timestamp`,`status`) values 
(278,100,106,106,'can you see me','friend',1492044311037,1),
(279,106,100,100,'yes i can','friend',1492044336744,1),
(284,100,106,106,'face[鼓掌] ','friend',1492044394069,1),
(285,100,106,106,'img[/upload/image/2017-04-13/078f382fbb4d440d8fbf48af6c06f6a9.gif]','friend',1492044396894,1),
(286,100,106,106,'file(/upload/file/2017-04-13/82b68e3fb3e84bb0b8907c4b3577debd/vaish.pdf)[vaish.pdf]','friend',1492044402645,1),
(287,100,106,106,'fdf','friend',1492045672858,1),
(288,100,106,106,'fdfdfd','friend',1492045675434,1),
(289,100,106,106,'img[/upload/image/2017-04-13/938f4da10d394ac88092a162b74764b7.gif]','friend',1492045686012,1),
(290,100,106,106,'img[/upload/image/2017-04-13/44585b3a65284c519dd378e8b156f77a.gif]','friend',1492045693439,1),
(291,100,106,106,'face[晕] face[晕] ','friend',1492045697463,1),
(292,100,106,106,'有','friend',1492059765813,1),
(293,100,106,106,'you are here','friend',1492059770843,1),
(294,106,100,100,'youy  fdfdfd','friend',1492059891380,1),
(299,100,106,106,'silfdnld','friend',1492060061492,1),
(304,106,100,100,'you are','friend',1492063193913,1),
(305,100,106,106,'img[/upload/image/2017-04-13/aa6172e85c0948b9af449d8ffca49f8a.gif]','friend',1492063200807,1),
(306,106,100,100,'img[/upload/image/2017-04-13/5c8ed451cd934ebb82e7808792f40841.png]','friend',1492063207684,1),
(307,100,106,106,'haha ','friend',1492063218341,1),
(308,100,106,106,'can you see','friend',1492063277745,1),
(309,1,106,106,'ni shi sha bi a ','friend',1492063320353,0),
(314,106,100,100,'hell you are','friend',1492065740132,0),
(315,100,106,106,'hahahahaahaha','friend',1492065768066,1),
(316,100,106,106,'you care','friend',1492066916971,1),
(317,100,106,106,'can you see','friend',1492066920771,1),
(318,106,100,100,'fgf','friend',1492066970584,1),
(319,106,100,100,'you fd','friend',1492068412715,1),
(320,100,106,106,'you can','friend',1492086999497,1),
(321,106,100,100,'so you can','friend',1492087007954,1),
(334,106,100,100,'can you','friend',1492091732776,1),
(335,100,106,106,'yes you can','friend',1492091740621,1),
(340,106,100,100,'12313','friend',1492130436919,0),
(397,1,1,100,'you are heere','group',1492134078521,0),
(398,100,106,106,'fdfd','friend',1492134112114,1),
(399,1,1,106,'you are here too','group',1492134126326,1),
(400,1,1,106,'ni shi shabi a ','group',1492138398473,1),
(401,1,1,100,'ni caishi shabi','group',1492138410246,1),
(402,1,1,106,'img[/upload/image/2017-04-14/3489a8d6f13d4f1386f40c7e4f1b1331.gif]','group',1492138415268,1),
(403,1,1,106,'face[拜拜] ','group',1492138419344,1),
(404,1,1,106,' can you see me','group',1492138432879,1),
(405,1,1,1,'you','group',1492138737038,1),
(406,1,1,1,' hahah a','group',1492138740731,1),
(407,1,1,1,'img[/upload/image/2017-04-14/2bbfdee13a454d8cb452eb8be10cec02.jpg]','group',1492138746490,1),
(408,106,100,100,'face[晕] ','friend',1492138938078,0),
(409,106,100,100,'you are here','friend',1492151190441,1),
(410,100,106,106,'so you can see me?','friend',1492151198992,1),
(411,106,100,100,'of course','friend',1492151209763,1),
(412,100,106,106,'img[/upload/image/2017-04-14/b273ba2372c54ed5b8294948daea9aad.gif]','friend',1492151213114,1),
(413,100,106,106,'file(/upload/file/2017-04-14/a505714207a24208ba93609115d0d86b/railstutorial4th-sample.pdf)[railstutorial4th-sample.pdf]','friend',1492151222502,1),
(414,106,100,100,'fdfd','friend',1492151279802,1),
(415,100,106,106,'can','friend',1492156031241,1),
(416,106,100,100,'yes i can','friend',1492156049674,1),
(417,100,106,106,'sorry you can?','friend',1492167152813,0),
(418,100,106,106,'you can see  me?','friend',1492221564710,0),
(419,1,1,106,'can','group',1492233567749,0),
(420,106,100,100,'123','friend',1492233588308,1),
(421,100,106,106,'can you','friend',1492244367611,1),
(422,100,106,106,'ca','friend',1492244458305,1),
(423,1,1,106,'you see me?','group',1492244469314,1),
(424,100,106,106,'fdf','friend',1492244570981,1),
(425,1,1,106,'3123','group',1492244582373,1),
(426,1,1,100,'you aree here','group',1492244589829,1),
(427,1,1,106,'so you','group',1492244593489,1),
(428,100,106,106,'fd','friend',1492244596254,1),
(429,100,106,106,'img[/upload/image/2017-04-15/83b8d86b1105487295b50f70210357b5.gif]','friend',1492244600136,1),
(430,106,100,100,'fdfd','friend',1492322377739,1),
(431,106,100,100,'fdf','friend',1492322384545,1),
(432,106,100,100,'img[/upload/image/2017-04-16/9b0615d4aa834988bf06398495930cbe.gif]','friend',1492322389875,1),
(433,106,100,100,'img[/upload/image/2017-04-16/ea9f884c214c45dd9025b1d872ff8baa.gif]','friend',1492322402801,1),
(434,106,100,100,'img[/upload/image/2017-04-16/8b8ba9e15d24492b8b3d344e47c3a90e.jpg]','friend',1492322415470,1),
(435,1,1,100,'fddf','group',1492322486128,1),
(436,106,100,100,'fdfd','friend',1492390696658,1),
(437,100,106,106,'fdfdfd','friend',1492390732772,1),
(438,106,100,100,'img[/upload/image/2017-04-17/ac6a6ccb25a445e29908c256830f19d1.png]','friend',1492390738798,1),
(439,106,100,100,'file(/upload/file/2017-04-17/0a7e89e4c39045428cff192b13f3c7bc/vaish.pdf)[vaish.pdf]','friend',1492390750063,1),
(440,1,1,100,'fdfd','group',1492390819118,1),
(441,1,1,106,'img[/upload/image/2017-04-17/405636c995db4f6982f374bdf59d2418.gif]','group',1492390865176,1),
(442,1,1,106,'file(/upload/file/2017-04-17/88e9c655485c4db9a5a925a1a6f7cc2c/vaish.pdf)[vaish.pdf]','group',1492390869928,1),
(443,1,1,106,'fdfd','group',1492390925888,1),
(444,111,110,110,'你好','friend',1531359812193,1),
(445,110,111,111,'哈哈','friend',1531359837784,1),
(446,111,110,110,'face[鼓掌] ','friend',1531359920151,1),
(447,110,111,111,'你好无聊','friend',1531359945311,1),
(448,111,110,110,'分布式消息推送服务，可以用于客服、推送、聊天等诸多系统的 核心组件服务!','friend',1531359968675,1),
(449,110,111,111,'face[鼓掌] ','friend',1531368352864,1),
(450,5,5,111,'asasd','group',1531380250408,0),
(451,5,5,111,'asdasd','group',1531380370615,1),
(452,5,5,112,'face[哈哈] ','group',1531380438989,1),
(453,5,5,110,'face[鼓掌] face[鼓掌] face[鼓掌] face[鼓掌] face[鼓掌] ','group',1531380475191,1),
(454,5,5,111,'马蛋','group',1531380501312,1),
(455,5,5,110,'aaaaa','group',1531380634746,1),
(456,111,110,110,'img[/upload/image/2018-07-12/8359732d408e4a2eb26d655c719f767c.bmp]','friend',1531385411051,1),
(457,110,111,111,'file(/upload/file/2018-07-12/5dd80009dc124f8fbc8dacb723e63e6a/application.yml)[application.yml]','friend',1531385481987,1);

/*Table structure for table `t_user` */

DROP TABLE IF EXISTS `t_user`;

CREATE TABLE `t_user` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `sign` varchar(255) DEFAULT NULL COMMENT '签名',
  `email` varchar(64) NOT NULL COMMENT '邮箱地址',
  `avatar` varchar(255) DEFAULT '/static/image/avatar/avatar(3).jpg' COMMENT '头像地址',
  `sex` int(2) NOT NULL DEFAULT '1' COMMENT '性别',
  `active` varchar(64) NOT NULL COMMENT '激活码',
  `status` varchar(16) NOT NULL DEFAULT 'nonactivated' COMMENT '是否激活',
  `create_date` date NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8mb4;

/*Data for the table `t_user` */

insert  into `t_user`(`id`,`username`,`password`,`sign`,`email`,`avatar`,`sex`,`active`,`status`,`create_date`) values 
(110,'jinggo','096cd996870a28332dfa800d362a6cddfec407f2ae4702c2088a02868dd5c80ba7286462ce316967','如果这世界上真有奇迹，那只是努力的另一个名字。','jinggo@sohu.com','/static/image/avatar/avatar(3).jpg',1,'542003d356b74ef088662e028c80ba26ffa8305697e94457bbeed0bafeaef273','hide','2018-07-10'),
(111,'wangyj','c7271ff3ef02d6e9628b03c70c631cedd4aac1c9ec7979768693857ba8a09d3d1e8f28db03f1def8',NULL,'wangyj0898@126.com','/static/image/avatar/avatar(3).jpg',1,'15ac6e4d95504c0d8c16be09f9ec49025f3cc75a22e14f82b6441b13150a86cf','hide','2018-07-11'),
(112,'admin','23a6bd4866a8661d0c1bbad13ada6bd613ee3a8dc70bf49fbc9da82d5b17dea3e515d14ae69a486a',NULL,'admin@admin.com','/static/image/avatar/avatar(3).jpg',1,'f2e9e9ad5f704f81b90554003c80b3b2a30080f2d3944b82b87176e2b4c26dfe','hide','2018-07-12'),
(114,'wangyj','e0ec75b3529a9cea92bd70fccab9b5d2b3aa7efa66c39e454d2ee5c9e3748c9ecda46cc8a23c6891',NULL,'wangyj@soonfor.com','/static/image/avatar/avatar(3).jpg',1,'2b0b74e503094050a84cd4a9d028ee52e6bee7baf3c74e5980799ae01973e90c','nonactivated','2018-07-12'),
(115,'jing','574f3f0c11bff6a6ceab23d0d98223fea77fe286d443c0f76d7cc7131168ebf9c79301ba3309c054',NULL,'176007262@qq.com','/static/image/avatar/avatar(3).jpg',1,'47dfc85862f4472eb55f27f384697131d14345c313fd45d7820bcc6fb7845f15','offline','2018-07-12');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
