import java.util.ArrayList;
import java.util.List;

public class Data<E> {
    /*
    OVERVIEW: Una classe che rappresenta sia i dati appartenuti che quelli condivisi
              con un determinato utente. I dati appartenuti sono contenuti in una lista di tipo E,
              mentre i dati condivisi sono contenuti in una lista di SharedData di tipo E.

    Fun_Data (c) =
    f(c) = <{c.data.get(i) per ogni i 0..c.data.size()-1},
             {<Fun_SharedData(c.shared.get(i))> per ogni i 0..c.shared.size()-1}>

    Inv_Data (c) =
    I(c) = c.data != null && for all 0 <= i < c.data.size() => c.data.get(i) != null &&
           c.shared != null && for all 0 <= i < c.shared.size() => c.shared.get(i) != null
                                                                     Inv_SharedData(c.shared.get(i))
    */

    private List<E> data;
    private List<SharedData<E>> shared;

    public Data() {
        this.data = new ArrayList<E>();
        this.shared = new ArrayList<SharedData<E>>();
    }

    public List<E> getData() {
        return data;
    }

    public List<SharedData<E>> getShared() {
        return shared;
    }
}
