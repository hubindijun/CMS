-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    password VARCHAR(128) NOT NULL COMMENT '密码',
    nickname VARCHAR(64) COMMENT '昵称',
    email VARCHAR(128) COMMENT '邮箱',
    phone VARCHAR(32) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME,
    update_time DATETIME,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(64) NOT NULL COMMENT '角色名称',
    code VARCHAR(64) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME,
    update_time DATETIME,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表（树形）
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    name VARCHAR(64) NOT NULL COMMENT '权限名称',
    type TINYINT NOT NULL COMMENT '类型 1目录 2菜单 3按钮',
    path VARCHAR(128) COMMENT '前端路由地址',
    component VARCHAR(255) COMMENT '前端组件路径',
    resource_path VARCHAR(255) COMMENT '资源路径规则（Ant风格，支持/**通配符）',
    icon VARCHAR(64) COMMENT '图标',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME,
    update_time DATETIME,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    UNIQUE KEY uk_role_perm (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 登录日志表
CREATE TABLE IF NOT EXISTS sys_login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) COMMENT '用户名',
    ip VARCHAR(128) COMMENT 'IP地址',
    location VARCHAR(255) COMMENT '地点',
    browser VARCHAR(64) COMMENT '浏览器',
    os VARCHAR(64) COMMENT '操作系统',
    status TINYINT DEFAULT 1 COMMENT '状态 0失败 1成功',
    message VARCHAR(255) COMMENT '提示消息',
    login_time DATETIME COMMENT '登录时间',
    create_time DATETIME,
    update_time DATETIME,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted TINYINT DEFAULT 0,
    KEY idx_username (username),
    KEY idx_login_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- 字典类型表
CREATE TABLE IF NOT EXISTS sys_dict_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dict_name VARCHAR(100) COMMENT '字典名称',
    dict_type VARCHAR(100) COMMENT '字典类型',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME,
    update_time DATETIME,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted TINYINT DEFAULT 0,
    UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 字典数据表
CREATE TABLE IF NOT EXISTS sys_dict_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dict_sort INT DEFAULT 0 COMMENT '字典排序',
    dict_label VARCHAR(100) COMMENT '字典标签',
    dict_value VARCHAR(100) COMMENT '字典键值',
    dict_type VARCHAR(100) COMMENT '字典类型',
    css_class VARCHAR(100) COMMENT '样式属性',
    list_class VARCHAR(100) COMMENT '表格回显样式',
    status TINYINT DEFAULT 1 COMMENT '状态 0禁用 1启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME,
    update_time DATETIME,
    create_by VARCHAR(64),
    update_by VARCHAR(64),
    deleted TINYINT DEFAULT 0,
    KEY idx_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';
