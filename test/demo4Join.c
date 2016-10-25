#include "syscall.h"

int main(){

	int id = Exec("test/demo4Join1");
	Join(id);
	return 0;
}
