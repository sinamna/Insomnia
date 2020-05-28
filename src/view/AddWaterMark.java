package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * this class adds given watermark to textField when its not focused and removes
 * it when focus is on textField
 */
public class AddWaterMark implements FocusListener {
    private String waterMark;

    /**
     * constructs class with given string as waterMark
     * @param waterMark text which is shown in textField
     */
    public AddWaterMark(String waterMark){
        this.waterMark=waterMark;
    }

    /**
     * removes waterMark and set color as white when focused
     * @param e focus event used to get textField
     */
    @Override
    public void focusGained(FocusEvent e) {
        JTextField textField=(JTextField)e.getSource();
        if(textField.getText().equals(waterMark)){
            textField.setText("");
            textField.setForeground(Color.white);
        }
    }

    /**
     * adds waterMark and sets the color as gray
     * @param e focus event used to get textField
     */
    @Override
    public void focusLost(FocusEvent e) {
        JTextField textField=(JTextField)e.getSource();
        if(textField.getText().isEmpty()){
            textField.setText(waterMark);
            textField.setForeground(Color.GRAY);
        }
    }
}
