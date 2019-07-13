package rebue.ibr.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import rebue.ibr.mo.IbrBuyRelationMo;
import rebue.robotech.mapper.MybatisBaseMapper;

@Mapper
public interface IbrBuyRelationMapper extends MybatisBaseMapper<IbrBuyRelationMo, Long> {

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int deleteByPrimaryKey(Long id);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int insert(IbrBuyRelationMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int insertSelective(IbrBuyRelationMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    IbrBuyRelationMo selectByPrimaryKey(Long id);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int updateByPrimaryKeySelective(IbrBuyRelationMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int updateByPrimaryKey(IbrBuyRelationMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    List<IbrBuyRelationMo> selectAll();

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    List<IbrBuyRelationMo> selectSelective(IbrBuyRelationMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    boolean existByPrimaryKey(Long id);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    boolean existSelective(IbrBuyRelationMo record);

    /**
     *    @mbg.generated 自动生成，如需修改，请删除本行
     */
    int countSelective(IbrBuyRelationMo record);

    /**
     * 在插入节点前更新右值
     *
     * @param nodeCount
     *            插入的节点数
     * @param parentRightValue
     *            父结点的右值
     * @return 影响行数
     */
    @// 
    Update(// 
    "UPDATE IBR_BUY_RELATION  " + // 
    "SET  " + // 
    "    RIGHT_VALUE = RIGHT_VALUE + 2 * ${nodeCount} " + "WHERE " + "    RIGHT_VALUE >= #{parentRightValue}")
    int updateRightValueBeforeInsertNode(@Param("nodeCount") int nodeCount, @Param("parentRightValue") Long parentRightValue);

    /**
     * 在插入节点前更新左值
     *
     * @param nodeCount
     *            插入的节点数
     * @param parentRightValue
     *            父结点的右值
     * @return 影响行数
     */
    @// 
    Update(// 
    "UPDATE IBR_BUY_RELATION  " + // 
    "SET  " + // 
    "    LEFT_VALUE = LEFT_VALUE + 2 * ${nodeCount} " + "WHERE " + "    LEFT_VALUE > #{parentRightValue}")
    int updateLeftValueBeforeInsertNode(@Param("nodeCount") int nodeCount, @Param("parentRightValue") Long parentRightValue);

    /**
     * 获取买家最早未匹配满的购买节点
     *
     * @param groupId
     *            分组ID，其实就是销售价格*100
     * @param buyerId
     *            买家ID
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     * @return 最早购买记录，如果没有则返回null
     */
    @// 
    Select(// 
    "SELECT  " + // 
    "    * " + // 
    "FROM " + // 
    "    IBR_BUY_RELATION " + // 
    "WHERE " + // 
    "    GROUP_ID = #{groupId} AND BUYER_ID = #{buyerId} " + // 
    "        AND CHILDREN_COUNT < #{maxChildernCount} " + "ORDER BY PAID_NOTIFY_TIMESTAMP " + "LIMIT 1")
    @ResultMap("BaseResultMap")
    IbrBuyRelationMo getEarlestBuyRelationOfBuyer(@Param("groupId") Long groupId, @Param("buyerId") Long buyerId, @Param("maxChildernCount") Integer maxChildernCount);

    /**
     * 获取最近邀请人的最早未匹配满的购买关系记录
     *
     * @param groupId
     *            分组ID，其实就是销售价格*100
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     * @return 最早购买记录，如果没有则返回null
     */
    @ResultMap("BaseResultMap")
    IbrBuyRelationMo getNotFullAndEarlestBuyRelationOfLatestInviter(@Param("groupId") Long groupId, @Param("maxChildernCount") Integer maxChildernCount);

    /**
     * 获取最早未匹配满的购买关系记录
     *
     * @param groupId
     *            分组ID，其实就是销售价格*100
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     * @return 最早购买记录，如果没有则返回null
     */
    @// 
    Select(// 
    "SELECT  " + // 
    "    * " + // 
    "FROM " + // 
    "    IBR_BUY_RELATION " + // 
    "WHERE " + // 
    "    GROUP_ID =  #{groupId}  " + // 
    "        AND CHILDREN_COUNT < #{maxChildernCount} " + "ORDER BY PAID_NOTIFY_TIMESTAMP " + "LIMIT 1")
    @ResultMap("BaseResultMap")
    IbrBuyRelationMo getNotFullAndEarlestBuyRelation(@Param("groupId") Long groupId, @Param("maxChildernCount") Integer maxChildernCount);
}
