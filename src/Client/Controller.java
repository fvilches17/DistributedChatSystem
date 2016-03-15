package Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
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

    public Controller(MainPanel mainPanel, ConnectionManager connManager, User user) {
        //Loading components to control/use
        this.MAIN_PANEL = mainPanel;
        this.CONN_MANAGER = connManager;
        this.USER = user;

        //Adding listener to send button
        JButton sendButton = MAIN_PANEL.getMESSAGE_DELIVERY_PANEL().getSEND_BUTTON();
        sendButton.addActionListener(new SendButtonListener());
        
        //Adding action listener to public conversation button
        JButton publicUserButton = MAIN_PANEL.getUSERS_PANEL().getPublicButton();
        publicUserButton.addActionListener(new UserButtonListener(publicUserButton));
        
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
    
    private void sendMessage() {
        JTextField textField = MAIN_PANEL.getMESSAGE_DELIVERY_PANEL().getTEXT_FIELD();
        if (textField.getText().isEmpty() || textField.getText().startsWith(" "))
            return;
        String messageTo = textField.getText() + "\n";
        TextArea conversation = CONVERSATIONS.get(recipient);
        Message message;
        conversation.append(messageTo);

        if (recipient.equals("Public")) {
            message = new PublicMessage(messageTo, USER);
        } else {
            message = new PrivateMessage(messageTo, recipient, USER);
        }
        try {
            CONN_MANAGER.sendMessage(message);
        } catch (IOException ex) {
            System.err.println("Message not sent");
            System.err.println(ex);
        }
        
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

    private class UserButtonListener implements ActionListener {

        private final JButton USER_BUTTON;

        public UserButtonListener(JButton userButton) {
            this.USER_BUTTON = userButton;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String userID = USER_BUTTON.getText();
            recipient = userID;
            TextArea conversation = CONVERSATIONS.get(userID);
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
            if (keyCode == e.VK_ENTER) {
                sendMessage();
            }
        }
    }

        private class MessageListener implements Runnable {

            UsersPanel UsersPanel = MAIN_PANEL.getUSERS_PANEL();

            @Override
            public void run() {
                Message message = null;
                ObjectInputStream inputStream = CONN_MANAGER.getInputStream();
                while (CONN_MANAGER.isTransmitionLive()) {
                    try {
                        //Awaiting message transmition
                        message = (Message) inputStream.readObject(); //blocking

                    //Decoding message and determining proper action
                        //TODO need to handle possibility that a user might be logged out while sending message
                        String userID = message.getUSER().getID();
                        if (message instanceof PrivateMessage) {
                            //Appending new message to conversation between sender and user
                            String privateMessage = ((PrivateMessage) message).toString();
                            CONVERSATIONS.get(userID).append(privateMessage);

                        } else if (message instanceof PublicMessage) {
                            //Appending new message to public conversation
                            String publicMessage = ((PublicMessage) message).toString();    
                            CONVERSATIONS.get(userID).append(publicMessage);
                            

                        } else { // messsage instanceof DisconectMessage
                            String disconectMessage = ((DisconectMessage) message).toString();
                            CONVERSATIONS.get(userID).append(disconectMessage);
                            CONVERSATIONS.remove(userID);
                            UsersPanel.removeUserID(userID);
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
         * Listens for UDP messages
         */
        private class BroadCastListener implements Runnable {
            private final int BYTE_LIMIT = 1000;
            private int HOST_PORT = 8081;
            private DatagramSocket socket;

            public BroadCastListener() {
                try {
                    this.socket = new DatagramSocket(HOST_PORT);
                } catch (SocketException ex) {
                    System.err.println("Unable to start create Datagram socket");
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
                //ArrayList<User> userList = new ArrayList<>();
                DatagramPacket packet = new DatagramPacket(buffer, BYTE_LIMIT);

                while (CONN_MANAGER.isTransmitionLive()) {
                    try {
                        socket.receive(packet); //blocking
                        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buffer));
                        ArrayList<User> userList = (ArrayList<User>) ois.readObject();
                        /*
                        OR>>>>>>
                        User user = (User) ois.readObject();
                        userList.add(user);
                        String userID = user.getID();
                        //TODO use buffer correctly
                        if (!CONVERSATIONS.containsKey(userID)) { //Means there is a new user
                                CONVERSATIONS.put(userID, TextAreaPanel.generateTextArea());
                                JButton newUser = new JButton(userID);
                                newUser.setMaximumSize(new Dimension(70,25));
                                newUser.setBackground(Color.WHITE);
                                newUser.addActionListener(new UserButtonListener(newUser));
                                MAIN_PANEL.getUSERS_PANEL().addUserID(newUser);
                            }
                        */
                        for (User user : userList) {
                            String userID = user.getID();
                            if (!CONVERSATIONS.containsKey(userID)) { //Means there is a new user
                                CONVERSATIONS.put(userID, TextAreaPanel.generateTextArea());
                                JButton newUser = new JButton(userID);
                                newUser.setMaximumSize(new Dimension(70,25));
                                newUser.setBackground(Color.WHITE);
                                newUser.addActionListener(new UserButtonListener(newUser));
                                MAIN_PANEL.getUSERS_PANEL().addUserID(newUser);
                            }
                        }
                    } catch (IOException ex) {
                        System.err.println("UDP transmition fail");
                        System.err.println(ex);
                    } catch (ClassNotFoundException ex) {
                        System.err.println("Received message was not an Array object of Users");
                        System.err.println(ex);
                    }
                }
            }
        }

    }
