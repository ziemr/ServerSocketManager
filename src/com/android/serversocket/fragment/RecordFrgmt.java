package com.android.serversocket.fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import com.android.serversocket.R;
import com.android.serversocket.provider.DBOperator;
import com.android.serversocket.util.BodyLine;
import com.android.serversocket.util.Const;
import com.android.serversocket.util.GroupingListAdapter;
import com.android.serversocket.util.PupWidowsAdapter;
import com.android.serversocket.util.Utils;
import com.nineoldandroids.view.ViewHelper;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class RecordFrgmt extends Fragment implements OnClickListener {
	private static final String TAG = "RecordListActivity";
	
	private DrawerLayout mDrawerLayout;
	RecentCallsAdapter mAdapter;
	private QueryHandler mQueryHandler;
	private DBOperator mDbOperator;
	private Context mContext;

	private String Bundle_recordid = null;
	private String Bundle_telnumber = null;
	private String Bundle_ID;

	private boolean mScrollToTop;
	private static final int QUERY_TOKEN = 55;

	private ListView mListView;
	private static int PageIndex = 0;

	private String _TextArray[];
	private PupWidowsAdapter mPupAdapter;
	// gesture and pupwindow
	// for list
	private GestureDetector mDetector;
	// flag for add/view to limit gesture
	private Boolean Bundle_Gesture = true;
	private Boolean editEnable = true;
	
	
	private Boolean Bundle_readd = false;
	
	private TextView SUM_HEAD = null;
	private TextView NUM_HEAD = null;
	private Button headNewRecord;
	private LinearLayout mLayoutHide;

	private int TypeCount;
	private String NumStr = "0";
	private BodyLine mBodyLine = new BodyLine();
	String[] remarkarr = null;
	
	private Uri RecordinUri = null;
    /** The projection to use when querying the record in table */
    static final String[] RECORD_IN_PROJECTION = new String[] {
    	"_id",      //0
    	"recordid", //1
        "phone",    //2
        "num",      //3
        "data1",    //4
        "data2" ,   //5
        "data3" ,   //6
        "data4" ,   //7
        "data5",    //8
        "date",      //9
        "data6"
    };
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.headIcon:
//			lockout();
			break;
		case R.id.head_btn:
			OpenRightMenu(v);
			break;
		default:
			break;
		}
	}
	 private static RecordFrgmt mRecordFrgmt;
    public static RecordFrgmt getInstance(){
        if( mRecordFrgmt == null ){
        	mRecordFrgmt = new RecordFrgmt();
        }
        return mRecordFrgmt;
    }
	public RecordFrgmt() {

	}
	public void OpenRightMenu(View view) {
		mDrawerLayout.openDrawer(Gravity.RIGHT);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,
				Gravity.RIGHT);
	}
	private void initEvents() {
		mDrawerLayout.setDrawerListener(new DrawerListener() {
			@Override
			public void onDrawerStateChanged(int newState) {
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				View mContent = mDrawerLayout.getChildAt(0);
				View mMenu = drawerView;
				float scale = 1 - slideOffset;
				float rightScale = 0.8f + scale * 0.2f;
				if (drawerView.getTag().equals("LEFT")) {

					ViewHelper.setTranslationX(mContent,
							mMenu.getMeasuredWidth() * (1 - scale));
				} else {
					ViewHelper.setTranslationX(mContent,
							-mMenu.getMeasuredWidth() * slideOffset);
					ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
					ViewHelper.setPivotY(mContent,
							mContent.getMeasuredHeight() / 2);
					mContent.invalidate();
					ViewHelper.setScaleX(mContent, rightScale);
					ViewHelper.setScaleY(mContent, rightScale);
				}

			}

			@Override
			public void onDrawerOpened(View drawerView) {
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				mDrawerLayout.setDrawerLockMode(
						DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
			}
		});
	}
	
	private void initView() {
//		mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);
//		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
//				Gravity.RIGHT);
	}
	// for list
	OnTouchListener mListTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mDetector != null && Bundle_Gesture || editEnable)
				return mDetector.onTouchEvent(event);
			return false;
		}
	};


	private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			// Toast.makeText(mContext, "observer", Toast.LENGTH_SHORT).show();
			startQuery();
		}
	};


	public void initPupWidowsAdapter() {
		_TextArray = new String[Const.PUPWIN_CONTENT_ARRAY_MAX];
		ArrayList<String> PupContName = mDbOperator.getPupLeafNames(Utils.toTreeID(PageIndex));
		Iterator<String> ite = PupContName.iterator();
		int k = 0;
		while (ite.hasNext()) {
			if (k == Const.PUPWIN_CONTENT_ARRAY_MAX)
				break;
			_TextArray[k++] = ite.next();
		}
		while (k < Const.PUPWIN_CONTENT_ARRAY_MAX) {
			_TextArray[k++] = null;
		}
		mPupAdapter = new PupWidowsAdapter(mContext, _TextArray, false);
	}


	private void startQuery() {
		mAdapter.setLoading(true);

		// Cancel any pending queries
		mQueryHandler.cancelOperation(QUERY_TOKEN);
		RecordinUri = mDbOperator.getTableUri(Const.TABLE_RecordIN);

		String order = "date ASC";
		if (Bundle_Gesture)
			order = "date DESC";
		mQueryHandler.startQuery(QUERY_TOKEN, null, RecordinUri, RECORD_IN_PROJECTION,
				"data7 is not -1", null, order);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	super.onCreate(savedInstanceState);
	init();
	
    initView();
    initEvents();
    Bundle_Gesture = false;
    mLayoutHide.setVisibility(View.VISIBLE);
		headNewRecord.setVisibility(View.VISIBLE);
		headNewRecord.setOnClickListener(this);
}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mView = inflater.inflate(R.layout.recordin_layout, container,
				false);
		mLayoutHide = (LinearLayout)mView.findViewById(R.id.head_hide);
		headNewRecord = (Button)mView.findViewById(R.id.head_btn);

		mListView = (ListView) mView.findViewById(R.id.callslist);
		mListView.setOnCreateContextMenuListener(this);
		mListView.setAdapter(mAdapter);
	    mListView.setOnTouchListener(mListTouchListener);
		return mView;
	}
	private void init() {
		mAdapter = new RecentCallsAdapter();

		mQueryHandler = new QueryHandler(this);
		mDbOperator = new DBOperator(mContext);

		// --reCall

		int adapterCount = mDbOperator.gettableCount(Const.TABLE_remark);
		remarkarr = new String[adapterCount];
		for (int i = 0; i < adapterCount; i++) {
//			remarkarr[i] = mDbOperator.getRemarkName(i);
		}

	}
	 
	private boolean sortflag = false;

	@Override
	public void onResume() {
		super.onResume();
		if (!sortflag) {
			startQuery();
			mAdapter.mPreDrawListener = null; // Let it restart the thread after
												// next draw

			Uri AUTHORITY_URI = Uri.parse("content://" + Const.AUTHORITY + "/" + Const.TABLE_RecordIN);
			mContext.getContentResolver().registerContentObserver(AUTHORITY_URI, true, mContentObserver);
			// for test
			// TypeCount = mDbOperator.gettableCount(Const.TABLE_PupWinMage);

			TypeCount = 3;
		} else {
			ArrayList<String> PupContName = mDbOperator.getPupLeafNames(Utils.toTreeID(PageIndex));
			Iterator<String> ite = PupContName.iterator();
			int k = 0;
			while (ite.hasNext()) {
				if (k == Const.PUPWIN_CONTENT_ARRAY_MAX)
					break;
				_TextArray[k++] = ite.next();
			}
			while (k < Const.PUPWIN_CONTENT_ARRAY_MAX) {
				_TextArray[k++] = null;
			}
		}
		sortflag = false;
	}
    @Override
	public void onDestroy() {
        super.onDestroy();
        mAdapter.changeCursor(null);
    }
    
	//////////////////////////////////////////////////////////////////	   
    public static final class RecentCallsListItemViews {
    TextView line1View;
    TextView remark;
    TextView line2View;
    TextView labelView;
    TextView typeView;
    TextView dateView;
    TextView rownumView;
    TextView callView;
    ImageView groupIndicator;
    TextView groupSize;
}

static final int COLUMN_INDEX_ID = 0;
static final int COLUMN_INDEX_PHONENUMBER = 1;
static final int COLUMN_INDEX_NUM = 3;
static final int COLUMN_INDEX_DATA1 = 4;
static final int COLUMN_INDEX_DATA2 = 5;
static final int COLUMN_INDEX_DATA3 = 6;
static final int COLUMN_INDEX_SUM = 7;
static final int COLUMN_INDEX_PIECE = 8;
static final int COLUMN_INDEX_DATE = 9;
static final int COLUMN_INDEX_REMARK = 10;
	final class RecentCallsAdapter extends GroupingListAdapter
			implements ViewTreeObserver.OnPreDrawListener, View.OnClickListener {
		private boolean mLoading = true;
		ViewTreeObserver.OnPreDrawListener mPreDrawListener;
		private static final int REDRAW = 1;
		private static final int START_THREAD = 2;
		private boolean mFirst;

		/**
		 * Reusable char array buffers.
		 */

		public void onClick(View view) {
			// String[] data =
			// view.getTag().toString().split(Const.KEY_DELIMITER);
			// form recordid _id
			// AlertDialog dialog = (AlertDialog) CreateDialog(data[0],data[1]);
			String data = view.getTag().toString();
			// String hintPiece = mDbOperator.queryRecordINhintPiece(data);
			// AlertDialog dialog = (AlertDialog) PieceDialog(data,hintPiece);
			// dialog.show();
			// if(Const.NO_DATA == hintPiece)
			// dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
		}

		public boolean onPreDraw() {
			if (mFirst) {
				mHandler.sendEmptyMessageDelayed(START_THREAD, 1000);
				mFirst = false;
			}
			return true;
		}

		private Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case REDRAW:
					notifyDataSetChanged();
					break;
				case START_THREAD:
					// startRequestProcessing();
					break;
				}
			}
		};

		public RecentCallsAdapter() {
			super(getActivity());
			mPreDrawListener = null;
		}

		/**
		 * Requery on background thread when {@link Cursor} changes.
		 */
		@Override
		protected void onContentChanged() {
			// Start async requery
			startQuery();
		}

		void setLoading(boolean loading) {
			mLoading = loading;
		}

		@Override
		public boolean isEmpty() {
			if (mLoading) {
				// We don't want the empty state to show when loading.
				return false;
			} else {
				return super.isEmpty();
			}
		}

		@Override
		protected View newStandAloneView(Context context, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.recordin_list, parent, false);
			findAndCacheViews(view);
			return view;
		}

		@Override
		protected void bindStandAloneView(View view, Context context, Cursor cursor) {
			bindView(context, view, cursor);
		}

		private void findAndCacheViews(View view) {

			// Get the views to bind to
			RecentCallsListItemViews views = new RecentCallsListItemViews();
			views.line1View = (TextView) view.findViewById(R.id.line1);
			views.remark = (TextView) view.findViewById(R.id.remark);
			views.line2View = (TextView) view.findViewById(R.id.line2);
			views.labelView = (TextView) view.findViewById(R.id.label);
			views.typeView = (TextView) view.findViewById(R.id.type);
			views.dateView = (TextView) view.findViewById(R.id.date);
			views.rownumView = (TextView) view.findViewById(R.id.rownum);
			views.callView = (TextView) view.findViewById(R.id.call_icon);
			views.callView.setOnClickListener(this);
			view.setTag(views);
		}

		public void bindView(Context context, View view, Cursor c) {
			final RecentCallsListItemViews views = (RecentCallsListItemViews) view.getTag();
			//
			int rownumber = c.getInt(COLUMN_INDEX_ID); // rownum 0
			views.callView.setTag(rownumber);

			// No.
			int No = c.getCount() - c.getPosition();
			if (!Bundle_Gesture) {
				No = c.getPosition() + 1;
			}
			views.rownumView.setText(Integer.toString(No));

			// piece
			String mPiece = c.getString(COLUMN_INDEX_PIECE); // data5 8
			mPiece = mPiece.trim();
			views.callView.setText(mPiece);

			String data2 = mDbOperator.getPupLeafName(Const.TABLE_PupLeaf, c.getString(COLUMN_INDEX_DATA2)); // data1
																													// 4
			String data3 = mDbOperator.getPupLeafName(Const.TABLE_PupLeaf, c.getString(COLUMN_INDEX_DATA3));
			views.labelView.setText(data2 + "  " + data3);

			String remark = c.getString(COLUMN_INDEX_REMARK);
			views.remark.setText(remark);

			String data1 = mDbOperator.getPupLeafName(Const.TABLE_PupLeaf, c.getString(COLUMN_INDEX_DATA1)); // name
																													// 5
			if (!TextUtils.isEmpty(data1)) {
				views.line1View.setText(data1);

				int num = c.getInt(COLUMN_INDEX_NUM); // num 3
				views.line2View.setText(Integer.toString(num));

				// if (mPiece != null && !mPiece.equals("")) {
				// float tmpMoney = num * Float.parseFloat(mPiece);
				// TOTALMONEY +=tmpMoney;
				// }

				// "type" and "label" are currently unused for SIP addresses.
				CharSequence numberLabel = null;
				String product = c.getString(COLUMN_INDEX_SUM);
				views.dateView.setText("$ " + product);

				if (!TextUtils.isEmpty(numberLabel)) {
					views.labelView.setText(numberLabel);
					views.labelView.setVisibility(View.VISIBLE);

					// Zero out the numberView's left margin (see below)
					ViewGroup.MarginLayoutParams numberLP = (ViewGroup.MarginLayoutParams) views.typeView
							.getLayoutParams();
					numberLP.leftMargin = 0;
					views.typeView.setLayoutParams(numberLP);
				} else {

					ViewGroup.MarginLayoutParams labelLP = (ViewGroup.MarginLayoutParams) views.labelView
							.getLayoutParams();
					ViewGroup.MarginLayoutParams numberLP = (ViewGroup.MarginLayoutParams) views.typeView
							.getLayoutParams();
					// Equivalent to setting android:layout_marginLeft in XML
					numberLP.leftMargin = -labelLP.rightMargin;
					views.typeView.setLayoutParams(numberLP);
				}
			} else {
				views.line1View.setText("any");
			}

			// Listen for the first draw
			if (mPreDrawListener == null) {
				mFirst = true;
				mPreDrawListener = this;
				view.getViewTreeObserver().addOnPreDrawListener(this);
			}
		}

	}
	///////////////////////////////////////////////////////////////

	private static final class QueryHandler extends AsyncQueryHandler {
		private final WeakReference<RecordFrgmt> mActivity;

		/**
		 * Simple handler that wraps background calls to catch
		 * {@link SQLiteException}, such as when the disk is full.
		 */
		protected class CatchingWorkerHandler extends AsyncQueryHandler.WorkerHandler {
			public CatchingWorkerHandler(Looper looper) {
				super(looper);
			}

			@Override
			public void handleMessage(Message msg) {
				try {
					// Perform same query while catching any exceptions
					super.handleMessage(msg);
				} catch (SQLiteDiskIOException e) {
					Log.w(TAG, "Exception on background worker thread", e);
				} catch (SQLiteFullException e) {
					Log.w(TAG, "Exception on background worker thread", e);
				} catch (SQLiteDatabaseCorruptException e) {
					Log.w(TAG, "Exception on background worker thread", e);
				}
			}
		}

		@Override
		protected Handler createHandler(Looper looper) {
			// Provide our special handler that catches exceptions
			return new CatchingWorkerHandler(looper);
		}

		public QueryHandler(Fragment context) {
			super(context.getActivity().getContentResolver());
			mActivity = new WeakReference<RecordFrgmt>((RecordFrgmt) context);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			final RecordFrgmt activity = mActivity.get();
			if (activity != null && !activity.isDetached()) {
				final RecordFrgmt.RecentCallsAdapter callsAdapter = activity.mAdapter;
				callsAdapter.setLoading(false);
				callsAdapter.changeCursor(cursor);
				if (activity.mScrollToTop) {
					activity.mScrollToTop = false;
				}
			} else {
				cursor.close();
			}
		}
	}

}
