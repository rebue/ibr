package rebue.ibr.svr.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import rebue.ibr.to.MatchTo;
import rebue.robotech.ro.Ro;
import rebue.sbs.feign.FeignConfig;

@FeignClient(name = "ibr-svr", configuration = FeignConfig.class)
public interface IbrMatchSvc {

    /**
     * 匹配(订单支付完成后，匹配订单详情到它的上家)
     */
    @PostMapping("/ibr/match")
    Ro match(@RequestBody MatchTo to);

}
