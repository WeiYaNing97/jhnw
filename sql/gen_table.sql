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

 Date: 09/02/2022 15:15:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table
-- ----------------------------
INSERT INTO `gen_table` VALUES (49, 'switch_problem', '交换机问题表', NULL, NULL, 'SwitchProblem', 'crud', 'com.sgcc.sql', 'sql', 'switch_problem', '交换机问题', 'ruoyi', '0', '/', '{\"parentMenuId\":2000}', 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:16', NULL);
INSERT INTO `gen_table` VALUES (50, 'value_information', '取值信息存储表', NULL, NULL, 'ValueInformation', 'crud', 'com.sgcc.sql', 'sql', 'value_information', '取值信息存储', 'ruoyi', '0', '/', '{\"parentMenuId\":2000}', 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:45', NULL);
INSERT INTO `gen_table` VALUES (51, 'basic_information', '获取基本信息命令表', NULL, NULL, 'BasicInformation', 'crud', 'com.sgcc.system', 'system', 'information', '获取基本信息命令', 'ruoyi', '0', '/', NULL, 'admin', '2021-12-28 13:11:41', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (52, 'command_logic', '命令逻辑表', NULL, NULL, 'CommandLogic', 'crud', 'com.sgcc.system', 'system', 'logic', '命令逻辑', 'ruoyi', '0', '/', NULL, 'admin', '2021-12-28 13:11:41', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (53, 'problem_scan_logic', '问题扫描逻辑表', NULL, NULL, 'ProblemScanLogic', 'crud', 'com.sgcc.system', 'system', 'logic', '问题扫描逻辑', 'ruoyi', '0', '/', NULL, 'admin', '2021-12-28 13:11:41', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (54, 'return_record', '返回信息表', NULL, NULL, 'ReturnRecord', 'crud', 'com.sgcc.system', 'system', 'record', '返回信息', 'ruoyi', '0', '/', NULL, 'admin', '2021-12-28 13:11:41', '', NULL, NULL);
INSERT INTO `gen_table` VALUES (55, 'total_question_table', '问题及命令表', NULL, NULL, 'TotalQuestionTable', 'crud', 'com.sgcc.system', 'system', 'table', '问题及命令', 'ruoyi', '0', '/', NULL, 'admin', '2021-12-28 13:11:41', '', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
