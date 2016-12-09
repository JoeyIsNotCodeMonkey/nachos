//#include <malloc.h>

int main(int argc, char const *argv[])
{
	int *addr1 = sf_malloc(128);
	printAddress(addr1);
  printFreeList();

	int *addr2 = sf_malloc(256);
	printAddress(addr2);
  printFreeList();

	int *addr3 = sf_malloc(512);
  printAddress(addr3);
  printFreeList();

	int *addr4 = sf_malloc(512);
  printAddress(addr4);
  printFreeList();

	int *addr5 = sf_malloc(512);
  printAddress(addr5);
  printFreeList();

	sf_free(addr2);
  printFreeList();

	int *addr6 = sf_malloc(512);
  printAddress(addr6);
  printFreeList();




	//Write("After free, firstfree->next size is: ", 50, 100);
	return 0;
}
