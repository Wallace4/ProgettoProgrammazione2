import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SecureDataContainerList<E> implements SecureDataContainer<E> {
    // f(c) = {<c.users.get(i).getId(), c.users.get(i).getHash(),
    //          {c.users.get(i).getDatas(password)}, {c.users.get(i).getSharedDatas(password)}> per ogni i 0..users.size()-1;}

    // Inv_SecureDataContainerList (c) =
    // I(c) = c.users != null && for all i..c.users.size()-1 => Inv_UserWithData(c.users.get(i))

    List<UserWithData<E>> users;

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
                    throw new NameAlreadyTakenException();
            users.add(new UserWithData<E>(id, passw));
        }
    }

    @Override
    public int getSize(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return getUser(owner).getDatas(passw).size();
        }
    }

    @Override
    public boolean put(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            return getUser(owner).getDatas(passw).add(data);
        }
    }

    @Override
    public E get(String owner, String passw, int data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return getUser(owner).getDatas(passw).get(data);
        }
    }

    @Override
    public E remove(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            if (getUser(owner).getDatas(passw).remove(data)) //magari sta implementazione è meglio. chiedi a gabri
                return data;
            else
                return null;
        }
    }

    @Override
    public void copy(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            UserWithData<E> u = getUser(owner);
            if (u.getDatas(passw).contains(data))
                u.getDatas(passw).add(data);
            else
                throw new DataNotFoundException();
        }
    }

    @Override
    public void share(String owner, String passw, String other, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || other == null || data == null)
            throw new NullPointerException();
        else {
            UserWithData<E> source = getUser(owner);
            UserWithData<E> destination = getUser(other);
            if (source.getDatas(passw).contains(data))
                destination.putShared(data);
            else
                throw new DataNotFoundException();
        }
    }

    @Override
    public Iterator<E> getIterator(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return getUser(owner).getDatas(passw).iterator();
        }
    }

    @Override
    public boolean insertShared(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            UserWithData<E> u = getUser(owner);
            if (u.getSharedDatas(passw).remove(data))
                return u.getDatas(passw).add(data);
            else
                throw new DataNotFoundException();
        }
    }

    @Override
    public E getShared(String owner, String passw, int data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            return getUser(owner).getSharedDatas(passw).get(data);
        }
    }

    @Override
    public E removeShared(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            if (getUser(owner).getSharedDatas(passw).remove(data))
                return data;
            else
                return null;
        }
    }

    private UserWithData<E> getUser (String owner) throws UserNotFoundException{
        for (UserWithData<E> u : users) {
            if (owner.equals(u.getId())) {
                return u;
            }
        }
        throw new UserNotFoundException("Non è stato trovato l'utente: "+owner);
    }
}
