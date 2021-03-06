import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SecureDataContainerDoubleList<E> implements SecureDataContainer<E> {
    /*
    Fun_SecureDataContainerDoubleList (c) =
    f(c) = {<Fun_User(c.users.get(i),
             Fun_Data(c.users_data.get(i)> per ogni i 0...users.size()-1;}

    Inv_SecureDataContainerDoubleList (c) =
    I(c) =   c.users != null && c.users_data != null
          && c.users.size() == c.users_data.size()
          && for all 0 <= i < c.users.size() => c.users.get(i) != null && Inv_User(c.users.get(i)
          && for all 0 <= i < c.users_data.size() => c.users_data.get(i) != null && Inv_Data(c.users_data.get(i))
          && for all 0 <= i < j < c.users.size() => c.users.get(i).getId().equals(c.users.get(j).getId())
          && for all 0 <= i < c.users_data.size() =>
                 for all 0 <= j < c.users_data.get(i).getShared().size() =>
                     exist 0 <= t < c.users.size() =>
                         c.users_data.get(i).getShared.get(j).getOwner().equals(c.users.get(t).getId())
                     && !c.users_data.get(i).getShared.get(j).getOwner().equals(c.users.get(i).getId())
    */


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
            int index = users_data.get(users.indexOf(getUser(owner, passw))).getData().indexOf(data);
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
    public void share(String owner, String passw, String other, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException, SharingToSelfException {
        if (owner == null || passw == null || other == null || data == null)
            throw new NullPointerException();
        else if (owner.equals(other))
            throw new SharingToSelfException(owner +", non puoi condividere dati a te stesso");
        else {
            User source = getUser(owner, passw);
            for (User destination : users) {
                if (other.equals(destination.getId())) {
                    if (users_data.get(users.indexOf(source)).getData().contains(data)) {
                        users_data.get(users.indexOf(destination)).getShared().add(new SharedData<E>(owner, data));
                        return;
                    } else
                        throw new DataNotFoundException("Non è stato trovato il dato nell'insieme di elementi di " + owner);
                }
            }
            throw new UserNotFoundException("Non è stato trovato l'utente: " + other);
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

    /*  REQUIRES: owner != null && passw != null &&
                  exist i tc (owner.equals(users.get(i).getId()) && user.checkHash(passw)== true)
        MODIFIES: null
        EFFECT: restituisce l'user corrispondente al nome utente owner e la password passw
        THROWS: NullPointerException sse owner == null || passw == null
                UserNotFoundException sse owner non è presente nell'istanza della classe
                IncorrectPasswordException sse la password non corrisponde
     */
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
