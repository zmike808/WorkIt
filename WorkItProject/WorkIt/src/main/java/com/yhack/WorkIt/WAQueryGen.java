package com.yhack.WorkIt;

import com.wolfram.alpha.*;

import java.util.ArrayList;

/**
 * Created by Michael on 11/9/13.
 */
public class WAQueryGen {
    private WAQueryResult queryResult;
    private static String appid = "3JG8K3-EE3J4WYHLH";
    private ArrayList<WAPlainText> textOutput;
    private ArrayList<WAImage> images;

    public WAQueryGen(String input)
    {
        textOutput = new ArrayList<WAPlainText>();
        images = new ArrayList<WAImage>();
        WAEngine engine = new WAEngine();
        engine.setAsync(1.0);
        // These properties will be set in all the WAQuery objects created from this WAEngine.
        engine.setAppID(appid);
        WAQuery query = engine.createQuery();

        // Set properties of the query.
        query.setInput(input);
        try{
        WAQueryResult queryResult = engine.performQuery(query);
        for(WAPod pod : queryResult.getPods())
        {
            for (WASubpod subpod : pod.getSubpods()) {
                for (Object element : subpod.getContents()) {
                    if(element instanceof WAPlainText){
                        textOutput.add((WAPlainText) element);
                }
                if(element instanceof WAImage)
                {
                    images.add((WAImage) element);
                }
            }
        }

        }
        }
        catch (WAException e)
        {
            e.printStackTrace();
        }
    }
    public ArrayList<String> getAllText()
    {
        ArrayList<String> text = new ArrayList<String>();

        for(WAPlainText txt : textOutput)
        {
            text.add(txt.getText());
        }
        return text;
    }
    public ArrayList<WAImage> getAllImages()
    {
        return images;
    }
}
