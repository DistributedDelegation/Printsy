package cart.queue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CartQueue {
    private Queue<CartItemTask> queue = new ConcurrentLinkedQueue<>();
    private static final int MAX_SIZE = 10; // there will be a maximum of 10 objects in the queue

    public boolean enqueue(CartItemTask task) {
        if (queue.size() >= MAX_SIZE) {
            return false;
        }
        queue.add(task);
        return true;
    }

    public CartItemTask dequeue() {
        return queue.poll();
    }

    public CartItemTask peekQueue() {
        return queue.peek();
    }
}