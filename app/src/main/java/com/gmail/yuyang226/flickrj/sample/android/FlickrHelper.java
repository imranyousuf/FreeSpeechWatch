package com.gmail.yuyang226.flickrj.sample.android;

import javax.xml.parsers.ParserConfigurationException;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.REST;
import com.googlecode.flickrjandroid.RequestContext;
import com.googlecode.flickrjandroid.interestingness.InterestingnessInterface;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

public final class FlickrHelper {

	private static FlickrHelper instance = null;
    public static final String API_KEY = "d74f5a4a5db17e60f326e8666a4dd518";
    private static final String API_SEC = "d83edbd6a74059ca";


    public static FlickrHelper getInstance()
    {
        if (instance == null)
            instance = new FlickrHelper();
        return instance;
    }

    public Flickr getFlickr()
    {
        try
        {
            Flickr localFlickr = new Flickr("d74f5a4a5db17e60f326e8666a4dd518", "d83edbd6a74059ca", new REST());
            return localFlickr;
        }
        catch (ParserConfigurationException localParserConfigurationException)
        {
        }
        return null;
    }

    public Flickr getFlickrAuthed(String paramString1, String paramString2)
    {
        Flickr localFlickr = getFlickr();
        RequestContext localRequestContext = RequestContext.getRequestContext();
        OAuth localOAuth = new OAuth();
        localOAuth.setToken(new OAuthToken(paramString1, paramString2));
        localRequestContext.setOAuth(localOAuth);
        return localFlickr;
    }

    public InterestingnessInterface getInterestingInterface()
    {
        Flickr localFlickr = getFlickr();
        if (localFlickr != null)
            return localFlickr.getInterestingnessInterface();
        return null;
    }

    public PhotosInterface getPhotosInterface()
    {
        Flickr localFlickr = getFlickr();
        if (localFlickr != null)
            return localFlickr.getPhotosInterface();
        return null;
    }
}