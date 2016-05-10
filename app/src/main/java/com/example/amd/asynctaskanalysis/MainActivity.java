package com.example.amd.asynctaskanalysis;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
/*
    将ListView的控制权从getView移动到了滚动
 */
public class MainActivity extends AppCompatActivity {
    ListView mListView;
    List<NewsBean> newsBeanList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.lv_main);
        String URL = "http://www.imooc.com/api/teacher?type=4&num=30";
        new MyAsyncTask().execute(URL);
    }

    /**
     * 将url对应的JSON格式数据转化为我们所封装的newsBean对象
     *
     * @param param
     * @return
     */
    private List<NewsBean> getJsonData(String param) {
        newsBeanList = new ArrayList<NewsBean>();
        String url = param;
        try {
            String jsonString = readStream(new URL(url).openStream());//因为是这种写法所以就不必关闭io流了吗
            JSONObject jsonObject;
            NewsBean newsBean;
            try {
                jsonObject = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    newsBean = new NewsBean();
                    newsBean.newsTitle = jsonObject.getString("name");
                    newsBean.newsContent = jsonObject.getString("description");
                    newsBean.newsIconUrl = jsonObject.getString("picSmall");
                    newsBeanList.add(newsBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsBeanList;
    }

    /**
     * 通过inputStream解析网页返回的数据
     *
     * @param inputStream
     * @return
     */
    private String readStream(InputStream inputStream) {
        InputStreamReader isr;
        String result = "";
        try {
            isr = new InputStreamReader(inputStream, "utf-8");
            String line = "";
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 实现网络的异步访问
     */
    class MyAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {

        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return getJsonData(params[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeen) {
            super.onPostExecute(newsBeen);
            mListView.setAdapter(new NewsAdapter(MainActivity.this, newsBeen,mListView));
        }

    }

}
