package com.fun.kh;

import java.io.File;
import java.io.FileOutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.annotation.SuppressLint;

import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;

import android.widget.GridView;
import android.widget.ImageView;

import android.widget.ProgressBar;

import android.widget.SimpleAdapter;

import android.widget.TextView;
import android.widget.Toast;

public class Favorite  extends ActionBarActivity{
	 SharedPreferences settings ;
  private GridView Gv;
  private ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();;
  private static FBean dataBean[];
  private HashMap<Integer, String> Del_Data=new HashMap<Integer, String>(); //Select物件用	position ,key
  int SelectPosition;
  File filepath=null;
  private SparseBooleanArray selectItem=new SparseBooleanArray();
  SimpleAdapter adapter;
  GetImageURL getView;
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
	  
	  menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "移除" );
	  menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "全部移除 " );
      return super.onCreateOptionsMenu(menu);
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
	  
	  System.out.print("ItemTitle:"+item.getTitle());
	   if(item.getTitle().equals("移除")){
		   System.out.println("移除");
		   Object key[]=Del_Data.keySet().toArray();
		   if(key.length>0){
		   for(int i=0;i<key.length;i++){
			   Integer keyset=(Integer) key[i];
			   System.out.println("Position:"+keyset+"dataKey:"+Del_Data.get(keyset));
			   deleteItem(Del_Data.get(keyset),keyset.toString());
		   }
		   }else{
			   System.out.println("無刪除項目");
		   }
		   list.clear();
		   selectItem.clear();
		   Del_Data.clear();
            settings =getSharedPreferences("SaveFavorite", 0);
		    creatView(settings);
			 Gv.setAdapter(adapter);
		   adapter.notifyDataSetChanged();
		  
	   }else if(item.getTitle().equals("全部移除 ")){
		   AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		   dialog.setTitle("是否全部刪除?!");
		   dialog.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO 自動產生的方法 Stub
						 SharedPreferences settings ;
						settings =getSharedPreferences("SaveFavorite", 0);
						SharedPreferences.Editor editor =settings.edit();
						editor.clear();
						editor.commit();
						File[] file=getCacheDir().listFiles();
						if(file.length>0){
						for(int i=0;i<file.length;i++){
							System.out.println("刪除"+file[i].getPath());
						System.out.println("刪除全部"+file[i].delete());
						}
						list.clear();
						   selectItem.clear();
						   Del_Data.clear();
				            settings =getSharedPreferences("SaveFavorite", 0);
						    creatView(settings);
							 Gv.setAdapter(adapter);
						   adapter.notifyDataSetChanged();
						}
				}
				
			});
			dialog.setNegativeButton("NO", null);
			dialog.show();
		  
	   }
	   
	  return super.onOptionsItemSelected(item);
  }
  @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//判斷有無網路
		try{
			isConnected(Favorite.this);
			}catch(Exception e){
				Toast.makeText(Favorite.this,"無網路",Toast.LENGTH_SHORT).show();;
			}
		setContentView(R.layout.favorite_layout);
		/* GOOGLE 廣告橫幅*/
		 AdView mAdView = (AdView) this.findViewById(R.id.adView2);
	        AdRequest adRequest = new AdRequest.Builder().build();
	        mAdView.loadAd(adRequest);
	        /* GOOGLE 廣告橫幅*/ 
		Gv=(GridView) findViewById(R.id.gridView1);
		
		SharedPreferences settings ;
		settings =getSharedPreferences("SaveFavorite", 0);
		creatView(settings);//set Favorite Bean
		 
		 	
		 adapter = new SimpleAdapter(this,list,R.layout.gridview_layout,new String[]{"image", "text"},new int[]{R.id.gimageView1,R.id.gtextView1}){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO 自動產生的方法 Stub
				View conview=super.getView(position, convertView, parent);
				ImageView image;
                TextView text; 
				 System.out.println("Position"+position);
				 image = (ImageView)conview.findViewById(R.id.gimageView1);
				 text=(TextView) conview.findViewById(R.id.gtextView1);
				 getView=new GetImageURL(Favorite.this,image,dataBean[position].getID());
				 getView.execute(dataBean[position].getPicture());
				 text.setText(dataBean[position].getName());
				 filepath=new File(getCacheDir(),dataBean[position].getID()+".png");
				 System.out.println("picture-"+filepath.getPath());
				 image.setImageDrawable(Drawable.createFromPath(filepath.getPath()));
				 System.out.println("狀態:"+getView.getStatus());
				  
				 conview
	                .setBackgroundColor(selectItem.get(position) ? 0xffffc0cb
	                        : 0xfffffacd);
				 image.setBackgroundColor(selectItem.get(position)?0xffffc0cb: 0xfffffacd);
				    return conview;
				
			}
			
		};
		
		Gv.setNumColumns(3);
		
		Gv.setAdapter(adapter);
	    
		Gv.setOnItemClickListener(new GridView.OnItemClickListener(){

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO 自動產生的方法 Stub
				Gv.setSelection(position);
				System.out.println("ItemClick"+position);
				boolean po=!selectItem.get(position);
				if(po){
					
					selectItem.put(position,po );
					Del_Data.put(position,dataBean[position].key);
					System.out.println("Key:"+dataBean[position].key);
				}else{
					
					selectItem.delete(position);
					Del_Data.remove(position);
				}
				Toast.makeText(Favorite.this, "您選擇的是"+dataBean[position].getName(), Toast.LENGTH_SHORT).show();
				adapter.notifyDataSetChanged();
			}
			
		});
	
		AdapterView.OnItemLongClickListener longClick=new AdapterView.OnItemLongClickListener() {
	    	
		     
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO 自動產生的方法 Stub
				 AlertDialog dialog = null;
				LayoutInflater inflater =Favorite.this.getLayoutInflater();
				AlertDialog.Builder builder = new AlertDialog.Builder(Favorite.this);
				View priview=inflater.inflate(R.layout.contain_layout,null);
			     int SelectPosition=arg2;
			     ImageView image=(ImageView) priview.findViewById(R.id.imageView1);
			     ProgressBar progressBar2=(ProgressBar) priview.findViewById(R.id.progressBar2);
			   
			     TextView title=(TextView) priview.findViewById(R.id.title);
			     TextView address=(TextView) priview.findViewById(R.id.address);
			     TextView text=(TextView) priview.findViewById(R.id.context);
			     TextView openTimeText=(TextView) priview.findViewById(R.id.openTimeText);
			     builder.setView(priview);	
					builder.setPositiveButton("OK", null);
					//Button OK=dialog.getButton(Dialog.BUTTON_POSITIVE);
					System.out.println(SelectPosition);
					String http="http://www.google.com.tw/maps/place/"+dataBean[SelectPosition].getAdd();
					address.setText(Html.fromHtml("<a href=\""+http+"\">"+dataBean[SelectPosition].getAdd()+"</a>"));
					address.setMovementMethod(LinkMovementMethod.getInstance());
					 
					title.setText(dataBean[SelectPosition].getName());
					text.setText(dataBean[SelectPosition].getDesc());
					openTimeText.setText(dataBean[SelectPosition].getOpenTime()+"  tel:"+dataBean[SelectPosition].getTel());
					new GetImageURL2(Favorite.this,image,progressBar2).execute(dataBean[SelectPosition].getPicture()); //設定圖片
					dialog=builder.create();
					dialog.show();
					
					return false;
			}
	    };
		Gv.setOnItemLongClickListener(longClick);
	}
  protected void creatView(SharedPreferences settings){
	 Map<String,?>data1=settings.getAll();//share
	 dataBean=new FBean[data1.size()];
	 HashMap<String,String> list2=new HashMap<String,String>();
  Object[] key=data1.keySet().toArray();
	 
	for(int i=0;i<key.length;i++){
		 
		dataBean[i]=new FBean();
		dataBean[i].setAll(data1.get(key[i].toString()).toString(),key[i].toString());
		System.out.println(key[i].toString()+":"+data1.get(key[i].toString()));//get map value
		System.out.println("keyId:"+key[i].toString());//get map stringID
		System.out.println("DataBeanID:"+dataBean[i].getID());
		
	}
	if(dataBean.length==0){
		Toast.makeText(Favorite.this, "無資料",Toast.LENGTH_SHORT).show();
	}else{
		System.out.println("databeansize:"+dataBean.length);
	for(int i=0;i<dataBean.length;i++){
		
		System.out.println("getPicture:"+dataBean[i].getPicture());
		System.out.println("getName:"+dataBean[i].getName());
		list2.put("image",dataBean[i].getPicture());
		list2.put("text", dataBean[i].getName());
		list.add(list2);
		 
		
	}
	 
   }
  }
 protected void deleteItem(String key,String position){
	
		settings =getSharedPreferences("SaveFavorite", 0);
		SharedPreferences.Editor editor =settings.edit();
		editor.remove(key);
		editor.commit();
		int position2=Integer.valueOf(position);
		File file =new File(getCacheDir(),dataBean[position2].getID()+".png");
		boolean deleted=file.delete();
		System.out.println(deleted+"file:"+file.getPath()+file.getName());
		
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
 class GetImageURL extends AsyncTask<String, Integer, Bitmap>{
	ImageView sa;
    Context context;
   String ID;
   File file=null;
    
public GetImageURL(Context context,ImageView sa,String ID ){
	
	this.context=context;
	 this.ID=ID;
	 this.sa=sa;
}
@Override
protected Bitmap doInBackground(String... Url) {
	// TODO 自動產生的方法 Stub
	Bitmap bitmap = null;
	URL http=null;
	HttpURLConnection conn=null;
	try{
		 http=new URL(Url[0]);
		SocketTimeoutException conEx=new SocketTimeoutException();
		 conn = (HttpURLConnection)http.openConnection();
      conn.setReadTimeout(10000 /* milliseconds */);
      conn.setConnectTimeout(15000 /* milliseconds */);
      conn.setRequestMethod("GET");
      conn.setDoInput(true);        
      conn.connect();
      if(conn.getInputStream()!=null){
    	 File dir=context.getCacheDir();
    	  File savePath=new File(dir,ID+".png");
    		if(savePath.exists()){
    			System.out.println(savePath.getPath()+"圖片已存在"+savePath.getName());
    			 
    		}else{
		     bitmap=BitmapFactory.decodeStream(conn.getInputStream());
		     //儲存圖片
		     try{
				 FileOutputStream fos =new FileOutputStream(savePath);
				 bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
				 fos.close();
				 conn.disconnect();
			}catch(Exception e){
				System.out.println("FILEOUTERROR:"+e);
				
			}
    		
    		}
		     
		     
      }else{
    		
    		System.out.println("conEx");
      	throw conEx;
      
      }
	}catch(java.net.SocketTimeoutException e){
		
		System.out.println("TimeoutException:"+e);
	}
	catch(Exception e){
		
		System.out.println("Exception :"+e);
	}
	 
	
	return bitmap;
}
@Override
protected void onPostExecute(Bitmap bitmap) {
	if(sa.getDrawable()==null){
	 file=new File(context.getCacheDir(),this.ID+".png");
	 
	 System.out.println("picturePath"+file.getPath());
	sa.setImageDrawable(Drawable.createFromPath(file.toString()));
	}
	
}
@Override
protected void onProgressUpdate(Integer... progress) {
	 
	
	 super.onProgressUpdate(progress);
}

}
 class GetImageURL2 extends AsyncTask<String, Integer, Bitmap>{
     ImageView image;//建立一個IMAGE物件
     ProgressBar progressBar2;
     Context context;
public GetImageURL2(Context context,ImageView image,ProgressBar progressBar2){
	this.image=image;
	this.progressBar2=progressBar2;
	this.context=context;
}
@Override
protected Bitmap doInBackground(String... Url) {
	// TODO 自動產生的方法 Stub
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
	Toast.makeText(context.getApplicationContext(),"圖片無法載入，主機端有誤", Toast.LENGTH_SHORT).show();
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

class FBean{
	 String ID;
	  String Name;
	  String Description;
	  String Address;
	  String Tel;
	  String OpenTime;
	  String picture;
	  String key;
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
	 protected void setAll(String s,String key){
		 String[] temp=s.split(",");
			 for(int i=0;i<temp.length;i++){
				 if(i==0)
					 this.setID(temp[i]);
				 if(i==1)
					 this.setName(temp[i]);
				 if(i==2)
					 this.setAddress(temp[i]);
				 if(i==3)
				 this.setTel(temp[i]);
				 if(i==4)
					 this.setDescription(temp[i]);
				if(i==5)
				    this.setOpenTime(temp[i]);
				if(i==6)
					this.setPicture(temp[i]);
				 
			 }
		
			this.key=key;	 
	 }
}
