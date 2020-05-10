import javax.swing.*;
import java.awt.*;

public class View {
    private RequestListPanel listPanel;
    JSplitPane mainBody, reqAndResponseSplit;
    JFrame frame;
    public View (){
        setUi();
        frame=new JFrame("HTTP Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        reqAndResponseSplit =new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,createVoidPanel(),createVoidPanel());
        listPanel=new RequestListPanel(reqAndResponseSplit);
        mainBody=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,listPanel, reqAndResponseSplit);
        frame.add(mainBody);
        frame.pack();
        frame.setVisible(true);

    }
    public static JPanel createVoidPanel(){
        JPanel voidPanel=new JPanel();
        voidPanel.setPreferredSize(new Dimension(370,550));
        voidPanel.setMinimumSize(new Dimension(100,400));
        return voidPanel;
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
