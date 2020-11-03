package cn.error0.controller;

import cn.error0.Annotation.Autowire;
import cn.error0.Annotation.Controller;
import cn.error0.Annotation.Mapping;
import cn.error0.Model.IModel;

import cn.error0.services.UserService;
import javax.servlet.http.HttpServletRequest;


@Controller
@Mapping("/")
public class UserController {

     @Autowire
     private UserService service;


    @Mapping("print")
    public String print(String msg, int code, boolean flag, HttpServletRequest req,IModel model)
    {

        model.put("User", service.getOneUser());
        return "index";

    }
}
