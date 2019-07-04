package rebue.ibr.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rebue.ibr.mo.IbrBuyRelationMo;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.IdRo;
import rebue.robotech.ro.Ro;
import rebue.wheel.OkhttpUtils;
import rebue.wheel.RandomEx;

/**
 * 购买关系
 *
 * @mbg.generated 自动生成的注释，如需修改本注释，请删除本行
 */
public class IbrBuyRelationTests {

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    private static final Logger _log = LoggerFactory.getLogger(IbrBuyRelationTests.class);

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    private final String hostUrl = "http://127.0.0.1:9800";

    /**
     * @mbg.generated 自动生成，如需修改，请删除本行
     */
    private final ObjectMapper _objectMapper = new ObjectMapper();

    /**
     *  测试基本的增删改查
     *
     *  @mbg.generated 自动生成，如需修改，请删除本行
     */
    @Test
    public void testCrud() throws IOException, ReflectiveOperationException {
        IbrBuyRelationMo mo = null;
        for (int i = 0; i < 20; i++) {
            mo = (IbrBuyRelationMo) RandomEx.randomPojo(IbrBuyRelationMo.class);
            mo.setId(null);
            _log.info("添加购买关系的参数为：" + mo);
            final String addResult = OkhttpUtils.postByJsonParams(hostUrl + "/ibr/buy-relation", mo);
            _log.info("添加购买关系的返回值为：" + addResult);
            final IdRo idRo = _objectMapper.readValue(addResult, IdRo.class);
            _log.info(idRo.toString());
            Assert.assertEquals(ResultDic.SUCCESS, idRo.getResult());
            mo.setId(Long.valueOf(idRo.getId()));
        }
        final String listResult = OkhttpUtils.get(hostUrl + "/ibr/buy-relation?pageNum=1&pageSize=5");
        _log.info("查询购买关系的返回值为：" + listResult);
        _log.info("获取单个购买关系的参数为：" + mo.getId());
        final String getByIdResult = OkhttpUtils.get(hostUrl + "/ibr/buy-relation/get-by-id?id=" + mo.getId());
        _log.info("获取单个购买关系的返回值为：" + getByIdResult);
        _log.info("修改购买关系的参数为：" + mo);
        final String modifyResult = OkhttpUtils.putByJsonParams(hostUrl + "/ibr/buy-relation", mo);
        _log.info("修改积分日志类型的返回值为：" + modifyResult);
        final Ro modifyRo = _objectMapper.readValue(modifyResult, Ro.class);
        _log.info(modifyRo.toString());
        Assert.assertEquals(ResultDic.SUCCESS, modifyRo.getResult());
        _log.info("删除购买关系的参数为：" + mo.getId());
        final String deleteResult = OkhttpUtils.delete(hostUrl + "/ibr/buy-relation?id=" + mo.getId());
        _log.info("删除购买关系的返回值为：" + deleteResult);
        final Ro deleteRo = _objectMapper.readValue(deleteResult, Ro.class);
        _log.info(deleteRo.toString());
        Assert.assertEquals(ResultDic.SUCCESS, deleteRo.getResult());
    }
}
