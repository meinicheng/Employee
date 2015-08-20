package com.sdbnet.hywy.employee.album;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.album.AlbumHelper.ImageItem;
import com.sdbnet.hywy.employee.ui.ImageDetailsActivity;
import com.sdbnet.hywy.employee.ui.PopWindowActivity;

public class ImageGridShowAdapter extends BaseAdapter {
	public static final int REQUEST_CODE_SCAN = 110;
	private final String TAG = getClass().getSimpleName();
	private Activity mContext;
	private List<ImageItem> dataList;
	public Map<String, String> map = new HashMap<String, String>();
	private boolean isEdit;
	private boolean isShowEvery = false;
	private boolean isDelete = false;
	private DisplayImageOptions options;

	public ImageGridShowAdapter(Activity mContext, List<ImageItem> list,
			boolean isEdit) {
		this.mContext = mContext;
		dataList = list;
		this.isEdit = isEdit;

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.no_photo)
				.showImageForEmptyUri(R.drawable.no_photo)
				.showImageOnFail(R.drawable.no_photo).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList == null) {
			dataList = new ArrayList<ImageItem>();
		}
		if (isShowEvery) {
			count = dataList.size() + Bimp.imgPath.size();
		} else if (dataList.size() + Bimp.imgPath.size() > Bimp.IMAGE_COUNT) {
			count = Bimp.IMAGE_COUNT;
		} else {
			count = dataList.size() + Bimp.imgPath.size();
		}
		// if (isShowEvery) {
		// count = dataList.size() + Bimp.bmp.size();
		// } else if (dataList.size() + Bimp.bmp.size() > Bimp.IMAGE_COUNT) {
		// count = Bimp.IMAGE_COUNT;
		// } else {
		// count = dataList.size() + Bimp.bmp.size();
		// }
		if (isEdit)
			count++;
		// Log.e("Grid", "count="+count+"");
		return count;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class Holder {
		private ImageView imgView;
		private ImageView imgDelete;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		final int pos;
		if (parent.getChildCount() != position) {
			pos = getCount() - 1;
		} else {
			pos = position;
		}
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_published_grida,
					null);
			holder = new Holder();
			holder.imgView = (ImageView) convertView
					.findViewById(R.id.item_grida_image);

			holder.imgDelete = (ImageView) convertView
					.findViewById(R.id.item_grid_delete_img);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.imgDelete.setVisibility(View.GONE);
		if (getCount() == pos + 1 && isEdit) {

			holder.imgView.setImageBitmap(BitmapFactory.decodeResource(
					mContext.getResources(), R.drawable.icon_addpic_unfocused));
			holder.imgView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					isDelete = false;
					notifyDataSetChanged();
					Bimp.loadCount = dataList.size();

					// 判断图片选择数量是否超过限制
					if ((Bimp.loadCount + Bimp.imgPath.size()) >= Bimp.IMAGE_COUNT) {
						String msg = mContext
								.getString(R.string.most_choose_num_images);
						Toast.makeText(mContext,
								String.format(msg, Bimp.IMAGE_COUNT),
								Toast.LENGTH_SHORT).show();
						return;
					}

					Intent intent = new Intent(mContext,
							PopWindowActivity.class);
					mContext.startActivityForResult(intent, 60);
					// mContext.overridePendingTransition(R.anim.push_bottom_in,
					// R.anim.push_bottom_out);
				}
			});
		} else {
			if (pos < dataList.size()) {
				loadImage(position, holder.imgView);
			} else if (pos < getCount()) {
				Bitmap bm = null;
				String uri;
				String thumbnailPath=Bimp.imgPath.get(pos - dataList.size()).thumbnailPath;
				if(thumbnailPath!=null&&new File(thumbnailPath).exists()){
					uri = "file://" + thumbnailPath;
				}else{
					uri = "file://" + Bimp.imgPath.get(pos - dataList.size()).imagePath;
				}
//				Log.e(TAG,thumbnailPath+","+new File(thumbnailPath).exists()+","+uri+","+new File(uri).exists());
				Log.e(TAG,thumbnailPath+","+uri+",");
				ImageLoader.getInstance().displayImage(uri, holder.imgView);
//				Log.e(TAG, thumbnailPath + "," + (thumbnailPath == null));
//				if (Bimp.imgPath.get(pos - dataList.size()).thumbnailPath != null) {
//					// bm = BitmapFactory.decodeFile(Bimp.imgPath.get(pos
//					// - dataList.size()).thumbnailPath);
//
//					Log.e(TAG,"uri1="+uri);
//					ImageLoader.getInstance().displayImage(uri, holder.imgView);
//				} else {
//
//
//					Log.e(TAG,"uri2="+uri);
////					bm = BitmapFactory.decodeFile(Bimp.imgPath.get(pos
////							- dataList.size()).imgPath);
//					ImageLoader.getInstance().displayImage(uri, holder.imgView);
//				}
//				bm = ThumbnailUtils.extractThumbnail(bm, 50, 50);
//				holder.imgView.setImageBitmap(bm);
				// holder.imgView.setImageBitmap(Bimp.bmp.get(pos
				// - dataList.size()));

			}
			holder.imgView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					isDelete = false;
					notifyDataSetChanged();
					Intent intent = new Intent(mContext,
							ImageDetailsActivity.class);
					intent.putExtra("list", dataList.toArray());
					intent.putExtra("image_position", pos);
					mContext.startActivityForResult(intent, REQUEST_CODE_SCAN);
				}
			});

			// holder.imgView.setOnLongClickListener(new OnLongClickListener() {
			//
			// @Override
			// public boolean onLongClick(View v) {
			// isDelete = !isDelete;
			// notifyDataSetChanged();
			// return false;
			// }
			// });
		}
		return convertView;
	}

	/**
	 * 开始加载图片，每张图片都会开启一个异步线程去下载。
	 */
	// public void loadImage(int position, ImageView imageView) {
	// if (UtilsAndroid.Sdcard.hasSDCard()) {
	// String imgURl = dataList.get(position).thumbnailPath;
	// ImageLoader.getInstance().asynLoadImage(imageView, imgURl);
	// } else {
	// Toast.makeText(mContext, mContext.getString(R.string.no_find_sd),
	// Toast.LENGTH_SHORT).show();
	// }
	// }

	private void loadImage(int position, ImageView imageView) {
		String imgURl = dataList.get(position).thumbnailPath;
		Log.i(TAG, "imgUrl=" + imgURl);
		ImageLoader.getInstance().displayImage(imgURl, imageView, options);
	}

	public boolean isShowEvery() {
		return isShowEvery;
	}

	public void setShowEvery(boolean isShowEvery) {
		this.isShowEvery = isShowEvery;
		notifyDataSetChanged();
	}

	public void setModleDelete(boolean isDelete) {
		this.isDelete = isDelete;
		notifyDataSetChanged();
	}

}
