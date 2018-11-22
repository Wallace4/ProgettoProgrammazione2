import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Data<E> {
    // f(c) = <{c.data.get(i)    per ogni i 0..c.data.size()-1}, {c.shared.get(i)    per ogni i 0..c.shared.size()-1}

    // Inv_Data (c) =
    // I(c) = c.data != null && for all 0 <= i < c.data.size() c.data.get(i) != null &&
    //       c.shared != null && for all 0 <= i < c.shared.size() c.shared.get(i) != null

    private List<E> data;
    private List<E> shared;

    public Data(List<E> data, List<E> shared) {
        this.data = data;
        this.shared = shared;
    }

    public Data() {
        this.data = new ArrayList<E>();
        this.shared = new ArrayList<E>();
    }

    public List<E> getData() {
        return data;
    }

    public List<E> getShared() {
        return shared;
    }

    public Iterator<E> getMergedIterator () { //ancora non so se è utile per la mia implementazione
        return new Iterator<E>() {

            Iterator<E> itrd = data.iterator();
            Iterator<E> itrs = shared.iterator();

            @Override
            public boolean hasNext() {
                return itrd.hasNext() || itrs.hasNext();
            }

            @Override
            public E next() {
                if (itrd.hasNext()) { //prova a vedere se si può dire quale dei due restituisci
                    return itrd.next();
                } else {
                    return itrs.next();
                }
            }
        };
    }
}
