package com.mmall.util;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mmall.pojo.User;

import com.mmall.pojo.testPojo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.*;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jorker
 * @date 2018/8/4 11:04
 */

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // ======================将对象转换为字符串的一些配置=================
        //将对象的所有字段全部列入
        /**
         * ALWAYS是将所有的字段列出，即使为空或者长度为0，也列出来
         * NON_NULL不将空字段进行列出
         * NON_DEFAUT不将赋有默认值的字段列出（ps:通过set方法进行赋值的字段进行列出，如果set的值和默认值相等也不列出。如果set的值不相等，也会将其列出）
         * NON_EMPTY,比null的还要严格，如果size为零或者Null都不进行列出，" "中间有一个空格的也是进行输出的
         *
         */
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);

        //取消默认转换timestamps形式
        /**
         *默认赋值给true，对于时间的字段信息，会打印成时间戳的形式，而不是时间形式
         */
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        //忽略空bean转json的错误，如果一个对象初始化的时候，所有的属性值为null,
        /**
         * 转换为String的时候，调用的为bean的get方法，如果没有get方法，任何设置都不会进行此字段的打印
         * 如果一个类，没有get方法，声明一个对象，将其进行json序列化，为空，下面的属性可以保证不出错，如果为true则会出现错误
         *
         *
         */
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        //所有的日期格式都统一为一下的样式，
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));


        //=====================将字符串转为对象的一些配置（反序列化）===================
        //忽略在json字符串中存在，但是在java对象中不存在对应属性的情况，防止错误发生，出现的多字段的情况，
        // 让这种情况不发生错误，如果反序列化的字符串少字段，并不会因为配置问题而发生错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);





    }

    /**
     * 用一个泛型，将泛型转换为String类型
     *
     * @param <T>
     * @return
     */
    public static <T> String obj2String(T obj) {
        if (obj == null)
            return null;
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("bean to String error", e);
            return null;
        }
    }

    /**
     * 格式化好的字符串
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> String obj2StringPretty(T obj) {
        if (obj == null)
            return null;
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.error("bean to String error", e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<T> claz) {
        if (StringUtils.isEmpty(str) || claz == null)
            return null;
        try {
            return claz.equals(String.class) ? (T) str : objectMapper.readValue(str, claz);
        } catch (IOException e) {
            log.error("String to bean error", e);
            return null;
        }
    }

    public static <T> T string2objList(String str, TypeReference<T> typeReference) {
        if (StringUtils.isEmpty(str) || typeReference == null)
            return null;
        try {
            return (T) (typeReference.getType().equals(String.class) ? str : objectMapper.readValue(str, typeReference));//为什么和上面的不一样，这个需要要进行强转，因为他不会进行泛型的转换，看源码
        } catch (IOException e) {
            log.error("String to bean error", e);
            return null;
        }
    }

    /**
     *
     * @param str
     * @param collections
     * @param elementClassess 可以传入多个为此？类型的数组
     * @param <T>
     * @return
     */
    public static <T> T string2Obj(String str, Class<?> collections, Class<?>... elementClassess) {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collections, elementClassess);
        try {
            return objectMapper.readValue(str, javaType);
        } catch (IOException e) {
            log.warn("error", e);
            return null;
        }
    }

    /**
     * google中的Gson默认不打印null值
     *  Gson gon = new GsonBuilder().serializeNulls().create();
     * @param args
     */
    public static void main(String[] args) {

            testPojo testPojo=new testPojo();

            testPojo.setJorker("");
            testPojo.setSh("jorker");
            Gson gson=new Gson();
            String test=gson.toJson(testPojo);
             log.info(test);

        com.mmall.pojo.testPojo1 testPojo1=gson.fromJson(test, com.mmall.pojo.testPojo1.class);

        System.out.println("end");




//        User user = new User();
//        user.setId(1);
//        user.setEmail("jorker@qq.com");
//        String userJson = JsonUtil.obj2String(user);
//        String userJsonPretty = JsonUtil.obj2StringPretty(user);
//        User user1 = new User();
//
//        user1.setId(2);
//        user1.setEmail("joe@16");
//        List<User> userList = Lists.newArrayList();
//        userList.add(user);
//        userList.add(user1);
//        String userList1 = JsonUtil.obj2StringPretty(userList);


//        log.info("userJson:{}", userJson);
//
//        log.info("userJsonPretty:{}", userList1);
//        List<User> result = JsonUtil.string2Obj(userList1, List.class);//用list.class 会默认返回LinkedHashMap.并不会返回为list<User>
//
//        Gson gon = new GsonBuilder().serializeNulls().create();
//
//        List<User> result1 = gon.fromJson(userList1, new TypeToken<ArrayList<User>>() {
//        }.getType());
//        List<User> result2 = JsonUtil.string2objList(userList1, new TypeReference<List<User>>() {
//        });


//        String h = gon.toJson(user);
//Object result=JsonUtil.string2Obj(userList1,List.class,User.class);
//
//
//        System.out.println("end");


    }


}

