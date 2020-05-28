package view;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import model.*;
/**
 * a panel with logo and a list of requests
 */
public class RequestListPanel extends JPanel {
    JSplitPane reqAndResponseSplit;

    /**
     * constructs panel with given split pane which is used in Main Frame
     * @param reqAndResponseSplit the split pane
     */
    public RequestListPanel(JSplitPane reqAndResponseSplit) {
        //set panel's attributes
        super(new BorderLayout());
        setPreferredSize(new Dimension(300, 470));
        setMinimumSize(new Dimension(200, 400));
        this.reqAndResponseSplit = reqAndResponseSplit;

        //create panel for logo
        JPanel namePanel = new JPanel(new BorderLayout());

        //create label and set its attributes
        JLabel programName = new JLabel("Insomnia");
        programName.setHorizontalAlignment(JLabel.CENTER);
        programName.setPreferredSize(new Dimension(programName.getPreferredSize().width
                ,programName.getPreferredSize().height+30));
        programName.setOpaque(true);
        programName.setForeground(Color.WHITE);
        programName.setBackground(new Color(124, 84, 145));
        programName.setFont(new Font("Palatino",Font.BOLD,20));
        //adding logo to name panel
        namePanel.add(programName, BorderLayout.CENTER);

        //creating list Panel
        ListPanel listPanel = new ListPanel();
        //adding components to the panel
        add(namePanel, BorderLayout.PAGE_START);
        add(new JScrollPane(listPanel), BorderLayout.CENTER);
    }
//----------------------------------------------------------------------------
    private class ListPanel extends JPanel {
        JButton addRequestBtn;
        DefaultListModel<Request> requestsModel;
        JList<Request> list;

        public ListPanel() {
            super(new BorderLayout());
            setPreferredSize(new Dimension(120,350));
            // add request Button
            addRequestBtn = new JButton("Add model.Request");
            addRequestBtn.addActionListener(new NewRequestHandler());
//            addRequestBtn.setPreferredSize(new Dimension(100,30));

            // the list of requests
            requestsModel = new DefaultListModel<>();
            list = new JList<>(requestsModel);
            list.setCellRenderer(new ListItemRenderer());
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.addListSelectionListener(new ChosenRequestHandler());

            //adding components to the panel
            add(addRequestBtn, BorderLayout.NORTH);
            add(new JScrollPane(list), BorderLayout.CENTER);
        }

    /**
     * creates a dialog when button is pressed and takes the information for new request
     */
    private class NewRequestHandler extends KeyAdapter implements ActionListener {
            private JComboBox<String> methodOptions;
            private String[] options;
            private JDialog addDialog;
            private JPanel addPanel;
            private JTextField requestNameField;
            private JButton createBtn;
            private JLabel nameLabel;
            @Override
            public void actionPerformed(ActionEvent e) {
                // create JDialog
                addDialog = new JDialog();
                addDialog.setTitle("New model.Request");
                addDialog.setLocationRelativeTo(RequestListPanel.this);
                addDialog.setLayout(new BoxLayout(addDialog.getContentPane(),BoxLayout.Y_AXIS));
                addDialog.setSize(800,200);
                addDialog.setVisible(true);
                addDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

                //create panel and its components
                addPanel =new JPanel();
                addPanel.setLayout(new BoxLayout(addPanel,BoxLayout.X_AXIS));

                //creating the label
                nameLabel=new JLabel("Name:");
                nameLabel.setPreferredSize(new Dimension(100,50));
                nameLabel.setMaximumSize(new Dimension(70,50));


                //creating the textField for name of request
                requestNameField = new JTextField();
                requestNameField.addKeyListener(this);
                requestNameField.setMaximumSize(new Dimension(500,50));



                //creating Combo Box
                options= new String[]{"GET", "DELETE", "POST", "PUT", "PATCH"};
                methodOptions=new JComboBox<>(options);
                methodOptions.setMaximumSize(new Dimension(100,50));


                //creating "create Button"
                createBtn = new JButton("Create");
                createBtn.addActionListener(new CreateRequestHandler());

                //adding components to addPanel
                addPanel.add(nameLabel);
                addPanel.add(requestNameField);
                addPanel.add(Box.createRigidArea(new Dimension(30,50)));
                addPanel.add(methodOptions);

                //creating components to add dialog and adding blank spaces
                addDialog.add(Box.createRigidArea(new Dimension(500,30)));
                addDialog.add(addPanel);
                addDialog.add(Box.createRigidArea(new Dimension (100,20)));
                addDialog.add(new JSeparator());
                //addDialog.add(Box.createRigidArea(new Dimension(500,30)));
                addDialog.add(new JPanel(){
                    {
                       setLayout(new FlowLayout(FlowLayout.RIGHT));
                       setPreferredSize(new Dimension(650,200));
                       setMinimumSize(this.getPreferredSize());
                       setMaximumSize(this.getPreferredSize());
                       add(createBtn);
                    }
                });
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    createBtn=new JButton("Create"){
                        {
                            addActionListener(new CreateRequestHandler());
                            doClick();
                        }
                    };
                }
            }

        /**
         * create request when "create Button" is clicked and adds it to list model
         */
        private class CreateRequestHandler implements ActionListener{
                @Override
                public void actionPerformed(ActionEvent e) {
                    String requestName = requestNameField.getText();
                    String selectedMethod=(String)methodOptions.getSelectedItem();
                    Request newRequest = new Request(requestName,selectedMethod,list);
                    requestsModel.addElement(newRequest);
                    ListPanel.this.updateUI();
                    addDialog.dispose();
                }
            }
        }

    /**
     * shows requests details (req panel and response panel) in split pane
     */
    private class ChosenRequestHandler implements ListSelectionListener {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(list.getSelectedIndex()>-1){
                    Request request = (Request) list.getSelectedValue();
//                reqAndResponseSplit.removeAll();
                    reqAndResponseSplit.setLeftComponent(request.getRequestPanel());
                    //sets the right panel as empty panel if request isn't sent yet
                    if(!request.getRequestPanel().isRequestSent())
                        reqAndResponseSplit.setRightComponent(MainFrame.createVoidPanel());
                    else
                        reqAndResponseSplit.setRightComponent(request.getResponse().getResponsePanel());
                    request.getRequestPanel().setSplitPane(reqAndResponseSplit);
                    reqAndResponseSplit.setResizeWeight(0.5);
                    reqAndResponseSplit.repaint();
                    reqAndResponseSplit.revalidate();
                    reqAndResponseSplit.updateUI();
                }
            }
        }
        private  void removeRequest(Request requestToRemove){

        }

    /**
     * a renderer for list items
     */
    private class ListItemRenderer implements ListCellRenderer<Request> {
            /*
            creates a panel and adds 2 labels to it and color the label for showing used-method
             */
            @Override
            public Component getListCellRendererComponent(JList<? extends Request> list
                    , Request request, int index, boolean isSelected, boolean cellHasFocus) {
                //creating panel
                JPanel shownPanel=new JPanel();
                shownPanel.setLayout(new BoxLayout(shownPanel,BoxLayout.X_AXIS));
                shownPanel.setBorder(BorderFactory.createLineBorder(Color.black));
                shownPanel.setPreferredSize(new Dimension(100,30));

                //creating labels
                JLabel requestOption=new JLabel(request.getMethod());
                setColorForOption(requestOption,request.getMethod());
                JLabel requestName=new JLabel(request.getRequestName());
                requestName.setForeground(Color.WHITE);

                //adding to shownPanel
                shownPanel.add(Box.createRigidArea(new Dimension(10,30)));
                shownPanel.add(requestOption);
                shownPanel.add(Box.createRigidArea(new Dimension(10,30)));
                shownPanel.add(requestName);
                shownPanel.add(Box.createRigidArea(new Dimension(40,30)));



                shownPanel.setVisible(true);
                shownPanel.repaint();
                shownPanel.updateUI();
                shownPanel.revalidate();

                return shownPanel;
            }

        /**
         * sets color of the label based on its option(method)
         * @param label the label which its foreground color to be changed
         * @param option the option used for picking color
         */
            private void setColorForOption(JLabel label,String option){
                switch (option){
                    case "GET":
                        label.setForeground(Color.magenta);
                        break;
                    case "DELETE":
                        label.setForeground(Color.RED);
                        break;
                    case "POST":
                        label.setForeground(Color.GREEN);
                        break;
                    case "PUT":
                        label.setForeground(Color.ORANGE);
                        break;
                    case "PATCH":
                        label.setForeground(Color.YELLOW);
                        break;
                }
            }
        }
    }

}