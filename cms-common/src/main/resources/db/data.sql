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
