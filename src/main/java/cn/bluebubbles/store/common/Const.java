package cn.bluebubbles.store.common;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yibo
 * @date 2019-01-11 15:34
 * @description 常用的常量
 */
public class Const {

    /**
     * 当前用户
     */
    public static final String CURRENT_USER = "currentUser";

    /**
     * Redis中session的有效时间,单位为秒(60 * 30 == 30分钟)
     */
    public static final int REDIS_SESSION_EXTIME = 60 * 30;

    /**
     * 用户角色
     */
    public interface Role {
        // 普通用户
        int ROLE_CUSTOMER = 0;
        // 管理员
        int ROLE_ADMIN = 1;
    }

    /**
     * 排列商品的顺序（按价格升序或降序）
     */
    public interface ProductListOrderBy {
        Set<String> PRICE_ORDER = Sets.newHashSet("price_desc","price_asc");
    }

    /**
     * 购物车状态：商品是否选中，是否限制用户的购买数量（应对可能库存不足的请况）
     */
    public interface Cart {
        // 购物车中选中商品
        int CHECKED = 1;
        // 购物车中未选中商品
        int UN_CHECKED = 0;

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    /**
     * 商品状态
     */
    public enum ProductStatusEnum {
        /**
         * 商品正在售卖
         */
        ON_SALE("在线", 1);
        private String value;
        private int code;

        ProductStatusEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 支付状态
     */
    public enum OrderStatusEnum {

        /**
         * 已取消
         */
        CANCELED(0, "已取消"),
        /**
         * 未支付
         */
        NO_PAY(10, "未支付"),
        /**
         * 已付款
         */
        PAID(20,"已付款"),
        /**
         * 已发货
         */
        SHIPPED(40,"已发货"),
        /**
         * 订单完成
         */
        ORDER_SUCCESS(50, "订单完成"),
        /**
         * 订单关闭
         */
        ORDER_CLOSE(60,"订单关闭");

        OrderStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    /**
     * 支付宝回调状态
     */
    public interface AlipayCallback {
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY ";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    /**
     * 支付平台，现仅支持支付宝支付
     */
    public enum PayPlatformEnum {

        /**
         * 支付宝支付
         */
        ALIPAY(1, "支付宝");

        PayPlatformEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 支付方式，只支持在线支付
     */
    public enum PaymentTypeEnum {

        /**
         * 支付方式为在线支付
         */
        ONLINE_PAY(1, "在线支付");

        PaymentTypeEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        private String value;
        private int code;

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum paymentTypeEnum : values()) {
                if (paymentTypeEnum.getCode() == code) {
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }
}
