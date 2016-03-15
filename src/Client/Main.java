package Client;

import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //Starting connection
            ConnectionManager connectionManager = new ConnectionManager();
            
            /*
            User user = null;
            try {
                user = new User("fvilches17");
            } catch (UnknownHostException ex) {
            }
            */
            
            //TODO ignore for now
            
            //User logging in
            User user = null;
            boolean loggedIn = false;
            while (!loggedIn) {
                //Getting user name
                String userID = JOptionPane.showInputDialog("Enter User ID");
                try {
                    user = new User("userID");
                    loggedIn = connectionManager.checkUserIDAvailability(new IDCheckMessage(user));
                    if (!loggedIn)
                        JOptionPane.showInputDialog("User ID already exists, try another ID");
                } catch (UnknownHostException ex) {
                    JOptionPane.showMessageDialog(null, "Unknown host error",
                            "Connection Error", JOptionPane.ERROR_MESSAGE);
                    System.err.println(ex);
                    System.exit(-1);
                }
            }
            
            //Building interface (loading panels)
            TextAreaPanel tap = new TextAreaPanel();
            MessageDeliveryPanel mdp = new MessageDeliveryPanel();
            UsersPanel up = new UsersPanel();
            MainPanel mainPanel = new MainPanel(up, mdp, tap);
            
            //Starting GUI
            GraphicalInterface gui = new GraphicalInterface(mainPanel, user, connectionManager);
            
            //Loading GUI controllers
            Controller controller = new Controller(mainPanel, connectionManager, user);
        });

    }
}
