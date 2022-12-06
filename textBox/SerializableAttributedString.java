package textBox;



import java.text.*;
import java.io.*;
import java.util.*;

public class SerializableAttributedString implements Serializable, Cloneable{
    private String string="";
    private ArrayList attributedIntervalList=new ArrayList();
    public static final String[] versions={"ver0.0"};
    static int debug=0;
    
    public SerializableAttributedString(){} // end of constructor
    
    public SerializableAttributedString(AttributedCharacterIterator iterator){
        AttributedStringUtil attribUtil=new AttributedStringUtil(iterator);
        this.string=attribUtil.getString();
        this.attributedIntervalList=attribUtil.getAttributedIntervalList();
    } // end of constructor
    
    public String getString(){
        return this.string;
    }
    
    public void setString(String str){
        this.string=str;
    }
    
    public ArrayList getAttributedIntervalList(){
        return this.attributedIntervalList;
    }
    
    public void setAttributedIntervalList(ArrayList list){
        this.attributedIntervalList=list;
    }
    
    public AttributedString getAttributedString(){
        AttributedString attribString=new AttributedString(this.string);
        int size=0;
        if(this.attributedIntervalList!=null) size=this.attributedIntervalList.size();
        for(int i=0;i<size;i++){
            AttributedInterval interval=(AttributedInterval) attributedIntervalList.get(i);
            attribString.addAttribute(interval.getKey(), interval.getValue(), 
                    interval.getStart(), interval.getEnd());
        }
        return attribString;
    }
    
    public Object clone(){
        SerializableAttributedString sString=new SerializableAttributedString();
        sString.string=new String(this.string);
        ArrayList arrayList=new ArrayList();
        int size=0;
        if(attributedIntervalList!=null||attributedIntervalList.size()>0)
            size=attributedIntervalList.size();
        for(int i=0;i<size;i++){
            AttributedInterval interval=(AttributedInterval)attributedIntervalList.get(i);
            AttributedInterval clonedInterval=(AttributedInterval)interval.clone();
            arrayList.add(clonedInterval);
        }
        sString.attributedIntervalList=arrayList;
        System.out.println("** AttributedStringData.clone" +
                "\n   original="+this.toString()+
                "\n   cloned="+sString.toString());
        return sString;
    }
    
    public String toString(){
        String str="";
        str+=this.string;
        str=str.replace("\n","\\n");
        int size=this.attributedIntervalList.size();
        if(size==0) {
            str+=", no attribute";
            return str;
        }
        for(int i=0;i<size;i++) {
            AttributedInterval interval=(AttributedInterval)this.attributedIntervalList.get(i);
            AttributedCharacterIterator.Attribute attribKey=interval.getKey();
            Object attribValue=interval.getValue();
            int start=interval.getStart();
            int end=interval.getEnd();
            str+="\n  - interval="+String.valueOf(start)+","
                    +String.valueOf(end)+", key="+attribKey+", value="+attribValue;
        } // end of for
        return str;
    } 
    
   public static int writeSerializableAttributedString(ObjectOutputStream out,
            SerializableAttributedString sAttributedString) throws Exception{
        if(debug>0) System.out.println(" -- writeSerializableAttributedString " +
                "sAttributedString="+sAttributedString.toString());
        int ret=0;
        try{
            out.writeUTF(SerializableAttributedString.versions[0]);
            String str="";
            if(sAttributedString!=null&&!sAttributedString.string.equals("")) str=sAttributedString.string;
            out.writeUTF(str);
            if(!str.equals("")){
                ArrayList attributedIntervalList=sAttributedString.getAttributedIntervalList();
                int size=0;
                if(attributedIntervalList!=null) size=attributedIntervalList.size();
                out.writeInt(size);
                for(int i=0;i<size;i++){
                    AttributedInterval attributedInterval=(AttributedInterval)attributedIntervalList.get(i);
                    out.writeInt(attributedInterval.start);
                    out.writeInt(attributedInterval.end);
                    out.writeObject(attributedInterval.key);
                    out.writeObject(attributedInterval.value);
                }
            }
        } catch(Exception e){
            //System.out.println("*** Error writeSerializableAttributedString version="+version);
            e.printStackTrace();
            ret=-1;
        }
        if(debug>0) System.out.println(" ++ writeSerializableAttributedString ret=="+ret);
        return ret;
    }
   
    public static SerializableAttributedString readSerializableAttributedString(ObjectInputStream in) 
            throws Exception{
        SerializableAttributedString sAttributedString=new SerializableAttributedString();
        String version="";
        try{
            version=in.readUTF();
            if(debug>0) System.out.println(" -- readSerializableAttributedString version="+version);
            String str=in.readUTF();
            if(str.equals("")) str="";
            sAttributedString.string=str;
            if(!str.equals("")){
                int size=0;
                size=in.readInt();
                ArrayList attributedIntervalList=new ArrayList();
                for(int i=0;i<size;i++){
                    int start=in.readInt();
                    int end=in.readInt();
                    AttributedCharacterIterator.Attribute key
                            =(AttributedCharacterIterator.Attribute)in.readObject();
                    Object value=(Object)in.readObject();
                    AttributedInterval attributedInterval=new AttributedInterval(start, end, key,value);
                    attributedIntervalList.add(attributedInterval);
                }
                sAttributedString.attributedIntervalList=attributedIntervalList;
            } else{
                sAttributedString.attributedIntervalList=null;
            }
        } catch(Exception e){
            System.err.println("*** Error readSerializableAttributedString version="+version+", e="+e);
            e.printStackTrace();
        }
        return sAttributedString;
    }
} 