package textBox;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import util.Util;
import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import main.DrawParameters;

public class LineBreaker {
     private AttributedCharacterIterator textIterator;
     private TextBox textBox;
     private Rectangle2D textArea;
     private int textAlign=0;
     private int lineSpace=0;
     private ArrayList lineList=new ArrayList();
     private int widthLimit=5;
     int debug=0;
     int idNumber=0;

     public LineBreaker(){}

     public void setData(AttributedCharacterIterator textIterator, TextBox textBox){
        this.lineList.clear();
        this.textIterator=textIterator;
        this.textBox=textBox;
        this.textArea=this.textBox.getMarginlessTextArea();
        this.textAlign=this.textBox.getTextAlign();
        this.lineSpace=this.textBox.lineSpace;
        
        if(this.textIterator!=null&&this.textIterator.getEndIndex()>0) this.createMultipleLines();
        if(debug>0) this.printTextLayouts();
    }
     
     private void createMultipleLines(){
        float formatWidth=(float)textArea.getWidth();
        if(formatWidth<this.widthLimit) formatWidth=this.widthLimit;
       //-----------------------------------------------------------------------------// 
        int[] breakPositions=createLineBreakPositions(this.textIterator, formatWidth);
       //-----------------------------------------------------------------------------//  
        int layoutSize=breakPositions.length-1;
        String[] layoutStrings=new String[layoutSize];
        TextLayout[] textLayouts=new TextLayout[layoutSize];
        FontRenderContext defaultFrc = new FontRenderContext(null, false, false);
        AttributedStringUtil attribUtil=new AttributedStringUtil(this.textIterator);
        AttributedString attributedSubString;
        String lf=DrawParameters.LF[0];
        String lfMark=DrawParameters.LF[2];
        if(this.textBox.isEditable()) lfMark=DrawParameters.LF[1];
        for(int i=0;i<breakPositions.length-1;i++){
            attributedSubString=attribUtil.getTextLayoutString(breakPositions[i], 
                    breakPositions[i+1], lf, lfMark);
            //attributedSubString=attribUtil.getAttributedSubString(breakPositions[i], breakPositions[i+1]);
            AttributedCharacterIterator iterator=attributedSubString.getIterator();
            textLayouts[i]=new TextLayout(iterator, defaultFrc);
            layoutStrings[i]=this.getString(this.textIterator, breakPositions[i], breakPositions[i+1]);
        } //End of for
      //---------------------------------------------------------------------//  
        Point2D[] textLayoutPositions=createTextLayoutPositions(textLayouts);
      //---------------------------------------------------------------------//  
        int size=breakPositions.length-1;
        for(int i=0;i<size;i++){
             Line line=new Line(breakPositions[i], breakPositions[i+1], textLayoutPositions[i],
                     textLayouts[i], layoutStrings[i]);
             this.lineList.add(line);
        }
     }

     private int[] createLineBreakPositions(AttributedCharacterIterator textIterator, 
             float formatWidth){
        Vector vector=new Vector();
        AttributedStringUtil attribUtil
                =new AttributedStringUtil(textIterator);
        String string=attribUtil.getString();
        
        StringTokenizerEx token = new StringTokenizerEx(string, "\n");
        int tokenBegin=0;
        while (token.hasMoreTokens()) {
            String subText=token.nextToken();
            AttributedString attributedStr
                    =attribUtil.getAttributedSubString(tokenBegin, tokenBegin+subText.length());
            AttributedCharacterIterator iterator = attributedStr.getIterator();
            int start = iterator.getBeginIndex();
            int end = iterator.getEndIndex();
            LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(iterator, 
                new FontRenderContext(null, false, false));
            lineMeasurer.setPosition(start);

            int oldPos =iterator.getBeginIndex();
            while (lineMeasurer.getPosition() < end) {
                int pos = lineMeasurer.nextOffset(formatWidth, end, false);
                //System.out.println("*** LineBreaker  formatWidth="+formatWidth);
                lineMeasurer.setPosition(pos);
                vector.add(tokenBegin+oldPos);
                oldPos = pos;
            } // End ofwhile (lineMeasurer.getPosition() < paragraphEnd)
            tokenBegin+=subText.length();
        } // End of while (token.hasMoreTokens())
        
        int posSize=vector.size();
        int[] breakPositions=new int[posSize+1];
        for(int i=0;i<posSize;i++) breakPositions[i]=(Integer)vector.get(i);
        breakPositions[posSize]=string.length();
        if(debug>0) {
            System.out.println("lineBreaker output");
            for( int i=0;i<breakPositions.length-1;i++)
                System.out.println(" - Break position="+breakPositions[i]
                        +"  "+string.substring(breakPositions[i],breakPositions[i+1]));
            System.out.println(" - Break position="+breakPositions[breakPositions.length-1]);
        }
        return breakPositions;
     }
     
     private Point2D[] createTextLayoutPositions(TextLayout[] textLayout){
     // draw text
        double X=textArea.getX();
        double Y =textArea.getY();
        double W=textArea.getWidth();
        double advance=0;
        double xShift=0;
        Point2D[] positions=new Point2D[textLayout.length];
        for(int i=0;i<textLayout.length;i++){
                Y += (textLayout[i].getAscent());
                advance= (textLayout[i].getAdvance());
                xShift=0;
                if(this.textAlign==1) xShift=0.5*(W-advance);
                if(this.textAlign==2) xShift=W-advance;
                positions[i]=new Point2D.Double(X+xShift,Y);
                Y += textLayout[i].getDescent()+textLayout[i].getLeading()+(double)lineSpace;
        } //End of for
        return positions;
    }// End of createTextLayoutPositions

    protected ArrayList getLineList(){
        return this.lineList;
    }
    
    public String getString(CharacterIterator iterator, int start, int end){
        String str="";
        char c;
        int toCopy = end-start;
        c = iterator.setIndex(start);
        while (toCopy-- > 0) {
            str+=Character.toString(c);
            c = iterator.next();
        }
        return str;
    } //getStringOfCharacterIterator

     public TextLayout[] getTextLayouts(){
         int size=this.lineList.size();
         TextLayout[] textLayouts=new TextLayout[size];
         for(int i=0;i<size;i++) {
             Line line=(Line)this.lineList.get(i);
             textLayouts[i]=line.getTextLayout();
         }
         return textLayouts;
     }
     
     public TextLayout getTextLayout(int lineIndex){
         boolean ret=this.lineRangeError(lineIndex, "*** Error LineBreaker.getTextLayouts");
         if(ret) return null;
         int size=this.lineList.size();
         if(lineIndex==size) return null;
         Line line=(Line)this.lineList.get(lineIndex);
         TextLayout textLayout=line.getTextLayout();
         return textLayout;
     }
     
     public String[] getTextLayoutStrings(){
         int size=this.lineList.size();
         String[] layoutStrings=new String[size];
         for(int i=0;i<size;i++) {
             Line line=(Line)this.lineList.get(i);
             layoutStrings[i]=line.getTextLayoutString();
         }
         return layoutStrings;
     }
     
     public String getTextLayoutString(int lineIndex){
        int lineMax=this.lineList.size();
         if(lineIndex>lineMax-1) {
             System.err.println("*** Warning LineBreaker.getCharacterCount"
                     + " lineIndex="+lineIndex+", lineMax="+lineMax);
             return "";
         }
         Line line=(Line)this.lineList.get(lineIndex);
         return line.getTextLayoutString();
     }
     
     protected String getPreceedingString(int lineIndex, int columnIndex){
         String str="";
         boolean ret=this.lineColumnRangeError(lineIndex, columnIndex, "*** Error LineBreaker.getPreceedingString");
         if(ret) return str;

         for(int i=0;i<lineIndex;i++) str+=this.getTextLayoutString(i)+"|";
         if(columnIndex>0) str+=this.getTextLayoutString(lineIndex).substring(0, columnIndex);
         return str;
     }
     
     protected String getPreceedingCharacter(int lineIndex, int columnIndex){
         String str="";
         boolean ret=this.lineColumnRangeError(lineIndex, columnIndex, 
                 "*** Error LineBreaker.getPreceedingCharacter");
         if(ret) return str;
         
         if(columnIndex>0) str=this.getTextLayoutString(lineIndex).substring(columnIndex-1, columnIndex);
         return str;
     }
     
     public Point2D[] getTextLayoutPositions(){
         int size=this.lineList.size();
         Point2D[] positions=new Point2D[size];
         for(int i=0;i<size;i++) {
             Line line=(Line)this.lineList.get(i);
             positions[i]=line.getTextLayoutPosition();
         }
         return positions;
     }
     
     public Rectangle2D[] getBounds(){
         Double Limit=10d;
         int size=this.lineList.size();
         Rectangle2D[] bounds=new Rectangle2D[size];
         for(int i=0;i<size;i++) {
             Line line=(Line)this.lineList.get(i);
             TextLayout textLayout=line.getTextLayout();
             //Rectangle2D boundingBox=textLayout.getBounds();
             double X=line.getTextLayoutPosition().getX();
             double Y=line.getTextLayoutPosition().getY();
             double ascent= textLayout.getAscent();
             double descent= textLayout.getDescent();
             double advance= textLayout.getAdvance();
             if(advance<Limit) advance=Limit;
             bounds[i]=new Rectangle2D.Double(X, Y-ascent, advance, ascent+descent);
         }
         return bounds;
     }
     
     public Rectangle2D getBounds(int lineIndex){
         boolean ret=this.lineRangeError(lineIndex, "*** Error LineBreaker.getBounds");
         if(ret) return null;
         int size=this.lineList.size();
         if(lineIndex==size) return null;
         Double Limit=10d;
         Line line=(Line)this.lineList.get(lineIndex);
         TextLayout textLayout=line.getTextLayout();
         //Rectangle2D boundingBox=textLayout.getBounds();
         double X=line.getTextLayoutPosition().getX();
         double Y=line.getTextLayoutPosition().getY();
         double ascent= textLayout.getAscent();
         double descent= textLayout.getDescent();
         double advance= textLayout.getAdvance();
         if(advance<Limit) advance=Limit;
         Rectangle2D bounds=new Rectangle2D.Double(X, Y-ascent, advance, ascent+descent);
         return bounds;
     }
     
     public int getCharacterCount(){
         int size=this.lineList.size();
         if(size==0) return 0;
         int charCount=0;
         for(int i=0;i<size;i++) {
             charCount+=this.getTextLayoutString(i).length();
         }
         return charCount;
     }
     
     public int getCharacterCount(int lineIndex){
         int size=this.getLineList().size();
         boolean ret=this.lineRangeError(lineIndex, "*** Error LineBreaker.getCharacterCount");
         if(ret) return 0;
         if(lineIndex>=size) lineIndex=size;
         return this.getTextLayoutString(lineIndex).length();
     }
     
     public int getCharacterCount(int lineIndex, int columnIndex){
         boolean ret=this.lineColumnRangeError(lineIndex, columnIndex, 
                 "*** Error LineBreaker.getCharacterCount");
         if(ret) return 0;;
         int size=this.lineList.size();
         if(lineIndex==size) {
             lineIndex=size;
             columnIndex=this.getCharacterCount(lineIndex);
         }
         int charCount=0;
         for(int i=0;i<lineIndex;i++) charCount+=this.getTextLayoutString(i).length();
         charCount+=columnIndex;
         return charCount;
     }

     public int[] getLineColumnIndices(int textIndex){
        int size=this.lineList.size();
        boolean ret=false;
        ret=this.textRangeError(textIndex, null);
        if(ret||textIndex==0){
            int[] newInt={0, 0};
            return newInt;
        }
        int charCount=0;
        int lineIndex=-1;
        for(int i=0;i<size;i++) {
            if(charCount+this.getCharacterCount(i)>textIndex){
                lineIndex=i;
                break;
            }
            if(charCount+this.getCharacterCount(i)==textIndex){
                lineIndex=i;
                break;
            }
            charCount+=this.getCharacterCount(i);
        }
        if(lineIndex<0){
            int[] newInt={0, 0};
            return newInt;
        }
        int columnIndex=textIndex-charCount;
        
        int[] newInt={lineIndex,columnIndex};
        if(debug>0) {
            System.out.println(" LineBreaker.getLineColumnIndex," +
              " textIndex="+textIndex+", lineIndex=="+newInt[0]+", columnIndex="+newInt[1]
              +", preceeding string="
              +this.getPreceedingCharacter(lineIndex, columnIndex).replace("\n", "\\n"));
        }
        return newInt;
     }
     
     public int getTextIndex(int lineIndex, int columnIndex){
        boolean ret=this.lineColumnRangeError(lineIndex, columnIndex, "*** Error LineBreaker.getTextIndex");
        if(ret) return 0;
        int count=this.getCharacterCount(lineIndex, columnIndex);
        return count;
     }

    public Rectangle getCaretRectangle(CaretPosition caretPosition, int offset){
        if(caretPosition.getLineIndex()<0||caretPosition.getColumnIndex()<0){
            System.err.println("*** Error LineBreaker.getCaretRectangle" +
                    ": invalid CaretPosition="+caretPosition.toStringOfTextIndex()+
                    ", offset="+offset);
            return null;
        }
        int width=0;
        Rectangle rectangle=null;
        CaretPosition offsetCaretPosition=(CaretPosition)caretPosition.clone();
        offsetCaretPosition.columnOffset(offset, this);
        //CaretPosition offsetCaretPosition=this.getOffsetCaretPosition(caretPosition, offset);
        TextLayout[] textLayouts=this.getTextLayouts();
        if(this.textIterator==null||this.textIterator.getEndIndex()==0||
                textLayouts.length==0){
            AttributedString attributedString=new AttributedString("  ");
            AttributedCharacterIterator tempIterator=attributedString.getIterator();
            TextLayout templayout=new TextLayout(tempIterator, new FontRenderContext(null, false, false));
            double x=this.textArea.getX(); 
            double y=this.textArea.getY();
            rectangle=new Rectangle((int)x, (int)y, width, 
                    (int)(templayout.getAscent()+templayout.getDescent()));
            if(debug>0) System.out.println("*** LineBreaker.getCaretRectangle" +
                    ": no text");
            return rectangle;
        }
        int row=offsetCaretPosition.getLineIndex();
        if(row>=0&&row<textLayouts.length){
            TextLayout textLayout=this.getTextLayout(row);
            Rectangle2D bounds=this.getBounds(row);
            int index=offsetCaretPosition.getColumnIndex();
            Shape[] carets=textLayout.getCaretShapes(index);
            Rectangle caretRect=carets[0].getBounds();
            int x=(int)(bounds.getX()+caretRect.getX());
            int y=(int)bounds.getY();
            rectangle= new Rectangle(x, y, width, 
                    (int)(textLayout.getAscent()+textLayout.getDescent()));
            if(debug>0) System.out.println("*** LineBreaker.getCaretRectangle" +
                    ": normal case");
        }
        if(row>=textLayouts.length){
            TextLayout lastlayout=textLayouts[textLayouts.length-1];
            Shape[] carets=lastlayout.getCaretShapes(0);
            rectangle=carets[0].getBounds();
            Rectangle2D bounds=this.getBounds(textLayouts.length-1);
            double height=lastlayout.getAscent()+lastlayout.getDescent();
            double shiftY=height+this.lineSpace;
            rectangle.setRect((int)bounds.getX(), (int)(bounds.getY()+shiftY), 
                    width, (int)rectangle.getHeight());
            if(debug>0) System.err.println("*** Warning LineBreaker.getCaretRectangle:" +
                    ": Temporary lower line of textLayout");
        }
        if(debug>0) System.out.println("** LineBreaker.getCaretRectangle" +
                    ": rectangle="+Util.Rect(rectangle));
        return rectangle;
    }

     public void printTextLayoutPositions(){
         int size=this.lineList.size();
         if(size==0) {
             System.err.println("*** Warning: LineBreaker textPositions=null");
             return;
         }
         System.out.println(" -- LineBreaker printTextPositions");
         Point2D[] textPositions=this.getTextLayoutPositions();
         for(int i=0;i<size;i++){
             System.out.print(" Pos["+i+"]="+textPositions[i]);
             if(i-i/10*10==0) System.out.println("");
         }
     }
     
     public void printTextLayouts(){
         int size=this.lineList.size();
         if(size==0) {
             System.out.println("*** Warning: LineBreaker textLayouts=null");
             return;
         }
         System.out.println("\n-- LineBreaker printTextLayouts totalCharCount="+getCharacterCount());
         TextLayout[] textLayouts=this.getTextLayouts();
         String[] layoutStrings=this.getTextLayoutStrings();
         for(int i=0;i<size;i++){
             //int charCount=textLayouts[i].getCharacterCount();
             String layoutString=layoutStrings[i].replace("\n", "\\n");
             System.out.println("　-- TextLayouts["+i+"] charCount="+textLayouts[i].getCharacterCount()
                     +", layoutString="+layoutString);
         }
         
     }
     
     public void printBounds(){
         int size=this.lineList.size();
         if(size==0) {
             System.out.println("*** Warning: LineBreaker bounds=null");
             return;
         }
         System.out.println(" -- LineBreaker printBouns");
         Rectangle2D[] bounds=this.getBounds();
         size=0;
         if(bounds!=null) size=bounds.length;
         for(int i=0;i<size;i++){
             Line line=(Line)this.lineList.get(i);
             System.out.println(" Bound["+i+"]="+bounds[i]);
         }
     }
     
     public String toString(){
         String str="LineBreaker.toString";
         str+=", textAlign="+this.textAlign+", lineSpace="+this.lineSpace+"\n";
         int size=this.lineList.size();
         if(size==0) {
             str+="textLayouts=null";
             return str;
         }
         for(int i=0;i<size;i++){
             Line line=(Line)this.lineList.get(i);
             str+=line.toString()+"\n";
         }
         return str;
    }
    
    private void rangeCheck(int index, int indexMax) throws RangeException{
        if(index<0||index>indexMax) throw new RangeException();
    } 
    
    private boolean textRangeError(int textIndex, String errMessage){
        boolean ret=false;
        int count=this.getCharacterCount();
        try{
            this.rangeCheck(textIndex, count);
        }catch(RangeException e){
            System.err.println(errMessage+" textIndex="+textIndex+", range=0,"+count);
            e.printStackTrace();
            ret=true;
        }
        return ret;
    }
    
    private boolean lineRangeError(int lineIndex, String errMessage){
        boolean ret=false;
        int lineSize=this.getLineList().size();
        try{
            this.rangeCheck(lineIndex, lineSize);
        }catch(RangeException e){
            System.err.println(errMessage+" lineIndex="+lineIndex+", range=0,"+lineSize);
            e.printStackTrace();
            ret=true;
        }
        return ret;
    }
    
    private boolean lineColumnRangeError(int lineIndex, int columnIndex, String errMessage){
        boolean ret=false;
        int lineSize=this.lineList.size();
        try{
            rangeCheck(lineIndex, lineSize);
        } catch(RangeException e){
            System.err.println(errMessage+" lineIndex="+lineIndex+", range=0,"+lineSize);
            e.printStackTrace();
            ret=true;
        }
        if(lineIndex==lineSize) return ret;
        Line line=(Line)this.lineList.get(lineIndex);
        int columnSize=line.getStringLength();
        try{
            rangeCheck(columnIndex, columnSize);
        } catch(RangeException e){
            System.err.println(errMessage+" lineIndex="+lineIndex+", columnIndex="+columnIndex
                    +", column range=0,"+columnSize);
            e.printStackTrace();
            ret=true;
        }
        return ret;
    }
    
    class RangeException extends Exception {
        private int detail;
        
        RangeException() {}
        
        public String toString() {
            return "range Exception";
        }
    }
}

class StringTokenizerEx{
    String str;
    String delim;
    int index;
    
    public StringTokenizerEx(String str, String delim){
        this.str=str;
        this.delim=delim;
        index=0;
    }
    
    public boolean hasMoreTokens(){
        int newIndex=index;
        boolean more=false;
        newIndex=str.indexOf(delim, index);
        if(newIndex>=0) more=true;
        if(newIndex<0&&index<str.length()) more=true;
        if(newIndex<0&&index>=str.length()) more=false;
        return more;
    }
    
    public String nextToken(){
        String outputString="";
        int newIndex=index;
        newIndex=str.indexOf(delim, index);
        //System.out.print("index="+index+" newIndex="+newIndex);
        if(newIndex>=0) {
            if(newIndex==index) {
                index+=delim.length();
                outputString=delim;
            }
            if(newIndex>index) {
                outputString=str.substring(index,newIndex)+delim;
                index=newIndex+delim.length();
            }
        }
        if(newIndex<0&&index<=str.length()) {
            outputString=str.substring(index,str.length());
            index+=str.length();
        }
        //System.out.println(" return index="+index);
        return outputString;
    }
}

class Line{
    int startCharPosition;
    int endCharPosition;
    Point2D textLayoutPosition=null;
    TextLayout textLayout=null;
    String textLayoutString="";
    
    public Line(){}
    public Line(int startCharPosition, int endCharPosition, Point2D textLayoutPosition, 
            TextLayout textLayout, String textLayoutString){
        this.startCharPosition=startCharPosition;
        this.endCharPosition=endCharPosition;
        this.textLayoutPosition=textLayoutPosition;
        this.textLayout=textLayout;
        this.textLayoutString=textLayoutString;
    }
    
    public int getStartCharPosition(){
        return this.startCharPosition;
    }
    public int getEndCharPosition(){
        return this.endCharPosition;
    }
    public Point2D getTextLayoutPosition(){
        return this.textLayoutPosition;
    }
    public TextLayout getTextLayout(){
        return this.textLayout;
    }
    public String getTextLayoutString(){
        return this.textLayoutString;
    }
    public int getStringLength(){
        return this.textLayoutString.length();
    }
    public String toString(){
        String str="Line ";
        str+="CaretPosition start="+this.startCharPosition+", end="+this.endCharPosition;
        str+=", TextLayout position="+Util.Pt(this.textLayoutPosition);
        str+=", ascent="+this.textLayout.getAscent()+", descent="+this.textLayout.getDescent()
                +", leading="+this.textLayout.getLeading();
        str+=", string="+this.textLayoutString.replace("\n", "\\n");
        return str;
    }
   
}