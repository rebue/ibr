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
     * 在插入新节点前更新父节点的子节点数+=1
     *
     * @param parentId
     *            父节点ID
     * @param nodeCount
     *            插入的节点数
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     * @return 影响行数
     */
    @//
    Update(//
    "UPDATE IBR_BUY_RELATION  " + //
            "SET  " + //
            "    CHILDREN_COUNT = CHILDREN_COUNT + ${nodeCount} " + "WHERE "
            + "    ID = #{parentId} AND CHILDREN_COUNT < #{maxChildernCount} AND IS_MOVING = FALSE ")
    int updateChildrenCountOfParentBeforeInsertNode(@Param("parentId") Long parentId, @Param("nodeCount") int nodeCount,
            @Param("maxChildernCount") int maxChildernCount);

    /**
     * 在插入节点前更新右值
     *
     * @param groupId
     *            分组ID，其实就是销售价格*100
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
            "    RIGHT_VALUE = RIGHT_VALUE + 2 * ${nodeCount} " + "WHERE "
            + "    GROUP_ID = #{groupId} AND RIGHT_VALUE >= #{parentRightValue}  AND IS_MOVING = FALSE  ORDER BY RIGHT_VALUE DESC")
    int updateRightValueBeforeInsertNode(@Param("groupId") Long groupId, @Param("nodeCount") int nodeCount,
            @Param("parentRightValue") Long parentRightValue);

    /**
     * 在插入节点前更新左值
     *
     * @param groupId
     *            分组ID，其实就是销售价格*100
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
            "    LEFT_VALUE = LEFT_VALUE + 2 * ${nodeCount} " + "WHERE "
            + "    GROUP_ID = #{groupId} AND LEFT_VALUE > #{parentRightValue}  AND IS_MOVING = FALSE  ORDER BY LEFT_VALUE DESC")
    int updateLeftValueBeforeInsertNode(@Param("groupId") Long groupId, @Param("nodeCount") int nodeCount,
            @Param("parentRightValue") Long parentRightValue);

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
            "        AND CHILDREN_COUNT < #{maxChildernCount} AND IS_MOVING = FALSE  "
            + "ORDER BY PAID_NOTIFY_TIMESTAMP " + "LIMIT 1")
    @ResultMap("BaseResultMap")
    IbrBuyRelationMo getEarlestBuyRelationOfBuyer(@Param("groupId") Long groupId, @Param("buyerId") Long buyerId,
            @Param("maxChildernCount") int maxChildernCount);

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
    IbrBuyRelationMo getNotFullAndEarlestBuyRelationOfLatestInviter(@Param("groupId") Long groupId,
            @Param("maxChildernCount") int maxChildernCount, @Param("isMoving") boolean isMoving);

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
            "        AND CHILDREN_COUNT < #{maxChildernCount}   AND IS_MOVING = FALSE   "
            + "ORDER BY PAID_NOTIFY_TIMESTAMP " + "LIMIT 1")
    @ResultMap("BaseResultMap")
    IbrBuyRelationMo getNotFullAndEarlestBuyRelation(@Param("groupId") Long groupId,
            @Param("maxChildernCount") Integer maxChildernCount);

    /**
     * 跟新节点的右值
     *
     * @param groupId
     * @param leftValue
     * @param rightValue
     * @param changeRange
     * @return
     */
    @Update("UPDATE IBR_BUY_RELATION SET RIGHT_VALUE = RIGHT_VALUE - ${changeRange} where RIGHT_VALUE >= #{rightValue} AND IS_MOVING = false  ORDER BY RIGHT_VALUE ${order} ")
    int updateRightValue(@Param("groupId") Long groupId, @Param("rightValue") Long rightValue,
            @Param("changeRange") Long changeRange, @Param("order") String order);

    /**
     * 更新节点的左值
     *
     * @param groupId
     * @param leftValue
     * @param rightValue
     * @param changeRange
     * @return
     */
    @Update("UPDATE IBR_BUY_RELATION SET  LEFT_VALUE =  LEFT_VALUE - ${changeRange}  where LEFT_VALUE > #{leftValue} AND IS_MOVING = false ORDER BY LEFT_VALUE ${order} ")
    int updateLeftValue(@Param("groupId") Long groupId, @Param("leftValue") Long leftValue,
            @Param("changeRange") Long changeRange, @Param("order") String order);

    /**
     * 设置删除节点下的子节点isMoving字段为true
     *
     * @param groupId
     * @param leftValue
     * @param rightValue
     * @return
     */
    @Update("UPDATE  IBR_BUY_RELATION SET IS_MOVING = true  WHERE LEFT_VALUE > #{leftValue} AND RIGHT_VALUE < #{rightValue}  ")
    int updateIsmoving(@Param("groupId") Long groupId, @Param("leftValue") Long leftValue,
            @Param("rightValue") Long rightValue);

    /**
     * 获取正在移动的节点数量
     *
     * @param groupId
     * @param leftValue
     * @param rightValue
     * @return
     */
    @Select("SELECT COUNT(*) FROM   IBR_BUY_RELATION  WHERE LEFT_VALUE >= #{leftValue} AND RIGHT_VALUE <= #{rightValue}  AND IS_MOVING = true ")
    int getMovingNodesCound(@Param("groupId") Long groupId, @Param("leftValue") Long leftValue,
            @Param("rightValue") Long rightValue);

    /**
     * 更新正在移动的节点左右值和移动标示
     * 最后一个参数是为了在调整的时候不出现唯一约束错误
     *
     * @param groupId
     * @param changeRange
     * @param order
     * @return
     */
    @Update("UPDATE  IBR_BUY_RELATION SET IS_MOVING = false ,LEFT_VALUE = LEFT_VALUE + #{changeRange} ,RIGHT_VALUE = RIGHT_VALUE + #{changeRange} WHERE IS_MOVING = true and LEFT_VALUE >= #{leftValue} and RIGHT_VALUE <= #{rightValue} ORDER BY LEFT_VALUE ${order} ")
    int updateMovingRightValueAndLeftValue(@Param("leftValue") Long leftValue, @Param("rightValue") Long rightValue,
            @Param("groupId") Long groupId, @Param("changeRange") Long changeRange, @Param("order") String order);

    /**
     * 删除父节点和关系来源
     * 
     * @param id
     * @return
     */
    @Update("UPDATE  IBR_BUY_RELATION  SET PARENT_ID = null , RELATION_SOURCE = null where  ID = #{id}  ")
    int delateParentIdAndRelationResouce(@Param("id") Long id);
}
