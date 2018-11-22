public class TestSDCL {

    public static void main(String[] args) {
        SecureDataContainer<String> sdcl = new SecureDataContainerList<String>();

        try {
            sdcl.createUser("Michele", "Anima");
            sdcl.createUser("Gabriele", "Luce");
            sdcl.createUser("Alfonso", "Sangue");
            sdcl.createUser("Lorenza", "Vita");
            sdcl.createUser("Daniele", "Vuoto");
        } catch (NameAlreadyTakenException e) {
            e.printStackTrace();
        }

        try {
            sdcl.createUser("Michele", "Tempo");
        } catch (NameAlreadyTakenException e) {
            System.out.println("Im already tracer");
        }

        try {
            sdcl.put("Gabriele", "Luce", "Heir");
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IncorrectPasswordException e) {
            System.out.println("Attenzione, la password inserita non è corretta");
            e.printStackTrace();
        }

        try {
            sdcl.copy("Gabriele", "Luce", "Hair");
        } catch (UserNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IncorrectPasswordException e) {
            System.out.println("Attenzione, la password inserita non è corretta");
            e.printStackTrace();
        } catch (DataNotFoundException e) {
            System.out.println("Non è stato trovato il dato");
        }


    }
}
