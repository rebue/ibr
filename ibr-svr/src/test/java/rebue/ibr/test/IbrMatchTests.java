package rebue.ibr.test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import rebue.ibr.dic.MatchSchemeDic;
import rebue.ibr.mo.IbrInviteRelationMo;
import rebue.ibr.to.MatchTo;
import rebue.robotech.dic.ResultDic;
import rebue.robotech.ro.IdRo;
import rebue.robotech.ro.Ro;
import rebue.wheel.OkhttpUtils;

/**
 * 匹配测试类
 */
public class IbrMatchTests {

    private static final Logger _log                     = LoggerFactory.getLogger(IbrMatchTests.class);

    private final String        hostUrl                  = "http://127.0.0.1:9800";

    private final ObjectMapper  _objectMapper            = new ObjectMapper();

    /**
     * 收到支付通知时的时间戳的计数器
     */
    private int                 paidNotifyTimestampCount = 0;

    /**
     * 测试匹配
     */
    @Test
    public void testMatch() throws IOException {
        _log.info("1. 添加邀请关系 买家3邀请买家4");
        final IbrInviteRelationMo inviteRelationMo = new IbrInviteRelationMo();
        inviteRelationMo.setInviterId(3L);
        inviteRelationMo.setInviteeId(4L);
        _log.info("添加邀请关系表的参数为：" + inviteRelationMo);
        final String postResult = OkhttpUtils.postByJsonParams(hostUrl + "/ibr/invite-relation", inviteRelationMo);
        _log.info("添加邀请关系表的返回值为：" + postResult);
        final IdRo idRo = _objectMapper.readValue(postResult, IdRo.class);
        _log.info(idRo.toString());
        Assert.assertEquals(ResultDic.SUCCESS, idRo.getResult());
        inviteRelationMo.setId(Long.valueOf(idRo.getId()));

        _log.info("2. 优先匹配自己 匹配价格9.9 节点1 买家1 应该是根节点");
        postMatch(MatchSchemeDic.SELF, 9.9, 1L, 1L);
        _log.info("3. 优先匹配自己 匹配价格9.9 节点2 买家2 应该匹配给节点1");
        postMatch(MatchSchemeDic.SELF, 9.9, 2L, 2L);
    }

    /**
     * 提交匹配的请求
     * 
     * @param matchScheme
     *            匹配方案
     * @param matchPrice
     *            匹配价格，*100就是分组ID
     * @param id
     *            新节点ID，其实就是订单详情ID
     * @param buyerId
     *            买家ID
     */
    private void postMatch(final MatchSchemeDic matchScheme, final Double matchPrice, final Long id, final Long buyerId)
            throws IOException, JsonParseException, JsonMappingException {
        String postResult;
        final MatchTo to = new MatchTo();
        to.setOrderDetailId(id);
        to.setMatchScheme(matchScheme);
        to.setMatchPrice(BigDecimal.valueOf(matchPrice));
        to.setBuyerId(buyerId);
        to.setPaidNotifyTimestamp(new Date().getTime() + paidNotifyTimestampCount++);
        to.setMaxChildernCount(2);
        _log.info("匹配的参数为：" + to);
        postResult = OkhttpUtils.postByJsonParams(hostUrl + "/ibr/match", to);
        final Ro ro = _objectMapper.readValue(postResult, Ro.class);
        _log.info(ro.toString());
    }

}
