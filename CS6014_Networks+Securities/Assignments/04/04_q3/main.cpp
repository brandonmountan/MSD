#include <iostream>
#include <array>
#include <vector>
#include <algorithm>
#include <random>
#include <cstdint>

// Define a Block as an array of 8 bytes (64 bits)
using Block = std::array<uint8_t, 8>;

// Function to generate a 64-bit key from a password
Block generateKey(const std::string& password) {
    Block key = {0}; // Initialize the key with zeros
    for (size_t i = 0; i < password.length(); ++i) {
        // XOR each byte of the key with the corresponding character in the password
        key[i % 8] ^= password[i];
    }
    return key; // Return the generated key
}

// Function to generate a substitution table (S-box) using Fisher-Yates shuffle
std::array<uint8_t, 256> generateSubstitutionTable() {
    std::array<uint8_t, 256> table; // Create a table with 256 entries (one for each byte value)
    for (int i = 0; i < 256; ++i) {
        table[i] = i; // Initialize the table with the identity substitution (0 -> 0, 1 -> 1, ..., 255 -> 255)
    }
    // Shuffle the table using the Fisher-Yates algorithm to randomize it
    std::random_device rd; // Seed the random number generator
    std::mt19937 g(rd());  // Mersenne Twister random number generator
    std::shuffle(table.begin(), table.end(), g); // Shuffle the table
    return table; // Return the shuffled substitution table
}

// Function to generate the reverse substitution table (inverse S-box)
std::array<uint8_t, 256> generateReverseSubstitutionTable(const std::array<uint8_t, 256>& table) {
    std::array<uint8_t, 256> reverseTable; // Create a table for the reverse substitution
    for (int i = 0; i < 256; ++i) {
        // Map each substituted value back to its original value
        reverseTable[table[i]] = i;
    }
    return reverseTable; // Return the reverse substitution table
}

// Function to encrypt a block using the cipher
Block encryptBlock(const Block& plaintext, const Block& key, const std::array<std::array<uint8_t, 256>, 8>& substitutionTables) {
    Block state = plaintext; // Initialize the state with the plaintext
    for (int round = 0; round < 16; ++round) { // Perform 16 rounds of encryption
        // XOR the state with the key
        for (int i = 0; i < 8; ++i) {
            state[i] ^= key[i];
        }
        // Substitute each byte in the state using the appropriate substitution table
        for (int i = 0; i < 8; ++i) {
            state[i] = substitutionTables[i][state[i]];
        }
        // Rotate the entire state 1 bit to the left
        uint8_t carry = 0; // Initialize the carry bit
        for (int i = 0; i < 8; ++i) {
            uint8_t nextCarry = (state[i] >> 7) & 1; // Save the most significant bit
            state[i] = (state[i] << 1) | carry;     // Shift left and add the carry
            carry = nextCarry; // Update the carry for the next byte
        }
    }
    return state; // Return the encrypted block
}

// Function to decrypt a block using the cipher
Block decryptBlock(const Block& ciphertext, const Block& key, const std::array<std::array<uint8_t, 256>, 8>& reverseSubstitutionTables) {
    Block state = ciphertext; // Initialize the state with the ciphertext
    for (int round = 0; round < 16; ++round) { // Perform 16 rounds of decryption
        // Rotate the state 1 bit to the right (reverse of encryption)
        uint8_t carry = 0; // Initialize the carry bit
        for (int i = 7; i >= 0; --i) {
            uint8_t nextCarry = state[i] & 1; // Save the least significant bit
            state[i] = (state[i] >> 1) | (carry << 7); // Shift right and add the carry
            carry = nextCarry; // Update the carry for the next byte
        }
        // Reverse substitute each byte in the state using the appropriate reverse substitution table
        for (int i = 0; i < 8; ++i) {
            state[i] = reverseSubstitutionTables[i][state[i]];
        }
        // XOR the state with the key
        for (int i = 0; i < 8; ++i) {
            state[i] ^= key[i];
        }
    }
    return state; // Return the decrypted block
}

// Helper function to print a block in hexadecimal format
void printBlock(const Block& block) {
    for (uint8_t byte : block) {
        std::cout << std::hex << (int)byte << " "; // Print each byte as a hexadecimal value
    }
    std::cout << std::endl;
}

int main() {
    // Password and key generation
    std::string password = "weakpassword"; // Define a password
    Block key = generateKey(password);    // Generate the key from the password
    std::cout << "Key: ";
    printBlock(key); // Print the generated key

    // Generate substitution tables (S-boxes)
    std::array<std::array<uint8_t, 256>, 8> substitutionTables;
    for (int i = 0; i < 8; ++i) {
        substitutionTables[i] = generateSubstitutionTable(); // Generate a substitution table for each byte position
    }

    // Generate reverse substitution tables (inverse S-boxes)
    std::array<std::array<uint8_t, 256>, 8> reverseSubstitutionTables;
    for (int i = 0; i < 8; ++i) {
        reverseSubstitutionTables[i] = generateReverseSubstitutionTable(substitutionTables[i]); // Generate the reverse table for each S-box
    }

    // Define a plaintext message
    Block plaintext = {0x01, 0x23, 0x45, 0x67, 0x89, 0xAB, 0xCD, 0xEF}; // Example plaintext
    std::cout << "Plaintext: ";
    printBlock(plaintext); // Print the plaintext

    // Encrypt the plaintext
    Block ciphertext = encryptBlock(plaintext, key, substitutionTables); // Encrypt the plaintext
    std::cout << "Ciphertext: ";
    printBlock(ciphertext); // Print the ciphertext

    // Decrypt the ciphertext
    Block decrypted = decryptBlock(ciphertext, key, reverseSubstitutionTables); // Decrypt the ciphertext
    std::cout << "Decrypted: ";
    printBlock(decrypted); // Print the decrypted plaintext

    // Test with wrong password
    std::string wrongPassword = "wrongpass"; // Define a wrong password
    Block wrongKey = generateKey(wrongPassword); // Generate a key from the wrong password
    Block wrongDecrypted = decryptBlock(ciphertext, wrongKey, reverseSubstitutionTables); // Attempt to decrypt with the wrong key
    std::cout << "Decrypted with wrong key: ";
    printBlock(wrongDecrypted); // Print the result (should be garbage)

    // Modify 1 bit of the ciphertext
    ciphertext[0] ^= 0x01; // Flip the least significant bit of the first byte
    Block modifiedDecrypted = decryptBlock(ciphertext, key, reverseSubstitutionTables); // Decrypt the modified ciphertext
    std::cout << "Decrypted after modifying 1 bit: ";
    printBlock(modifiedDecrypted); // Print the result (should be different from the original plaintext)

    return 0; // End of program
}