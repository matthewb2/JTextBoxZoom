package util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import main.ObjectTable;
import static util.ButtonOfPulldownMenu.debug;


public class ButtonOfPulldownMenu extends JMenuBar {
    JMenu menu=null;
    PulldownMenuAction menuAction=new PulldownMenuAction(this);
    public static int debug=0;
    
    //public ButtonOfPulldownMenu() {};
    
    public ButtonOfPulldownMenu(String commandName, String tip) {
        this.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        this.setName(commandName);
        this.setOpaque(false);
        this.menu=new JMenuEx();
        this.add(this.menu);
        this.menu.setName(commandName);
        this.menu.setActionCommand(commandName);
        this.menu.addActionListener(this.menuAction);
        this.menu.setText(commandName);
        this.menu.setToolTipText(tip);
        this.setStandardButtonStyle();
  }
    
    public ButtonOfPulldownMenu(String commandName, ImageIcon imageIcon, String tip) {
        this.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
        this.setName(commandName);
        this.setOpaque(false);
        this.menu=new JMenu();
        this.menu.setName(commandName);
        this.menu.setActionCommand(commandName);
        this.menu.addActionListener(this.menuAction);
        this.add(this.menu);
        if(imageIcon!=null) this.menu.setIcon(imageIcon);
        this.menu.setToolTipText(tip);
        this.setStandardButtonStyle();
  }
 
    public void setStandardButtonStyle() { 
        Border raisedBorder = new BevelBorder(BevelBorder.RAISED);
        this.menu.setBorder(raisedBorder);
        this.menu.setFont(DrawMenu.MenuFont);
        this.menu.setIconTextGap(0);
        this.menu.setOpaque(true);
        Color backGround=new Color(0xDDE8F3);
        this.menu.setBackground(backGround);
        this.menu.setForeground(Color.black);
    }

    public void setSelected(boolean selected) {
        this.menu.setSelected(selected);
    }
    
    public boolean isSelected() { 
        return this.menu.isSelected(); 
    }
    
    public JMenu getMenu(){
        return this.menu;
    }
    
    public void setMenuItems(String[] menuItemNames, ImageIcon[] imageIcons, 
            String[] accelerators){
        int acceleratorWidth=24;
        int textWidthMax=0, textHeightMax=0;
        for(int i=0;i<menuItemNames.length;i++) {
             Dimension textSize=this.getTextLayoutSize(menuItemNames[i], 
                     DrawMenu.MenuItemFont);
             int textWidth=(int)textSize.getWidth();
             int textHeight=(int)textSize.getHeight();
             if(accelerators!=null&&!accelerators[i].equals("")) textWidth+=acceleratorWidth;
             if(textWidth>textWidthMax) textWidthMax=textWidth;
             if(textHeight>textHeightMax) textHeightMax=textHeight;
        }
        int marginWidth=32, marginHeight=6;
        for(int i=0;i<menuItemNames.length;i++) {
           JMenuItem menuItem=new JMenuItem();
           if(accelerators!=null&&!accelerators[i].equals("")){
                KeyStroke stroke = KeyStroke.getKeyStroke(accelerators[i]);
                menuItem.setAccelerator(stroke);
           }
           int imageWidth=0, imageHeight=0;
           int menuItemWidth=0;
           if(imageIcons[i]!=null){
                menuItem.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
                JLabel textLabel=new JLabel(" "+menuItemNames[i]+" ");
                textLabel.setPreferredSize(new Dimension(textWidthMax+marginWidth, 
                        textHeightMax+marginHeight));
                menuItem.add(textLabel);
                menuItemWidth+=textWidthMax+marginWidth;
                
                Image image=imageIcons[i].getImage();
                imageWidth=image.getWidth(null);
                imageHeight=image.getHeight(null);
                JLabel imageLabel=new JLabel(imageIcons[i]);
                imageLabel.setPreferredSize(new Dimension(imageWidth, imageHeight));
                menuItem.add(imageLabel);
                menuItemWidth+=imageWidth+marginWidth;
           } else{
               menuItem.setText(menuItemNames[i]);
               menuItemWidth+=textWidthMax+marginWidth;
           }

           int menuItemHeight=textHeightMax+marginHeight;
           menuItem.setPreferredSize(new Dimension(menuItemWidth, menuItemHeight));
           menuItem.setActionCommand(menuItemNames[i]);
           menuItem.addActionListener(this.menuAction);
           menuItem.setName(menuItemNames[i]);
           this.menu.add(menuItem);
        }
    }

    private Dimension getTextLayoutSize(String text, Font font){
        TextLayout textlayout=new TextLayout(text, font, 
                new FontRenderContext(null, false, false));
        int textWidth=(int)textlayout.getAdvance();
        int textHeight=(int)(textlayout.getAscent()+textlayout.getDescent());
        return new Dimension(textWidth, textHeight);
    }

    public void setSelectedMenuItem(String menuItemName){
        if(debug>0) System.out.println("** ButtonOfPullDownMenu.selectedMenuItem " +
                "menuItemName="+menuItemName);
        Border loweredBorder = new BevelBorder(BevelBorder.LOWERED);
        Component[] components=this.menu.getMenuComponents();
        for(int i=0;i<components.length;i++){
            JMenuItem menuItem=(JMenuItem)components[i];
            menuItem.setBackground(null);
            menuItem.setBorder(null);
            String name=menuItem.getName();
            //
            if(menuItemName.equals(name)){
                menuItem.setBackground(Color.WHITE);
                menuItem.setBorder(loweredBorder);
            }
        }
    }

    public JMenuItem getMenuItem(JMenu menu, String menuItemName){
        JMenuItem menuItem=null;
        Component[] components=menu.getMenuComponents();
        for(int i=0;i<components.length;i++){
            String type=components[i].getClass().getSimpleName();
            if(type.equals("JMenu")) {
                JMenu subMenu=(JMenu)components[i];
                JMenuItem item=this.getMenuItem(subMenu, menuItemName);
                if(item!=null){
                    menuItem=item;
                    break;
                }
            }
            if(type.equals("JMenuItem")){
                JMenuItem item=(JMenuItem)components[i];
                String name=item.getName();
                if(menuItemName.equals(name)){
                    menuItem=item;
                    break;
                }
            }
            if(type.equals("JCheckBoxMenuItem")){
                JCheckBoxMenuItem item=(JCheckBoxMenuItem)components[i];
                String name=item.getName();
                if(menuItemName.equals(name)) {
                    menuItem=item;
                    break;
                }
            }
        }// for
        if(debug>0) System.out.println("  --> ButtonOfPullDownMenu.getMenuItem "
                +menuItemName+", return menuItem="+menuItem.getActionCommand());
        return menuItem;
    }

    public static ButtonOfPulldownMenu createEditButton(int width) {
        String tip="edit";
        String commandName="edit";
        ButtonOfPulldownMenu button=new ButtonOfPulldownMenu(commandName, tip);
        button.setName(commandName);
        button.setPreferredSize(new Dimension(width, DrawMenu.buttonHeight));
        
        String[] menuItemNames={"cut", "copy", "paste"};
        String[] imageName={"", "", "", "", ""};
        String[] accelerators={"ctrl X", "ctrl C", "ctrl V", "ctrl A"};
        ImageIcon[] itemIcons=new ImageIcon[menuItemNames.length];
        for(int i=0;i<menuItemNames.length;i++) {
           itemIcons[i]=null;
           if(!imageName[i].equals("")){
               itemIcons[i]=DrawImageIcon.get(imageName[i], DrawMenu.menuItemImageWidth, 
                       DrawMenu.menuItemImageHeight);
           }
        }
        button.setMenuItems(menuItemNames, itemIcons, accelerators);
        button.menu.setPreferredSize(new Dimension(width, DrawMenu.buttonHeight));
        return button;
    }

}

class PulldownMenuAction extends AbstractAction{
    ButtonOfPulldownMenu buttonOfPulldownMenu;
    int debug=0;
    
    PulldownMenuAction(ButtonOfPulldownMenu buttonOfPulldownMenu){
        this.buttonOfPulldownMenu=buttonOfPulldownMenu;
    }
    
    public void actionPerformed(ActionEvent e) {
        //
        String commandName=e.getActionCommand();
        String componentClassName=""; 
        componentClassName=e.getSource().getClass().getSimpleName();
        String componentName=((Component)e.getSource()).getName();
        if(debug>0) System.out.println("- PulldownMenuAction.actionPerformed  " +
                "commandName="+commandName+
                ", source component name="+componentName+
                ", source class simple name="+componentClassName);
        Edit edit=ObjectTable.getEdit();
        if(commandName.equals("cut")){
            edit.cut();
        }
        if(commandName.equals("copy")){
            edit.copy();
        }
        if(commandName.equals("paste")){
            edit.paste();
        }

    }//End of actionPerformed
}


class JMenuEx extends JMenu{
    Color baseColor=new Color(0xCDD8F3);
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size=this.getPreferredSize();
        //
        double w=size.getWidth();
        double h=size.getHeight();
        if(w==0||h==0) return;
        Point2D p1=new Point2D.Double(0d,0.2*h);
        Point2D p2=new Point2D.Double(0d,0.8*h);
        Point2D p3=new Point2D.Double(0d,h);
        GradientPaint gradient0 =new GradientPaint(p1, Color.white, p2, baseColor,true);
        GradientPaint gradient1 =new GradientPaint(p2, baseColor, p3, baseColor,true);
        Rectangle2D rect0=new Rectangle2D.Double(0,0,w,h);
        Rectangle2D rect1=new Rectangle2D.Double(0,p2.getY(),w,h);
        
        Graphics2D g2d = (Graphics2D)g;
        g2d.setPaint(gradient0);
        g2d.fill(rect0);
        g2d.setPaint(gradient1);
        g2d.fill(rect1);
        g2d.setColor(Color.black);
        FontMetrics metrics=g.getFontMetrics();
        double stringH=metrics.getDescent()+metrics.getAscent();
        double stringW=metrics.stringWidth(this.getText());
        double stringX=0d;
        if(w>stringW) stringX=0.5*(w-stringW);
        double stringY=h-metrics.getDescent();
        if(h>stringH) stringY=0.5*(h+stringH)-metrics.getDescent();
        g2d.drawString(this.getText(), (float)stringX,(float)stringY);
    }
}
