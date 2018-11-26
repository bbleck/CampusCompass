# CampusCompass
What does it do?
- CampusCompass is an android app that provides directions to locations on the University of New Mexico (UNM) campus, as well as information related to particular locations.
- The main menu allows users to select a type of destination, which will take them to the map screen.
- The map screen contains a list of all destinations of the selected type. This list is sorted by distance, from closest to the user to farthest from the user.
- The map screen also shows the map location of the visible destinations in the list and dynamically redraws itself as the user scrolls through the list of destinations. 
- The map screen has a search option, which allows the user to use key words to filter the list results. The list is filtered using the destinations titles and descriptions, as provided by the UNM open data. The list and map will dynamically update as the user types characters into the search bar.
- In the map screen, the items in the list have two buttons: an "Info" button and a "Get Directions" button.
- The Info button summons a dialog that provides the user with more information related to the list item.
- The Get Directions button takes the user into the Google Maps App (it must be installed on the user's device) where walking directions will be displayed that will lead the user to the list item's location.

## Aims and Motivations for CampusCompass
The driving motivation for this project was my experience at the beginning of every semester, wishing there was an easier way to find buildings on campus. The framework of this project could be applied to any campus, geographic region, etc. At the heart of this project is the open data APIs provided by UNM.  Those APIs allow the app to supply the user with lists of related locations, which the user can search through and see dynamically displayed on an in-app googlemap.  The app uses user location to connect to the GoogleMaps app and provide detailed directions to help the user find the chosen location.  Additionally, the UNM APIs (sometimes) include extra information pertinent to particular locations.


## Current State of CampusCompass
CampusCompass is currently functional.  Remaining fixes to be made are:
- Working with UNM to add more locations to their API, particularly RestRooms which are currently limited to Gender Neutral restrooms and are mostly located off-campus.  Finding the closest restroom is a feature that would probably be broadly used.


## Android Version Testing
This app has been extensively testing on a Google Pixel running Android Pie (API 28).  It has also been testing using the Android Studio Emulator running API 27 on a Nexus 5X emulation.  Minimum API is 24, and recommended API is 28 (API 28 has superior location finding).  At present, there are not any known Android devices that do not support Campus Compass, so long as the minimum Android API is met and the user gives the required permissions. Orientation is locked in Portrait.  The app currently supports english only.  Users must enable location services and GPS in order to use the app.  Required permissions:  Internet, Fine Location.


## Third Party Libraries
```
implementation 'android.arch.persistence.room:runtime:1.1.1'
implementation 'com.squareup.retrofit2:retrofit:2.4.0'
implementation 'com.squareup.retrofit2:converter-gson:2.4.0'
implementation "com.squareup.retrofit2:adapter-rxjava:2.1.0"
implementation 'com.google.android.gms:play-services-maps:16.0.0'
implementation 'com.google.android.gms:play-services-location:16.0.0'
implementation 'com.google.maps.android:android-maps-utils:0.5+'
implementation 'com.google.maps:google-maps-services:0.9.0'
implementation 'com.google.code.gson:gson:2.8.5'
annotationProcessor 'android.arch.persistence.room:compiler:1.1.1'
implementation "io.reactivex:rxjava:1.2.1"
implementation "io.reactivex:rxandroid:1.2.1"
implementation 'de.hdodenhof:circleimageview:2.2.0'
implementation 'org.slf4j:slf4j-simple:1.7.25'
```


## External Services
The app consumes the following external services:
- [Open Data API](http://datastore.unm.edu/locations/) from the University of New Mexico  
- [GoogleMaps API](https://developers.google.com/maps/documentation/)


## Cosmetic Improvements
The following cosmetic improvements could be made to the app:
- Color scheme
- Increasing the size of compass drawables
- Handling unexpected javascript in JSON strings (as seen in the extra info associated with some, but not all, library locations).
- Clicking on a search list item should highlight and center the associated marker on the map.
- Better graphical representation helping users to use the toolbar to navigate back to the main menu (this navigation is the expected way to return to main menu).


## Stretch Goals
The app could be extended in the following ways:
- As previousy mentioned, adding restroom locations for every restroom on campus would be ideal.
- Working with UNM to add more useful information to their API and supporting that information within CampusCompass. For example, if UNM allowed nearby restaurants to advertise then CampusCompass could support such advertisements using the current framework. Advertising dollars could make CampusCompass self-sustaining and allow for it to be expanded (and help UNM create more APIs or to improve current APIs).


## Wireframes
[Adobe XD Wireframe: CampusCompass](https://xd.adobe.com/view/6e52afdd-105d-4f56-64a1-5b48c37b17db-1863/)

## User Stories
- As a UNM Student, I would like to be able to easily find academic buildings on campus so I can easily find where my classes are at the beginning of each semester.
- As a Visitor to UNM, I would like to be able to get directions to places on campus, including where metered parking is located so that I don't have to wander around or search the internet for such information.
- As UNM, I would like to be able to sell advertising for local restaurants in a way that will also provide students with useful information so they can easily discover nearby dining options.
- As a human who is on the UNM campus, I would like to be able to easily find the closest blue phone and the closest restroom so that I can quickly find them in case of emergency.
- As a user of the app, I would like to be able to filter the list of potential destinations and search for a particular one so that I can find it quickly.


## ERD
[Entity Relationship Diagram](https://github.com/bbleck/CampusCompass/raw/master/CampusCompass_ERD.pdf)

## DDL
```
CREATE TABLE IF NOT EXISTS `Token`(
  `title`          TEXT COLLATE NOCASE,
  `building_num`   TEXT,
  `abbr`           TEXT,
  `campus`         TEXT,
  `keywords`       TEXT,
  `image`          TEXT,
  `link`           TEXT,
  `token_id`       TEXT NOT NULL,
  `description`    TEXT,
  `token_type`     INTEGER,
  `mLongitude`     REAL,
  `mLatitude`      REAL,
  PRIMARY KEY(`token_id`)
);

CREATE  INDEX `index_Token_title` 
                   ON `Token` (`title`);

CREATE  INDEX `index_Token_token_type` 
                   ON `Token` (`token_type`);
);
```

## Project Javadocs
[CampusCompass Javadocs](https://github.com/bbleck/CampusCompass/tree/master/docs)


## Copyrights and Licenses
- General Google [Terms](https://developers.google.com/terms/site-policies)
- Google Play Services [Dependencies](https://developers.google.com/android/guides/setup)
- Google [Maps](https://developers.google.com/maps/documentation/android-sdk/intro)
- Google [GSON](https://github.com/google/gson)
- Google GSON [License](https://github.com/google/gson/blob/master/LICENSE)
- [Retrofit](https://square.github.io/retrofit/)
- Retrofit [License](http://www.apache.org/licenses/LICENSE-2.0)
- [RxJava](https://github.com/ReactiveX/RxJava)
- RxJava [License](https://github.com/ReactiveX/RxJava/blob/2.x/LICENSE)
- Simple Logging Facade For Java [SLF4J](https://www.slf4j.org/license.html)
- [CircleImageView](https://github.com/hdodenhof/CircleImageView)
- CircleImageView [License](https://github.com/hdodenhof/CircleImageView/blob/master/LICENSE.txt)


## Instructions for Building CampusCompass
1. Clone the repository into a local Android Studio project.
2. Add a live Google API key to: app -> res -> values -> google_maps_api.xml.  Cut the text "GOOGLE_KEY_NEEDS_TO_BE_PASTED_HERE" and replace it with the live key.
3. The Google API keys in the version history of CampusCompass are all dead and will not work for you.
4. 


## Instructions for Using CampusCompass
1. The initial screen is the Main Menu.  It presents users with the option to choose a type of destination.  To choose a destination, the user must click on one of the icons.
2. Upon clicking on an icon, the user is taken to the Map Screen.
3. To return to the Main Menu from the Map Screen the user must click on the top toolbar, either on the text indicating a return to the Main Menu or on the 'Home' button.
4. To quit the app, the user may press the Android 'Back Navigation' button at any time. 
5. In the Map Screen, the user may enter the name of the destination they are searching for or they may try entering key words.  For example, if the user selects "Find Building" on the Main Menu they may enter either "Center for the arts" or "popejoy" to find the same building (the building's official name is "Center For The Arts").
6. For any destination list item in the Map Screen, the user may press the Info button to see more information on the destination (if UNM open data provides more information).
7. For any destination list item in the Map Screen, the user may press the Get Directions buttons to get directions to the destination. Get Directions will take the user into the Google Maps App (must already be installed on the user's device) with the destination preloaded.

## Compass Graphics
Many thanks to Danielle Andazola for the compass graphics, inquiries for freelance work can be directed to <Dzolamarketing@gmail.com> 
