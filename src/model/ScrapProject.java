package model;

import java.util.List;

public class ScrapProject {
   private String name;

   private String startDate;
   private String endDate;
   private String description;
   List<String> teamMembers;
   private String projectUrl;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getStartDate() {
      return startDate;
   }

   public void setStartDate(String startDate) {
      this.startDate = startDate;
   }

   public String getEndDate() {
      return endDate;
   }

   public void setEndDate(String endDate) {
      this.endDate = endDate;
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

   public String getProjectUrl() {
      return projectUrl;
   }

   public void setProjectUrl(String projectUrl) {
      this.projectUrl = projectUrl;
   }

}
