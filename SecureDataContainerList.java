import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SecureDataContainerList<E> implements SecureDataContainer<E> {
    /*
    Fun_SecureDataContainerList(c) =
    f(c) = {<Fun_UserWithData(c.users.get(i))> per ogni i 0..users.size()-1;}

    Inv_SecureDataContainerList (c) =
    I(c) = c.users != null && for all 0 <= i < c.users.size() => c.users.get(i) != null
                                                          && Inv_UserWithData(c.users.get(i))
           && for all 0 <= i < j < c.users.size() => !c.users.get(i).getId().equals(c.users.get(i).getId())
           && for all 0 <= i < c.users.size() =>
                 for all 0 <= j < c.users.get(i).getSharedData(password).size() =>
                     exist 0 <= t < c.users.size() /
                         c.users.get(i).shared_data.get(j).getOwner().equals(c.users.get(t).getId())
                     && !c.users.get(i).shared_data.get(j).getOwner().equals(c.users.get(i).getId())
    */

    private List<UserWithData<E>> users;

    public SecureDataContainerList() {
        this.users = new ArrayList<UserWithData<E>>();
    }

    @Override
    public void createUser(String id, String passw) throws NameAlreadyTakenException {
        if (id == null || passw == null)
            throw new NullPointerException();
        else {
            for (UserWithData<E> u : users)
                if (id.equals(u.getId()))
                    throw new NameAlreadyTakenException("Il nome "+id+" è già stato preso");
            users.add(new UserWithData<E>(id, passw));
        }
    }

    @Override
    public int getSize(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return getUser(owner).getData(passw).size();
        }
    }

    @Override
    public int getSharedSize(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return getUser(owner).getSharedData(passw).size();
        }
    }

    @Override
    public boolean put(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            return getUser(owner).getData(passw).add(data);
        }
    }

    @Override
    public E get(String owner, String passw, int index) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return getUser(owner).getData(passw).get(index);
        }
    }

    @Override
    public E get(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            List<E> d = getUser(owner).getData(passw);
            int index = d.indexOf(data);
            if (index < 0)
                throw new DataNotFoundException();
            else
                return d.get(index);
        }
    }

    @Override
    public E remove(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            if (getUser(owner).getData(passw).remove(data)) //magari sta implementazione è meglio. chiedi a gabri
                return data;
            else
                return null;
        }
    }

    @Override
    public E remove(String owner, String passw, int index) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return getUser(owner).getData(passw).remove(index);
        }
    }

    @Override
    public void copy(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            UserWithData<E> u = getUser(owner);
            if (u.getData(passw).contains(data))
                u.getData(passw).add(data);
            else
                throw new DataNotFoundException("Non è stato trovato il dato nell'insieme di elementi di "+owner);
        }
    }

    @Override
    public void share(String owner, String passw, String other, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException, SharingToSelfException {
        if (owner == null || passw == null || other == null || data == null)
            throw new NullPointerException();
        else if (owner.equals(other))
            throw new SharingToSelfException(owner +", non puoi condividere dati a te stesso");
        else {
            UserWithData<E> source = getUser(owner);
            UserWithData<E> destination = getUser(other);
            if (source.getData(passw).contains(data))
                destination.putShared(owner, data);
            else
                throw new DataNotFoundException("Non è stato trovato il dato nell'insieme di elementi di "+owner);
        }
    }

    @Override
    public Iterator<E> getIterator(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return getUser(owner).getData(passw).iterator();
        }
    }

    @Override
    public boolean insertShared(String owner, String passw, SharedData<E> data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            UserWithData<E> u = getUser(owner);
            if (u.getSharedData(passw).remove(data))
                return u.getData(passw).add(data.getData());
            else
                throw new DataNotFoundException("Non è stato trovato il dato nell'insieme di elementi condivisi con "+owner);
        }
    }

    @Override
    public SharedData<E> getShared(String owner, String passw, int index) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return getUser(owner).getSharedData(passw).get(index);
        }
    }

    @Override
    public SharedData<E> removeShared(String owner, String passw, SharedData<E> data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            if (getUser(owner).getSharedData(passw).remove(data))
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
            return getUser(owner).getSharedData(passw).iterator();
        }
    }

    /*  REQUIRES: owner != null
        MODIFIES: null
        EFFECT: restituisce l'utente corrispondente al nome owner, se presente nell'istanza della classe
        THROWS: NullPointerException sse owner == null
                UserNotFoundException sse l'utente non è presente
     */
    private UserWithData<E> getUser (String owner) throws UserNotFoundException{
        for (UserWithData<E> u : users) {
            if (owner.equals(u.getId())) {
                return u;
            }
        }
        throw new UserNotFoundException("Non è stato trovato l'utente: "+owner);
    }
}
