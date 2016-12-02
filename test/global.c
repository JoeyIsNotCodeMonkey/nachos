

extern void *heap_start, *heap_limit;

main()
{
int *p = heap_limit;
*p++;
}
