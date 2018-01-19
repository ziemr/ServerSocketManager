package com.android.serversocket.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.android.serversocket.R;
import com.android.serversocket.calendar.CalendarAdapter;
import com.android.serversocket.provider.DBOperator;
import com.android.serversocket.util.Const;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;



@SuppressLint("ValidFragment")
public class CalendarFrgmt extends Fragment {
//	 private GestureDetector gestureDetector = null;
	 private int tabType = 0;
	 private CalendarAdapter calV = null;
	 private GridView gridView = null;
	 private TextView topText = null;
	 private static int jumpMonth = 0;      //每次滑动，增加或减去�?个月,默认�?0（即显示当前月）
	 private static int jumpYear = 0;      //滑动跨越�?年，则增加或者减去一�?,默认�?0(即当前年)
	 private int year_c = 0;
	 private int month_c = 0;
	 private int day_c = 0;
	 private String currentDate = "";
	 private Bundle sendBundle=null;//发�?�参�?
	 private Bundle bun=null;//接收参数
	 private String clickDate;
	 private String state="";
	 private Context mContext;
     private static CalendarFrgmt mCalendarFrgmt;
     private Button btn_prev_month,btn_next_month;
     private Button btn_calendar;
     private DBOperator mOperator;
     private TextView txt_calTextView;
	@SuppressLint("ValidFragment")
	public CalendarFrgmt() {
//		this.tabType = tabType;
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		currentDate = sdf.format(date); // 当期日期
		year_c = Integer.parseInt(currentDate.split("-")[0]);
		month_c = Integer.parseInt(currentDate.split("-")[1]);
		day_c = Integer.parseInt(currentDate.split("-")[2]);

	}
    /**
     * �?儞僗僞儞僗庢�?
     * @return
     */
    public static CalendarFrgmt getInstance(){
        if( mCalendarFrgmt == null ){
        	mCalendarFrgmt = new CalendarFrgmt();
        }
        return mCalendarFrgmt;
    }
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  mContext = getActivity();
	  mOperator = new DBOperator(mContext);
//	  addGridView();
	  calV = new CalendarAdapter(mContext,getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
	  setHasOptionsMenu(true);
	  
	  String scheduleYear = calV.getShowYear();
		String scheduleMonth = calV.getShowMonth();
		String scheduleDay = Integer.toString(day_c);
		if (scheduleMonth.length()<2) scheduleMonth="0"+scheduleMonth;
		if (scheduleDay.length()<2) scheduleDay="0"+scheduleDay;
		clickDate = scheduleYear + "/" + scheduleMonth + "/"+ scheduleDay;
//	  gestureDetector = new GestureDetector(this,new GestureListener(this));
	 }
	 
	 private TextView txt_calls,txt_records,txt_recordspec;
	 @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			View mView =inflater.inflate(R.layout.calendar_main , container, false); 
//			mListView = (ListView) mView.findViewById(R.id.callslist);
			topText = (TextView) mView.findViewById(R.id.tv_month);
			topText.setTextColor(Color.WHITE);
		    topText.setTypeface(Typeface.DEFAULT_BOLD);
		    addTextToTopTextView(topText);
		    
		    gridView =(GridView)mView.findViewById(R.id.gridview);
		    addGridView();
		    gridView.setAdapter(calV);
		    btn_calendar = (Button) mView.findViewById(R.id.calendar_btn);
		    btn_calendar.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (clickDate == null) {
						Toast.makeText(mContext, getString(R.string.calendar_date), Toast.LENGTH_LONG).show();
					} else {
						Intent intent = new Intent();
//						 intent.setClass(mContext,IndexFrgmtActivity.class);
						 intent.putExtra(Const.BUNDLE_TOADY,clickDate);
						 startActivity(intent);
					}
				}
			});
		    btn_prev_month = (Button) mView.findViewById(R.id.btn_prev_month);
		    btn_next_month = (Button) mView.findViewById(R.id.btn_next_month);
		    btn_prev_month.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					 jumpMonth--;    //上一个月
					   
					   calV = new CalendarAdapter(mContext,getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
					        gridView.setAdapter(calV);
//					        gvFlag++;
					        addTextToTopTextView(topText);					
				}
			});
		    btn_next_month.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					int gvFlag = 0;
					   jumpMonth++;    //下一个月
				   
					   calV = new CalendarAdapter(mContext,getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
					        gridView.setAdapter(calV);
					        addTextToTopTextView(topText);
//					        gvFlag++;
				}
			});
		txt_calls = (TextView) mView.findViewById(R.id.txt_calendar_calls);
		txt_records = (TextView) mView.findViewById(R.id.txt_calendar_records);
		txt_recordspec = (TextView) mView.findViewById(R.id.txt_calendar_recordspec);
		txt_calTextView = (TextView)mView.findViewById(R.id.txt_calendar_day);
		txt_calTextView.setText(clickDate);
//		txt_calls.setText(getString(R.string.calendar_calls) + mOperator.getRecordIndexCount(clickDate));
//		txt_records.setText(getString(R.string.calendar_records) + mOperator.getRecordCount(clickDate));
//		txt_recordspec.setText(mOperator.queryRecordincalendarnum(clickDate));
		return mView;
	}
//	 private class GestureListener implements OnGestureListener {
//
//			private Context mContext;
//			
//			public GestureListener(Context context) {
//				mContext = context ;
//			}
//		 @Override
//		 public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//					float velocityY) {
//		  int gvFlag = 0;        //每次添加gridview到viewflipper中时给的标记
//		  if (e1.getX() - e2.getX() > 120) {
//		            //像左滑动
////		   addGridView();  //添加�?个gridView
//		   jumpMonth++;    //下一个月
//		   
//		   calV = new CalendarAdapter(mContext,getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
//		        gridView.setAdapter(calV);
//		        addTextToTopTextView(topText);
//		        gvFlag++;
//
//		   return true;
//		  } else if (e1.getX() - e2.getX() < -120) {
//		            //向右滑动
////		   addGridView();  //添加�?个gridView
//		   jumpMonth--;    //上一个月
//		   
//		   calV = new CalendarAdapter(mContext,getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
//		        gridView.setAdapter(calV);
//		        gvFlag++;
//		        addTextToTopTextView(topText);
//
//		   return true;
//		  }
//		  return false;
//		 }
//		@Override
//		public boolean onDown(MotionEvent e) {
//			// TODO 自動生成されたメソッド・スタ�?
//			return false;
//		}
//		@Override
//		public void onLongPress(MotionEvent e) {
//			// TODO 自動生成されたメソッド・スタ�?
//			
//		}
//		@Override
//		public boolean onScroll(MotionEvent e1, MotionEvent e2,
//				float distanceX, float distanceY) {
//			// TODO 自動生成されたメソッド・スタ�?
//			return false;
//		}
//		@Override
//		public void onShowPress(MotionEvent e) {
//			// TODO 自動生成されたメソッド・スタ�?
//			
//		}
//		@Override
//		public boolean onSingleTapUp(MotionEvent e) {
//			// TODO 自動生成されたメソッド・スタ�?
//			return false;
//		}
//
//		 
//	 }


	/**
	  * 创建菜单
	  */
	 @Override
	 public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

	  menu.add(0, menu.FIRST, menu.FIRST, "今天");
	  super.onCreateOptionsMenu(menu, inflater);
	 }

	 @Override
	public void onResume() {
		super.onResume();
//		txt_calls.setText(getString(R.string.calendar_calls) + mOperator.getRecordIndexCount(clickDate));
//		txt_records.setText(getString(R.string.calendar_records) + mOperator.getRecordCount(clickDate));
//		txt_recordspec.setText(mOperator.queryRecordincalendarnum(clickDate));
	}
	/**
	  * 选择菜单
	  */
	 @Override
	 public  boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()){
	        case Menu.FIRST:
	         //跳转到今�?
	         int xMonth = jumpMonth;
	         int xYear = jumpYear;
	         int gvFlag =0;
	         jumpMonth = 0;
	         jumpYear = 0;
//	         addGridView();  //添加�?个gridView
	         year_c = Integer.parseInt(currentDate.split("-")[0]);
	         month_c = Integer.parseInt(currentDate.split("-")[1]);
	         day_c = Integer.parseInt(currentDate.split("-")[2]);
	         calV = new CalendarAdapter(mContext,getResources(),jumpMonth,jumpYear,year_c,month_c,day_c);
	        gridView.setAdapter(calV);
	        addTextToTopTextView(topText);
	        gvFlag++;

	         break;
	        }
	        return super.onOptionsItemSelected(item);
	 }

	 //添加头部的年�? 闰哪月等信息
	public void addTextToTopTextView(TextView view){
		clickPosition = -1;
	  StringBuffer textDate = new StringBuffer();
	  textDate.append(calV.getShowYear()).append("年").append(
	    calV.getShowMonth()).append("月").append("\t");
	  view.setText(textDate);
	 }
	 int clickPosition = -1;
	 //添加gridview
	 private void addGridView() {
//	  gridView =(GridView)findViewById(R.id.gridview);
	  
	  gridView.setOnItemClickListener(new OnItemClickListener() {
	            //gridView中的每一个item的点击事�?
	   
	   @Override
	   public void onItemClick(AdapterView<?> arg0, View arg1, int position,
	     long arg3) {
	      //点击任何�?个item，得到这个item的日�?(排除点击的是周日到周�?(点击不响�?))
	      int startPosition = calV.getStartPositon();
	      int endPosition = calV.getEndPosition();
				if (startPosition <= position + 7&& position <= endPosition - 7) {
					sendBundle = new Bundle();// out
					String scheduleYear = calV.getShowYear();
					String scheduleMonth = calV.getShowMonth();
					String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0]; // 这一天的阳历

					if (scheduleMonth.length()<2) scheduleMonth="0"+scheduleMonth;
					if (scheduleDay.length()<2) scheduleDay="0"+scheduleDay;
					clickDate = scheduleYear + "/" + scheduleMonth + "/"+ scheduleDay;
					
					if (clickPosition != -1)
					    arg0.getChildAt(clickPosition).setBackgroundColor(Color.WHITE);
					clickPosition = position;
				    arg0.getChildAt(position).setBackgroundColor(Color.RED);
				    txt_calTextView.setText(clickDate);
//				    txt_calls.setText(getString(R.string.calendar_calls) + mOperator.getRecordIndexCount(clickDate));
//					txt_records.setText(getString(R.string.calendar_records) + mOperator.getRecordCount(clickDate));
//					txt_recordspec.setText(mOperator.queryRecordincalendarnum(clickDate));
//				    act=com.android.phone.action.RECENT_CALLS cmp=com.android.irm/.contact
				}
	      }
	   
	  });
	 }

}
