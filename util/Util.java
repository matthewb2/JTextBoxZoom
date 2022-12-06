package util;



import textBox.AttributedStringUtil;
import java.awt.*;
import java.text.*;
import java.awt.geom.*;
import util.*;

public class Util {

    static NumberFormat nf = NumberFormat.getNumberInstance();
    static int debug = 0;

    public Util() {
    }

//-------//
//  Sort //
//-------//

    public static void simpleSort(int[] data) {
        String message = "initial array: ";
        printSort(message, data);
        for (int i = 0; i < data.length - 1; i++) {
            for (int j = i; j < data.length; j++) {
                if (data[j] < data[i]) {
                    swap(data, i, j);
                }
            }
            message = i + "th array:    ";
            if (debug > 0) {
                printSort(message, data);
            }
        }
    }

    public static void simpleSort(double[] data) {
        String message = "simpleSort initial array: ";
        printSort(message, data);
        for (int i = 0; i < data.length - 1; i++) {
            for (int j = i; j < data.length; j++) {
                if (data[j] < data[i]) {
                    swap(data, i, j);
                }
            }
            message = "simpleSort  " + i + "th array:    ";
            if (debug > 0) {
                printSort(message, data);
            }
        }
    }

    public static int[] indexedSimpleSort(int[] data) {
        int size = data.length;
        int[] indices = new int[size];
        for (int i = 0; i < size; i++) {
            indices[i] = i;
        }
        String message = "simpleSort initial array: ";
        if (debug > 0) {
            printSort(message, data, indices);
        }

        for (int i = 0; i < size - 1; i++) {
            for (int j = i; j < data.length; j++) {
                if (data[indices[j]] < data[indices[i]]) {
                    indexedSwap(indices, i, j);
                }
            }
        }
        message = "simpleSort final array: ";
        if (debug > 0) {
            printSort(message, data, indices);
        }
        return indices;
    }

    public static int[] indexedSimpleSort(double[] data) {
        int size = data.length;
        int[] indices = new int[size];
        for (int i = 0; i < size; i++) {
            indices[i] = i;
        }
        String message = "simpleSort initial array: ";
        if (debug > 0) {
            printSort(message, data, indices);
        }

        for (int i = 0; i < size - 1; i++) {
            for (int j = i; j < data.length; j++) {
                if (data[indices[j]] < data[indices[i]]) {
                    indexedSwap(indices, i, j);
                }
            }
        }
        message = "simpleSort final array: ";
        if (debug > 0) {
            printSort(message, data, indices);
        }
        return indices;
    }

    private static void indexedSwap(int[] indices, int i, int j) {
        int tmp = indices[j];
        indices[j] = indices[i];
        indices[i] = tmp;
    }

    private static void swap(int[] data, int i, int j) {
        int tmp = data[j];
        data[j] = data[i];
        data[i] = tmp;
    }

    private static void swap(double[] data, int i, int j) {
        double tmp = data[j];
        data[j] = data[i];
        data[i] = tmp;
    }

    private static void printSort(String message, int[] data) {
        int i;
        System.out.print(message);
        for (i = 0; i < data.length - 1; i++) {
            System.out.print(data[i] + ",");
        }
        System.out.println(data[i]);
    }

    private static void printSort(String message, double[] data) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumIntegerDigits(4);
        nf.setMinimumFractionDigits(2);
        int i;
        System.out.print(message);
        for (i = 0; i < data.length - 1; i++) {
            System.out.print(nf.format(data[i]) + " ,");
        }
        System.out.println(nf.format(data[i]));
    }

    private static void printSort(String message, int[] data, int[] indices) {
        int i;
        System.out.print(message);
        for (i = 0; i < data.length - 1; i++) {
            System.out.print("[" + indices[i] + "] " + data[indices[i]] + ", ");
        }
        System.out.println("[" + indices[i] + "] " + data[indices[i]]);
    }

    private static void printSort(String message, double[] data, int[] indices) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumIntegerDigits(4);
        nf.setMinimumFractionDigits(10);
        int i;
        System.out.print(message);
        for (i = 0; i < data.length - 1; i++) {
            System.out.print("[" + indices[i] + "] " + nf.format(data[indices[i]]) + ", ");
        }
        System.out.println("[" + indices[i] + "] " + nf.format(data[indices[i]]));
    }

//----------------------------------//
//  String/charactertype conversion //
//----------------------------------//
    public static String getASCIIControlString(String str) {
        char ascCode = 0x40;
        char[] c = str.toCharArray();
        char code = c[0];
        char hitChar;
        String outputStr = "";
        for (char i = 0x00; i < 0x1a; i++) {
            if (code == i) {
                hitChar = (char) (i + ascCode);
                outputStr = "ctrl " + String.valueOf(hitChar);
                break;
            }
        }
        if (code == 0x7f) {
            outputStr = "DELETE";
        }
        return outputStr;
    }

    public static String toHexString(String str) {
        String hexString = "";
        byte[] btArray = str.getBytes();
        hexString = Util.toHexString(btArray);
        return hexString;
    }

    public static String toHexString(byte[] byteBuff) {
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        String result = "";
        for (int i = 0; i < byteBuff.length; i++) {
            result += "0x";
            int num = byteBuff[i] & 0xff;
            int firstNum = num / 16;
            int secondNum = num - 16 * firstNum;
            result += hex[firstNum];
            result += hex[secondNum];
            result += " ";
        }
        return result;
    }
//-----------//
//  toString //
//-----------//
    public static String Text(String string) {
        String str = "NULL";
        if(str!=null&&!str.equals("")){
            str = string.replace("\n", "\\n");
        }
        return str;
    }
    
    public static String Text(AttributedString attribStr) {
        String str = "NULL";
        if(attribStr!=null){
            AttributedStringUtil attribUtil = new AttributedStringUtil(attribStr.getIterator());
            str = attribUtil.getString();
            if(str!=null&&!str.equals("")){
                str = str.replace("\n", "\\n");
            }
        }
        return str;
    }
    
    public static String Text(AttributedCharacterIterator iterator) {
        String str = "NULL";
        if(iterator!=null){
            AttributedStringUtil attribUtil = new AttributedStringUtil(iterator);
            str = attribUtil.getString();
            if(str!=null&&!str.equals("")){
                str = str.replace("\n", "\\n");
            }
        }
        return str;
    }
    
    public static String Num(double d) {
        nf.setMaximumIntegerDigits(4);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String str = "";
        str = nf.format(d);
        return str;

    }

    public static String PercentNum(double d, int fraction) {
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumIntegerDigits(3 + fraction);
        nf.setMaximumFractionDigits(fraction);
        String str = "";
        str = nf.format(d);
        return str;
    }

    public static String Pt(Point point) {
        nf.setMaximumIntegerDigits(4);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String str = "";
        if (point == null) {
            str = "null";
        } else {
            str = nf.format(point.getX()) + ", " + nf.format(point.getY());
        }
        return str;
    }

    public static String Pt(Point2D point) {
        nf.setMaximumIntegerDigits(4);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String str = "";
        if (point == null) {
            str = "null";
        } else {
            str = nf.format(point.getX()) + ", " + nf.format(point.getY());
        }
        return str;
    }
/*
    public static String Pt(Vector2D vec) {
        nf.setMaximumIntegerDigits(4);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String str = "";
        if (vec == null) {
            str = "null";
        } else {
            str = nf.format(vec.getX()) + ", " + nf.format(vec.getY());
        }
        return str;
    }
*/
    public static String Pt(Rectangle2D rect) {
        nf.setMaximumIntegerDigits(4);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String str = "";
        if (rect == null) {
            str = "null";
        } else {
            str = nf.format(rect.getX()) + ", " + nf.format(rect.getY());
        }
        return str;
    }

    public static String Pt(Arc2D arc) {
        nf.setMaximumIntegerDigits(4);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String str = "";
        if (arc == null) {
            str = "null";
        } else {
            str = nf.format(arc.getX()) + ", " + nf.format(arc.getY());
        }
        return str;
    }

    public static String Angle(Arc2D arc) {
        nf.setMaximumIntegerDigits(4);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String str = "";
        if (arc == null) {
            str = "null";
        } else {
            str = nf.format(arc.getAngleStart()) + ", " + nf.format(arc.getAngleExtent());
        }
        return str;
    }

    public static String Rect(Rectangle rect) {
        nf.setMaximumIntegerDigits(4);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String str = "";
        if (rect == null) {
            str = "null";
        } else {
            str = "x,y=" + nf.format(rect.getX()) + ", " + nf.format(rect.getY())
                    + ", w,h=" + nf.format(rect.getWidth()) + ", " + nf.format(rect.getHeight());
        }
        return str;
    }

    public static String Rect(Component component) {
        nf.setMaximumIntegerDigits(4);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String str = "";
        if (component == null) {
            str = "null";
        } else {
            Dimension size = component.getPreferredSize();
            str = "x,y=" + nf.format(component.getX()) + ", " + nf.format(component.getY())
                    + ", w,h=" + nf.format(component.getWidth()) + ", " + nf.format(component.getHeight())
                    + ", preferredSize=" + nf.format(size.getWidth()) + ", " + nf.format(size.getHeight());
        }

        return str;
    }

    public static String Rect(Rectangle2D rect) {
        nf.setMaximumIntegerDigits(4);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String str = "";
        if (rect == null) {
            str = "null";
        } else {
            str = "x,y=" + nf.format(rect.getX()) + ", " + nf.format(rect.getY())
                    + ", w,h=" + nf.format(rect.getWidth()) + ", " + nf.format(rect.getHeight());
        }
        return str;
    }

    public static String Dim(Dimension dim) {
        nf.setMaximumIntegerDigits(4);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String str = "";
        if (dim == null) {
            str = "null";
        } else {
            str = "w,h=" + nf.format(dim.getWidth()) + ", " + nf.format(dim.getHeight());
        }
        return str;
    }

    public static String Insets(Insets insets) {
        nf.setMaximumIntegerDigits(4);
        nf.setMaximumFractionDigits(1);
        nf.setMinimumFractionDigits(1);
        String str = "";
        if (insets == null) {
            str = "null";
        } else {
            str = "left, right=" + nf.format(insets.left) + ", " + nf.format(insets.right)
                    + ", top, bottom=" + nf.format(insets.top) + ", " + nf.format(insets.bottom);
        }
        return str;
    }
}
