package com.sdbnet.hywy.employee.ui;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.album.AlbumHelper;
import com.sdbnet.hywy.employee.album.AlbumHelper.ImageItem;
import com.sdbnet.hywy.employee.album.Bimp;
import com.sdbnet.hywy.employee.album.BitmapCache;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;

public class ImageGridActivity extends BaseActivity {
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	private static final String TAG = "ImageGridActivity";
	private List<ImageItem> mImgItemList;
	private GridView mGridView;
	private ImageGridAdapter mGridAdapter;
	private AlbumHelper helper;
	private Button mBtOk;
	private ImageView mBack;
	private int selectTotal = 0;
	// private List<String> mSelectImgPath = new ArrayList<String>();
	private List<ImageItem> mSelectImgPath = new ArrayList<AlbumHelper.ImageItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);

		setContentView(R.layout.activity_image_grid);

		initBaseData();
		initUI();

	}

	private void initBaseData() {
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		mImgItemList = (List<ImageItem>) getIntent().getSerializableExtra(
				EXTRA_IMAGE_LIST);
		// for (int i = 0; i < Bimp.imgPath.size(); i++) {
		// Log.i(TAG, Bimp.imgPath.get(i).toString());
		// }
		for (ImageItem item : mImgItemList) {
			Log.d(TAG, item.toString() + ";" + Bimp.imgPath.contains(item));
			if (Bimp.imgPath.contains(item)) {
				item.isSelected = true;
				mSelectImgPath.add(item);
				selectTotal++;
			}
		}

	}

	private void initUI() {
		mBack = (ImageView) findViewById(R.id.common_view_title_img);
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(R.string.select_pic);

		mGridView = (GridView) findViewById(R.id.activity_image_grid_gridview);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mGridAdapter = new ImageGridAdapter();
		mGridView.setAdapter(mGridAdapter);

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ImageItem item = mImgItemList.get(position);
				// String path = item.imagePath;
				if (item.isSelected) {
					item.isSelected = !item.isSelected;
					// mSelectImgPath.remove(path);
					mSelectImgPath.remove(item);
					Bimp.imgPath.remove(item);
					selectTotal--;
				} else if ((Bimp.loadCount + Bimp.imgPath.size() + selectTotal) < Bimp.IMAGE_COUNT) {
					item.isSelected = !item.isSelected;
					// mSelectImgPath.add(path);
					mSelectImgPath.add(item);
					selectTotal++;
				} else {
					showShortToast(String.format(
							getString(R.string.most_choose_num_images),
							Bimp.IMAGE_COUNT));
				}
				if (selectTotal != 0) {
					mBtOk.setText(getString(R.string.ok) + "(" + selectTotal
							+ ")");
				} else {
					mBtOk.setText(getString(R.string.ok));

				}

				mGridAdapter.notifyDataSetChanged();
			}

		});

		mBtOk = (Button) findViewById(R.id.activity_image_grid_bt);
		mBtOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0; i < mSelectImgPath.size(); i++) {
					if (Bimp.imgPath.size() < Bimp.IMAGE_COUNT) {
						if (!Bimp.imgPath.contains(mSelectImgPath.get(i)))
							Bimp.imgPath.add(mSelectImgPath.get(i));
						Log.i(TAG, "path2:" + mSelectImgPath.get(i));
					}
				}
				finish();
			}

		});

		if (selectTotal > 0)
			mBtOk.setText(getString(R.string.ok) + "(" + selectTotal + ")");
	}

	public class ImageGridAdapter extends BaseAdapter {
		final String TAG = getClass().getSimpleName();

		class Holder {
			private ImageView iv;
			private ImageView selected;
			private TextView text;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final Holder holder;
			final ImageItem item = mImgItemList.get(position);
			if (convertView == null) {
				holder = new Holder();
				convertView = View.inflate(ImageGridActivity.this,
						R.layout.item_image_grid, null);
				holder.iv = (ImageView) convertView.findViewById(R.id.image);
				holder.selected = (ImageView) convertView
						.findViewById(R.id.isselected);
				holder.text = (TextView) convertView
						.findViewById(R.id.item_image_grid_text);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}

			holder.iv.setTag(item.imagePath);
			BitmapCache.getBitmapCacheInstance().displayBmp(holder.iv,
					item.thumbnailPath, item.imagePath);
			if (item.isSelected) {
				holder.selected.setVisibility(View.VISIBLE);
				holder.text.setBackgroundResource(R.drawable.bgd_relatly_line);
			} else {

				holder.selected.setVisibility(View.INVISIBLE);
				holder.text.setBackgroundColor(0x00000000);

			}
			return convertView;
		}

		@Override
		public int getCount() {
			if (mImgItemList != null) {
				return mImgItemList.size();
			} else {
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// ActivityStackManager.getStackManager().popActivity(this);
	}
	// private TextCallback textcallback = null;
	//
	// public static interface TextCallback {
	// public void onListen(int count);
	// }
	//
	// public void setTextCallback(TextCallback listener) {
	// textcallback = listener;
	// }
}
