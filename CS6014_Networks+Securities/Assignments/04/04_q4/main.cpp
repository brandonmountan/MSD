#include <iostream>
#include <vector>
#include <array>
#include <cstdint> // For uint8_t

class RC4 {
private:
    std::array<uint8_t, 256> S; // State array for the RC4 cipher
    uint8_t i = 0, j = 0;       // Indices for the pseudorandom byte generation

    // Key Scheduling Algorithm (KSA): Initializes the state array using the key
    void initialize(const std::vector<uint8_t>& key) {
        // Initialize the state array with values 0 to 255
        for (int i = 0; i < 256; ++i) {
            S[i] = i;
        }
        uint8_t j = 0;
        // Shuffle the state array based on the key
        for (int i = 0; i < 256; ++i) {
            j = j + S[i] + key[i % key.size()]; // Update j using the key and state array
            std::swap(S[i], S[j]);              // Swap S[i] and S[j]
        }
    }

public:
    // Constructor: Initializes the RC4 cipher with the given key
    RC4(const std::vector<uint8_t>& key) {
        initialize(key); // Call the KSA to set up the state array
    }

    // Generate the next pseudorandom byte from the keystream
    uint8_t nextByte() {
        i = i + 1;               // Increment i
        j = j + S[i];            // Update j using S[i]
        std::swap(S[i], S[j]);   // Swap S[i] and S[j]
        return S[(S[i] + S[j]) & 0xFF]; // Return the pseudorandom byte
    }

    // Encrypt or decrypt a message using the RC4 keystream
    std::vector<uint8_t> process(const std::vector<uint8_t>& input) {
        std::vector<uint8_t> output; // Output vector for the encrypted/decrypted message
        for (uint8_t byte : input) {
            // XOR each byte of the input with the next pseudorandom byte
            output.push_back(byte ^ nextByte());
        }
        return output; // Return the processed message
    }
};

// Helper function to print a vector of bytes as a string
void printBytes(const std::vector<uint8_t>& bytes) {
    for (uint8_t byte : bytes) {
        std::cout << byte; // Print each byte as a character
    }
    std::cout << std::endl;
}

int main() {
    // Test 1: Encrypt and decrypt with the same key
    std::vector<uint8_t> key = {0x01, 0x02, 0x03, 0x04, 0x05}; // Example key
    std::vector<uint8_t> plaintext = {'H', 'e', 'l', 'l', 'o', ' ', 'R', 'C', '4', '!'}; // Example plaintext

    // Encrypt the plaintext
    RC4 rc4Encrypt(key); // Initialize RC4 with the key
    std::vector<uint8_t> ciphertext = rc4Encrypt.process(plaintext); // Encrypt the plaintext
    std::cout << "Ciphertext: ";
    printBytes(ciphertext); // Print the ciphertext

    // Decrypt the ciphertext
    RC4 rc4Decrypt(key); // Initialize RC4 with the same key
    std::vector<uint8_t> decrypted = rc4Decrypt.process(ciphertext); // Decrypt the ciphertext
    std::cout << "Decrypted: ";
    printBytes(decrypted); // Print the decrypted plaintext

    // Test 2: Decrypt with a different key
    std::vector<uint8_t> wrongKey = {0x05, 0x04, 0x03, 0x02, 0x01}; // Wrong key
    RC4 rc4WrongDecrypt(wrongKey); // Initialize RC4 with the wrong key
    std::vector<uint8_t> wrongDecrypted = rc4WrongDecrypt.process(ciphertext); // Attempt to decrypt
    std::cout << "Decrypted with wrong key: ";
    printBytes(wrongDecrypted); // Print the result (should be garbage)

    // Test 3: Encrypt two messages with the same keystream
    std::vector<uint8_t> plaintext1 = {'S', 'e', 'c', 'r', 'e', 't', '1'}; // First plaintext
    std::vector<uint8_t> plaintext2 = {'S', 'e', 'c', 'r', 'e', 't', '2'}; // Second plaintext

    // Encrypt the first plaintext
    RC4 rc4Encrypt1(key); // Initialize RC4 with the key
    std::vector<uint8_t> ciphertext1 = rc4Encrypt1.process(plaintext1); // Encrypt the first plaintext
    std::cout << "Ciphertext 1: ";
    printBytes(ciphertext1); // Print the first ciphertext

    // Encrypt the second plaintext
    RC4 rc4Encrypt2(key); // Initialize RC4 with the same key
    std::vector<uint8_t> ciphertext2 = rc4Encrypt2.process(plaintext2); // Encrypt the second plaintext
    std::cout << "Ciphertext 2: ";
    printBytes(ciphertext2); // Print the second ciphertext

    // XOR the two ciphertexts
    std::vector<uint8_t> xorResult;
    for (size_t i = 0; i < ciphertext1.size(); ++i) {
        xorResult.push_back(ciphertext1[i] ^ ciphertext2[i]); // XOR corresponding bytes
    }
    std::cout << "XOR of ciphertexts: ";
    printBytes(xorResult); // Print the XOR result (should be the XOR of the two plaintexts)

    // Test 4: Bit-flipping attack
    std::vector<uint8_t> salaryMessage = {'Y', 'o', 'u', 'r', ' ', 's', 'a', 'l', 'a', 'r', 'y', ' ', 'i', 's', ' ', '$', '1', '0', '0', '0'}; // Example message
    RC4 rc4SalaryEncrypt(key); // Initialize RC4 with the key
    std::vector<uint8_t> salaryCiphertext = rc4SalaryEncrypt.process(salaryMessage); // Encrypt the message

    // Modify the ciphertext to change $1000 to $9999
    size_t pos = 16; // Position of '1' in the plaintext
    salaryCiphertext[pos] ^= '1' ^ '9';     // Flip '1' to '9'
    salaryCiphertext[pos + 1] ^= '0' ^ '9'; // Flip '0' to '9'
    salaryCiphertext[pos + 2] ^= '0' ^ '9'; // Flip '0' to '9'
    salaryCiphertext[pos + 3] ^= '0' ^ '9'; // Flip '0' to '9'

    // Decrypt the modified ciphertext
    RC4 rc4SalaryDecrypt(key); // Initialize RC4 with the key
    std::vector<uint8_t> modifiedSalary = rc4SalaryDecrypt.process(salaryCiphertext); // Decrypt the modified ciphertext
    std::cout << "Modified salary message: ";
    printBytes(modifiedSalary); // Print the modified message

    return 0; // End of program
}