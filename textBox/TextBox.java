package textBox;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.util.*;
import java.io.*;
import java.text.*;
import java.text.AttributedCharacterIterator.Attribute;
import javax.swing.*;
import javax.swing.JOptionPane;
import main.*;
import util.*;

public class TextBox implements MouseListener, MouseMotionListener, Serializable {

    ShapeContainer shapeContainer;
    public Rectangle2D textArea = null;
    public Insets textBoxInsets = new Insets(1, 1, 1, 1);
    public int textAlign = 0;
    public int lineSpace = 0;
    private FontStyle currentFontStyle = FontStyle.getDefaultFontStyle();
    //UnselectableArea unselectableArea;
    private CommittedTextContainer committedTextContainer = new CommittedTextContainer();
    private AttributedCharacterIterator composedTextIterator = null;
    //int composedTextPosition=0;
    private LineBreaker lineBreaker = new LineBreaker();
    private boolean validTextLayout = false;
    public CaretPosition caretPosition = new CaretPosition(0, 0, 0, true);
    private CaretPosition selStart = new CaretPosition(-1, -1);
    private CaretPosition selEnd = new CaretPosition(-1, -1);
    private CaretPosition clickedPoint = new CaretPosition(-1, -1);
    private CaretPosition dragStart = new CaretPosition(-1, -1);
    private CaretPosition dragEnd = new CaretPosition(-1, -1);
    //MousePositionInfo mousePositionInfo = null;
    private boolean rightButton;
    boolean modified = false;
    Point2D oldPoint = null;
    Point2D newPoint = null;
    //ConnectionUtil connectionUtil=null;
    private TextUndoSetup.DeleteText UndoSetupDeleteText = null;
    private TextUndoSetup.InsertText UndoSetupInsertText = null;
    final public static int KEYBOARD = 0;
    final public static int INPUTMETHOD_TEXTCHANGED = 1;
    final public static int COMMAND = 3;
    final public static int UNDO_REDO = 4;
    final public static String[] 
            methodStr={"KEYBOARD", "INPUTMETHOD_TEXTCHANGED", "NULL", "COMMAND", "UNDO_REDO"};
    public static int debug = 0;

    public TextBox() {
        ObjectTable.getListenerPanel().requestFocus();
    }

    public ShapeContainer getShapeContainer() {
        if (this.shapeContainer == null) {
            System.err.println("*** Error TextBox.getShapeContainer :"
                    + "No shapeContainer");
        }
        return this.shapeContainer;
    }

    public void setShapeContainer(ShapeContainer container) {
        this.shapeContainer = container;
    }

    public void activateMouseListener(boolean activate) {
        //MouseListener mouseLS=(MouseListener)this;
        ListenerPanel listenerPanel = ObjectTable.getListenerPanel();
        if (activate) {
            if (!listenerPanel.isMouseListener(this)
                    && !listenerPanel.isMouseMotionListener(this)) {
                listenerPanel.addMouseListener(this);
                listenerPanel.addMouseMotionListener(this);
            }

        }
        if (!activate) {
            if (listenerPanel.isMouseListener(this)
                    && listenerPanel.isMouseMotionListener(this)) {
                listenerPanel.removeMouseListener(this);
                listenerPanel.removeMouseMotionListener(this);
            }

        }
        this.setValidTextLayout(false);
    }//activateTextBox

    public boolean isEditable(){
        ListenerPanel listenerPanel = ObjectTable.getListenerPanel();
        MouseListener[] mouseLS =listenerPanel.getMouseListeners();
        MouseMotionListener[] mouseMotionLS = listenerPanel.getMouseMotionListeners();
        boolean isMouseListener = false;
        for (int i = 0; i < mouseLS.length; i++) {
            if (mouseLS[i].equals(this)) {
                isMouseListener = true;
            }
        }
        boolean isMouseMotionListener=false;
        for (int i = 0; i < mouseMotionLS.length; i++) {
            if (mouseMotionLS[i].equals(this)) {
                isMouseMotionListener = true;
            }
        }
        boolean isRegistered;
        isRegistered=isMouseListener&&isMouseMotionListener;
        //System.out.println("TextBox isActivated="+isRegistered);
        return isRegistered;
    }

    public Rectangle2D getBoundingBox() {
        return textArea;
    }

    public Rectangle2D getTextArea() {
        return this.textArea;
    }

    public void setTextArea(Rectangle2D textArea) {
        this.textArea = textArea;
        this.setValidTextLayout(false);
    }

    public CommittedTextContainer getCommittedTextContainer() {
        return this.committedTextContainer;
    }

    public boolean isCommittedText() {
        boolean has = false;
        if (this != null && this.getCommittedTextContainer() != null
                && this.getCommittedTextContainer().isComittedText()) {
            has = true;
        }
        return has;
    }

    public Rectangle getTextLocation() {
        return this.lineBreaker.getCaretRectangle(this.caretPosition, 0);
    }

    public void resizeTextArea(Rectangle2D oldBox, Rectangle2D newBox) {
        Rectangle2D textArea = this.getTextArea();
        double x = newBox.getX();
        double y = newBox.getY();
        double scaleX = newBox.getWidth() / oldBox.getWidth();
        double scaleY = newBox.getHeight() / oldBox.getHeight();
        Double X = scaleX * (textArea.getX() - oldBox.getX()) + x;
        Double Y = scaleY * (textArea.getY() - oldBox.getY()) + y;
        textArea.setRect(X, Y, scaleX * textArea.getWidth(), scaleY * textArea.getHeight());
        this.setTextArea(textArea);
    }

    public Insets getTextBoxInsets() {
        return this.textBoxInsets;
    }

    public void setTextBoxInsets(Insets insets) {
        this.textBoxInsets = insets;
        this.setValidTextLayout(false);
    }

    public int getTextLineSpace() {
        return this.lineSpace;
    }

    public void setTextLineSpace(double lineSpace) {
        this.lineSpace = (int) lineSpace;
        this.setValidTextLayout(false);
    }

    public int getTextAlign() {
        return this.textAlign;
    }

    public void setTextAlign(int textAlign) {
        this.textAlign = textAlign;
        this.setValidTextLayout(false);
    }

    public void setTextBoxLayout(Insets textBoxInsets, int textAlign, double lineSpace) {
        if (textBoxInsets != null) {
            this.setTextBoxInsets(textBoxInsets);
        }
        if (textAlign >= 0) {
            this.setTextAlign(textAlign);
        }
        if (lineSpace >= 0.0) {
            this.setTextLineSpace(lineSpace);
        }
    }

    public void setFontStyle(FontStyle newFontStyle) {
        if (debug > 0) {
            System.out.println(" - TextBox.setFontStyle"
                    + ", newFontStyle=" + newFontStyle.toString());
        }
        int start = this.committedTextContainer.getBeginIndex();
        int end = this.committedTextContainer.getEndIndex();
        if (start < 0 || end < 0) {
            return;
        }
        if (this.shapeContainer.isEditableTextBox()) {
            if (this.hasSelectedText()) {
                start = this.getSelectedTextStart();
                end = this.getSelectedTextEnd();
/*
                start = this.selStart.getTextIndex();
                end = this.selEnd.getTextIndex();
 */
            } else {
                return;
            }
        }
        AttributedCharacterIterator commitedText = this.committedTextContainer.getCommittedText();
        commitedText = newFontStyle.setTo(commitedText, start, end);
        this.getCommittedTextContainer().setCommittedText(commitedText);
        this.setValidTextLayout(false);
        if (debug > 0) {
            System.out.println(" - TextBox.setFontStyle"
                    + ", start=" + start + ", end=" + end
                    + ", committedText=" + this.committedTextContainer.getString(start, end));
        }
    }

    public void setCurrentFontStyle(FontStyle newFontStyle, String callFrom) {
        if (debug > 0) {
            System.out.println(" ** TextBox.setCurrentFontStyle callFrom"
                    + callFrom + ", currentFontStyle=" + newFontStyle);
        }
        this.currentFontStyle = newFontStyle;
    }
    
    public Color[] getFontColors(){
        Color[] colors = new Color[0];
        AttributedString attribSTring = this.getCommittedTextContainer().getAttributedString();
        if (attribSTring == null) return colors;
        AttributedCharacterIterator iterator = attribSTring.getIterator();
        int start = iterator.getBeginIndex();
        int end = iterator.getEndIndex();
        Color color = null;
        AttributedStringUtil util = new AttributedStringUtil(iterator);
        AttributedInterval[] intervals=util.getAttributedIntervals(TextAttribute.FOREGROUND, start, end);
        Vector<Color> vector = new Vector<Color>();
        for (int i = 0; i < intervals.length; i++) {
            Color currentColor=(Color)intervals[i].getValue();
            if(currentColor!=null) vector.add(currentColor);
        }
        colors = new Color[vector.size()];
        for (int i = 0; i < vector.size(); i++) {
            colors[i]=vector.get(i);
        }
        return colors;
    }

    public Rectangle2D getMarginlessTextArea() {
        if (this.textArea == null) {
            return null;
        }
        double x0 = textArea.getX();
        double y0 = textArea.getY();
        double width = textArea.getWidth();
        double height = textArea.getHeight();
        int leftMargin = this.textBoxInsets.left;
        int rightMargin = this.textBoxInsets.right;
        int topMargin = this.textBoxInsets.top;
        int bottomMargin = this.textBoxInsets.bottom;
        Rectangle2D realTextArea = new Rectangle2D.Double(x0 + leftMargin, y0 + topMargin,
                width - leftMargin - rightMargin, height - topMargin - bottomMargin);
        return realTextArea;
    }
/*
    public SerializableTextBox getSerializableTextBox() {
        SerializableTextBox data = new SerializableTextBox();
        data.shapeId = this.getShapeContainer().getShapeId();
        data.textArea = (Rectangle2D) this.getTextArea().clone();
        CommittedTextContainer container = this.getCommittedTextContainer();
        data.attributedString = container.getSerializableAttributedString();
        data.textBoxInsets = (Insets) this.getTextBoxInsets().clone();
        data.textAlign = this.getTextAlign();
        data.lineSpace = this.getTextLineSpace();
        return data;
    }

    public void setSerializableTextBox(SerializableTextBox data) {
        this.setTextArea(data.textArea);
        this.committedTextContainer.setSerializableAttributedString(data.attributedString);
        this.setTextBoxInsets(data.textBoxInsets);
        this.setTextAlign(data.textAlign);
        this.setTextLineSpace(data.lineSpace);
    }
*/
    public String toString() {
        String str = "";
        double x = textArea.getX();
        double y = textArea.getY();
        double width = textArea.getWidth();
        double height = textArea.getHeight();
        str = "TextBox  Parent=" + this.shapeContainer.getShapeId()
                + ", TextArea  x,y=(" + String.valueOf((int) x) + "," + String.valueOf((int) y) + ")"
                + "  width,height=" + String.valueOf((int) width) + "," + String.valueOf((int) height);
        return str;
    }

    private void setValidTextLayout(boolean valid) {
        this.validTextLayout = valid;
    }
    
    private boolean getValidTextLayout() {
        return this.validTextLayout;
    }
    
    public void keyTyped(char keyChar) {
        this.deleteSelectedText(TextBox.KEYBOARD);
        AttributedString attribChar = new AttributedString(String.valueOf(keyChar));
        if (debug > 0) {
            System.out.println(" - TextBox.keyTyped String :" + Util.Text(attribChar));
        }
        int insertionIndex = this.caretPosition.getTextIndex();
        this.insertText(TextBox.KEYBOARD, insertionIndex, attribChar.getIterator());
        this.setValidTextLayout(false);
        ObjectTable.getDrawPanel().repaint();
    }

    public void keyPressed(int keyCode) {
        if(debug>0) System.out.println("TextBox.keyPressed keyCode=" + keyCode);
        if (keyCode == KeyEvent.VK_LEFT) {
            this.caretPosition.columnOffset(-1, this.lineBreaker);
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            this.caretPosition.columnOffset(1, this.lineBreaker);
        }
        if (keyCode == KeyEvent.VK_UP) {
            this.caretPosition.lineOffset(-1, this.lineBreaker);
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            this.caretPosition.lineOffset(1, this.lineBreaker);
        }
        FontStyle fontStyle = FontStyle.setFontStyleToMenu(this, "TextBox");
        this.setCurrentFontStyle(fontStyle, "keyPressed");
        ObjectTable.getDrawPanel().repaint();
    }
//InputMethodListener implementation

    public void inputMethodTextChanged(InputMethodEvent event) {
        final Attribute[] IM_ATTRIBUTES = {TextAttribute.INPUT_METHOD_HIGHLIGHT};
        if (debug >0) {
            printInputMethodStatus(event);
        }
        int committedCharacterCount = event.getCommittedCharacterCount();
        AttributedCharacterIterator text = event.getText();
        this.composedTextIterator = null;
        if (text == null) {
            event.consume();
            this.setValidTextLayout(false);
            ObjectTable.getDrawPanel().repaint();
            return;
        }
        // insert the committed text
        AttributedStringUtil attribUtil = new AttributedStringUtil(text);
        if (committedCharacterCount > 0) {
            AttributedString committedText = attribUtil.getAttributedSubString(0, committedCharacterCount);
            int insertionIndex = this.caretPosition.getTextIndex();
            if(debug>0) System.out.println("** inputMethodTextChanged insertionIndex="+insertionIndex);
            AttributedCharacterIterator committedTextIterator = committedText.getIterator();
            this.insertText(TextBox.INPUTMETHOD_TEXTCHANGED, insertionIndex, committedTextIterator);
        }
        // set the composed text
        if (text.getEndIndex() - (text.getBeginIndex() + committedCharacterCount) > 0) {
            this.deleteSelectedText(TextBox.INPUTMETHOD_TEXTCHANGED);
            AttributedString composedText = new AttributedString(text,
                    text.getBeginIndex() + committedCharacterCount,
                    text.getEndIndex(), IM_ATTRIBUTES);
            this.composedTextIterator = composedText.getIterator();
            this.composedTextIterator = this.currentFontStyle.setTo(this.composedTextIterator, 0,
                    this.composedTextIterator.getEndIndex());
        }
        this.setValidTextLayout(false);
        ObjectTable.getDrawPanel().repaint();
        event.consume();
    }

    private void printInputMethodStatus(InputMethodEvent event) {
        int committedCharacterCount = event.getCommittedCharacterCount();
        AttributedCharacterIterator text = event.getText();
        TextHitInfo caret = event.getCaret();
        if (text == null) {
            System.out.println(" - TextBox.inputMethodTextChanged  text=null");
            return;
        }
        int textLength = text.getEndIndex() - text.getBeginIndex();
        String committedTextStr = (new AttributedStringUtil(text)).getString(0, committedCharacterCount);
        String composedTextstr = (new AttributedStringUtil(text)).getString(committedCharacterCount, textLength);
        System.out.println(" - TextBox.inputMethodTextChanged  "
                + "event.caret=" + caret.getInsertionIndex()
                + ", committedCharacterCount=" + committedCharacterCount
                + ", committedText=" + committedTextStr + ", composedText=" + composedTextstr);
    }

    public void insertText(int method, int position, AttributedCharacterIterator attribStr) {
        int insertionIndex = position;
        if (position < 0) {
            insertionIndex = 0;
        }
        AttributedStringUtil util=new AttributedStringUtil(attribStr);
        if (debug >0) System.out.println("* TextBox.insertString method="+TextBox.methodStr[method]
                    +", position="+position+"\n   attribStr="+util.toString());

        AttributedCharacterIterator insertStr = attribStr;
        //Input using keyboard or InputMethod TextChange ?
        if(method==TextBox.KEYBOARD||method==TextBox.INPUTMETHOD_TEXTCHANGED){
            FontStyle fontStyle=FontStyle.getDefaultFontStyle();
            AttributedString committedText=this.committedTextContainer.getAttributedString();
            if(committedText!=null) {
                AttributedCharacterIterator iterator=committedText.getIterator();
                FontStyle fontStyleAt=FontStyle.getFontStyleAt(iterator, position);
                if(!FontStyle.isDefaultFontStyle(fontStyleAt)) fontStyle=fontStyleAt;
            }
            this.currentFontStyle=fontStyle;
            insertStr = fontStyle.setTo(attribStr, 0, attribStr.getEndIndex());
        }
        if(method==TextBox.COMMAND){
            boolean isFontStyle=FontStyle.isFontStyle(attribStr, attribStr.getBeginIndex(), 
                    attribStr.getEndIndex());
            if(!isFontStyle){
                FontStyle fontStyle=FontStyle.getDefaultFontStyle();
                AttributedString committedText=this.committedTextContainer.getAttributedString();
                if(committedText!=null){
                    AttributedCharacterIterator iterator=committedText.getIterator();
                    FontStyle fontStyleAt=FontStyle.getFontStyleAt(iterator, position);
                    if(!FontStyle.isDefaultFontStyle(fontStyleAt)) fontStyle=fontStyleAt;
                }
                this.currentFontStyle=fontStyle;
                insertStr = fontStyle.setTo(attribStr, 0, attribStr.getEndIndex());
            }
        }
        this.committedTextContainer.insertText(insertionIndex, insertStr);
        this.lineBreaker.setData(this.getDisplayText(), this);
        int textPosition = insertionIndex + insertStr.getEndIndex();
        this.caretPosition.updateCaretPosition(textPosition, this.lineBreaker,"insetText");
        //this.caretPosition=CaretPosition.getCaretPosition(textPosition, this.lineBreaker, "insetText");
        
        if (debug > 0) {
            System.out.println(" - TextBox.insertString "
                    + "caretPosition updated=" + this.caretPosition);
        }
        
        if (method != TextBox.UNDO_REDO) {
            //----------------------- undo setup -----------------------//
            AttributedString insertedStr = this.committedTextContainer.getAttributedSubString(insertionIndex,
                    insertionIndex + insertStr.getEndIndex());
            UndoDrawManager undoDrawManager = ObjectTable.getUndoDrawManager();
            if (this.UndoSetupInsertText == null) {
                this.UndoSetupInsertText = new TextUndoSetup.InsertText(undoDrawManager);
            }
            boolean delimit=false;
            if(method==TextBox.COMMAND) delimit=true;
            this.UndoSetupInsertText.setInsertedText(this.shapeContainer,
                    insertedStr, insertionIndex, delimit);
            //----------------------- undo setup -----------------------//  
        }
        this.setValidTextLayout(false);
        ObjectTable.getDrawPanel().repaint();
    }

    public void deleteSelectedText(int method) {
        if (!this.hasSelectedText()) {
            return;
        }
        int start = this.getSelectedTextStart();
        int end = this.getSelectedTextEnd();
        this.deleteText(method, start, end);
        this.resetSelection();
    }

    public void deleteText(int method, int start, int end) {
        String str = this.getCommittedTextContainer().getString();
        String delStr = this.getCommittedTextContainer().getString(start, end);
        if (debug >0) {
            System.out.println("** TextBox deleteText" 
                    + ", start=" + start + ", end" + end
                    +"\ncurrent text=" + Util.Text(str));
        }
        AttributedString deletedText =
                this.getCommittedTextContainer().getAttributedSubString(start, end);
        this.getCommittedTextContainer().deleteText(method, start, end);
        this.lineBreaker.setData(this.getDisplayText(), this);
        this.caretPosition.updateCaretPosition(start, this.lineBreaker, "deleteText");
        this.resetSelection();
        if (method != TextBox.UNDO_REDO && start < end) {
            //------------------------------ undo setup ----------------// 
            UndoDrawManager undoDrawManager = ObjectTable.getUndoDrawManager();
            if (this.UndoSetupDeleteText == null) {
                this.UndoSetupDeleteText = new TextUndoSetup.DeleteText(undoDrawManager);
            }
            this.UndoSetupDeleteText.setDeletedText(this.shapeContainer,
                    deletedText, start, end);
            //------------------------------- undo setup ---------------//
        }
        this.setValidTextLayout(false);
        ObjectTable.getDrawPanel().repaint();
    }

    public int deleteTextByDelCommandOrBSkey(int method) {
        int start = -1;
        int end = -1;
        if (this.hasSelectedText()) {
            start = this.getSelectedTextStart();
            end = this.getSelectedTextEnd();
        } else {
            int textPosition = this.caretPosition.getTextIndex();
            if (textPosition > 0) {
                start = textPosition - 1;
                end = textPosition;
                if (debug > 0) {
                    System.out.println("** Textbox.deleteText one char");
                }
            } else {
                Toolkit.getDefaultToolkit().beep();
                return -1;
            }
        }
        if (start >= 0) {
            this.deleteText(method, start, end);
        }
        return (end - start);
    }

    public void replaceText(int method, AttributedString attribStr) {
        this.getCommittedTextContainer().setCommittedText(attribStr.getIterator());
    }

    public AttributedCharacterIterator getComposedText() {
        return this.composedTextIterator;
    }

    public AttributedCharacterIterator getDisplayText() {
        AttributedCharacterIterator displayText = null;
        if (this.composedTextIterator == null) {
            displayText = this.committedTextContainer.getCommittedText();
        } else {
            int characterIndex = 0;
            if (this.caretPosition.isValid()) {
                characterIndex = this.caretPosition.getTextIndex();
                if(debug>0) System.out.println("** getDisplayText characterIndex="+characterIndex
                        +", caretPosition="+this.caretPosition.toString());
            }
            displayText = this.committedTextContainer.getDisplayText(characterIndex, this.composedTextIterator);
        }
        if (debug >0) {
            System.out.println("** TextBox.getDisplayText=" + Util.Text(displayText));
        }
        return displayText;
    }

// 
    public void drawTextBox(Graphics g) {
        if (debug > 1) {
            System.out.println(" - TextBox.drawTextBox "
                    + "isEditableTextBox=" + this.shapeContainer.isEditableTextBox()
                    + ", caretPosition: " + caretPosition.toStringOfTextIndex());
        }
        Graphics2D g2 = (Graphics2D) g;
        //drawTextArea(g);
        if (DrawParameters.DrawMode == DrawParameters.DrawOnScreen) {
            drawTextArea(g);
        }
        // set Clip of text Area
        Shape currentClip = g2.getClip();
        g2.setClip(this.getEnlargedRectangle(this.getBoundingBox(), 10d, 10d));
        //g2.setClip(this.getEnlargedBoundingBox(10d, 10d));
        //-----------//
        drawText(g2);
        //-----------//
        //draw Caret and Selection
        if (this.shapeContainer.isEditableTextBox()) {
            if (!hasSelectedText()) {
                AttributedCharacterIterator composedText = this.getComposedText();
                int offset = 0;
                if (composedText != null) {
                    offset = composedText.getEndIndex() - composedText.getBeginIndex();
                }
                //----------------------//
                this.drawCaret(g, offset);
                //----------------------//
            } else {
                this.resetCaretPosition();
                this.drawTextSelection(g);
            }
        }
        g2.setClip(currentClip);
    }// End of paint

    public void drawTextArea(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Rectangle currentClip = g2.getClipBounds();
        Color currentColor = g2.getColor();
        Stroke currentStroke = g2.getStroke();
        Color lineColor = Color.LIGHT_GRAY;
        if (this.getShapeContainer().isEditableTextBox()) {
            lineColor = Color.GREEN;
        }
        g2.setColor(lineColor);
        Rectangle2D shrinkedRect = this.getShrinkedRectangle(this.textArea, 0.5d, 0.5d);
        if (this.shapeContainer.isSelected()&&shrinkedRect!=null) {
            g2.draw(shrinkedRect);
        }
        g2.setStroke(currentStroke);
        g2.setColor(currentColor);
        g2.setClip(currentClip);
    }

    public void drawText(Graphics2D g) {
        if (debug >0) {
            System.out.println("** TextBox.drawText "
                    + "isEditableTextBox=" + this.shapeContainer.isEditableTextBox()
                    +", hasSelectedText="+hasSelectedText()
                    + ", validTextLayout=" + this.getValidTextLayout()
                    + ", num of textLayouts="+(this.lineBreaker.getTextLayouts()).length
                    + ", caretPosition " + caretPosition.toStringOfTextIndex()
                    + "\n  text='" + Util.Text(this.getCommittedTextContainer().getString()) + "'");
        }
        Graphics2D g2 = (Graphics2D) g;
        Color currentColor = g2.getColor();
        g2.setColor(Color.BLACK);
        Shape currentClip = g2.getClip();
        if (this.textArea == null) {
            return;
        }
        g2.setClip(this.textArea);
        // Draw Text with Multiple Lines
        if (!this.getValidTextLayout()) {
            this.lineBreaker.setData(this.getDisplayText(), this);
            if (hasSelectedText()) {
                int startPos = this.selStart.getTextIndex();
                int endPos = this.selEnd.getTextIndex();
                this.selStart.updateCaretPosition(startPos, this.lineBreaker, "drawText");
                this.selEnd.updateCaretPosition(endPos, this.lineBreaker, "drawText");
            }
            if (this.caretPosition.isValid()) {
                int textPos = this.caretPosition.getTextIndex();
                if (textPos >= 0) {
                    this.caretPosition.updateCaretPosition(textPos, this.lineBreaker, "drawText");
                }
            }
            this.validTextLayout = true;
        }
        TextLayout[] textLayouts = this.lineBreaker.getTextLayouts();
        Point2D[] positions = this.lineBreaker.getTextLayoutPositions();
        Rectangle2D[] bounds = this.lineBreaker.getBounds();
        for (int i = 0; i < textLayouts.length; i++) {
            textLayouts[i].draw(g2, (float) positions[i].getX(),
                    (float) positions[i].getY());
        }
        // Draw TextLayoutBounds for test
        if (DrawParameters.DRAW_TEXTLAYOUT) {
            g2.setColor(Color.MAGENTA);
            for (int i = 0; i < textLayouts.length; i++) {
                g2.draw(bounds[i]);
            }
        } //End of for
        g2.setColor(currentColor);
        g2.setClip(currentClip);

    }

    public void drawCaret(Graphics g, int offset) {
        if (debug > 0) {
            System.out.println(" - TextBox.drawCaret "
                    + "isEditableTextBox=" + this.shapeContainer.isEditableTextBox()
                    + ", caretPosition=" + caretPosition.toString());
        }
        if (!this.caretPosition.isValid()) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        Shape currentClip = g2.getClip();
        g2.setClip(this.textArea);
        Rectangle caretRectangle = this.lineBreaker.getCaretRectangle(this.caretPosition, offset);
        //Draw caret
        Color currentColor = g2.getColor();
        g2.setColor(Color.red);
        if (caretRectangle != null) {
            g2.draw(caretRectangle);
        }
        g2.setClip(currentClip);
        g2.setColor(currentColor);
    }

    public void drawTextSelection(Graphics g) {
        if (debug > 1) {
            System.out.println(" - TextBox.drawTextSelection ");
        }
        if (this.selEnd.getLineIndex()< 0) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        if(!this.hasSelectedText()) return;
        int startLine = this.selStart.getLineIndex();
        int endLine = this.selEnd.getLineIndex();
        int startColumnIndex = this.selStart.getColumnIndex();
        int endColumnIndex = this.selEnd.getColumnIndex();

        if (debug >0) {
            System.out.println("** TextBox.drawTextSelection selStart="
                    + this.selStart.toString() + ", selEnd=" + this.selEnd.toString());
        }
        AffineTransform currentTranform = null;
        Color currentColor = g2.getColor();
        int start, end;
        TextLayout[] textLayouts = this.lineBreaker.getTextLayouts();
        Point2D[] positions = this.lineBreaker.getTextLayoutPositions();
        if(endLine>textLayouts.length-1) endLine=textLayouts.length-1;
        for (int i = startLine; i <= endLine; i++) {
            int charCount = this.lineBreaker.getCharacterCount(i);
            start = 0;
            end = charCount;
            if (i == startLine) {
                start = startColumnIndex;
            }
            if (i == endLine) {
                end = endColumnIndex;
            }
            Shape highlight = null;
            try {
                highlight = textLayouts[i].getLogicalHighlightShape(start, end);
            } catch (Exception e) {
                String str = this.lineBreaker.getTextLayoutString(i);
                System.err.println("*** Error TextBox.drawTextSelection+"
                        + ", startLine, endLine=" + startLine + "," + endLine + ", start, end=" + start + "," + end
                        + ", str=" + str);
                System.out.println("    Exception e=" + e);
            }
            currentColor = g2.getColor();
            g2.setColor(Color.pink);
            currentTranform = g2.getTransform();
            g2.translate(positions[i].getX(), positions[i].getY());
            if (highlight != null) {
                g2.fill(highlight);
            }
            g2.setColor(currentColor);
            g2.setTransform(currentTranform);
            textLayouts[i].draw(g2, (float) positions[i].getX(),
                    (float) positions[i].getY());
            g2.setTransform(currentTranform);
        }
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

    public int getTextIndex() {
        int positionInText = -1;
        if (this.caretPosition.isValid()) {
            positionInText = this.caretPosition.getTextIndex();
        }
        return positionInText;
    }

    public boolean hasSelectedText() {
        int selStartLine = -1;
        int selStartColumn = -1;
        if (this.selStart.getLineIndex()>=0) {
            selStartLine = this.selStart.getLineIndex();
            selStartColumn = this.selStart.getColumnIndex();
        }
        int selEndLine = -1;
        int selEndColumn = -1;
        if (this.selStart.getLineIndex()>=0) {
            selEndLine = this.selEnd.getLineIndex();
            selEndColumn = this.selEnd.getColumnIndex();
        }
        boolean selectionSucceeded = selStartLine >= 0 && selEndLine >= 0
                && (selStartLine != selEndLine || selStartColumn != selEndColumn);
        return selectionSucceeded;
    }
    
    public int getSelectedTextStart() {
        int selStartLine = this.selStart.getLineIndex();
        int selStartColumn = this.selStart.getColumnIndex();
        int selEndLine = this.selEnd.getLineIndex();
        int selEndColumn = this.selEnd.getColumnIndex();
        boolean selectionSucceeded = selStartLine >= 0 && selEndLine >= 0
                && (selStartLine != selEndLine || selStartColumn != selEndColumn);
        int start = -1;
        if (selectionSucceeded) {
            start = this.lineBreaker.getTextIndex(selStartLine, selStartColumn);
            //start = this.selStart.getTextIndex();
        }
        return start;
    }

    public int getSelectedTextEnd() {
        int selStartLine = this.selStart.getLineIndex();
        int selStartColumn = this.selStart.getColumnIndex();
        int selEndLine = this.selEnd.getLineIndex();
        int selEndColumn = this.selEnd.getColumnIndex();
        boolean selectionSucceeded = selStartLine >= 0 && selEndLine >= 0
                && (selStartLine != selEndLine || selStartColumn != selEndColumn);
        int end = -1;
        if (selectionSucceeded) {
            end=this.lineBreaker.getTextIndex(selEndLine, selEndColumn);
            //end = this.selEnd.getTextIndex();
        }
        return end;
    }

    public AttributedString getSelectedText() {
        AttributedString attribString = null;
        int selStartLine = this.selStart.getLineIndex();
        int selStartColumn = this.selStart.getColumnIndex();
        int selEndLine = this.selEnd.getLineIndex();
        int selEndColumn = this.selEnd.getColumnIndex();
        boolean selectionSucceeded = selStartLine >= 0 && selEndLine >= 0
                && (selStartLine != selEndLine || selStartColumn != selEndColumn);
        if (selectionSucceeded) {
            int start = this.lineBreaker.getTextIndex(selStartLine, selStartColumn);
            int end = this.lineBreaker.getTextIndex(selEndLine, selEndColumn);
            attribString = this.committedTextContainer.getAttributedSubString(start, end);
        }
        return attribString;
    }

    public void mousePressed(MouseEvent e) {
        //System.out.println("TextBox.mousePressed");
        this.rightButton = false;
        if (e.isPopupTrigger()) {
            this.rightButton= true;
        }
        double scale = DrawParameters.getScale();
        double X = e.getX() / scale;
        double Y = e.getY() / scale;
        int iX = (int) X;
        int iY = (int) Y;
        //--------------------------------------------//
        this.displayMousePosition("mousePressed", X, Y);
        //--------------------------------------------//
/*
        //Check moving a boundary of textArea

        MousePositionInfo[] infos = ObjectTable.getMousePositionLS().getAllMousePositionInfo();
        for (int i = 0; i < infos.length; i++) {
            if (infos[i].getPosition() != MousePositionInfo.TEXT_BOX_BOUNDARY) {
                continue;
            }
            if (!infos[i].getShapeContainerInGroup().getShapeId().equals(this.getShapeContainer().getShapeId())) {
                continue;
            }
            this.mousePositionInfo = (MousePositionInfo) infos[i].clone();
            this.oldPoint = (Point2D) this.mousePositionInfo.getPoint().clone();
        }
        //Start moving a boundary of textArea
        if (this.mousePositionInfo != null
                && this.mousePositionInfo.getPosition()
                == MousePositionInfo.TEXT_BOX_BOUNDARY) {
            //ShapeContainer[] containers = new ShapeContainer[1];
            //containers[0] = this.getShapeContainer();
            this.modified = true;
            this.connectionUtil=new ConnectionUtil();
            ShapeContainer[] targetContainers=new ShapeContainer[1];
            targetContainers[0]=this.getShapeContainer();
            this.connectionUtil.setTargets(targetContainers);
            ShapeContainer[] containers=this.connectionUtil.getTargetsAndConnectors();
            //-----------------------------------------------------------//
            ObjectTable.getContainerManager().undoSetupStart(containers);
            //-----------------------------------------------------------//
            if (debug > 0) {
                System.out.println("TextBox.mousePressed MousePositionInfo="
                + this.mousePositionInfo.toString());
            }
            return;
        }
        //End moving a boundary of textArea
*/
        //Start text selection or mouse click
        if (contain(iX, iY) == 0) {
            return;
        }
        if (this.getComposedText() != null) {
            String message = " Unconverted text exists !"
                    + "\n Finish conversion !";
            JOptionPane.showMessageDialog(ObjectTable.getDrawMain(),
                    message, "", JOptionPane.INFORMATION_MESSAGE);
            //System.out.println("** Warning TextBox; "+message);
            return;
        }
        CaretPosition caretP = this.getCaretPositionAtMouse(X, Y, "mousePressed");
        this.dragStart=new CaretPosition(-1,-1);
        this.dragEnd=new CaretPosition(-1,-1);
        if (caretP.getLineIndex()>=0) {
            this.dragStart = caretP;
        }
        //End text selection or mouse click
    }

    public void mouseDragged(MouseEvent e) {
        //mouseStatusChanged("mouseDragged", e);
        double scale = DrawParameters.getScale();
        double X = e.getX() / scale;
        double Y = e.getY() / scale;
        int ctrl = 0;
        int key = e.getModifiersEx();
        if ((key & InputEvent.SHIFT_DOWN_MASK) != 0) {
            ctrl = 1;
        }
        if ((key & InputEvent.CTRL_DOWN_MASK) != 0) {
            ctrl = 2;
        }
        if ((key & InputEvent.ALT_DOWN_MASK) != 0) {
            ctrl = 3;
        }
        int iX = (int) X;
        int iY = (int) Y;

        //----------------------------------------------//
        this.displayMousePosition("mouseDragged", X, Y);
        //----------------------------------------------//
/*
        //Start moving a boundary of textArea
        if (this.mousePositionInfo != null
                && this.mousePositionInfo.getPosition() == MousePositionInfo.TEXT_BOX_BOUNDARY) {
            ShapeContainer shapeContainer = this.mousePositionInfo.getShapeContainerInGroup();
            Rectangle2D box = shapeContainer.getBoundingBox();
            //Rectangle2D newBox =new Rectangle2D.Double(0,0,0,0);
            String pos = this.mousePositionInfo.getInformation();
            this.newPoint = new Point2D.Double(X, Y);
            double x = this.textArea.getX();
            double y = this.textArea.getY();
            double w = this.textArea.getWidth();
            double h = this.textArea.getHeight();
            Rectangle2D oldTextArea=(Rectangle2D)this.textArea.clone();

            if (pos.equalsIgnoreCase("bottom")) {
                double delta=Y-this.oldPoint.getY();
                this.textArea.setRect(x, y+delta, w, h-delta);
            }
            if (pos.equalsIgnoreCase("top")) {
                double delta=Y-this.oldPoint.getY();
                this.textArea.setRect(x, y, w, h+delta);
            }
            if (pos.equalsIgnoreCase("left")) {
                double delta=X-this.oldPoint.getX();
                this.textArea.setRect(x+delta, y, w-delta, h);
            }
            if (pos.equalsIgnoreCase("right")) {
                double delta=X-this.oldPoint.getX();
                this.textArea.setRect(x, y, w+delta, h);
            }
            
            if (!box.contains(this.textArea)) {
                shapeContainer.getElement().moveResize(oldTextArea, this.textArea, false);
            }
            this.oldPoint = this.newPoint;
            this.validTextLayout = false;
            //connector resize option
            int connectorResizeOption = DrawParameters.AUTO_TRACKING_OPTION;
            this.connectionUtil.resizeConnectors(connectorResizeOption);
            ObjectTable.getDrawPanel("TextBox").repaint();
            return;
        }
        //End moving a boundary of textArea
*/
        //Start text selection or mouse click
        if (contain(iX, iY) == 0) {
            return;
        }
        if (this.getComposedText() != null) {
            return;
        }
        CaretPosition caretP = this.getCaretPositionAtMouse(X, Y, "mouseDragged");
        if (caretP.getLineIndex()>=0) {
            if (this.dragStart.getLineIndex()<0) {
                this.dragStart = caretP;
                this.dragEnd=new CaretPosition(-1, -1);
            }else{
                this.dragEnd = caretP;
                this.setTextSelection(this.dragStart, this.dragEnd, ctrl, "mouseDragged");
            }
        }
        //End text selection or mouse click
        DrawPanel drawPanel = ObjectTable.getDrawPanel();
        drawPanel.repaint();
    } //mouseDragged

    public void mouseReleased(MouseEvent e) {
        //mouseStatusChanged("mouseReleased", e);
        if (e.isPopupTrigger()) {
            this.rightButton= true;
        }
        double scale = DrawParameters.getScale();
        double X = e.getX() / scale;
        double Y = e.getY() / scale;
        int ctrl = 0;
        int key = e.getModifiersEx();
        if ((key & InputEvent.SHIFT_DOWN_MASK) != 0) {
            ctrl = 1;
        }
        if ((key & InputEvent.CTRL_DOWN_MASK) != 0) {
            ctrl = 2;
        }
        if ((key & InputEvent.ALT_DOWN_MASK) != 0) {
            ctrl = 3;
        }
/*
        //Start moving a boundary of textArea
        this.mousePositionInfo = null;
        this.newPoint = null;
        if (this.modified) {
            this.modified = false;
            ShapeContainer container = this.getShapeContainer();
            container.setChangeCode(UndoConstants.CONTAINER);
            //System.out.println("undoSetupEnd move TextBoundary "
            //     + container.toShortString());
            this.connectionUtil.end();
            //-----------------------------------------------//
            ObjectTable.getContainerManager().undoSetupEnd();
            //------------------------------------------------//
        }
        
        //End moving a boundary of textArea
*/
        //Start text selection or mouse click
        CaretPosition caretP = this.getCaretPositionAtMouse(X, Y, "mouseReleased");
        if (caretP.getLineIndex()>=0&& this.dragEnd.getLineIndex()>=0) {
            this.setTextSelection(this.dragStart, caretP, ctrl, "mouseReleased");
        }
        if(debug>0){
            String str="TextBox.mouseReleased text selection end "
                +", selStart="+this.selStart.toString()
                +", selEnd="+this.selEnd.toString()+"\n";
            if(this.selStart.getLineIndex()>=0&&this.selEnd.getLineIndex()>=0){
                int start=this.selStart.getLineIndex();
                int end=this.selEnd.getLineIndex();
                for(int i=start;i<=end;i++){
                    str+=" line["+i+"]="+this.lineBreaker.getTextLayoutString(i).replace("\n", "\\n")+"\n";
                }
            }
            System.out.println(str);
        }
        //End text selection or mouse click
        //------------------------------------------//
          displayMousePosition("mouseReleased", X, Y);
        //------------------------------------------//
        DrawPanel drawPanel = ObjectTable.getDrawPanel();
        drawPanel.repaint();
    }

    public void mouseClicked(MouseEvent e) {
        double scale = DrawParameters.getScale();
        double X = e.getX() / scale;
        double Y = e.getY() / scale;
        int ctrl = 0;
        int key = e.getModifiersEx();
        if ((key & InputEvent.SHIFT_DOWN_MASK) != 0) {
            ctrl = 1;
        }
        if ((key & InputEvent.CTRL_DOWN_MASK) != 0) {
            ctrl = 2;
        }
        if ((key & InputEvent.ALT_DOWN_MASK) != 0) {
            ctrl = 3;
        }
        int iX = (int) X;
        int iY = (int) Y;
        int result = contain(iX, iY);
        if (result == 0) {
            return;
        }
        if (this.getComposedText() != null) {
            return;
        }
        ListenerPanel listenerPanel = ObjectTable.getListenerPanel();
        listenerPanel.requestFocus();
        CaretPosition caretP = this.getCaretPositionAtMouse(X, Y, "mouseClicked");
        if (caretP.getLineIndex()>=0) {
            if(!this.rightButton) {
               this.setTextSelection(caretP, new CaretPosition(-1,-1), ctrl, "mouseClicked");
            }
        } else {
            if (!this.caretPosition.isValid()) {
                if (this.getComposedText() != null) {
                    System.err.println("*** Warning: TextBox mouseClicked "
                            + ", caretPosition set to (0,0,0), caretP=null");
                }
                this.caretPosition = new CaretPosition(0, 0, 0);
            }
        } //else

        //------------------------------------------//
        displayMousePosition("mouseClicked", X, Y);
        //------------------------------------------//
    } //mouseClicked

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public CaretPosition getCaretPositionAtMouse(double X, double Y, String callFrom) {
        TextLayout[] textLayouts = this.lineBreaker.getTextLayouts();
        Point2D[] positions = this.lineBreaker.getTextLayoutPositions();
        Rectangle2D[] bounds = this.lineBreaker.getBounds();
        Rectangle2D bound = null;
        int lineIndex = -1;
        float clickX = 0f;
        float clickY = 0f;
        for (int i = 0; i < textLayouts.length; i++) {
            bound = bounds[i];
            clickX = (float) (X - positions[i].getX());
            clickY = (float) (Y - positions[i].getY());
            if (Y > bound.getY() && Y < bound.getY() + bound.getHeight()) {
                lineIndex = i;
                break;
            }
        } //End of for
        CaretPosition mouseP=new CaretPosition(-1, -1);
        if (lineIndex >= 0) {
            TextHitInfo hitInfo = textLayouts[lineIndex].hitTestChar(clickX, clickY);
            int columnIndex = hitInfo.getInsertionIndex();
            mouseP=CaretPosition.getCaretPosition(lineIndex, columnIndex, false, 
                    this.lineBreaker, "getCaretPositionAtMouse");
            //mouseP=new CaretPosition(lineIndex, columnIndex);
        }
        if (debug >0) {
            System.out.println("*** getCaretPositionAtMouse callFrom="+callFrom
                        +", mouseP="+mouseP.toString());
        }
        return mouseP;
    }

    public void setTextSelection(CaretPosition mousePos1, CaretPosition mousePos2, 
            int ctrl, String callFrom) {
        if (debug >0) {
            System.out.println("*** setTextSelection ctrl=" + ctrl+", callFrom="+callFrom
                + ", mousePos1: "+mousePos1.toString()+ ", mousePos2: "+mousePos2.toString());
        }
        CaretPosition mouseP1 = new CaretPosition(-1,-1);
        CaretPosition mouseP2 = new CaretPosition(-1,-1);
        if (mousePos2 == null||mousePos2.getLineIndex()<0) {
            if (ctrl == 0) {
              // clicked 
                this.clickedPoint = mousePos1;
                this.caretPosition = CaretPosition.getCaretPosition(mousePos1.getLineIndex(), 
                        mousePos1.getColumnIndex(), true, this.lineBreaker, "setTextSelection");
                this.resetSelection(); 
            } else {
              //selected 
                mouseP1 = this.clickedPoint;
                mouseP2 = mousePos1;
            }
        } else {
          // selected 
            mouseP1 = mousePos1;
            mouseP2 = mousePos2;
        }
        
        if (mouseP1.getLineIndex()>=0&& mouseP2.getLineIndex()>=0) {
          // selected   
            int line1 = mouseP1.getLineIndex();
            int column1 =mouseP1.getColumnIndex();
            int line2 = mouseP2.getLineIndex();
            int column2 = mouseP2.getColumnIndex();
            if (line1 < line2 || line1 == line2 && column1 < column2) {
                this.selStart = mouseP1;
                this.selEnd = mouseP2;
                this.resetClickedPoint();
                this.resetCaretPosition();
            } else if (line1 > line2 || line1 == line2 && column1 > column2) {
                this.selStart = mouseP2;
                this.selEnd = mouseP1;
                this.resetClickedPoint();
                this.resetCaretPosition();
            } else if (line1 == line2 && column1 == column2) {
                this.clickedPoint = mouseP1;
                this.caretPosition = CaretPosition.getCaretPosition(mouseP1.getLineIndex(), 
                        mouseP1.getColumnIndex(), true, this.lineBreaker, "setTextSelection");
                this.resetSelection();
            }
        }
        if(debug>0){
            if(this.selStart.getLineIndex()>=0&&this.selEnd.getLineIndex()>=0){
                System.out.println("*** setTextSelection selected"
                    +", callFrom="+callFrom
                    +", start: "+this.selStart.toString()
                    +", end: "+this.selEnd.toString());
            } else {
                System.out.println("*** setTextSelection clicked"+", callFrom="+callFrom
                    + ", clickedPoint: "+this.clickedPoint.toString());
            }
        }
        FontStyle fontStyle = FontStyle.setFontStyleToMenu(this, callFrom);
        //this.setCurrentFontStyle(fontStyle, "setCaretPositionOrSelection");
    }
    public void resetCaretPosition() {
        this.caretPosition = new CaretPosition(0, 0, 0, true);
    }

    public void resetClickedPoint() {
        this.clickedPoint=new CaretPosition(-1,-1);
    }

    public void resetSelection() {
        this.selStart=new CaretPosition(-1,-1);
        this.selEnd=new CaretPosition(-1,-1);
    }
    
    private void displayMousePosition(String mouseAction, double X, double Y) {
        StatusPanel statusPanel = ObjectTable.getStatusPanel();
        if (mouseAction==null||mouseAction.equals("") || mouseAction.equals("stop")) {
            statusPanel.showText(1, "");
            return;
        }
        String caret = ",  caret position=";
        int textIndex = -1;
        if (this.caretPosition != null) {
            textIndex = this.caretPosition.getTextIndex();
        }
        if (textIndex < 0) {
            caret += "null";
        } else {
            caret += +textIndex;
        }
        String textSelection = ",  text selection";
        int start = -1;
        int end = -1;
        if (this.selStart.getLineIndex()>=0) {
            start = this.getSelectedTextStart();
        }
        if (this.selEnd.getLineIndex()>=0) {
            end = this.getSelectedTextEnd();
        }
        if (start < 0) {
            textSelection += "  start=null";
        } else {
            textSelection += "  start=" + start;
        }
        if (end < 0) {
            textSelection += ", end=null";
        } else {
            textSelection += ", end=" + end;
        }
        String mousePosition = " Text box :  mouse action=" + mouseAction
                + ",  x,y=" + (int) X + "," + (int) Y + caret + textSelection
                + ",  shape=" + this.getShapeContainer().getShapeId();
        statusPanel.showText(1, mousePosition);
    }

    private int contain(int iX, int iY) {
        Rectangle2D realTextArea = this.getMarginlessTextArea();
        int ix = (int) realTextArea.getX();
        int iy = (int) realTextArea.getY();
        int iw = (int) realTextArea.getWidth();
        int ih = (int) realTextArea.getHeight();
        int ret=0;
        if (iX >=ix&&iX<=ix + iw&&iY>=iy&&iY<=iy+ih) ret=1;
        return ret;
    }

    public Object clone() {
        TextBox textBox = new TextBox();
        Rectangle2D rect = this.getTextArea();
        Rectangle2D textArea = new Rectangle2D.Double(rect.getX(), rect.getY(),
                rect.getWidth(), rect.getHeight());
        textBox.setTextArea(textArea);
        Insets insets = (Insets) this.getTextBoxInsets().clone();
        textBox.setTextBoxInsets(insets);
        textBox.setTextLineSpace(this.getTextLineSpace());
        textBox.setTextAlign(this.getTextAlign());
        AttributedCharacterIterator iterator = this.getCommittedTextContainer().getCommittedText();
        textBox.getCommittedTextContainer().setCommittedText(iterator);
        return textBox;
    }
    
}//Ebd of TextBox 

