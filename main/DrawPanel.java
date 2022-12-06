package main;


import textBox.ShapeContainer;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;
import util.*;

public class DrawPanel extends JPanel{
    int debug=0;
    
    public DrawPanel(){
        this.setLayout(null);
        Dimension size=DrawParameters.getSheetSizeByPixel();
        double scale=1.0;
        DrawParameters.Scale=scale;
        size=new Dimension((int)(size.getWidth()*scale), (int)(size.getHeight()*scale));
        this.setPreferredSize(size);
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
    }

    public void paint(Graphics g) {
        Graphics2D g2=(Graphics2D)g;
        Color currentColor=g2.getColor();
      // draw frame of the canvas 
        Dimension size=this.getPreferredSize();
        Rectangle2D bounds=new Rectangle2D.Double(0.1d, 0.1d, size.getWidth()-0.2d, size.getHeight()-0.2d);
        g2.setColor(Color.WHITE);
        g2.fill(bounds);
        g2.setColor(Color.BLACK);
        g2.draw(bounds);
      // draw shapes  
        ContainerManager manager=ObjectTable.getContainerManager();
        ShapeContainer[] containers=manager.getContainers();
        double scale=DrawParameters.getScale();
        g2.scale(scale, scale);

        for(int i=0;i<containers.length;i++){
            containers[i].drawShape(g);
        }
        g2.setColor(currentColor);
        g2.scale(1d/scale, 1d/scale);
    }
    
}//end of DrawPanel
