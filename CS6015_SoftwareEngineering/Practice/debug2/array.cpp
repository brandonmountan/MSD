#include <iostream>
# include <cstdlib>

using namespace std;

void array_data()
{
    int i;
    int *x;
    x = new int[6];
    //access unitialized element
    cout<<" Un-initialized element: "<< x[0] <<"\n";
    
    //x[5] not initialized
    for ( i = 1; i < 5 ; i++ ) {
        x[i]=i+1;
    }
    
    for ( i = 0; i < 5; i++ ){
        x[i]=2*x[i+1];
    }
    for ( i = 0; i < 5; i++ ){
        cout<<" "<<i<<" "<<x[i] <<"\n";

    }
    
    //out-of-bounds access
   cout<<"Out-of-bound element: "<< x[10] <<"\n";
    
    delete [] x;
    
    //use after free
    //cout<<" 1st element after free: "<< x[0] <<"\n";
}


int main () {
    array_data();
    return 0;
}
