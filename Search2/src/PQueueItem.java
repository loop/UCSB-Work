/*
 * PQueueItem.java
 *
 * Computer Science E-119, Harvard University
 */

/**
 * PQueueItem - a class that is used for items stored in a priority queue (e.g.,
 * a heap). Associated with each item is a priority, and the class' compareTo()
 * method compares items according to their priorities.
 */
public class PQueueItem implements Comparable<PQueueItem> {
    private Object data;
    private double priority;

    public PQueueItem(Object data, double priority) {
        this.data = data;
        this.priority = priority;
    }

    public Object getData() {
        return data;
    }

    public int compareTo(PQueueItem other) {
        double diff = priority - other.priority;
        if (diff > 0)
            return 1;
        else if (diff < 0)
            return -1;
        else
            return 0;
    }
}
