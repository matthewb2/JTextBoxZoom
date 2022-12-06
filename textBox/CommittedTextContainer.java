package textBox;

import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import util.*;

class CommittedTextContainer{
    
    private AttributedCharacterIterator committedText=null;
    public int debug=0;
    //int idNumber=0;
    
    public CommittedTextContainer(){}
    
    private boolean rangeError(int start, int end, String errMessage){
        boolean ret=false;
        if(start<this.getBeginIndex()||end>this.getEndIndex()||start>end){
            System.err.println(errMessage+" start="+start+", end="+end
                    +", range="+this.getBeginIndex()+","+this.getEndIndex());
            ret=true;
        }
        return ret;
    }
    
    private boolean rangeError(int pos, String errMessage){
        boolean ret=false;
        if(pos<this.getBeginIndex()||pos>this.getEndIndex()){
            System.err.println(errMessage+" pos="+pos
                    +", range="+this.getBeginIndex()+","+this.getEndIndex());
            ret=true;
        }
        return ret;
    }

    public int getBeginIndex(){
        if(this.committedText==null) return -1;
        return this.committedText.getBeginIndex();
    }
    
    public int getEndIndex(){
        if(this.committedText==null) return -1;
        return this.committedText.getEndIndex();
    }
    
    public AttributedCharacterIterator getCommittedText(){
        return this.committedText;
    }

    public void setCommittedText(AttributedCharacterIterator committedText){
        AttributedStringUtil attribUtil=new AttributedStringUtil(committedText);
        TextAttribute attribute=TextAttribute.INPUT_METHOD_HIGHLIGHT;
        if( attribUtil.hasAttribute(attribute)) attribUtil.removeAttribute(attribute);
        AttributedString text=attribUtil.getAttributedString();
        if(text==null) this.committedText=null;
        else this.committedText=text.getIterator();
    }

    public boolean isComittedText(){
        if(this.committedText==null) return false;
        if(this.getBeginIndex()==this.getEndIndex()) return false;
        return true;
    }
    
    public String getString(){
        String str=(new AttributedStringUtil(this.committedText)).getString();
        return str;
    }
    
    public String getString(int start, int end){
        String str="";
        boolean ret=this.rangeError(start, end, "*** Error: CommitedTextContainer.getString");
        if(ret) return str;
        str=(new AttributedStringUtil(this.committedText)).getString();
        str=str.substring(start, end);
        return str;
    }

    public AttributedString getAttributedString(){
        return AttributedStringUtil.getAttributedString(this.committedText);
    }
    
    public AttributedString getAttributedSubString(int start, int end){
        boolean ret=this.rangeError(start, end, "*** Error: CommitedTextContainer.getAttributedSubString");
        if(ret) return null;
        AttributedStringUtil attribUtil=new AttributedStringUtil(this.committedText);
        //String str=attribUtil.getString();
        if(debug>0) System.out.println("** CommitedTextContainer.getAttributedSubString" +
                " current text="+Util.Text(this.committedText)+
                ", delete range start, end="+start+","+end);
        return attribUtil.getAttributedSubString(start, end);
    }
    
    public AttributedCharacterIterator getDisplayText(int insertionPosition, 
            AttributedCharacterIterator composedText) {
        
        if(debug>0) System.out.println(" - CommitedTextContainer.getDisplayText " +
                 "composedText="+Util.Text(composedText)+
                 ", insertionPosition="+insertionPosition);
        AttributedCharacterIterator displayText=null;
        if (composedText==null) {
            displayText=this.committedText;
        } else {
            displayText=AttributedStringUtil.createCompositeText(this.committedText, 
                    composedText, insertionPosition, true);
        }
        if(debug>0) System.out.println(" - CommitedTextContainer.getDisplayText " +
                 "compositeText="+Util.Text(displayText));
        return displayText;
    }
    
    public void insertText(int insertionPosition, AttributedCharacterIterator string){
        if(this.committedText==null) insertionPosition=0;
        if(this.committedText!=null&&insertionPosition>this.committedText.getEndIndex()) {
            System.err.println("*** Warning: CommitedTextContainer.string " +
                    "insertionPosition="+insertionPosition+" out of range");
            insertionPosition=this.committedText.getEndIndex();
        }
        if(string==null) {
            System.err.println("*** Warning: CommitedTextContainer.string " +
                    "string=null");
            insertionPosition=this.committedText.getEndIndex();
        }
        TextAttribute attribute=TextAttribute.INPUT_METHOD_HIGHLIGHT;
        if(this.committedText!=null&&this.committedText.getEndIndex()>0) {
            AttributedStringUtil util=new AttributedStringUtil(this.committedText);
            if( util.hasAttribute(attribute)) util.removeAttribute(attribute);
            this.committedText=util.getAttributedString().getIterator();
        }

        AttributedStringUtil util=new AttributedStringUtil(string);
        AttributedCharacterIterator.Attribute attribute1=TextAttribute.LANGUAGE;
        AttributedCharacterIterator.Attribute attribute2=TextAttribute.READING;
        AttributedCharacterIterator.Attribute attribute3=TextAttribute.INPUT_METHOD_SEGMENT;
        if( util.hasAttribute(attribute1)) util.removeAttribute(attribute1);
        if( util.hasAttribute(attribute2)) util.removeAttribute(attribute2);
        if( util.hasAttribute(attribute3)) util.removeAttribute(attribute3);
        AttributedCharacterIterator insertIterator=util.getAttributedString().getIterator();

        this.committedText=AttributedStringUtil.createCompositeText(this.committedText, 
                    insertIterator, insertionPosition, false);
        if(debug>0) {
            String fontStyleStr="null";
            System.out.println(" - CommitedTextContainer.string" +
                    ", insertionPosition="+insertionPosition+", insertFontStyle="+fontStyleStr+
                    ",\n string="+Util.Text(string)+
                    ",\n committedText="+Util.Text(this.committedText));
        }
    }

    public void deleteText(int method, int delStart, int delEnd) {
        boolean ret=this.rangeError(delStart, delEnd, "*** Error: CommitedTextContainer.deleteText");
        if(ret) return;
        AttributedStringUtil attribUtil=new AttributedStringUtil(this.committedText);
        String str=attribUtil.getString();

        StringBuffer strBuffer=new StringBuffer(str);
        int length=strBuffer.length();
        if(delStart>delEnd){
            int temp=delStart;
            delStart=delEnd;
            delEnd=temp;
        }
        strBuffer=strBuffer.delete(delStart, delEnd);
        if(strBuffer.length()==0) {
            this.committedText=null;
            return;
        } else {
            AttributedString attribStr=new AttributedString(strBuffer.toString());
            int size=attribUtil.arrayList.size();
            AttributedInterval[] attribIntervals=attribUtil.getAttributedIntervals();
            Interval newInterval=null;
            for(int i=0;i<size;i++){
                AttributedCharacterIterator.Attribute key=attribIntervals[i].getKey();
                Object value=attribIntervals[i].getValue();
                Interval interval=new Interval(attribIntervals[i].getStart(), 
                        attribIntervals[i].getEnd());
                Interval delInterval=new Interval(delStart, delEnd);
                newInterval=Interval.del(interval, delInterval);
                if(newInterval!=null) {
                    attribStr.addAttribute(key, value, newInterval.getStart(), 
                            newInterval.getEnd());
                 }
            } //for(int i)
            this.committedText=attribStr.getIterator();
        }
        if(debug>0) System.out.println(" - CommitedTextContainer.deleteChar" +
                    ", delStart, delEnd="+delStart+","+delEnd+
                    ", committedText="+Util.Text(this.committedText));
    } 
/*
    public SerializableAttributedString getSerializableAttributedString(){
        SerializableAttributedString data=new SerializableAttributedString(this.getCommittedText());
        if(debug>0) System.out.println("getSerializableAttributedString date="+data);
        return data;
    }

    public void setSerializableAttributedString(SerializableAttributedString data){
        if(debug>0) System.out.println(" -AT("+(idNumber++)+") CommitedTextContainer.setAttributedStringData" +
                    ", data="+data.getString());
        String str=data.getString();
        ArrayList arrayList=data.getAttributedIntervalList();
        int intervalsSize=0;
        if(arrayList!=null) intervalsSize=arrayList.size();
        AttributedString attribStr=null;
        if(str==null||str.equals("")) this.committedText=null;
        attribStr=new AttributedString(str);
        for(int i=0;i<intervalsSize;i++){
            AttributedInterval attribInterval=(AttributedInterval)arrayList.get(i);
            AttributedCharacterIterator.Attribute key=attribInterval.getKey();
            Object value=attribInterval.getValue();
            int start=attribInterval.getStart();
            int end=attribInterval.getEnd();
            attribStr.addAttribute(key, value, start, end);
        }
        this.committedText=attribStr.getIterator();
        if(debug>0) System.out.println(" -AT("+(idNumber++)+") CommitedTextContainer.setAttributedStringData end" +
                    ", committedText="+this.getString());
    }
*/
}

