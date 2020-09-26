package cn.error0.controller;

import cn.error0.Annotation.Autowire;
import cn.error0.Annotation.Controller;
import cn.error0.Annotation.Mapping;
import cn.error0.services.UserService;

import javax.servlet.http.HttpServletRequest;


@Controller
@Mapping("/")
public class UserController {

     @Autowire
     private UserService service;


    @Mapping("getUser")
    public String getUser()
    {
        return service.getOneUser().toString();
    }
    @Mapping("hello")
    public String printf(String msg, int code, boolean flag, char c, HttpServletRequest req)
    {

        System.out.println(code);
        System.out.println(flag);
        System.out.println(c);
        System.out.println(req.getRequestURI());
        return msg;

    }
}
