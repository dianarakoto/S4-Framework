package etu2000.framework.servlet;

import etu2000.framework.Mapping;
import etu2000.framework.annotation.Url;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FrontServlet extends HttpServlet {
    HashMap<String, Mapping> mappingUrls;

    public HashMap<String, Mapping> getMappingUrls() {
        return mappingUrls;
    }

    public void setMappingUrls(HashMap<String, Mapping> MappingUrls) {
        this.mappingUrls = MappingUrls;
    }

    @Override
    public void init() throws ServletException {
        try{
            mappingUrls = new HashMap<>();
                String packageName = getInitParameter("packages");
                URL root = Thread.currentThread().getContextClassLoader().getResource(packageName.replace(".", "/"));
                for (File file : new File(root.getFile()).listFiles()) {
                    String className = file.getName().replaceAll(".class$", "");
                    Class<?> classes = Class.forName(packageName + "." + className);
                    for (Method method : classes.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(Url.class)) {
                            mappingUrls.put(method.getAnnotation(Url.class).value(), new Mapping(classes.getName(), method.getName()));
                        }
                    }
                }
        }
        catch (Exception e){
            
        }
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            out.println("<p>"+request.getRequestURL()+"</p>");
            out.println("<p>Voici le contenu de l'hashmap:");
            for (Map.Entry<String, Mapping> entry : mappingUrls.entrySet()) {
                out.print("-Url: "+entry.getKey()+"; Class: "+entry.getValue().getClassName()+"; Method: "+entry.getValue().getMethod());
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
