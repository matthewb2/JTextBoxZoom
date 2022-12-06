package textBox;


public class CaretPosition{
    
    int lineIndex=0;
    int columnIndex=0; 
    int textIndex=0;
    boolean lineFeedOption=false;
    static int debug=0;
    
    public CaretPosition(int lineIndex, int columnIndex){
        this.lineIndex=lineIndex;
        this.columnIndex=columnIndex;
        this.textIndex=-1;
    }
    
    public CaretPosition(int lineIndex, int columnIndex, int textIndex){
        this.lineIndex=lineIndex;
        this.columnIndex=columnIndex;
        this.textIndex=textIndex;
    }
    
    public CaretPosition(int lineIndex, int columnIndex, boolean lineFeedOption){
        this.lineIndex=lineIndex;
        this.columnIndex=columnIndex;
        this.textIndex=-1;
        this.lineFeedOption=lineFeedOption;
    }
    
    public CaretPosition(int lineIndex, int columnIndex, int textIndex, boolean lineFeedOption){
        this.lineIndex=lineIndex;
        this.columnIndex=columnIndex;
        this.textIndex=textIndex;
        this.lineFeedOption=lineFeedOption;
    }
    
    public boolean isValid(){
        return (this.lineIndex>=0&&this.columnIndex>=0&&this.textIndex>=0);
    }
    
    public int getLineIndex(){
        return this.lineIndex;
    }
    
    public int getColumnIndex(){
        return this.columnIndex;
    }
    
    public int getTextIndex(){
        return this.textIndex;
    }
    
    public void resetPosition(){
        this.lineIndex=0;
        this.columnIndex=0;
        this.textIndex=0;
    }

    public void updateCaretPosition(int newTextIndex, LineBreaker lineBreaker,
             String callFrom){
        int size=lineBreaker.getLineList().size();
        if(size==0||newTextIndex<=0) {
            this.lineIndex=0;
            this.columnIndex=0;
            this.textIndex=0;
            return;
        }
        int[] indices=lineBreaker.getLineColumnIndices(newTextIndex);
        int lineIndex=indices[0];
        int columnIndex=indices[1];
        String preChar="";
        if(this.lineFeedOption&&columnIndex>0){
            preChar=lineBreaker.getPreceedingCharacter(lineIndex, columnIndex);
            if(preChar.equals("\n")){
                lineIndex++;
                columnIndex=0;
            }
        }
        if(debug>0){
            System.out.println("CaretPosition.update updated caretposition: "+this.toString()
                    +", preceedingCharacter="+preChar.replace("\n", "\\n")+", callFrom="+callFrom);
        }
        this.lineIndex=Math.max(lineIndex, 0);
        this.columnIndex=Math.max(columnIndex, 0);
        this.textIndex=Math.max(newTextIndex, 0);
     }

     public void columnOffset(int offset, LineBreaker lineBreaker){
        int size=lineBreaker.getLineList().size();
        if(size==0){
            this.resetPosition();
            return;
        }
         int line=this.getLineIndex();
         int column=this.getColumnIndex();
         if(line<0||column<0) {
             System.err.println("*** Warning  LineBreaker.getOffsetCaretPosition;" +
                     " invalid caret position ");
             return;
         }
         //
         if(offset!=0) {
             this.offsetCaretPosition(line, column, offset, lineBreaker);
         }
         return;
     }

     private void offsetCaretPosition(int lineIndex, int columnIndex, 
             int offset, LineBreaker lineBreaker){
        int size=lineBreaker.getLineList().size();
        if(size==0||lineIndex<0||columnIndex<0){
            this.resetPosition();
            return ;
        }
        int totalCount=lineBreaker.getCharacterCount();
        int count=lineBreaker.getCharacterCount(lineIndex, columnIndex);
        int newTextIndex=count+offset;
        if(newTextIndex>totalCount) newTextIndex=totalCount;
        this.updateCaretPosition(newTextIndex, lineBreaker,"CaretPosition");
     }
          
     public void lineOffset(int lineOffset, LineBreaker lineBreaker){
        int size=lineBreaker.getLineList().size();
        if(size==0){
            this.resetPosition();
            return;
        }
         int lineIndex=this.getLineIndex();
         int colIndex=this.getColumnIndex();
         if(lineIndex<0||colIndex<0) {
             System.err.println("*** Warning LineBreaker.getOffsetCaretPosition;" +
                     " invalid caret position ");
             return;
         }
         int newLineIndex=lineIndex+lineOffset;
         if(newLineIndex<0) newLineIndex=0;
         if(newLineIndex>size-1) newLineIndex=size-1;
         int count=lineBreaker.getCharacterCount(newLineIndex);
         int newColIndex=colIndex;
         if(newColIndex>count) {
             newColIndex=count;
             if(lineBreaker.getPreceedingCharacter(newLineIndex, count).equals("\n"))
                 newColIndex=count-1;
         }
         this.offsetCaretPosition(newLineIndex, newColIndex, 0, lineBreaker);
         return;
     }

    public void print(String message){
        System.out.println(message+", lineIndex="+lineIndex+", columnIndex="
                +columnIndex+", textIndex="+textIndex);
    }
    
    public String toString(){
        String str="NULL";
        if(this.isValid()){
            str="lineIndex="+lineIndex+", columnIndex="
                    +columnIndex+", textIndex="+textIndex
                    +", lineFeedOption="+lineFeedOption;
        }
        return str;
    }
    
    public String toStringOfTextIndex(){
        String str="";
        str="TextIndex="+textIndex;
        return str;
    }
    
    public Object clone(){
        CaretPosition position=new CaretPosition(this.lineIndex, this.columnIndex,
                this.textIndex, this.lineFeedOption);
        return position;
    }

    public static CaretPosition getCaretPosition(int textIndex, boolean lineFeedOption, 
            LineBreaker lineBreaker, String callFrom){
        int[] indices=lineBreaker.getLineColumnIndices(textIndex);
        int lineIndex=indices[0];
        int columnIndex=indices[1];
        String str="";
        if(lineFeedOption&&columnIndex>0){
            str=lineBreaker.getPreceedingCharacter(lineIndex, columnIndex);
            //System.out.println("CaretPosition.updateCaretPosition preceedingCharacter="+str);
            if(str.equals("\n")){
                lineIndex++;
                columnIndex=0;
            }
        }
        CaretPosition caretPosition=new CaretPosition(lineIndex, columnIndex, textIndex, lineFeedOption);
        if(debug>0){
            System.out.println("getCaretPosition(textIndex) callFrom="+callFrom
                    +", CaretPosition="+caretPosition.toString()+", preceedingCharacter="+str);
        }
        return caretPosition;
     }

    public static CaretPosition getCaretPosition(int lineIndex, int columnIndex, 
            boolean lineFeedOption, LineBreaker lineBreaker, String callFrom){
        int textIndex=lineBreaker.getTextIndex(lineIndex, columnIndex);
        String str="";
        if(lineFeedOption&&columnIndex>0){
            str=lineBreaker.getPreceedingCharacter(lineIndex, columnIndex);
            if(str.equals("\n")){
                lineIndex++;
                columnIndex=0;
            }
        }
        CaretPosition caretPosition=new CaretPosition(lineIndex, columnIndex, textIndex, lineFeedOption);
        if(debug>0){
            System.out.println("getCaretPosition(lineIndex, columnIndex) callFrom="+callFrom
                    +", CaretPosition="+caretPosition.toString()+", preceedingCharacter="+str);
        }
        return caretPosition;
     }
}