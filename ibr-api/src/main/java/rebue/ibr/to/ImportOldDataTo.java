package rebue.ibr.to;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import rebue.ibr.dic.RelationSourceDic;

/**
 * 从订单中导入旧的购买关系数据类
 * 
 * @author jjl
 *
 */

@Data
@JsonInclude(Include.NON_NULL)
public class ImportOldDataTo {

    private Long              uplineUserId;
    private Long              downlineUserId;
    private Long              parentNodeId;
    private Long              childrenNodeId;
    private BigDecimal        groupId;
    private Long              payTime;
    private RelationSourceDic relationSource;
    private boolean           isFitst;
    private Boolean           isSettled;
    private Boolean           isCommission;
}
