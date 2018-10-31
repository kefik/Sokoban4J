//********************************************************************
//
// CircularArrayQueue.java       Authors: Lewis/Chase
//
//Represents an array implementation of a queue in which the
//indexes for the front and rear of the queue circle back to 0
//when they reach the end of the array.
//********************************************************************

// ADAPTED BY JAKUB GEMROT FROM: http://faculty.washington.edu/moishe/javademos/ch07%20Code/jss2/CircularArrayQueue.java

package cz.sokoban4j.agents.actions;

/**
 * Queue realized as circular array; contains no bounds/capacity checking (performance-wise).
 * 
 * @author Jimmy
 *
 * @param <T>
 */
public class CircularArrayQueue<T> {
	private final int DEFAULT_CAPACITY = 100;
	private int front, rear, count;
	private T[] queue;

	/**
	 * Creates an empty queue using the default capacity.
	 */
	@SuppressWarnings("unchecked")
	public CircularArrayQueue() {
		front = rear = count = 0;
		queue = (T[]) (new Object[DEFAULT_CAPACITY]);
	}

	/**
	 * Creates an empty queue using the specified capacity.
	 * @param initialCapacity
	 */
	@SuppressWarnings("unchecked")
	public CircularArrayQueue(int initialCapacity) {
		front = rear = count = 0;
		queue = ((T[]) (new Object[initialCapacity]));
	}

	/**
	 * Adds the specified element to the rear of the queue. Does not check capacity!
	 * @param element
	 */
	public void enqueue(T element) {
		queue[rear] = element;

		rear = (rear + 1) % queue.length;

		count++;
	}
	
	/**
	 * Adds the specified element to the rear of the queue ensuring enough space in the underlaying array.
	 * @param element
	 */
	public void enqueueSafe(T element) {
		if (count+1 >= queue.length) {
			int maxPlus = Math.min(queue.length, 1024);
			ensureCapacity(queue.length + maxPlus);
		}
		
		queue[rear] = element;

		rear = (rear + 1) % queue.length;

		count++;
	}

	/**
	 * Removes the element at the front of the queue and returns a reference to it.
	 * 
	 * @return
	 */
	public T dequeue() {
		T result = queue[front];
		queue[front] = null;

		front = (front + 1) % queue.length;

		count--;

		return result;
	}

	/**
	 * Returns a reference to the element at the front of the queue.
	 * The element is not removed from the queue. Throws an
	 * EmptyCollectionException if the queue is empty.
	 * @return
	 */
	public T first()  {
		return queue[front];
	}

	/**
	 * Returns true if this queue is empty and false otherwise.
	 * @return
	 */
	public boolean isEmpty() {
		return (count == 0);
	}

	/**
	 * Returns the number of elements currently in this queue.
	 * @return
	 */
	public int size() {
		return count;
	}

	/**
	 * Returns a string representation of this queue.
	 */
	@Override	
	public String toString() {
		if (count == 0) return "queue-empty";
		
		StringBuffer result = new StringBuffer();
		int scan = front;

		while (true) {
			if (queue[scan] != null) {
				result.append(queue[scan].toString());
				result.append("\n");
			}
			
			scan = (scan + 1) % queue.length;
			if (scan == rear) break;			
		}

		return result.toString();
	}

	/**
	 * Creates a new array to store the contents of the queue.
	 * @param newCapacity
	 */
	public void ensureCapacity(int newCapacity) {
		if (newCapacity <= queue.length) return;
		
		T[] larger = (T[]) (new Object[newCapacity]);

		for (int scan = 0; scan < count; scan++) {
			larger[scan] = queue[front];
			front = (front + 1) % queue.length;
		}

		front = 0;
		rear = count;
		queue = larger;
	}
	
	/**
	 * What is the (current) maximum capacity in there?
	 * @return
	 */
	public int getCapacity() {
		return queue.length;
	}
}

