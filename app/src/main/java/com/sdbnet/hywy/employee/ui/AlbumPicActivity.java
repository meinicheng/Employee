package com.sdbnet.hywy.employee.ui;

import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdbnet.hywy.employee.R;
import com.sdbnet.hywy.employee.album.AlbumHelper;
import com.sdbnet.hywy.employee.album.BitmapCache;
import com.sdbnet.hywy.employee.album.AlbumHelper.ImageBucket;
import com.sdbnet.hywy.employee.ui.base.BaseActivity;

public class AlbumPicActivity extends BaseActivity {
	private final String TAG = getClass().getSimpleName();
	// ArrayList<Entity> dataList;//用来装载数据源的列表
	private List<ImageBucket> dataList;
	private GridView gridView;
	private ImageBucketAdapter adapter;// 自定义的适配器
	private AlbumHelper helper;
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static Bitmap bimap;

	private ImageView mBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ActivityStackManager.getStackManager().pushActivity(this);
		setContentView(R.layout.activity_image_bucket);

		initData();
		initUI();
		initListener();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {

		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		dataList = helper.getImagesBucketList(false);
		bimap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_addpic_unfocused);
	}

	/**
	 * 初始化view视图
	 */
	private void initUI() {
		mBack = (ImageView) findViewById(R.id.common_view_title_img);
		((TextView) findViewById(R.id.common_view_title_text))
				.setText(R.string.select_album);

		gridView = (GridView) findViewById(R.id.activity_album_igridview);
		adapter = new ImageBucketAdapter();
		gridView.setAdapter(adapter);

	}

	private void initListener() {
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/**
				 * 根据position参数，可以获得跟GridView的子View相绑定的实体类，然后根据它的isSelected状态，
				 * 来判断是否显示选中效果。 至于选中效果的规则，下面适配器的代码中会有说明
				 */
				// if(dataList.get(position).isSelected()){
				// dataList.get(position).setSelected(false);
				// }else{
				// dataList.get(position).setSelected(true);
				// }
				/**
				 * 通知适配器，绑定的数据发生了改变，应当刷新视图
				 */
				// adapter.notifyDataSetChanged();
				Intent intent = new Intent(AlbumPicActivity.this,
						ImageGridActivity.class);
				intent.putExtra(AlbumPicActivity.EXTRA_IMAGE_LIST,
						(Serializable) dataList.get(position).imageList);
				startActivity(intent);
				finish();
			}

		});

	}

	public class ImageBucketAdapter extends BaseAdapter {

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			Holder holder;
			if (arg1 == null) {
				holder = new Holder();
				arg1 = View.inflate(AlbumPicActivity.this,
						R.layout.item_image_bucket, null);
				holder.iv = (ImageView) arg1.findViewById(R.id.image);
				holder.selected = (ImageView) arg1
						.findViewById(R.id.isselected);
				holder.name = (TextView) arg1.findViewById(R.id.name);
				holder.count = (TextView) arg1.findViewById(R.id.count);
				arg1.setTag(holder);
			} else {
				holder = (Holder) arg1.getTag();
			}
			ImageBucket item = dataList.get(arg0);
			holder.count.setText("" + item.count);
			holder.name.setText(item.bucketName);
			holder.selected.setVisibility(View.GONE);
			if (item.imageList != null && item.imageList.size() > 0) {
				String thumbPath = item.imageList.get(0).thumbnailPath;
				String sourcePath = item.imageList.get(0).imagePath;
				holder.iv.setTag(sourcePath);
				BitmapCache.getBitmapCacheInstance().displayBmp(holder.iv,
						thumbPath, sourcePath);
				// ImageLoader.getInstance().asynLoadImage(holder.iv,
				// thumbPath);
			} else {
				holder.iv.setImageBitmap(null);
				Log.e(TAG, "no images in bucket " + item.bucketName);
			}
			return arg1;
		}

		class Holder {
			private ImageView iv;
			private ImageView selected;
			private TextView name;
			private TextView count;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (dataList != null) {
				count = dataList.size();
			}
			return count;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// ActivityStackManager.getStackManager().popActivity(this);

	}
}
