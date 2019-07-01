package rebue.ibr.svc.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rebue.ibr.dao.IbrInviteRelationDao;
import rebue.ibr.jo.IbrInviteRelationJo;
import rebue.ibr.mapper.IbrInviteRelationMapper;
import rebue.ibr.mo.IbrInviteRelationMo;
import rebue.ibr.svc.IbrInviteRelationSvc;
import rebue.robotech.svc.impl.BaseSvcImpl;

/**
 * 邀请关系表
 *
 * 在单独使用不带任何参数的 @Transactional 注释时，
 * propagation(传播模式)=REQUIRED，readOnly=false，
 * isolation(事务隔离级别)=READ_COMMITTED，
 * 而且事务不会针对受控异常（checked exception）回滚。
 *
 * 注意：
 * 一般是查询的数据库操作，默认设置readOnly=true, propagation=Propagation.SUPPORTS
 * 而涉及到增删改的数据库操作的方法，要设置 readOnly=false, propagation=Propagation.REQUIRED
 *
 * @mbg.generated 自动生成的注释，如需修改本注释，请删除本行
 */
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
@Service
public class IbrInviteRelationSvcImpl extends BaseSvcImpl<java.lang.Long, IbrInviteRelationJo, IbrInviteRelationDao, IbrInviteRelationMo, IbrInviteRelationMapper>
        implements IbrInviteRelationSvc {

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    private static final Logger _log = LoggerFactory.getLogger(IbrInviteRelationSvcImpl.class);

    /**
     * 添加邀请关系
     * 邀请时间戳不用传进来，在这里直接取当前时间戳
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int add(final IbrInviteRelationMo mo) {
        _log.info("添加邀请关系表");
        // 如果id为空那么自动生成分布式id
        if (mo.getId() == null || mo.getId() == 0) {
            mo.setId(_idWorker.getId());
        }
        mo.setInviteTimestamp(new Date().getTime());
        return super.add(mo);
    }
}
