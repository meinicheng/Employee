package com.sdbnet.hywy.employee.ui.widget;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sdbnet.hywy.employee.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

/**
 * 日期时间选择控件 使用方法： private EditText inputDate;//需要设置的日期时间文本编辑框 private String
 * initDateTime="2012年9月3日 14:44",//初始日期时间值 在点击事件中使用：
 * inputDate.setOnClickListener(new OnClickListener() {
 * 
 * @Override public void onClick(View v) { DateTimePickDialogUtil
 *           dateTimePicKDialog=new
 *           DateTimePickDialogUtil(SinvestigateActivity.this,initDateTime);
 *           dateTimePicKDialog.dateTimePicKDialog(inputDate);
 * 
 *           } });
 * 
 * @author
 */
public class DialogDateTimePick implements OnDateChangedListener,
		OnTimeChangedListener {
	private DatePicker datePicker;
	private TimePicker timePicker;
	private AlertDialog ad;
	private String dateTime;
	private String initDateTime;
	private Activity activity;
	private final String SIGN_YEAR = "-";// 年
	private final String SIGN_MONTH = "-";// 月
	private final String SIGN_DAY = "-";// 日
	private final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

	/**
	 * 日期时间弹出选择框构造函数
	 * 
	 * @param activity
	 *            ：调用的父activity
	 * @param initDateTime
	 *            初始日期时间值，作为弹出窗口的标题和日期时间初始值
	 */
	public DialogDateTimePick(Activity activity, String initDateTime) {
		this.activity = activity;
		this.initDateTime = initDateTime;

	}

	public DialogDateTimePick(Activity activity) {
		this.activity = activity;

	}

	public void init(DatePicker datePicker) {
		init(datePicker, null);
	}

	public void init(DatePicker datePicker, TimePicker timePicker) {
		Calendar calendar = Calendar.getInstance();
		if (!(null == initDateTime || "".equals(initDateTime))) {
			calendar = this.getCalendarByInintData(initDateTime);
		} else {
			initDateTime = calendar.get(Calendar.YEAR) + SIGN_YEAR
					+ calendar.get(Calendar.MONTH) + SIGN_MONTH
					+ calendar.get(Calendar.DAY_OF_MONTH)
					+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
					+ calendar.get(Calendar.MINUTE);
		}

		datePicker.init(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH), this);
		if (timePicker != null) {
			timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		}
	}

	/**
	 * 弹出日期时间选择框方法
	 * 
	 * @param inputDate
	 *            :为需要设置的日期时间文本编辑框
	 * @return
	 */
	public AlertDialog dateTimePicKDialog(final TextView inputDate) {
		LinearLayout dateTimeLayout = (LinearLayout) activity
				.getLayoutInflater().inflate(R.layout.dialog_datetime, null);
		datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
		init(datePicker, null);
		timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
		init(datePicker, timePicker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(this);

		ad = new AlertDialog.Builder(activity)
				.setTitle(initDateTime)
				.setView(dateTimeLayout)
				.setPositiveButton(activity.getString(R.string.set),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								inputDate.setText(dateTime);
							}
						})
				.setNegativeButton(activity.getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// inputDate.setText(initDateTime);
							}
						}).show();

		onDateChanged(null, 0, 0, 0);
		return ad;
	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// 获得日历实例
		Calendar calendar = Calendar.getInstance();

		calendar.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
				timePicker.getCurrentMinute());
		// calendar.set(datePicker.getYear(), datePicker.getMonth(),
		// datePicker.getDayOfMonth());
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		dateTime = sdf.format(calendar.getTime());
		ad.setTitle(dateTime);
	}

	/**
	 * 实现将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒,并赋值给calendar
	 * 
	 * @param initDateTime
	 *            初始日期时间值 字符串型
	 * @return Calendar
	 */
	// private Calendar getCalendarByInintData(String initDateTime) {
	// Calendar calendar = Calendar.getInstance();
	//
	// // 将初始日期时间2012年07月02日 16:45 拆分成年 月 日 时 分 秒
	// String date = spliteString(initDateTime, "日", "index", "front"); // 日期
	// String time = spliteString(initDateTime, "日", "index", "back"); // 时间
	//
	// String yearStr = spliteString(date, "年", "index", "front"); // 年份
	// String monthAndDay = spliteString(date, "年", "index", "back"); // 月日
	//
	// String monthStr = spliteString(monthAndDay, "月", "index", "front"); // 月
	// String dayStr = spliteString(monthAndDay, "月", "index", "back"); // 日
	//
	// String hourStr = spliteString(time, ":", "index", "front"); // 时
	// String minuteStr = spliteString(time, ":", "index", "back"); // 分
	//
	// int currentYear = Integer.valueOf(yearStr.trim()).intValue();
	// int currentMonth = Integer.valueOf(monthStr.trim()).intValue() - 1;
	// int currentDay = Integer.valueOf(dayStr.trim()).intValue();
	// int currentHour = Integer.valueOf(hourStr.trim()).intValue();
	// int currentMinute = Integer.valueOf(minuteStr.trim()).intValue();
	//
	// calendar.set(currentYear, currentMonth, currentDay, currentHour,
	// currentMinute);
	// return calendar;
	// }
	private Calendar getCalendarByInintData(String initDateTime) {
		Calendar calendar = Calendar.getInstance();

		// 将初始日期时间2012-07-02 12:32拆分

		String[] strs = initDateTime.split("[-:\\s]");

		int currentYear = Integer.valueOf(strs[0].trim()).intValue();
		int currentMonth = Integer.valueOf(strs[1].trim()).intValue() - 1;
		int currentDay = Integer.valueOf(strs[2].trim()).intValue();
		int currentHour = Integer.valueOf(strs[3].trim()).intValue();
		int currentMinute = Integer.valueOf(strs[4].trim()).intValue();
		calendar.set(currentYear, currentMonth, currentDay, currentHour,
				currentMinute);
		return calendar;
	}

	/**
	 * 截取子串
	 * 
	 * @param srcStr
	 *            源串
	 * @param pattern
	 *            匹配模式
	 * @param indexOrLast
	 * @param frontOrBack
	 * @return
	 */
	public static String spliteString(String srcStr, String pattern,
			String indexOrLast, String frontOrBack) {
		String result = "";
		int loc = -1;
		if (indexOrLast.equalsIgnoreCase("index")) {
			loc = srcStr.indexOf(pattern); // 取得字符串第一次出现的位置
		} else {
			loc = srcStr.lastIndexOf(pattern); // 最后一个匹配串的位置
		}
		if (frontOrBack.equalsIgnoreCase("front")) {
			if (loc != -1)
				result = srcStr.substring(0, loc); // 截取子串
		} else {
			if (loc != -1)
				result = srcStr.substring(loc + 1, srcStr.length()); // 截取子串
		}
		return result;
	}

}
