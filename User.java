import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User {
    //f(c) = <c.id, c.hash>
    //I(c) = c.id != null && c.hash != null

    private String id;
    private String hash;

    public User (String id, String passwd) {
        this.id = id;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String strintToHash = this.id + passwd;
            md.update(strintToHash.getBytes());
            this.hash = new String(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("Porco il lama lo sha 256");
        }
    }

    public String getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public boolean checkHash (String passwd) {
        MessageDigest md = null; //Usare BCrypt è troppo scocciante for now
        try {
            md = MessageDigest.getInstance("SHA-256");
            String strintToHash = this.id + passwd;
            md.update(strintToHash.getBytes());
            String hashToCheck = new String(md.digest());
            return this.hash.equals(hashToCheck);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("Porco il lama lo sha 256");
            return false;
        }
    }
}
