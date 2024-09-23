////
////  MyFractions.hpp
////  FractionClass
////
////  Created by Brandon Mountan on 9/11/24.
////
//
//#ifndef MyFractions_hpp
//#define MyFractions_hpp
//
//#include <stdio.h>
//#include <string>
//
//#endif /* MyFractions_hpp */
//
//class Fraction {
//private:
//    long numerator;
//    long denominator;
//    void reduce(){
//        long gcd = GCD();
//        numerator = numerator/gcd;
//        denominator = denominator/gcd;
//      };
//    long GCD(){
//        long gcd = abs(numerator);
//        long remainder = abs(denominator);
//        while(remainder != 0) {
//          long temp = remainder;
//          remainder = gcd % remainder;
//          gcd = temp;
//        }
//        return gcd;
//    };
//    Fraction plus(Fraction rhs);
//    Fraction minus(Fraction rhs) const;
//    Fraction times(Fraction rhs) const;
//    Fraction dividedBy(Fraction rhs) const;
//
//public:
//    Fraction();
//    Fraction(long n, long d);
//    Fraction reciprocal();
//    std::string toString();
//    double toDouble();
//    
//    //operators
//    Fraction operator +(Fraction rhs) const;
//    Fraction operator -(Fraction rhs) const;
//    Fraction operator *(Fraction rhs) const;
//    Fraction operator /(Fraction rhs) const;
//    bool operator ==(Fraction rhs) const;
//    bool operator !=(Fraction rhs) const;
//    Fraction& operator +=(Fraction rhs);
//    Fraction& operator -=(Fraction rhs);
//    Fraction& operator /=(Fraction rhs);
//    Fraction& operator *=(Fraction rhs);
//    bool operator <=(Fraction rhs);
//    bool operator >=(Fraction rhs);
//    bool operator <(Fraction rhs);
//    bool operator >(Fraction rhs);
//
//};
