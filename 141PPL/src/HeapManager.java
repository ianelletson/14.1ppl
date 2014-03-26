public class HeapManager {
	static private final int NULL = -1; // our null link
	public int[] memory; // the memory we manage
	private int freeStart; // start of the free list

	// "Predecessor" and "successor" refer to order within the free list.
	// NULL is used both as the predecessor of the first block and the
	// successor of the last block.
	// Getting or setting the successor of NULL is treated as getting
	// or setting the start of the free list.

	// The first word of each memory block is used to store the block's size.
	private void setSize(int block, int size) {
		memory[block] = size;
	}

	private int getSize(int block) {
		return memory[block];
	}

	// The second word of each memory block is used to store the block's
	// successor.
	private void setSuccessor(int block, int successor) {
		if (block == NULL) {
			freeStart = successor;
		} else {
			memory[block + 1] = successor;
		}
	}

	private int getSuccessor(int block) {
		if (block == NULL) {
			return freeStart;
		} else {
			return memory[block + 1];
		}
	}

	/**
	 * HeapManager constructor.
	 * 
	 * @param initialMemory
	 *            the int[] of memory to manage
	 */
	public HeapManager(int[] initialMemory) {
		memory = initialMemory;
		int heapStart = 0; // use all the memory for the heap
		int heapSize = memory.length;
		// put a block at the start of the heap
		setSize(heapStart, heapSize); // one big free block
		setSuccessor(heapStart, NULL); // free list ends with it
		freeStart = heapStart; // free list starts with it
	}

	/**
	 * Allocate a block and return its address.
	 * 
	 * @param requestSize
	 *            int size of block, > 0
	 * @return block address
	 * @throws OutOfMemoryError
	 *             if no block big enough
	 */
	public int allocate(int requestSize) {
		int size = requestSize + 1; // size including header
		int predecessor = findBlockPredecessor(size);
		int block = getSuccessor(predecessor);
		trimBlock(block, size);
		setSuccessor(predecessor, getSuccessor(block)); // unlink the block
		return block + 1; // index of first useable word (after header)
	}

	// Find a large enough block and return its predecessor in the free list.
	// The specified size includes the header.
	// Throws OutOfMemoryError if no block big enough.
	private int findBlockPredecessor(int size) {
		// Do first-fit search: linear search of the free
		// list for the first block of sufficient size.

		int predecessor = NULL;
		while (true) {
			int block = getSuccessor(predecessor);
			if (getSize(block) == size) {
				// Found a match
				return predecessor;
			} else if (getSize(block) >= size) {
				return predecessor;
			} else if (block == NULL) {
				throw new OutOfMemoryError();
			}
			
//			if (block == NULL) {
//				// All blocks on the free list have been looked at.
//				// Because we return as soon as we find a suitable one,
//				// there must not have been any suitable one.
//				throw new OutOfMemoryError();
//			} else if (getSize(block) >= size) {
//				// We found a suitable block.
//				return predecessor;
//			}
			predecessor = block; // move down the free list
		}
	}

//	// Find a large enough block and return its predecessor in the free list.
//	// The specified size includes the header.
//	// Throws OutOfMemoryError if no block big enough.
//	private int findBlockPredecessor(int size) {
//		// Do first-fit search: linear search of the free
//		// list for the first block of sufficient size.
//
//		int predecessor = NULL;
//		while (true) {
//			int block = getSuccessor(predecessor);
//			if (block == NULL) {
//				// All blocks on the free list have been looked at.
//				// Because we return as soon as we find a suitable one,
//				// there must not have been any suitable one.
//				throw new OutOfMemoryError();
//			} else if (getSize(block) >= size) {
//				// We found a suitable block.
//				return predecessor;
//			}
//			predecessor = block; // move down the free list
//		}
//	}

	private void trimBlock(int block, int size) {
		int unused = getSize(block) - size; // extra space in block
		if (unused > 1) { // if more than a header's worth
			int newBlock = block + size; // index of the unused piece
			setSize(newBlock, unused);
			setSize(block, size);
			insertAfter(newBlock, block);
		}
	}

	private void insertAfter(int newBlock, int block) {
		// The order of the next two lines is crucial.
		setSuccessor(newBlock, getSuccessor(block));
		setSuccessor(block, newBlock);
	}

	/**
	 * Deallocate an allocated block. This works only if the block address is
	 * one that was returned by allocate and has not yet been deallocated.
	 * 
	 * @param address
	 *            int address of the block
	 */
	public void deallocate(int address) {
		int addr = address - 1; // adjust for the header
		final int predecessor = findLastBlockBefore(addr);
		insertAfter(addr, predecessor);
		coalesceIfAdjacent(addr, getSuccessor(addr));
		coalesceIfAdjacent(predecessor, addr);
	}

	private void coalesceIfAdjacent(int predecessor, int addr) {
		if (predecessor != NULL && addr != NULL
				&& getSize(predecessor) == addr - predecessor) {
			setSize(predecessor, getSize(predecessor) + getSize(addr));
			setSuccessor(predecessor, getSuccessor(addr));
		}

	}

	private int findLastBlockBefore(int addr) {
		int predecessor = NULL;
		while (true) {
			int block = getSuccessor(predecessor);
			if (block == NULL || block >= addr)
				break;
			predecessor = block; // move down the free list
		}
		return predecessor;
	}

}