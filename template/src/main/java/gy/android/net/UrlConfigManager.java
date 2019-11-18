package gy.android.net;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

import gy.android.R;

public class UrlConfigManager {

    private static ArrayList<UrlData> urlList;

    private static void fetchUrlDataFromXml(final Context context) {
        urlList = new ArrayList<>();

        final XmlResourceParser xmlParser = context.getResources().getXml(R.xml.url);

        int eventCode;
        try {
            eventCode = xmlParser.getEventType();
            while (eventCode != XmlPullParser.END_DOCUMENT) {
                switch (eventCode) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("Node".equals(xmlParser.getName())) {
                            final String key = xmlParser.getAttributeValue(null, "Key");
                            final UrlData urlData = new UrlData();
                            urlData.setKey(key);
                            urlData.setExpires(Long.parseLong(xmlParser.getAttributeValue(null, "Expires")));
                            urlData.setNetType(xmlParser.getAttributeValue(null, "NetType"));
                            urlData.setMockClass(xmlParser.getAttributeValue(null, "MockClass"));
                            urlData.setUrl(xmlParser.getAttributeValue(null, "Url"));
                            urlData.setUseMockData(Boolean.parseBoolean(xmlParser.getAttributeValue(null, "UseMockData")));
                            urlList.add(urlData);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventCode = xmlParser.next();
            }
        } catch (final XmlPullParserException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            xmlParser.close();
        }
    }

    public static UrlData findUrlData(final Context context, final String findKey) {
        // 如果urlList还没有数据（第一次），或者被回收了，那么（重新）加载xml
        if (urlList == null || urlList.isEmpty())
            fetchUrlDataFromXml(context);

        for (UrlData data : urlList) {
            if (findKey.equals(data.getKey())) {
                return data;
            }
        }

        return null;
    }
}
