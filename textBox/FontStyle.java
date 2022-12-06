package textBox;

import util.DrawMenu;
import java.awt.*;
import java.awt.font.*;
import java.text.*;
import java.util.*;
import util.*;

public class FontStyle {
/*
 * FontStyle.var  FontStyle.value         TextAttribute.key  TextAttribute.value    
 * fontFamily     String                  FAMILY             String
 * bold           plain:0, bold:1         WEIGHT             WEIGHT_REGULAR(1.0)/WEIGHT_BOLD(2.0)
 * italic         regular:0, italic:1     POSTURE            POSTURE_REGULAR(0.0)/POSTURE_OBLIQUE(0.2)
 * fontSize       integer                 SIZE               NUMBER
 * underLine      on:0, off:-1            UNDERLINE          UNDERLINE_ON(0)/-1
 * superScript    super:1, sub:-1, non:0  SUPERSCRIPT        SUPERSCRIPT_SUPER(1)/SUPERSCRIPT_SUB(-1)/0
 * fontColor      Color                   FOREGROUND         Color
 *
 * Ref.  Font.const  PLAIN:0, BOLD:1, ITALIC:2  BOLD+ITALIC:3
 */
    private String fontFamily;
    private int bold;
    private int italic;
    private int fontSize;
    private int underLine;
    private int superScript;
    private Color fontColor;
    public static final String UNDEF_string = "UNDEF";
    public static final int UNDEF_int = -99;
    
    public static final String DefaultStyle = "DefaultStyle";
    public static final String DefaultFontFamily = "Dialog";
    public static final int DefaultBold = 0;
    public static final int DefaultItalic = 0;
    public static final int DefaultFontSize = 12;
    public static final int DefaultUnderLine = -1;
    public static final int DefaultSuperScript = 0;
    public static final Color DefaultFontColor = Color.BLACK;
    static int debug = 0;

    public FontStyle() {
        this.fontFamily = UNDEF_string;
        this.bold = UNDEF_int;
        this.italic = UNDEF_int;
        this.fontSize = UNDEF_int;
        this.underLine = UNDEF_int;
        this.superScript = UNDEF_int;
        this.fontColor = null;
    }

    public FontStyle(String str) {
        this.fontFamily = UNDEF_string;
        this.bold = UNDEF_int;
        this.italic = UNDEF_int;
        this.fontSize = UNDEF_int;
        this.underLine = UNDEF_int;
        this.superScript = UNDEF_int;
        this.fontColor = null;
        if (!str.equals(FontStyle.DefaultStyle)) {
            System.err.println("*** Error FontStyle.constructor Parameter Error=" + str);
            return;
        }
        this.fontFamily = FontStyle.DefaultFontFamily;
        this.bold = FontStyle.DefaultBold;
        this.italic = FontStyle.DefaultItalic;
        this.fontSize = FontStyle.DefaultFontSize;
        this.underLine = FontStyle.DefaultUnderLine;
        this.superScript = FontStyle.DefaultSuperScript;
        this.fontColor = FontStyle.DefaultFontColor;
    }

    public FontStyle(String fontFamily, int bold, int italic, int fontSize, int underLine,
            int superScript, Color fontColor) {
        this.fontFamily = fontFamily;
        this.bold = bold;
        this.italic = italic;
        this.fontSize = fontSize;
        this.underLine = underLine;
        this.superScript = superScript;
        this.fontColor = fontColor;
        if(debug>0) this.printTextAttribute("FontStyle Constructor");
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public String getFontFamily() {
        return this.fontFamily;
    }

    public void setBold(int bold) {
        this.bold = bold;
    }

    public int getBold() {
        return this.bold;
    }

    public void setItalic(int italic) {
        this.italic = italic;
    }

    public int getItalic() {
        return this.italic;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontSize() {
        return this.fontSize;
    }

    public void setUnderLine(int underLine) {
        this.underLine = underLine;
    }

    public int getUnderLine() {
        return this.underLine;
    }

    public void setSuperScript(int superScript) {
        this.superScript = superScript;
    }

    public int getSuperScript() {
        return this.superScript;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public Color getFontColor() {
        return this.fontColor;
    }

    public AttributedCharacterIterator setTo(AttributedCharacterIterator iterator,
            int selStart, int selEnd) {
        if (debug >0) {
            System.out.println("- FontStyle.setTo " + this.toString());
        }
        if (selStart > selEnd) {
            int temp = selStart;
            selStart = selEnd;
            selStart = temp;
            System.err.println("*** Warning: AttributedStringUtil.setFontStyle "
                    + "selStart>selEnd" + ", selStart,selEnd=" + selStart + "," + selEnd);
        }
        if (selStart < iterator.getBeginIndex()) {
            System.err.println("*** Warning: AttributedStringUtil.setFontStyle "
                    + "delStart=" + selStart + " out of range");
            selStart = iterator.getBeginIndex();
        }
        if (selEnd > iterator.getEndIndex()) {
            System.err.println("*** Warning: AttributedStringUtil.setFontStyle "
                    + "selEnd=" + selEnd + " out of range");
            selEnd = iterator.getEndIndex();
        }

        AttributedCharacterIterator newText = null;
        AttributedStringUtil util = new AttributedStringUtil(iterator);
        if (debug>0) {
            System.out.println(" - FontStyle.setTo  this.fontStyle " + this.toString());
            System.out.println(" - FontStyle.setTo  selStart,selEnd=" + selStart + "," + selEnd
                    + "\n  Text Before Operation=" + util.toString());
        }
        if (!this.getFontFamily().equals(FontStyle.UNDEF_string)) {
            util.addAttribute(TextAttribute.FAMILY, this.getFontFamily(),
                    selStart, selEnd);
            newText = util.getAttributedString().getIterator();
        }

        if (this.getBold() != FontStyle.UNDEF_int) {
            int bold = this.getBold();
            if (bold == 0) {
                util.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_REGULAR,
                        selStart, selEnd);
            } else {
                util.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD,
                        selStart, selEnd);
            }
            newText = util.getAttributedString().getIterator();
        }

        if (this.getItalic() != FontStyle.UNDEF_int) {
            int italic = this.getItalic();
            if (italic == 0) {
                util.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_REGULAR,
                        selStart, selEnd);
            } else {
                util.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE,
                        selStart, selEnd);
            }
            newText = util.getAttributedString().getIterator();
        }

        if (this.getFontSize() != FontStyle.UNDEF_int) {
            util.addAttribute(TextAttribute.SIZE, this.getFontSize(),
                    selStart, selEnd);
            newText = util.getAttributedString().getIterator();
        }

        if (this.getUnderLine() != FontStyle.UNDEF_int) {
            util.addAttribute(TextAttribute.UNDERLINE, this.getUnderLine(),
                    selStart, selEnd);
            newText = util.getAttributedString().getIterator();
        }

        if (this.getSuperScript() != FontStyle.UNDEF_int) {
            int superScript = this.getSuperScript();
            int setScript = 0;
            if (superScript < 0) {
                setScript = TextAttribute.SUPERSCRIPT_SUB;
            } else if (superScript > 0) {
                setScript = TextAttribute.SUPERSCRIPT_SUPER;
            }
            util.removeAttribute(TextAttribute.FONT, selStart, selEnd);
            util.addAttribute(TextAttribute.SUPERSCRIPT, setScript, selStart, selEnd);
            newText = util.getAttributedString().getIterator();
        }

        if (this.getFontColor() != null) {
            util.addAttribute(TextAttribute.FOREGROUND, this.getFontColor(),
                    selStart, selEnd);
            newText = util.getAttributedString().getIterator();
        }
        newText = util.getAttributedString().getIterator();
        if (newText != null) {
            util.setAttributedString(newText);
        }
        if (debug>0) {
            System.out.println(" - FontStyle.setTo  selStart,selEnd=" + selStart + "," + selEnd
                    + "\n  Text After Operation=" + util.toString());
        }
        return newText;
    }

    public String toString() {
        String str = "";
        String fontFamilyStr = UNDEF_string;
        if (!this.fontFamily.equals(UNDEF_string)) {
            fontFamilyStr = this.fontFamily;
            str += "fontFamily=" + fontFamilyStr+",";
        }

        String sizeStr = UNDEF_string;
        if (this.fontSize != UNDEF_int) {
            sizeStr = String.valueOf(this.fontSize);
            str += "size=" + sizeStr+",";
        }
        
        String boldStr = UNDEF_string;
        if (this.bold == 0) {
            boldStr = "PLAIN";
            str += "bold=" + boldStr+",";
        }
        if (this.bold == 1) {
            boldStr = "BOLD";
            str += "bold=" + boldStr+",";
        }

        String italicStr = UNDEF_string;
        if (this.italic == 0) {
            italicStr = "REGULAR";
            str += "posture=" + italicStr+",";
        }
        if (this.italic == 1) {
            italicStr = "ITALIC";
            str += "posture=" + italicStr+",";
        }


        String underLineStr = "UNDERLINE_OFF";
        ;
        if (this.underLine == TextAttribute.UNDERLINE_ON) {
            underLineStr = "UNDERLINE_ON";
            str += "underline=" + underLineStr+",";
        }

        String superScriptStr = UNDEF_string;
        if (this.superScript == 0) {
            superScriptStr = "0";
            str += "superScript=" + superScriptStr+",";
        }
        if (this.superScript > 0) {
            superScriptStr = "1";
            str += "superScript=" + superScriptStr+",";
        }
        if (this.superScript < 0) {
            superScriptStr = "-1";
            str += "superScript=" + superScriptStr+",";
        }
        if (this.superScript == UNDEF_int) {
            superScriptStr = UNDEF_string;
        }
        Color color = this.fontColor;
        String colorStr = UNDEF_string;
        if (color != null) {
            colorStr = this.fontColor.toString();
            str += "color=" + colorStr+",";
        }
        str="";
        str += "fontFamily=" + fontFamilyStr + ", size=" + sizeStr + ", bold=" + boldStr
                + ", posture=" + italicStr + ", underLine=" + underLineStr + ", superScript=" + superScriptStr
                + ", color=" + colorStr;
        return str;
    }

    public void printTextAttribute(String title) {
        String str = "";
        str += "WEIGHT_REGULAR=" + TextAttribute.WEIGHT_REGULAR+
             ", WEIGHT_BOLD=" + TextAttribute.WEIGHT_BOLD + 
             ", POSTURE_REGULAR=" + TextAttribute.POSTURE_REGULAR+
             ", POSTURE_OBLIQUE=" + TextAttribute.POSTURE_OBLIQUE + 
             ", UNDERLINE_ON=" + TextAttribute.UNDERLINE_ON + 
             ", SUPERSCRIPT_SUPER=" + TextAttribute.SUPERSCRIPT_SUPER+
             ", SUPERSCRIPT_SUB=" + TextAttribute.SUPERSCRIPT_SUB;
        System.out.println(title+"  "+str);
    }
    
    public static FontStyle getDefaultFontStyle() {
        FontStyle defaultFontStyle = new FontStyle();
        defaultFontStyle.setFontFamily(FontStyle.DefaultFontFamily);
        defaultFontStyle.setBold(FontStyle.DefaultBold);
        defaultFontStyle.setItalic(FontStyle.DefaultItalic);
        defaultFontStyle.setFontSize(FontStyle.DefaultFontSize);
        defaultFontStyle.setUnderLine(FontStyle.DefaultUnderLine);
        defaultFontStyle.setSuperScript(FontStyle.DefaultSuperScript);
        defaultFontStyle.setFontColor(FontStyle.DefaultFontColor);

        return defaultFontStyle;
    }
    

    public static FontStyle getFontStyleFromMenu() {

        FontStyle fontStyle = new FontStyle();
        //MenuUtil menuUtil = ObjectTable.getMenuUtil();
        //fontFamily
        ComboBox familyComboBox=DrawMenu.fontFamily;
        //ComboBox familyComboBox = (ComboBox) menuUtil.getMenuComponent(Command.getCommandString(Command.FONT_FAMILY));
        String family = (String) familyComboBox.getSelectedItem();
        if (!family.equals("")) {
            fontStyle.setFontFamily(family);
        }

        //bold
        ButtonOfToggle boldButton=DrawMenu.fontBold;
        //ButtonOfToggle boldButton = (ButtonOfToggle) menuUtil.getMenuComponent(Command.getCommandString(Command.BOLD));
        int bold = 0;
        if (boldButton.isSelected()) {
            bold = 1;
        }
        fontStyle.setBold(bold);

        //italic
        ButtonOfToggle italicButton=DrawMenu.fontItalic;
        //ButtonOfToggle italicButton = (ButtonOfToggle) menuUtil.getMenuComponent(Command.getCommandString(Command.ITALIC));
        int italic = 0;
        if (italicButton.isSelected()) {
            italic = 1;
        }
        fontStyle.setItalic(italic);

        //fontSize
        ComboBox fontSize=DrawMenu.fontSize;
        //ComboBox fontSize = (ComboBox) menuUtil.getMenuComponent(Command.getCommandString(Command.FONT_SIZE));
        String sizeString = (String) fontSize.getSelectedItem();
        if (!sizeString.equals("")) {
            fontStyle.setFontSize(Integer.parseInt((String) sizeString));
        }

        //underLine
        //int underLine=fontStyle.getUnderLine();
        ButtonOfToggle undetLineButton=DrawMenu.fontUnderline;
        //ButtonOfToggle undetLineButton = (ButtonOfToggle) menuUtil.getMenuComponent(Command.getCommandString(Command.UNDERLINE));
        int undeLine = -1;
        if (undetLineButton.isSelected()) {
            undeLine = TextAttribute.UNDERLINE_ON;
        }
        fontStyle.setUnderLine(undeLine);

        //subScript
        ButtonOfToggle subscriptButton=DrawMenu.fontSubscript;
        //ButtonOfToggle subscriptButton = (ButtonOfToggle) menuUtil.getMenuComponent(Command.getCommandString(Command.SUBSCRIPT));
        int subScript = 0;
        if (subscriptButton.isSelected()) {
            subScript = TextAttribute.SUPERSCRIPT_SUB;
        }
        fontStyle.setSuperScript(subScript);

        //superScript
        ButtonOfToggle superScriptButton=DrawMenu.fontSuperscript;
        //ButtonOfToggle superScriptButton = (ButtonOfToggle) menuUtil.getMenuComponent(Command.getCommandString(Command.SUPERSCRIPT));
        int superScript = subScript;
        if (superScriptButton.isSelected()) {
            superScript = TextAttribute.SUPERSCRIPT_SUPER;
        }
        fontStyle.setSuperScript(superScript);

        //fontColor
        ButtonOfColorChooser fontColorButton=DrawMenu.fontColor;
        //ButtonOfColorChooser fontColorButton = (ButtonOfColorChooser) menuUtil.getMenuComponent(Command.getCommandString(Command.FONT_COLOR));
        Color fontColor = fontColorButton.getCurrentColor();
        if (fontColor != null) {
            fontStyle.setFontColor(fontColor);
        }
        if (debug >0) {
            System.out.println("- FontStyle.getFontStyleFromMenu fontStyle: " + fontStyle.toString());
        }
        return fontStyle;
    }

    public static FontStyle setFontStyleToMenu(TextBox textBox, String callFrom) {
        if (debug >0) {
            System.out.println("- FontStyle.setFontStyleToMenu(extBox) callFrom="+ callFrom);
            System.out.println("  "+textBox.getShapeContainer().getShapeId());
        }
        FontStyle fontStyle = FontStyle.getDefaultFontStyle();
        AttributedString attribString = textBox.getCommittedTextContainer().getAttributedString();
        if (attribString == null) {
            return fontStyle;
        }
        AttributedCharacterIterator iterator = attribString.getIterator();
        //int start = 0;
        //int end = 0;
        int start=iterator.getBeginIndex();
        int end=iterator.getEndIndex();

        if (textBox.hasSelectedText()) {
            start = textBox.getSelectedTextStart();
            end = textBox.getSelectedTextEnd();
        } else if(textBox.getTextIndex()>=0){
            end = textBox.getTextIndex();
            if (end > 0) {
                start = end - 1;
            } else {
                end = 1;
            }
        }
        //-----------------------------------------------------------//
        fontStyle = FontStyle.getCommonFontStyle(iterator, start, end);
        //-----------------------------------------------------------//
        if (debug >0) {
            System.out.println("- FontStyle.setFontStyleToMenu(textBox) callFrom="
                    + callFrom +", start="+start+", end="+end+", FontStyle=" + fontStyle.toString());
        }
        //-------------------------------------//
        FontStyle.updateFontMenu(fontStyle);
        //-------------------------------------//
        return fontStyle;
    }
/*
    public static FontStyle setFontStyleToMenu(ShapeContainer[] containers, String callFrom) {
        FontStyle fontStyle=null;
        if(containers==null||containers.length==0) return null;
        if (debug >0) {
            System.out.println("- FontStyle.setFontStyleToMenu(ShapeContainers) callFrom="+ callFrom);
            for (int i = 0; i < containers.length; i++) {
                System.out.println("   "+containers[i].getShapeId());
            }
        }
        Vector vector = new Vector();
        for (int i = 0; i < containers.length; i++) {
            TextBox[] textBoxes = containers[i].getGroupedTextBoxes();
            for (int j = 0; j < textBoxes.length; j++) {
                vector.add(textBoxes[j]);
            }
        }
        TextBox[] textBoxes = new TextBox[vector.size()];
        for (int i = 0; i < textBoxes.length; i++) {
            textBoxes[i] = (TextBox) vector.get(i);
        }
        //-----------------------------------------------------------//
        fontStyle = FontStyle.getCommonFontStyle(textBoxes);
        //-----------------------------------------------------------//
        if (debug >0) {
            System.out.println("- FontStyle.setFontStyleToMenu(ShapeContainers) callFrom="
                    + callFrom + ", FontStyle=" + fontStyle.toString());
        }
        //-------------------------------------//
        FontStyle.updateFontMenu(fontStyle);
        //-------------------------------------//
        return fontStyle;
    }
*/
    public static void setDefaultFontStyleToMenu() {
        FontStyle defaultStyle=FontStyle.getDefaultFontStyle();
        //-------------------------------------//
        FontStyle.updateFontMenu(defaultStyle);
        //-------------------------------------//
    }
    
    private static void updateFontMenu(FontStyle fontStyle) {
        if (debug>0) {
            System.out.println("- FontStyle.updateFontMenu"
                    + ", fontStyle " + fontStyle.toString());
        }
        //MenuUtil menuUtil = ObjectTable.getMenuUtil();
        //fontFamily
        String fontFamily = fontStyle.getFontFamily();
        ComboBox familyComboBox=DrawMenu.fontFamily;
        //ComboBox family = (ComboBox) menuUtil.getMenuComponent(Command.getCommandString(Command.FONT_FAMILY));
        if (fontFamily.equals(FontStyle.UNDEF_string)) {
            fontFamily = " ";
        }
        familyComboBox.activateListener(false);
        familyComboBox.setSelectedItem(fontFamily);
        if (debug >1) {
            System.out.println("-  FontStyle.updateFontMenu  "
                    + "setSelectedItem(fontFamily) fontFamily=" + fontFamily);
        }
         familyComboBox.activateListener(true);
        //bold
        int bold = fontStyle.getBold();
        ButtonOfToggle boldButton=DrawMenu.fontBold;
        //ButtonOfToggle boldButton = (ButtonOfToggle) menuUtil.getMenuComponent(Command.getCommandString(Command.BOLD));
        boldButton.setSelected(false);
        if (bold != FontStyle.UNDEF_int && bold>0) {
            boldButton.setSelected(true);
        }

        int italic = fontStyle.getItalic();
        //italic
        ButtonOfToggle italicButton=DrawMenu.fontItalic;
        //ButtonOfToggle italicButton = (ButtonOfToggle) menuUtil.getMenuComponent(Command.getCommandString(Command.ITALIC));
        italicButton.setSelected(false);
        if (italic != FontStyle.UNDEF_int && italic>0) {
            italicButton.setSelected(true);
        }
        //fontSize
        int size = fontStyle.getFontSize();
        ComboBox fontSize=DrawMenu.fontSize;
        //ComboBox fontSize = (ComboBox) menuUtil.getMenuComponent(Command.getCommandString(Command.FONT_SIZE));
        fontSize.activateListener(false);
        if (size == FontStyle.UNDEF_int) {
            fontSize.setSelectedItem("");
        } else {
            fontSize.setSelectedItem(String.valueOf(size));
        }
        fontSize.activateListener(true);
        //underLine
        int underLine = fontStyle.getUnderLine();
        ButtonOfToggle undetLineButton=DrawMenu.fontUnderline;
        //ButtonOfToggle undetLineButton = (ButtonOfToggle) menuUtil.getMenuComponent(Command.getCommandString(Command.UNDERLINE));
        if (underLine == FontStyle.UNDEF_int || underLine < 0) {
            undetLineButton.setSelected(false);
        } else {
            undetLineButton.setSelected(true);
        }
        //superScript
        int superScript = fontStyle.getSuperScript();
        ButtonOfToggle subscriptButton=DrawMenu.fontSubscript;
        ButtonOfToggle superScriptButton=DrawMenu.fontSuperscript;
        //ButtonOfToggle subscriptButton = (ButtonOfToggle) menuUtil.getMenuComponent(Command.getCommandString(Command.SUBSCRIPT));
        //ButtonOfToggle superScriptButton = (ButtonOfToggle) menuUtil.getMenuComponent(Command.getCommandString(Command.SUPERSCRIPT));
        if (superScript == FontStyle.UNDEF_int || superScript == 0) {
            subscriptButton.setSelected(false);
            superScriptButton.setSelected(false);
        } else {
            if (superScript < 0) {
                subscriptButton.setSelected(true);
                superScriptButton.setSelected(false);
            } else if (superScript > 0) {
                subscriptButton.setSelected(false);
                superScriptButton.setSelected(true);
            }
        }
        //fontColor
        Color fontColor = fontStyle.getFontColor();
        ButtonOfColorChooser fontColorButton=DrawMenu.fontColor;
        //ButtonOfColorChooser fontColorButton = (ButtonOfColorChooser) menuUtil.getMenuComponent(Command.getCommandString(Command.FONT_COLOR));
        fontColorButton.setCurrentColor(fontColor);
    }

    private static FontStyle getCommonFontStyle(AttributedCharacterIterator text,
            int start, int end) {
        
        FontStyle fontStyle=new FontStyle();
        boolean ret=FontStyle.rangeError(text, start, end, "*** Error: FontStyle.getCommonFontStyle");
        if(ret) return fontStyle;
        
        if (debug >0) System.out.println("FontStyle.getCommonFontStyle "
                +" start="+start+", end="+end);
       
        AttributedStringUtil util = new AttributedStringUtil(text);
        String string=util.getString();
        if(debug>0) System.out.println("FontStyle.getCommonFontStyle fontColor"
                + ", start="+start+", end="+end+"\n"+util.toString());
        AttributedInterval[] intervals=new AttributedInterval[0];
        
        String fontFamily = FontStyle.UNDEF_string;
        String currentFontFamily = FontStyle.UNDEF_string;
        intervals = util.getAttributedIntervals(TextAttribute.FAMILY, start, end);
        if(debug>0) System.out.println("\n** FontStyle.getCommonFontStyle fontFamily"
                + ", start="+start+", end="+end+"\n"+AttributedInterval.toString(intervals));
        if (intervals.length > 0) {
            fontFamily = (String) intervals[0].getValue();
            for (int i = 1; i < intervals.length; i++) {
                currentFontFamily=(String)intervals[i].getValue();
                int istart=intervals[i].getStart();
                int iend=intervals[i].getEnd();
                if(string.substring(istart, iend).equals("\n")) continue;
                if (!fontFamily.equals(currentFontFamily)) {
                    fontFamily = FontStyle.UNDEF_string;
                }
            }
        }
        //Bold or Plain
        intervals = util.getAttributedIntervals(TextAttribute.WEIGHT, start, end);
        if(debug>0) System.out.println("\n** FontStyle.getCommonFontStyle bold"
                + ", start="+start+", end="+end+"\n"+AttributedInterval.toString(intervals));
        int bold = FontStyle.UNDEF_int;
        if (intervals.length > 0) {
            float weight = ((Float) intervals[0].getValue()).floatValue();
            bold = (weight == TextAttribute.WEIGHT_BOLD) ? 1 : 0;
            for (int i = 1; i < intervals.length; i++) {
                float currentWeight = ((Float) intervals[0].getValue()).floatValue();
                int currentBold = (currentWeight == TextAttribute.WEIGHT_BOLD) ? 1 : 0;
                int istart=intervals[i].getStart();
                int iend=intervals[i].getEnd();
                if(string.substring(istart, iend).equals("\n")) continue;
                if (currentBold != bold) {
                    bold = FontStyle.UNDEF_int;
                }
            } // end of for
        }
        
        //Italic or Regular
        intervals = util.getAttributedIntervals(TextAttribute.POSTURE, start, end);
        if(debug>0) System.out.println("\n** FontStyle.getCommonFontStyle italic"
                + ", start="+start+", end="+end+"\n"+AttributedInterval.toString(intervals));
        int italic = FontStyle.UNDEF_int;
        if (intervals.length > 0) {
            float posture = ((Float) intervals[0].getValue()).floatValue();
            italic = (posture == TextAttribute.POSTURE_OBLIQUE) ? 1 : 0;
            for (int i = 1; i < intervals.length; i++) {
                float currentPosture = ((Float) intervals[0].getValue()).floatValue();
                int currentItalic = (currentPosture == TextAttribute.POSTURE_OBLIQUE) ? 1 : 0;
                int istart=intervals[i].getStart();
                int iend=intervals[i].getEnd();
                if(string.substring(istart, iend).equals("\n")) continue;
                if (currentItalic != italic) {
                    italic = FontStyle.UNDEF_int;
                }
            } // end of for
        }
        //Font size
        int size = FontStyle.UNDEF_int;
        intervals = util.getAttributedIntervals(TextAttribute.SIZE, start, end);
        if(debug>0) System.out.println("\n** FontStyle.getCommonFontStyle font size"
                + ", start="+start+", end="+end+"\n"+AttributedInterval.toString(intervals));
        if (intervals.length > 0) {
            size = ((Integer) intervals[0].getValue()).intValue();
        
            for (int i = 1; i < intervals.length; i++) {
                int currentSize = ((Integer) intervals[i].getValue()).intValue();
                int istart=intervals[i].getStart();
                int iend=intervals[i].getEnd();
                if(string.substring(istart, iend).equals("\n")) continue;
                if (currentSize != size) {
                    size = FontStyle.UNDEF_int;
                }
            }
        }

        //underline
        int underLine = FontStyle.UNDEF_int;
        intervals = util.getAttributedIntervals(TextAttribute.UNDERLINE, start, end);
        if(debug>0) System.out.println("\n** FontStyle.getCommonFontStyle underline"
                + ", start="+start+", end="+end+"\n"+AttributedInterval.toString(intervals));
        if (intervals.length > 0) {
            underLine = ((Integer) intervals[0].getValue()).intValue();

            for (int i = 1; i < intervals.length; i++) {
               int currentUnderLine = ((Integer) intervals[0].getValue()).intValue();
                int istart=intervals[i].getStart();
                int iend=intervals[i].getEnd();
                if(string.substring(istart, iend).equals("\n")) continue;

                if (currentUnderLine != underLine) {
                    underLine = FontStyle.UNDEF_int;
                }
            }
        }
        //superScript
        int superScript = FontStyle.UNDEF_int;
        intervals = util.getAttributedIntervals(TextAttribute.SUPERSCRIPT, start, end);
        if(debug>0) System.out.println("\n** FontStyle.getCommonFontStyle superscript"
                + ", start="+start+", end="+end+"\n"+AttributedInterval.toString(intervals));
        if (intervals.length > 0) {
            superScript = ((Integer) intervals[0].getValue()).intValue();
            
            for (int i = 1; i < intervals.length; i++) {
                int currentSuperScript = ((Integer) intervals[i].getValue()).intValue();
                int istart=intervals[i].getStart();
                int iend=intervals[i].getEnd();
                if(string.substring(istart, iend).equals("\n")) continue;
                
                if (currentSuperScript != superScript) {
                    superScript = FontStyle.UNDEF_int;
                }
            }
        }
        
        //font color
        Color color = null;
        Color currentColor=null;
        intervals = util.getAttributedIntervals(TextAttribute.FOREGROUND, start, end);
        if(debug>0) System.out.println("\n** FontStyle.getCommonFontStyle fontColor"
                + ", start="+start+", end="+end+"\n"+AttributedInterval.toString(intervals));
        if (intervals.length > 0) {
            color = (Color) intervals[0].getValue();
            for (int i = 1; i < intervals.length; i++) {
                currentColor = (Color) intervals[i].getValue();
                int istart=intervals[i].getStart();
                int iend=intervals[i].getEnd();
                if(string.substring(istart, iend).equals("\n")) continue;
                if (currentColor != color) {
                    color = null;
                }
            }  //End of for
        }
        fontStyle = new FontStyle(fontFamily, bold, italic, size, underLine, superScript, color);
        if (debug >0) {
            System.out.println(" - FontStyle.getCommonFontStyle(AttributedCharacters)"
                    + ", fontStyle " + fontStyle.toString());
        }
        return fontStyle;
    }

    private static FontStyle getCommonFontStyle(TextBox[] textBoxes) {
        FontStyle commonFontStyle = new FontStyle();
        FontStyle[] fontStyles = new FontStyle[textBoxes.length];
        if (textBoxes == null || textBoxes.length == 0) {
            return commonFontStyle;
        }
        for (int i = 0; i < textBoxes.length; i++) {
            AttributedString attribSTring = textBoxes[i].getCommittedTextContainer().getAttributedString();
            if (attribSTring == null) {
                continue;
            }
            AttributedCharacterIterator iterator = attribSTring.getIterator();
            int start = iterator.getBeginIndex();
            int end = iterator.getEndIndex();
            //--------------------------------------------------------//
            fontStyles[i] = getCommonFontStyle(iterator, start, end);
            //--------------------------------------------------------//
        }
        if (debug>0) {
            for (int i = 0; i < fontStyles.length; i++) {
                System.out.println(" - FontStyles"
                        + " fontStyle[" + i + "]:" + fontStyles[i].toString());
            }
        }
        int index = -1;
        for (int i = 0; i < textBoxes.length; i++) {
            if (fontStyles[i] != null) {
                index = i;
            }
        }
        if (index < 0) {
            return commonFontStyle;
        }
        String fontFamily = fontStyles[index].getFontFamily();
        int bold = fontStyles[index].getBold();
        int italic = fontStyles[index].getItalic();
        int size = fontStyles[index].getFontSize();
        int underLine = fontStyles[index].getUnderLine();
        int superScript = fontStyles[index].getSuperScript();
        Color color = fontStyles[index].getFontColor();
        for (int i = index + 1; i < fontStyles.length; i++) {
            if (fontStyles[i] == null) {
                continue;
            }
            if (!fontStyles[i].getFontFamily().equals(fontFamily)) {
                fontFamily = FontStyle.UNDEF_string;
            }
            if (fontStyles[i].getBold() != bold) {
                bold = FontStyle.UNDEF_int;
            }
            if (fontStyles[i].getItalic() != italic) {
                italic = FontStyle.UNDEF_int;
            }
            if (fontStyles[i].getFontSize() != size) {
                size = FontStyle.UNDEF_int;
            }
            if (fontStyles[i].getUnderLine() != underLine) {
                underLine = FontStyle.UNDEF_int;
            }
            if (fontStyles[i].getSuperScript() != superScript) {
                superScript = FontStyle.UNDEF_int;
            }
            if (fontStyles[i].getFontColor() != color) {
                color = null;
            }
        }
        commonFontStyle = new FontStyle(fontFamily, bold, italic, size, underLine,
                superScript, color);
        if (debug>0) {
            System.out.println(" - FontStyle.getCommonFontStyle(textBoxes)"
                    + ", fontStyle " + commonFontStyle.toString());
        }
        return commonFontStyle;
    }
    
    public static FontStyle getFontStyleAt(AttributedCharacterIterator text, int position){
        
        FontStyle fontStyle = FontStyle.getDefaultFontStyle();
        boolean ret=FontStyle.rangeError(text, position, "*** Error: FontStyle.getFontStyleAt");
        if(ret) return fontStyle;
        if (text==null) return fontStyle;
        int start=text.getBeginIndex();
        int end=text.getEndIndex();
        int pos=position;
        AttributedStringUtil util=new AttributedStringUtil(text);
        if(position==0){
            for(int i=0;i<=end;i++){
                if(!util.getString(i, i+1).equals("\n")){
                    fontStyle=FontStyle.getCommonFontStyle(text,i, i+1);
                    break;
                }
            }
        }else{
            for(int i=position-1;i>=0;i--){
                if(!util.getString(i, i+1).equals("\n")){
                    fontStyle=FontStyle.getCommonFontStyle(text,i, i+1);
                    break;
                }
            }
        }
        return fontStyle;
    }
/*
    public static FontStyle getFontStyleAt(AttributedCharacterIterator text, int position){
        
        FontStyle fontStyle = FontStyle.getDefaultFontStyle();
        boolean ret=FontStyle.rangeError(text, position, "*** Error: FontStyle.getFontStyleAt");
        if(ret) return fontStyle;
        if (text==null) return fontStyle;
        int start=text.getBeginIndex();
        int end=text.getEndIndex();
        int pos=position;
        if (pos==0) pos=1;
        AttributedStringUtil util=new AttributedStringUtil(text);
        if(!util.getString(pos-1, pos).equals("\n")){
            fontStyle=FontStyle.getCommonFontStyle(text, pos-1, pos);
        }
        return fontStyle;
    }
 */

    public static boolean isFontStyle(AttributedCharacterIterator text,
            int start, int end) {
        boolean ret=FontStyle.rangeError(text, start, end, "*** Error: FontStyle.isFontStyle");
        if(ret) return false;

        AttributedStringUtil util = new AttributedStringUtil(text);
        String fontAttrib = "";
        // Font family
        AttributedInterval[] intervals = util.getAttributedIntervals(TextAttribute.FAMILY, start, end);
        if (intervals.length > 0) {
            fontAttrib += "font.family";
        }
        //Bold or Plain
        intervals = util.getAttributedIntervals(TextAttribute.WEIGHT, start, end);
        if (intervals.length > 0) {
            fontAttrib += "+plain/bold";
        }

        //Italic or Regular
        intervals = util.getAttributedIntervals(TextAttribute.POSTURE, start, end);
        if (intervals.length > 0) {
            fontAttrib += "+italic";
        }
        //Font size
        intervals = util.getAttributedIntervals(TextAttribute.SIZE, start, end);
        if (intervals.length > 0) {
            fontAttrib += "+font.size";
        }
        //underline
        intervals = util.getAttributedIntervals(TextAttribute.UNDERLINE, start, end);
        if (intervals.length > 0) {
            fontAttrib += "+underline";
        }
        //superScript
        intervals = util.getAttributedIntervals(TextAttribute.SUPERSCRIPT, start, end);
        if (intervals.length > 0) {
            fontAttrib += "+superscript";
        }
        //font color
        intervals = util.getAttributedIntervals(TextAttribute.FOREGROUND, start, end);
        if (intervals.length > 0) {
            fontAttrib += "+font.color";
        }
        if (fontAttrib.equals("")) {
            return false;
        }
        if (debug>0) {
            System.out.println("** FontStyle.isFontStyle fontAttrib=" + fontAttrib);
        }
        return true;
    }
/*
    public static boolean isDefaultFontStyle(AttributedCharacterIterator text,
            int start, int end) {
        
        boolean ret=FontStyle.rangeError(text, start, end, "*** Error: FontStyle.isDefaultFontStyle");
        if(ret) return true;
        
        FontStyle currentStyle = FontStyle.getCommonFontStyle(text, start, end);
        FontStyle DefaultStyle = FontStyle.getDefaultFontStyle();
        if (debug>0) {
            System.out.println("** isDefaultFontStyle"
                    + "\n currentStyle=" + currentStyle
                    + "\n DefaultStyle=" + DefaultStyle);
        }
        String unmatch = "";
        if (!currentStyle.getFontFamily().equals(DefaultStyle.getFontFamily())) {
            unmatch += "fontFamily;";
        }
        if (currentStyle.getBold() != 0
                && currentStyle.getBold() != TextAttribute.WEIGHT_REGULAR
                && currentStyle.getBold() != DefaultStyle.getBold()) {
            unmatch += "+plain/bold;";
        }
        if (currentStyle.getItalic() != TextAttribute.POSTURE_REGULAR
                && currentStyle.getItalic() != DefaultStyle.getItalic()) {
            unmatch += "+italic;";
        }
        if (currentStyle.getFontSize() != DefaultStyle.getFontSize()) {
            unmatch += "+font.size;";
        }
        if (currentStyle.getSuperScript() != 0
                && currentStyle.getSuperScript() != DefaultStyle.getSuperScript()) {
            unmatch += "+superscript;";
        }
        if (currentStyle.getUnderLine() != -1
                && currentStyle.getUnderLine() != DefaultStyle.getUnderLine()) {
            unmatch += "+underline;";
        }
        if (currentStyle.getFontColor() != null
                && !currentStyle.getFontColor().equals(DefaultStyle.getFontColor())) {
            unmatch += "+font.color;";
        }
        if (unmatch.equals("")) {
            return true;
        }
        if (debug>0) {
            System.out.println("** FontStyle.isDefaultFontStyle unmatch=" + unmatch);
        }
        return false;
    }
*/
    public static boolean isDefaultFontStyle(FontStyle fontStyle) {
        
        FontStyle defaultStyle = FontStyle.getDefaultFontStyle();
        boolean isDefault=true;
        
        String family=fontStyle.getFontFamily();
        if(!family.equals(UNDEF_string)&&
                !family.equals(defaultStyle.getFontFamily())) isDefault=false;
        
        int bold=fontStyle.getBold();
        if(bold!=UNDEF_int&&bold!=defaultStyle.getBold()) isDefault=false;

        int italic=fontStyle.getItalic();
        if(italic!=UNDEF_int&&italic!=defaultStyle.getItalic()) isDefault=false;
        
        int size=fontStyle.getFontSize();
        if(size!=UNDEF_int&&size!=defaultStyle.getFontSize()) isDefault=false;
        
        int superScript=fontStyle.getSuperScript();
        if(superScript!=UNDEF_int&&superScript!=defaultStyle.getSuperScript()) isDefault=false;
        
        int underLine=fontStyle.getUnderLine();
        if(underLine!=UNDEF_int&&underLine!=defaultStyle.getUnderLine()) isDefault=false;        

        Color color=fontStyle.getFontColor();
        if(color!=null&&!color.equals(Color.BLACK)) isDefault=false;
        
        if (debug>0) {
            System.out.println("FontStyle.isDefaultFontStyle  isDefault="+isDefault
                    +", fontStyle="+fontStyle);
        }
        return isDefault;
    }
    
    private static void rangeCheck(int index, int indexMax) throws RangeException{
        if(index<0||index>indexMax) throw new RangeException();
    } 
    
    private static boolean rangeError(AttributedCharacterIterator text, int pos, String errMessage){
        int beginIndex=-1;
        int endIndex=-1;
        if(text!=null){
            beginIndex=text.getBeginIndex();
            endIndex=text.getEndIndex();
        }
        boolean ret=false;
        try{
            rangeCheck(pos, endIndex);
        } catch(RangeException e){
            System.err.println(errMessage+" pos="+pos
                    +", range="+beginIndex+","+endIndex);
            e.printStackTrace();
            ret=true;
        }
        return ret;
    }
    
    private static boolean rangeError(AttributedCharacterIterator text, int start, int end, String errMessage){
        int beginIndex=-1;
        int endIndex=-1;
        if(text!=null){
            beginIndex=text.getBeginIndex();
            endIndex=text.getEndIndex();
        }
        boolean ret=false;
/*
        if(start<beginIndex||end>endIndex||start>end){
            System.err.println(errMessage+" start="+start+", end="+end
                    +", range="+beginIndex+","+endIndex);
            ret=true;
        }
*/
        try{
            rangeCheck(start, end);
            rangeCheck(end, endIndex);
        } catch(RangeException e){
            System.err.println(errMessage+" start="+start+", end="+end
                    +", range="+beginIndex+","+endIndex);
            e.printStackTrace();
            ret=true;
        }
        return ret;
    }

    static class RangeException extends Exception {
        RangeException() {}
        
        public String toString() {
            return "range Exception";
        }
    }
    
}
