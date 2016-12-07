

extern void *heap_start, *heap_limit;

main()
{

long *p = heap_start;
*p=1;

Yield();

*p = 2;
}
