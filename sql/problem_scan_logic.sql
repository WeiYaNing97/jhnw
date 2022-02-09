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

 Date: 09/02/2022 15:16:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for problem_scan_logic
-- ----------------------------
DROP TABLE IF EXISTS `problem_scan_logic`;
CREATE TABLE `problem_scan_logic`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键索引',
  `logic` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '逻辑',
  `matched` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '匹配',
  `relative_position` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '相对位置',
  `match_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '匹配内容',
  `action` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '动作',
  `r_position` int(2) NOT NULL DEFAULT 0 COMMENT '位置',
  `length` varchar(3) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '长度',
  `exhibit` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '否' COMMENT '是否显示',
  `word_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '取词名称',
  `compare` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '比较',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内容',
  `t_next_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'true下一条分析索引',
  `t_com_id` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'true下一条命令索引',
  `t_problem_id` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'true问题索引',
  `f_next_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'false下一条分析索引',
  `f_com_id` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'false下一条命令索引',
  `f_problem_id` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'false问题索引',
  `return_cmd_id` int(11) NULL DEFAULT NULL COMMENT '返回命令',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '问题扫描逻辑表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of problem_scan_logic
-- ----------------------------
INSERT INTO `problem_scan_logic` VALUES (1, 'null', 'null', '1,0', 'DEVICE_NAME', '取词', 2, '7l', '否', NULL, NULL, NULL, '没问题', '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (2, 'null', '精确匹配', '0', 'VENDOR_NAME', '取词', 2, '1w', '否', NULL, NULL, NULL, '没问题', '2', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (3, 'null', '精确匹配', '0', 'Version', '取词', 1, '1w', '否', NULL, NULL, NULL, '没问题', '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (4, '', '模糊匹配', 'null', 'telnet server enable', 'null', 0, '0', '否', NULL, NULL, NULL, '有问题', '3307', '3', '没问题', '', '3', NULL);
INSERT INTO `problem_scan_logic` VALUES (5, 'null', '不存在', '0', 'command found at \'^\' position.', 'null', 0, '0', '否', NULL, NULL, NULL, '有问题', '5', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (6, 'null', '不存在', '0', 'command found at \'^\' position.', 'null', 0, '0', '否', NULL, NULL, NULL, '有问题', '0', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (7, 'or', 'null', '0', '', '取版本', 0, '0', '否', NULL, '<', '5.20.99', '没问题', '0', '4', '有问题', '0318', '4', NULL);
INSERT INTO `problem_scan_logic` VALUES (8, '', '精确匹配', 'null', 'local-user', 'null', 1, '0', '是', '用户名', '', NULL, 'wh300009', '0', NULL, '完成', '', '', NULL);
INSERT INTO `problem_scan_logic` VALUES (9, 'and', 'null', '0,0', 'local-user', '取词', 1, '1w', '是', '用户名', NULL, NULL, 'mh300010', '0', NULL, '', NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (10, 'and', '精确匹配', '1,0', 'password cipher', 'null', 1, '0', '否', '密码', NULL, NULL, 'wh300011', '0', NULL, '没问题', '3291', '2', NULL);
INSERT INTO `problem_scan_logic` VALUES (11, 'and', 'null', '0,0', 'password cipher', '取词', 1, '1w', '否', '密码', NULL, NULL, '有问题', '3291', '2', '', '3291', '2', NULL);
INSERT INTO `problem_scan_logic` VALUES (12, 'null', 'null', '0', NULL, 'null', 0, '0', '否', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (13, '', '精确匹配', 'null', 'DEVICE_NAME', 'null', 0, '0', '否', '', '', NULL, 'wh300014', '0', NULL, '', NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (14, 'and', 'null', '0,0', 'DEVICE_NAME', '取词', 2, '1w', '是', '设备型号', NULL, NULL, 'mh300015', '0', NULL, '', NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (15, 'and', '精确匹配', 'null', 'VENDOR_NAME', 'null', 0, '0', '否', '', '', NULL, 'wh300016', '0', NULL, '', NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (16, 'and', 'null', '0,0', 'VENDOR_NAME', '取词', 2, '1w', '是', '设备品牌', NULL, NULL, 'mh300017', '0', NULL, '', NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (17, 'and', '精确匹配', 'null', 'Comware Software, Version', 'null', 0, '0', '否', '', '', NULL, 'wh300018', '0', NULL, '', NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (18, 'and', 'null', '0,0', 'Comware Software, Version', '取词', 1, '1w', '是', '内部固件版本', NULL, NULL, 'mh300020', '0', NULL, '', NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (20, 'and', '精确匹配', 'null', 'Release', 'null', 0, '0', '否', '', '', NULL, 'wh300021', '0', NULL, '', NULL, NULL, NULL);
INSERT INTO `problem_scan_logic` VALUES (21, 'and', 'null', '0,0', 'Release', '取词', 1, '1w', '是', '子版本号', NULL, NULL, '没问题', '0', NULL, '没问题', NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
