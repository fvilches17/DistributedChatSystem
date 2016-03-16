package Client;

import Protocol.LogInMessage;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * This is the starting point for the entire application
 * @author Francisco Vilches | Saif Asad
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            //Starting connection. If connection fails, message displays and system terminates
            ConnectionManager connectionManager = new ConnectionManager();
            
            //User logging in
            User user = null;
            boolean loggedIn = false;
            while (!loggedIn) {
                //Getting user (i.e. client) id 
                String userID = JOptionPane.showInputDialog("Enter User ID");
                user = new User(userID);
                loggedIn = connectionManager.checkUserIDAvailability(new LogInMessage(user));
                if (!loggedIn) {
                    JOptionPane.showInputDialog("User ID already exists, try another ID");
                }
            }
            
            //Building interface (loading panels)
            TextAreaPanel tap = new TextAreaPanel();
            MessageDeliveryPanel mdp = new MessageDeliveryPanel();
            UsersPanel up = new UsersPanel();
            MainPanel mainPanel = new MainPanel(up, mdp, tap);
            
            //Starting GUI. Loading main frame.
            GraphicalInterface gui = new GraphicalInterface(mainPanel, user, connectionManager);
            
            //Loading GUI controllers
            Controller controller = new Controller(mainPanel, connectionManager, user);
        });
    }
}
