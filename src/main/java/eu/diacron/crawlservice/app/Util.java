/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.diacron.crawlservice.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author eleni
 */
public class Util {

    public static String getCrawlid(URL urltoCrawl) {
        String crawlid = "";
        System.out.println("start crawling page");

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials("diachron", "7nD9dNGshTtficn"));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            HttpPost httppost = new HttpPost("http://diachron.hanzoarchives.com/crawl");

            List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
            urlParameters.add(new BasicNameValuePair("name", UUID.randomUUID().toString()));
            urlParameters.add(new BasicNameValuePair("scope", "page"));
            urlParameters.add(new BasicNameValuePair("seed", urltoCrawl.toString()));

            httppost.setEntity(new UrlEncodedFormEntity(urlParameters));

            System.out.println("Executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println("----------------------------------------");

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    crawlid = inputLine;
                }
                in.close();
                EntityUtils.consume(response.getEntity());
            } finally {
                response.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                httpclient.close();
            } catch (IOException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return crawlid;
    }

    public static String getCrawlStatusById(String crawlid) {

        String status = "";
        System.out.println("get crawlid");

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials("diachron", "7nD9dNGshTtficn"));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            HttpGet httpget = new HttpGet("http://diachron.hanzoarchives.com/crawl/" + crawlid);

            System.out.println("Executing request " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println("----------------------------------------");

                String result = "";

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                    result += inputLine;
                }
                in.close();

                result = result.replace("u'", "'");
                result = result.replace("'", "\"");

                JSONObject crawljson = new JSONObject(result);
                System.out.println("myObject " + crawljson.toString());

                status = crawljson.getString("status");

                EntityUtils.consume(response.getEntity());
            } catch (JSONException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                response.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                httpclient.close();
            } catch (IOException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return status;
    }

    public static JSONArray getwarcsByCrawlid(String crawlid) {

        JSONArray warcsArray = null;
        System.out.println("get crawlid");

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials("diachron", "7nD9dNGshTtficn"));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {

            HttpGet httpget = new HttpGet("http://diachron.hanzoarchives.com/warcs/" + crawlid);

            System.out.println("Executing request " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println("----------------------------------------");

                String result = "";

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                    result += inputLine;
                }
                in.close();

                result = result.replace("u'", "'");
                result = result.replace("'", "\"");

                warcsArray = new JSONArray(result);

                for (int i = 0; i < warcsArray.length(); i++) {

                    System.out.println("url to download: " + warcsArray.getString(i));

                }

                EntityUtils.consume(response.getEntity());
            } catch (JSONException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                response.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                httpclient.close();
            } catch (IOException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return warcsArray;
    }

    public static void getwarcByURL(String warcURL) {

        
        //TODO
        try {
            URL obj = new URL(warcURL);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();


            Map m = con.getHeaderFields();
            if (m.get("Content-Disposition") != null) {
//                String raw = con.getHeaderField("Content-Disposition");
//                System.out.println("raw" + raw);
                System.out.println("hello");
            }

            // FileUtils.copyURLToFile(warcURL, "");
        } catch (ProtocolException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void getAllCrawls() {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials("diachron", "7nD9dNGshTtficn"));
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            HttpGet httpget = new HttpGet("http://diachron.hanzoarchives.com/crawl");

            System.out.println("Executing request " + httpget.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println("----------------------------------------");

                System.out.println(response.getEntity().getContent());

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                }
                in.close();
                EntityUtils.consume(response.getEntity());
            } finally {
                response.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                httpclient.close();
            } catch (IOException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * GunZip it
     */
    public void gunzipIt(String input_gzip_file, String output_file) {

        byte[] buffer = new byte[1024];

        try {

            GZIPInputStream gzis
                    = new GZIPInputStream(new FileInputStream(input_gzip_file));

            FileOutputStream out = new FileOutputStream(output_file);

            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            gzis.close();
            out.close();

            System.out.println("Done");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        
                getwarcByURL("http://hanzoenterprise.s3.amazonaws.com/RaC/DIACHRON/7551fda7-08cf-4662-9bd6-670006d1e027/7551fda7-08cf-4662-9bd6-670006d1e027-crawl/7551fda7-08cf-4662-9bd6-670006d1e027-crawl-20150707131625-00001-560e5316-271e-44b3-9222-0c9ce38ff61c.warc.gz?Signature=I6I9jnoAA%2BF9egxcXlBmHr2IBnk%3D&Expires=1436283548&AWSAccessKeyId=0WTGAF2NBCANE1MSZQG2&x-amz-storage-class=REDUCED_REDUNDANCY");

//        URL a = new URL("http://eventmedia.eurecom.fr/");
//        String crawlid  = crawlpage(a);
//        System.out.println("to crawl id mou einai "+crawlid);
//        String status = getCrawlStatusById("d0be3ec6-4566-4b6d-bb83-cf6cf95d1217");
//        System.out.println("my status " + status);
//
//        JSONArray warcsArray = getwarcsByCrawlid("d0be3ec6-4566-4b6d-bb83-cf6cf95d1217");
//
//        for (int i = 0; i < warcsArray.length(); i++) {
//
//            System.out.println("url to download1: " + warcsArray.getString(i));
//
//        }

    }

}
