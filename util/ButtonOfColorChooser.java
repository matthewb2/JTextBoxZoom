package util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import main.DrawPanel;
import main.ObjectTable;
import textBox.FontStyle;
import static util.ButtonOfColorChooser.debug;

public class ButtonOfColorChooser extends JButton implements ActionListener, ColorSelectionListener{
    Color currentColor=null;
    public static int debug=0;

    public ButtonOfColorChooser(String commandName, String tip) {
        this.setActionCommand(commandName);
        this.addActionListener(this);
        this.setName(commandName);
        this.setText(" "+commandName+" ");
        this.setToolTipText(tip);
        this.setStandardButtonStyle();
        
  }
    public ButtonOfColorChooser(String commandName, boolean setText, 
                ImageIcon imageIcon, String tip) {
        super(imageIcon);
        this.setActionCommand(commandName);
        this.addActionListener(this);
        this.setName(commandName);
        if(setText) setText(commandName);
        this.setToolTipText(tip);
        this.setStandardButtonStyle();
        
  }

    public void setStandardButtonStyle() { 
        Border raisedBorder = new BevelBorder(BevelBorder.RAISED);
        this.setBorder(raisedBorder);
        this.setIconTextGap(0);
        this.setHorizontalTextPosition(SwingConstants.CENTER );
        this.setVerticalTextPosition(SwingConstants.BOTTOM );
        this.setHorizontalAlignment(CENTER);
        this.setFont(DrawMenu.MenuFont);
        this.setBackground(null);
        this.setForeground(Color.BLACK);
        this.setOpaque(true);
  }

  public Color getCurrentColor() { 
      return this.currentColor; 
  }

  public void setCurrentColor(Color color) { 
        this.currentColor=color; 
        CustomColorChooserDialog dialog=ObjectTable.getCustomColorChooserDialog();
        dialog.getPreviewPanel().setSelectedColor(color);
        if(debug>0) System.out.println("ButtonOfColorChooser setCurrentColor");
  }

    public void actionPerformed(ActionEvent e) {
        String commandName=e.getActionCommand();
        String componentClassName=""; 
        componentClassName=e.getSource().getClass().getSimpleName();
        String componentName=((Component)e.getSource()).getName();

        if(debug>=0) System.out.println("- ColorChooserAction.actionPerformed  " +
                "commandName="+commandName+
                ", source component name="+componentName+
                ", source class simple name="+componentClassName);
        CustomColorChooserDialog dialog=ObjectTable.getCustomColorChooserDialog();
        String title="  Command: font color";
        dialog.commandLabel.setText(title);
        dialog.removeColorSelectionListener();
        dialog.addColorSelectionListener(this);
        dialog.showDialog("font color");
    }//End of actionPerformed
    
    public void colorSelected(ColorSelectionEvent event) {
        Color selectedColor = event.getColor();
        if(debug>0) System.out.println("** ColorSelectionListener colorSelected event.getColor()="
                + event.getColor());
        this.setCurrentColor(selectedColor);
        if(selectedColor==null) return;

        Object[] args = new Object[1];
        args[0] = selectedColor;
        DrawPanel drawPanel=ObjectTable.getDrawPanel();
        DrawMenu drawMenu=ObjectTable.getDrawMenu();
        
        FontStyle menuStyle=FontStyle.getFontStyleFromMenu();
      //-----------------------------------//
        FontStyle fontStyle=new FontStyle();
        fontStyle.setFontColor(menuStyle.getFontColor());
        drawMenu.setFontStyle(fontStyle);
      //-----------------------------------//
        drawPanel.repaint();

    }

  public static ButtonOfColorChooser createColorChooserButton(String commandName, int width,
                 String tip) {
         ButtonOfColorChooser button = new ButtonOfColorChooser(commandName, tip);
         if(width<=0){
            TextLayout textlayout=new TextLayout(commandName, DrawMenu.MenuFont, 
                  new FontRenderContext(null, false, false));
            int textWidth=(int)textlayout.getAdvance()+5;
            button.setPreferredSize(new Dimension(textWidth, DrawMenu.buttonHeight));
         } else {
            button.setPreferredSize(new Dimension(width, DrawMenu.buttonHeight));
         }
         //MenuUtil menuUtil=ObjectTable.getMenuUtil();
         //menuUtil.setMenuComponent(button);
         //if(debug>0) menuUtil.printMenuComponent(button);
         //System.out.println(" button size="+Util.Dim(button.getPreferredSize()));
         return button;
     }
  
  public static ButtonOfColorChooser createColorChooserButton(String commandName, boolean setText, 
                 String imageName, String tip) {
         ButtonOfColorChooser button=null;
         if(imageName==null||imageName.equals("")){
             button = new ButtonOfColorChooser(commandName, tip);
             TextLayout textlayout=new TextLayout(commandName, DrawMenu.MenuFont, 
                     new FontRenderContext(null, false, false));
             int textWidth=(int)textlayout.getAdvance()+5;
             button.setPreferredSize(new Dimension(textWidth, DrawMenu.buttonHeight));
         } else{
            ImageIcon icon=DrawImageIcon.get(imageName, DrawMenu.imageWidth, 
                    DrawMenu.imageHeight);
            button = new ButtonOfColorChooser(commandName, setText, icon, tip);
            button.setPreferredSize(new Dimension(DrawMenu.buttonWidth, DrawMenu.buttonHeight));
         }
         //MenuUtil menuUtil=ObjectTable.getMenuUtil();
         //menuUtil.setMenuComponent(button);
         //if(debug>0) menuUtil.printMenuComponent(button);
         return button;
     }

} //ButtonOfColorChooser

