-- 获取买家最早未匹配满的购买节点
alter table IBR_BUY_RELATION add index IDX_MATCH_NOT_FULL_AND_EARLEST_OF_BUYER(GROUP_ID, BUYER_ID, PAID_NOTIFY_TIMESTAMP);

-- 获取买家的所有邀请人ID，并按邀请时间从近到远排序
alter table IBR_INVITE_RELATION add index IDX_INVITERS_OF_BUYER(INVITEE_ID, INVITE_TIMESTAMP DESC);

-- 获取最晚邀请人的最早未匹配满的购买关系记录

-- 获取最早未匹配满的购买关系记录
alter table IBR_BUY_RELATION add index IDX_MATCH_NOT_FULL_AND_EARLEST(GROUP_ID, PAID_NOTIFY_TIMESTAMP);