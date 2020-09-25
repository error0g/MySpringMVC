import cn.error0.Annotation.Autowire;
import cn.error0.controller.UserController;
import cn.error0.services.Impl.UserServiceImpl;
import cn.error0.services.UserService;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;

public class Test {

   @org.junit.Test
    public void getPropertyTest() throws IllegalAccessException, InstantiationException {

        Map<String,Object> map= new HashMap();
        map.put("cn.error.userService",UserServiceImpl.class.newInstance());

        Field[] declaredFields =UserController.class.getDeclaredFields();
        for(Field field:declaredFields)
        {
                if(field.isAnnotationPresent(Autowire.class))
                {
                    for(Map.Entry entry:map.entrySet())
                    {
                      field.setAccessible(true);
                        field.set(map.get("cn.error.userService"),null);
                    }
//                    System.out.println(field.getType().);
                }
        }

    }
}
