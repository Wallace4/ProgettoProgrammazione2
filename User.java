public class User {
    /*
    OVERVIEW: Una classe che rappresenta un utente, con il suo nome e la sua password.
              Usata in nell'implementazione SecureDataContainerDoubelList
              Usa una criptazione della password basata sull'MD5

    Fun_User (c) =
    f(c) = <c.id, c.hash>

    Inv_User (c) =
    I(c) = c.id != null && c.hash != null
    */

    private String id;
    private String hash;

    public User (String id, String passwd) {
        if (id == null || passwd == null) {
            throw new NullPointerException();
        } else {
            this.id = id;
            this.hash = Hashing.Md5(id, passwd);
        }
    }

    public String getId() {
        return id;
    }

    /*  REQUIRES: passwd != null
        MODIFIES: null
        EFFECT: controlla se la password passata come argomento ha lo stesso hash di quella dell'utente.
                Se è così la password corrisponde a quella dell'utente, e restituisce true, se no la password
                è incorretta e restituisce false
        THROWS: NullPointerException sse passwd == null
     */
    public boolean checkHash (String passwd) {
        if (passwd == null)
            throw new NullPointerException();
        else {
            String hashToCheck = Hashing.shaDue(this.id, passwd);
            return this.hash.equals(hashToCheck);
        }
    }
}
