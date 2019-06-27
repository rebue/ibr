package rebue.ibr.mo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.io.Serializable;

/**
 * 邀请关系表
 *
 * 数据库表: IBR_INVITER_RELATION
 *
 * @mbg.generated 自动生成的注释，如需修改本注释，请删除本行
 */
@JsonInclude(Include.NON_NULL)
public class IbrInviterRelationMo implements Serializable {

    /**
     *    邀请关系ID
     *
     *    数据库字段: IBR_INVITER_RELATION.ID
     *
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    private Long id;

    /**
     *    邀请人ID，也就是邀请人的用户ID
     *
     *    数据库字段: IBR_INVITER_RELATION.INVITER_ID
     *
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    private Long inviterId;

    /**
     *    被邀请人ID，也就是被邀请人的用户ID
     *
     *    数据库字段: IBR_INVITER_RELATION.INVITEE_ID
     *
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    private Long inviteeId;

    /**
     *    邀请时间戳
     *
     *    数据库字段: IBR_INVITER_RELATION.INVITE_TIMESTAMP
     *
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    private Long inviteTimestamp;

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    private static final long serialVersionUID = 1L;

    /**
     *    邀请关系ID
     *
     *    数据库字段: IBR_INVITER_RELATION.ID
     *
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    public Long getId() {
        return id;
    }

    /**
     *    邀请关系ID
     *
     *    数据库字段: IBR_INVITER_RELATION.ID
     *
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *    邀请人ID，也就是邀请人的用户ID
     *
     *    数据库字段: IBR_INVITER_RELATION.INVITER_ID
     *
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    public Long getInviterId() {
        return inviterId;
    }

    /**
     *    邀请人ID，也就是邀请人的用户ID
     *
     *    数据库字段: IBR_INVITER_RELATION.INVITER_ID
     *
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    public void setInviterId(Long inviterId) {
        this.inviterId = inviterId;
    }

    /**
     *    被邀请人ID，也就是被邀请人的用户ID
     *
     *    数据库字段: IBR_INVITER_RELATION.INVITEE_ID
     *
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    public Long getInviteeId() {
        return inviteeId;
    }

    /**
     *    被邀请人ID，也就是被邀请人的用户ID
     *
     *    数据库字段: IBR_INVITER_RELATION.INVITEE_ID
     *
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    public void setInviteeId(Long inviteeId) {
        this.inviteeId = inviteeId;
    }

    /**
     *    邀请时间戳
     *
     *    数据库字段: IBR_INVITER_RELATION.INVITE_TIMESTAMP
     *
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    public Long getInviteTimestamp() {
        return inviteTimestamp;
    }

    /**
     *    邀请时间戳
     *
     *    数据库字段: IBR_INVITER_RELATION.INVITE_TIMESTAMP
     *
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    public void setInviteTimestamp(Long inviteTimestamp) {
        this.inviteTimestamp = inviteTimestamp;
    }

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", inviterId=").append(inviterId);
        sb.append(", inviteeId=").append(inviteeId);
        sb.append(", inviteTimestamp=").append(inviteTimestamp);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
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
        IbrInviterRelationMo other = (IbrInviterRelationMo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()));
    }

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }
}
