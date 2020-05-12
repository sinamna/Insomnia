import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class RequestListPanel extends JPanel {
    JSplitPane reqAndResponseSplit;

    public RequestListPanel(JSplitPane reqAndResponseSplit) {
        //set panel's attributes
        super(new BorderLayout());
        setPreferredSize(new Dimension(300, 470));
        setMinimumSize(new Dimension(200, 400));
        this.reqAndResponseSplit = reqAndResponseSplit;

        JPanel namePanel = new JPanel(new BorderLayout());

        //label
        JLabel programName = new JLabel("Insomnia");
        programName.setHorizontalAlignment(JLabel.CENTER);
        programName.setPreferredSize(new Dimension(programName.getPreferredSize().width
                ,programName.getPreferredSize().height+30));
        programName.setOpaque(true);
        programName.setForeground(Color.WHITE);
        programName.setBackground(new Color(124, 84, 145));
        programName.setFont(new Font("Palatino",Font.BOLD,20));
        namePanel.add(programName, BorderLayout.CENTER);
        add(namePanel, BorderLayout.PAGE_START);
        ListPanel listPanel = new ListPanel();
        add(new JScrollPane(listPanel), BorderLayout.CENTER);
    }
//----------------------------------------------------------------------------
    private class ListPanel extends JPanel {
        JButton addRequestBtn;
        DefaultListModel<Request> requestsModel;
        JList<Request> list;

        public ListPanel() {
            super(new BorderLayout());
            // add request Button
            addRequestBtn = new JButton("Add Request");
            addRequestBtn.addActionListener(new NewRequestHandler());


            // the list of requests
            requestsModel = new DefaultListModel<>();
            list = new JList<>(requestsModel);
            list.setCellRenderer(new ListItemRenderer());
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.addListSelectionListener(new ChosenRequestHandler());

            //adding components to the panel
            add(addRequestBtn, BorderLayout.NORTH);
            add(list, BorderLayout.CENTER);
        }
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
                addDialog.setTitle("New Request");
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
            private class CreateRequestHandler implements ActionListener{
                @Override
                public void actionPerformed(ActionEvent e) {
                    String requestName = requestNameField.getText();
                    String selectedMethod=(String)methodOptions.getSelectedItem();
                    Request newRequest = new Request(requestName,selectedMethod);
                    requestsModel.addElement(newRequest);
                    ListPanel.this.updateUI();
                    addDialog.dispose();
                }
            }
        }
        private class ChosenRequestHandler implements ListSelectionListener {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Request request = (Request) list.getSelectedValue();
//                reqAndResponseSplit.removeAll();
                reqAndResponseSplit.setLeftComponent(request.getRequestPanel());
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

        public class ListItemRenderer implements ListCellRenderer<Request> {

            @Override
            public Component getListCellRendererComponent(JList<? extends Request> list
                    , Request request, int index, boolean isSelected, boolean cellHasFocus) {
                //creating panel
                JPanel shownPanel=new JPanel();
                shownPanel.setLayout(new BoxLayout(shownPanel,BoxLayout.X_AXIS));
                shownPanel.setBorder(BorderFactory.createLineBorder(Color.black));
                shownPanel.setPreferredSize(new Dimension(200,30));

                //creating labels
                JLabel requestOption=new JLabel(request.getOption());
                setColorForOption(requestOption,request.getOption());
                JLabel requestName=new JLabel(request.getRequestName());
                requestName.setForeground(Color.WHITE);

//                //creating delete button
//                JButton delBtn=new JButton("DEL");
//                delBtn.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        requestsModel.removeElement(request);
//                    }
//                });

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