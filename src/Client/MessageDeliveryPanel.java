

package Client;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This object represents the Jpanel which contains a text field and a send button 
 * through which the client can send messages to any intended recipient. 
 * 
 * @author Francisco Vilches | Saif Asad
 */
public class MessageDeliveryPanel extends JPanel {
    //Fields
    private final JButton SEND_BUTTON = new JButton("Send");
    private final JTextField TEXT_FIELD = new JTextField("Type your message here...");

    //--------------------------------------------------------------------------
    /**
     * Constructor
     * 
     * Sets layout, adds, and groups all components
     */
    public MessageDeliveryPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        
        //Setting up text field
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        add(TEXT_FIELD, constraints);
        
        //Setting up send button
        constraints.gridy = 1;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.EAST;
        add(SEND_BUTTON, constraints);
    }
    
    //--------------------------------------------------------------------------
    public JTextField getTEXT_FIELD() {
        return TEXT_FIELD;
    }
    
    //--------------------------------------------------------------------------
    public JButton getSEND_BUTTON() {
        return SEND_BUTTON;
    }
}
