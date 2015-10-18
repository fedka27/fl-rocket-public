package wash.rocket.xor.rocketwash.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.Profile;
import wash.rocket.xor.rocketwash.util.Preferences;

public class NavigationMenuContent extends ContentProvider {
    final String LOG_TAG = "NavigationMenuContent";

    // // Константы для БД
    // БД
    static final String DB_NAME = "menudb";
    static final int DB_VERSION = 1;

    // Таблица
    static final String MENU_TABLE = "menu";

    // Поля
    static public final String MENU_ID = "_id";
    static public final String MENU_NAME = "name";
    static public final String MENU_RES_ICON = "used_value";
    static public final String MENU_VALUE = "value";

    // // Uri
    // authority
    static final String AUTHORITY = "xor.recketwash.provider";

    // path
    static final String MENU_PATH = "NavigationMenu";

    // Общий Uri
    public static final Uri MENU_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + MENU_PATH);


    static final String DB_CREATE = "create table " + MENU_TABLE + "("
            + MENU_ID + " integer primary key autoincrement, "
            + MENU_NAME + " text, "
            + MENU_RES_ICON + " integer,"
            + MENU_VALUE + " integer );";

    static final String[] COLUMNS = new String[]{MENU_ID, MENU_VALUE};
    static final String[] COLUMNS_ALL = new String[]{MENU_ID, MENU_NAME, MENU_RES_ICON, MENU_VALUE};

    // набор строк
    static final String MENU_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + MENU_PATH;

    // одна строка
    static final String MENU_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + MENU_PATH;

    //// UriMatcher
    // общий Uri
    static final int URI_MENU = 1;

    // Uri с указанным ID
    static final int URI_MENU_ID = 2;

    // описание и создание UriMatcher
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MENU_PATH, URI_MENU);
        uriMatcher.addURI(AUTHORITY, MENU_PATH + "/#", URI_MENU_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase mDB;

    Preferences prefs;
    Profile profile;

    public boolean onCreate() {
        Log.d(LOG_TAG, "onCreate");
        //dbHelper = new DBHelper(getContext());

        prefs = new Preferences(getContext());
        profile = prefs.getProfile();

        return true;
    }

    @SuppressWarnings("deprecation")
    public synchronized SQLiteDatabase getDatabase(Context context) {
        // Always return the cached database, if we've got one
        if (mDB == null || !mDB.isOpen()) {
            DBHelper helper = new DBHelper(context);
            mDB = helper.getWritableDatabase();
            if (mDB != null) {
                mDB.setLockingEnabled(true);
            }
        }

        return mDB;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(LOG_TAG, "query, " + uri.toString());
        // проверяем Uri
        switch (uriMatcher.match(uri)) {
            case URI_MENU: // общий Uri
                Log.d(LOG_TAG, "URI_MENU");
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = MENU_ID + " ASC";
                }
                break;
            case URI_MENU_ID: // Uri с ID
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_MENU_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = MENU_ID + " = " + id;
                } else {
                    selection = selection + " AND " + MENU_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        SQLiteDatabase db = getDatabase(getContext());
        Cursor cursor = db.query(MENU_TABLE, COLUMNS, selection, selectionArgs, null, null, sortOrder);

        String[] menu_list = getContext().getResources().getStringArray(R.array.menu_list);
        TypedArray icons = getContext().getResources().obtainTypedArray(R.array.menu_icons);
        MatrixCursor menuResCursor = new MatrixCursor(new String[]{MENU_ID, MENU_NAME, MENU_RES_ICON}, 1);

        profile = prefs.getProfile();

        for (int i = 0; i < menu_list.length; i++) {

            if (profile != null && !profile.isPhone_verified()) {
                Log.d("provider", "profile.isPhone_verified() = " + profile.isPhone_verified());
                //if (i != 2 && i != 3)
                if (i != 2 && i != 3 && i != 4 && i != 5 && i != 7) // XXX
                    menuResCursor.addRow(new Object[]{i, "" + menu_list[i], icons.getResourceId(i, -1)});
            } else if (i != 3 && i != 4 && i != 5 && i != 7) //XXX
                menuResCursor.addRow(new Object[]{i, "" + menu_list[i], icons.getResourceId(i, -1)});
        }
        icons.recycle();

        MatrixCursor resCursor = new MatrixCursor(COLUMNS_ALL, 1);

        CursorJoiner joiner = new CursorJoiner(cursor, new String[]{MENU_ID}, menuResCursor, new String[]{MENU_ID});
        for (CursorJoiner.Result joinerResult : joiner) {
            switch (joinerResult) {
                case LEFT:
                    // handle case where a row in cursorA is unique
                    break;
                case RIGHT:
                    // handle case where a row in cursorB is unique
                    break;
                case BOTH:
                    // handle case where a row with the same key is in both cursors

                    resCursor.addRow(new Object[]{
                            cursor.getLong(cursor.getColumnIndex(MENU_ID)),
                            menuResCursor.getString(menuResCursor.getColumnIndex(MENU_NAME)),
                            menuResCursor.getInt(menuResCursor.getColumnIndex(MENU_RES_ICON)),
                            cursor.getInt(cursor.getColumnIndex(MENU_VALUE))
                    });
                    break;
            }
        }

        /*
        // XXX test
        resCursor.addRow(new Object[]{
                1,
                String.valueOf("bla-bla"),
                R.drawable.ic_acievments_black,
                123
        });*/

        menuResCursor.close();
        cursor.close();

        resCursor.setNotificationUri(getContext().getContentResolver(), MENU_CONTENT_URI);
        return resCursor;
    }

    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(LOG_TAG, "insert, " + uri.toString());
        if (uriMatcher.match(uri) != URI_MENU)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        SQLiteDatabase db = getDatabase(getContext());
        long rowID = db.insert(MENU_TABLE, null, values);
        Uri resultUri = ContentUris.withAppendedId(MENU_CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(LOG_TAG, "update, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_MENU:
                Log.d(LOG_TAG, "URI_MENU");

                break;
            case URI_MENU_ID:
                String id = uri.getLastPathSegment();
                Log.d(LOG_TAG, "URI_MENU_ID, " + id);
                if (TextUtils.isEmpty(selection)) {
                    selection = MENU_ID + " = " + id;
                } else {
                    selection = selection + " AND " + MENU_ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        SQLiteDatabase db = getDatabase(getContext());
        int cnt = db.update(MENU_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public String getType(Uri uri) {
        Log.d(LOG_TAG, "getType, " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case URI_MENU:
                return MENU_CONTENT_TYPE;
            case URI_MENU_ID:
                return MENU_CONTENT_ITEM_TYPE;
        }
        return null;
    }


    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            Log.d("DBHelper", "onCreate");
            db.execSQL(DB_CREATE);

            // fill menu;
            String[] menu_list = getContext().getResources().getStringArray(R.array.menu_list);
            TypedArray icons = getContext().getResources().obtainTypedArray(R.array.menu_icons);

            ContentValues cv = new ContentValues();
            for (int i = 0; i < menu_list.length; i++) {
                cv.put(MENU_NAME, menu_list[i]);
                cv.put(MENU_VALUE, 0);
                cv.put(MENU_RES_ICON, icons.getResourceId(i, -1));
                db.insert(MENU_TABLE, null, cv);
            }

            icons.recycle();
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            /*
            switch (newVersion)
            {

            } */
        }
    }
}

