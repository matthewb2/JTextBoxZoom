package util;

import textBox.ShapeContainer;
import textBox.FontStyle;
import textBox.TextBox;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.EventListener;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.border.Border.*;
import main.DrawPanel;
import main.ObjectTable;
import util.*;


public class DrawMenu {
    int debug=0;
    static String font=Font.DIALOG;
    //static String font="Arial Unicode MS";
    public final static int imageWidth=20, imageHeight=20;
    public final static int menuItemImageWidth=64, menuItemImageHeight=10;
    public final static int buttonWidth=22, buttonHeight=22;
    public final static Font DefaultFont=new Font(font, Font.PLAIN, 12);
    public final static Font MenuFont=new Font(font, Font.BOLD, 12);
    public final static Font MenuItemFont=new Font(font, Font.BOLD, 11);
    
    public static ButtonOfToggle zoom=null;
    public static ButtonOfToggle undo=null;
    public static ButtonOfToggle redo=null;
    public static ButtonOfPulldownMenu edit=null;
    public static ButtonOfToggle del=null;
    public static ComboBox fontFamily=null;
    public static ComboBox fontSize=null;
    public static ButtonOfToggle fontBold=null;
    public static ButtonOfToggle fontItalic=null;
    public static ButtonOfToggle fontUnderline=null;
    public static ButtonOfToggle fontSubscript=null;
    public static ButtonOfToggle fontSuperscript=null;
    public static ButtonOfColorChooser fontColor=null;

    
    public static JPanel createEditGroup(){
        JPanel panel=new JPanel();
        FlowLayout flowLayout=new FlowLayout(FlowLayout.LEADING, 0, 0);
        Border paneEdge = BorderFactory.createLineBorder(Color.black);
        Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        panel.setLayout(flowLayout);
        panel.setBorder(paneEdge);
        panel.setBorder(raisedetched);
        panel.setFont(DrawMenu.MenuFont);

        //Component component;
        zoom=ButtonOfToggle.createButton("zoom", true, "", "zoom");
        panel.add(zoom);
        
        undo=ButtonOfToggle.createButton("undo", "undoEnabled32T.png", "undoDisenabled32T.png", "undo");
        undo.setEnabled(false);
        panel.add(undo);
        redo=ButtonOfToggle.createButton("redo","redoEnabled32T.png", "redoDisenabled32T.png","redo");
        redo.setEnabled(false);
        panel.add(redo);
        
        int width=32;
        edit=ButtonOfPulldownMenu.createEditButton(width);
        panel.add(edit);
        String[] accelarators={"typed \b", "DELETE"};
        del=ButtonOfToggle.createButton("del", width, "del", accelarators);
        panel.add(del);

        return panel;
    }
    
    public static JPanel createFontStyleGroup(){
        JPanel panel=new JPanel();
        FlowLayout flowLayout=new FlowLayout(FlowLayout.LEADING, 0, 0);
        Border paneEdge = BorderFactory.createLineBorder(Color.black);
        Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        //JPanel fontPanel=new JPanel();
        panel.setLayout(flowLayout);
        panel.setBorder(paneEdge);
        panel.setBorder(raisedetched);
        panel.setFont(DrawMenu.MenuFont);

        //Component component;
        fontFamily=ComboBox.createFontFamilyComboBox("font family");
        panel.add(fontFamily);
        fontSize=ComboBox.createFontSizeComboBox("font size");
        panel.add(fontSize);

        fontBold=ButtonOfToggle.createButton("bold", false, "font_bold32T.png", "Bold");
        panel.add(fontBold);
        fontItalic=ButtonOfToggle.createButton("italic", false, "font_italic32T.png", "Italic");
        panel.add(fontItalic);
        fontUnderline=ButtonOfToggle.createButton("underline", false, "font_underline32T.png", 
                "Underline");
        panel.add(fontUnderline);
        fontSubscript=ButtonOfToggle.createButton("subscript", false, "subscript32T.png", 
                "subscript");
        panel.add(fontSubscript);
        fontSuperscript=ButtonOfToggle.createButton("superscript", false, "superscript32T.png", 
                "superscript");
        panel.add(fontSuperscript);
        fontColor=ButtonOfColorChooser.createColorChooserButton("font color", false, 
                "font_color32T.png", "font color");
        panel.add(fontColor);

        return panel;
    }
    
    
    public void setFontStyle(FontStyle fontStyle){
        ContainerManager containerManager=ObjectTable.getContainerManager();
      //---------------------------------//
        //this.containerManager.undoSetupStart();
      //---------------------------------//
        ShapeContainer container=containerManager.getEditableTextBox();
        if(container!=null){
            TextBox textBox=container.getTextBox();
            textBox.setCurrentFontStyle(fontStyle, "ExecCommand");
            if(textBox.hasSelectedText()) {
                textBox.setFontStyle(fontStyle);
                //
            }
        } else{
        
        }
      //-------------------------------//
        //this.containerManager.undoSetupEnd();
      //-------------------------------//
    }
}

class DrawImageIcon {

    public static ImageIcon get(String imageName, int width, int height){
        ImageIcon imageIcon=null;
        if(imageName.equals("")) return imageIcon;
        BufferedImage image=getBufferedImage(imageName);
        if(image==null) return imageIcon;
        Image newImage=image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        imageIcon=new ImageIcon(newImage);
        return imageIcon;
    }
     
    private static BufferedImage getBufferedImage(String imageName){
        File file = new File("");
        String currentPath=file.getAbsolutePath();
        String filePath=currentPath+ "/build/classes/res/"+imageName;
        //System.out.println(currentPath);
        BufferedImage bufferedImage=null;
        try{
           bufferedImage= ImageIO.read(new File(filePath));
        }catch (IOException e){
           System.err.println("*** Error: DrawImageIcon.getImage" +
                   ", Image file not found, fileName="+imageName+"\n  e="+e);
        }
        return bufferedImage;
    }
}