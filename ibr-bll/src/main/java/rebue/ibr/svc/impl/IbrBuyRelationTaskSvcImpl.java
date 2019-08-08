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
import rebue.ibr.jo.IbrBuyRelationTaskJo;
import rebue.ibr.mapper.IbrBuyRelationMapper;
import rebue.ibr.mapper.IbrBuyRelationTaskMapper;
import rebue.ibr.mo.IbrBuyRelationTaskMo;
import rebue.ibr.svc.IbrBuyRelationSvc;
import rebue.ibr.svc.IbrBuyRelationTaskSvc;
import rebue.ibr.svc.IbrMatchSvc;
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
            result = ibrBuyRelationSvc.executePaidNotifyMatchTask(taskResult.getOrderDetailId());
            break;
        case 3:
            result = ibrBuyRelationSvc.executeRefundAgainMatchTask(taskResult.getOrderDetailId());
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
        if (TaskExecuteStateDic.DONE.getCode() == taskResult.getExecuteState()) {
            final String msg = "任务已被执行，不能取消";
            _log.error("{}-{}", msg, taskId);
            throw new IllegalArgumentException("任务已被执行，不能取消");
        }
        if (TaskExecuteStateDic.CANCEL.getCode() == taskResult.getExecuteState()) {
            final String msg = "任务已被取消，不能重复取消";
            _log.error("{}-{}", msg, taskId);
            throw new IllegalArgumentException("任务已被取消，不能重复取消");
        }
        try {
            ibrMatchSvc.executeOrderSettle(taskResult);
        } catch (final RuntimeException e) {
            _log.error("执行订单结算任务订单结算时出错", e);
            throw new RuntimeException("订单结算出错");
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

}
