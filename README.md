# RWTDVRP-Bachelor-Project
**This is the accompanying project of my Bachelor Thesis from UBB, 2020.**

## Project overview


## Features of the Wrapper Application:
* feature 1

## How to start the project:
**Backend of the API:**
* Make sure that you have a Google Cloud project with Directions API enabled and a generated API key;
* Make sure that the "rwtdvrp.google-maps-api-key" key from the application.properties file is set according to your own API key;
* Run the app.

**Backend of the Wrapper Application:**
* Make sure that you have installed a mysql server and created an empty "vrp" database (tables will be generated automatically);
* Modify the "...initializeData.sql" migration file from the resources folder in order to customize your initial data that will be inserted in the DB tables;
* Run the app.

**Frontend of the Wrapper Application:**
* Make sure that you have a Google Cloud project with Geocoding API, Places API and Maps JavaScript API enabled and a generated API key;
* Set the "apiKey" key of the AgmCoreModule module to match your own API key;
* Install Node.js;
* Install Angular CLI;
* Run "npm install" followed by "npm audit fix" in the vrp-angular-client folder from the frontend module;
* Run "npm start" on the same vrp-angular-client folder.

## Screenshots from the application

<img src="screenshots/ss1.png"/>
<img src="screenshots/ss2.png"/>
<img src="screenshots/ss3.png"/>
<img src="screenshots/ss4.png"/>
<img src="screenshots/ss5.png"/>
<img src="screenshots/ss6.png"/>
<img src="screenshots/ss7.png"/>
<img src="screenshots/ss8.png"/>
