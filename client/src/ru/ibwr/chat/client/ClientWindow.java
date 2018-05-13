package ru.ibwr.chat.client;

import ru.ibwr.chat.network.TCPConnection;
import ru.ibwr.chat.network.TCPConnetionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

public class ClientWindow extends JFrame implements ActionListener, TCPConnetionListener{

    private static final String IPADDR = "192.168.1.5";
    private static final int PORT =8189;
    private static final int WIDTH =600;
    private static final int HEIGHT =400;




    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldName = new JTextField();
    private final JTextField fieldInput = new JTextField();
    private TCPConnection connection;


    private ClientWindow (){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        log.setEditable(false);
        log.setLineWrap(true);

        fieldInput.addActionListener(this);

        add(log, BorderLayout.CENTER);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldName, BorderLayout.NORTH);

        setVisible(true);
        try {
            connection= new TCPConnection(this,IPADDR,PORT);
        } catch (IOException e) {
            printMessage("Connection  exception " + e);
        }


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldInput.getText();
        if(msg.equals("")){
            return;
        }else {
            fieldInput.setText(null);
            connection.sendString(fieldName.getText()+ ": " + msg);
        }
    }

    @Override
    public void OnConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready");
    }

    @Override
    public void OnReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection closed");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection  exception " + e);
    }

    private synchronized void printMessage(String msg){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\n");
                log.setCaretPosition(log.getDocument().getLength());
            }
        });
    }
}
