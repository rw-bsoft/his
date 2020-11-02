package phis.source.utils;

/**
 * 获取主键ID，使用时间+计数器方式实现
 * 
 * @author
 * 
 */
public class IdUtils {

	public static IdUtils instance = null;

	private static final long ONE_STEP = 100;
	private static long lastTime = System.currentTimeMillis();
	private static short count = 0;

	/**
	 * 获取单例对象
	 * 
	 * @return
	 */
	public synchronized static IdUtils getInstanse() {
		if (instance == null) {
			instance = new IdUtils();
		}
		return instance;
	}

	private IdUtils() {
	}

	/**
	 * 根据对象获取表主键
	 * 
	 * @param clazz
	 * @return
	 */
	public synchronized Long getUID() {
		try {
			if (count == ONE_STEP) {
				boolean done = false;
				while (!done) {
					long now = System.currentTimeMillis();
					if (now == lastTime) {
						try {
							Thread.sleep(1);
						} catch (java.lang.InterruptedException e) {
						}
						continue;
					} else {
						lastTime = now;
						count = 0;
						done = true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Long result = lastTime * 100 + (count++);
		return result;
	}
}