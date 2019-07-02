package rebue.ibr.svr.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import rebue.ibr.mo.IbrBuyRelationMo;
import rebue.robotech.ro.IdRo;
import rebue.robotech.ro.Ro;
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
     * 删除购买关系表
     *
     */
    @GetMapping("/ibr/buy-relation/get-by-id")
    IbrBuyRelationMo getById(@RequestParam("id") java.lang.Long id);

    /**
     * 删除购买关系表
     * 
     */
    @DeleteMapping("/ibr/buy-relation")
    Ro del(@RequestParam("id") Long id);

    /**
     * 获取单个购买关系表
     *
     */
    @GetMapping("/ibr/buy-relation/get-one")
    IbrBuyRelationMo getOne(@RequestBody IbrBuyRelationMo mo);

    /**
     * 添加购买关系表
     *
     */
    @PostMapping("/ibr/buy-relation")
    IdRo add(@RequestBody IbrBuyRelationMo mo);
}
