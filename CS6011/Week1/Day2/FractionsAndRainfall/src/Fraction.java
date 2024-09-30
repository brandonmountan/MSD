import static java.lang.Math.abs;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Fraction implements Comparable<Fraction> {
    long numerator;
    long denominator;

// Fractions should have the following private methods (along with any other that you need to add):
    long GCD() {
// Returns the greatest common divisor of this fraction's numerator and denominator. This is a helper method for the reduce method, below.
// Use the following iterative algorithm to compute the GCD:
        long gcd = abs(numerator);
        long remainder = abs(denominator);
        while (remainder != 0) {
            long temp = remainder;
            remainder = gcd % remainder;
            gcd = temp;
        }
        return gcd;
    }

    // Changes this fraction to its reduced form.
    void reduce() {
        long gcd = GCD();
        numerator = numerator / gcd;
        denominator = denominator / gcd;
    }

    // The default constructor, which sets the value of the fraction to 0/1. (Note, a constructor is similar to a method, but not actually a method.)
    public Fraction() {
        numerator = 0;
        denominator = 1;
    }

    // A constructor which sets the value of the fraction to a specific numerator (n) and denominator (d).
    public Fraction(long n, long d) {
        numerator = n;
        denominator = d;
        if (d < 0) {
            denominator = denominator * -1;
            numerator = numerator * -1;
        } else if (d == 0) {
            try {
                System.out.println(n/d);
            } catch (ArithmeticException e) {
                System.out.println("Cannot divide by 0!");
            }
        }
        reduce();
    }

    // Returns a new fraction that is the result of the right hand side (rhs) fraction added to this fraction.
    public Fraction plus(Fraction rhs) {
        long num = (numerator * rhs.denominator) + (denominator * rhs.numerator);
        long den = (denominator * rhs.denominator);
        return new Fraction(num, den);
    }

    // Returns a new fraction that is the result of the right hand side (rhs) fraction subtracted from this fraction.
    public Fraction minus(Fraction rhs) {
        long num = (numerator * rhs.denominator) - (denominator * rhs.numerator);
        long den = (denominator * rhs.denominator);
        return new Fraction(num, den);
    }

    // Returns a new fraction that is the result of this fraction multiplied by the right hand side (rhs) fraction.
    public Fraction times(Fraction rhs) {
        long num = numerator * rhs.numerator;
        long den = denominator * rhs.denominator;
        return new Fraction(num, den);
    }

    // Returns a new fraction that is the result of this fraction divided by the right hand side (rhs) fraction.
    public Fraction dividedBy(Fraction rhs) {
        long num = numerator * rhs.denominator;
        long den = denominator * rhs.numerator;
        return new Fraction(num, den);
    }

    // Returns a new fraction that is the reciprocal of this fraction.
    public Fraction reciprocal() {
        long num = denominator;
        long den = numerator;
        return new Fraction(num, den);
    }

    // Returns a string representing this fraction. The string should have the format: "N/D", where N is the numerator, and D is the denominator.
    public String toString() {
        return (numerator) + "/" + (denominator);
    }

    // This method should always print the reduced form of the fraction.
    // If the fraction is negative, the sign should be displayed on the numerator, e.g., "-1/2" not "1/-2".
    // Note: once you implement this, you will be able to print a Fraction with println... more on how this works later.
    // Returns a (double precision) floating point number that is the approximate value of this fraction, printed as a real number.
    public double toDouble() {
        double num = numerator;
        double den = denominator;
        return num / den;
    }

    @Override
    public int compareTo(Fraction o) {
        double f1 = this.toDouble();
        double f2 = o.toDouble();
//        if (f1 < f2) {
//            return -1;
//        } else if (f1 > f2) {
//            return 1;
//        } else if (f1 == f2) {
//            return 0;
//        }
//        return 0;
        return Double.compare(f1, f2);
    }


    public static void main(String[] args) {
        Fraction myObj = new Fraction();
        System.out.println(myObj.numerator);
        System.out.println(myObj.denominator);
        Fraction myObj1 = new Fraction(2, 4);
        Fraction myObj2 = new Fraction(5, 6);
        Fraction myObj3 = myObj1.plus(myObj2);
        System.out.println(myObj3.numerator);
        System.out.println(myObj3.denominator);
    }
}
