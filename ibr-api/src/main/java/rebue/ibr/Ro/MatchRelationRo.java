package rebue.ibr.Ro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import rebue.ibr.dic.RelationSourceDic;
import rebue.ibr.mo.IbrBuyRelationMo;
import rebue.robotech.dic.ResultDic;

/**
 * 匹配购买关系返回类
 * 
 * 需要返回
 * 1：匹配到的父节点
 * 2：是否是首单
 * 3: 匹配的结果
 * 4：关系来源
 * 5：返回值类型
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
     * 匹配的结果
     */
    private String msg;

    /**
     * 关系来源
     */
    private RelationSourceDic source;

    /**
     * 返回值类型
     */
    private ResultDic result;
}
