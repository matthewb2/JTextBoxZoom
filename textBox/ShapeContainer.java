package textBox;



import util.Util;
import main.DrawParameters;
import java.awt.*;
import java.awt.geom.*;
import java.util.Vector;

public class ShapeContainer {

    private String shapeId = "";
    private boolean selected = false;
    private Shape shape = null;
    private TextBox textBox = null;
    Color fillColor=null;
    int debug=0;

    public ShapeContainer(String shapeId, Shape shape) {
        this.shapeId=shapeId;
        this.shape=shape;
        this.fillColor=new Color(240, 255, 240);
    }

    public String getShapeId() {
        return this.shapeId;
    }
    
    public void setShapeId(String shapeId) {
        this.shapeId=shapeId;
    }
    
    public Shape getShape() {
        return this.shape;
    }
    
    public Rectangle2D getBoundingBox() {
        return this.shape.getBounds2D();
    }
    
    public boolean isSelected() {
        return this.selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
        if (!selected && this.isEditableTextBox()) {
            this.makeTextBoxEditable(false);
        }
    }
    
    public boolean isTextBox() {
        boolean isTextBox=false;
        if(this.textBox!=null) isTextBox=true;
        return isTextBox;
    }
    
    public TextBox getTextBox() {
        return this.textBox;
    }
    
    public void makeTextBoxEditable(boolean activate) {
        if (this.textBox==null) {
            return;
        }
        if (activate) {
            if (!this.selected) {
                this.selected=true;
            }
            this.getTextBox().activateMouseListener(true);
        }else{
            this.getTextBox().activateMouseListener(false);
        }
    }

    public boolean isEditableTextBox() {
        if (this.textBox==null) {
            return false;
        }
        return this.getTextBox().isEditable();
    }



    public int addTextBox() {
        int result = 0;
        if (this.textBox!=null) {
            result = 1;
        }
         Rectangle2D textArea = this.createTextArea();
        if (textArea == null) {
            result = 2;
        }
        if (result == 0) {
            TextBox newTextBox = new TextBox();
            newTextBox.setShapeContainer(this);
            newTextBox.setTextArea(textArea);
            this.textBox = newTextBox;
            newTextBox.setShapeContainer(this);
        } else {
            System.err.println("*** Error ShapeContainer.addTextBox error code=" + result);
            if (result == 6) {
                System.err.println(" --created text area too small"
                        + ", text area: " + Util.Rect(textArea));
            }
        }
        return result;
    }

    private Rectangle2D createTextArea(){
        Shape shape=this.getShape();
        if(debug>0) System.out.println("ShapeContainer.createTextArea simple name="+shape.getClass().getSimpleName()+
                ", name="+shape.getClass().getName());
        String name=shape.getClass().getName();
        Rectangle2D box=this.getBoundingBox();
        Rectangle2D textArea=null;
        if(name.indexOf("Rectangle2D")>=0){
            double delta=2d;
            textArea=this.getShrinkedRectangle(box, delta, delta);
        }
        if(name.indexOf("RoundRectangle2D")>=0){
            RoundRectangle2D rect=(RoundRectangle2D)shape;
            double arcw=rect.getArcWidth();
            double arch=rect.getArcHeight();
            double margin=3d;
            double width=Math.min(arcw, arch)*(1d-1d/Math.sqrt(2))+margin;
            double x=rect.getX();
            double y=rect.getY();
            double w=rect.getWidth();
            double h=rect.getHeight();
            textArea=new Rectangle2D.Double(x+0.5*width, y+0.5*width, w-width, h-width);
        }
        if (name.indexOf("Ellipse2D") >= 0) {
            double x = box.getX();
            double y = box.getY();
            double w = box.getWidth();
            double h = box.getHeight();
            double pai = Math.PI;
            double angle = pai / 4;
            double W = w * Math.cos(angle);
            double H = h * Math.sin(angle);
            textArea = new Rectangle2D.Double(x+0.5*w-0.5*W, y+0.5*h-0.5*H, W, H);
        }
        return textArea;
    }

    public Color[] getColors() {
        Vector<Color> vector = new Vector<Color>();
        Color[] colors = new Color[0];
        TextBox textBox = this.getTextBox();
        if (textBox != null) {
            Color[] fontColors = textBox.getFontColors();
            for (int i = 0; i < fontColors.length; i++) {
                if(fontColors[i]!=null) vector.add(fontColors[i]);
            }
        }
        Vector<Color> vector0 = new Vector<Color>();
        for (int i = 0; i < vector.size(); i++) {
             Color color=vector.get(i);
             int rgb=color.getRGB();
             int jsave=-1;
             for(int j=0;j<vector0.size();j++){
                 if(vector0.get(j).getRGB()==rgb){
                     jsave=j;
                     break;
                 }
             }
             if(jsave==-1) vector0.add(color);
        }
        colors = new Color[vector0.size()];
        for (int i = 0; i < vector0.size(); i++) {
            colors[i]=vector0.get(i);
        }
        return colors;
    }

    public void drawShape(Graphics g) {
        if (debug >0) System.out.println(" - drawShape start "+ this.getShapeId());
        Graphics2D g2 = (Graphics2D) g;
        Shape shape=this.getShape();
        Rectangle currentClip=g2.getClipBounds();
        Color currentColor=g2.getColor();
        Stroke currentStroke=g2.getStroke();
        double lineWidth=0.5;
        double enLarge=0.5d*lineWidth+10d;
        Rectangle2D enlargedBoundingBox
                =this.getEnlargedRectangle(this.getBoundingBox(), enLarge, enLarge);
        if(enlargedBoundingBox!=null) g2.setClip(enlargedBoundingBox);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        //Color fillColor=Color.YELLOW;
        g2.setColor(this.fillColor);
        boolean closed=true;
      //------------- fill shape ------------------//  
        if(closed&&fillColor!=null) g2.fill(shape);
      //--------------------------------------------//  
        Color lineColor=Color.BLACK;
        g2.setColor(lineColor);
      //---- draw border ---//
        g2.draw(shape);
      //--------------------//
      //------------ draw TextBox ----------//
        if (this.getTextBox() != null) {
            this.getTextBox().drawTextBox(g);
        }
      //------------------------------------//  
      //------------ draw TextBox ----------//
        if (this.isSelected()) {
            this.drawSelectionBox(g);
        }
      //------------------------------------//  
        g2.setStroke(currentStroke);
        g2.setColor(currentColor);
        g2.setClip(currentClip); 

        if(debug>0) System.out.println(" - drawShape end");
    }
    
    private void drawSelectionBox(Graphics g){
         Graphics2D g2=(Graphics2D)g;
         double scale=DrawParameters.Scale;
         Font smallFont=new Font(Font.DIALOG, Font.PLAIN, (int)(12/scale));
         double offset=DrawParameters.SelectionBoxOffset/scale;
         double markNSize=DrawParameters.Mark_NormalSize/scale;
         double markSSize=DrawParameters.Mark_SmallSize/scale;

         BasicStroke defaultStroke10=new BasicStroke((float)1.0, BasicStroke.CAP_SQUARE, 
                    BasicStroke.JOIN_MITER, 10.0f);
         BasicStroke defaultStroke15=new BasicStroke((float)1.5, BasicStroke.CAP_SQUARE, 
                    BasicStroke.JOIN_MITER, 10.0f);
         
         //double halfMarkSize=markSize/2;
         Rectangle2D boundingBox=this.getBoundingBox();
         if(boundingBox==null||boundingBox.getWidth()==0&&boundingBox.getHeight()==0) return;
         Rectangle2D selectionBox=this.getEnlargedRectangle(boundingBox, offset, offset);
         double X=selectionBox.getX();
         double Y=selectionBox.getY();
         double W=selectionBox.getWidth();
         double H=selectionBox.getHeight();
         double markSize = markNSize;
         double halfMarkSize=markSize/2;
         Color markFillColor=Color.white;
         Color markLineColor=Color.black;
         //int type=shapeElement.getTypeE();
      // draw selection box
         markSize=markNSize;
         halfMarkSize=markSize/2;
         Color currentColor=g2.getColor();
         g2.setColor(Color.LIGHT_GRAY);

         Composite currentComposit=g2.getComposite();
         float alfa=((AlphaComposite)currentComposit).getAlpha();
         AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f*alfa );
         g2.setComposite( ac );
         Stroke currentStroke=g2.getStroke();
       // selection frame
         Rectangle2D selectionFrame=new Rectangle2D.Double(X-halfMarkSize, Y-halfMarkSize, W, markSize);
         g2.fill(selectionFrame);
         selectionFrame=new Rectangle2D.Double(X-halfMarkSize, Y+H-halfMarkSize, W, markSize);
         g2.fill(selectionFrame);
         selectionFrame=new Rectangle2D.Double(X-halfMarkSize, Y-halfMarkSize, markSize, H+markSize);
         g2.fill(selectionFrame);
         selectionFrame=new Rectangle2D.Double(X+W-halfMarkSize, Y-halfMarkSize, markSize, H+markSize);
         g2.fill(selectionFrame);
            
         g2.setComposite(currentComposit);
         g2.setColor(currentColor);
         g2.setStroke(currentStroke);

       // draw resize NW, SW, SE, NE  marks at corner
         markLineColor=Color.BLACK;
         markFillColor=Color.WHITE;
         AffineTransform currentTransform=g2.getTransform();
         //if(!unresizable){
            g2.setStroke(defaultStroke10);
            Ellipse2D circle=new Ellipse2D.Double(X-halfMarkSize, Y-halfMarkSize,
                    markSize, markSize);
            g2.setColor(markFillColor);
            g2.fill(circle);
            g2.translate(W, 0d); g2.fill(circle);
            g2.translate(0d, H); g2.fill(circle);
            g2.translate(-W, 0d); g2.fill(circle);
            g2.translate(0d, -H);
            g2.setColor(markLineColor);
            g2.draw(circle);
            g2.translate(W, 0d); g2.draw(circle);
            g2.translate(0d, H); g2.draw(circle);
            g2.translate(-W, 0d); g2.draw(circle);
            g2.translate(0d, -H);
         //}
       // draw resize N, W, S, E  marks at center of selection frame
            Rectangle2D rect=new Rectangle.Double(X+0.5d*W-halfMarkSize,
                    Y-halfMarkSize, markSize, markSize);
            g2.setColor(markFillColor);
            g2.fill(rect);
            g2.translate(0.5d*W, 0.5d*H); g2.fill(rect);
            g2.translate(-0.5d*W, 0.5d*H); g2.fill(rect);
            g2.translate(-0.5d*W, -0.5d*H); g2.fill(rect);
            g2.translate(0.5d*W, -0.5d*H);
            g2.setColor(markLineColor);
            g2.draw(rect);
            g2.translate(0.5d*W, 0.5d*H); g2.draw(rect);
            g2.translate(-0.5d*W, 0.5d*H); g2.draw(rect);
            g2.translate(-0.5d*W, -0.5d*H); g2.draw(rect);
            g2.translate(0.5d*W, -0.5d*H);
            g2.setTransform(currentTransform);
            g2.setColor(currentColor);
         //}
    }
    
    public Rectangle2D getEnlargedRectangle(Rectangle2D box, double wEx, double hEx) {
        if (box == null) {
            return null;
        }
        double X = box.getX();
        double Y = box.getY();
        double Width = box.getWidth();
        double Height = box.getHeight();
        return new Rectangle2D.Double(X - wEx, Y - hEx, Width + 2.5d * wEx,
                Height + 2.5d * hEx);
    }
    
    public Rectangle2D getShrinkedRectangle(Rectangle2D rectangle,
            double wideSh, double heightSh) {
        if (rectangle == null) {
            return null;
        }
        double X = rectangle.getX();
        double Y = rectangle.getY();
        double Width = rectangle.getWidth();
        double Height = rectangle.getHeight();
        Rectangle2D newShape = null;
        if (Width > 2d * wideSh && Height > 2d * heightSh) {
            newShape = new Rectangle2D.Double(X + wideSh, Y + heightSh, Width - 2d * wideSh,
                    Height - 2d * heightSh);
        }
        return newShape;
    }
    
    public void translate(double x, double y){
        RectangularShape rect=(RectangularShape)this.shape;
        rect.setFrame(x+rect.getX(), y+rect.getY(), rect.getWidth(),rect.getHeight());
    }
    
    public Object clone() {
        Shape shape=this.getShape();
        if(debug>0) System.out.println("ShapeContainer clone simple name="+shape.getClass().getSimpleName()+
                ", name="+shape.getClass().getName());
        String name=shape.getClass().getName();
        Shape newShape=null;
        if(name.indexOf("Rectangle2D")>=0){
            Rectangle2D rect=(Rectangle2D)shape;
            newShape=new Rectangle2D.Double(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        }
        if(name.indexOf("RoundRectangle2D")>=0){
            RoundRectangle2D rect=(RoundRectangle2D)shape;
            newShape=new RoundRectangle2D.Double(rect.getX(), rect.getY(), 
                    rect.getWidth(), rect.getHeight(), rect.getArcWidth(), rect.getArcHeight());
        }
        if(name.indexOf("Ellipse2D")>=0){
            Ellipse2D rect=(Ellipse2D)shape;
            newShape=new Ellipse2D.Double(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        }
        ShapeContainer newContainer=new ShapeContainer("clone", newShape);
        return newContainer;
    }

    public String toString() {
        String str = "";

        return str;
    }

} 
