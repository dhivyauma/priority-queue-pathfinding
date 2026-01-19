public class ArrayUniquePriorityQueue<T> implements UniquePriorityQueueADT<T> {
	
    private T[] queue; // Store elements in the queue
    private double[] priority;//Store priorities corresponding to elements in the queue
    private int count;// Keep track of the number of elements in the queue

    public ArrayUniquePriorityQueue() {// Initialize the queue and priority arrays 
        queue = (T[]) new Object[10];
        priority = new double[10];
        count = 0; 
    }
    public void add(T data, double prio) { // Add an element with its priority to the queue
        if (contains(data)) return; // If the element is already present in the queue

        if (count == (queue != null ? queue.length : 0)) { //Number of elements in the queue is equal to the current capacity
            int newSize = (queue != null ? queue.length : 0) + 5; // Capacity increase by 5 elements
            T[] newQueue = (T[]) new Object[newSize]; // New arrays with the increased capacity
            double[] newPriority = new double[newSize];

            if (queue != null) { // Copy elements and priorities to the new arrays, if not empty
                for (int i = 0; i < count; i++) {
                    newQueue[i] = queue[i];
                    newPriority[i] = priority[i];
                }
            }

            queue = newQueue; // Update with the new arrays
            priority = newPriority;
        }

        int index = count; //Insert the new element based on its priority into correct position
        while (index > 0 && priority[index - 1] > prio) {
            index--;
        }

        if (index < count) { // If the new element is to be inserted at a position other than the end of the queue
            //Make space for the new element
            for (int i = count; i > index; i--) {
                queue[i] = queue[i - 1];
                priority[i] = priority[i - 1];
            }
        }

        queue[index] = data; // Add the new item and its priority to the queue
        priority[index] = prio;
        count++; // Reflect the addition of a new element
    }

    public boolean contains(T data) {// Loop through the elements in the queue
        for (int i = 0; i < count; i++) {// Current element is equal to the provided data
            if (queue[i].equals(data)) {
                return true;
            }
        }
        return false;
    }

    public T peek() throws CollectionException {// If queue is empty or not
        if (isEmpty()) {
            throw new CollectionException("PQ is empty");
        }
        return queue[0];
    }

    public T removeMin() throws CollectionException {// If queue is empty or not
        if (isEmpty()) {
        	
            throw new CollectionException("PQ is empty");
        }
        T min = queue[0];// Retrieve the minimum element from the front of the queue

        for (int i = 0; i < count - 1; i++) {// Shift elements to the left
            queue[i] = queue[i + 1];// Each element moved one position to the left
            priority[i] = priority[i + 1];// Move the priority corresponded
        }

        queue[count - 1] = null; // Null last element to remove it from the queue
        priority[count - 1] = 0.0;// Reset the priority of the removed element to 0
        count--;// Count updated to reflect the removal of an element

        return min;
    }

    public void updatePriority(T data, double newPrio) throws CollectionException {
        // If the item exists in the priority queue
        if (!contains(data)) {
            //throw an exception with a descriptive message, if the item is not found
            throw new CollectionException("Item not found in PQ");
        }
        
        //Re-add the item with the new priority
        removeAndReAdd(data, newPrio);
    }

    private void removeAndReAdd(T data, double newPrio) {//Initialize the index to know the position of the item
        int index = -1;
        
        for (int i = 0; i < count; i++) {// Search for the item in the queue
            if (queue[i].equals(data)) {// Store its index if found 
                index = i;
                break;
            }
        }

        for (int i = index; i < count - 1; i++) {// Shift elements to the left to remove the item from the queue
            queue[i] = queue[i + 1]; 
            priority[i] = priority[i + 1]; // Move the corresponding priority
        }

        queue[count - 1] = null;// Last element set to null to remove it from the queue
        priority[count - 1] = 0.0;// Reset the priority of the removed element to 0
        count--;// Decrement the count to reflect the removal of an element
        
        add(data, newPrio);//Re-add the item with the new priority
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public int size() {
        return count;
    }

    public int getLength() {
        return queue.length;
    }
    
    public String toString() {
        if (isEmpty()) { // Check if the priority queue is empty
            return "The PQ is empty"; 
        }
        
        String result = ""; // Initialize an empty string to store the result
        for (int i = 0; i < count; i++) { // Iterate through the elements in the queue
            result += queue[i] + " [" + priority[i] + "], "; //Each element and its priority to the result string
        }
  
        result = result.substring(0, result.length() - 2);
        
        return result;
    }
}

