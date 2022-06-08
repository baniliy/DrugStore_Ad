package com.example.drugstore_ad;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WeatherService {
    /*解析xml文件返回天气信息的集合*/
    public static List<WeatherInfo> getInfosFromXML (InputStream is)
            throws Exception {
        /*初始化解析器,第一个参数代表包含xml的数据*/
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "utf-8");
        List<WeatherInfo> weatherInfos = null;
        WeatherInfo weatherInfo = null;
        /*得到当前事件的类型*/
        int type = parser.getEventType();
        /*END_DOCUMENT文档结束标签*/
        while (type != XmlPullParser.END_DOCUMENT) {
            switch (type) {
                /*一个节点的开始标签*/
                case XmlPullParser.START_TAG:
                    //解析到全局开始的标签 infos 根节点
                    if("infos".equals(parser.getName())){
                        weatherInfos = new ArrayList<WeatherInfo>();
                    }else if("city".equals(parser.getName())){
                        weatherInfo = new WeatherInfo();
                        String idStr = parser.getAttributeValue(0);
                        weatherInfo.setId(idStr);
                    }else if("temp".equals(parser.getName())){
                        //parset.nextText()得到该tag节点中的内容
                        String temp = parser.nextText();
                        weatherInfo.setTemp(temp);
                    }else if("weather".equals(parser.getName())){
                        String weather = parser.nextText();
                        weatherInfo.setWeather(weather);
                    }else if("name".equals(parser.getName())){
                        String name = parser.nextText();
                        weatherInfo.setName(name);
                    }else if("pm".equals(parser.getName())){
                        String pm = parser.nextText();
                        weatherInfo.setPm(pm);
                    }else if("wind".equals(parser.getName())){
                        String wind = parser.nextText();
                        weatherInfo.setWind(wind);
                    }
                    break;
                /*一个节点结束的标签*/
                case XmlPullParser.END_TAG:
                    /*一个城市的信息处理完毕，city的结束标签*/
                    if("city".equals(parser.getName())){
                        weatherInfos.add(weatherInfo);
                        weatherInfo = null;
                    }
                    break;
            }
            type = parser.next();
        }
        return weatherInfos;
    }

}