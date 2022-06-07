/**
 * Queue class mainly for iterator.
 * @author Corey
 *
 * @param <T> Generic type of item held in queue.
 */
public class Queue<T>
{
	/**
	 * Front index of queue, back index of queue, number of elements in queue.
	 */
	private int front, back, elements;

	/**
	 * Maximum capacity of queue.
	 */
	private static final int MAX_INT = 1444444;

	/**
	 * Generic type array of max capacity.
	 */
	@SuppressWarnings("unchecked")
	private  T[] queue = (T[]) new Object[MAX_INT];

	/**
	 * Constructor for queue.
	 * @param <T> Generic type.
	 */
	@SuppressWarnings("hiding")
	public <T> Queue() {
		this.front = 0;
		this.back = 0;
		this.elements = 0;
	}

	/**
	 * Finds out whether queue is empty or not.
	 * @return Boolean that returns true if queue is empty.
	 */
	public boolean isEmpty() {
		if(this.elements == 0)
			return true;
		else
			return false;
	}

	/**
	 * Adds parameter to back of queue.
	 * @param value Generic value wanted to add to end of queue.
	 */
	public void enqueue(T value) {
		if(this.elements == MAX_INT) {
			System.out.println("OVERFLOW");
			return;
		}

		this.queue[back] = value;
		this.back++;
		this.elements++;

		return;

	}

	/**
	 * Removes value from front of queue.
	 * @return Returns removed value. 
	 */
	public T dequeue() {

		if(this.isEmpty() || this.queue[front] == null) {
			throw new RuntimeException();		
		}

		T value = this.queue[front];
		this.queue[front] = null;
		this.front++;
		this.elements--;
		return value;

	}

	/**
	 * Looks at value at front of queue.
	 * @return returns that value.
	 */
	public T peek() {
		if(this.queue[front] == this.queue[back])
			return null;
		if(this.isEmpty() || this.queue[front] == null) {
			throw new RuntimeException();		
		}

		return this.queue[front];
	}

	/**
	 * Find number of elements in queue.
	 * @return returns number elements in queue.
	 */
	public int getElements() {
		return this.elements;
	}

}
