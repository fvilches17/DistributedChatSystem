

package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

/**
 * This object represents the Text Area where the conversations between the client
 * and other users is displayed
 * 
 * @author Francisco Vilches | Saif Assad
 */
public class TextAreaPanel extends JPanel {
    private final TextArea PUBLIC_TEXT_AREA = TextAreaPanel.generateTextArea();
    
    //--------------------------------------------------------------------------
    /**
     * Constructor
     */
    public TextAreaPanel() {
        setLayout(new BorderLayout());
        setAutoscrolls(true);
        add(PUBLIC_TEXT_AREA, BorderLayout.CENTER);
    }
    
    //--------------------------------------------------------------------------
    public TextArea getPUBLIC_TEXT_AREA() {
        return PUBLIC_TEXT_AREA;
    }
    
    //--------------------------------------------------------------------------
    /**
     * Takes in a TextArea object which represents a conversation between client/user.
     * It then removes the currently displayed conversation, and replaces it with
     * this TextArea(conversation).
     * 
     * @param conversation a TextArea object
     */
    public void switchConversation(TextArea conversation) {
        if (this.getComponentCount() == 0) {
            add(conversation);
        } else {
            removeAll();
            revalidate();
            repaint();
            add(conversation);
        }
    }
    
    /**
     * @return a newly created TextArea object which can be used to display a 
     * client/user conversation.
     */
    public static TextArea generateTextArea() {
        //Loading TextArea
        TextArea textArea = new TextArea();
        textArea.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        textArea.setBackground(Color.WHITE);
        textArea.setSize(1000,1000);
        textArea.setEditable(false);
        
        //Adding ScrollBar
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        scrollPane.setAutoscrolls(true);
        scrollPane.setBorder(BorderFactory.createBevelBorder(TitledBorder.DEFAULT_JUSTIFICATION,
                Color.DARK_GRAY, Color.LIGHT_GRAY));
        
        return textArea;
    }
}
