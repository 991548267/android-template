package gy.android.net;

public class UrlData {

    public static final String GET = "get";
    public static final String POST = "post";
    public static final String PUT = "put";
    public static final String DELETE = "delete";

    public static final String POST_IMAGE = "postImage";
    public static final String POST_IMAGES = "postImages";
    public static final String POST_VIDEO = "postVideo";
    public static final String POST_VIDEOS = "postVideos";

    private long expires;
    private String netType;
    private String key;
    private String mockClass;
    private String url;
    private boolean useMockData;

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMockClass() {
        return mockClass;
    }

    public void setMockClass(String mockClass) {
        this.mockClass = mockClass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUseMockData() {
        return useMockData;
    }

    public void setUseMockData(boolean useMockData) {
        this.useMockData = useMockData;
    }
}
