public class TestSDCL {

    public static void main(String[] args) {
        SecureDataContainer<String> sdcl = new SecureDataContainerDoubleList<String>();
        sdcl.createUser("Gabriele", "1535");
        try {
            sdcl.copy("Gabriele", "1535", null);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        } catch (IncorrectPasswordException e) {
            e.printStackTrace();
        }
    }
}
