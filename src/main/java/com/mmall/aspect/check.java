package com.mmall.aspect;

import com.mmall.common.Const;
import com.mmall.pojo.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.net.httpserver.HttpsServerImpl;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * @author Jorker
 * @date 2018/7/20 11:01
 */
@Aspect
@Component
public class check {

    public check(){
        System.out.print("===========this is jorker============");
    }


    //第一个为方法的参数（可以忽略掉），第二个为方法的返回值，第三个为具体的要做切面的包路径，第4个为要切入的方法，第五个为方法内的参数
    @Pointcut("execution(public * com.mmall.controller.backend..*(..))")
    public void isAdmin() {


    }


    @Around("isAdmin()")
    //用ProceedingJoinPoint拿出来切入点的信息
   //Around用的为proceedingjoinPoint 其他的用jointpoint来获取织入点的信息
    public Object check(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.print("===========this is jorker============");
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequet = servletRequestAttributes.getRequest();
        HttpServletResponse httpServletResponse = servletRequestAttributes.getResponse();
        HttpSession httpSession = httpServletRequet.getSession();

        Object[] objects=proceedingJoinPoint.getArgs();//获取参数列表
        User user = (User) httpSession.getAttribute(Const.CURRENT_USER);
        if (user == null || user.getRole() != Const.Role.ROLE_ADMIN) {
            System.out.print("===========no ability============");
            httpServletRequet.getRequestDispatcher("/product/list.do").forward(httpServletRequet,httpServletResponse);//将request的数据传过去
            //     httpServletResponse.sendRedirect("/product/detail.do");
            return null;
        }

/**
 * reponse重定向是post方式，在controller中不指明method ，则post和get都可以，但是只要指明了方式，那么只能用指明的方法进行传参
   当上面获取了对应方法的参数列表后，可以用我们修改后的参数列表进行方法的继续执行
   jointpoint.getTarget
 */

         proceedingJoinPoint.proceed(objects);
//

        return null;


    }


}
