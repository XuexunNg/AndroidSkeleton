package sg.com.utrix.skeleton.app.sample.http;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static sg.com.utrix.skeleton.app.sample.util.LogUtils.LOGD;
import static sg.com.utrix.skeleton.app.sample.util.LogUtils.LOGI;
import static sg.com.utrix.skeleton.app.sample.util.LogUtils.makeLogTag;

/**
 * Encapsulates functions for asynchronous RESTful requests so that subclass
 * content providers can use them for initiating request while still using
 * custom methods for interpreting REST based content such as, RSS, ATOM,
 * JSON, etc.
 */
public abstract class RESTfulContentProvider extends ContentProvider {
    private static final String TAG = makeLogTag(RESTfulContentProvider.class);

    protected FileHandlerFactory mFileHandlerFactory;
    private Map<String, UriRequestTask> mRequestsInProgress =
            new HashMap<String, UriRequestTask>();

    public RESTfulContentProvider() {
    }

    public void setFileHandlerFactory(FileHandlerFactory fileHandlerFactory) {
        mFileHandlerFactory = fileHandlerFactory;
    }

    //public abstract Uri insert(Uri uri, ContentValues cv, SQLiteDatabase db);

    private UriRequestTask getRequestTask(String queryText) {
        return mRequestsInProgress.get(queryText); //1
    }

    /**
     * Allows the subclass to define the database used by a response handler.
     *
     * @return database passed to response handler.
     */
    public abstract SQLiteDatabase getDatabase();

    public void requestComplete(String mQueryText) {
        synchronized (mRequestsInProgress) {
            mRequestsInProgress.remove(mQueryText); //2
        }
    }

    /**
     * Abstract method that allows a subclass to define the type of handler
     * that should be used to parse the response of a given request.
     *
     * @param requestTag unique tag identifying this request.
     * @return The response handler created by a subclass used to parse the
     *         request response.
     */
    protected abstract ResponseHandler newResponseHandler(String requestTag);

    UriRequestTask newQueryTask(String requestTag, String url, String tag) {
        UriRequestTask requestTask;

        final HttpGet get = new HttpGet(url);

        ResponseHandler handler = newResponseHandler(tag);
        requestTask = new UriRequestTask(requestTag, this, get, //3
                handler, getContext());

        mRequestsInProgress.put(tag, requestTask);
        return requestTask;
    }

    UriRequestTask newPostTask(JSONObject json, String url, String tag) throws Exception {
        UriRequestTask requestTask;

        final HttpPost post = new HttpPost(url);

        StringEntity se = new StringEntity(json.toString());
		se.setContentType((Header) new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		post.setEntity(se);


        ResponseHandler handler = newResponseHandler(tag);
        requestTask = new UriRequestTask(tag, this, post,
                handler, getContext());

        mRequestsInProgress.put(tag, requestTask);
        return requestTask;
    }

    

    /**
     * Creates a new worker thread to carry out a RESTful network invocation.
     *
     * @param queryTag unique tag that identifies this request.
     * @param queryUri the complete URI that should be access by this request.
     */
    public void asyncQueryRequest(String queryTag, String queryUri, String tag) {
        synchronized (mRequestsInProgress) {
            UriRequestTask requestTask = getRequestTask(queryTag);
            if (requestTask == null) {
                requestTask = newQueryTask(queryTag, queryUri, tag); //4
                Thread t = new Thread(requestTask);
                // allows other requests to run in parallel.
                t.start();
            }
        }
    }
    
    
    public void asyncPostRequest(JSONObject json, String queryUri, String tag) throws Exception {
        synchronized (mRequestsInProgress) {
            UriRequestTask requestTask = getRequestTask(tag);
            if (requestTask == null) {
                requestTask = newPostTask(json, queryUri, tag); //4
                Thread t = new Thread(requestTask);
                // allows other requests to run in parallel.
                t.start();
            }
        }
    }
    


    /**
     * Spawns a thread to download bytes from a url and store them in a file,
     * such as for storing a thumbnail.
     *
     * @param id the database id used to reference the downloaded url.
     */
    public void cacheUri2File(String id, String url) {
        // use media id as a unique request tag
        final HttpGet get = new HttpGet(url);
        UriRequestTask requestTask = new UriRequestTask(
                get, mFileHandlerFactory.newFileHandler(id),
                getContext(), "");
        Thread t = new Thread(requestTask);
        t.start();
    }

    public void deleteFile(String id) {
        mFileHandlerFactory.delete(id);
    }

    public String getCacheName(String id) {
        return mFileHandlerFactory.getFileName(id);
    }

    public static String encode(String gDataQuery) {
        try {
            return URLEncoder.encode(gDataQuery, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGD(TAG, "could not decode UTF-8," +" this should not happen");
        }
        return null;
    }
}
