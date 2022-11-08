# SoCalBeach
Team members: Jiahang Zhang, Quanyong Bi, Yuchen Wang.

## How to use:
1. Clone or download the zip of the project and open it in Android studio.
2. Build gradle.
3. Check in the local.property to see if the correct API key is stored in proper location.
![image](https://user-images.githubusercontent.com/84767679/200501088-1c83b5c5-312d-45e5-87db-4da3d00e45b5.png)
### MAPS_API_KEY=AIzaSyBNF_W_dJPHr-HGw3YtFCbfMoUcvKdBlSg
4. Open a phone simulator with API 24 or higher.
5. Before launching the program, open google map and click get a current location. (This is a un-solvable bug clarified by google developer document. For any brand new device with no previous user location access, the program cannot get user's location).
6. Then you can launch the program, and will be led to sign in page.

## Short Instruction on using the program
1. To access the map page, Sign in/ Sign up first.

2. The virtual machine has limited run-speed, which can caused many unexpected bug:
  
  (a). don't press sign in/ register button over and over again.
  
  (b). In the routing system, whenever you click on a parking icon or restaurant icon, the route will be shown. **However, due to api run speed, when you click on another icon when one route is on map, you may need to clikc on the icon twice to update the route polyline. We'll try to fix the bug in future development.**

3. When you are led to the map page, zoom out to see the nearby beaches.
![image](https://user-images.githubusercontent.com/84767679/200502240-ff0ed5ba-cff4-4e3d-8b19-3ac6e566b789.png)

4. All markers are clickable. And all operations are straightforward.

5. Menu side bar can be opened from the top left icon.
6. The GPS button on right side can re-located where you at.

