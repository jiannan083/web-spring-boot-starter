package cn.bfay.web.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 类说明.
 *
 * @author weibo
 * @since 2018/8/30
 */
@Slf4j
public abstract class CommonDecoder extends SpringDecoder {
    protected static ObjectMapper mapper = new ObjectMapper();
    public static final String ITEMS = "list";

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public CommonDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        super(messageConverters);
    }

    /**
     * 转换泛型列表.
     *
     * @param type  类型
     * @param items 数据
     * @return Object
     */
    public static Object convertGenericList(ParameterizedType type,
                                            LinkedHashMap<String, Object> items) {
        return convertGenericList(type, items, ITEMS);
    }

    /**
     * 转换泛型列表.
     *
     * @param type  类型
     * @param items 数据
     * @return Object
     */
    public static Object convertGenericList(ParameterizedType type,
                                            LinkedHashMap<String, Object> items, String itemName) {
        JavaType javaType = getJavaType(type);
        List<LinkedHashMap> linkedHashMapList = (List<LinkedHashMap>) items.get(itemName);
        return MapUtils.isEmpty(items) ? null : mapper.convertValue(linkedHashMapList, javaType);
    }

    /**
     * 转换泛型列表.
     *
     * @param type              类型
     * @param linkedHashMapList 数据
     * @return Object
     */
    public static Object convertGenericList(ParameterizedType type, List<LinkedHashMap> linkedHashMapList) {
        JavaType javaType = getJavaType(type);
        return mapper.convertValue(linkedHashMapList, javaType);
    }

    private static JavaType getJavaType(ParameterizedType type) {
        ParameterizedType pt = type;
        Type pta = pt.getActualTypeArguments()[0];
        return mapper.getTypeFactory()
            .constructParametricType((Class) type.getRawType(), (Class) pta);
    }
}
