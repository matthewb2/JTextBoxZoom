package util;



import textBox.SerializableAttributedString;
import textBox.ShapeContainer;
import textBox.TransferableAttributedString;
import textBox.AttributedStringUtil;
import textBox.TextBox;
import main.ObjectTable;
import main.DrawPanel;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.*;
import java.text.*;
import java.awt.Toolkit;
import java.awt.datatransfer.*;


public class Edit implements ClipboardOwner {

    Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    boolean lostOwnership = false;
    Point2D clickedPointSave = null;
    public static final int PlainString = 1;
    public static final int AttributedString = 2;
    public static final int Shape = 3;
    public static final int Image = 4;
    public static int debug = 0;

    public void cut() {
        ContainerManager containerManager = ObjectTable.getContainerManager();
        ShapeContainer shapeContainer = containerManager.getEditableTextBox();
        if (shapeContainer != null) {
            boolean copied = copyString(shapeContainer, true);
            if (copied) {
                return;
            }
        }

    }

    public void copy() {
        ContainerManager containerManager = ObjectTable.getContainerManager();
        ShapeContainer shapeContainer = containerManager.getEditableTextBox();
        if (shapeContainer != null) {
            boolean copied = this.copyString(shapeContainer, false);
            if (copied) {
                return;
            }
        }

    }

    private boolean copyString(ShapeContainer shapeContainer, boolean cut) {
        boolean selected = false;
        TextBox textBox = shapeContainer.getTextBox();
        if (textBox == null || !textBox.hasSelectedText()) {
            return false;
        }
        AttributedString attribStr = textBox.getSelectedText();
        if (cut) {
            textBox.deleteSelectedText(TextBox.COMMAND);
            System.out.println("*** Warning textBox.deleteSelectedText called");
        }
        try {
            AttributedStringUtil util=new AttributedStringUtil(attribStr.getIterator());
            attribStr=util.getAttributedString();
            TransferableAttributedString transferableText 
                    =new TransferableAttributedString(attribStr.getIterator());
            this.systemClipboard.setContents(transferableText, this);
            selected = true;
            if (debug > 0) {
                System.out.println("** Edit.cutString string=" + transferableText.toString());
                System.out.println("** SystemClipboard information (Edit.copyString) ; "
                        + this.getClipboardInformation(systemClipboard, this));
            }
        } catch (Exception e) {
            System.err.println("*** Warning Edit.copyString System Clipboard "
                    + "is currently not available, e=" + e);
        }
        return selected;
    }//cutString

    public void paste() {
        Transferable contents = this.systemClipboard.getContents(null);
        if (debug > 0) {
            System.out.println("** SystemClipboard information (Edit.paste);"
                    + this.getClipboardInformation(systemClipboard, this));
        }
        if (contents == null) {
            System.err.println("*** Warning  Edit.paste;  nothing pasted");
            return;
        } else {
            if (debug > 0) {
                DataFlavor[] dataFlavors = contents.getTransferDataFlavors();
                for (int i = 0; i < dataFlavors.length; i++) {
                    System.out.println("dataFlavors[" + i + "]=" + dataFlavors[i]
                            + ", presentableName=" + dataFlavors[i].getHumanPresentableName());
                }
            }
        }
        boolean pasted = false;
        DataFlavor dataFlavor=TransferableAttributedString.SerializableAttributedStringFlavor;
        if (contents.isDataFlavorSupported(dataFlavor)) {
            AttributedString attribStr = null;
            try {
                Object object = contents.getTransferData(dataFlavor);
                if (object != null) {
                    String name=object.getClass().getSimpleName();
                     if(debug>0)System.out.println("** Edit.paste contents.getTransferData data type="+name);
                    if(name.equals("SerializableAttributedString")){
                        SerializableAttributedString sStr = (SerializableAttributedString) object;
                        attribStr=sStr.getAttributedString();
                    } else if(name.equals("AttributedString")){
                        attribStr = (AttributedString) object;
                    } else if(name.equals("AttributedCharacterIterator")){
                         AttributedCharacterIterator iterator=(AttributedCharacterIterator) object;
                         AttributedStringUtil util=new AttributedStringUtil(iterator);
                         attribStr=util.getAttributedString();
                    }
                    pasted = this.pasteAttributedString(attribStr);
                }
            } catch (Exception e) {
                
            }
            if (pasted) {
                if (debug > 0) {
                    System.out.println("AttributedStringFlavor pasted");
                }
                return;
            }
        }

        pasted = false;
        if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String str = "";
            try {
                str = (String) (contents.getTransferData(DataFlavor.stringFlavor));
                pasted = this.pasteString(str);
            } catch (Exception e) {
                System.err.println("*** Warning: Edit.paste String e=" + e);
            }
            if (pasted) {
                if (debug > 0) {
                    System.out.println("stringFlavor pasted");
                }
                return;
            }
        }
 
    } //paste

    public boolean hasClipboad(DataFlavor dataFlavor) {
        Transferable contents = this.systemClipboard.getContents(null);
        if (contents.isDataFlavorSupported(dataFlavor)) {
            return true;
        }
        return false;
    }

    public boolean pasteString(String str) {
        boolean pasted = false;
        if (debug >=0) {
            System.out.println("** Edit.pasteString " + str);
        }
        ContainerManager containerManager = ObjectTable.getContainerManager();
        ShapeContainer container = containerManager.getEditableTextBox();
        if (container != null && container.isTextBox()) {
            TextBox textBox = container.getTextBox();
            textBox.deleteSelectedText(TextBox.COMMAND);
            AttributedString attribStr = new AttributedString(str);
            int position = textBox.getTextIndex();
            //
            if (position < 0) {
                position = 0;
            }
            textBox.insertText(TextBox.COMMAND, position, attribStr.getIterator());
            pasted = true;
            return pasted;
        }
        return pasted;
    }

    private boolean pasteAttributedString(AttributedString attribStr) {
        if (debug >=0) {
            System.out.println("** Edit.pasteAttributedString " + attribStr.toString());
        }
        boolean pasted = false;
        ContainerManager containerManager = ObjectTable.getContainerManager();
        ShapeContainer container = containerManager.getEditableTextBox();
        if (container != null && container.isTextBox()) {
            TextBox textBox = container.getTextBox();
            textBox.deleteSelectedText(TextBox.COMMAND);
            //
            int position = textBox.getTextIndex();
            if (position < 0) {
                position = 0;
            }
            textBox.insertText(TextBox.COMMAND, position, attribStr.getIterator());
            pasted = true;
            return pasted;
        }
        return pasted;
    }


    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        this.lostOwnership = true;
        if (debug > 0) {
            System.out.println("** lostOwnership: Clipboard contents replaced");
            System.out.println("-- " + this.getClipboardInformation(clipboard, this));
        }
        DataFlavor[] flavors = contents.getTransferDataFlavors();
        if (debug > 0) {
            System.out.println("-- Transferable contents DataFlavors listing");
        }
        for (int i = 0; i < flavors.length; i++) {
            if (debug > 0) {
                System.out.println("    DataFlavor[" + i + "]  "
                        + "name=" + flavors[i].getHumanPresentableName()
                        + ", mime type=" + flavors[i].getMimeType());
            }
        }
    }

    private String getClipboardInformation(Clipboard clipboard, ClipboardOwner clipboardOwner) {
        String str = "Clipboard name=" + clipboard.getName();
        try {
            Transferable content = clipboard.getContents(clipboardOwner);
            str += "\n-- getContents=" + content + ", content.simpleName="
                    + content.getClass().getSimpleName();
            String name = clipboard.getName();
            str += ", getName=" + name;
            DataFlavor[] flavors = clipboard.getAvailableDataFlavors();
            if (flavors.length == 0) {
                str += ", flavors=null";
            }
            for (int i = 0; i < flavors.length; i++) {
                str += "\n-- AvailableDataFlavor[" + i + "], name="
                        + flavors[i].getHumanPresentableName()
                        + ", MIME type=" + flavors[i].getMimeType();
            }
        } catch (Exception e) {
            str += "\n-- system clipboard is not available now, e=" + e;
        }
        return str;
    }

    public void delete() {
        if (debug > 0) {
            System.out.println("** Edit.delete called");
        }
        ContainerManager containerManager = ObjectTable.getContainerManager();
        DrawPanel drawPanel = ObjectTable.getDrawPanel();
        ShapeContainer shapeContainer = containerManager.getEditableTextBox();
//delete text
        if (shapeContainer != null && shapeContainer.isTextBox()) {
            TextBox textBox = shapeContainer.getTextBox();
            int ret = textBox.deleteTextByDelCommandOrBSkey(TextBox.COMMAND);
            drawPanel.repaint();
            if (ret > 0 || ret < 0) {
                return;
            }
        }
        drawPanel.repaint();
    } //delete
}
