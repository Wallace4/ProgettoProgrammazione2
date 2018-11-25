public class User {
    // Fun_User (c) =
    // f(c) = <c.id, c.hash>

    // Inv_User (c) =
    // I(c) = c.id != null && c.hash != null

    private String id;
    private String hash;

    public User (String id, String passwd) {
        if (id == null || passwd == null) {
            throw new NullPointerException();
        } else {
            this.id = id;
            this.hash = Hashing.shaDue(id, passwd);
        }
    }

    public String getId() {
        return id;
    }

    public boolean checkHash (String passwd) {
        if (passwd == null)
            throw new NullPointerException();
        else {
            String hashToCheck = Hashing.shaDue(this.id, passwd);
            return this.hash.equals(hashToCheck);
        }
    }
}
