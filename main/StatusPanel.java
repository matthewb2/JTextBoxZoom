package main;



import main.ObjectTable;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.border.Border.*;

public class StatusPanel extends JPanel{
    JLabel label1=null;
    JLabel label2=null;
    Dimension panelSize=new Dimension(600, 40);
    
    public StatusPanel(){
        Box box=Box.createVerticalBox();
        this.add(box);
        this.label1=this.createLabel();
        this.label2=this.createLabel();
        box.add(label1);
        box.add(Box.createVerticalStrut(0));
        box.add(label2);
        this.setPreferredSize(ObjectTable.getDrawMain().statusPanelSize);
    }
    
    public JLabel createLabel(){
        JLabel label=new JLabel();
        FlowLayout flowLayout=new FlowLayout(FlowLayout.LEADING, 0, 0);
        this.setLayout(flowLayout);
        this.add(label);
        //Font font=MenuConstants.DefaultFont;
        Font font=new Font(Font.DIALOG, Font.PLAIN, 12);
        label.setFont(font);
        label.setForeground(Color.MAGENTA);
        Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        this.setBorder(raisedetched);
        this.setBackground(Color.WHITE);
        this.setOpaque(true);
        this.setVisible(true);
        return label;
    }
    
    public void showText(int i, String text){
        if(i==0) label1.setText(text);
        if(i==1) label2.setText(text);
    }
}