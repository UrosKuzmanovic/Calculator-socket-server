package server;

import services.Database;

import java.io.*;
import java.net.Socket;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class ClientHandler extends Thread {

    private BufferedReader clientInput = null;
    private PrintStream clientOutput = null;
    private Socket soketZaKomunikaciju;

    public ClientHandler(Socket socket){
        this.soketZaKomunikaciju = socket;
    }

    public void run(){
        try {
            clientInput = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
            clientOutput = new PrintStream(soketZaKomunikaciju.getOutputStream());

            clientOutput.println("Došlo je do konekcije.");
            boolean user = true;

            while(user){

            boolean isLogin = false;
            String username = "";

            while(!isLogin) {
                int opcija = Integer.parseInt(clientInput.readLine());
                username = clientInput.readLine();
                String password = clientInput.readLine();
                if (opcija == 0){
                    if (Database.register(username, password) == 1){
                        clientOutput.println("1");
                    } else
                        clientOutput.println("0");
                }
                if (opcija == 1) { // logovanje
                    if (Database.login(username, password)) {
                        isLogin = true;
                        clientOutput.println("1");
                    } else
                        clientOutput.println("0");
                }
            }
            System.out.println("Korisnik " + username + " se ulogovao.");

                int gostBrojac = 3;
                boolean izadji = false;
                while (!izadji) {
                    int opcija = Integer.parseInt(clientInput.readLine());

                    switch (opcija) {
                        case 0: // exit
                            izadji = true;
                            user = false;
                            soketZaKomunikaciju.close();
                            System.out.println("Korisnik " + username + " je napustio server.");
                            break;

                        case 1: // izracunaj
                            Double rezultat = 0.0;
                            String izraz[] = (clientInput.readLine()).split(" ");
                            if (izraz[1].equals("+")) {
                                rezultat = Double.parseDouble(izraz[0]) + Double.parseDouble(izraz[2]);
                            } else if (izraz[1].equals("-")) {
                                rezultat = Double.parseDouble(izraz[0]) - Double.parseDouble(izraz[2]);
                            } else if (izraz[1].equals("*")) {
                                rezultat = Double.parseDouble(izraz[0]) * Double.parseDouble(izraz[2]);
                            } else if (izraz[1].equals("/")) {
                                rezultat = Double.parseDouble(izraz[0]) / Double.parseDouble(izraz[2]);
                            }
                            if (!username.equals("Gost") || (username.equals("Gost") && gostBrojac > 0)) {
                                clientOutput.println(rezultat);
                                if (username.equals("Gost"))
                                    gostBrojac--;
                                else {
                                    Database.sacuvajRacunanje(username, (izraz[0] + " " + izraz[1] + " " +
                                            izraz[2] + " = " + rezultat));
                                }
                            } else if (username.equals("Gost") && gostBrojac == 0) {
                                clientOutput.println("Registrujte se");
                            }
                            break;

                        case 2: // sacuvaj
                            if (username.equals("Gost")) {
                                clientOutput.println("gost");
                                return;
                            }
                            clientOutput.println("1"); // lista je sacuvana
                            String niz[] = Database.listaRacunanja(username);
                            String naziv = "src/racunanja/racunanje-" + username + ".txt";
                            PrintWriter pw = new PrintWriter(new FileWriter(naziv));
                            pw.println("Istorija računanja korisnika " + username + ":");
                            int i = 0;
                            while (i < niz.length) {
                                pw.println(niz[i++] + "\n");
                            }
                            pw.close();

                            File file = new File(naziv);
                            int velicina = (int) file.length();
                            byte[] bajtovi = new byte[velicina];
                            clientOutput.println(velicina);
                            clientOutput.println(username);
                            FileInputStream fileInputStream = new FileInputStream(file);
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                            bufferedInputStream.read(bajtovi, 0, velicina);
                            OutputStream outputStream = soketZaKomunikaciju.getOutputStream();
                            outputStream.write(bajtovi, 0, velicina);
                            outputStream.flush();
                            fileInputStream.close();
                            bufferedInputStream.close();
                            break;

                        case 3: // izloguj se
                            System.out.println("Korisnik " + username + " se izlogovao.");
                            izadji = true;
                            break;
                    }
                }
            }

            soketZaKomunikaciju.close();
        } catch (IOException e){
            System.out.println("Greska prilikom slanja ili primanja poruke.");
        } catch (NumberFormatException e){
            System.out.println("Klijent je napustio server.");
            clientOutput.close();
            try {
                clientInput.close();
                soketZaKomunikaciju.close();
            } catch (IOException e1) {
                System.out.println("Greska prilikom prekida konekcije.");
            }

        }
    }

}
