package com.example.yangliang.myweatherapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yangliang.myweatherapp.db.City;
import com.example.yangliang.myweatherapp.db.County;
import com.example.yangliang.myweatherapp.db.Province;
import com.example.yangliang.myweatherapp.util.HttpUtil;
import com.example.yangliang.myweatherapp.util.JsonParserUtility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 创建日期：2017/9/25 on 上午10:59
 * 描述: 用于遍历省市县的碎片
 * 作者: liangyang
 */
public class ChooseAreaFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    //初始化ListView数据
    private List<String> dataList = new ArrayList<>();
    private View view;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    //省列表
    private List<Province> provinceList;
    //市列表
    private List<City> cityList;
    //区县列表
    private List<County> countyList;
    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;
    private ProgressDialog progressDialog;

    public ChooseAreaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        //初始化视图
        initView();
        return view;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //在Fragment的onActivityCreated中设置ListView的点击事件
        listView.setOnItemClickListener(this);

        //button的点击事件
        backButton.setOnClickListener(this);

        //在onActivityCreated方法的最后，调用了queryProvinces()方法，也就是从这里开始加载省级数据的
        queryProvinces();

    }

    /**
     * ListView的点击监听事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (currentLevel == LEVEL_PROVINCE) {
            //如果当前是省级选项,则数据由省数组中获取,去查询市
            selectedProvince = provinceList.get(position);
            Toast.makeText(getContext(), "你所选的省份是：" + selectedProvince.getProvinceName(), Toast.LENGTH_SHORT).show();
            //查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            //如果当前是市级选项,则数据由市数组中获取，去查询区县
            selectedCity = cityList.get(position);
            Toast.makeText(getContext(), "你所选的城市份是：" + selectedCity.getCityName(), Toast.LENGTH_SHORT).show();
            //查询选中市内所有的区县，优先从数据库查询，如果没有查询到再去服务器上查询
            queryCounties();
        }

    }

    /**
     * 查询选中市内所有的区县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        //调用LitePal的查询接口，根据当前市的id来获取所选区县的数据，如果读取到了则显示到界面上
        countyList = DataSupport.where("cityid = ?",
                String.valueOf(selectedCity.getId())).find(County.class);
        //优先从数据库查询，如果没有查询到再去服务器上查询
        if (countyList.size() > 0) {
            dataList.clear();
            //循环遍历区县数据
            for (County county : countyList) {
                //填充数据
                dataList.add(county.getCountyName());
            }
            //刷新适配器
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            //如果没有读取到,则根据服务请求的接口组装出一个请求地址,然后调用queryFromServer()方法从服务器上查询数据
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        //调用LitePal的查询接口，根据当前省的id来获取所选市的数据，如果读取到了则显示到界面上
        cityList = DataSupport.where("provinceid = ?",
                String.valueOf(selectedProvince.getId())).find(City.class);
        //优先从数据库查询，如果没有查询到再去服务器上查询
        if (cityList.size() > 0) {
            //刷新数据
            dataList.clear();
            //循环遍历市数据cityList
            for (City city : cityList) {
                //获取当前省内的所有城市的名称
                dataList.add(city.getCityName());
            }
            //刷新listView的适配器
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            //进入城市级
            currentLevel = LEVEL_CITY;
        } else {
            //如果没有读取到,则根据服务请求的接口组装出一个请求地址,然后调用queryFromServer()方法从服务器上查询数据
            //由省级查询市级：handleCityResponse(String response, int provinceId)
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }

    }

    /**
     * 查询选中省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        titleText.setText("中国");
        //省级不能再返回
        backButton.setVisibility(View.GONE);
        //优先从数据库中查询,调用LitePal的查询接口l从数据库中独取省级数据
        provinceList = DataSupport.findAll(Province.class);
        //如果从数据库中读取到了数据，则直接显示到界面上
        if (provinceList.size() > 0) {
            //清空数据列表
            dataList.clear();
            //循环遍历省数据
            for (Province province : provinceList) {
                //加载数据
                dataList.add(province.getProvinceName());
            }
            //刷新适配器
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            //如果没有读取到,则根据服务请求的接口组装出一个请求地址,然后调用queryFromServer()方法从服务器上查询数据
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据。
     *
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type) {
        //显示进度条
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //服务器返回的json数据
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    //解析和处理服务器返回的省级数据，并存储到数据库中
                    result = JsonParserUtility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    //解析和处理服务器返回的市级数据，并存储到数据库中
                    result = JsonParserUtility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    //解析和处理服务器返回的区县级数据，并存储到数据库中
                    result = JsonParserUtility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result) {
                    //解析和处理完JSON数据后再次调用queryProvinces()，queryCities()，queryCounties()来从新加载数据
                    //这三个方法涉及UI操作，必须在主线程中调用
                    //此时数据库中已经存在数据，直接查询可查询到数据，显示到界面上
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭Dialog
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                //查询省
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                //查询市
                                queryCities();
                            } else if ("county".equals(type)) {
                                //查询区县
                                queryCounties();
                            }
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败~", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    //关闭进度对话框
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    //显示进度对话框
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * button的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        }
    }
}
