import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class UserWithData<E> {
    //f(c) = <c.id, c.hash, {c.datas.get(i) tc i 0...c.gata.size()-1}>
    //I(c) = c.id != null && c.hash != null && c.datas != null
    //          && for all 0 <= i < c.datas.size() c.datas.get(i) != null

    private String id;
    private String hash;
    private List<E> datas;
    private List<E> shared_data;

    public UserWithData (String id, String passwd) {
        if (id == null || passwd == null)
            throw new NullPointerException();
        else {
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
            datas = new ArrayList<E>();
            shared_data = new ArrayList<E>();
        }
    }

    public String getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

    public List<E> getDatas (String passwd) throws IncorrectPasswordException {
        if (passwd == null)
            throw new NullPointerException();
        if (checkHash(passwd)) {
            return this.datas;
        }
        else {
            throw new IncorrectPasswordException();
        }

    }

    public List<E> getSharedDatas (String passwd) throws IncorrectPasswordException {
        if (passwd == null)
            throw new NullPointerException();
        if (checkHash(passwd)) {
            return this.shared_data;
        }
        else {
            throw new IncorrectPasswordException();
        }

    }

    public void putShared (E data) {
        if (data == null)
            throw new NullPointerException();
        else
            shared_data.add(data);
    }

    public boolean checkHash (String passwd) {
        if (passwd == null)
            throw new NullPointerException();
        else {
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
}
