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

 Date: 09/02/2022 15:16:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `sort` int(11) NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 389 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------
INSERT INTO `gen_table_column` VALUES (334, '49', 'id', '主键', 'int(11)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:16');
INSERT INTO `gen_table_column` VALUES (335, '49', 'switch_ip', '交换机ip', 'varchar(255)', 'String', 'switchIp', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:16');
INSERT INTO `gen_table_column` VALUES (336, '49', 'switch_name', '交换机姓名', 'varchar(255)', 'String', 'switchName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 3, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:16');
INSERT INTO `gen_table_column` VALUES (337, '49', 'switch_password', '交换机密码', 'varchar(255)', 'String', 'switchPassword', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:16');
INSERT INTO `gen_table_column` VALUES (338, '49', 'problem_id', '问题索引', 'varchar(11)', 'String', 'problemId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:16');
INSERT INTO `gen_table_column` VALUES (339, '49', 'com_id', '命令索引', 'varchar(11)', 'String', 'comId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:16');
INSERT INTO `gen_table_column` VALUES (340, '49', 'value_id', '参数索引', 'varchar(11)', 'String', 'valueId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:16');
INSERT INTO `gen_table_column` VALUES (341, '49', 'resolved', '是否解决', 'varchar(4)', 'String', 'resolved', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:16');
INSERT INTO `gen_table_column` VALUES (342, '49', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '1', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 9, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:16');
INSERT INTO `gen_table_column` VALUES (343, '50', 'id', '主键', 'int(11)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:45');
INSERT INTO `gen_table_column` VALUES (344, '50', 'exhibit', '是否显示', 'varchar(4)', 'String', 'exhibit', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:45');
INSERT INTO `gen_table_column` VALUES (345, '50', 'dynamic_name', '动态信息名称', 'varchar(100)', 'String', 'dynamicName', '0', '0', '1', '1', '1', '1', '1', 'LIKE', 'input', '', 3, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:45');
INSERT INTO `gen_table_column` VALUES (346, '50', 'dynamic_information', '动态信息', 'varchar(255)', 'String', 'dynamicInformation', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:45');
INSERT INTO `gen_table_column` VALUES (347, '50', 'out_id', '下一信息ID', 'int(11)', 'Long', 'outId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2021-12-28 10:43:54', '', '2021-12-28 10:44:45');
INSERT INTO `gen_table_column` VALUES (348, '51', 'id', '主键', 'int(11)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (349, '51', 'command', '命令', 'varchar(255)', 'String', 'command', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (350, '51', 'problem_id', '返回分析id', 'varchar(11)', 'String', 'problemId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (351, '52', 'id', '主键索引', 'int(11)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (352, '52', 'state', '状态', 'varchar(2)', 'String', 'state', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (353, '52', 'command', '命令', 'varchar(100)', 'String', 'command', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (354, '52', 'result_check_id', '返回结果验证id', 'int(11)', 'Long', 'resultCheckId', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (355, '52', 'problem_id', '返回分析id', 'varchar(11)', 'String', 'problemId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (356, '52', 'end_index', '命令结束索引', 'int(11)', 'Long', 'endIndex', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (357, '53', 'id', '主键索引', 'int(11)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (358, '53', 'logic', '逻辑', 'varchar(4)', 'String', 'logic', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (359, '53', 'matched', '匹配', 'varchar(12)', 'String', 'matched', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 3, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (360, '53', 'relative_position', '相对位置', 'varchar(10)', 'String', 'relativePosition', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (361, '53', 'match_content', '匹配内容', 'varchar(255)', 'String', 'matchContent', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'editor', '', 5, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (362, '53', 'action', '动作', 'varchar(8)', 'String', 'action', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (363, '53', 'r_position', '位置', 'int(2)', 'Integer', 'rPosition', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 7, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (364, '53', 'length', '长度', 'varchar(3)', 'String', 'length', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 8, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (365, '53', 'exhibit', '是否显示', 'varchar(4)', 'String', 'exhibit', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 9, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (366, '53', 'word_name', '取词名称', 'varchar(100)', 'String', 'wordName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 10, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (367, '53', 'compare', '比较', 'varchar(2)', 'String', 'compare', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 11, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (368, '53', 'content', '内容', 'varchar(255)', 'String', 'content', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'editor', '', 12, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (369, '53', 't_next_id', 'true下一条分析索引', 'varchar(20)', 'String', 'tNextId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 13, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (370, '53', 't_com_id', 'true下一条命令索引', 'varchar(11)', 'String', 'tComId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 14, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (371, '53', 't_problem_id', 'true问题索引', 'varchar(11)', 'String', 'tProblemId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 15, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (372, '53', 'f_next_id', 'false下一条分析索引', 'varchar(20)', 'String', 'fNextId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 16, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (373, '53', 'f_com_id', 'false下一条命令索引', 'varchar(11)', 'String', 'fComId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 17, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (374, '53', 'f_problem_id', 'false问题索引', 'varchar(11)', 'String', 'fProblemId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 18, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (375, '53', 'return_cmd_id', '返回命令', 'int(11)', 'Long', 'returnCmdId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 19, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (376, '54', 'id', '主键', 'int(11)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (377, '54', 'current_comm_log', '当前通信日志', 'varchar(255)', 'String', 'currentCommLog', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (378, '54', 'current_return_log', '当前返回日志 ', 'text', 'String', 'currentReturnLog', '0', '0', '1', '1', '1', '1', '1', 'EQ', 'textarea', '', 3, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (379, '54', 'current_identifier', '当前标识符', 'text', 'String', 'currentIdentifier', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'textarea', '', 4, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (380, '54', 'create_time', '创建时间', 'datetime', 'Date', 'createTime', '0', '0', '1', '1', NULL, NULL, NULL, 'EQ', 'datetime', '', 5, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (381, '55', 'id', '主键索引', 'int(11)', 'Long', 'id', '1', '1', NULL, '1', NULL, NULL, NULL, 'EQ', 'input', '', 1, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (382, '55', 'brand', '品牌', 'varchar(100)', 'String', 'brand', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 2, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (383, '55', 'type', '型号', 'varchar(100)', 'String', 'type', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'select', '', 3, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (384, '55', 'fireware_version', '内部固件版本', 'varchar(100)', 'String', 'firewareVersion', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 4, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (385, '55', 'sub_version', '子版本号', 'varchar(100)', 'String', 'subVersion', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 5, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (386, '55', 'command_id', '启动命令ID', 'int(11)', 'Long', 'commandId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 6, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (387, '55', 'problem_name', '问题名称', 'varchar(100)', 'String', 'problemName', '0', '0', NULL, '1', '1', '1', '1', 'LIKE', 'input', '', 7, 'admin', '2021-12-28 13:11:41', '', NULL);
INSERT INTO `gen_table_column` VALUES (388, '55', 'problem_describe_id', '问题详细说明和指导索引', 'int(11)', 'Long', 'problemDescribeId', '0', '0', NULL, '1', '1', '1', '1', 'EQ', 'input', '', 8, 'admin', '2021-12-28 13:11:41', '', NULL);

SET FOREIGN_KEY_CHECKS = 1;
