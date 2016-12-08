//#include <unistd.h>
//#include <stdio.h>

#define NULL 0
#define PAGE_SIZE 4096

#define WSIZE       8       /* Word and header size (bytes) */

struct memory_region {
	struct memory_region *next;
	int size;
    char data[0];  /* The data starts here and continues on. */
};
typedef struct memory_region memory_region;

static struct memory_region *firstfree = NULL;

extern void *heap_start;
extern void *heap_limit;

void *sf_malloc(unsigned int size);
void sf_free(void *ptr);

void sf_init();
void add_to_freelist(memory_region *ptr);
void remove_from_freelist(memory_region *ptr);

memory_region *find_fit(unsigned int size);
memory_region *place(memory_region *ptr, unsigned int size);
memory_region *coalesce(void *ptr);
memory_region *extend_heap(unsigned int size);
unsigned int align(unsigned int size);
void* itoa(int num, char* str, int base);
void reverse(char str[], int length);
void decToHex(int dec, char* hexadecimalNumber);
void sf_strcat(char *dest, char *src);
int sf_strlen(char *s);
void printAddress(int *addr);
void printFreeList();
