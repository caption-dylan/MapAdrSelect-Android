### 地图地址选择器
1. 支持地图：高德、腾讯、百度    
2. 界面支持自定义修改
### 效果图如下：    
![image](https://github.com/bigStuart/MapAdrSelect-Android/blob/master/res/37265ed0-4945-4106-8bd1-7d561b06a6bb.gif)    
### 基本用法
```
Intent intent = new Intent(MainActivity.this, ActivityMapMainIndex.class);
startActivityForResult(intent, ActivityMapMainIndex.KEY_REQUEST_CODE);
```

### 修改ui界面
```
Intent intent = new Intent(MainActivity.this, ActivityMapMainIndex.class);

//修改ui的属性
Customize customize = new Customize();

customize.setBgColor(getResourcesColor(R.color.colorAccent));

customize.setBackText(getResourcesString(R.string.back_name));
customize.setBackColor(getResourcesColor(R.color.colorPrimary));

customize.setTitle(getResourcesString(R.string.title));
customize.setTitleColor(getResourcesColor(R.color.colorPrimary));

customize.setSearchIcon(R.mipmap.ic_search);

customize.setConfirmBg(R.drawable.confirm_bg);
customize.setConfirmText(getResourcesString(R.string.confirm));
customize.setConfirmTextColor(getResourcesColor(R.color.colorPrimary));

customize.setLocationCenterIcon(R.mipmap.location);

customize.setLocationBackIcon(R.mipmap.location_back);

customize.setRvSelectIcon(R.mipmap.select);

customize.setSearchBackIcon(R.mipmap.back);
customize.setSearchInputHint("请输入地址");

//传入对象对可重新更改ui界面
intent.putExtra(ActivityMapMainIndex.KEY_CUSTOMIZE_UI, customize);

startActivityForResult(intent, ActivityMapMainIndex.KEY_REQUEST_CODE);
```

### 接收结果
```
if(requestCode == ActivityMapMainIndex.KEY_REQUEST_CODE && resultCode == ActivityMapMainIndex.KEY_RESULT_CODE_SUCCESS){
    AddressDetail ad = data.getParcelableExtra(ActivityMapMainIndex.KEY_RESULT_DATA);
    tvResult.setText(ad.getDetail());
}
```

### 切换地图，根据需要引入地图、删除不需要的 lib
找到 map.main 项目下的 ActivityMapMainIndex
```
//腾讯地图
fmMain = new FragmentTencentSelectAdr();
//高德地图
fmMain = new FragmentGaoDeSelectAdr();
//百度地图
fmMain = new FragmentBaiDuSelectAdr();
```

### 切换地址搜索
找到 map.main 项目下的 ActivityMapMainIndex
```
//腾讯搜索
bundle.putInt(ActivityMapMainSearch.DATA_SOURCE_KEY, ActivityMapMainSearch.DATA_SOURCE_TENCENT);
//高德搜索
bundle.putInt(ActivityMapMainSearch.DATA_SOURCE_KEY, ActivityMapMainSearch.DATA_SOURCE_GAO_DE);
//百度搜索
bundle.putInt(ActivityMapMainSearch.DATA_SOURCE_KEY, ActivityMapMainSearch.DATA_SOURCE_BAI_DU);
```

### 如果觉得项目对您有帮助，欢迎star
### 如有问题欢迎 issues
### 同时也欢迎 pr