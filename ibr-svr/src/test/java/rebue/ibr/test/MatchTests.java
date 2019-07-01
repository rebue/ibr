package rebue.ibr.test;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import rebue.ibr.mo.IbrInviteRelationMo;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.IdRo;
import rebue.wheel.OkhttpUtils;

/**
 * 匹配测试类
 */
public class MatchTests {

    private static final Logger _log          = LoggerFactory.getLogger(MatchTests.class);

    private final String        hostUrl       = "http://127.0.0.1:9800";

    private final ObjectMapper  _objectMapper = new ObjectMapper();

    /**
     * 测试匹配
     */
    @Test
    public void testMatch() throws IOException {
        _log.info("1. 添加邀请关系");
        final IbrInviteRelationMo inviteRelationMo = new IbrInviteRelationMo();
        inviteRelationMo.setInviterId(3L);
        inviteRelationMo.setInviteeId(4L);
        _log.info("添加邀请关系表的参数为：" + inviteRelationMo);
        final String addResult = OkhttpUtils.postByJsonParams(hostUrl + "/ibr/invite-relation", inviteRelationMo);
        _log.info("添加邀请关系表的返回值为：" + addResult);
        final IdRo idRo = _objectMapper.readValue(addResult, IdRo.class);
        _log.info(idRo.toString());
        Assert.assertEquals(ResultDic.SUCCESS, idRo.getResult());
        inviteRelationMo.setId(Long.valueOf(idRo.getId()));
    }

}
