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
        Assertions.assertEquals( f0.toString(), "0/1");
        Assertions.assertEquals( f0.toDouble(), 0);
        Assertions.assertEquals( f3.toString(), "5/6" );
        Assertions.assertEquals( f4.toString(), "1/6" );
        Assertions.assertEquals( f5.toString(), "1/6" );
        Assertions.assertEquals( f6.toDouble(), 1.5 );
        Assertions.assertEquals( f7.toString(), "2/1" );
        Assertions.assertEquals( f7.toDouble(), 2);

        Fraction f8 = new Fraction( -2, 4 );
        Fraction f9 = new Fraction( 1, -3 );
        Fraction f10 = f8.plus( f9 );
        Fraction f11 = f8.minus( f9 );
        Fraction f12 = f8.times( f9 );
        Fraction f13 = f8.dividedBy( f9 );
        Fraction f14 = f8.reciprocal();
        Assertions.assertEquals( f8.toString(), "-1/2" );
        Assertions.assertEquals( f8.toDouble(), -0.5);
        Assertions.assertEquals( f9.toString(), "-1/3" );
        Assertions.assertEquals( f10.toString(), "-5/6");
        Assertions.assertEquals( f11.toString(), "-1/6");
        Assertions.assertEquals( f12.toString(), "1/6");
        Assertions.assertEquals( f13.toDouble(), 1.5);
        Assertions.assertEquals( f14.toString(), "-2/1");
        Assertions.assertEquals( f14.toDouble(), -2);

        Fraction f15 = new Fraction( 10, 0 );
//        Fraction f16 = f15.reciprocal();
        Assertions.assertEquals( f15.toString(), "1/0");
//        Assertions.assertEquals( f16.toString(), "0/10");
    }
}