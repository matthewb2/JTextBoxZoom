
package util;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.colorchooser.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.util.*;
import main.*;
import textBox.*;
import util.*;


public class CustomColorChooserDialog extends JDialog 
            implements ActionListener, ComponentListener, WindowListener{
    String commandName="";
    JLabel commandLabel;
    JColorChooser colorChooser;
    PreviewPanel previewPanel;
    ImportedColorsPanel importedColorsPanel;
    //
    Point dialogLocation=new Point(100, 100);
    JButton okButton;
    JButton cancelButton;
    JButton importButton;
    JButton deleteButton;
    protected EventListenerList listenerList = new EventListenerList();  
    int debug = 0;

    public CustomColorChooserDialog(JFrame frame) {
        super(frame, "Custom Color Chooser", false);
        this.initializeDialog();
        this.addComponentListener(this);
    }

    private void initializeDialog() {
        Container contentPane = this.getContentPane();
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        this.commandLabel = new JLabel("command:");
        this.commandLabel.setOpaque(true);
        this.commandLabel.setForeground(Color.WHITE);
        this.commandLabel.setBackground(Color.BLACK);
        this.commandLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
        this.commandLabel.setPreferredSize(new Dimension(520,20));
        topPanel.add(this.commandLabel, BorderLayout.WEST);
       //JColorChooser
        this.colorChooser = new JColorChooser();
        this.colorChooser.setPreviewPanel(new JLabel("Preview", JLabel.CENTER));
        this.colorChooser.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), 0);
        this.colorChooser.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), 0);
        topPanel.add(this.colorChooser, BorderLayout.SOUTH);
        
        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BoxLayout(basePanel, BoxLayout.X_AXIS));
        TitledBorder baseBorder = BorderFactory.createTitledBorder("");
        basePanel.setBorder(baseBorder);

        //ImportedColorsPanel
        this.importedColorsPanel = new ImportedColorsPanel();
        TitledBorder currentColorBorder = BorderFactory.createTitledBorder("Imported colors");

        JPanel importedColorsPanel = new JPanel();
        importedColorsPanel.setBorder(currentColorBorder);
        importedColorsPanel.add(this.importedColorsPanel);
        
        JPanel buttonBasePanel = new JPanel();
        this.importButton = new JButton("import colors");
        this.deleteButton = new JButton("delete colors");
        this.importButton.setPreferredSize(new Dimension(105, 20));
        this.deleteButton.setPreferredSize(new Dimension(110, 20));
        String tip="Select shapes on the canvas and click this button, then the colors will be imported to the left pallete.";
        this.importButton.setToolTipText(tip);
        tip="Select colors on the left pallete and press this button, then the selected colors will be deleted.";
        this.deleteButton.setToolTipText(tip);
        this.importButton.setActionCommand("import");
        this.deleteButton.setActionCommand("delete");
        this.importButton.addActionListener(this);
        this.deleteButton.addActionListener(this);
        Box box=Box.createVerticalBox();
        buttonBasePanel.add(box);
        box.add(this.importButton);
        box.add(Box.createVerticalStrut(15));
        box.add(this.deleteButton);
        importedColorsPanel.add(buttonBasePanel);
        
        //Preview Panel 
        this.previewPanel = new PreviewPanel(this);
        TitledBorder previewBorder = BorderFactory.createTitledBorder("Preview");
        JPanel previewBasePanel = new JPanel();
        previewBasePanel.setBorder(previewBorder);
        previewBasePanel.add(this.previewPanel);
        
        basePanel.add(previewBasePanel);
        basePanel.add(importedColorsPanel);
        contentPane.add(basePanel, BorderLayout.CENTER);
        contentPane.add(topPanel, BorderLayout.NORTH);
        //OK, Cancel Button    
        JPanel buttonPanel=new JPanel();
        this.okButton=new JButton("OK !");
        this.cancelButton=new JButton("Cancel");
        this.okButton.setPreferredSize(new Dimension(60,24));
        this.cancelButton.setPreferredSize(new Dimension(80,24));
        //
        this.okButton.setActionCommand("OK");
        this.cancelButton.setActionCommand("Cancel");
        this.okButton.addActionListener(this);
        this.cancelButton.addActionListener(this);
        buttonPanel.add(this.okButton);
        buttonPanel.add(this.cancelButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        this.pack();
        this.setVisible(false);
    }

    public void showDialog(String commandName) {
        Component owner = this.getOwner();
        Point pos = new Point(owner.getX() + (int) this.dialogLocation.getX(),
                owner.getY() + (int) this.dialogLocation.getY());
        this.setLocation(pos);
        this.pack();
        this.setVisible(true);
        this.addWindowListener(this);
        this.commandName=commandName;

        Color[] colors={Color.WHITE, Color.BLACK, Color.RED, Color.GREEN, Color.BLUE};
        this.getImportedColorsPanel().setImportedColors(colors);
    }

    protected String getCommandName() {
        return this.commandName;
    }

    protected JColorChooser getColorChooser() {
        return this.colorChooser;
    }
    
    protected PreviewPanel getPreviewPanel() {
        return this.previewPanel;
    }
    
    protected ImportedColorsPanel getImportedColorsPanel() {
        return this.importedColorsPanel;
    }

    public void setPreviewColor(Color color, String command){
        command=command.toLowerCase();
        if(command.equals(this.commandName)){
            this.getPreviewPanel().setSelectedColor(color);
            this.repaint();
            if( debug>0) System.out.println("CustomColorChooserDialog.setPreviewColor command="
                +command+", color="+color+" >> The color was set to the PreviewPanel");
        }
    }

    public Color getPreviewColor(String command){
        Color color=this.getPreviewPanel().getSelectedColor();
        if(debug>0) System.out.println("CustomColorChooserDialog.getPreviewColor command="
                +command+", color="+color);
        return color;
    }
    
    public void actionPerformed(ActionEvent e) {
        String commandName = e.getActionCommand();
        if(debug>0) System.out.println("actionPerformed commandName="+commandName);
        if(commandName.equals("OK")){
            Color color=this.previewPanel.getSelectedColor();
            ColorSelectionEvent event = new ColorSelectionEvent(this, color);
            this.fireEvent(event);
        }
        if(commandName.equals("Cancel")){
            this.closeDialog();
        }
        if(commandName.equals("import")){
            this.importColors();
        } 
        if(commandName.equals("delete")){
            this.importedColorsPanel.removeCurrentColors();
        }
        //
    }

    public void importColors(){
        Vector<Color> vector = new Vector<Color>();
        ContainerManager manager=ObjectTable.getContainerManager();
        ShapeContainer container=manager.getEditableTextBox();
        Color[] colors=new Color[0];
        if(container!=null) colors=container.getColors();
        for(int k=0;k<colors.length;k++) vector.add(colors[k]);
        this.importedColorsPanel.setImportedColors(colors);
    }

    protected void addColorSelectionListener(ColorSelectionListener listener) {
        this.listenerList.add(ColorSelectionListener.class, listener);
        if(debug>0) System.out.println("** Num of SelectionListener="+this.listenerList.getListenerCount());
    }

    protected void removeColorSelectionListener(ColorSelectionListener listener) {
        this.listenerList.remove(ColorSelectionListener.class, listener);
    }
    
    protected void removeColorSelectionListener() {
        this.listenerList=new EventListenerList();  
    }
    
    protected void fireEvent(ColorSelectionEvent event) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == ColorSelectionListener.class) {
                ((ColorSelectionListener) listeners[i + 1]).colorSelected(event);
            }
        }
    }
    
    protected void closeDialog(){
        this.setVisible(false);
        this.removeColorSelectionListener();
        //
    }
    public void windowActivated(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {
        this.closeDialog();
    }
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    public void componentHidden(ComponentEvent e){} 
    public void componentMoved(ComponentEvent e){
        Point pt=this.getLocation();
        this.dialogLocation=pt;
        //
    } 
    public void componentResized(ComponentEvent e){} 
    public void componentShown(ComponentEvent e){} 
}

class PreviewPanel extends JComponent implements ChangeListener, 
        ColorSelectionListener, MouseListener{
    Color selectedColor=Color.WHITE;
    //Color selectedColor;
    CustomColorChooserDialog dialog;
    protected Dimension panelSize=new Dimension(200, 60);
    protected Dimension margin=new Dimension(10, 5);
    protected String[][] messages={
        {"Command : Fill color", "NULL colored or multi-colored"},
        {"Command : Line color", "NULL colored or multi-colored"},
        {"Command : Font color", "NULL colored, multi-colored or no text"}};
    int debug=0;
    
    
    public PreviewPanel(CustomColorChooserDialog dialog) {
        dialog.getColorChooser().getSelectionModel().addChangeListener(this);
        dialog.getImportedColorsPanel().addColorSelectionListener(this);
        int width = (int)(panelSize.width+2*margin.getWidth());
        int height= (int)(panelSize.height+2*margin.getHeight());
        this.setPreferredSize(new Dimension(width, height));
        this.setOpaque(true);
        this.dialog=dialog;
        this.setToolTipText("");
        this.addMouseListener(this);
    }
    
    public void stateChanged(ChangeEvent changeEvent) {
        this.selectedColor= this.dialog.getColorChooser().getColor();
        if(debug>0) System.out.println("** JColorChooser.SelectionModel->PreviewPanel "
                + "stateChanged  currentColor="+this.selectedColor
                + ", selectedColor="+this.selectedColor);
        this.repaint();
    }

    public void colorSelected(ColorSelectionEvent event){
        this.selectedColor=event.getColor();
        if(debug>0) System.out.println("** ImportedColorsPanel->PreviewPanel "
                + "colorSelected current="+this.selectedColor
                + ", selectedColor="+this.selectedColor);
        this.repaint();
    }
    
    protected Color getSelectedColor(){
        return this.selectedColor; 
    }
    
    protected void setSelectedColor(Color color){
        this.selectedColor=color;
        this.repaint();
        if(debug>0) System.out.println("PreviewPanel.setSelectedColor  "
                + "currentColor="+this.selectedColor+ ", selectedColor="+this.selectedColor);
    }
/*
    protected void updateCurrentColor(){
        this.currentColor=this.selectedColor;
        this.repaint();
    }
*/
    public String getToolTipText(MouseEvent e) {
        Color color=this.selectedColor;
        String str="";
        if(color==null) str="Null color";
        else {
            str="RGB: "+this.selectedColor.getRed() + ", " + this.selectedColor.getGreen() 
                    + ", " + this.selectedColor.getBlue();
        }
        return str;
    }
    
    public void mouseClicked(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    
    public void paint(Graphics g) {
        Graphics2D g2=(Graphics2D)g;
        g2.setColor(Color.white);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.translate(margin.getWidth(), margin.getHeight());
        int width=panelSize.width;
        
        Rectangle2D rect=new Rectangle2D.Float(0,0,30,30);
        Color paintColor=this.selectedColor;
        if(this.selectedColor==null) paintColor=Color.white;
        Color shadow=Color.GRAY;
        double shadowX=1;
        double shadowY=1;
        
        g2.translate(shadowX, shadowY);
        g2.setColor(shadow);
        g2.fill(rect);
        g2.translate(-shadowX, -shadowY);
        
        g2.setColor(paintColor);
        g2.fill(rect);
        
        g2.translate(width/3, 0);
        BasicStroke stroke=new BasicStroke(2);
        g2.setStroke(stroke);
        
        g2.translate(shadowX, shadowY);
        g2.setColor(shadow);
        g2.draw(rect);
        g2.translate(-shadowX, -shadowY);
        
        g2.setColor(paintColor);
        g2.draw(rect);
        
        g2.translate(width/3, 0);
        Font font=new Font("Java", Font.PLAIN, 14);
        g2.setFont(font);
        
        g2.translate(shadowX, shadowY);
        g2.setColor(shadow);
        g2.drawString("Java", 0, 14);
        g2.translate(-shadowX, -shadowY);
        
        g2.setColor(paintColor);
        g2.drawString("Java", 0, 14);
        
        font=new Font("Java", Font.BOLD+Font.ITALIC, 14);
        g2.setFont(font);
        
        g2.translate(shadowX, shadowY);
        g2.setColor(shadow);
        g2.drawString("Java", 0, 30);
        g2.translate(-shadowX, -shadowY);
        
        g2.setColor(paintColor);
        g2.drawString("Java", 0, 30);
        
        String[] names={"fill color","line color","font color"};
        String commandName=this.dialog.getCommandName();
        int id=0;
        for(int i=0;i<names.length;i++) {
            if(commandName.equals(names[i])){
                id=i;
                 break;
            }
        }
        String str1=messages[id][0];
        String str2=messages[id][1];
        if(this.selectedColor!=null){
            str2="(RGB : "+this.selectedColor.getRed() + ", " + this.selectedColor.getGreen() 
                    + ", " + this.selectedColor.getBlue()+")";
        }
  
        g2.translate(-width*2.0/3.0-margin.getWidth()+3, 50);
        g2.setColor(Color.DARK_GRAY);
        font=new Font("", Font.BOLD, 12);
        g2.setFont(font);
        g2.drawString(str1, 0, 0);
        g2.setFont(font);
        g2.drawString(str2, 0, 12);
    }

}

class ImportedColorsPanel extends JComponent implements MouseListener {
    protected EventListenerList listenerList = new EventListenerList();  
    protected Color[] colors=new Color[0];
    protected boolean[] selected;
    Dimension numSwatches=new Dimension(10,5);
    protected Dimension swatchSize=new Dimension(10,10);
    protected Dimension gap=new Dimension(1, 1);
    protected Dimension margin=new Dimension(3, 3);
    //JCheckBox importCheckBox;
    //JButton deleteButton;
    int ctrl=0;
    int debug=0;
    
    public ImportedColorsPanel(){
        this.setToolTipText("");
        int width = (int) (numSwatches.width * (swatchSize.width + gap.width)+2*margin.width);
        int height = (int) (numSwatches.height * (swatchSize.height + gap.height)+2*margin.height);
        Dimension panaleSize=new Dimension(width, height);
        this.setPreferredSize(panaleSize);
        this.addMouseListener(this);
    }

    protected void setImportedColors(Color[] colors){
        Color[] newColors=new Color[this.colors.length+colors.length];
        for(int i=0;i<this.colors.length;i++) newColors[i]=this.colors[i];
        for(int i=0;i<colors.length;i++) newColors[this.colors.length+i]=colors[i];
        int[] rgb = new int[newColors.length];
        for (int i = 0; i < newColors.length; i++) {
            rgb[i] = newColors[i].getRGB();
            //rgb[i] = newColors[i].getRed()+newColors[i].getGreen()+newColors[i].getBlue();
        }
        int[] indice = Util.indexedSimpleSort(rgb);
        Color[] orderedColors = new Color[newColors.length];
        for (int i =0; i<newColors.length; i++) {
            orderedColors[i] = newColors[indice[i]];
        }
        if (debug>0) {
            System.out.print("setImportedColors orderedColors=");
            for (int i = 0; i < newColors.length; i++) System.out.print(orderedColors[i] + ", ");
            System.out.println(" ");
        }
        
        int index=0;
        int count=0;
        for (int i =1; i<orderedColors.length; i++) {
            int rgbRef=orderedColors[index].getRGB();
            if(orderedColors[i].getRGB()==rgbRef){
                orderedColors[i]=null;
                count++;
            } else {
                index=i;
            }
        }
        Color[] newOrderedColors=new Color[orderedColors.length-count];
        int id=0;
        for (int i =orderedColors.length-1; i>=0; i--) {
            if(orderedColors[i]!=null) {
                newOrderedColors[id]=orderedColors[i];
                id++;
            }
        }
        this.colors = newOrderedColors;
        this.selected=new boolean[this.colors.length];
        for(int i=0;i<this.colors.length;i++) this.selected[i]=false;
        this.repaint();
    }

    protected void removeCurrentColors(){
        int count=0;
        for(int i=0;i<colors.length;i++) {
            if(this.selected[i]) count++;
        }
        Color[] currentColors=new Color[this.colors.length-count];
        int id=0;
        for(int i=0;i<colors.length;i++) {
            if(!this.selected[i]){
                currentColors[id]=this.colors[i];
                id++;
            }
        }
        this.colors=currentColors;
        for(int i=0;i<colors.length;i++) this.selected[i]=false;
        this.repaint();
    }
    
    public void mousePressed(MouseEvent e){
        //System.out.println("ImportedColorsPanel mousePressed");
        double X = e.getX();
        double Y = e.getY();
        int key = e.getModifiersEx();
        this.ctrl = 0;
        if ((key & InputEvent.SHIFT_DOWN_MASK) != 0) this.ctrl = 2;
        if ((key & InputEvent.CTRL_DOWN_MASK) != 0) this.ctrl = 3;
        //System.out.println("ImportedColorsPanel mousePressed ctrl="+this.ctrl);
        int id=getColorIndex(X, Y);
        if(id>=0&&id<this.colors.length){
            Color color=this.colors[id];
            ColorSelectionEvent event=new ColorSelectionEvent(this, color);
            this.fireEvent(event);
            this.setSelected(id);
            this.repaint();
        }

    } 

    public void mouseClicked(MouseEvent e){} 
    public void mouseReleased(MouseEvent e){} 
    public void mouseEntered(MouseEvent e){} 
    public void mouseExited(MouseEvent e){} 
    
    protected void setSelected(int id){
        if (this.selected[id]) {
            this.selected[id] = false;
        } else {
            this.selected[id] = true;
        }
        if(this.ctrl==0){
            for(int i=0;i<this.colors.length;i++) {
                if(i!=id) this.selected[i]=false;
            }
        }
    }
    
    public String getToolTipText(MouseEvent e) {
        int id = this.getColorIndex(e.getX(), e.getY());
        Color color=null;
        if(id>=0&&id<colors.length){
            color=this.colors[id];
            return "RGB: "+color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
        }else{
            color=null;
        }
        return "";
    }
    
    protected int getColorIndex(double X, double Y){
        double x=X-margin.getWidth();
        double y=Y-margin.getHeight();
        int ix=(int)(x/(swatchSize.width + gap.width));
        int iy=(int)(y/(swatchSize.height + gap.height));
        if(ix>=numSwatches.width||iy>=numSwatches.height) return -1;
        int id=ix+iy*numSwatches.width;
        if(debug>0) System.out.println("ImportedColorsPanel getColorIndex ix="
                +ix+", iy="+iy+", id="+id);
        return id;
    }
    
    
    protected void addColorSelectionListener(ColorSelectionListener listener){
        listenerList.add(ColorSelectionListener.class, listener);
    }

    protected void removeColorSelectionListener(ColorSelectionListener listener){
        listenerList.remove(ColorSelectionListener.class, listener);
    }

    protected void fireEvent(ColorSelectionEvent event){
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == ColorSelectionListener.class) {
                ((ColorSelectionListener) listeners[i + 1]).colorSelected(event);
            }
        }
    }

    public void paint(Graphics g) {
        Graphics2D g2=(Graphics2D)g;
        //g2.setColor(getBackground());
        g2.setColor(Color.white);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.translate(margin.getWidth(), margin.getHeight());
        for (int row = 0; row < numSwatches.height; row++) {
            int y = row * (swatchSize.height + gap.height);
            for (int column = 0; column < numSwatches.width; column++) {
                Color color=getColorForCell(column, row);
                if(color==null) color=Color.white;
                g2.setColor(color);
                int x;
                x = column * (swatchSize.width + gap.width);
                g2.fillRect(x, y, swatchSize.width, swatchSize.height);
                g2.setColor(Color.black);
                g2.drawLine(x + swatchSize.width - 1, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
                g2.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y + swatchSize.height - 1);
            }
        }
        Color colorSave = g2.getColor();
        Stroke strokeSave = g2.getStroke();
        for (int row = 0; row < numSwatches.height; row++) {
            int y = row * (swatchSize.height + gap.height);
            for (int column = 0; column < numSwatches.width; column++) {
                int id=(row * numSwatches.width) + column;
                if(id>=this.colors.length||!this.selected[id]) continue;

                g2.setColor(Color.DARK_GRAY);
                BasicStroke stroke = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
                g2.setStroke(stroke);
                int x;
                x = column * (swatchSize.width + gap.width);
                Rectangle2D rect=new Rectangle2D.Float((float)(x), (float)(y), swatchSize.width, swatchSize.height);
                //Rectangle2D rect=new Rectangle2D.Float(x, y, swatchSize.width, swatchSize.height);
                g2.draw(rect);
            }
        }
        g2.setColor(colorSave);
        g2.setStroke(strokeSave);
        int width = (int)(numSwatches.width * (swatchSize.width + gap.width));
	int height = (int)(numSwatches.height * (swatchSize.height + gap.height));
        g2.setColor(Color.black);
        g2.drawRect(0, 0, width, height);
        g2.translate(-margin.getWidth(), -margin.getHeight());
    }
    
    private Color getColorForCell( int column, int row) {
        int index=(row * numSwatches.width) + column;
        if(index<colors.length){
            return colors[index];
        }else{
            return null;
        }
    }
}

interface ColorSelectionListener extends EventListener {
    public void colorSelected(ColorSelectionEvent event);
}

class ColorSelectionEvent extends EventObject {
    Color color;
    public ColorSelectionEvent(Object source, Color color) {
        super(source);
        this.color=color;
    }
    public Color getColor(){
        return this.color;
    }
}
