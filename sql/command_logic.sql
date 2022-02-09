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

 Date: 09/02/2022 15:15:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for command_logic
-- ----------------------------
DROP TABLE IF EXISTS `command_logic`;
CREATE TABLE `command_logic`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键索引',
  `state` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '状态',
  `command` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '命令',
  `result_check_id` int(11) NOT NULL DEFAULT 0 COMMENT '返回结果验证id',
  `problem_id` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '返回分析id',
  `end_index` int(11) NOT NULL DEFAULT 0 COMMENT '命令结束索引',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '命令逻辑表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of command_logic
-- ----------------------------
INSERT INTO `command_logic` VALUES (1, '>', 'display device manuinfo', 0, '1', 0);
INSERT INTO `command_logic` VALUES (2, '>', 'display cu', 0, 'mh300004', 0);
INSERT INTO `command_logic` VALUES (3, '>', 'display cu', 0, 'mh300008', 0);
INSERT INTO `command_logic` VALUES (4, '>', 'sys', 1, '0', 5);
INSERT INTO `command_logic` VALUES (5, ']', 'local-user', 1, '0', 6);
INSERT INTO `command_logic` VALUES (6, ']', 'password cimple', 1, '0', 0);
INSERT INTO `command_logic` VALUES (7, '>', 'display cu', 0, 'mh300007', 0);

SET FOREIGN_KEY_CHECKS = 1;
