package net.ds.blocking;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiThreadChatServer extends Thread {
    private List<Conversation> conversations = new ArrayList<>();
    private int count;
    public static void main(String[] args) {
        new MultiThreadChatServer().start();
    }

    @Override
    public void run() {
        System.out.println("The server is started using port 1234");
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            while (true) {
                Socket socket = serverSocket.accept();
                ++count;
                Conversation conversation = new Conversation(socket, count);
                conversations.add(conversation);
                conversation.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    class Conversation extends Thread {
        private int connexionId;
        private Socket socket;
        public Conversation(Socket socket, int connexionId) {
            this.socket = socket;
            this.connexionId = connexionId;
        }
        @Override
        public void run() {
            try {
                InputStream inputStream = socket.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                OutputStream outputStream = socket.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream, true);
                String ip = socket.getRemoteSocketAddress().toString();
                System.out.println("New Client Connection => " + connexionId + " IP = " + ip);
                printWriter.println("Welcome, you ID is " + connexionId);
                String request;
                while ((request = bufferedReader.readLine()) != null) {
                    System.out.println("New request => IP = " + ip + " Request = " + request);
                    List<Integer> clientsTo = new ArrayList<>();
                    String message;
                    if (request.contains("=>")) {
                        String[] items = request.split("=>");
                        String clients = items[0];
                        message = items[1];
                        if (clients.contains(",")) {
                            String[] clientIds = clients.split(",");
                            for (String id: clientIds) {
                                clientsTo.add(Integer.parseInt(id));
                            }
                        }
                        else {
                            clientsTo.add(Integer.parseInt(clients));
                        }
                    }
                    else {
                        clientsTo = conversations.stream().map(c -> c.connexionId).collect(Collectors.toList());
                        message = request;
                    }
                    broadcastMessage(message, this, clientsTo);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void broadcastMessage(String message, Conversation from, List<Integer> clientIds) {
        try {
            for (Conversation conversation: conversations) {
                if (conversation != from && clientIds.contains(conversation.connexionId)) {
                    Socket socket = conversation.socket;
                    OutputStream outputStream = socket.getOutputStream();
                    PrintWriter printWriter = new PrintWriter(outputStream, true);
                    printWriter.println(message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
