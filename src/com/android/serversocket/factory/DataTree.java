package com.android.serversocket.factory;

import java.util.ArrayList;

import com.android.serversocket.R;
import com.android.serversocket.provider.DBOperator;
import com.android.serversocket.util.Const;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;



public class DataTree extends Activity {


	private String TAG = "TYPE";
	private Context Context;
	private static DBOperator mDbOperator;
	
	private GridView DragGridViewType;
	private ArrayList<String> items;
	private PupWinMegaAdapter mAdapter;
    private int adapterCount;
    private boolean EDIT_FLAG = true;  // true --- no edit    false  edit
    
	//GridView OnClick
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			Log.i(TAG, "OnClick postion: "+ position);
			mPosition = position;
			if(mPosition >= adapterCount) {
				CreateReNameDialog(null);
				return;
			} else {
				if (EDIT_FLAG) {
					//type exist and then show its content
					startPupWinContent(position);
				}else {
				   String oldname = items.get(position);
				   CreateReNameDialog(oldname);
				}
			}
		}
	};
	
	//
	OnItemDragListener mItemDragTypeListener =  new OnItemDragListener() {
        @Override
        public void onItemDragReady(int dragPosition) {
            Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE); 
            vib.vibrate(50); 
        }

        @Override
        public void onItemChanged(int dragPosition, int movPosition) {
        	Log.d("dragPosition",""+dragPosition);
        	Log.d("movPosition",""+movPosition);
//        	mDbOperator.changePupWinMageType(Integer.toString(dragPosition), Integer.toString(movPosition));
		}
    };

	private void startPupWinContent(int typeid) {
		Intent intent = new Intent();
		intent.setClass(DataTree.this, DataLeaf.class);
		intent.putExtra(Const.BUNDLE_TYPEID, typeid);
		intent.putExtra(Const.BUNDLE_STORE, STORE);
		if (HOTKEY != null)
			intent.putExtra(Const.BUND_HOTKEY, Const.KEY_VALUE_TRUE);
		else
			intent.putExtra(Const.BUND_HOTKEY, Const.KEY_VALUE_FALSE);
		startActivity(intent);
	}
    private TextView headTV;
	private Button head_btn;
	private void inithead() {
		 OnClickListener mSettingsListener = new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					EDIT_FLAG = ! EDIT_FLAG;
					reflashItems();
					Utils.showToast(Context, EDIT_FLAG == true ? getString(R.string.toast_setting_whcont_NG) : getString(R.string.toast_setting_whtree_OK));
				}
			};
		
		headTV = (TextView)findViewById(R.id.headTV);
        headTV.setText(getString(R.string.head_name_wartree));
            head_btn = (Button) findViewById(R.id.head_btn);
	    	head_btn.setVisibility(View.VISIBLE);
	    	head_btn.setOnClickListener(mSettingsListener);
	    	
	    	
	}
	private String HOTKEY = null;
	 private String STORE = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_warhos_data_tree);
			
		Context = getApplicationContext();
		mDbOperator = new DBOperator(Context);
        
		DragGridViewType = (GridView)findViewById(R.id.gridview);
		DragGridViewType.setOnItemClickListener(mOnItemClickListener);
		Intent intent = getIntent();
		HOTKEY = intent.getStringExtra(Const.BUND_HOTKEY);
		STORE = intent.getStringExtra(Const.BUNDLE_STORE);
		inithead();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter == null) {
			DragGridViewType.setAdapter(getTypeAdapter());
//			DragGridViewType.setItems(items);
		}
		
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	private PupWinMegaAdapter getTypeAdapter() {
		items = new ArrayList<String>();
		mAdapter = new PupWinMegaAdapter(Context, items);
		reflashItems();
		return mAdapter;
	}
	
	private int mPosition;

	public void reflashItems() {
		Log.i(TAG, "reflashItems start");
 		items.clear();
// 		adapterCount = mDbOperator.gettableCount(Const.TABLE_PupWinMage,"data2",Const.FLAG_DATAIN);
 		adapterCount = mDbOperator.gettableCount(Const.TABLE_tree);
    	for (int i = 0; i < adapterCount; i++) {
//        	String name = mDbOperator.getWinMageName(i);
    		String name = mDbOperator.getTreeName(i);
            items.add(name);
        }
    	if (!EDIT_FLAG) 
    	   items.add("+");
    	Log.i(TAG, "reflashItems end");
    	mAdapter.notifyDataSetChanged();
	}
	protected View mView;
//	Window window = dialog.getWindow(); 
//	WindowManager.LayoutParams lp = window.getAttributes();
//	lp.alpha = 0.9f;
//	window.setAttributes(lp);
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

						Log.i(TAG, "getReNameDialogPositiveButton start");
				    	
						if(mPosition >= adapterCount) {
							mDbOperator.insertTrees(et_dialog_confirmphoneguardpswd.getText().toString().trim());
//							mDbOperator.inWinMageTable(et_dialog_confirmphoneguardpswd.getText().toString().trim(), Const.FLAG_DATAIN);
						} else {
							mDbOperator.updateTreeName(Utils.toTreeID(mPosition), et_dialog_confirmphoneguardpswd.getText().toString().trim());
//							mDbOperator.upWinMageTableName(Utils.toWarTypeID(mPosition), et_dialog_confirmphoneguardpswd.getText().toString().trim());
						}
						
					    reflashItems();
				    	Log.i(TAG, "getReNameDialogPositiveButton end");
					
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
				convertView = View.inflate(Context,R.layout.mage_pupwin_button, null);
				viewHolder = new ViewHolder();
				viewHolder.image = (ImageView) convertView.findViewById(R.id.function_icon);
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
