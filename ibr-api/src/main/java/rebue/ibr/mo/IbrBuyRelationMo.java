package rebue.ibr.mo;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

/**
购买关系表

数据库表: IBR_BUY_RELATION

@mbg.generated 自动生成的注释，如需修改本注释，请删除本行
*/
@JsonInclude(Include.NON_NULL)
public class IbrBuyRelationMo implements Serializable {
    /**
    购买关系ID,其实就是本家的订单详情ID
    
    数据库字段: IBR_BUY_RELATION.ID
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    private Long id;

    /**
    分组ID，按照商品单价来分组，单位是分
    
    数据库字段: IBR_BUY_RELATION.GROUP_ID
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    private Long groupId;

    /**
    父节点ID,其实也就是上家的订单详情ID
    
    数据库字段: IBR_BUY_RELATION.P_ID
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    private Long pId;

    /**
    左值
    
    数据库字段: IBR_BUY_RELATION.LEFT_VALUE
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    private Long leftValue;

    /**
    右值
    
    数据库字段: IBR_BUY_RELATION.RIGHT_VALUE
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    private Long rightValue;

    /**
    关系来源（1：自己匹配自己  2：购买关系  3：注册关系  4：差一人且已有购买关系  5：差两人  6：差一人但没有购买关系 7:自由匹配）
    
    数据库字段: IBR_BUY_RELATION.RELATION_SOURCE
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    private Byte relationSource;

    /**
    @mbg.generated 自动生成，如需修改，请删除本行
     */
    private static final long serialVersionUID = 1L;

    /**
    购买关系ID,其实就是本家的订单详情ID
    
    数据库字段: IBR_BUY_RELATION.ID
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    public Long getId() {
        return id;
    }

    /**
    购买关系ID,其实就是本家的订单详情ID
    
    数据库字段: IBR_BUY_RELATION.ID
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    public void setId(Long id) {
        this.id = id;
    }

    /**
    分组ID，按照商品单价来分组，单位是分
    
    数据库字段: IBR_BUY_RELATION.GROUP_ID
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    public Long getGroupId() {
        return groupId;
    }

    /**
    分组ID，按照商品单价来分组，单位是分
    
    数据库字段: IBR_BUY_RELATION.GROUP_ID
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
    父节点ID,其实也就是上家的订单详情ID
    
    数据库字段: IBR_BUY_RELATION.P_ID
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    public Long getpId() {
        return pId;
    }

    /**
    父节点ID,其实也就是上家的订单详情ID
    
    数据库字段: IBR_BUY_RELATION.P_ID
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    public void setpId(Long pId) {
        this.pId = pId;
    }

    /**
    左值
    
    数据库字段: IBR_BUY_RELATION.LEFT_VALUE
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    public Long getLeftValue() {
        return leftValue;
    }

    /**
    左值
    
    数据库字段: IBR_BUY_RELATION.LEFT_VALUE
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    public void setLeftValue(Long leftValue) {
        this.leftValue = leftValue;
    }

    /**
    右值
    
    数据库字段: IBR_BUY_RELATION.RIGHT_VALUE
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    public Long getRightValue() {
        return rightValue;
    }

    /**
    右值
    
    数据库字段: IBR_BUY_RELATION.RIGHT_VALUE
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    public void setRightValue(Long rightValue) {
        this.rightValue = rightValue;
    }

    /**
    关系来源（1：自己匹配自己  2：购买关系  3：注册关系  4：差一人且已有购买关系  5：差两人  6：差一人但没有购买关系 7:自由匹配）
    
    数据库字段: IBR_BUY_RELATION.RELATION_SOURCE
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    public Byte getRelationSource() {
        return relationSource;
    }

    /**
    关系来源（1：自己匹配自己  2：购买关系  3：注册关系  4：差一人且已有购买关系  5：差两人  6：差一人但没有购买关系 7:自由匹配）
    
    数据库字段: IBR_BUY_RELATION.RELATION_SOURCE
    
    @mbg.generated 自动生成，如需修改，请删除本行
    */
    public void setRelationSource(Byte relationSource) {
        this.relationSource = relationSource;
    }

    /**
    @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", groupId=").append(groupId);
        sb.append(", pId=").append(pId);
        sb.append(", leftValue=").append(leftValue);
        sb.append(", rightValue=").append(rightValue);
        sb.append(", relationSource=").append(relationSource);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    /**
    @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        IbrBuyRelationMo other = (IbrBuyRelationMo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
        ;
    }

    /**
    @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }
}