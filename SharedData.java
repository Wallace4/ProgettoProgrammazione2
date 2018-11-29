import java.util.Objects;

public class SharedData<E> {
    /*
    OVERVIEW: wrapper di dati di tipo E che contiene due elementi. Il dato di tipo E condiviso,
              Ed una stringa (owner) contenente il nome utente di chi ha condiviso il dato.

    Fun_SharedData(c) =
    f(c) = <owner, data>

    Inv_SharedData(c) =
    I(c) = owner != null && data != null
    */

    private String owner;
    private E data;

    public SharedData(String owner, E data) {
        this.owner = owner;
        this.data = data;
    }

    public String getOwner() {
        return owner;
    }

    public E getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedData<?> that = (SharedData<?>) o;
        return Objects.equals(owner, that.owner) &&
                Objects.equals(data, that.data);
    }
}
