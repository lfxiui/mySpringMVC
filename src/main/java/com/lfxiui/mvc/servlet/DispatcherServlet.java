package com.lfxiui.mvc.servlet;

import com.lfxiui.mvc.annotation.Controller;
import com.lfxiui.mvc.annotation.RequestMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

/**
 * @author Fuxi
 */
public class DispatcherServlet extends HttpServlet {
    private Properties properties = new Properties();
    private List<String> classNames = new ArrayList<>();
    private Map<String, Object> ioc = new HashMap<>(16);
    private Map<String, Method> handlerMapping = new HashMap<>(16);
    private Map<String, Object> controllerMap = new HashMap<>(16);

    @Override
    public void init(ServletConfig config) throws ServletException {
        loadConfig(config.getInitParameter("contextConfigLocation"));
        scannerPackage(properties.getProperty("scanner.package"));
        initController();
        initHandlerMapping();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().replace("/mySpringMVC", "");
        Method method = handlerMapping.get(path);
        Object controller = controllerMap.get(path);
        if (method == null || controller == null) {
            resp.getWriter().write("404 NOT FOUND");
        } else {
            try {
                method.invoke(controller, req, resp);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 加载配置
     *
     * @param location 配置文件名称地址
     */
    private void loadConfig(String location) {
        System.out.println("loadConfig");
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 扫描用户配置的包
     *
     * @param packageName 包名
     */
    private void scannerPackage(String packageName) {
        System.out.println("scannerPackage");
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scannerPackage(packageName + "." + file.getName());
            } else {
                String className = packageName + "." + file.getName().replace(".class", "");
                System.out.println("Scanner: " + className);
                classNames.add(className);
            }
        }
    }

    /**
     * 创建controller实例
     */
    private void initController() {
        System.out.println("initController");
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(Controller.class)) {
                    ioc.put(toLowerFirstWord(className), clazz.newInstance());
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                continue;
            }

        }
    }

    /**
     * 初始化映射器
     */
    private void initHandlerMapping() {
        System.out.println("initHandlerMapping");
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            try {
                Class<?> clazz = Class.forName(entry.getKey());
                if (!clazz.isAnnotationPresent(Controller.class)) {
                    continue;
                }

                StringBuilder baseUrl = new StringBuilder();
                if (clazz.isAnnotationPresent(RequestMapping.class)) {
                    RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                    baseUrl.append(requestMapping.path());
                }
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(RequestMapping.class)) {
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        baseUrl.append(requestMapping.path());
                        String requestUrl = baseUrl.toString();
                        handlerMapping.put(requestUrl, method);
                        controllerMap.put(requestUrl, entry.getValue());
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    /**
     * 类名首字母改成小写
     *
     * @param className
     * @return
     */
    private String toLowerFirstWord(String className) {
        char firstWord = className.charAt(0);
        return className.replace(String.valueOf(firstWord), String.valueOf(firstWord).toLowerCase());
    }
}
