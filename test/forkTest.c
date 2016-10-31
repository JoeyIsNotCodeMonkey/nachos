#include "syscall.h"


void show (){
	
	Write("Hello ", 6 , 1);
	
	Exit(0);
}


int main ( )
{


	void (*f)() = &show;
	int ii = 0;
	while(ii < 3){
		Fork(f);
		++ii;
	}
	
	return 0;
}

