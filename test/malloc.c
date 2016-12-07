#include <malloc.h>

void *sf_malloc(unsigned int size) {
	// char malloc_start[] = "------malloc starting------";
	// Write(malloc_start, sf_strlen(malloc_start), 100);
	//Write("caonima", 10, 100);

	memory_region *ptr;

	//first call
	if(firstfree == NULL) {
		sf_init();
	}

	/* Ignore spurious requests */
    if (size == 0){
    	return NULL;
    }

    if((ptr = find_fit(align(size))) != NULL){
		place(ptr, size);

		return (void *)(ptr->data);
	} else {
		int count;
		if(align(size) % PAGE_SIZE != 0)
			count = align(size)/PAGE_SIZE + 1;
		else
			count= align(size)/PAGE_SIZE;

		ptr = extend_heap(count * PAGE_SIZE);

	}

	place(ptr, size);

	//print help info

	// char *addr = NULL;
	//
	// int *start_addr = (void *)heap_start;
	// decToHex(start_addr, addr);
	// sf_strcat(return_addr, addr);



	return (void *)(ptr->data);

}

void sf_free(void *ptr) {
	//printf("Before coalesce, the adress of the pointer is: %p\n", (void *)ptr);
	memory_region *new_free_region = coalesce(ptr);
	//printf("%s\n", "coalesce completed");
	add_to_freelist(new_free_region);
}

memory_region *coalesce(void *ptr) {
	struct memory_region *pp =
		(struct memory_region *)(ptr - (((void *)((struct memory_region *)ptr)->data) - ptr));


	//printf("new adress of the pointer is: %p\n", (void *)pp);
	memory_region *next_block = (void *)pp + pp->size;

	//printf("the adress of the next_block is: %p\n", (void *)next_block);

	memory_region *cursor = firstfree;
	while(cursor != NULL && cursor != next_block) {
		cursor = cursor->next;
	}

	//next blcok is not free
	if(cursor == NULL) {

		return pp;
	}

	else {
		memory_region *new_free_region = pp;

		new_free_region->size = pp->size + cursor->size;

		remove_from_freelist(cursor);
		return new_free_region;
	}

}

void sf_init() {
	//heap_start = (memory_region *)sbrk(0);

	//Write("Initializing virtual memory...", 32, 100);

	//make sure the first free data's address is double words alignment
	memory_region* record = heap_start;
	while((long)heap_start % WSIZE != 0) {
		(char *)heap_start++;
	}

	int padding = (char *)heap_start - (char *)record;
	//sbrk(padding);
	heap_limit = heap_start + padding;

	//firstfree = (memory_region *)sbrk(PAGE_SIZE);
	heap_limit = heap_start + PAGE_SIZE;
	//heap_start = firstfree;
	firstfree = heap_start;
	//printf("After init, the starting address is: %p\n", (void *)heap_start);
	//heap_limit = (memory_region *)((char *)firstfree + PAGE_SIZE);

	firstfree->next = NULL;
    firstfree->size = heap_limit - heap_start;
    //printf("After init, the size of free list is: %d\n", firstfree->size);

//print help info
		// Write("Initializing finished", 22, 100);
		// char str[] = "Memory starts from ";
		// char *addr = NULL;
		//
		// int *start_addr = (void *)heap_start;
		// decToHex(start_addr, addr);
		// sf_strcat(str, addr);
		//
		// Write(str, sf_strlen(str), 100);
		//
		// char *freesize = "Initialy, the free list size is: ";
		// char* firstfreeSize = NULL;
		// itoa(firstfree->size, firstfreeSize, 10);
		// sf_strcat(freesize, firstfreeSize);
		// Write(freesize, sf_strlen(freesize), 100);
}

memory_region *find_fit(unsigned int size) {
	memory_region *ptr = firstfree;

	while(ptr != NULL) {
		if(ptr->size > size) {
			return ptr;
		}
		ptr = ptr->next;
	}
	return NULL;
}

memory_region *place(memory_region *ptr, unsigned int size) {
	int old_size = ptr->size;
	int header_size = ((void *)((memory_region *)ptr)->data) - (void *)ptr;
	//printf("inside place, the header size is: %d\n", header_size);

	ptr->size = size + header_size;

	//if after allocation, the rest size is stll enough for
	//"next" ptr, size info (2 words in total) and at least 2 words data
	if(old_size-align(size) >= header_size + WSIZE) {

		memory_region *new_free_region = (memory_region *)((char *)ptr + align(size) + header_size);
		new_free_region->size = old_size-align(size)-header_size;
		//printf("new free region size is: %d\n", new_free_region->size);
		remove_from_freelist(ptr);
		add_to_freelist(new_free_region);
	}

	else {
		remove_from_freelist(ptr);
	}

	return ptr;
}

memory_region *extend_heap(unsigned int size) {
	//memory_region *ptr = (memory_region *)sbrk(size);
	memory_region *ptr = (memory_region *)heap_limit;

	heap_limit = (memory_region *)((char *)ptr + size);

	ptr = coalesce(ptr);

	add_to_freelist(ptr);

	return ptr;
}

void add_to_freelist(memory_region *ptr) {
	if(firstfree == NULL) {
		firstfree = ptr;
		firstfree->next = NULL;
	} else {
		memory_region *cursor = firstfree;
		memory_region *prev = NULL;
		while(cursor != NULL && cursor < ptr) {
			prev = cursor;
			cursor = cursor->next;
		}

		if(prev == NULL) {
			ptr->next = firstfree;
			firstfree = ptr;
		}

		else {
			prev->next = ptr;
			ptr->next = cursor;
		}

	}
}

void remove_from_freelist(memory_region *ptr) {
	memory_region *cursor = firstfree;
	memory_region *prev = NULL;
	while(cursor != NULL && cursor != ptr) {
		prev = cursor;
		cursor = cursor->next;
	}

	if(prev != NULL && ptr->next != NULL) {
		prev->next = ptr->next;
	}

	else if(prev == NULL && ptr->next != NULL) {
		firstfree = ptr->next;
	}

	else if(prev != NULL && ptr->next == NULL){
		prev->next = NULL;
	}

	else {
		firstfree = NULL;
	}
}


unsigned int align(unsigned int size) {
	while(size % WSIZE != 0){
		size++;
	}
	return size;
}


void reverse(char str[], int length)
{
    int start = 0;
    int end = length -1;
    while (start < end)
    {

        char tmp = str[start];
				str[start] = str[end];
				str[end] = tmp;

        start++;
        end--;
    }
}

void* itoa(int num, char* str, int base)
{
    int i = 0;
    int isNegative = 0;

    /* Handle 0 explicitely, otherwise empty string is printed for 0 */
    if (num == 0)
    {
        str[i++] = '0';
        str[i] = '\0';
        return str;
    }

    // In standard itoa(), negative numbers are handled only with
    // base 10. Otherwise numbers are considered unsigned.
    if (num < 0 && base == 10)
    {
        isNegative = 1;
        num = -num;
    }

    // Process individual digits
    while (num != 0)
    {
        int rem = num % base;
        str[i++] = (rem > 9)? (rem-10) + 'a' : rem + '0';
        num = num/base;
    }

    // If number is negative, append '-'
    if (isNegative)
        str[i++] = '-';

    str[i] = '\0'; // Append string terminator

    // Reverse the string
    reverse(str, i);

}

void decToHex(int dec, char* hexadecimalNumber) {
	  int quotient;
    int i=2,j,temp;



		hexadecimalNumber[0] = '0';
		hexadecimalNumber[1] = 'x';

    quotient = dec;

    while(quotient!=0){
         temp = quotient % 16;

      //To convert integer into character
      if( temp < 10)
           temp =temp + 48;
      else
         temp = temp + 55;

      hexadecimalNumber[i++]= temp;
      quotient = quotient / 16;
  }

	hexadecimalNumber[i] = '\0';

	reverse(hexadecimalNumber+2, i-2);

}

void sf_strcat(char *dest, char *src)
{

    while (*dest!= '\0')
        *dest++ ;
    do
    {
        *dest++ = *src++;
    }
    while (*src != '\0') ;
		*dest = '\0';
}

int sf_strlen(char *s)
{
    char *start;
    start = s;
    while(*s != 0)
    {
        s++;
    }
    return s - start;
}

void printAddress(int *addr) {
	char str[] = "The address is: ";
	char address[] = "";
	decToHex(addr, address);
	sf_strcat(str, address);
	Write(str, sf_strlen(str), 100);
}

void printFreeList() {
	char list[] = "The free list is: ";
	char size[] = "";
	char arrow[] = "->";
	memory_region *ptr = firstfree;
	while(ptr != NULL) {
		itoa(ptr->size, size, 10);
		//Write(size, sf_strlen(size), 100);
		sf_strcat(list, size);
		sf_strcat(list, arrow);

		ptr = ptr->next;
	}
	list[sf_strlen(list)-2] = '\0';
	Write(list, sf_strlen(list), 100);
}
