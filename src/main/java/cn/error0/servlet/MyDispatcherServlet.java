package cn.error0.servlet;

import cn.error0.Annotation.Autowire;
import cn.error0.Annotation.Controller;
import cn.error0.Annotation.Mapping;
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
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MyDispatcherServlet extends HttpServlet {

    private static List<String> Path=new ArrayList<>();
    private static List<String> Names=new ArrayList<>();
    private static Map<String,Object> Singleton=new HashMap<>();
    private static Map<String,Object> HandlerMapping=new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Method method= (Method) HandlerMapping.get(req.getRequestURI());
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        if(method==null)
        {
            out.print("404");
        }
        try {
            Object Controller=Singleton.get(method.getDeclaringClass().getName());
            String string= (String) method.invoke(Controller,null);
            out.print(string);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }finally {
            out.close();
        }

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        String Parameter=config.getInitParameter("contextConfigLocation");
        //加载相关配置
        loadConfig(Parameter);
        //扫描注解
        doScan();
        //IOC注入
        Autowire();
        //映射url
       UrlMaping();
    }
    private void loadConfig(String parameter)   {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(parameter);
            Element root = document.getRootElement();
            for (Iterator<Element> it = root.elementIterator(); it.hasNext();) {
                Element element = it.next();
                Path.add(element.attribute("base-package").getValue());
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
    private void doScan() {
        //1、读取class文件路径获取全限定名
        //2、扫描注解

        String root = Thread.currentThread().getContextClassLoader().getResource("").getPath();

        for (int i = 0; i < Path.size(); i++) {
            String path = Path.get(i).replaceAll("\\.", "/");
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

            } catch (ClassNotFoundException | IllegalAccessException |InstantiationException e) {
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
                    String classes="classes";
                    String name=ClassPath.substring(ClassPath.indexOf(classes)+classes.length()+1,ClassPath.lastIndexOf(".")).replace(File.separator,".");
                    Names.add(name);
                }
            }
        }

    }

    private void UrlMaping() {
        for(String name:Names)
        {
            try {
                Class cl = Class.forName(name);
                if(cl.isAnnotationPresent(Controller.class))
                {

                Mapping ClassMapping= (Mapping) cl.getAnnotation(Mapping.class);
                Method[] methods = cl.getMethods();
                    for(Method method:methods)
                    {

                        if(method.isAnnotationPresent(Mapping.class))
                        {
                            StringBuffer url=new StringBuffer();
                            Mapping Mapping= method.getAnnotation(Mapping.class);
                            if(ClassMapping!=null)
                            {
                                url.append(ClassMapping.value());
                            }

                            if(Mapping!=null)
                            {
                                url.append(Mapping.value());
                            }
                            HandlerMapping.put(url.toString(),method);
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void Autowire() {

        for(String name:Names)
        {
            try {
                Class cl=Class.forName(name);
                Field[] fields=cl.getDeclaredFields();
                for(Field field:fields)
                {
                    if(field.isAnnotationPresent(Autowire.class)&&Singleton.get(cl.getName())==null) {
                        field.setAccessible(true);
                        Object object=cl.newInstance();
                        field.set(object,Singleton.get(field.getType().getName()));
                        Singleton.put(cl.getName(),object);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




}
