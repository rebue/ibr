package rebue.ibr.scheduler.task;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import rebue.ibr.svr.feign.IbrBuyRelationTaskSvc;
import rebue.robotech.dic.TaskExecuteStateDic;

/**
 * 订单结算任务
 */
@Component
public class IbrOrderSettleTasks {

    private final static Logger _log = LoggerFactory.getLogger(IbrOrderSettleTasks.class);

    @Resource
    private IbrBuyRelationTaskSvc ibrBuyRelationTaskSvc;

    // settleRelation:订单结算执行的间隔(毫秒)，默认5分钟检查一次
    @Scheduled(fixedDelayString = "${ibr.scheduler.settleRelation:300000}")
    public void executeTasks() throws InterruptedException {
        _log.info("定时执行需要订单结算的的任务");
        List<Long> taskIds = ibrBuyRelationTaskSvc.getTaskIdsThatShouldSettleExecute(TaskExecuteStateDic.NONE);
        _log.info("获取到所有需要订单结算的任务的列表为：{}", taskIds);
        try {
            for (Long taskId : taskIds) {
                try {
                    _log.info("当前任务id-{}", taskId);
                    ibrBuyRelationTaskSvc.executeOrderSettleTask(taskId);
                    Thread.sleep(1000);
                } catch (final RuntimeException e) {
                    _log.error("需要执行订单结算任务失败", e);
                }
            }
        } catch (final RuntimeException e) {
            _log.info("获取需要执行订单结算任务时出现异常", e);
        }
    }
}
