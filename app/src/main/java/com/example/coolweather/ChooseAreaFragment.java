package com.example.coolweather;

import android.os.Bundle;
import android.print.PrinterId;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coolweather.R;
import com.example.coolweather.db.City;
import com.example.coolweather.db.Country;
import com.example.coolweather.db.Province;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;



public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressBar progressBar;//代替ProgressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<Country> countryList;
    /**
     * 选中省份
     */
    private Province selectedProvince;
    /**
     * 选中城市
     */
    private City selectedCity;
    /**
     * 当前选中级别
     */
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);//ListView中显示的数据来源于dataList
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);//获取点击区域的省，listView中现实的位置应跟省数组中对应顺序肯定一致；
                    //后面程序可以看出
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);//获取点击区域的市
                    queryCounties();
                }
            }
        });//点击listview子项

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });//如果当前是县级界面，点击按钮则回退到市级界面，如果当前是市级界面点击按钮则回退到省级界面

        queryProvinces();//一开始的界面应该是看到省
    }

    /**
     * 查询全国所有省，优先从数据库查询，没找到去服务器找
     */
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);//省级界面时回退按键不可见
        provinceList = LitePal.findAll(Province.class);//从数据库中找到所有省的数据
        if (provinceList.size() > 0) {//如果数据库中有数据，则从数据库中找到所有省的数据
            dataList.clear();//临时存放数据用的
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();//刷新ListView
            listView.setSelection(0);//?这个动作是干嘛的
            currentLevel = LEVEL_PROVINCE;
        } else {
           String address = "http://guolin.tech/api/china";
           // String address = "https://vivo.com";
            Log.d("MainActivity", "执行到访问省级数据 ");
            queryFromServer(address, "province");//否则从服务器上申请
        }//发现，碎片只加载了一次布局，但是可以复用多次，省，市，县都是用的同一个碎片；因为他们布局都是相同的，所以用碎片
        //这里碎片优于活动，直接操作控件就行，反正是同一布局
        //后面会发现，从服务器访问数据，也是先存在数据库，再访问；
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，没找到去服务器找
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);//市级界面时回退按键可见
        cityList = LitePal.where("provinceid=?", String.valueOf(selectedProvince.getId())).find(City.class);//获得该省中所有的市
        //数据库中provinceId会变成小写？是的，并不是跟类定义中完全一致，而是只有小写；大写也会转为小写
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，没找到去服务器找
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);//县级界面时回退按键不可见
        countryList = LitePal.where("cityid=?", String.valueOf(selectedCity.getId())).find(Country.class);//获得该省中所有的市
        if (countryList.size() > 0) {
            dataList.clear();
            for (Country country : countryList) {
                dataList.add(country.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china" + provinceCode + "/" + cityCode;
            queryFromServer(address, "country");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询数据
     *
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {//这里是常量的原因，传入的是字符串常量；
        // showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // closeProgressDialog();
                       // Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity", "加载失败");
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);//不同地址返回的数据是不同的
                   if(result==false)
                    Log.d("MainActivity", "没下载到资源");
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("country".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // closeProgressDialog();
                            if ("province".equals(type)) {
                              //  Log.d("MainActivity", "换个网址可以");
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("country".equals(type)) {
                                queryCounties();
                            }
                        }
                    });//切换回主线程,Callback()是个什么玩意,什么时候切换到子线程了
                }
            }
        });

    }

    private void closeProgressDialog() {
        if (progressBar != null) {
            //progressBar.setVisibility();
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressBar == null) {
            progressBar = new ProgressBar(getContext());
            // progressBar.setProgress();
            //progressBar.se
        }
        progressBar.isShown();
    }
}
