package textBox;

import java.io.*;
import java.util.*;

public class Interval implements Serializable{
    private int start;
    private int end;
    //public Interval(){}
    public Interval(int start, int end){
        this.start=start;
        this.end=end;
        if(start>=end) System.err.println("*** Error Interval.Constructor ; invalid range"+
                ", strat,end="+start+","+end);
    }
    public int getStart(){
        return this.start;
    }
    
    public int getEnd(){
        return this.end;
    }
    
    public int length(){
        return (this.end-this.start);
    }
    
    public String toString(){
        String str="";
        str+="start,end="+String.valueOf(this.start)+","+String.valueOf(this.end);
        return str;
    }
    
    public static Interval[] add(Interval interval, Interval addInterval){
        Vector vector=new Vector();
        if(interval.end<=addInterval.start||interval.start>=addInterval.end){
            if(interval.end==addInterval.start){
                Interval newInterval=new Interval(interval.start, addInterval.end);
                vector.add(newInterval);
            } else if(interval.start==addInterval.end){
                Interval newInterval=new Interval(addInterval.start, interval.end);
                vector.add(newInterval); 
            } else {
                vector.add(interval);
                vector.add(addInterval);
            }
        }

        if(interval.start>=addInterval.start&&interval.start<addInterval.end&&interval.end>addInterval.end){
            Interval newInterval=new Interval(addInterval.start, interval.end);
            vector.add(newInterval);
        }
        
       if(interval.start<addInterval.start&&interval.end>addInterval.start&&interval.end<=addInterval.end){
            Interval newInterval=new Interval(interval.start, addInterval.end);
            vector.add(newInterval);
        }
        
       if(interval.start<addInterval.start&&interval.end>addInterval.end){
            vector.add(interval);
        }
        
       if(interval.start>=addInterval.start&&interval.end<=addInterval.end){
            vector.add(addInterval);
        }
        
        int size=0;
        if(vector!=null) size=vector.size();
        Interval[] intervals=new Interval[size];
        for(int i=0;i<size;i++) intervals[i]=(Interval)vector.get(i);

        String str=" ++ intervalAdd interval="+String.valueOf(interval.start)+
                ","+String.valueOf(interval.end)+
                 ", addInterval="+String.valueOf(addInterval.start)+
                 ","+String.valueOf(addInterval.end);
        if(size==0) str+=", addeded interval non";
        else for(int i=0;i<size;i++) str+=", added interval["+i+"]="+((Interval)vector.get(i)).toString();
        //System.out.println(str);
        return intervals;
    }
    
    public static Interval[] sub(Interval interval, Interval subInterval){
        Vector vector=new Vector();
        if(interval.end<=subInterval.start||interval.start>=subInterval.end){
            vector.add(interval);
        }

        if(interval.start>=subInterval.start&&interval.start<subInterval.end&&interval.end>subInterval.end){
            Interval newInterval=new Interval(subInterval.end, interval.end);
            vector.add(newInterval);
        }
        
       if(interval.start<subInterval.start&&interval.end>subInterval.start&&interval.end<=subInterval.end){
            Interval newInterval=new Interval(interval.start, subInterval.start);
            vector.add(newInterval);
        }
        
       if(interval.start<subInterval.start&&interval.end>subInterval.end){
            Interval newInterval=new Interval(interval.start, subInterval.start);
            vector.add(newInterval);
            newInterval=new Interval(subInterval.end, interval.end);
            vector.add(newInterval);
        }
        
        int size=0;
        if(vector!=null) size=vector.size();
        Interval[] intervals=new Interval[size];
        for(int i=0;i<size;i++) intervals[i]=(Interval)vector.get(i);

        String str=" ++ intervalSubtract interval="+String.valueOf(interval.start)+
                ","+String.valueOf(interval.end)+
                 ", subtract interval="+String.valueOf(subInterval.start)+
                 ","+String.valueOf(subInterval.end);
        if(size==0) str+=", subtracted interval non";
        else for(int i=0;i<size;i++) str+=", subtracted interval["+i+"]="+((Interval)vector.get(i)).toString();
        //System.out.println(str);
        return intervals;
    }
/*
    public static Interval[] intersection(Interval interval, Interval multInterval){
        return mult(interval, multInterval);
    }
*/
    public static Interval intersection(Interval interval1, Interval interval2){
        //Vector vector=new Vector();
        Interval newInterval=null;
        if(interval1.end<=interval2.start||interval1.start>=interval2.end){
        }

        if(interval1.start>=interval2.start&&interval1.start<interval2.end&&interval1.end>interval2.end){
            newInterval=new Interval(interval1.start, interval2.end);
        }
        
       if(interval1.start<interval2.start&&interval1.end>interval2.start&&interval1.end<=interval2.end){
            newInterval=new Interval(interval2.start, interval1.end);
        }
        
       if(interval1.start<interval2.start&&interval1.end>interval2.end){
           newInterval=interval2;
        }
        
       if(interval1.start>=interval2.start&&interval1.end<=interval2.end){
            newInterval=interval1;
        }
        return newInterval;
    }
/*
    public static Interval[] intersection(Interval interval, Interval multInterval){
        Vector vector=new Vector();
        if(interval.end<=multInterval.start||interval.start>=multInterval.end){
        }

        if(interval.start>=multInterval.start&&interval.start<multInterval.end&&interval.end>multInterval.end){
            Interval newInterval=new Interval(interval.start, multInterval.end);
            vector.add(newInterval);
        }
        
       if(interval.start<multInterval.start&&interval.end>multInterval.start&&interval.end<=multInterval.end){
            Interval newInterval=new Interval(multInterval.start, interval.end);
            vector.add(newInterval);
        }
        
       if(interval.start<multInterval.start&&interval.end>multInterval.end){
            vector.add(multInterval);
        }
        
       if(interval.start>=multInterval.start&&interval.end<=multInterval.end){
            vector.add(interval);
        }
        
        int size=0;
        if(vector!=null) size=vector.size();
        Interval[] intervals=new Interval[size];
        for(int i=0;i<size;i++) intervals[i]=(Interval)vector.get(i);

        String str=" ++ intervalMult interval="+String.valueOf(interval.start)+
                ","+String.valueOf(interval.end)+
                 ", multiply interval="+String.valueOf(multInterval.start)+
                 ","+String.valueOf(multInterval.end);
        if(size==0) str+=", multiplied interval non";
        else for(int i=0;i<size;i++) str+=", multiplied interval["+i+"]="+((Interval)vector.get(i)).toString();
        //System.out.println(str);
        return intervals;
    }
*/
/*
    public static Interval[] insert(Interval interval, int insertionIndex, 
            int insertionLength){
        Vector vector=new Vector();
        
        if(insertionIndex>=interval.end){
            vector.add(interval);
        }
        
        if(insertionIndex<=interval.start){
            Interval newInterval=new Interval(interval.start+insertionLength,
                    interval.end+insertionLength);
            vector.add(newInterval);
        }
        
        if(insertionIndex>interval.start&&insertionIndex<interval.end){
            Interval newInterval=new Interval(interval.start, insertionIndex);
            vector.add(newInterval);
            newInterval=new Interval(insertionIndex+insertionLength, interval.end+insertionLength);
            vector.add(newInterval);
        }
        
        int size=0;
        if(vector!=null) size=vector.size();
        Interval[] intervals=new Interval[size];
        for(int i=0;i<size;i++) intervals[i]=(Interval)vector.get(i);

        String str=" ++ interval insertion interval="+String.valueOf(interval.start)+
                ","+String.valueOf(interval.end)+
                 ", inserttionIndex="+String.valueOf(insertionIndex)+
                 ",insertionLength="+String.valueOf(insertionLength);
        if(size==0) str+=", inserted interval non";
        else for(int i=0;i<size;i++) str+=", inserted interval["+i+"]="+((Interval)vector.get(i)).toString();
        //System.out.println(str);
        return intervals;
    }
*/
    public static Interval del(Interval interval, Interval delInterval){
        Vector vector=new Vector();
        Interval newInterval=null;
//interval      *----------*  
//delInterval                  *-------* 
        if(interval.end<=delInterval.start){
            newInterval=interval;
        }
//interval                  *----------*  
//delInterval   *-------* 
        if(interval.start>=delInterval.end){
            newInterval=new Interval(interval.start-delInterval.length(),
                    interval.end-delInterval.length());
            //vector.add(newInterval);
        }
//interval          *----------*  
//delInterval   *-------* 
        if(interval.start>=delInterval.start&&interval.start<delInterval.end&&interval.end>delInterval.end){
            newInterval=new Interval(delInterval.start, interval.end-delInterval.length());
            //vector.add(newInterval);
        }
//interval      *----------*  
//delInterval      *-----------* 
        if(interval.start<delInterval.start&&interval.end>delInterval.start&&interval.end<=delInterval.end){
            newInterval=new Interval(interval.start, delInterval.start);
            //vector.add(newInterval);
        }
//interval      *--------------*  
//delInterval      *--------* 
        if(interval.start<delInterval.start&&interval.end>delInterval.end){
            newInterval=new Interval(interval.start, interval.end-delInterval.length());
            //vector.add(newInterval);
        }
//interval        *-------*  
//delInterval   *-------------* 
        if(interval.start>=delInterval.start&&interval.end<=delInterval.end){
        }
        //System.out.println("");
        return newInterval;
    }
/*
    public static Interval[] del(Interval interval, Interval delInterval){
        Vector vector=new Vector();
       //(1)
        if(interval.end<=delInterval.start){
            vector.add(interval);
        }
      //(2)
        if(interval.start>=delInterval.end){
            Interval newInterval=new Interval(interval.start-delInterval.length(),
                    interval.end-delInterval.length());
            vector.add(newInterval);
        }
      //(3)
        if(interval.start>=delInterval.start&&interval.start<delInterval.end&&interval.end>delInterval.end){
            Interval newInterval=new Interval(delInterval.start, interval.end-delInterval.length());
            vector.add(newInterval);
        }
      //(4)
        if(interval.start<delInterval.start&&interval.end>delInterval.start&&interval.end<=delInterval.end){
            Interval newInterval=new Interval(interval.start, delInterval.start);
            vector.add(newInterval);
        }
      //(5)
        if(interval.start<delInterval.start&&interval.end>delInterval.end){
            Interval newInterval=new Interval(interval.start, interval.end-delInterval.length());
            vector.add(newInterval);
        }
      //(6)
        if(interval.start>=delInterval.start&&interval.end<=delInterval.end){
        }
        
        int size=0;
        if(vector!=null) size=vector.size();
        Interval[] intervals=new Interval[size];
        for(int i=0;i<size;i++) intervals[i]=(Interval)vector.get(i);

        String str=" ++ interval delete interval="+String.valueOf(interval.start)+
                ","+String.valueOf(interval.end)+
                 ", delete interval="+String.valueOf(delInterval.start)+
                 ","+String.valueOf(delInterval.end);
        if(size==0) str+=", deleted interval non";
        else for(int i=0;i<size;i++) str+=", deleted interval["+i+"]="+((Interval)vector.get(i)).toString();
        //System.out.println(str);
        return intervals;
    }
*/
}