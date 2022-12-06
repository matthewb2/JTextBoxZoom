package util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import main.*;
import textBox.*;
import util.*;


public class ButtonOfToggle extends JToggleButton implements ItemListener{
    ButtonOfToggelAction action=new ButtonOfToggelAction(this);
    protected Border raisedBorder=new BevelBorder(BevelBorder.RAISED);
    protected Border loweredBorder=new BevelBorder(BevelBorder.LOWERED);
    ImageIcon imageEnabled=null;
    ImageIcon imageDisabled=null;
    public static int debug=0;
    
    public ButtonOfToggle(String commandName, String tip, String[] accelerators) {
        this.setActionCommand(commandName);
        this.addActionListener(this.action);
        this.setName(commandName);
        this.setText(commandName);
        this.setName(commandName);
        this.setToolTipText(tip);
        this.setStandardButtonStyle();
        this.setAccelerators(accelerators);
        this.addItemListener(this);
    }
    
    public ButtonOfToggle(String commandName, boolean setText,  ImageIcon imageEnabled, 
                ImageIcon imageDisabled, String tip, String[] accelerators) {
        super(imageDisabled, false);
        this.setActionCommand(commandName);
        this.addActionListener(this.action);
        this.setName(commandName);
        this.imageEnabled=imageEnabled;
        this.imageDisabled=imageDisabled;
        if(setText) {
             this.setText(commandName);
             this.setHorizontalTextPosition(SwingConstants.LEFT);
        }
        this.setToolTipText(tip);
        this.setStandardButtonStyle();
      //-----------------------------//  
        this.setAccelerators(accelerators);
        this.addItemListener(this);
      //-----------------------------//    
    }

    public void setStandardButtonStyle() { 
        this.setBorder(this.raisedBorder);
        this.setIconTextGap(0);
        this.setHorizontalTextPosition(SwingConstants.CENTER );
        this.setVerticalTextPosition(SwingConstants.BOTTOM );
        this.setHorizontalAlignment(CENTER);
        this.setFont(DrawMenu.MenuFont);
        this.setBackground(null);
        this.setForeground(Color.BLACK);
    }
  
    public void setAccelerators(String[] accelerators){
        int size=0;
        if(accelerators!=null) size=accelerators.length;
        for(int i=0;i<size;i++){
            KeyStroke stroke = KeyStroke.getKeyStroke(accelerators[i]);
            InputMap inputMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(stroke, accelerators[i]);
            ActionMap actionMap = this.getActionMap();
            actionMap.put(accelerators[i], this.action);
            if(debug>0) System.out.println("-- ButtonOfAcceleratoractionMap stroke="+stroke+
                    ", key="+accelerators[i]+", value="+actionMap.get(accelerators[i]));
        }
    }
    
    public void setEnabled(boolean enable) { 
        super.setEnabled(enable);
        if(enable) super.setIcon(this.imageEnabled);
        else super.setIcon(this.imageDisabled);
    }
    
    public void itemStateChanged(ItemEvent e) {
        this.setBorder(isSelected() ? this.loweredBorder : this.raisedBorder);
    }

    
    public static ButtonOfToggle createButton(String commandName, int width, String tip, 
            String[] accelerators) {
        ButtonOfToggle button=new ButtonOfToggle(commandName, tip, accelerators);
        if(width>0){
             button.setPreferredSize(new Dimension(width, DrawMenu.buttonHeight));
        } else {
             TextLayout textlayout=new TextLayout(commandName, DrawMenu.MenuFont, 
                  new FontRenderContext(null, false, false));
             int textWidth=(int)textlayout.getAdvance()+5;
             button.setPreferredSize(new Dimension(textWidth, DrawMenu.buttonHeight));
        }
        //MenuUtil menuUtil=ObjectTable.getMenuUtil();
        //menuUtil.setMenuComponent(button);
        //if(debug>0) menuUtil.printMenuComponent(button);
        button.requestFocus();
        return button;
    }

    public static ButtonOfToggle createButton(String commandName, boolean setText, 
          String imageName, String tip) {
         ButtonOfToggle button=null;
         if(imageName==null||imageName.equals("")){
             button = new ButtonOfToggle(commandName, tip, null);
             TextLayout textlayout=new TextLayout(commandName, DrawMenu.MenuFont, 
                     new FontRenderContext(null, false, false));
             int textWidth=(int)textlayout.getAdvance()+5;
             button.setPreferredSize(new Dimension(textWidth, DrawMenu.buttonHeight));
         } else{
            ImageIcon icon=DrawImageIcon.get(imageName, DrawMenu.imageWidth, 
                    DrawMenu.imageHeight);
            button = new ButtonOfToggle(commandName, setText, icon, icon, tip, null);
            button.setPreferredSize(new Dimension(DrawMenu.buttonWidth, DrawMenu.buttonHeight));
         }
         //MenuUtil menuUtil=ObjectTable.getMenuUtil();
         //menuUtil.setMenuComponent(button);
         //if(debug>0) menuUtil.printMenuComponent(button);
         return button;
    }

    public static ButtonOfToggle createButton(String commandName, String imageEnabled, 
          String imageDisabled, String tip) {
         ButtonOfToggle button=null;
         if(imageEnabled.equals("")||imageDisabled.equals("")){
            System.err.println("*** Error Button.createButton image not found");
            return null;
         } else{
            ImageIcon iconEnabled=DrawImageIcon.get(imageEnabled, DrawMenu.imageWidth, 
                    DrawMenu.imageHeight);
            ImageIcon iconDisenabled=DrawImageIcon.get(imageDisabled, DrawMenu.imageWidth, 
                    DrawMenu.imageHeight);
            button = new ButtonOfToggle(commandName, false, iconEnabled, iconDisenabled, tip, null);
            button.setPreferredSize(new Dimension(DrawMenu.buttonWidth, DrawMenu.buttonHeight));
         }
        
         return button;
    }
}

class ButtonOfToggelAction extends AbstractAction{
    int debug=0;
    ButtonOfToggle button;
    public ButtonOfToggelAction(ButtonOfToggle button){
        this.button=button;
    }
    
    public void actionPerformed(ActionEvent e) {
        String commandName=e.getActionCommand();
        String componentClassName=""; 
        componentClassName=e.getSource().getClass().getSimpleName();
        String componentName=((Component)e.getSource()).getName();
        if(debug>0) System.out.println(" - ButtonOfToggelAction.actionPerformed  " +
                "commandName="+commandName+", source component name="+componentName+
                ", source class simple name="+componentClassName);
        String ctrlCommand=Util.getASCIIControlString(commandName);
        if(ctrlCommand!=null&&!ctrlCommand.equals("")) {
            if(debug>0) System.out.println(" - ButtonOfToggelAction  ctrlCommand="+ctrlCommand+
                    ", Hex of action command="+Util.toHexString(commandName));
            if(ctrlCommand.equalsIgnoreCase("ctrl H")) commandName="del";
            if(ctrlCommand.equalsIgnoreCase("DELETE")) commandName="del";
        }
        main.DrawPanel drawPanel=ObjectTable.getDrawPanel();
        DrawMenu drawMenu=ObjectTable.getDrawMenu();
        
        if(commandName.equals("zoom")){
            DialogOfZoom zoom = new DialogOfZoom();
            zoom.showDialog();
            this.button.setSelected(false);
            drawPanel.repaint();
        }
        
        if(commandName.equals("undo")){
            UndoDrawManager undoDrawManager=ObjectTable.getUndoDrawManager();
            undoDrawManager.undo(); 
            drawPanel.repaint();
            DrawMenu.undo.setSelected(false);
        }
        
        if(commandName.equals("redo")){
            UndoDrawManager undoDrawManager=ObjectTable.getUndoDrawManager();
            undoDrawManager.redo(); 
            drawPanel.repaint();
            DrawMenu.redo.setSelected(false);
        }

        if(commandName.equals("del")){
            ObjectTable.getEdit().delete();
            drawPanel.repaint();
            DrawMenu.del.setSelected(false);
        }
        
        if(commandName.equals("bold")){
            FontStyle menuStyle=FontStyle.getFontStyleFromMenu();
          //-----------------------------//
            FontStyle fontStyle=new FontStyle();
            fontStyle.setBold(menuStyle.getBold());
            drawMenu.setFontStyle(fontStyle);
          //-----------------------------//
            drawPanel.repaint();
        }
        if(commandName.equals("italic")){
            FontStyle menuStyle=FontStyle.getFontStyleFromMenu();
          //-----------------------------//
            FontStyle fontStyle=new FontStyle();
            fontStyle.setItalic(menuStyle.getItalic());
            drawMenu.setFontStyle(fontStyle);
          //-----------------------------//
            drawPanel.repaint();
        }
        if(commandName.equals("underline")){
            FontStyle menuStyle=FontStyle.getFontStyleFromMenu();
          //-----------------------------//
            FontStyle fontStyle=new FontStyle();
            fontStyle.setUnderLine(menuStyle.getUnderLine());
            drawMenu.setFontStyle(fontStyle);
          //-----------------------------//
            drawPanel.repaint();
        }
        if(commandName.equals("subscript")){
            FontStyle menuStyle=FontStyle.getFontStyleFromMenu();
          //-----------------------------//
            FontStyle fontStyle=new FontStyle();
            fontStyle.setSuperScript(menuStyle.getSuperScript());
            drawMenu.setFontStyle(fontStyle);
          //-----------------------------// 
            drawPanel.repaint();
        }
        if(commandName.equals("superscript")){
            FontStyle menuStyle=FontStyle.getFontStyleFromMenu();
          //-----------------------------//
            FontStyle fontStyle=new FontStyle();
            fontStyle.setSuperScript(menuStyle.getSuperScript());
            drawMenu.setFontStyle(fontStyle);
          //-----------------------------// 
            drawPanel.repaint();
        }
        
    }//End of actionPerformed

}
