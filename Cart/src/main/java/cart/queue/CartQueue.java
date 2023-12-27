package cart.queue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CartQueue {
    private Queue<CartItemTask> queue = new ConcurrentLinkedQueue<>();

    public void enqueue(CartItemTask task) {
        queue.add(task);
    }

    public CartItemTask dequeue() {
        return queue.poll();
    }

    public CartItemTask peekQueue() {
        return queue.peek();
    }
}