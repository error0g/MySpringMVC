package cn.error0.controller;

import cn.error0.Annotation.Autowire;
import cn.error0.Annotation.Controller;
import cn.error0.Annotation.Mapping;
import cn.error0.services.UserService;

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
}
