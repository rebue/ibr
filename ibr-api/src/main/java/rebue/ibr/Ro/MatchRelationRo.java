package rebue.ibr.Ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import rebue.ibr.dic.MatchSchemeDic;
import rebue.ibr.mo.IbrBuyRelationMo;

/**
 * 匹配购买关系返回类
 * 
 * 需要返回
 * 1：匹配到的父节点
 * 2：是否是首单
 * 3：匹配到的模式
 * 4:匹配的结果
 * 
 * @author xym
 *
 */
@JsonInclude(Include.NON_NULL)
@Data
public class MatchRelationRo {

    /**
     * 匹配到的父节点
     */
    private IbrBuyRelationMo parentNode;

    /**
     * 是否是首单
     */
    private boolean isFirst;

    /**
     * 匹配到的模式
     */
    private MatchSchemeDic scheme;

    /**
     * 匹配的结果
     */
    private String msg;
}
