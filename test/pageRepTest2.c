//#include <malloc.h>
extern void *heap_limit;
extern void *heap_start;


int main(int argc, char const *argv[])
{
	int *addr1 = sf_malloc(2048);
	printAddress(addr1);

	char *ptr = (char *)addr1;
	int i=2048;
	while(i<2048) {
		*ptr++ = 'a';
		i++;
	}



	//Write("After free, firstfree->next size is: ", 50, 100);
	return 0;
}
