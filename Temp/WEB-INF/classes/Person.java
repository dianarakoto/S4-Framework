package model;

import etu2000.framework.annotation.Url;
import etu2000.framework.annotation.Scope;
import etu2000.framework.ModelView;
import etu2000.framework.FileUpload;
import java.sql.Date;
import java.util.Vector;

@Scope("singleton")
public class Person {
    int id;
    String name;
    int appel = 1;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public Person(int id, String name){
        this.id = id;
        this.name = name;
        appel++;
    }

    public Person(){}

    @Url("test-singleton")
    public ModelView test(){
        ModelView view = new ModelView("singleton.jsp");
        view.addItem("test", this);
        return view;
    }

}