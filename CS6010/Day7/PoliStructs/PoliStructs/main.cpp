//
//  main.cpp
//  PoliStructs
//
//  Created by Brandon Mountan on 8/27/24.
//

#include <iostream>
#include <vector>
#include <string>

struct Politician {
    std::string name;
    bool partyAffiliation;
    bool stateOrFederal;
};

// if true add politician name to javacan list (true = Javacan)

std::vector<std::string> Javacans(std::vector<Politician> testList){
    std::vector<std::string> javacansList = {};
    for (Politician x : testList){
        if (x.partyAffiliation == true){
            javacansList.push_back(x.name);
        }
    }
        return javacansList;
}

// if false, add politician name to cplusers list (false = cpluser)

std::vector<std::string> FederalCplusers(std::vector<Politician> testList){
    std::vector<std::string> cplusersList = {};
    for (Politician x : testList){
        if (x.partyAffiliation == false && x.stateOrFederal == true){
            cplusersList.push_back(x.name);
        }
    }
        return cplusersList;
}


int main(int argc, const char * argv[]) {
    
    Politician politician1;
        politician1.name = "Don";
        politician1.partyAffiliation = true;
        politician1.stateOrFederal = true;

    Politician politician2;
        politician2.name = "Joe";
        politician2.partyAffiliation = true;
        politician2.stateOrFederal = false;
    
    Politician politician3;
        politician3.name = "George";
        politician3.partyAffiliation = false;
        politician3.stateOrFederal = true;
    
    Politician politician4;
        politician4.name = "Bill";
        politician4.partyAffiliation = false;
        politician4.stateOrFederal = false;
    
    std::vector<Politician> p1{politician1, politician2, politician3, politician4};
    
    std::vector<std::string> javacanContainer;
    
    javacanContainer = Javacans(p1);
    
    std::string javacanString = "";
    
    for (int i = 0; i < javacanContainer.size(); i++){
        javacanString += javacanContainer[i] + "\n";
    }
    
    std::cout << "List of Javacans: " << "\n" << javacanString << std::endl;
    
    std::vector<std::string> cpuserContainer;
    
    cpuserContainer = FederalCplusers(p1);
    
    std::string cpuserString = "";
    
    for (int i = 0; i < cpuserContainer.size(); i++){
        cpuserString += cpuserContainer[i] + "\n";
    }
    
    std::cout << "List of Federal Cpusers:" << "\n" << cpuserString << std::endl;
    
    return 0;
}
