# Travel-system
Spring Boot web system for groups to plan journey/activity and display route with directions on maps. 
<br>
<img src="/images/1.PNG" width="75%">

Journeys can be:
 - Open (Available for everyone unless number of people reached bump limit)
 - By invitations (Activity is available only for these users who accepted the creator's invitation)
 
<br>
<img src="/images/2.PNG" width="75%">


To show information about location are used [OpenStreetMap](https://www.openstreetmap.org) and [LeafletJS](https://leafletjs.com/). For complex route and directions is used [Google Maps API](cloud.google.com). Information about covid numbers is used from ARCGIS json file.

<br>
<img src="/images/3.PNG" width="75%">

Additional functions/aspects:
- 2 roles, user and admin.
- Advanced search by parameters. 
- Possibility to write review, comments.
- Embedded documentation
