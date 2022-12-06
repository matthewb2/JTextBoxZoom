package util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



public class UndoConstants {
    public static final int ADD=1;
    public static final int DELETE=2;
    public static final int CONTAINER=3;
    public static final int SHAPE=4;
    public static final int SIZE_POSITION=5;
    public static final int TEXTBOX=6;
    public static final int PAINTSTYLE=7;
    
    
    public static final String[] undoStr={"","ADD", "DELETE", "CONTAINER","SHAPE", 
                                   "SIZE_POSITION", "TEXTBOX", "PAINTSTYLE"};
}