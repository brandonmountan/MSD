//
//  main.cpp
//  Cards
//
//  Created by Brandon Mountan on 8/27/24.
//

#include <iostream>
#include <vector>
#include <string>
#include <cstdlib>

struct Cards {
    int rank;
    std::string suit;
};

std::string intToChar(int test){
    if (test == 11){
        return "Jack";
    } else if (test == 12){
        return "Queen";
    } else if (test == 13){
        return "King";
    } else if (test == 1){
        return "Ace";
    }
    return std::to_string(test);
}

std::vector<Cards> deckCreator(){
    std::string suits[] = {"Spades", "Clubs", "Hearts", "Diamonds"};
    std::vector<Cards> container = {};
    for (std::string i : suits){
        for (int j = 1; j < 14; j++){
            Cards card;
            card.suit = i;
            card.rank = j;
            container.push_back(card);
        }
    }
    return container;
}

int main(int argc, const char * argv[]) {
    
    std::vector<Cards> deck = deckCreator();
    // printing deck
    for (int i = 0; i < deck.size(); i++){
        std::string rank = intToChar(deck[i].rank);
            
        std::cout << rank << " of " << deck[i].suit << "\n";
    }

    
    return 0;
}
