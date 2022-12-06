package util;



import textBox.ShapeContainer;
import textBox.TextBox;
import util.Util;
import main.ObjectTable;
import java.text.*;
import javax.swing.undo.*;

public class UndoableDrawEdit extends AbstractUndoableEdit{

    //static int debug=0;
    public UndoableDrawEdit(){}
    
//Delimiter
    public static class Delimiter extends AbstractUndoableEdit{
        int debug=0;
        public Delimiter(){}

        public String getUndoPresentationName(){
            return "Delimiter";
        }
        public String getRedoPresentationName(){
            return "Delimiter";
        }
        public boolean isSignificant() {
            return true;
        }

        public void undo() throws CannotUndoException {
            super.undo();
        }

        public void redo() throws CannotRedoException {
            super.redo();
        }
        public String toString() {
            String str="Delimiter";
            return str;
        }
    }// Delmiter


//Change AttributedString
    public static class ChangeText extends AbstractUndoableEdit{
        //ContainerManager containerManager=ObjectTable.getContainerManager("UndoableDrawEdit");
        String shapeId="";
        AttributedString oldStr=null;
        AttributedString newStr=null;
        int position=0;
        int debug=0;
        
        public ChangeText(String shapeId, AttributedString oldStr,
                AttributedString newStr){
            this.shapeId=shapeId;
            this.oldStr=oldStr;
            this.newStr=newStr;
            if(debug>0) System.out.println(" ** UndoableDrawEdit.ChangeText" +
                    "  oldStr="+Util.Text(oldStr)+"  newStr="+Util.Text(newStr));
        }
        
        public ShapeContainer getContainer(){
            ContainerManager containerManager=ObjectTable.getContainerManager();
            ShapeContainer container=containerManager.getContainer(this.shapeId);
            return (ShapeContainer)container;
        }
        
        public boolean canUndo(){
            return true;
        }
        public boolean canRedo(){
            return true;
        }
        public String getUndoPresentationName(){
            return "ChangeText Undo";
        }
        public String getRedoPresentationName(){
            return "ChangeText Redo";
        }
        public boolean isSignificant(){
            return true;
        }
        public void undo(){
            ContainerManager containerManager=ObjectTable.getContainerManager();
            ShapeContainer container=containerManager.getContainer(this.shapeId);
            ShapeContainer shapeContainer=(ShapeContainer)container;
            TextBox textBox=shapeContainer.getTextBox();
            if(textBox==null){
                System.err.println("*** Error UndoableDrawEdit.ChangeText.undo; textBox not found");
                return;
            } else{
                shapeContainer.getTextBox().replaceText(TextBox.UNDO_REDO, this.oldStr);
                if(debug>0) System.out.println(" ** UndoableDrawEdit.InsertText.undo" +
                    "  attribStr="+Util.Text(oldStr));
            }
        }

        public void redo(){
            ContainerManager containerManager=ObjectTable.getContainerManager();
            ShapeContainer container=containerManager.getContainer(this.shapeId);
            ShapeContainer shapeContainer=(ShapeContainer)container;
            TextBox textBox=shapeContainer.getTextBox();
            if(textBox==null){
                System.err.println("*** Error UndoableDrawEdit.ChangeText.undo; textBox not found");
                return;
            } else{
                shapeContainer.getTextBox().replaceText(TextBox.UNDO_REDO, this.newStr);
            }
        }
    } //End of InsertText
    
//insert AttributedString
    public static class InsertText extends AbstractUndoableEdit{
        String shapeId="";
        AttributedString attribStr=null;
        int position=0;
        int debug=0;
        
        public InsertText(String shapeId, AttributedString attribStr,
                int position){
            this.shapeId=shapeId;
            this.attribStr=attribStr;
            this.position=position;
            if(debug>0) System.out.println(" ** UndoableDrawEdit.InsertText" +
                    "  attribStr="+Util.Text(attribStr));
        }

        public ShapeContainer getContainer(){
            ContainerManager containerManager=ObjectTable.getContainerManager();
            ShapeContainer container=containerManager.getContainer(this.shapeId);
            return (ShapeContainer)container;
        }

        public int getPosition(){
            return this.position;
        }
        public void setPosition(int position){
            this.position=position;
        }
        public AttributedString getAttribString(){
            return this.attribStr;
        }
        public void setAttribString(AttributedString attribStr){
            if(debug>0) System.out.println(" ** UndoableDrawEdit.InsertText.setAttribString()" +
                    "  attribStr="+Util.Text(attribStr));
            this.attribStr=attribStr;
        }
        public boolean canUndo(){
            return true;
        }
        public boolean canRedo(){
            return true;
        }
        public String getUndoPresentationName(){
            return "InsertText Undo";
        }
        public String getRedoPresentationName(){
            return "InsertText Redo";
        }
        public boolean isSignificant(){
            return true;
        }
        public void undo(){
            ContainerManager containerManager=ObjectTable.getContainerManager();
            ShapeContainer shapeContainer=containerManager.getContainer(this.shapeId);
            TextBox textBox=shapeContainer.getTextBox();
            if(textBox==null){
                System.err.println("*** Error UndoableDrawEdit.DeleteText.undo; textBox not found");
                return;
            } else{
                int length=this.attribStr.getIterator().getEndIndex();
                shapeContainer.getTextBox().deleteText(TextBox.UNDO_REDO, this.position, 
                        this.position+length);
                if(debug>0) System.out.println(" ** UndoableDrawEdit.InsertText.undo" +
                    "  attribStr="+Util.Text(attribStr));
            }
        }

        public void redo(){
            ContainerManager containerManager=ObjectTable.getContainerManager();
            ShapeContainer shapeContainer=containerManager.getContainer(this.shapeId);
            TextBox textBox=shapeContainer.getTextBox();
            if(textBox==null){
                System.err.println("*** Error UndoableDrawEdit.DeleteText.undo; textBox not found");
                return;
            } else{
                shapeContainer.getTextBox().insertText(TextBox.UNDO_REDO, 
                        this.position, this.attribStr.getIterator());
            }
        }
    } //End of InsertText
    
//delete AttributedString
    public static class DeleteText extends AbstractUndoableEdit{
        //ContainerManager containerManager=ObjectTable.getContainerManager("UndoableDrawEdit");
        String shapeId="";
        AttributedString attribStr=null;
        int position=0;
        //boolean significant=true;
        int debug=0;
        public DeleteText(String shapeId, AttributedString attribStr,
                int position){
            this.shapeId=shapeId;
            this.attribStr=attribStr;
            this.position=position;
            if(debug>0) System.out.println(" ** UndoableDrawEdit.DeleteText" +
                    "  attribStr="+Util.Text(attribStr));
        }

        public ShapeContainer getContainer(){
            ContainerManager containerManager=ObjectTable.getContainerManager();
            ShapeContainer container=containerManager.getContainer(this.shapeId);
            return (ShapeContainer)container;
        }

        public int getPosition(){
            return this.position;
        }
        public void setPosition(int position){
            this.position=position;
        }
        public AttributedString getAttribString(){
            return this.attribStr;
        }
        public void setAttribString(AttributedString attribStr){
            if(debug>0) System.out.println(" ** UndoableDrawEdit.DeleteText.setAttribString" +
                    "  attribStr="+Util.Text(attribStr));
            this.attribStr=attribStr;
        }
        public boolean canUndo(){
            return true;
        }
        public boolean canRedo(){
            return true;
        }
        public String getUndoPresentationName(){
            return "DeleteText Undo";
        }
        public String getRedoPresentationName(){
            return "DeleteText Redo";
        }
        public boolean isSignificant(){
            return true;
        }
        public void undo(){
            ContainerManager containerManager=ObjectTable.getContainerManager();
            ShapeContainer container=containerManager.getContainer(this.shapeId);
            ShapeContainer shapeContainer=(ShapeContainer)container;
            TextBox textBox=shapeContainer.getTextBox();
            if(textBox==null){
                System.err.println("*** Error UndoableDrawEdit.DeleteText.undo; textBox not found");
                return;
            } else{
                shapeContainer.getTextBox().insertText(TextBox.UNDO_REDO, this.position, 
                        this.attribStr.getIterator());
            if(debug>0) System.out.println(" ** UndoableDrawEdit.DeleteText.undo" +
                    "  attribStr="+Util.Text(attribStr));
            }
        }
        public void redo(){
            ContainerManager containerManager=ObjectTable.getContainerManager();
            ShapeContainer container=containerManager.getContainer(this.shapeId);
            ShapeContainer shapeContainer=(ShapeContainer)container;
            TextBox textBox=shapeContainer.getTextBox();
            if(textBox==null){
                System.err.println("*** Error UndoableDrawEdit.DeleteText.undo; textBox not found");
                return;
            } else{
                int length=this.attribStr.getIterator().getEndIndex();
                shapeContainer.getTextBox().deleteText(TextBox.UNDO_REDO, this.position, 
                        this.position+length);
            }
        }
    }//End of DeleteText
    
}//UndoableDrawEdit
