package cn.bluebubbles.store.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;

/**
 * @author yibo
 * @date 2019-01-19 19:46
 * @description
 */
@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 将对象的所有字段全部列入
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        // 取消默认转换timestamps的形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // 忽略空Bean转JSON错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        // 忽略在JSON字符串中存在,但是在Java对象中不存在对应属性的情况
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 将对象转为JSON字符串
     * @param obj 对象
     * @param <T> 对象的类型
     * @return
     */
    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error ", e);
        }
        return null;
    }

    /**
     * 将对象转为格式化好的JSON字符串(格式化为每个字段占一行)
     * @param obj 对象
     * @param <T> 对象的类型
     * @return
     */
    public static <T> String obj2StringPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse object to String error ", e);
        }
        return null;
    }

    /**
     * 将JSON字符串转为对象
     * @param str JSON字符串
     * @param clazz 对象的Class
     * @param <T> 对象的类型
     * @return
     */
    public static <T> T string2Obj(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str) || clazz == null) {
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
        } catch (Exception e) {
            log.warn("Parse String to object error ", e);
        }
        return null;
    }

    /**
     * 将JSON字符串反序列化为指定的对象类型
     * 使用示例:
     *   List<User> userList = JsonUtil.string2Obj(userListJson, new TypeReference<List<User>>() {});
     * @param str JSON字符串
     * @param typeReference 对象的类型引用
     * @param <T> 类型
     * @return
     */
    public static <T> T string2Obj(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));
        } catch (Exception e) {
            log.warn("Parse String to object error ", e);
        }
        return null;
    }

    /**
     * 将JSON字符串反序列化为指定的集合类型
     * 使用示例:
     *   List<User> userList = JsonUtil.string2Obj(userListJson, List.class, User.class);
     * @param str JSON字符串
     * @param collectionClass 集合的Class
     * @param elementClasses 集合中元素的类型
     * @param <T> 返回类型
     * @return
     */
    public static <T> T string2Obj(String str, Class<?> collectionClass, Class<?>... elementClasses) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (Exception e) {
            log.warn("Parse String to object error ", e);
        }
        return null;
    }
}
