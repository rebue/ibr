package rebue.ibr.svc.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rebue.ibr.dao.IbrBuyRelationTaskDao;
import rebue.ibr.dic.MatchSchemeDic;
import rebue.ibr.dic.MatchTaskTypeDic;
import rebue.ibr.jo.IbrBuyRelationTaskJo;
import rebue.ibr.mapper.IbrBuyRelationTaskMapper;
import rebue.ibr.mo.IbrBuyRelationTaskMo;
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

    @Resource
    private OrdOrderDetailSvc ordOrderDetailSvc;

    @Resource
    private OrdOrderSvc ordOrderSvc;

    @Resource
    private IbrMatchSvc ibrMatchSvc;

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

    @Override
    public List<Long> getTaskIdsThatShouldExecute(TaskExecuteStateDic executeState, MatchTaskTypeDic taskType) {
        return _mapper.getTaskIdsThatShouldExecute((byte) executeState.getCode(), (byte) taskType.getCode());
    }

    /**
     * 执行匹配购买关系任务
     * 1: 将任务取出来
     * 2：根据任务中的详情id取出来详情
     * 3：根据详情中的订单id取出来判断订单状态大于以下单状态
     * 4：调用执行匹配的方法
     */
    @Override
    public void executeSubscribeTraceTask(Long taskId) {
        // 1：将任务取出来
        IbrBuyRelationTaskMo taskResult = super.getById(taskId);
        if (taskResult == null) {
            _log.error("任务不存在 taskId-{}", taskId);
            throw new IllegalArgumentException("任务不存在");
        }
        // 2：根据任务中的详情id取出来详情
        _log.info("获取订单详情信息的参数 detailId-{}", taskResult.getOrderDetailId());
        OrdOrderDetailMo detailResult = ordOrderDetailSvc.getById(taskResult.getOrderDetailId());
        _log.info("获取订单详情信息的结果 detailResult-{}", detailResult);
        if (detailResult == null) {
            _log.error("订单详情不存在 detailId-{}", taskResult.getOrderDetailId());
            throw new IllegalArgumentException("订单详情不存在");
        }
        if (detailResult.getReturnState() == 2 || detailResult.getSubjectType() == 0) {
            _log.info("该订单详情已退货或不是全返商品 detailId-{},resultState-{},subjectType-{}", taskResult.getOrderDetailId(),
                    detailResult.getReturnState(), detailResult.getSubjectType());
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
        // 3：根据详情中的订单id取出来判断订单状态大于以下单状态
        _log.info("获取订单信息的参数：orserId-{}", detailResult.getOrderId());
        OrdOrderMo orderResult = ordOrderSvc.getById(detailResult.getOrderId());
        _log.info("获取订单信息的结果为： orderResult-{}", orderResult);
        if (orderResult == null) {
            _log.error("订单不存在 orserId-{}", detailResult.getOrderId());
            throw new IllegalArgumentException("订单不存在");
        }
        if (orderResult.getOrderState() < 2) {
            _log.info("订单未支付或已作废 orserId-{},orderState-{}", detailResult.getOrderId(), orderResult.getOrderState());
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
        // 4:调用执行匹配的方法
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
        _log.info("调用执行匹配的参数为：matchTo-{}", matchTo);
        Ro result = ibrMatchSvc.match(matchTo);
        if (result.getResult() == ResultDic.SUCCESS) {
            IbrBuyRelationTaskMo modifyTaskMo = new IbrBuyRelationTaskMo();
            modifyTaskMo.setId(taskId);
            modifyTaskMo.setExecuteFactTime(new Date());
            modifyTaskMo.setExecuteState((byte) TaskExecuteStateDic.DONE.getCode());
            _log.info("修改任务的参数为 modifyTaskMo-{}", modifyTaskMo);
            if (super.modify(modifyTaskMo) != 1) {
                _log.error("修改任务失败 modifyTaskMo-{}", modifyTaskMo);
                throw new IllegalArgumentException("修改任务失败");
            }
            _log.info("调用执行匹配关系成功");
        } else {
            _log.error("匹配失败 result-{}", result);
            throw new IllegalArgumentException("匹配失败");
        }
    }
}
