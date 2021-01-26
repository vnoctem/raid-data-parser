# Raid data parser

View the data in this [Google Sheet](https://docs.google.com/spreadsheets/d/1V2CvJWzpBZFiRGgs92B0FW-HnsMukyIr3_IJfLtmMGo/edit#gid=1759257290 "RSL - Multipliers").


### Description
Creates a Google Sheet containing data parsed from a JSON https://github.com/Da-Teach/RaidStaticData (credits to Da-Teach). It uses Google Sheet API v4 to create a Google Sheet contaning 3 sheets: Multipliers, Champions and Skills.

### Built with
* Spring Boot
* Jackson (JSON library)
* Google Sheets API v4
* Google Drive API v3

### Setup
##### APIs
1. Go to [Google API console](https://console.developers.google.com/) and create a new project
2. Enable Google Sheets API and Google Drive API
3. Create credentials (OAuth Client ID) for a Web application and add `http://localhost:8888/Callback` as an authorized redirect URI
4. Download JSON of your credentials
5. Replace the file `src/main/resources/google-oauth-credentials.json.TEMPLATE` with your downloaded file and change the name to `google-oauth-credentials.json`

##### Run the application
1. Navigate to the root of the project
2. `./mvnw clean install`
3. `./mvnw spring-boot:run`
4. In your terminal, copy the link for the Google Oauth consent screen and open in a browser
5. Sign in your Google account and allow permissions
6. After the application is done running, a Google Sheet named `RSL - Multipliers (last updated: YYYY-MM-DD)` will be present on your Drive
