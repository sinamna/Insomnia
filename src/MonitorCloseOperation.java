import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MonitorCloseOperation implements ActionListener {
    private TrayIcon trayIcon;
    private SystemTray systemTray;
    private PopupMenu popupMenu;
    private MainFrame frame;
    public  MonitorCloseOperation(JFrame frame){
        //frame
        this.frame=(MainFrame)frame;

        //system Tray
        systemTray=SystemTray.getSystemTray();

        //popup menu
//        popupMenu=createPopupMenu();

        //trayIcon
        Image image=Toolkit.getDefaultToolkit().getImage("media\\insomnia_Icon.png");
        trayIcon=new TrayIcon(image,"Insomnia");
        trayIcon.addMouseListener(new ClickOnTrayHandler());
        trayIcon.setImageAutoSize(true);
    }
    private class ClickOnTrayHandler extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount()>=1){
                new JButton(){
                    {
                        addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                frame.setVisible(true);
                                frame.setExtendedState(JFrame.NORMAL);
                                systemTray.remove(trayIcon);
                            }
                        });
                        doClick();
                    }
                };
            }
        }
    }
//    private PopupMenu createPopupMenu(){
//        PopupMenu popupMenu=new PopupMenu();
//
//        //creating exitItem
//        MenuItem exitItem=new MenuItem("Exit");
//        exitItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                frame.dispose();
//                System.exit(0);
//            }
//        });
//
//        //creating openItem
//        MenuItem openItem=new MenuItem("Open");
//        openItem.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                frame.setVisible(true);
//                frame.setExtendedState(JFrame.NORMAL);
//                systemTray.remove(trayIcon);
//            }
//        });
//        //add items to menu
//        popupMenu.add(exitItem);
//        popupMenu.add(openItem);
//
//        return popupMenu;
//    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(frame.getHideInTray()) {
            try {
                frame.setVisible(false);
                systemTray.add(trayIcon);
            } catch (AWTException ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage(), "Warning!", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            frame.dispose();
            System.exit(0);
        }
    }
}