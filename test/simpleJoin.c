#include "syscall.h"

int main(){

	int id = Exec("test/cs1");
	Join(id);
	return 0;
}
