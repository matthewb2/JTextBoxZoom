package textBox;



import java.awt.*;
import java.awt.font.*;
import java.text.*;
import java.io.*;
import java.util.*;
import java.awt.im.*;

public class AttributedStringUtil implements Serializable{
    AttributedCharacterIterator iterator=null;
    ArrayList arrayList=new ArrayList();
    int debug=0;
    int idNumber=0;
    
    public AttributedStringUtil(AttributedCharacterIterator iterator){
        this.iterator=iterator;
        this.createAttributedIntervalList(iterator);
    } // end of constructor
    
    private boolean rangeError(int start, int end, String errMessage){
        boolean ret=false;
        if(start<this.getBeginIndex()||end>this.getEndIndex()||start>end){
            System.err.println(errMessage+" start="+start+", end="+end
                    +", range="+this.getBeginIndex()+","+this.getEndIndex());
            ret=true;
        }
        return ret;
    }

    public int getBeginIndex(){
        if(this.iterator==null) return -1;
        return this.iterator.getBeginIndex();
    }
    
    public int getEndIndex(){
        if(this.iterator==null) return -1;
        return this.iterator.getEndIndex();
    }
    
    public void setAttributedString(AttributedCharacterIterator iterator){
        this.iterator=iterator;
        this.arrayList.clear();
        this.createAttributedIntervalList(iterator);
    }
    
    private void createAttributedIntervalList(AttributedCharacterIterator iterator){
        this.arrayList.clear();
        if(iterator!=null&&iterator.getEndIndex()>0){
            Set<AttributedCharacterIterator.Attribute> keys=iterator.getAllAttributeKeys();
            ArrayList keyList = new ArrayList(keys);
            for(int i=0;i<keyList.size();i++){
                AttributedCharacterIterator.Attribute key
                        =(AttributedCharacterIterator.Attribute)keyList.get(i);
                AttributedInterval[] attribIntervals=this.createAttributedIntervals(key);
                for(int j=0;j<attribIntervals.length;j++){
                    this.arrayList.add(attribIntervals[j]);
                }
            }//end of for i
        } 
        //return arrayList;
    }

    private AttributedInterval[] createAttributedIntervals(AttributedCharacterIterator.Attribute key){
        Vector vector=new Vector();
        Object value;
        char c=this.iterator.first();
        while(c!=CharacterIterator.DONE) {
            int runStart=this.iterator.getRunStart(key);
            int runLimit=this.iterator.getRunLimit(key);
            value=this.iterator.getAttribute(key);
            if( value!=null) {
                AttributedInterval attribInterval=new AttributedInterval(runStart, runLimit,
                        key, value);
                vector.add(attribInterval);
            }
            c=this.iterator.setIndex(runLimit);
        } //end of while

        AttributedInterval[] intervals=new AttributedInterval[vector.size()];
        for(int i=0;i<vector.size();i++){
            intervals[i]=(AttributedInterval)vector.get(i);
        }
        return intervals;
    } //getInterval

    public ArrayList getAttributedIntervalList(){
        return this.arrayList;
    }
    
    public String getString(){
        String str="";
        if(this.iterator==null) return str;
        //int begin=this.iterator.getBeginIndex();
        int end=this.iterator.getEndIndex();
        int toCopy = end;
        char c=this.iterator.first();
        while (toCopy-- > 0) {
            str+=Character.toString(c);
            c=this.iterator.next();
        }
        return str;
    } //getString
    
    public String getString(int start, int end){
        String str="";
        boolean ret=this.rangeError(start, end, "*** Error: AttributedStringUtil.getString");
        if(ret) return str;
        str=this.getString();
        str=str.substring(start, end);
        return str;
    } //getString
    
    public AttributedString getAttributedString(){
        String str=this.getString();
        int intervalsSize=this.arrayList.size();
        if(str==null||str.equals("")) return null;
        AttributedString attribStr=new AttributedString(str);
        for(int i=0;i<intervalsSize;i++){
            AttributedInterval attribInterval=(AttributedInterval)this.arrayList.get(i);
            AttributedCharacterIterator.Attribute key=attribInterval.key;
            Object value=attribInterval.value;
            int start=attribInterval.start;
            int end=attribInterval.end;
            attribStr.addAttribute(key, value, start, end);
        }
        return attribStr;
    } //getAttributedString

    public AttributedString getAttributedSubString(int start, int end){
        boolean ret=this.rangeError(start, end, "*** Error: AttributedStringUtil.getAttributedSubString");
        if(ret) return null;
        String str=getString();
        int intervalsLength=this.arrayList.size();
        AttributedString attribStr=null;
        if(str.equals("")||str==null) return null;
        String subString=str.substring(start, end);
        attribStr=new AttributedString(subString);
        for(int i=0;i<intervalsLength;i++){
            AttributedInterval attribInterval=(AttributedInterval)this.arrayList.get(i);
            AttributedCharacterIterator.Attribute key=attribInterval.key;
            Object value=attribInterval.value;
            int attribIntervalStart=attribInterval.start;
            int attribIntervalEnd=attribInterval.end;
            int intervalStart=attribIntervalStart;
            int intervalEnd=attribIntervalEnd;
            if(intervalStart<start) intervalStart=start;
            if(intervalEnd<start) intervalEnd=start;
            if(intervalStart>end) intervalStart=end;
            if(intervalEnd>end) intervalEnd=end;
            if(intervalStart<intervalEnd) attribStr.addAttribute(key, value, 
                        intervalStart-start, intervalEnd-start);
        }
        return attribStr;
    } //getAttributedString
    
    public AttributedString getTextLayoutString(int start, int end, 
            String escapeChar, String replaceChar){
        boolean ret=this.rangeError(start, end, "*** Error: AttributedStringUtil.getAttributedSubString");
        if(ret) return null;
        String str=getString();
        int intervalsLength=this.arrayList.size();
        AttributedString attribStr=null;
        if(str.equals("")||str==null) return null;
        String substring=str.substring(start, end);
        int index=0;
        Vector<Integer> vector=new Vector<Integer>();
        while(substring.indexOf(escapeChar, index)>=0){
            index=substring.indexOf(escapeChar, index);
            vector.add(index);
            index++;
        }
        substring=substring.replace(escapeChar, replaceChar);
        //System.out.println();
        attribStr=new AttributedString(substring);
        for(int i=0;i<intervalsLength;i++){
            AttributedInterval attribInterval=(AttributedInterval)this.arrayList.get(i);
            AttributedCharacterIterator.Attribute key=attribInterval.key;
            Object value=attribInterval.value;
            int attribIntervalStart=attribInterval.start;
            int attribIntervalEnd=attribInterval.end;
            int intervalStart=attribIntervalStart;
            int intervalEnd=attribIntervalEnd;
            if(intervalStart<start) intervalStart=start;
            if(intervalEnd<start) intervalEnd=start;
            if(intervalStart>end) intervalStart=end;
            if(intervalEnd>end) intervalEnd=end;
            if(intervalStart<intervalEnd) attribStr.addAttribute(key, value, 
                        intervalStart-start, intervalEnd-start);
        }
        if(vector.size()>0){
            AttributedCharacterIterator iterator=attribStr.getIterator();
            FontStyle defaultStyle=FontStyle.getDefaultFontStyle();
            defaultStyle.setFontColor(Color.DARK_GRAY);
            defaultStyle.setFontSize(12);
            for(int i=0;i<vector.size(); i++){
                index=vector.get(i).intValue();
                iterator=defaultStyle.setTo(attribStr.getIterator(), index, index+1);
            }
            attribStr=(new AttributedStringUtil(iterator)).getAttributedString();
        }
        return attribStr;
    } //getAttributedString
    
    public AttributedInterval[] getAttributedIntervals(){
        int size=this.arrayList.size();
        AttributedInterval[] attribIntervals=new AttributedInterval[size];
        for(int i=0;i<size;i++) attribIntervals[i]=(AttributedInterval)this.arrayList.get(i);
        return attribIntervals;
    }
    
    public AttributedInterval[] getAttributedIntervals(int start, int end){
        boolean ret=this.rangeError(start, end, "*** Error: AttributedStringUtil.getAttributedSubString");
        if(ret) return null;
        Vector vector=new Vector();
        int size=this.arrayList.size();
        AttributedInterval attribInterval=null;
        //Interval[] intervals=null;
        Interval intersection=null;
        Interval subinterval=new Interval(start, end);
        
        for(int i=0;i<size;i++) {
            attribInterval=(AttributedInterval)this.arrayList.get(i);
            Interval interval=new Interval(attribInterval.start, attribInterval.end);
            intersection=Interval.intersection(interval, subinterval);

            AttributedInterval newInterval=null;
            if(intersection!=null){
                newInterval=new AttributedInterval(intersection.getStart(), 
                        intersection.getEnd(), attribInterval.key, attribInterval.value);
                vector.add(newInterval);
            }
        }
        
        AttributedInterval[] attribIntervals=new AttributedInterval[vector.size()];
        for(int i=0;i<vector.size();i++) {
            attribIntervals[i]=(AttributedInterval)vector.get(i);
        }
        return attribIntervals;
    }

    public AttributedInterval[] getAttributedIntervals(AttributedCharacterIterator.Attribute key, 
            int start, int end){
        boolean ret=this.rangeError(start, end, "*** Error: AttributedStringUtil.getAttributedIntervals");
        if(ret) return null;
        
        Vector vector=new Vector();
        int size=this.arrayList.size();
        AttributedInterval attribInterval=null;
        //Interval[] intervals=null;
        Interval intersection=null;
        Interval subinterval=new Interval(start, end);
        for(int i=0;i<size;i++) {
            attribInterval=(AttributedInterval)this.arrayList.get(i);
            AttributedCharacterIterator.Attribute intervalKey=attribInterval.key;
            Interval interval=new Interval(attribInterval.start, attribInterval.end);
            if(intervalKey==key) {
                intersection=Interval.intersection(interval, subinterval);
                AttributedInterval newInterval=null;
                if(intersection!=null){
                    newInterval=new AttributedInterval(intersection.getStart(), 
                            intersection.getEnd(), attribInterval.key, attribInterval.value);
                    vector.add(newInterval);
                }
            }
        }
        AttributedInterval[] attribIntervals=new AttributedInterval[vector.size()];
        for(int i=0;i<vector.size();i++) attribIntervals[i]=(AttributedInterval)vector.get(i);
        return attribIntervals;
    }

    public void addAttribute(AttributedCharacterIterator.Attribute key,
             Object value, int start, int end){
        boolean ret=this.rangeError(start, end, "*** Error: AttributedStringUtil.addAttribute");
        if(ret) return;
        
        AttributedString attribStr=this.getAttributedString();
        attribStr.addAttribute(key, value, start, end);
        this.iterator=attribStr.getIterator();
        //this.arrayList.clear();
        this.createAttributedIntervalList(attribStr.getIterator());
        if(debug>0) System.out.println("\n + AttributedStringUtil.addAttribute"+
                ", key="+key+", value="+value+", start,end="+start+","+end+
                "\n  string="+this.toString());
    } // end of addAttribute
    

    public void removeAttribute(AttributedCharacterIterator.Attribute key,
            int start, int end){
        boolean ret=this.rangeError(start, end, "*** Error: AttributedStringUtil.removeAttribute");
        if(ret) return;
        
        String str=getString();
        int intervalsSize=this.arrayList.size();
        AttributedString attribStr=null;
        attribStr=new AttributedString(str);
        for(int i=0;i<intervalsSize;i++){
            AttributedInterval attribInterval=(AttributedInterval)this.arrayList.get(i);
            AttributedCharacterIterator.Attribute currentKey=attribInterval.getKey();
            Object value=attribInterval.getValue();
            int intervalStart=attribInterval.getStart();
            int intervalEnd=attribInterval.getEnd();
            if(currentKey!=key) {
                attribStr.addAttribute(currentKey, value, intervalStart, intervalEnd);
            } else{
                Interval interval=new Interval(intervalStart, intervalEnd);
                Interval subInterval=new Interval(start, end);
                Interval[] intervals=Interval.sub(interval, subInterval);
                for(int j=0;j<intervals.length;j++){
                    attribStr.addAttribute(currentKey, value, intervals[j].getStart(), 
                            intervals[j].getEnd());
                }
            }
        }
        this.iterator=attribStr.getIterator();
        this.arrayList.clear();
        this.createAttributedIntervalList(attribStr.getIterator());
        //this.arrayList=this.createAttributedIntervalList(attribStr.getIterator());
        if(debug>0) System.out.println(" - AttributedStringUtil.removeAttribute"+
                " iterator="+this.toString());
    } // end of addAttribute
    
    
    public void removeAttribute(AttributedCharacterIterator.Attribute key){
        String str=this.getString();
        int intervalsSize=this.arrayList.size();
        AttributedString attribStr=new AttributedString(str);
        for(int i=0;i<intervalsSize;i++){
            AttributedInterval attribInterval=(AttributedInterval)this.arrayList.get(i);
            AttributedCharacterIterator.Attribute currentKey=attribInterval.getKey();
            Object value=attribInterval.getValue();
            int start=attribInterval.getStart();
            int end=attribInterval.getEnd();
            if(currentKey!=key) attribStr.addAttribute(currentKey, value, start, end);
        }
        this.iterator=attribStr.getIterator();
        this.arrayList.clear();
        this.createAttributedIntervalList(attribStr.getIterator());
        if(debug>0) System.out.println(" - AttributedStringUtil.removeAttribute"+
                " iterator="+this.toString());
    } // end of addAttribute
    
    public boolean hasAttribute(AttributedCharacterIterator.Attribute key){
        int size=this.arrayList.size();
        boolean has=false;
        for(int i=0;i<size;i++){
            AttributedInterval interval=(AttributedInterval)this.arrayList.get(i);
            if(interval.key==key) { 
                has=true; 
                break;
            }
        }
        return has;
    } // end of addAttribute

    public String toString(){
        String str="";
        String text=this.getString();
        text=text.replace("\n","\\n");
        str+="- Text: "+text+"\"";
        int size=this.arrayList.size();
        if(size==0) {
            str+="\n- Text attributes; none";
            return str;
        }
        str+="\n- Text attributes";
        for(int i=0;i<size;i++) {
            AttributedInterval interval=(AttributedInterval)this.arrayList.get(i);
            AttributedCharacterIterator.Attribute attribKey=interval.key;
            Object attribValue=interval.value;
            int start=interval.start;
            int end=interval.end;
            str+="\n  - interval="+String.valueOf(start)+","
                    +String.valueOf(end)+", key="+attribKey+", value="+attribValue;
        } // end of for
        return str;
    }
    
    public static String getString(AttributedCharacterIterator iterator){
        AttributedStringUtil util=new AttributedStringUtil(iterator);
        return util.getString();
    }
    
    public static AttributedString getAttributedString(AttributedCharacterIterator iterator){
        AttributedStringUtil util=new AttributedStringUtil(iterator);
        return util.getAttributedString();
    }
    
    public static AttributedCharacterIterator createCompositeText(AttributedCharacterIterator iterator1, 
            AttributedCharacterIterator iterator2, int insertionPosition, boolean highLight){
        if(iterator1==null&&iterator2==null) return null;
        if(iterator2==null||iterator2.getEndIndex()==0) return iterator1;
        if(iterator1!=null&&insertionPosition>iterator1.getEndIndex()) {
            System.err.println("*** Warning AttributedStringContainer.createCompositeText: " +
                    "insertionPosition< out of range");
            if(insertionPosition>iterator1.getEndIndex()) insertionPosition=iterator1.getEndIndex();
        }
        int str2Length=iterator2.getEndIndex();
        AttributedStringUtil util1=new AttributedStringUtil(iterator1);
        AttributedStringUtil util2=new AttributedStringUtil(iterator2);
        if(!highLight){
            TextAttribute attribute=TextAttribute.INPUT_METHOD_HIGHLIGHT;
            if(util1.hasAttribute(attribute)) util1.removeAttribute(attribute);
            if(util2.hasAttribute(attribute)) util2.removeAttribute(attribute);
        }
        String str1=util1.getString();
        String str2=util2.getString();
        StringBuffer compositeStr=new StringBuffer("");
        if(!str1.equals("")) {
            StringBuffer str1Buffer=new StringBuffer(str1);
            compositeStr=str1Buffer.insert(insertionPosition, str2);
        } else if(!str2.equals("")){
            compositeStr=new StringBuffer(str2);
        }
      // set attributes to iterator1 of the composite text
        AttributedString compositeAttribStr=new AttributedString(compositeStr.toString());
        AttributedInterval[] attribInterval1=util1.getAttributedIntervals();
        for(int i=0;i<util1.arrayList.size();i++){
            AttributedCharacterIterator.Attribute key=attribInterval1[i].getKey();
            Object value=attribInterval1[i].getValue();
            int start=attribInterval1[i].getStart();
            int end=attribInterval1[i].getEnd();
              if(end<=insertionPosition) {
                  compositeAttribStr.addAttribute(key, value, start, end);
              }
              if(start<insertionPosition&&end>insertionPosition) {
                  compositeAttribStr.addAttribute(key, value, start, insertionPosition);
                  compositeAttribStr.addAttribute(key, value, insertionPosition+str2Length, 
                          end+str2Length);
              }
              if(start>=insertionPosition){
                  compositeAttribStr.addAttribute(key, value, start+str2Length, end+str2Length);
              }
           //}
        }
      // set attributes to iterator2 of the composite text
        AttributedInterval[] attribInterval2=util2.getAttributedIntervals();
        for(int i=0;i<attribInterval2.length;i++){
            AttributedCharacterIterator.Attribute key=attribInterval2[i].getKey();
            Object value=attribInterval2[i].getValue();
            int start=attribInterval2[i].getStart();
            int end=attribInterval2[i].getEnd();
            try{
            compositeAttribStr.addAttribute(key, value, start+insertionPosition,
                        end+insertionPosition);
            } catch(Exception e){
                System.err.println("*** Error createCompositeText: insertionPosition="+
                        insertionPosition+", start,end="+start+","+end+
                        "\n   e="+e);
            }
        } //for(int i)
        if(highLight) {
            compositeAttribStr.addAttribute(TextAttribute.INPUT_METHOD_HIGHLIGHT,
                new InputMethodHighlight(false, InputMethodHighlight.RAW_TEXT),
                insertionPosition, insertionPosition+str2.length());
        }
        return compositeAttribStr.getIterator();
    }

} //End of class
