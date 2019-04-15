package laofuzi.concurrent;

import java.util.LinkedList;

/**
 * 一个事件队列的例子
 * 
 * @author eric
 *
 */
public class EventQueue {
	private int maxSize = 10;
	private LinkedList<Event> queue;

	public EventQueue(int maxSize) {
		this.maxSize = maxSize;
		queue = new LinkedList<Event>();
	}

	public void offer(Event event) {
		synchronized (queue) {
			while (queue.size() >= maxSize) {//注意这个循环
				try {
					queue.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			queue.addLast(event);
			queue.notifyAll();
		}

	}

	public Event take() {
		synchronized (queue) {
			while (queue.isEmpty()) {
				try {
					queue.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Event event = queue.getFirst();
			queue.notifyAll();
			return event;
		}
	}
}

class Event {

}
