/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2019/6/26 16:03:53                           */
/*==============================================================*/


drop table if exists IBR_BUY_RELATION;

drop table if exists IBR_INVITER_RELATION;

/*==============================================================*/
/* Table: IBR_BUY_RELATION                                      */
/*==============================================================*/
create table IBR_BUY_RELATION
(
   ID                   bigint not null comment '购买关系ID,其实就是本家的订单详情ID',
   GROUP_ID             bigint not null comment '分组ID，按照商品单价来分组，单位是分',
   P_ID                 bigint not null comment '父节点ID,其实也就是上家的订单详情ID',
   LEFT_VALUE           bigint not null comment '左值',
   RIGHT_VALUE          bigint not null comment '右值',
   RELATION_SOURCE      tinyint not null comment '关系来源（1：自己匹配自己  2：购买关系  3：注册关系  4：差一人且已有购买关系  5：差两人  6：差一人但没有购买关系 7:自由匹配）',
   primary key (ID),
   unique key AK_GROUP_ID_AND_LEFT_VALUE (GROUP_ID, LEFT_VALUE),
   unique key AK_GROUP_ID_AND_RIGHT_VALUE (GROUP_ID, RIGHT_VALUE)
);

alter table IBR_BUY_RELATION comment '购买关系表';

/*==============================================================*/
/* Table: IBR_INVITER_RELATION                                  */
/*==============================================================*/
create table IBR_INVITER_RELATION
(
   ID                   bigint not null comment '邀请关系ID',
   INVITER_ID           bigint not null comment '邀请人ID，也就是邀请人的用户ID',
   INVITEE_ID           bigint not null comment '被邀请人ID，也就是被邀请人的用户ID',
   INVITE_TIMESTAMP     bigint not null comment '邀请时间戳',
   primary key (ID),
   key AK_INVITER_AND_INVITEE (INVITER_ID, INVITEE_ID)
);

alter table IBR_INVITER_RELATION comment '邀请关系表';
