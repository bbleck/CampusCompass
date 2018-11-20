# CampusCompass
Android Individual Project: an android app that provides directions to locations on campus, as well as information related to particular locations.


## Aims and Motivations for CampusCompass
The driving motivation for this project was my experience at the beginning of every semester, wishing there was an easier way to find buildings on campus. The framework of this project could be applied to any campus, geographic region, etc. At the heart of this project is the open data APIs provided by UNM.  Those APIs allow the app to supply the user with lists of related locations, which the user can search through and see dynamically displayed on an in-app googlemap.  The app uses user location to connect to the GoogleMaps app and provide detailed directions to help the user find the chosen location.  Additionally, the UNM APIs (sometimes) include extra information pertinent to particular locations.


## Current State of CampusCompass
CampusCompass is currently mostly functional.  Remaining fixes to be made are:
- The list search functionality and dynamically updated RecyclerView.
- Implementing compass drawables in the RecyclerView instead of the default drawables. The compasses should add tell the user the direction each location is from the user's current location.
- Dynamically changing the Title in the Toolbar to indicate clicking on it will take the user back to the Main Menu.
- Working with UNM to add more locations to their API, particularly RestRooms which are currently limited to Gender Neutral restrooms and are mostly located off-campus.  Finding the closest restroom is a feature that would probably be broadly used.


## Android Version Testing
This app has been extensively testing on a Google Pixel running Android Pie (API 28).  It has also been testing using the Android Studio Emulator running API 27 on a Nexus 5X.  Minimum API is 24, and recommended API is 28 (API 28 has superior location finding).  At present, there are not any known Android devices that do not support Campus Compass, so long as the minimum Android API is met and the user gives the require permissions. Orientation is locked in Portrait.  The app currently supports english only.  Users must enable location services and GPS in order to use the app.  Required permissions:  Internet, Fine Location.


## Third Party Libraries
Dependencies:
- implementation 'com.google.android.gms:play-services-maps:16.0.0'
- implementation 'android.arch.persistence.room:runtime:1.1.1'
- implementation 'com.squareup.retrofit2:retrofit:2.4.0'
- implementation 'com.squareup.retrofit2:converter-gson:2.4.0'
- implementation 'com.google.code.gson:gson:2.8.5'
- annotationProcessor 'android.arch.persistence.room:compiler:1.1.1'
- implementation 'com.facebook.stetho:stetho:1.5.0'
- testImplementation 'junit:junit:4.12'
- androidTestImplementation 'com.android.support.test:runner:1.0.2'
- androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
- implementation "com.squareup.retrofit2:adapter-rxjava:2.1.0"
- implementation 'com.squareup.picasso:picasso:2.71828'
- implementation "io.reactivex:rxjava:1.2.1"
- implementation "io.reactivex:rxandroid:1.2.1"
- implementation 'de.hdodenhof:circleimageview:2.2.0'
- implementation 'com.google.android.gms:play-services-location:16.0.0'
- implementation 'com.google.maps.android:android-maps-utils:0.5+'
- implementation 'com.google.maps:google-maps-services:0.9.0'
- implementation 'org.slf4j:slf4j-simple:1.7.25'


## External Services
The app consumes the following external services:
- Open Data API from the University of New Mexico:  http://datastore.unm.edu/locations/
- GoogleMaps API


## Cosmetic Improvements
The following cosmetic improvements could be made to the app:
- Color scheme
- Implementing the compass drawables
- Handling unexpected javascript in JSON strings (as seen in the extra info associated with some, but not all, library locations).
- Clicking on a search list item should highlight and center the associated marker on the map.
- Better graphical representation helping users to use the toolbar to navigate back to the main menu (this navigation is the expected way to return to main menu).


## Stretch Goals
The app could be extended in the following ways:
- As previousy mentioned, adding restroom locations for every restroom on campus would be ideal.
- Working with UNM to add more useful information to their API and supporting that information within CampusCompass. For example, if UNM allowed nearby restaurants to advertise then CampusCompass could support such advertisements using the current framework. Advertising dollars could make CampusCompass self-sustaining and allow for it to be expanded to include more UNM APIs (and help UNM create/improve APIs).


## Wireframes


## ERD


## DDL
''' SQL
CREATE TABLE IF NOT EXISTS `Token`(
  `title` TEXT COLLATE NOCASE,
  `building_num` TEXT,
  `abbr` TEXT,  
  `campus` TEXT,  
  `keywords` TEXT,  
  `image` TEXT,  
  `link` TEXT,  
  `token_id` TEXT NOT NULL,  
  `description` TEXT,  
  `token_type` INTEGER,
  `mLongitude` REAL, 
  `mLatitude` REAL,
  PRIMARY KEY(`token_id`) 
);
'''

## Project Javadocs


## Licenses


## Instructions for Building CampusCompass


## Instructions for Using CampusCompass
