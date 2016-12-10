//#include <malloc.h>
extern void *heap_limit;
extern void *heap_start;

int main(int argc, char const *argv[])
{

	int *addr1 = sf_malloc(128);
  printAddress(heap_start);
	printAddress(addr1); //the address returned after fist malloc
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
