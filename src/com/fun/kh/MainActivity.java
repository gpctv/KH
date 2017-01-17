package com.fun.kh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 















import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
/*�O�o���STRING CODE�X*/

public class MainActivity extends ActionBarActivity {
	
	private static ListView listView1;
	static ArrayList<HashMap<String,String>> list2 = new ArrayList<HashMap<String,String>>();
	private static SimpleAdapter adapter;
	private static  ProgressBar pro;
	private static SwipeRefreshLayout laySwipe;
	private static KHBean saveJsonBean[];
	private static SparseBooleanArray selectItem;
	private static SharedPreferences settings ;
	private static SharedPreferences.Editor editor;
	private static HashMap<Integer, String> addFavoirte=new HashMap<Integer, String>(); ;
	private static EditText searchText;
	private static boolean isSearch=false;
	private static final int Version=Build.VERSION.SDK_INT;
	//private static ImageView image;
	public boolean onKeyDown(int keyCode,KeyEvent  event){
		if (keyCode == KeyEvent.KEYCODE_BACK) {  
			if(isSearch==true){
				
			isSearch=false;
			System.out.println("isSearch"+isSearch);
			list2.clear();
			selectItem.clear();
			addFavoirte.clear();
			pro.setVisibility(View.VISIBLE);
			 HttpAsyncTask HAT=new HttpAsyncTask(this);//�إ߫D�P�B����
			 HAT.execute();
			 adapter.notifyDataSetChanged();
			return true; 
			}else{
				System.out.println("isSearch"+isSearch);
				android.os.Process.killProcess(android.os.Process.myPid());
				
				return true;
			}
        }
		return true;
	    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//�j��X�{overflow menu 
		try {
			if(Version>11){ //�j�� android 3.0���j��X�{
			 ViewConfiguration mconfig = ViewConfiguration.get(this);
			 Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			 System.out.println("menuKey:"+menuKeyField);
			 if(menuKeyField != null) {
				
			 menuKeyField.setAccessible(true);
			 menuKeyField.setBoolean(mconfig, false);
			 }else{
				 
			 }
			
			 }
			 } catch (Exception ex) {
				 ex.printStackTrace();
			 }
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		 if(Version<=11){//android 3.0�H�U
			 getMenuInflater().inflate(R.menu.main2, menu);
			 return true;
		 }else{
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
		 }
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		switch(item.getItemId()){
		
		case R.id.favorite_settings:
			Intent intent=new Intent();
			intent.setClass(MainActivity.this,Favorite.class);
			startActivity(intent);
			return true;
		case R.id.Favoite_add:
			//�s�W�x�s�ܧڪ��̷R
			settings =getSharedPreferences("SaveFavorite", 0);
			editor=settings.edit();
			//�P�_�O�_�����
			if(addFavoirte.isEmpty()){
				Toast.makeText(MainActivity.this,"��������󶵥�",Toast.LENGTH_SHORT).show();
				
			}else{
			//�N�����������SharePreferences
			for(Object o:addFavoirte.keySet()){
				int temp=(Integer) o;
				System.out.println("temp"+temp);
				//�P�_SharePreferences(�ڪ��̷R)�O�_�w�g��������
				if(settings.contains(String.valueOf(temp))){
					System.out.println("�w�s�b");
					Toast.makeText(MainActivity.this,saveJsonBean[temp].getName()+"�w�s�b",Toast.LENGTH_SHORT).show();
				}else{
					//�g�J
					System.out.println(temp);
					editor.putString(saveJsonBean[temp].positionID,getValue(temp) );
					
				}
				editor.commit();
				
			}
			Toast.makeText(MainActivity.this,"�w�W�[���ئܧڪ��̷R",Toast.LENGTH_SHORT).show();
			
			}
			selectItem.clear();
			addFavoirte.clear();
			adapter.notifyDataSetChanged();
			return true;
		case R.id.search_tool:
            
			final AlertDialog dialog2;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater =this.getLayoutInflater();
			View priview=inflater.inflate(R.layout.search_layout,null);
			searchText=(EditText) priview.findViewById(R.id.autoCompleteTextView1);
			builder.setView(priview);	
			builder.setPositiveButton("�d��", null);
			builder.setNegativeButton("Cancel", null);
			dialog2=builder.create();
			dialog2.show();
			Button search_click=dialog2.getButton(Dialog.BUTTON_POSITIVE);
			
			//�d�ߨƥ�
			OnClickListener Search_ClickListen=new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO �۰ʲ��ͪ���k Stub
					
					String searchString=searchText.getText().toString();
					 	System.out.println("searchString:"+searchString);
					if(searchString.isEmpty()){
						Toast.makeText(MainActivity.this,"����J�d��",Toast.LENGTH_SHORT).show();
						
					}else{
						isSearch=true;
						list2.clear();//�M��LIST�e��
						addFavoirte.clear();//�M����ܥ[�J�ڪ��̷R
						selectItem.clear();//�M����ܶ���
						try{
					for(int i=0;i<saveJsonBean.length;i++){
						 
						boolean a=saveJsonBean[i].getName().indexOf(searchString)>-1||saveJsonBean[i].getAdd().indexOf(searchString)>-1;
						
						if(a){
							HashMap<String,String> list=new HashMap<String,String>();
                           list.put("Name", saveJsonBean[i].getName());
                           list.put("Address", saveJsonBean[i].getAdd());
                           list.put("positionID",saveJsonBean[i].positionID);
                           list2.add(list);
						}
						
					}
						}catch(Exception e){
							Toast.makeText(MainActivity.this,"��Ʃ|�����J�����L�k�j�M",Toast.LENGTH_SHORT).show();
							
						}
					
					listView1.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					}
					dialog2.dismiss();
					 
				}
				
				
			};
			search_click.setOnClickListener(Search_ClickListen);
			return true;
		case R.id.action_settings:
			return true;
		default:
		return super.onOptionsItemSelected(item);
	     
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		
		public PlaceholderFragment() {
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			try{
			isConnected(getActivity());
			}catch(Exception e){
				Toast.makeText(getActivity(),"�L����",Toast.LENGTH_SHORT).show();;
			}
			
			  System.out.println("version:"+Build.VERSION.SDK_INT);
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			/* GOOGLE �s�i��T*/
			 AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
		        AdRequest adRequest = new AdRequest.Builder().build();
		        mAdView.loadAd(adRequest);
		        /* GOOGLE �s�i��T*/ 
			listView1 =(ListView) rootView.findViewById(R.id.listView1); //listView����
			pro=(ProgressBar) rootView.findViewById(R.id.progressBar1);
		    selectItem=new SparseBooleanArray();
		    
			//�]�w�̭�Item�C��
			adapter=new SimpleAdapter(getActivity(), list2,R.layout.list_viewlayout ,new String[]{"Name","Address"}, new int[] { R.id.listViewT1, R.id.listViewT2 }){
				//simpleAdapater �]�wLIST�n��ܪ����ؤμ˦�	
				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					// TODO �۰ʲ��ͪ���k Stub
					View conview=super.getView(position, convertView, parent);
					conview
	                .setBackgroundColor(selectItem.get(position) ? 0xffffc0cb
	                        : 0xfffacd);
					
					return conview;
				}
				
				
			};
			 laySwipe = (SwipeRefreshLayout) rootView.findViewById(R.id.laySwipe);
			 System.out.println(list2.size());
			 
			//Toast.makeText(getActivity(),String.valueOf(list2.size()), Toast.LENGTH_SHORT).show();
			 HttpAsyncTask HAT=new HttpAsyncTask(getActivity());//�إ߫D�P�B����
				
			 HAT.execute();//����D�P�B����
			
			//����@�U�ԴN��s
				 OnScrollListener onListScroll = new OnScrollListener() {

				     
					 @Override
				        public void onScroll(AbsListView view, int firstVisibleItem,
				                int visibleItemCount, int totalItemCount) {
				            if (firstVisibleItem == 0) {
				                laySwipe.setEnabled(true);
				            }else{
				                laySwipe.setEnabled(false);
				            }
				        }

						@Override
						public void onScrollStateChanged(AbsListView view,
								int scrollState) {
							// TODO �۰ʲ��ͪ���k Stub
							
						}

						
				    };
				    listView1.setOnScrollListener(onListScroll);
			//�]�w�U��REFRESH
			OnRefreshListener onSwipeToRefresh = new OnRefreshListener() {
		        @Override
		        public void onRefresh() {
		            laySwipe.setRefreshing(false);
		            pro.setVisibility(View.VISIBLE);
		            selectItem.clear();
		            adapter.notifyDataSetChanged();
		            new Handler().postDelayed(new Runnable() {

		                @Override
		                public void run() {
		                    //laySwipe.setRefreshing(false);
		                    HttpAsyncTask HAT=new HttpAsyncTask(getActivity());//�إ߫D�P�B����
		                    HAT.execute();//����D�P�B����
		                    pro.setVisibility(View.INVISIBLE);
		                    Toast.makeText(getActivity(), "�w��s", Toast.LENGTH_SHORT).show();
		                    Toast.makeText(getActivity(), "��ܶ��ؤw�M��", Toast.LENGTH_SHORT).show();
				               
		                }
		            }, 5000);
		        }
		    };
		   
		    laySwipe.setOnRefreshListener(onSwipeToRefresh);
		    AdapterView.OnItemLongClickListener longClick=new AdapterView.OnItemLongClickListener() {
		    	
			     
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO �۰ʲ��ͪ���k Stub
					 AlertDialog dialog = null;
					LayoutInflater inflater =getActivity().getLayoutInflater();
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					View priview=inflater.inflate(R.layout.contain_layout,null);
				   
				     ImageView image=(ImageView) priview.findViewById(R.id.imageView1);
				     ProgressBar progressBar2=(ProgressBar) priview.findViewById(R.id.progressBar2);
				     ListView listView = (ListView) arg0;
				     String data1=listView.getItemAtPosition(arg2).toString();
				     
				     System.out.println("longItemclick:"+data1);
				     String data2[]=data1.split(",");
				     String pos[]=data2[0].split("=");
				     int SelectPosition;
				   //�����C�� android4.0�ɱj
				     if(Version<=20){
				    	 if(isSearch){
				    		 data2=data1.split(",");
					    	 pos=data2[0].split("=");
					    	 SelectPosition= Integer.valueOf(pos[1]);
				    	 }else{
				    	 data2=data1.split(",");
				    	 pos=data2[1].split("=");
				    	 SelectPosition= Integer.valueOf(pos[1]);
				    	 }
				     }else{
				    	 SelectPosition=Integer.valueOf(pos[1]);
				     }
				     
				     TextView title=(TextView) priview.findViewById(R.id.title);
				     TextView address=(TextView) priview.findViewById(R.id.address);
				     TextView text=(TextView) priview.findViewById(R.id.context);
				     TextView openTimeText=(TextView) priview.findViewById(R.id.openTimeText);
				     builder.setView(priview);	
						builder.setPositiveButton("OK", null);
						//Button OK=dialog.getButton(Dialog.BUTTON_POSITIVE);
						System.out.println(SelectPosition);
						String http="http://www.google.com.tw/maps/place/"+saveJsonBean[SelectPosition].getAdd();
						address.setText(Html.fromHtml("<a href=\""+http+"\">"+saveJsonBean[SelectPosition].getAdd()+"</a>"));
						address.setMovementMethod(LinkMovementMethod.getInstance());
						 
						title.setText(saveJsonBean[SelectPosition].getName());
						text.setText(saveJsonBean[SelectPosition].getDesc());
						openTimeText.setText(saveJsonBean[SelectPosition].getOpenTime()+"  tel:"+saveJsonBean[SelectPosition].getTel());
						new GetImageURL(getActivity(),image,progressBar2).execute(saveJsonBean[SelectPosition].getPicture()); //�]�w�Ϥ�
						dialog=builder.create();
						dialog.show();
						
						return false;
				}
		    };
		    OnItemClickListener listViewItemListenClick=new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View conview,
						int position, long arg3) {
					// TODO �۰ʲ��ͪ���k Stub
					boolean po=!selectItem.get(position);
					
					System.out.println(po+" "+position);
					ListView listView = (ListView) arg0;
					//���θ�Ƨ�ID
					int pos2;
					String data2=(String) listView.getItemAtPosition(position).toString();//��X�@������
					String dataArry[]=data2.split(",");
					String pos[]=dataArry[0].split("=");
					if(Version<=20){
						if(isSearch){
							dataArry=data2.split(",");
							 pos=dataArry[0].split("=");
							 pos2=Integer.valueOf(pos[1]);
						 }
						else{
							dataArry=data2.split(",");
							 pos=dataArry[1].split("=");
							 pos2=Integer.valueOf(pos[1]);
						}
					}else{
					 pos2=Integer.valueOf(pos[1]);//��POSITION ID(���o�ϥ�position�]���C�Ʒ|�ܰ�)
					}
					System.out.println("pos2:"+pos2);
					if(po){
						selectItem.put(position,po );
						addFavoirte.put(Integer.valueOf(saveJsonBean[pos2].positionID), saveJsonBean[pos2].ID);
					
					}else{
						selectItem.delete(position);
						addFavoirte.remove(Integer.valueOf(saveJsonBean[pos2].positionID));
					}
					Toast.makeText(getActivity(), "��F"+String.valueOf(selectItem.size())+"�Ӷ���", Toast.LENGTH_SHORT).show();
		              
					
					adapter.notifyDataSetChanged();
				}
		    	
		    };
		    
            listView1.setOnItemClickListener(listViewItemListenClick);
		    listView1.setOnItemLongClickListener(longClick);
			
			return rootView;
		}
		
		private void isConnected(Context context)throws Exception {
			 Exception connEx=new ConnectException();
			ConnectivityManager conn=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo network=conn.getActiveNetworkInfo();
			if(network==null || !network.isConnected()){
				throw connEx;
			
		   }
		
	    }
	}
	protected static class GetImageURL extends AsyncTask<String, Integer, Bitmap>{
              ImageView image;//�إߤ@��IMAGE����
              ProgressBar progressBar2;
              Context context;
		public GetImageURL(Context context,ImageView image,ProgressBar progressBar2){
			this.image=image;
			this.progressBar2=progressBar2;
			this.context=context;
		}
		@Override
		protected Bitmap doInBackground(String... Url) {
			// TODO �۰ʲ��ͪ���k Stub
			Bitmap bitmap = null;
			
			try{
				URL http=new URL(Url[0]);
				SocketTimeoutException conEx=new SocketTimeoutException();
				HttpURLConnection conn = (HttpURLConnection)http.openConnection();
	            conn.setReadTimeout(10000 /* milliseconds */);
	            conn.setConnectTimeout(15000 /* milliseconds */);
	            conn.setRequestMethod("GET");
	            conn.setDoInput(true);        
	            conn.connect();
	            if(conn.getInputStream()!=null){
				bitmap=BitmapFactory.decodeStream(conn.getInputStream());
	            }else{
	            	
	            	throw conEx;
	            	
	            }
			}catch(java.net.SocketTimeoutException e){
				System.out.println(e);
			}
			catch(Exception e){
				System.out.println("Exception :"+e);
			}
			
			return bitmap;
		}
		@Override
		 protected void onPostExecute(Bitmap bitmap) {
			if(bitmap==null){
			Toast.makeText(context.getApplicationContext(),"�Ϥ��L�k���J�A�D���ݦ��~", Toast.LENGTH_SHORT).show();
			}
			image.setImageBitmap(bitmap);
			progressBar2.setVisibility(View.INVISIBLE);
			
	    }
		@Override
		protected void onProgressUpdate(Integer... progress) {
			 
			progressBar2.setVisibility(View.VISIBLE);
			 super.onProgressUpdate(progress);
		}
		
	}
	
	protected static class HttpAsyncTask extends AsyncTask<Void,Integer,KHBean[] >{
		private Context thisContext;
		public HttpAsyncTask(Context context) {
			// TODO �۰ʲ��ͪ��غc�l Stub
			thisContext=context;
		}
		@Override
		protected void onProgressUpdate(Integer... progress) {
			 
			 pro.setVisibility(View.VISIBLE);
			 super.onProgressUpdate(progress);
		}
		 
		@Override//���槹����
		protected void onPostExecute(KHBean[]  DataBean) {
			 super.onPostExecute(DataBean);
			try{
			
			for(KHBean resultBean :DataBean){
				 HashMap<String,String> list=new HashMap<String,String>();
			     
			    list.put("Name",resultBean.getName());
			     list.put("Address", resultBean.getAdd());
			     list.put("Position",resultBean.positionID);//���θ�Ʃw��ϥ�
			     list2.add(list);
			    //System.out.println(resultBean.getID());
			    
			}
			}catch(Exception e){
				System.out.println(e);
				Toast.makeText(thisContext.getApplicationContext(),"�L�k�P�O�D���ӷ�", Toast.LENGTH_SHORT).show();
			}
			listView1.setAdapter(adapter);
			 pro.setVisibility(View.INVISIBLE);
			adapter.notifyDataSetChanged();
			
		}
		@Override//���椤
		protected KHBean[]  doInBackground(Void... params) {
			// TODO �۰ʲ��ͪ���k Stub
			try {
				  
	            return getWebData();
	        } catch (IOException e) {
	            e.printStackTrace();
	             
	            return null;
	        }        
		}
		
		 public KHBean[]  getWebData() throws IOException{
	            URL url=new URL("http://data.kaohsiung.gov.tw/Opendata/DownLoad.aspx?Type=2&CaseNo1=AV&CaseNo2=2&FileType=1&Lang=C&FolderType=");
	            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	            conn.setReadTimeout(10000 /* milliseconds */);
	            conn.setConnectTimeout(15000 /* milliseconds */);
	            conn.setRequestMethod("GET");
	            conn.setDoInput(true);        
	            conn.connect();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
	            StringBuilder sb = new StringBuilder();
	            String jsonString = null;
	            while ((jsonString = reader.readLine()) != null) {
	                sb.append(jsonString + "\n");
	            }
	            //System.out.println(sb);
	            reader.close();
	      
	            try {
	                return (getJson(sb.toString()));
	            } catch (JSONException e) {
	                e.printStackTrace();
	                return null;
	            }
	        
	        }
		 public KHBean[]   getJson(String jsonString) throws JSONException {
	           
	            JSONArray jsonArray =new JSONArray(jsonString);//�ѪRJSON
	           
	            String jsonName[]=new String[jsonArray.length()];
	            //String jsonAdd[]=new String [jsonArray.length()];
	            System.out.println(jsonArray.length());
	             saveJsonBean=new KHBean[jsonArray.length()];//��JBEAN
	           
	            for(int i=0;i<jsonArray.length();i++){
	            	JSONObject jsonobj=jsonArray.getJSONObject(i);
	            	
	            	jsonName[i]=jsonobj.getString("Name")+","+jsonobj.getString("Add");
	            	saveJsonBean[i]=new KHBean();
	            	saveJsonBean[i].setID(jsonobj.getString("Id"));
	            	saveJsonBean[i].setName(jsonobj.getString("Name"));
	            	saveJsonBean[i].setDescription(jsonobj.getString("Description"));
	            	saveJsonBean[i].setAddress(jsonobj.getString("Add"));
	            	saveJsonBean[i].setOpenTime(jsonobj.getString("Opentime"));
	            	saveJsonBean[i].setTel(jsonobj.getString("Tel"));
	            	saveJsonBean[i].setPicture(jsonobj.getString("Picture1"));
	            	saveJsonBean[i].setPostitionID(String.valueOf(i));
	            	//System.out.println(jsonobj.getString("Id"));
	            	
	            }

	            return saveJsonBean;
	}
         

		
	}
	//�^�ǵ�sharePreferences���r��
	public String getValue(int id){
		String ID=saveJsonBean[id].getID();
		String Name=saveJsonBean[id].getName();
		String Address=saveJsonBean[id].getAdd();
		String Tel=saveJsonBean[id].getTel();
		String Desc=saveJsonBean[id].getDesc();
		String opt=saveJsonBean[id].getOpenTime();
		String picture=saveJsonBean[id].getPicture();
	   
	    return ID+","+Name+","+Address+","+Tel+","+Desc+","+opt+","+picture;
	
	}
	
}
class KHBean{
	  String ID;
	  String Name;
	  String Description;
	  String Address;
	  String Tel;
	  String OpenTime;
	  String picture;
	  String positionID;
	  KHBean(){
		  
	  }
	  protected void setPostitionID(String id){
		  this.positionID=id;
	  }
	  protected void setID(String id){
		  this.ID=id;
	  }
	  protected void setName(String name){
		  this.Name=name;
	  }
	  protected void setDescription(String desc){
		  this.Description=desc;
	  }
	  protected void setAddress(String add){
		  this.Address=add;
	  }
	  protected void setTel(String tel){
		  this.Tel=tel;
	  }
	  protected void setOpenTime(String opentime){
		  this.OpenTime=opentime;
	  }
	  protected void setPicture(String picture){
		  this.picture=picture;
	  }
	  protected String getID(){
		  return this.ID;
	  }
	  protected String getName(){
		  return this.Name;
	  }
	  protected String getDesc(){
		  return this.Description;
	  }
	  protected String getAdd(){
		  return this.Address;
	  }
	  protected String getTel(){
		  return this.Tel;
		  
	  }
	  protected String getOpenTime(){
		  return this.OpenTime;
	  }
	  protected String getPicture(){
		  return this.picture;
	  }
	  
}

