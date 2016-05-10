package com.example.amd.asynctaskanalysis;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by amd on 2016/5/7.
 */
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{

    private LayoutInflater mLayoutInflater ;
    private List<NewsBean> list;
    private ImageLoader mImageLoader;//不用每次都new一个，保证只有一个LruCache
    private int mStart,mEnd;
    public static  String[] URLS;
    private boolean mFirstIn;//是不是第一次启动

    NewsAdapter(Context context, List<NewsBean> list, ListView listView){
        mLayoutInflater = LayoutInflater.from(context);
        this.list = list;
        mImageLoader = new ImageLoader(listView);
        URLS = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            URLS[i] = list.get(i).newsIconUrl;
        }
        mFirstIn = true ;
        //一定要注册事件，启动控制权！！！
        listView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        Log.d("asd","哈哈count");
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        Log.d("asd","getItem");
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.d("asd","getItemId");
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("asd","getView");
        ViewHolder viewHolder ;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item,null);
            viewHolder.Icon = (ImageView) convertView.findViewById(R.id.item_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.Icon.setImageResource(R.mipmap.ic_launcher);
        String url = list.get(position).newsIconUrl;
        viewHolder.Icon.setTag(url);
        mImageLoader.showBitmapByAsyncTask(url,viewHolder.Icon);
        viewHolder.title.setText(list.get(position).newsTitle);
        viewHolder.content.setText(list.get(position).newsContent);
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE){
            //加载可见项
            mImageLoader.loadImages(mStart,mEnd);
        }else{
            //停止任务
            mImageLoader.cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mStart = firstVisibleItem;
        mEnd = firstVisibleItem + visibleItemCount;
        //第一次显示时候调用
        if(mFirstIn && visibleItemCount > 0 ){
            mImageLoader.loadImages(mStart,mEnd);
            mFirstIn = false;
        }
    }

    class ViewHolder{
        ImageView Icon;
        TextView title;
        TextView content;
    }

}
