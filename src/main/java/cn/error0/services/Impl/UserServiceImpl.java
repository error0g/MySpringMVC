package cn.error0.services.Impl;

import cn.error0.Annotation.Service;
import cn.error0.entity.User;
import cn.error0.services.UserService;

@Service
public class UserServiceImpl implements UserService {


    @Override
    public User getOneUser() {
        User user=new User();
        user.setName("测试");
       return user;
    }
}
