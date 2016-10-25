#include "syscall.h"

int main(){

	int id = Exec("test/demo4Join3");
	Join(id);
	return 0;
}
