/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2019/8/1 15:30:38                            */
/*==============================================================*/


drop table if exists IBR_BUY_RELATION;

drop table if exists IBR_BUY_RELATION_TASK;

drop table if exists IBR_INVITE_RELATION;

/*==============================================================*/
/* Table: IBR_BUY_RELATION                                      */
/*==============================================================*/
create table IBR_BUY_RELATION
(
   ID                   bigint not null comment '购买关系ID,其实就是本家的订单详情ID',
   GROUP_ID             bigint not null comment '分组ID，按照商品单价来分组，单位是分',
   PARENT_ID            bigint comment '父节点ID,其实也就是上家的订单详情ID',
   LEFT_VALUE           bigint not null comment '左值',
   RIGHT_VALUE          bigint not null comment '右值',
   CHILDREN_COUNT       tinyint not null default 0 comment '下家数量，默认是零，不能超过2',
   BUYER_ID             bigint not null comment '买家ID(也就是在suc中用户表的id)',
   IS_SETTLED           bool not null default false comment '是否已结算，在该订单结算的时候修改，默认是false',
   RELATION_SOURCE      tinyint comment '关系来源（1：自己匹配自己  2：购买关系  3：注册关系  4：差一人且已有购买关系  5：差两人  6：差一人但没有购买关系 7:自由匹配8：指定人）纪录为空的是根节点',
   PAID_NOTIFY_TIMESTAMP bigint not null comment '收到支付完成时的时间戳',
   IS_MOVING            bool default false comment '默认false 是否移动中，在退款成功后移动节点树的时候true',
   IS_COMMISSION        bool default false comment '是否已返佣',
   primary key (ID),
   unique key AK_GROUP_ID_AND_LEFT_VALUE_AND_IS_MOVING (GROUP_ID, LEFT_VALUE, IS_MOVING),
   unique key AK_GROUP_ID_AND_RIGHT_VALUE_AND_IS_MOVING (GROUP_ID, RIGHT_VALUE, IS_MOVING)
);

alter table IBR_BUY_RELATION comment '购买关系';

/*==============================================================*/
/* Table: IBR_BUY_RELATION_TASK                                 */
/*==============================================================*/
create table IBR_BUY_RELATION_TASK
(
   ID                   bigint not null comment '任务ID',
   EXECUTE_STATE        tinyint not null default 0 comment '执行状态(-1:取消；0:未执行；1:已执行)',
   EXECUTE_PLAN_TIME    datetime not null comment '计划执行时间',
   EXECUTE_FACT_TIME    datetime comment '实际执行时间',
   TASK_TYPE            tinyint not null comment '任务类型（1：匹配购买关系 2：结算返佣金 3:退款成功后重新匹配）',
   ORDER_DETAIL_ID      bigint not null comment '订单详情ID',
   SUB_TASK_TYPE        tinyint default -1 comment '子任务类型',
   primary key (ID),
   unique key AK_TASK_TYPE_AND_ORDER_DETAIL_ID (TASK_TYPE, ORDER_DETAIL_ID)
);

alter table IBR_BUY_RELATION_TASK comment '购买关系任务';

/*==============================================================*/
/* Table: IBR_INVITE_RELATION                                   */
/*==============================================================*/
create table IBR_INVITE_RELATION
(
   ID                   bigint not null comment '邀请关系ID',
   INVITER_ID           bigint not null comment '邀请人ID，也就是邀请人的用户ID',
   INVITEE_ID           bigint not null comment '被邀请人ID，也就是被邀请人的用户ID',
   INVITE_TIMESTAMP     bigint not null comment '邀请时间戳',
   primary key (ID),
   unique key AK_INVITER_AND_INVITEE (INVITER_ID, INVITEE_ID)
);

alter table IBR_INVITE_RELATION comment '邀请关系';

