package com.yx.controller;

import com.yx.model.Userinfo;
import com.yx.service.IUserinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * @Author 刘东祥
 * @Date 2022/9/21
 * @Description 登陆管理
 * @Since version-1.0
 */

@Controller
public class LoginController {
    @Autowired
    private IUserinfoService userinfoService;

    @RequestMapping("/loginIn")
    @ResponseBody
    public Map loginIn(Userinfo userinfo, HttpServletRequest request){
        Map map=new HashMap();
        HttpSession session=request.getSession();
        if(session==null){
            map.put("code",404);
            map.put("msg","登录超时了");
            return map;
        }

        Userinfo user=userinfoService.queryUserByNameAndPwd(userinfo);
        if(user==null){
            map.put("code",404);
            map.put("msg","用户名或者密码错误");
            return map;
        }else{
            session.setAttribute("user",user);
            map.put("code",200);
            map.put("user",user);
            map.put("username",user.getUsername());
            return map;
        }

    }


    /**
     * 退出功能
     */
    @RequestMapping("/loginOut")
    public void loginOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session=request.getSession();
        session.invalidate();
        response.sendRedirect(request.getContextPath()+"/login.html");
    }
}
