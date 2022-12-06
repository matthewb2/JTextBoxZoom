/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.border.Border.*;
import main.*;

public class DialogOfZoom extends JDialog{
    public static DialogOfZoom main;
    Dimension dialogDimension=new Dimension(300,120);
    JComboBox zoomComboBox;
    JSpinner zoomSpinner;
    SpinnerNumberModel zoomSpinnerModel;
    double scaleSave=DrawParameters.Scale;
    
    public DialogOfZoom(){
        super(ObjectTable.getDrawMain(), "zoom");
        this.main=this;
    }
    
    public void showDialog(){
        Component owner=this.getOwner();
        Point centerP=new Point(owner.getX()+150, owner.getY()+90);
        this.setLocation(centerP);
        
        Container contentPane=this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        JTabbedPane tabbedPane=new JTabbedPane();
        contentPane.add(tabbedPane,BorderLayout.NORTH);
        JPanel zoomPanel=this.createZoomPanel();
        JPanel propertyPanel=new JPanel();
        zoomPanel.setPreferredSize(dialogDimension);
        propertyPanel.setPreferredSize(dialogDimension);
        
        tabbedPane.add("zoom",zoomPanel);
        tabbedPane.add("property",propertyPanel);
        this.pack();
        this.setVisible(true);
    }
    
    private JPanel createZoomPanel(){
        JPanel basePanel=new JPanel();
        JPanel zoomPanel=new JPanel();
        GridLayout zoomPanelLayout=new GridLayout(1,2);
        zoomPanelLayout.setHgap(20);
        zoomPanel.setLayout(zoomPanelLayout);
    //ComboBox   
        JPanel comboBoxPanel=new JPanel();
        TitledBorder frameBorder=BorderFactory.createTitledBorder("Zoom to");
        frameBorder.setTitleColor(Color.BLACK);
        frameBorder.setTitleFont(DrawMenu.MenuFont);
        comboBoxPanel.setBorder(frameBorder);
        GridLayout comboBoxPanelLayout=new GridLayout(1,1);
        comboBoxPanel.setLayout(comboBoxPanelLayout);
         String[] scales = new String[] {"", "400%", "300%","200%", "173%", "141%", 
                    "122%", "100%", "86%", "71%", "61%", "50%", "35%", 
                    "fit width", "fit height"};
        this.zoomComboBox=new JComboBox(scales);
        DialogOfZoomAction action=new DialogOfZoomAction();
        ZoomItemListener zoomItemListener=new ZoomItemListener();
        this.zoomComboBox.setActionCommand("zomm to");
        int percentScale=(int)(DrawParameters.Scale*100);
        this.scaleSave=DrawParameters.Scale;
        String scalStr=percentScale+"%";
        this.zoomComboBox.setSelectedItem(scalStr);
        this.zoomComboBox.setFont(DrawMenu.MenuItemFont);
        comboBoxPanel.add(this.zoomComboBox);
        zoomPanel.add(comboBoxPanel);
    //Spinner   
        JPanel spinnerPanel=new JPanel();
        frameBorder=BorderFactory.createTitledBorder("percent");
        frameBorder.setTitleColor(Color.BLACK);
        frameBorder.setTitleFont(DrawMenu.MenuFont);
        spinnerPanel.setBorder(frameBorder);
        GridLayout spinnerPanelLayout=new GridLayout(1,1);
        spinnerPanel.setLayout(spinnerPanelLayout);
        this.zoomSpinnerModel=new SpinnerNumberModel(100d, 10d, 500d, 1d);
        this.zoomSpinner=new JSpinner(this.zoomSpinnerModel);
        ZoomSpinnerChangeListener zoomSpinnerChangeListener=new ZoomSpinnerChangeListener();
        this.zoomSpinner.setValue(percentScale);
        this.zoomSpinner.setFont(DrawMenu.MenuItemFont);
        spinnerPanel.add(this.zoomSpinner);
        zoomPanel.add(spinnerPanel);
        
    //OK, Cancel Button    
        JPanel buttonPanel=new JPanel();
        JButton okButton=new JButton("OK");
        JButton cancelButton=new JButton("Cancel");
        okButton.setActionCommand("OK Zoom");
        cancelButton.setActionCommand("Cancel Zoom");
        okButton.addActionListener(action);
        cancelButton.addActionListener(action);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
    // Layout BasePanel
        Box box=Box.createVerticalBox();
        basePanel.add(box);
        box.add(Box.createVerticalStrut(5));
        box.add(zoomPanel);
        box.add(Box.createVerticalStrut(5));
        box.add(buttonPanel);
        box.add(Box.createVerticalStrut(5));
        
     // add listeners
        this.zoomComboBox.addActionListener(action);
        this.zoomComboBox.setActionCommand("Zoom");
        this.zoomComboBox.addItemListener(zoomItemListener);
        this.zoomSpinner.addChangeListener(zoomSpinnerChangeListener);
        return basePanel;
    }
} //end of class

class DialogOfZoomAction extends AbstractAction{
    static DialogOfZoomAction main;
    int debug=0;
    
    public DialogOfZoomAction(){
        main=this;
    }
    
    public void actionPerformed(ActionEvent e) {
        //
        String commandName=e.getActionCommand();
        String componentClassName=""; 
        componentClassName=e.getSource().getClass().getSimpleName();
        String componentName=((Component)e.getSource()).getName();
        if(debug>0) System.out.println("- DialogOfZoomAction.actionPerformed  " +
                "commandName="+commandName+
                ", source component name="+componentName+
                ", source class simple name="+componentClassName);
        
        if(commandName.equals("Zoom")){
            double scale=1d;
            String scaleStr=(String)DialogOfZoom.main.zoomComboBox.getSelectedItem();
            if(!scaleStr.equals("")){
                if(debug>0) System.out.println("** Utility.zoomTo called scaleString="+scaleStr);
                scale=this.getScale(scaleStr);
            } else {
                double percentScale=DialogOfZoom.main.zoomSpinnerModel.getNumber().doubleValue();
                scale=percentScale/100;
            }
            Object[] args=new Object[1];
            ViewUtil viewUtil=ObjectTable.getViewUtil("");
            viewUtil.zoom(scale); 
        }
        
        if(commandName.equals("OK Zoom")){
            DialogOfZoom.main.setVisible(false);
            return;
        }
        if(commandName.equals("Cancel Zoom")){
            DialogOfZoom.main.setVisible(false);
            return;
        }
    }//End of actionPerformed

    protected double getScale(String scaleString){
        scaleString=scaleString.replace("%", "");
        Dimension drawPanelSize=DrawParameters.getSheetSizeByPixel();
        JScrollPane scrollPane=ObjectTable.getScrollPane();
        JViewport viewport=scrollPane.getViewport();
        double scale=1.0;
        double margin=5d;
        if(scaleString.equals("fit width")){
            scale=(viewport.getWidth()-margin)/drawPanelSize.getWidth();
        } else if(scaleString.equals("fit height")){
            scale=(viewport.getHeight()-margin)/drawPanelSize.getHeight();
        } else {
            if(!scaleString.equals("")) scale=Double.valueOf(scaleString).doubleValue()/100;
        }
        return scale;
    }

} // end of class

class ZoomItemListener implements ItemListener{
    public void itemStateChanged(ItemEvent e) {
        String itemStr=(String)e.getItem();
        double scale=DialogOfZoomAction.main.getScale(itemStr);
        ChangeListener[] listeners=DialogOfZoom.main.zoomSpinner.getChangeListeners();
        DialogOfZoom.main.zoomSpinner.removeChangeListener(listeners[0]);
        DialogOfZoom.main.zoomSpinner.setValue((int)(scale*100));
        DialogOfZoom.main.zoomSpinner.addChangeListener(listeners[0]);
    }
}

class ZoomSpinnerChangeListener implements ChangeListener{
    public void stateChanged(ChangeEvent e){
        double percentScale=DialogOfZoom.main.zoomSpinnerModel.getNumber().doubleValue();
        JComboBox zoomComboBox=DialogOfZoom.main.zoomComboBox;
        int count=zoomComboBox.getItemCount();
        ItemListener[] listeners=zoomComboBox.getItemListeners();
        zoomComboBox.removeItemListener(listeners[0]);
        zoomComboBox.setSelectedIndex(0);
        for(int i=0;i<count;i++){
            String itemStr=(String)zoomComboBox.getItemAt(i);
            if(itemStr.startsWith(String.valueOf((int)percentScale))){
                zoomComboBox.setSelectedIndex(i);
                break;
            }
        }
        zoomComboBox.addItemListener(listeners[0]);
    }//end of stateChanged
}