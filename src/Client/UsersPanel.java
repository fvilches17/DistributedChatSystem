package Client;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Panel object which represents the west component of the main panel.
 * Used to contain buttons which represent users that are logged in to the chat
 * @author Francisco Vilches | Saif Asad
 */
public class UsersPanel extends JPanel {
    //Fields
    private final JButton PUBLIC_BUTTON = new JButton("Public");
    private final int BUTTON_HEIGHT = 25;
    private final int BUTTON_WIDTH = 97;
    private final ArrayList<String> USER_ID_LIST = new ArrayList<>();
    private final Color BACKGROUND_COLOR = new Color(22, 26, 71); //Dark Blue
    
    //--------------------------------------------------------------------------
    /**
     * Constructor
     * By default, adds a Public button, each user will see this when the program
     * Starts
     */
    public UsersPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BACKGROUND_COLOR);
        PUBLIC_BUTTON.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        PUBLIC_BUTTON.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        PUBLIC_BUTTON.setBackground(Color.WHITE);
        add(PUBLIC_BUTTON);
    }
    
    //--------------------------------------------------------------------------
    public JButton getPublicButton() {
        return PUBLIC_BUTTON;
    }
    
    //--------------------------------------------------------------------------
    /**
     * Takes in a JButton and adds it to the panel
     * @param newUserButton a JButton that represents a logged in user 
     */
    public void addUserIDButton(JButton newUserButton) {
        newUserButton.setSize(getWidth(), BUTTON_HEIGHT);
        newUserButton.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        newUserButton.setBackground(Color.WHITE);
        add(newUserButton);
        USER_ID_LIST.add(newUserButton.getText());
        //Refreshing the panel
        revalidate();
        repaint();
    }
    
    //--------------------------------------------------------------------------
    /**
     * Takes in a String representing a user ID, and removes the button related
     * to this user from the panel.
     * @param userID a String representing the user id of a logged in user
     */
    public void removeUserID(String userID) {
        int componentIndex = USER_ID_LIST.indexOf(userID);
        USER_ID_LIST.remove(userID);
        remove(componentIndex);
        //Refreshing the panel
        revalidate();
        repaint();
    }
    
    //--------------------------------------------------------------------------
    /**
     * Takes in a String and a Color object. Changes the background color of the
     * specified user's button.
     * 
     * Color.YELLOW = means this user has received a new message
     * Color.WHITE = when button white, means all messages from this user have
     * been read
     * 
     * @param userID a String representing a logged in user's id.
     * @param color Color object representing the color of the user's button
     */
    public void changeUserButtonColor(String userID, Color color) {
        int componentIndex = USER_ID_LIST.indexOf(userID);
        JButton userButton = (JButton) this.getComponent(componentIndex);
        userButton.setBackground(color);
        revalidate();
        repaint();
    }

    
}