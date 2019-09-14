package services;

public class Provera {

    public static boolean isInRange(int broj){
        if (broj < -2147483648 || broj > 2147483647)
            return false;
        return true;
    }

}
