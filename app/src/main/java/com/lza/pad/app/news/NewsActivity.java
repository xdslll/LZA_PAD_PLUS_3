package com.lza.pad.app.news;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.app.base.BaseModuleActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/21/15.
 */
public class NewsActivity extends BaseModuleActivity {

    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(this);
    }

    @Override
    protected String getModName() {
        return "新闻动态";
    }

    @Override
    protected void onDrawWindow(LinearLayout container, int w, int h) {
        ListView list = new ListView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h);
        list.setLayoutParams(params);
        list.setAdapter(new NewsAdapter());
        list.setBackgroundColor(Color.WHITE);
        list.setDivider(new ColorDrawable(getResources().getColor(R.color.common_light_gray)));
        list.setDividerHeight(2);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(NewsActivity.this, NewsContentActivity.class));
            }
        });
        container.addView(list);
    }

    static List<Object[]> mDatas = new ArrayList<Object[]>();
    static {
        for (int i = 0; i < 15; i++) {
            if (i % 2 == 0) {
                mDatas.add(new Object[]{"我校开通不列颠百科全书网络版的通知",
                        "不列颠百科全书网络版是第一部网络百科全书。世界各地的用户都可通过网络查询不列颠百科全书的全文。不列颠百科全书公司以其强大的内容编辑实力，丰富可靠咨询来源及数据库检索技术，成为全球工具书领域的领航者。目前，不列颠百科全书网络版已被世界各地的高等院校、中、小学、图书馆及政府机构等普遍应用于教学和研究中，是世界上使用最广泛的完整的电子参考和学习研究工具之一。\n不列颠百科全书网络版提供高品质、全面、快速、方便查找的信息。可检索词条达到225,000多条，超过340,000种词类变化。并收录了超过160,000篇文章，33,000篇传记，1000种的电子期刊文章，75,000张的图解、地图、统计图、7,000段影片、动画、声音文件等多媒体数据。不列颠百科全书公司还精心挑选了160,000多个优秀的互联网网站链接，其中还包括一个对世界每一个国家的综合统计数据库。",
                        "2015-1-10 13:08",
                        -1});
            } else {
                mDatas.add(new Object[]{"我校开通不列颠百科全书网络版的通知",
                        "不列颠百科全书网络版是第一部网络百科全书。世界各地的用户都可通过网络查询不列颠百科全书的全文。不列颠百科全书公司以其强大的内容编辑实力，丰富可靠咨询来源及数据库检索技术，成为全球工具书领域的领航者。目前，不列颠百科全书网络版已被世界各地的高等院校、中、小学、图书馆及政府机构等普遍应用于教学和研究中，是世界上使用最广泛的完整的电子参考和学习研究工具之一。\n不列颠百科全书网络版提供高品质、全面、快速、方便查找的信息。可检索词条达到225,000多条，超过340,000种词类变化。并收录了超过160,000篇文章，33,000篇传记，1000种的电子期刊文章，75,000张的图解、地图、统计图、7,000段影片、动画、声音文件等多媒体数据。不列颠百科全书公司还精心挑选了160,000多个优秀的互联网网站链接，其中还包括一个对世界每一个国家的综合统计数据库。",
                        "2015-1-10 13:08",
                        R.drawable.test_news});
            }
        }
    }

    private class NewsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.news_list_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.news_list_item_title);
                holder.content = (TextView) convertView.findViewById(R.id.news_list_item_content);
                holder.date = (TextView) convertView.findViewById(R.id.news_list_item_date);
                holder.img = (ImageView) convertView.findViewById(R.id.news_list_item_img);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Object[] data = mDatas.get(position);
            holder.title.setText((String) data[0]);
            holder.content.setText((String) data[1]);
            holder.date.setText((String) data[2]);

            int resId = (Integer) data[3];
            if (resId != -1) {
                holder.img.setImageResource(resId);
                holder.img.setVisibility(View.VISIBLE);
            } else {
                holder.img.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

    private static class ViewHolder {
        TextView title, content, date;
        ImageView img;
    }
}
