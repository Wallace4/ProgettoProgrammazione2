import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {
    /*
    OVERVIEW classe contenente vari metodi statici che servono per generare un Hash della password
             in questa implementazione usiamo sia id che password per generare l'hash, in modo da
             non avere due utenti con nomi diversi e password uguali che hanno anche l'hash uguale.
    */


    //Questo metodo cripta la password usando lo SHA-256
    public static String shaDue (String id, String passw) {
        if (id == null || passw == null) {
            throw new NullPointerException();
        } else {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                String stringToHash = id + passw;
                md.update(stringToHash.getBytes());
                return new String(md.digest());
            } catch (NoSuchAlgorithmException e) {  //Questa eccezzione non dovrebbe essere lanciata perché
                e.printStackTrace();                // lo SHA-256 esiste come algoritmo
                return null;
            }
        }
    }

    //Questo metodo cripta la password usando l'MD5
    public static String Md5 (String id, String passw) {
        if (id == null || passw == null) {
            throw new NullPointerException();
        } else {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                String stringToHash = id + passw;
                md.update(stringToHash.getBytes());
                return new String(md.digest());
            } catch (NoSuchAlgorithmException e) {  //Questa eccezzione non dovrebbe essere lanciata perché
                e.printStackTrace();                //l'MD5 esiste come algoritmo
                return null;
            }
        }
    }

}
