/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.diacron.crawlservice.app;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import eu.diacron.crawlservice.config.Configuration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
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
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveRecord;
import org.archive.io.warc.WARCReaderFactory;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author eleni
 */
public final class Util {
      private final Properties prop = new Properties();

    public static String getCrawlid(URL urltoCrawl) {
        String crawlid = "";
        System.out.println("start crawling page");

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(Configuration.REMOTE_CRAWLER_USERNAME, Configuration.REMOTE_CRAWLER_PASS));        
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            //HttpPost httppost = new HttpPost("http://diachron.hanzoarchives.com/crawl");
            HttpPost httppost = new HttpPost(Configuration.REMOTE_CRAWLER_URL_CRAWL);
            
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
                new UsernamePasswordCredentials(Configuration.REMOTE_CRAWLER_USERNAME, Configuration.REMOTE_CRAWLER_PASS));        

        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            //HttpGet httpget = new HttpGet("http://diachron.hanzoarchives.com/crawl/" + crawlid);
            HttpGet httpget = new HttpGet(Configuration.REMOTE_CRAWLER_URL_CRAWL + crawlid);
            
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
/*        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials("diachron", "7nD9dNGshTtficn"));
        */
        
        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(Configuration.REMOTE_CRAWLER_USERNAME, Configuration.REMOTE_CRAWLER_PASS));        
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {

            //HttpGet httpget = new HttpGet("http://diachron.hanzoarchives.com/warcs/" + crawlid);
            HttpGet httpget = new HttpGet(Configuration.REMOTE_CRAWLER_URL + crawlid);
            
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

    private static String getwarcByURL(String warcURLasString) {
        File warcfile = null;
        try {
            URL warcURL = new URL(warcURLasString);
            String fileName = FilenameUtils.getBaseName(warcURLasString);

            warcfile = new File(Configuration.TMP_FOLDER_CRAWL + fileName + ".gz");
            FileUtils.copyURLToFile(warcURL, warcfile);
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("warcfile.getAbsolutePath() " + warcfile.getAbsolutePath());
        return warcfile.getAbsolutePath();

    }

    public static JSONArray manageWarcFile(String warcfilepath) {
        String downloadedfile = getwarcByURL(warcfilepath);

        JSONArray json4RDFizing = RDFizeWarcFile(downloadedfile);
//         String unziped_warcfilepath = unzipWarcFile(downloadedfile);
//         String rdfized_warcfilepaty = RDFizeWarcFile(unziped_warcfilepath);
//         return storeRDFizedWarcFile(rdfized_warcfilepaty);
        return json4RDFizing;
    }

    private static JSONArray RDFizeWarcFile(String warcfilepath) {
        JSONArray json4RDFizing = new JSONArray();
        FileInputStream is = null;
        try {
            String fileName = null;
            // Set up a local compressed WARC file for reading
            is = new FileInputStream(warcfilepath);
            // The file name identifies the ArchiveReader and indicates if it should be decompressed
            ArchiveReader ar = WARCReaderFactory.get(warcfilepath, is, true);
            // Once we have an ArchiveReader, we can work through each of the records it contains
            int i = 0;
            for (ArchiveRecord r : ar) {
                // The header file contains information such as the type of record, size, creation time, and URL
                System.out.println(r.getHeader());
                System.out.println(r.getHeader().getUrl());
                System.out.println();

                // If we want to read the contents of the record, we can use the ArchiveRecord as an InputStream
                // Create a byte array that is as long as the record's stated length
                byte[] rawData = IOUtils.toByteArray(r, r.available());

                // Why don't we convert it to a string and print the start of it? Let's hope it's text!
                String content = new String(rawData);
                System.out.println(content.substring(0, Math.min(500, content.length())));
                System.out.println((content.length() > 500 ? "..." : ""));

                //get only HTTP info
                if (content.contains("HTTP/1.1 200 OK")) {

                    JSONObject pageBasics = new JSONObject();

                    pageBasics.put("headerURL", r.getHeader().getUrl());
                    System.out.println("headerURL " + r.getHeader().getUrl());

                    pageBasics.put("title", getTextBetweenTags(content, "title"));
                    pageBasics.put("firstParagraph", getTextBetweenTags(content, "p"));

                    json4RDFizing.put(pageBasics);

                }

                // Pretty printing to make the output more readable
                System.out.println("=-=-=-=-=-=-=-=-=");
                if (i++ > 4) {
                    break;
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return json4RDFizing;

    }

    public static String getTextBetweenTags(String content, String tag) {
        final Pattern pattern = Pattern.compile("<" + tag + ">(.+?)</" + tag + ">");
        final List<String> tagValues = new ArrayList<String>();
        final Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            tagValues.add(matcher.group(1));
        }
        return tagValues.get(0);

    }

    public static boolean storeRDFizedWarcFile(Model model , String rdfizedWarcFilepath) {
        try{
        //fileName = targetFileNameFullPath + ".nt";
        FileWriter outToSave = new FileWriter(rdfizedWarcFilepath);
        model.write(outToSave, Configuration.TMP_SERIALIZATION_RDF_FORMAT);
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;

    }

    public static void getAllCrawls() {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(Configuration.REMOTE_CRAWLER_USERNAME, Configuration.REMOTE_CRAWLER_PASS));        
        CloseableHttpClient httpclient = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
        try {
            //HttpGet httpget = new HttpGet("http://diachron.hanzoarchives.com/crawl");
            HttpGet httpget = new HttpGet(Configuration.REMOTE_CRAWLER_URL_CRAWL);
            
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

    private static String unzipWarcFile(String input_gzip_file) {

        String output_file = input_gzip_file.split(".gz")[0];

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

        return output_file;
    }

    public static JSONArray concatArray(JSONArray... arrs)
            throws JSONException {
        JSONArray result = new JSONArray();
        for (JSONArray arr : arrs) {
            for (int i = 0; i < arr.length(); i++) {
                result.put(arr.get(i));
            }
        }
        return result;
    }

    public static boolean generateRDFModel(JSONArray jsonArray4RDFizing, String crawlid) {

        // Create the model and define some prefixes (for nice serialization in RDF/XML and TTL)
        Model model = ModelFactory.createDefaultModel();

        String base = "http://localhost:8181/Diacrawl";

        String ds = base + "#";

        String crawl_base = base + "/" + crawlid;
        String crawl_base_ns = crawl_base + "#";

        model.setNsPrefix("ds", ds);
        model.setNsPrefix("rdf", RDF.getURI());
        model.setNsPrefix("xsd", XSD.getURI());
        model.setNsPrefix("rdfs", RDFS.getURI());
        model.setNsPrefix("cr", crawl_base_ns);

        Resource pageinfo_node = model.createResource(crawl_base_ns + "pageinfo");

        for (int i = 0; i < jsonArray4RDFizing.length(); i++) {

            try {
                JSONObject ob = jsonArray4RDFizing.getJSONObject(i);
                Resource pageinfo_node_statement = model.createResource(crawl_base_ns + "pageinfo/" + i);
                pageinfo_node_statement.addProperty(RDF.type, pageinfo_node);
                pageinfo_node_statement.addProperty(RDFS.label, ob.getString("title"));
                pageinfo_node_statement.addProperty(RDFS.isDefinedBy, ob.getString("headerURL"));
                pageinfo_node_statement.addProperty(RDFS.comment, ob.getString("firstParagraph"));
            } catch (JSONException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        storeRDFizedWarcFile(model,Configuration.TMP_FOLDER_CRAWL + crawlid + "RDFized." + Configuration.TMP_SERIALIZATION_RDF_FILEEXT);

        return true;
    }
    
    

    public static void main(String[] args) throws Exception {
//        String urlstring = "http://hanzoenterprise.s3.amazonaws.com/RaC/DIACHRON/7551fda7-08cf-4662-9bd6-670006d1e027/7551fda7-08cf-4662-9bd6-670006d1e027-crawl/7551fda7-08cf-4662-9bd6-670006d1e027-crawl-20150707131529-00000-560e5316-271e-44b3-9222-0c9ce38ff61c.warc.gz?Signature=hbg6ecctMldLf%2BwA7ldIRT77O4s%3D&Expires=1436347898&AWSAccessKeyId=0WTGAF2NBCANE1MSZQG2&x-amz-storage-class=REDUCED_REDUNDANCY";
//
//        getwarcByURL(urlstring);

//        URL a = new URL("http://example.com/");
//        String crawlid  = getCrawlid(a);
//        System.out.println("to crawl id mou einai "+crawlid);
//        String status = getCrawlStatusById("ca23f7a8-02ba-4a39-afe3-a03cc6f5ea67");
//        System.out.println("my status " + status);
//
//        JSONArray warcsArray = getwarcsByCrawlid("d0be3ec6-4566-4b6d-bb83-cf6cf95d1217");
//
//        for (int i = 0; i < warcsArray.length(); i++) {
//
//            System.out.println("url to download1: " + warcsArray.getString(i));
//
//        }
        String warcfile = "/home/eleni/Downloads/608c4b6b-42e7-40c1-a333-2b22c6f7b980-crawl-20150708110440-00000-c359dfd0-ce37-4f1c-a5d0-70c76e02f077.warc.gz";
        RDFizeWarcFile(warcfile);
    }

}
