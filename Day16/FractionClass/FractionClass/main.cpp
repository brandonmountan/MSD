//
//  main.cpp
//  FractionClass
//
//  Created by Brandon Mountan on 9/11/24.
//

#include <iostream>
#include <cmath>

class Fraction {
private:
    long numerator;
    long denominator;
    void reduce(){
        long gcd = GCD();
        numerator = numerator/gcd;
        denominator = denominator/gcd;
      };
    long GCD(){
        long gcd = abs(numerator);
        long remainder = abs(denominator);
        while(remainder != 0) {
          long temp = remainder;
          remainder = gcd % remainder;
          gcd = temp;
        }
        return gcd;
    };

public:
    Fraction(){
        numerator = 0;
        denominator = 1;
    };
    Fraction(long n, long d){
        numerator = n;
        denominator = d;
        if (denominator == 0){
            std::cerr << "error: denominator cannot be zero" << std::endl;
        } else if (denominator < 0 && numerator > 0){
            numerator *= -1;
            denominator *= -1;
        } else if (denominator < 0 && numerator < 0){
            numerator *= -1;
            denominator *= -1;
        }
        reduce();
    };
    Fraction plus(Fraction& rhs) const{
        long num = (numerator*rhs.denominator) + (rhs.numerator*denominator);
        long den = denominator * rhs.denominator;
        return Fraction(num, den);
    };
    Fraction minus(Fraction rhs) const{
        long num = (numerator*rhs.denominator) - (rhs.numerator*denominator);
        long den = denominator * rhs.denominator;
        return Fraction(num, den);
    };
    Fraction times(Fraction rhs) const{
        long num = numerator * rhs.numerator;
        long den = denominator * rhs.denominator;
        return Fraction(num, den);
    };
    Fraction dividedBy(Fraction rhs) const{
        long num = numerator * rhs.denominator;
        long den = denominator * rhs.numerator;
        return Fraction(num, den);
    };
    Fraction reciprocal(){
        long num = denominator;
        long den = numerator;
        return Fraction(num, den);
    };
    std::string toString(){
        return std::to_string(numerator) + "/" + std::to_string(denominator);
      };
    double toDouble(){
        double n = numerator;
        double d = denominator;
        double result = n / d;
        return result;
    };
    //operators
    Fraction operator +(Fraction rhs) const {
        return plus(rhs);
    };
    Fraction operator -(Fraction rhs) const {
        return minus(rhs);
    };
    Fraction operator *(Fraction rhs) const {
        return times(rhs);
    };
    Fraction operator /(Fraction rhs) const {
        return dividedBy(rhs);
    };
    bool operator ==(Fraction rhs) const {
        return (rhs.numerator == numerator && rhs.denominator == denominator);
    };
    bool operator !=(Fraction rhs) const {
        return (rhs.numerator != numerator && rhs.denominator != denominator);
    };
    Fraction& operator +=(Fraction rhs) {
        numerator += rhs.numerator;
        denominator += rhs.denominator;
        return *this;
    };
    Fraction& operator -=(Fraction rhs) {
        numerator -= rhs.numerator;
        denominator -= rhs.denominator;
        return *this;
    };
    Fraction& operator /=(Fraction rhs) {
        numerator /= rhs.numerator;
        denominator /= rhs.denominator;
        return *this;
    };
    Fraction& operator *=(Fraction rhs) {
        numerator *= rhs.numerator;
        denominator *= rhs.denominator;
        return *this;
    };
    bool operator <=(Fraction rhs) {
        //is 'this' less than or equal to rhs
        return minus(rhs).numerator <= 0;
    };
    bool operator >=(Fraction rhs) {
        //is 'this' greater than or equal to rhs.
        return minus(rhs).numerator >= 0;
    };
    bool operator <(Fraction rhs) {
        return minus(rhs).numerator < 0;
    };
    bool operator >(Fraction rhs) {
        return minus(rhs).numerator > 0;
    };
};
