package rebue.ibr.svc.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import rebue.ibr.dao.IbrBuyRelationDao;
import rebue.ibr.dic.RelationSourceDic;
import rebue.ibr.jo.IbrBuyRelationJo;
import rebue.ibr.mapper.IbrBuyRelationMapper;
import rebue.ibr.mo.IbrBuyRelationMo;
import rebue.ibr.svc.IbrBuyRelationSvc;
import rebue.robotech.svc.impl.BaseSvcImpl;

/**
 * 购买关系
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
public class IbrBuyRelationSvcImpl
        extends BaseSvcImpl<java.lang.Long, IbrBuyRelationJo, IbrBuyRelationDao, IbrBuyRelationMo, IbrBuyRelationMapper>
        implements IbrBuyRelationSvc {

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    private static final Logger _log = LoggerFactory.getLogger(IbrBuyRelationSvcImpl.class);

    /**
     * 该方法已经被修改，没有传id进来将无法添加，因为id就是订单
     * 详情id，按理不可能自动生成
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public int add(final IbrBuyRelationMo mo) {
        _log.info("ibrBuyRelationSvc.add: 添加购买关系 mo-", mo);
        // 如果id为空那么自动生成分布式id
        if (mo.getId() == null || mo.getId() == 0) {
            _log.error("添加购买关系失败,id(订单详情id)没有传进来");
            throw new IllegalArgumentException("添加购买关系失败");
        }
        return super.add(mo);
    }

    @Resource
    private IbrBuyRelationSvc selfSvc;

    /**
     * 在指定的父节点下插入新节点
     *
     * @param parent
     *            父节点购买关系
     * @param buyerId
     *            买家ID
     * @param paidNotifyTimestamp
     *            收到支付通知时的时间戳
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     */
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void insertNode(final IbrBuyRelationMo parent, final Long buyerId, final Long currentDetalId,
            final Long paidNotifyTimestamp, final RelationSourceDic relationSource, final Integer maxChildernCount) {
        _log.info(
                "ibrBuyRelationSvc.insertNode: 在指定的父节点下插入新节点 parent-{},buyerId-{},currentDetalId-{},paidNotifyTimestamp-{},relationSource-{},maxChildernCount-{}",
                parent, buyerId, currentDetalId, paidNotifyTimestamp, relationSource, maxChildernCount);

        // 插入节点的左值 = 父节点原来的右值
        final Long insertLeftValue = parent.getRightValue();
        // 插入节点的右值 = 插入节点的左值 + 1
        final Long insertRightValue = insertLeftValue + 1;
        _log.debug("计算插入新节点的左右值: 左值-{}, 右值-{}", insertLeftValue, insertRightValue);

        _log.debug("在插入新节点前更新父节点的子节点数+=1");
        _mapper.updateChildrenCountOfParentBeforeInsertNode(parent.getId(), 1, maxChildernCount);

        _log.debug("在插入新节点前更新这棵树的右值");
        _mapper.updateRightValueBeforeInsertNode(parent.getGroupId(), 1, parent.getRightValue());

        _log.debug("在插入新节点前更新这棵树的左值");
        _mapper.updateLeftValueBeforeInsertNode(parent.getGroupId(), 1, parent.getRightValue());

        // 添加新节点
        final IbrBuyRelationMo mo = new IbrBuyRelationMo();
        mo.setId(currentDetalId);
        mo.setGroupId(parent.getGroupId());
        mo.setBuyerId(buyerId);
        mo.setPaidNotifyTimestamp(paidNotifyTimestamp);
        mo.setParentId(parent.getId());
        mo.setRelationSource((byte) relationSource.getCode());
        mo.setLeftValue(insertLeftValue);
        mo.setRightValue(insertRightValue);
        _log.debug("插入新的节点: {}", mo);
        selfSvc.add(mo);
    }

    /**
     * 获取买家最早未匹配满的购买节点
     *
     * @param groupId
     *            分组ID，其实就是销售价格*100
     * @param buyerId
     *            买家ID
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     * @return 最早购买记录，如果没有则返回null
     */
    @Override
    public IbrBuyRelationMo getNotFullAndEarlestBuyRelationOfBuyer(final Long groupId, final Long buyerId,
            final Integer maxChildernCount) {
        _log.info(
                "ibrBuyRelationSvc.getNotFullAndEarlestBuyRelationOfBuyer: 获取买家最早未匹配满的购买节点 groupId-{},buyerId-{},maxChildernCount-{}",
                groupId, buyerId, maxChildernCount);
        return _mapper.getEarlestBuyRelationOfBuyer(groupId, buyerId, maxChildernCount);
    }

    /**
     * 获取最近邀请人的最早未匹配满的购买关系记录
     *
     * @param groupId
     *            分组ID，其实就是销售价格*100
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     * @return 最早购买记录，如果没有则返回null
     */
    @Override
    public IbrBuyRelationMo getNotFullAndEarlestBuyRelationOfLatestInviter(final Long groupId,
            final Integer maxChildernCount) {
        _log.info(
                "ibrBuyRelationSvc.getNotFullAndEarlestBuyRelationOfLatestInviter: 获取最近邀请人的最早未匹配满的购买关系记录 groupId-{},maxChildernCount-{}",
                groupId, maxChildernCount);
        return _mapper.getNotFullAndEarlestBuyRelationOfLatestInviter(groupId, maxChildernCount);
    }

    /**
     * 获取最早未匹配满的购买关系记录
     *
     * @param groupId
     *            分组ID，其实就是销售价格*100
     * @param maxChildernCount
     *            最大子节点的数量，其实就是最多有多少个下家，目前规则是2家
     * @return 最早购买记录，如果没有则返回null
     */
    @Override
    public IbrBuyRelationMo getNotFullAndEarlestBuyRelation(final Long groupId, final Integer maxChildernCount) {
        _log.info("ibrBuyRelationSvc.getNotFullAndEarlestBuyRelation: 获取最早未匹配满的购买关系记录 groupId-{},maxChildernCount-{}",
                groupId, maxChildernCount);
        return _mapper.getNotFullAndEarlestBuyRelation(groupId, maxChildernCount);
    }
}
