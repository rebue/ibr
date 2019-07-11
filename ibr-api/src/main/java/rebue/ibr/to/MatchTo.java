package rebue.ibr.to;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import rebue.ibr.dic.MatchSchemeDic;

@Data
@JsonInclude(Include.NON_NULL)
public class MatchTo {
    /**
     * 匹配方案
     */
    private MatchSchemeDic matchScheme;
    /**
     * 匹配的价格(单位是元)
     */
    private BigDecimal     matchPrice;
    /**
     * 买家ID
     */
    private Long           buyerId;
    /**
     * 收到支付通知时的时间戳
     */
    private Long           paidNotifyTimestamp;
    /**
     * 订单详情ID
     */
    private Long           orderDetailId;
    /**
     * 匹配人ID(指定将要匹配的上家是谁)
     */
    private Long           matchPersonId;
    /**
     * 最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     */
    private Integer        maxChildernCount;
}
