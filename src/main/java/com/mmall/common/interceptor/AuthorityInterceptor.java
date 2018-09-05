package com.mmall.common.interceptor;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {

    /**
     * @param request
     * @param response
     * @param handler  controller标注的类就是一个handler ,而他的方法就是一个HandlerMethod的类型对象，可以拿到方法名，参数，或者返回类型
     * @return         如果return为false的话，那么不会进行controller方法，不再进行
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("preHandler");
        //请求中HandlerMethod
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String methodName = handlerMethod.getMethod().getName();
        //simpleName是返回的类名，而getname是获得包括包路径的类名，可以解决不同包下类名重复的问题
        String className = handlerMethod.getBean().getClass().getSimpleName();
        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap = request.getParameterMap();
        Iterator iterable = paramMap.entrySet().iterator();
        while (iterable.hasNext()) {
            Map.Entry entry = (Map.Entry) iterable.next();
            String mapkey = (String) entry.getKey();
            //为了代码的健壮性，判断其是否为String数组
            String mapValue = StringUtils.EMPTY;

            Object obj = entry.getValue();
            if (obj instanceof String[]) {
                String[] strs = (String[]) obj;
                mapValue = Arrays.toString(strs);
            }
            requestParamBuffer.append(mapkey).append("=").append(mapValue);
        }

         User user = null;
         String loginToken = CookieUtil.readLoginToken(request);

        /**两种方式
         * 登陆请求可以通过配置里面不接收manage/user/login.do
         * 也可以在interceptor当中进行判断，通过类名和方法名来排除登陆页面
         * 如下
         */
//        if (StringUtils.equals(className, "UserMangageController") && StringUtils.equals("login", methodName))
//            return true;
        //对于富文本的上传，返回值必须为指定的类型，返回map类型


        if (StringUtils.isNotEmpty(loginToken)) {
             String userJsonStr = RedisShardedPoolUtil.get(loginToken);
             user = JsonUtil.string2Obj(userJsonStr, User.class);
               }

        if (user == null || (user.getRole().intValue() != Const.Role.ROLE_ADMIN)) {


            //返回false,既不会调用controller
            //这里要添加reset,否则报异常，
            //todo

            /**
             * 将response交给interceptor进行托管，应该设置一些配置
             * 细化操作细节
             */
            response.reset();
            response.setCharacterEncoding("UTF-8");//设置编码，否则会乱码
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            if (user == null) {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals("richtextImgUpload", methodName)) {//返回一个map的序列化
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "请登录管理员");
                    out.print(JsonUtil.obj2String(resultMap));

                } else {


                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户未登陆")));
                }
            } else {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals("richtextImgUpload", methodName)) {//返回一个map的序列化
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权操作");
                    out.print(JsonUtil.obj2String(resultMap));

                } else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户不是管理员")));
                }
            }
            out.flush();
            out.close();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandler");

    }

    @Override
    /**
     * 如果返回的modelandview。下面的方法会在渲染之前进行执行
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("afterHandler");
    }


}
