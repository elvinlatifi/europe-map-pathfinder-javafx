# europe-map-pathfinder-javafx
This repo contains a javafx program that allows you to create nodes on a map of Europe, define connections with weights between these nodes and find the fastest path between any two nodes.  

The program uses a graphical interface to interact with the user, and in the background everything is implemented using a graph data structure. It was written in May 2021 as part of an assignment in a university course. The implementation of Dijkstra's algorithm was written without looking anything up, and since I was a novice it is far from optimal. I have since learned about priorityqueues and know how to implement the algortihm more efficiently, but want to leave this code as a snapshot of my ability at the time it was written.  

When you run the program, you can use the "File" drop down menu in the upper left hand corner, to click on "New Map". This will load in an empty map. Below is an explanation of what all the buttons do:  
- "New Place": Allows you to create a new node, by first clicking on the button and then on the map where you want the node to be placed.  
- "New Connection": Allows you to create a connection with a specified name and weight between two selected nodes (a node turns red when selected). 
- "Change Connection": Allows you to change the weight of an already existing connection. 
- "Show Connection": Allows you to see the details (name and weight) of an existing connection. 
- "Find Path": Calculates and presents the fastest path between any two selected nodes.  

There are also some more options in the "File" drop down menu:  
- "Save": Allows you to save the current state of the map, with all the nodes and connections that you have defined, in the europa.graph file. 
- "Open": Will load in whatever you have saved in the europa.graph file. 
- "Save Image": Will save an image of the map, with all the nodes and connections you have defined, in a file called "capture.png".  
- "Exit": Exists the program.


