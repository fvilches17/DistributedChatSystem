package Client;

import Protocol.LogOutMessage;
import Protocol.PrivateMessage;
import Protocol.PublicMessage;
import Protocol.Message;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public final class Controller {

    private final HashMap<String, TextArea> CONVERSATIONS = new HashMap<>();
    private final MainPanel MAIN_PANEL;
    private final ConnectionManager CONN_MANAGER;
    private final User USER;
    private String recipient = "Public"; //default
    
    //CONSTRUCTOR---------------------------------------------------------------
    public Controller(MainPanel mainPanel, ConnectionManager connManager, User user) {
        //Loading components to control and helper objects
        this.MAIN_PANEL = mainPanel;
        this.CONN_MANAGER = connManager;
        this.USER = user;

        //Adding listener to send button
        JButton sendButton = MAIN_PANEL.getMESSAGE_DELIVERY_PANEL().getSEND_BUTTON();
        sendButton.addActionListener(new SendButtonListener());

        //Adding action listener to public conversation button
        JButton publicUserButton = MAIN_PANEL.getUSERS_PANEL().getPublicButton();
        publicUserButton.addActionListener(new RecipientButtonListener(publicUserButton));

        //Adding keyboard listener to Text field
        JTextField textField = MAIN_PANEL.getMESSAGE_DELIVERY_PANEL().getTEXT_FIELD();
        textField.addKeyListener(new KeyboardListener());
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textField.setText("");
            }
        });

        //By default, adding public user button and text area, will be first to appear
        mainPanel.getUSERS_PANEL().addUserID(publicUserButton);
        CONVERSATIONS.put("Public", MAIN_PANEL.getTEXT_AREA_PANEL().getPUBLIC_TEXT_AREA());

        startMessageListener(); //For TCP message (sends and received messages, and removes logged out users)
        startBroadCastListener(); //For UDP Messages (updates list of logged in users)
    }
    
    //METHODS-------------------------------------------------------------------
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
        String messageTo = textField.getText() + "\n";
        
        //Adding message to conversation text area
        TextArea conversation = CONVERSATIONS.get(recipient);
        Message message;
        conversation.append(messageTo);
        
        //Transmitting this typed message to the server as well. Server to forward message
        if (recipient.equals("Public")) {
            System.out.println("sending public message");
            message = new PublicMessage(messageTo, USER);
        } else {
            System.out.println("sending private message");
            message = new PrivateMessage(messageTo, recipient, USER);
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

    public void startMessageListener() {
        Thread thread = new Thread(new MessageListener());
        thread.start();
    }

    public void startBroadCastListener() {
        Thread thread = new Thread(new BroadCastListener());
        thread.start();
    }
    
    //LISTENERS-----------------------------------------------------------------
    /**
     * Listens for when the client clicks on any button representing a logged in
     * users. When button clicks, the conversation with that user is displayed to
     * the main message text area panel. The client can then send messages to that
     * intended user (i.e. recipient)
     */
    private class RecipientButtonListener implements ActionListener {

        private final JButton RECIPIENT_BUTTON;

        public RecipientButtonListener(JButton recipientButton) {
            this.RECIPIENT_BUTTON = recipientButton;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String recipientUserID = RECIPIENT_BUTTON.getText();
            recipient = recipientUserID;
            TextArea conversation = CONVERSATIONS.get(recipient);
            MAIN_PANEL.getTEXT_AREA_PANEL().switchConversation(conversation);
        }
    }

    private class SendButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            sendMessage();
        }
    }

    private class KeyboardListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_ENTER) {
                sendMessage();
            }
        }
    }
    
    /**
     * Actively listens for incoming messages from server and actions those messages
     * accordingly. Listens via TCP.
     */
    private class MessageListener implements Runnable {

        @Override
        public void run() {
            ObjectInputStream inputStream = CONN_MANAGER.getInputStream();
            while (CONN_MANAGER.isTransmitionLive()) {
                try {
                    //Awaiting message transmition
                    Message senderMessage = (Message) inputStream.readObject(); //blocking

                    //Decoding message and determining proper action
                    String senderUserID = senderMessage.getUSER().getID();
                    String receivedMessage;
                    if (senderMessage instanceof PrivateMessage) {
                        //Appending new message to conversation between sender and user
                        receivedMessage = ((PrivateMessage) senderMessage).toString();
                        CONVERSATIONS.get(senderUserID).append(receivedMessage);

                    } else if (senderMessage instanceof PublicMessage) {
                        //Appending new message to public conversation
                        receivedMessage = ((PublicMessage) senderMessage).toString();
                        CONVERSATIONS.get(senderUserID).append(receivedMessage);

                    } else { // messsage instanceof LogOutMessage (i.e. a particular user logged out)
                        receivedMessage = ((LogOutMessage) senderMessage).toString();
                        CONVERSATIONS.get(senderUserID).append(receivedMessage);
                        CONVERSATIONS.remove(senderUserID);
                        MAIN_PANEL.getUSERS_PANEL().removeUserID(senderUserID); //removes button for this sender
                        JOptionPane.showMessageDialog(null, senderUserID + " has logged out",
                                "User Logout", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (IOException ex) {
                    System.err.println("Could not retrieve message");
                    System.err.println(ex);
                } catch (ClassNotFoundException ex) {
                    System.err.println("Object retrieved not instance of Message");
                    System.err.println(ex);
                }
            }
        }
    }

    /**
     * Listens for recurring UDP messages which transmit a userID string.
     * This tells the client who is currently logged in. If a new user gets
     * logged in then the client will load this new user
     */
    private class BroadCastListener implements Runnable {

        private final int BYTE_LIMIT = 1000;
        private int PORT = 8081;
        private DatagramSocket socket;

        public BroadCastListener() {
            try {
                this.socket = new DatagramSocket(PORT);
            } catch (SocketException ex) {
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
            byte[] buffer = new byte[BYTE_LIMIT];
            DatagramPacket packet = new DatagramPacket(buffer, BYTE_LIMIT);

            while (CONN_MANAGER.isTransmitionLive()) {
                try {
                    socket.receive(packet); //blocking
                    String loggedInUserID = new String(packet.getData());
                    if (!CONVERSATIONS.containsKey(loggedInUserID)) { //Means this is a new user
                        //Creating new conversation
                        CONVERSATIONS.put(loggedInUserID, TextAreaPanel.generateTextArea());
                        //Adding a new button so client can see and select this user if sending messages
                        JButton newUserButton = new JButton(loggedInUserID);
                        newUserButton.setMaximumSize(new Dimension(70, 25));
                        newUserButton.setBackground(Color.WHITE);
                        newUserButton.addActionListener(new RecipientButtonListener(newUserButton));
                        MAIN_PANEL.getUSERS_PANEL().addUserID(newUserButton);
                    }//else ignore (this user is already logged in)

                } catch (IOException ex) {
                    System.err.println("UDP transmition fail");
                    System.err.println(ex);
                } 
            }
            socket.close();
        }
    }
}
