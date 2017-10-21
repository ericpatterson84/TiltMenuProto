# TiltMenuProto

An Android application to simulate a wearable device that implements a two way menu navigation system. Menu options are selected by tilting the device in the direction of the arrows until the ball reaches the edges of the circle. The user is instructed to navigate to a specific menu item. They must use their best judgement to determine the correct navigation path. 

A series of menu navigation targets are presented to the user in random order. The user is notified when all the navigation points have been reached.

User quantitative data is collected after each attempted menu navigation. The intended menu target, time to reach the menu target, and whether the user was successful. This data is logged to Google Analytics for easy access and analysis.

## Build Instructions
1. Clone this repo
2. Download and install Android studio (https://developer.android.com/studio/index.html)
3. Ensure the latest Android SDK is downloaded during installation
4. Run Android Studio and Choose 'File -> Open' menu option
5. Browse to the root of cloned repo
6. Project will load and attempt to build
   - If you see errors in the 'Messages' panel regarding the build.gradle file and failing to resolve artifacts, choose the option to install the artifact and sync the project
7. Ensure project builds successfully by choosing 'Build -> Make Project'
8. Connect Android device running at least Android 5.0 to computer via USB 
    - You may need to install device specific drivers
    - Ensure USB mode is set to "Transfer files"
    - Ensure USB debugging is enabled
9. Select 'Run -> Run app' OR 'Run -> Debug app' to run the app on the connected device
    - Your connected device should be listed in a dialog that pops up
    - Choose OK
10. Click OK if a dialog pops up on the device asking for permission to debug

11. App should be visible on screen and responding to rotational movement
12. If you see a dialog about "Instant Run", you can choose to install it but it's not necessary

## UX Facilitator Instructions
1. Ensure device used for usability sessions has an active internet connection so that data can be logged
2. Upon app startup, a menu navigation point is shown to the user at the top
3. Inform the user that by tilting the device in the direction of the arrows, the ball moves and will select a menu item when the ball enters the edge of the circle
4. Instruct the user to attempt to navigate to the prompted menu item by selecting menu items they deem is most appropriate
5. A message appears at the bottom of the screen when the last menu item has been selected and a new target is prompted on screen
6. After all menu targets have been attempted, a dialog appears to the user to return the device to the faciliator
7. Press 'Restart Session' when a another user is ready for a usability session

## Data analysis
Quantitative data for each user session is seamlessly logged to a Google Analytics account for analysis. Be aware that although the data is sent immediately, it can take up to 48 hours for Google's servers to process it and appear in the Analytics dashboard. The following data is available for analysis:

- Intended menu target
- Duration in seconds the user spent navigating the menu tree
- Success or failure of the user to reach the intended target

Google account details are provided on a need to know basis

