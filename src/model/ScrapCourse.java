package model;

import java.util.List;

public class ScrapCourse {
   private List<String> courseName;
   private String organisation;
   
   public List<String> getCourseName() {
      return courseName;
   }
   public void setCourseName(List<String> courseName) {
      this.courseName = courseName;
   }
   public String getOrganisation() {
      return organisation;
   }
   public void setOrganisation(String organisation) {
      this.organisation = organisation;
   }
   
}
