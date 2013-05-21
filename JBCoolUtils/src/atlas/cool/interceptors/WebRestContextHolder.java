/**
 * 
 */
package atlas.cool.interceptors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author formica
 * 
 */
public class WebRestContextHolder {

	private static final ThreadLocal<Map<String, Object>> THREAD_WITH_CONTEXT = new ThreadLocal<Map<String, Object>>();

	private WebRestContextHolder() {
	}

	public static void put(String key, Object payload) {

		if (THREAD_WITH_CONTEXT.get() == null) {

			THREAD_WITH_CONTEXT.set(new HashMap<String, Object>());
		}

		THREAD_WITH_CONTEXT.get().put(key, payload);

	}

	public static Object get(String key) {
		if (THREAD_WITH_CONTEXT.get() == null) {
			return null;
		}
		return THREAD_WITH_CONTEXT.get().get(key);
	}

	public static boolean containsKey(String key) {

		if (THREAD_WITH_CONTEXT.get() == null) {
			return false;
		}
		return THREAD_WITH_CONTEXT.get().containsKey(key);
	}

	public static void cleanupThread() {

		THREAD_WITH_CONTEXT.remove();
	}
}
