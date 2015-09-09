package com.technisat.appstock.drawer;

public class NavDrawerItem {
     
    private String title;
    private int icon;
    private int id;
    private boolean isVisable;
    
     
    public NavDrawerItem(){}
 
    public NavDrawerItem(String title, int icon, int id,boolean isVisible){
        this.title = title;
        this.icon = icon;
        this.id = id;
        this.isVisable = isVisible;
    }
    
    public boolean isVisible(){
    	return isVisable;
    }
    
    public void setVisiblity(boolean isVisible){
    	this.isVisable = isVisible;
    }
    
    public int getId(){
    	return this.id;
    }
     
    public String getTitle(){
        return this.title;
    }
     
    public int getIcon(){
        return this.icon;
    }
     
    public void setTitle(String title){
        this.title = title;
    }
     
    public void setIcon(int icon){
        this.icon = icon;
    }
}