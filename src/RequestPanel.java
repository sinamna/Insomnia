import Insomnia.TextLineNumber;
import com.sun.xml.internal.ws.api.message.Header;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class RequestPanel extends JPanel {
    private Request request;
    private JSplitPane splitPane;
    private boolean requestSent;

    public RequestPanel(Request request) {
        setLayout(new BorderLayout());
        this.request = request;
        requestSent = false;
        setPreferredSize(new Dimension(370, 550));
        setMinimumSize(new Dimension(100, 400));
        UpperPanel upperPanel = new UpperPanel();
        add(upperPanel,BorderLayout.NORTH);
        CenterPanel centerPanel = new CenterPanel();
        add(centerPanel,BorderLayout.CENTER);
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
//            methodList.setPreferredSize(new Dimension(70, 30));
//            methodList.setMinimumSize(new Dimension(70, 40));
            methodList.setPreferredSize(new Dimension(methodList.getPreferredSize().width,methodList.getPreferredSize().height+15));
            methodList.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String option = (String) methodList.getSelectedItem();
                    request.setOption(option);
                    updateUI();
                }
            });
            String option=request.getOption();
            methodList.setSelectedIndex(findSelectedIndex(option));

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
                Response newResponse = new Response(request.getHeaders());
                request.setResponse(newResponse);
                splitPane.setRightComponent(newResponse.getResponsePanel());
                splitPane.updateUI();
                requestSent = true;
            }
        }

        private int findSelectedIndex(String option){
            int found = 0;
            for(int i=0;i<options.length;i++){
                if (options[i].equals(option))
                    found=i;
            }
            return found;
        }
    }

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
            menuBar.setPreferredSize(new Dimension(menuBar.getPreferredSize().width,menuBar.getPreferredSize().height+30));
            add(menuBar, BorderLayout.NORTH);

            // creating main panel with card Layout
            mainPanel = new JPanel();
            layout = new CardLayout();
            mainPanel.setLayout(layout);
            add(mainPanel, BorderLayout.CENTER);

            //the body menu
            bodyMenu = new JMenu("Body");
            JMenuItem formDataItem = new JMenuItem("Form Data");
            formDataItem.addActionListener(new MenuHandler.MenuItemHandler(layout, mainPanel));
            JMenuItem jsonItem = new JMenuItem("JSON");
            jsonItem.addActionListener(new MenuHandler.MenuItemHandler(layout, mainPanel));
            bodyMenu.add(formDataItem);
            bodyMenu.add(jsonItem);
            menuBar.add(bodyMenu);

            //header Menu
            headerMenu = new JMenu("Header");
            headerMenu.addMenuListener(new MenuHandler.MenuSelectionHandler(layout, mainPanel));
            menuBar.add(headerMenu);

            //Auth Menu
            authMenu = new JMenu("Auth");
            JMenuItem bearerItem = new JMenuItem("Bearer");
            bearerItem.addActionListener(new MenuHandler.MenuItemHandler(layout, mainPanel));
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
            mainPanel.add(new JScrollPane(formDataPanel), "Form Data");
            mainPanel.add(jsonPanel, "JSON");
            mainPanel.add(new JScrollPane(headerPanel), "Header");
            mainPanel.add(bearer, "Bearer");
        }

        //---------------------------------------------------------------------
        private class HeaderPanel extends JPanel {
            private HashMap<Info, InfoBox> headerList;

            public HeaderPanel() {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                headerList = new HashMap<>();

                //initialize first info and headerBox
                Info firstHeader = new Info();
                InfoBox firstHeaderBox = new InfoBox(firstHeader);
                firstHeaderBox.setIsLast(true);
                addListeners(firstHeaderBox,headerList, HeaderPanel.this);
                headerList.put(firstHeader, firstHeaderBox);

                //add components to panel
                add(Box.createRigidArea(new Dimension(100, 10)));
                add(headerList.get(firstHeader));
            }

            public HashMap<Info, InfoBox> getHeaderList() {
                return headerList;
            }
        }
        //----------------------------------------------------------------------------
        public void addListeners(InfoBox infoBox,HashMap<Info,InfoBox>list,JPanel parentPanel) {
            //getting components from infoBox
            JTextField key = infoBox.getKey();
            JTextField value = infoBox.getValue();
            JButton delBtn = infoBox.getDelBtn();

            //adding listeners to them
            key.addFocusListener(new SaveToRequest());
            key.addMouseListener(new AutomaticAddingHandler(list,parentPanel));

            value.addFocusListener(new SaveToRequest());
            value.addMouseListener(new AutomaticAddingHandler(list,parentPanel));

            delBtn.addActionListener(new RemoveHandler(infoBox,list,parentPanel));
        }

        public class AutomaticAddingHandler extends MouseAdapter {
            private HashMap<Info,InfoBox>list;
            private JPanel parentPanel;
            public AutomaticAddingHandler(HashMap<Info,InfoBox> list,JPanel parentPanel){
                this.list=list;
                this.parentPanel=parentPanel;
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                JTextField textField = (JTextField) e.getSource();
                InfoBox infoBox = (InfoBox) textField.getParent();

                parentPanel.grabFocus();
                textField.grabFocus();

                if (infoBox.isLast()) {
                    infoBox.setIsLast(false);
                    Info newInfo = new Info();
                    InfoBox newInfoBox = new InfoBox(newInfo);
                    addListeners(newInfoBox,list,parentPanel);
                    newInfoBox.setIsLast(true);
                    list.put(newInfo, newInfoBox);
                    parentPanel.add(list.get(newInfo));
                    parentPanel.repaint();
                    parentPanel.revalidate();
                    parentPanel.updateUI();
                }
            }
        }

        private class RemoveHandler implements ActionListener {
            private InfoBox infoBox;
            private HashMap<Info, InfoBox> list;
            private JPanel parentPanel;

            public RemoveHandler(InfoBox infoBox, HashMap<Info, InfoBox> list, JPanel parentPanel) {
                this.infoBox = infoBox;
                this.list = list;
                this.parentPanel = parentPanel;

            }
            @Override
            public void actionPerformed(ActionEvent e) {
                int chosenOption = JOptionPane.showConfirmDialog(null, "Are you sure ?",
                        "WARNING", JOptionPane.YES_NO_OPTION);
                if (chosenOption == JOptionPane.YES_OPTION) {
                    //the least number of components includes one Box rigid area and one info Box =2
                    if(!(parentPanel.getComponentCount()==2)){
                        list.remove(infoBox.getInfo());
                        parentPanel.remove(infoBox);
                        InfoBox lastBox = (InfoBox) parentPanel.getComponent(parentPanel.getComponentCount() - 1);
                        lastBox.setIsLast(true);
                        parentPanel.repaint();
                        parentPanel.revalidate();
                    }else{
                        JOptionPane.showMessageDialog(null,"The only header can't be deleted.");
                    }

                }
                //there should be sth handle the situation which there is no header ->        serious BUG shit
            }
        }

        private class SaveToRequest extends FocusAdapter {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField textField = (JTextField) e.getSource();
                InfoBox infoBox = (InfoBox) textField.getParent();
                if (infoBox.getInfo().isCompleted())
                    if(infoBox.getParent() instanceof HeaderPanel)
                        request.addHeaderInfo(infoBox.getInfo());
                    else
                        request.addDataInfo(infoBox.getInfo());
            }
        }
        //-----------------------------------------------------------------------------
        private class FormData extends JPanel {
            private HashMap<Info, InfoBox> formDataList;
            public FormData(){
                //setting panel attribute
                setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

                formDataList=new HashMap<>();

                //adding first formData
                Info firstForm=new Info();
                InfoBox firstFormBox=new InfoBox(firstForm);
                firstFormBox.setIsLast(true);
                formDataList.put(firstForm,firstFormBox);
                addListeners(firstFormBox,formDataList,FormData.this);

                //adding components to panel
                add(Box.createRigidArea(new Dimension(100,25)));
                add(formDataList.get(firstForm));
            }
        }

        //-----------------------------------------------------------------------------
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
