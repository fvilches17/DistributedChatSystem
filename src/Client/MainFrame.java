package Client;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;


public class MainFrame extends JFrame {
    //FIELDS--------------------------------------------------------------------
    //private final URL LOGO_URL = Main.class.getResource("images/Logo.png");
    //private final Image LOGO = new ImageIcon(LOGO_URL).getImage();
    private final MainPanel MAIN_PANEL = new MainPanel();
    private final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();
    private final Insets SCN_MAX = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
    private final int TASK_BAR_HEIGHT = SCN_MAX.bottom;
    private final Dimension SCREEN_SIZE = new Dimension(TOOLKIT.getScreenSize().width,
                                          TOOLKIT.getScreenSize().height - TASK_BAR_HEIGHT);
    
    //CONSTRUCTOR---------------------------------------------------------------
    public MainFrame() {
        //Initializing and defining frame settings
        setTitle("Distributed Chat System");
        setName("Distributed Chat System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setSize(SCREEN_SIZE);
        //setIconImage(LOGO);
        add(MAIN_PANEL);
        setLocationRelativeTo(null);
    }
    
    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame();
    }
}
