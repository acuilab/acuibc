package com.acuilab.bc.main.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.im.InputMethodRequests;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author 78429
 */
public class JEditPane extends JTextPane {
        
    /**
     * Gets the input method request handler which supports requests from input
     * methods for this component. A component that supports on-the-spot text
     * input must override this method to return an InputMethodRequests
     * instance. At the same time, it also has to handle input method events.
     *
     * @return
     */
    @Override
    public InputMethodRequests getInputMethodRequests() {
        return null;
    }
    
    /**
     * Applies the given font to character content. If there is a selection, the
     * font are applied to the selection range. If there is no selection, the
     * font are applied to the input attribute set which defines the font for
     * any new text that gets inserted.
     *
     * @param font
     */
    public void setFont4TextPane(Font font) {
        SimpleAttributeSet aset = new SimpleAttributeSet();

        StyleConstants.setFontFamily(aset, font.getFamily());
        StyleConstants.setFontSize(aset, font.getSize());

        if (font.isPlain()) {
            StyleConstants.setBold(aset, false);
            StyleConstants.setItalic(aset, false);
        }
        if (font.isBold()) {
            StyleConstants.setBold(aset, true);
        }
        if (font.isItalic()) {
            StyleConstants.setItalic(aset, true);
        }

        setCharacterAttributes(aset, false);
    }
    
    /**
     * Applies the given color to character content. If there is a selection,
     * the color are applied to the selection range. If there is no selection,
     * the attributes are applied to the input attribute set which defines the
     * color for any new text that gets inserted.
     *
     * @param color
     */
    public void setColor4TextPane(Color color) {
        SimpleAttributeSet aset = new SimpleAttributeSet();
        StyleConstants.setForeground(aset, color);
        this.setCharacterAttributes(aset, false);
    }
    
    /**
     * Get the color attribute from an AttributeSet.
     *
     * @param aset
     * @return
     */
    public static Color getColorFromAttributeSet(AttributeSet aset) {
        return StyleConstants.getForeground(aset);
    }

    /**
     * Get the font attribute from an AttributeSet.
     *
     * @param aset
     * @return
     */
    public static Font getFontFromAttributeSet(AttributeSet aset) {
        boolean bold = StyleConstants.isBold(aset);
        boolean italic = StyleConstants.isItalic(aset);
        String fontFamily = StyleConstants.getFontFamily(aset);
        int fontSize = StyleConstants.getFontSize(aset);

        if (bold && italic) {
            return new Font(fontFamily, Font.BOLD | Font.ITALIC, fontSize);
        } else if (bold) {
            return new Font(fontFamily, Font.BOLD, fontSize);
        } else if (italic) {
            return new Font(fontFamily, Font.ITALIC, fontSize);
        }

        return new Font(fontFamily, Font.PLAIN, fontSize);
    }

    /**
     * Get the AttributeSet using font and color attribute.
     *
     * @param font
     * @param color
     * @return
     */
    public static AttributeSet getAttributeSet(Font font, Color color) {
        SimpleAttributeSet aset = new SimpleAttributeSet();

        StyleConstants.setFontFamily(aset, font.getFamily());
        StyleConstants.setFontSize(aset, font.getSize());
        StyleConstants.setForeground(aset, color);

        if (font.isPlain()) {
            StyleConstants.setBold(aset, false);
            StyleConstants.setItalic(aset, false);
        }
        if (font.isBold()) {
            StyleConstants.setBold(aset, true);
        }
        if (font.isItalic()) {
            StyleConstants.setItalic(aset, true);
        }

        return aset;
    }
    
    /**
     * Insert a blank with current input attributes
     */
    public void appendEnter() throws BadLocationException {
        SimpleAttributeSet aset = new SimpleAttributeSet(this.getInputAttributes());
        getDocument().insertString(getDocument().getLength(), "\n", aset);
    }
}
