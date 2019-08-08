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
import rebue.ibr.dic.MatchSchemeDic;
import rebue.ibr.dic.RelationSourceDic;
import rebue.ibr.mo.IbrBuyRelationMo;
import rebue.ibr.mo.IbrBuyRelationTaskMo;
import rebue.ibr.svc.IbrBuyRelationSvc;
import rebue.ibr.svc.IbrInviteRelationSvc;
import rebue.ibr.svc.IbrMatchSvc;
import rebue.ibr.to.MatchTo;
import rebue.ord.mo.OrdOrderDetailMo;
import rebue.ord.svr.feign.OrdOrderDetailSvc;
import rebue.robotech.dic.ResultDic;

/**
 * 匹配相关接口的实现类
 *
 * 在单独使用不带任何参数的 @Transactional 注释时，
 * propagation(传播模式)=REQUIRED，readOnly=false，
 * isolation(事务隔离级别)=READ_COMMITTED，
 * 而且事务不会针对受控异常（checked exception）回滚。
 *
 * 注意：
 * 一般是查询的数据库操作，默认设置readOnly=true, propagation=Propagation.SUPPORTS
 * 而涉及到增删改的数据库操作的方法，要设置 readOnly=false, propagation=Propagation.REQUIRED
 */
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@Service
public class IbrMatchSvcImpl implements IbrMatchSvc {

    private static final Logger _log = LoggerFactory.getLogger(IbrMatchSvcImpl.class);

    @Resource
    private IbrBuyRelationSvc        buyRelationSvc;
    @Resource
    private IbrInviteRelationSvc     inviteRelationSvc;
    @Resource
    private OrdOrderDetailSvc        ordOrderDetailSvc;
    @Resource
    private AfcTradeSvc              afcTradeSvc;
    @Resource
    private AfcPlatformTradeTradeSvc afcPlatformTradeSvc;

    /**
     * 匹配(订单支付完成后，匹配订单详情到它的上家)
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public MatchRelationRo match(final MatchTo to) {
        MatchRelationRo result = new MatchRelationRo();
        _log.info("ibrMatchSvc.match: 匹配(订单支付完成后，匹配订单详情到它的上家) to-{}", to);
        _log.info(
                "↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 开始进行匹配 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");
        try {
            // 步骤计数器
            int stepCount = 1;

            _log.info("{}. 检查参数是否正确", stepCount++);
            if (to.getMatchScheme() == null || to.getBuyerId() == null || to.getMatchPrice() == null
                    || to.getOrderDetailId() == null || to.getMaxChildernCount() == null) {
                final String msg = "参数错误.没有填写匹配方案/买家ID/匹配价格/订单详情ID/最大子节点的数量";
                _log.warn("{}: {}", msg, to);
                result.setMsg(msg);
                result.setResult(ResultDic.PARAM_ERROR);
                return result;
            }
            if (MatchSchemeDic.SPECIFIED_PERSON == to.getMatchScheme() && to.getMatchPersonId() == null) {
                final String msg = "参数错误.没有填写匹配人ID";
                _log.warn("{}: {}", msg, to);
                result.setMsg(msg);
                result.setResult(ResultDic.PARAM_ERROR);
                return result;
            }

            _log.info("{}. 将匹配价格转换成分组ID", stepCount++);
            final Long groupId = to.getMatchPrice().multiply(BigDecimal.valueOf(100)).longValueExact();
            _log.info("分组ID={}(匹配价格*100)", groupId);

            _log.info("{}. 判断是否是分组首单", stepCount++);
            final IbrBuyRelationMo qo = new IbrBuyRelationMo();
            qo.setGroupId(groupId);
            qo.setIsMoving(false);
            List<IbrBuyRelationMo> isfirstResult = buyRelationSvc.list(qo);
            if (isfirstResult.size() < 1) {
                _log.info("该订单详情是分组首单");
                _log.info("{}. 返回匹配到是首单的数据，然后退出", stepCount++);
                final String msg = "匹配成功";
                _log.info("{}. {}: {}", stepCount++, msg, to);
                result.setFirst(true);
                result.setMsg(msg);
                result.setResult(ResultDic.SUCCESS);
                return result;
            }
            _log.info("该订单详情不是分组首单");
            IbrBuyRelationMo parent;
            switch (to.getMatchScheme()) {
            case SPECIFIED_PERSON:
                _log.info("执行优先匹配给指定人的方案");
                _log.info("{}. 获取指定人最早未匹配满的购买关系记录", stepCount++);
                parent = buyRelationSvc.getNotFullAndEarlestBuyRelationOfBuyer(groupId, to.getMatchPersonId(),
                        to.getMaxChildernCount());
                if (parent != null) {
                    _log.info("获取到指定人最早未匹配满的购买关系记录: {}", parent);
                    _log.info("{}. 返回匹配到给指定人后的数据", stepCount++);
//                    buyRelationSvc.insertNode(parent, to.getBuyerId(), to.getOrderDetailId(),
//                            to.getPaidNotifyTimestamp(), RelationSourceDic.APPOINTED, to.getMaxChildernCount());
                    final String msg = "匹配成功";
                    _log.info("{}. {}: {}", stepCount++, msg, to);
                    result.setMsg(msg);
                    result.setFirst(false);
                    result.setParentNode(parent);
                    result.setResult(ResultDic.SUCCESS);
                    result.setSource(RelationSourceDic.APPOINTED);
                    return result;
                }
                _log.info("没有找到指定人的购买记录");
            case SELF:
                _log.info("执行优先匹配给自己的方案");
                _log.info("{}. 获取自己最早未匹配满的购买关系记录", stepCount++);
                parent = buyRelationSvc.getNotFullAndEarlestBuyRelationOfBuyer(groupId, to.getBuyerId(),
                        to.getMaxChildernCount());
                if (parent != null) {
                    _log.info("获取到自己最早未匹配满的购买关系记录: {}", parent);
                    _log.info("{}. 返回匹配到给自己后的数据", stepCount++);
//                    buyRelationSvc.insertNode(parent, to.getBuyerId(), to.getOrderDetailId(),
//                            to.getPaidNotifyTimestamp(), RelationSourceDic.OWN, to.getMaxChildernCount());
                    final String msg = "匹配成功";
                    _log.info("{}. {}: {}", stepCount++, msg, to);
                    result.setMsg(msg);
                    result.setFirst(false);
                    result.setParentNode(parent);
                    result.setResult(ResultDic.SUCCESS);
                    result.setSource(RelationSourceDic.OWN);
                    return result;
                }
                _log.info("没有找到自己的购买记录");

                _log.info("{}. 获取最近邀请人的最早未匹配满的购买关系记录", stepCount++);
                _log.info("{}.1. 获取买家的所有邀请人ID，并按邀请时间从近到远排序", stepCount++);
                final List<Long> inviterIds = inviteRelationSvc.listIdsOfBuyer(to.getBuyerId());
                if (inviterIds != null && !inviterIds.isEmpty()) {
                    _log.info("{}.2. 开始遍历邀请人，获取最近邀请人的最早未匹配满的购买关系记录", stepCount++);
                    // 循环计数器
                    int count = 0;
                    for (final Long inviterId : inviterIds) {
                        _log.info("{}.2.{}. 按顺序获取邀请人(id-{})的最早未匹配满的购买关系记录", stepCount++, ++count, inviterId);
                        parent = buyRelationSvc.getNotFullAndEarlestBuyRelationOfBuyer(groupId, inviterId,
                                to.getMaxChildernCount());
                        if (parent != null) {
                            _log.info("获取到最近邀请人的最早未匹配满的购买关系记录: {}", parent);
                            _log.info("{}. 返回匹配到给最近邀请人后的数据", stepCount++);
//                            buyRelationSvc.insertNode(parent, to.getBuyerId(), to.getOrderDetailId(),
//                                    to.getPaidNotifyTimestamp(), RelationSourceDic.BUY, to.getMaxChildernCount());
                            final String msg = "匹配成功";
                            _log.info("{}. {}: {}", stepCount++, msg, to);
                            result.setMsg(msg);
                            result.setFirst(false);
                            result.setParentNode(parent);
                            result.setResult(ResultDic.SUCCESS);
                            result.setSource(RelationSourceDic.BUY);
                            return result;
                        }
                        _log.info("{}.2.{}. 没有找到邀请人(id-{})的购买关系记录", stepCount++, ++count, inviterId);
                    }
                    _log.info("没有找到邀请人的购买记录");
                } else {
                    _log.info("没有找到邀请人");
                }

                _log.info("{}. 获取最早未匹配满的购买关系记录", stepCount++);
                parent = buyRelationSvc.getNotFullAndEarlestBuyRelation(groupId, to.getMaxChildernCount());
                if (parent != null) {
                    _log.info("获取到最早未匹配满的购买关系记录: {}", parent);
                    _log.info("{}. 匹配最早未匹配满的购买关系记录", stepCount++);
//                    buyRelationSvc.insertNode(parent, to.getBuyerId(), to.getOrderDetailId(),
//                            to.getPaidNotifyTimestamp(), RelationSourceDic.FREE, to.getMaxChildernCount());
                    final String msg = "匹配成功";
                    _log.info("{}. {}: {}", stepCount++, msg, to);
                    result.setMsg(msg);
                    result.setFirst(false);
                    result.setParentNode(parent);
                    result.setResult(ResultDic.SUCCESS);
                    result.setSource(RelationSourceDic.FREE);
                    return result;
                }
                throw new RuntimeException("匹配失败，没有找到未匹配满的购买关系记录: " + to);
            default:
                throw new RuntimeException("未定义此匹配方案: " + to.getMatchScheme());
            }
        } finally {
            _log.info(
                    "↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 匹配完成 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        }
    }

    /**
     * 执行订单结算
     */
    @Override
    public void executeOrderSettle(IbrBuyRelationTaskMo taskResult) {
        // 2.获取任务的的购买关系
        _log.info("获取本家购买关系的参数 detailId-{}", taskResult.getOrderDetailId());
        IbrBuyRelationMo buyRelationResult = buyRelationSvc.getById(taskResult.getOrderDetailId());
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
        int modifyResult = buyRelationSvc.modify(modifySettled);
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
        IbrBuyRelationMo parentRelationResult = buyRelationSvc.getById(buyRelationResult.getParentId());
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
    }

    /**
     * 返佣
     */
    private void cashback(IbrBuyRelationMo buyRelation) {
        _log.info("-----------------------------------开始返佣----------------------------------");
        // 查询订单详情id为父id或id时已结算的购买关系数量
        Timestamp timestamp = new Timestamp(new Date().getTime());
//            _log.info("该订单详情有2个下家");
//            // 获取该购买关系下家的购买关系，并判断两个下家是否已结算
//            IbrBuyRelationMo parentBuyRelation = new IbrBuyRelationMo();
//            parentBuyRelation.setParentId(buyRelation.getId());
//            _log.info("根据父id查询下家购买关系的参数 parentBuyRelation-{}", parentBuyRelation);
//            List<IbrBuyRelationMo> childrenBuyRelationResult = buyRelationSvc.list(parentBuyRelation);
//            _log.info("根据父id查询下家购买关系的返回值 childrenBuyRelationResult-{}", childrenBuyRelationResult);
//            // 判断下家是否都已结算
//            Boolean childrenSettle = true;
//            for (IbrBuyRelationMo childBuyRelation : childrenBuyRelationResult) {
//                if (!childBuyRelation.getIsSettled()) {
//                    _log.info("该订单详情，有下家处于未结算状态");
//                    childrenSettle = false;
//                    break;
//                }
//            }
        _log.info("查询订单详情id为父id或id时已结算的购买关系数量参数为:id-{}", buyRelation.getId());
        int settleCount = buyRelationSvc.getCountByIdOrPId(buyRelation.getId());
        _log.info("查询订单详情id为父id或id时已结算的购买关系数量返回值为:count-{}", settleCount);
        // 两个下家已结算将返佣金返还给用户
        if (settleCount == 3) {
            _log.info("该订单详情下家都已结算，添加一笔返佣金记录并将佣金添加给用户");
            // 返佣金额
            BigDecimal accountReturn = new BigDecimal(buyRelation.getGroupId()).divide(new BigDecimal("100"), 3,
                    BigDecimal.ROUND_DOWN);

            timestamp = new Timestamp(new Date().getTime());
            // 添加一笔交易记录 1. 添加交易记录 2. 修改账户相应的金额字段 3. 添加账户流水
            OrdOrderDetailMo orderDetail = ordOrderDetailSvc.getById(buyRelation.getId());
            _log.info("orderDetail -{} ", orderDetail);
            AfcTradeMo accountTrade = new AfcTradeMo();
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
            int modifyResult = buyRelationSvc.modify(modifyCommission);
            _log.info("修改购买关系返佣状态的返回值为：-{}", modifyResult);
            if (modifyResult != 1) {
                _log.error("修改购买关系返佣状态出错 modifySettled-{}", modifyCommission);
                throw new IllegalArgumentException("修改购买关系返佣状态出错");
            }

        } else {
            _log.error("该订单为父id或id时已结算的购买关系数量不足3个,id-{}", buyRelation.getId());
        }
        _log.info("+++++++++++++++++++++++++++++++++结束返佣++++++++++++++++++++++++++++++++++++");
    }
}
