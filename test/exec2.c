/* Basic test of Exec() system call */

#include "syscall.h"

int
main()
{
  Exec("test/cs2");
  Exec("test/cs3");
  Exit(0);
}
