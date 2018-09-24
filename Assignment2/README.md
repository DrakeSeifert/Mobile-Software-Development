# Assignment 2
**Due by 11:59pm on Monday, 2/12/2018**

**Demo due by 11:59pm on Monday, 2/26/2018**

In this assignment, we'll continue working on our weather app, hooking it up to data from the OpenWeatherMap API and using Intents to start new activities.  The parts of the assignment are outlined below.

## 1. Hook your app up to the OpenWeatherMap API

This repository provides you with some starter code that displays dummy forecast data in a `RecyclerView`.  Your first task for this assignment is to write an `AsyncTask` to fetch forecast data from the OpenWeatherMap API and to display that data in the `RecyclerView` instead of the dummy data.  You can find more info about the OpenWeatherMap API here: https://openweathermap.org/api.  Here are some steps you can follow to get everything working for this part of the assignment:

  1. Sign up for an OpenWeatherMap API key here: http://openweathermap.org/appid.  You'll need this to make calls to the API.  If signing up for an API key is a problem for you, please contact me.

  2. Write a utility method to construct a URL to query OpenWeatherMap's 5-day forecast API for a specified city name.  You can read more about this API here: https://openweathermap.org/forecast5.  Make sure you include your API key as a query string parameter in your URL.

  3. Write a subclass of `AsyncTask` that uses a URL from the method you just wrote to get forecast data from OpenWeatherMap.  Your `AsyncTask` should do the following things:
      * Display a `ProgressBar` in `onPreExecute()`.
      * Fetch forecast data for a specified city as a JSON string in `doInBackground()`.  For this assignment, you can hard code the name of a city for which to fetch data in your main activity class (e.g. "Corvallis, OR").
      * In `onPostExecute()`:
        * Hide the `ProgressBar`.
        * If for some reason you were unable to fetch forecast data in `doInBackground()`, display an error message.
        * If you successfully fetched forecast data, pass it into the `ForecastAdapter` using its `updateForecastData()` method to display the data in the `RecyclerView`.

  4. Call your `AsyncTask` from your main activity class's `onCreate()` to make sure data is loaded when the app starts.

  5. Write a utility method to parse the JSON data returned by OpenWeatherMap into an `ArrayList` of `String` objects, where each string in the list represents a forecast for one date/time entry in the JSON data.  Specifically, each string should contain the following fields from one entry from `list` in the JSON data:
      * `dt_txt` - the date and time
      * `main.temp` - the temperature (make sure you get the units right)
      * `weather.main` - a general description of the weather

      Don't worry too much about formatting your string super nicely (e.g. don't worry about converting UTC time into local time for now).  For example, one of your weather strings might look like this:
      ```
      2017-04-29 00:00:00 - Clear - 54F
      ```
      Plug this parsing method into your `AsyncTask` to parse the JSON data before you pass it into the adapter.

## 2. Use an Intent to start a new activity

Once you have your app hooked up to the OpenWeatherMap API, implement functionality that allows the user to click on any item in the forecast list to view a "detailed" version of that forecast.  Here are some steps you can follow for this part of the assignment:

  1. Implement a new activity to represent the "detailed" view of the forecast.  To do this, you should write:
      * A new layout XML file for this activity.  At a minimum, this layout should contain a `TextView` you can use to display the weather string.
      * A new subclass of `AppCompatActivity`.  At a minimum, this class should implement an `onCreate()` method that does the following:
        * Uses `getIntent()` to get the `Intent` that initiated the activity.
        * If the `Intent` was not `null`, grabs the forecast string extra from the `Intent` and displays it in the activity's `TextView`.

  2. Add an entry in `AndroidManifest.xml` for the new activity.

  3. The app is currently set up to handle clicks on individual items in the forecast list by displaying a toast with the corresponding detailed forecast.  Change this functionality so that a new explicit `Intent` is created to start the new activity you just implemented.  When a forecast item is clicked, pass the the forecast string as an argument to the click handler and then into the `Intent` as an extra, and use the `Intent` to start the activity.

## 3. Add some implicit intents

Finally, add the following features using implicit intents:

  1. Add an action to the action bar of the main activity that allows the user to see in a map the location for which the forecast is displayed.

  2. Add an action to the action bar of the "detailed" forecast activity that allows the user to share the "detailed" forecast text.  Use the `ShareCompat` class's `IntentBuilder` to create an implicit intent to accomplish this.

## Submission

As usual, we'll be using GitHub Classroom for this assignment, and you will submit your assignment via GitHub.  Make sure your completed files are committed and pushed by the assignment's deadline to the master branch of the GitHub repo that was created for you by GitHub Classroom.  A good way to check whether your files are safely submitted is to look at the master branch your assignment repo on the github.com website (i.e. https://github.com/OSU-CS492-W18/assignment-2-YourGitHubUsername/). If your changes show up there, you can consider your files submitted.

## Grading criteria

This assignment is worth 100 total points, broken down as follows:

  * 50 points: The app fetches and displays data from the OpenWeatherMap API

  * 30 points: The app uses an explicit intent to start a "detailed" forecast activity whenever the user clicks on an item in the forecast list

  * 20 points: The app uses implicit intents to launch other activities:
    * 10 points: The app includes an action in the main activity's action bar to allow the user to see the forecast location in a map
    * 10 points: The app includes an action in the "detailed" forecast activity's action bar to allow the user to share the text of the "detailed" forecast
