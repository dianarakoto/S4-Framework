package etu2000.framework.servlet;

import etu2000.framework.Mapping;
import etu2000.framework.annotation.Url;
import etu2000.framework.ModelView;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            throws Exception, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException {
        response.setContentType("text/html;charset=UTF-8");
        try ( PrintWriter out = response.getWriter()) {
            out.println("<p>"+request.getRequestURL()+"</p>");
            out.println("<p>Voici le contenu de l'hashmap:");
           for (Map.Entry<String, Mapping> entry : mappingUrls.entrySet()) {
               out.println("-Url: "+entry.getKey()+"; Class: "+entry.getValue().getClassName()+"; Method: "+entry.getValue().getMethod());
           }
            String url = request.getRequestURI().substring(request.getContextPath().length()+1);
            if(this.getMappingUrls().containsKey(url)){
                Mapping mapping = this.getMappingUrls().get(url);
                Class clazz = Class.forName(mapping.getClassName());
                Object object = clazz.getConstructor().newInstance();
                Method method = clazz.getDeclaredMethod(mapping.getMethod());
                Object returnObject = method.invoke(object,(Object[])null);
                if(returnObject instanceof ModelView){
                    ModelView modelView = (ModelView)returnObject;
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher(modelView.getView());
                    HashMap<String,Object> data= modelView.getData();
                    int i = 0;
                    for(HashMap.Entry<String,Object> d : data.entrySet()){
                      request.setAttribute(d.getKey(),d.getValue());
                      i++;
                    }
                    out.println(i);
                    requestDispatcher.forward(request,response);
                }
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(FrontServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
