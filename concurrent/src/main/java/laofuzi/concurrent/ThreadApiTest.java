package laofuzi.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

public class ThreadApiTest {
	private Logger log = Logger.getLogger(ThreadApiTest.class.getName());

	@Test
	public void sleepDemo() {
		// TimeUnit is better than sleep
		try {
			log.info("sleep 3 seconds...");
			TimeUnit.SECONDS.sleep(3);
			log.info("week up");
		} catch (InterruptedException e) {
			log.log(Level.INFO, e.getMessage(), e);
		}
	}

	@Test
	public void idNameDemo() {
		Thread t1 = new Thread();
		Thread t2 = new Thread();
		log.log(Level.INFO, "thread id = {0}, name={1}", new Object[] { t1.getId(), t1.getName() });
		log.log(Level.INFO, "thread id = {0}, name={1}", new Object[] { t2.getId(), t2.getName() });

	}

	@Test
	public void isInterrupted() throws InterruptedException {
		// interrupt flag will be reset to false after InterruptedException is thrown
		Thread t = new Thread() {
			@Override
			public void run() {

				while (true) {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						log.info("1: interrupted=" + this.isInterrupted());// false
					}
				}
			}
		};

		t.setDaemon(true);
		t.start();
		TimeUnit.SECONDS.sleep(2);
		log.info("2: interrupted=" + t.isInterrupted()); // false
		t.interrupt();

		TimeUnit.SECONDS.sleep(2);
		log.info("3: interrupted=" + t.isInterrupted()); // false
	}

	/**
	 * 线程中断后执行可中断方法会立即抛出异常
	 * 
	 */
	@Test
	public void interrupt() throws InterruptedException {
		log.info("1: interrupted = " + Thread.interrupted()); // false

		Thread.currentThread().interrupt();

		log.info("2: interrupted = " + Thread.currentThread().isInterrupted()); // true

		TimeUnit.SECONDS.sleep(2);
	}


}
