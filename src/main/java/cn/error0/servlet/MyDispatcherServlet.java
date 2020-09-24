package cn.error0.servlet;

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
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;

public class MyDispatcherServlet extends HttpServlet {

    private static List<String> PagesPath=new ArrayList<>();

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
//        ioc();
        //映射url
//        HeaderMap();

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
        //1、读取class文件路径
        //2、反射类读取读取注解
        if(PagesPath.isEmpty())
        {
            return;
        }
        for(int i=0;i<PagesPath.size();i++)
        {

            String root=Thread.currentThread().getContextClassLoader().getResource("").getPath();
            String path=PagesPath.get(i).replaceAll("\\.", "/");
            readFile(root+path);
        }
    }

    private void readFile(String path) {
        File filePath=new File(path);
        System.out.println(path);
        List<File> files= new ArrayList<>(Arrays.asList(filePath.listFiles()));
        for(File file:files)
        {
            if(file.isDirectory())
            {
                files.add(file);
            }
            else {
                if(file.getName().endsWith(".class"))
                {
                    System.out.println(file.getName());
                }
            }
        }

    }

}
