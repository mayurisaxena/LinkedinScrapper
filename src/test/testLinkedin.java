package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import model.ScrapCertificate;
import model.ScrapCourse;
import model.ScrapEducation;
import model.ScrapExperience;
import model.ScrapLanguage;
import model.ScrapProfile;
import model.ScrapProject;
import model.ScrapPublication;
import service.Client;
import service.LinkedinScrape;

public class testLinkedin {

   private static final org.apache.log4j.Logger logger = Logger.getLogger(testLinkedin.class);

   
   public static void main(String args[]) throws Exception {
      ScrapProfile profile = new ScrapProfile();
      profile.setPublicProfileUrl("https://www.linkedin.com/pub/varun-malhotra/71/a14/131");
      logger.info(profile.getPublicProfileUrl());
     /* File file = new File("E:\\Work Data\\linkedin scraper\\Linkedin docs\\AdarshPrasad.txt");
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;
      StringBuffer sb = new StringBuffer(); 
      while((line=br.readLine())!=null) {
         sb.append(line);
         sb.append("\n");
      }*/
      profile = getLinkedInProfile(profile);
      
      int liConn = (profile.getLinkedinConnection() != null && !profile.getLinkedinConnection().equals(""))
            ? Integer.parseInt(profile.getLinkedinConnection().split("\\+")[0].trim()) : 0;
      System.out.println(liConn);
      LinkedinScrape.writeProfileToFile(profile);
   }

  

   public static ScrapProfile getLinkedInProfile(ScrapProfile profile) {

      try {
         logger.info("In getLinkedInProfile for url: " + profile.getPublicProfileUrl());
        
         Document doc = null;
         if (profile.getPublicProfileUrl().contains("?")) {
            String liUrl[] = profile.getPublicProfileUrl().split("\\?");
            doc = Client.linkedInProxyCall(liUrl[0]);
            logger.info("Going to fetch connection to LinkedIn profile with parameters");
         } else {
            logger.info("Going to fetch connection to LinkedIn profile");
            doc = Client.linkedInProxyCall(profile.getPublicProfileUrl());
         }
         String name = doc.select("#name").text();
         if (name != null && !name.equals("")) {
            profile.setProfileFrom("SCRAPPED_ONLY");
         } else {
            if (doc.body().text() != null && !doc.body().text().equals("\"\"") && doc.body().text().length()>1) {
               logger.info("Suspicious profile link found " + profile.getPublicProfileUrl());
               return profile;
            }
            logger.info("Thread sleep for url: " + profile.getPublicProfileUrl());
            Thread.sleep(30000);
            getLinkedInProfile(profile);
         }
         profile.setName(doc.select("#name").text());
         profile.setLocation(doc.select("#demographics .locality").text());
         Elements descriptor = doc.select("#demographics .descriptor");
         if (descriptor.size()>1){
            String industry = descriptor.get(1).text();
            profile.setIndustry(industry);
         }
         profile.setLinkedInPicUrl(doc.select(".profile-picture a img").attr("data-delayed-url").toString());
         profile.setLinkedinConnection(doc.select(".member-connections strong").text());
         
         logger.info("going to scrap skills");
         // Scrape Skills
         Set<String> skills = new HashSet<String>();
         try {
            Elements skillEle = doc.select("#skills li a");
            Iterator<Element> skilIt = skillEle.iterator();
            while (skilIt.hasNext()) {
               skills.add(skilIt.next().text());
            }
         } catch (Exception e) {
            logger.error("Scrape skill failed for " + profile.getPublicProfileUrl(),e);
         }
         profile.setSkills(skills);
         logger.info("Skills Scrapped");
         // Scrape Education
         List<ScrapEducation> educationList = new ArrayList<ScrapEducation>();
         try {
            Elements schools = doc.select("#education .schools .school");
            Iterator<Element> schoolsIt = schools.iterator();
            ScrapEducation education;
            while (schoolsIt.hasNext()) {
               education = new ScrapEducation();
               Elements school = schoolsIt.next().select(".school");
               Iterator<Element> schoolIt = school.iterator();
               while (schoolIt.hasNext()) {
                  Element element = schoolIt.next();
                  education.setName(element.select(".item-title").text());
                  education.setDegree(element.select(".item-subtitle").text());
                  Elements period = element.select(".date-range time");
                  int i = 0;
                  Iterator<Element> periodIt = period.iterator();
                  while (periodIt.hasNext()) {
                     String time = periodIt.next().text();
                     if (i == 0) {
                        education.setFromDate(time == null ? "" : time);
                        i++;
                     } else {
                        education.setEndDate(time == null ? "" : time);
                     }
                  }
                  educationList.add(education);
               }
            }

         } catch (Exception e) {
            logger.error("Scrape education failed for " + profile.getPublicProfileUrl(),e);
         }
         profile.setEducation(educationList);
         logger.info("Education Scrapped");
         // Scrape experience
         List<ScrapExperience> experienceList = new ArrayList<ScrapExperience>();
         try {
            ScrapExperience experience = null;
            Elements experiences = doc.select("#experience .positions .position");
            Iterator<Element> experiencesIt = experiences.iterator();
            while (experiencesIt.hasNext()) {
               experience = new ScrapExperience();
               Elements position = experiencesIt.next().select(".position");
               Iterator<Element> positionIt = position.iterator();
               while (positionIt.hasNext()) {
                  Element element = positionIt.next();
                  experience.setDesignation(element.select(".item-title a").text());
                  String organisationName;
                  if (element.select(".item-subtitle a").hasText()) {
                     organisationName = element.select(".item-subtitle a").text();
                  } else {
                     organisationName = element.select(".item-subtitle").text();
                  }
                  experience.setOrganisationName(organisationName);
                  Elements period = element.select(".date-range time");
                  int i = 0;
                  Iterator<Element> periodIt = period.iterator();
                  while (periodIt.hasNext()) {
                     String time = periodIt.next().text();
                     if (i == 0) {
                        experience.setStartDate(time == null ? "" : time);
                        i++;
                     } else {
                        experience.setEndDate(time == null ? "" : time);
                     }
                  }
                  experience.setDescription(element.select(".description").text());
                  experience.setLocation(element.select(".location").text());
                  experienceList.add(experience);
               }
            }
         } catch (Exception e) {
            logger.error("Scrape experience failed for " + profile.getPublicProfileUrl(),e);
         }
         profile.setExperiences(experienceList);
         logger.info("Experience Scrapped");
         // Scrape group
         Set<String> groupSet = new HashSet<String>();
         try {
            Elements groups = doc.select("#groups .group");
            Iterator<Element> groupsIt = groups.iterator();
            while (groupsIt.hasNext()) {
               groupSet.add(groupsIt.next().text());
            }
         } catch (Exception e) {
            logger.error("Scrape groups failed for " + profile.getPublicProfileUrl(),e);
         }
         profile.setGroups(groupSet);
         logger.info("Groups Scrapped");
         // Scrape Projects
         List<ScrapProject> projectList = new ArrayList<ScrapProject>();
         try {
            ScrapProject project = null;
            Elements projects = doc.select("#projects .project");
            Iterator<Element> projectIt = projects.iterator();
            while (projectIt.hasNext()) {
               project = new ScrapProject();
               Elements projectEle = projectIt.next().select(".project");
               Iterator<Element> projectEleIt = projectEle.iterator();
               while (projectEleIt.hasNext()) {
                  Element ele = projectEleIt.next();
                  String projectName;
                  if (ele.select(".item-title a").hasText()) {
                     projectName = ele.select(".item-title a").text();
                     project.setProjectUrl(ele.select(".item-title a").attr("href"));
                  } else {
                     projectName = ele.select(".item-title").text();
                  }
                  project.setName(projectName);
                  project.setDescription(ele.select(".description").text());

                  Elements period = ele.select(".date-range time");
                  int i = 0;
                  Iterator<Element> periodIt = period.iterator();
                  while (periodIt.hasNext()) {
                     String time = periodIt.next().text();
                     if (i == 0) {
                        project.setStartDate(time == null ? "" : time);
                        i++;
                     } else {
                        project.setEndDate(time == null ? "" : time);
                     }
                  }
                  List<String> teamList = new ArrayList<String>();
                  Elements membersEle = ele.select(".contributors .contributor");
                  Iterator<Element> membersEleIt = membersEle.iterator();
                  while (membersEleIt.hasNext()) {
                     Element memberEle = membersEleIt.next();
                     teamList.add(memberEle.select(".contributor a").hasText()
                           ? memberEle.select(".contributor a").text() : memberEle.select(".contributor").text());
                  }
                  project.setTeamMembers(teamList);
               }

               projectList.add(project);
            }
         } catch (Exception e) {
            logger.error("Scrape projects failed for " + profile.getPublicProfileUrl(),e);
         }
         profile.setProjects(projectList);
         logger.info("Projects Scrapped");
         // Scrape Publications
         List<ScrapPublication> publicationList = new ArrayList<ScrapPublication>();
         try {
            ScrapPublication publication = null;
            Elements publicationsEle = doc.select("#publications .publication");
            Iterator<Element> publicationsEleIt = publicationsEle.iterator();
            while (publicationsEleIt.hasNext()) {
               publication = new ScrapPublication();
               Elements publicationEle = publicationsEleIt.next().select(".publication");
               Iterator<Element> publicationEleIt = publicationEle.iterator();
               while (publicationEleIt.hasNext()) {
                  Element ele = publicationEleIt.next();

                  String title;
                  if (ele.select(".item-title a").hasText()) {
                     title = ele.select(".item-title a").text();
                     publication.setPublicationUrl(ele.select(".item-title a").attr("href"));
                  } else {
                     title = ele.select(".item-title").text();
                  }
                  publication.setTitle(title);
                  publication.setPublicationName(ele.select(".item-subtitle").text());
                  publication.setPublicationDate(ele.select(".date-range time").text());
                  publication.setDescription(ele.select(".description").text());
                  List<String> teamList = new ArrayList<String>();
                  Elements membersEle = ele.select(".contributors .contributor");
                  Iterator<Element> membersEleIt = membersEle.iterator();
                  while (membersEleIt.hasNext()) {
                     Element memberEle = membersEleIt.next();
                     teamList.add(memberEle.select(".contributor a").hasText()
                           ? memberEle.select(".contributor a").text() : memberEle.select(".contributor").text());
                  }
                  publication.setTeamMembers(teamList);
               }
               publicationList.add(publication);
            }
         } catch (Exception e) {
            logger.error("Scrape publication failed for " + profile.getPublicProfileUrl(),e);
         }
         profile.setPublications(publicationList);
         logger.info("Publications Scrapped");
         // Scrape Language
         List<ScrapLanguage> languageList = new ArrayList<ScrapLanguage>();
         try {
            Elements languagesEle = doc.select("#languages .language");
            Iterator<Element> languagesEleIt = languagesEle.iterator();
            ScrapLanguage language = null;
            while (languagesEleIt.hasNext()) {
               language = new ScrapLanguage();
               Elements languageEle = languagesEleIt.next().select(".language");
               Iterator<Element> languageEleIt = languageEle.iterator();
               while (languageEleIt.hasNext()) {
                  Element ele = languageEleIt.next();
                  language.setName(ele.select(".wrap").select(".name").text());
                  language.setProficiency(ele.select(".wrap").select(".proficiency").text());
               }
               languageList.add(language);
            }
         } catch (Exception e) {
            logger.error("Scrape language failed for " + profile.getPublicProfileUrl(),e);
         }
         profile.setLanguages(languageList);
         logger.info("Languages Scrapped");
         // Scrape Certifications
         List<ScrapCertificate> certificateList = new ArrayList<ScrapCertificate>();
         try {
            Elements certificatesEle = doc.select("#certifications .certification");
            Iterator<Element> certificatesEleIt = certificatesEle.iterator();
            ScrapCertificate certificate = null;
            while (certificatesEleIt.hasNext()) {
               certificate = new ScrapCertificate();
               Elements certificateEle = certificatesEleIt.next().select(".certification");
               Iterator<Element> certificateEleIt = certificateEle.iterator();
               while (certificateEleIt.hasNext()) {
                  Element ele = certificateEleIt.next();

                  String certificateName;
                  if (ele.select(".item-title a").hasText()) {
                     certificateName = ele.select(".item-title a").text();
                     certificate.setUrl(ele.select(".item-title a").attr("href"));
                  } else {
                     certificateName = ele.select(".item-title").text();
                  }
                  certificate.setName(certificateName);

                  String authority;
                  if (ele.select(".item-subtitle a").hasText()) {
                     authority = ele.select(".item-subtitle a").text();
                  } else {
                     authority = ele.select(".item-subtitle").text();
                  }
                  certificate.setAuthority(authority);
                  if (authority.contains(",")) {
                     certificate.setLicenseNo(ele.select(".item-subtitle").text());
                  }
                  Elements period = ele.select(".date-range time");
                  int i = 0;
                  Iterator<Element> periodIt = period.iterator();
                  while (periodIt.hasNext()) {
                     String time = periodIt.next().text();
                     if (i == 0) {
                        certificate.setStartDate(time == null ? "" : time);
                        i++;
                     } else {
                        certificate.setEndDate(time == null ? "" : time);
                     }
                  }

               }
               certificateList.add(certificate);
            }
         } catch (Exception e) {
            logger.error("Scrape certificate failed for " + profile.getPublicProfileUrl(),e);
         }
         profile.setCertificates(certificateList);
         logger.info("Certificates Scrapped");
         // Scrape Courses
         List<ScrapCourse> coursesList = new ArrayList<ScrapCourse>();
         try {
            Elements coursesEle = doc.select("#courses ul li");
            Iterator<Element> coursesEleIt = coursesEle.iterator();
            ScrapCourse course = null;
            while (coursesEleIt.hasNext()) {
               course = new ScrapCourse();
               Elements courseEle = coursesEleIt.next().select("li");
               Iterator<Element> courseEleIt = courseEle.iterator();
               while (courseEleIt.hasNext()) {
                  Element courseDesc = courseEleIt.next();
                  if (!courseDesc.select(".item-title").isEmpty()) {
                     course.setOrganisation(courseDesc.select(".item-title").text());
                     Elements subjectList = courseDesc.select(".courses-list li");
                     Iterator<Element> subjectListIt = subjectList.iterator();
                     List<String> courseName = new ArrayList<String>();
                     while (subjectListIt.hasNext()) {
                        Element subjectEle = subjectListIt.next();
                        courseName.add(subjectEle.select(".course").text());
                     }
                     course.setCourseName(courseName);
                  }
               }
               if (course.getCourseName() != null || course.getOrganisation() != null) {
                  coursesList.add(course);
               }
            }
         } catch (Exception e) {
            logger.error("Scrape course failed for " + profile.getPublicProfileUrl(),e);
         }
         profile.setCourses(coursesList);
         logger.info("Courses Scrapped");
      } catch (Exception e) {
         logger.error("In getLinkedInProfile for url : " + profile.getPublicProfileUrl(),e);
      }
      return profile;

   }

 
}
