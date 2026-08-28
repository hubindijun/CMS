-- admin 用户（密码: admin123，BCrypt 加密）
INSERT INTO sys_user (id, username, password, nickname, status) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 1);

-- 超级管理员角色
INSERT INTO sys_role (id, name, code, description, status) VALUES
(1, '超级管理员', 'admin', '拥有所有权限', 1);

-- admin 用户分配超级管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 系统管理目录
INSERT INTO sys_permission (id, parent_id, name, type, path, component, resource_path, icon, sort) VALUES
(1, 0, '系统管理', 1, '/system', NULL, '/api/system/**', 'icon-settings', 1);

-- 用户管理菜单
INSERT INTO sys_permission (id, parent_id, name, type, path, component, resource_path, icon, sort) VALUES
(2, 1, '用户管理', 2, '/system/user', 'system/user/index', '/api/system/user/**', 'icon-user', 1);

-- 角色管理菜单
INSERT INTO sys_permission (id, parent_id, name, type, path, component, resource_path, icon, sort) VALUES
(9, 1, '角色管理', 2, '/system/role', 'system/role/index', '/api/system/role/**', 'icon-user-group', 2);

-- 权限管理菜单
INSERT INTO sys_permission (id, parent_id, name, type, path, component, resource_path, icon, sort) VALUES
(15, 1, '权限管理', 2, '/system/permission', 'system/permission/index', '/api/system/permission/**', 'icon-shield', 3);

-- 登录日志菜单
INSERT INTO sys_permission (id, parent_id, name, type, path, component, resource_path, icon, sort) VALUES
(20, 1, '登录日志', 2, '/system/login-log', 'system/login-log/index', '/api/system/login-log/**', 'icon-experiment', 4);

-- 字典管理菜单
INSERT INTO sys_permission (id, parent_id, name, type, path, component, resource_path, icon, sort) VALUES
(24, 1, '字典管理', 2, '/system/dict', 'system/dict/index', '/api/system/dict/**', 'icon-book', 5);

-- 超级管理员角色分配所有系统管理权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 9), (1, 15), (1, 20), (1, 24);

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
