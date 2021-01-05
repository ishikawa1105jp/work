package com.si1105.utils;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * Utility method collection class.
 * 
 * @author shinya@si1105.com
 *
 */
public class Utils {

	/**
	 * Returns parameters as String array.
	 * 
	 * @param objects parameters
	 * @return params as String array.
	 */
	public static String[] asArray(Object... objects) {
		return Arrays.stream(objects).map(e -> e == null ? "null" : e.toString()).toArray(String[]::new);
	}

	/**
	 * Change a file's time stamp.
	 * 
	 * @param file   The target file.
	 * @param ymdhms The new time stamp. It must be yyyyMMddHHmmss formatted.
	 */
	public static void touch(String file, String ymdhms) {
		doTryCatch(() -> {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			Paths.get(file).toFile().setLastModified(sdf.parse(ymdhms).getTime());
		});
	}

	/**
	 * Try - catch Syntax wrapper.
	 * 
	 * @param function
	 */
	public static void doTryCatch(TryCatchConsumer function) {
		function.execute();
	}

	@FunctionalInterface
	public static interface TryCatchConsumer {

		default void execute() {

			try {
				doLogic();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		void doLogic() throws Exception;
	}
}
