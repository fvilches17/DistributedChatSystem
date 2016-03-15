

package Client;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

public class UsersPanel extends JPanel {
    private final JButton PUBLIC_BUTTON = new JButton("Public");
    private final JScrollBar SCROLL_BAR = new JScrollBar();
    private final ArrayList<String> USER_ID_LIST = new ArrayList<>();
    private final Color BACKGROUND_COLOR = new Color(22, 26, 71); //Dark Blue

    public UsersPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BACKGROUND_COLOR);
        PUBLIC_BUTTON.setMaximumSize(new Dimension(70,25));
        PUBLIC_BUTTON.setBackground(Color.WHITE);
        add(PUBLIC_BUTTON);
        //TODO modify scroll bar so that you can scroll if there are more users
    }
    
    public JButton getPublicButton() {
        return PUBLIC_BUTTON;
    }
    
    public void addUserID(JButton newUser) {
        add(newUser);
        USER_ID_LIST.add(newUser.getText());
    }
    
    public void removeUserID(String userID) {
        int componentIndex = USER_ID_LIST.indexOf(userID);
        remove(componentIndex);
    }

    
}
