package rebue.ibr.svr.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import rebue.ibr.mo.IbrBuyRelationMo;
import rebue.sbs.feign.FeignConfig;

@FeignClient(name = "ibr-svr", configuration = FeignConfig.class)
public interface IbrBuyRelationSvc {

    /**
     * 查询购买关系表
     * 
     * @param mo
     * @return
     */
    @GetMapping("/ibr/buy-relation")
    List<IbrBuyRelationMo> list(@RequestBody IbrBuyRelationMo mo);

    /**
     * 获取单个购买关系表
     *
     */
    @GetMapping("/ibr/buy-relation/get-one")
    IbrBuyRelationMo getOne(@RequestBody IbrBuyRelationMo mo);

}
