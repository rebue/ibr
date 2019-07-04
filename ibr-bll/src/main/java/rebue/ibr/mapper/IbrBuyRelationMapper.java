package rebue.ibr.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import rebue.ibr.mo.IbrBuyRelationMo;
import rebue.robotech.mapper.MybatisBaseMapper;

@Mapper
public interface IbrBuyRelationMapper extends MybatisBaseMapper<IbrBuyRelationMo, Long> {

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int deleteByPrimaryKey(Long id);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int insert(IbrBuyRelationMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int insertSelective(IbrBuyRelationMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    IbrBuyRelationMo selectByPrimaryKey(Long id);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int updateByPrimaryKeySelective(IbrBuyRelationMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    int updateByPrimaryKey(IbrBuyRelationMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    List<IbrBuyRelationMo> selectAll();

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    List<IbrBuyRelationMo> selectSelective(IbrBuyRelationMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    boolean existByPrimaryKey(Long id);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    boolean existSelective(IbrBuyRelationMo record);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    int countSelective(IbrBuyRelationMo record);

    /**
     * 获取买家的最早购买节点
     * 
     * @return 最早购买记录，如果没有则返回null
     */
    @Select("SELECT " + //
            "    *" + //
            " FROM" + //
            "    ibr.IBR_BUY_RELATION" + //
            " WHERE" + //
            "    GROUP_ID = 990" + //
            " ORDER BY ORDER_TIMESTAMP" + //
            " LIMIT 1")
    IbrBuyRelationMo getEarlestBuyRelationOfBuyer(Long buyerId);
}
