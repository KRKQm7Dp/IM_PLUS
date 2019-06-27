-- IM+ 数据库表设计

create table user(
  U_ID int primary key auto_increment,
  U_LoginID    Varchar(20) not null unique ,     --(登陆账号)
  U_NickName    Varchar(20) not null,    --(昵称)
  U_PassWord    Varchar(255) not null,    --(密码)
  U_SignaTure    Varchar(150),  --(个性签名)    Null
  U_Sex    Bit,                 --(性别: 0 女 1 男)    Null
  U_Birthday    datetime,       --(生日)    Null
  U_Telephone    Varchar(30),   --(电话)    Null
  U_Name    Varchar(30),        --(真实姓名)    Null
  U_Email    Varchar(50),       --(邮箱)    Null
  U_Intro    Varchar(255),      --(简介)    Null
  U_HeadPortrait     Varchar(255),   --(头像)
  U_ShengXiao    Char(2),            --生肖    Null
  U_Age    Int,                      --年龄    Null
  U_Constellation    Char(6),        --星座    Null
  U_BloodType    Varchar(10),        --血型    Null
  U_SchoolTag    Varchar(50),        --毕业学校    Null
  U_Vocation    Varchar(30),         --(职业)    Null
  U_NationID    Int,                 --(国家ID)    外键
  U_ProvinceID    Int,               --（省份ID）    外键
  U_CityID    Int,                   --（城市ID）    外键
  U_FriendshipPolicyID    Int,       --好友策略ID    外键
  U_UserStateID    Int,              --(用户状态ID)    外键
  U_FriendPolicyQuestion    Varchar(30),    --好友策略问题    Null
  U_FriendPolicyAnswer    Varchar(30),      --好友策略答案    Null
  U_FriendPolicyPassword    Varchar(30)     --好友策略密码    Null
)auto_increment = 1;

create table Friends(--好友表
  F_ID    Int primary key auto_increment, --主键ID    主键
  F_FirendID    Int not null,        --朋友的ID    外键
  F_UserID    Int not null,          --自己的ID    外键
  F_Name    Varchar(30),    --备注昵称    Null
  F_FriendTypeID    Int,    --(好友类型)    外键
  F_FriendGroupsID    Int   --(所属分组ID)    外键
)auto_increment = 1;

create table FriendGroups(--好友分组表
  FG_ID    Int primary key auto_increment,    --(分组ID)    主键
  FG_Name    Varchar not null,    --(分组名字)
  FG_UserID    Int    --用户ID    外键
)auto_increment = 1;

create table Messages(--聊天记录表
  M_ID    Int primary key auto_increment,    --(消息ID)    主键，自增
  M_PostMessages  text,                      --(消息内容)
  M_status    Bit,                           --(接收状态)
  M_Time    datetime,                        --(发送时间)    默认值
  M_MessagesTypeID    Int,                   --(消息类型)    外键
  M_FromUserID     Int,                      --(发送者ID)指向用户表    外键
  M_ToUserID     Int,                        --(接收者ID)指向用户表    外键
)auto_increment = 1;


create table User_Groups(--用户群表
  UG_ID    Int primary key auto_increment,    --群ID    主键
  UG_Name    Varchar(30),       --群名称
  UG_CreateTime    datetime,    --创建时间    默认值
  UG_AdminID    Int,            --群主ID（指向用户表） 外键
  UG_ICon    Varchar(255),      --群图标
  UG_Notice    Varchar(255),    --群公告
  UG_Intro    Varchar(255)      --群简介
)auto_increment = 1;

create table User_GroupsToUser(--群用户关联表
  UG_ID    Int primary key auto_increment,   --ID    主键
  UG _UserID    Int,                         --用户ID    外键
  UG _GroupID    Int,                        --群ID    外键
  UG _CreateTime    datetime,                --发送时间    Null
  UG _GroupNick    Varchar(15)               --群内用户昵称    Null
)auto_increment = 1;


create table User_GroupsMSGContent(--群消息内容表
GM _ID    Int primary key auto_increment,  --群消息ID    主键
GM _Content    text,                       --消息内容
GM _FromID    Int,                         --发送者ID
GM _FromUName    Varchar(30),              --发送者昵称
GM _CreateTime    datetime                 --发送时间
)auto_increment = 1;

create table User_GroupsMSGToUser(--群消息关联表
  GM_ID    Int primary key auto_increment,  --ID    主键
  GM _UserID    Int,                        --接收者ID
  GM _GroupMessageID    Int,                --群消息ID    外键
  GM _State    Bit,                         --接收状态
  GM _CreateTime    datetime                --发送时间
)auto_increment = 1;


-- create table User_GroupsMSGUserToUser(--群内私聊消息关联表
--   GM_ID    Int primary  key auto_increment,   --ID    主键
--   GM_FromUserID    Int,                       --发送者ID
--   GM_FromUserName    Varchar(30),             --发送者昵称
--   GM_ToUserID    Int,                         --接收者ID
--   GM_MSGContent    text,                      --消息内容
--   GM_State    Bit,                            --接收状态
--   GM_CreateTime    datetime,                  --发送时间
--   GM_UserGroupID    Int,                      --所属群ID
-- );



-- 以下表为创建好的
create table UserState(--用户状态表
  US_ID    Int  primary key ,--(ID)    主键
  US_Name    Varchar(10)    --(状态名字)
);

create table User_FriendshipPolicy(--添加好友策略
  U_FP_ID主键    Int primary key ,      --策略ID    主键
  U_FriendshipPolicy    varchar(20)     --好友添加方式
);

create table FriendType (--好友类型
  FT_ID    Int primary key ,    --（类型ID）    主键
  FT_Name    Varchar(20)        --(类型名称)
);

create table MessagesType(--消息类型
  MT_ID    Int primary key,             --(类型ID)    主键
  MT_Name    Varchar(20)    --类型名称
);

create table Nation (--国家
  N_ID    Int primary key,--(国家ID)    主键
  N_Name    Varchar(30)   -- (名字)
);

create table Province (--省份
  P_ID    Int primary key , --（省份ID）
  P_Name    Varchar(30),    --(名字)
  P_NationID    Int         --所属国家ID    外键
);

create table City (--城市
  C_ID    Int primary key,  --（城市ID）
  C_Name    Varchar(30),    --(名字)
  C_ProvinceID    Int       -- 所属省份ID    外键
);