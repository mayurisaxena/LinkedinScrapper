package test;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

public class testFile {
   static String path =null;
   static Properties prop;
   static File propertiesFile;
   static File trackUrl;
   public testFile() {
      prop = new Properties();
      InputStream input = null;

      try {

         String filename = "config.properties";
         String propertiesFilePath= "E:\\ndata\\ScrapProfiles\\"+filename;
         propertiesFile = new File(propertiesFilePath);
         input = new FileInputStream(propertiesFile);
         
         prop.load(input);
         path = prop.getProperty("path");
        
         trackUrl = new File(path + "trackUrl.txt");
         prop.setProperty("path", "E:\\");
         prop.store(new FileOutputStream(propertiesFile), null);
         if (!trackUrl.exists()) {
            trackUrl.createNewFile();
         }
      } catch (Exception e) {
         
      }
   }
   public static void main(String args[]) throws FileNotFoundException, IOException {
      testFile obj = new testFile();
     /* String url = "https://in.linkedin.com/directory/people-b-1/";
      
      FileOutputStream fileOut = new FileOutputStream(trackUrl);
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOut));
     
      String charList =  (String) prop.get("chars");
      List<String> chars = 
            new ArrayList<String>(Arrays.asList(charList.split(",")));
      Iterator< String > listItr = chars.iterator(); 
      while (listItr.hasNext()) {
         String link = listItr.next();
         bw.write(link);
         bw.newLine();
      }
      bw.newLine();
      bw.write(url.split("-")[1].substring(0, 1));
      bw.close();
      
      File trackUrlCopy = new File(path + "trackUrlCopy.txt");
      FileOutputStream fileOutCopy = new FileOutputStream(trackUrlCopy);
      BufferedWriter bwCopy = new BufferedWriter(new OutputStreamWriter(fileOutCopy));
      BufferedReader br = new BufferedReader(new FileReader(trackUrl));
      String line;
      while ((line = br.readLine()) != null) {
         if (!line.trim().equals("e")) {
            bwCopy.write(line);
            bwCopy.newLine();
         }
      }
      bwCopy.close();
      br.close();
      trackUrl.delete();
      trackUrlCopy.renameTo(trackUrl);
      br.close();*/
      //checkStringInFile();
      
   }
   
   public static void checkStringInFile() throws IOException {
      Path path1 = Paths.get("E:\\ndata\\ScrapProfiles\\"+"trackUrl.txt");
      try (Stream<String> filteredLines = Files.lines(path1)
            // test if file is closed or not
            .filter(s -> s.contains("https://in.linkedin.com/directory/people-"))) {
         Optional<String> hasUrl = filteredLines.findFirst();
         if (hasUrl.isPresent()) {
            System.out.println(hasUrl.get());
         }
}
   }
   
   
}
