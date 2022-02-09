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

 Date: 09/02/2022 15:15:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for total_question_table
-- ----------------------------
DROP TABLE IF EXISTS `total_question_table`;
CREATE TABLE `total_question_table`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键索引',
  `brand` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '品牌',
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '型号',
  `fireware_version` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内部固件版本',
  `sub_version` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子版本号',
  `command_id` int(11) NULL DEFAULT NULL COMMENT '启动命令ID',
  `problem_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '问题名称',
  `type_problem` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '问题种类',
  `problem_describe_id` int(11) NULL DEFAULT 0 COMMENT '问题详细说明和指导索引',
  `if_cycle` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否循环',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '问题及命令表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of total_question_table
-- ----------------------------
INSERT INTO `total_question_table` VALUES (2, 'H3C', 'S2152', '5.20.99', '1106', 3, '明文存储', '安全配置', 0, 'loop');
INSERT INTO `total_question_table` VALUES (3, 'H3C', 'S2152', '5.20.99', '1106', 2, 'telnet开启状态', '安全配置', 0, 'end');
INSERT INTO `total_question_table` VALUES (4, 'H3C', 'S2152', '5.20.99', '1106', 7, '比较固件版本号', '设备缺陷', 0, 'end');
INSERT INTO `total_question_table` VALUES (5, 'H3C', 'S3600-28P-EI', '3.10', '1510P09', 3, '明文存储', '安全配置', 0, 'loop');
INSERT INTO `total_question_table` VALUES (6, 'H3C', 'S3600-28P-EI', '3.10', '1510P09', 2, 'telnet开启状态', '安全配置', 0, 'end');
INSERT INTO `total_question_table` VALUES (7, 'H3C', 'S3600-28P-EI', '3.10', '1510P09', 7, '比较固件版本号', '设备缺陷', 0, 'end');

SET FOREIGN_KEY_CHECKS = 1;
