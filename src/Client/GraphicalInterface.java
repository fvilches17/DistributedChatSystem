

package Client;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class GraphicalInterface extends JFrame {
    //FIELDS--------------------------------------------------------------------
    //private final URL LOGO_URL = Main.class.getResource("images/Logo.png");
    //private final Image LOGO = new ImageIcon(LOGO_URL).getImage();
    private final MainPanel MAIN_PANEL;

    //CONSTRUCTOR---------------------------------------------------------------
    public GraphicalInterface(MainPanel mainPanel, User user, ConnectionManager commsManager) {
        MAIN_PANEL = mainPanel;
        
        //Defining screen dimension
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Insets screen = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        int taskBarHeight = screen.bottom;
        Dimension userScreenDimension = new Dimension(toolkit.getScreenSize().width,
                toolkit.getScreenSize().height - taskBarHeight);
        
        
        //Loading frame settings
        setTitle("Distributed Chat System");
        setName("Distributed Chat System");
        getContentPane().add(mainPanel);
        JScrollPane jsp = new JScrollPane(mainPanel);
        getContentPane().add(jsp);
        pack();
        setVisible(true);
        setSize(userScreenDimension);
        //setIconImage(LOGO);
        setLocationRelativeTo(null);
        addWindowListener(new WindowCloseEvent(user, commsManager));
        
    }
    
    private class WindowCloseEvent implements WindowListener {
        
        private final User USER;
        private final ConnectionManager COMMS_MANAGER;

        public WindowCloseEvent(User user, ConnectionManager commsManager) {
            this.USER = user;
            this.COMMS_MANAGER = commsManager;
        }
        
        @Override
        public void windowClosing(WindowEvent e) {
            DisconectMessage logoutMessage = new DisconectMessage(USER, false);
            try {
                COMMS_MANAGER.sendMessage(logoutMessage);
            } catch (IOException ex) {
                System.err.println("Unable to send logout message");
                System.err.println(ex);
            }
            COMMS_MANAGER.closeConnection();
            System.out.println("Terminating program");
            System.exit(-1);
        }
        
        //Unused
        @Override
        public void windowOpened(WindowEvent e) {        }
        @Override
        public void windowClosed(WindowEvent e) {}
        @Override
        public void windowIconified(WindowEvent e) {}
        @Override
        public void windowDeiconified(WindowEvent e) {}
        @Override
        public void windowActivated(WindowEvent e) {}
        @Override
        public void windowDeactivated(WindowEvent e) {}
    }
    
    /**
     * For debugging purposes only
     * @param args
     * @throws UnknownHostException 
     */
    public static void main(String[] args) throws UnknownHostException {
        //Starting connection
        ConnectionManager commsManager = new ConnectionManager();
        
        //User logging in
        User user = new User("fvilches17");
        
        //Building interface (loading panels)
        TextAreaPanel tap = new TextAreaPanel();
        MessageDeliveryPanel mdp = new MessageDeliveryPanel();
        UsersPanel up = new UsersPanel();
        MainPanel mainPanel = new MainPanel(up, mdp, tap);
        
        
        //Starting GUI
        GraphicalInterface gui = new GraphicalInterface(mainPanel, user, commsManager);
    }
}
