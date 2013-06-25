package com.eebbk.greenbrowser.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

/**
 * 
 * Some Bitmap tools.
 * 
 * @author humingming <humingming@oaserver.dw.gdbbk.com>
 * 
 */
public final class BitmapUtils {

	private final static String TAG = "BitmapUtils";

	private static boolean checkFileDirExists(String fileName) {
		String dir = getParentDir(fileName);
		if (null == dir) {
			return false;
		}

		File fDir = new File(dir);
		try {
			if (!fDir.exists()) {
				if (!fDir.mkdirs()) {
					JLog.e(TAG, "create folder " + dir + " failed");
				}
			}

			return true;

		} catch (SecurityException e) {
			JLog.e(TAG, "create folder " + dir + " failed: " + e.toString());
			return false;
		}
	}

	/**
	 * Compute give bitmap size's matched sample value.
	 * 
	 * @param opts
	 *            Object of {@link BitmapFactory.Options}, the source bitmap
	 *            size is in it.
	 * @param tagW
	 *            Target width.
	 * @param tagH
	 *            Target height.
	 * @return Matched sample value.
	 */
	public final static int computeSampleSize(BitmapFactory.Options opts,
			int tagW, int tagH) {
		/*
		 * int scale = 1; if (opts.outHeight > w || opts.outWidth > h) { scale =
		 * (int) Math.pow( 2, (int) Math.round(Math.log(w / (double)
		 * Math.max(opts.outHeight, opts.outWidth)) / Math.log(0.5))); if (scale
		 * <= 0) { scale = 1; } }
		 * 
		 * return scale;
		 */

		return computeSampleSize(opts.outWidth, opts.outHeight, tagW, tagH);
	}

	/**
	 * Compute give bitmap size' matched sample value.
	 * 
	 * @param srcW
	 *            Source bitmap width.
	 * @param srcH
	 *            Source bitmap height.
	 * @param tagW
	 *            Target width.
	 * @param tagH
	 *            Target height.
	 * @return Matched sample value.
	 */
	public final static int computeSampleSize(int srcW, int srcH, int tagW,
			int tagH) {
		int scale = 1;
		if (srcW > tagH || srcH > tagW) {
			scale = (int) Math.pow(
					2,
					(int) Math.round(Math.log(tagW
							/ (double) Math.max(srcW, srcH))
							/ Math.log(0.5)));
			if (scale <= 0) {
				scale = 1;
			}
		}

		return scale;
	}

	/**
	 * Optimization bitmap decode. Avoid out of memory Exception. Use the bitmap
	 * default size as source size. see
	 * {@link #decodeBitmapLocal(byte[], int, int, int, int, int, int)}.
	 * 
	 * @param bytes
	 * @param offset
	 * @param length
	 * @param tagW
	 * @param tagH
	 * @return
	 */
	public final static Bitmap decodeBitmapLocal(byte[] bytes, int offset,
			int length, int tagW, int tagH) {
		if (null == bytes || length <= 0 || offset >= length) {
			JLog.d(TAG, "params is invalid !");
			return null;
		}

		BitmapFactory.Options bfSizeOp = new BitmapFactory.Options();
		bfSizeOp.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, offset, length, bfSizeOp);

		return decodeBitmapLocal(bytes, offset, length, bfSizeOp.outWidth,
				bfSizeOp.outHeight, tagW, tagH);
	}

	/**
	 * Optimization bitmap decode. Avoid out of memory Exception. Wrapper of
	 * {@link BitmapFactory#decodeByteArray(byte[], int, int, BitmapFactory.Options)}
	 * .
	 * 
	 * @param bytes
	 *            Bytes pixel of bitmap.
	 * @param offset
	 * @param length
	 * @param srcW
	 *            Source bitmap width.
	 * @param srcH
	 *            Source bitmap height.
	 * @param tagW
	 *            Target width.
	 * @param tagH
	 *            Target height.
	 * @return Decoded bitmap, if occurred error, will return null.
	 */
	public final static Bitmap decodeBitmapLocal(byte[] bytes, int offset,
			int length, int srcW, int srcH, int tagW, int tagH) {
		if (null == bytes || length <= 0 || offset >= length || srcW <= 0
				|| srcH <= 0) {
			JLog.d(TAG, "params is invalid !");
			return null;
		}

		Bitmap bmp = null;

		try {
			// decide target image size.
			int scale = 1;
			if (tagW <= 0 && tagH <= 0) {
				scale = 1;
			} else {
				scale = computeSampleSize(srcW, srcH, tagW, tagH);
			}

			// decode with inSampleSize and let it auto-gc.
			BitmapFactory.Options bfOp = new BitmapFactory.Options();
			bfOp.inSampleSize = scale;
			bfOp.inPurgeable = true;
			bmp = BitmapFactory.decodeByteArray(bytes, offset, length, bfOp);

			if (null == bmp || bmp.getWidth() <= 0 || bmp.getHeight() <= 0) {
				JLog.d(TAG,
						"Optimize decode bitmap failed, try to use origin decode!");
				bmp = BitmapFactory.decodeByteArray(bytes, offset, length);
			}

		} catch (Exception e) {
			e.printStackTrace();
			bmp = null;
		} finally {

		}

		if (null == bmp) {
			JLog.d(TAG, "==============> " + "length: " + length + "srcW: "
					+ srcW + " srcH: " + srcH + " tagW: " + tagW + " tagH: "
					+ tagH + "decode bitmap failed !");
		}

		return bmp;
	}

	/**
	 * Optimization bitmap decode. Avoid out of memory Exception. Decode the
	 * bitmap file as {@link InputStream}. see
	 * {@link #decodeBitmapLocal(InputStream, int, int)}.
	 * 
	 * @param file
	 *            {@link File} of bitmap.
	 * @param tagW
	 *            Target width, if set 0 it will be bitmap origin width.
	 * @param tagH
	 *            Target height, if set 0 it will be bitmap origin height.
	 * @return Decoded bitmap, if occurred error, will return null.
	 */
	public final static Bitmap decodeBitmapLocal(File file, int tagW, int tagH) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
		} catch (Exception e) {
			JLog.e(TAG, e.toString());
			return null;
		}

		return decodeBitmapLocal(is, tagW, tagH);
	}

	/**
	 * Optimization bitmap decode. Avoid out of memory Exception. Wrapper of
	 * {@link BitmapFactory#decodeStream(InputStream, Rect, BitmapFactory.Options)}
	 * .
	 * 
	 * @param is
	 *            Bitmap local input stream.
	 * @param tagW
	 *            Target width, if set 0 it will be bitmap origin width.
	 * @param tagH
	 *            Target height, if set 0 it will be bitmap origin height.
	 * @return Decoded bitmap, if occurred error, will return null.
	 */
	public final static Bitmap decodeBitmapLocal(InputStream is, int tagW,
			int tagH) {
		if (null == is) {
			JLog.d(TAG, "InputStream is null!");
			return null;
		}

		Bitmap bmp = null;

		try {
			// decide target image size.
			int scale = 1;
			if (tagW <= 0 && tagH <= 0) {
				scale = 1;
			} else {
				BitmapFactory.Options bfSizeOp = new BitmapFactory.Options();

				bfSizeOp.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(is, null, bfSizeOp);
				scale = computeSampleSize(bfSizeOp, tagW, tagH);
			}

			// decode with inSampleSize and let it auto-gc.
			BitmapFactory.Options bfOp = new BitmapFactory.Options();
			bfOp.inSampleSize = scale;
			bfOp.inPurgeable = true;
			bmp = BitmapFactory.decodeStream(is, null, bfOp);

			if (null == bmp || bmp.getWidth() <= 0 || bmp.getHeight() <= 0) {
				JLog.d(TAG,
						"Optimize decode bitmap failed, try to use origin decode!");
				bmp = BitmapFactory.decodeStream(is);
			}

		} catch (Exception e) {
			e.printStackTrace();
			bmp = null;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			// occur out of memory error, we let system gc.
			// System.gc();
			bmp = null;
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				// ignore it.
			}
		}

		if (null == bmp) {
			JLog.d(TAG, "==============> " + is.toString()
					+ " : decode bitmap failed!");
		}

		return bmp;
	}

	/**
	 * Optimization bitmap decode. Avoid out of memory Exception for internet.
	 * Wrapper of {@link BitmapFactory#decodeStream(InputStream)}.
	 * 
	 * @param is
	 * @param tagW
	 * @param tagH
	 * @return
	 */
	public final static Bitmap decodeBitmapOnline(InputStream is, int tagW,
			int tagH) {
		if (null == is) {
			JLog.e(TAG, "InputStream is null!");
			return null;
		}

		Bitmap bmp = null;

		try {
			bmp = BitmapFactory.decodeStream(is);
		} catch (Exception e) {
			e.printStackTrace();
			bmp = null;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			// occur out of memory error, we let system gc.
			// System.gc();
			bmp = null;
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				// ignore it.
			}
		}

		if (null == bmp) {
			JLog.d(TAG, "==============> " + is.toString()
					+ " : decode bitmap failed!");
		}

		return bmp;
	}

	/**
	 * Safely free a {@link Bitmap}.
	 * 
	 * @param bmp
	 *            Object of bitmap.
	 */
	public final static void freeBitmap(Bitmap bmp) {
		if (null != bmp && !bmp.isRecycled()) {
			bmp.recycle();
		}
	}

	/**
	 * Safely free all {@link Bitmap} in a bitmap {@link List}.
	 * 
	 * @param bmpList
	 *            Bitmap list.
	 */
	public final static void freeBitmapList(List<Bitmap> bmpList) {
		if (null != bmpList) {
			for (Bitmap bmp : bmpList) {
				if (null != bmp && !bmp.isRecycled()) {
					bmp.recycle();
				}
			}
			bmpList.clear();
		}
	}

	/**
	 * 
	 * @param bmp
	 * @return
	 */
	// @SuppressWarnings("deprecation")
	public final static BitmapDrawable getBitmapDrawable(Bitmap bmp) {
		return new BitmapDrawable(bmp);
	}

	private static String getParentDir(String path) {
		if (null == path) {
			return null;
		}

		try {
			int last = path.lastIndexOf("/");
			if (last <= -1) {
				return null;
			}

			return path.substring(0, last);

		} catch (Exception e) {
			JLog.e(TAG, e.toString());
			return null;
		}
	}

	/**
	 * Save bitmap to specified file.
	 * 
	 * @param bitmap
	 *            Object of save {@link Bitmap}.
	 * @param fileName
	 *            save path.
	 * @return True success otherwise false.
	 */
	public final static boolean saveBitmapToFile(Bitmap bitmap, String fileName) {
		boolean ret = false;
		File file = null;
		FileOutputStream fOut = null;

		// check save bitmap path.
		if (!checkFileDirExists(fileName)) {
			return false;
		}

		file = new File(fileName);

		try {
			file.createNewFile();
			fOut = new FileOutputStream(file);

			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();

			ret = true;

		} catch (Exception e) {
			e.printStackTrace();
			ret = false;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			// occur out of memory error, we let system gc.
			// System.gc();
			ret = false;
		} finally {
			if (null != fOut) {
				try {
					fOut.close();
					ret = true;
				} catch (IOException e) {
					// ignore it.
				}
			}
		}

		if (!ret) {
			JLog.d(TAG, "==============> " + fileName
					+ " : cache image failed!");
		}

		return ret;
	}

	/**
	 * Scale {@link Bitmap} to give size. Wrapper of
	 * {@link Bitmap#createScaledBitmap(Bitmap, int, int, boolean)}.
	 * 
	 * @param src
	 *            Source bitmap.
	 * @param tagW
	 *            Target scale width, if 0 will be bitmap origin width.
	 * @param tagH
	 *            Target scale height, if 0 will be bitmap origin height.
	 * @return Scaled bitmap, if occurred error, will return null.
	 */
	public final static Bitmap scaleBitmap(Bitmap src, int tagW, int tagH) {
		if (null == src) {
			return null;
		}

		Bitmap target = null;
		try {
			if (tagW <= 0 || tagH <= 0) {
				return src;
			}

			if (tagW == src.getWidth() && tagH == src.getHeight()) {
				return src;
			}

			// bmp = ThumbnailUtils.extractThumbnail(src, width, height);
			target = Bitmap.createScaledBitmap(src, tagW, tagH, true);
			if (null != target && !target.isRecycled()) {
				if (!src.isRecycled()) {
					src.recycle();
				}

				// target = bmp;
				return target;
			}

			JLog.d(TAG, "==============> " + src.toString()
					+ " : scale image failed!");
			return src;

		} catch (Exception e) {
			e.printStackTrace();
			return src;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
			// occur out of memory error, we let system gc.
			// System.gc();
			return src;
		}
	}

	/**
	 * Decode {@link InputStream} to {@link Bitmap} and scale it to give size.
	 * see {@link #decodeBitmapLocal(InputStream, int, int)}.
	 * 
	 * @param is
	 *            Object of {@link InputStream}.
	 * @param tagW
	 *            Target bitmap width, if 0 will be bitmap origin width.
	 * @param tagH
	 *            Target bitmap height, if 0 will be bitmap origin height.
	 * @return Decoded and scaled bitmap, if occurred error, will return null.
	 */
	public final static Bitmap scaleBitmap(InputStream is, int tagW, int tagH) {
		Bitmap bmp = null;

		bmp = decodeBitmapLocal(is, tagW, tagH);
		bmp = scaleBitmap(bmp, tagW, tagH);

		return bmp;
	}

}
