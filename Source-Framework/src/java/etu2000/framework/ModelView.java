package etu2000.framework;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class ModelView {
    String view;
    HashMap<String, Object> data;
    HashMap<String, Object> session;
    boolean json;
    boolean invalidateSession = false;
    List<String> sessionRemove = new ArrayList<String>();

    public void setSessionRemove(List<String> sessionRemove){
        this.sessionRemove = sessionRemove;
    }

    public List<String> getSessionRemove(){
        return sessionRemove;
    }

    public boolean getInvalidateSession(){
        return invalidateSession;
    }

    public void setInvalidateSession(boolean invalidateSession){
        this.invalidateSession = invalidateSession;
    }

    public boolean getJson(){
        return json;
    }

    public void setJson(boolean json){
        this.json = json;
    }

    public HashMap<String, Object> getSession() {
        return session;
    }

    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }
    
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
    
    public void addSessionItem(String key, Object value){
        if(session == null){
            setSession(new HashMap<String, Object>());
        }
        session.put(key, value);
    }
}