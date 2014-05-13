package uni.ma.todotogo.model;

import android.graphics.Color;

public enum AvailableColors {
	RED("red", Color.RED),
	BLUE("blue", Color.BLUE),
	GREEN("green", Color.GREEN);
	
    private String name;
    private int color;
    
    private AvailableColors(String name, int color) {
    	this.name = name;
        this.color = color;
    }

    @Override
    public String toString() {
        return name;
    }
    
    public int getColor() {
    	return color;
    }
    
    /** 
     * Creates an array containing the names of the available colors.
     * @return Array containing the names of the available colors.
     */
    public static String[] getColorNames() {
    	String[] names = new String[AvailableColors.values().length];
    	for(int i = 0; i < AvailableColors.values().length; i++) {
    		names[i] = AvailableColors.values()[i].name;
    	}
    	return names;
    }
    
    /**
     * Takes an RGB color as int and converts it to a AvailableColor.
     * @param color
     * @return
     */
    public static AvailableColors getColorByInt(int color) {
    	for(int i = 0; i < AvailableColors.values().length; i++) {
    		if(AvailableColors.values()[i].color == color) {
    			return AvailableColors.values()[i];
    		}
    	}
    	return AvailableColors.RED; // if unspecified
    }
}
