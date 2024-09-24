import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
// Test Class
class FractionTest {
    @Test
    public void runAllTests() {
        // Qualify the assertEquals() with "Assertions." to say that it comes from the Assertions library.
        // Assertions library, as can be seen from the import, is: org.junit.jupiter.api.Assertions.
//        Assertions.assertEquals(3,3); // Dummy assert... put real code here.
        Fraction f0 = new Fraction();
        Fraction f1 = new Fraction(2,4 );
        Fraction f2 = new Fraction( 1, 3 );
        Fraction f3 = f1.plus( f2 );
        Fraction f4 = f1.minus( f2 );
        Fraction f5 = f1.times( f2 );
        Fraction f6 = f1.dividedBy( f2 );
        Fraction f7 = f1.reciprocal();
        Assertions.assertEquals( f0.fractionToString(), "0/1");
        Assertions.assertEquals( f3.fractionToString(), "5/6" );
        Assertions.assertEquals( f4.fractionToString(), "1/6" );
        Assertions.assertEquals( f5.fractionToString(), "1/6" );
        Assertions.assertEquals( f6.toDouble(), 1.5 );
        Assertions.assertEquals( f7.fractionToString(), "2/1" );
    }
}