package sg.com.utrix.skeleton.app.sample.provider;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import sg.com.utrix.skeleton.app.sample.provider.SkeletonContract.*;

import static sg.com.utrix.skeleton.app.sample.util.LogUtils.*;

/**
 * Helper for managing {@link android.database.sqlite.SQLiteDatabase} that stores data for
 */
public class SkeletonDatabase extends SQLiteOpenHelper{
    private static final String TAG = makeLogTag(SkeletonDatabase.class);

    private static final String DATABASE_NAME = "skeleton.db";
    private static final int DATABASE_VERSION = 100;

    private final Context mContext;

    interface Tables {
        String BONES = "bones";
    }

    public SkeletonDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.BONES + "("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BoneColumns.BONE_ID + " INTEGER NOT NULL,"
                + BoneColumns.BONE_TITLE + " TEXT NOT NULL,"
                + BoneColumns.BONE_START + " INTEGER NOT NULL,"
                + BoneColumns.BONE_URL + " NULL,"
                + "UNIQUE (" + BoneColumns.BONE_ID + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGD(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.BONES);

    }
}
