package rebue.ibr.jo;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The persistent class for the IBR_BUY_RELATION database table.
 * @mbg.generated 自动生成，如需修改，请删除本行
 */
@Entity
@Table(name = "IBR_BUY_RELATION")
@Getter
@Setter
@ToString
public class IbrBuyRelationJo implements Serializable {

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    private static final long serialVersionUID = 1L;

    /**
     *  购买关系ID,其实就是本家的订单详情ID
     *
     *  @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Id
    @Basic(optional = false)
    @Column(name = "ID", nullable = false, length = 19)
    private Long id;

    /**
     *  分组ID，按照商品单价来分组，单位是分
     *
     *  @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Basic(optional = false)
    @Column(name = "GROUP_ID", nullable = false, length = 19)
    private Long groupId;

    /**
     *  父节点ID,其实也就是上家的订单详情ID
     *
     *  @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Basic(optional = true)
    @Column(name = "PARENT_ID", nullable = true, length = 19)
    private Long parentId;

    /**
     *  左值
     *
     *  @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Basic(optional = false)
    @Column(name = "LEFT_VALUE", nullable = false, length = 19)
    private Long leftValue;

    /**
     *  右值
     *
     *  @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Basic(optional = false)
    @Column(name = "RIGHT_VALUE", nullable = false, length = 19)
    private Long rightValue;

    /**
     *  买家ID
     *
     *  @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Basic(optional = false)
    @Column(name = "BUYER_ID", nullable = false, length = 19)
    private Long buyerId;

    /**
     *  是否已结算，在该订单结算的时候修改，默认是false
     *
     *  @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Basic(optional = false)
    @Column(name = "IS_SETTLED", nullable = false, length = 3)
    private Boolean isSettled;

    /**
     *  关系来源
     *
     *  @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Basic(optional = true)
    @Column(name = "RELATION_SOURCE", nullable = true, length = 3)
    private Byte relationSource;

    /**
     *  下单时间戳
     *
     *  @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Basic(optional = false)
    @Column(name = "ORDER_TIMESTAMP", nullable = false, length = 19)
    private Long orderTimestamp;

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IbrBuyRelationJo other = (IbrBuyRelationJo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
