/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package textBox;

import java.io.*;
import java.text.*;
import java.util.*;

public class AttributedInterval implements Serializable, Cloneable {
    public int start;
    public int end;
    public AttributedCharacterIterator.Attribute key;
    public Object value;
    
    public AttributedInterval(int start, int end, 
            AttributedCharacterIterator.Attribute key, Object value){
        this.start=start;
        this.end=end;
        this.key=key;
        this.value=value;
    }
    
    public int getStart(){
        return this.start;
    }
    
    public int getEnd(){
        return this.end;
    }
    
    public AttributedCharacterIterator.Attribute getKey(){
        return this.key;
    }
    
    public Object getValue(){
        return this.value;
    }
    
    public Object clone(){
        AttributedInterval interval=new AttributedInterval(this.start, this.end, 
                this.key, this.value);
        return interval;
    }
    
    public String toString(){
        String str="";
        str+="AttributedInterval: interval="+String.valueOf(this.start)+","
                    +String.valueOf(this.end)+", key="+this.key+", value="+this.value;
        return str;
        
    }
    
    public static String toString(AttributedInterval[] intervals){
        String str="AttributedInterval array num="+intervals.length;
        for(int i=0;i<intervals.length;i++){
            str+="\n"+intervals[i].toString();
        }
        return str;
    }
}