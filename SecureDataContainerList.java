import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SecureDataContainerList<E> implements SecureDataContainer<E> {
    //f(c) = {<c.users.get(i).getId(), c.users.get(i).getHash(),
    //          {c.users.get(i).getDatas(password)}, {c.users.get(i).getDatas(password)}> per ogni i 0...users.size()-1;}

    //I(c) = c.users != null && for all i 0...c.users.size()-1 => c.users.get(i) != null
    //       && for all 0 <= i < j < c.users.size() => !c.users.get(i).getId().equals(c.users.get(i).getId())



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
            for (UserWithData<E> u : users)
                if (owner.equals(u.getId()))
                    u.getDatas(passw).size();
            throw new UserNotFoundException();
        }
    }

    @Override
    public boolean put(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            for (UserWithData<E> u : users)
                if (owner.equals(u.getId()))
                    return u.getDatas(passw).add(data);
            throw new UserNotFoundException();
        }
    }

    @Override
    public E get(String owner, String passw, int data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            for (UserWithData<E> u : users)
                if (owner.equals(u.getId()))
                    return u.getDatas(passw).get((int) data);
            throw new UserNotFoundException();
        }
    }

    @Override
    public E remove(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            for (UserWithData<E> u : users)
                if (owner.equals(u.getId())) {
                    if (u.getDatas(passw).remove(data)) //magari sta implementazione è meglio. chiedi a gabri
                        return data;
                    else
                        return null;
                    //return u.getDatas(passw).remove(u.getDatas(passw).indexOf(data));
                }
            throw new UserNotFoundException();
        }
    }

    @Override
    public void copy(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            for (UserWithData<E> u : users)
                if (owner.equals(u.getId())) {
                    if (u.getDatas(passw).contains(data))
                        u.getDatas(passw).add(data);
                    else
                        throw new DataNotFoundException();
                }
            throw new UserNotFoundException();
        }
    }

    @Override
    public void share(String owner, String passw, String other, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || other == null || data == null)
            throw new NullPointerException();
        else {
            for (UserWithData<E> source : users)
                if (owner.equals(source.getId())) {
                    for (UserWithData<E> destination : users)
                        if (other.equals(destination.getId())) {
                            if (source.getDatas(passw).contains(data))
                                destination.putShared(data);
                            else
                                throw new DataNotFoundException();
                        }
                    throw new UserNotFoundException("Non è stato trovato other");
                }
            throw new UserNotFoundException("Non è stato trovato owner");
        }
    }

    @Override
    public Iterator<E> getIterator(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            for (UserWithData<E> u : users)
                if (owner.equals(u.getId())) {
                    return u.getDatas(passw).iterator();
                }
            throw new UserNotFoundException();
        }
    }

    @Override
    public boolean insertShared(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            for (UserWithData<E> u : users)
                if (owner.equals(u.getId())) {
                    if (u.getSharedDatas(passw).remove(data))
                        return u.getDatas(passw).add(data);
                    else
                        throw new DataNotFoundException();
                }
            throw new UserNotFoundException();
        }
    }

    @Override
    public E getShared(String owner, String passw, int data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            for (UserWithData<E> u : users)
                if (owner.equals(u.getId())) {
                    return u.getSharedDatas(passw).get((int) data);
                }
            throw new UserNotFoundException();
        }
    }

    @Override
    public E removeShared(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            for (UserWithData<E> u : users)
                if (owner.equals(u.getId())) {
                    if (u.getSharedDatas(passw).remove(data))
                        return data;
                    else
                        return null;
                }
            throw new UserNotFoundException();
        }
    }
}
