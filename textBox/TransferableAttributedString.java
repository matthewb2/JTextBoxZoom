package textBox;



import java.text.*;
import java.awt.datatransfer.*;
import java.io.*;

public class TransferableAttributedString implements Transferable, Serializable{
    public static final DataFlavor SerializableAttributedStringFlavor =
            new DataFlavor(java.io.Serializable.class, "SerializableAttributedString");
    //public static final DataFlavor SerializableAttributedStringFlavor =
    //        new DataFlavor(DataFlavor.javaSerializedObjectMimeType , "SerializableAttributedString");
    public static final DataFlavor AttributedStringFlavor =
            new DataFlavor("application/x-java-remote-object", "AttributedString");
    private SerializableAttributedString serializableString;
    private AttributedString attributedString;
    private AttributedCharacterIterator iterator;

    
    public TransferableAttributedString(AttributedCharacterIterator iterator){
        this.serializableString=new SerializableAttributedString(iterator);
        this.iterator=iterator;
    } // end of constructor
    
    public TransferableAttributedString(AttributedString attributedString){
        this.attributedString=attributedString;
        this.serializableString=new SerializableAttributedString(attributedString.getIterator());
        this.iterator=attributedString.getIterator();
    } // end of constructor

//Transferable method
    public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException {
        if (flavor.equals(SerializableAttributedStringFlavor)){
                return this.serializableString;
        }

        if (flavor.equals(AttributedStringFlavor)){
            return this.attributedString;
        }
        
        if (flavor.equals(DataFlavor.stringFlavor)) {
            return this.serializableString.getString();
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

//Transferable method
    public DataFlavor[] getTransferDataFlavors(){
        DataFlavor[] flavors=new DataFlavor[3];
        flavors[0]=SerializableAttributedStringFlavor;
        flavors[1]=DataFlavor.stringFlavor;
        flavors[2]=AttributedStringFlavor;
        return flavors;
    }
//Transferable method
    public boolean isDataFlavorSupported(DataFlavor flavor){
        boolean supported=false;
        if(flavor==SerializableAttributedStringFlavor) supported=true;
        if(flavor==DataFlavor.stringFlavor) supported=true;
        if(flavor==AttributedStringFlavor) supported=true;
        return supported;
    }
      
} 