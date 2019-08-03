package rebue.ibr.svc;

import rebue.ibr.Ro.MatchRelationRo;
import rebue.ibr.to.MatchTo;

/**
 * 匹配相关的接口
 */
public interface IbrMatchSvc {
    /**
     * 匹配(订单支付完成后，匹配订单详情到它的上家)
     */
    MatchRelationRo match(MatchTo to);
}
