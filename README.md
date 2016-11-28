# PropertyManager
A Property Management System for Monitoring Repair Tickets
<img width="960" alt="loginview" src="https://cloud.githubusercontent.com/assets/865352/19712434/6c168724-9b03-11e6-9433-783e2dd16d78.PNG">

## What Is It?
PropertyManager is a generic web app that I created for a class I took in 2016. The class focused on building information systems so I decided to try my luck at it. "Mercy Affordable Housing" is the name of the mock organization that this service was made for. This system allows users (admins, repair crew, building managers, executives, etc) to login and see a custom dashboard that will provide them with useful information to do their job more efficiently. This program is focused specifically at tracking repair tickets for each property as well as the expenses, locations, and status information that is tied to them.

<img width="960" alt="newuserview" src="https://cloud.githubusercontent.com/assets/865352/19712444/82668dd0-9b03-11e6-8d47-c16281320109.png">

## How Do I Install It?
First you will need eclipse for Java EE with maven installed. Then create a workspace somewhere and clone this project into that workspace. If you do it correctly, you should end up with a folder called PropertyManager inside your workspace folder. Inside eclipse, just go File > Import > Maven > Existing Maven Project. Then wait a few minutes for Maven to build your project. Once complete, you will need an integrated server in eclipse if you plan on developing with it. I suggest using Apache Tomcat 7. Because this application relies on a preconfigured mysql database, you will have to do some customization of the SessionManager in order for it to properly connect. 

In the future I will post an SQL script that will create all the required tables.

## How Was It Made?
I built this program using the Vaadin Web Framework. This allows programmers to write swing-style code in Java and view the program as a web page. Vaadin basically allows for developement of the back-end Java to generate the front-end HTML, JavaScript, and CSS.

This system uses a MySQL database to store persistant data and most queries are managed by a useful framework called ActiveJDBC. ActiveJDBC creates an easy way for developers to persist java classes into a database with minimal configuration. All config used in this project can be found in the com.wilsongateway.Framework.Tables class.

All dependencies are managed by Maven and the project runs as a Servlet. For testing purposes, a preconfigured Apache Tomcat server is included in the LocalServer folder.

##Status
Currently the project is in a working state although I would like to continue to add more views. I'll be the first to admit that the code is not cleanest. Once all views are made, I plan on refactoring the Forms package and commenting all classes. I would like to eventually turn the Framework package into an API to use in future projects or for forking here.

###Credits
This program was made by Nicholas Wilson in 2016. Please refer to the License.md file for usage concerns.
