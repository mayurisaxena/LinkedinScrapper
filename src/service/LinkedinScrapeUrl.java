package service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import model.ScrapProfile;

public class LinkedinScrapeUrl {

   private static final org.apache.log4j.Logger logger = Logger.getLogger(LinkedinScrapeUrl.class);
   static ObjectMapper mapper = new ObjectMapper();
   static File propertiesFile;
   static File readFromFile;
   static String writeToPath;
   static File trackFile;
   static Properties prop = new Properties();
   static String temppath;

   public LinkedinScrapeUrl(String propertiesFilePath) {
      FileInputStream input = null;
      try {
         propertiesFile = new File(propertiesFilePath);
         input = new FileInputStream(propertiesFile);
         prop.load(input);
         readFromFile = new File(prop.getProperty("readFrom"));
         writeToPath = prop.getProperty("writeTo");
         trackFile = new File(prop.getProperty("trackFile"));
         temppath = prop.getProperty("temppath");
         if(!readFromFile.exists()) {
            logger.info("File containing Url does not exists.");
            System.exit(0);
         }
         if (!trackFile.exists()) {
            trackFile.createNewFile();
         }
      } catch (Exception e) {
         logger.error("Files initialisation failed",e);
      } finally {
         if (input != null) {
            try {
               input.close();
            } catch (Exception e) {
               logger.error("Files initialisation failed",e);
            }
         }
      }
   }
   
   public static void main(String args[]) {
      String propertiesFilePath = args[0];
      LinkedinScrapeUrl obj = new LinkedinScrapeUrl(propertiesFilePath);
      readUrlFromFile();
   }
   
   public static void readUrlFromFile() {
      BufferedReader bf = null;
      logger.info(readFromFile);
      try {
         bf = new BufferedReader(new FileReader(readFromFile));
         String line;
         if(bf.ready()) {
            logger.info("Read from file ready.");
         }
         while((line = bf.readLine())!=null){
            String normalisedUrl = getValidLinkedInUrl(line);
            if (!checkUrlALreadyScraped(normalisedUrl)) {
               scrapeUrl(normalisedUrl);
            } else {
               logger.info("This url is already scrapped:"+line);
            }
         }
         logger.info("Read from file finished.");
      } catch (Exception e) {
         logger.error("Failed to read file",e);
      }
   }
   
   public static void scrapeUrl(String normalisedUrl) {
      ScrapProfile profile = new ScrapProfile();
      profile.setPublicProfileUrl(normalisedUrl);
      profile = LinkedinScrape.getLinkedInProfile(profile);
      if (profile.getProfileFrom() != null) {
         logger.info("Profile found for " + profile.getName());
         writeProfileToFile(profile);
      }
   }
   
   private static String getValidLinkedInUrl(String linkedInUrl) {
      return "https://www.linkedin.com" + getUrlPath(linkedInUrl);
  }
   
   public static String getUrlPath(String urlString) {
      URI url = null;
      try {
          url = new URI(urlString);
      } catch (URISyntaxException e) {
          logger.error("Url not proper",e);
      }
      return url.normalize().getPath();
  }
   public static void writeProfileToFile(ScrapProfile profile) {
      String link = profile.getPublicProfileUrl();
      logger.info("In writeProfileToFile for link :" +link);
      String linkedinId = link.substring(link.lastIndexOf("/"), link.length());
      try {
         DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
         Date date = new Date();
         String folderName = dateFormat.format(date);
         String finalPath = writeToPath + folderName;
         File folder = new File(finalPath);
         if (!folder.exists()) {
            folder.mkdirs();
         }
         File oldFile = new File(finalPath + linkedinId + ".json");
         if (oldFile.exists()) {
            File newFile = new File(temppath + linkedinId + ".json");

            mapper.writeValue(newFile, profile);

            if (newFile.length() > oldFile.length()) {
               mapper.writeValue(new File(finalPath + linkedinId + ".json"), profile);
            }
            newFile.delete();
         } else {
            mapper.writeValue(new File(finalPath + linkedinId + ".json"), profile);
         }
         writeScrapedUrl(profile.getPublicProfileUrl());
      } catch (Exception e) {
         logger.error("File write failed for profile : " + linkedinId, e);
      }
   }

   public static void writeScrapedUrl(String link) {
      logger.info("In writeScrapedUrl for url "+ link);
      BufferedWriter bw = null;
      try {
         bw = new BufferedWriter(new FileWriter(trackFile, true));
         bw.append(link);
         bw.newLine();
         
      } catch (IOException e) {
         logger.error("In writeScrapedUrl for url : " + link, e);
      } finally {
         try {
            if (bw != null)
               bw.close();
         } catch (Exception e) {
            logger.error("In finally for writeScrapedUrl for url : " + link, e);
         }
      }
   }
   
   public static boolean checkUrlALreadyScraped(String link) throws IOException {
      logger.info("In checkUrlALreadyScraped for url :"+link);
      Path path1 = Paths.get(trackFile.getAbsolutePath());
      try (Stream<String> filteredLines = Files.lines(path1)
            .filter(s -> s.contains(link))) {
         Optional<String> hasUrl = filteredLines.findFirst();
         if (hasUrl.isPresent()) {
            logger.info("Link already scraped " + link);
            return true;
         } else {
            logger.info("Link not scraped " + link);
            return false;
         }
      }
   }
}
