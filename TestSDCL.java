import java.util.ArrayList;
import java.util.Iterator;

public class TestSDCL {

    static String[] users = {"Alice", "Bob", "Charlie", "Dennie", "Eva"};
    static String[] pass = {"aaa", "bbb", "ccc", "ddd", "eee"};
    static int n = 5; //numero di utenti utilizzati

    public static void main(String[] args) {
        SecureDataContainer<String> sdcl;

        // Con questo for prima testo tutto SecureDataContainerList e poi SecureDataContainerDoubleList
        // con gli stessi metodi e gli stessi passi
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                sdcl = new SecureDataContainerList<String>();
                System.out.println("Inizio Test SecureDataContainerList");
            }
            else {
                sdcl = new SecureDataContainerDoubleList<String>();
                System.out.println("Inizio Test SecureDataContainerDoubleList");
            }
            System.out.println();

            //Creazione degli utenti nella SDC.
            //Non dovrebbe lanciare eccezzioni perché gli utenti hanno tutti nomi distinti
            try {
                for (int j = 0; j < n; j++) {
                    sdcl.createUser(users[j], pass[j]);
                }
            } catch (NameAlreadyTakenException e) {
                e.printStackTrace();
            }

            //Try-Catch fatto in modo che venga lanciata l'eccezione NameAlreadyTakenException
            try {
                sdcl.createUser("Alice", "AAA");
            } catch (NameAlreadyTakenException e) {
                System.out.println("test NATE:");
                System.out.println(e.getMessage());
            }

            try {
                //TEST metodo put
                assert sdcl.put("Bob", "bbb", "LPP");
                assert sdcl.put("Bob", "bbb", "PR2");
                assert sdcl.put("Bob", "bbb", "PRL");
                assert sdcl.getSize("Bob", "bbb") == 3;

                //TEST eccezione UserNotFoundException
                try {
                    sdcl.put("Francesca", "Pr2", "Professoressa"); //throwa UNFE
                } catch (UserNotFoundException e) {
                    System.out.println("test UNFE:");
                    System.out.println(e.getMessage());
                }

                //TEST metodo copy
                sdcl.copy("Bob", "bbb", "LPP");
                assert sdcl.getSize("Bob", "bbb") == 4;

                //TEST eccezione DataNotFoundException
                try {
                    sdcl.copy("Bob", "bbb", "Lpp"); //throwa DNFE
                } catch (DataNotFoundException e) {
                    System.out.println("test DNFE:");
                    System.out.println(e.getMessage());
                }

                //TEST remove
                assert sdcl.put("Alice", "aaa", "ROB");
                sdcl.copy("Alice", "aaa", "ROB");
                sdcl.copy("Alice", "aaa", "ROB");
                sdcl.copy("Alice", "aaa", "ROB");
                assert sdcl.remove("Alice", "aaa", "ROB").equals("ROB");
                assert sdcl.remove("Alice", "aaa", "ROB").equals("ROB");
                assert sdcl.getSize("Alice", "aaa") == 2;

                //TEST eccezione IncorrectPasswordException
                try {
                    sdcl.remove("Alice", "aaa", "ROB");
                } catch (IncorrectPasswordException e) {
                    System.out.println("test IPE:");
                    System.out.println(e.getMessage());
                }

                //TEST Shared
                sdcl.share("Bob", "bbb", "Charlie", "PR2");
                sdcl.share("Bob", "bbb", "Charlie", "LPP");
                sdcl.share("Bob", "bbb", "Charlie", "PRL");
                sdcl.share("Alice", "aaa", "Charlie", "ROB");
                assert sdcl.getSize("Charlie", "ccc") == 0;
                assert sdcl.getSharedSize("Charlie", "ccc") == 4;

                //TEST iterator per accettare gli shared
                //Note: Qui creo un array dove copio gli elementi trovati con l'iterator,
                //      per poi usarlo per il metodo insertShared. Non posso usare questo metodo mentre scorro
                //      con l'iteratore perché il metodo cancella elementi dalla lista di shared, generando quindi
                //      una ConcurrentModificationException.
                //      Uso quindi questo iterator solo per selezionare gli elementi che mi interessa accettare,
                //      e poi uso un altro for per accettarli definitivamente.
                //      In questo caso accetto solo gli elementi che mi sono stati mandati da Bob
                ArrayList<SharedData<String>> asd = new ArrayList<>();
                for (Iterator<SharedData<String>> it = sdcl.getSharedIterator("Charlie", "ccc"); it.hasNext(); ) {
                    SharedData<String> sd = it.next();
                    if (sd.getOwner().equals("Bob")) {
                        asd.add(sd);
                    }
                }
                for (SharedData<String> sd : asd) {
                    assert sdcl.insertShared("Charlie", "ccc", sd);
                }
                assert sdcl.getSize("Charlie", "ccc") == 3;
                assert sdcl.getSharedSize("Charlie", "ccc") == 1;

                //TEST remove shared
                SharedData<String> sd = new SharedData<String>("Alice", "ROB");
                assert sdcl.removeShared("Charlie", "ccc", sd).equals(sd);
                assert sdcl.getSharedSize("Charlie", "ccc") == 0;

                //TEST get
                assert sdcl.put("Dennie", "ddd", "ALGO");
                assert sdcl.put("Dennie", "ddd", "CPS");
                assert sdcl.getSize("Dennie", "ddd") == 2;
                assert sdcl.get("Dennie", "ddd", "CPS").equals("CPS");
                assert sdcl.get("Dennie", "ddd", 0).equals("ALGO");

                //TEST getShared
                sdcl.share("Dennie", "ddd", "Eva", "CPS");
                assert sdcl.insertShared("Eva", "eee", sdcl.getShared("Eva", "eee", 0));
                assert sdcl.remove("Eva", "eee", "CPS").equals("CPS");
                assert sdcl.getSize("Eva", "eee") == 0;
                assert sdcl.getSharedSize("Eva", "eee") == 0;
                assert sdcl.getSize("Dennie", "ddd") == 2;

                //TEST eccezione SharingToSelfException
                try {
                    sdcl.put("Eva", "eee", "AE");
                    sdcl.share("Eva", "eee", "Eva", "AE");
                } catch (SharingToSelfException e) {
                    System.out.println("test STSE:");
                    System.out.println(e.getMessage());
                }

                System.out.println();

            } catch (UserNotFoundException e) {
                System.out.println("UNFE:");
                e.printStackTrace();
            } catch (IncorrectPasswordException e) {
                System.out.println("IPE:");
                e.printStackTrace();
            } catch (DataNotFoundException e) {
                System.out.println("DNFE:");
                e.printStackTrace();
            } catch (SharingToSelfException e) {
                System.out.println("STSE:");
                e.printStackTrace();
            }
        }


    }
}
