package com.example.jacobclarkson.define;

/**
 * Created by Jacob Clarkson on 13/01/2016.
 */
public class Definition {
    private String entryNumber;
    private String word;
    private String partOfSpeech;
    private String pronunciation;
    private String definitions;
    private String audioUrl;

    public Definition(String entryNumber, String word, String partOfSpeech, String pronunciation, String definitions, String audioUrl) {
        this.entryNumber = entryNumber;
        this.word = word;
        this.partOfSpeech = partOfSpeech;
        this.pronunciation = pronunciation;
        this.definitions = definitions;
        this.audioUrl = audioUrl;
    }

    public String getEntryNumber() {
        return entryNumber;
    }

    public String getWord() {
        return word;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getDefinitions() {
        return definitions;
    }

    public String getAudioUrl()  {
        return audioUrl;
    }

    public boolean hasAudio() {
        if(audioUrl.equals(""))
            return false;
        else
            return true;
    }
}
