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

 Date: 09/02/2022 15:15:25
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for value_information
-- ----------------------------
DROP TABLE IF EXISTS `value_information`;
CREATE TABLE `value_information`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `exhibit` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否显示',
  `dynamic_Vname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '动态信息名称',
  `dynamic_information` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '动态信息',
  `out_id` int(11) NOT NULL DEFAULT 0 COMMENT '下一信息ID',
  `display_information` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '动态信息(显示)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '取值信息存储表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of value_information
-- ----------------------------
INSERT INTO `value_information` VALUES (1, '是', '子版本号', '1106', 0, '1106');
INSERT INTO `value_information` VALUES (2, '是', '内部固件版本', '5.20.99,', 1, '5.20.99,');
INSERT INTO `value_information` VALUES (3, '是', '设备品牌', 'H3C', 2, 'H3C');
INSERT INTO `value_information` VALUES (4, '是', '设备型号', 'S2152', 3, 'S2152');
INSERT INTO `value_information` VALUES (5, '否', '密码', '$c$3$ucuLP5tRIUiNMSGST3PKZPvR0Z0bw2/g', 0, '$c$3$ucuLP5tRIUiNMSGST3PKZPvR0Z0bw2/g');
INSERT INTO `value_information` VALUES (6, '是', '用户名', 'admin', 5, 'admin');
INSERT INTO `value_information` VALUES (7, '否', '密码', '$c$3$OY0X1/eznU7U82j2WUcCwjfhsCh25Nqoeg==', 0, '$c$3$OY0X1/eznU7U82j2WUcCwjfhsCh25Nqoeg==');
INSERT INTO `value_information` VALUES (8, '是', '用户名', 'user1', 7, 'user1');
INSERT INTO `value_information` VALUES (9, '否', '密码', '$c$3$fWohTnscKZVRlfAhH7KwKK+ZA4+Jaw==', 0, '$c$3$fWohTnscKZVRlfAhH7KwKK+ZA4+Jaw==');
INSERT INTO `value_information` VALUES (10, '是', '用户名', 'user2', 9, 'user2');
INSERT INTO `value_information` VALUES (11, '是', '子版本号', '1106', 0, '1106');
INSERT INTO `value_information` VALUES (12, '是', '内部固件版本', '5.20.99,', 11, '5.20.99,');
INSERT INTO `value_information` VALUES (13, '是', '设备品牌', 'H3C', 12, 'H3C');
INSERT INTO `value_information` VALUES (14, '是', '设备型号', 'S2152', 13, 'S2152');
INSERT INTO `value_information` VALUES (15, '否', '密码', '$c$3$ucuLP5tRIUiNMSGST3PKZPvR0Z0bw2/g', 0, '$c$3$ucuLP5tRIUiNMSGST3PKZPvR0Z0bw2/g');
INSERT INTO `value_information` VALUES (16, '是', '用户名', 'admin', 15, 'admin');
INSERT INTO `value_information` VALUES (17, '否', '密码', '$c$3$OY0X1/eznU7U82j2WUcCwjfhsCh25Nqoeg==', 0, '$c$3$OY0X1/eznU7U82j2WUcCwjfhsCh25Nqoeg==');
INSERT INTO `value_information` VALUES (18, '是', '用户名', 'user1', 17, 'user1');
INSERT INTO `value_information` VALUES (19, '否', '密码', '$c$3$fWohTnscKZVRlfAhH7KwKK+ZA4+Jaw==', 0, '$c$3$fWohTnscKZVRlfAhH7KwKK+ZA4+Jaw==');
INSERT INTO `value_information` VALUES (20, '是', '用户名', 'user2', 19, 'user2');

SET FOREIGN_KEY_CHECKS = 1;
