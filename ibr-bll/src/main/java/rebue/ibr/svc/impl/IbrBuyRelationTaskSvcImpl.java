package rebue.ibr.svc.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rebue.afc.dic.TradeTypeDic;
import rebue.afc.mo.AfcPlatformTradeMo;
import rebue.afc.mo.AfcTradeMo;
import rebue.afc.platform.dic.PlatformTradeTypeDic;
import rebue.afc.svr.feign.AfcPlatformTradeTradeSvc;
import rebue.afc.svr.feign.AfcTradeSvc;
import rebue.ibr.Ro.MatchRelationRo;
import rebue.ibr.dao.IbrBuyRelationTaskDao;
import rebue.ibr.dic.MatchSchemeDic;
import rebue.ibr.jo.IbrBuyRelationTaskJo;
import rebue.ibr.mapper.IbrBuyRelationMapper;
import rebue.ibr.mapper.IbrBuyRelationTaskMapper;
import rebue.ibr.mo.IbrBuyRelationMo;
import rebue.ibr.mo.IbrBuyRelationTaskMo;
import rebue.ibr.svc.IbrBuyRelationSvc;
import rebue.ibr.svc.IbrBuyRelationTaskSvc;
import rebue.ibr.svc.IbrMatchSvc;
import rebue.ibr.to.MatchTo;
import rebue.ord.mo.OrdOrderDetailMo;
import rebue.ord.mo.OrdOrderMo;
import rebue.ord.svr.feign.OrdOrderDetailSvc;
import rebue.ord.svr.feign.OrdOrderSvc;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.dic.TaskExecuteStateDic;
import rebue.robotech.ro.Ro;
import rebue.robotech.svc.impl.BaseSvcImpl;

/**
 * 购买关系任务
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
public class IbrBuyRelationTaskSvcImpl extends
        BaseSvcImpl<java.lang.Long, IbrBuyRelationTaskJo, IbrBuyRelationTaskDao, IbrBuyRelationTaskMo, IbrBuyRelationTaskMapper>
        implements IbrBuyRelationTaskSvc {

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    private static final Logger _log = LoggerFactory.getLogger(IbrBuyRelationTaskSvcImpl.class);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int add(IbrBuyRelationTaskMo mo) {
        _log.info("ibrBuyRelationTaskSvc.add: 添加购买关系任务 mo-", mo);
        // 如果id为空那么自动生成分布式id
        if (mo.getId() == null || mo.getId() == 0) {
            mo.setId(_idWorker.getId());
        }
        return super.add(mo);
    }

    @Resource
    private OrdOrderDetailSvc ordOrderDetailSvc;

    @Resource
    private OrdOrderSvc ordOrderSvc;

    @Resource
    private IbrMatchSvc ibrMatchSvc;

    @Resource
    private IbrBuyRelationSvc ibrBuyRelationSvc;

    @Resource
    private IbrBuyRelationMapper ibrBuyRelationMapper;

    @Resource
    private AfcTradeSvc afcTradeSvc;

    @Resource
    private AfcPlatformTradeTradeSvc afcPlatformTradeSvc;

    @Override
    public List<Long> getTaskIdsThatShouldExecute(TaskExecuteStateDic executeState) {
        return _mapper.getTaskIdsThatShouldExecute((byte) executeState.getCode());
    }

    @Override
    public List<Long> getTaskIdsThatShouldSettleExecute(TaskExecuteStateDic executeState) {
        return _mapper.getTaskIdsThatShouldSettleExecute((byte) executeState.getCode());
    }

    /**
     * 执行匹配购买关系任务
     * 1: 将任务取出来
     * 2：根据任务中的详情id取出来详情
     * 3：根据详情中的订单id取出来判断订单状态大于以下单状态
     * 4：调用执行匹配的方法
     */
    @Override
    public void executeMatchTask(Long taskId) {
        Ro result;
        IbrBuyRelationTaskMo taskResult = super.getById(taskId);
        _log.info("当前执行的任务为 taskResult-{}", taskResult);
        if (taskResult == null) {
            _log.error("任务不存在 taskId-{}", taskId);
            throw new IllegalArgumentException("任务不存在");
        }
        switch (taskResult.getTaskType()) {
        case 1:
            result = executePaidNotifyMatchTask(taskResult.getOrderDetailId());
            break;
        case 3:
            result = executeRefundAgainMatchTask(taskResult.getOrderDetailId());
            break;
        default:
            throw new RuntimeException("任务类型错误，匹配关系任务type:" + taskResult.getTaskType());
        }
        _log.info("匹配任务的结果为 result-{}", result);
        if (result.getResult() == ResultDic.SUCCESS) {
            _log.info("修改任务");
            IbrBuyRelationTaskMo modifyTaskMo = new IbrBuyRelationTaskMo();
            modifyTaskMo.setId(taskId);
            modifyTaskMo.setExecuteFactTime(new Date());
            modifyTaskMo.setExecuteState((byte) TaskExecuteStateDic.DONE.getCode());
            _log.info("修改任务的参数为 modifyTaskMo-{}", modifyTaskMo);
            if (super.modify(modifyTaskMo) != 1) {
                _log.error("修改任务失败 modifyTaskMo-{}", modifyTaskMo);
                throw new IllegalArgumentException("修改任务失败");
            }
        } else {
            _log.error("匹配失败");
            throw new IllegalArgumentException("修改任务失败");
        }

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
        IbrBuyRelationMo buyRelationResult = ibrBuyRelationSvc.getById(detailId);
        _log.info("当前节点 currentNode-{}", buyRelationResult);
        if (buyRelationResult == null) {
            final String msg = "关系不存在";
            return new Ro(ResultDic.FAIL, msg);
        }

        _log.info("1:将当前节点的父节点(如果有，首单的话没有父节点)的下家数量减1");
        IbrBuyRelationMo parentResult = ibrBuyRelationSvc.getById(buyRelationResult.getParentId());
        if (parentResult != null) {
            IbrBuyRelationMo modifyParentChildrenCountMo = new IbrBuyRelationMo();
            modifyParentChildrenCountMo.setId(buyRelationResult.getParentId());
            modifyParentChildrenCountMo.setChildrenCount((byte) (parentResult.getChildrenCount() - 1));
            _log.info("修改父节点下家数量的参数为：Mo-{}", modifyParentChildrenCountMo);
            if (ibrBuyRelationSvc.modify(modifyParentChildrenCountMo) != 1) {
                final String msg = "修改父节点下家数量失败";
                return new Ro(ResultDic.FAIL, msg);
            }
        }

        _log.info("2:删除(当前删除节点");
        ibrBuyRelationSvc.del(buyRelationResult.getId());

        _log.info("3:将其子节点都加上标识");
        ibrBuyRelationMapper.updateIsmoving(buyRelationResult.getGroupId(), buyRelationResult.getLeftValue(),
                buyRelationResult.getRightValue());

        _log.info("4:调整剩下节点的左右值(调整幅度=删除的节点右值-删除节点左值+1)");
        long changeRange = (buyRelationResult.getRightValue() - buyRelationResult.getLeftValue()) + 1;
        _log.info("4-1:更新右值(减去删除的节点数量),更新幅度为 changeRange-{}", changeRange);
        ibrBuyRelationMapper.updateRightValue(buyRelationResult.getGroupId(), buyRelationResult.getRightValue(),
                changeRange, "ASC");
        _log.info("4-2:更新左值(减去删除的节点数量),更新幅度为 changeRange-{}", changeRange);
        ibrBuyRelationMapper.updateLeftValue(buyRelationResult.getGroupId(), buyRelationResult.getLeftValue(),
                buyRelationResult.getRightValue(), changeRange, "ASC");

        IbrBuyRelationMo getchildrenNodesMo = new IbrBuyRelationMo();
        getchildrenNodesMo.setParentId(detailId);
        List<IbrBuyRelationMo> childrenNodes = ibrBuyRelationSvc.list(getchildrenNodesMo);
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
                if (ibrBuyRelationMapper.delateParentIdAndRelationResouce(childrenNode.getId()) != 1) {
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
                if (ibrBuyRelationSvc.modify(modifyMo) != 1) {
                    _log.error("修改当前节点的父节点，来源字段失败");
                    throw new IllegalArgumentException("修改当前节点的父节点，来源字段失败");
                }
                _log.info("匹配结果不为首单,将父节点的下家数量加1");
                IbrBuyRelationMo modifyParentChildrenCount = new IbrBuyRelationMo();
                modifyParentChildrenCount.setId(result.getParentNode().getId());
                modifyParentChildrenCount.setChildrenCount((byte) (result.getParentNode().getChildrenCount() + 1));
                if (ibrBuyRelationSvc.modify(modifyParentChildrenCount) != 1) {
                    _log.error("将父节点的下家数量加1失败");
                    throw new IllegalArgumentException("将父节点的下家数量加1失败");
                }
                _log.info("6：增加节点数(调整幅度公式为：即将插入的节点数x2)");

                int movingCount = (int) (childrenNode.getRightValue() - childrenNode.getLeftValue() + 1);
                final Long groupId = matchTo.getMatchPrice().multiply(BigDecimal.valueOf(100)).longValueExact();
                changeRange = movingCount * -1;
                _log.info("6-1:更新右值(加上增加的节点数量),更新幅度为负数 changeRange-{}", changeRange);
                ibrBuyRelationMapper.updateRightValue(groupId, result.getParentNode().getRightValue(), changeRange,
                        "DESC");
                _log.info("6-2:更新左值(加上增加的节点数量),更新幅度为负数 changeRange-{}", changeRange);
                ibrBuyRelationMapper.updateLeftValue(groupId, result.getParentNode().getLeftValue(),
                        result.getParentNode().getRightValue(), changeRange, "DESC");
            }

            changeRange = NewLeftValue - childrenNode.getLeftValue();
            _log.info("{},{},{}", changeRange, NewLeftValue, childrenNode.getLeftValue());
            _log.info("7：调整即将插入节点树的左右值(调整幅度为：当前插入节点的左值-当前插入节点旧的左值) changeRange-{}", changeRange);

            ibrBuyRelationMapper.updateMovingRightValueAndLeftValue(childrenNode.getLeftValue(),
                    childrenNode.getRightValue(), childrenNode.getGroupId(), changeRange,
                    changeRange > 0 ? "DESC" : "ASC");

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
            if (ibrBuyRelationSvc.add(mo) == 1) {
                final String msg = "添加首单成功";
                return new Ro(ResultDic.SUCCESS, msg);
            }
        } else if (result.getResult() == ResultDic.SUCCESS) {
            _log.info("插入新节点");
            ibrBuyRelationSvc.insertNode(result.getParentNode(), matchTo.getBuyerId(), matchTo.getOrderDetailId(),
                    matchTo.getPaidNotifyTimestamp(), result.getSource(), matchTo.getMaxChildernCount());
            final String msg = "添加关系成功";
            return new Ro(ResultDic.SUCCESS, msg);
        }
        final String msg = "添加关系失败";
        return new Ro(ResultDic.FAIL, msg);
    }

    /**
     * 订单结算任务
     */
    @Override
    public void executeOrderSettleTask(Long taskId) {
        // 1：将任务取出来
        IbrBuyRelationTaskMo taskResult = super.getById(taskId);
        if (taskResult == null) {
            _log.error("任务不存在 taskId-{}", taskId);
            throw new IllegalArgumentException("任务不存在");
        }
        // 2.获取任务的的购买关系
        _log.info("获取本家购买关系的参数 detailId-{}", taskResult.getOrderDetailId());
        IbrBuyRelationMo buyRelationResult = ibrBuyRelationSvc.getById(taskResult.getOrderDetailId());
        _log.info("获取本家购买关系的结果 detailResult-{}", buyRelationResult);
        if (buyRelationResult == null) {
            _log.error("本家订单详情不存在 orderDetailId-{}", taskResult.getOrderDetailId());
            throw new IllegalArgumentException("本家订单详情不存在");
        }
        // 3.修改本家订单结算状态
        IbrBuyRelationMo modifySettled = new IbrBuyRelationMo();
        modifySettled.setId(buyRelationResult.getId());
        modifySettled.setIsSettled(true);
        buyRelationResult.setIsSettled(true);
        _log.info("修改本家订单结算状态的参数为：-{}", modifySettled);
        int modifyResult = ibrBuyRelationSvc.modify(modifySettled);
        _log.info("修改本家订单结算状态的返回值为：-{}", modifyResult);
        if (modifyResult != 1) {
            _log.error("修改本家订单状态出错 modifySettled-{}", modifySettled);
            throw new IllegalArgumentException("修改本家结算状态出错");
        }
        // 4.判断本家是否可以返佣
        _log.info("开始判断本家是否可以返佣");
        cashback(buyRelationResult);
        // 5.获取上家的购买关系
        _log.info("获取上家购买关系的参数 parentId-{}", buyRelationResult.getParentId());
        IbrBuyRelationMo parentRelationResult = ibrBuyRelationSvc.getById(buyRelationResult.getParentId());
        _log.info("获取上家购买关系的结果 parentRelationResult-{}", parentRelationResult);
        if (parentRelationResult == null) {
            _log.error("上家订单详情不存在 id-{}", buyRelationResult.getId());
        } else {
            // 6.判断上家是否可以返佣
            _log.info("开始判断上家是否可以返佣");
            if (!parentRelationResult.getIsSettled()) {
                _log.error("上家还没有结算 parentId-{}", parentRelationResult.getIsSettled());
            } else {
                cashback(parentRelationResult);
            }
        }
        _log.info("修改任务");
        IbrBuyRelationTaskMo modifyTaskMo = new IbrBuyRelationTaskMo();
        modifyTaskMo.setId(taskId);
        modifyTaskMo.setExecuteFactTime(new Date());
        modifyTaskMo.setExecuteState((byte) TaskExecuteStateDic.DONE.getCode());
        _log.info("修改任务的参数为 modifyTaskMo-{}", modifyTaskMo);
        if (super.modify(modifyTaskMo) != 1) {
            _log.error("修改任务失败 modifyTaskMo-{}", modifyTaskMo);
            throw new IllegalArgumentException("修改任务失败");
        }

    }

    // 返佣
    public void cashback(IbrBuyRelationMo buyRelation) {
        _log.info("-----------------------------------开始返佣----------------------------------");
        // 查询该购买关系的下家
        Timestamp timestamp = new Timestamp(new Date().getTime());
        if (buyRelation.getChildrenCount() == 2) {
            _log.info("该订单详情有2个下家");
            // 获取该购买关系下家的购买关系，并判断两个下家是否已结算
            IbrBuyRelationMo parentBuyRelation = new IbrBuyRelationMo();
            parentBuyRelation.setParentId(buyRelation.getId());
            _log.info("根据父id查询下家购买关系的参数 parentBuyRelation-{}", parentBuyRelation);
            List<IbrBuyRelationMo> childrenBuyRelationResult = ibrBuyRelationSvc.list(parentBuyRelation);
            _log.info("根据父id查询下家购买关系的返回值 childrenBuyRelationResult-{}", childrenBuyRelationResult);
            // 判断下家是否都已结算
            Boolean childrenSettle = true;
            for (IbrBuyRelationMo childBuyRelation : childrenBuyRelationResult) {
                if (!childBuyRelation.getIsSettled()) {
                    _log.info("该订单详情，有下家处于未结算状态");
                    childrenSettle = false;
                    break;
                }
            }
            // 两个下家已结算将返佣金返还给用户
            if (childrenSettle) {
                _log.info("该订单详情下家都已结算，添加一笔返佣金记录并将佣金添加给用户");
                // 返佣金额
                BigDecimal accountReturn = new BigDecimal(buyRelation.getGroupId()).divide(new BigDecimal("100"), 3,
                        BigDecimal.ROUND_DOWN);

                timestamp = new Timestamp(new Date().getTime());
                // 添加一笔交易记录 1. 添加交易记录 2. 修改账户相应的金额字段 3. 添加账户流水
                OrdOrderDetailMo orderDetail = ordOrderDetailSvc.getById(buyRelation.getId());
                _log.info("orderDetail -{} ", orderDetail);
                _log.info("id -{} ", _idWorker.getId());
                AfcTradeMo accountTrade = new AfcTradeMo();
                accountTrade.setId(_idWorker.getId());
                accountTrade.setAccountId(buyRelation.getBuyerId());
                accountTrade.setTradeType((byte) TradeTypeDic.SETTLE_COMMISSION.getCode());
                accountTrade.setTradeAmount(accountReturn);
                accountTrade.setChangeAmount2(accountReturn);
                accountTrade.setTradeTitle("大麦网络-结算本家订单并返佣金");
                accountTrade.setTradeTime(timestamp);
                accountTrade.setOrderId(orderDetail.getOrderId().toString());
                accountTrade.setOrderDetailId(buyRelation.getId().toString());
                accountTrade.setOpId(0L);
                accountTrade.setMac("00-16-3E-12-D8-F4");
                accountTrade.setIp("127.18.175.121");
                _log.info("添加一笔返佣金记录的参数为 accountTrade-{}", accountTrade);

                afcTradeSvc.addTrade(accountTrade);

                // 从平台扣除相应的佣金,添加一条平台流水
                AfcPlatformTradeMo platformTrade = new AfcPlatformTradeMo();
                platformTrade.setId(_idWorker.getId());
                platformTrade.setPlatformTradeType((byte) PlatformTradeTypeDic.PLATFORM_RETURN_TO_USER.getCode());
                platformTrade.setOrderId(orderDetail.getOrderId().toString());
                platformTrade.setOrderDetailId(buyRelation.getId().toString());
                platformTrade.setTradeAmount(accountReturn);
                platformTrade.setModifiedTimestamp(new Date().getTime());
                _log.info("添加一笔平台扣除返佣金记录的参数为 platformTrade-{}", platformTrade);

                afcPlatformTradeSvc.addTrade(platformTrade);

                // 购买关系中改为已返佣
                IbrBuyRelationMo modifyCommission = new IbrBuyRelationMo();
                modifyCommission.setId(buyRelation.getId());
                modifyCommission.setIsCommission(true);
                _log.info("修改购买关系返佣状态的参数为：-{}", modifyCommission);
                int modifyResult = ibrBuyRelationSvc.modify(modifyCommission);
                _log.info("修改购买关系返佣状态的返回值为：-{}", modifyResult);
                if (modifyResult != 1) {
                    _log.error("修改购买关系返佣状态出错 modifySettled-{}", modifyCommission);
                    throw new IllegalArgumentException("修改购买关系返佣状态出错");
                }
            }
        } else {
            _log.error("该订单详情的下家不足2个,childrenCount:-{}", buyRelation.getChildrenCount());
        }
        _log.info("+++++++++++++++++++++++++++++++++结束返佣++++++++++++++++++++++++++++++++++++");

    }
}
