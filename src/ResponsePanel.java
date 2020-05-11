import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ResponsePanel extends JPanel {
    private JPanel upperPanel;
    private JPanel centerPanel;
    private Response response;

    public ResponsePanel(Response response) {
        //setting panel attributes
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(370, 550));
        setMinimumSize(new Dimension(100, 400));

        //initializing fields
        this.response = response;

        //creating and adding upperPanel
        upperPanel = new UpperPanel();

        //creating and adding centerPanel
        centerPanel=new CenterPanel();

        //adding components
        add(upperPanel, BorderLayout.PAGE_START);
        add(centerPanel,BorderLayout.CENTER);

    }

    //--------------------------------------------------------------------------
    private class UpperPanel extends JPanel {
        JTextField statusCode;
        JTextField statusMessage;
        JTextField elapsedTime;
        JTextField responseSize;

        //creating costume JTextField with preDefined attributes
        private class ResponseDetail extends JTextField {
            public ResponseDetail(String text) {
                super(text);
                setEditable(false);
                setBackground(Color.white);
                setBorder(BorderFactory.createLineBorder(Color.black));
                setPreferredSize(new Dimension(getPreferredSize().width+35, getPreferredSize().height+15));
                setMinimumSize(this.getPreferredSize());
                setMaximumSize(this.getPreferredSize());
                setHorizontalAlignment(JTextField.CENTER);
            }
        }

        public UpperPanel() {
            super(new FlowLayout(FlowLayout.LEFT));
            setBackground(Color.WHITE);
            //Initializing fields
            statusCode = new ResponseDetail("200");
            statusMessage = new ResponseDetail("Ok");
            elapsedTime = new ResponseDetail("6.2 s");
            responseSize = new ResponseDetail("73.3 kb");

            //adding to panel
            add(statusCode);
            add(statusMessage);
            add(elapsedTime);
            add(responseSize);
        }
    }

    //-------------------------------------------------------------------------------
    private class CenterPanel extends JPanel {
        private JMenuBar menuBar;
        private JMenu messageBody;
        private JMenu headers;
        private JPanel mainPanel;
        private JPanel rawBody;
        private JPanel previewBody;
        private JPanel headerPanel;
        private CardLayout layout;

        public CenterPanel() {
            //setting panel's attributes
            super(new BorderLayout());

            //creating main panel and its layOut
            mainPanel=new JPanel();
            layout=new CardLayout();
            mainPanel.setLayout(layout);
            add(mainPanel,BorderLayout.CENTER);

            //creating MenuBar
            menuBar = new JMenuBar();
            menuBar.setPreferredSize(new Dimension(menuBar.getPreferredSize().width,menuBar.getPreferredSize().height+30));
            add(menuBar, BorderLayout.PAGE_START);

            //creating menu Items
            messageBody = new JMenu("Body");
            JMenuItem rawItem = new JMenuItem("Raw");
            rawItem.addActionListener(new MenuHandler.MenuItemHandler(layout,mainPanel));
            JMenuItem previewItem = new JMenuItem("Preview");
            previewItem.addActionListener(new MenuHandler.MenuItemHandler(layout,mainPanel));
            messageBody.add(rawItem);
            messageBody.add(previewItem);
            menuBar.add(messageBody);

            headers=new JMenu("Header");
            headers.addMenuListener(new MenuHandler.MenuSelectionHandler(layout,mainPanel));
            menuBar.add(headers);


            //creating menu panels
            rawBody=new RawBody();
            previewBody=new PreviewPanel();
            headerPanel=new HeaderPanel();

            //adding panels to mainPanel
            mainPanel.add(rawBody,"Raw");
            mainPanel.add(previewBody,"Preview");
            mainPanel.add(new JScrollPane(headerPanel),"Header");

        }
    }

    //-----------------------------------------------------------------------------------
    private class RawBody extends JPanel{
        private JTextArea display;
        public RawBody(){
            super(new BorderLayout());
            //initializing display
            display=new JTextArea();
            display.setEditable(false);
            if(!response.getResponseText().isEmpty())
                display.setText(response.getResponseText());
            else
                display.setText("Error: URL using bad/illegal format or missing URL");
            add(display,BorderLayout.CENTER);
        }
    }
    //-------------------------------------------------------------------------------------
    private class PreviewPanel extends JPanel{
        private JEditorPane display;
        public PreviewPanel(){
            super(new BorderLayout());
            //initializing display
            display=new JEditorPane();
            display.setEditable(false);
            display.setText("preview panel :)");
            display.setVisible(true);
            add(display);
        }
    }
    //---------------------------------------------------------------------------------------
    private class HeaderPanel extends JPanel{
        private JLabel nameLabel;
        private JLabel valueLabel;
        private JButton copyBtn;
        public HeaderPanel(){
            setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            // name Label
            nameLabel=new JLabel("Name");
            nameLabel.setHorizontalAlignment(JLabel.CENTER);
            nameLabel.setMaximumSize(new Dimension(50,30));

            //value Label
            valueLabel=new JLabel("Value");
            valueLabel.setHorizontalAlignment(JLabel.CENTER);
            valueLabel.setMaximumSize(new Dimension(50,30));

            //create a panel to hold Labels
            JPanel labelHolder=new JPanel();
            labelHolder.setLayout(new BoxLayout(labelHolder,BoxLayout.X_AXIS));
            labelHolder.add(nameLabel);
            labelHolder.add(Box.createRigidArea(new Dimension(100,30)));
            labelHolder.add(valueLabel);

            //create a button to copy them to clipBoard
            copyBtn=new JButton("Copy to Clipboard");
            copyBtn.setPreferredSize(new Dimension(150,30));
            copyBtn.addActionListener(new CopyBtnHandler());

            //adding components to the panel
            add(Box.createRigidArea(new Dimension(100,20)));
            add(labelHolder);
            add(Box.createRigidArea(new Dimension(100,5)));
            createHeaderPanels();
            add(Box.createRigidArea(new Dimension(100,40)));
            add(copyBtn);

        }
        private void createHeaderPanels(){
            for(Info header:response.getHeaders()){
                if(header.getState())
                    add(new HeaderBox(header.getKey(),header.getValue()));
            }
        }

        private class HeaderBox extends JPanel{
            private JTextField key;
            private JTextField value;
            public HeaderBox(String keyText,String valueText){
                setLayout(new BoxLayout(this,BoxLayout.X_AXIS));

                //setting key textField
                key=new JTextField();
                key.setText(keyText);
                key.setHorizontalAlignment(JTextField.CENTER);
                key.setEditable(false);
                key.setMaximumSize(new Dimension(1000,30));

                //setting value textField
                value=new JTextField();
                value.setText(valueText);
                value.setHorizontalAlignment(JTextField.CENTER);
                value.setEditable(false);
                value.setMaximumSize(new Dimension(1000,30));

                //adding components to the panels
                add(Box.createRigidArea(new Dimension(10,5)));
                add(key);
                add(Box.createRigidArea(new Dimension(5,5)));
                add(value);
                add(Box.createRigidArea(new Dimension(10,5)));
            }
        }

        private class CopyBtnHandler implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder headersCopy=new StringBuilder();
                for(Info header:response.getHeaders()){
                    if(header.getState())
                        headersCopy.append(header.toString());
                }
                StringSelection stringSelection=new StringSelection(headersCopy.toString());
                Clipboard clipboard=Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection,null);
                JOptionPane.showMessageDialog(null,"Headers copied to Clipboard successfully");
            }
        }
    }
}
