-- root 用户（密码: admin123，BCrypt 加密）
INSERT INTO sys_user (id, username, password, nickname, status) VALUES
(1, 'root', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 1);

-- root 角色
INSERT INTO sys_role (id, name, code, description, status) VALUES
(1, '超级管理员', 'root', '拥有所有权限', 1);

-- root 用户分配 root 角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 系统管理目录
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(1, 0, '系统管理', 1, '/system', NULL, NULL, 'icon-settings', 1);

-- 用户管理菜单
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(2, 1, '用户管理', 2, '/system/user', 'system/user/index', NULL, 'icon-user', 1);

-- 用户管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(3, 2, '查询', 3, NULL, NULL, 'system:user:query', NULL, 1),
(4, 2, '新增', 3, NULL, NULL, 'system:user:add', NULL, 2),
(5, 2, '编辑', 3, NULL, NULL, 'system:user:edit', NULL, 3),
(6, 2, '删除', 3, NULL, NULL, 'system:user:delete', NULL, 4),
(7, 2, '分配角色', 3, NULL, NULL, 'system:user:role', NULL, 5),
(8, 2, '重置密码', 3, NULL, NULL, 'system:user:resetPwd', NULL, 6);

-- 角色管理菜单
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(9, 1, '角色管理', 2, '/system/role', 'system/role/index', NULL, 'icon-user-group', 2);

-- 角色管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(10, 9, '查询', 3, NULL, NULL, 'system:role:query', NULL, 1),
(11, 9, '新增', 3, NULL, NULL, 'system:role:add', NULL, 2),
(12, 9, '编辑', 3, NULL, NULL, 'system:role:edit', NULL, 3),
(13, 9, '删除', 3, NULL, NULL, 'system:role:delete', NULL, 4),
(14, 9, '分配权限', 3, NULL, NULL, 'system:role:permission', NULL, 5);

-- 权限管理菜单
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(15, 1, '权限管理', 2, '/system/permission', 'system/permission/index', NULL, 'icon-shield', 3);

-- 权限管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(16, 15, '查询', 3, NULL, NULL, 'system:permission:query', NULL, 1),
(17, 15, '新增', 3, NULL, NULL, 'system:permission:add', NULL, 2),
(18, 15, '编辑', 3, NULL, NULL, 'system:permission:edit', NULL, 3),
(19, 15, '删除', 3, NULL, NULL, 'system:permission:delete', NULL, 4);

-- 登录日志菜单
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(20, 1, '登录日志', 2, '/system/login-log', 'system/login-log/index', NULL, 'icon-experiment', 4);

-- 登录日志按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(21, 20, '查询', 3, NULL, NULL, 'system:loginLog:query', NULL, 1),
(22, 20, '删除', 3, NULL, NULL, 'system:loginLog:delete', NULL, 2),
(23, 20, '清空', 3, NULL, NULL, 'system:loginLog:remove', NULL, 3);

-- 字典管理菜单
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(24, 1, '字典管理', 2, '/system/dict', 'system/dict/index', NULL, 'icon-book', 5);

-- 字典管理按钮权限
INSERT INTO sys_permission (id, parent_id, name, type, path, component, perms, icon, sort) VALUES
(25, 24, '查询', 3, NULL, NULL, 'system:dict:query', NULL, 1),
(26, 24, '新增', 3, NULL, NULL, 'system:dict:add', NULL, 2),
(27, 24, '编辑', 3, NULL, NULL, 'system:dict:edit', NULL, 3),
(28, 24, '删除', 3, NULL, NULL, 'system:dict:delete', NULL, 4);

-- 字典类型数据
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark) VALUES
(1, '用户状态', 'sys_user_status', 1, '用户账号状态'),
(2, '通用状态', 'sys_common_status', 1, '通用启用/禁用状态'),
(3, '登录状态', 'sys_login_status', 1, '登录成功/失败状态');

-- 字典数据
INSERT INTO sys_dict_data (id, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, status, remark) VALUES
(1, 1, '启用', '1', 'sys_user_status', '', 'success', 1, '用户正常状态'),
(2, 2, '禁用', '0', 'sys_user_status', '', 'danger', 1, '用户禁用状态'),
(3, 1, '正常', '1', 'sys_common_status', '', 'success', 1, '通用正常状态'),
(4, 2, '停用', '0', 'sys_common_status', '', 'danger', 1, '通用停用状态'),
(5, 1, '成功', '1', 'sys_login_status', '', 'success', 1, '登录成功'),
(6, 2, '失败', '0', 'sys_login_status', '', 'danger', 1, '登录失败');
