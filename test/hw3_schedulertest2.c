#include "syscall.h"

int main(){
        
    int number1 = 0;
    int number2 = 0;
    int outer_counter = 100;
    int inner_counter = 1000;
    
    while((outer_counter--)>0){
        
        while((inner_counter--)>0){
            number1++;
            number1++;
            number1++;
        }
        number2++;
        number2++;
        number2++;
        
    }

}