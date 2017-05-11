#########################################################
# Welcome to the SAP Cloud Platform Connectivity Sample #
#########################################################

The connectivity sample covers two basic connectivity scenarios:
 - Outbound internet destinations usage
 - On-demand to on-premise connectivity scenario.

Prerequisites for execution on a Local Server from the Eclipse IDE

1. Configure the destination 'outbound-internet-destination'
In order to run the sample from your Eclipse IDE, you need to import a destination into the local server.
You can do that by double-clicking on your local server in the 'Servers' view and then switching to the Connectivity tab.
Import the destination outbound-internet-destination from this sample '/destinations' folder.

This operation is done automatically by the Maven build. It uses the accordingly located destination and copies it (check out pom.xml)
to the local server, which it creates on-the-fly during the build.
In this way, the integration tests will successfully detect the destination.

NOTE: If you need to create yourself another local server in the Eclipse IDE, you have to do it separately 
in the 'Servers' view.

For more information, see https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/e592cf6cbb57101495d3c28507d20f1b.html


2. (Optional) Configure an HTTP proxy for the outbound Internet connection
The sample shows how to consume the Internet resource https://help.hana.ondemand.com/terms_of_use.html. In case you run the sample
from behind an outgoing HTTP proxy, make sure you have configured the proxy as part of the SAP Cloud Platform local runtime
in Eclipse. To do this, double-click the local server in the 'Servers' view, then click the 'Open launch
configuration' link on the Overview tab, this opens the 'Edit launch configuration properties' view. Navigate there to
the 'Arguments' tab and add your proxy definition as 'VM arguments' in the following format:
-Dhttps.proxyHost=<your_proxy_host> -Dhttps.proxyPort=<your_proxy_port>


On-premise destinations

1. Configure on-premise destinations.
On-premise destinations are used in a more complex scenario when an cloud application wants to access resources from an on-premise backend system.
The following destinations, according to your authentication case:
  - 'backend-no-auth-destination'
  - 'backend-basic-auth-destination'
  should be uploaded to the cloud server application
Both destinations are located in this sample '/destinations' folder.

For more information, see https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/e76f9e75bb571014a7218bcd30a8771b.html

2. Run backend service application

The application WAR files located in the '/onpremise' folder of the sample have to be deployed as backend applications.
You can use an arbitrary local Java Web Container for this, like the local server created in Eclipse

For more information, see https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/e59dc589bb57101480939e290c55e680.html

In order to use on-premise backend services in a cloud application, the SAP Cloud Connector should be set up and configured.

For more information, see https://help.sap.com/viewer/65de2977205c403bbc107264b8eccf4b/Cloud/en-US/e6c7616abb5710148cfcf3e75d96d596.html