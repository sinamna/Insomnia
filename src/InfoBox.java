import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class InfoBox extends JPanel {
    private JTextField key;
    private JTextField value;
    private JButton delBtn;
    private JCheckBox checkBox;
    private boolean isLast;
    private Info info;

    public InfoBox(Info info) {
        super.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.info = info;

        //setting HeaderBox attributes
        setFocusable(true);
        setPreferredSize(new Dimension(300, 30));
        setMaximumSize(new Dimension(1000, 35));

        //initializing the textField for key
        key = new JTextField("Key");
        key.setForeground(Color.gray);
        key.setPreferredSize(new Dimension(110, 30));
        key.addFocusListener(new SaveInfoHandler());
        key.addFocusListener(new AddWaterMark("Key"));

        //initializing the textField for value
        value = new JTextField("Value");
        value.setForeground(Color.gray);
        value.setPreferredSize(new Dimension(110, 30));
        value.addFocusListener(new SaveInfoHandler());
        value.addFocusListener(new AddWaterMark("Value"));


        //initializing the combo box to represent state of header
        checkBox = new JCheckBox();
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (checkBox.isSelected()) {
                    info.setEnabled(true);
                } else {
                    info.setEnabled(false);
                }
            }
        });

        //initializing a button to delete a box
        delBtn = new JButton("DEL");
//        delBtn.addActionListener(new RemoveHandler());

        //adding components to panel
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(key);
        add(Box.createRigidArea(new Dimension(10, 0)));
        add(value);
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(checkBox);
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(delBtn);
        add(Box.createRigidArea(new Dimension(5, 0)));
    }

    public class SaveInfoHandler extends FocusAdapter {
        @Override
        public void focusLost(FocusEvent e) {
            try {
                JTextField textField = (JTextField) e.getSource();
                if (!textField.getText().isEmpty()) {
                    if (e.getSource() == key) {
                        info.setKey(textField.getText());
                    } else if (e.getSource() == value) {
                        info.setValue(textField.getText());
                    }
//                    System.out.println(info);
                }
            } catch (ClassCastException exception) {
            }
        }
    }

    public boolean isLast() {
        return isLast;
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }

    public JTextField getKey() {
        return key;
    }

    public JTextField getValue() {
        return value;
    }

    public JButton getDelBtn() {
        return delBtn;
    }

    public Info getInfo() {
        return info;
    }
}
