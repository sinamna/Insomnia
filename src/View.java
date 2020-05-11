import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class View {
    private RequestListPanel listPanel;
    private JMenuBar menuBar;
    private JMenu applicationMenu;
    private JMenu viewMenu;
    private JMenu helpMenu;
    private boolean hideInTray;
    private boolean followRedirect;
    JSplitPane mainBody, reqAndResponseSplit;
    JFrame frame;
    public View (){
        //setting frame's attributes
        setUi();
        frame=new JFrame("Insomnia");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        followRedirect=false;
        hideInTray=false;

        //creating application menu
        applicationMenu=new JMenu("Application");
        JMenuItem optionsItem=new JMenuItem("Options");
        optionsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OptionDialog dialog=new OptionDialog();
            }
        });
        JMenuItem exitItem=new JMenuItem("Exit");
        applicationMenu.add(optionsItem);
        applicationMenu.add(exitItem);

        //creating View menu
        viewMenu=new JMenu("View");
        JMenuItem toggleFullScreenItem=new JMenuItem("Toggle Full Screen");
        JMenuItem toggleSidebarItem=new JMenuItem("Toggle Sidebar");
        viewMenu.add(toggleFullScreenItem);
        viewMenu.add(toggleSidebarItem);

        //creating help menu
        helpMenu=new JMenu("Help");
        JMenuItem aboutItem=new JMenuItem("About");
        JMenuItem helpItem=new JMenuItem("Help");
        helpMenu.add(aboutItem);
        helpMenu.add(helpItem);

        //creating menu
        menuBar=new JMenuBar();
        menuBar.add(applicationMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        //creating splitPanes
        reqAndResponseSplit =new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,createVoidPanel(),createVoidPanel());
        listPanel=new RequestListPanel(reqAndResponseSplit);
        mainBody=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,listPanel, reqAndResponseSplit);

        //adding components to the frame
        frame.setJMenuBar(menuBar);
        frame.add(mainBody);
        frame.pack();
        frame.setVisible(true);

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
            setLocationRelativeTo(frame);
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
}
