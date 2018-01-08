package com.android.serversocket.factory;

import java.util.ArrayList;
import java.util.Iterator;

import com.android.serversocket.R;
import com.android.serversocket.provider.DBOperator;
import com.android.serversocket.util.Const;
import com.android.serversocket.util.DragGridView;
import com.android.serversocket.util.DragGridView.OnItemDragListener;
import com.android.serversocket.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class DataLeaf extends Activity {

//	DragGridView DragGridViewList;
	private DragGridView DragGridViewContent;
	
//	private ArrayList<String> items;
	private ArrayList<String> contentItems;
	private Context Context;
	private DBOperator mDbOperator;
	private boolean EDIT_FLAG = true;
    private int mTypeId = -1;
    private String HOTKEY = null;
    private String STORE = null;
	private int pupWinIn_rownum = 0;
	private String person = null;
	private PupWinMegaAdapter mContentAdapter = null;
    private int adapterCount;
    private OnClickListener mSettingsListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			EDIT_FLAG = ! EDIT_FLAG;
			refreshItems();
			Utils.showToast(Context, EDIT_FLAG == true ? getString(R.string.toast_setting_whcont_NG) : getString(R.string.toast_setting_whtree_OK));
		}
		
	};
    
	private void CreateReNameDialog(final String oldname) {
		final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);	// 系统默认Dialog没有输入框
		View alertDialogView = View.inflate(this, R.layout.dialog_rename, null);
		final EditText et_dialog_confirmphoneguardpswd = (EditText) alertDialogView.findViewById(R.id.edit_rename);
		if (!(oldname == null || oldname.length() <= 0)) {
			et_dialog_confirmphoneguardpswd.setText(oldname);
		}
		alertDialog.setTitle(getResources().getString(R.string.renameConfirmation_title));
		alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		alertDialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

				    	
						
					    if (pupWinIn_rownum >= adapterCount) {
					    	//case: +  --> rename
					    	Boolean flag = mDbOperator.insertLeaf(mTypeId, et_dialog_confirmphoneguardpswd.getText().toString().trim());
					    } else {
					    	
					        String toastStr = mDbOperator.updateLeafName(mTypeId, oldname, et_dialog_confirmphoneguardpswd.getText().toString().trim()) == true ? getString(R.string.toast_ok) : getString(R.string.toast_ng);
					        Utils.showToast(Context, toastStr);
					    }
					    refreshItems();
					}
				});
		alertDialog.setNegativeButton(android.R.string.cancel, null);
		AlertDialog tempDialog = alertDialog.create();
		tempDialog.setView(alertDialogView, 0, 0, 0, 0);
		
		/** 3.自动弹出软键盘 **/
		tempDialog.setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(et_dialog_confirmphoneguardpswd, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		tempDialog.show();
	} 
	//GridView OnClick
	private OnItemClickListener mOnItemContentClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			pupWinIn_rownum = position;
			if(position >= adapterCount) {
				CreateReNameDialog(null);
			} /*else {
				//rename
				String tmpData = contentItems.get(position);
				
				if (EDIT_FLAG) {
					String recordid = null;
					String time = null;
					String Today = Utils.Today();
					////
				//	String oldRecordid = mDbOperator.getRecordIndex(Today);
					
					if (Const.NO_DATA.equals(oldRecordid)) {
						time = Utils.systemFrmtTime("yyMMddHHmmss");
						recordid = "wh" + time;
						///////
						mDbOperator.insertWHRecordIndex(recordid);  //zhang  dan suo yin

					} else {
						recordid = oldRecordid;
					}
					
				  Intent intent = new Intent();
			      intent.putExtra(Const.BUNDLE_RECORD_ID, recordid);
			      intent.putExtra(Const.BUNDLE_ID,tmpData);
					// add item use gesture(pupwindow)
				  intent.putExtra(Const.BUNDLE_Gesture, true);
				  intent.putExtra(Const.BUNDLE_STORE, STORE);
				  startActivity(intent);
				}else {
					String oldname = contentItems.get(position);
				   CreateReNameDialog(oldname);
				}
			}*/
		}
	};
	
	private OnItemDragListener mItemDragTypeListener =  new OnItemDragListener() {
        @Override
        public void onItemDragReady(int dragPosition) {
//            Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE); 
//            vib.vibrate(50); 
        }

        @Override
        public void onItemChanged(int dragPosition, int movPosition) {
        	Log.d("dragPosition",""+dragPosition);
        	Log.d("movPosition",""+movPosition);
//        	mDbOperator.changeWarhosContPosition(contentItems.get(dragPosition),contentItems.get(movPosition),dragPosition + 1, movPosition + 1);
        	Toast.makeText(Context, "changed",Toast.LENGTH_LONG).show();
		}
    };
	
	public void refreshItems() {
		adapterCount = mDbOperator.gettableCount(Const.TABLE_leaf,Const.COLOUM_TYPEID,Utils.toTreeID(mTypeId));
		contentItems.clear();
//		ArrayList<String> PupContName = mDbOperator.getPupContentNames(Utils.toWarTypeID(mTypeId));
		ArrayList<String> PupContName = mDbOperator.getleafNames(Utils.toTreeID(mTypeId));
		Iterator<String> ite = PupContName.iterator();
		while(ite.hasNext()) {
			contentItems.add(ite.next());
		}
		if (!EDIT_FLAG) 
		    contentItems.add("+");
		mContentAdapter.notifyDataSetChanged();
	}
	private TextView headTV;
	private Button head_btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context = getApplicationContext();
		mDbOperator = new DBOperator(Context);
		setContentView(R.layout.activity_warhos_data);
		//GridView
		DragGridViewContent = (DragGridView)findViewById(R.id.gridview);
		head_btn = (Button) findViewById(R.id.head_btn);
		head_btn.setVisibility(View.VISIBLE);
		head_btn.setOnClickListener(mSettingsListener);
		
		Intent intent = getIntent();
		person = intent.getStringExtra(Const.BUNDLE_Person);
		mTypeId = intent.getIntExtra(Const.BUNDLE_TYPEID,0);
		HOTKEY = intent.getStringExtra(Const.BUND_HOTKEY);
		
		STORE = intent.getStringExtra(Const.BUNDLE_STORE);
		
		String title = mDbOperator.getTreeName(mTypeId);
        headTV = (TextView)findViewById(R.id.headTV);
        headTV.setText(title);
        
        DragGridViewContent.setOnItemDragListener(mItemDragTypeListener);
    	DragGridViewContent.setOnItemClickListener(mOnItemContentClickListener);
        
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mContentAdapter == null) {
			DragGridViewContent.setAdapter(getContentAdapter());
			DragGridViewContent.setItems(contentItems);
		}
	}
	
	private PupWinMegaAdapter getContentAdapter() {
		contentItems = new ArrayList<String>();
		mContentAdapter = new PupWinMegaAdapter(Context, contentItems);
		refreshItems();
        return mContentAdapter;
	}

	//adapter
	public class PupWinMegaAdapter extends BaseAdapter {
	    class ViewHolder {
	        public TextView title;
	        public ImageView image;
	    }
	    
	    private Context Context;
	    ArrayList<String> items;
	    
	    public PupWinMegaAdapter(Context ct, ArrayList<String> items) {
	    	Context = ct;
	        this.items = items;
	    }
		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
	        if (convertView == null) {
			    convertView = View.inflate(Context, R.layout.mage_pupwin_button, null);
			    viewHolder = new ViewHolder();
			    viewHolder.image  = (ImageView) convertView.findViewById(R.id.function_icon);
	            viewHolder.title = (TextView) convertView.findViewById(R.id.function_text);
	            viewHolder.image.setVisibility(View.GONE);
			    convertView.setTag(viewHolder);
	        } else {
	            viewHolder = (ViewHolder) convertView.getTag();
	        }
	        viewHolder.title.setText(items.get(position));
	        return convertView;
		}
	}

}
