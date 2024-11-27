package assignment07;

/**
 * A good hash functor that minimizes collisions.
 */
public class GoodHashFunctor implements HashFunctor {
    @Override
    public int hash(String item) {
        int hash = 0;
        int prime = 31;
        for (int i = 0; i < item.length(); i++) {
            hash = hash * prime + item.charAt(i);
        }
        return hash;
    }
}
