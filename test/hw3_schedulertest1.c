#include "syscall.h"


int main(int num_of_loop){
 
        
    int number1 = 0;
    int number2 = 0;
    int inner_counter = 200;
    
    PredictCPU(num_of_loop * 10);
        
    while((num_of_loop--)>0){
        
        while((inner_counter--)>0){
            number1++;
        }
        number2++;
        
    }

}
