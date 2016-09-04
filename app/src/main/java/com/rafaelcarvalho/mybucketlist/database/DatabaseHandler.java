package com.rafaelcarvalho.mybucketlist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rafaelcarvalho.mybucketlist.model.Book;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.model.BucketListMediaItem;
import com.rafaelcarvalho.mybucketlist.model.Movie;
import com.rafaelcarvalho.mybucketlist.model.Series;
import com.rafaelcarvalho.mybucketlist.util.AppResources;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Rafael on 08/11/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler sDatabaseHandler;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_bucket_list";
    private static final String TABLE_ITEMS = "list_items";

    private static final String KEY_TITLE = "title";
    private static final String KEY_ID = "id";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_RATING = "rating";
    private static final String KEY_COVER = "cover";
    private static final String KEY_SEEN = "seen";
    private static final String KEY_YEAR = "year";
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_TYPE = "type";


    public static synchronized DatabaseHandler getDatabaseReference(Context context) {
        // Use the application context, which will ensure that we don't leak an Activity context
        if (sDatabaseHandler == null) {
            sDatabaseHandler = new DatabaseHandler(context.getApplicationContext());
        }
        return sDatabaseHandler;
    }

    // Prevents public instantiation. Use singleton.
    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO: Test this Database. Goodnight!
        String QUERY_CREATE_ITEMS_TABLE =
                "CREATE TABLE " + TABLE_ITEMS + "("
                        + KEY_ID + " TEXT PRIMARY KEY,"
                        + KEY_TITLE + " TEXT,"
                        + KEY_COVER + " TEXT,"
                        + KEY_DESCRIPTION + " TEXT,"
                        + KEY_RATING + " REAL,"
                        + KEY_YEAR + " TEXT,"
                        + KEY_AUTHOR + " TEXT, "
                        + KEY_TYPE + " INTEGER, "
                        + KEY_SEEN + " INTEGER" + ")";
        db.execSQL(QUERY_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_ITEMS);
    }

    public void addItem(BucketListItem item){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, item.getId());
        values.put(KEY_TITLE, item.getTitle());
        values.put(KEY_COVER, item.getCover());
        values.put(KEY_DESCRIPTION, item.getDescription());
        values.put(KEY_RATING, item.getRating());
        if(item instanceof BucketListMediaItem)
            values.put(KEY_YEAR, ((BucketListMediaItem)item).getYear());
        if(item instanceof Book) {
            values.put(KEY_AUTHOR, ((Book) item).getAuthor());
            values.put(KEY_TYPE, BucketListItemType.BOOKS.ordinal());
        }
        if(item instanceof Movie){
            values.put(KEY_TYPE, BucketListItemType.MOVIES.ordinal());
        }else if(item instanceof Series){
            values.put(KEY_TYPE, BucketListItemType.SERIES.ordinal());
        }

        values.put(KEY_SEEN, item.isSeen());

        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    public HashMap<BucketListItemType, List<BucketListItem>> getAll() {
        HashMap<BucketListItemType, List<BucketListItem>> itemMap = AppResources.getItemMap();

        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                BucketListItemType type = BucketListItemType.values()[cursor.getInt(7)];

                switch (type){
                    case MOVIES:
                        Movie movie = new Movie();
                        movie.setId(cursor.getString(0));
                        movie.setTitle(cursor.getString(1));
                        movie.setCover(cursor.getString(2));
                        movie.setDescription(cursor.getString(3));
                        movie.setRating(cursor.getFloat(4));
                        movie.setSeen(cursor.getInt(8) > 0);
                        movie.setYear(cursor.getString(5));
                        addToMap(itemMap, type,movie);
                        break;
                    case SERIES:
                        Series series = new Series();
                        series.setId(cursor.getString(0));
                        series.setTitle(cursor.getString(1));
                        series.setCover(cursor.getString(2));
                        series.setDescription(cursor.getString(3));
                        series.setRating(cursor.getFloat(4));
                        series.setSeen(cursor.getInt(8) > 0);
                        series.setYear(cursor.getString(5));
                        addToMap(itemMap, type, series);
                        break;
                    case BOOKS:
                        Book book = new Book();
                        book.setId(cursor.getString(0));
                        book.setTitle(cursor.getString(1));
                        book.setCover(cursor.getString(2));
                        book.setDescription(cursor.getString(3));
                        book.setRating(cursor.getFloat(4));
                        book.setSeen(cursor.getInt(8) > 0);
                        book.setAuthor(cursor.getString(6));
                        addToMap(itemMap, type, book);
                        break;
                }
            }while(cursor.moveToNext());
        }
        cursor.close();
        return itemMap;
    }


    public List<BucketListItem> getAllFromType(BucketListItemType type){

        List<BucketListItem> items = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + KEY_TYPE + " = " + type.ordinal();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //run through the cursor to get the items

        switch (type)
        {
            case MOVIES:
                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    Movie movie = new Movie();
                    movie.setId(cursor.getString(0));
                    movie.setTitle(cursor.getString(1));
                    movie.setCover(cursor.getString(2));
                    movie.setDescription(cursor.getString(3));
                    movie.setRating(cursor.getFloat(4));
                    movie.setSeen(cursor.getInt(8) > 0);
                    movie.setYear(cursor.getString(5));
                    items.add(movie);
                }
                break;
            case SERIES:
                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    Series series = new Series();
                    series.setId(cursor.getString(0));
                    series.setTitle(cursor.getString(1));
                    series.setCover(cursor.getString(2));
                    series.setDescription(cursor.getString(3));
                    series.setRating(cursor.getFloat(4));
                    series.setSeen(cursor.getInt(8) > 0);
                    series.setYear(cursor.getString(5));
                    items.add(series);
                }
                break;
            case BOOKS:
                for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    Book book = new Book();
                    book.setId(cursor.getString(0));
                    book.setTitle(cursor.getString(1));
                    book.setCover(cursor.getString(2));
                    book.setDescription(cursor.getString(3));
                    book.setRating(cursor.getFloat(4));
                    book.setSeen(cursor.getInt(8) > 0);
                    book.setAuthor(cursor.getString(6));
                    items.add(book);
                }
                break;
        }
        cursor.close();
        Log.d("DATABASE", "REACHED DATABASE");
        return items;
    }



    private void addToMap(HashMap<BucketListItemType, List<BucketListItem>> hashMap,
                                  BucketListItemType type, BucketListItem item){
            List<BucketListItem> list = hashMap.get(type);
            if(list == null){
                list = new LinkedList<>();
                hashMap.put(type,list);
            }
            list.add(item);

    }

    public void remove(BucketListItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, KEY_ID + " = ?",
                new String[] { String.valueOf(item.getId()) });
        db.close();
    }
}
