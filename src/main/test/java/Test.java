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
    public void getPropertyTest() throws IllegalAccessException, InstantiationException, ClassNotFoundException {

        Map<String,Object> map= new HashMap();

       Class cl=Class.forName("cn.error0.controller.UserController");
       Object object=cl.newInstance();
       System.out.println(object);
        Field[] declaredFields =UserController.class.getDeclaredFields();
        for(Field field:declaredFields)
        {
            field.setAccessible(true);
                if(field.isAnnotationPresent(Autowire.class))
                {
                    field.set(object,UserServiceImpl.class.newInstance());

                }
        }
        UserController controller= (UserController) object;
       System.out.println( controller.getUser());

    }
}
