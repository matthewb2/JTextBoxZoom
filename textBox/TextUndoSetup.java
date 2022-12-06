package textBox;

import util.Util;
import java.text.*;
import util.*;

public class TextUndoSetup {

    public TextUndoSetup(){}

// InsertText
    public static class InsertText{
        private UndoDrawManager undoDrawManager=null;
        UndoableDrawEdit.InsertText InsertText=null;
        int debug=0;
        
        public InsertText(UndoDrawManager undoDrawManager){
        if(undoDrawManager==null) 
            System.err.println("*** Error  TextUndoSetup.InsertText undoDrawManager=null");
            this.undoDrawManager=undoDrawManager;
        }
        
        public void setInsertedText(ShapeContainer shapeContainer, 
                AttributedString insString, int start, boolean delimit){
            //TextBox textBox=shapeContainer.getTextBox();
            int endPosition=0;
            if(this.InsertText!=null){
                endPosition=this.InsertText.getPosition()+
                    this.InsertText.getAttribString().getIterator().getEndIndex();
            }
            if(debug>0) System.out.println(" ++ TextUndoSetup.InsertText.setInsertedText() " +
                        "strat="+start+", endPosition="+endPosition+", insString="+Util.Text(insString));
            if(this.InsertText==null||this.InsertText.getContainer()!=shapeContainer||
            endPosition!=start){
                 initialSet(shapeContainer, insString, start);
            } else {
                AttributedString oldString=this.InsertText.getAttribString();
                AttributedCharacterIterator newString=
                AttributedStringUtil.createCompositeText(oldString.getIterator(), 
                        insString.getIterator(), oldString.getIterator().getEndIndex(), 
                        false);
                AttributedString attribStr=AttributedStringUtil.getAttributedString(newString);
                if(debug>0) System.out.println(" ** TextUndoSetup.InsertText.setInsertedText() " +
                        "strat="+start+", newString="+Util.Text(attribStr));
                this.InsertText.setAttribString(attribStr);
            }
           //added 2012.09.23 
            if(delimit) this.InsertText=null;
        }
        
        private void initialSet(ShapeContainer shapeContainer, 
                AttributedString insString, int start){
          //--------------------------- undo setups-----------------------//
            UndoableDrawEdit.InsertText InsertText=
                    new UndoableDrawEdit.InsertText(shapeContainer.getShapeId(), 
                    insString, start);
            this.undoDrawManager.addEdit(InsertText);
            UndoableDrawEdit.Delimiter delimiter=new UndoableDrawEdit.Delimiter();
            this.undoDrawManager.addEdit(delimiter);
            this.InsertText=InsertText;
           //--------------------------- undo setups-----------------------//
        }
    }//End of InsertText
    
// DeleteText
    public static class DeleteText{
        private UndoDrawManager undoDrawManager=null;
        UndoableDrawEdit.DeleteText DeleteText=null;
        int debug=0;
        
        public DeleteText(UndoDrawManager undoDrawManager){
        if(undoDrawManager==null) 
            System.err.println("*** Error  TextUndoSetup.DeleteText undoDrawManager=null");
            this.undoDrawManager=undoDrawManager;
        }
        
        public void setDeletedText(ShapeContainer shapeContainer, 
                AttributedString delString, int start, int end){
            //TextBox textBox=shapeContainer.getTextBox();
            if(this.DeleteText==null||this.DeleteText.getContainer()!=shapeContainer||
            this.DeleteText.getPosition()!=end){
                 initialSet(shapeContainer, delString, start);
            } else {
                AttributedString oldString=this.DeleteText.getAttribString();
                AttributedCharacterIterator newString=
                AttributedStringUtil.createCompositeText(delString.getIterator(), 
                         oldString.getIterator(), delString.getIterator().getEndIndex(), 
                         false);
                AttributedString attribStr=AttributedStringUtil.getAttributedString(newString);
                if(debug>0) System.out.println(" ** TextUndoSetup.DeleteText.setString " +
                        "strat,end="+start+","+end+", newString="+Util.Text(attribStr));
                this.DeleteText.setAttribString(attribStr);
                this.DeleteText.setPosition(start);
            }
        }
        
        private void initialSet(ShapeContainer shapeContainer, 
                AttributedString delString, int start){
          //--------------------------- undo setups-----------------------//
            boolean significant=true;
            UndoableDrawEdit.DeleteText DeleteText=
                    new UndoableDrawEdit.DeleteText(shapeContainer.getShapeId(), 
                    delString, start);
            this.undoDrawManager.addEdit(DeleteText);
            UndoableDrawEdit.Delimiter delimiter=new UndoableDrawEdit.Delimiter();
            this.undoDrawManager.addEdit(delimiter);
            this.DeleteText=DeleteText;
           //--------------------------- undo setups-----------------------//
        }
    }//End of DeleteText
    
}
