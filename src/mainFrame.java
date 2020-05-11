import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class mainFrame extends JFrame{
    private RequestListPanel listPanel;
    private JMenuBar menuBar;
    private JMenu applicationMenu;
    private JMenu viewMenu;
    private JMenu helpMenu;
    private boolean hideInTray;
    private boolean followRedirect;
//    private GraphicsEnvironment env;
//    private GraphicsDevice device;
    JSplitPane mainBody, reqAndResponseSplit;
    public mainFrame(){
        //setting frame's attributes
        super("Insomnia");
//        env = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        device = env.getDefaultScreenDevice();
        setUi();
        ImageIcon frameIcon=new ImageIcon("media\\insomnia_Icon.png");
        setIconImage(frameIcon.getImage());
        addWindowListener(new MonitorCloseOperation(this));
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        followRedirect=false;
        hideInTray=false;

        //creating application menu
        applicationMenu=new JMenu("Application");
        applicationMenu.setMnemonic('A');//set mnemonic to 'A'
        JMenuItem optionsItem=new JMenuItem("Options");
        optionsItem.setMnemonic('O');//set mnemonic to 'O'
        optionsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
        optionsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OptionDialog dialog=new OptionDialog();
            }
        });
        JMenuItem exitItem=new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK));
        exitItem.setMnemonic('X');//set mnemonic to X
        applicationMenu.add(optionsItem);
        applicationMenu.add(exitItem);

        //creating View menu
        viewMenu=new JMenu("View");
        viewMenu.setMnemonic('V');//set mnemonic to 'V'
        JMenuItem toggleFullScreenItem=new JMenuItem("Toggle Full Screen");
        toggleFullScreenItem.setMnemonic('F');//set mnemonic to 'T'
        toggleFullScreenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK));
        toggleFullScreenItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.this.setExtendedState(mainFrame.this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
            }
        });
        JMenuItem toggleSidebarItem=new JMenuItem("Toggle Sidebar");
        toggleSidebarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
        toggleSidebarItem.setMnemonic('S');//set mnemonic to 'S'
        viewMenu.add(toggleFullScreenItem);
        viewMenu.add(toggleSidebarItem);

        //creating help menu
        helpMenu=new JMenu("Help");
        helpMenu.setMnemonic('M');//set mnemonic to 'M'
        JMenuItem aboutItem=new JMenuItem("About");
        aboutItem.setMnemonic('A');//set mnemonic to 'A'
        aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
        aboutItem.addActionListener(new AboutMenuItemHandler());

        //help menuItem
        JMenuItem helpItem=new JMenuItem("Help");
        helpItem.setMnemonic('H');//set mnemonic to H
        helpItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,ActionEvent.CTRL_MASK));
        helpItem.addActionListener(new HelpMenuItemHandler());
        helpMenu.add(aboutItem);
        helpMenu.add(helpItem);

        //creating menu
        menuBar=new JMenuBar();
        menuBar.add(applicationMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        //creating splitPanes
        reqAndResponseSplit =new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,createVoidPanel(),createVoidPanel());
        reqAndResponseSplit.setResizeWeight(0.5);
        listPanel=new RequestListPanel(reqAndResponseSplit);
        mainBody=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,listPanel, reqAndResponseSplit);

        //adding components to the frame
        setJMenuBar(menuBar);
        add(mainBody);
//        pack();
        setSize(new Dimension(1000,470));
        setVisible(true);

    }
    private class AboutMenuItemHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            new JDialog(){
                {
                    //setting JDialog's attributes
                    setTitle("About");
                    setSize(new Dimension(350,450));
                    setLayout(new BorderLayout());
                    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    setLocationRelativeTo(this);
                    setVisible(true);

                    //adding components
                    add(new JPanel(){
                        JLabel email;
                        JLabel studentID;
                        {
                            setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
                            //creating JtextFields
                            email=new JLabel("Sina3050@gmail.com");
                            email.setBackground(Color.white);
                            email.setHorizontalAlignment(JTextField.CENTER);
                            studentID=new JLabel("Student ID: 9831009");
                            studentID.setBackground(Color.WHITE);
                            studentID.setHorizontalAlignment(JTextField.CENTER);

                            //adding components to the panel
                            add(Box.createRigidArea(new Dimension(100,150)));
                            add(email);
                            add(studentID);
                        }
                    });

                }
            };
        }
    }
    private class HelpMenuItemHandler implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            new JDialog(){
                JTextArea helpText;
                {
                    //setting JDialogs attributes
                    setTitle("Help");
                    setSize(new Dimension(350,450));
                    setLayout(new BorderLayout());
                    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    setLocationRelativeTo(this);
                    setVisible(true);

                    //creating fields
                    helpText=new JTextArea();
                    helpText.setEditable(false);
                    helpText.setText(" the helping-text ");

                    //adding component to JDialog
                    add(helpText,BorderLayout.CENTER);
                }
            };
        }
    }
    private void loadSettings(){

    }
    public static JPanel createVoidPanel(){
        JPanel voidPanel=new JPanel();
        voidPanel.setPreferredSize(new Dimension(370,470));
        voidPanel.setMinimumSize(new Dimension(100,400));
        return voidPanel;
    }

    private class OptionDialog extends JDialog{
        private JPanel holder;
        private JLabel redirectLabel;
        private JLabel hideInTrayLabel;
        private JCheckBox redirectCheckBox;
        private JCheckBox hideCheckBox;

        public OptionDialog(){
            //creating panel
            holder=new JPanel();
            holder.setLayout(new BoxLayout(holder,BoxLayout.Y_AXIS));

            //creating redirect Panel
            redirectLabel=new JLabel("Follow Redirect");
            redirectCheckBox=new JCheckBox();

            //adding item listener
            redirectCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    followRedirect=redirectCheckBox.isSelected();
                }
            });
            JPanel redirectPanel=createPanel(redirectLabel,redirectCheckBox,"Follow Redirect");

            //creating hideOptionPanel
            hideInTrayLabel=new JLabel("Hide in Tray");
            hideCheckBox=new JCheckBox();
            hideCheckBox.setSelected(hideInTray);

            //adding item listener
            hideCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    hideInTray=hideCheckBox.isSelected();
                }
            });
            JPanel hideOptionPanel=createPanel(hideInTrayLabel,hideCheckBox,"Hide in Tray");

            //adding component to holder
            holder.add(Box.createRigidArea(new Dimension(100,25)));
            holder.add(redirectPanel);
            holder.add(Box.createRigidArea(new Dimension(100,5)));
            holder.add(hideOptionPanel);
            holder.add(Box.createRigidArea(new Dimension(100,25)));


            //adding holder to dialog
            add(holder);

            //setting dialog attributes
            setTitle("Options");
//            setLocationRelativeTo(this);
            pack();
            setVisible(true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        }

        private JPanel createPanel(JLabel label, JCheckBox checkBox, String labelText){
            //creating Label
            label.setHorizontalAlignment(SwingConstants.CENTER);

            //creating Panel
            JPanel panel=new JPanel();
            panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
            panel.add(Box.createRigidArea(new Dimension(15,30)));
            panel.add(label);
            panel.add(Box.createRigidArea(new Dimension(5,30)));
            panel.add(checkBox);
            panel.add(Box.createRigidArea(new Dimension(15,30)));

            return panel;
        }
    }

    private void setUi(){
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean getHideInTray(){
        return hideInTray;
    }
}
