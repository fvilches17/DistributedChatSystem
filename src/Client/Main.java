package Client;

import Protocol.LogInMessage;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * This is the starting point for the entire client application
 * @author Francisco Vilches | Saif Asad
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            //Starting connection. If connection fails, message displays and system terminates*
            //*See ClientConnectionManager default contructor
            ClientConnectionManager clientConnManager = new ClientConnectionManager();
            
            //User trying to logging in
            User user = null;
            boolean isLoggedIn = false;
            while (!isLoggedIn) {
                //Getting user (i.e. client) id 
                String userID = JOptionPane.showInputDialog("Enter User ID");
                if (userID == null) //Means user closed input dialog window 
                    System.exit(-1);
                //Checking userID format is correct
                if (userID.length() > 15) {
                    JOptionPane.showMessageDialog(null,"UserID max length is 15");
                    continue;
                }
                user = new User(userID);
                //Checking with chat server if userID is available
                isLoggedIn = clientConnManager.checkUserIDAvailability(new LogInMessage(user));
                if (!isLoggedIn) {
                    JOptionPane.showMessageDialog(null,"User ID already exists, try another ID");
                }
            }
            
            //Building interface (loading panels)
            TextAreaPanel tap = new TextAreaPanel();
            MessageDeliveryPanel mdp = new MessageDeliveryPanel();
            UsersPanel up = new UsersPanel();
            MainPanel mainPanel = new MainPanel(up, mdp, tap);
            
            //Starting GUI. Loading main frame.
            GraphicalInterface gui = new GraphicalInterface(mainPanel, user, clientConnManager);
            
            //Loading GUI controller(controller to also start threads for TCP and UDP message listening)
            Controller controller = new Controller(mainPanel, clientConnManager, user);
        });
    }
}
