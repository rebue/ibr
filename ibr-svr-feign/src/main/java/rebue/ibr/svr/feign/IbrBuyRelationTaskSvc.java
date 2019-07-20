package rebue.ibr.svr.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import rebue.ibr.dic.MatchTaskTypeDic;
import rebue.ibr.mo.IbrBuyRelationTaskMo;
import rebue.robotech.dic.TaskExecuteStateDic;
import rebue.robotech.ro.IdRo;
import rebue.sbs.feign.FeignConfig;

@FeignClient(name = "ibr-svr", configuration = FeignConfig.class)
public interface IbrBuyRelationTaskSvc {

    /**
     * 添加匹配购买任务
     * 
     * @param mo
     * @return
     */
    @PostMapping("/ibr/buy-relation-task")
    IdRo add(@RequestBody final IbrBuyRelationTaskMo mo);

    /**
     * 获取需要执行的任务列表
     * 
     * @param executeState
     * @param taskType
     * @return
     */
    @GetMapping(value = "/ibr/buy-relation-task/tasks")
    List<Long> getTaskIdsThatShouldExecute(@RequestParam("executeState") final TaskExecuteStateDic executeState,
            @RequestParam("taskType") final MatchTaskTypeDic taskType);

    /**
     * 执行匹配购买关系任务
     * 
     * @param taskId
     */
    @PostMapping("/ibr/execute-task")
    void executeMatchBuyRelationTask(@RequestParam("taskId") final Long taskId);

}
