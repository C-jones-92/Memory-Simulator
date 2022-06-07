//TO DO: Add JavaDocs, you can add more things too, but don't break anything
/**
 * Represents BareNode data in MemMan linked list.
 * @author Corey and Professor
 *
 */
public class MemBlock {
	//******************************************************
	//****    IMPORTANT: DO NOT CHANGE/ALTER/REMOVE     ****
	//****    ANYTHING PROVIDED LIKE THESE INTS, THIS   ****
	//****    BOOLEAN, OR THE CONSTRUCTOR.              ****
	//******************************************************
	
	/**
	 * Address that memory block is stored at.
	 */
	public final int addr;
	
	/**
	 * Size of the memory block.
	 */
	public final int size;
	
	/**
	 * Whether the block of memory is free or not.
	 */
	public final boolean isFree;
	
	/**
	 * Creates Memory Block.
	 * @param addr The address of the memory block;
	 * @param size The size of the memory block;
	 * @param isFree Whether the memory block is free or not.
	 */
	public MemBlock(int addr, int size, boolean isFree) {
		this.addr = addr;
		this.size = size;
		this.isFree = isFree;
	}
	
	/**
	 * Copies a block of memory.
	 * @param memBlock Memory block you wish to copy.
	 */
	public MemBlock(MemBlock memBlock) {
		this.addr = memBlock.addr;
		this.size = memBlock.size;
		this.isFree = memBlock.isFree;
		
	}
}