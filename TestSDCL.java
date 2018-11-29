import java.util.ArrayList;
import java.util.Iterator;

public class TestSDCL {

    static String[] users = {"Michele", "Gabriele", "Alfonso", "Lorenza", "Daniele"};
    static String[] pass = {"Anima", "Luce", "Sangue", "Vita", "Vuoto"};
    static int n = 5; //numero di utenti utilizzati

    public static void main(String[] args) {

        //SecureDataContainer<String> sdcl = new SecureDataContainerDoubleList<String>();
        //SecureDataContainer<String> sdcl = new SecureDataContainerList<String>();

        SecureDataContainer<String> sdcl;

        // Con questo metodo prima testo tutto SecureDataContainerList e poi SecureDataContainerDoubleList
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
                sdcl.createUser("Michele", "Tempo");
            } catch (NameAlreadyTakenException e) {
                System.out.println("test NATE:");
                System.out.println(e.getMessage());
            }

            try {
                //TEST metodo put
                assert sdcl.put("Gabriele", "Luce", "Heir");
                assert sdcl.put("Gabriele", "Luce", "Erede");
                assert sdcl.put("Gabriele", "Luce", "Magician");
                assert sdcl.getSize("Gabriele", "Luce") == 3;

                //TEST eccezione UserNotFoundException
                try {
                    sdcl.put("Francesca", "Pr2", "Professoressa"); //throwa UNFE
                } catch (UserNotFoundException e) {
                    System.out.println("test UNFE:");
                    System.out.println(e.getMessage());
                }

                //TEST metodo copy
                sdcl.copy("Gabriele", "Luce", "Heir");
                assert sdcl.getSize("Gabriele", "Luce") == 4;

                //TEST eccezione DataNotFoundException
                try {
                    sdcl.copy("Gabriele", "Luce", "Hair"); //throwa DNFE
                } catch (DataNotFoundException e) {
                    System.out.println("test DNFE:");
                    System.out.println(e.getMessage());
                }

                //TEST remove
                assert sdcl.put("Michele", "Anima", "Seer");
                sdcl.copy("Michele", "Anima", "Seer");
                sdcl.copy("Michele", "Anima", "Seer");
                sdcl.copy("Michele", "Anima", "Seer");
                assert sdcl.remove("Michele", "Anima", "Seer").equals("Seer");
                assert sdcl.remove("Michele", "Anima", "Seer").equals("Seer");
                assert sdcl.getSize("Michele", "Anima") == 2;

                //TEST eccezione IncorrectPasswordException
                try {
                    sdcl.remove("Michele", "anima", "Seer");
                } catch (IncorrectPasswordException e) {
                    System.out.println("test IPE:");
                    System.out.println(e.getMessage());
                }

                //TEST Shared
                sdcl.share("Gabriele", "Luce", "Alfonso", "Erede");
                sdcl.share("Gabriele", "Luce", "Alfonso", "Heir");
                sdcl.share("Gabriele", "Luce", "Alfonso", "Magician");
                sdcl.share("Michele", "Anima", "Alfonso", "Seer");
                assert sdcl.getSize("Alfonso", "Sangue") == 0;
                assert sdcl.getSharedSize("Alfonso", "Sangue") == 4;

                //TEST iterator per accettare gli shared
                //Note: Qui creo un array dove copio gli elementi trovati con l'iterator,
                //      per poi usarlo per il metodo insertShared. Non posso usare questo metodo mentre scorro
                //      con l'iteratore perché il metodo cancella elementi dalla lista di shared, generando quindi
                //      una ConcurrentModificationException.
                //      Uso quindi questo iterator solo per selezionare gli elementi che mi interessa accettare,
                //      e poi uso un altro for per accettarli definitivamente.
                //      In questo caso accetto solo gli elementi che mi sono stati mandati da Gabriele
                ArrayList<SharedData<String>> asd = new ArrayList<>();
                for (Iterator<SharedData<String>> it = sdcl.getSharedIterator("Alfonso", "Sangue"); it.hasNext(); ) {
                    SharedData<String> sd = it.next();
                    if (sd.getOwner().equals("Gabriele")) {
                        asd.add(sd);
                    }
                }
                for (SharedData<String> sd : asd) {
                    assert sdcl.insertShared("Alfonso", "Sangue", sd);
                }
                assert sdcl.getSize("Alfonso", "Sangue") == 3;
                assert sdcl.getSharedSize("Alfonso", "Sangue") == 1;

                //TEST remove shared
                SharedData<String> sd = new SharedData<String>("Michele", "Seer");
                assert sdcl.removeShared("Alfonso", "Sangue", sd).equals(sd);
                assert sdcl.getSharedSize("Alfonso", "Sangue") == 0;

                //TEST get
                assert sdcl.put("Lorenza", "Vita", "Sylph");
                assert sdcl.put("Lorenza", "Vita", "Silfide");
                assert sdcl.getSize("Lorenza", "Vita") == 2;
                assert sdcl.get("Lorenza", "Vita", "Silfide").equals("Silfide");
                assert sdcl.get("Lorenza", "Vita", 0).equals("Sylph");

                //TEST getShared
                sdcl.share("Lorenza", "Vita", "Daniele", "Silfide");
                assert sdcl.insertShared("Daniele", "Vuoto", sdcl.getShared("Daniele", "Vuoto", 0));
                assert sdcl.remove("Daniele", "Vuoto", "Silfide").equals("Silfide");
                assert sdcl.getSize("Daniele", "Vuoto") == 0;
                assert sdcl.getSharedSize("Daniele", "Vuoto") == 0;
                assert sdcl.getSize("Lorenza", "Vita") == 2;

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
            }
        }


    }
}
