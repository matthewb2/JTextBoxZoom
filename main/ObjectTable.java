package main;

import javax.swing.*;
import main.*;
import util.*;


public class ObjectTable {
    protected static DrawMain main=null;
    protected static DrawPanel drawPanel=null;
    protected static ListenerPanel listenerPanel=null;
    protected static StatusPanel statusPanel=null;
    protected static JScrollPane scrollPane=null;
    protected static JViewport viewport=null;
    public static ViewUtil viewUtil=null;
    protected static DrawMenu drawMenu=null;
    protected static ContainerManager containerManager=null;
    public static UndoDrawManager undoDrawManager=null;
    public static Edit edit=null;
    protected static CustomColorChooserDialog customColorChooserDialog=null;
    

    
    public static DrawMain getDrawMain(){
        if(main==null) System.out.println("* ObjectTable DrawMain=null");
        return main;
    }

    public static DrawPanel getDrawPanel(){
        if(drawPanel==null) System.out.println("* ObjectTable ListenerPanel=null, ");
        return drawPanel;
    }
    public static ListenerPanel getListenerPanel(){
        if(listenerPanel==null) System.out.println("* ObjectTable ListenerPanel=null, ");
        return listenerPanel;
    }
    public static JScrollPane getScrollPane(){
        if(scrollPane==null) System.out.println("* ObjectTable ScrollPane=null, " );
        return scrollPane;
    }
    public static ViewUtil getViewUtil(String from){
        if(viewUtil==null) System.out.println("* ObjectTable Utility=null, called from="+from);
        return viewUtil;
    }
    public static DrawMenu getDrawMenu(){
        if(drawMenu==null) System.out.println("* ObjectTable DrawMenu=null");
        return drawMenu;
    }

    public static ContainerManager getContainerManager(){
        if(containerManager==null) System.out.println("* ObjectTable ContainerManager=null");
        return containerManager;
    }
    public static UndoDrawManager getUndoDrawManager(){
        if(undoDrawManager==null) System.out.println("* ObjectTable UndoDrawManager=null, " );
        return undoDrawManager;
    }
    public static Edit getEdit(){
        if(edit==null) System.out.println("* ObjectTable Edit=null, " );
        return edit;
    }
    public static StatusPanel getStatusPanel(){
        if(statusPanel==null) System.out.println("* ObjectTable statusPanel=null, " );
        return statusPanel;
    }
    
    public static CustomColorChooserDialog getCustomColorChooserDialog(){
        if(customColorChooserDialog==null) System.out.println("* ObjectTable statusPanel=null, " );
        return customColorChooserDialog;
    }

}

