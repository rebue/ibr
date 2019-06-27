package rebue.ibr.scheduler.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IbrBuyRelationTasks {

	private final static Logger _log = LoggerFactory.getLogger(IbrBuyRelationTasks.class);

	// buyRelation:订单匹配关执行的间隔(毫秒)，默认5分钟检查一次
	@Scheduled(fixedDelayString = "${ord.scheduler.buyRelation:300000}")
	public void executeTasks() {
		_log.info("定时执行需要订单匹配关系的的任务");
		try {
			for (int i = 0; i < 50; i++) {
				_log.info("定时执行需要订单匹配关系的的任务i-{}",i);
			}
		} catch (final RuntimeException e) {
			_log.info("获取需要执行订单匹配关系任务时出现异常", e);
		}
	}

}
