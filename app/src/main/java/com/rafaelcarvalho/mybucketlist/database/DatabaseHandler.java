package com.rafaelcarvalho.mybucketlist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.rafaelcarvalho.mybucketlist.Interfaces.IDatabaseHandler;
import com.rafaelcarvalho.mybucketlist.model.Book;
import com.rafaelcarvalho.mybucketlist.model.BucketListItem;
import com.rafaelcarvalho.mybucketlist.model.BucketListMediaItem;
import com.rafaelcarvalho.mybucketlist.model.Movie;
import com.rafaelcarvalho.mybucketlist.model.Series;
import com.rafaelcarvalho.mybucketlist.util.AppResources;
import com.rafaelcarvalho.mybucketlist.util.BucketListItemType;
import com.rafaelcarvalho.mybucketlist.util.Modification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.rafaelcarvalho.mybucketlist.util.Modification.Type.UPDATE;

/**
 * Created by Rafael on 08/11/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper  implements IDatabaseHandler {

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
        Log.d("DATABASE", "ADDED " + item.getTitle());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, item.getId());
        values.put(KEY_TITLE, item.getTitle());
        values.put(KEY_COVER, item.getCover());
        values.put(KEY_DESCRIPTION, item.getDescription());
        values.put(KEY_RATING, item.getRating());
        if(item instanceof BucketListMediaItem) {
            values.put(KEY_YEAR, ((BucketListMediaItem) item).getYear());
            values.put(KEY_TYPE, -1);
        }
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

    public ArrayList<String> getAllIds()
    {
        ArrayList<String> ids = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_ITEMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            ids.add(cursor.getString(0));
        }

        cursor.close();
        db.close();
        return ids;
    }




    public List<BucketListItem> getAllFromType(BucketListItemType type){

        List<BucketListItem> items = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + KEY_TYPE + " = " + type.ordinal();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //run through the cursor to get the items
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            items.add(cursorToItem(type, cursor));
        }
        cursor.close();
        db.close();
        Log.d("DATABASE", "REACHED DATABASE");
        return items;
    }

    public List<BucketListItem> getAllFromTypeAndSeen(BucketListItemType type, boolean isSeen){

        List<BucketListItem> items = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + KEY_TYPE + " = " + type.ordinal()
                                                + " AND " + KEY_SEEN + " = " + (isSeen?1:0);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //run through the cursor to get the items
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            items.add(cursorToItem(type, cursor));
        }
        cursor.close();
        db.close();
        Log.d("DATABASE", "REACHED DATABASE");
        return items;
    }

    private BucketListItem cursorToItem(BucketListItemType type, Cursor cursor) {
        switch (type)
        {
            case MOVIES:
                    Movie movie = new Movie();
                    movie.setId(cursor.getString(0));
                    movie.setTitle(cursor.getString(1));
                    movie.setCover(cursor.getString(2));
                    movie.setDescription(cursor.getString(3));
                    movie.setRating(cursor.getFloat(4));
                    movie.setSeen(cursor.getInt(8) > 0);
                    movie.setYear(cursor.getString(5));

                return movie;
            case SERIES:
                    Series series = new Series();
                    series.setId(cursor.getString(0));
                    series.setTitle(cursor.getString(1));
                    series.setCover(cursor.getString(2));
                    series.setDescription(cursor.getString(3));
                    series.setRating(cursor.getFloat(4));
                    series.setSeen(cursor.getInt(8) > 0);
                    series.setYear(cursor.getString(5));
                return series;
            case BOOKS:
                    Book book = new Book();
                    book.setId(cursor.getString(0));
                    book.setTitle(cursor.getString(1));
                    book.setCover(cursor.getString(2));
                    book.setDescription(cursor.getString(3));
                    book.setRating(cursor.getFloat(4));
                    book.setSeen(cursor.getInt(8) > 0);
                    book.setAuthor(cursor.getString(6));
                return book;
            default:
                return null;
        }
    }


    public BucketListItem remove(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        BucketListItem item = getItemById(db, id);
        db.delete(TABLE_ITEMS, KEY_ID + " = ?",
                new String[] { id });
        db.close();

        return item;
    }
    public BucketListItem getItemById(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        BucketListItem item = getItemById(db,id);
        db.close();
        return item;
    }

    private BucketListItem getItemById(SQLiteDatabase db, String id){
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + KEY_ID + " = '" + id + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            return cursorToItem(BucketListItemType.values()[cursor.getInt(7)],cursor);
        }else{
            return null;
        }

    }

    public void applyChanges(List<Modification> modifications)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        for (Modification mod :modifications)
        {
            ContentValues values = new ContentValues();

            switch (mod.getType())
            {
                case UPDATE:
                    //Switch because we might want to update other fields in the future

                    switch (mod.getField())
                    {
                        case SEEN:
                            values.put(KEY_SEEN, ((Boolean) mod.getNewValue())?1:0); //key seen is integer
                            break;
                        default:
                    }
                    db.update(TABLE_ITEMS,values, "" + KEY_ID+"= ?", new String[]{mod.getId()});

                    break;
                case CREATE:
                    //TODO:
                    break;
                case REMOVE:
                    //TODO:
                    break;
                default:
            }
        }


        db.close();

    }

    public HashMap<BucketListItemType, List<BucketListItem>> getAll() {
        return null;
    }
}
