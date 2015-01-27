package com.lza.pad.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lza.pad.R;
import com.lza.pad.fragment.base.BaseFragment;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 1/18/15.
 */
public class EbookContentFragment extends BaseFragment {

    private TextView mTxtLabIntro, mTxtLabList, mTxtLabRead, mTxtLabCol, mTxtIntro;
    private FrameLayout mLayoutSubContainer;

    private static final int LAB_INTRO = 1;
    private static final int LAB_LIST = 2;
    private static final int LAB_READ = 3;
    private static final int LAB_COL = 4;

    private int COLOR_BLUE, COLOR_RED;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        COLOR_BLUE = mActivity.getResources().getColor(R.color.common_blue);
        COLOR_RED = mActivity.getResources().getColor(R.color.red);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ebook_content, container, false);

        mTxtLabIntro = (TextView) view.findViewById(R.id.ebook_content_lab_intro);
        mTxtLabList = (TextView) view.findViewById(R.id.ebook_content_lab_list);
        mTxtLabRead = (TextView) view.findViewById(R.id.ebook_content_lab_read);
        mTxtLabCol = (TextView) view.findViewById(R.id.ebook_content_lab_col);
        mTxtIntro = (TextView) view.findViewById(R.id.ebook_content_intro);

        mLayoutSubContainer = (FrameLayout) view.findViewById(R.id.ebook_content_sub_container);

        mTxtLabIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickState(LAB_INTRO);
                mTxtIntro.setText(TEXT_INTRO);
            }
        });
        mTxtLabList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickState(LAB_LIST);
                mTxtIntro.setText(TEXT_LIST);
            }
        });
        mTxtLabRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickState(LAB_READ);
                mTxtIntro.setText(TEXT_READ);
            }
        });
        mTxtLabCol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickState(LAB_COL);
                mTxtIntro.setText(TEXT_COL);
            }
        });
        return view;
    }

    private void setClickState(int index) {
        mTxtLabIntro.setTextColor(COLOR_BLUE);
        mTxtLabList.setTextColor(COLOR_BLUE);
        mTxtLabRead.setTextColor(COLOR_BLUE);
        mTxtLabCol.setTextColor(COLOR_BLUE);
        if (index == LAB_INTRO) {
            mTxtLabIntro.setTextColor(COLOR_RED);
        } else if (index == LAB_LIST) {
            mTxtLabList.setTextColor(COLOR_RED);
        } else if (index == LAB_READ) {
            mTxtLabRead.setTextColor(COLOR_RED);
        } else if (index == LAB_COL) {
            mTxtLabCol.setTextColor(COLOR_RED);
        }
    }

    private static final String TEXT_INTRO = "騎著青蛙翻閱安地斯山，在低音大提琴盒裡藏侏儒屍體，戴著毛皮面具吃肉的狼人，以玩具槍、鋸子聲響、狂歡派對不斷出招的高校女生……種種奇詭色彩的情節，盡在寺山修司的書海漫遊之間。\n日本知名的劇場與電影導演暨詩人寺山修司，暢談胴人、賭馬、拷問、娼妓、變形漫畫等怪誕的閱讀主題，展現他一貫敏銳、感傷又不失幽默的異想風格。這是寺山修司第一本在台問世的作品，透過寺山修司的私閱讀，讀者將可一窺這位素以大膽前衛著稱的創作者的靈感源頭。";
    private static final String TEXT_LIST = "1關於頭髮的趣味事典\n" +
            "2.成為青蛙學者的愉快百科\n" +
            "3.當男人擁有後宮時\n" +
            "4.怪物們的嘉年華\n" +
            "5.奧茲魔法師的剪貼簿\n" +
            "6.黑人的真實畫報\n" +
            "7.關於娼妓的黑暗畫報\n" +
            "8.邊睡邊讀的趣味寢台書\n" +
            "9.鞋子民俗學的閱讀方法\n" +
            "10.關於書的百科\n" +
            "11.推理小說中描繪的女人肖像\n" +
            "12.愛馬的知識畫報\n" +
            "13.受虐狂的電影民俗學\n" +
            "14.少年時代是個獵奇雜誌迷\n" +
            "15.蒐集狂們的謎樣情報交換誌\n" +
            "16.失眠夜晚的拷問博物誌\n" +
            "17.月夜下獨自閱讀的狼人入門書\n" +
            "18.聖特利妮安女學生的叛亂\n" +
            "19.格蘭威爾的發狂漫畫集";
    private static final String TEXT_READ = "[试读部分，暂未实现，敬请期待]";
    private static final String TEXT_COL = "[馆藏地查询，暂未实现，敬请期待]";
}
