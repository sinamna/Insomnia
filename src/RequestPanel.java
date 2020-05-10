import Insomnia.TextLineNumber;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class RequestPanel extends JPanel {
    private Request request;
    private JSplitPane splitPane;
    private boolean requestSent;

    public RequestPanel(Request request) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.request = request;
        requestSent = false;
        setPreferredSize(new Dimension(370, 550));
        setMinimumSize(new Dimension(100, 400));
        UpperPanel upperPanel = new UpperPanel();
        add(upperPanel, BorderLayout.PAGE_START);
        CenterPanel centerPanel = new CenterPanel();
        add(centerPanel, BorderLayout.CENTER);
    }

    private class UpperPanel extends JPanel {
        String[] options;
        private JComboBox methodList;
        JTextField urlText;
        JButton sendBtn;

        public UpperPanel() {
            //setting panel's attributes
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setMaximumSize(new Dimension(1000, 40));
            setBackground(Color.WHITE);

            //creating comboBox
            options = new String[]{"GET", "DELETE", "POST", "PUT", "PATCH"};
            methodList = new JComboBox(options);
            methodList.setPreferredSize(new Dimension(70, 30));
            methodList.setMinimumSize(new Dimension(70, 40));
            methodList.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String option = (String) methodList.getSelectedItem();
                    request.setOption(option);
                    updateUI();
                }
            });

            //creating textField for url
            urlText = new JTextField("https://api.myproduct.com/v1/users");
            urlText.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    request.setUrl(urlText.getText());
                    System.out.println(request.getUrl());
                }
            });
            urlText.setMinimumSize(new Dimension(100, 40));

            //creating button for sending request
            sendBtn = new JButton("Send");
            sendBtn.addActionListener(new SendBtnHandler());
            sendBtn.setMinimumSize(new Dimension(30, 40));

            //adding components to the panel
            add(methodList);
            add(urlText);
            add(sendBtn);
        }

        private class SendBtnHandler implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                Response newResponse=new Response(request.getHeaders());
                splitPane.setRightComponent(newResponse.getResponsePanel());
                splitPane.updateUI();
                requestSent = true;
            }
        }
    }
//        private int findDefaultSelection(String option) {
//            int index = 0;
//            for (String currentOP : options) {
//                if (currentOP.equals(option))
//                    break;
//                index++;
//            }
//            return index;
//        }
//    }

    //------------------------------------------------------------------------------------------------
    private class CenterPanel extends JPanel {
        JMenuBar menuBar;
        JMenu bodyMenu;
        JMenu headerMenu;
        JMenu authMenu;
        JMenu query;
        CardLayout layout;
        JPanel mainPanel;
        JPanel formDataPanel;
        JPanel jsonPanel;
        JPanel headerPanel;
        JPanel bearer;
        JPanel queryPanel;

        public CenterPanel() {
            setLayout(new BorderLayout());

            //menubar is added to the north side of panel
            menuBar = new JMenuBar();
            menuBar.setMaximumSize(new Dimension(370, 40));
            add(menuBar, BorderLayout.NORTH);

            // creating main panel with card Layout
            mainPanel = new JPanel();
            layout = new CardLayout();
            mainPanel.setLayout(layout);
            add(mainPanel, BorderLayout.CENTER);

            //the body menu
            bodyMenu = new JMenu("Body");
            JMenuItem formDataItem = new JMenuItem("Form Data");
            formDataItem.addActionListener(new MenuHandler.MenuItemHandler(layout,mainPanel));
            JMenuItem jsonItem = new JMenuItem("JSON");
            jsonItem.addActionListener(new MenuHandler.MenuItemHandler(layout,mainPanel));
            bodyMenu.add(formDataItem);
            bodyMenu.add(jsonItem);
            menuBar.add(bodyMenu);

            //header Menu
            headerMenu = new JMenu("Header");
            headerMenu.addMenuListener(new MenuHandler.MenuSelectionHandler(layout,mainPanel));
            menuBar.add(headerMenu);

            //Auth Menu
            authMenu = new JMenu("Auth");
            JMenuItem bearerItem = new JMenuItem("Bearer");
            bearerItem.addActionListener(new MenuHandler.MenuItemHandler(layout,mainPanel));
            authMenu.add(bearerItem);
            menuBar.add(authMenu);

            //queryMenu
            query = new JMenu("Query");
            menuBar.add(query);



            //creating menu items panel
            formDataPanel = new FormData();
            jsonPanel = new JsonPanel();
            headerPanel = new HeaderPanel();
            bearer = new AuthPanel();

            //adding panels to mainPanel
            mainPanel.add(formDataPanel, "Form Data");
            mainPanel.add(jsonPanel, "JSON");
            mainPanel.add(new JScrollPane(headerPanel), "Header");
            mainPanel.add(bearer, "Bearer");
        }

        //---------------------------------------------------------------------
        private class HeaderPanel extends JPanel {
            private HashMap<HeaderInfo, JPanel> list;

            private class HeaderBox extends JPanel {
                private JTextField key;
                private JTextField value;
                private JButton delBtn;
                private JCheckBox checkBox;
                private boolean isLast;
                private HeaderInfo headerInfo;

                public HeaderBox(HeaderInfo headerInfo) {
                    super.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                    //setting HeaderBox attributes
                    setFocusable(true);
                    addFocusListener(new SaveDataHandler());
                    setPreferredSize(new Dimension(300, 30));
                    setMaximumSize(new Dimension(1000, 35));

                    //initializing the textField for key
                    key = new JTextField();
                    key.addMouseListener(new AutomaticAddingHandler());
                    key.addFocusListener(new SaveDataHandler());
                    key.setPreferredSize(new Dimension(110, 30));

                    //initializing the textField for value
                    value = new JTextField();
                    value.addMouseListener(new AutomaticAddingHandler());
                    value.setPreferredSize(new Dimension(110, 30));
                    value.addFocusListener(new SaveDataHandler());
                    //initializing the combo box to represent state of header
                    checkBox = new JCheckBox();
                    checkBox.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            if (checkBox.isSelected()) {

                                headerInfo.setEnabled(true);
                            } else {
                                System.out.println("true");
                                headerInfo.setEnabled(false);
                            }
                        }
                    });
                    //initializing a button to delete a header
                    delBtn = new JButton("DEL");
                    delBtn.addActionListener(new RemoveHandler());
                    add(Box.createRigidArea(new Dimension(5, 0)));
                    add(key);
                    add(Box.createRigidArea(new Dimension(10, 0)));
                    add(value);
                    add(Box.createRigidArea(new Dimension(5, 0)));
                    add(checkBox);
                    add(Box.createRigidArea(new Dimension(5, 0)));
                    add(delBtn);
                    add(Box.createRigidArea(new Dimension(5, 0)));
                    this.headerInfo = headerInfo;
                }

                public boolean isLast() {
                    return isLast;
                }

                public void setIsLast(boolean isLast) {
                    this.isLast = isLast;
                }

                private class RemoveHandler implements ActionListener {
                    /**
                     * Invoked when an action occurs.
                     *
                     * @param e
                     */
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int chosenOption = JOptionPane.showConfirmDialog(null, "Are you sure ?",
                                "WARNING", JOptionPane.YES_NO_OPTION);
                        if (chosenOption == JOptionPane.YES_OPTION) {
                            list.remove(headerInfo);
                            HeaderPanel.this.remove(HeaderBox.this);
                            HeaderBox lastBox = (HeaderBox) HeaderPanel.this.
                                    getComponent(HeaderPanel.this.getComponentCount() - 1);
                            lastBox.setIsLast(true);
                            HeaderPanel.this.repaint();
                            HeaderPanel.this.revalidate();
                        }
                        //there should be sth handle the situation which there is no header
                    }
                }

                private class SaveDataHandler extends FocusAdapter {
                    /**
                     * Invoked when a component loses the keyboard focus.
                     *
                     * @param e
                     */
                    @Override
                    public void focusLost(FocusEvent e) {
                        try{
                            JTextField textField = (JTextField) e.getSource();
                            if (!textField.getText().isEmpty()) {
                                if (e.getSource() == key) {
                                    headerInfo.setKey(textField.getText());
                                } else if (e.getSource() == value) {
                                    headerInfo.setValue(textField.getText());
                                }
                                if (headerInfo.isCompleted()) {
                                    request.addHeaderInfo(headerInfo);
                                    System.out.println(request.getHeaders());
                                }
                            }
                        }catch (ClassCastException exception){

                        }

                    }
                }

            }

            public HeaderPanel() {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                add(Box.createRigidArea(new Dimension(100, 10)));
                list = new HashMap<>();
                HeaderInfo firstHeader = new HeaderInfo();
                HeaderBox firstHeaderBox = new HeaderBox(firstHeader);
                firstHeaderBox.setIsLast(true);
                list.put(firstHeader, firstHeaderBox);
                add(list.get(firstHeader));
            }

            private class AutomaticAddingHandler extends MouseAdapter {
                /**
                 * {@inheritDoc}
                 *
                 * @param
                 */
                @Override
                public void mouseClicked(MouseEvent e) {
                    JTextField textField = (JTextField) e.getSource();
                    textField.grabFocus();
                    HeaderBox header = (HeaderBox) textField.getParent();
                    if (header.isLast()) {
                        header.setIsLast(false);
                        HeaderInfo newHeader = new HeaderInfo();
                        HeaderBox newHeaderBox = new HeaderBox(newHeader);
                        newHeaderBox.setIsLast(true);
                        list.put(newHeader, newHeaderBox);
                        add(list.get(newHeader));
                        repaint();
                        revalidate();
                    }
                }
            }


        }

        //-----------------------------------------------------------------------------
        private class FormData extends JPanel {

        }

        private class JsonPanel extends JPanel {
            private JEditorPane editor;
            private TextLineNumber lineNumber;

            public JsonPanel() {
                super(new BorderLayout());
                editor = new JEditorPane();
                add(editor, BorderLayout.CENTER);
                lineNumber = new TextLineNumber(editor);
                add(lineNumber, BorderLayout.LINE_START);
            }

            public JEditorPane getEditor() {
                return editor;
            }
        }
    }

    //-------------------------------------------------------------------------------------
    private class AuthPanel extends JPanel {
        private JLabel tokenLabel;
        private JLabel prefixLabel;
        private JLabel stateLabel;
        private JTextField tokenField;
        private JTextField prefixField;
        private JCheckBox enabledStatus;
        private JPanel tokenHolder;
        private JPanel prefixHolder;
        private JPanel stateHolder;

        public AuthPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            // making panel to hold token label and textField
            tokenHolder = panelMaker(tokenHolder, tokenLabel, tokenField, "Token");
            //creating panel and its components for prefix
            prefixHolder = panelMaker(prefixHolder, prefixLabel, prefixField, "Prefix");
            //creating panel for check box
            stateHolder = new JPanel();
            stateHolder.setLayout(new BoxLayout(stateHolder, BoxLayout.X_AXIS));
            enabledStatus = new JCheckBox();
            enabledStatus.setSelected(true);
            stateLabel = new JLabel("Enabled", JLabel.CENTER);
            stateHolder.add(Box.createRigidArea(new Dimension(10, 30)));
            stateHolder.add(stateLabel);
            stateHolder.add(Box.createRigidArea(new Dimension(5, 30)));
            stateHolder.add(enabledStatus);
            //adding components to auth panel
            add(Box.createRigidArea(new Dimension(1000, 20)));
            add(tokenHolder);
            add(prefixHolder);
            add(stateHolder);
        }

        private JPanel panelMaker(JPanel panel, JLabel label, JTextField textField, String usage) {
            panel = new JPanel();
            panel.setMaximumSize(new Dimension(1000, 30));
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            label = new JLabel(usage, JLabel.CENTER);
            textField = new JTextField(JTextField.CENTER);
            panel.add(Box.createRigidArea(new Dimension(10, 30)));
            panel.add(label);
            panel.add(Box.createRigidArea(new Dimension(5, 30)));
            panel.add(textField);
            panel.add(Box.createRigidArea(new Dimension(10, 30)));
            return panel;
        }
    }

    public void setSplitPane(JSplitPane splitPane) {
        this.splitPane = splitPane;
    }

    public boolean isRequestSent() {
        return requestSent;
    }
}
