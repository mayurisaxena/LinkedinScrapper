package service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Random;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;




public class Client {
   

   private static final org.apache.log4j.Logger logger = Logger.getLogger(Client.class);
   public static final String PROXY_HOST = ""; // MENTION YOUR PROXY HOST HERE
   public static final int PROXY_PORT = 8123;
   public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
   public String sessionId = Integer.toString(new Random().nextInt(Integer.MAX_VALUE));
   public CloseableHttpClient client;

   public Client(String country) {
      //String login = PROXY_USERNAME + (country != null ? "-country-" + country : "") + "-session-" + sessionId;
      
      
      //CredentialsProvider credProvider = new BasicCredentialsProvider();
      //credProvider.setCredentials(new AuthScope(proxy), new UsernamePasswordCredentials(login, PROXY_PASSWORD));
      logger.info("Trying to establish connection");
      HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
      RequestConfig config = RequestConfig.custom()
            .setConnectTimeout(10 * 1000)
            .setConnectionRequestTimeout(10 * 1000)
            .setSocketTimeout(10 * 1000)
            .setProxy(proxy)
            .build();
      SocketConfig sc = SocketConfig.custom()
            .setSoTimeout(10 * 1000)
            .build();
      client = HttpClientBuilder.create()
            .setDefaultRequestConfig(config)
            .setDefaultSocketConfig(sc).build();
            
            /*HttpClients.custom().setConnectionManager(new BasicHttpClientConnectionManager()).setDefaultRequestConfig(config).setProxy(proxy)
            //.setDefaultCredentialsProvider(credProvider).build();
            .build();*/
      logger.info("Connection done.");
   }

   public String request(String url) throws IOException, InterruptedException {
      CloseableHttpResponse response = null;
      try {
         HttpGet request = new HttpGet(url);
         HttpHost target = new HttpHost("in.linkedin.com",443,"https");
         
         
         request.setHeader("User-Agent", USER_AGENT);
         logger.info("Going to execute http method");
         
         response = client.execute(target,request);
         logger.info("http method execution ends");
         
         return EntityUtils.toString(response.getEntity());
      }  finally {
         try {
            response.close(); 
         } catch (Exception e) {
            logger.error("In finally of request for url : "+url,e);
         }
      }
   }

   public void close() throws IOException {
      client.close();
   }

   public static Document linkedInProxyCall(String url) throws Exception {
      Client client = new Client(null);
      try{
      if (null !=  client.client){
         logger.info("client object established:"+ client.client.toString());
         String doc = client.request(url);
         logger.info("doc retrived from LinkedIn:"+ url);
         return Jsoup.parse(doc);
      }
      else { 
         logger.info("In Client Linkedin call blocked for url " + url);
         Thread.sleep(30000);
         return linkedInProxyCall(url);
      }
      } 
      catch (SocketTimeoutException e) {
         logger.error("In client request call for url :" +url,e);
         Thread.sleep(3000);
         return linkedInProxyCall(url);
      } catch (ConnectTimeoutException e) {
         logger.error("In client request call for url :" +url,e);
         Thread.sleep(3000);
         return linkedInProxyCall(url);
      } 
      finally{
         try {
         client.close();
         } catch (Exception e) {
            logger.error("In finally of linkedinProxyCall for url : "+url,e);
         }
      }
   }
}
