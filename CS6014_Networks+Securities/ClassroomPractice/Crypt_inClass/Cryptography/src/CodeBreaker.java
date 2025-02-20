/*
* You suspect that Homer is secretly meeting with a wanted person.
* He has just received the following message:
*
*      POAURJNBVLRAWAYKOWIIWRSJHTJUAOSJKQQIXECKULQCS
*
* Where is the meeting?
*
* Whoever sent this message, you suspect that they found the
* first CS 6014 cryptography video on YouTube and used it to
* encrypt the message. So maybe they used some combination of
* `Permute.encode`, `Substitute.encode`, and `Chain.encode`
* (in some sensible order) to encode the message, and they
* expect a matching use of `Permute.decode`, `Substitute.decode`,
* and `Chain.decode` to decode the message. If only you knew
* the rotation, column count, chain seed, and chain iteration
* count that they used! At 26 * 32 * 27 * 10 (assuming up to 10
* chain iterations makes sense) combinations, there are roughly
* 224,640 possibilities.
*
* Hm... That's actually not a lot of variants for a computer to
* try, especially if you had a `Spell.check` function (like the
* one here) to check whether a candidate decoding is plausible.
*
* */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

public class CodeBreaker {
    public static void main(String[] args) throws IOException {
        Spell.init_words();

        String demo_plaintext = "Meet me at the clock tower";
        String demo_s = demo_plaintext;

        demo_s = Chain.encode(demo_s, 2, 5);
        demo_s = Substitute.encode(demo_s, 13);
        demo_s = Permute.encode(demo_s, 4);
        System.out.println(demo_s);

        demo_s = Permute.decode(demo_s, 4);
        demo_s = Substitute.decode(demo_s, 13);
        demo_s = Chain.decode(demo_s, 2, 5);
        System.out.println(demo_s);
        System.out.println(Spell.check(demo_s));


        for (int i = 1; i < 33; i++) {
            String ciphertext = "POAURJNBVLRAWAYKOWIIWRSJHTJUAOSJKQQIXECKULQCS";
            String permuted = Permute.decode(ciphertext, i); // Decode permutation
            for (int l = 0; l < 27; l++) {
                String substituted = Substitute.decode(permuted, l); // Decode substitution
                for (int j = 0; j < 28; j++) {
                    for (int k = 0; k < 11; k++) {
                        String chained = Chain.decode(substituted, k, j); // Decode chain
                        if (Spell.check(chained)) {
                            System.out.println("Decoded message: " + chained);
                            System.out.println("Parameters: Permute columns=" + i + ", Substitute rotation=" + l + ", Chain seed=" + j + ", Chain iterations=" + k);
                        }
                    }
                }
            }
        }
    }
}

class Substitute {
    static String encode(String s, int rot) {
        StringBuffer b = new StringBuffer();
        if (rot < 0) rot += 26;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                c = (char)((int)'A' + ((c - (int)'A') + rot) % 26);
            } else if (c >= 'a' && c <= 'z') {
                c = (char)((int)'a' + ((c - (int)'a') + rot) % 26);
            }
            b.append(c);
        }
        return b.toString();
    }

    static String decode(String s, int rot) {
        return encode(s, 26 - rot);
    }
}

class Permute {
    static String encode(String s, int columns) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < columns; i++) {
            for (int j = i; j < s.length(); j += columns) {
                b.append(s.charAt(j));
            }
        }
        return b.toString();
    }

    static String decode(String s, int columns) {
        StringBuffer b = new StringBuffer();
        int rows = (s.length() + columns - 1) / columns;
        int full_rows = s.length() / columns;
        int full_columns = s.length() % columns;
        if (full_columns == 0) {
            full_columns = columns;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int k;
                if (j < full_columns) {
                    k = j * rows + i;
                } else if (i < full_rows) {
                    k = full_columns * rows + (j - full_columns) * (rows - 1) + i;
                } else
                    continue;
                b.append(s.charAt(k));
            }
        }
        return b.toString();
    }
}

class Chain {
    static int to_val(char c) {
        if (c >= 'A' && c <= 'Z') {
            return ((int) c - (int) 'A') + 1;
        } else if (c >= 'a' && c <= 'z') {
            return ((int) c - (int) 'a') + 1;
        } else if (c == ' ' || c == '_') {
            return 0;
        } else {
            return -1;
        }
    }
    static char to_char(int v, char old_c) {
        boolean is_upper = ((old_c >= 'A' && old_c <= 'Z') || (old_c == ' '));

        if (v == 0)
            return (is_upper ? ' ' : '_');
        else {
            return (char) ((int)(is_upper ? 'A' : 'a') + v - 1);
        }
    }
    static String encode(String s, int times, int seed) {
        for (int k = 0; k < times; k++) {
            StringBuffer b = new StringBuffer();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                int v = to_val(c);
                if (v == -1) {
                    b.append(c);
                } else {
                    int new_v = (v + seed) % 27;
                    b.append(to_char(new_v, c));
                    seed = new_v;
                }
            }
            s = b.toString();
        }

        return s;
    }

    static int find_preceding(String s, int i) {
        while (i > 0) {
            i--;
            if (to_val(s.charAt(i)) >= 0) {
                return i;
            }
        }
        return -1;
    }

    static String decode(String s, int times, int seed) {
        for (int k = 0; k < times; k++) {
            StringBuffer b = new StringBuffer();
            for (int i = s.length(); i-- > 0; ) {
                char c = s.charAt(i);
                int v = to_val(c);

                if (v == -1) {
                    b.append(c);
                    continue;
                } else {
                    int v2;
                    int j = find_preceding(s, i);
                    if (j >= 0) {
                        v2 = to_val(s.charAt(j));
                    } else {
                        if (k + 1 == times)
                            v2 = seed;
                        else
                            v2 = to_val(b.charAt(0));
                    }
                    int new_v = (v - v2 + 27) % 27;
                    b.append(to_char(new_v, c));
                }
            }
            b.reverse();
            s = b.toString();
        }

        return s;
    }
}

class Spell {
    static HashSet<String> words;

    static void init_words() throws IOException {
        words = new HashSet<String>();
        // If your machine doesn't have this file, you can download
        // a copy from https://users.cs.utah.edu/~mflatt/tmp/words
        words.addAll(Files.readAllLines(Path.of("/usr/share/dict/words")));
    }

    static boolean check(String s) {
        String[] maybe_words = s.split(" ");
        for (String maybe_word : maybe_words) {
            if (!words.contains(maybe_word.toLowerCase())) {
                return false;
            }
        }
        return true;
    }
}
