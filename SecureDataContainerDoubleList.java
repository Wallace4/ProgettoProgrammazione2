import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SecureDataContainerDoubleList<E> implements SecureDataContainer<E> {
    //f(c) = {<c.users.get(i).getId(), c.users.get(i).getHash(),
    //          {c.users_data.get(i).getData()}, {c.users_data.get(i).getShared()}> per ogni i 0...users.size()-1;}

    //I(c) = c.users != null && c.users_data != null && for all i 0..c.users.size()-1 => c.users.get(i) != null
    //       && for all 0 <= i < j < c.users.size() => !c.users.get(i).getId().equals(c.users.get(j).getId())
    //       && for all i 0..c.users_data.size()-1  => (c.users_data.get(i) != null
    //                                              && c.users_data.get(i) instanceof Data
    //                                              && c.users_data.get(i).getData() != null
    //                                              && c.users_data.get(i).getShared() != null)

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
                    throw new NameAlreadyTakenException();
            users.add(new User(id, passw));
            users_data.add(new Data<E>());
        }
    }

    @Override
    public int getSize(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException{
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            for (User u : users) {
                if (owner.equals(u.getId())) {
                    if (u.checkHash(passw)) {
                        return users_data.get(users.indexOf(u)).getData().size();
                    } else {
                        throw new IncorrectPasswordException();
                    }
                }
            }
            throw new UserNotFoundException();
        }
    }

    @Override
    public boolean put(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException{
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            for (User u : users) {
                if (owner.equals(u.getId())) {
                    if (u.checkHash(passw)) {
                        return users_data.get(users.indexOf(u)).getData().add(data);
                    } else {
                        throw new IncorrectPasswordException();
                    }
                }
            }
            throw new UserNotFoundException();
        }
    }

    @Override
    public E get(String owner, String passw, int data) throws UserNotFoundException, IncorrectPasswordException{
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            for (User u : users) {
                if (owner.equals(u.getId())) {
                    if (u.checkHash(passw)) {
                        return users_data.get(users.indexOf(u)).getData().get(data);
                    } else {
                        throw new IncorrectPasswordException();
                    }
                }
            }
            throw new UserNotFoundException();
        }
    }

    @Override
    public E remove(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null || data != null)
            throw new NullPointerException();
        else {
            for (User u : users) {
                if (owner.equals(u.getId())) {
                    if (u.checkHash(passw)) {

                        if (users_data.get(users.indexOf(u)).getData().remove(data))
                            return data; //magari sta cosa è meglio, chiedi a gabri
                        else
                            return null;
                        /*
                        List<E> d = users_data.get(users.indexOf(u)).getData();
                        return d.remove(d.indexOf(data));*/
                    } else {
                        throw new IncorrectPasswordException();
                    }
                }
            }
            throw new UserNotFoundException();
        }

    }

    @Override
    public void copy(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            for (User u : users) {
                if (owner.equals(u.getId())) {
                    if (u.checkHash(passw)) {
                        if (users_data.get(users.indexOf(u)).getData().contains(data))
                            users_data.get(users.indexOf(u)).getData().add(data);
                        else
                            throw new DataNotFoundException();
                    } else {
                        throw new IncorrectPasswordException();
                    }
                }
            }
            throw new UserNotFoundException();
        }
    }

    @Override
    public void share(String owner, String passw, String other, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || other == null || data == null)
            throw new NullPointerException();
        else {
            for (User source : users) {
                if (owner.equals(source.getId())) {
                    if (source.checkHash(passw)) {
                        for (User destination : users) {
                            if (other.equals(destination.getId())) {
                                if (users_data.get(users.indexOf(source)).getData().contains(data)) {
                                    users_data.get(users.indexOf(destination)).getShared().add(data);
                                    return;
                                } else
                                    throw new DataNotFoundException();
                            }
                        }
                        throw new UserNotFoundException("Non è stato trovato other");
                    }
                    else
                        throw new IncorrectPasswordException();
                }
            }
            throw new UserNotFoundException("Non è stato trovato owner");
        }
    }

    @Override
    public Iterator<E> getIterator(String owner, String passw) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            for (User u : users) {
                if (owner.equals(u.getId())) {
                    if (u.checkHash(passw)) {
                        return users_data.get(users.indexOf(u)).getData().iterator();
                    } else {
                        throw new IncorrectPasswordException();
                    }
                }
            }
            throw new UserNotFoundException();
        }
    }

    @Override
    public boolean insertShared(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException, DataNotFoundException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            for (User u: users) {
                if (owner.equals(u.getId())) {
                    if(u.checkHash(passw)) {
                        if (users_data.get(users.indexOf(u)).getShared().remove(data))
                            return users_data.get(users.indexOf(u)).getData().add(data);
                        else
                            throw new DataNotFoundException();
                    } else
                        throw new IncorrectPasswordException();
                }
            }
            throw new UserNotFoundException();
        }
    }

    @Override
    public E getShared(String owner, String passw, int data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null)
            throw new NullPointerException();
        else {
            for (User u: users) {
                if (owner.equals(u.getId())) {
                    if(u.checkHash(passw)) {
                        return users_data.get(users.indexOf(u)).getShared().get((int) data);
                    } else
                        throw new IncorrectPasswordException();
                }
            }
            throw new UserNotFoundException();
        }
    }

    @Override
    public E removeShared(String owner, String passw, E data) throws UserNotFoundException, IncorrectPasswordException {
        if (owner == null || passw == null || data == null)
            throw new NullPointerException();
        else {
            for (User u: users) {
                if (owner.equals(u.getId())) {
                    if(u.checkHash(passw)) {
                        List<E> s = users_data.get(users.indexOf(u)).getShared();
                        return s.remove(s.indexOf(data));
                    } else
                        throw new IncorrectPasswordException();
                }
            }
            throw new UserNotFoundException();
        }
    }
}
