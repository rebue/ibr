package rebue.ibr.dic;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;

import rebue.wheel.baseintf.EnumBase;

/**
 * <pre>
 * 
 * 1:匹配购买关系
 * 2:结算-结算返佣金
 * 3:退款成功后重新匹配
 * </pre>
 */
public enum TaskTypeDic implements EnumBase {

    /**
     * 1: 匹配购买关系
     **/
    MATCH_BUY_RELATION(1),

    /**
     * 2:结算-结算返佣金
     **/
    SETTLE_COMMISSION(2),

    /**
     * 3:退款成功后重新匹配
     */
    REFUND_AGAIN_MATCH(3);

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
    public static TaskTypeDic getItem(final int code) {
        final EnumBase result = valueMap.get(code);
        if (result == null) {
            throw new IllegalArgumentException("输入的code" + code + "不在枚举的取值范围内");
        }
        return (TaskTypeDic) result;
    }

    private int code;

    /**
     * 构造器，传入code
     */
    TaskTypeDic(final int code) {
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
