package rebue.ibr.svc;

import rebue.ibr.to.MatchTo;
import rebue.robotech.ro.Ro;

/**
 * 匹配相关的接口
 */
public interface IbrMatchSvc {
    /**
     * 匹配(订单支付完成后，匹配订单详情到它的上家)
     */
    Ro match(MatchTo to);
}
