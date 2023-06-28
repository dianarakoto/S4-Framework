package etu2000.framework.servlet;

import etu2000.framework.Mapping;
import etu2000.framework.annotation.Url;
import etu2000.framework.ModelView;
import etu2000.framework.FileUpload;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.sql.Date;
import java.net.*;
import java.util.logging.*;

@MultipartConfig
public class FrontServlet extends HttpServlet {
    int BYTE_SIZE = 8192;
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
                Method[] methods = clazz.getDeclaredMethods();
                Method method = null;
                for (Method methode : methods) {
                    if(methode.getName() == mapping.getMethod()){
                        method = methode;
                    }
                }

                Object[] arguments = null;
                if(request.getParameterMap() != null){
                    Map<String, String[]> parameter = request.getParameterMap();
                    Set<String> parameterName = parameter.keySet();                    
                    String[] attribute= parameterName.toArray(new String[parameterName.size()]);
                    Field[] objectAttributes= object.getClass().getDeclaredFields();
                    for(Field field : objectAttributes){
                        try{
                            if(field.getType() == FileUpload.class) {
                                Method methody= object.getClass().getMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), field.getType());
                                Collection<Part> files = request.getParts();
                                FileUpload file = this.fileTraitement(files, field);
                                methody.invoke(object,file);
                            }
                        } catch(Exception e){
                            out.println(e.getMessage());
                        }
                    }
                    this.setAttribute(request,attribute,objectAttributes,object);
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if(parameterTypes.length != 0){
                        arguments = new Object[parameterTypes.length];
                        Parameter[] parameters = method.getParameters();
                        int arg = 0;
                        for (Parameter parametre : parameters) {
                            String parametreName = parametre.getName();
                            for (int k = 0; k<attribute.length; k++){
                                if(attribute[k].equals(parametreName)){
                                    arguments[arg] = cast(request, parametre, object);
                                    arg++;
                                }
                            }
                        }
                    }
                }

                Object returnObject = method.invoke(object,arguments);
                if(returnObject != null){   
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
                    else {
                        out.println(returnObject);
                    }
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

    public void setAttribute(HttpServletRequest request,String[] attribute, Field[] att,Object o){
        try{
            for(int i=0; i<att.length; i++){
                for(int j=0; j<attribute.length; j++){
                    if(att[i].getName().toLowerCase().equalsIgnoreCase(attribute[j].toLowerCase())){
                        Method method= o.getClass().getMethod("set" + att[i].getName().substring(0, 1).toUpperCase() + att[i].getName().substring(1), att[i].getType());
                        if(att[i].getType() == String.class) method.invoke(o, request.getParameter(att[i].getName()));
                        if(att[i].getType() == int.class)  method.invoke(o, Integer.parseInt(request.getParameter(att[i].getName())));
                        if(att[i].getType() == double.class)  method.invoke(o, Double.parseDouble(request.getParameter(att[i].getName())));
                        if(att[i].getType() == Date.class)  method.invoke(o, Date.valueOf(request.getParameter(att[i].getName())));
                        if(att[i].getType() == float.class) method.invoke(o, Float.parseFloat(request.getParameter(att[i].getName())));
                    }
                }                           
            }
        }catch(Exception e){
        
        }
    }

    public Object cast(HttpServletRequest request, Parameter parametre, Object o) {
        try {
            Method method = o.getClass().getMethod("get" + parametre.getName().substring(0, 1).toUpperCase() + parametre.getName().substring(1));
            return method.invoke(o);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private FileUpload fileTraitement( Collection<Part> files, Field field){
        FileUpload file = new FileUpload();
        String name = field.getName();
        boolean exists = false;
        String filename = null;
        Part filepart = null;
        for( Part part : files ){
            if( part.getName().equals(name) ){
                filepart = part;
                exists = true;
                break;
            }
        }
        try(InputStream io = filepart.getInputStream()){
            ByteArrayOutputStream buffers = new ByteArrayOutputStream();
            byte[] buffer = new byte[(int)filepart.getSize()];
            int read;
            while( ( read = io.read( buffer , 0 , buffer.length )) != -1 ){
                buffers.write( buffer , 0, read );
            }
            file.setName( this.getFileName(filepart) );
            file.setBytes( buffers.toByteArray() );
            return file;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        String[] parts = contentDisposition.split(";");
        for (String partStr : parts) {
            if (partStr.trim().startsWith("filename"))
                return partStr.substring(partStr.indexOf('=') + 1).trim().replace("\"", "");
        }
        return null;
    }

}