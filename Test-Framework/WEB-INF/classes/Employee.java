package model;

import etu2000.framework.annotation.Url;
import etu2000.framework.ModelView;
import java.util.Vector;

public class Employee {
    int id;
    String name;

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
    public String save(){
        return "Sprint 7 marche";
    }

    @Url("get-info")
    public ModelView getInfo(String name){
        ModelView view = new ModelView("saved.jsp");
        view.addItem("employee", name);
        return view;
    }

    @Url("save-emp")
    public String save(){
        return this.getName();
    }
}
