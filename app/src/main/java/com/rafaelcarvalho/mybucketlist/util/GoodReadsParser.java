package com.rafaelcarvalho.mybucketlist.util;

import android.util.Xml;

import com.rafaelcarvalho.mybucketlist.gson.SimpleSearchGson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rafael on 24/10/15.
 */
public class GoodReadsParser {

    private static final String ns = null;


    /**
     * The xml goes like this
     *
     * <GoodreadsResponse>
     *     <search>
     *         <results>
     *             <work>
     *                 <best_book>
     *                     <id></id>
     *                     <title></title>
     *                     <image_url></image_url>
     *                 </best_book>
     *             </work>
     *             <work>
     *                 <best_book>
     *                     <id></id>
     *                     <title></title>
     *                     <image_url></image_url>
     *                 </best_book>
     *             </work>
     *         </results>
     *     </search>
     * </GoodreadsResponse>
     *
     *
     * @param in
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public List<SimpleSearchGson.SearchItem> parseList(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }



    private List<SimpleSearchGson.SearchItem> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<SimpleSearchGson.SearchItem> books = new ArrayList<SimpleSearchGson.SearchItem>();

        parser.require(XmlPullParser.START_TAG, ns, "GoodreadsResponse");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("search")) {
                books = readSearch(parser);
            } else {
                skip(parser);
            }
        }
        return books;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
    private List<SimpleSearchGson.SearchItem> readSearch(XmlPullParser parser) throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG,ns, "search");

        List<SimpleSearchGson.SearchItem> books = new ArrayList<SimpleSearchGson.SearchItem>();
        while(parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if(name.equals("results")){
                books = readResults(parser);
            }else{
                skip(parser);
            }
        }

        return books;
    }

    private List<SimpleSearchGson.SearchItem> readResults(XmlPullParser parser) throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG,ns, "results");

        List<SimpleSearchGson.SearchItem> books = new ArrayList<SimpleSearchGson.SearchItem>();
        while(parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            if(name.equals("work")){
                books.add(readWork(parser));
            }else{
                skip(parser);
            }
        }

        return books;
    }

    private SimpleSearchGson.SearchItem readWork(XmlPullParser parser)
            throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG,ns, "work");

        SimpleSearchGson.SearchItem book = null;
        while(parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("best_book")) {
                book = readBestBook(parser);
            } else {
                skip(parser);
            }
        }
        return book;
    }

    private SimpleSearchGson.SearchItem readBestBook(XmlPullParser parser)
            throws XmlPullParserException, IOException{

        parser.require(XmlPullParser.START_TAG, ns, "best_book");
        SimpleSearchGson.SearchItem book = null;

        String title = null;
        String id = null;
        String coverUrl = null;
        String author = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("id")) {
                id = readId(parser);
            } else if (name.equals("image_url")) {
                coverUrl = readImageUrl(parser);
            }else if(name.equals("author")){
                author = readAuthor(parser);
            } else {
                skip(parser);
            }
        }

        //TODO: alter this so we use another class
        book = new SimpleSearchGson.SearchItem(title,coverUrl,id,author);
        return book;
    }

    private String readAuthor(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG,ns, "author");

        String authorName = null;
        while(parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("name")) {
                authorName = readName(parser);
            } else {
                skip(parser);
            }
        }
        return authorName;

    }

    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    private String readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "id");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "id");
        return title;
    }

    private String readImageUrl(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "image_url");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "image_url");
        return title;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

}
