package rebue.ibr.svc;

import java.util.List;

import rebue.ibr.jo.IbrBuyRelationTaskJo;
import rebue.ibr.mo.IbrBuyRelationTaskMo;
import rebue.robotech.dic.TaskExecuteStateDic;
import rebue.robotech.ro.Ro;
import rebue.robotech.svc.BaseSvc;

/**
 * 购买关系任务
 *
 * @mbg.generated 自动生成的注释，如需修改本注释，请删除本行
 */
public interface IbrBuyRelationTaskSvc extends BaseSvc<java.lang.Long, IbrBuyRelationTaskMo, IbrBuyRelationTaskJo> {

    /**
     * 
     * 获取需要执行的任务ids集合
     * 
     * @param taskId
     * @return
     */
    List<Long> getTaskIdsThatShouldExecute(TaskExecuteStateDic executeState);

    /**
     * 执行任务
     * 
     * @param taskId
     */
    void executeTask(Long taskId);

    /**
     * 执行订单结算任务
     */
    Ro executeOrderSettleTask(Long taskId);

}
