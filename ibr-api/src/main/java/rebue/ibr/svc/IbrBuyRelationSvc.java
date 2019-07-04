package rebue.ibr.svc;

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
     * 获取买家的最早购买节点
     * 
     * @return 最早购买记录，如果没有则返回null
     */
    IbrBuyRelationMo getEarlestBuyRelationOfBuyer(Long buyerId);
}
