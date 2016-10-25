#include "syscall.h"

char msg[5];
int shared_data = 0 ;
char* m = &msg;

void show (){
	Write("Thread: ", 8 , 1);
	Write(m, 1, 1);
	m++;
	Exit(0);
}


int main ( )
{
msg[0] = '1';
msg[1] = '2';
msg[2] = '3';
msg[3] = '4';
msg[4] = '5';
	void (*f)() = &show;
	int ii = 0;
	while(ii < 2){
		Fork(f);
		++ii;
	}
	
	return 0;
}

