package rebue.ibr.svc;

import rebue.ibr.dic.RelationSourceDic;
import rebue.ibr.jo.IbrBuyRelationJo;
import rebue.ibr.mo.IbrBuyRelationMo;
import rebue.robotech.svc.BaseSvc;

/**
 * 购买关系
 *
 * @mbg.generated 自动生成的注释，如需修改本注释，请删除本行
 */
public interface IbrBuyRelationSvc extends BaseSvc<java.lang.Long, IbrBuyRelationMo, IbrBuyRelationJo> {

    /**
     * 在指定的父节点下插入新节点
     *
     * @param parent
     *            父节点购买关系
     * @param buyerId
     *            买家ID
     * @param paidNotifyTimestamp
     *            收到支付通知时的时间戳
     * @param relationSource
     *            关系来源
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     */
    void insertNode(IbrBuyRelationMo parent, Long buyerId, Long paidNotifyTimestamp, RelationSourceDic relationSource, Integer maxChildernCount);

    /**
     * 获取买家最早未匹配满的购买节点
     *
     * @param groupId
     *            分组ID，其实就是销售价格*100
     * @param buyerId
     *            买家ID
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     * @return 最早购买记录，如果没有则返回null
     */
    IbrBuyRelationMo getNotFullAndEarlestBuyRelationOfBuyer(Long groupId, Long buyerId, Integer maxChildernCount);

    /**
     * 获取最近邀请人的最早未匹配满的购买关系记录
     *
     * @param groupId
     *            分组ID，其实就是销售价格*100
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     * @return 最早购买记录，如果没有则返回null
     */
    IbrBuyRelationMo getNotFullAndEarlestBuyRelationOfLatestInviter(Long groupId, Integer maxChildernCount);

    /**
     * 获取最早未匹配满的购买关系记录
     *
     * @param groupId
     *            分组ID，其实就是销售价格*100
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     * @return 最早购买记录，如果没有则返回null
     */
    IbrBuyRelationMo getNotFullAndEarlestBuyRelation(Long groupId, Integer maxChildernCount);
}
