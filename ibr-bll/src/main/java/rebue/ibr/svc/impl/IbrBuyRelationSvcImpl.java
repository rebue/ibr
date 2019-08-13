package rebue.ibr.svc.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rebue.ibr.Ro.MatchRelationRo;
import rebue.ibr.dao.IbrBuyRelationDao;
import rebue.ibr.dic.MatchSchemeDic;
import rebue.ibr.dic.RelationSourceDic;
import rebue.ibr.jo.IbrBuyRelationJo;
import rebue.ibr.mapper.IbrBuyRelationMapper;
import rebue.ibr.mo.IbrBuyRelationMo;
import rebue.ibr.svc.IbrBuyRelationSvc;
import rebue.ibr.svc.IbrMatchSvc;
import rebue.ibr.to.ImportOldDataTo;
import rebue.ibr.to.MatchTo;
import rebue.ord.mo.OrdOrderDetailMo;
import rebue.ord.mo.OrdOrderMo;
import rebue.ord.svr.feign.OrdOrderDetailSvc;
import rebue.ord.svr.feign.OrdOrderSvc;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.Ro;
import rebue.robotech.svc.impl.BaseSvcImpl;

/**
 * 购买关系
 *
 * 在单独使用不带任何参数的 @Transactional 注释时，
 * propagation(传播模式)=REQUIRED，readOnly=false，
 * isolation(事务隔离级别)=READ_COMMITTED，
 * 而且事务不会针对受控异常（checked exception）回滚。
 *
 * 注意：
 * 一般是查询的数据库操作，默认设置readOnly=true, propagation=Propagation.SUPPORTS
 * 而涉及到增删改的数据库操作的方法，要设置 readOnly=false, propagation=Propagation.REQUIRED
 *
 * @mbg.generated 自动生成的注释，如需修改本注释，请删除本行
 */
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@Service
public class IbrBuyRelationSvcImpl
        extends BaseSvcImpl<java.lang.Long, IbrBuyRelationJo, IbrBuyRelationDao, IbrBuyRelationMo, IbrBuyRelationMapper>
        implements IbrBuyRelationSvc {

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    private static final Logger _log = LoggerFactory.getLogger(IbrBuyRelationSvcImpl.class);

    /**
     * 该方法已经被修改，没有传id进来将无法添加，因为id就是订单
     * 详情id，按理不可能自动生成
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int add(final IbrBuyRelationMo mo) {
        _log.info("ibrBuyRelationSvc.add: 添加购买关系 mo-", mo);
        // 如果id为空那么自动生成分布式id
        if (mo.getId() == null || mo.getId() == 0) {
            _log.error("添加购买关系失败,id(订单详情id)没有传进来");
            throw new IllegalArgumentException("添加购买关系失败");
        }

        return super.add(mo);
    }

    @Resource
    private IbrBuyRelationSvc selfSvc;

    @Resource
    private OrdOrderDetailSvc ordOrderDetailSvc;

    @Resource
    private IbrMatchSvc ibrMatchSvc;

    @Resource
    private OrdOrderSvc ordOrderSvc;

    /**
     * 在指定的父节点下插入新节点
     *
     * @param parent
     *            父节点购买关系
     * @param buyerId
     *            买家ID
     * @param paidNotifyTimestamp
     *            收到支付通知时的时间戳
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void insertNode(final IbrBuyRelationMo parent, final Long buyerId, final Long currentDetalId,
            final Long paidNotifyTimestamp, final RelationSourceDic relationSource, final Integer maxChildernCount) {
        _log.info(
                "ibrBuyRelationSvc.insertNode: 在指定的父节点下插入新节点 parent-{},buyerId-{},currentDetalId-{},paidNotifyTimestamp-{},relationSource-{},maxChildernCount-{}",
                parent, buyerId, currentDetalId, paidNotifyTimestamp, relationSource, maxChildernCount);
        // 插入节点的左值 = 父节点原来的右值
        final Long insertLeftValue = parent.getRightValue();
        // 插入节点的右值 = 插入节点的左值 + 1
        final Long insertRightValue = insertLeftValue + 1;
        _log.debug("计算插入新节点的左右值: 左值-{}, 右值-{}", insertLeftValue, insertRightValue);
        _log.debug("在插入新节点前更新父节点的子节点数+=1");
        _mapper.updateChildrenCountOfParentBeforeInsertNode(parent.getId(), 1, maxChildernCount);
        _log.debug("在插入新节点前更新这棵树的右值");
        _mapper.updateRightValueBeforeInsertNode(parent.getGroupId(), 1, parent.getRightValue());
        _log.debug("在插入新节点前更新这棵树的左值");
        _mapper.updateLeftValueBeforeInsertNode(parent.getGroupId(), 1, parent.getRightValue());
        // 添加新节点
        final IbrBuyRelationMo mo = new IbrBuyRelationMo();
        mo.setId(currentDetalId);
        mo.setGroupId(parent.getGroupId());
        mo.setBuyerId(buyerId);
        mo.setPaidNotifyTimestamp(paidNotifyTimestamp);
        mo.setParentId(parent.getId());
        mo.setRelationSource((byte) relationSource.getCode());
        mo.setLeftValue(insertLeftValue);
        mo.setRightValue(insertRightValue);
        _log.debug("插入新的节点: {}", mo);
        selfSvc.add(mo);
    }

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
    @Override
    public IbrBuyRelationMo getNotFullAndEarlestBuyRelationOfBuyer(final Long groupId, final Long buyerId,
            final Integer maxChildernCount) {
        _log.info(
                "ibrBuyRelationSvc.getNotFullAndEarlestBuyRelationOfBuyer: 获取买家最早未匹配满的购买节点 groupId-{},buyerId-{},maxChildernCount-{}",
                groupId, buyerId, maxChildernCount);
        return _mapper.getEarlestBuyRelationOfBuyer(groupId, buyerId, maxChildernCount);
    }

    /**
     * 获取最近邀请人的最早未匹配满的购买关系记录
     *
     * @param groupId
     *            分组ID，其实就是销售价格*100
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     * @return 最早购买记录，如果没有则返回null
     */
    @Override
    public IbrBuyRelationMo getNotFullAndEarlestBuyRelationOfLatestInviter(final Long groupId,
            final Integer maxChildernCount) {
        _log.info(
                "ibrBuyRelationSvc.getNotFullAndEarlestBuyRelationOfLatestInviter: 获取最近邀请人的最早未匹配满的购买关系记录 groupId-{},maxChildernCount-{}",
                groupId, maxChildernCount);
        return _mapper.getNotFullAndEarlestBuyRelationOfLatestInviter(groupId, maxChildernCount, false);
    }

    /**
     * 获取最早未匹配满的购买关系记录
     *
     * @param groupId
     *            分组ID，其实就是销售价格*100
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     * @return 最早购买记录，如果没有则返回null
     */
    @Override
    public IbrBuyRelationMo getNotFullAndEarlestBuyRelation(final Long groupId, final Integer maxChildernCount) {
        _log.info("ibrBuyRelationSvc.getNotFullAndEarlestBuyRelation: 获取最早未匹配满的购买关系记录 groupId-{},maxChildernCount-{}",
                groupId, maxChildernCount);
        return _mapper.getNotFullAndEarlestBuyRelation(groupId, maxChildernCount);
    }

    /**
     * 1：先删除(当前删除节点)
     * 2：给删除的节点下面的所有子节点设置IS_MOVING=true的标识
     * 3：整个树大于删除节点的左右值都减去节点数
     * 4：匹配父节点
     * 5：大于父节点的节点的左右值增加节点数
     * 6：调整即将插入节点树的左右值。
     * 提示： 加上排序的字段是因为在调整节点的时候可能会有唯一约束,因此如果是减的，就从左右值小的开始减起，增加的则相反
     */
    @Override
    public Ro executeRefundAgainMatchTask(Long detailId) {
        IbrBuyRelationMo buyRelationResult = super.getById(detailId);
        _log.info("当前节点 currentNode-{}", buyRelationResult);
        if (buyRelationResult == null) {
            final String msg = "关系不存在";
            return new Ro(ResultDic.FAIL, msg);
        }

        _log.info("1:将当前节点的父节点(如果有，首单的话没有父节点)的下家数量减1");
        IbrBuyRelationMo parentResult = super.getById(buyRelationResult.getParentId());
        if (parentResult != null) {
            IbrBuyRelationMo modifyParentChildrenCountMo = new IbrBuyRelationMo();
            modifyParentChildrenCountMo.setId(buyRelationResult.getParentId());
            modifyParentChildrenCountMo.setChildrenCount((byte) (parentResult.getChildrenCount() - 1));
            _log.info("修改父节点下家数量的参数为：Mo-{}", modifyParentChildrenCountMo);
            if (super.modify(modifyParentChildrenCountMo) != 1) {
                final String msg = "修改父节点下家数量失败";
                return new Ro(ResultDic.FAIL, msg);
            }
        }

        _log.info("2:删除(当前删除节点");
        super.del(buyRelationResult.getId());

        _log.info("3:将其子节点都加上标识");
        _mapper.updateIsmoving(buyRelationResult.getGroupId(), buyRelationResult.getLeftValue(),
                buyRelationResult.getRightValue());

        _log.info("4:调整剩下节点的左右值(调整幅度=删除的节点右值-删除节点左值+1)");
        long changeRange = (buyRelationResult.getRightValue() - buyRelationResult.getLeftValue()) + 1;
        _log.info("4-1:更新右值(减去删除的节点数量),更新幅度为 changeRange-{}", changeRange);
        _mapper.updateRightValue(buyRelationResult.getGroupId(), buyRelationResult.getRightValue(), changeRange, "ASC");
        _log.info("4-2:更新左值(减去删除的节点数量),更新幅度为 changeRange-{}", changeRange);
        _mapper.updateLeftValue(buyRelationResult.getGroupId(), buyRelationResult.getLeftValue(),
                buyRelationResult.getRightValue(), changeRange, "ASC");

        IbrBuyRelationMo getchildrenNodesMo = new IbrBuyRelationMo();
        getchildrenNodesMo.setParentId(detailId);
        List<IbrBuyRelationMo> childrenNodes = super.list(getchildrenNodesMo);
        _log.info("子节点childrenNodes-{}", childrenNodes);
        for (IbrBuyRelationMo childrenNode : childrenNodes) {
            _log.info("------------------------------循环插入节点树开始------------------------------");
            // 定义即将插入节点的左值以便后面计算
            long NewLeftValue = 0;
            _log.info("5:匹配父节点");
            _log.info("当前节点 childrenNode-{}", childrenNode);
            OrdOrderDetailMo detailResult = ordOrderDetailSvc.getById(childrenNode.getId());
            MatchTo matchTo = new MatchTo();
            matchTo.setOrderDetailId(detailResult.getId());
            matchTo.setMatchPrice(detailResult.getBuyPrice());
            matchTo.setBuyerId(detailResult.getUserId());
            matchTo.setPaidNotifyTimestamp(childrenNode.getPaidNotifyTimestamp());
            matchTo.setMaxChildernCount(2);
            if (detailResult.getInviteId() != null) {
                matchTo.setMatchPersonId(detailResult.getInviteId());
                matchTo.setMatchScheme(MatchSchemeDic.SPECIFIED_PERSON);
            } else {
                matchTo.setMatchScheme(MatchSchemeDic.SELF);
            }
            _log.info("调用执行匹配的参数为：matchTo-{}", matchTo);
            MatchRelationRo result = ibrMatchSvc.match(matchTo);
            if (result.getResult() != ResultDic.SUCCESS) {
                _log.error("匹配失败");
                throw new IllegalArgumentException("匹配失败");
            }
            _log.info("5-1:更新父节点，来源字段。");
            if (result.getResult() == ResultDic.SUCCESS && result.isFirst()) {
                NewLeftValue = 1l;
                _log.info("匹配结果为首单,去掉当前节点的父节点，来源字段 isFirst-{}", result.isFirst());
                if (_mapper.delateParentIdAndRelationResouce(childrenNode.getId()) != 1) {
                    _log.error("去掉当前节点的父节点，来源字段失败");
                    throw new IllegalArgumentException("去掉当前节点的父节点，来源字段失败");
                }

            } else if (result.getResult() == ResultDic.SUCCESS) {
                NewLeftValue = result.getParentNode().getRightValue();
                _log.info("匹配结果不为首单,修改当前节点的父节点，来源字段 isFirst-{}", result.isFirst());
                IbrBuyRelationMo modifyMo = new IbrBuyRelationMo();
                modifyMo.setId(childrenNode.getId());
                modifyMo.setParentId(result.getParentNode().getId());
                modifyMo.setRelationSource((byte) result.getSource().getCode());
                if (super.modify(modifyMo) != 1) {
                    _log.error("修改当前节点的父节点，来源字段失败");
                    throw new IllegalArgumentException("修改当前节点的父节点，来源字段失败");
                }
                _log.info("匹配结果不为首单,将父节点的下家数量加1");
                IbrBuyRelationMo modifyParentChildrenCount = new IbrBuyRelationMo();
                modifyParentChildrenCount.setId(result.getParentNode().getId());
                modifyParentChildrenCount.setChildrenCount((byte) (result.getParentNode().getChildrenCount() + 1));
                if (super.modify(modifyParentChildrenCount) != 1) {
                    _log.error("将父节点的下家数量加1失败");
                    throw new IllegalArgumentException("将父节点的下家数量加1失败");
                }
                _log.info("6：增加节点数(调整幅度公式为：即将插入的节点数x2)");

                int movingCount = (int) (childrenNode.getRightValue() - childrenNode.getLeftValue() + 1);
                final Long groupId = matchTo.getMatchPrice().multiply(BigDecimal.valueOf(100)).longValueExact();
                changeRange = movingCount * -1;
                _log.info("6-1:更新右值(加上增加的节点数量),更新幅度为负数 changeRange-{}", changeRange);
                _mapper.updateRightValue(groupId, result.getParentNode().getRightValue(), changeRange, "DESC");
                _log.info("6-2:更新左值(加上增加的节点数量),更新幅度为负数 changeRange-{}", changeRange);
                _mapper.updateLeftValue(groupId, result.getParentNode().getLeftValue(),
                        result.getParentNode().getRightValue(), changeRange, "DESC");
            }

            changeRange = NewLeftValue - childrenNode.getLeftValue();
            _log.info("{},{},{}", changeRange, NewLeftValue, childrenNode.getLeftValue());
            _log.info("7：调整即将插入节点树的左右值(调整幅度为：当前插入节点的左值-当前插入节点旧的左值) changeRange-{}", changeRange);

            _mapper.updateMovingRightValueAndLeftValue(childrenNode.getLeftValue(), childrenNode.getRightValue(),
                    childrenNode.getGroupId(), changeRange, changeRange > 0 ? "DESC" : "ASC");

            _log.info("++++++++++++++++++++++++++++++++循环插入节点树结束+++++++++++++++++++++++++");
        }

        final String msg = "执行退款成功后重新匹配任务成功";
        _log.info(msg);
        return new Ro(ResultDic.SUCCESS, msg);

    }

    @Override
    public Ro executePaidNotifyMatchTask(Long detailId) {
        // 1：获取订单详情信息
        _log.info("获取订单详情信息的参数 detailId-{}", detailId);
        OrdOrderDetailMo detailResult = ordOrderDetailSvc.getById(detailId);
        _log.info("获取订单详情信息的结果 detailResult-{}", detailResult);
        if (detailResult == null || detailResult.getSubjectType() == 0) {
            final String msg = "该订单详情不存在或已退货或不是全返商品";
            return new Ro(ResultDic.FAIL, msg);
        }
        if (detailResult.getReturnState() == 2) {
            final String msg = "订单已退货";
            return new Ro(ResultDic.SUCCESS, msg);
        }

        // 2：获取订单信息
        _log.info("获取订单信息的参数：orserId-{}", detailResult.getOrderId());
        OrdOrderMo orderResult = ordOrderSvc.getById(detailResult.getOrderId());
        _log.info("获取订单信息的结果为： orderResult-{}", orderResult);
        if (orderResult == null) {
            final String msg = "订单不存在";
            return new Ro(ResultDic.FAIL, msg);
        }
        if (orderResult.getOrderState() < 2) {
            final String msg = "订单未支付或已作废";
            return new Ro(ResultDic.SUCCESS, msg);
        }

        // 3:调用执行匹配的方法
        MatchTo matchTo = new MatchTo();
        matchTo.setOrderDetailId(detailResult.getId());
        matchTo.setMatchPrice(detailResult.getBuyPrice());
        matchTo.setBuyerId(detailResult.getUserId());
        matchTo.setPaidNotifyTimestamp(orderResult.getPayTime().getTime());
        matchTo.setMaxChildernCount(2);
        if (detailResult.getInviteId() != null) {
            matchTo.setMatchPersonId(detailResult.getInviteId());
            matchTo.setMatchScheme(MatchSchemeDic.SPECIFIED_PERSON);
        } else {
            matchTo.setMatchScheme(MatchSchemeDic.SELF);
        }
        _log.info("调用执行计算匹配的参数为：matchTo-{}", matchTo);
        MatchRelationRo result = ibrMatchSvc.match(matchTo);
        _log.info("调用执行计算匹配的返回值为：result-{}", result);
        if (result.getResult() == ResultDic.SUCCESS && result.isFirst()) {
            _log.info("匹配结果为首单直接添加到分组的根节点 isFirst-{}", result.isFirst());
            final Long groupId = matchTo.getMatchPrice().multiply(BigDecimal.valueOf(100)).longValueExact();
            final IbrBuyRelationMo mo = new IbrBuyRelationMo();
            mo.setId(matchTo.getOrderDetailId());
            mo.setGroupId(groupId);
            mo.setLeftValue(1L);
            mo.setRightValue(2L);
            mo.setBuyerId(matchTo.getBuyerId());
            mo.setPaidNotifyTimestamp(matchTo.getPaidNotifyTimestamp());
            if (super.add(mo) == 1) {
                final String msg = "添加首单成功";
                return new Ro(ResultDic.SUCCESS, msg);
            }
        } else if (result.getResult() == ResultDic.SUCCESS) {
            _log.info("插入新节点");
            insertNode(result.getParentNode(), matchTo.getBuyerId(), matchTo.getOrderDetailId(),
                    matchTo.getPaidNotifyTimestamp(), result.getSource(), matchTo.getMaxChildernCount());
            final String msg = "添加关系成功";
            return new Ro(ResultDic.SUCCESS, msg);
        }
        final String msg = "添加关系失败";
        return new Ro(ResultDic.FAIL, msg);
    }

    /**
     * 根据id或父id获取已结算购买关系的数量
     */
    @Override
    public int getCountByIdOrPId(Long id) {
        _log.info("id或父id -{}", id);
        return _mapper.getCountByIdOrPId(id);
    }

    /**
     * 导入入旧的数据
     * 1:检查是否是首单
     * 2:获取应该被匹配到的父节点
     * 3:插入节点
     */
    @Override
    public Ro importOldData(ImportOldDataTo to) {

        final IbrBuyRelationMo qo = new IbrBuyRelationMo();
        final Long groupId = to.getGroupId().multiply(BigDecimal.valueOf(100)).longValueExact();
        qo.setGroupId(groupId);
        qo.setIsMoving(false);
        if (!super.existSelective(qo)) {
            _log.info("首单不存在，当前订单详情是首单");
            final IbrBuyRelationMo mo = new IbrBuyRelationMo();
            mo.setId(to.getParentNodeId());
            mo.setGroupId(groupId);
            mo.setLeftValue(1L);
            mo.setRightValue(2L);
            mo.setBuyerId(to.getUplineUserId());
            mo.setPaidNotifyTimestamp(to.getPayTime());
            // 还需要设置该条插入的是否已经结算，是否已经返佣
            if (to.getIsCommission()) {
                mo.setIsCommission(true);
            }
            if (to.getIsSettled()) {
                mo.setIsSettled(true);
            }
            if (super.add(mo) != 1) {
                final String msg = "添加首单失败";
                throw new IllegalArgumentException(msg);
            }

        } else {
            if (to.isFitst()) {
                _log.info("是相同价格的树，需要合并");
                if (super.getById(to.getParentNodeId()) == null) {
                    _log.info("插入节点");
                    // 3:调用执行匹配的方法
                    MatchTo matchTo = new MatchTo();
                    matchTo.setOrderDetailId(to.getParentNodeId());
                    matchTo.setMatchPrice(to.getGroupId());
                    matchTo.setBuyerId(to.getUplineUserId());
                    matchTo.setPaidNotifyTimestamp(to.getPayTime());
                    matchTo.setMaxChildernCount(2);
                    matchTo.setMatchScheme(MatchSchemeDic.SELF);
                    _log.info("调用执行计算匹配的参数为：matchTo-{}", matchTo);
                    MatchRelationRo result = ibrMatchSvc.match(matchTo);
                    _log.info("调用执行计算匹配的结果为：result-{}", result);

                    insertNode(result.getParentNode(), to.getUplineUserId(), to.getParentNodeId(), to.getPayTime(),
                            result.getSource(), 2);
                } else {

                    String msg = "节点已经存在";
                    _log.info(msg);
                    return new Ro(ResultDic.WARN, msg);
                }

                // 还需要修改该条插入的是否已经结算，是否已经返佣
                if (to.getIsCommission() || to.getIsSettled()) {
                    IbrBuyRelationMo modifyMo = new IbrBuyRelationMo();
                    modifyMo.setId(to.getParentNodeId());
                    if (to.getIsCommission()) {
                        modifyMo.setIsCommission(true);
                    }
                    if (to.getIsSettled()) {
                        modifyMo.setIsSettled(true);
                    }
                    _log.info("修改已返佣参数-modifyMo-{}", modifyMo);
                    if (super.modify(modifyMo) != 1) {
                        throw new IllegalArgumentException("修改已结算或已返佣失败");
                    }
                }

            } else {
                _log.info("首单已存在，当前订单详情不是首单");
                _log.info("获取节点的参数 parentId-{}", to.getParentNodeId());
                IbrBuyRelationMo parentNode = super.getById(to.getParentNodeId());
                _log.info("获取节点的结果 parentNode-{}", parentNode);
                // if (parentNode != null) {
                if (super.getById(to.getChildrenNodeId()) == null) {
                    _log.info("插入子节点");
                    insertNode(parentNode, to.getDownlineUserId(), to.getChildrenNodeId(), to.getPayTime(),
                            to.getRelationSource(), 2);
                } else {
                    String msg = "节点已经存在";
                    _log.info(msg);
                    return new Ro(ResultDic.WARN, msg);
                }

                // }
                // 还需要修改该条插入的是否已经结算，是否已经返佣
                if (to.getIsCommission() || to.getIsSettled()) {
                    IbrBuyRelationMo modifyMo = new IbrBuyRelationMo();
                    modifyMo.setId(to.getChildrenNodeId());
                    if (to.getIsCommission()) {
                        modifyMo.setIsCommission(true);
                    }
                    if (to.getIsSettled()) {
                        modifyMo.setIsSettled(true);
                    }
                    _log.info("修改已返佣参数-modifyMo-{}", modifyMo);
                    if (super.modify(modifyMo) != 1) {
                        throw new IllegalArgumentException("修改已结算或已返佣失败");
                    }
                }
            }

        }

        final String msg = "添加节点成功";
        return new Ro(ResultDic.SUCCESS, msg);
    }

}
