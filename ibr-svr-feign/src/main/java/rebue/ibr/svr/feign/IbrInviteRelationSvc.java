package rebue.ibr.svr.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import rebue.ibr.mo.IbrInviteRelationMo;
import rebue.sbs.feign.FeignConfig;

@FeignClient(name = "ibr-svr", configuration = FeignConfig.class, contextId = "ibr-svr-invite-relation")
public interface IbrInviteRelationSvc {

    /**
     * 获取单个邀请关系
     * 
     * @param mo
     * @return
     */
    @GetMapping("/ibr/invite-relation/get-one")
    IbrInviteRelationMo getOne(@RequestParam("inviteeId") java.lang.Long inviteeId);

}
