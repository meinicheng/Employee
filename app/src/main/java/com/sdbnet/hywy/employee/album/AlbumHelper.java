package com.sdbnet.hywy.employee.album;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

/**
 * 专辑帮助类
 * 
 * @author Administrator
 * 
 */
public class AlbumHelper {
	private final String TAG = getClass().getSimpleName();
	private Context context;
	private ContentResolver mReslover;

	// 缩略图列表
	private HashMap<String, String> thumbnailList = new HashMap<String, String>();
	// 专辑列表
	private List<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();
	private HashMap<String, ImageBucket> bucketList = new HashMap<String, ImageBucket>();

	private static AlbumHelper instance;

	private AlbumHelper() {
	}

	public static AlbumHelper getHelper() {
		if (instance == null) {
			instance = new AlbumHelper();
		}
		return instance;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		if (this.context == null) {
			this.context = context;
			mReslover = context.getContentResolver();
		}
	}

	/**
	 * 从数据库中得到缩略图
	 */

	private void getAlbumThumbnail() {
		String[] projection = { BaseColumns._ID, Thumbnails.IMAGE_ID,
				Thumbnails.DATA };
		Cursor cursor = mReslover.query(Thumbnails.EXTERNAL_CONTENT_URI,
				projection, null, null, null);
		if (cursor.moveToFirst()) {

			do {
				// Get the field values
				int _id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
				int image_id = cursor.getInt(cursor
						.getColumnIndex(Thumbnails.IMAGE_ID));
				String image_path = cursor.getString(cursor
						.getColumnIndex(Thumbnails.DATA));

				// Do something with the values.
				// Log.i(TAG, _id + " image_id:" + image_id + " path:"
				// + image_path + "---");
				// HashMap<String, String> hash = new HashMap<String, String>();
				// hash.put("image_id", image_id + "");
				// hash.put("path", image_path);
				// thumbnailList.add(hash);
				thumbnailList.put("" + image_id, image_path);
			} while (cursor.moveToNext());
		}
		cursor.close();
	}

	/**
	 * 从本地数据库中得到原图
	 */
	private void getAlbumColumnData() {
		String[] projection = { BaseColumns._ID, AlbumColumns.ALBUM,
				AlbumColumns.ALBUM_ART, AlbumColumns.ALBUM_KEY,
				AlbumColumns.ARTIST, AlbumColumns.NUMBER_OF_SONGS };
		Cursor cursor = mReslover.query(Albums.EXTERNAL_CONTENT_URI,
				projection, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				// Get the field values
				int _id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
				String album = cursor.getString(cursor
						.getColumnIndex(AlbumColumns.ALBUM));
				String albumArt = cursor.getString(cursor
						.getColumnIndex(AlbumColumns.ALBUM_ART));
				String albumKey = cursor.getString(cursor
						.getColumnIndex(AlbumColumns.ALBUM_KEY));
				String artist = cursor.getString(cursor
						.getColumnIndex(AlbumColumns.ARTIST));
				int numOfSongs = cursor.getInt(cursor
						.getColumnIndex(AlbumColumns.NUMBER_OF_SONGS));

				// Do something with the values.
				Log.i(TAG, _id + " album:" + album + " albumArt:" + albumArt
						+ "albumKey: " + albumKey + " artist: " + artist
						+ " numOfSongs: " + numOfSongs + "---");
				HashMap<String, String> hash = new HashMap<String, String>();
				hash.put("_id", _id + "");
				hash.put("album", album);
				hash.put("albumArt", albumArt);
				hash.put("albumKey", albumKey);
				hash.put("artist", artist);
				hash.put("numOfSongs", numOfSongs + "");
				albumList.add(hash);

			} while (cursor.moveToNext());

		}
		cursor.close();

	}

	/**
	 * 是否创建了图片集
	 */
	boolean hasBuildImagesBucketList = false;

	/**
	 * 得到图片集
	 */
	private void buildImagesBucketList() {
		long startTime = System.currentTimeMillis();

		// 构造缩略图索引
		getAlbumThumbnail();

		// 构造相册索引
		String columns[] = new String[] { BaseColumns._ID,
				ImageColumns.BUCKET_ID, ImageColumns.PICASA_ID,
				MediaColumns.DATA, MediaColumns.DISPLAY_NAME,
				MediaColumns.TITLE, MediaColumns.SIZE,
				ImageColumns.BUCKET_DISPLAY_NAME };
		// 得到一个游标
		Cursor cur = mReslover.query(Media.EXTERNAL_CONTENT_URI, columns, null,
				null, null);
		if (cur.moveToFirst()) {
			// 获取指定列的索引
			int photoIDIndex = cur.getColumnIndexOrThrow(BaseColumns._ID);
			int photoPathIndex = cur.getColumnIndexOrThrow(MediaColumns.DATA);
			int photoNameIndex = cur
					.getColumnIndexOrThrow(MediaColumns.DISPLAY_NAME);
			int photoTitleIndex = cur.getColumnIndexOrThrow(MediaColumns.TITLE);
			int photoSizeIndex = cur.getColumnIndexOrThrow(MediaColumns.SIZE);
			int bucketDisplayNameIndex = cur
					.getColumnIndexOrThrow(ImageColumns.BUCKET_DISPLAY_NAME);
			int bucketIdIndex = cur
					.getColumnIndexOrThrow(ImageColumns.BUCKET_ID);
			int picasaIdIndex = cur
					.getColumnIndexOrThrow(ImageColumns.PICASA_ID);
			// 获取图片总数
			// int totalNum = cur.getCount();

			do {
				String _id = cur.getString(photoIDIndex);
				String name = cur.getString(photoNameIndex);
				String path = cur.getString(photoPathIndex);
				String title = cur.getString(photoTitleIndex);
				String size = cur.getString(photoSizeIndex);
				String bucketName = cur.getString(bucketDisplayNameIndex);
				String bucketId = cur.getString(bucketIdIndex);
				String picasaId = cur.getString(picasaIdIndex);

				// Log.i(TAG, _id + ", bucketId: " + bucketId + ", picasaId: "
				// + picasaId + " name:" + name + " path:" + path
				// + " title: " + title + " size: " + size + " bucket: "
				// + bucketName + "---");

				ImageBucket bucket = bucketList.get(bucketId);
				if (bucket == null) {
					bucket = new ImageBucket();
					bucketList.put(bucketId, bucket);
					bucket.imageList = new ArrayList<ImageItem>();
					bucket.bucketName = bucketName;
				}
				bucket.count++;
				ImageItem imageItem = new ImageItem();
				imageItem.imageId = _id;
				imageItem.imagePath = path;
				imageItem.thumbnailPath = thumbnailList.get(_id);
				bucket.imageList.add(imageItem);

			} while (cur.moveToNext());
		}
		cur.close();

		// Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet()
		// .iterator();
		// while (itr.hasNext()) {
		// Map.Entry<String, ImageBucket> entry = (Map.Entry<String,
		// ImageBucket>) itr
		// .next();
		// ImageBucket bucket = entry.getValue();
		// Log.d(TAG, entry.getKey() + ", " + bucket.bucketName + ", "
		// + bucket.count + " ---------- ");
		// for (int i = 0; i < bucket.imageList.size(); ++i) {
		// ImageItem image = bucket.imageList.get(i);
		// Log.d(TAG, "----- " + image.imageId + ", " + image.imagePath
		// + ", " + image.thumbnailPath);
		// }
		// }
		hasBuildImagesBucketList = true;
		long endTime = System.currentTimeMillis();
		Log.d(TAG, "use time: " + (endTime - startTime) + " ms");
	}

	/**
	 * 得到图片集
	 * 
	 * @param refresh
	 * @return
	 */
	public List<ImageBucket> getImagesBucketList(boolean refresh) {
		if (refresh || !hasBuildImagesBucketList) {
			buildImagesBucketList();
		}
		List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
		Iterator<Entry<String, ImageBucket>> itr = bucketList.entrySet()
				.iterator();
		while (itr.hasNext()) {
			Map.Entry<String, ImageBucket> entry = itr.next();
			tmpList.add(entry.getValue());
		}
		return tmpList;
	}

	/**
	 * 得到原始图像路径
	 * 
	 * @param image_id
	 * @return
	 */
	public String getOriginalImagePath(String image_id) {
		String path = null;
		Log.i(TAG, "---(^o^)----" + image_id);
		String[] projection = { BaseColumns._ID, MediaColumns.DATA };
		Cursor cursor = mReslover.query(Media.EXTERNAL_CONTENT_URI, projection,
				BaseColumns._ID + "=" + image_id, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			path = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));

		}
		return path;
	}

	public static class ImageBucket implements Serializable {
		public int count = 0;
		public String bucketName;
		public List<ImageItem> imageList;

		@Override
		public String toString() {
			return "ImageBucket [count=" + count + ", bucketName=" + bucketName
					+ ", imageList=" + imageList + "]";
		}

	}

	/**
	 * 一个图片对象
	 * 
	 * @author Administrator
	 * 
	 */
	public static class ImageItem implements Serializable {
		/**
		 * 
		 */
		public String thumbnailPath;
		public String imagePath;

		public boolean isSelected = false;
		public String imageId;

		@Override
		public String toString() {
			return "{\"thumbnailPath\":\"" + thumbnailPath + "\", \"imagePath\":\""
					+ imagePath + "\",\"isSelected\":\"" + isSelected
					+ "\", \"imageId\":\"" + imageId + "\"}";
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((imagePath == null) ? 0 : imagePath.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ImageItem other = (ImageItem) obj;
			if (imagePath == null) {
				if (other.imagePath != null)
					return false;
			} else if (!imagePath.equals(other.imagePath))
				return false;
			return true;
		}

	}

}
