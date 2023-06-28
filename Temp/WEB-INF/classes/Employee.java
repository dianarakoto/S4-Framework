package model;

import etu2000.framework.annotation.Url;
import etu2000.framework.ModelView;
import etu2000.framework.FileUpload;
import java.sql.Date;
import java.util.Vector;

public class Employee {
    int id;
    String name;
    Date embauche;
    FileUpload badge;

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

    public Employee() {
    }

    public Employee(int id, String name){
        setId(id);
        setName(name);
    }
    
    @Url("find-emp")
    public ModelView findAll(){
        ModelView view = new ModelView("test.jsp");
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
        ModelView view = new ModelView("saved.jsp");
        view.addItem("employee", name);
        return view;
    }
}
