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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;



public class PoPupWidowsDataTree extends Activity {


	private String TAG = "TYPE";
	private Context Context;
	private static DBOperator mDbOperator;
	
	private GridView DragGridViewType;
	private ArrayList<String> items;
	private PupWinMegaAdapter mAdapter;
    private int adapterCount;
	
	//GridView OnClick
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			Log.i(TAG, "OnClick postion: "+ position);
			mPosition = position;
			if(mPosition >= adapterCount) {
				CreateReNameDialog();
				return;
			} else {
//				Toast.makeText(getApplicationContext(), "超出限制", Toast.LENGTH_LONG).show();
			}
			
			//type exist and then show its content
			startPupWinContent(position);
		}
	};
	
	//GirdView OnLongClick
	private OnItemLongClickListener mLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
			Log.i(TAG, "OnLongClick postion: "+ position);
//			mPosition = Integer.toString(position);
			mPosition = position;
//			showReNameDialog(PoPupWidowsDataTree.this);
			return false;
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
		intent.setClass(PoPupWidowsDataTree.this, PoPupWidowsDataLeaf.class);
		intent.putExtra(Const.BUNDLE_TYPEID, typeid);
		startActivity(intent);
	}
    private TextView headTV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
    	setContentView(R.layout.mage_pupwin_type);
			
		Context = getApplicationContext();
		mDbOperator = new DBOperator(Context);
        headTV = (TextView)findViewById(R.id.headTV);
        headTV.setText(getString(R.string.head_name_tree));
        
		DragGridViewType = (GridView)findViewById(R.id.gridview);
		DragGridViewType.setOnItemClickListener(mOnItemClickListener);
//		DragGridViewType.setOnItemLongClickListener(mLongClickListener);
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
		reflashItems();
		mAdapter = new PupWinMegaAdapter(Context, items);
		return mAdapter;
	}
	
	private int mPosition;

	public void reflashItems() {
		Log.i(TAG, "reflashItems start");
 		items.clear();
 		   adapterCount = mDbOperator.gettableCount(Const.TABLE_PupWinMage);
 		
    	for (int i = 0; i < adapterCount; i++) {
        	String name = mDbOperator.getPupWinMageName(i);
            items.add(name);
        }
    	if (adapterCount < Const.DB_DATATREE_LIMIT)
    	   items.add("+");
    	Log.i(TAG, "reflashItems end");
	}
	protected View mView;
	private void CreateReNameDialog() {
		final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);	// 系统默认Dialog没有输入框
		View alertDialogView = View.inflate(this, R.layout.dialog_rename, null);
		final EditText et_dialog_confirmphoneguardpswd = (EditText) alertDialogView.findViewById(R.id.edit_rename);
		alertDialog.setTitle(getResources().getString(R.string.renameConfirmation_title));
		alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		alertDialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Log.i(TAG, "getReNameDialogPositiveButton start");
				    	
						if(mPosition >= adapterCount) {
							mDbOperator.insertPupWinMage(et_dialog_confirmphoneguardpswd.getText().toString().trim());
						} else {
							mDbOperator.updatePupWinMageName(Utils.toTypeID(mPosition), et_dialog_confirmphoneguardpswd.getText().toString().trim());
						}
						
					    reflashItems();
				    	mAdapter.notifyDataSetChanged();
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
