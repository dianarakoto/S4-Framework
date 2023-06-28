package etu2000.framework;

import java.util.HashMap;

public class ModelView {
    String view;
    HashMap<String, Object> data;

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public HashMap<String, Object> getData() {
        return data;
    }
    
    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
    
    public ModelView (String view){
        this.view = view;
    }

    public ModelView () {}
    
    public void addItem(String nom, Object valeur){
        data = new HashMap<String,Object>();
        data.put(nom, valeur);
    }
}