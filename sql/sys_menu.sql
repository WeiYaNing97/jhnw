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

 Date: 09/02/2022 15:14:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int(4) NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由参数',
  `is_frame` int(1) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2181 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 100, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2021-10-25 11:19:04', 'admin', '2021-10-25 11:49:40', '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 200, 'monitor', NULL, '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2021-10-25 11:19:04', 'admin', '2021-10-25 11:49:48', '系统监控目录');
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 300, 'tool', NULL, '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', '2021-10-25 11:19:04', 'admin', '2021-10-25 11:49:58', '系统工具目录');
INSERT INTO `sys_menu` VALUES (4, '若依官网', 0, 400, 'http://ruoyi.vip', NULL, '', 0, 0, 'M', '0', '0', '', 'guide', 'admin', '2021-10-25 11:19:04', 'admin', '2021-10-25 11:50:11', '若依官网地址');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2021-10-25 11:19:04', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2021-10-25 11:19:04', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2021-10-25 11:19:04', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2021-10-25 11:19:04', '', NULL, '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', '2021-10-25 11:19:04', '', NULL, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2021-10-25 11:19:04', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2021-10-25 11:19:04', '', NULL, '参数设置菜单');
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2021-10-25 11:19:04', '', NULL, '通知公告菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', '', 1, 0, 'M', '0', '0', '', 'log', 'admin', '2021-10-25 11:19:04', '', NULL, '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2021-10-25 11:19:04', '', NULL, '在线用户菜单');
INSERT INTO `sys_menu` VALUES (110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2021-10-25 11:19:04', '', NULL, '定时任务菜单');
INSERT INTO `sys_menu` VALUES (111, '数据监控', 2, 3, 'druid', 'monitor/druid/index', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2021-10-25 11:19:04', '', NULL, '数据监控菜单');
INSERT INTO `sys_menu` VALUES (112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2021-10-25 11:19:04', '', NULL, '服务监控菜单');
INSERT INTO `sys_menu` VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2021-10-25 11:19:04', '', NULL, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES (114, '表单构建', 3, 1, 'build', 'tool/build/index', '', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2021-10-25 11:19:04', '', NULL, '表单构建菜单');
INSERT INTO `sys_menu` VALUES (115, '代码生成', 3, 2, 'gen', 'tool/gen/index', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2021-10-25 11:19:04', '', NULL, '代码生成菜单');
INSERT INTO `sys_menu` VALUES (116, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', '', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2021-10-25 11:19:04', '', NULL, '系统接口菜单');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2021-10-25 11:19:04', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2021-10-25 11:19:04', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1001, '用户查询', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户新增', 100, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户修改', 100, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户删除', 100, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导出', 100, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '用户导入', 100, 6, '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '重置密码', 100, 7, '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色查询', 101, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色新增', 101, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色修改', 101, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色删除', 101, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '角色导出', 101, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单查询', 102, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单新增', 102, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单修改', 102, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1016, '菜单删除', 102, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1017, '部门查询', 103, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1018, '部门新增', 103, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1019, '部门修改', 103, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1020, '部门删除', 103, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1021, '岗位查询', 104, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, '岗位新增', 104, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, '岗位修改', 104, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, '岗位删除', 104, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, '岗位导出', 104, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '字典查询', 105, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '字典新增', 105, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '字典修改', 105, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, '字典删除', 105, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1030, '字典导出', 105, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1031, '参数查询', 106, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1032, '参数新增', 106, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1033, '参数修改', 106, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1034, '参数删除', 106, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1035, '参数导出', 106, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1036, '公告查询', 107, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1037, '公告新增', 107, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1038, '公告修改', 107, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '公告删除', 107, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作查询', 500, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '操作删除', 500, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '日志导出', 500, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录查询', 501, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '登录删除', 501, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '日志导出', 501, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '在线查询', 109, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1047, '批量强退', 109, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '单条强退', 109, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1049, '任务查询', 110, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1050, '任务新增', 110, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1051, '任务修改', 110, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1052, '任务删除', 110, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1053, '状态修改', 110, 5, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1054, '任务导出', 110, 7, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1055, '生成查询', 115, 1, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1056, '生成修改', 115, 2, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1057, '生成删除', 115, 3, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1058, '导入代码', 115, 2, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1059, '预览代码', 115, 4, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1060, '生成代码', 115, 5, '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2021-10-25 11:19:04', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2000, '数据库管理', 0, 1, 'sql', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'edit', 'admin', '2021-10-25 11:49:33', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2025, '返回信息', 2000, 1, 'return_record', 'sql/return_record/index', NULL, 1, 0, 'C', '0', '0', 'sql:return_record:list', '#', 'admin', '2021-10-25 14:14:59', '', NULL, '返回信息菜单');
INSERT INTO `sys_menu` VALUES (2026, '返回信息查询', 2025, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:return_record:query', '#', 'admin', '2021-10-25 14:14:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2027, '返回信息新增', 2025, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:return_record:add', '#', 'admin', '2021-10-25 14:14:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2028, '返回信息修改', 2025, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:return_record:edit', '#', 'admin', '2021-10-25 14:14:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2029, '返回信息删除', 2025, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:return_record:remove', '#', 'admin', '2021-10-25 14:14:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2030, '返回信息导出', 2025, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:return_record:export', '#', 'admin', '2021-10-25 14:14:59', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2133, '获取基本信息命令', 2000, 1, 'get_basic_information', 'sql/get_basic_information/index', NULL, 1, 0, 'C', '0', '0', 'sql:get_basic_information:list', '#', 'admin', '2021-12-02 17:08:35', '', NULL, '获取基本信息命令菜单');
INSERT INTO `sys_menu` VALUES (2134, '获取基本信息命令查询', 2133, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:get_basic_information:query', '#', 'admin', '2021-12-02 17:08:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2135, '获取基本信息命令新增', 2133, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:get_basic_information:add', '#', 'admin', '2021-12-02 17:08:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2136, '获取基本信息命令修改', 2133, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:get_basic_information:edit', '#', 'admin', '2021-12-02 17:08:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2137, '获取基本信息命令删除', 2133, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:get_basic_information:remove', '#', 'admin', '2021-12-02 17:08:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2138, '获取基本信息命令导出', 2133, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:get_basic_information:export', '#', 'admin', '2021-12-02 17:08:35', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2145, '动态信息', 2000, 1, 'dynamic_information', 'sql/dynamic_information/index', NULL, 1, 0, 'C', '0', '0', 'sql:dynamic_information:list', '#', 'admin', '2021-12-10 15:42:33', '', NULL, '动态信息菜单');
INSERT INTO `sys_menu` VALUES (2146, '动态信息查询', 2145, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:dynamic_information:query', '#', 'admin', '2021-12-10 15:42:33', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2147, '动态信息新增', 2145, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:dynamic_information:add', '#', 'admin', '2021-12-10 15:42:33', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2148, '动态信息修改', 2145, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:dynamic_information:edit', '#', 'admin', '2021-12-10 15:42:33', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2149, '动态信息删除', 2145, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:dynamic_information:remove', '#', 'admin', '2021-12-10 15:42:33', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2150, '动态信息导出', 2145, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:dynamic_information:export', '#', 'admin', '2021-12-10 15:42:33', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2151, '命令逻辑', 2000, 1, 'command_logic', 'sql/command_logic/index', NULL, 1, 0, 'C', '0', '0', 'sql:command_logic:list', '#', 'admin', '2021-12-14 10:14:22', '', NULL, '命令逻辑菜单');
INSERT INTO `sys_menu` VALUES (2152, '命令逻辑查询', 2151, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:command_logic:query', '#', 'admin', '2021-12-14 10:14:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2153, '命令逻辑新增', 2151, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:command_logic:add', '#', 'admin', '2021-12-14 10:14:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2154, '命令逻辑修改', 2151, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:command_logic:edit', '#', 'admin', '2021-12-14 10:14:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2155, '命令逻辑删除', 2151, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:command_logic:remove', '#', 'admin', '2021-12-14 10:14:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2156, '命令逻辑导出', 2151, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:command_logic:export', '#', 'admin', '2021-12-14 10:14:22', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2157, '问题扫描逻辑', 2000, 1, 'problem_scan_logic', 'sql/problem_scan_logic/index', NULL, 1, 0, 'C', '0', '0', 'sql:problem_scan_logic:list', '#', 'admin', '2021-12-14 10:14:37', '', NULL, '问题扫描逻辑菜单');
INSERT INTO `sys_menu` VALUES (2158, '问题扫描逻辑查询', 2157, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:problem_scan_logic:query', '#', 'admin', '2021-12-14 10:14:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2159, '问题扫描逻辑新增', 2157, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:problem_scan_logic:add', '#', 'admin', '2021-12-14 10:14:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2160, '问题扫描逻辑修改', 2157, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:problem_scan_logic:edit', '#', 'admin', '2021-12-14 10:14:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2161, '问题扫描逻辑删除', 2157, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:problem_scan_logic:remove', '#', 'admin', '2021-12-14 10:14:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2162, '问题扫描逻辑导出', 2157, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:problem_scan_logic:export', '#', 'admin', '2021-12-14 10:14:37', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2163, '问题及命令', 2000, 1, 'total_question_table', 'sql/total_question_table/index', NULL, 1, 0, 'C', '0', '0', 'sql:total_question_table:list', '#', 'admin', '2021-12-14 10:14:48', '', NULL, '问题及命令菜单');
INSERT INTO `sys_menu` VALUES (2164, '问题及命令查询', 2163, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:total_question_table:query', '#', 'admin', '2021-12-14 10:14:48', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2165, '问题及命令新增', 2163, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:total_question_table:add', '#', 'admin', '2021-12-14 10:14:48', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2166, '问题及命令修改', 2163, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:total_question_table:edit', '#', 'admin', '2021-12-14 10:14:48', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2167, '问题及命令删除', 2163, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:total_question_table:remove', '#', 'admin', '2021-12-14 10:14:48', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2168, '问题及命令导出', 2163, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:total_question_table:export', '#', 'admin', '2021-12-14 10:14:48', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2169, '获取基本信息命令', 2000, 1, 'basic_information', 'sql/basic_information/index', NULL, 1, 0, 'C', '0', '0', 'sql:basic_information:list', '#', 'admin', '2021-12-21 11:39:10', '', NULL, '获取基本信息命令菜单');
INSERT INTO `sys_menu` VALUES (2170, '获取基本信息命令查询', 2169, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:basic_information:query', '#', 'admin', '2021-12-21 11:39:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2171, '获取基本信息命令新增', 2169, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:basic_information:add', '#', 'admin', '2021-12-21 11:39:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2172, '获取基本信息命令修改', 2169, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:basic_information:edit', '#', 'admin', '2021-12-21 11:39:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2173, '获取基本信息命令删除', 2169, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:basic_information:remove', '#', 'admin', '2021-12-21 11:39:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2174, '获取基本信息命令导出', 2169, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:basic_information:export', '#', 'admin', '2021-12-21 11:39:10', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2175, '取值信息存储', 2000, 1, 'value_information', 'sql/value_information/index', NULL, 1, 0, 'C', '0', '0', 'sql:value_information:list', '#', 'admin', '2021-12-22 13:16:05', '', NULL, '取值信息存储菜单');
INSERT INTO `sys_menu` VALUES (2176, '取值信息存储查询', 2175, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:value_information:query', '#', 'admin', '2021-12-22 13:16:05', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2177, '取值信息存储新增', 2175, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:value_information:add', '#', 'admin', '2021-12-22 13:16:05', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2178, '取值信息存储修改', 2175, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:value_information:edit', '#', 'admin', '2021-12-22 13:16:05', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2179, '取值信息存储删除', 2175, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:value_information:remove', '#', 'admin', '2021-12-22 13:16:05', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2180, '取值信息存储导出', 2175, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'sql:value_information:export', '#', 'admin', '2021-12-22 13:16:05', '', NULL, '');

SET FOREIGN_KEY_CHECKS = 1;
