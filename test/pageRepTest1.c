//#include <malloc.h>
extern void *heap_limit;
extern void *heap_start;


int main(int argc, char const *argv[])
{
	int *addr1 = sf_malloc(3096);
	printAddress(addr1);

	char *ptr = (char *)addr1;
	int i=0;
	while(i<3096) {
		*ptr++ = 'a';
		i++;
	}

	//Yield();

	ptr = (char *)addr1;
	i=0;
	char b = 'b';
	while(i<3096) {
		b = *ptr++;
		i++;
	}




	//Write("After free, firstfree->next size is: ", 50, 100);
	return 0;
}
