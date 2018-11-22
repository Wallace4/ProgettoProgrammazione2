public class SharedData<E> {
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
}
