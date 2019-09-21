package rebue.ibr.svr.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import rebue.ibr.Ro.MatchRelationRo;
import rebue.ibr.to.MatchTo;
import rebue.sbs.feign.FeignConfig;

@FeignClient(name = "ibr-svr", configuration = FeignConfig.class, contextId = "ibr-svr-match")
public interface IbrMatchSvc {

    /**
     * 匹配(订单支付完成后，匹配订单详情到它的上家)
     */
    @PostMapping("/ibr/match")
    MatchRelationRo match(@RequestBody MatchTo to);

}
