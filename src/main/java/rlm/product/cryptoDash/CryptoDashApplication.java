package rlm.product.cryptoDash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rlm.product.cryptoDash.restApi.bitvavoApi.BitvavoPriceTicker;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class CryptoDashApplication {

	public static void main(String[] args) {

		// Start the Spring application
		SpringApplication.run(CryptoDashApplication.class, args);

		// Create ticker instance
		BitvavoPriceTicker bitvavoPriceTicker = new BitvavoPriceTicker();

		// Set interval on ticker (in milliseconds)
		Timer timer = new Timer();
		int begin = 0;
		int timeInterval = 5000;
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// Start ticking
				bitvavoPriceTicker.startTicking();
			}
		}, begin, timeInterval);
	}
}