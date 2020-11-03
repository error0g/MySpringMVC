package cn.error0.servlet;

import cn.error0.Annotation.Autowire;
import cn.error0.Annotation.Controller;
import cn.error0.Annotation.Mapping;
import cn.error0.Annotation.Service;

import cn.error0.Model.IModel;
import cn.error0.Resolver.BaseResolver;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.enterprise.inject.Model;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class MyDispatcherServlet extends HttpServlet {
    //需要扫描的目录
    private static List<String> Path=new ArrayList<>();
    //类的全限定名称
    private static List<String> Names=new ArrayList<>();
    //Bean容器
   private static Map<String,Object> Singleton=new HashMap<>();
   //url映射容器
    private static Map<String,Object> HandlerMapping=new HashMap<>();
    //视图解析器
    private  BaseResolver baseResolver=new BaseResolver();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        baseResolver.setPrefix("/");
        baseResolver.setSuffix(".jsp");

        Method method= (Method) HandlerMapping.get(req.getRequestURI());
        PrintWriter out = resp.getWriter();
        if(method!=null)
        {
            Map<String, String[]> parameterMap = req.getParameterMap();
            Set<String> keys = parameterMap.keySet();
            List<Object> list=new LinkedList<>();
            Parameter[] parameters = method.getParameters();
            Iterator iterator=keys.iterator();
            IModel iModel=new IModel();

            try {
                for(int i=0;i<parameters.length||iterator.hasNext();i++) {
                    //根据参数类型 转化类型
                    Class<?> type = parameters[i].getType();
                    if(type.equals(Integer.class)) {
                        list.add(new Integer(parameterMap.get(iterator.next())[0]));
                    }
                    else if(type.equals(Character.class))
                    {
                        list.add((parameterMap.get(iterator.next())[0]));
                    }
                    else if(type.equals(Byte.class))
                    {
                        list.add(new Byte(parameterMap.get(iterator.next())[0]));
                    }
                    else if(type.equals(Long.class))
                    {
                        list.add(new Long(parameterMap.get(iterator.next())[0]));
                    }
                    else if(type.equals(Double.class))
                    {
                        list.add(new Double(parameterMap.get(iterator.next())[0]));
                    }
                    else if(type.equals(Float.class))
                    {
                        list.add(new Float(parameterMap.get(iterator.next())[0]));
                    }
                    else if(type.equals(Short.class))
                    {
                        list.add(new Short(parameterMap.get(iterator.next())[0]));
                    }
                    else if(type.equals(Boolean.class))
                    {
                        list.add(new Boolean(parameterMap.get(iterator.next())[0]));
                    }
                    else if(type.equals(IModel.class))
                    {
                        list.add(iModel);
                    }
                    else if(type.equals(HttpServletRequest.class))
                    {
                        list.add(req);
                    }
                    else if(type.equals(HttpServletResponse.class))
                    {
                        list.add(resp);
                    }
                    else {
                        list.add(type.cast(parameterMap.get(iterator.next())[0]));
                    }

                }

                Object Controller=Singleton.get(method.getDeclaringClass().getName());
                String view= (String) method.invoke(Controller, list.toArray());

                baseResolver.setView(view);
                baseResolver.setModel(iModel);
                baseResolver.forward(req,resp);
            } catch (IllegalAccessException | InvocationTargetException  e) {
                e.printStackTrace();
            }
        }
        else {
            out.print("404");
        }
        out.close();
    }

    @Override
    public void init(ServletConfig config)   {
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
                        //创建Bean实例并加入Singleton容器
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
