public class TestSDCL {

    public static void main(String[] args) {
        SecureDataContainer<String> sdcl = new SecureDataContainerList<String>();

        try {
            sdcl.createUser("Michele", "Anima");
            sdcl.createUser("Gabriele", "Luce");
            sdcl.createUser("Alfonso", "Sangue");
            sdcl.createUser("Lorenza", "Vita");
            sdcl.createUser("Daniele", "Vuoto");

            //sdcl.createUser("Michele", "Tempo");  //throwa NATE

            sdcl.put("Gabriele", "Luce", "Heir");
            assert sdcl.getSize("Gabriele", "Luce") == 1;

            sdcl.copy("Gabriele", "Luce", "Hair"); //throwa UNFE
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
