package service;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import model.ScrapProfile;

public class LinkedinURLConnection {

   public static void main(String[] args) {
      String path = args[0];
      int threshold = Integer.parseInt(args[1]);
      File folder = new File(path);
      File[] profiles = folder.listFiles();
      if (profiles!= null) {
         for (File profileStr:profiles) {
            try {
            String linkedInStreamData = FileUtils.readFileToString(profileStr);
            ScrapProfile profile = getObjectFromStream(linkedInStreamData);
            int count = profile.getLinkedinConnection()!=null && profile.getLinkedinConnection().equals("500+") ? 500 : Integer.parseInt(profile.getLinkedinConnection());
            
            if (count > threshold)
               System.out.println(profile.getPublicProfileUrl());
            } catch (Exception e) {
               
            }
         }
      }
     
   }

   private static ScrapProfile getObjectFromStream(String linkedInStreamData) {
      ScrapProfile profile = new Gson().fromJson(linkedInStreamData, ScrapProfile.class);
      return profile;
   }
}
