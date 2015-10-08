package com.thunsaker.rapido;

import com.twitter.Extractor;

import java.util.List;

public class TwitterTextUtils {

    public static List<String> GetLinksInText(String textToCheck) {
        Extractor twitterExtractor = new Extractor();
        List<String> myLinks = twitterExtractor.extractURLs(textToCheck);

        if(myLinks != null && !myLinks.isEmpty()) {
            return myLinks;
        }

        return null;
    }

    public static List<String> GetHashtagsInText(String textToCheck) {
        Extractor twitterExtractor = new Extractor();
        List<String> myHashtags = twitterExtractor.extractHashtags(textToCheck);

        if(myHashtags != null && !myHashtags.isEmpty()) {
            return myHashtags;
        }

        return null;
    }
}
