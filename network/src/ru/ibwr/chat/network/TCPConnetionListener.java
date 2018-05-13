package ru.ibwr.chat.network;

public interface TCPConnetionListener {

    void OnConnectionReady(TCPConnection tcpConnection);
    void OnReceiveString(TCPConnection tcpConnection,String value);
    void onDisconnect(TCPConnection tcpConnection);
    void onException(TCPConnection tcpConnection, Exception e);

}
