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

 Date: 09/02/2022 15:18:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for switch_problem
-- ----------------------------
DROP TABLE IF EXISTS `switch_problem`;
CREATE TABLE `switch_problem`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `switch_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '交换机ip',
  `switch_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '交换机姓名',
  `switch_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '交换机密码',
  `problem_id` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '问题索引',
  `if_question` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否有问题',
  `com_id` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '命令索引',
  `value_id` int(11) NULL DEFAULT NULL COMMENT '参数索引',
  `resolved` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '否' COMMENT '是否解决',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录名称',
  `phonenumber` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录手机号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `name`(`user_name`) USING BTREE COMMENT '姓名',
  INDEX `phone`(`phonenumber`) USING BTREE COMMENT '手机号'
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '交换机问题表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of switch_problem
-- ----------------------------
INSERT INTO `switch_problem` VALUES (1, '192.168.1.100', 'admin', 'admin', NULL, '没问题', '0', 4, '否', '2022-02-09 15:09:51', '韦卫', '18200000222');
INSERT INTO `switch_problem` VALUES (2, '192.168.1.100', 'admin', 'admin', '2', '有问题', '3291', 6, '否', '2022-02-09 15:09:59', '韦卫', '18200000222');
INSERT INTO `switch_problem` VALUES (3, '192.168.1.100', 'admin', 'admin', '2', '有问题', '3291', 8, '否', '2022-02-09 15:09:59', '韦卫', '18200000222');
INSERT INTO `switch_problem` VALUES (4, '192.168.1.100', 'admin', 'admin', '2', '有问题', '3291', 10, '否', '2022-02-09 15:09:59', '韦卫', '18200000222');
INSERT INTO `switch_problem` VALUES (5, '192.168.1.100', 'admin', 'admin', '4', '没问题', '0', 0, '否', '2022-02-09 15:10:15', '韦卫', '18200000222');
INSERT INTO `switch_problem` VALUES (6, '192.168.1.100', 'admin', 'admin', NULL, '没问题', '0', 14, '否', '2022-02-09 15:11:33', '韦卫', '18200000222');
INSERT INTO `switch_problem` VALUES (7, '192.168.1.100', 'admin', 'admin', '2', '有问题', '3291', 16, '否', '2022-02-09 15:11:41', '韦卫', '18200000222');
INSERT INTO `switch_problem` VALUES (8, '192.168.1.100', 'admin', 'admin', '2', '有问题', '3291', 18, '否', '2022-02-09 15:11:41', '韦卫', '18200000222');
INSERT INTO `switch_problem` VALUES (9, '192.168.1.100', 'admin', 'admin', '2', '有问题', '3291', 20, '否', '2022-02-09 15:11:42', '韦卫', '18200000222');
INSERT INTO `switch_problem` VALUES (10, '192.168.1.100', 'admin', 'admin', '4', '没问题', '0', 0, '否', '2022-02-09 15:11:58', '韦卫', '18200000222');

SET FOREIGN_KEY_CHECKS = 1;
