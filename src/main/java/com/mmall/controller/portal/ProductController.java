package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import com.mmall.vo.ProductDetailVo;
import org.apache.commons.lang.StringUtils;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by geely
 */

@Controller
@RequestMapping("/product/")

public class ProductController {

    @Autowired
    private IProductService iProductService;


//    @RequestMapping("detail.do")
//    @ResponseBody
//    public ServerResponse<ProductDetailVo> detail(Integer productId){
//        System.out.print("转发成功");
//        return iProductService.getProductDetail(productId);
//    }


    /**
     * RestFul风格的接口改进
     *
     * @param productId
     * @return
     */
    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(@PathVariable Integer productId) {
        System.out.print("转发成功");
        return iProductService.getProductDetail(productId);
    }
//    @RequestMapping("list.do")
//    @ResponseBody
//    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword",required = false)String keyword,
//                                         @RequestParam(value = "categoryId",required = false)Integer categoryId,
//                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
//                                         @RequestParam(value = "pageSize",defaultValue = "10") int pageSize,
//                                         @RequestParam(value = "orderBy",defaultValue = "") String orderBy){
//        return iProductService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderBy);
//    }

    /**
     * 占位的地方必须有值，不能为空
     *
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping("/{keyword}/{categoryId}/{pageNum}/{pageSize}/{orderBy}")
    @ResponseBody
    public ServerResponse<PageInfo> list(@PathVariable(value = "keyword") String keyword,
                                         @PathVariable(value = "categoryId") Integer categoryId,
                                         @PathVariable(value = "pageNum") int pageNum,
                                         @PathVariable(value = "pageSize") int pageSize,
                                         @PathVariable(value = "orderBy") String orderBy) {
        return iProductService.getProductByKeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }


    @RequestMapping("/{categoryId}/{pageNum}/{pageSize}/{orderBy}")
    @ResponseBody
    public ServerResponse<PageInfo> listRestfulBadcase(
            @PathVariable(value = "categoryId") Integer categoryId,
            @PathVariable(value = "pageNum") int pageNum,
            @PathVariable(value = "pageSize") int pageSize,
            @PathVariable(value = "orderBy") String orderBy) {
        return iProductService.getProductByKeywordCategory("", categoryId, pageNum, pageSize, orderBy);
    }


    /**
     * 因为上面的第一个参数为String类型，
     * 下面的第一个参数为Integer类型
     * 所以不知道该映射到哪个controller上面去
     * 都有4个资源的占位符
     *
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */


    @RequestMapping("/{keyword}/{pageNum}/{pageSize}/{orderBy}")
    @ResponseBody
    public ServerResponse<PageInfo> listRestfulBadcase(
            @PathVariable(value = "keyword") String keyword,
            @PathVariable(value = "pageNum") int pageNum,
            @PathVariable(value = "pageSize") int pageSize,
            @PathVariable(value = "orderBy") String orderBy) {
        return iProductService.getProductByKeywordCategory("", null, pageNum, pageSize, orderBy);
    }


    /**
     * 下面的两个case是正确的
     *
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    //   /product/category/1/1/10/price_asc
    @RequestMapping("/category/{cadtegoryI}/{pageNum}/{pageSize}/{orderBy}")
    @ResponseBody
    public ServerResponse<PageInfo> listRestful(
            @PathVariable(value = "categoryId") Integer categoryId,
            @PathVariable(value = "pageNum") int pageNum,
            @PathVariable(value = "pageSize") int pageSize,
            @PathVariable(value = "orderBy") String orderBy) {
        return iProductService.getProductByKeywordCategory("", categoryId, pageNum, pageSize, orderBy);
    }

    /**
     * 对于restful风格的url，是用占位符来进行占位的，如果pageNum为NULL,那么可以肯定后面的参数一定都为零
     *
     * @param keyword
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    //   /product/keyword/手机/1/10/price_asc
    @RequestMapping("/keyword/{keyword}/{pageNum}/{pageSize}/{orderBy}")
    @ResponseBody
    public ServerResponse<PageInfo> listRestful(
            @PathVariable(value = "keyword") String keyword,
            @PathVariable(value = "pageNum") int pageNum,
            @PathVariable(value = "pageSize") int pageSize,
            @PathVariable(value = "orderBy") String orderBy) {
        return iProductService.getProductByKeywordCategory("", null, pageNum, pageSize, orderBy);
    }


}
