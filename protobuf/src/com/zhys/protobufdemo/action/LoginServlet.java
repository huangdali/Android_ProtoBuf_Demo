package com.zhys.protobufdemo.action;


import com.zhys.protobufdemo.protobean.LoginRequestOuterClass;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//次注解需要tomcat7及以上不能才可以运行
@WebServlet("/login.action")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("请求登陆了");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        LoginRequestOuterClass.LoginRequest loginRequest = LoginRequestOuterClass.LoginRequest.parseFrom(request.getInputStream());
        System.out.println("登陆信息：username = " + loginRequest.getUsername() + "\tpwd = " + loginRequest.getPwd());
        LoginRequestOuterClass.LoginResponse.Builder builder = LoginRequestOuterClass.LoginResponse.newBuilder();
        if ("admin".equals(loginRequest.getUsername()) && "132".equals(loginRequest.getPwd())) {
            builder.setCode(0);
            builder.setMsg("登陆成功");
            System.out.println("登陆成功");
        } else {
            builder.setCode(1001);
            builder.setMsg("用户名或密码错误");
            System.out.println("用户名或密码错误");
        }
       builder.build().writeTo(response.getOutputStream());
    }
}
