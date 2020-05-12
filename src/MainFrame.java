import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

import com.github.weisj.darklaf.DarkLaf;
import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.theme.DarculaTheme;

public class MainFrame extends JFrame{
    private RequestListPanel listPanel;
    private JMenuBar menuBar;
    private JMenu applicationMenu;
    private JMenu viewMenu;
    private JMenu helpMenu;
    //boolean variables to save details
    private boolean hideInTray;
    private boolean followRedirect;
    private boolean isFullScreen;
    private Dimension lastSize;
    private GraphicsEnvironment env;
    private GraphicsDevice device;
    private JSplitPane mainBody, reqAndResponseSplit;

    /**
     * constructs a frame with menu bar and 2 splitPanes
     */
    public MainFrame(){
        //setting frame's attributes
        super("Insomnia");
        setUi();
        ImageIcon frameIcon=new ImageIcon("media\\insomnia_Icon.png");
        setIconImage(frameIcon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(new Dimension(1000,470));
        addComponentListener(new ResizeListener());
        env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = env.getDefaultScreenDevice();
        followRedirect=false;
        hideInTray=false;
        isFullScreen=false;
        lastSize=getBounds().getSize();

        //save or load files
        /*
        if file is available it loads the setting ,if not it creates new file
         */
        File savedSettings=new File("saved_data\\savedSettings.txt");
        try{
            //create file method returns false if file already exists
            if(!savedSettings.createNewFile())
                loadSettings(savedSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //adding listener to save data to frame
        addWindowListener(new OptionSaver(savedSettings));


        //creating application menu
        applicationMenu=new JMenu("Application");
        applicationMenu.setMnemonic('A');//set mnemonic to 'A'
        JMenuItem optionsItem=new JMenuItem("Options");
        optionsItem.setMnemonic('O');//set mnemonic to 'O'
        optionsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
        optionsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OptionDialog();
            }
        });

        //exit menuItem
        JMenuItem exitItem=new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,ActionEvent.CTRL_MASK));
        exitItem.setMnemonic('X');//set mnemonic to X
        //close operation will be handled based on hideInTray value
        exitItem.addActionListener(new MonitorCloseOperation(this));
        applicationMenu.add(optionsItem);
        applicationMenu.add(exitItem);

        //creating View menu
        viewMenu=new JMenu("View");
        viewMenu.setMnemonic('V');//set mnemonic to 'V'
        JMenuItem toggleFullScreenItem=new JMenuItem("Toggle Full Screen");
        toggleFullScreenItem.setMnemonic('F');//set mnemonic to 'T'
        toggleFullScreenItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.CTRL_MASK));
        toggleFullScreenItem.addActionListener(new ToggleFullScreen());

        //toggleSideBar
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
//        listPanel.setVisible(false);
        mainBody=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,listPanel, reqAndResponseSplit);

        //adding components to the frame
        setJMenuBar(menuBar);
        add(mainBody);

        setVisible(true);
    }

    /**
     * toggles fullscreen and save the last size of the frame
     * if the frame is already in fullscreen mode it will load the last size
     */
    private class ToggleFullScreen implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(isFullScreen){
                device.setFullScreenWindow(null);
                setVisible(true);
                MainFrame.this.setSize(lastSize);
                isFullScreen=false;
            }else{
                //saving the info
                isFullScreen=true;
                lastSize=getBounds().getSize();
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                validate();
            }
        }
    }

    /**
     * creates a dialog with explanation in it
     */
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

    /**
     * creates new dialog for help menu item
     */
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

    /**
     * loads the options and last resolution from file
     * @param file file used to load settings from
     */
    private void loadSettings(File file){
        try(Scanner savedSettings=new Scanner(file))
        {
            while(savedSettings.hasNext()){
                String option=(String)savedSettings.nextLine();
                String[] divided=option.split(" ");
                if(option.contains("Follow")) {
                    followRedirect=divided[1].equals("true");
                }else if(option.contains("Hide"))
                    hideInTray=divided[1].equals("true");
                else if(option.contains("Resolution"))
                    MainFrame.this.setSize(new Dimension(Integer.parseInt(divided[1])
                            ,Integer.parseInt(divided[2])));
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * sets the last size of frame
     * @param lastSize size to be set
     */
    public void setLastSize(Dimension lastSize) {
        this.lastSize = lastSize;
    }

    /**
     * saves the last size whenever frame being resized
     */
    private class ResizeListener extends ComponentAdapter{
        @Override
        public void componentResized(ComponentEvent e) {
            super.componentResized(e);
            MainFrame frame=(MainFrame)e.getSource();
            if(!isFullScreen)
                 setLastSize(frame.getBounds().getSize());
        }
    }

    /**
     * saves the options and last frame size in string format
     */
    private class OptionSaver extends WindowAdapter{
        private File file;
        public OptionSaver(File file){
            this.file=file;
        }

        @Override
        public void windowClosing(WindowEvent e) {
            String[] options={"FollowRedirect "+followRedirect,"HideInTray "+hideInTray};
            System.out.println();
            try(PrintWriter out=new PrintWriter(file))
            {
                for(String option:options)
                    out.println(option);
                out.println("Resolution "+lastSize.getSize().width+" "+lastSize.getSize().height);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * creates empty void panels
     * @return panel created
     */
    public static JPanel createVoidPanel(){
        JPanel voidPanel=new JPanel();
        voidPanel.setPreferredSize(new Dimension(370,470));
        voidPanel.setMinimumSize(new Dimension(100,400));
        return voidPanel;
    }

    /**
     * the dialog which settings being set
     */
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
            redirectCheckBox.setSelected(followRedirect);

            //adding item listener
            redirectCheckBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    followRedirect=redirectCheckBox.isSelected();
                }
            });
            JPanel redirectPanel=createPanel(redirectLabel,redirectCheckBox);

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
            JPanel hideOptionPanel=createPanel(hideInTrayLabel,hideCheckBox);

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
            pack();
            setVisible(true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        }

        /**
         * creates panel with customized order
         * @param label the label in panel
         * @param checkBox the checkbox in panel
         * @return the created panel
         */
        private JPanel createPanel(JLabel label, JCheckBox checkBox){
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

    /**
     * sets the ui
     */
    private void setUi(){
        try{
            LafManager.setTheme(new DarculaTheme());
            UIManager.setLookAndFeel(DarkLaf.class.getCanonicalName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * return the hide in tray value
     * @return the boolean value
     */
    public boolean getHideInTray(){
        return hideInTray;
    }
}
