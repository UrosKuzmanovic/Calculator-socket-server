package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args){

        int port = 9000;
        ServerSocket serverSocket;
        Socket soketZaKomunikaciju;

        try {
            serverSocket = new ServerSocket(port);

            while(true) {

                System.out.println("Čekam na konekciju...");
                soketZaKomunikaciju = serverSocket.accept();
                System.out.println("Došlo je do konekcije!");

                ClientHandler client = new ClientHandler(soketZaKomunikaciju);

                client.start();

            }

        } catch (IOException e) {
            System.out.println("Greška prilikom pokretanja servera.");
        }
    }

}
