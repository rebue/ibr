package rebue.ibr.svc;

import java.util.List;

import rebue.ibr.jo.IbrInviteRelationJo;
import rebue.ibr.mo.IbrInviteRelationMo;
import rebue.robotech.svc.BaseSvc;

/**
 * 邀请关系
 *
 * @mbg.generated 自动生成的注释，如需修改本注释，请删除本行
 */
public interface IbrInviteRelationSvc extends BaseSvc<java.lang.Long, IbrInviteRelationMo, IbrInviteRelationJo> {
    /**
     * 获取买家的所有邀请人ID，并按邀请时间从近到远排序
     * 
     * @param buyerId
     *            买家ID
     * @return 所有邀请人ID列表
     */
    List<Long> listIdsOfBuyer(Long buyerId);

}
