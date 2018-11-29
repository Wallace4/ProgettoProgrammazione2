import java.util.ArrayList;
import java.util.List;

public class UserWithData<E> {
    /*
    OVERVIEW: Una classe che rappresenta un utente, contenente anche i suoi dati all'interno.
              Questa classe viene utilizzata nell'implementazione SecureDataContainerList
              Usa un metodo di hashing di tipo SHA-256 per criptare la password
    Fun_UserWithData (c) =
    f(c) = <c.id, c.hash,
             {c.data.get(i) per ogni i 0...c.data.size()-1},
             {<Fun_SharedData(shared_data.get(i))> per ogni i 0...c.shared_data.size()-1}>

    Inv_UserWithData (c) =
    I(c) = c.id != null && c.hash != null && c.data != null && c.shared_data != null
             && for all 0 <= i < c.data.size() c.data.get(i) != null
             && for all 0 <= i < c.shared_data.size() => c.shared_data.get(i) != null
                                                      && Inv_SharedData(c.shared_data.get(i))
    */

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

    //Questi sono normali getter ma hanno come parametro la password, infatti restituiscono
    //la lista sse la password passata è corretta.
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


    /*  REQUIRES: other != null && data != null
        MODIFIES: this
        EFFECT: inserisce un nuovo oggetto sharedData formato dai parametri other e data all'interno
                della lista di dati condivisi con l'utente (shared_data)
        THROWS: NullPointerException sse other == null || data == null
     */
    public void putShared (String other, E data) {
        if (data == null)
            throw new NullPointerException();
        else
            shared_data.add(new SharedData<E>(other, data));
    }

    /*  REQUIRES: passwd != null
        MODIFIES: null
        EFFECT: controlla se la password passata come argomento ha lo stesso hash di quella dell'utente.
                Se è così la password corrisponde a quella dell'utente, e restituisce true, se no la password
                è incorretta e restituisce false
        THROWS: NullPointerException sse passwd == null
     */
    private boolean checkHash (String passwd) {
        if (passwd == null)
            throw new NullPointerException();
        else {
            String hashToCheck = Hashing.shaDue(this.id, passwd);
            return hashToCheck.equals(this.hash);
        }
    }
}
