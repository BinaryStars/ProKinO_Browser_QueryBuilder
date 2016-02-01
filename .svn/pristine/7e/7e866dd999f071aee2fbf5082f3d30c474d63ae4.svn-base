How to compile and install the ProKinO Browser
----------------------------------------------
0. Set up your CLASSPATH and Java environment:

   * You must have the resteasy, freemarker, and servlet-api jars on
     your CLASSPATH.
   * If you are deploying on om, use a different war file name in
     steps 2, 3 and 4 below, as prokin.war already exists.
     Of course, change the server address in step 4, if you are
     using a different server than om.
   * Note that the ProKinO ontology file is not included.
     It must be copied into the directory app/WEB-INF/classes with the
     name Prokino.owl.

1. Update the build.properties file.

2. Compile the system:

     ant compile

   and then 

     ant war

   or

     ant deploy

3. The WAR file can be deployed manually

     cp prokino.war /usr/local/jboss/server/default/deploy
     chmod a+r /usr/local/jboss/server/default/deploy/prokino.war

4. Configure Apache httpd redirection and set up the initial web page.

   The redirection modifications are in file etc/httpd.conf.mods.

   The initial prokino web page is in etc/prokino.  It should be
   copied to either the default Apache www/html directory, or
   somewhere accessible to the Apache web server.

5. Start browsing by opening the following link:

     http://host.address:8080/prokino
