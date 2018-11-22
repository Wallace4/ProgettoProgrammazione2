import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserWithData<E> {
    // f(c) = <c.id, c.hash, {c.datas.get(i) tc i 0...c.gat(i).size()-1}>

    // Inv_UserWithData (c) =
    // I(c) = c.id != null && c.hash != null && c.datas != null
    //          && for all 0 <= i < c.datas.size() c.datas.get(i) != null
    //          && for all 0 <= i < c.shared_data.size() => c.shared_data.get(i) != null

    private String id;
    private String hash;
    private List<E> datas;
    private List<E> shared_data;

    public UserWithData (String id, String passwd) {
        if (id == null || passwd == null)
            throw new NullPointerException();
        else {
            this.id = id;
            this.hash = Hashing.shaDue(this.id, passwd);
            datas = new ArrayList<E>();
            shared_data = new ArrayList<E>();
        }
    }

    public String getId() {
        return id;
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

    public Iterator<E> getMergedIterator (String passwd) throws IncorrectPasswordException {
        if (checkHash(passwd)) {
            return new Iterator<E>() {

                Iterator<E> itrd = datas.iterator();
                Iterator<E> itrs = shared_data.iterator();

                @Override
                public boolean hasNext() {
                    return itrs.hasNext() || itrd.hasNext();
                }

                @Override
                public E next() {
                    if (itrd.hasNext()) { //prova a vedere se si può dire quale dei due restituisci
                        return itrd.next();
                    } else {
                        return itrs.next();
                    }
                }
            };
        } else
            throw new IncorrectPasswordException();
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
            String hashToCheck = Hashing.shaDue(this.id, passwd);
            return hashToCheck.equals(this.hash);
        }
    }
}
