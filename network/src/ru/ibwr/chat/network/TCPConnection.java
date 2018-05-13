package ru.ibwr.chat.network;


import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxTread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final  TCPConnetionListener eventListener;


    public TCPConnection(TCPConnetionListener eventListener, String ipAddr, int port) throws IOException {
        this(eventListener, new Socket(ipAddr,port));
    }

    public TCPConnection(TCPConnetionListener eventListener, Socket socket) throws IOException {
        this.socket = socket;
        this.eventListener=eventListener;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter((new OutputStreamWriter(socket.getOutputStream(),Charset.forName("UTF-8"))));
        rxTread = new Thread(new Runnable() {
            @Override
            public void run() {
               try {
                   eventListener.OnConnectionReady(TCPConnection.this);
                   while (!rxTread.isInterrupted()){
                      String mag = in.readLine();
                       eventListener.OnReceiveString(TCPConnection.this,mag);
                   }

               }catch (IOException e){
                   eventListener.onException(TCPConnection.this, e);
               }finally {
                   eventListener.onDisconnect(TCPConnection.this);

               }
               }
        });
        rxTread.start();
    }

    public synchronized void sendString(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this,e);
            disconnect();
        }

    }
    public synchronized void disconnect(){
        rxTread.interrupt();
        try {
            socket.close();
        }catch (IOException e){
            eventListener.onException(TCPConnection.this, e);
        }

    }

    @Override
    public String toString() {
        return "TCPConnection: "+ socket.getInetAddress() + ": " + socket.getPort();
    }
}
