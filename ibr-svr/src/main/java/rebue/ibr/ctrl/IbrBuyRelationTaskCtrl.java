package rebue.ibr.ctrl;

import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rebue.ibr.mo.IbrBuyRelationTaskMo;
import rebue.ibr.svc.IbrBuyRelationTaskSvc;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.IdRo;
import rebue.robotech.ro.Ro;

/**
 * 购买关系任务
 *
 * @mbg.generated 自动生成的注释，如需修改本注释，请删除本行
 */
@RestController
public class IbrBuyRelationTaskCtrl {

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    private static final Logger _log = LoggerFactory.getLogger(IbrBuyRelationTaskCtrl.class);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Resource
    private IbrBuyRelationTaskSvc svc;

    /**
     * 添加购买关系任务
     *
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @PostMapping("/ibr/buy-relation-task")
    IdRo add(@RequestBody final IbrBuyRelationTaskMo mo) throws Exception {
        _log.info("add IbrBuyRelationTaskMo: {}", mo);
        final IdRo ro = new IdRo();
        try {
            final int result = svc.add(mo);
            if (result == 1) {
                final String msg = "添加成功";
                _log.info("{}: mo-{}", msg, mo);
                ro.setMsg(msg);
                ro.setResult(ResultDic.SUCCESS);
                ro.setId(mo.getId().toString());
                return ro;
            } else {
                final String msg = "添加失败";
                _log.error("{}: mo-{}", msg, mo);
                ro.setMsg(msg);
                ro.setResult(ResultDic.FAIL);
                return ro;
            }
        } catch (final DuplicateKeyException e) {
            final String msg = "添加失败，唯一键重复：" + e.getMessage();
            _log.error(msg + ": mo-" + mo, e);
            ro.setMsg(msg);
            ro.setResult(ResultDic.FAIL);
            return ro;
        } catch (final RuntimeException e) {
            final String msg = "添加失败，出现运行时异常";
            _log.error(msg + ": mo-" + mo, e);
            ro.setMsg(msg);
            ro.setResult(ResultDic.FAIL);
            return ro;
        }
    }

    /**
     * 修改购买关系任务
     *
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @PutMapping("/ibr/buy-relation-task")
    Ro modify(@RequestBody final IbrBuyRelationTaskMo mo) throws Exception {
        _log.info("modify IbrBuyRelationTaskMo: {}", mo);
        final Ro ro = new Ro();
        try {
            if (svc.modify(mo) == 1) {
                final String msg = "修改成功";
                _log.info("{}: mo-{}", msg, mo);
                ro.setMsg(msg);
                ro.setResult(ResultDic.SUCCESS);
                return ro;
            } else {
                final String msg = "修改失败";
                _log.error("{}: mo-{}", msg, mo);
                ro.setMsg(msg);
                ro.setResult(ResultDic.FAIL);
                return ro;
            }
        } catch (final DuplicateKeyException e) {
            final String msg = "修改失败，唯一键重复：" + e.getMessage();
            _log.error(msg + ": mo=" + mo, e);
            ro.setMsg(msg);
            ro.setResult(ResultDic.FAIL);
            return ro;
        } catch (final RuntimeException e) {
            final String msg = "修改失败，出现运行时异常";
            _log.error(msg + ": mo-" + mo, e);
            ro.setMsg(msg);
            ro.setResult(ResultDic.FAIL);
            return ro;
        }
    }

    /**
     * 删除购买关系任务
     *
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @DeleteMapping("/ibr/buy-relation-task")
    Ro del(@RequestParam("id") final java.lang.Long id) {
        _log.info("del IbrBuyRelationTaskMo by id: {}", id);
        final int result = svc.del(id);
        final Ro ro = new Ro();
        if (result == 1) {
            final String msg = "删除成功";
            _log.info("{}: id-{}", msg, id);
            ro.setMsg(msg);
            ro.setResult(ResultDic.SUCCESS);
            return ro;
        } else {
            final String msg = "删除失败，找不到该记录";
            _log.error("{}: id-{}", msg, id);
            ro.setMsg(msg);
            ro.setResult(ResultDic.FAIL);
            return ro;
        }
    }

    /**
     * 查询购买关系任务
     *
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @GetMapping("/ibr/buy-relation-task")
    PageInfo<IbrBuyRelationTaskMo> list(final IbrBuyRelationTaskMo mo, @RequestParam(value = "pageNum", required = false) Integer pageNum, @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 5;
        }
        _log.info("list IbrBuyRelationTaskMo:" + mo + ", pageNum = " + pageNum + ", pageSize = " + pageSize);
        if (pageSize > 50) {
            final String msg = "pageSize不能大于50";
            _log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        final PageInfo<IbrBuyRelationTaskMo> result = svc.list(mo, pageNum, pageSize);
        _log.info("result: " + result);
        return result;
    }

    /**
     * 获取单个购买关系任务
     *
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @GetMapping("/ibr/buy-relation-task/get-by-id")
    IbrBuyRelationTaskMo getById(@RequestParam("id") final java.lang.Long id) {
        _log.info("get IbrBuyRelationTaskMo by id: {}", id);
        return svc.getById(id);
    }
}