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
        // 步骤计数器
        int stepCount = 0;

        _log.info("{}. 添加邀请关系 买家4邀请买家7", ++stepCount);
        postAddInviteRelation(4L, 7L);
        _log.info("{}. 添加邀请关系 买家4邀请买家8", ++stepCount);
        postAddInviteRelation(4L, 8L);
        _log.info("{}. 添加邀请关系 买家4邀请买家9", ++stepCount);
        postAddInviteRelation(4L, 9L);
        _log.info("{}. 添加邀请关系 买家5邀请买家9", ++stepCount);
        postAddInviteRelation(5L, 9L);

        _log.info("{}. 优先匹配自己 匹配价格9.9 节点1 买家1 应该是根节点", ++stepCount);
        postMatch(MatchSchemeDic.SELF, 9.9, 1L, 1L);
        _log.info("{}. 优先匹配自己 匹配价格9.9 节点2 买家2 应该匹配给节点1", ++stepCount);
        postMatch(MatchSchemeDic.SELF, 9.9, 2L, 2L);
        _log.info("{}. 优先匹配自己 匹配价格9.9 节点3 买家3 应该匹配给节点1", ++stepCount);
        postMatch(MatchSchemeDic.SELF, 9.9, 3L, 3L);
        _log.info("{}. 优先匹配自己 匹配价格9.9 节点4 买家4 应该匹配给节点2", ++stepCount);
        postMatch(MatchSchemeDic.SELF, 9.9, 4L, 4L);
        _log.info("{}. 优先匹配自己 匹配价格9.9 节点5 买家5 应该匹配给节点2", ++stepCount);
        postMatch(MatchSchemeDic.SELF, 9.9, 5L, 5L);
        _log.info("{}. 优先匹配自己 匹配价格9.9 节点6 买家6 应该匹配给节点3", ++stepCount);
        postMatch(MatchSchemeDic.SELF, 9.9, 6L, 6L);
        _log.info("{}. 优先匹配自己 匹配价格9.9 节点7 买家7 应该匹配给节点4", ++stepCount);
        postMatch(MatchSchemeDic.SELF, 9.9, 7L, 7L);
        _log.info("{}. 优先匹配自己 匹配价格9.9 节点8 买家6 应该匹配给节点6", ++stepCount);
        postMatch(MatchSchemeDic.SELF, 9.9, 8L, 6L);
        _log.info("{}. 优先匹配自己 匹配价格9.9 节点9 买家9 应该匹配给节点5", ++stepCount);
        postMatch(MatchSchemeDic.SELF, 9.9, 9L, 9L);
    }

    /**
     * 提交添加邀请关系的请求
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
    private void postAddInviteRelation(final Long inviterId, final Long inviteeId) throws IOException, JsonParseException, JsonMappingException {
        final IbrInviteRelationMo inviteRelationMo = new IbrInviteRelationMo();
        inviteRelationMo.setInviterId(inviterId);
        inviteRelationMo.setInviteeId(inviteeId);
        _log.info("添加邀请关系表的参数为：" + inviteRelationMo);
        final String postResult = OkhttpUtils.postByJsonParams(hostUrl + "/ibr/invite-relation", inviteRelationMo);
        _log.info("添加邀请关系表的返回值为：" + postResult);
        final IdRo idRo = _objectMapper.readValue(postResult, IdRo.class);
        _log.info(idRo.toString());
        Assert.assertEquals(ResultDic.SUCCESS, idRo.getResult());
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
        Assert.assertEquals(ResultDic.SUCCESS, ro.getResult());
    }

}
