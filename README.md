# WorkOut
A general purpose, android exercise app.


#Purpose of the App

This app is aimed towards users who know what exercises they want to do, to allow them to store all their exercises together. The exercises are displayed as a list for them to choose. For each exercise listed, the user can input the number of sets, and the rest time between sets, which are also saved, and can be updated.


#How to use:

In the starting screen of the app, there are three possible options the user can do:
  - Add a new exercise to the list by tapping the "+" button
  - Remove specific exercise names that are on the list by tapping the "-" button
  - Start an exercise that is listed by tapping on it (they are all listed as buttons)
  
 
#Add an Exercise:
 
To add a new exercise to the exercise list, tap on the "+" button, which will display an alert window for you to type the exercise name (whatever you want to call it). When finished, tap on the "done" button to exit the window, and you will see your exercise name on the list. You can add more exercises, one at a time by repeating this process.
  
  
#Remove an Exercise: 

To remove an exercise from the list, tap on the "-" button, which will display an alert window for you to select the exercises that you want removed. When finished, tap on the "done" button to exit the window, and the chosen exercises will now be gone from the list.


#Start an Exercise:

To start an exercise, tap on the exercise that you wish to do, and the app will change to another screen that prompts you to input the number of sets, and the rest time. Tap the "update sets" button to save (to the database) the number of sets value, and similarly, tap the "update timer" button to save the rest time value. Note that these two inputs must be integers, otherwise a toast message will warn you to provide an integer, and the incorrect format will not be saved.

Having saved the inputs, the next time you open this exercise, the number of sets, and rest time input fields will be filled with the previous values.

You must count the repetitions yourself. Upon reaching the number of repetitions that you consider as a set, tap the "start" to begin the timer, which will count down from the rest time. Once the timer ends, do another set, and so on. Upon completing the final set, an alert window appears to congradulate you, and you can tap the "done" button to return to the starting screen, and choose another exercise from the list.

  
#Possible Updates:

- when an exercise is completed, change its button to a different color (red to green?)
- reset the color of the exercise buttons to default (uncompleted exercise) color after a specific real-time 
- when an exercise in completed, move the position of its button to the bottom of the list
- check for duplicate names, and deny adding name if it is already present on the list
- user can sort the list, and save the order
- if previous input values exists (in database), do not auto-display the soft keyboard
