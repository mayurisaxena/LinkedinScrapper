package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class testFolder {
   
   public static void main(String args[]) throws FileNotFoundException, IOException {
      String path = "E:\\ndata\\ScrapProfiles\\";
      DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
      //get current date time with Date()
      Date date = new Date();
      String folderName = dateFormat.format(date);
      File folder = new File(path+folderName);
      if(!folder.exists()) {
         folder.mkdirs();
         path = path + folderName;
      }
      System.out.println(folderName);
   }
   
}
