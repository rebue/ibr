package rebue.ibr.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import rebue.ibr.mo.IbrInviterRelationMo;
import rebue.robotech.mapper.MybatisBaseMapper;

@Mapper
public interface IbrInviterRelationMapper extends MybatisBaseMapper<IbrInviterRelationMo, Long> {

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int deleteByPrimaryKey(Long id);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int insert(IbrInviterRelationMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int insertSelective(IbrInviterRelationMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    IbrInviterRelationMo selectByPrimaryKey(Long id);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int updateByPrimaryKeySelective(IbrInviterRelationMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int updateByPrimaryKey(IbrInviterRelationMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    List<IbrInviterRelationMo> selectAll();

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    List<IbrInviterRelationMo> selectSelective(IbrInviterRelationMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    boolean existByPrimaryKey(Long id);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    boolean existSelective(IbrInviterRelationMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int countSelective(IbrInviterRelationMo record);
}
