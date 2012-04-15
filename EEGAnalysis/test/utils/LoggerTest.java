package utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class LoggerTest {

	@Test
	public void testLog() {
		try {
			Logger.log("text");
			Logger.log(1);
			Logger.log(1.);
			Logger.log(0x01);
			Logger.log(new Object());
			Logger.log(new int[] {1,1});
			Logger.log(new int[] {});
			Logger.log(true);
			Logger.log(false);	
		} catch (Exception e) {
			fail();
		}
	}
}
