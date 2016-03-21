

package Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * This is the Main Panel which contains all GUI components. It is to be added to
 * The graphical user interface frame.
 * 
 * @author Francisco Vilches | Saif Asad
 */
public class MainPanel extends JPanel {
    //Fields
    private final UsersPanel USERS_PANEL;
    private final MessageDeliveryPanel MESSAGE_DELIVERY_PANEL;
    private final TextAreaPanel TEXT_AREA_PANEL;
    
    //--------------------------------------------------------------------------
    /**
     * Constructor. Takes in all necessary GUI components, adds them to this 
     * panel, and groups them.
     * @param usersPanel
     * @param messageDeliveryPanel
     * @param textAreaPanel 
     */
    public MainPanel(UsersPanel usersPanel, MessageDeliveryPanel messageDeliveryPanel, TextAreaPanel textAreaPanel) {
        //Initializing fields
        this.USERS_PANEL = usersPanel;
        this.USERS_PANEL.setPreferredSize(new Dimension(100, this.getHeight()));
        this.MESSAGE_DELIVERY_PANEL = messageDeliveryPanel;
        this.TEXT_AREA_PANEL = textAreaPanel;
        
        //Setting this panel's layout and borders
        Border defaultborder = BorderFactory.createBevelBorder(TitledBorder.DEFAULT_JUSTIFICATION, Color.DARK_GRAY, Color.LIGHT_GRAY);
        setLayout(new GridBagLayout());
        GridBagConstraints C = new GridBagConstraints();
        
        //Defining GridBag layout settings
        C.ipadx = 5;
        C.ipady = 5;
        C.fill = GridBagConstraints.BOTH;
        
        //Adding usersPanel, where the logged in user buttons will be displayed
        C.gridheight = 2;
        C.weightx = 0;
        usersPanel.setBorder(defaultborder);
        add(usersPanel, C);
        
        //Adding the Text Area panel, where the conversations will be displayed
        C.gridheight = 1;
        C.weightx = 2;
        C.weighty = 1;
        C.gridx = 1;
        add(textAreaPanel, C);
        
        C.weightx = 0;
        C.weighty = 0;
        C.gridy = 1;
        messageDeliveryPanel.setBorder(defaultborder);
        add(messageDeliveryPanel, C);
        
        //Defining more panel settings
        setFocusable(true);
        requestFocus();
    }
    
    //--------------------------------------------------------------------------
    public UsersPanel getUSERS_PANEL() {
        return USERS_PANEL;
    }
    
    //--------------------------------------------------------------------------
    public MessageDeliveryPanel getMESSAGE_DELIVERY_PANEL() {
        return MESSAGE_DELIVERY_PANEL;
    }
    
    //--------------------------------------------------------------------------
    public TextAreaPanel getTEXT_AREA_PANEL() {
        return TEXT_AREA_PANEL;
    }
    
    /**
     * for debugging purposes only
     * @param args 
     */
    public static void main(String[] args) {
        ClientConnectionManager cm = new ClientConnectionManager();
        
        TextAreaPanel tap = new TextAreaPanel();
        MessageDeliveryPanel mdp = new MessageDeliveryPanel();
        UsersPanel up = new UsersPanel();
        MainPanel gui = new MainPanel(up, mdp, tap);
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(gui);
    }
}
