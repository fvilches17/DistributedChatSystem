package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.TextArea;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

/**
 * class: MainTextAreaPanel
 * Creates a panel that contains a text area which represents the 
 * are that displays the conversations in the chat room.
 * 
 * Instance Variables:
 *  TextArea, JScrollPane 
 * 
 * Methods:
 *  loadScrollPane(): creates a JScrollPanel objects and adds it to 
 *                   the containing panel 
 * @author Saif Asad
 */
public final class MainTextAreaPanel extends  JPanel{
    
    //------------------------------------------------------------------------------------------
    //Fields
    private final TextArea mainTextArea;
    private JScrollPane scrollPane;
    
    //------------------------------------------------------------------------------------------
    //Constructor
    public MainTextAreaPanel () {
        mainTextArea = new TextArea();
        mainTextArea.setSize(300 , 300);
        add(mainTextArea);
        loadScrollPane();
        
        setLayout(new BorderLayout());
        add(mainTextArea, BorderLayout.CENTER);
    }
    //------------------------------------------------------------------------------------------
    /**
     * Constructs a JScroll pain and adds this class to itself
     */
    public void loadScrollPane() {
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(this);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        scrollPane.setBorder(BorderFactory.createBevelBorder(TitledBorder.DEFAULT_JUSTIFICATION, 
                   Color.DARK_GRAY, Color.LIGHT_GRAY));
    }
    //------------------------------------------------------------------------------------------    
    //Testing
     public static void main(String[] args) {
        JFrame testFrame = new JFrame();
        testFrame.setTitle("Main text area test");
        MainTextAreaPanel mainTextAreaPanel = new MainTextAreaPanel();
        MainPanel mainPanel = new MainPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(mainTextAreaPanel, BorderLayout.CENTER);
        testFrame.add(mainPanel);
        testFrame.setLocationRelativeTo(null);
        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testFrame.setSize(500, 500);
        testFrame.setVisible(true);
    }
}
