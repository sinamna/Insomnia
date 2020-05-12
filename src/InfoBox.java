import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

/**
 * a panel with key-valued feature and textFields and arranged components
 * with the ability to save its data to Info obj
 */
public class InfoBox extends JPanel {
    private JTextField key;
    private JTextField value;
    private JButton delBtn;
    private JCheckBox checkBox;
    private boolean isLast;
    private Info info;

    /**
     * constructs a panel with key&value textfields and data saver feature
     * @param info the info used to store data
     */
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
        //sets the boolean variable in info object based on check box's state
        checkBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                info.setEnabled(checkBox.isSelected());
            }
        });

        //initializing a button to delete a box
        delBtn = new JButton("DEL");//the actionListener related to deleting infoBoxed will be added later


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

    /**
     * saves the textFields text into Info String fields whenever loses focus
     */
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
                }
            } catch (ClassCastException exception) {
            }
        }
    }

    /**
     * returns the state of the box in container
     * @return
     */
    public boolean isLast() {
        return isLast;
    }

    /**
     * sets the state of the box in container
     * @param isLast the state to be set
     */
    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }

    /**
     *
     * @return the key textField
     */
    public JTextField getKey() {
        return key;
    }

    /**
     *
     * @return the value TextField
     */
    public JTextField getValue() {
        return value;
    }

    /**
     *
     * @return the delete button
     */
    public JButton getDelBtn() {
        return delBtn;
    }

    /**
     *
     * @return the info which this box used to store data
     */
    public Info getInfo() {
        return info;
    }
}
