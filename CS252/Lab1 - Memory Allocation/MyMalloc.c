/*
 * CS252: MyMalloc Project
 *
 * The current implementation gets memory from the OS
 * every time memory is requested and never frees memory.
 *
 * You will implement the allocator as indicated in the handout,
 * as well as the deallocator.
 *
 * You will also need to add the necessary locking mechanisms to
 * support multi-threaded programs.
 */

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/mman.h>
#include <pthread.h>
#include "MyMalloc.h"

static pthread_mutex_t mutex;

const int arenaSize = 2097152;

void increaseMallocCalls()  { _mallocCalls++; }

void increaseReallocCalls() { _reallocCalls++; }

void increaseCallocCalls()  { _callocCalls++; }

void increaseFreeCalls()    { _freeCalls++; }

extern void atExitHandlerInC()
{
    atExitHandler();
}

/* 
 * Initial setup of allocator. First chunk is retrieved from the OS,
 * and the fence posts and freeList are initialized.
 */
void initialize()
{
    // Environment var VERBOSE prints stats at end and turns on debugging
    // Default is on
    _verbose = 1;
    const char *envverbose = getenv("MALLOCVERBOSE");
    if (envverbose && !strcmp(envverbose, "NO")) {
        _verbose = 0;
    }

    pthread_mutex_init(&mutex, NULL);
    void *_mem = getMemoryFromOS(arenaSize);

    // In verbose mode register also printing statistics at exit
    atexit(atExitHandlerInC);

    // establish fence posts
    ObjectHeader * fencePostHead = (ObjectHeader *)_mem;
    fencePostHead->_allocated = 1;
    fencePostHead->_objectSize = 0;

    char *temp = (char *)_mem + arenaSize - sizeof(ObjectHeader);
    ObjectHeader * fencePostFoot = (ObjectHeader *)temp;
    fencePostFoot->_allocated = 1;
    fencePostFoot->_objectSize = 0;
    fencePostFoot->_leftObjectSize = arenaSize - (2*sizeof(ObjectHeader));

    // Set up the sentinel as the start of the freeList
    _freeList = &_freeListSentinel;

    // Initialize the list to point to the _mem
    temp = (char *)_mem + sizeof(ObjectHeader);
    ObjectHeader *currentHeader = (ObjectHeader *)temp;
    currentHeader->_objectSize = arenaSize - (2*sizeof(ObjectHeader)); // ~2MB
    currentHeader->_leftObjectSize = 0;
    currentHeader->_allocated = 0;
    currentHeader->_listNext = _freeList;
    currentHeader->_listPrev = _freeList;
    _freeList->_listNext = currentHeader;
    _freeList->_listPrev = currentHeader;

    // Set the start of the allocated memory
    _memStart = (char *)currentHeader;

    _initialized = 1;
}

/* 
 * TODO: In allocateObject you will handle retrieving new memory for the malloc
 * request. The current implementation simply pulls from the OS for every
 * request.
 *
 * @param: amount of memory requested
 * @return: pointer to start of useable memory
 */
void * allocateObject(size_t size)
{
    size_t roundedSize;
    
    // Make sure that allocator is initialized
    if (!_initialized){
          initialize();
    }

    /* Add the ObjectHeader to the size and round the total size up to a 
     * multiple of 8 bytes for alignment.
     */
    roundedSize = (size + sizeof(ObjectHeader) + 7) & ~7;
    
    //beginning of freeList
    ObjectHeader *curr = _freeList->_listNext;
    //for splitting
    ObjectHeader *ret;

    while(curr != _freeList) {
            //size request fits free space exactly
        if(curr->_objectSize == roundedSize) {
	        //change to allocated
	        curr->_allocated = 1;
            //remove from free list
		    curr->_listNext->_listPrev = curr->_listPrev;
		    curr->_listPrev->_listNext = curr->_listNext;
            curr->_listPrev = NULL;
            curr->_listNext = NULL;
            pthread_mutex_unlock(&mutex);
		    //return usable memory
		    return (void *)((char *)curr + sizeof(ObjectHeader));
        }
        //size request is smaller than free space
        else if(roundedSize < curr->_objectSize) {
            //update size of free memory
            curr->_objectSize = curr->_objectSize - roundedSize;
            //move ret pointer to split in memory
            ret = (ObjectHeader *)((char *)curr + curr->_objectSize);
            //give values to newly allocated memory
            ret->_objectSize = roundedSize;
            ret->_leftObjectSize = curr->_objectSize;
            ret->_allocated = 1;
            //change left object size attrib of mem after newly allocated mem
            ObjectHeader * rightOfRet = (ObjectHeader *)((char *)ret + ret->_objectSize);
            rightOfRet->_leftObjectSize = ret->_objectSize;

            pthread_mutex_unlock(&mutex);
            //return usable memory
            return (void *)((char *)ret + sizeof(ObjectHeader));
        }
        //move to next in free list
        curr = curr->_listNext;
    }


    //if not returned in while loop then another 2MB is needed
    void * _mem2 = getMemoryFromOS(arenaSize);

    // establish fence posts
    ObjectHeader * fencePostHead = (ObjectHeader *)_mem2;
    fencePostHead->_allocated = 1;
    fencePostHead->_objectSize = 0;

    char *temp = ((char *)_mem2 + arenaSize - sizeof(ObjectHeader));
    ObjectHeader * fencePostFoot = (ObjectHeader *)temp;
    fencePostFoot->_allocated = 1;
    fencePostFoot->_objectSize = 0;
    fencePostFoot->_leftObjectSize = arenaSize - (2*sizeof(ObjectHeader));

    // insert _mem2 into the beginning of the free list
    temp = (char *)_mem2 + sizeof(ObjectHeader);
    ObjectHeader *currentHeader = (ObjectHeader *)temp;
    currentHeader->_objectSize = arenaSize - (2*sizeof(ObjectHeader)); // ~2MB
    currentHeader->_leftObjectSize = 0;
    currentHeader->_allocated = 0;
    currentHeader->_listNext = _freeList->_listNext;
    currentHeader->_listPrev = _freeList;
    _freeList->_listNext->_listPrev = currentHeader;
    _freeList->_listNext = currentHeader;

    return allocateObject(size);

    // Store the size in the header
    // ObjectHeader *o = (ObjectHeader *) _mem;
    // o->_objectSize = roundedSize;

    // pthread_mutex_unlock(&mutex);

    /*curr = currentHeader;

    if(curr->_objectSize == roundedSize) {
	    //change to allocated
	    curr->_allocated = 1;
        //remove from free list
		curr->_listNext->_listPrev = curr->_listPrev;
		curr->_listPrev->_listNext = curr->_listNext;

        pthread_mutex_unlock(&mutex);
		//return usable memory
		return (void *)((char *)curr + sizeof(ObjectHeader));
    }
    //size request is smaller than free space
    else if(roundedSize < curr->_objectSize) {
        //update size of free memory
        curr->_objectSize -= roundedSize;
        //move ret pointer to split in memory
        ret = (ObjectHeader *)((char *)curr + curr->_objectSize);
        //give values to newly allocated memory
        ret->_objectSize = roundedSize;
        ret->_leftObjectSize = curr->_objectSize;
        ret->_allocated = 1;
        //change left object size attrib of mem after newly allocated mem
        ObjectHeader * rightOfRet = (ObjectHeader *)((char *)ret + ret->_objectSize);
        rightOfRet->_leftObjectSize = ret->_objectSize;

        pthread_mutex_unlock(&mutex);
        //return usable memory
        return (void *)((char *)ret + sizeof(ObjectHeader));
    }*/

    // Return a pointer to useable memoryfreeMem
    // return (void *)((char *)o + sizeof(ObjectHeader));
}

/* 
 * TODO: In freeObject you will implement returning memory back to the free
 * list, and coalescing the block with surrounding free blocks if possible.
 *
 * @param: pointer to the beginning of the block to be returned
 * Note: ptr points to beginning of useable memory, not the block's header
 */
void freeObject(void *ptr)
{
    // Add your implementation here

    //point to header of allocated memory to free
    ObjectHeader * freeMem = (ObjectHeader *)((char *)ptr - sizeof(ObjectHeader));

    //point to left memory
    ObjectHeader * leftMem = (ObjectHeader *)((char *)freeMem - freeMem->_leftObjectSize);

    //point to right memory
    ObjectHeader * rightMem = (ObjectHeader *)((char *)freeMem + freeMem->_objectSize);

    //change alloc attrib of mem to free
    freeMem->_allocated = 0;

    //only left is free
    if(!leftMem->_allocated) {
        //combine left and curr sizesfreeMemSize
        leftMem->_objectSize = leftMem->_objectSize + freeMem->_objectSize;
        rightMem->_leftObjectSize = leftMem->_objectSize;
        //return (void *)((char *)leftMem + sizeof(ObjectHeader));
        //when both are free
        if(!rightMem->_allocated) {
            leftMem->_objectSize = leftMem->_objectSize + rightMem->_objectSize;
            rightMem->_listPrev->_listNext = rightMem->_listNext;
            rightMem->_listNext->_listPrev = rightMem->_listPrev;
            rightMem->_listNext = NULL;
            rightMem->_listPrev = NULL;
            ObjectHeader * rightNeighbor = (ObjectHeader *) ((char *)rightMem + rightMem->_objectSize);
            rightNeighbor->_leftObjectSize = leftMem->_objectSize;
        }
    }
    //only right is free
    else if(!rightMem->_allocated) {
        //combine curr and right sizes
        freeMem->_objectSize += rightMem->_objectSize;
        
        //update pointers
        freeMem->_listNext = rightMem->_listNext;
        freeMem->_listPrev = rightMem->_listPrev;
        rightMem->_listNext->_listPrev = freeMem;
        rightMem->_listPrev->_listNext = freeMem;
        rightMem->_listNext = NULL;
        rightMem->_listPrev = NULL;
        ObjectHeader * rightNeighbor = (ObjectHeader *) ((char *)rightMem + rightMem->_objectSize);
        rightNeighbor->_leftObjectSize = freeMem->_objectSize;
    }
    //both left and right are allocated
    else if(rightMem->_allocated && leftMem->_allocated){
        //add free(mem) to the beginning of free list
        freeMem->_listNext = _freeList->_listNext;
        freeMem->_listPrev = _freeList;
        freeMem->_listNext->_listPrev = freeMem;
        _freeList->_listNext = freeMem;
    }
    pthread_mutex_unlock(&mutex);
    return;
}

/* 
 * Prints the current state of the heap.
 */
void print()
{
    printf("\n-------------------\n");

    printf("HeapSize:\t%zd bytes\n", _heapSize );
    printf("# mallocs:\t%d\n", _mallocCalls );
    printf("# reallocs:\t%d\n", _reallocCalls );
    printf("# callocs:\t%d\n", _callocCalls );
    printf("# frees:\t%d\n", _freeCalls );

    printf("\n-------------------\n");
}

/* 
 * Prints the current state of the freeList
 */
void print_list() {
    printf("FreeList: ");
    if (!_initialized) 
        initialize();

    ObjectHeader * ptr = _freeList->_listNext;

    while (ptr != _freeList) {
        long offset = (long)ptr - (long)_memStart;
        printf("[offset:%ld,size:%zd]", offset, ptr->_objectSize);
        ptr = ptr->_listNext;
        if (ptr != NULL)
            printf("->");
    }
    printf("\n");
}

/* 
 * This function employs the actual system call, sbrk, that retrieves memory
 * from the OS.
 *
 * @param: the chunk size that is requested from the OS
 * @return: pointer to the beginning of the chunk retrieved from the OS
 */
void * getMemoryFromOS(size_t size)
{
    _heapSize += size;

    // Use sbrk() to get memory from OS
    void *_mem = sbrk(size);

    // if the list hasn't been initialized, initialize memStart to mem
    if (!_initialized)
        _memStart = _mem;

    return _mem;
}

void atExitHandler()
{
    // Print statistics when exit
    if (_verbose)
        print();
}

/*
 * C interface
 */

extern void * malloc(size_t size)
{
    pthread_mutex_lock(&mutex);
    increaseMallocCalls();

    return allocateObject(size);
}

extern void free(void *ptr)
{
    pthread_mutex_lock(&mutex);
    increaseFreeCalls();

    if (ptr == 0) {
        // No object to free
        pthread_mutex_unlock(&mutex);
        return;
    }

    freeObject(ptr);
}

extern void * realloc(void *ptr, size_t size)
{
    pthread_mutex_lock(&mutex);
    increaseReallocCalls();

    // Allocate new object
    void *newptr = allocateObject(size);

    // Copy old object only if ptr != 0
    if (ptr != 0) {

        // copy only the minimum number of bytes
        ObjectHeader* hdr = (ObjectHeader *)((char *) ptr - sizeof(ObjectHeader));
        size_t sizeToCopy =  hdr->_objectSize;
        if (sizeToCopy > size)
            sizeToCopy = size;

        memcpy(newptr, ptr, sizeToCopy);

        //Free old object
        freeObject(ptr);
    }

    return newptr;
}

extern void * calloc(size_t nelem, size_t elsize)
{
    pthread_mutex_lock(&mutex);
    increaseCallocCalls();

    // calloc allocates and initializes
    size_t size = nelem *elsize;

    void *ptr = allocateObject(size);

    if (ptr) {
        // No error; initialize chunk with 0s
        memset(ptr, 0, size);
    }

    return ptr;
}

