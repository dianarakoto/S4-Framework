package model;

import etu2000.framework.annotation.Url;
import etu2000.framework.annotation.Scope;
import etu2000.framework.annotation.Session;
import etu2000.framework.ModelView;
import etu2000.framework.FileUpload;
import java.sql.Date;
import java.util.Vector;

@Scope("singleton")
public class Person {
    int id;
    String name;
    String password;
    int appel = 1;

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

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

    @Url("connect")
    public ModelView connect(){
        ModelView view = new ModelView("accueil.jsp");
        view.addSessionItem("isConnected", this);
        view.addSessionItem("profile", this.getName());
        view.addItem("info", this);
        return view;
    }

}