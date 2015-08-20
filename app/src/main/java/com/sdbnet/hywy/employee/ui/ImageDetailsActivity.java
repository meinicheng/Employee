package com.sdbnet.hywy.employee.ui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.album.AlbumHelper.ImageItem;
import com.sdbnet.hywy.employee.album.Bimp;
import com.sdbnet.hywy.employee.album.ZoomImageView;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;

/**
 * 查看大图的Activity界面。
 * 
 * @author guolin
 */
public class ImageDetailsActivity extends BaseActivity implements
		OnPageChangeListener {

	/**
	 * 对图片进行管理的工具类
	 */
	// private ImageLoader imageLoader;

	/**
	 * 用于管理图片的滑动
	 */
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;

	private List<ImageItem> dataList;

	private List<View> mListViews;
	// /**
	// * 记录所有正在下载或等待下载的任务。
	// */
	// private static Set<LoadImageTask> taskCollection;

	/**
	 * 显示当前图片的页数
	 */
	private Button mButtonDelete;
	private TextView pageText;
	private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image_details);
		options = new DisplayImageOptions.Builder()
//				.showImageOnLoading(R.drawable.no_photo)
				.showImageForEmptyUri(R.drawable.no_photo)
				.showImageOnFail(R.drawable.no_photo).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		// taskCollection = new HashSet<LoadImageTask>();
		initUI();
		// 初始化url
		initDatas();
		initViewPage();

	}

	private void initUI() {
		mButtonDelete = (Button) findViewById(R.id.common_view_title_btn);
		mButtonDelete.setTextColor(Color.RED);
		mButtonDelete.setText(R.string.delete);
		mButtonDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				int page = viewPager.getCurrentItem();

				Log.e(TAG,
						page + "," + dataList.size() + "," + adapter.getCount());
				if (dataList.size() == 0) {
					if (page == 0 && adapter.getCount() == 1) {
						// Bimp.bmp.remove(page);
						Bimp.imgPath.remove(page);
						finish();
						return;
					} else if (page == 0) {
						// Bimp.bmp.remove(page);
						Bimp.imgPath.remove(page);
						mListViews.remove(page);
						pageText.setText((page + 1) + "/" + (mListViews.size()));
						// viewPager.setCurrentItem(page);
						// viewPager.removeViewAt(page);
					} else if (page < adapter.getCount()) {

						// Bimp.bmp.remove(page);
						Bimp.imgPath.remove(page);
						mListViews.remove(page);
						pageText.setText((page) + "/" + (mListViews.size()));
						// viewPager.removeViewAt(page);
						// viewPager.setCurrentItem(page - 1);
					}
				} else {
					if (page < adapter.getCount()) {
						// Bimp.bmp.remove(page);
						Bimp.imgPath.remove(page);
						mListViews.remove(page);
						pageText.setText((page) + "/" + (mListViews.size()));
						// viewPager.setCurrentItem(page - 1);
					}
				}
				adapter.notifyDataSetChanged();
			}
		});
		findViewById(R.id.common_view_title_img).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(getString(R.string.scan));

	}

	private void initDatas() {
		dataList = new ArrayList<ImageItem>();
		Object[] objArr = (Object[]) getIntent().getSerializableExtra("list");
		for (int i = 0; i < objArr.length; i++) {
			dataList.add((ImageItem) objArr[i]);
		}
	}

	private void initViewPage() {
		initViewList();
		int imagePosition = getIntent().getIntExtra("image_position", 0);
		pageText = (TextView) findViewById(R.id.activity_image_details_page_text);
		viewPager = (ViewPager) findViewById(R.id.activity_image_details_view_pager);
		adapter = new ViewPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(imagePosition);
		if (imagePosition >= dataList.size()) {
			mButtonDelete.setVisibility(View.VISIBLE);
		}
		viewPager.setOnPageChangeListener(this);
		viewPager.setEnabled(false);
		// 设定当前的页数和总页数
		// pageText.setText((imagePosition + 1) + "/"
		// + (dataList.size() + Bimp.bmp.size()));
		pageText.setText((imagePosition + 1) + "/"
				+ (dataList.size() + Bimp.imgPath.size()));
	}

	private void initViewList() {
		mListViews = new ArrayList<View>();
		for (int i = 0; i < dataList.size() + Bimp.imgPath.size(); i++) {
			View view = LayoutInflater.from(ImageDetailsActivity.this).inflate(
					R.layout.page_zoom_image, null);
			mListViews.add(view);
		}
	}

	/**
	 * ViewPager的适配器
	 * 
	 * @author guolin
	 */
	class ViewPagerAdapter extends PagerAdapter {
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Log.e(TAG, "instantiateItem=" + position);

			// View view =
			// LayoutInflater.from(ImageDetailsActivity.this).inflate(
			// R.layout.page_zoom_image, null);
			View view = mListViews.get(position);
			ZoomImageView zoomImageView = (ZoomImageView) view
					.findViewById(R.id.zoom_image_view);
			ProgressBar progressBar = (ProgressBar) view
					.findViewById(R.id.progressBar);
			if (position < dataList.size()) {
				zoomImageView.setVisibility(View.GONE);
				loadImage(position, zoomImageView, progressBar);

			} else if (position < getCount()) {
				// Bitmap bitmap =( Bimp.bmp.get(position - dataList.size()));
//				Bitmap bitmap = getThumbnail(Bimp.imgPath.get(position
//						- dataList.size()));
//				if (bitmap == null) {
//					bitmap = BitmapFactory.decodeResource(getResources(),
//							R.drawable.no_photo);
//				}
//				zoomImageView.setImageBitmap(bitmap);
				String uri;
				if (Bimp.imgPath.get(position - dataList.size()).thumbnailPath != null) {
					// bm = BitmapFactory.decodeFile(Bimp.imgPath.get(pos
					// - dataList.size()).thumbnailPath);
					uri = "file://"
							+ Bimp.imgPath.get(position - dataList.size()).thumbnailPath;
					ImageLoader.getInstance().displayImage(uri, zoomImageView);
				} else {
					uri = "file://"
							+ Bimp.imgPath.get(position - dataList.size()).imagePath;
//					bm = BitmapFactory.decodeFile(Bimp.imgPath.get(pos
//							- dataList.size()).imgPath);
					ImageLoader.getInstance().displayImage(uri, zoomImageView);
				}
				progressBar.setVisibility(View.GONE);
			}
			container.addView(view);
			return view;
		}

		@Override
		public int getCount() {
			// Log.e(TAG, "getCount");
			// return dataList.size() + Bimp.bmp.size();
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			Log.e(TAG, "isViewFromObject");
			return view == (object);
			// return view.equals(object);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			Log.e(TAG, "destroyItem=" + position);
			container.removeView((View) object);
			// ((ViewPager) container).removeView(mListViews.get(position));
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		Log.e(TAG, "changed=" + arg0);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// Log.e(TAG, "onPageScrolled=" + arg0 + "," + arg1 + "," + arg2);
	}

	@Override
	public void onPageSelected(int currentPage) {
		// 每当页数发生改变时重新设定一遍当前的页数和总页数
		Log.e(TAG, "Selected=" + currentPage);
		if (currentPage < dataList.size()) {
			mButtonDelete.setVisibility(View.INVISIBLE);
		} else if (currentPage < adapter.getCount()) {
			mButtonDelete.setVisibility(View.VISIBLE);
		}
		// pageText.setText((currentPage + 1) + "/"
		// + (dataList.size() + Bimp.bmp.size()));
		pageText.setText((currentPage + 1) + "/"
				+ (dataList.size() + Bimp.imgPath.size()));
	}

	/**
	 * 开始加载图片，每张图片都会开启一个异步线程去下载。
	 */
	// public void loadImage(int position, final ZoomImageView imageView,
	// final ProgressBar progressBar) {
	//
	// String imagUrl = dataList.get(position).imagePath;
	// new ImageLoader().asynLoadImage(imagUrl,
	// new ImageLoader.AsynCallBack() {
	//
	// @Override
	// public void onFinish(Bitmap bitmap) {
	// progressBar.setVisibility(View.GONE);
	// if (bitmap != null) {
	// imageView.setVisibility(View.VISIBLE);
	// imageView.setImageBitmap(bitmap);
	//
	// }
	// }
	// });
	// // LoadImageTask task = new LoadImageTask(imageView, progressBar);
	// // // taskCollection.add(task);
	// // task.execute(position);
	//
	// }

	public void loadImage(int position, final ZoomImageView imageView,
			final ProgressBar progressBar) {

		String imagUrl = dataList.get(position).imagePath;
		ImageLoader.getInstance().displayImage(imagUrl, imageView, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String arg0, View arg1) {

					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {

					}

					@Override
					public void onLoadingComplete(String arg0, View arg1,
							Bitmap bitmap) {
						progressBar.setVisibility(View.GONE);
						if (bitmap != null) {
							imageView.setVisibility(View.VISIBLE);
							imageView.setImageBitmap(bitmap);
						}
					}

					@Override
					public void onLoadingCancelled(String arg0, View arg1) {

					}
				});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// ActivityStackManager.getStackManager().popActivity(this);
	}

	private Bitmap getThumbnail(ImageItem imageItem) {
		Bitmap bm = null;
		if (imageItem == null) {
			// return bm;
		} else if (imageItem.thumbnailPath != null) {
			bm = BitmapFactory.decodeFile(imageItem.thumbnailPath);
		} else if (imageItem.imagePath != null) {
			bm = BitmapFactory.decodeFile(imageItem.imagePath);
		}
		 bm = ThumbnailUtils.extractThumbnail(bm, 500, 500);
		return bm;
	}

}