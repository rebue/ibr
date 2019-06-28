package rebue.ibr.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import rebue.ibr.mo.IbrBuyRelationTaskMo;
import rebue.robotech.mapper.MybatisBaseMapper;

@Mapper
public interface IbrBuyRelationTaskMapper extends MybatisBaseMapper<IbrBuyRelationTaskMo, Long> {

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int deleteByPrimaryKey(Long id);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int insert(IbrBuyRelationTaskMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int insertSelective(IbrBuyRelationTaskMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    IbrBuyRelationTaskMo selectByPrimaryKey(Long id);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int updateByPrimaryKeySelective(IbrBuyRelationTaskMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int updateByPrimaryKey(IbrBuyRelationTaskMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    List<IbrBuyRelationTaskMo> selectAll();

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    List<IbrBuyRelationTaskMo> selectSelective(IbrBuyRelationTaskMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    boolean existByPrimaryKey(Long id);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    boolean existSelective(IbrBuyRelationTaskMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int countSelective(IbrBuyRelationTaskMo record);
}
