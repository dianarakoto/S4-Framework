package model;

import etu2000.framework.annotation.Url;
import etu2000.framework.annotation.Authentification;
import etu2000.framework.annotation.RestApi;
import etu2000.framework.annotation.Session;
import etu2000.framework.ModelView;
import etu2000.framework.FileUpload;
import java.sql.Date;
import java.io.*;
import java.util.*;
import java.sql.Date;

public class Employee {
    int id;
    String name;
    Date embauche;
    FileUpload badge;
    HashMap<String, Object> session;
    boolean json;

    public void setJson(boolean json){
        this.json = json;
    }

    public boolean getJson(){
        return json;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setEmbauche(Date embauche){
        this.embauche = embauche;
    }

    public Date getEmbauche(){
        return embauche;
    }

    public void setBadge(FileUpload badge){
        this.badge = badge;
    }

    public FileUpload getBadge(){
        return badge;
    }
    
    public void setSession(HashMap<String, Object> session){
        this.session = session;
    }

    public HashMap<String, Object> getSession(){
        return session;
    }

    public Employee() {
    }

    public Employee(int id, String name){
        setId(id);
        setName(name);
    }

    @Authentification(profile = "admin")
    @Url("find-emp")
    public ModelView findAll(){
        ModelView view = new ModelView("test.jsp");
        view.setJson(true);
        Vector<Employee> employees = new Vector<>();
        Employee un = new Employee(1, "Diana");
        Employee deux = new Employee(2, "Megane");
        employees.add(un);
        employees.add(deux);
        view.addItem("allEmployees", employees);
        return view;
    }

    @Url("save-emp")
    public ModelView save(){
        ModelView view = new ModelView("employe.jsp");
        view.addItem("employee", this);
        return view;
    }

    @Url("get-info")
    public ModelView getInfo(String name){
        ModelView view = new ModelView("test.jsp");
        view.setJson(true);
        view.addItem("employee", name);
        view.addItem("olona", "za");
        return view;
    }

    @RestApi
    @Url("test-api")
    public Employee getEmp(){
        Employee emp = new Employee(1, "Diana");
        this.setJson(true);
        return emp;
    }

    @Session
    @Url("test-session")
    public String testSession(){
        String sessionName = null;
        for (Map.Entry<String, Object> entry : getSession().entrySet()) {
            sessionName = entry.getKey();
        }
        return sessionName;
    }

    @Url("index")
    public ModelView logout(){
        ModelView view = new ModelView("index.jsp");
        view.setInvalidateSession(true);
        return view;
    }
}
