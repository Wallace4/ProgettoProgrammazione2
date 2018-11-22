import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {
    // OVERVIEW classe contenente vari metodi statici che servono per generare un Hash della password
    //          in questa implementazione usiamo sia id che password per generare l'hash, in modo da
    //          non avere due utenti con nomi diversi e password uguali che hanno pure l'hash uguale.
    //          I metodi restituiscono la password in chiaro (coerente con la specifica) se non viene trovato
    //          l'algoritmo (Cosa che non dovrebbe succedere)

    public static String shaDue (String id, String passw) {
        if (id == null || passw == null) {
            throw new NullPointerException();
        } else {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                String strintToHash = id + passw;
                md.update(strintToHash.getBytes());
                return new String(md.digest());
            } catch (NoSuchAlgorithmException e) {
                return passw; //se l'algoritmo non esiste passa la password in chiaro
            }
        }
    }

}
