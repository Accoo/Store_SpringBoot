package cn.bluebubbles.store.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author yibo
 * @date 2019-01-13 11:30
 * @description
 */
public class BigDecimalUtil {

    public static BigDecimal add(double a, double b) {
        BigDecimal left = new BigDecimal(Double.toString(a));
        BigDecimal right = new BigDecimal(Double.toString(b));
        return left.add(right);
    }

    public static BigDecimal sub(double a, double b) {
        BigDecimal left = new BigDecimal(Double.toString(a));
        BigDecimal right = new BigDecimal(Double.toString(b));
        return left.subtract(right);
    }

    public static BigDecimal mul(double a, double b) {
        BigDecimal left = new BigDecimal(Double.toString(a));
        BigDecimal right = new BigDecimal(Double.toString(b));
        return left.multiply(right);
    }

    public static BigDecimal div(double a, double b) {
        BigDecimal left = new BigDecimal(Double.toString(a));
        BigDecimal right = new BigDecimal(Double.toString(b));
        // 四舍五入,保留两位小数
        return left.divide(right, 2, RoundingMode.HALF_UP);
    }
}
