

package Client;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


public class MainPanel extends JPanel {
    private final UsersPanel USERS_PANEL;
    private final MessageDeliveryPanel MESSAGE_DELIVERY_PANEL;
    private final TextAreaPanel TEXT_AREA_PANEL;

    public MainPanel(UsersPanel USERS_PANEL, MessageDeliveryPanel MESSAGE_DELIVERY_PANEL, TextAreaPanel TEXT_AREA_PANEL) {
        this.USERS_PANEL = USERS_PANEL;
        this.MESSAGE_DELIVERY_PANEL = MESSAGE_DELIVERY_PANEL;
        this.TEXT_AREA_PANEL = TEXT_AREA_PANEL;
        
        Border defaultborder = BorderFactory.createBevelBorder(TitledBorder.DEFAULT_JUSTIFICATION, Color.DARK_GRAY, Color.LIGHT_GRAY);
        setLayout(new GridBagLayout());
        GridBagConstraints C = new GridBagConstraints();
        
        C.ipadx = 5;
        C.ipady = 5;
        C.fill = GridBagConstraints.BOTH;
        
        C.gridheight = 2;
        USERS_PANEL.setBorder(defaultborder);
        add(USERS_PANEL, C);
        
        C.gridheight = 1;
        C.weightx = 1;
        C.weighty = 1;
        C.gridx = 1;
        add(TEXT_AREA_PANEL, C);
        
        C.weightx = 0;
        C.weighty = 0;
        C.gridy = 1;
        MESSAGE_DELIVERY_PANEL.setBorder(defaultborder);
        add(MESSAGE_DELIVERY_PANEL, C);
        
        setFocusable(true);
        requestFocus();
    }

    public UsersPanel getUSERS_PANEL() {
        return USERS_PANEL;
    }

    public MessageDeliveryPanel getMESSAGE_DELIVERY_PANEL() {
        return MESSAGE_DELIVERY_PANEL;
    }

    public TextAreaPanel getTEXT_AREA_PANEL() {
        return TEXT_AREA_PANEL;
    }
    
    public static void main(String[] args) {
        ConnectionManager cm = new ConnectionManager();
        
        TextAreaPanel tap = new TextAreaPanel();
        MessageDeliveryPanel mdp = new MessageDeliveryPanel();
        UsersPanel up = new UsersPanel();
        MainPanel gui = new MainPanel(up, mdp, tap);
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(gui);
    }
    
}
