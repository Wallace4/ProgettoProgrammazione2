import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SecureDataContainerDoubleList<E> implements SecureDataContainer<E> {
    // f(c) = {<c.users.get(i).getId(), c.users.get(i).getHash(),
    //          {c.users_data.get(i).getData()},
    //          {c.users_data.get(i).getShared()}>
    //              per ogni i 0...users.size()-1;}

/*  // I(c) = c.users != null && c.users_data != null && for all i 0..c.users.size()-1 => c.users.get(i) != null
    //       && for all 0 <= i < j < c.users.size() => !c.users.get(i).getId().equals(c.users.get(j).getId())
    //       && for all i 0..c.users_data.size()-1  => (c.users_data.get(i) != null
    //                                              && c.users_data.get(i).getData() != null
    //                                              && for all 0 <= j < c.users_data.get(i).getData().size() => c.users_data.get(i).getData().get(j) != null
    //                                              && c.users_data.get(i).getShared() != null
    //                                              && for all 0 <= j < c.users_data.get(i).getShared().size() => c.users_data.get(i).getShared().get(j) != null)
*/
    // Inv_SecureDataContainerDoubleList (c) =
    // I(c) = c.users != null && c.users_data != null
    //       && for all i 0..c.users.size()-1 => c.users.get(i) != null && Inv_User(c.users.get(i)
    //       && for all i 0..c.users_data()-1 => c.users_data.get(i) != null && Inv_Data(c.users_data.get(i))


    private List<User> users;
    private List<Data<E>> users_data;

    public SecureDataContainerDoubleList() {
        users = new ArrayList<User>();
        users_data = new ArrayList<Data<E>>();
    }

    @Override
    public void createUser(String id, String passw) throws NameAlreadyTakenException {
        if (id == null || passw == null)
            throw new NullPointerException();
        else {
            for (User u : users)
                if (u.getId().equals(id))
                    throw new NameAlreadyTakenException("Il nome "+id+" è già stato preso");
            users.add(new User(id, passw));
            users_data.add(new Data<E>());
        }
    }

    @Override
    public int getSize(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException{
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            //getUser restituisce le eccezzioni o l'elemento User corrispondente, poi ne viene trovato l'indice
            //e si accede al data corrispondente nell'altra lista. Poi si ritorna la size
            return users_data.get(users.indexOf(getUser(owner, passw))).getData().size();
        }
    }

    @Override
    public int getSharedSize(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            //getUser restituisce le eccezzioni o l'elemento User corrispondente, poi ne viene trovato l'indice
            //e si accede al data corrispondente nell'altra lista. Poi si ritorna la size
            return users_data.get(users.indexOf(getUser(owner, passw))).getShared().size();
        }
    }

    @Override
    public boolean put(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException{
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            return users_data.get(users.indexOf(getUser(owner, passw))).getData().add(data);
        }
    }

    @Override
    public E get(String owner, String passw, int index) throws UserNotFoundException, IncorrectPasswordException{
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return users_data.get(users.indexOf(getUser(owner, passw))).getData().get(index);
        }
    }

    @Override
    public E get(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            int index = users_data.indexOf(data);
            if (index < 0)
                throw new DataNotFoundException();
            else
                return users_data.get(users.indexOf(getUser(owner, passw))).getData().get(index);
        }
    }

    @Override
    public E remove(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            if (users_data.get(users.indexOf(getUser(owner, passw))).getData().remove(data))
                return data; //magari sta cosa è meglio, chiedi a gabri
            else
                return null;
        }

    }

    @Override
    public E remove(String owner, String passw, int index) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return users_data.get(users.indexOf(getUser(owner, passw))).getData().remove(index);
        }
    }

    @Override
    public void copy(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            User u = getUser(owner, passw);
            if (users_data.get(users.indexOf(u)).getData().contains(data))
                users_data.get(users.indexOf(u)).getData().add(data);
            else
                throw new DataNotFoundException("Non è stato trovato il dato nell'insieme di elementi di "+owner);
        }
    }

    @Override
    public void share(String owner, String passw, String other, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || other == null || data == null)
            throw new NullPointerException();
        else {
            User source = getUser(owner, passw);
            for (User destination : users) {
                if (other.equals(destination.getId())) {
                    if (users_data.get(users.indexOf(source)).getData().contains(data)) {
                        users_data.get(users.indexOf(destination)).getShared().add(new SharedData<E>(owner, data));
                        return;
                    } else
                        throw new DataNotFoundException("Non è stato trovato il dato nell'insieme di elementi di "+owner);
                }
            }
            throw new UserNotFoundException("Non è stato trovato l'utente: "+other);
        }
    }

    @Override
    public Iterator<E> getIterator(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return users_data.get(users.indexOf(getUser(owner, passw))).getData().iterator();
        }
    }

    @Override
    public boolean insertShared(String owner, String passw, SharedData<E> data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            User u = getUser(owner, passw);
            if (users_data.get(users.indexOf(u)).getShared().remove(data))
                return users_data.get(users.indexOf(u)).getData().add(data.getData());
            else
                throw new DataNotFoundException("Non è stato trovato il dato nell'insieme di elementi condivisi con "+owner);
        }
    }

    @Override
    public SharedData<E> getShared(String owner, String passw, int index) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return users_data.get(users.indexOf(getUser(owner, passw))).getShared().get(index);
        }
    }

    @Override
    public SharedData<E> removeShared(String owner, String passw, SharedData<E> data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            if (users_data.get(users.indexOf(getUser(owner, passw))).getShared().remove(data))
                return data;
            else
                return null;
        }
    }

    @Override
    public Iterator<SharedData<E>> getSharedIterator(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return users_data.get(users.indexOf(getUser(owner, passw))).getShared().iterator();
        }
    }

    private User getUser (String owner, String passw) throws UserNotFoundException, IncorrectPasswordException{
        for (User u : users) {
            if (owner.equals(u.getId())) {
                if (u.checkHash(passw)) {
                    return u;
                } else
                    throw new IncorrectPasswordException("La password inserita per "+owner+" non è corretta");
            }
        }
        throw new UserNotFoundException("Non è stato trovato l'utente: "+owner);
    }
}
