package com.mmall.controller.backend;


import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;


@Controller
@RequestMapping("manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;


    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpServletRequest httpServletRequest, Product product) {
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginCookie(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆,无法获取用户的信息");
        }
        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登陆管理员账户");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充增加产品的业务逻辑
            return iProductService.saveOrUpdateProduct(product);

        } else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpServletRequest httpServletRequest,Integer productId,Integer status) {
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginCookie(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆,无法获取用户的信息");
        }
        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登陆管理员账户");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充增加产品的业务逻辑
            return iProductService.setSaleStatus(productId,status);

        } else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpServletRequest httpServletRequest,Integer productId,Integer status) {
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginCookie(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆,无法获取用户的信息");
        }
        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登陆管理员账户");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充增加产品的业务逻辑
            return iProductService.manageProductDetail(productId);

        } else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getlist(HttpServletRequest httpServletRequest, @RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "10") int pageSize) {
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginCookie(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆,无法获取用户的信息");
        }
        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登陆管理员账户");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充增加产品的业务逻辑
            return iProductService.getProductList(pageNum,pageSize);

        } else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse searchProduct(HttpServletRequest httpServletRequest, String productName,Integer productId,@RequestParam(value="pageNum",defaultValue = "1") int pageNum, @RequestParam(value="pageSize",defaultValue = "10") int pageSize) {
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginCookie(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆,无法获取用户的信息");
        }
        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登陆管理员账户");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充增加产品的业务逻辑
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);

        } else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }

    }
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpServletRequest httpServletRequest,@RequestParam(value="upload_file", required=false) MultipartFile file, HttpServletRequest request){
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginCookie(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登陆,无法获取用户的信息");
        }
        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录,请登陆管理员账户");
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充增加产品的业务逻辑
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);
            String url=PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            Map fileMap= Maps.newHashMap();
            fileMap.put("uri","targetFileName");
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);

        } else {
            return ServerResponse.createByErrorMessage("无操作权限");
        }


    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextUpload(HttpServletRequest httpServletRequest, @RequestParam(value="upload_file", required=false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap=Maps.newHashMap();
//        User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken=CookieUtil.readLoginCookie(httpServletRequest);
        if(StringUtils.isEmpty(loginToken)){
            resultMap.put("success",false);
            resultMap.put("mas","请登录管理员");
            return resultMap;
        }
        String userJsonStr=RedisShardedPoolUtil.get(loginToken);
        User user=JsonUtil.string2Obj(userJsonStr,User.class);
        if (user == null) {
            resultMap.put("success",false);
            resultMap.put("mas","请登录管理员");
            return resultMap;
        }

        if (iUserService.checkAdminRole(user).isSuccess()) {
            //填充增加产品的业务逻辑
            String path=request.getSession().getServletContext().getRealPath("upload");
            String targetFileName=iFileService.upload(file,path);
            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }

            String url=PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;

        } else {
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }


    }




}
