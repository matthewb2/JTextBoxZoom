package util;



import textBox.ShapeContainer;
import java.util.*;

public class ContainerManager {

    private ArrayList ContainerList;
    Vector workVector=new Vector();
    public static int debug = 0;

    public ContainerManager() {
        this.ContainerList = new ArrayList();
    }

    public int size() {
        return this.ContainerList.size();
    }

    public void clear() {
        this.ContainerList.clear();
    }

    public boolean addContainer(ShapeContainer container) {
        if (container == null) {
            System.err.println("*** Error in ContainerManager.addContainer"
                    + ", container=null");
            return false;
        }
        String shapeId=container.getShapeId();
        ShapeContainer anotherContainer=null;
        for(int i=0;i<this.ContainerList.size();i++){
            ShapeContainer shapeContainer=(ShapeContainer)this.ContainerList.get(i);
            if(shapeContainer.getShapeId().equals(shapeId)){
                anotherContainer=shapeContainer;
                break;
            }
        }
        if(anotherContainer==null){
            this.ContainerList.add(container);
        } else {
            System.err.println("*** Error ContainerManager.addContainer"
                    + ", The container of the same shapeId was found in the List"
                    +"  shapeId="+container.getShapeId());
            return false;
        }

        return true;
    }

    public boolean addContainer(int index, ShapeContainer container) {
        if (index <0 ) {
            System.err.println("*** Error ContainerManager.addContainer with index"
                    + ", index="+index);
            return false;
        }
       
        if (index <= ContainerList.size()) {
            ContainerList.add(index, container);
        } else {
            ContainerList.add(container);
            System.err.println("*** Warning in ContainerManager.addContainer with index"
                   + ", index out of bound, index=" + index
                   + ", ContainerList size=" + ContainerList.size());
        }
 
        return true;
    }
    
    public boolean deleteContainer(ShapeContainer container) {
        if (container == null) {
            System.err.println("*** Error ContainerManager.deleteContainer"
                    + ", container=null");
            return false;
        }
        //
        boolean removed = ContainerList.remove(container);
        if (!removed) {
            System.err.println("*** Error ContainerManager.deleteContainer: Not removed"
                    + " container=" + container.getShapeId());
            System.out.println(this.toString());
        }
        return removed;
    }
    
    public ShapeContainer getContainer(int index) {
        ShapeContainer container = (ShapeContainer) ContainerList.get(index);
        return container;
    }
   
    public ShapeContainer getContainer(String shapeId) {
        if (shapeId.equals("")) {
            System.err.println("*** Error ContainerManager.getContainer:"
                    + " shapeId=null");
            return null;
        }
        ShapeContainer target = null;
        int size = ContainerList.size();
        for (int i = 0; i < size; i++) {
            ShapeContainer container = (ShapeContainer) ContainerList.get(i);
            if(container.getShapeId().equals(shapeId)){
                target = container;
                break;
            }
        }
        if (target == null) {
            if(debug>0) System.err.println("*** Error ContainerManager.getContainer(shapeId)"
                    + ": Container of shapeId not found  shapeId=" + shapeId);
        }
        return target;
    }

    public ShapeContainer[] getContainers() {
        int size = ContainerList.size();
        ShapeContainer[] containers = new ShapeContainer[size];
        for (int i = 0; i < size; i++) {
            containers[i] = (ShapeContainer) ContainerList.get(i);
        }
        return containers;
    }

    public ShapeContainer getEditableTextBox() {

        workVector.clear();
        int size = ContainerList.size();
        for (int i = 0; i < size; i++) {
            ShapeContainer container = (ShapeContainer) ContainerList.get(i);
            if (container.isEditableTextBox()) {
                    this.workVector.add(container);
            }
        }
        if (workVector.size() == 0) {
            return null;
        }
        if (workVector.size() > 1) {
            System.err.println("*** Warning ContainerManager.getActivatedTextBox "
                    + "Multiple ActivatedTextBoxes exist");
            for (int i = 1; i < workVector.size(); i++) {
                ShapeContainer container = (ShapeContainer) workVector.get(i);
                container.makeTextBoxEditable(false);
            }
        }
        ShapeContainer container = (ShapeContainer) workVector.get(0);
        //
        return container;
    }

    public void printContainers(String message) {
        System.out.println("\n ContainerManager.printContainers"
                + ", Title:" + message );
        ShapeContainer[] containers = getContainers();
        for (int i = 0; i < containers.length; i++) {
            System.out.println(" - Container[" + i + "] ; " + containers[i].toString());
        }//for(int j=0)
    }

    public String toString() {
        String str = "";
        str += "ContainerManager ContainerList";
        ShapeContainer[] containers = getContainers();
        if (containers.length == 0) {
            str += "=null";
        }
        for (int i = 0; i < containers.length; i++) {
            str += "\n  container[" + i + "]=" + containers[i].getShapeId();
        }//
        return str;
    }
}
