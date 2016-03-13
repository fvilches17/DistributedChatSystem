

package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class MsgDeliveryPanel extends JPanel {
    private final JButton SEND_BUTTON = new JButton("Send");
    private final JTextField TEXT_FIELD = new JTextField("Write your message here...");
    private final JTextArea TEXT_AREA = new JTextArea(10, 30);

    public MsgDeliveryPanel() {
        /*
        setLayout(new BorderLayout(5, 5));
        add(TEXT_FIELD, BorderLayout.CENTER);
        add(SEND_BUTTON, BorderLayout.SOUTH);
        */
        
        
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(new GridBagLayout());
        setBackground(Color.GRAY);
        //constraints.insets = new Insets(2,2,2,2);
        
        //Setting up text area/field
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        add(TEXT_FIELD, constraints);
        
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        add(SEND_BUTTON, constraints);
        
    }
    
    //Main method for testing purposes only
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        MsgDeliveryPanel msg = new MsgDeliveryPanel();
        mainPanel.add(msg, BorderLayout.SOUTH);
        JPanel saifPanel = new JPanel();
        saifPanel.add(new JTextArea());
        mainPanel.add(saifPanel, BorderLayout.EAST);
        
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(mainPanel);
    }
    
    

}
