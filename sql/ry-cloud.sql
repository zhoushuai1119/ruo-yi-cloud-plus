-- ----------------------------
-- 第三方平台授权表
-- ----------------------------
drop table if exists sys_social;
create table sys_social
(
    id                 bigint           not null        comment '主键',
    user_id            bigint           not null        comment '用户ID',
    tenant_id          varchar(20)      default null    comment '租户id',
    auth_id            varchar(255)     not null        comment '平台+平台唯一id',
    source             varchar(255)     not null        comment '用户来源',
    open_id            varchar(255)     default null    comment '平台编号唯一id',
    user_name          varchar(30)      not null        comment '登录账号',
    nick_name          varchar(30)      default ''      comment '用户昵称',
    email              varchar(255)     default ''      comment '用户邮箱',
    avatar             varchar(500)     default ''      comment '头像地址',
    access_token       varchar(255)     not null        comment '用户的授权令牌',
    expire_in          int              default null    comment '用户的授权令牌的有效期，部分平台可能没有',
    refresh_token      varchar(255)     default null    comment '刷新令牌，部分平台可能没有',
    access_code        varchar(255)     default null    comment '平台的授权信息，部分平台可能没有',
    union_id           varchar(255)     default null    comment '用户的 unionid',
    scope              varchar(255)     default null    comment '授予的权限，部分平台可能没有',
    token_type         varchar(255)     default null    comment '个别平台的授权信息，部分平台可能没有',
    id_token           varchar(255)     default null    comment 'id token，部分平台可能没有',
    mac_algorithm      varchar(255)     default null    comment '小米平台用户的附带属性，部分平台可能没有',
    mac_key            varchar(255)     default null    comment '小米平台用户的附带属性，部分平台可能没有',
    code               varchar(255)     default null    comment '用户的授权code，部分平台可能没有',
    oauth_token        varchar(255)     default null    comment 'Twitter平台用户的附带属性，部分平台可能没有',
    oauth_token_secret varchar(255)     default null    comment 'Twitter平台用户的附带属性，部分平台可能没有',
    create_dept        bigint(20)                       comment '创建部门',
    create_by          bigint(20)                       comment '创建者',
    create_time        datetime                         comment '创建时间',
    update_by          bigint(20)                       comment '更新者',
    update_time        datetime                         comment '更新时间',
    del_flag           char(1)          default '0'     comment '删除标志（0代表存在 2代表删除）',
    PRIMARY KEY (id)
) engine=innodb comment = '社会化关系表';

-- ----------------------------
-- 租户表
-- ----------------------------
drop table if exists sys_tenant;
create table sys_tenant
(
    id                bigint(20)    not null        comment 'id',
    tenant_id         varchar(20)   not null        comment '租户编号',
    contact_user_name varchar(20)                   comment '联系人',
    contact_phone     varchar(20)                   comment '联系电话',
    company_name      varchar(50)                   comment '企业名称',
    license_number    varchar(30)                   comment '统一社会信用代码',
    address           varchar(200)                  comment '地址',
    intro             varchar(200)                  comment '企业简介',
    domain            varchar(200)                  comment '域名',
    remark            varchar(200)                  comment '备注',
    package_id        bigint(20)                    comment '租户套餐编号',
    expire_time       datetime                      comment '过期时间',
    account_count     int           default -1      comment '用户数量（-1不限制）',
    status            char(1)       default '0'     comment '租户状态（0正常 1停用）',
    del_flag          char(1)       default '0'     comment '删除标志（0代表存在 2代表删除）',
    create_dept       bigint(20)                    comment '创建部门',
    create_by         bigint(20)                    comment '创建者',
    create_time       datetime                      comment '创建时间',
    update_by         bigint(20)                    comment '更新者',
    update_time       datetime                      comment '更新时间',
    primary key (id)
) engine=innodb comment = '租户表';


-- ----------------------------
-- 初始化-租户表数据
-- ----------------------------

insert into sys_tenant values(1, '000000', '管理组', '15888888888', 'XXX有限公司', NULL, NULL, '多租户通用后台管理管理系统', NULL, NULL, NULL, NULL, -1, '0', '0', 103, 1, sysdate(), NULL, NULL);


-- ----------------------------
-- 租户套餐表
-- ----------------------------
drop table if exists sys_tenant_package;
create table sys_tenant_package (
    package_id              bigint(20)     not null    comment '租户套餐id',
    package_name            varchar(20)                comment '套餐名称',
    menu_ids                varchar(3000)              comment '关联菜单id',
    remark                  varchar(200)               comment '备注',
    menu_check_strictly     tinyint(1)     default 1   comment '菜单树选择项是否关联显示',
    status                  char(1)        default '0' comment '状态（0正常 1停用）',
    del_flag                char(1)        default '0' comment '删除标志（0代表存在 2代表删除）',
    create_dept             bigint(20)                 comment '创建部门',
    create_by               bigint(20)                 comment '创建者',
    create_time             datetime                   comment '创建时间',
    update_by               bigint(20)                 comment '更新者',
    update_time             datetime                   comment '更新时间',
    primary key (package_id)
) engine=innodb comment = '租户套餐表';


-- ----------------------------
-- 1、部门表
-- ----------------------------
drop table if exists sys_dept;
create table sys_dept (
  dept_id           bigint(20)      not null                   comment '部门id',
  tenant_id         varchar(20)     default '000000'           comment '租户编号',
  parent_id         bigint(20)      default 0                  comment '父部门id',
  ancestors         varchar(500)    default ''                 comment '祖级列表',
  dept_name         varchar(30)     default ''                 comment '部门名称',
  order_num         int(4)          default 0                  comment '显示顺序',
  leader            bigint(20)      default null               comment '负责人',
  phone             varchar(11)     default null               comment '联系电话',
  email             varchar(50)     default null               comment '邮箱',
  status            char(1)         default '0'                comment '部门状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_dept       bigint(20)      default null               comment '创建部门',
  create_by         bigint(20)      default null               comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         bigint(20)      default null               comment '更新者',
  update_time       datetime                                   comment '更新时间',
  primary key (dept_id)
) engine=innodb comment = '部门表';

-- ----------------------------
-- 初始化-部门表数据
-- ----------------------------


insert into sys_dept values(100, '000000', 0,   '0',          'XXX科技',   0, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(101, '000000', 100, '0,100',      '深圳总公司', 1, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(102, '000000', 100, '0,100',      '长沙分公司', 2, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(103, '000000', 101, '0,100,101',  '研发部门',   1, 1, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(104, '000000', 101, '0,100,101',  '市场部门',   2, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(105, '000000', 101, '0,100,101',  '测试部门',   3, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(106, '000000', 101, '0,100,101',  '财务部门',   4, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(107, '000000', 101, '0,100,101',  '运维部门',   5, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(108, '000000', 102, '0,100,102',  '市场部门',   1, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);
insert into sys_dept values(109, '000000', 102, '0,100,102',  '财务部门',   2, null, '15888888888', 'xxx@qq.com', '0', '0', 103, 1, sysdate(), null, null);


-- ----------------------------
-- 2、用户信息表
-- ----------------------------
drop table if exists sys_user;
create table sys_user (
  user_id           bigint(20)      not null                   comment '用户ID',
  tenant_id         varchar(20)     default '000000'           comment '租户编号',
  dept_id           bigint(20)      default null               comment '部门ID',
  user_name         varchar(30)     not null                   comment '用户账号',
  nick_name         varchar(30)     not null                   comment '用户昵称',
  user_type         varchar(10)     default 'sys_user'         comment '用户类型（sys_user系统用户）',
  email             varchar(50)     default ''                 comment '用户邮箱',
  phonenumber       varchar(11)     default ''                 comment '手机号码',
  sex               char(1)         default '0'                comment '用户性别（0男 1女 2未知）',
  avatar            bigint(20)                                 comment '头像地址',
  password          varchar(100)    default ''                 comment '密码',
  status            char(1)         default '0'                comment '帐号状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  login_ip          varchar(128)    default ''                 comment '最后登录IP',
  login_date        datetime                                   comment '最后登录时间',
  create_dept       bigint(20)      default null               comment '创建部门',
  create_by         bigint(20)      default null               comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         bigint(20)      default null               comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (user_id)
) engine=innodb comment = '用户信息表';

-- ----------------------------
-- 初始化-用户信息表数据
-- ----------------------------
insert into sys_user values(1, '000000', 103, 'admin', '疯狂的狮子Li', 'sys_user', 'crazyLionLi@163.com', '15888888888', '1', null, '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), 103, 1, sysdate(), null, null, '管理员');
insert into sys_user values(2, '000000', 105, 'lionli', '疯狂的狮子Li', 'sys_user', 'crazyLionLi@qq.com',  '15666666666', '1', null, '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), 103, 1, sysdate(), null, null, '测试员');


-- ----------------------------
-- 3、岗位信息表
-- ----------------------------
drop table if exists sys_post;
create table sys_post
(
  post_id       bigint(20)      not null                   comment '岗位ID',
  tenant_id     varchar(20)     default '000000'           comment '租户编号',
  post_code     varchar(64)     not null                   comment '岗位编码',
  post_name     varchar(50)     not null                   comment '岗位名称',
  post_sort     int(4)          not null                   comment '显示顺序',
  status        char(1)         not null                   comment '状态（0正常 1停用）',
  create_dept   bigint(20)      default null               comment '创建部门',
  create_by     bigint(20)      default null               comment '创建者',
  create_time   datetime                                   comment '创建时间',
  update_by     bigint(20)      default null               comment '更新者',
  update_time   datetime                                   comment '更新时间',
  remark        varchar(500)    default null               comment '备注',
  primary key (post_id)
) engine=innodb comment = '岗位信息表';

-- ----------------------------
-- 初始化-岗位信息表数据
-- ----------------------------
insert into sys_post values(1, '000000', 'ceo',  '董事长',    1, '0', 103, 1, sysdate(), null, null, '');
insert into sys_post values(2, '000000', 'se',   '项目经理',  2, '0', 103, 1, sysdate(), null, null, '');
insert into sys_post values(3, '000000', 'hr',   '人力资源',  3, '0', 103, 1, sysdate(), null, null, '');
insert into sys_post values(4, '000000', 'user', '普通员工',  4, '0', 103, 1, sysdate(), null, null, '');


-- ----------------------------
-- 4、角色信息表
-- ----------------------------
drop table if exists sys_role;
create table sys_role (
  role_id              bigint(20)      not null                   comment '角色ID',
  tenant_id            varchar(20)     default '000000'           comment '租户编号',
  role_name            varchar(30)     not null                   comment '角色名称',
  role_key             varchar(100)    not null                   comment '角色权限字符串',
  role_sort            int(4)          not null                   comment '显示顺序',
  data_scope           char(1)         default '1'                comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  menu_check_strictly  tinyint(1)      default 1                  comment '菜单树选择项是否关联显示',
  dept_check_strictly  tinyint(1)      default 1                  comment '部门树选择项是否关联显示',
  status               char(1)         not null                   comment '角色状态（0正常 1停用）',
  del_flag             char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_dept          bigint(20)      default null               comment '创建部门',
  create_by            bigint(20)      default null               comment '创建者',
  create_time          datetime                                   comment '创建时间',
  update_by            bigint(20)      default null               comment '更新者',
  update_time          datetime                                   comment '更新时间',
  remark               varchar(500)    default null               comment '备注',
  primary key (role_id)
) engine=innodb comment = '角色信息表';

-- ----------------------------
-- 初始化-角色信息表数据
-- ----------------------------
insert into sys_role values(1, '000000', '超级管理员',  'superadmin',  1, 1, 1, 1, '0', '0', 103, 1, sysdate(), null, null, '超级管理员');
insert into sys_role values(2, '000000', '普通角色',    'common', 2, 2, 1, 1, '0', '0', 103, 1, sysdate(), null, null, '普通角色');


-- ----------------------------
-- 5、菜单权限表
-- ----------------------------
drop table if exists sys_menu;
create table sys_menu (
  menu_id           bigint(20)      not null                   comment '菜单ID',
  menu_name         varchar(50)     not null                   comment '菜单名称',
  parent_id         bigint(20)      default 0                  comment '父菜单ID',
  order_num         int(4)          default 0                  comment '显示顺序',
  path              varchar(200)    default ''                 comment '路由地址',
  component         varchar(255)    default null               comment '组件路径',
  query_param       varchar(255)    default null               comment '路由参数',
  is_frame          int(1)          default 1                  comment '是否为外链（0是 1否）',
  is_cache          int(1)          default 0                  comment '是否缓存（0缓存 1不缓存）',
  menu_type         char(1)         default ''                 comment '菜单类型（M目录 C菜单 F按钮）',
  visible           char(1)         default 0                  comment '显示状态（0显示 1隐藏）',
  status            char(1)         default 0                  comment '菜单状态（0正常 1停用）',
  perms             varchar(100)    default null               comment '权限标识',
  icon              varchar(100)    default '#'                comment '菜单图标',
  create_dept       bigint(20)      default null               comment '创建部门',
  create_by         bigint(20)      default null               comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         bigint(20)      default null               comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default ''                 comment '备注',
  primary key (menu_id)
) engine=innodb comment = '菜单权限表';

-- ----------------------------
-- 6、用户和角色关联表  用户N-1角色
-- ----------------------------
drop table if exists sys_user_role;
create table sys_user_role (
                               user_id   bigint(20) not null comment '用户ID',
                               role_id   bigint(20) not null comment '角色ID',
                               primary key(user_id, role_id)
) engine=innodb comment = '用户和角色关联表';

-- ----------------------------
-- 初始化-用户和角色关联表数据
-- ----------------------------
insert into sys_user_role values ('1', '1');
insert into sys_user_role values ('2', '2');

-- ----------------------------
-- 7、角色和菜单关联表  角色1-N菜单
-- ----------------------------
drop table if exists sys_role_menu;
create table sys_role_menu (
                               role_id   bigint(20) not null comment '角色ID',
                               menu_id   bigint(20) not null comment '菜单ID',
                               primary key(role_id, menu_id)
) engine=innodb comment = '角色和菜单关联表';

-- ----------------------------
-- 初始化-菜单信息表数据
-- ----------------------------
-- 一级菜单
insert into sys_menu values('1', '系统管理', '0', '1', 'system',           null, '', 1, 0, 'M', '0', '0', '', 'system',   103, 1, sysdate(), null, null, '系统管理目录');
insert into sys_menu values('2', '租户管理', '0', '2', 'tenant',           null, '', 1, 0, 'M', '0', '0', '', 'chart',    103, 1, sysdate(), null, null, '租户管理目录');
insert into sys_menu values('3', '系统监控', '0', '3', 'monitor',          null, '', 1, 0, 'M', '0', '0', '', 'monitor',  103, 1, sysdate(), null, null, '系统监控目录');
insert into sys_menu values('4', '系统工具', '0', '4', 'tool',             null, '', 1, 0, 'M', '0', '0', '', 'tool',     103, 1, sysdate(), null, null, '系统工具目录');

-- ----------------------------
-- 初始化-角色和菜单关联表数据
-- ----------------------------
insert into sys_role_menu values ('2', '1');
insert into sys_role_menu values ('2', '2');
insert into sys_role_menu values ('2', '3');
insert into sys_role_menu values ('2', '4');

-- 系统管理 -- 二级菜单

-- 用户管理
insert into sys_menu values('101',  '用户管理',     '1',   '1', 'user',             'system/user/index',            '', 1, 0, 'C', '0', '0', 'system:user:list',            'user',          103, 1, sysdate(), null, null, '用户管理菜单');
insert into sys_role_menu values ('2', '101');
-- 用户管理按钮
insert into sys_menu values('1011', '用户查询', '101', '1',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:query',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1012', '用户新增', '101', '2',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:add',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1013', '用户修改', '101', '3',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1014', '用户删除', '101', '4',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1015', '用户导出', '101', '5',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:export',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1016', '用户导入', '101', '6',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:import',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1017', '重置密码', '101', '7',  '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '1011');
insert into sys_role_menu values ('2', '1012');
insert into sys_role_menu values ('2', '1013');
insert into sys_role_menu values ('2', '1014');
insert into sys_role_menu values ('2', '1015');
insert into sys_role_menu values ('2', '1016');
insert into sys_role_menu values ('2', '1017');

-- 角色管理
insert into sys_menu values('102',  '角色管理',     '1',   '2', 'role',             'system/role/index',            '', 1, 0, 'C', '0', '0', 'system:role:list',            'peoples',       103, 1, sysdate(), null, null, '角色管理菜单');
insert into sys_role_menu values ('2', '102');
-- 角色管理按钮
insert into sys_menu values('1021', '角色查询', '102', '1',  '', '', '', 1, 0, 'F', '0', '0', 'system:role:query',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1022', '角色新增', '102', '2',  '', '', '', 1, 0, 'F', '0', '0', 'system:role:add',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1023', '角色修改', '102', '3',  '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1024', '角色删除', '102', '4',  '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1025', '角色导出', '102', '5',  '', '', '', 1, 0, 'F', '0', '0', 'system:role:export',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '1021');
insert into sys_role_menu values ('2', '1022');
insert into sys_role_menu values ('2', '1023');
insert into sys_role_menu values ('2', '1024');
insert into sys_role_menu values ('2', '1025');

-- 菜单管理
insert into sys_menu values('103',  '菜单管理',     '1',   '3', 'menu',             'system/menu/index',            '', 1, 0, 'C', '0', '0', 'system:menu:list',            'tree-table',    103, 1, sysdate(), null, null, '菜单管理菜单');
insert into sys_role_menu values ('2', '103');
-- 菜单管理按钮
insert into sys_menu values('1031', '菜单查询', '103', '1',  '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1032', '菜单新增', '103', '2',  '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1033', '菜单修改', '103', '3',  '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1034', '菜单删除', '103', '4',  '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '1031');
insert into sys_role_menu values ('2', '1032');
insert into sys_role_menu values ('2', '1033');
insert into sys_role_menu values ('2', '1034');

-- 部门管理
insert into sys_menu values('104',  '部门管理',     '1',   '4', 'dept',             'system/dept/index',            '', 1, 0, 'C', '0', '0', 'system:dept:list',            'tree',          103, 1, sysdate(), null, null, '部门管理菜单');
insert into sys_role_menu values ('2', '104');
-- 部门管理按钮
insert into sys_menu values('1041', '部门查询', '104', '1',  '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1042', '部门新增', '104', '2',  '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1043', '部门修改', '104', '3',  '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1044', '部门删除', '104', '4',  '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '1041');
insert into sys_role_menu values ('2', '1042');
insert into sys_role_menu values ('2', '1043');
insert into sys_role_menu values ('2', '1044');

-- 岗位管理
insert into sys_menu values('105',  '岗位管理',     '1',   '5', 'post',             'system/post/index',            '', 1, 0, 'C', '0', '0', 'system:post:list',            'post',          103, 1, sysdate(), null, null, '岗位管理菜单');
insert into sys_role_menu values ('2', '105');
-- 岗位管理按钮
insert into sys_menu values('1051', '岗位查询', '105', '1',  '', '', '', 1, 0, 'F', '0', '0', 'system:post:query',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1052', '岗位新增', '105', '2',  '', '', '', 1, 0, 'F', '0', '0', 'system:post:add',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1053', '岗位修改', '105', '3',  '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1054', '岗位删除', '105', '4',  '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1055', '岗位导出', '105', '5',  '', '', '', 1, 0, 'F', '0', '0', 'system:post:export',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '1051');
insert into sys_role_menu values ('2', '1052');
insert into sys_role_menu values ('2', '1053');
insert into sys_role_menu values ('2', '1054');
insert into sys_role_menu values ('2', '1055');

-- 字典管理
insert into sys_menu values('106',  '字典管理',     '1',   '6', 'dict',             'system/dict/index',            '', 1, 0, 'C', '0', '0', 'system:dict:list',            'dict',          103, 1, sysdate(), null, null, '字典管理菜单');
insert into sys_role_menu values ('2', '106');
-- 字典管理按钮
insert into sys_menu values('1061', '字典查询', '106', '1', '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:query',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1062', '字典新增', '106', '2', '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:add',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1063', '字典修改', '106', '3', '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1064', '字典删除', '106', '4', '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1065', '字典导出', '106', '5', '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:export',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '1061');
insert into sys_role_menu values ('2', '1062');
insert into sys_role_menu values ('2', '1063');
insert into sys_role_menu values ('2', '1064');
insert into sys_role_menu values ('2', '1065');

-- 参数设置
insert into sys_menu values('107',  '参数设置',     '1',   '7', 'config',           'system/config/index',          '', 1, 0, 'C', '0', '0', 'system:config:list',          'edit',          103, 1, sysdate(), null, null, '参数设置菜单');
insert into sys_role_menu values ('2', '107');
-- 参数设置按钮
insert into sys_menu values('1071', '参数查询', '107', '1', '#', '', '', 1, 0, 'F', '0', '0', 'system:config:query',        '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1072', '参数新增', '107', '2', '#', '', '', 1, 0, 'F', '0', '0', 'system:config:add',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1073', '参数修改', '107', '3', '#', '', '', 1, 0, 'F', '0', '0', 'system:config:edit',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1074', '参数删除', '107', '4', '#', '', '', 1, 0, 'F', '0', '0', 'system:config:remove',       '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1075', '参数导出', '107', '5', '#', '', '', 1, 0, 'F', '0', '0', 'system:config:export',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '1071');
insert into sys_role_menu values ('2', '1072');
insert into sys_role_menu values ('2', '1073');
insert into sys_role_menu values ('2', '1074');
insert into sys_role_menu values ('2', '1075');

-- 通知公告
insert into sys_menu values('108',  '通知公告',     '1',   '8', 'notice',           'system/notice/index',          '', 1, 0, 'C', '0', '0', 'system:notice:list',          'message',       103, 1, sysdate(), null, null, '通知公告菜单');
insert into sys_role_menu values ('2', '108');
-- 通知公告按钮
insert into sys_menu values('1081', '公告查询', '108', '1', '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:query',        '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1082', '公告新增', '108', '2', '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:add',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1083', '公告修改', '108', '3', '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1084', '公告删除', '108', '4', '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '1081');
insert into sys_role_menu values ('2', '1082');
insert into sys_role_menu values ('2', '1083');
insert into sys_role_menu values ('2', '1084');

-- 日志管理
insert into sys_menu values('109',  '日志管理',     '1',   '9', 'log',              '',                             '', 1, 0, 'M', '0', '0', '',                            'log',           103, 1, sysdate(), null, null, '日志管理菜单');
insert into sys_role_menu values ('2', '109');

-- 日志管理--三级菜单
-- 操作日志
insert into sys_menu values('1091',  '操作日志', '109', '1', 'operlog',    'monitor/operlog/index',    '', 1, 0, 'C', '0', '0', 'monitor:operlog:list',    'form',          103, 1, sysdate(), null, null, '操作日志菜单');
insert into sys_role_menu values ('2', '1091');
-- 操作日志按钮
insert into sys_menu values('10911', '操作查询', '1091', '1', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query',      '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('10912', '操作删除', '1091', '2', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove',     '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('10913', '日志导出', '1091', '3', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export',     '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '10911');
insert into sys_role_menu values ('2', '10912');
insert into sys_role_menu values ('2', '10913');

-- 登录日志
insert into sys_menu values('1092',  '登录日志', '109', '2', 'logininfor', 'monitor/logininfor/index', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor',    103, 1, sysdate(), null, null, '登录日志菜单');
insert into sys_role_menu values ('2', '1092');
-- 登录日志按钮
insert into sys_menu values('10921', '登录查询', '1092', '1', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query',   '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('10922', '登录删除', '1092', '2', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove',  '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('10923', '日志导出', '1092', '3', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export',  '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('10924', '账户解锁', '1092', '4', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock',  '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '10921');
insert into sys_role_menu values ('2', '10922');
insert into sys_role_menu values ('2', '10923');
insert into sys_role_menu values ('2', '10924');

-- 文件管理
insert into sys_menu values('110',  '文件管理',     '1',   '10', 'oss',              'system/oss/index',            '', 1, 0, 'C', '0', '0', 'system:oss:list',              'upload',       103, 1, sysdate(), null, null, '文件管理菜单');
insert into sys_role_menu values ('2', '110');
-- 文件管理按钮
insert into sys_menu values('1101', '文件查询', '110', '1', '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:query',        '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1102', '文件上传', '110', '2', '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:upload',       '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1103', '文件下载', '110', '3', '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:download',     '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1104', '文件删除', '110', '4', '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:remove',       '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1105', '配置添加', '110', '5', '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:add',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1106', '配置编辑', '110', '6', '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '1101');
insert into sys_role_menu values ('2', '1102');
insert into sys_role_menu values ('2', '1103');
insert into sys_role_menu values ('2', '1104');
insert into sys_role_menu values ('2', '1105');
insert into sys_role_menu values ('2', '1106');

-- 客户端管理
insert into sys_menu values('111',  '客户端管理',   '1',   '11', 'client',           'system/client/index',         '', 1, 0, 'C', '0', '0', 'system:client:list',          'international', 103, 1, sysdate(), null, null, '客户端管理菜单');
insert into sys_role_menu values ('2', '111');
-- 客户端管理按钮
insert into sys_menu values('1111', '客户端管理查询', '111', '1',  '#', '', '', 1, 0, 'F', '0', '0', 'system:client:query',        '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1112', '客户端管理新增', '111', '2',  '#', '', '', 1, 0, 'F', '0', '0', 'system:client:add',          '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1113', '客户端管理修改', '111', '3',  '#', '', '', 1, 0, 'F', '0', '0', 'system:client:edit',         '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1114', '客户端管理删除', '111', '4',  '#', '', '', 1, 0, 'F', '0', '0', 'system:client:remove',       '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('1115', '客户端管理导出', '111', '5',  '#', '', '', 1, 0, 'F', '0', '0', 'system:client:export',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '1111');
insert into sys_role_menu values ('2', '1112');
insert into sys_role_menu values ('2', '1113');
insert into sys_role_menu values ('2', '1114');
insert into sys_role_menu values ('2', '1115');

-- 租户管理 -- 二级菜单

-- 租户管理
insert into sys_menu values('201', '租户管理',      '2',   '1', 'tenant',           'system/tenant/index',          '', 1, 0, 'C', '0', '0', 'system:tenant:list',          'list',          103, 1, sysdate(), null, null, '租户管理菜单');
insert into sys_role_menu values ('2', '201');
-- 租户管理相关按钮
insert into sys_menu values ('2011', '租户查询', '201', '1', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:query',   '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('2012', '租户新增', '201', '2', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:add',     '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('2013', '租户修改', '201', '3', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:edit',    '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('2014', '租户删除', '201', '4', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:remove',  '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('2015', '租户导出', '201', '5', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:export',  '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '2011');
insert into sys_role_menu values ('2', '2012');
insert into sys_role_menu values ('2', '2013');
insert into sys_role_menu values ('2', '2014');
insert into sys_role_menu values ('2', '2015');

-- 租户套餐管理
insert into sys_menu values('202', '租户套餐管理',   '2',   '2', 'tenantPackage',    'system/tenantPackage/index',   '', 1, 0, 'C', '0', '0', 'system:tenantPackage:list',   'form',          103, 1, sysdate(), null, null, '租户套餐管理菜单');
insert into sys_role_menu values ('2', '202');
-- 租户套餐管理相关按钮
insert into sys_menu values ('2021', '租户套餐查询', '202', '1', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:query',   '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('2022', '租户套餐新增', '202', '2', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:add',     '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('2023', '租户套餐修改', '202', '3', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:edit',    '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('2024', '租户套餐删除', '202', '4', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:remove',  '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('2025', '租户套餐导出', '202', '5', '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:export',  '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '2021');
insert into sys_role_menu values ('2', '2022');
insert into sys_role_menu values ('2', '2023');
insert into sys_role_menu values ('2', '2024');
insert into sys_role_menu values ('2', '2025');

-- 多数据源管理
insert into sys_menu values('203', '多数据源管理',   '2',   '3', 'datasource',    'system/datasource/index',   '', 1, 0, 'C', '0', '0', 'system:datasource:list',   'datasource',          103, 1, sysdate(), null, null, '多数据源管理菜单');
insert into sys_role_menu values ('2', '203');
-- 多数据源管理相关按钮
insert into sys_menu values ('2031', '多数据源查询', '203', '1', '#', '', '', 1, 0, 'F', '0', '0', 'system:datasource:query',   '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('2032', '多数据源新增', '203', '2', '#', '', '', 1, 0, 'F', '0', '0', 'system:datasource:add',     '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('2033', '多数据源修改', '203', '3', '#', '', '', 1, 0, 'F', '0', '0', 'system:datasource:edit',    '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('2034', '多数据源删除', '203', '4', '#', '', '', 1, 0, 'F', '0', '0', 'system:datasource:remove',  '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values ('2035', '多数据源导出', '203', '5', '#', '', '', 1, 0, 'F', '0', '0', 'system:datasource:export',  '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '2031');
insert into sys_role_menu values ('2', '2032');
insert into sys_role_menu values ('2', '2033');
insert into sys_role_menu values ('2', '2034');
insert into sys_role_menu values ('2', '2035');

-- 系统监控 -- 二级菜单

-- 在线用户
insert into sys_menu values('301',  '在线用户',        '3',    '1', 'online',           'monitor/online/index',        '', 1, 0, 'C', '0', '0', 'monitor:online:list',         'online',        103, 1, sysdate(), null, null, '在线用户菜单');
insert into sys_role_menu values ('2', '301');

-- 在线用户按钮
insert into sys_menu values('3011', '在线查询', '301', '1', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query',       '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('3012', '批量强退', '301', '2', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('3013', '单条强退', '301', '3', '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 103, 1, sysdate(), null, null, '');
insert into sys_role_menu values ('2', '3011');
insert into sys_role_menu values ('2', '3012');
insert into sys_role_menu values ('2', '3013');

-- 接口文档
insert into sys_menu values('302',  '接口文档',        '3',    '2', 'knife4j',           'monitor/knife4j/index',      '', 1, 0, 'C', '0', '0', 'monitor:knife4j:list',        'knife4j',      103, 1, sysdate(), null, null, '接口文档菜单');
insert into sys_role_menu values ('2', '302');

-- 缓存监控
insert into sys_menu values('303',  '缓存监控',        '3',    '3',  'cache',              'monitor/cache/index',      '', 1, 0, 'C', '0', '0', 'monitor:cache:list',           'redis',         103, 1, sysdate(), null, null, '缓存监控');
insert into sys_role_menu values ('2', '303');

-- 数据库文档
insert into sys_menu values('304',  '数据库文档',       '3',    '4', 'dbDoc',           'monitor/dbDoc/index',      '', 1, 0, 'C', '0', '0', 'monitor:dbDoc:list',            'dbDoc',      103, 1, sysdate(), null, null, '数据库文档');
insert into sys_role_menu values ('2', '304');

-- 中间件控制台
insert into sys_menu values('305',  'PowerJob控制台',  '3',    '5', 'http://localhost:7700',        '',                '', 0, 0, 'C', '0', '0', 'monitor:job:list',             'job',           103, 1, sysdate(), null, null, '定时任务菜单');
insert into sys_menu values('306',  'Sentinel控制台',  '3',    '6', 'http://localhost:8718',        '',                '', 0, 0, 'C', '0', '0', 'monitor:sentinel:list',        'sentinel',      103, 1, sysdate(), null, null, '流量控制菜单');
insert into sys_menu values('307',  'Nacos控制台',     '3',    '7', 'http://139.196.208.53:8848/nacos',  '',           '', 0, 0, 'C', '0', '0', 'monitor:nacos:list',           'nacos',         103, 1, sysdate(), null, null, '服务治理菜单');
insert into sys_menu values('308',  'Apollo配置中心',  '3',    '8', 'http://139.196.208.53:8070',  '',                 '', 0, 0, 'C', '0', '0', 'monitor:apollo:list',          'apollo',         103, 1, sysdate(), null, null, '服务配置菜单');
insert into sys_menu values('309',  'RocketMQ控制台',  '3',    '9', 'http://139.196.208.53:18888',  '',                '', 0, 0, 'C', '0', '0', 'monitor:rocketmq:list',        'rocketmq',       103, 1, sysdate(), null, null, '服务RocketMQ菜单');
insert into sys_role_menu values ('2', '305');
insert into sys_role_menu values ('2', '306');
insert into sys_role_menu values ('2', '307');
insert into sys_role_menu values ('2', '308');
insert into sys_role_menu values ('2', '309');

-- 系统工具 -- 二级菜单

-- 代码生成
insert into sys_menu values('401',  '代码生成',     '4',   '1', 'gen',              'tool/gen/index',               '', 1, 0, 'C', '0', '0', 'tool:gen:list',               'code',          103, 1, sysdate(), null, null, '代码生成菜单');
insert into sys_role_menu values ('2', '401');
-- 代码生成按钮
insert into sys_menu values('4011', '生成查询', '401', '1', '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query',             '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('4012', '生成修改', '401', '2', '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit',              '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('4013', '生成删除', '401', '3', '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('4014', '导入代码', '401', '4', '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import',            '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('4015', '预览代码', '401', '5', '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview',           '#', 103, 1, sysdate(), null, null, '');
insert into sys_menu values('4016', '生成代码', '401', '6', '#', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code',              '#', 103, 1, sysdate(), null, null, '');

insert into sys_role_menu values ('2', '4011');
insert into sys_role_menu values ('2', '4012');
insert into sys_role_menu values ('2', '4013');
insert into sys_role_menu values ('2', '4014');
insert into sys_role_menu values ('2', '4015');
insert into sys_role_menu values ('2', '4016');

-- ----------------------------
-- 8、角色和部门关联表  角色1-N部门
-- ----------------------------
drop table if exists sys_role_dept;
create table sys_role_dept (
  role_id   bigint(20) not null comment '角色ID',
  dept_id   bigint(20) not null comment '部门ID',
  primary key(role_id, dept_id)
) engine=innodb comment = '角色和部门关联表';

-- ----------------------------
-- 初始化-角色和部门关联表数据
-- ----------------------------
insert into sys_role_dept values ('2', '100');
insert into sys_role_dept values ('2', '101');
insert into sys_role_dept values ('2', '105');

-- ----------------------------
-- 9、用户与岗位关联表  用户1-N岗位
-- ----------------------------
drop table if exists sys_user_post;
create table sys_user_post
(
  user_id   bigint(20) not null comment '用户ID',
  post_id   bigint(20) not null comment '岗位ID',
  primary key (user_id, post_id)
) engine=innodb comment = '用户与岗位关联表';

-- ----------------------------
-- 初始化-用户与岗位关联表数据
-- ----------------------------
insert into sys_user_post values ('1', '1');
insert into sys_user_post values ('2', '2');


-- ----------------------------
-- 10、操作日志记录
-- ----------------------------
drop table if exists sys_oper_log;
create table sys_oper_log (
  oper_id           bigint(20)      not null                   comment '日志主键',
  tenant_id         varchar(20)     default '000000'           comment '租户编号',
  title             varchar(50)     default ''                 comment '模块标题',
  business_type     int(2)          default 0                  comment '业务类型（0其它 1新增 2修改 3删除）',
  method            varchar(100)    default ''                 comment '方法名称',
  request_method    varchar(10)     default ''                 comment '请求方式',
  operator_type     int(1)          default 0                  comment '操作类别（0其它 1后台用户 2手机端用户）',
  oper_name         varchar(50)     default ''                 comment '操作人员',
  dept_name         varchar(50)     default ''                 comment '部门名称',
  oper_url          varchar(255)    default ''                 comment '请求URL',
  oper_ip           varchar(128)    default ''                 comment '主机地址',
  oper_location     varchar(255)    default ''                 comment '操作地点',
  oper_param        varchar(2000)   default ''                 comment '请求参数',
  json_result       varchar(2000)   default ''                 comment '返回参数',
  status            int(1)          default 0                  comment '操作状态（0正常 1异常）',
  error_msg         varchar(2000)   default ''                 comment '错误消息',
  oper_time         datetime                                   comment '操作时间',
  cost_time         bigint(20)      default 0                  comment '消耗时间',
  primary key (oper_id),
  key idx_sys_oper_log_bt (business_type),
  key idx_sys_oper_log_s  (status),
  key idx_sys_oper_log_ot (oper_time)
) engine=innodb comment = '操作日志记录';


-- ----------------------------
-- 11、字典类型表
-- ----------------------------
drop table if exists sys_dict_type;
create table sys_dict_type
(
  dict_id          bigint(20)      not null                   comment '字典主键',
  tenant_id        varchar(20)     default '000000'           comment '租户编号',
  dict_name        varchar(100)    default ''                 comment '字典名称',
  dict_type        varchar(100)    default ''                 comment '字典类型',
  create_dept      bigint(20)      default null               comment '创建部门',
  create_by        bigint(20)      default null               comment '创建者',
  create_time      datetime                                   comment '创建时间',
  update_by        bigint(20)      default null               comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注',
  primary key (dict_id),
  unique (tenant_id, dict_type)
) engine=innodb comment = '字典类型表';

insert into sys_dict_type values(1, '000000', '用户性别', 'sys_user_sex',        103, 1, sysdate(), null, null, '用户性别列表');
insert into sys_dict_type values(2, '000000', '菜单状态', 'sys_show_hide',       103, 1, sysdate(), null, null, '菜单状态列表');
insert into sys_dict_type values(3, '000000', '系统开关', 'sys_normal_disable',  103, 1, sysdate(), null, null, '系统开关列表');
insert into sys_dict_type values(6, '000000', '系统是否', 'sys_yes_no',          103, 1, sysdate(), null, null, '系统是否列表');
insert into sys_dict_type values(7, '000000', '通知类型', 'sys_notice_type',     103, 1, sysdate(), null, null, '通知类型列表');
insert into sys_dict_type values(8, '000000', '通知状态', 'sys_notice_status',   103, 1, sysdate(), null, null, '通知状态列表');
insert into sys_dict_type values(9, '000000', '操作类型', 'sys_oper_type',       103, 1, sysdate(), null, null, '操作类型列表');
insert into sys_dict_type values(10, '000000', '系统状态', 'sys_common_status',   103, 1, sysdate(), null, null, '登录状态列表');
insert into sys_dict_type values(11, '000000', '授权类型', 'sys_grant_type',     103, 1, sysdate(), null, null, '认证授权类型');
insert into sys_dict_type values(12, '000000', '设备类型', 'sys_device_type',    103, 1, sysdate(), null, null, '客户端设备类型');


-- ----------------------------
-- 12、字典数据表
-- ----------------------------
drop table if exists sys_dict_data;
create table sys_dict_data
(
  dict_code        bigint(20)      not null                   comment '字典编码',
  tenant_id        varchar(20)     default '000000'           comment '租户编号',
  dict_sort        int(4)          default 0                  comment '字典排序',
  dict_label       varchar(100)    default ''                 comment '字典标签',
  dict_value       varchar(100)    default ''                 comment '字典键值',
  dict_type        varchar(100)    default ''                 comment '字典类型',
  css_class        varchar(100)    default null               comment '样式属性（其他样式扩展）',
  list_class       varchar(100)    default null               comment '表格回显样式',
  is_default       char(1)         default 'N'                comment '是否默认（Y是 N否）',
  create_dept      bigint(20)      default null               comment '创建部门',
  create_by        bigint(20)      default null               comment '创建者',
  create_time      datetime                                   comment '创建时间',
  update_by        bigint(20)      default null               comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注',
  primary key (dict_code)
) engine=innodb comment = '字典数据表';

insert into sys_dict_data values(1, '000000', 1,  '男',       '0',       'sys_user_sex',        '',   '',        'Y', 103, 1, sysdate(), null, null, '性别男');
insert into sys_dict_data values(2, '000000', 2,  '女',       '1',       'sys_user_sex',        '',   '',        'N', 103, 1, sysdate(), null, null, '性别女');
insert into sys_dict_data values(3, '000000', 3,  '未知',     '2',       'sys_user_sex',        '',   '',        'N', 103, 1, sysdate(), null, null, '性别未知');
insert into sys_dict_data values(4, '000000', 1,  '显示',     '0',       'sys_show_hide',       '',   'primary', 'Y', 103, 1, sysdate(), null, null, '显示菜单');
insert into sys_dict_data values(5, '000000', 2,  '隐藏',     '1',       'sys_show_hide',       '',   'danger',  'N', 103, 1, sysdate(), null, null, '隐藏菜单');
insert into sys_dict_data values(6, '000000', 1,  '正常',     '0',       'sys_normal_disable',  '',   'primary', 'Y', 103, 1, sysdate(), null, null, '正常状态');
insert into sys_dict_data values(7, '000000', 2,  '停用',     '1',       'sys_normal_disable',  '',   'danger',  'N', 103, 1, sysdate(), null, null, '停用状态');
insert into sys_dict_data values(12, '000000', 1,  '是',       'Y',       'sys_yes_no',          '',   'primary', 'Y', 103, 1, sysdate(), null, null, '系统默认是');
insert into sys_dict_data values(13, '000000', 2,  '否',       'N',       'sys_yes_no',          '',   'danger',  'N', 103, 1, sysdate(), null, null, '系统默认否');
insert into sys_dict_data values(14, '000000', 1,  '通知',     '1',       'sys_notice_type',     '',   'warning', 'Y', 103, 1, sysdate(), null, null, '通知');
insert into sys_dict_data values(15, '000000', 2,  '公告',     '2',       'sys_notice_type',     '',   'success', 'N', 103, 1, sysdate(), null, null, '公告');
insert into sys_dict_data values(16, '000000', 1,  '正常',     '0',       'sys_notice_status',   '',   'primary', 'Y', 103, 1, sysdate(), null, null, '正常状态');
insert into sys_dict_data values(17, '000000', 2,  '关闭',     '1',       'sys_notice_status',   '',   'danger',  'N', 103, 1, sysdate(), null, null, '关闭状态');
insert into sys_dict_data values(29, '000000', 99, '其他',     '0',       'sys_oper_type',       '',   'info',    'N', 103, 1, sysdate(), null, null, '其他操作');
insert into sys_dict_data values(18, '000000', 1,  '新增',     '1',       'sys_oper_type',       '',   'info',    'N', 103, 1, sysdate(), null, null, '新增操作');
insert into sys_dict_data values(19, '000000', 2,  '修改',     '2',       'sys_oper_type',       '',   'info',    'N', 103, 1, sysdate(), null, null, '修改操作');
insert into sys_dict_data values(20, '000000', 3,  '删除',     '3',       'sys_oper_type',       '',   'danger',  'N', 103, 1, sysdate(), null, null, '删除操作');
insert into sys_dict_data values(21, '000000', 4,  '授权',     '4',       'sys_oper_type',       '',   'primary', 'N', 103, 1, sysdate(), null, null, '授权操作');
insert into sys_dict_data values(22, '000000', 5,  '导出',     '5',       'sys_oper_type',       '',   'warning', 'N', 103, 1, sysdate(), null, null, '导出操作');
insert into sys_dict_data values(23, '000000', 6,  '导入',     '6',       'sys_oper_type',       '',   'warning', 'N', 103, 1, sysdate(), null, null, '导入操作');
insert into sys_dict_data values(24, '000000', 7,  '强退',     '7',       'sys_oper_type',       '',   'danger',  'N', 103, 1, sysdate(), null, null, '强退操作');
insert into sys_dict_data values(25, '000000', 8,  '生成代码', '8',       'sys_oper_type',       '',   'warning', 'N', 103, 1, sysdate(), null, null, '生成操作');
insert into sys_dict_data values(26, '000000', 9,  '清空数据', '9',       'sys_oper_type',       '',   'danger',  'N', 103, 1, sysdate(), null, null, '清空操作');
insert into sys_dict_data values(27, '000000', 1,  '成功',     '0',       'sys_common_status',   '',   'primary', 'N', 103, 1, sysdate(), null, null, '正常状态');
insert into sys_dict_data values(28, '000000', 2,  '失败',     '1',       'sys_common_status',   '',   'danger',  'N', 103, 1, sysdate(), null, null, '停用状态');
insert into sys_dict_data values(30, '000000', 0,  '密码认证', 'password',   'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, sysdate(), null, null, '密码认证');
insert into sys_dict_data values(31, '000000', 0,  '短信认证', 'sms',        'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, sysdate(), null, null, '短信认证');
insert into sys_dict_data values(32, '000000', 0,  '邮件认证', 'email',      'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, sysdate(), null, null, '邮件认证');
insert into sys_dict_data values(33, '000000', 0,  '小程序认证', 'xcx',      'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, sysdate(), null, null, '小程序认证');
insert into sys_dict_data values(34, '000000', 0,  '三方登录认证', 'social', 'sys_grant_type',   'el-check-tag',   'default', 'N', 103, 1, sysdate(), null, null, '三方登录认证');
insert into sys_dict_data values(35, '000000', 0,  'PC',    'pc',         'sys_device_type',     '',   'default', 'N', 103, 1, sysdate(), null, null, 'PC');
insert into sys_dict_data values(36, '000000', 0,  '安卓', 'android',     'sys_device_type',     '',   'default', 'N', 103, 1, sysdate(), null, null, '安卓');
insert into sys_dict_data values(37, '000000', 0,  'iOS', 'ios',          'sys_device_type',     '',   'default', 'N', 103, 1, sysdate(), null, null, 'iOS');
insert into sys_dict_data values(38, '000000', 0,  '小程序', 'xcx',       'sys_device_type',     '',   'default', 'N', 103, 1, sysdate(), null, null, '小程序');

-- ----------------------------
-- 13、参数配置表
-- ----------------------------
drop table if exists sys_config;
create table sys_config (
  config_id         bigint(20)      not null                   comment '参数主键',
  tenant_id         varchar(20)     default '000000'           comment '租户编号',
  config_name       varchar(100)    default ''                 comment '参数名称',
  config_key        varchar(100)    default ''                 comment '参数键名',
  config_value      varchar(500)    default ''                 comment '参数键值',
  config_type       char(1)         default 'N'                comment '系统内置（Y是 N否）',
  create_dept       bigint(20)      default null               comment '创建部门',
  create_by         bigint(20)      default null               comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         bigint(20)      default null               comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (config_id)
) engine=innodb comment = '参数配置表';

insert into sys_config values(1, '000000', '主框架页-默认皮肤样式名称',     'sys.index.skinName',            'skin-blue',     'Y', 103, 1, sysdate(), null, null, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow' );
insert into sys_config values(2, '000000', '用户管理-账号初始密码',        'sys.user.initPassword',         '123456',        'Y', 103, 1, sysdate(), null, null, '初始化密码 123456' );
insert into sys_config values(3, '000000', '主框架页-侧边栏主题',          'sys.index.sideTheme',           'theme-dark',    'Y', 103, 1, sysdate(), null, null, '深色主题theme-dark，浅色主题theme-light' );
insert into sys_config values(5, '000000', '账号自助-是否开启用户注册功能',  'sys.account.registerUser',      'false',         'Y', 103, 1, sysdate(), null, null, '是否开启注册用户功能（true开启，false关闭）');
insert into sys_config values(11, '000000', 'OSS预览列表资源开关',         'sys.oss.previewListResource',   'true',          'Y', 103, 1, sysdate(), null, null, 'true:开启, false:关闭');


-- ----------------------------
-- 14、系统访问记录
-- ----------------------------
drop table if exists sys_logininfor;
create table sys_logininfor (
  info_id        bigint(20)     not null                  comment '访问ID',
  tenant_id      varchar(20)    default '000000'          comment '租户编号',
  user_name      varchar(50)    default ''                comment '用户账号',
  client_key     varchar(32)    default ''                comment '客户端',
  device_type    varchar(32)    default ''                comment '设备类型',
  ipaddr         varchar(128)   default ''                comment '登录IP地址',
  login_location varchar(255)   default ''                comment '登录地点',
  browser        varchar(50)    default ''                comment '浏览器类型',
  os             varchar(50)    default ''                comment '操作系统',
  status         char(1)        default '0'               comment '登录状态（0成功 1失败）',
  msg            varchar(255)   default ''                comment '提示消息',
  login_time     datetime                                 comment '访问时间',
  primary key (info_id),
  key idx_sys_logininfor_s  (status),
  key idx_sys_logininfor_lt (login_time)
) engine=innodb comment = '系统访问记录';


-- ----------------------------
-- 17、通知公告表
-- ----------------------------
drop table if exists sys_notice;
create table sys_notice (
  notice_id         bigint(20)      not null                   comment '公告ID',
  tenant_id         varchar(20)     default '000000'           comment '租户编号',
  notice_title      varchar(50)     not null                   comment '公告标题',
  notice_type       char(1)         not null                   comment '公告类型（1通知 2公告）',
  notice_content    longblob        default null               comment '公告内容',
  status            char(1)         default '0'                comment '公告状态（0正常 1关闭）',
  create_dept       bigint(20)      default null               comment '创建部门',
  create_by         bigint(20)      default null               comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         bigint(20)      default null               comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(255)    default null               comment '备注',
  primary key (notice_id)
) engine=innodb comment = '通知公告表';

-- ----------------------------
-- 初始化-公告信息表数据
-- ----------------------------
insert into sys_notice values('1', '000000', '温馨提醒：2018-07-01 新版本发布啦', '2', '新版本内容', '0', 103, 1, sysdate(), null, null, '管理员');
insert into sys_notice values('2', '000000', '维护通知：2018-07-01 系统凌晨维护', '1', '维护内容',   '0', 103, 1, sysdate(), null, null, '管理员');


-- ----------------------------
-- 18、代码生成业务表
-- ----------------------------
drop table if exists gen_table;
create table gen_table (
  table_id          bigint(20)      not null                   comment '编号',
  data_name         varchar(200)    default ''                 comment '数据源名称',
  table_name        varchar(200)    default ''                 comment '表名称',
  table_comment     varchar(500)    default ''                 comment '表描述',
  sub_table_name    varchar(64)     default null               comment '关联子表的表名',
  sub_table_fk_name varchar(64)     default null               comment '子表关联的外键名',
  class_name        varchar(100)    default ''                 comment '实体类名称',
  tpl_category      varchar(200)    default 'crud'             comment '使用的模板（crud单表操作 tree树表操作）',
  package_name      varchar(100)                               comment '生成包路径',
  module_name       varchar(30)                                comment '生成模块名',
  business_name     varchar(30)                                comment '生成业务名',
  function_name     varchar(50)                                comment '生成功能名',
  function_author   varchar(50)                                comment '生成功能作者',
  gen_type          char(1)         default '0'                comment '生成代码方式（0zip压缩包 1自定义路径）',
  gen_path          varchar(200)    default '/'                comment '生成路径（不填默认项目路径）',
  options           varchar(1000)                              comment '其它生成选项',
  create_dept       bigint(20)      default null               comment '创建部门',
  create_by         bigint(20)      default null               comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         bigint(20)      default null               comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (table_id)
) engine=innodb comment = '代码生成业务表';


-- ----------------------------
-- 19、代码生成业务表字段
-- ----------------------------
drop table if exists gen_table_column;
create table gen_table_column (
  column_id         bigint(20)      not null                   comment '编号',
  table_id          bigint(20)                                 comment '归属表编号',
  column_name       varchar(200)                               comment '列名称',
  column_comment    varchar(500)                               comment '列描述',
  column_type       varchar(100)                               comment '列类型',
  java_type         varchar(500)                               comment 'JAVA类型',
  java_field        varchar(200)                               comment 'JAVA字段名',
  is_pk             char(1)                                    comment '是否主键（1是）',
  is_increment      char(1)                                    comment '是否自增（1是）',
  is_required       char(1)                                    comment '是否必填（1是）',
  is_insert         char(1)                                    comment '是否为插入字段（1是）',
  is_edit           char(1)                                    comment '是否编辑字段（1是）',
  is_list           char(1)                                    comment '是否列表字段（1是）',
  is_query          char(1)                                    comment '是否查询字段（1是）',
  query_type        varchar(200)    default 'EQ'               comment '查询方式（等于、不等于、大于、小于、范围）',
  html_type         varchar(200)                               comment '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  dict_type         varchar(200)    default ''                 comment '字典类型',
  sort              int                                        comment '排序',
  create_dept       bigint(20)      default null               comment '创建部门',
  create_by         bigint(20)      default null               comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         bigint(20)      default null               comment '更新者',
  update_time       datetime                                   comment '更新时间',
  primary key (column_id)
) engine=innodb comment = '代码生成业务表字段';

-- ----------------------------
-- OSS对象存储表
-- ----------------------------
drop table if exists sys_oss;
create table sys_oss (
  oss_id          bigint(20)   not null                   comment '对象存储主键',
  tenant_id       varchar(20)           default '000000'  comment '租户编号',
  file_name       varchar(255) not null default ''        comment '文件名',
  original_name   varchar(255) not null default ''        comment '原名',
  file_suffix     varchar(10)  not null default ''        comment '文件后缀名',
  url             varchar(500) not null                   comment 'URL地址',
  create_dept     bigint(20)            default null      comment '创建部门',
  create_time     datetime              default null      comment '创建时间',
  create_by       bigint(20)            default null      comment '上传人',
  update_time     datetime              default null      comment '更新时间',
  update_by       bigint(20)            default null      comment '更新人',
  service         varchar(20)  not null default 'minio'   comment '服务商',
  primary key (oss_id)
) engine=innodb comment ='OSS对象存储表';

-- ----------------------------
-- OSS对象存储动态配置表
-- ----------------------------
drop table if exists sys_oss_config;
create table sys_oss_config (
  oss_config_id   bigint(20)    not null                  comment '主建',
  tenant_id       varchar(20)             default '000000'comment '租户编号',
  config_key      varchar(20)   not null  default ''      comment '配置key',
  access_key      varchar(255)            default ''      comment 'accessKey',
  secret_key      varchar(255)            default ''      comment '秘钥',
  bucket_name     varchar(255)            default ''      comment '桶名称',
  prefix          varchar(255)            default ''      comment '前缀',
  endpoint        varchar(255)            default ''      comment '访问站点',
  domain          varchar(255)            default ''      comment '自定义域名',
  is_https        char(1)                 default 'N'     comment '是否https（Y=是,N=否）',
  region          varchar(255)            default ''      comment '域',
  access_policy   char(1)       not null  default '1'     comment '桶权限类型(0=private 1=public 2=custom)',
  status          char(1)                 default '1'     comment '是否默认（0=是,1=否）',
  ext1            varchar(255)            default ''      comment '扩展字段',
  create_dept     bigint(20)              default null    comment '创建部门',
  create_by       bigint(20)              default null    comment '创建者',
  create_time     datetime                default null    comment '创建时间',
  update_by       bigint(20)              default null    comment '更新者',
  update_time     datetime                default null    comment '更新时间',
  remark          varchar(500)            default null    comment '备注',
  primary key (oss_config_id)
) engine=innodb comment='对象存储配置表';

insert into sys_oss_config values (1, '000000', 'minio', 'zhoushuai',    'Zs11195310',      'ruoyi',     '', '139.196.208.53:9001',    '', 'N', '',  '1', '0', '', 103, 1, sysdate(), 1, sysdate(), NULL);
-- ----------------------------
-- 系统授权表
-- ----------------------------
drop table if exists sys_client;
create table sys_client (
    id                  bigint(20)    not null            comment 'id',
    client_id           varchar(64)   default null        comment '客户端id',
    client_key          varchar(32)   default null        comment '客户端key',
    client_secret       varchar(255)  default null        comment '客户端秘钥',
    grant_type          varchar(255)  default null        comment '授权类型',
    device_type         varchar(32)   default null        comment '设备类型',
    active_timeout      int(11)       default 1800        comment 'token活跃超时时间',
    timeout             int(11)       default 604800      comment 'token固定超时',
    status              char(1)       default '0'         comment '状态（0正常 1停用）',
    del_flag            char(1)       default '0'         comment '删除标志（0代表存在 2代表删除）',
    create_dept         bigint(20)    default null        comment '创建部门',
    create_by           bigint(20)    default null        comment '创建者',
    create_time         datetime      default null        comment '创建时间',
    update_by           bigint(20)    default null        comment '更新者',
    update_time         datetime      default null        comment '更新时间',
    primary key (id)
) engine=innodb comment='系统授权表';

insert into sys_client values (1, 'e5cd7e4891bf95d1d19206ce24a7b32e', 'pc', 'pc123', 'password,social', 'pc', 1800, 604800, 0, 0, 103, 1, sysdate(), 1, sysdate());
insert into sys_client values (2, '428a8310cd442757ae699df5d894f051', 'app', 'app123', 'password,sms,social', 'android', 1800, 604800, 0, 0, 103, 1, sysdate(), 1, sysdate());

-- ----------------------------
-- Seata测试表
-- ----------------------------
drop table if exists res_seata_test;
CREATE TABLE res_seata_test (
    `id`         bigint unsigned      NOT NULL                    COMMENT '主键自增ID',
    `tenant_id`  varchar(20)          DEFAULT      '000000'       COMMENT '租户编号',
    `value`      varchar(100)         NOT NULL                    COMMENT '值',
    primary key (id)
) ENGINE=InnoDB comment='资源Seata测试表';

drop table if exists sys_seata_test;
CREATE TABLE sys_seata_test (
     `id`         bigint unsigned      NOT NULL                    COMMENT '主键自增ID',
     `tenant_id`  varchar(20)          DEFAULT     '000000'        COMMENT '租户编号',
     `value`      varchar(100)         NOT NULL                    COMMENT '值',
     primary key (id)
) ENGINE=InnoDB comment='系统Seata测试表';

-- for AT mode you must to init this sql for you business database. the seata server not need it.
CREATE TABLE IF NOT EXISTS undo_log
(
    branch_id     BIGINT(20)   NOT NULL COMMENT 'branch transaction id',
    xid           VARCHAR(100) NOT NULL COMMENT 'global transaction id',
    context       VARCHAR(128) NOT NULL COMMENT 'undo_log context,such as serialization',
    rollback_info LONGBLOB     NOT NULL COMMENT 'rollback info',
    log_status    INT(11)      NOT NULL COMMENT '0:normal status,1:defense status',
    log_created   DATETIME(6)  NOT NULL COMMENT 'create datetime',
    log_modified  DATETIME(6)  NOT NULL COMMENT 'modify datetime',
    UNIQUE KEY ux_undo_log (xid, branch_id)
) ENGINE = InnoDB COMMENT ='AT transaction mode undo table';


-- ----------------------------
-- 多数据源配置表
-- ----------------------------
drop table if exists sys_datasource_config;
CREATE TABLE sys_datasource_config (
    `id`                    bigint         NOT NULL        COMMENT '主键ID',
    `tenant_id`           varchar(20)      NOT NULL        COMMENT '租户id',
    `name`                varchar(30)      NOT NULL        COMMENT '数据源名称',
    `driver_class_name`   varchar(50)     NOT NULL        COMMENT '数据库驱动',
    `url`                 varchar(300)     NOT NULL        COMMENT '数据库连接地址',
    `user_name`           varchar(20)      NOT NULL        COMMENT '数据库用户名',
    `password`            varchar(100)     NOT NULL        COMMENT '数据库密码',
    `create_dept`           bigint         DEFAULT NULL    COMMENT '创建部门',
    `create_time`          datetime        DEFAULT NULL    COMMENT '创建时间',
    `create_by`             bigint         DEFAULT NULL    COMMENT '创建者',
    `update_time`          datetime        DEFAULT NULL    COMMENT '更新时间',
    `update_by`             bigint         DEFAULT NULL    COMMENT '更新者',
    primary key (`id`),
    UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB COMMENT='多数据源配置表';
