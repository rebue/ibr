package rebue.ibr.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import rebue.ibr.mo.IbrBurRelationTaskMo;
import rebue.robotech.mapper.MybatisBaseMapper;

@Mapper
public interface IbrBurRelationTaskMapper extends MybatisBaseMapper<IbrBurRelationTaskMo, Long> {

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int deleteByPrimaryKey(Long id);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int insert(IbrBurRelationTaskMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int insertSelective(IbrBurRelationTaskMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    IbrBurRelationTaskMo selectByPrimaryKey(Long id);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int updateByPrimaryKeySelective(IbrBurRelationTaskMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int updateByPrimaryKey(IbrBurRelationTaskMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    List<IbrBurRelationTaskMo> selectAll();

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    List<IbrBurRelationTaskMo> selectSelective(IbrBurRelationTaskMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    boolean existByPrimaryKey(Long id);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    boolean existSelective(IbrBurRelationTaskMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int countSelective(IbrBurRelationTaskMo record);
}
