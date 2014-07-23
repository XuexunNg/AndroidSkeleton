package sg.com.utrix.skeleton.app.sample.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for interacting with {@link sg.com.utrix.skeleton.app.sample.provider.SkeletonDatabase}. Unless
 * otherwise noted, all time-based fields are milliseconds since epoch and can
 * be compared against {@link System#currentTimeMillis()}.
 * <p>
 * The backing {@link android.content.ContentProvider} assumes that {@link android.net.Uri}
 * are generated using stronger {@link String} identifiers, instead of
 * {@code int} {@link android.provider.BaseColumns#_ID} values, which are prone to shuffle during
 * sync.
 */
public class SkeletonContract {

    /**
     * Special value for {@link SyncColumns#UPDATED} indicating that an entry
     * has never been updated, or doesn't exist yet.
     */
    public static final long UPDATED_NEVER = -2;

    /**
     * Special value for {@link SyncColumns#UPDATED} indicating that the last
     * update time is unknown, usually when inserted from a local file source.
     */
    public static final long UPDATED_UNKNOWN = -1;

    public interface SyncColumns {
        /** Last time this entry was updated or synchronized. */
        String UPDATED = "updated";
    }

    interface BoneColumns {
        /** Unique string identifying this block of record. */
        String BONE_ID = "bone_id";
        /** Title describing this block of time. */
        String BONE_TITLE = "bone_title";
        /** Time when this block starts. */
        String BONE_START = "bone_start";
        String BONE_URL = "url";

    }

    /**
     * Create more interface class for more tables
     */

    public static final String CONTENT_AUTHORITY = "sg.com.utrix.skeleton.app.sample";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String BONES = "bones";

    public static class Bones implements BaseColumns, BoneColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(BONES).build();

        /* For more info, visit http://developer.android.com/guide/topics/providers/content-provider-creating.html#MIMETypes */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.io.bone"; //for multiple items notice the dir
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.io.bone"; //for single item notice the item

        /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = BaseColumns._ID + " ASC, ";

        public static String getBoneId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
