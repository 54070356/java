package laofuzi.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class DaemonTest {
	private static Logger log = Logger.getLogger(DaemonTest.class.getName());
	
	public static void main(String[] args) throws InterruptedException {
		final Object lock = new Object();
		//若主线程结束，daemon线程即使在阻塞情况下也会结束
		final Thread t = new Thread() {
			@Override
			public void run() {
				try {
					log.info("wait 3 seconds");
					synchronized(lock) {
						lock.wait(3000);
					}
					
					log.info("sleep 3 seconds");
					TimeUnit.SECONDS.sleep(3);
					log.info("wake up");
				} catch (InterruptedException e) {
					log.info("interrupted=" + this.isInterrupted());// false
				}

			}
		};
		t.setDaemon(true);
		log.info("daemon = " + t.isDaemon());
		t.start();

		TimeUnit.SECONDS.sleep(1);
		log.info("main ended");
	}
}
