package com.bedrock.padder.model.about;

import android.content.Context;
import android.graphics.Color;

import com.bedrock.padder.model.preset.Preset;

import static com.bedrock.padder.helper.FileHelper.PROJECT_LOCATION_PRESETS;
import static com.bedrock.padder.helper.WindowHelper.getStringFromIdWithFallback;

public class About {

    private String songName;

    private String songArtist;

    private String presetArtist;

    private String tutorialVideoLink;

    private Boolean isTutorialAvailable;

    private String color; // formatted in #000000

    private Bio bio;

    private Detail[] details;

    public About(String songName,
                 String songArtist,
                 String presetArtist,
                 String tutorialVideoLink,
                 Boolean isTutorialAvailable,
                 String color,
                 Bio bio,
                 Detail[] details) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.presetArtist = presetArtist;
        this.tutorialVideoLink = tutorialVideoLink;
        this.isTutorialAvailable = isTutorialAvailable;
        this.color = color;
        this.bio = bio;
        this.details = details;
    }

    public About(String songName,
                 String songArtist,
                 String presetArtist,
                 String color,
                 Bio bio,
                 Detail[] details) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.presetArtist = presetArtist;
        this.tutorialVideoLink = null;
        this.isTutorialAvailable = false;
        this.color = color;
        this.bio = bio;
        this.details = details;
    }

    public About(String songName,
                 String songArtist,
                 String color,
                 Bio bio,
                 Detail[] details) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.color = color;
        this.bio = bio;
        this.details = details;
    }

    public String getTitle() {
        return songName + " - " + songArtist;
    }

    public String getTitle(Context context) {
        return getSongName(context) + " - " + getSongArtist(context);
    }

    public String getSongName() {
        return songName;
    }

    public String getSongName(Context context) {
        return getStringFromIdWithFallback(getSongName(), context);
    }

    public String getSongArtist() {
        return songArtist;
    }

    public String getSongArtist(Context context) {
        return getStringFromIdWithFallback(getSongArtist(), context);
    }

    public String getImage(Preset preset) {
        String tag = preset.getTag();
        if (presetArtist != null) {
            // normal preset
            return PROJECT_LOCATION_PRESETS + "/" + tag + "/about/album_art";
        } else {
            // in-app about
            return tag;
        }
    }

    public int getColor() {
        try {
            return Color.parseColor(color);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return Color.BLACK;
        }
    }

    public Bio getBio() {
        return bio;
    }

    public Detail[] getDetails() {
        return details;
    }

    public Detail getDetail(Integer index) {
        return details[index];
    }

    public String getTutorialVideoLink() {
        return tutorialVideoLink;
    }

    public String getPresetArtist() {
        return presetArtist;
    }

    public String getPresetArtist(Context context) {
        return getStringFromIdWithFallback(getPresetArtist(), context);
    }

    public Boolean getTutorialAvailable() {
        return isTutorialAvailable;
    }

    public void setBio(Bio bio) {
        this.bio = bio;
    }

    public void setDetails(Detail[] details) {
        this.details = details;
    }
}
