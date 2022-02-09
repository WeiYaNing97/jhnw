/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50733
 Source Host           : localhost:3306
 Source Schema         : jhtest

 Target Server Type    : MySQL
 Target Server Version : 50733
 File Encoding         : 65001

 Date: 09/02/2022 15:14:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 278 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (100, 'admin', '192.168.0.102', '内网IP', 'Internet Explorer 11', 'Windows 10', '1', '验证码错误', '2021-10-25 11:46:03');
INSERT INTO `sys_logininfor` VALUES (101, 'admin', '192.168.0.102', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-10-25 11:46:07');
INSERT INTO `sys_logininfor` VALUES (102, 'admin', '192.168.0.102', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-10-25 13:42:31');
INSERT INTO `sys_logininfor` VALUES (103, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-10-25 14:01:51');
INSERT INTO `sys_logininfor` VALUES (104, 'admin', '127.0.0.1', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-10-25 14:19:07');
INSERT INTO `sys_logininfor` VALUES (105, 'admin', '192.168.0.102', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-10-25 14:20:10');
INSERT INTO `sys_logininfor` VALUES (106, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-10-25 14:57:11');
INSERT INTO `sys_logininfor` VALUES (107, 'admin', '192.168.0.102', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-10-26 13:54:29');
INSERT INTO `sys_logininfor` VALUES (108, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-10-26 15:37:27');
INSERT INTO `sys_logininfor` VALUES (109, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '退出成功', '2021-10-26 15:38:41');
INSERT INTO `sys_logininfor` VALUES (110, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-10-26 15:39:01');
INSERT INTO `sys_logininfor` VALUES (111, 'admin', '192.168.0.103', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-10-26 15:46:17');
INSERT INTO `sys_logininfor` VALUES (112, 'admin', '192.168.1.98', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-10-26 15:53:55');
INSERT INTO `sys_logininfor` VALUES (113, 'admin', '127.0.0.1', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-10-26 15:58:27');
INSERT INTO `sys_logininfor` VALUES (114, 'admin', '127.0.0.1', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-10-26 16:03:39');
INSERT INTO `sys_logininfor` VALUES (115, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-10-26 16:32:35');
INSERT INTO `sys_logininfor` VALUES (116, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码错误', '2021-10-27 10:11:55');
INSERT INTO `sys_logininfor` VALUES (117, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-10-27 10:12:03');
INSERT INTO `sys_logininfor` VALUES (118, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-10-27 18:39:27');
INSERT INTO `sys_logininfor` VALUES (119, 'admin', '127.0.0.1', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-10-29 15:17:15');
INSERT INTO `sys_logininfor` VALUES (120, 'admin', '192.168.0.102', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-10-29 16:23:30');
INSERT INTO `sys_logininfor` VALUES (121, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-10-29 16:28:41');
INSERT INTO `sys_logininfor` VALUES (122, 'admin', '192.168.0.102', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-10-29 17:14:23');
INSERT INTO `sys_logininfor` VALUES (123, 'admin', '127.0.0.1', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-11-10 11:43:34');
INSERT INTO `sys_logininfor` VALUES (124, 'admin', '192.168.0.102', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-11-10 11:47:52');
INSERT INTO `sys_logininfor` VALUES (125, 'admin', '192.168.0.102', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-11-10 13:16:41');
INSERT INTO `sys_logininfor` VALUES (126, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-10 13:37:30');
INSERT INTO `sys_logininfor` VALUES (127, 'admin', '127.0.0.1', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-11-10 15:14:35');
INSERT INTO `sys_logininfor` VALUES (128, 'admin', '127.0.0.1', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-11-11 09:54:14');
INSERT INTO `sys_logininfor` VALUES (129, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-11 09:56:17');
INSERT INTO `sys_logininfor` VALUES (130, 'admin', '127.0.0.1', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-11-11 14:44:33');
INSERT INTO `sys_logininfor` VALUES (131, 'admin', '127.0.0.1', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-11-11 15:35:25');
INSERT INTO `sys_logininfor` VALUES (132, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码已失效', '2021-11-12 12:11:19');
INSERT INTO `sys_logininfor` VALUES (133, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-12 12:11:27');
INSERT INTO `sys_logininfor` VALUES (134, 'admin', '127.0.0.1', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-11-12 14:30:43');
INSERT INTO `sys_logininfor` VALUES (135, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-12 14:32:48');
INSERT INTO `sys_logininfor` VALUES (136, 'admin', '127.0.0.1', '内网IP', 'Internet Explorer 11', 'Windows 10', '0', '登录成功', '2021-11-12 14:36:13');
INSERT INTO `sys_logininfor` VALUES (137, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-12 14:36:52');
INSERT INTO `sys_logininfor` VALUES (138, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-12 14:37:54');
INSERT INTO `sys_logininfor` VALUES (139, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-12 14:45:55');
INSERT INTO `sys_logininfor` VALUES (140, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-15 10:51:28');
INSERT INTO `sys_logininfor` VALUES (141, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-18 09:54:09');
INSERT INTO `sys_logininfor` VALUES (142, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-19 14:22:14');
INSERT INTO `sys_logininfor` VALUES (143, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-19 14:59:26');
INSERT INTO `sys_logininfor` VALUES (144, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-19 15:25:10');
INSERT INTO `sys_logininfor` VALUES (145, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-22 10:11:49');
INSERT INTO `sys_logininfor` VALUES (146, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-22 10:24:52');
INSERT INTO `sys_logininfor` VALUES (147, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-22 10:30:56');
INSERT INTO `sys_logininfor` VALUES (148, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-23 09:43:48');
INSERT INTO `sys_logininfor` VALUES (149, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-23 15:27:52');
INSERT INTO `sys_logininfor` VALUES (150, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-24 15:28:43');
INSERT INTO `sys_logininfor` VALUES (151, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码错误', '2021-11-26 09:44:35');
INSERT INTO `sys_logininfor` VALUES (152, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-26 09:44:44');
INSERT INTO `sys_logininfor` VALUES (153, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-29 09:51:39');
INSERT INTO `sys_logininfor` VALUES (154, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-11-30 14:53:19');
INSERT INTO `sys_logininfor` VALUES (155, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-01 11:04:11');
INSERT INTO `sys_logininfor` VALUES (156, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-02 09:09:41');
INSERT INTO `sys_logininfor` VALUES (157, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-02 17:03:12');
INSERT INTO `sys_logininfor` VALUES (158, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-03 09:29:32');
INSERT INTO `sys_logininfor` VALUES (159, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-06 08:54:07');
INSERT INTO `sys_logininfor` VALUES (160, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码错误', '2021-12-06 15:49:23');
INSERT INTO `sys_logininfor` VALUES (161, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-06 15:49:29');
INSERT INTO `sys_logininfor` VALUES (162, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-07 17:00:42');
INSERT INTO `sys_logininfor` VALUES (163, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-07 17:18:08');
INSERT INTO `sys_logininfor` VALUES (164, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-09 08:48:29');
INSERT INTO `sys_logininfor` VALUES (165, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-09 17:02:42');
INSERT INTO `sys_logininfor` VALUES (166, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-10 09:03:56');
INSERT INTO `sys_logininfor` VALUES (167, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-10 15:39:18');
INSERT INTO `sys_logininfor` VALUES (168, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-14 09:09:44');
INSERT INTO `sys_logininfor` VALUES (169, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-14 10:10:18');
INSERT INTO `sys_logininfor` VALUES (170, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-14 14:38:49');
INSERT INTO `sys_logininfor` VALUES (171, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-15 08:32:56');
INSERT INTO `sys_logininfor` VALUES (172, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-15 11:19:09');
INSERT INTO `sys_logininfor` VALUES (173, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-16 09:13:59');
INSERT INTO `sys_logininfor` VALUES (174, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-16 11:58:18');
INSERT INTO `sys_logininfor` VALUES (175, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-17 08:44:09');
INSERT INTO `sys_logininfor` VALUES (176, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-17 10:29:30');
INSERT INTO `sys_logininfor` VALUES (177, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码已失效', '2021-12-20 09:10:38');
INSERT INTO `sys_logininfor` VALUES (178, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-20 09:10:43');
INSERT INTO `sys_logininfor` VALUES (179, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-20 13:33:15');
INSERT INTO `sys_logininfor` VALUES (180, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-21 10:25:15');
INSERT INTO `sys_logininfor` VALUES (181, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-21 11:36:43');
INSERT INTO `sys_logininfor` VALUES (182, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-22 09:04:03');
INSERT INTO `sys_logininfor` VALUES (183, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-22 13:10:04');
INSERT INTO `sys_logininfor` VALUES (184, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-22 14:35:01');
INSERT INTO `sys_logininfor` VALUES (185, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-22 15:43:50');
INSERT INTO `sys_logininfor` VALUES (186, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码错误', '2021-12-24 09:18:59');
INSERT INTO `sys_logininfor` VALUES (187, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-24 09:19:05');
INSERT INTO `sys_logininfor` VALUES (188, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码已失效', '2021-12-28 08:56:29');
INSERT INTO `sys_logininfor` VALUES (189, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-28 08:56:38');
INSERT INTO `sys_logininfor` VALUES (190, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-28 09:26:51');
INSERT INTO `sys_logininfor` VALUES (191, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-28 10:41:59');
INSERT INTO `sys_logininfor` VALUES (192, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-28 13:11:06');
INSERT INTO `sys_logininfor` VALUES (193, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码已失效', '2021-12-28 15:04:03');
INSERT INTO `sys_logininfor` VALUES (194, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-28 15:04:07');
INSERT INTO `sys_logininfor` VALUES (195, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-29 08:41:53');
INSERT INTO `sys_logininfor` VALUES (196, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-29 09:24:32');
INSERT INTO `sys_logininfor` VALUES (197, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-29 13:05:36');
INSERT INTO `sys_logininfor` VALUES (198, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码已失效', '2021-12-31 08:51:47');
INSERT INTO `sys_logininfor` VALUES (199, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2021-12-31 08:51:52');
INSERT INTO `sys_logininfor` VALUES (200, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码已失效', '2022-01-04 08:58:36');
INSERT INTO `sys_logininfor` VALUES (201, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码错误', '2022-01-04 08:58:40');
INSERT INTO `sys_logininfor` VALUES (202, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-04 08:58:46');
INSERT INTO `sys_logininfor` VALUES (203, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-05 14:56:30');
INSERT INTO `sys_logininfor` VALUES (204, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-06 09:14:24');
INSERT INTO `sys_logininfor` VALUES (205, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-07 09:06:30');
INSERT INTO `sys_logininfor` VALUES (206, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-07 13:34:25');
INSERT INTO `sys_logininfor` VALUES (207, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-07 17:44:56');
INSERT INTO `sys_logininfor` VALUES (208, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-10 09:19:01');
INSERT INTO `sys_logininfor` VALUES (209, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-11 08:37:25');
INSERT INTO `sys_logininfor` VALUES (210, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-11 15:30:37');
INSERT INTO `sys_logininfor` VALUES (211, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-11 15:43:49');
INSERT INTO `sys_logininfor` VALUES (212, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码已失效', '2022-01-12 08:38:24');
INSERT INTO `sys_logininfor` VALUES (213, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-12 08:38:31');
INSERT INTO `sys_logininfor` VALUES (214, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-12 10:39:50');
INSERT INTO `sys_logininfor` VALUES (215, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-12 11:11:30');
INSERT INTO `sys_logininfor` VALUES (216, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-12 17:28:43');
INSERT INTO `sys_logininfor` VALUES (217, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-13 08:42:39');
INSERT INTO `sys_logininfor` VALUES (218, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-13 11:24:27');
INSERT INTO `sys_logininfor` VALUES (219, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-13 14:27:08');
INSERT INTO `sys_logininfor` VALUES (220, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码错误', '2022-01-14 08:30:42');
INSERT INTO `sys_logininfor` VALUES (221, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-14 08:30:48');
INSERT INTO `sys_logininfor` VALUES (222, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '1', '验证码已失效', '2022-01-14 14:21:18');
INSERT INTO `sys_logininfor` VALUES (223, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-14 14:21:23');
INSERT INTO `sys_logininfor` VALUES (224, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-14 17:49:24');
INSERT INTO `sys_logininfor` VALUES (225, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-17 08:41:29');
INSERT INTO `sys_logininfor` VALUES (226, 'admin', '127.0.0.1', '内网IP', 'Firefox 9', 'Windows 10', '0', '登录成功', '2022-01-18 14:11:13');
INSERT INTO `sys_logininfor` VALUES (227, 'admin', '127.0.0.1', '内网IP', 'Firefox 9', 'Windows 10', '0', '登录成功', '2022-01-19 11:39:54');
INSERT INTO `sys_logininfor` VALUES (228, 'admin', '127.0.0.1', '内网IP', 'Firefox 9', 'Windows 10', '0', '登录成功', '2022-01-20 08:59:16');
INSERT INTO `sys_logininfor` VALUES (229, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2022-01-20 12:57:42');
INSERT INTO `sys_logininfor` VALUES (230, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2022-01-20 15:39:01');
INSERT INTO `sys_logininfor` VALUES (231, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-20 15:40:27');
INSERT INTO `sys_logininfor` VALUES (232, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2022-01-20 15:42:59');
INSERT INTO `sys_logininfor` VALUES (233, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2022-01-21 08:52:31');
INSERT INTO `sys_logininfor` VALUES (234, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-21 09:05:19');
INSERT INTO `sys_logininfor` VALUES (235, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-21 10:12:15');
INSERT INTO `sys_logininfor` VALUES (236, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-21 10:52:04');
INSERT INTO `sys_logininfor` VALUES (237, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-24 10:53:29');
INSERT INTO `sys_logininfor` VALUES (238, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-24 11:51:28');
INSERT INTO `sys_logininfor` VALUES (239, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '退出成功', '2022-01-24 11:54:05');
INSERT INTO `sys_logininfor` VALUES (240, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-24 11:54:30');
INSERT INTO `sys_logininfor` VALUES (241, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-24 12:58:00');
INSERT INTO `sys_logininfor` VALUES (242, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-24 17:00:11');
INSERT INTO `sys_logininfor` VALUES (243, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '退出成功', '2022-01-24 17:24:50');
INSERT INTO `sys_logininfor` VALUES (244, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-24 17:24:55');
INSERT INTO `sys_logininfor` VALUES (245, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-25 10:52:34');
INSERT INTO `sys_logininfor` VALUES (246, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-26 09:07:56');
INSERT INTO `sys_logininfor` VALUES (247, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-26 11:30:54');
INSERT INTO `sys_logininfor` VALUES (248, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-26 14:08:08');
INSERT INTO `sys_logininfor` VALUES (249, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-26 16:46:54');
INSERT INTO `sys_logininfor` VALUES (250, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-27 08:48:40');
INSERT INTO `sys_logininfor` VALUES (251, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-27 09:48:49');
INSERT INTO `sys_logininfor` VALUES (252, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '退出成功', '2022-01-27 10:15:07');
INSERT INTO `sys_logininfor` VALUES (253, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-27 10:15:13');
INSERT INTO `sys_logininfor` VALUES (254, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-01-28 11:35:02');
INSERT INTO `sys_logininfor` VALUES (255, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-02-08 08:40:03');
INSERT INTO `sys_logininfor` VALUES (256, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2022-02-09 10:03:54');
INSERT INTO `sys_logininfor` VALUES (257, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2022-02-09 11:24:11');
INSERT INTO `sys_logininfor` VALUES (258, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2022-02-09 11:33:23');
INSERT INTO `sys_logininfor` VALUES (259, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-02-09 11:39:07');
INSERT INTO `sys_logininfor` VALUES (260, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2022-02-09 11:43:23');
INSERT INTO `sys_logininfor` VALUES (261, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '1', '验证码已失效', '2022-02-09 13:18:09');
INSERT INTO `sys_logininfor` VALUES (262, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2022-02-09 13:18:15');
INSERT INTO `sys_logininfor` VALUES (263, 'admin', '192.168.0.102', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-02-09 13:35:34');
INSERT INTO `sys_logininfor` VALUES (264, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2022-02-09 13:40:36');
INSERT INTO `sys_logininfor` VALUES (265, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2022-02-09 14:01:21');
INSERT INTO `sys_logininfor` VALUES (266, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2022-02-09 14:02:36');
INSERT INTO `sys_logininfor` VALUES (267, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2022-02-09 14:05:35');
INSERT INTO `sys_logininfor` VALUES (268, 'wei', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '1', '登录用户：wei 不存在', '2022-02-09 14:05:47');
INSERT INTO `sys_logininfor` VALUES (269, 'weiwei', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '1', '验证码错误', '2022-02-09 14:05:53');
INSERT INTO `sys_logininfor` VALUES (270, '韦卫', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '1', '验证码错误', '2022-02-09 14:06:02');
INSERT INTO `sys_logininfor` VALUES (271, '韦卫', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2022-02-09 14:06:07');
INSERT INTO `sys_logininfor` VALUES (272, '韦卫', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '退出成功', '2022-02-09 14:08:50');
INSERT INTO `sys_logininfor` VALUES (273, 'admin', '127.0.0.1', '内网IP', 'Chrome 8', 'Windows 10', '0', '登录成功', '2022-02-09 14:08:57');
INSERT INTO `sys_logininfor` VALUES (274, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-02-09 14:18:28');
INSERT INTO `sys_logininfor` VALUES (275, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-02-09 14:51:29');
INSERT INTO `sys_logininfor` VALUES (276, 'admin', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '退出成功', '2022-02-09 14:53:49');
INSERT INTO `sys_logininfor` VALUES (277, '韦卫', '127.0.0.1', '内网IP', 'Chrome 9', 'Windows 10', '0', '登录成功', '2022-02-09 14:53:58');

SET FOREIGN_KEY_CHECKS = 1;
