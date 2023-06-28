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
    
    @Url("find-emp")
    public ModelView findAll(){
        ModelView view = new ModelView("test.jsp");
        Vector<String> noms = new Vector<>();
        noms.add("Diana");
        noms.add("Megane");
        // view.setView("list.jsp");
        view.addItem("allEmployees", noms);
        return view;
    }
}
