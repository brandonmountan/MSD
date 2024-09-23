//
// main.cpp
// Cards
//
// Created by Brandon Mountan on 8/27/24.
//
#include <iostream>
#include <vector>
#include <string>
#include <cstdlib>
#include <algorithm>

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
    } else if (test == 14){
        return "Ace";
    }
    return std::to_string(test);
}

std::vector<Cards> deckCreator(){
    std::string suits[] = {"Spades", "Clubs", "Hearts", "Diamonds"};
    std::vector<Cards> container = {};
    for (std::string i : suits){
        for (int j = 2; j < 15; j++){
            Cards card;
            card.suit = i;
            card.rank = j;
            container.push_back(card);
        }
    }
    return container;
}

std::vector<Cards> shuffleDeck(std::vector<Cards> deck){
    for (int i = 51; i > 0; i--){
        int j;
        j = (rand()%i);
        Cards temp = deck[i];
        deck[i] = deck[j];
        deck[j] = temp;
    }
    return deck;
}

void printHand(std::vector<Cards> deck){
    std::vector<Cards> hand;
    for (int i = 0; i < 5; i++){
        Cards card;
        card.rank = deck[i].rank;
        card.suit = deck[i].suit;
        hand.push_back(card);
        std::cout << intToChar(card.rank) << " of " << card.suit << std::endl;
    }
}

std::vector<Cards> createOneHand(std::vector<Cards> deck){
    std::vector<Cards> hand;
    for (int i = 0; i < 5; i++){
        Cards card;
        card.rank = deck[i].rank;
        card.suit = deck[i].suit;
        hand.push_back(card);
    }
    return hand;
}

void sortHand(std::vector<Cards> &hand){
    int min;
    for (int i = 0; i < hand.size(); i++){
        min = i;
        for (int j = i + 1; j < hand.size(); j++){
            if (hand[i].rank > hand[j].rank){
                min = j;
                int temp;
                temp = hand[min].rank;
                hand[min].rank = hand[i].rank;
                hand[i].rank = temp;
            }
        }
    }
}

bool isFlush(std::vector<Cards> hand){
    std::string suit = hand[0].suit;
    for (int i = 0; i < 5; i++){
        if(hand[i].suit != suit) {
            return false;
        }
    }
    return true;
}

bool isStraight( std::vector<Cards> hand ){
    sortHand(hand);
    if (hand[0].rank == 2 && hand[1].rank == 3 && hand[2].rank == 4 && hand[3].rank == 5 && hand[4].rank == 14){
        return true;
    }
    for (int i = 0; i < hand.size() - 1; i++){
        if (hand[i].rank != (hand[i+1].rank - 1)){
            return false;
        }
    }
    return true;
}


bool isStraightFlush(std::vector<Cards> hand){
    if(isStraight(hand) == true && isFlush(hand) == true){
        return true;
    }
    return false;
}

bool isRoyalFlush(std::vector<Cards> hand){
    if(isStraightFlush (hand) == true && hand[0].rank == 10){
        return true;
    }
    return false;
}

bool isFullHouse(std::vector<Cards> hand){
    sortHand(hand);
    if (hand[0].rank == hand[1].rank == hand[2].rank && hand[3].rank == hand[4].rank){
        return true;
    } else if (hand[0].rank == hand[1].rank && hand[2].rank == hand[3].rank == hand[4].rank){
        return true;
    }
    return false;
}

void analyzeHands(std::vector<Cards> deck){
    int runTime = 1;
    int flushCount = 0, straightCount = 0, straightFlushCount = 0, royalFlushCount = 0, fullHouseCount = 0;
    // counts for flush, straight etc.
    while (runTime <= 1000000){
        std::vector<Cards> shuffledDeck = shuffleDeck(deck);
        std::vector<Cards> hand = createOneHand(shuffledDeck);
        if (isFlush(hand)){
            flushCount += 1;
        } else if (isStraight(hand)){
            straightCount += 1;
        } else if (isStraightFlush(hand)){
            straightFlushCount += 1;
        } else if (isRoyalFlush(hand)){
            royalFlushCount += 1;
        } else if (isFullHouse(hand)){
            fullHouseCount += 1;
        }
        runTime += 1;
    }
    std::cout << "Flushes: " << flushCount << "\n" << "Straights: " << straightCount << "\n" << "Straight Flushes: " << straightFlushCount << "\n" << "Royal Flushes: " << royalFlushCount << "\n" << "Full Houses: " << fullHouseCount << "\n";
}

int main(int argc, const char * argv[]) {
    
    srand((unsigned)time(0));

    std::vector<Cards> deck = deckCreator();

    std::vector<Cards> shuffledDeck = shuffleDeck(deck);
    
    std::vector<Cards> createdHand = createOneHand(shuffledDeck);
    
//    printHand(createdHand);
    
//    sortHand(createdHand);
    
//    std::vector<Cards> hand = printOneHand(shuffledDeck);
    
//    analyzeHands(shuffledDeck);
    
//    std::vector<Cards> flushHand = {{2, "Diamonds"},{8, "Diamonds"},{4, "Diamonds"},{9, "Diamonds"},{6, "Diamonds"}};
//
//    std::vector<Cards> straightHand = {{2, "Spades"},{4, "Diamonds"},{3, "Hearts"},{5, "Clubs"},{6, "Diamonds"}};
//
//    std::vector<Cards> straightFlushHand = {{9, "Spades"},{10, "Spades"},{11, "Spades"},{12, "Spades"},{13, "Spades"}};
//
//    std::vector<Cards> RoyalFlushHand = {{10, "Spades"},{11, "Spades"},{12, "Spades"},{13, "Spades"},{14, "Spades"}};
    
        std::vector<Cards> fullHouseHand = {{10, "Spades"},{10, "Hearts"},{10, "Diamonds"},{13, "Spades"},{13, "Hearts"}};

//
//    std::cout<< isFlush(flushHand) << std::endl;
//
//    std::cout<< isStraight(straightHand) << std::endl;
//
//    std::cout<< isStraightFlush(straightFlushHand) << std::endl;
//
//    std::cout<< isRoyalFlush(RoyalFlushHand) << std::endl;
    
    std::cout<< isFullHouse(fullHouseHand) << std::endl;

    
  return 0;
}
