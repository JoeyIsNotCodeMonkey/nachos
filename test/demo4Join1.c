#include "syscall.h"

int main(){

	int id = Exec("test/demo4Join2");
	Join(id);
	return 0;
}
