package cm.android.common.http;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import android.content.Context;

import java.lang.ref.WeakReference;

public class MyHttp {

    private final WeakReference<Context> context;

    // private Context context;

    public MyHttp(Context context) {
        // this.context = context;
        this.context = new WeakReference<Context>(context);
    }

    public void cancel() {
        HttpUtil.cancel(context.get());
    }

    public <T> void exec(String url, RequestParams params,
            HttpListener<T> httpHandler) {
        exec(url, null, params, httpHandler);
    }

    public <T> void exec(String url, Header[] headers, RequestParams params,
            HttpListener<T> httpListener) {
        HttpUtil.exec(context.get(), url, headers, params, httpListener);
    }

    public void exec(String url, Header[] headers, RequestParams params,
            AsyncHttpResponseHandler responseHandler) {
        HttpUtil.exec(context.get(), url, headers, params, responseHandler);
    }

    public <T> void exec(String url, byte[] data, HttpListener<T> httpListener) {
        exec(url, null, data, httpListener);
    }

    public <T> void exec(String url, Header[] header, byte[] data, HttpListener<T> httpListener) {
        HttpUtil.exec(context.get(), url, header, data, httpListener);
    }

    public void exec(String url, Header[] header, byte[] data,
            AsyncHttpResponseHandler responseHandler) {
        HttpUtil.exec(context.get(), url, header, data, responseHandler);
    }
}
