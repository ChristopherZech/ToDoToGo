package uni.ma.todotogo.model;

import java.util.ArrayList;

public class Parent {
	private String mTitle;
    private ArrayList mArrayChildren;
 
    public String getTitle() {
        return mTitle;
    }
 
    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
 
    public ArrayList getArrayChildren() {
        return mArrayChildren;
    }
 
    public void setArrayChildren(ArrayList mArrayChildren) {
        this.mArrayChildren = mArrayChildren;
    }
}
