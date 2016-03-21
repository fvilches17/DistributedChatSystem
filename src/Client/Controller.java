package Client;

import Protocol.LogInMessage;
import Protocol.LogOutMessage;
import Protocol.PrivateMessage;
import Protocol.PublicMessage;
import Protocol.Message;
import java.awt.Color;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * The purpose of this class is to allow the user to control to interact with the
 * chat server via the Graphical user interface. 
 * 
 * This class is responsible for receiving messages from the server and displaying
 * them on the GUI. Therefore it runs to "Listening" threads. One to listen for 
 * incoming UDP messages from the server, and the other to listen for incoming 
 * TCP messages from the server.
 * 
 * It also allows the user to send messages to the chat server by interacting with
 * the GUI.
 * 
 * @author Francisco Vilches | Saif Asad
 */
public final class Controller {
    //Fields
    private final HashMap<String, TextArea> CONVERSATIONS = new HashMap<>();
    private final MainPanel MAIN_PANEL;
    private final ClientConnectionManager CONN_MANAGER;
    private final User THIS_CLIENT;
    private String recipient = "Public"; //default
    
    //--------------------------------------------------------------------------
    /**
     * This constructor takes in a MainPanel, ClientConnectionManager, and User
     * object. It then declares these object and stores into the class fields.
     * 
     * It also starts all necessary listeners necessary for user/GUI interaction.
     * As well, it starts 2 threads. One which will listen to incoming TCP messages
     * from the server, and the other which will listen for incoming UDP messages
     * from the server as well.
     * 
     * @param mainPanel a main panel which contains all the necessary components
     * for user/server interaction.
     * @param clientConnManager ClientConnectionMagager object which facilitates
     * sending messages to the chat server.
     * @param user the user which is logged in to this session.
     */
    public Controller(MainPanel mainPanel, ClientConnectionManager clientConnManager, User user) {
        //Loading components to control and helper objects
        this.MAIN_PANEL = mainPanel;
        this.CONN_MANAGER = clientConnManager;
        this.THIS_CLIENT = user;

        //Adding listener to send button
        JButton sendButton = MAIN_PANEL.getMESSAGE_DELIVERY_PANEL().getSEND_BUTTON();
        sendButton.addActionListener(new SendButtonListener());

        //Adding action listener to public conversation button
        JButton publicUserButton = MAIN_PANEL.getUSERS_PANEL().getPublicButton();
        publicUserButton.addActionListener(new RecipientButtonListener(publicUserButton));
        //Adding public user button and public conversation text area, will be first to appear
        mainPanel.getUSERS_PANEL().addUserIDButton(publicUserButton);
        CONVERSATIONS.put("Public", MAIN_PANEL.getTEXT_AREA_PANEL().getPUBLIC_TEXT_AREA());

        /**
         * Adding keyboard listener to Text field. Allows the user to send messages
         * just by pressing the 'Enter' keyboard key
         */
        JTextField textField = MAIN_PANEL.getMESSAGE_DELIVERY_PANEL().getTEXT_FIELD();
        textField.addKeyListener(new KeyboardListener());
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textField.setText("");
            }
        });
        
        //Starting communication threads, in order to listen for server TCP & UDP messages.
        startMessageListener(); //For TCP messages
        startBroadCastListener(); //For UDP Messages
    }
    
    //--------------------------------------------------------------------------
    /**
     * Lets the client send the message he/she has typed. Besides sending the
     * message to the server (which forwards it to the intended recipient), it
     * also appends this message to the conversation text area between client and
     * recipient
     */
    private void sendMessage() {
        //Extracting typed in message from text field
        JTextField textField = MAIN_PANEL.getMESSAGE_DELIVERY_PANEL().getTEXT_FIELD();
        if (textField.getText().isEmpty() || textField.getText().startsWith(" ")) {
            return;
        }
        String messageTo = textField.getText();
        
        //Adding message to conversation text area
        TextArea conversation = CONVERSATIONS.get(recipient);
        conversation.append("<<Me>>\n" + messageTo + "\n");
        Message message;
        
        //Transmitting this typed message to the server as well. Server to forward message
        if (recipient.equals("Public")) {
            System.out.println("sending public message");
            message = new PublicMessage(messageTo, THIS_CLIENT);
        } else {
            System.out.println("sending private message");
            message = new PrivateMessage(messageTo, recipient, THIS_CLIENT);
        }
        try {
            CONN_MANAGER.sendMessage(message);
        } catch (IOException ex) {
            System.err.println("Message not sent");
            System.err.println(ex);
        }
        //Resetting text field to blank
        textField.setText("");
    }

    //--------------------------------------------------------------------------
    public void startMessageListener() {
        Thread thread = new Thread(new MessageListener());
        thread.start();
    }

    //--------------------------------------------------------------------------
    public void startBroadCastListener() {
        Thread thread = new Thread(new BroadCastListener());
        thread.start();
    }
    
    //LISTENERS-----------------------------------------------------------------
    //--------------------------------------------------------------------------
    /**
     * Listens for when the client clicks on any button representing a logged in
     * users. When the button is clicked, the conversation with that user is displayed to
     * the main message text area panel. The client can then send messages to that
     * intended user (i.e. recipient).
     * 
     */
    private class RecipientButtonListener implements ActionListener {

        private final JButton RECIPIENT_BUTTON;

        public RecipientButtonListener(JButton recipientButton) {
            this.RECIPIENT_BUTTON = recipientButton;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            /**
             * Updating this class recipient field, to indicate to whom any sent
             * message should be going to. Then switching that text area panel
             * so this client can see the conversation between intended recipient
             * and him/her
            **/
            recipient = RECIPIENT_BUTTON.getText();
            TextArea conversation = CONVERSATIONS.get(recipient);
            MAIN_PANEL.getTEXT_AREA_PANEL().switchConversation(conversation);
            //Marking button color as white, to visually indicate that messages from conversation are read
            MAIN_PANEL.getUSERS_PANEL().changeUserButtonColor(recipient, Color.WHITE);
        }
    }
    
    //--------------------------------------------------------------------------
    /**
     * Listener to be attached to the send button on the main panel. Each time
     * the send button is pressed, this class sendMessage() method is invoked.
     * This method will access the class 'recipient' field and send a message
     * to that recipient
     */
    private class SendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sendMessage();
        }
    }
    
    //--------------------------------------------------------------------------
    /**
     * Listens for keyboard events. Each time the 'Enter' key is pressed, 
     * this class sendMessage() method is invoked. This method will access the 
     * class 'recipient' field and send a message to that recipient.
     */
    private class KeyboardListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_ENTER) {
                sendMessage();
            }
        }
    }
    
    //--------------------------------------------------------------------------
    /**
     * Inner class/thread. Actively listens for incoming messages from server and 
     * actions those messages accordingly. Listens via TCP.
     */
    private class MessageListener implements Runnable {

        @Override
        public void run() {
            while (CONN_MANAGER.isTransmitionLive()) {
                try {
                    //Awaiting message transmition
                    Message senderMessage = (Message) CONN_MANAGER.getInputStream().readObject(); //blocking
                    System.out.println("New " + senderMessage.getClass().getTypeName() + "message received");

                    //Decoding message and determining proper action
                    String senderUserID = senderMessage.getUSER().getID();
                    String receivedMessage;
                    if (senderMessage instanceof PrivateMessage) {
                        //Appending new message to conversation between sender and user
                        receivedMessage = ((PrivateMessage) senderMessage).toString();
                        String receiverUserID = ((PrivateMessage) senderMessage).getUSER().getID().trim();
                        CONVERSATIONS.get(receiverUserID).append(receivedMessage);
                        /**
                         * Checking if this client already has this recipient's conversation
                         * displayed. If not, this recipient's button in the logged in
                         * users panel will be changed to yellow, in order to indicate to the
                         * client that he/she has a new message from this user
                         **/
                        if (!(recipient.equals(senderUserID)))
                            MAIN_PANEL.getUSERS_PANEL().changeUserButtonColor(senderUserID, Color.YELLOW);
                        
                    } else if (senderMessage instanceof PublicMessage) {
                        //Appending new message to public conversation
                        receivedMessage = ((PublicMessage) senderMessage).toString();
                        CONVERSATIONS.get("Public").append(receivedMessage);
                        /**
                         * Checking if the public conversation is already displayed.
                         * If not, the Public button in the logged in users panel
                         * will be changed to yellow, in order to indicate to the
                         * client that there is a new message on the Public conversation
                         */
                        if (!(recipient.equals("Public")))
                            MAIN_PANEL.getUSERS_PANEL().changeUserButtonColor("Public", Color.YELLOW);

                    } else { // messsage instanceof LogOutMessage (i.e. a particular user logged out)
                        receivedMessage = ((LogOutMessage) senderMessage).toString();
                        //Appending logout message to logged out user's conversation
                        CONVERSATIONS.get(senderUserID).append(receivedMessage);
                        //Removing conversation from conversations Map<UserID, Conversation>
                        CONVERSATIONS.remove(senderUserID);
                        //Removing loggedout user's button on logged in users panel
                        MAIN_PANEL.getUSERS_PANEL().removeUserID(senderUserID); 
                        //Indicating to this client that this user has logged out
                        JOptionPane.showMessageDialog(null, senderUserID + " has logged out",
                                "User Logout", JOptionPane.INFORMATION_MESSAGE);
                        //If client was chatting with logged out user, by default, goes back to public conversation
                        if (recipient.equals(senderUserID)) {
                            TextArea publicConversation = CONVERSATIONS.get("Public");
                            MAIN_PANEL.getTEXT_AREA_PANEL().switchConversation(publicConversation);
                            //Changing Public conversation button, to indicate conversation is read
                            MAIN_PANEL.getUSERS_PANEL().changeUserButtonColor("Public", Color.WHITE);
                        }
                    }
                } catch (IOException ex) {
                    /**
                     * Means that the stream between client/chatServer has failed.
                     * Server can no longer interact with the client. Therefore terminating
                     * program. Giving client a visual message
                     */
                    JOptionPane.showMessageDialog(null, "Terminating program",
                                "Unexpected Connection Failure!", JOptionPane.INFORMATION_MESSAGE);
                    System.err.println("Could not retrieve message, connection abruptly closed");
                    System.err.println("Terminating program");
                    System.err.println(ex);
                    System.exit(-1);
                } catch (ClassNotFoundException ex) {
                    System.err.println("Object retrieved not instance of Message");
                    System.err.println(ex);
                    //Ignore, and listen for messages again.
                }
            }
        }
    }
    
    //--------------------------------------------------------------------------
    /**
     * Listens for recurring UDP messages which transmit a userID string.
     * This tells the client which users are currently logged in. If a new 
     * brand new user has logged in then the client will load this new user and
     * display on the GUI
     */
    private class BroadCastListener implements Runnable {

        private final int BYTE_LIMIT = 1000;
        private final int SAIF_PORT = 5555; //TODO remove
        private int PORT = 8081; //TODO, change to 8082
        private DatagramSocket socket;

        private BroadCastListener() {
            try {
                //Setting up socket where the UDP messages will be comming through
                if (THIS_CLIENT.getID().equals("Saif")) {
                    System.out.println("Starting saif port");
                    this.socket = new DatagramSocket(SAIF_PORT); //TODO Remove this
                } else
                    this.socket = new DatagramSocket(PORT);
            } catch (SocketException ex) {
                //Cannot interact with chat server wihout a socket, therefore terminating prog
                System.err.println("Unable to start/create Datagram socket");
                System.err.println(ex);
                JOptionPane.showMessageDialog(null, "Unable to setup Datagram socket",
                        "Socket Error", JOptionPane.ERROR_MESSAGE);
                CONN_MANAGER.closeConnection();
                System.exit(-1);
            }
        }
        @Override
        public void run() {
            while (CONN_MANAGER.isTransmitionLive()) {
                byte[] incomingData = new byte[BYTE_LIMIT];
                DatagramPacket inPacket = new DatagramPacket(incomingData, incomingData.length);
                try {
                    //Waiting for new UDP message
                    socket.receive(inPacket); //blocking
                    //Decoding message
                    String loggedInUserID = new String(inPacket.getData()).trim();
                    //Checking if loggedInUserID is new. If not, skip and listen for new message
                    if (CONVERSATIONS.containsKey(loggedInUserID))
                        continue;
                    //New client!
                    System.out.println("Setting up new user: " + loggedInUserID);
                    //Creating new conversation and appending log in message to conversation panel
                    LogInMessage logInMessage = new LogInMessage(new User(loggedInUserID));
                    CONVERSATIONS.put(loggedInUserID, TextAreaPanel.generateTextArea());
                    CONVERSATIONS.get(loggedInUserID).append(logInMessage.toString());
                    //Adding a new button so client can see and select this user if sending messages
                    JButton newUserButton = new JButton(loggedInUserID);
                    newUserButton.addActionListener(new RecipientButtonListener(newUserButton));
                    MAIN_PANEL.getUSERS_PANEL().addUserIDButton(newUserButton);

                } catch (IOException ex) {
                    System.err.println("UDP transmition fail");
                    System.err.println(ex);
                } 
            }
            socket.close();
        }
    }
}
