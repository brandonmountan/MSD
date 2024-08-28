//
//  main.cpp
//  Cards
//
//  Created by Brandon Mountan on 8/27/24.
//

#include <iostream>
#include <vector>
#include <string>


// spade = 1, club = 2, heart = 3, diamond = 4
// rank: 1,2,3,4,5,6,7,8,9,10,11,12,13
struct Cards {
    int rank;
    int suit;
};

std::vector<Cards> cardCreator(){
    std::vector<Cards> container = {};
    for (int suit = 1; suit < 5; suit++){
        for (int rank = 1; rank < 14; rank++){
            Cards card;
            card.rank = rank;
            card.suit = suit;
            container.push_back(card);
        }
    }
    return container;
}

int main(int argc, const char * argv[]) {

//    Cards spade1;
//    spade1.rank = 1;
//    spade1.suit = 1;
    
    std::vector<Cards> deck = cardCreator();
        
    
    for (int i = 0; i < deck.size(); i++){
        std::cout << deck[i].suit << " " << deck[i].rank << "\n";
    }
    
    
    return 0;
}
