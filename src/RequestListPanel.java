import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class RequestListPanel extends JPanel {
    JSplitPane reqAndResponseSplit;

    public RequestListPanel(JSplitPane reqAndResponseSplit) {
        super(new BorderLayout());
        setPreferredSize(new Dimension(300, 470));
        setMinimumSize(new Dimension(200, 400));
        this.reqAndResponseSplit = reqAndResponseSplit;
        JPanel namePanel = new JPanel(new BorderLayout());
        JTextArea programName = new JTextArea("HTTP Client");
        programName.setEditable(false);
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
        ArrayList<Request> requests;

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
                       setPreferredSize(new Dimension(700,200));
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
                    reqAndResponseSplit.setRightComponent(mainFrame.createVoidPanel());
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
            public ListItemRenderer(){
                //super(new FlowLayout());
               setOpaque(true);
            }
            @Override
            public Component getListCellRendererComponent(JList<? extends Request> list
                    , Request request, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel shownPanel=new JPanel(new FlowLayout());
                JLabel requestOption=new JLabel(request.getOption());
                JLabel requestName=new JLabel(request.getRequestName());
                shownPanel.add(requestOption);
                shownPanel.add(requestName);
                setVisible(true);
                if(isSelected){
                    shownPanel.setBackground(list.getSelectionBackground());
                    shownPanel.setForeground(list.getSelectionForeground());
                }else{
                    shownPanel.setBackground(list.getBackground());
                    shownPanel.setForeground(list.getForeground());
                }
                return shownPanel;
            }
        }
    }

}