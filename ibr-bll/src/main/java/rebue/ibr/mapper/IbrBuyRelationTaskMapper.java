package rebue.ibr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import rebue.ibr.mo.IbrBuyRelationTaskMo;
import rebue.robotech.mapper.MybatisBaseMapper;

@Mapper
public interface IbrBuyRelationTaskMapper extends MybatisBaseMapper<IbrBuyRelationTaskMo, Long> {

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int deleteByPrimaryKey(Long id);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int insert(IbrBuyRelationTaskMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int insertSelective(IbrBuyRelationTaskMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    IbrBuyRelationTaskMo selectByPrimaryKey(Long id);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int updateByPrimaryKeySelective(IbrBuyRelationTaskMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int updateByPrimaryKey(IbrBuyRelationTaskMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    List<IbrBuyRelationTaskMo> selectAll();

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    List<IbrBuyRelationTaskMo> selectSelective(IbrBuyRelationTaskMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    boolean existByPrimaryKey(Long id);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    boolean existSelective(IbrBuyRelationTaskMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    int countSelective(IbrBuyRelationTaskMo record);

    @Select("select ID from IBR_BUY_RELATION_TASK where EXECUTE_STATE= #{executeState} and EXECUTE_PLAN_TIME<=SYSDATE() and TASK_TYPE in(1,3) order by EXECUTE_PLAN_TIME  ")
    List<Long> getTaskIdsThatShouldExecute(@Param("executeState") Byte executeState);

    @Select("select ID from IBR_BUY_RELATION_TASK where EXECUTE_STATE= #{executeState} and EXECUTE_PLAN_TIME<=SYSDATE() and TASK_TYPE in(2) order by EXECUTE_PLAN_TIME  ")
    List<Long> getTaskIdsThatShouldSettleExecute(@Param("executeState") Byte executeState);

}
