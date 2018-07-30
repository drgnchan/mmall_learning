package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value="login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(HttpServletResponse httpServletResponse,String username, String password, HttpSession session, HttpServletRequest httpServletRequest){
        ServerResponse<User> response=iUserService.login(username,password);

        if(response.isSuccess()){

            User user=response.getData();
            if(user.getRole()==Const.Role.ROLE_ADMIN){
                CookieUtil.wtriteLoginToken(httpServletResponse,session.getId());
                RedisShardedPoolUtil.setEx(session.getId(),Const.RedisCacheExTime.REDIS_SESSION_EXTIME,JsonUtil.obj2String(response.getData()));
                return response;
            }else{
                return ServerResponse.createByErrorMessage("不是管理员,无法登录");
            }
        }
        return response;
    }
}
