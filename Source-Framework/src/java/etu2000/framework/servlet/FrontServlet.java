package etu2000.framework.servlet;

import etu2000.framework.Mapping;
import etu2000.framework.annotation.Url;
import etu2000.framework.annotation.Scope;
import etu2000.framework.annotation.Session;
import etu2000.framework.annotation.RestApi;
import etu2000.framework.annotation.Authentification;
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
import com.google.gson.Gson; 

@MultipartConfig
public class FrontServlet extends HttpServlet {
    int BYTE_SIZE = 8192;
    HashMap<String, Mapping> mappingUrls;
    HashMap<Class, Object> singleton;
    String sessionName;
    String sessionProfile;
    boolean containsFile = false;
    Gson gson = new Gson();

    public HashMap<String, Mapping> getMappingUrls() {
        return mappingUrls;
    }

    public void setMappingUrls(HashMap<String, Mapping> MappingUrls) {
        this.mappingUrls = MappingUrls;
    }

    public HashMap<Class, Object> getSingleton() {
        return singleton;
    }

    public void setSingleton(HashMap<Class, Object> singleton) {
        this.singleton = singleton;
    }

    @Override
    public void init() throws ServletException {
        try{
            mappingUrls = new HashMap<>();
            singleton = new HashMap<>();
            String packageName = getInitParameter("packages");
            this.sessionName = getInitParameter("sessionName");
            this.sessionProfile = getInitParameter("sessionProfile");
            URL root = Thread.currentThread().getContextClassLoader().getResource(packageName.replace(".", "/"));
            for (File file : new File(root.getFile()).listFiles()) {
                String className = file.getName().replaceAll(".class$", "");
                Class<?> classes = Class.forName(packageName + "." + className);
                for (Method method : classes.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(Url.class)) {
                        mappingUrls.put(method.getAnnotation(Url.class).value(), new Mapping(classes.getName(), method.getName()));
                    }
                }
                if(classes.isAnnotationPresent(Scope.class) && classes.getAnnotation(Scope.class).value().equalsIgnoreCase("singleton")){
                    singleton.put(classes, null);
                }
            }

        }
        catch (Exception e){
            
        }
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
        //     out.println("<p>"+request.getRequestURL()+"</p>");
        //     out.println("<p>Voici le contenu de l'hashmap:");
        //    for (Map.Entry<String, Mapping> entry : mappingUrls.entrySet()) {
        //        out.println("-Url: "+entry.getKey()+"; Class: "+entry.getValue().getClassName()+"; Method: "+entry.getValue().getMethod());
        //    }
            String url = request.getRequestURI().substring(request.getContextPath().length()+1);
            // out.println(url);
            if(this.getMappingUrls().containsKey(url)){
                Mapping mapping = this.getMappingUrls().get(url);
                Class clazz = Class.forName(mapping.getClassName());
                Object object = null;
                if(this.singleton.containsKey(clazz)){
                    if(this.singleton.get(clazz) == null){
                        this.singleton.put(clazz, clazz.getConstructor().newInstance());
                    }
                    object = this.singleton.get(clazz);
                }
                else{
                    object = clazz.getConstructor().newInstance();
                }
                Method[] methods = clazz.getDeclaredMethods();
                Method method = null;
                for (Method methode : methods) {
                    if(methode.getName().equals(mapping.getMethod())){
                        if(methode.isAnnotationPresent(Authentification.class)){
                            if(request.getSession().getAttribute(sessionName) != null ){
                                if(request.getSession().getAttribute(sessionProfile) != null && !methode.getAnnotation(Authentification.class).profile().equals("") && !methode.getAnnotation(Authentification.class).profile().equals(request.getSession().getAttribute(sessionProfile))){
                                    throw new Exception("You can't access this method");
                                }
                            }
                        }
                        if(methode.isAnnotationPresent(Session.class)){
                            HashMap<String,Object> Hashsessions=new HashMap<>();
                            HttpSession sess = request.getSession();
                            List<String> sessions = Collections.list(sess.getAttributeNames());
                            for (String string : sessions) {
                                Hashsessions.put(string,sess.getAttribute(string));
                            }
                            object.getClass().getDeclaredMethod("setSession",HashMap.class).invoke(object,Hashsessions);
                        }
                        method = methode;
                    }
                }

                Object[] arguments = null;

                if(request.getParameterMap() != null){
                    Map<String, String[]> parameter = request.getParameterMap();
                    Set<String> parameterName = parameter.keySet();                    
                    String[] attribute= parameterName.toArray(new String[parameterName.size()]);
                    Field[] objectAttributes= object.getClass().getDeclaredFields();
                    String contentType = request.getHeader("Content-Type");
                    for(Field field : objectAttributes){
                        try{
                            if(field.getType() == FileUpload.class) {
                                if (contentType != null && contentType.startsWith("multipart/form-data")) {
                                    containsFile= true;
                                } 
                                //set attribut file de la classe
                                if (containsFile == true) {
                                    Method methody= object.getClass().getMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), field.getType());
                                    Collection<Part> files = request.getParts();
                                    FileUpload file = this.fileTraitement(files, field);
                                    methody.invoke(object,file);
                                }
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
                        if(method.isAnnotationPresent(Session.class)) {
                                HashMap<String,Object> Hashsessions=new HashMap<>();
                                HttpSession session = request.getSession();
                                List<String> sessions = Collections.list(session.getAttributeNames());
                                Hashsessions = (HashMap<String,Object>) object.getClass().getDeclaredMethod("getSession").invoke(object);
                                for (String string : Hashsessions.keySet()) {
                                session.setAttribute(string,Hashsessions.get(string));
                            }
                        }    
                        // javax.swing.JOptionPane.showMessageDialog(new javax.swing.JFrame(),request.getSession().getAttribute(sessionProfile));
                        RequestDispatcher requestDispatcher = request.getRequestDispatcher(modelView.getView());
                        this.checkMethod(modelView, request);
                        HashMap<String,Object> data= modelView.getData();
                        if(data != null){
                            int i = 0;
                            for(HashMap.Entry<String,Object> d : data.entrySet()){
                                request.setAttribute(d.getKey(),d.getValue());
                                i++;
                            }
                        }
                        if(modelView.getInvalidateSession()){
                            request.getSession().invalidate();
                        }

                        List<String> sessions = modelView.getSessionRemove();
                        for(String string : sessions){
                            request.getSession().removeAttribute(string);
                        }
                        
                        if(modelView.getJson()){
                            response.setContentType("application/json");
                            out.println(gson.toJson(modelView.getData()));
                        }
                        else{
                            requestDispatcher.forward(request,response);
                        }
                    }
                    else {
                        if(method.isAnnotationPresent(RestApi.class)){
                            response.setContentType("application/json");
                            out.println(gson.toJson(returnObject));
                        }
                        else{
                            out.println(returnObject);
                        }
                    }
                }
            }
        } catch(Exception e){

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
            // return e.getMessage();
        }
        return null;
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
    
    public void checkMethod(ModelView modelView, HttpServletRequest request) throws Exception{
        if(modelView.getSession() != null){
            HashMap<String,Object> objet = modelView.getSession();
            for(Map.Entry<String, Object> e : objet.entrySet()){
                request.getSession().setAttribute(e.getKey(), e.getValue());
            }
        }
    }
}

