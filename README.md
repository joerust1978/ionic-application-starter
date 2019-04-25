# ionic-application-starter
Starter application for an ionic based application.  This application is preconfigured as both an ios and android application.  Here are the steps to clone it and start it up and test it with the browser emulator.

1. git clone https://github.com/joerust1978/ionic-application-starter.git
2. cd ionic-application-starter
3. npm install
4. ionic cordova run browser --ssl --livereload
5. Navigate to https://localhost:8100/ in browser.  Ignore the security warnings and go to the page, the test server has spun up a self signed certificate.
6. If you wish to login using my facebook app, you will need to give me your facebook user id so i can add you as a tester.
7. The facebook login returns very basic information about you, and the list of your friends who are using the app (facebook api limitation).  If you wish to build this out a lot further, feel free.
8. I did not create the page for you to display your user info.  That's on you to bind some ionic components to the data that is being returned, or creating a new service to fetch new data for it to return.
9. With your chrome dev tools, i recommend debugging your app using its mobile emulator.  Any development towards the goal of the hackathon should be done in this form factor.
10. Time/ambition permitting i can help you (if necessary) of compiling the cordova app into an android application that can be side loaded onto a phone.
11. The subfolder of lambda-service is a maven based project that builds a lambda service that can do SMS messaging via nexmo.
12. If you opt to modify this service and deploy it on your own AWS account, i can help with that setup.  If you wish to connect to a version i have running, let me know, i'll give that url out on demand so i stay in the freemium zone.
