import java.util.ArrayList;
import java.util.List;

public class UserWithData<E> {
    // Fun_UserWithData (c) =
    // f(c) = <c.id, c.hash,
    //          {c.data.get(i) per ogni i 0...c.data.size()-1},
    //          {<Fun_SharedData(shared_data.get(i))> per ogni i 0...c.shared_data.size()-1}>

    // Inv_UserWithData (c) =
    // I(c) = c.id != null && c.hash != null && c.data != null
    //          && for all 0 <= i < c.data.size() c.data.get(i) != null
    //          && for all 0 <= i < c.shared_data.size() => c.shared_data.get(i) != null
    //                                                      && Inv_SharedData(c.shared_data.get(i))

    private String id;
    private String hash;
    private List<E> data;
    private List<SharedData<E>> shared_data;

    public UserWithData (String id, String passwd) {
        if (id == null || passwd == null)
            throw new NullPointerException();
        else {
            this.id = id;
            this.hash = Hashing.shaDue(this.id, passwd);
            data = new ArrayList<E>();
            shared_data = new ArrayList<SharedData<E>>();
        }
    }

    public String getId() {
        return id;
    }

    public List<E> getData(String passwd) throws IncorrectPasswordException {
        if (passwd == null)
            throw new NullPointerException();
        if (checkHash(passwd)) {
            return this.data;
        }
        else {
            throw new IncorrectPasswordException("La password inserita per "+this.id+" non è corretta");
        }

    }

    public List<SharedData<E>> getSharedData(String passwd) throws IncorrectPasswordException {
        if (passwd == null)
            throw new NullPointerException();
        if (checkHash(passwd)) {
            return this.shared_data;
        }
        else {
            throw new IncorrectPasswordException("La password inserita per "+this.id+" non è corretta");
        }

    }

    public void putShared (String other, E data) {
        if (data == null)
            throw new NullPointerException();
        else
            shared_data.add(new SharedData<E>(other, data));
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
