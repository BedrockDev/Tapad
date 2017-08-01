package com.bedrock.padder.model.about;

import android.content.Context;

import static com.bedrock.padder.helper.WindowHelper.getStringFromId;

public class Detail {

    private String title;

    private Item[] items;

    public Detail (String title, Item[] items) {
        this.title = title;
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public String getTitle(Context context) {
        String res = getStringFromId(title, context);
        if (res != null) {
            return res;
        } else {
            return title;
        }
    }

    public String getTitle(String prefix, Context context) {
        String res = getStringFromId(prefix, context);
        if (res != null) {
            return res + " " + title;
        } else {
            return title;
        }
    }

    public Item[] getItems() {
        return items;
    }
}
