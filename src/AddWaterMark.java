import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class AddWaterMark implements FocusListener {
    private String waterMark;
    public AddWaterMark(String waterMark){
        this.waterMark=waterMark;
    }

    @Override
    public void focusGained(FocusEvent e) {
        JTextField textField=(JTextField)e.getSource();
        if(textField.getText().equals(waterMark)){
            textField.setText("");
            textField.setForeground(Color.white);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        JTextField textField=(JTextField)e.getSource();
        if(textField.getText().isEmpty()){
            textField.setText(waterMark);
            textField.setForeground(Color.GRAY);
        }
    }
}
