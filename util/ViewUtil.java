package util;

import java.awt.*;
import javax.swing.*;
import main.*;

public class ViewUtil {

    public static int debug=0;

    public ViewUtil(){}
    public static void zoom(double scale){
        DrawPanel drawPanel=ObjectTable.getDrawPanel();
        JPanel framePanel=(JPanel)drawPanel.getParent();
        JScrollPane scrollPane=ObjectTable.getScrollPane();
        double currentScale=DrawParameters.getScale();
        //Center point of viewport
        JViewport viewport=scrollPane.getViewport();
        int centerX=viewport.getWidth()/2;
        int centerY=viewport.getHeight()/2;
        Point viewportCenterP=new Point(centerX, centerY);
        Point drawPanelCenterP=SwingUtilities.convertPoint(viewport, viewportCenterP, drawPanel);
        DrawParameters.Scale=scale;
        if(debug>0) System.out.println("-- ViewUtil.zoom newScale="+Util.PercentNum(scale,1));
        double ratio=scale/currentScale;
        drawPanelCenterP=new Point((int)(ratio*drawPanelCenterP.getX()), 
                (int)(ratio*drawPanelCenterP.getY()));
        Point frameCenterP=SwingUtilities.convertPoint(drawPanel, drawPanelCenterP, framePanel);
        //Get leftTop
        int viewPositionX=(int)(frameCenterP.getX()-centerX);
        int viewPositionY=(int)(frameCenterP.getY()-centerY);
        Point viewPosition=new Point(viewPositionX ,viewPositionY);
        //Set leftTopPoint as new ViewPosition
        ViewUtil.setView(scale, viewPosition);
    } //zoom

    public static JPanel getFramePanel(JPanel drawPanel){
        JPanel panel=(JPanel)drawPanel.getParent();
        if(panel==null) {
            panel=new JPanel();
            panel.add(drawPanel, 0);
            panel.setOpaque(true);
            panel.setBackground(Color.LIGHT_GRAY);
        }
        return panel;
    }
    
    public static void setView(double scale, Point viewPosition){
        DrawPanel drawPanel=ObjectTable.getDrawPanel();
        if(debug>0) System.out.println("** ViewUtil.zoom called scale="+scale);
        JScrollPane scrollPane=ObjectTable.getScrollPane();
        Dimension size=DrawParameters.getSheetSizeByPixel();
        int drawPanelW=(int)size.getWidth();
        int drawPanelH=(int)size.getHeight();
        Dimension newSize=new Dimension((int)(scale*drawPanelW), (int)(scale*drawPanelH));
        drawPanel.setPreferredSize(newSize);
        JPanel framePanel=ViewUtil.getFramePanel(drawPanel);
        JViewport viewport=scrollPane.getViewport();
        viewport.setView(framePanel);
        double dpX=drawPanel.getX();
        double dpY=drawPanel.getY();
        double dpW=drawPanel.getWidth();
        double dpH=drawPanel.getHeight();
        if(viewPosition!=null){
            double viewPosX=viewPosition.getX();
            double viewPosY=viewPosition.getY();
            if(viewPosX<dpX) viewPosX=dpX;
            if(viewPosX>dpX+dpW) viewPosX=dpX+dpW;
            if(viewPosY<dpY) viewPosY=dpY;
            if(viewPosY>dpY+dpH) viewPosY=dpY+dpH;
            viewport.setViewPosition(new Point((int)viewPosX,(int)viewPosY));
        }    
        Point newViewPosition=viewport.getViewPosition();
        if(debug>0){
            System.out.println("-- ViewUtil.setView scale="+Util.PercentNum(scale,0)+
                    ", drawPanel.preferredSize: "+Util.Dim(newSize)+
                    ", viewPosition="+Util.Pt(viewPosition)+
                    ", newViewPosition="+Util.Pt(newViewPosition)+
                    "\n  viewPort: "+Util.Rect(viewport));
        }
        drawPanel.revalidate();
        drawPanel.repaint();
    } //zoom
    
}
