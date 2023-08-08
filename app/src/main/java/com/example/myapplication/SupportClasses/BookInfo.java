package com.example.myapplication.SupportClasses;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class BookInfo implements Serializable {

    private String id;
    private int shelfID;
    private String title;
    private String subtitle;
    private ArrayList<String> authors;
    private String publisher;
    private String publishedDate;
    private String description;
    private int pageCount;
    private String thumbnail;
    private String previewLink;
    private String infoLink;
    private String buyLink;
    private String webLink;
    private String accessViewStatus;

    // creating getter and setter methods
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
    }

    public String getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(String infoLink) {
        this.infoLink = infoLink;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public String getId() {
        return id;
    }
    public String getWebLink() {
        return webLink;
    }
    public String getaccessViewStatus() {
        return accessViewStatus;
    }
    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }
    public int getShelfID() {
        return shelfID;
    }
    public void setShelfID(int id) {
        shelfID = id;
    }

    public BookInfo(String id, String title, String subtitle, ArrayList<String> authors, String publisher,
                    String publishedDate, String description, int pageCount, String thumbnail,
                    String previewLink, String infoLink, String buyLink) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.pageCount = pageCount;
        this.thumbnail = thumbnail;
        this.previewLink = previewLink;
        this.infoLink = infoLink;
        this.buyLink = buyLink;
        this.shelfID = -1;
    }

    public BookInfo(JSONObject itemsObj) throws JSONException {

            this.id = itemsObj.optString("id");
            this.shelfID = -1;

            JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");
            setBookInfo(volumeObj);

            JSONObject saleInfoObj = itemsObj.optJSONObject("saleInfo");
            setsSaleInfo(saleInfoObj);

            JSONObject accessInfo = itemsObj.optJSONObject("accessInfo");
            setsAccessInfo(accessInfo);

            ArrayList<String> authorsArrayList = new ArrayList<>();
            if (volumeObj.has("authors")) {
                JSONArray authorsArray = volumeObj.getJSONArray("authors");
                authorsArrayList = new ArrayList<>();
                if (authorsArray.length() != 0) {
                    for (int j = 0; j < authorsArray.length(); j++) {
                        authorsArrayList.add(authorsArray.optString(j));
                    }
                }
            }
            this.authors = authorsArrayList;
        }

    private String checkString(String str) {
        return (str != null) ? str : "";
    }

    private String cutString(String text) {
        int startIndex = text.indexOf("hl=");
        return text.substring(0, startIndex);
    }

    private void setBookInfo(JSONObject volumeObj){
        this.title = checkString(volumeObj.optString("title"));
        this.subtitle = checkString(volumeObj.optString("subtitle"));
        this.publisher = checkString(volumeObj.optString("publisher"));
        this.publishedDate = checkString(volumeObj.optString("publishedDate"));
        this.description = checkString(volumeObj.optString("description"));
        this.pageCount = volumeObj.optInt("pageCount");
        JSONObject imageLinks = volumeObj.optJSONObject("imageLinks");
        this.previewLink = checkString(volumeObj.optString("previewLink"));
        this.infoLink = checkString(volumeObj.optString("infoLink"));

        if (imageLinks != null) {
            this.thumbnail = checkString(imageLinks.optString("thumbnail"));
        } else {
            this.thumbnail = null;
        }
    }

    private void setsSaleInfo(JSONObject saleInfoObj) {
        if (saleInfoObj != null)
            this.buyLink = checkString(saleInfoObj.optString("buyLink"));
        else
            this.buyLink = null;
    }

    private void setsAccessInfo(JSONObject accessInfo) {
        if (accessInfo != null) {
            this.webLink = cutString(checkString(accessInfo.optString("webReaderLink")));
            this.accessViewStatus = checkString(accessInfo.optString("accessViewStatus"));
        } else {
            this.webLink = null;
            this.accessViewStatus = null;
        }
    }
}