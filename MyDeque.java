/**
 * File: MyDeque.java
 * Description: a generic double ended queue with bounded size
 * @author Zhiwei Jia
 */

import java.util.ArrayList;

/**
 * Class name: MyDeque
 * Description: a generic double ended queue class, which uses ArrayList as
 * field and is implemented by creating circular ArrayList; 
 */
public class MyDeque<E> implements Cloneable {
    
    private int cap = 0;       // its capacity
    private ArrayList<E> list;
    private int size = 0;      // its actual number of elements inside
    private int rear = 0;      // its rear index
    private int front = 0;     // its front index
    
    /**
     * Constructor with specified capacity
     * @param cap the capacity of this Deque
     */
    public MyDeque(int cap) {
	this.cap = cap;
	list = new ArrayList<>(cap);
	
	// add all elements as null
	for (int i = 0; i < capacity(); i++)
	    list.add(null);
    }
    
    /**
     * Method to return the capacity
     * @return its capacity
     */
    public int capacity() {
	return cap;
    }
    
    /**
     * Method to return the size, i.e., the actual number of elements in
     * this Deque
     * @return the size
     */
    public int size() {
	return size;
    }
    
    /**
     * Method to add an element in the front of this deque
     * @param e the element to add in the front
     * @return whether add successfully
     * @throws NullPointerException if the element to be added is null
     */
    public boolean addFront(E e) throws NullPointerException {
	
	// check if the element is null and if there is still available space
	if (e == null && size() < capacity())
	    throw new NullPointerException();
	if (size() >= capacity())
	    return false;
	
	// if the original Deque is empty, add it and do not change
	// the front index, otherwise increase it by 1, with a mod operation
	if (size() == 0)
	    list.set(front, e);
	else {
	    front++;
	    front %= cap;
	    list.set(front, e);
	}
	
	// increase the size and return true
	size++;
	return true;
    }
    
    /**
     * Method to add an element in the rear of this Deque
     * @param e the element to add in the rear
     * @return whether add successfully
     * @throws NullPointerException if the element to be added is null
     */
    public boolean addBack(E e) throws NullPointerException {
	
	// check if the element is null and if there is still available space
	if (e == null && size() < capacity())
	    throw new NullPointerException();
	if (size() >= capacity())
	    return false;
	
	// if the original Deque is empty, add it and do not change
	// the rear index, otherwise decrease it by 1, with a mod operation
	if (size() == 0)
	    list.set(rear, e);
	else {
	    rear--;
	    if (rear < 0)
		rear += cap;
	    list.set(rear, e);
	}
	
	// increase the size and return true
	size++;
	return true;
    }
    
    /**
     * Method to remove the element in the front of the deque
     * @return the element deleted, or null if there are no such element
     */
    public E removeFront() {
	
	// return null if the deque is empty
	if (size() == 0)
	    return null;
	
	// store the deleted element before delete it, decrease the front
	// index if necessary
	E temp = list.get(front);
	list.set(front, null);
	if (size() > 1) {
	    front--;
	    if (front < 0)
		front += cap;
	}
	
	// decrease the size and return the stored element
	size--;
	return temp;
    }
    
    /**
     * Method to remove the element in the rear of the deque
     * @return the element deleted, or null if there are no such element
     */
    public E removeBack() {
	
	// return null if the deque is empty
	if (size() == 0)
	    return null;
	
	// store the deleted element before delete it, increase the rear
	// index if necessary
	E temp = list.get(rear);
	list.set(rear, null);
	if (size() >1) {
	    rear++;
	    rear %= cap;
        }
	
	// decrease the size and return the stored element
	size--;
	return temp;
    }
    
    /**
     * Method to peek the front element
     * @return the front element, return null if no such element
     */
    public E peekFront() {
	if (size() == 0)
	    return null;
	return list.get(front);
    }
    
    /**
     * Method to peek the rear element
     * @return the rear element, return null if no such element
     */
    public E peekBack() {
	if (size() == 0)
	    return null;
	return list.get(rear);
    }
    
    @Override
    /**
     * Method to return a String for this Deque
     * @return a String
     */
    public String toString() {
	ArrayList<E> copy0 = new ArrayList<>();
	MyDeque<E> copy = new MyDeque<>(size());
        for (int i = 0; i < size(); i++)
            copy0.add(null);
        arrListCopy(list, copy0, 0, size()-1);
	copy.list = copy0;
	
	// build the String object to return
	String result = "[";
	for (int i = 0; i < size()-1; i++) 
	    result += copy.removeFront() + "; ";
	
	// return the String
        return result += copy.removeFront() + "]";
    }

    /**
     * Method to copy an ArrayList to another one in some specified range
     * @param src source ArrayList
     * @param dest destination ArrayList
     * @param from the from index
     * @param to the to index
     */
    private static <E> void arrListCopy(ArrayList<E> src, ArrayList<E> dest,
	    int from, int to) {
	for (int i = from; i <= to; i++)
	    dest.set(i, src.get(i));
    }
}
