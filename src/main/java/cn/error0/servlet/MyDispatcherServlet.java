package cn.error0.servlet;

import cn.error0.Annotation.Autowire;
import cn.error0.Annotation.Controller;
import cn.error0.Annotation.Service;
import cn.error0.controller.UserController;
import cn.error0.services.Impl.UserServiceImpl;
import cn.error0.services.UserService;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class MyDispatcherServlet extends HttpServlet {

    private static List<String> PagesPath=new ArrayList<>();
    private static List<String> Names=new ArrayList<>();
    private static Map<String,Object> Singleton=new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        String Parameter=config.getInitParameter("contextConfigLocation");
        //加载相关配置
        loadConfig(Parameter);
        //扫描注解
        doScan();
        //IOC注入
        ioc();

        //映射url
//        HeaderMap();

    }

    private void ioc() {

        for(String name:Names)
        {
            try {
                Class aclass = Class.forName(name);
                Object bean=aclass.newInstance();
                Field[] fields = bean.getClass().getDeclaredFields();
                for(Field field:fields)
                {
                    if(field.isAnnotationPresent(Autowire.class))
                    {
                        String fieldName=field.getType().getName();
                         Object object=Singleton.get(fieldName);
//                        UserService object=new UserServiceImpl();
                         if(object!=null)
                         {
                             field.setAccessible(true);
                             field.set(object,null);
                         }
                    }
                }

            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }

    }

    private void loadConfig(String parameter)   {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(parameter);
            Element root = document.getRootElement();
            for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
                Element element = it.next();
                PagesPath.add(element.attribute("base-package").getValue());
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
    private void doScan() {
        //1、读取class文件路径获取全限定名
        //2、扫描注解
        if (PagesPath.isEmpty()) {
            return;
        }
        String root = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        for (int i = 0; i < PagesPath.size(); i++) {
            String path = PagesPath.get(i).replaceAll("\\.", "/");
            readFile(root + path);
        }
        for (int i = 0; i < Names.size(); i++) {
            try {
                Class aclass = Class.forName(Names.get(i));
                if (aclass.isAnnotationPresent(Service.class)) {
                    Class[] interfaces = aclass.getInterfaces();
                    for (Class c : interfaces) {
                        Singleton.put(c.getName(),aclass.newInstance());
                    }
                }
                else if (aclass.isAnnotationPresent(Controller.class)) {
                    Singleton.put(aclass.getName(),aclass.newInstance());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

        }
    }
    private void readFile(String path) {
        File filePath=new File(path);
        List<File> files= new LinkedList<>();
        files.add(filePath);

        for(int i=0;i<files.size();i++)
        {
            if(files.get(i).isDirectory())
            {
                File[] dir=files.get(i).listFiles();
                for(File item:dir)
                {
                    files.add(item);
                }
            }
            else {
                if(files.get(i).getName().endsWith(".class"))
                {
                    String ClassPath=files.get(i).getPath();
                    String classes="\\classes\\";
                    String name=ClassPath.substring(ClassPath.indexOf(classes)+classes.length(),ClassPath.lastIndexOf(".")).replace("\\",".");
                    Names.add(name);
                }
            }
        }

    }

    private String lower(char[] str) {
        for(int i=0;i<str.length;i++)
        {
            if(str[i]>='A'&&str[i]<='Z')
            {
                str[i]+=32;
            }
        }
        return new String(str);
    }

}
