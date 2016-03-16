

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


public class TextAreaPanel extends JPanel {
    private final TextArea PUBLIC_TEXT_AREA = TextAreaPanel.generateTextArea();
    
    //CONSTRUCTOR---------------------------------------------------------------
    public TextAreaPanel() {
        setLayout(new BorderLayout());
        add(PUBLIC_TEXT_AREA, BorderLayout.CENTER);
    }
    
    //GETTERS-------------------------------------------------------------------
    public TextArea getPUBLIC_TEXT_AREA() {
        return PUBLIC_TEXT_AREA;
    }
    
    //METHODS-------------------------------------------------------------------
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
