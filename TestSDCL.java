import java.util.ArrayList;
import java.util.Iterator;

public class TestSDCL {

    public static void main(String[] args) {
        //Qualsiasi dei due si usi, funziona, perché sono implementazioni diverse della stessa interfaccia
        //SecureDataContainer<String> sdcl = new SecureDataContainerDoubleList<String>();
        SecureDataContainer<String> sdcl = new SecureDataContainerList<String>();

        try {
            //creazione utenti
            sdcl.createUser("Michele", "Anima");
            sdcl.createUser("Gabriele", "Luce");
            sdcl.createUser("Alfonso", "Sangue");
            sdcl.createUser("Lorenza", "Vita");
            sdcl.createUser("Daniele", "Vuoto");

            //test eccezione NameAlreadyTakenException
            try {
                sdcl.createUser("Michele", "Tempo");  //throwa NATE
            } catch (NameAlreadyTakenException e) {
                System.out.println("test NATE:");
                System.out.println(e.getMessage());
            }

            //test metodo put
            sdcl.put("Gabriele", "Luce", "Heir");
            sdcl.put("Gabriele", "Luce", "Erede");
            sdcl.put("Gabriele", "Luce", "Magician");
            assert sdcl.getSize("Gabriele", "Luce") == 3;

            //test eccezzione UserNoTFoundException
            try {
                sdcl.put("Francesca", "Pr2", "Professoressa");
            } catch (UserNotFoundException e) {
                System.out.println("test UNFE:");
                System.out.println(e.getMessage());
            }

            //test metodo copy
            sdcl.copy("Gabriele", "Luce", "Heir");
            assert sdcl.getSize("Gabriele", "Luce") == 4;

            //test eccezzione DataNotFoundException
            try {
                sdcl.copy("Gabriele", "Luce", "Hair"); //throwa DNFE
            } catch (DataNotFoundException e) {
                System.out.println("test DNFE:");
                System.out.println(e.getMessage());
            }

            //test remove
            sdcl.put("Michele", "Anima", "Seer");
            sdcl.copy("Michele", "Anima", "Seer");
            sdcl.copy("Michele", "Anima", "Seer");
            sdcl.copy("Michele", "Anima", "Seer");
            sdcl.remove("Michele", "Anima", "Seer");
            sdcl.remove("Michele", "Anima", "Seer");
            assert sdcl.getSize("Michele", "Anima") == 2;

            //test eccezzione IncorrectPasswordException
            try {
                sdcl.remove("Michele", "anima", "Seer");
            } catch (IncorrectPasswordException e) {
                System.out.println("test IPE:");
                System.out.println(e.getMessage());
            }

            //test Shared
            sdcl.share("Gabriele", "Luce", "Alfonso", "Erede");
            sdcl.share("Gabriele", "Luce", "Alfonso", "Heir");
            sdcl.share("Gabriele", "Luce", "Alfonso", "Magician");
            sdcl.share("Michele", "Anima", "Alfonso", "Seer");
            assert sdcl.getSize("Alfonso", "Sangue") == 0;
            assert sdcl.getSharedSize("Alfonso", "Sangue") == 4;

            //test iterator per accettare gli shared
            ArrayList<SharedData<String>> asd = new ArrayList<>();
            for (Iterator<SharedData<String>> it = sdcl.getSharedIterator("Alfonso", "Sangue"); it.hasNext(); ) {
                SharedData<String> sd = it.next();
                if (sd.getOwner().equals("Gabriele")) {
                    //sdcl.insertShared("Alfonso", "Sangue", sd);
                    asd.add(sd);
                }
            }
            //dato che all'intero di insert shared rimuovo gli elementi dalla lista allora devo prima copiarli e poi usare il metodo
            //se no l'iteratore si arrabbia
            for (SharedData<String> sd : asd) {
                sdcl.insertShared("Alfonso", "Sangue", sd);
            }
            assert sdcl.getSize("Alfonso", "Sangue") == 3;
            assert sdcl.getSharedSize("Alfonso", "Sangue") == 1;



        } catch (UserNotFoundException e) {
            System.out.println("UNFE:");
            System.out.println(e.getMessage());
        } catch (IncorrectPasswordException e) {
            System.out.println("IPE:");
            System.out.println(e.getMessage());
        } catch (DataNotFoundException e) {
            System.out.println("DNFE:");
            System.out.println(e.getMessage());
        } catch (NameAlreadyTakenException e) {
            System.out.println("NATE:");
            System.out.println(e.getMessage());
        }


    }
}
