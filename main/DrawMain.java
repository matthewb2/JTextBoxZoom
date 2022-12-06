package main;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.*;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import util.*;
import textBox.*;

public class DrawMain extends JFrame{
    //protected Dimension drawPanelSize=new Dimension(800,700);
    protected Dimension buttonPanelSize=new Dimension(800,30);
    protected Dimension statusPanelSize=new Dimension(800,40);
    static int numOfShapes=2;
    
    public static void main(String[] args) {
        DrawMain drawMain=new DrawMain();
        drawMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ObjectTable.main=drawMain;
        drawMain.setFrame();
        drawMain.pack();
        drawMain.setVisible(true);
        createShape();
    }
    
    void setFrame() {
        this.setTitle("Draw");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//TabbedPane, StatusPanel
        Container contentPane=this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        JPanel buttonPanel=this.createButtonPanel();
        contentPane.add(buttonPanel, BorderLayout.NORTH);

//DrawPanel, listenerPanel
        ListenerPanel listenerPanel=new ListenerPanel();
        ObjectTable.listenerPanel=listenerPanel;
        DrawPanel drawPanel=(DrawPanel)listenerPanel;
        ObjectTable.drawPanel=drawPanel;
        //contentPane.add(drawPanel, BorderLayout.CENTER);
        
//StatusPanel
        StatusPanel statusPanel=new StatusPanel();
        contentPane.add(statusPanel, BorderLayout.SOUTH);
        ObjectTable.statusPanel=statusPanel;
//ScrollPane
        JScrollPane scrollPane=new JScrollPane();
        ObjectTable.scrollPane=scrollPane;
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        JViewport viewport=new JViewport();
        ObjectTable.viewport=viewport;
        viewport.setOpaque(false);

        JPanel framePanel=ViewUtil.getFramePanel(drawPanel);
        framePanel.setAutoscrolls(true); 
        viewport.setView(framePanel);
        scrollPane.setViewport(viewport);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setPreferredSize(new Dimension(800,500));

//ViewUtil
        ObjectTable.viewUtil=new ViewUtil();
//ContainerManager
        ObjectTable.drawMenu=new DrawMenu(); 
//ContainerManager
        ObjectTable.containerManager=new ContainerManager();
//Edit
        ObjectTable.edit=new Edit();
//Undo Manager
        UndoDrawManager undoDrawManager=new UndoDrawManager();
        ObjectTable.undoDrawManager=undoDrawManager;
//Other object  
        ObjectTable.customColorChooserDialog=new CustomColorChooserDialog(this);
    }

    public JPanel createButtonPanel(){
        JPanel panel=new JPanel();
        FlowLayout flowLayout=new FlowLayout(FlowLayout.LEADING, 0, 0);
        Border paneEdge = BorderFactory.createLineBorder(Color.black);
        Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        panel.setLayout(flowLayout);
        panel.setBorder(paneEdge);
        panel.setBorder(raisedetched);
        panel.setPreferredSize(this.buttonPanelSize);
        //CreateMenu menu=new DrawMenu();
        JPanel edit=DrawMenu.createEditGroup();
        panel.add(edit);
        JPanel separator=new JPanel();
        separator.setPreferredSize(new Dimension(3,20));
        panel.add(separator);
        JPanel fontStyle=DrawMenu.createFontStyleGroup();
        panel.add(fontStyle);

        return panel;
    }

    public static void createShape() {
        ContainerManager manager=ObjectTable.getContainerManager();
        Shape shape;
        int index = 0;
        int num=numOfShapes;
        ShapeContainer container;
        Rectangle2D[] rect=new Rectangle2D[num];
        rect[0]=new Rectangle2D.Double(150, 20, 700, 300);
        rect[1]=new Rectangle2D.Double(150, 350, 700, 300);
        
        Dimension[] sizes=new Dimension[num];
        sizes[0]=new Dimension(700, 300);
        sizes[1]=new Dimension(700, 300);

        Dimension[] origin=new Dimension[num];
        origin[0]=new Dimension(100, 20);
        origin[1]=new Dimension(100, 350);
        //double x=100, y=20;
     //-- temp set--//
          num=2;
     //-------------//   
        AttributedCharacterIterator[] iterators=createText();
        for(int i=0;i<num;i++){
            double x=rect[i].getX();
            double y=rect[i].getY();
            double w=rect[i].getWidth();
            double h=rect[i].getHeight();
            double arcw=0.2*Math.min(w, h);
            shape = new RoundRectangle2D.Double(x, y, w, h, arcw, arcw);
            container = new ShapeContainer("roundRectangle-" + index, shape);
            container.addTextBox();
            container.getTextBox().insertText(TextBox.COMMAND, 0, iterators[i]);
            manager.addContainer(container);
            if(i==0) container.makeTextBoxEditable(true);
            else container.makeTextBoxEditable(false);
            index++;
        }
        DrawMenu.undo.setEnabled(false);
    }
    
    public static AttributedCharacterIterator[] createText(){
        int num=numOfShapes;
        String[] texts=new String[num];
        texts[0]="MILITARY Jokes : "+"Alter Your Course\n"+
        "This is an actual radio conversation released by the Chief of Naval\n"+
        "Operations, 10-10-95, MSG#H0000115020ecb52EMHS\n"+
        "#1: Please divert your course 15 degrees to the north to avoid a collision.\n"+
        "#2: Recommend that you change YOUR course 15 degrees to the south to avoid a collision.\n"+
        "#1: This is the captain of a U.S. navy ship. I say again, divert YOUR course.\n"+
        "#2: No, I say again divert YOUR course.\n"+
        "#1: This is the aircraft carrier Enterprise, we are a large warship of the U.S. navy. Divert your course NOW!\n"+
        "#2: This is a lighthouse. Your call?";
        
        texts[1]="ミトコンドリア・イブ\n"+
        "1981年、イギリスのフレデリック・サンガーらは、ヒトのミトコンドリアDNA（mt-DNA）の" +
        "配列パターンを完全に決定した。彼らはミトコンドリアDNAから分子時計を求めたが、" +
        "やはりヒトとチンパンジーの分岐を400万年前程度と認めた。\n"+
        "1987年、アメリカのアラン・ウィルソンは更にヨーロッパ、アフリカ、アジア、"+
        "オーストラリア、アメリカの147人のミトコンドリアDNAを使って調査を行った結果を公表した。\n"+
        "論理上、共通の女系祖先がることは明らかであり、問題は「いつ頃存在したか」であった。"+
        "その結果、人類の共通の女系祖先は14万年前から29万年前のアフリカにいたことがわかった。";

        
        FontStyle[] fontStyles=new FontStyle[4];
        fontStyles[0]=new FontStyle(Font.DIALOG, Font.BOLD, 0, 14, TextAttribute.UNDERLINE_ON, 
                FontStyle.UNDEF_int, Color.RED);
        fontStyles[1]=new FontStyle(Font.DIALOG, Font.PLAIN,  0, 12, FontStyle.UNDEF_int, 
                FontStyle.UNDEF_int, Color.BLUE);
        fontStyles[2]=new FontStyle(Font.DIALOG, Font.BOLD, 0, 14, TextAttribute.UNDERLINE_ON, 
                FontStyle.UNDEF_int, Color.BLUE);
        fontStyles[3]=new FontStyle(Font.DIALOG, Font.PLAIN,  0, 12, FontStyle.UNDEF_int, 
                FontStyle.UNDEF_int, Color.BLACK);
        
        FontStyle bold_red=new FontStyle(Font.DIALOG, Font.BOLD, 0, 14, TextAttribute.UNDERLINE_ON, 
                FontStyle.UNDEF_int, Color.RED);
        FontStyle plain_red=new FontStyle(Font.DIALOG, Font.PLAIN, 0, 14, FontStyle.UNDEF_int, 
                FontStyle.UNDEF_int, Color.RED);
        FontStyle bold_green=new FontStyle(Font.DIALOG, Font.BOLD, 0, 14, FontStyle.UNDEF_int, 
                FontStyle.UNDEF_int, new Color(0, 102, 0));
        FontStyle plain_green=new FontStyle(Font.DIALOG, Font.PLAIN, 0, 14, FontStyle.UNDEF_int, 
                FontStyle.UNDEF_int, new Color(0, 102, 0));
        FontStyle bold_blue=new FontStyle(Font.DIALOG, Font.BOLD, 0, 14, FontStyle.UNDEF_int, 
                FontStyle.UNDEF_int, Color.BLUE);
        FontStyle plain_blue=new FontStyle(Font.DIALOG, Font.PLAIN, 0, 14, FontStyle.UNDEF_int, 
                FontStyle.UNDEF_int, Color.BLUE);
        
        AttributedCharacterIterator[] iterators=new AttributedCharacterIterator[num];
        
        int length=texts[0].length();
        AttributedString attribStr=new AttributedString(texts[0]);
        AttributedCharacterIterator iterator=attribStr.getIterator();
        iterator=bold_red.setTo(iterator, 0, 34);
        iterator=plain_blue.setTo(iterator, 35, length);
        iterator=bold_blue.setTo(iterator, 256, 260);
        iterator=bold_blue.setTo(iterator, 379, 383);
        iterator=bold_blue.setTo(iterator, 419, 423);
        iterators[0]=iterator;
        
        length=texts[1].length();
        attribStr=new AttributedString(texts[1]);
        iterator=attribStr.getIterator();
        iterator=bold_red.setTo(iterator, 0, 10);
        iterator=plain_blue.setTo(iterator, 11, length);
        iterator=bold_blue.setTo(iterator, 22, 33);
        iterator=bold_blue.setTo(iterator, 39, 57);
        iterator=bold_green.setTo(iterator, 114, 122);
        iterator=bold_blue.setTo(iterator, 139, 148);
        iterator=bold_green.setTo(iterator, 270, 282);
        iterators[1]=iterator;
        
        return iterators;
    }
    
}//End of DrawMain
