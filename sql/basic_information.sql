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

 Date: 09/02/2022 15:15:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for basic_information
-- ----------------------------
DROP TABLE IF EXISTS `basic_information`;
CREATE TABLE `basic_information`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `command` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '命令',
  `problem_id` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '返回分析id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '获取基本信息命令表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of basic_information
-- ----------------------------
INSERT INTO `basic_information` VALUES (2, 'display device manuinfo,display ver', 'mh300013');
INSERT INTO `basic_information` VALUES (3, 'display cu,display ver', 'mh300013');

SET FOREIGN_KEY_CHECKS = 1;
