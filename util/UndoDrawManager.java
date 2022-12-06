package util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.awt.*;
import javax.swing.undo.*;

public class UndoDrawManager extends UndoManager{
    //int id=0;
    int debug=0;
    
    public UndoDrawManager(){
        super();
        //System.out.println("** UndoDrawManager_Constructor this.UndoDrawManager="+
        //        this.toString());
    }
    
    public boolean addEdit(UndoableEdit anEdit){
        boolean returnCode=super.addEdit(anEdit);
        this.changeButtonState();
        return returnCode;
    }
    
    public void discardAllEdits(){
        super.discardAllEdits();
        this.changeButtonState();
    }
    
    public void undo() {
        boolean realDone=false;
        while(super.canUndo()){
            String name=super.getUndoPresentationName();
            if(debug>0) System.out.println("   ++ undoManager.undo PresentationName="+
                name+", realDone="+realDone);
            if(name.startsWith("Delimiter")&&realDone) break;
            super.undo();
            realDone=true;
        }
        this.changeButtonState();
    }

    public void redo() {
        boolean realDone=false;
        while(super.canRedo()){
            String name=super.getRedoPresentationName();
            if(debug>0) System.out.println("   ++ undoManager.redo PresentationName="+
                super.getRedoPresentationName()+", realDone="+realDone);
            if(name.startsWith("Delimiter")&&realDone) break;
            super.redo();
            realDone=true;
        }
        this.changeButtonState();
    }
    
    protected void changeButtonState(){
        //Component undoButton=(Component)ObjectTable.getMenuUtil().getMenuComponent("undo");
        //Component redoButton=(Component)ObjectTable.getMenuUtil().getMenuComponent("redo");
        ButtonOfToggle undoButton=DrawMenu.undo;
        ButtonOfToggle redoButton=DrawMenu.redo;
        undoButton.setEnabled(super.canUndo());
        redoButton.setEnabled(super.canRedo());
        if(debug>0) System.out.println("** UndoDrawManager.changeButtonState this.UndoDrawManager="+
                this.toString());
    }
/*
    protected void changeButtonState(){
        Component undoButton=(Component)ObjectTable.getMenuUtil().getMenuComponent("undo");
        Component redoButton=(Component)ObjectTable.getMenuUtil().getMenuComponent("redo");
        undoButton.setEnabled(super.canUndo());
        redoButton.setEnabled(super.canRedo());
        if(debug>0) System.out.println("** UndoDrawManager.changeButtonState this.UndoDrawManager="+
                this.toString());
    }
*/
    public String toString(){
        String str="";
        str+=" UndoDrawManager canUndo="+super.canUndo()+
                ", canRedo="+super.canRedo();
        return str;
    }
}//End of UndoDrawManager
