

package Client;

import Protocol.LogOutMessage;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * This object represents the chat graphical user interface
 * @author Francisco Vilches | Saif Asad
 */
public class GraphicalInterface extends JFrame {
    //Fields
    private final URL LOGO_URL = Main.class.getResource("ImageLibrary/Logo.png");
    private final Image LOGO = new ImageIcon(LOGO_URL).getImage();
    
    //--------------------------------------------------------------------------
    /**
     * Constructor
     * @param mainPanel
     * @param user
     * @param connManager 
     */
    public GraphicalInterface(MainPanel mainPanel, User user, ClientConnectionManager connManager) {
        
        //Defining screen dimension
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Insets screen = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        int taskBarHeight = screen.bottom;
        Dimension interfaceDimension = new Dimension(toolkit.getScreenSize().width/3 + 200,
                (toolkit.getScreenSize().height - taskBarHeight)/2 + 200);
        
        //Loading frame settings
        setTitle("Distributed Chat System - Logged in as..." + user.getID());
        setName("Distributed Chat System");
        setIconImage(LOGO);
        setSize(interfaceDimension);
        int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
        int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
        JScrollPane jsp = new JScrollPane(mainPanel.getUSERS_PANEL(), v, h);
        add(jsp, BorderLayout.WEST);
        getContentPane().add(mainPanel);
        setVisible(true);
        setResizable(false);
        setLocationRelativeTo(null);
        addWindowListener(new WindowCloseEvent(user, connManager));
    }
    
    //--------------------------------------------------------------------------
    /**
     * Inner class which implements WindowListener. When added to the JFrame 
     * (i.e this class) it acts as a default close operation. It's main purpose
     * is to send a LogOutMessage to the chat server in the event of the Client
     * closing the Graphical User Interface.
     */
    private class WindowCloseEvent implements WindowListener {
        
        private final User USER;
        private final ClientConnectionManager CONN_MNGR;

        public WindowCloseEvent(User user, ClientConnectionManager commsManager) {
            this.USER = user;
            this.CONN_MNGR = commsManager;
        }
        
        @Override
        public void windowClosing(WindowEvent e) {
            LogOutMessage logoutMessage = new LogOutMessage(USER);
            try {
                CONN_MNGR.sendMessage(logoutMessage);
                System.out.println("User logged out!");
            } catch (IOException ex) {
                System.err.println("Unable to send logout message");
                System.err.println(ex);
            }
            CONN_MNGR.closeConnection();
            System.out.println("Terminating program");
            System.exit(0);
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
        ClientConnectionManager commsManager = new ClientConnectionManager("localhost");
        
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
