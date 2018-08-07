package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.net.httpserver.HttpServerImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by geely
 */
@Slf4j
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;


    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpServletRequest httpServletRequest, Integer shippingId) {
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken))
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");

        String userJsonStr = RedisShardedPoolUtil.get(loginToken);

        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(), shippingId);
    }


    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpServletRequest httpServletRequest, Long orderNo) {
       // User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken))
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");

        String userJsonStr = RedisShardedPoolUtil.get(loginToken);

        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(), orderNo);
    }


    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpServletRequest httpServletRequest) {
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken))
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");

        String userJsonStr = RedisShardedPoolUtil.get(loginToken);

        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }


    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpServletRequest httpServletRequest, Long orderNo) {
       // User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken))
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");

        String userJsonStr = RedisShardedPoolUtil.get(loginToken);

        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpServletRequest httpServletRequest, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken))
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");

        String userJsonStr = RedisShardedPoolUtil.get(loginToken);

        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }

    @Transactional
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpServletRequest httpServletRequest, Long orderNo, HttpServletRequest request) {
       // User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken))
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");

        String userJsonStr = RedisShardedPoolUtil.get(loginToken);

        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }
/**
 * 注意getServletContext和getContextPath的区别
 * getServletContext是获取项目发布的路径，在本机的路径就是
 * (127.0.0.1//sell/order)getContextPath是得到上下文的根目录，例如reponse.redirect(getContextPath).forward(request,reponse);
 * sell为application的文件名，那么返回的就是127.0.0.1/sel/的路径
 *
 *
 */
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo, user.getId(), path);

    }


    @RequestMapping(value = "alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {

        Map<String, String> params = Maps.newHashMap();
        Map requestParms = request.getParameterMap();
        //一个map的迭代器
        for (Iterator iter = requestParms.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();//用迭代器要记得强转
            String[] values = (String[]) requestParms.get(name);//map中的get方法会发生强转的问题
            String valueStr = "";
            for (int i = 0; i < values.length; i++)
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";

            params.put(name, valueStr);

        }
        log.info("支付宝回调，sign:{},trade_status:{},参数{}", params.get("sign"), params.get("trade_status"), params.toString());

        //非常重要的，验证回调的正确性，是不是支付宝发的，并且不需要重复通知


        // 除去sign 和sing_type，凡是返回的参数均需要进行验签 源码会移除掉sign
        params.remove("sign_type");
        try {
            //进行支付宝的验签
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSACheckedV2) {
                 log.info("不是支付宝的请求");
                return ServerResponse.createByErrorMessage("非法请求");
            }

        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常");
            return ServerResponse.createByErrorMessage("支付宝回调异常");
        }
/**
 * 如果收到了success就会停止回调
 */
        //todo 验证是否为我这个商城订单或者订单状态是否正确
         ServerResponse serverResponse = iOrderService.aliCallback(params);
         if (serverResponse.isSuccess())

            return Const.AlipayCallback.RESPONSE_SUCCESS;

            return Const.AlipayCallback.RESPONSE_FAILED;


    }

     @Transactional
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus( HttpServletRequest httpServletRequest, Long orderNo) {
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
         String loginToken = CookieUtil.readLoginToken(httpServletRequest);
         if (StringUtils.isEmpty(loginToken))
             return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");

         String userJsonStr = RedisShardedPoolUtil.get(loginToken);

         User user = JsonUtil.string2Obj(userJsonStr, User.class);
         if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        }

        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        log.error("创建成功");
        if (serverResponse.isSuccess()) {
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }


}
