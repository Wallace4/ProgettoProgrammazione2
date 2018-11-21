import java.util.Iterator;

public interface SecureDataContainer<E> {
    /*  OVERVIEW: Contenitore di oggetti di tipo E a cui è possibile accedere e modificare i dati solo
                    se ne si è autorizzati.
                    la password, per motivi di sicurezza, non viene memorizzata all'interno dell'oggetto,
                    ma ne viene memorizzato un hash, che non è visibile nell'attuale visione dell'interfaccia.

        Typical Element: { <id_0, pass_0, {data_0}, {shared_0}>, ..., <id_n-1, pass_n-1, {data_n-1}, {shared_n-1}> }
        Dove: id_i è una stringa contenente l'identificativo di un utente;
              pass_i è la password dell'user i criptata o in chiaro;
              data_i è un insieme di dati appartenenti all'utente i;
              shared_i è un insieme di dati che sono stati condivisi con l'utente i;
              per ogni 0 <= i < n
              Inoltre per ogni 0 <= i < j < n allora id_i != id_j, quindi non ci possono essere User con nomi uguali.
     */


    /*  REQUIRES: id != null && passw != null
        MODIFIES: this
        EFFECTS: Aggiunge a this una nuova quadrupla <id, password, {}, {}>
        THROWS: NullPointerException sse id == null || passwd == null
                NameAlreadyTakenException sse id è già presente come id nella collezione
     */
    public void createUser (String id, String passw) throws NameAlreadyTakenException;



    /*  REQUIRES: owner != null && passw != null
        MODIFIES: null
        EFFECT: ritorna la cardinalità dell'insieme di oggetti appartenenti a owner sse la password corrisponde
        THROWS: NullPointerException sse owner == null || passwd == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
     */
    public int getSize(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException;


    /*  REQUIRES: owner != null && passw != null && data != null
        MODIFIES: this
        EFFECT: Inserisce data dentro l'insieme di oggetti appartenenti ad owner sse la password corrisponde
        THROWS: NullPointerException sse owner == null || passwd == null || data == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
     */
    public boolean put(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException;



    /*  REQUIRES: owner != null && passw != null && data != null
        MODIFIES: null
        EFFECT: ritorna l'oggetto dell'insieme appartenente ad owner e che corrisponde alla posizione data,
                sse la password corrisponde
                in questo caso data viene inteso come l'indice o la chiave della collezione dove è stato
                inserito l'oggetto che si vuole recuperare
        THROWS: NullPointerException sse owner == null || passwd == null || data == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
                IndexOutOfBoundException sse data < 0 || data >= dimensione dell'insieme di dati condivisi con owner

        alla fine ho messo int e lo spiegherò
     */
    public E get(String owner, String passw, int data) throws UserNotFoundException, IncorrectPasswordException;



    /*  REQUIRES: owner != null && passw != null && data != null
        MODIFIES: this
        EFFECT: rimuove l'elemento data dall'insieme di oggetti appartenenti a owner, sse la password corrisponde
                restituisce l'oggetto così rimosso, o null se non era presente
        THROWS: NullPointerException sse owner == null || passwd == null || data == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
     */
    public E remove(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException;



    /*  REQUIRES: owner != null && passw != null && data != null
        MODIFIES: this
        EFFECT: Copia l'oggetto data presente nell'insieme appartenente a owner,
                sse presente e sse la password corrisponde
        THROWS: NullPointerException sse owner == null || passwd == null || data == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
                DataNotFoundException sse non viene trovato il dato da copiare
     */
    public void copy(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException;



    /*  REQUIRES: owner != null && passw != null && other != null && data != null
        MODIFIES: this
        EFFECT: Inserisce un oggetto data presente nell'insieme appartenente a owner nell'insieme di shared
                appartenente a other, sse la password corrisponde per owner.
        THROWS: NullPointerException sse owner == null || passwd == null || other == null || data == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
                DataNotFoundException sse l'oggetto data non è dentro l'insieme di oggetti appartenenti ad owner
     */
    public void share(String owner, String passw, String other, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException;



    /*  REQUIRES: owner != null && passw != null
        MODIFIES: null
        EFFECT: ritorna un iteratore per l'insieme dei dati appartenenti ad owner, sse la password corrisponde.
                quindi l'iteratore permette di navigare solo per i dati, e non gli shared
        THROWS: NullPointerException sse owner == null || passwd == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
     */
    public Iterator<E> getIterator(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException;


    //Metodi extra

    /*  REQUIRES: owner != null && passw != null && data != null
        MODIFIES: this
        EFFECT: Sposta un data da l'insieme di oggetti condivisi con owner all'insieme di oggetti appartenenti ad owner,
                sse la password corrisponde
        THROWS: NullPointerException sse owner == null || passwd == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
                DataNotFoundException sse il data da accettare non è presente nell'insieme di oggetti condivisi con owner
     */
    public boolean insertShared (String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException;



    /*  REQUIRES: owner != null && passw != null && data != null
        MODIFIES: this
        EFFECT: ritorna l'oggetto appartenente all'insieme di dati condivisi con owner,
                sse la password corrisponde. null altrimenti
        THROWS: NullPointerException sse owner == null || passwd == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
                IndexOutOfBoundException sse data < 0 || data >= dimensione dell'insieme di dati condivisi con owner
     */
    public E getShared (String owner, String passw, int data) throws UserNotFoundException, IncorrectPasswordException;



    /*  REQUIRES: owner != null && passw != null && data != null
        MODIFIES: this
        EFFECT: elimina l'oggetto dall'insieme di oggetti condivisi con owner,
                sse la password corrisponde.
                ritorna l'oggetto così eliminato, null altrimenti.
        THROWS: NullPointerException sse owner == null || passwd == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
     */
    public E removeShared (String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException;
}
