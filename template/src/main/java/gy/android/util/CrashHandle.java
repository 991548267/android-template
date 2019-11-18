package gy.android.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CrashHandle implements Thread.UncaughtExceptionHandler {

    private Context context;

    private static CrashHandle instance = new CrashHandle();

    public static CrashHandle getInstance() {
        if (instance == null) {
            instance = new CrashHandle();
        }
        return instance;
    }

    private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler;

    public void init(Context context) {
        LogUtil.i(CrashHandle.this, "init");
        this.context = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
        mUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        if (mUncaughtExceptionHandler != null && !handleException(e)) {
            mUncaughtExceptionHandler.uncaughtException(t, e);
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleException(Throwable e) {
        if (e == null) {
            return false;
        }
        saveCrash2File(e);
        return true;
    }

    private Map<String, String> info = new HashMap<String, String>();
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);

    private void saveCrash2File(Throwable e) {
        LogUtil.i(CrashHandle.this, "saveCrash2File");
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, String> entry : info.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            buffer.append(key + "=" + value + "\r\n");
        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        e.printStackTrace(pw);
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();
        String result = writer.toString();
        buffer.append(result);
        long timetamp = System.currentTimeMillis();
        String time = format.format(new Date());
        String path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/crash";
        LogUtil.i(CrashHandle.this, "saveCrash2File path:" + path);
        String fileName = "crash-" + time + "-" + timetamp + ".txt";
        LogUtil.i(CrashHandle.this, "saveCrash2File fileName:" + fileName);
        File resultPath = new File(path);
        if (!resultPath.exists()) {
            resultPath.mkdirs();
        }
        File saveFile = new File(resultPath, fileName);
        try {
            FileOutputStream outStream = new FileOutputStream(saveFile);
            outStream.write(buffer.toString().getBytes());
            outStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
