package com.lukeyes.artie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lukeyes.artie.domain.Message;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

public class ChatServer extends WebSocketServer {

    private static ChatServer instance = null;

    private ObjectMapper mapper;

    public static ChatServer getInstance() {
        if(instance == null) {
            try {
                instance = new ChatServer(8080);
                instance.start();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }

    public ChatServer(InetSocketAddress address) {
        super(address);
    }

    private ChatServer(int port) throws UnknownHostException {
        super( new InetSocketAddress(port));
        mapper = new ObjectMapper();
    }

    @Override
    public void onOpen( WebSocket conn, ClientHandshake handshake ) {
      //  this.sendToAll( "new connection: " + handshake.getResourceDescriptor() );
        System.out.println( conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!" );
    }

    @Override
    public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
       // this.sendToAll( conn + " has left the room!" );
        System.out.println( conn + " has left the room!" );
    }

    @Override
    public void onMessage( WebSocket conn, String message ) {
      //  this.sendToAll( message );
        try {
            Message parsedMessage = mapper.readValue(message, Message.class);
            if("server".equals(parsedMessage.recipient)) {
                // process the message here
                System.out.println(parsedMessage.message);

                if("remote".equals(parsedMessage.sender)) {
                    Message outgoingMessage = new Message();
                    outgoingMessage.sender = "server";
                    outgoingMessage.recipient = "client";
                    outgoingMessage.message = parsedMessage.message;
                    String outgoingMessageAsString = mapper.writeValueAsString(outgoingMessage);
                    this.sendToAll(outgoingMessageAsString);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println( conn + ": " + message );
    }

    @Override
    public void onError( WebSocket conn, Exception ex ) {
        ex.printStackTrace();
        if( conn != null ) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    /**
     * Sends <var>text</var> to all currently connected WebSocket clients.
     *
     * @param text
     *            The String to send across the network.
     * @throws InterruptedException
     *             When socket related I/O errors occur.
     */
    void sendToAll(String text) {
        System.out.println("Sending text - " + text);
        Collection<WebSocket> con = connections();
        synchronized ( con ) {
            for( WebSocket c : con ) {
                c.send( text );
            }
        }
    }
}
