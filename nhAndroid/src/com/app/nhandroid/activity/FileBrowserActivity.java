package com.app.nhandroid.activity;  
  
import java.io.File;  
import java.util.ArrayList;  
import java.util.Arrays;
import java.util.Collections;
import java.util.List;  

import com.app.nhandroid.libs.FileAdapter;
import com.example.nhandroid.R;
  
import android.app.Activity;
import android.app.AlertDialog;  
import android.content.DialogInterface;  
import android.content.Intent;  
import android.graphics.Color;  
import android.os.Bundle;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.AdapterView;  
import android.widget.AdapterView.OnItemClickListener;  
import android.widget.Button;  
import android.widget.ImageView;  
import android.widget.LinearLayout;  
import android.widget.ListView;  
import android.widget.TextView;  
import android.widget.Toast;  
  
  
public class FileBrowserActivity extends Activity {  
  
    private List<String> items=null;  
    private List<String> paths=null;  
    private String rootPath="/sdcard/";  
    private TextView mPath;  
    private ListView list;    
    private FileAdapter m_FileAdapter;  
    private void getFileDir(String filePath){  
  
        File f=new File(filePath);   
        if(f.exists() && f.canWrite()){  
            mPath.setText(filePath);  
            items=new ArrayList<String>();  
            paths=new ArrayList<String>();  
            File[] files=f.listFiles();
            
            ArrayList<File> totalFiles=new ArrayList<File>();
            for(int i=0;i<files.length;i++){  
                File file=files[i];  
                totalFiles.add(file);
            }  
            
            Collections.sort(totalFiles);
            
            if(!filePath.equals(rootPath)){  
                items.add("goroot");  
                paths.add(rootPath);  
                items.add("goparent");  
                paths.add(f.getParent());  
            }  
            for(int i=0;i<totalFiles.size();i++){  
                File file=totalFiles.get(i);  
                if(file.isDirectory()){  
                    items.add(file.getName());  
                    paths.add(file.getPath());  
                }  
            }  
            
            m_FileAdapter = new FileAdapter(this,items,paths);  
            list.setAdapter(m_FileAdapter);  
            list.setOnItemClickListener(new OnItemClickListener(){  
                  
                @Override  
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {  
                      
                    if(items.get(position).toString().equals("goparent")){  
                        getFileDir(paths.get(position));  
                    }else if(items.get(position).toString().equals("goroot")){  
                        getFileDir(paths.get(position));  
                        return;  
                    }else{  
                        File file=new File(paths.get(position));  
                        if(file.canWrite()){  
                            if (file.isDirectory()){  
                                getFileDir(paths.get(position));  
                            }  
                        }else{  
                            LinearLayout lay = new LinearLayout(FileBrowserActivity.this);  
                            lay.setOrientation(LinearLayout.HORIZONTAL);  
                            ImageView image = new ImageView(FileBrowserActivity.this);  
                            TextView text = new TextView(FileBrowserActivity.this);  
                            text.setTextColor(Color.RED);  
                            text.setTextSize(20);  
                            text.setText("很抱歉您的权限不足!");  
                            Toast toast = Toast.makeText(FileBrowserActivity.this, text.getText().toString(), Toast.LENGTH_LONG);  
                            image.setImageResource(android.R.drawable.stat_sys_warning);  
                            lay.addView(image);  
                            lay.addView(text);  
                            toast.setView(lay);  
                            toast.show();  
                        }  
                    }  
                }  
            });  
        }else{  
            LinearLayout lay = new LinearLayout(FileBrowserActivity.this);  
            lay.setOrientation(LinearLayout.HORIZONTAL);  
            ImageView image = new ImageView(FileBrowserActivity.this);  
            TextView text = new TextView(FileBrowserActivity.this);  
            text.setTextColor(Color.RED);  
            text.setTextSize(20);  
            text.setText("无效目录！");  
            Toast toast = Toast.makeText(FileBrowserActivity.this, text.getText().toString(), Toast.LENGTH_LONG);  
            image.setImageResource(android.R.drawable.stat_sys_warning);  
            lay.addView(image);  
            lay.addView(text);  
            toast.setView(lay);  
            toast.show();  
//            this.finish();  
        }  
    }  
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        this.setContentView(R.layout.filelist);  
        mPath = (TextView)this.findViewById(R.id.mPath);  
        list = (ListView)this.findViewById(R.id.filelist);  
        getFileDir(rootPath);  
//        mPath.setTextColor(this.getResources().getColor(R.color.text_color));  
        this.setTitle("请选择保存目录:");  
        Button ok = (Button)this.findViewById(R.id.fileok);  
        ok.setPadding(0, 5, 0, 5);  
        ok.setOnClickListener(new OnClickListener(){  
            @Override  
            public void onClick(View v) {  
                /*if(mPath.getText().toString().equals(rootPath)){ 
                    LinearLayout lay = new LinearLayout(FileBrowserActivity.this); 
                    lay.setOrientation(LinearLayout.HORIZONTAL); 
                    ImageView image = new ImageView(FileBrowserActivity.this); 
                    TextView text = new TextView(FileBrowserActivity.this); 
                    text.setTextColor(FileBrowserActivity.this.getResources().getColor(R.color.text_color)); 
                    text.setTextSize(16); 
                    text.setText("很抱歉您的权限不足!"); 
                    Toast toast = Toast.makeText(FileBrowserActivity.this, text.getText().toString(), Toast.LENGTH_SHORT); 
                    image.setImageResource(android.R.drawable.stat_sys_warning); 
                    lay.addView(image); 
                    lay.addView(text); 
                    toast.setView(lay); 
                    toast.show(); 
                }else{*/  
                    Intent i = new Intent();  
                    Bundle b = new Bundle();    
                    b.putString("savePath", mPath.getText().toString());    
                    b.putString("url", FileBrowserActivity.this.getIntent().getStringExtra("url"));    
                    b.putString("fileName", FileBrowserActivity.this.getIntent().getStringExtra("fileName"));    
                    i.putExtras(b);  
                    FileBrowserActivity.this.setResult(RESULT_OK, i);  
                    FileBrowserActivity.this.finish();  
                //}  
                  
            }  
        });  
        Button cancel = (Button)this.findViewById(R.id.filecancel);  
        cancel.setPadding(0, 5, 0, 5);  
        cancel.setOnClickListener(new OnClickListener(){  
            @Override  
            public void onClick(View v) {  
                FileBrowserActivity.this.finish();  
            }  
        });  
    }  
}  