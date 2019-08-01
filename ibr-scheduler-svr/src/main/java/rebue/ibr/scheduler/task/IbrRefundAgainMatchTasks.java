package rebue.ibr.scheduler.task;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import rebue.ibr.dic.TaskTypeDic;
import rebue.ibr.svr.feign.IbrBuyRelationTaskSvc;
import rebue.robotech.dic.TaskExecuteStateDic;

@Component
public class IbrRefundAgainMatchTasks {

    private final static Logger _log = LoggerFactory.getLogger(IbrRefundAgainMatchTasks.class);

    @Resource
    private IbrBuyRelationTaskSvc ibrBuyRelationTaskSvc;

    // buyRelation:退款成功后重新匹配关系执行的间隔(毫秒)，默认5分钟检查一次
    @Scheduled(fixedDelayString = "${ibr.scheduler.refundAgainMatch:30000}")
    public void executeTasks() throws InterruptedException {
        _log.info("定时执行退款成功后重新匹配关系的任务");
        List<Long> taskIds = ibrBuyRelationTaskSvc.getTaskIdsThatShouldExecute(TaskExecuteStateDic.NONE,
                TaskTypeDic.REFUND_AGAIN_MATCH);
        _log.info("获取到所有退款成功后重新匹配关系任务的列表为：{}", taskIds);
        try {
            for (Long taskId : taskIds) {
                try {
                    _log.info("当前任务id-{}", taskId);
                    ibrBuyRelationTaskSvc.executeRefundAgainMatchTask(taskId);
                    Thread.sleep(1000);
                } catch (final RuntimeException e) {
                    _log.error("需要执行退款成功后重新匹配关系任务失败", e);
                }
            }
        } catch (final RuntimeException e) {
            _log.info("获取需要退款成功后重新匹配关系任务时出现异常", e);
        }
    }

}
