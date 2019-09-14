package services;

import java.sql.*;

public class Database {

    private static String url = "jdbc:mysql://localhost:3306/rmtdatabase";
    private static String user = "root";
    private static String pass = "Ukikuzma96";

    private static boolean isRegister(String username) { // proverava da li postoji korisnicko ime u bazi
        try {
            Connection con = DriverManager.getConnection(url, user, pass);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select count(id) from users where username='" + username + "'");
            rs.next();
            if (rs.getInt(1) == 0)
                return false;
            con.close();
        } catch (SQLException e) {
            System.out.println("Nije moguće konektovati se na MySQL");
        }
        return true;
    }

    public static boolean login(String username, String password) { // proverava da li korisnik moze da se uloguje
        try {
            Connection con = DriverManager.getConnection(url, user, pass);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select password from users where username='" + username + "'");

            if (!rs.next()) {
                System.out.println("Korisnik nije registrovan");
                return false;
            }
            if (rs.getString(1).startsWith(password)) {
                return true;
            } else {
                System.out.println("Šifra nije tačna");
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int register(String username, String password) { // registruje korisnika
        int br = 0;
        if (isRegister(username)){
            return br;
        }
        try {
            Connection con = DriverManager.getConnection(url, user, pass);
            Statement st = con.createStatement();
            br = st.executeUpdate("insert into users (username, password) " +
                    "values ('" + username + "', '" + password + "')");
            con.close();
        } catch (SQLException e) {
            System.out.println("Nije moguće konektovati se na MySQL");
        }
        return br;
    }

    public static void sacuvajRacunanje(String username, String racun){ // cuva racunanje u bazu
        int id = selectID(username);
        try {
            Connection con = DriverManager.getConnection(url, user, pass);
            Statement st = con.createStatement();
            st.executeUpdate("insert into racunanja (id, vrednost) values ('" + id + "', '" + racun + "')");
        } catch (SQLException e) {
            System.out.println("Nije moguće konektovati se na MySQL");
        }
    }

    private static int selectID(String username){ // nalazi ID na osnovu korisnickog imena
        int id = -1;
        try {
            Connection con = DriverManager.getConnection(url, user, pass);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select id from users where username='" + username + "'");
            if (!rs.next()) {
                System.out.println("Korisnik nije registrovan");
                return -1;
            }
            id = rs.getInt(1);
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static String[] listaRacunanja(String username){ // vraca listu svih racunanja korisnika
        String[] niz = new String[brojRacunanja(username)];
        if (brojRacunanja(username) == 0)
            return niz;
        int id = selectID(username);
        int i = 0;
        Connection con;
        try {
            con = DriverManager.getConnection(url, user, pass);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select vrednost from racunanja where id='" + id + "'");
            while (rs.next()){
                niz[i++] = rs.getString(1);
            }
        } catch (SQLException e) {
            System.out.println("Nije moguće konektovati se na MySQL");
        }
        return niz;
    }

    private static int brojRacunanja(String username){ // vraca koliko korisnik ima racunanja
        int br = 0;
        int id = selectID(username);
        try {
            Connection con = DriverManager.getConnection(url, user, pass);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select count(id) from racunanja where id='" + id + "'");
            rs.next();
            br = rs.getInt(1);
            con.close();
        } catch (SQLException e) {
            System.out.println("Nije moguće konektovati se na MySQL");
        }
        return br;
    }

}
