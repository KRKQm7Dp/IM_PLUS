create table user(
  U_ID int primary key auto_increment,
  U_LoginID    Varchar(20) not null unique ,
  U_NickName    Varchar(20) not null,
  U_PassWord    Varchar(255) not null,
  U_SignaTure    Varchar(150),
  U_Sex    Bit,
  U_Birthday    datetime,
  U_Telephone    Varchar(30),
  U_Name    Varchar(30),
  U_Email    Varchar(50),
  U_Intro    Varchar(255),
  U_HeadPortrait     Varchar(255),
  U_ShengXiao    Char(2),
  U_Age    Int,
  U_Constellation    Char(6),
  U_BloodType    Varchar(10),
  U_SchoolTag    Varchar(50),
  U_Vocation    Varchar(30),
  U_NationID    Int,
  U_ProvinceID    Int,
  U_CityID    Int,
  U_FriendshipPolicyID    Int,
  U_UserStateID    Int,
  U_FriendPolicyQuestion    Varchar(30),
  U_FriendPolicyAnswer    Varchar(30),
  U_FriendPolicyPassword    Varchar(30)
)auto_increment = 1;

create table Friends(
  F_ID    Int primary key auto_increment,
  F_FirendID    varchar(20) not null,
  F_UserID    varchar(20) not null,
  F_Name    Varchar(30),
  F_FriendTypeID    Int,
  F_FriendGroupsID    Int
)auto_increment = 1;

create table Messages(
  M_ID    Int primary key auto_increment,
  M_PostMessages  text,
  M_status    Bit,
  M_Time    datetime,
  M_MessagesTypeID    Int,
  M_FromUserID     varchar(20),
  M_ToUserID     varchar(20)
)auto_increment = 1;

