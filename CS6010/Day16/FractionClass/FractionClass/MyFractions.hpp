//
//  MyFractions.hpp
//  FractionClass
//
//  Created by Brandon Mountan on 09/11/24.
//

#ifndef FRACTION_H
#define FRACTION_H

#include <string>
#include <cmath>
#include <stdexcept>

class Fraction {
private:
    long numerator;
    long denominator;

    // Helper function to compute GCD
    long GCD(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return std::abs(a);
    }

    // Helper function to reduce the fraction
    void reduce() {
        long gcd = GCD(numerator, denominator);
        numerator /= gcd;
        denominator /= gcd;

        // Ensure the sign is always on the numerator
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
    }

public:
    // Default constructor
    Fraction() : numerator(0), denominator(1) {}

    // Constructor with parameters
    Fraction(long n, long d) : numerator(n), denominator(d) {
        if (denominator == 0) {
            throw std::invalid_argument("Denominator cannot be zero.");
        }
        // Normalize the fraction
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        reduce();
    }

    // Addition
    Fraction plus(const Fraction& rhs) {
        long n = numerator * rhs.denominator + rhs.numerator * denominator;
        long d = denominator * rhs.denominator;
        return Fraction(n, d);
    }

    // Subtraction
    Fraction minus(const Fraction& rhs) {
        long n = numerator * rhs.denominator - rhs.numerator * denominator;
        long d = denominator * rhs.denominator;
        return Fraction(n, d);
    }

    // Multiplication
    Fraction times(const Fraction& rhs) {
        long n = numerator * rhs.numerator;
        long d = denominator * rhs.denominator;
        return Fraction(n, d);
    }

    // Division
    Fraction dividedBy(const Fraction& rhs) {
        if (rhs.numerator == 0) {
            throw std::invalid_argument("Cannot divide by zero.");
        }
        long n = numerator * rhs.denominator;
        long d = denominator * rhs.numerator;
        return Fraction(n, d);
    }

    // Reciprocal
    Fraction reciprocal() {
        if (numerator == 0) {
            throw std::invalid_argument("Cannot take reciprocal of zero.");
        }
        return Fraction(denominator, numerator);
    }

    // Convert to string
    std::string toString() {
        return std::to_string(numerator) + "/" + std::to_string(denominator);
    }

    // Convert to double
    double toDouble() {
        return static_cast<double>(numerator) / static_cast<double>(denominator);
    }

    // Operator overloads
    Fraction operator+(const Fraction& rhs) { return plus(rhs); }
    Fraction operator-(const Fraction& rhs) { return minus(rhs); }
    Fraction operator*(const Fraction& rhs) { return times(rhs); }
    Fraction operator/(const Fraction& rhs) { return dividedBy(rhs); }

    Fraction& operator+=(const Fraction& rhs) {
        *this = *this + rhs;
        return *this;
    }

    Fraction& operator-=(const Fraction& rhs) {
        *this = *this - rhs;
        return *this;
    }

    Fraction& operator*=(const Fraction& rhs) {
        *this = *this * rhs;
        return *this;
    }

    Fraction& operator/=(const Fraction& rhs) {
        *this = *this / rhs;
        return *this;
    }

    bool operator==(const Fraction& rhs) {
        return numerator == rhs.numerator && denominator == rhs.denominator;
    }

    bool operator!=(const Fraction& rhs) {
        return !(*this == rhs);
    }

    bool operator<(const Fraction& rhs) {
        return numerator * rhs.denominator < rhs.numerator * denominator;
    }

    bool operator<=(const Fraction& rhs) {
        return *this < rhs || *this == rhs;
    }

    bool operator>(const Fraction& rhs) {
        return !(*this <= rhs);
    }

    bool operator>=(const Fraction& rhs) {
        return !(*this < rhs);
    }
};

#endif // FRACTION_H
