package rebue.ibr.svc.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rebue.ibr.dic.MatchSchemeDic;
import rebue.ibr.dic.RelationSourceDic;
import rebue.ibr.mo.IbrBuyRelationMo;
import rebue.ibr.svc.IbrBuyRelationSvc;
import rebue.ibr.svc.IbrInviteRelationSvc;
import rebue.ibr.svc.IbrMatchSvc;
import rebue.ibr.to.MatchTo;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.Ro;

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

    private static final Logger  _log = LoggerFactory.getLogger(IbrMatchSvcImpl.class);

    @Resource
    private IbrBuyRelationSvc    buyRelationSvc;
    @Resource
    private IbrInviteRelationSvc inviteRelationSvc;

    /**
     * 匹配(订单支付完成后，匹配订单详情到它的上家)
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Ro match(final MatchTo to) {
        _log.info("ibrMatchSvc.match: 匹配(订单支付完成后，匹配订单详情到它的上家) to-{}", to);

        _log.info("↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 开始进行匹配 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓");

        try {
            // 步骤计数器
            int stepCount = 1;

            _log.info("{}. 检查参数是否正确", stepCount++);
            if (to.getMatchScheme() == null || to.getBuyerId() == null || to.getMatchPrice() == null || to.getOrderDetailId() == null || to.getMaxChildernCount() == null) {
                final String msg = "参数错误.没有填写匹配方案/买家ID/匹配价格/订单详情ID/最大子节点的数量";
                _log.warn("{}: {}", msg, to);
                return new Ro(ResultDic.PARAM_ERROR, msg);
            }
            if (MatchSchemeDic.SPECIFIED_PERSON == to.getMatchScheme() && to.getMatchPersonId() == null) {
                final String msg = "参数错误.没有填写匹配人ID";
                _log.warn("{}: {}", msg, to);
                return new Ro(ResultDic.PARAM_ERROR, msg);
            }

            _log.info("{}. 将匹配价格转换成分组ID", stepCount++);
            final Long groupId = to.getMatchPrice().multiply(BigDecimal.valueOf(100)).longValueExact();
            _log.info("分组ID={}(匹配价格*100)", groupId);

            _log.info("{}. 判断是否是分组首单", stepCount++);
            final IbrBuyRelationMo qo = new IbrBuyRelationMo();
            qo.setGroupId(groupId);
            if (!buyRelationSvc.existSelective(qo)) {
                _log.info("该订单详情是分组首单");
                _log.info("{}. 直接添加到分组的根节点，然后退出", stepCount++);
                final IbrBuyRelationMo mo = new IbrBuyRelationMo();
                mo.setId(to.getOrderDetailId());
                mo.setGroupId(groupId);
                mo.setLeftValue(1L);
                mo.setRightValue(2L);
                mo.setBuyerId(to.getBuyerId());
                mo.setPaidNotifyTimestamp(to.getPaidNotifyTimestamp());
                mo.setId(to.getOrderDetailId());
                buyRelationSvc.add(mo);

                final String msg = "匹配成功";
                _log.info("{}. {}: {}", stepCount++, msg, to);
                return new Ro(ResultDic.SUCCESS, msg);
            }

            _log.info("该订单详情不是分组首单");
            IbrBuyRelationMo parent;
            switch (to.getMatchScheme()) {
            case SPECIFIED_PERSON:
                _log.info("执行优先匹配给指定人的方案");
                _log.info("{}. 获取指定人最早未匹配满的购买关系记录", stepCount++);
                parent = buyRelationSvc.getNotFullAndEarlestBuyRelationOfBuyer(groupId, to.getMatchPersonId(), to.getMaxChildernCount());
                if (parent != null) {
                    _log.info("获取到指定人最早未匹配满的购买关系记录: {}", parent);
                    _log.info("{}. 匹配指定人最早未匹配满的购买关系记录", stepCount++);
                    buyRelationSvc.insertNode(parent, to.getBuyerId(),to.getOrderDetailId(), to.getPaidNotifyTimestamp(), RelationSourceDic.APPOINTED, to.getMaxChildernCount());
                    final String msg = "匹配成功";
                    _log.info("{}. {}: {}", stepCount++, msg, to);
                    return new Ro(ResultDic.SUCCESS, msg);
                }
                _log.info("没有找到指定人的购买记录");
            case SELF:
                _log.info("执行优先匹配给自己的方案");
                _log.info("{}. 获取自己最早未匹配满的购买关系记录", stepCount++);
                parent = buyRelationSvc.getNotFullAndEarlestBuyRelationOfBuyer(groupId, to.getBuyerId(), to.getMaxChildernCount());
                if (parent != null) {
                    _log.info("获取到自己最早未匹配满的购买关系记录: {}", parent);
                    _log.info("{}. 匹配自己最早未匹配满的购买关系记录", stepCount++);
                    buyRelationSvc.insertNode(parent, to.getBuyerId(),to.getOrderDetailId(), to.getPaidNotifyTimestamp(), RelationSourceDic.OWN, to.getMaxChildernCount());
                    final String msg = "匹配成功";
                    _log.info("{}. {}: {}", stepCount++, msg, to);
                    return new Ro(ResultDic.SUCCESS, msg);
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
                        parent = buyRelationSvc.getNotFullAndEarlestBuyRelationOfBuyer(groupId, inviterId, to.getMaxChildernCount());
                        if (parent != null) {
                            _log.info("获取到最近邀请人的最早未匹配满的购买关系记录: {}", parent);
                            _log.info("{}. 匹配最近邀请人的最早未匹配满的购买关系记录", stepCount++);
                            buyRelationSvc.insertNode(parent, to.getBuyerId(),to.getOrderDetailId(), to.getPaidNotifyTimestamp(), RelationSourceDic.BUY, to.getMaxChildernCount());
                            final String msg = "匹配成功";
                            _log.info("{}. {}: {}", stepCount++, msg, to);
                            return new Ro(ResultDic.SUCCESS, msg);
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
                    buyRelationSvc.insertNode(parent, to.getBuyerId(),to.getOrderDetailId(), to.getPaidNotifyTimestamp(), RelationSourceDic.FREE, to.getMaxChildernCount());
                    final String msg = "匹配成功";
                    _log.info("{}. {}: {}", stepCount++, msg, to);
                    return new Ro(ResultDic.SUCCESS, msg);
                }
                throw new RuntimeException("匹配失败，没有找到未匹配满的购买关系记录: " + to);
            default:
                throw new RuntimeException("未定义此匹配方案: " + to.getMatchScheme());
            }
        } finally {
            _log.info("↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ 匹配完成 ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑");
        }
    }

}
