package sg.com.utrix.skeleton.app.sample.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;

import sg.com.utrix.skeleton.app.sample.provider.SkeletonContract.*;
import sg.com.utrix.skeleton.app.sample.provider.SkeletonDatabase.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import sg.com.utrix.skeleton.app.sample.http.RESTfulContentProvider;
import sg.com.utrix.skeleton.app.sample.http.ResponseHandler;
import sg.com.utrix.skeleton.app.sample.io.*;

import static sg.com.utrix.skeleton.app.sample.util.LogUtils.*;
import static sg.com.utrix.skeleton.app.sample.Config.*;

/**
 * SkeletonProvider inherits from RESTfulContentProvider which provides methods to API.
 * RESTfulContentProvider in turns inherits from the orginal ContentProvider
 */
public class SkeletonProvider extends RESTfulContentProvider {
    private static final String TAG = makeLogTag(SkeletonProvider.class);

    private SkeletonDatabase mOpenHelper;
    private SQLiteDatabase mDb;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //Add here
    private static final int RETURN_ROW = 1;
    private static final int BONES = 100;
    private static final int BONES_PAGE = 101;
    private static final int BONES_ID = 102;

    //Add here
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SkeletonContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "bones", BONES);
        matcher.addURI(authority, "bones/page/*", BONES_PAGE);
        matcher.addURI(authority, "bones/*", BONES_ID);


        return matcher;
    }

    Uri.Builder builder = new Uri.Builder();

    @Override
    public boolean onCreate() {
        mOpenHelper = new SkeletonDatabase(getContext());
        return true;
    }

    @Override
    public SQLiteDatabase getDatabase() {
        return mDb;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        //Add here
        switch (match) {
            case BONES:
                return Bones.CONTENT_TYPE;
            case BONES_PAGE:
                return Bones.CONTENT_TYPE;
            case BONES_ID:
                return Bones.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        long rowId;

        switch (match) {
            case BONES_PAGE:
                rowId = db.insertWithOnConflict(Tables.BONES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                break;

            default:
                throw new IllegalArgumentException("unsupported uri in insert");

        }

        //Notify the uri when a record is added
        if (rowId > 0) {
            notifyChange(uri);
            return uri;
        } else {
            return null;
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        LOGI(TAG, "query(uri=" + uri + ")");

        /*
         * Gets a writeable database. This will trigger its creation if it doesn't already exist.
         */
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);


        Cursor mCursor;
        //Add here
        switch (match) {

            case BONES_PAGE:
                long pageID = ContentUris.parseId(uri);

                    mCursor = db.query(Tables.BONES, projection, null, null, null, null, sortOrder);
                    mCursor.setNotificationUri(getContext().getContentResolver(), uri);

                    //Connect to API, trigger newResponseHandler when request is completed
                    asyncQueryRequest("",
                            BASE_URL + "/page/" + pageID,
                            String.valueOf(BONES_PAGE));
                    return mCursor;

            default:
                throw new IllegalArgumentException("unsupported uri ");
        }
    }

    @Override
    protected ResponseHandler newResponseHandler(String requestTag) {
        LOGI(TAG, requestTag);
        int requestTagInt = Integer.parseInt(requestTag); //got to cast this to int because java does not allow string for switch case

        //Route to the respective handler
        switch (requestTagInt){
            case BONES_PAGE:
                return new BonePageHandler(this, requestTag);
            default:
                throw new IllegalArgumentException("unsupported tag ");
        }

    }

    private void notifyChange(Uri uri) {
        Context context = getContext();
        context.getContentResolver().notifyChange(uri, null, false);
        // Widgets can't register content observers so we refresh widgets separately.
        //context.sendBroadcast(ScheduleWidgetProvider.getRefreshBroadcastIntent(context, false));
    }

}
