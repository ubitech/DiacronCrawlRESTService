# DiacronCrawlRESTService

See DiacronCrawlRESTService workflow from here :https://docs.google.com/drawings/d/1NyGk_29rEKNwgRZg545tLiFj-7P8pfirCeY0NF8Lb78/edit


##Step 2 Install  and confiture Activemq server 
> - Download activemq from http://activemq.apache.org/activemq-5111-release.html
> - install activemq and init it as :```bin\activemq start```
> - delete destinations that are inactive for a period of time. See how to in:
http://activemq.apache.org/delete-inactive-destinations.html

for example:

<broker xmlns="http://activemq.apache.org/schema/core" schedulePeriodForDestinationPurge="10000">
 ```
<destinationPolicy>
     <policyMap>
        <policyEntries>
           <policyEntry queue=">" gcInactiveDestinations="true" inactiveTimoutBeforeGC="14400000"/>
        </policyEntries>
     </policyMap>
  </destinationPolicy>
       
</broker>
```
This configurationwill check for inactive destination every 10 seconds (schedulePeriodForDestinationPurge option, default value is 0). And it will delete all queues (gcInactiveDestinations option, false by default) if they are empty for 14400000 seconds (which is 4 hours) (inactiveTimoutBeforeGC option, default is 1 minute).


##Step 2 Install  WildFly server 

####Step 2.1 Download & Install WildFly 9  
http://wildfly.org/downloads/
(Actual link:
http://download.jboss.org/wildfly/9.0.0.CR1/wildfly-9.0.0.CR1.zip)

####Step 2.2 Add properties module to wildfly
> - create in $WILDFLY_HOME/modules/ 3 folders one inside of the other as  "/lindaAnalytics/configuration/main/".

> - inside "main" folder create module.xml file as follows:
```
<?xml version="1.0" encoding="UTF-8"?>  
  <module xmlns="urn:jboss:module:1.1" name="lindaAnalytics.configuration">  
      <resources>  
	  <resource-root path="."/>  
      </resources>  
  </module>  
```  
> - inside "main" folder create RESTfulLINDA.properties file as follows (change adequately the properties):
```
BROKER_URL = tcp://127.0.0.1:61616?jms.prefetchPolicy.all=1000
REMOTE_CRAWLER_URL = http://diachron.hanzoarchives.com/warcs/
REMOTE_CRAWLER_URL_CRAWL_INIT = http://diachron.hanzoarchives.com/crawl
REMOTE_CRAWLER_URL_CRAWL = http://diachron.hanzoarchives.com/crawl/
REMOTE_CRAWLER_USERNAME=diachron
REMOTE_CRAWLER_PASS=7nD9dNGshTtficn
TMP_FOLDER_CRAWL = /tmp/
TMP_SERIALIZATION_RDF_FORMAT  = RDF/XML                                                                                                                                                         
TMP_SERIALIZATION_RDF_FILEEXT = rdf 
```

##Step 3 How to use Diachron Crawl service as Client:
See eu.diacron.crawlservice.client classes in order to init a new page crawl process and get informed after topic is filled.

