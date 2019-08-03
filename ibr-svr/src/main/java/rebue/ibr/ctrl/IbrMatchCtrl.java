package rebue.ibr.ctrl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import rebue.ibr.Ro.MatchRelationRo;
import rebue.ibr.svc.IbrMatchSvc;
import rebue.ibr.to.MatchTo;

/**
 * 匹配相关的控制器
 */
@RestController
public class IbrMatchCtrl {

    private static final Logger _log = LoggerFactory.getLogger(IbrMatchCtrl.class);

    @Resource
    private IbrMatchSvc svc;

    /**
     * 匹配(订单支付完成后，匹配订单详情到它的上家)
     */
    @PostMapping("/ibr/match")
    MatchRelationRo match(@RequestBody final MatchTo to) {
        _log.info("received post:/ibr/match: {}", to);
        return svc.match(to);
    }
}
