#include "shelpers.hpp"

//////////////////////////////////////////////////////////////////////////////////
//
// Author: Ben Jones (I think) with a lot of clean up by J. Davison de St. Germain
//
// Date:   2019?
//         Jan 2022 - Cleanup
//
// Class: CS 6013 - Systems I
//
//////////////////////////////////////////////////////////////////////////////////

using namespace std;

////////////////////////////////////////////////////////////////////////
// Example test commands you can try once your shell is up and running:
//
// ls
// ls | nl
// cd [dir]
// cat < shelpers.cpp
// cat < shelpers.cpp | nl
// cat shelpers.cpp | nl
// cat shelpers.cpp | nl | head -50 | tail -10
// cat shelpers.cpp | nl | head -50 | tail -10 > ten_lines.txt 
//
// - The following two commands are equivalent.  [data.txt is sent into nl and the
//   output is saved to numbered_data.txt.]
//
// nl > numbered_data.txt < data.txt
// nl < data.txt > numbered_data.txt 
//
// - Assuming numbered_data.txt has values in it... try running:
//   [Note this probably doesn't work like one might expect...
//    does it behave the same as your normal shell?]
//
// nl < numbered_data.txt > numbered_data.txt
//
// - The following line is an error (input redirection at end of line).
//   It should fail gracefully (ie, 1) without doing anything, 2) cleaning
//   up any file descriptors that were opened, 3) giving an appropriate
//   message to the user).
//
// cat shelpers.cpp | nl | head -50 | tail -10 > ten_lines.txt < abc
// 

////////////////////////////////////////////////////////////////////////
// This routine is used by tokenize().  You do not need to modify it.

bool splitOnSymbol( vector<string> & words, int i, char c )
{
   if( words[i].size() < 2 ){
      return false;
   }
   int pos;
   if( (pos = words[i].find(c)) != string::npos ){
      if( pos == 0 ){
         // Starts with symbol.
         words.insert( words.begin() + i + 1, words[i].substr(1, words[i].size() -1) );
         words[i] = words[i].substr( 0, 1 );
      }
      else {
         // Symbol in middle or end.
         words.insert( words.begin() + i + 1, string{c} );
         string after = words[i].substr( pos + 1, words[i].size() - pos - 1 );
         if( !after.empty() ){
            words.insert( words.begin() + i + 2, after );
         }
         words[i] = words[i].substr( 0, pos );
      }
      return true;
   }
   else {
      return false;
   }
}

////////////////////////////////////////////////////////////////////////
// You do not need to modify tokenize().  

vector<string> tokenize( const string& s )
{
   vector<string> ret;
   int pos = 0;
   int space;

   // Split on spaces:

   while( (space = s.find(' ', pos) ) != string::npos ){
      string word = s.substr( pos, space - pos );
      if( !word.empty() ){
         ret.push_back( word );
      }
      pos = space + 1;
   }

   string lastWord = s.substr( pos, s.size() - pos );

   if( !lastWord.empty() ){
      ret.push_back( lastWord );
   }

   for( int i = 0; i < ret.size(); ++i ) {
      for( char c : {'&', '<', '>', '|'} ) {
         if( splitOnSymbol( ret, i, c ) ){
            --i;
            break;
         }
      }
   }
   return ret;
}

////////////////////////////////////////////////////////////////////////

ostream& operator<<( ostream& outs, const Command& c )
{
   outs << c.execName << " [argv: ";
   for( const auto & arg : c.argv ){
      if( arg ) {
         outs << arg << ' ';
      }
      else {
         outs << "NULL ";
      }
   }
   outs << "] -- FD, in: " << c.inputFd << ", out: " << c.outputFd << " "
        << (c.background ? "(background)" : "(foreground)");
   return outs;
}

////////////////////////////////////////////////////////////////////////
//
// getCommands()
//
// Parses a vector of command line tokens and places them into (as appropriate)
// separate Command structures.
//
// Returns an empty vector if the command line (tokens) is invalid.
//
// You'll need to fill in a few gaps in this function and add appropriate error handling
// at the end.  Note, most of the gaps contain "assert( false )".
//

vector<Command> getCommands(const vector<string>& tokens) {
    // Step 1: Determine the number of commands
    // Count the number of pipe symbols ("|") in the tokens and add 1 to get the total number of commands.
    // For example, "ls | grep foo | wc" has 2 pipes and 3 commands.
    vector<Command> commands(count(tokens.begin(), tokens.end(), "|") + 1);

    // Step 2: Initialize indices for parsing tokens
    int first = 0; // Index of the first token for the current command
    int last = find(tokens.begin(), tokens.end(), "|") - tokens.begin(); // Index of the next pipe or end of tokens

    // Step 3: Track errors during parsing
    bool error = false;

    // Step 4: Loop through each command
    for (int cmdNumber = 0; cmdNumber < commands.size(); ++cmdNumber) {
        // Get the first token of the current command
        const string& token = tokens[first];

        // Step 5: Check for invalid starting tokens
        // If the first token is a special symbol (&, <, >, |), it's an error.
        if (token == "&" || token == "<" || token == ">" || token == "|") {
            error = true;
            break;
        }

        // Step 6: Initialize the current Command struct
        Command& command = commands[cmdNumber]; // Get a reference to the current Command
        command.execName = token; // Set the executable name (e.g., "ls")

        // Step 7: Copy the token into argv
        // argv[0] should be the program name (e.g., "ls").
        // strdup() creates a copy of the string to avoid issues with scope.
        command.argv.push_back(strdup(token.c_str()));

        // Step 8: Initialize file descriptors
        // By default, input comes from STDIN and output goes to STDOUT.
        command.inputFd = STDIN_FILENO;
        command.outputFd = STDOUT_FILENO;

        // Step 9: Initialize background flag
        // By default, commands run in the foreground.
        command.background = false;

        // Step 10: Process the remaining tokens for the current command
        for (int j = first + 1; j < last; ++j) {
            if (tokens[j] == ">" || tokens[j] == "<") {
                // Handle I/O redirection tokens
                const string& filename = tokens[j + 1]; // The next token is the filename

                if (tokens[j] == "<") {
                    // Input redirection (e.g., "cat < file.txt")
                    if (cmdNumber != 0) {
                        // Only the first command can have input redirection
                        error = true;
                        break;
                    }
                    // Open the file for reading
                    command.inputFd = open(filename.c_str(), O_RDONLY);
                    if (command.inputFd == -1) {
                        perror("open input file"); // Print an error if the file cannot be opened
                        error = true;
                        break;
                    }
                } else if (tokens[j] == ">") {
                    // Output redirection (e.g., "ls > output.txt")
                    if (cmdNumber != commands.size() - 1) {
                        // Only the last command can have output redirection
                        error = true;
                        break;
                    }
                    // Open the file for writing, create it if it doesn't exist, and truncate it if it does
                    command.outputFd = open(filename.c_str(), O_WRONLY | O_CREAT | O_TRUNC, 0666);
                    if (command.outputFd == -1) {
                        perror("open output file"); // Print an error if the file cannot be opened
                        error = true;
                        break;
                    }
                }
                ++j; // Skip the filename token since we've already processed it
            } else if (tokens[j] == "&") {
                // Background command (e.g., "sleep 10 &")
                command.background = true;
            } else {
                // Normal command argument (e.g., "-l" in "ls -l")
                command.argv.push_back(strdup(tokens[j].c_str()));
            }
        }

        // Step 11: Handle pipes between commands
        if (!error) {
            if (cmdNumber > 0) {
                // There are multiple commands. Open a pipe to connect them.
                int pipefd[2]; // Array to hold the pipe file descriptors
                if (pipe(pipefd) == -1) {
                    perror("pipe"); // Print an error if the pipe cannot be created
                    error = true;
                    break;
                }
                // Set up the pipe:
                // - The previous command writes to the pipe (outputFd = pipefd[1])
                // - The current command reads from the pipe (inputFd = pipefd[0])
                commands[cmdNumber - 1].outputFd = pipefd[1];
                commands[cmdNumber].inputFd = pipefd[0];
            }

            // Step 12: Null-terminate the argv array
            // execvp() requires the argument list to end with a nullptr.
            command.argv.push_back(nullptr);

            // Step 13: Find the next pipe character
            first = last + 1; // Move to the token after the current pipe
            if (first < tokens.size()) {
                last = find(tokens.begin() + first, tokens.end(), "|") - tokens.begin();
            }
        } // end if !error
    } // end for( cmdNumber = 0 to commands.size )

    // Step 14: Handle errors
    if (error) {
        // Close any open file descriptors to avoid resource leaks
        for (auto& cmd : commands) {
            if (cmd.inputFd != STDIN_FILENO) close(cmd.inputFd);
            if (cmd.outputFd != STDOUT_FILENO) close(cmd.outputFd);
        }
        return {}; // Return an empty vector to indicate an error
    }

    // Step 15: Return the parsed commands
    return commands;
} // end getCommands()