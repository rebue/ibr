package rebue.ibr.dic;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

import rebue.wheel.baseintf.EnumBase;

/**
 * <pre>
 * 匹配方案字典类
 * 1: 优先匹配给自己( ①.匹配自己 -> ②.匹配给邀请人 -> ③.匹配给最早下单的订单详情 )
 * 2: 优先匹配给指定人( ①.匹配给指定人 -> ②.方案1 )
 * </pre>
 */
public enum MatchSchemeDic implements EnumBase {

    /**
     * 1: 优先匹配给自己( ①.匹配自己 -> ②.匹配给邀请人 -> ③.匹配给最早下单的订单详情 )
     **/
    SELF(1),

    /**
     * 2: 优先匹配给指定人( ①.匹配给指定人 -> ②.方案1 )
     **/
    SPECIFIED_PERSON(2);

    /**
     * 枚举的所有项，注意这个变量是静态单例的
     */
    private static Map<Integer, EnumBase> valueMap;
    // 初始化map，保存枚举的所有项到map中以方便通过code查找
    static {
        valueMap = new HashMap<>();
        for (final EnumBase item : values()) {
            valueMap.put(item.getCode(), item);
        }
    }

    /**
     * jackson反序列化时，通过code得到枚举的实例 注意：此方法必须是static的方法，且返回类型必须是本枚举类，而不能是接口EnumBase
     * 否则jackson将调用默认的反序列化方法，而不会调用本方法
     */
    @JsonCreator
    public static MatchSchemeDic getItem(final int code) {
        final EnumBase result = valueMap.get(code);
        if (result == null) {
            throw new IllegalArgumentException("输入的code" + code + "不在枚举的取值范围内");
        }
        return (MatchSchemeDic) result;
    }

    private int code;

    /**
     * 构造器，传入code
     */
    MatchSchemeDic(final int code) {
        this.code = code;
    }

    /**
     * @return jackson序列化时，输出枚举实例的code
     */
    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name();
    }

}
