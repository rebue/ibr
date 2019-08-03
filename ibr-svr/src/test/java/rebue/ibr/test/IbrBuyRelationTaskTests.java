package rebue.ibr.test;

import java.io.IOException;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import rebue.ibr.dic.TaskTypeDic;
import rebue.ibr.mo.IbrBuyRelationTaskMo;
import rebue.ibr.to.TempTo;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.IdRo;
import rebue.robotech.ro.Ro;
import rebue.wheel.OkhttpUtils;
import rebue.wheel.RandomEx;

/**
 * 购买关系任务
 *
 * @mbg.generated 自动生成的注释，如需修改本注释，请删除本行
 */
public class IbrBuyRelationTaskTests {

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    private static final Logger _log = LoggerFactory.getLogger(IbrBuyRelationTaskTests.class);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    private final String hostUrl = "http://127.0.0.1:9800";

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    private final ObjectMapper _objectMapper = new ObjectMapper();

    /**
     * 测试基本的增删改查
     *
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    // @Test
    public void testCrud() throws IOException, ReflectiveOperationException {
        IbrBuyRelationTaskMo mo = null;
        for (int i = 0; i < 20; i++) {
            mo = (IbrBuyRelationTaskMo) RandomEx.randomPojo(IbrBuyRelationTaskMo.class);
            mo.setId(null);
            _log.info("添加购买关系任务的参数为：" + mo);
            final String addResult = OkhttpUtils.postByJsonParams(hostUrl + "/ibr/buy-relation-task", mo);
            _log.info("添加购买关系任务的返回值为：" + addResult);
            final IdRo idRo = _objectMapper.readValue(addResult, IdRo.class);
            _log.info(idRo.toString());
            Assert.assertEquals(ResultDic.SUCCESS, idRo.getResult());
            mo.setId(Long.valueOf(idRo.getId()));
        }
        final String listResult = OkhttpUtils.get(hostUrl + "/ibr/buy-relation-task?pageNum=1&pageSize=5");
        _log.info("查询购买关系任务的返回值为：" + listResult);
        _log.info("获取单个购买关系任务的参数为：" + mo.getId());
        final String getByIdResult = OkhttpUtils.get(hostUrl + "/ibr/buy-relation-task/get-by-id?id=" + mo.getId());
        _log.info("获取单个购买关系任务的返回值为：" + getByIdResult);
        _log.info("修改购买关系任务的参数为：" + mo);
        final String modifyResult = OkhttpUtils.putByJsonParams(hostUrl + "/ibr/buy-relation-task", mo);
        _log.info("修改积分日志类型的返回值为：" + modifyResult);
        final Ro modifyRo = _objectMapper.readValue(modifyResult, Ro.class);
        _log.info(modifyRo.toString());
        Assert.assertEquals(ResultDic.SUCCESS, modifyRo.getResult());
        _log.info("删除购买关系任务的参数为：" + mo.getId());
        final String deleteResult = OkhttpUtils.delete(hostUrl + "/ibr/buy-relation-task?id=" + mo.getId());
        _log.info("删除购买关系任务的返回值为：" + deleteResult);
        final Ro deleteRo = _objectMapper.readValue(deleteResult, Ro.class);
        _log.info(deleteRo.toString());
        Assert.assertEquals(ResultDic.SUCCESS, deleteRo.getResult());
    }

    // 添加结算任务
    // @Test
    public void addTask() throws IOException {
        IbrBuyRelationTaskMo mo = new IbrBuyRelationTaskMo();
        mo.setId(1L);
        mo.setExecuteState((byte) 0);
        mo.setExecutePlanTime(new Date());
        mo.setTaskType((byte) TaskTypeDic.SETTLE_COMMISSION.getCode());
        mo.setOrderDetailId(1L);
        _log.info("添加购买关系任务的参数为：" + mo);
        final String addResult = OkhttpUtils.postByJsonParams(hostUrl + "/ibr/buy-relation-task", mo);
        _log.info("添加购买关系任务的返回值为：" + addResult);
    }

    // 开始结算任务
    @Test
    public void orderTask() throws IOException {
        TempTo to = new TempTo();
        to.setTaskId(1L);
        final String addResult = OkhttpUtils.postByJsonParams(hostUrl + "/ibr/execute-order-settle-task", to);
        _log.info("添加购买关系任务的返回值为：" + addResult);
    }
}
