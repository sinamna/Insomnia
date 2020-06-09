package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import model.*;
/**
 * panel for request
 */
public class RequestPanel extends JPanel {
    private Request request;
    private JSplitPane splitPane;
    private boolean requestSent;
    private JList<Request> requestJList;

    /**
     * constructs panel for given request
     * @param request the request which is panel for
     * @param model the JList which requests are stored in
     */
    public RequestPanel(Request request, JList<Request> model) {
        //setting panel's attributes
        setLayout(new BorderLayout());
        this.request = request;
        requestSent = false;
        this.requestJList=model;
        setPreferredSize(new Dimension(370, 550));
        setMinimumSize(new Dimension(100, 400));

        //creating upper panel
        UpperPanel upperPanel = new UpperPanel();
        //creating centerPanel
        CenterPanel centerPanel = new CenterPanel();

        //adding components to panels
        add(upperPanel,BorderLayout.NORTH);
        add(centerPanel,BorderLayout.CENTER);
    }

    /**
     * the upper panel of request panel which contains a check box a textField and 2 buttons
     *
     */
    private class UpperPanel extends JPanel {
        String[] options;
        private JComboBox methodList;
        JTextField urlText;
        JButton sendBtn;
        private JButton delBtn;

        /**
         * constructs upperPanel
         */
        public UpperPanel() {
            //setting panel's attributes
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setMaximumSize(new Dimension(1000, 40));

            //creating comboBox
            options = new String[]{"GET", "DELETE", "POST", "PUT", "PATCH"};
            methodList = new JComboBox(options);
            methodList.setPreferredSize(new Dimension(methodList.getPreferredSize().width,methodList.getPreferredSize().height+15));
            methodList.addActionListener(new ActionListener() {
                /**
                 * triggers when an item is chosen in combo box
                 * sets the request option
                 * updates the list
                 * @param e the action event
                 */
                @Override
                public void actionPerformed(ActionEvent e) {
                    String option = (String) methodList.getSelectedItem();
                    request.setMethod(option);
                    requestJList.updateUI();
                }
            });
            String option=request.getMethod();
            methodList.setSelectedIndex(findSelectedIndex(option));

            //creating textField for url
            urlText = new JTextField("Url");
            urlText.setForeground(Color.gray);
            urlText.addFocusListener(new FocusAdapter() {
                /**
                 * sets the request's url whenever focus is lost
                 * @param e focusEvent
                 */
                @Override
                public void focusLost(FocusEvent e) {
                    request.setUrl(urlText.getText());
                }
            });
            urlText.addFocusListener(new AddWaterMark("Url"));
            urlText.setMinimumSize(new Dimension(100, 40));

            //creating button for sending request
            sendBtn = new JButton("Send");
            sendBtn.addActionListener(new SendBtnHandler());
            sendBtn.setMinimumSize(new Dimension(30, 30));

            //creating button for deleting request
            delBtn=new JButton("Delete");
            delBtn.setMinimumSize(sendBtn.getMinimumSize());
            delBtn.addActionListener(new DelBtnHandler());

            //adding components to the panel
            add(methodList);
            add(urlText);
            //adding a panel containing send button and delete button
            add(new JPanel(){
                {
                    setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
                    add(sendBtn);
                    add(delBtn);
                }
            });
        }

        /**
         * creates listener for when send button is pressed
         */
        private class SendBtnHandler implements ActionListener {
            /**
             * creates response and update GUI to show it and set it as request's response
             * @param e action event
             */
            @Override
            public void actionPerformed(ActionEvent e) {

                SwingWorker<Void,Void> swingWorker=new SwingWorker<Void, Void>() {
                    private String response;
                    @Override
                    protected Void doInBackground(){
                        try{
                            String[] requestCommandLine=request.createCommandLine()
                                    .replaceAll("\\s{2,}", " ")//this code replaces 2 or more spaces with one space
                                    .trim()
                                    .split(" ");

                            response =Jurl.createHTTPConnection(requestCommandLine,true);
                        } catch (MalformedURLException | ErrorException ex) {
//                            ex.printStackTrace();
                            this.cancel(true);
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
//                        super.done();
                        if(!isCancelled()){
                            try{
                                Response newResponse = new Response(response);
                                request.setResponse(newResponse);
                                splitPane.setRightComponent(newResponse.getResponsePanel());
                                splitPane.updateUI();
                                requestSent = true;
                            }catch (ErrorException ex){
                                //do nothing
                            }

                        }
                    }
                };
                swingWorker.execute();


            }
        }
        /**
         * listener for the time delete button is pressed
         */
        private class DelBtnHandler implements ActionListener{
            /**
             * deletes the request from list and reset the GUI
             * @param e action Event
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultListModel<Request> model= (DefaultListModel<Request>) requestJList.getModel();
                model.removeElement(request);
                splitPane.setRightComponent(MainFrame.createVoidPanel());
                splitPane.setLeftComponent(MainFrame.createVoidPanel());
                splitPane.updateUI();
                requestJList.updateUI();
            }
        }

        /**
         * finds the index of preSelected option in the options list
         * @param option option to be found
         * @return the index of option in the list
         */
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
    /**
     * the center panel which other panels can be navigated through
     */
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

        /**
         * constructs center panel with menu to choose and cardLayout panel
         * and adding listeners to items needed
         */
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

        /**
         * panel for creating headers
         */
        private class HeaderPanel extends JPanel {
            private HashMap<Info, InfoBox> headerList;

            /**
             * constructs panel with a map of info to headers
             */
            public HeaderPanel() {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                headerList = new HashMap<>();

                //initialize first info and headerBox
                Info firstHeader = new Info();
                InfoBox firstHeaderBox = new InfoBox(firstHeader);
                firstHeaderBox.setIsLast(true);
                addListeners(firstHeaderBox,headerList, HeaderPanel.this,"Header");
                headerList.put(firstHeader, firstHeaderBox);

                //add components to panel
                add(Box.createRigidArea(new Dimension(100, 20)));
                add(headerList.get(firstHeader));
            }

        }
        //----------------------------------------------------------------------------

        /**
         * add created listeners to components
         * @param infoBox the info Box shown in panel
         * @param list the map of view.Info and info boxes
         * @param parentPanel the parent panel
         * @param panelType the type of panel (Header or formData)
         */
        public void addListeners(InfoBox infoBox,HashMap<Info,InfoBox>list,JPanel parentPanel,String panelType) {
            //getting components from infoBox
            JTextField key = infoBox.getKey();
            JTextField value = infoBox.getValue();
            JButton delBtn = infoBox.getDelBtn();

            //adding listeners to them
            key.addFocusListener(new SaveToRequest());
            key.addMouseListener(new AutomaticAddingHandler(list,parentPanel,panelType));

            value.addFocusListener(new SaveToRequest());
            value.addMouseListener(new AutomaticAddingHandler(list,parentPanel,panelType));

            delBtn.addActionListener(new RemoveHandler(infoBox,list,parentPanel,panelType));
        }

        /**
         * a listener for text Fields to automatically add new view.InfoBox when clicking the last one
         */
        public class AutomaticAddingHandler extends MouseAdapter {
            private HashMap<Info,InfoBox>list;
            private JPanel parentPanel;
            private String type;

            /**
             * constructs listener with given parameters
             * @param list the list of info and infoBoxes
             * @param parentPanel the parent panel
             * @param type the type of parent panel
             */
            public AutomaticAddingHandler(HashMap<Info,InfoBox> list,JPanel parentPanel,String type){
                this.list=list;
                this.parentPanel=parentPanel;
                this.type=type;
            }

            /**
             * adds a new info Box at the end of the panel whenever clicking on the last one
             * @param e the MouseEvent
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                JTextField textField = (JTextField) e.getSource();
                InfoBox infoBox = (InfoBox) textField.getParent();

                parentPanel.grabFocus();
                textField.grabFocus();
                //checks if the box is last components in the panel
                if (infoBox.isLast()) {
                    infoBox.setIsLast(false);
                    Info newInfo = new Info();
                    InfoBox newInfoBox = new InfoBox(newInfo);
                    addListeners(newInfoBox,list,parentPanel,type);
                    newInfoBox.setIsLast(true);
                    list.put(newInfo, newInfoBox);
                    parentPanel.add(list.get(newInfo));
                    parentPanel.repaint();
                    parentPanel.revalidate();
                    parentPanel.updateUI();
                }
            }
        }

        /**
         * listener for Delete button
         */
        private class RemoveHandler implements ActionListener {
            private InfoBox infoBox;
            private HashMap<Info, InfoBox> list;
            private JPanel parentPanel;
            private String panelType;

            /**
             * constructs the list with given parameters
             * @param infoBox the infoBox intended to be removed
             * @param list the list of info and infoBoxes
             * @param parentPanel the parent panel
             * @param type the type of parent panel
             */
            public RemoveHandler(InfoBox infoBox, HashMap<Info, InfoBox> list, JPanel parentPanel,String type) {
                this.infoBox = infoBox;
                this.list = list;
                this.parentPanel = parentPanel;
                panelType=type;

            }

            /**
             * whenever user clicks the button
             * first it shows a warning dialog
             * and if user choose to delete it  it will
             * remove the view.Info from the list and will remove info box from the panel
             * and sets the previous box as the last one
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                int chosenOption = JOptionPane.showConfirmDialog(null, "Are you sure ?",
                        "WARNING", JOptionPane.YES_NO_OPTION);
                if (chosenOption == JOptionPane.YES_OPTION) {
                    //the least number of components includes one Box rigid area and one info Box =2
                    if(!(parentPanel.getComponentCount()==2)){
                        list.remove(infoBox.getInfo());
                        reformRequestList();
                        parentPanel.remove(infoBox);
                        InfoBox lastBox = (InfoBox) parentPanel.getComponent(parentPanel.getComponentCount() - 1);
                        lastBox.setIsLast(true);
                        parentPanel.repaint();
                        parentPanel.revalidate();
                    }else{
                        JOptionPane.showMessageDialog(null,"The only box can't be deleted.");
                    }
                }
            }

            /**
             * it goes throw the list in request object and remove each one that its info box is removed
             */
            private void reformRequestList(){
                ArrayList<Info> listToReform;
                if (panelType.equals("Header"))
                    listToReform=request.getHeaders();
                else
                    listToReform=request.getFormData();
                Iterator iter=listToReform.iterator();
                while(iter.hasNext()){
                    Info info=(Info)iter.next();
                    if(list.get(info)==null)
                        iter.remove();
                }
            }
        }

        /**
         * listener to add info boxes data to the request list
         */
        private class SaveToRequest extends FocusAdapter {
            /**
             * whenever a textFiled loses its focues it checks and if both fields are written \
             * it will add it to list in the request
             * @param e Focus event
             */
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

        /**
         * a panel with key and value pairs
         */
        private class FormData extends JPanel {
            private HashMap<Info, InfoBox> formDataList;
            public FormData(){
                //setting panel attribute
                setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

                // initialize the list
                formDataList=new HashMap<>();

                //adding first formData
                Info firstForm=new Info();
                InfoBox firstFormBox=new InfoBox(firstForm);
                firstFormBox.setIsLast(true);
                formDataList.put(firstForm,firstFormBox);
                addListeners(firstFormBox,formDataList,FormData.this,"FormData");

                //adding components to panel
                add(Box.createRigidArea(new Dimension(100,25)));
                add(formDataList.get(firstForm));
            }
        }

        //-----------------------------------------------------------------------------

        /**
         * a panel with editor in it for JSON mode
         */
        private class JsonPanel extends JPanel {
            private JEditorPane editor;
            private TextLineNumber lineNumber;

            public JsonPanel() {
                super(new BorderLayout());
                editor = new JEditorPane();
                editor.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        request.setJsonBody(editor.getText());
                    }
                });
                lineNumber = new TextLineNumber(editor);
                //adding components
                add(editor, BorderLayout.CENTER);
                add(lineNumber, BorderLayout.LINE_START);
            }

            public JEditorPane getEditor() {
                return editor;
            }
        }
    }

    //-------------------------------------------------------------------------------------

    /**
     * a panel for taking token and prefix
     */
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

        /**
         * creates panel in customized format with given parameters
         * @param panel the panel to be created
         * @param label the label to be added to panel
         * @param textField the textField to be added to panel
         * @param usage the usage of panel
         * @return the created panel
         */
        private JPanel panelMaker(JPanel panel, JLabel label, JTextField textField, String usage) {
            //creating panel
            panel = new JPanel();
            panel.setMaximumSize(new Dimension(1000, 30));
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            //creating label and textField
            label = new JLabel(usage, JLabel.CENTER);
            textField = new JTextField(usage);
            textField.setForeground(Color.WHITE);
            textField.addFocusListener(new AddWaterMark(usage));
            //adding components to panel
            panel.add(Box.createRigidArea(new Dimension(10, 30)));
            panel.add(label);
            panel.add(Box.createRigidArea(new Dimension(5, 30)));
            panel.add(textField);
            panel.add(Box.createRigidArea(new Dimension(10, 30)));
            return panel;
        }
    }

    /**
     * sets the split pane
     * @param splitPane split Pane to be set
     */
    public void setSplitPane(JSplitPane splitPane) {
        this.splitPane = splitPane;
    }

    /**
     * returns if request is send or not
     * @return state of request
     */
    public boolean isRequestSent() {
        return requestSent;
    }
}
