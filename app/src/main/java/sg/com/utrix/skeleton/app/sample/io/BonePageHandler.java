package sg.com.utrix.skeleton.app.sample.io;

import android.content.Context;
import android.net.Uri;
import android.content.ContentValues;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.IOException;

import sg.com.utrix.skeleton.app.sample.http.ResponseHandler;
import sg.com.utrix.skeleton.app.sample.http.RESTfulContentProvider;
import sg.com.utrix.skeleton.app.sample.provider.SkeletonContract.*;
import sg.com.utrix.skeleton.app.sample.ui.PlaceholderFragment;

import static sg.com.utrix.skeleton.app.sample.util.LogUtils.LOGE;
import static sg.com.utrix.skeleton.app.sample.util.LogUtils.makeLogTag;

/**
 * Created by HUANG on 15/7/2014.
 */
public class BonePageHandler implements ResponseHandler {
    private static final String TAG = makeLogTag(BonePageHandler.class);

    private RESTfulContentProvider mContentProvider;
    private String mQueryText;

    public BonePageHandler(RESTfulContentProvider restfulProvider, String queryText)
    {
        mContentProvider = restfulProvider;
        mQueryText = queryText;
    }
    @Override
    public void handleResponse(HttpResponse response, Uri uri) throws IOException {
        ContentValues contentValues = new ContentValues();
        String returnJSON = EntityUtils.toString(response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();

        if (statusCode == 200) {
            try {
                JSONObject boneWrapper = (JSONObject) new JSONTokener(returnJSON).nextValue();
                String bonePage = boneWrapper.getString("Page");
                JSONArray boneArray = boneWrapper.getJSONArray("Result");

                for (int i = 0; i < boneArray.length(); i++) {
                    JSONObject boneObject = boneArray.getJSONObject(i);
                    contentValues.put(Bones.BONE_ID, boneObject.getInt(Bones.BONE_ID));
                    contentValues.put(Bones.BONE_TITLE, boneObject.getString(Bones.BONE_TITLE));
                    contentValues.put(Bones.BONE_START, boneObject.getString(Bones.BONE_START));
                    contentValues.put(Bones.BONE_URL, boneObject.getString(Bones.BONE_URL));

                    //LOGI(TAG, boneObject.getString(Bones.BONE_URL));
                    Uri newUri = Bones.CONTENT_URI.buildUpon().appendPath("page").appendPath(bonePage).build();

                    Uri providerUri = mContentProvider.insert(newUri, contentValues);
                }

            } catch (JSONException e) {
                LOGE(TAG, "Failed to parse JSON.", e);
            }
        }
        PlaceholderFragment.mSwipeView.setRefreshing(false);
    }
}
