import java.util.Objects;

public class SharedData<E> {
    // Fun_SharedData(c) =
    // f(c) = <owner, data>

    // Inv_SharedData(c) =
    // I(c) = owner != null && data != null

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
