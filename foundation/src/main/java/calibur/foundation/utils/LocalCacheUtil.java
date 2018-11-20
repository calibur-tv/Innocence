package calibur.foundation.utils;

import android.os.Environment;
import android.os.StatFs;

/**
 * author : J.Chou
 * e-mail : who_know_me@163.com
 * time   : 2018/11/20 1:30 PM
 * version: 1.0
 * description:
 */
public class LocalCacheUtil {

	//
	private static final int ALERT_LOW_CACHE_SIZE = 50 * 1024 * 1024;

	// 判断SD卡是否被挂载
	public static boolean isSDCardMounted() {
		try {
			return Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED);
		} catch (Exception ex) {
			return false;
		}

	}

	/**
	 * 计算SD卡的剩余空间
	 *
	 * @return 剩余空间
	 */
	public static long getSDAvailableSize() {
		if (isSDCardMounted()) {
			return getAvailableSize(Environment.getExternalStorageDirectory().toString());
		}

		return 0;
	}

	/**
	 * 计算系统的剩余空间
	 *
	 * @return 剩余空间
	 */
	public static long getSystemAvailableSize() {
		// context.getFilesDir().getAbsolutePath();
		return getAvailableSize("/data");
	}


	/**
	 * 获取SD卡的总空间
	 *
	 */
	public static long getSDTotalSize() {
		if (isSDCardMounted()) {
			return getTotalSize(Environment.getExternalStorageDirectory().toString());
		}

		return 0;
	}

	/**
	 * 获取系统可读写的总空间
	 *
	 */
	public static long getSysTotalSize() {
		return getTotalSize("/data");
	}

	/**
	 * 是否有足够的空间
	 *
	 */
	public static boolean hasEnoughSpace() {
		if (isSDCardMounted()) {
			return getSDAvailableSize() >= ALERT_LOW_CACHE_SIZE;
		} else {
			return getSystemAvailableSize() >= ALERT_LOW_CACHE_SIZE;
		}
	}


	/**
	 * 计算剩余空间
	 *
	 * @param path path
	 */
	private static long getAvailableSize(String path) {
		try {
			StatFs fileStats = new StatFs(path);
			fileStats.restat(path);
			return (long) fileStats.getAvailableBlocks() * fileStats.getBlockSize(); // 注意与fileStats.getFreeBlocks()的区别
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return ALERT_LOW_CACHE_SIZE;
		}
	}

	/**
	 * 计算总空间
	 *
	 * @param path path
	 */
	private static long getTotalSize(String path) {
		StatFs fileStats = new StatFs(path);
		fileStats.restat(path);
		return (long) fileStats.getBlockCount() * fileStats.getBlockSize();
	}

}
