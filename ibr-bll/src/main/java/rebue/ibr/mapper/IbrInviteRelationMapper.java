package rebue.ibr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import rebue.ibr.mo.IbrInviteRelationMo;
import rebue.robotech.mapper.MybatisBaseMapper;

@Mapper
public interface IbrInviteRelationMapper extends MybatisBaseMapper<IbrInviteRelationMo, Long> {

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int deleteByPrimaryKey(Long id);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int insert(IbrInviteRelationMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int insertSelective(IbrInviteRelationMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    IbrInviteRelationMo selectByPrimaryKey(Long id);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int updateByPrimaryKeySelective(IbrInviteRelationMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int updateByPrimaryKey(IbrInviteRelationMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    List<IbrInviteRelationMo> selectAll();

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    List<IbrInviteRelationMo> selectSelective(IbrInviteRelationMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    boolean existByPrimaryKey(Long id);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    boolean existSelective(IbrInviteRelationMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    int countSelective(IbrInviteRelationMo record);

    /**
     * 获取买家的所有邀请人ID，并按邀请时间从近到远排序
     *
     * @param buyerId
     *            买家ID
     * @return 所有邀请人ID列表
     */
    @Select("SELECT  " + //
            "    INVITER_ID " + //
            "FROM " + //
            "    IBR_INVITE_RELATION " + //
            "WHERE " + //
            "    INVITEE_ID = #{buyerId} " + //
            "ORDER BY INVITE_TIMESTAMP DESC")
    List<Long> listIdsOfBuyer(@Param("buyerId") Long buyerId);

    @Delete(" delete from IBR_INVITE_RELATION where INVITEE_ID = #{inviteeId} and INVITER_ID = #{inviterId} ")
    int deleteOldRelation(@Param("inviteeId") Long inviteeId, @Param("inviterId") Long inviterId);
}
