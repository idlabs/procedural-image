/**
 * Copyright Ikea Communications AB
 */
package com.ikea.spatiallab.procedural;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import com.google.gson.Gson;
import com.ikea.digitallabs.dela.FileUtils;
import com.ikea.spatiallab.procedural.operators.ProceduralImage;

/**
 *
 */
public class Loader {

    private Loader() {
    }

    /**
     * Loads a procedural asset in json format
     * 
     * @param path
     * @param asset JSON asset
     * @return
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    public static ProceduralImage load(String path, String asset) throws FileNotFoundException,
            UnsupportedEncodingException {
        Gson gson = ProceduralImage.createGson();
        InputStream is = FileUtils.getInstance().getInputStream(path, asset);
        Reader reader = new InputStreamReader(is, "UTF-8");
        if (asset.toLowerCase().endsWith(ProceduralImage.OLLM_EXTENSION)) {
            return gson.fromJson(reader, ProceduralImage.class);
        } else {
            return gson.fromJson(reader, ProceduralImage.class);
        }

    }

}
