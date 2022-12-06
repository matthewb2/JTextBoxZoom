package main;


import textBox.ShapeContainer;
import textBox.TextBox;
import main.ObjectTable;
import main.DrawPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.im.*;
import java.text.*;
import java.text.AttributedCharacterIterator.Attribute;
import javax.swing.*;
import javax.swing.border.Border.*;
import util.*;

public class ListenerPanel extends DrawPanel
        implements KeyListener, MouseListener, InputMethodListener, InputMethodRequests{
        ContainerManager containerManager=null;
        int idNumber=0;
        int debug=0;
        
    public ListenerPanel(){
        this.enableInputMethods(true);
        this.addInputMethodListener(this);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.requestFocus();
    }
    
    public String getMouseListenersInfo(){
        String str="";
        MouseListener[] mouseListeners=this.getMouseListeners();
        if(mouseListeners.length==0) 
             str="MouseListener=null";
        else str="ListenerPanel MouseListener list";
        for(int i=0;i<mouseListeners.length;i++) 
            str+="\n  --  "+mouseListeners[i].toString();
        return str;
    }
    
    public void printMouseListeners(String title){
        MouseListener[] mouseListeners = this.getMouseListeners();
        if (mouseListeners.length == 0) {
            System.out.println(title + " MouseListener=null");
        } else {
            System.out.println(title);
        }
        for (int i = 0; i < mouseListeners.length; i++) {
            System.out.println("  --  " + mouseListeners[i]);
        }
    }
    
    public boolean isMouseListener(MouseListener listener){
        MouseListener[] mouseLS=this.getMouseListeners();
        boolean isRegistered=false;
        for(int i=0;i<mouseLS.length;i++){
            if(listener.equals(mouseLS[i])) isRegistered=true;
        }
        return isRegistered;
    }
    
    public boolean isMouseMotionListener(MouseMotionListener listener){
        MouseMotionListener[] mouseMotionLS=this.getMouseMotionListeners();
        boolean isRegistered=false;
        for(int i=0;i<mouseMotionLS.length;i++){
            if(listener.equals(mouseMotionLS[i])) isRegistered=true;
        }
        return isRegistered;
    }
    
    public boolean isMouseListener(String listenerClassName){
        MouseListener[] mouseLS=this.getMouseListeners();
        boolean isRegistered=false;
        for(int i=0;i<mouseLS.length;i++){
            String className=mouseLS[i].getClass().getSimpleName();
            if(listenerClassName.equals(className)) isRegistered=true;
        }
        return isRegistered;
    }
    
    public boolean isMouseMotionListener(String listenerClassName){
        MouseMotionListener[] mouseMotionLS=this.getMouseMotionListeners();
        boolean isRegistered=false;
        for(int i=0;i<mouseMotionLS.length;i++){
            String className=mouseMotionLS[i].getClass().getSimpleName();
            if(listenerClassName.equals(className)) isRegistered=true;
        }
        return isRegistered;
    }
    
// KeyListener method
     public void keyTyped(KeyEvent event) {
        this.containerManager=ObjectTable.getContainerManager();
        this.requestFocus();
        char keyChar = event.getKeyChar();
        int keyCode = event.getKeyCode();
        if(debug>0) System.out.println("++ ListenerPanel keyTyped   keyChar="+keyChar+
                ", Hex="+Integer.toHexString(keyChar)+", keyCode="+keyCode);
        if((keyChar<0x20||keyChar==0x7F)&&keyChar!='\n') {
        /*
            JTabbedPane tabbedPane=ObjectTable.getTabbedPane("Listenerpanel.keyTyped");
            tabbedPane.setSelectedIndex(1);
        */
            return;
        }  
        ShapeContainer shapeContainer=this.containerManager.getEditableTextBox();
        if(debug>0) {
            if(shapeContainer==null) System.out.println("++ ListenerPanel keyTyped  shapeContainer is null");
            else System.out.println("++ ListenerPanel keyTyped  shapeContainer="+shapeContainer.getShapeId());
        }
        if( shapeContainer!=null&&shapeContainer.getTextBox()!=null){
            TextBox textBox=shapeContainer.getTextBox();
            textBox.keyTyped(keyChar);
        }

        //event.consume();
    }

// KeyListener method
    public void keyPressed(KeyEvent event) {
        char keyChar=event.getKeyChar();
        int keyCode=event.getKeyCode();
        if(debug>0) System.out.println("++ ListenerPanel keyPressed   keyChar="+keyChar+
                ", Hex="+Integer.toHexString(keyChar)+", keyCode="+keyCode);
        if(keyCode==KeyEvent.VK_LEFT||keyCode==KeyEvent.VK_RIGHT||
                keyCode==KeyEvent.VK_UP||keyCode==KeyEvent.VK_DOWN){
            this.containerManager=ObjectTable.getContainerManager();
            ShapeContainer shapeContainer=this.containerManager.getEditableTextBox();
            if( shapeContainer!=null&&shapeContainer.getTextBox()!=null){
                TextBox textBox=shapeContainer.getTextBox();
                textBox.keyPressed(keyCode);
            }
        }
    }
// KeyListener method
    public void keyReleased(KeyEvent event) {}
   
// InputMethodListener method
    public InputMethodRequests getInputMethodRequests() {
        return this;
    }

// InputMethodListener method
    public void inputMethodTextChanged(InputMethodEvent event) {
        this.containerManager=ObjectTable.getContainerManager();
        ShapeContainer container=this.containerManager.getEditableTextBox();
        if( container!=null&&container.getTextBox()!=null){
            TextBox textBox=container.getTextBox();
            textBox.inputMethodTextChanged(event);
        }
    }

// InputMethodListener method
    public void caretPositionChanged(InputMethodEvent event) {}

// InputMethodListener method
    public int getCommittedTextLength() {
        return 0;
    }

// InputMethodRequests method
    public Rectangle getTextLocation(TextHitInfo offset) {
        //return new Rectangle(50,50,10,10);
        Rectangle rect=null;
        ShapeContainer container=containerManager.getEditableTextBox();
        if( container!=null&&container.getTextBox()!=null){
            TextBox textBox=container.getTextBox();
           //------------------------------// 
            rect=textBox.getTextLocation();
            //------------------------------//  
            Point location = this.getLocationOnScreen();
            rect.translate(location.x, location.y);
        }
        return rect;
    }

// InputMethodRequests method
    public TextHitInfo getLocationOffset(int x, int y) {
        return null;
    }

// InputMethodRequests method
    public int getInsertPositionOffset() {
        return 0;
    }

// InputMethodRequests method
    public AttributedCharacterIterator getCommittedText(int beginIndex,
    		int endIndex, Attribute[] attributes) {
        return null;
    }

// InputMethodRequests method
    public AttributedCharacterIterator cancelLatestCommittedText(Attribute[] attributes) {
        return null;
    }

// InputMethodRequests method
    public AttributedCharacterIterator getSelectedText(Attribute[] attributes) {
        return null;
    }
    
    public void mousePressed(MouseEvent e) {
        this.requestFocus();
        this.enableInputMethods(true);
    } 
    
    public void mouseClicked(MouseEvent e) {
        double x=e.getX();
        double y=e.getY();
        this.requestFocus();
        this.enableInputMethods(true);
        this.selection(x,y);
    } 
    
    private void selection(double x, double y){
        ContainerManager manager=ObjectTable.getContainerManager();
        ShapeContainer[] containers=manager.getContainers();
        for(int i=0;i<containers.length;i++){
            containers[i].setSelected(false);
            Rectangle2D rect=containers[i].getBoundingBox();
            if(rect.contains(x, y)){
                containers[i].setSelected(true);
                containers[i].makeTextBoxEditable(true);
            }
        }
    }
    
    public void mouseReleased(MouseEvent e) {} 
    
    public void mouseEntered(MouseEvent e) {} 
    
    public void mouseExited(MouseEvent e) {} 
}
