package com.eebbk.greenbrowser.util;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

public class JFileUtil {

	@SuppressWarnings("unused")
	private static final String FLASH_A = "/mnt/flasha/";

	public static final String FLASH = "/mnt/flash/";

	public static final String SDCARD = "/mnt/sdcard/";

	/**
	 * FLASH是否可用
	 * 
	 * @return boolean
	 */
	public static boolean existFlash() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	/**
	 * SDCARD卡是否可用
	 * 
	 * @return
	 */
	public static boolean existSDCard() {
		if (Environment.getExternalFlashStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取手机内部可用空间大小
	 * 
	 * @return long
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	public static long getFlashFreeSize() {
		if (existSDCard()) {
			StatFs sf = new StatFs(FLASH);
			long blockSize = sf.getBlockSize();
			long freeBlocks = sf.getAvailableBlocks();
			return freeBlocks * blockSize; // 单位B
		}

		return -1;
	}

	/**
	 * 获取手机SDcard可用空间大小
	 * 
	 * @return long
	 */
	public static long getSDFreeSize() {
		if (existSDCard()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			long blockSize = sf.getBlockSize();
			long freeBlocks = sf.getAvailableBlocks();
			return freeBlocks * blockSize; // 单位B
		}

		return -1;
	}

	/**
	 * 获取手机内部空间大小
	 * 
	 * @return long
	 */
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	public long getFlashAllSize() {
		if (existSDCard()) {
			StatFs sf = new StatFs(FLASH);
			long blockSize = sf.getBlockSize();
			long allBlocks = sf.getBlockCount();
			return allBlocks * blockSize; // 单位B
		}

		return -1;
	}

	/**
	 * 获取手机SDcard总空间大小
	 * 
	 * @return long
	 */
	public long getSDAllSize() {
		if (existSDCard()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(path.getPath());
			long blockSize = sf.getBlockSize();
			long allBlocks = sf.getBlockCount();
			return allBlocks * blockSize; // 单位B
		}

		return -1;
	}
}
