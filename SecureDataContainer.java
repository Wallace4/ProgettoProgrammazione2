import java.util.Iterator;

public interface SecureDataContainer<E> {
    /*
    OVERVIEW: Contenitore di oggetti di tipo E a cui è possibile accedere e modificare i dati solo
              se ne si è autorizzati.
              l'autorizzazione viene da un confrontro tra l'utente e la password (Salvata in chiaro o meno)

    Typical Element: { <id_0, pass_0, {data_0}, {shared_0}>,
                        ..., <id_n-1, pass_n-1, {data_n-1}, {shared_n-1}> }
    Dove: id_i è una stringa contenente l'identificativo di un utente;
          pass_i è la password dell'user i criptata o in chiaro;
          data_i è un insieme di dati appartenenti all'utente i;
          shared_i è un insieme di dati che sono stati condivisi con l'utente i così formato:
                <data_shared_j, data_owner_j> per ogni 0 <= j < m_i
                Dove:
                    data_shared_j è il dato che è stato condiviso all'utente i
                    data_owner_j è l'utente che ha condiviso il dato con l'utente i
                    m_i rappresenta il numero di dati condivisi con l'utente i
          per ogni 0 <= i < n

          Inoltre per ogni 0 <= i < j < n allora id_i != id_j, quindi non ci possono essere User con nomi uguali.
    */


    /*  REQUIRES: id != null && passw != null && id non deve essere già presente nell'insieme degli utenti
        MODIFIES: this
        EFFECTS: Aggiunge a this un nuovo utente di nome id, avente la password passata come argomento.
                 l'insieme di dati iniziali appartenenti e quelli condivisi con id sono vuoti.
        THROWS: NullPointerException sse id == null || passwd == null
                NameAlreadyTakenException sse id è già presente come id nella collezione
     */
    public void createUser (String id, String passw) throws NameAlreadyTakenException;



    /*  REQUIRES: owner != null && passw != null
                  && owner deve essere presente nell'insieme degli utenti && la password deve corrispondere
        MODIFIES: null
        EFFECT: ritorna la cardinalità dell'insieme di oggetti appartenenti a owner sse la password corrisponde
        THROWS: NullPointerException sse owner == null || passwd == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
     */
    public int getSize(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException;


    /*  REQUIRES: owner != null && passw != null
                  && owner deve essere presente nell'insieme degli utenti && la password deve corrispondere
        MODIFIES: null
        EFFECT: ritorna la cardinalità dell'insieme di oggetti condivisi con owner sse la password corrisponde
        THROWS: NullPointerException sse owner == null || passwd == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
     */
    public int getSharedSize(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException;

    /*  REQUIRES: owner != null && passw != null && data != null
                  && owner deve essere presente nell'insieme degli utenti && la password deve corrispondere
        MODIFIES: this
        EFFECT: Inserisce data dentro l'insieme di oggetti appartenenti ad owner sse la password corrisponde
        THROWS: NullPointerException sse owner == null || passwd == null || data == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
     */
    public boolean put(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException;



    /*  REQUIRES: owner != null && passw != null && index >= 0 && index < getSize(owner, passw)
                  && owner deve essere presente nell'insieme degli utenti && la password deve corrispondere
        MODIFIES: null
        EFFECT: ritorna l'oggetto dell'insieme appartenente ad owner e che corrisponde alla posizione index,
                sse la password corrisponde
        THROWS: NullPointerException sse owner == null || passwd == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
                IndexOutOfBoundException sse data < 0 || data >= dimensione dell'insieme di dati appartenenti ad owner
     */
    public E get(String owner, String passw, int index) throws UserNotFoundException, IncorrectPasswordException;



    /*  REQUIRES: owner != null && passw != null && data != null
                  && owner deve essere presente nell'insieme degli utenti && la password deve corrispondere
        MODIFIES: null
        EFFECT: ritorna l'oggetto dell'insieme appartenente ad owner e che corrisponde all'elemento data,
                sse la password corrisponde
        THROWS: NullPointerException sse owner == null || passwd == null || data == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
                DataNotFoundException sse data non fa parte dell'insieme di dati appartenenti ad owner
     */
    public E get(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException;



    /*  REQUIRES: owner != null && passw != null && data != null
                  && owner deve essere presente nell'insieme degli utenti && la password deve corrispondere
        MODIFIES: this
        EFFECT: rimuove la prima occorrenza dell'elemento data dall'insieme di oggetti appartenenti a owner,
                sse la password corrisponde
                restituisce l'oggetto così rimosso, o null se non era presente
        THROWS: NullPointerException sse owner == null || passwd == null || data == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
     */
    public E remove(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException;



    /*  REQUIRES: owner != null && passw != null && index >= 0 && index < getSize(owner, passw)
                  && owner deve essere presente nell'insieme degli utenti && la password deve corrispondere
        MODIFIES: this
        EFFECT: rimuove la prima occorrenza dell'elemento alla posizione index dall'insieme di oggetti
                appartenenti a owner, sse la password corrisponde.
                restituisce l'oggetto così rimosso, o null se non era presente
        THROWS: NullPointerException sse owner == null || passwd == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
                IndexOutOfBoundException sse data < 0 || index >= dimensione dell'insieme di dati condivisi con owner
     */
    public E remove(String owner, String passw, int index) throws UserNotFoundException, IncorrectPasswordException;


    /*  REQUIRES: owner != null && passw != null && data != null
                  && owner deve essere presente nell'insieme degli utenti && la password deve corrispondere
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
                  && owner ed other devono essere presenti nell'insieme degli utenti
                  && la password deve corrispondere
        MODIFIES: this
        EFFECT: Inserisce un oggetto data presente nell'insieme appartenente a owner nell'insieme di shared
                appartenente a other, sse la password corrisponde per owner.
        THROWS: NullPointerException sse owner == null || passwd == null || other == null || data == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
                SharingToSelfException sse owner == other
                DataNotFoundException sse l'oggetto data non è dentro l'insieme di oggetti appartenenti ad owner
     */
    public void share(String owner, String passw, String other, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException, SharingToSelfException;



    /*  REQUIRES: owner != null && passw != null
                  && owner deve essere presente nell'insieme degli utenti && la password deve corrispondere
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
                  && owner deve essere presente nell'insieme degli utenti && la password deve corrispondere
        MODIFIES: this
        EFFECT: Sposta un data da l'insieme di oggetti condivisi con owner all'insieme di oggetti appartenenti ad owner,
                sse la password corrisponde
        THROWS: NullPointerException sse owner == null || passwd == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
                DataNotFoundException sse il data da accettare non è presente nell'insieme di oggetti condivisi con owner
     */
    public boolean insertShared (String owner, String passw, SharedData<E> data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException;



    /*  REQUIRES: owner != null && passw != null && data != null
                  && owner deve essere presente nell'insieme degli utenti && la password deve corrispondere
        MODIFIES: null
        EFFECT: ritorna l'oggetto appartenente all'insieme di dati condivisi con owner,
                sse la password corrisponde. null altrimenti
        THROWS: NullPointerException sse owner == null || passwd == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
                IndexOutOfBoundException sse data < 0 || data >= dimensione dell'insieme di dati condivisi con owner
     */
    public SharedData<E> getShared (String owner, String passw, int index) throws UserNotFoundException, IncorrectPasswordException;



    /*  REQUIRES: owner != null && passw != null && data != null
                  && owner deve essere presente nell'insieme degli utenti && la password deve corrispondere
        MODIFIES: this
        EFFECT: elimina l'oggetto dall'insieme di oggetti condivisi con owner,
                sse la password corrisponde.
                ritorna l'oggetto così eliminato, null altrimenti.
        THROWS: NullPointerException sse owner == null || passwd == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
     */
    public SharedData<E> removeShared (String owner, String passw, SharedData<E> data) throws UserNotFoundException, IncorrectPasswordException;


    /*  REQUIRES: owner != null && passw != null
                  && owner deve essere presente nell'insieme degli utenti && la password deve corrispondere
        MODIFIES: null
        EFFECT: ritorna un iteratore per l'insieme dei dati condivisi con owner, sse la password corrisponde.
                quindi l'iteratore permette di navigare solo per gli shared, e non i dati
        THROWS: NullPointerException sse owner == null || passwd == null
                UserNotFoundException sse non esiste un owner nella collezione
                IncorrectPasswordException sse la password inserita non è quella corretta
     */
    public Iterator<SharedData<E>> getSharedIterator(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException;

}
