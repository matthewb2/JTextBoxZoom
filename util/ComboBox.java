package util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import main.DrawPanel;
import main.ObjectTable;
import textBox.FontStyle;
import util.DrawMenu;


public class ComboBox extends JComboBox implements ActionListener{
    int debug=0;
    public ComboBox(String commandName, String[] items, String tip) {
        super(items);
        this.setActionCommand(commandName);
        this.addActionListener(this);
        this.setName(commandName);
        this.setToolTipText(tip);
    }

    public void activateListener(boolean activate){
        if(activate&&!this.isActionListener(this)){
            this.addActionListener(this);
        }
        if(!activate&&this.isActionListener(this)){
            this.removeActionListener(this);
        }
    }//activateListener

    private boolean isActionListener(ActionListener listener){
        ActionListener[] listeners=this.getListeners(ActionListener.class);
        boolean isRegistered=false;
        for(int i=0;i<listeners.length;i++){
            if(listener.equals(listeners[i])) isRegistered=true;
        }
        return isRegistered;
    }
    
    public void actionPerformed(ActionEvent e) {
        String commandName=e.getActionCommand();
        String componentClassName=""; 
        componentClassName=e.getSource().getClass().getSimpleName();
        String componentName=((Component)e.getSource()).getName();
        if(debug>0) System.out.println("- ComboBoxAction.actionPerformed  " +
                "commandName="+commandName+
                ", source component name="+componentName+
                ", source class simple name="+componentClassName);
        
        //
        //
        String selectedItem=(String)this.getSelectedItem();
        Object[] args=new Object[1];
        args[0]=selectedItem;
        if(debug>0) System.out.println("- ComboBoxAction.actionPerformed  " +
                "selectedItem="+selectedItem);
        DrawPanel drawPanel=ObjectTable.getDrawPanel();
        DrawMenu drawMenu=ObjectTable.getDrawMenu();
        if(commandName.equals("font family")){
                FontStyle menuStyle=FontStyle.getFontStyleFromMenu();
                String family=menuStyle.getFontFamily();
                if(family.equals("")||family.equals(" ")) return;
              //-----------------------------//
                FontStyle fontStyle=new FontStyle();
                fontStyle.setFontFamily(family);
                drawMenu.setFontStyle(fontStyle);
              //-----------------------------//
                drawPanel.repaint();
        }
        if(commandName.equals("font size")){
                FontStyle menuStyle=FontStyle.getFontStyleFromMenu();
              //-----------------------------//
                FontStyle fontStyle=new FontStyle();
                fontStyle.setFontSize(menuStyle.getFontSize());
                drawMenu.setFontStyle(fontStyle);
              //-----------------------------//
                drawPanel.repaint();
        }
    }//End of actionPerformed

    public static ComboBox createFontFamilyComboBox(String name) {
        int debug=0;
         GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
         String[] fontNames=env.getAvailableFontFamilyNames();
         //
         fontNames[0]=" ";
         if(debug>0){
            String str="createFontFamilyComboBox  ";
            for(int i=0;i<fontNames.length;i++) str+=fontNames[i]+", ";
            System.out.println(str);
        }
         ComboBox comboBox = new ComboBox(name, fontNames, "font family");
         comboBox.activateListener(false);
         comboBox.setSelectedItem("Dialog");
         comboBox.setFont(DrawMenu.MenuItemFont);
         comboBox.setPreferredSize(new Dimension(DrawMenu.buttonWidth*6, 
                 DrawMenu.buttonHeight));
         //
         return comboBox;
     }
     
     public static ComboBox createFontSizeComboBox(String name) {
         String[] fontSizes = new String[] {"", "8", "9", "10", "11", "12", "14",
            "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"};
         ComboBox comboBox = new ComboBox(name, fontSizes, "font size");
         comboBox.activateListener(false);
         comboBox.setSelectedIndex(5);
         comboBox.activateListener(true);
         comboBox.setFont(DrawMenu.MenuItemFont);
         comboBox.setPreferredSize(new Dimension(DrawMenu.buttonWidth*2,
                 DrawMenu.buttonHeight));
         //
         return comboBox;
     }
}

