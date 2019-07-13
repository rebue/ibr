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
import rebue.ibr.mo.IbrInviteRelationMo;
import rebue.ibr.svc.IbrInviteRelationSvc;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.IdRo;
import rebue.robotech.ro.Ro;

/**
 * 邀请关系
 *
 * @mbg.generated 自动生成的注释，如需修改本注释，请删除本行
 */
@RestController
public class IbrInviteRelationCtrl {

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    private static final Logger _log = LoggerFactory.getLogger(IbrInviteRelationCtrl.class);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Resource
    private IbrInviteRelationSvc svc;

    /**
     * 添加邀请关系
     *
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @PostMapping("/ibr/invite-relation")
    IdRo add(@RequestBody final IbrInviteRelationMo mo) throws Exception {
        _log.info("received post:/ibr/invite-relation");
        _log.info("inviteRelationCtrl.add: {}", mo);
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
            final String msg = "添加失败，唯一键重复：" + e.getCause().getMessage();
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
     * 修改邀请关系
     *
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @PutMapping("/ibr/invite-relation")
    Ro modify(@RequestBody final IbrInviteRelationMo mo) throws Exception {
        _log.info("received put:/ibr/invite-relation");
        _log.info("inviteRelationCtrl.modify: {}", mo);
        try {
            if (svc.modify(mo) == 1) {
                final String msg = "修改成功";
                _log.info("{}: mo-{}", msg, mo);
                return new Ro(ResultDic.SUCCESS, msg);
            } else {
                final String msg = "修改失败";
                _log.error("{}: mo-{}", msg, mo);
                return new Ro(ResultDic.FAIL, msg);
            }
        } catch (final DuplicateKeyException e) {
            final String msg = "修改失败，唯一键重复：" + e.getCause().getMessage();
            _log.error(msg + ": mo=" + mo, e);
            return new Ro(ResultDic.FAIL, msg);
        } catch (final RuntimeException e) {
            final String msg = "修改失败，出现运行时异常";
            _log.error(msg + ": mo-" + mo, e);
            return new Ro(ResultDic.FAIL, msg);
        }
    }

    /**
     * 删除邀请关系
     *
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @DeleteMapping("/ibr/invite-relation")
    Ro del(@RequestParam("id") final java.lang.Long id) {
        _log.info("received delete:/ibr/invite-relation");
        _log.info("inviteRelationCtrl.del: {}", id);
        final int result = svc.del(id);
        if (result == 1) {
            final String msg = "删除成功";
            _log.info("{}: id-{}", msg, id);
            return new Ro(ResultDic.SUCCESS, msg);
        } else {
            final String msg = "删除失败，找不到该记录";
            _log.error("{}: id-{}", msg, id);
            return new Ro(ResultDic.FAIL, msg);
        }
    }

    /**
     * 查询邀请关系
     *
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @GetMapping("/ibr/invite-relation")
    PageInfo<IbrInviteRelationMo> list(final IbrInviteRelationMo mo, @RequestParam(value = "pageNum", required = false) Integer pageNum, @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        _log.info("received get:/ibr/invite-relation");
        _log.info("inviteRelation.list: {},pageNum-{},pageSize-{}", mo, pageNum, pageSize);
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 5;
        }
        _log.info("list IbrInviteRelationMo:" + mo + ", pageNum = " + pageNum + ", pageSize = " + pageSize);
        if (pageSize > 50) {
            final String msg = "pageSize不能大于50";
            _log.error(msg);
            throw new IllegalArgumentException(msg);
        }
        final PageInfo<IbrInviteRelationMo> result = svc.list(mo, pageNum, pageSize);
        _log.info("result: " + result);
        return result;
    }

    /**
     * 获取单个邀请关系
     *
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    @GetMapping("/ibr/invite-relation/get-by-id")
    IbrInviteRelationMo getById(@RequestParam("id") final java.lang.Long id) {
        _log.info("received get:/ibr/invite-relation/get-by-id");
        _log.info("ibrInviteRelationMoCtrl.getById: {}", id);
        return svc.getById(id);
    }
}
