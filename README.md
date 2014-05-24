VehicleApp
==========

Android application code dealing with Sockets, GPS, Media Player and Camera

The android application is one of the three programming platforms used for the project "Autonomous Vehicle". The other two
platforms used were a multi-threaded server which employed java and a toy car which was programmed using C.

The android phone was fitted on the toy car to fetch its latest GPS coordinates and send them back to the server, as the car 
approached the accident site, whose GPS coordinates were already stored on the server. The application sends the GPS coordinates 
of the car based on the event listener "onLocationChanged()". The car's real time distance from the accident site is calculated
on the server and appropriate command is sent back to the android application to control the movement of the car.

The car's movement was controlled using one of the two options: through the mobile phones' audio output or the camera flash.
A threshold distance was set at the server. Whenever the car's distance from the accident site became less than the threshold
distance, a command to stop the car was issued from the server to the android application. In case of audio command, a specific 
audio file stored on the mobile phones' internal storage was played using media player to control the car. And in other case, 
a string "F" (meaning continue moving forward) or "S" (meaning stop) was sent from the server to control the car movement using 
the camera flash. Basically in the second case, the camera's LED flash remained on until the car crossed the threshold distance 
from the accident site. All communcation between the server and the android application was carried out using sockets.
