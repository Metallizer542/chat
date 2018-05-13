package ru.ibwr.server;

import ru.ibwr.chat.network.TCPConnection;
import ru.ibwr.chat.network.TCPConnetionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnetionListener{

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    public static void main(String[] args) {
        new ChatServer();
    }
    private ChatServer (){
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)){
            while (true){
                try {
                    new TCPConnection(this, serverSocket.accept());

                }catch (IOException e){
                    System.out.println("TCPConnection exception: " + e);
                }
            }

        }catch (IOException e){
            throw new RuntimeException(e);
        }


    }


    @Override
    public synchronized void OnConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendAllConnections("Client connected: " + tcpConnection);
    }

    @Override
    public  synchronized void OnReceiveString(TCPConnection tcpConnection, String value) {
        sendAllConnections(value);
    }

    @Override
    public  synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendAllConnections("Client disconnected: " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception" + e);
    }

    private void sendAllConnections(String value){
        System.out.println(value);
        for (int i = 0; i <connections.size() ; i++) {
            connections.get(i).sendString(value);

        }
    }
}
