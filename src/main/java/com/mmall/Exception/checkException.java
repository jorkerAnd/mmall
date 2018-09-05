package com.mmall.Exception;

/**
 * @author Jorker
 * @date 2018/7/20 11:09
 */
public class checkException extends RuntimeException {
    private Integer code;

    public checkException(Integer code, String msg) {
        //调用父类的构造函数的话只能在开头进行调用
        super(msg);
        this.code = code;
    }


}
