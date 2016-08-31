package model;

import java.util.List;

public class ScrapPublication {
   private String title;
   private String publicationName;
   private String publicationDate;
   private String description;
   List<String> teamMembers;
   private String publicationUrl;

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getPublicationName() {
      return publicationName;
   }

   public void setPublicationName(String publicationName) {
      this.publicationName = publicationName;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public List<String> getTeamMembers() {
      return teamMembers;
   }

   public void setTeamMembers(List<String> teamMembers) {
      this.teamMembers = teamMembers;
   }

   public String getPublicationUrl() {
      return publicationUrl;
   }

   public void setPublicationUrl(String publicationUrl) {
      this.publicationUrl = publicationUrl;
   }

   public String getPublicationDate() {
      return publicationDate;
   }

   public void setPublicationDate(String publicationDate) {
      this.publicationDate = publicationDate;
   }

}
