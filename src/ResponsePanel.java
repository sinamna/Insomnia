import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ResponsePanel extends JPanel {
    private JPanel upperPanel;
    public ResponsePanel(){
        setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(370,550));
        setMinimumSize(new Dimension(100,400));
        upperPanel=new UpperPanel();
        add(upperPanel,BorderLayout.PAGE_START);
    }


    private class UpperPanel extends JPanel{
        JTextField statusCode;
        JTextField statusMessage;
        JTextField elapsedTime;
        JTextField responseSize;
        private class ResponseDetail extends JTextField{
            public ResponseDetail(String text){
                super(text);
                setEditable(false);
                setBackground(Color.white);
                setBorder(BorderFactory.createLineBorder(Color.black));
                setPreferredSize(new Dimension(50,30));
                setMinimumSize(this.getPreferredSize());
                setMaximumSize(this.getPreferredSize());
                setHorizontalAlignment(JTextField.CENTER);
            }
        }
        public UpperPanel(){
            super(new FlowLayout(FlowLayout.LEFT));
            setBackground(Color.WHITE);
            statusCode=new ResponseDetail("200");
            statusMessage=new ResponseDetail("OK");
            elapsedTime=new ResponseDetail("6.64 s");
            responseSize=new ResponseDetail("13.1 KB");
            add(statusCode);
            add(statusMessage);
            add(elapsedTime);
            add(responseSize);
        }
    }
}
