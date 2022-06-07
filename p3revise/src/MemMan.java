//TO DO: Complete this class, add JavaDocs

//Do not add any more imports!
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
/**
 * Memory Manager for heap-like storage.
 * @author Corey
 */
@SuppressWarnings("unused")
public class MemMan implements Iterable<MemBlock> {

	//******************************************************
	//****    IMPORTANT: DO NOT CHANGE/ALTER/REMOVE     ****
	//****    ANYTHING PROVIDED LIKE THESE INTS HERE... ****
	//******************************************************
	/**
	 * First final placement type "First Fit".
	 */
	public static final int FIRST_FIT = 0;
	
	/**
	 * Second final placement type "Best Fit".
	 */
	public static final int BEST_FIT = 1;
	
	/**
	 * Third final placement type "Worst Fit".
	 */
	public static final int WORST_FIT = 2;
	
	/**
	 * Fourth final placement type "Next Fit".
	 */
	public static final int NEXT_FIT = 3;

	//******************************************************
	//****    IMPORTANT: DO NOT CHANGE/ALTER/REMOVE     ****
	//****    ANYTHING PROVIDED LIKE THE INSTANCE       ****
	//****    VARIABLES IN THIS NESTED CLASS. ALSO      ****
	//****    DON'T REMOVE THE CLASS ITSELF OR ANYTHING ****
	//****    STRANGE LIKE THAT.                        ****
	//******************************************************
	/**
	 * Bare Node class for a doubly linked list.
	 * @author Professor 
	 */
	public static class BareNode {
		/**
		 * Block of memory AKA node data.
		 */
		public MemBlock block;
		
		/**
		 * Next node in the linked list.
		 */
		public BareNode next;
		
		/**
		 * Previous node in the linked list.
		 */
		public BareNode prev;
		
		/**
		 * Marker used in garbage collector.
		 */
		public boolean marked;
		
		/**
		 * BareNode constructor that takes in MemBlock.
		 * @param block block of memory for the node to hold.
		 */
		public BareNode(MemBlock block) {
			this.block = block;
		}
	}
	
	/**
	 * Placement type of MemMan.
	 */
	private int type;
	
	/**
	 * Pointed used in traversing.
	 */
	private BareNode current;
	
	/**
	 * Head of linked list.
	 */
	private BareNode head;
	
	/**
	 * Last free node used in Next Fit.
	 */
	private BareNode lastFree;
	
	/**
	 * Location used to store address information if needed.
	 */
	int location = 0;
	
	/**
	 * Memory Manager constructor used to create a new MemMan based on a previous head.
	 * @param head The head of a linked list.
	 */
	protected MemMan(BareNode head) {

	}
	
	/**
	 * Memory Manager constructor used to create a new MemMan based on type and previous manager.
	 * @param type placement type.
	 * @param m previous memMan.
	 */
	protected MemMan(int type, MemMan m) {

		this.type = type;
		this.current = m.current;
		this.head = m.head;
		this.lastFree = m.lastFree;
		this.location = m.location;

	}
	
	/**
	 * Used to create the linked list with a type and starting head.
	 * @param type Placement type.
	 * @param head Head of linked list.
	 * @return returns a new MemMan based on parameters.
	 */
	public static MemMan factory(int type, BareNode head) {

		MemMan m = new MemMan(head);

		m.type = type;
		m.head = head;
		m.current = m.head;

		return m;

	}
	
	/**
	 * Get method for head of linked list.
	 * @return returns head of linked list.
	 */
	public BareNode getHead() {

		return this.head;

	}
	
	/**
	 * Allocates memory based on MemMan type and size of block.
	 * @param size size of memory in the MemMan the node data will take up.
	 * @return returns the newly allocated node. Or null if impossible to allocate.
	 */
	public BareNode malloc(int size) {

		if(size >= 1) {
			switch(this.type) {
				
				//First Fit Placement Type.
				case 0:
					
					//Start at head of list and traverse.
					this.current = this.head;
					while(this.current!= null) {
						
						//Checks to see if current node is free and if current node size == size. 
						if(this.current.block.isFree && this.current.block.size == size) {
							MemBlock data = new MemBlock(this.current.block.addr, size, false);
							this.current.block = data;
							BareNode ret = this.current;
							lastFree = this.current;
							this.current = this.head;
							return ret;
						}
						
						//Checks to see if the current block size > size and free.
						if(this.current.block.isFree && this.current.block.size > size) {

							MemBlock free = new MemBlock(this.current.block.addr + size, this.current.block.size - size, true);
							BareNode newFree = new BareNode(free);
							this.current.block = new MemBlock(this.current.block.addr, size, false);
							lastFree = this.current;
							newFree.next = this.current.next;
							newFree.prev = this.current;
							this.current.next = newFree;
							BareNode alloc = this.current;
							alloc.prev = this.current.prev;
							alloc.next = this.current.next;
							this.current = this.head;

							return alloc;
						}
						
						//Moves to next node.
						this.current = this.current.next;


					}
					return null;
				
				//Best Fit placement type.
				case 1:
					
					//Start at head of list and traverse. Temporary nodes and difference used for placement type.
					BareNode temp = null;
					this.current = this.head;
					int difference = 99999;
					while(this.current!= null) {
						
						//Checks to see if current node is free and == size. 
						if(this.current.block.isFree && this.current.block.size == size) {
							this.current.block = new MemBlock(this.current.block.addr, size, false);
							BareNode ret = this.current;
							lastFree = this.current;
							this.current = this.head;
							return ret;
						}

						//Checking if size is big enough and stores into temp value.
						if(this.current.block.isFree && this.current.block.size > size) {
							if(this.current.block.size - size < difference) {
								difference = this.current.block.size - size;
								temp = this.current;

							}
						}
						this.current = this.current.next;
					}
					
					//While loop either finds perfect size, or finds size closest to desired size, and allocates to that node.
					MemBlock data = new MemBlock(temp.block.addr, size, false);
					MemBlock free = new MemBlock(temp.block.addr + size, difference, true);
					BareNode freeNode = new BareNode(free);
					lastFree = this.current;
					freeNode.next = temp.next;
					temp.block = data;
					temp.next = freeNode;
					freeNode.prev = temp;
					this.current = this.head;

					return temp;
				
				//Worst Fit Placement Type.
				case 2:
					
					//Start at head of list and traverse. Temporary nodes and difference used for placement type.
					BareNode temp2 = null;
					this.current = this.head;
					int difference2 = 0;
					while(this.current!= null) {
						
						//Checks to see if current node is free and == size. 
						if(this.current.block.isFree && this.current.block.size == size) {
							difference2 = 0;
							temp2 = this.current;
						}
						//Checks to see if current node is free and > size. 
						if(this.current.block.isFree && this.current.block.size > size) {
							if(this.current.block.size - size >= difference2) {
								difference2 = this.current.block.size - size;
								temp2 = this.current;

							}
						}
						this.current = this.current.next;
					}
					
					//While loops traverses through the whole list finding the largest free node that can hold the desired size and allocates there.
					MemBlock data2 = new MemBlock(temp2.block.addr, size, false);
					MemBlock free2 = new MemBlock(temp2.block.addr + size, difference2, true);
					BareNode freeNode2 = new BareNode(free2);

					freeNode2.next = temp2.next;
					temp2.block = data2;
					temp2.next = freeNode2;
					freeNode2.prev = temp2;
					lastFree = this.current;
					this.current = this.head;


					return temp2;

				//Next Fit Placement Type.
				case 3:
					
					//Start at the last free node used and traverses the whole. Temporary used for placement type.
					//Once we arrive at the last free node, this behaves much like First Fit Placement Type.
					this.current = lastFree;
					BareNode temporary = lastFree;
					while(this.current != null) {
						
						//Checks to see if current node is free and if current node size == size. 
						if(this.current.block.isFree && this.current.block.size == size) {
							MemBlock data3 = new MemBlock(this.current.block.addr, size, false);
							this.current.block = data3;
							BareNode ret = this.current;
							lastFree = this.current;
							this.current = this.head;
							return ret;
						}
						
						//Checks to see if the current block size > size and free.
						if(this.current.block.isFree && this.current.block.size > size) {

							MemBlock free3 = new MemBlock(this.current.block.addr + size, this.current.block.size - size, true);
							BareNode newFree = new BareNode(free3);
							this.current.block = new MemBlock(this.current.block.addr, size, false);
							lastFree = this.current;
							newFree.next = this.current.next;
							newFree.prev = this.current;
							this.current.next = newFree;
							BareNode alloc = this.current;
							alloc.prev = this.current.prev;
							alloc.next = this.current.next;
							this.current = this.head;


							return alloc;
						}
						this.current = this.current.next;
					}
					
					//Since this while loop could start from anywhere in the list, we send the current node back to the head once it reaches the end of the linked list.
					//We then traverse the list again until it reaches the original node which was held in a temporary node.
					this.current = this.head;
				
					while(this.current != temporary) {
						
						//Checks to see if current node is free and if current node size == size. 
						if(this.current.block.isFree && this.current.block.size == size) {
							MemBlock data3 = new MemBlock(this.current.block.addr, size, false);
							this.current.block = data3;
							BareNode ret = this.current;
							lastFree = this.current;
							this.current = this.head;
							return ret;
						}
						
						//Checks to see if the current block size > size and free.
						if(this.current.block.isFree && this.current.block.size > size) {

							MemBlock free3 = new MemBlock(this.current.block.addr + size, this.current.block.size - size, true);
							BareNode newFree = new BareNode(free3);
							this.current.block = new MemBlock(this.current.block.addr, size, false);
							lastFree = this.current;
							newFree.next = this.current.next;
							newFree.prev = this.current;
							this.current.next = newFree;
							BareNode alloc = this.current;
							alloc.prev = this.current.prev;
							alloc.next = this.current.next;
							this.current = this.head;


							return alloc;
						}
						this.current = this.current.next;
					}
			
					break;
			}
		}
		return null;
	}
	
	/**
	 * Frees a node's block of data.
	 * @param node The node you wish to free.
	 * @return returns true if node was freed, false otherwise.
	 */
	public boolean free(BareNode node) {
		
		//Checks for coalescence.
		if(node.block.isFree && node.next.block.isFree) {
			node.block = new MemBlock(node.block.addr, node.block.size + node.next.block.size, true);
			node.next = node.next.next;	
			if(node.next !=null)
				node.next.prev = node;
			return true;
		}
		
		//If the block is already free, or null returns false.
		if(node.block.isFree || node == null) {
			return false;
		}
		
		//Frees the node.
		node.block = new MemBlock(node.block.addr, node.block.size, true);
		
		//Checks if next node is also free. If yes, the two nodes join.
		if(node.next.block.isFree) {
			node.block = new MemBlock(node.block.addr, node.block.size + node.next.block.size, true);
			node.next = node.next.next;	
			if(node.next !=null)
				node.next.prev = node;
		}
		
		//Checks if previous node is also free. If yes, the two nodes join. 
		if(node.prev != null && node.prev.block.isFree) {
			node.block = new MemBlock(node.prev.block.addr, node.block.size + node.prev.block.size, true);
			node.prev = node.prev.prev;	
			if(node.prev !=null)
				node.prev.next = node;
		}

		return true;

	}
	/**
	 * Reallocates memory in the linked list.
	 * @param node the node you wish to reallocate.
	 * @param size The size you wish the node to be.
	 * @return returns the reallocated node.
	 */
	public BareNode realloc(BareNode node, int size) {
		
		//If trying to reallocate to the same size, nothing happens.
		if(size == node.block.size) {
			return node;
		}
		
		//Shrinks node if desired size is smaller than current size.
		if(node.block.size > size) {
			MemBlock free = new MemBlock(node.block.addr+size, node.block.size - size, true);
			BareNode freeNode = new BareNode(free);
			node.block = new MemBlock(node.block.addr, size, false);
			freeNode.prev = node;
			freeNode.next = node.next;
			if(node.prev !=null) node.prev.next = node;
			if(node.next != null) node.next.prev = freeNode;
			node.next = freeNode;
			free(freeNode);
			return node;
		}
		
		//Expands node if desired size is larger than current size and if the next node is free and large enough to be expanded into.
		if(node.block.size < size && node.next.block.isFree && node.block.size + node.next.block.size >= size) {
			
			//If the node is the next node size and current node size sum equals desired size, current node is rewritten, and the old node is removed from list.
			if(node.block.size + node.next.block.size == size) {
				node.block = new MemBlock(node.block.addr, size, false);
				if(node.next != null) node.next = node.next.next;
				if(node.prev != null) node.prev = node.prev.prev;
				return node;
			}

			//Otherwise, current data is rewritten, and a new free node is made to attach to the list.
			MemBlock free = new MemBlock(node.block.addr+size, size - node.block.size, true);
			BareNode freeNode = new BareNode(free);
			node.block = new MemBlock(node.block.addr, size, false);
			freeNode.prev = node;
			freeNode.next = node.next;
			if(node.prev !=null) node.prev.next = node;
			if(node.next != null) node.next.prev = freeNode;
			node.next = freeNode;
			free(freeNode);
			return node;
		}
		
		// If the node cannot be shrunk or expanded, the method then tries to copy the node and malloc() the data based on placement type.
		MemBlock mem = new MemBlock(node.block);
		BareNode temp = node;
		node = malloc(size);
		free(temp);
		return node;
	}
	
	/**
	 * Collects and frees up and unused spaces (null spaces) in the MemMan.
	 * @param addrs The addrs of the nodes you are currently using.
	 * @return returns the amount of freed bytes the collector has collected.
	 */
	public int garbageCollect(Set<Integer> addrs) {
		
		//Traverses the list.
		int freedBytes = 0;
		this.current = this.head;
		while(this.current != null) {
			
			//Marks any nodes you do not wish to collected.
			for(int i = 0; i < addrs.size(); i++) {
				if(addrs.contains(this.current.block.addr)) {
					this.current.marked = true;
				}
			}
			this.current = this.current.next;
		}
		
		//Traverses list and frees any unmarked nodes that have not yet been freed.
		this.current = this.head;
		while(this.current !=null) {
			if(!this.current.marked && !this.current.block.isFree) {
				freedBytes += this.current.block.size;
				free(this.current);
			}
			this.current = this.current.next;
		}

		return freedBytes;
	}
	
	/**
	 * Iterates through the linked list.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterator<MemBlock> iterator()
	{

		return new MemBlockIterator(this, this.type);

	}
	/**
	 * Iterator used for iterating over the linked list.
	 * @author Corey
	 */
	@SuppressWarnings("rawtypes")
	public class MemBlockIterator implements Iterator{
		/**
		 * Memory manager.
		 */
		MemMan man;
		/**
		 * Pointer node.
		 */
		BareNode ptr;
		/**
		 * Count.
		 */
		public int count = 0;
		
		/**
		 * Creates the iterator and sets its value using parameters.
		 * @param m Memory manger you wish to iterate over.
		 * @param type Type the memory manager is.
		 */
		public MemBlockIterator(MemMan m, int type) {
			this.man = m;
			this.man.current = this.man.head;
			this.man.type = type;
		}
		
		/**
		 * Checks if node has a next node.
		 */
		@Override
		public boolean hasNext()
		{

			if(this.man.current == null) {
				System.err.println("HN FALSE");
				return false;
			}
			else 
				System.err.println("HN TRUE");
			return true;

		}
		/**
		 * Returns the next object in the list.
		 */
		@Override
		public Object next()
		{

			System.err.println("next()!!");
			MemBlock temp = this.man.current.block;
			this.man.current = this.man.current.next;			
			return temp;
		}


	}


}