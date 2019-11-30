Universal Pokemon Randomizer

By Dabomstew

Homepage: http://pokehacks.dabomstew.com/randomizer/

Forks used -
* 0xhexrobot - https://github.com/0xhexrobot/universal-pokemon-randomizer.git
* Ajarmar - https://github.com/Ajarmar/universal-pokemon-randomizer-zx.git
* Challenert - https://github.com/challenert/universal-pokemon-randomizer
* Hejsil - https://github.com/Hejsil/universal-pokemon-randomizer
* Hwaterke - https://github.com/hwaterke/universal-pokemon-randomizer
* Juanmferreira93 - https://github.com/juanmferreira93/universal-pokemon-randomizer
* Mikabre - https://github.com/mikabre/universal-pokemon-randomizer
* Tj963 - https://github.com/tj963/universal-pokemon-randomizer
* TricksterGuy - https://github.com/TricksterGuy/universal-pokemon-randomizer
* Ttgmichael - https://github.com/ttgmichael/universal-pokemon-randomizer
* Voliol - https://github.com/voliol/universal-pokemon-randomizer

**Notice: New binary releases of the randomizer have been indefinitely suspended since 2016. The original repository remains available to distribute source code and facilitiate forks.**

# For Program Users
If you're looking to actually *use* the randomizer as opposed to looking at the 
source code, you're reading the wrong readme file. This file shouldn't be 
included in official releases, but if it is, head on over to UserGuide.txt instead.

I would advise you to read this file, especially the "Fair Warning" and 
"Compiling the Program" parts. However, there is also another file detailing the
usage of the program and giving credit to those who originally made it a possibility.
Check it out if you want to.

# Fair Warning
This GitHub repo should not have any automatic updates as it points to the original
(and now archived) website. If Dabomstew ever does resume updates, there is a good
chance that the functionality in here will break and/or be removed.

# Compiling the Program

Binaries are hosted at `https://github.com/brentspector/universal-pokemon-randomizer/releases`

As this is a Maven project, you will need to create a build/run configuration which
accomplishes the equivalent of `mvn clean package`, or just run it from the command
line. This will build a jar for you under the `target` folder which contains any
modifications to the code you have made.

This is subject to change. Make sure you're paying attention to which version of Java
(currently this project is set to use Java 8) and Maven (3.6) you're using.

# Adding Options to Interface

This project has a number of files that need to be updated correctly in order to
support any new options. 

It is strongly recommended that you download `NetBeans` to take advantage of their
Java Swing editor, since this project was built using it. NetBeans should be able
to import the project as Eclipse (you will need a plug-in for this). Once you open
`RandomizerGUI.java` in NetBeans, it should automatically switch over to a design
perspective where you can edit it to your heart's content. Edits here will
automatically update the `RandomizerGUI.form`, `RandomizerGUI.java`, and 
`Bundle.properties` file.

**NOTE: NetBeans does make a number of temp/lock files to keep you from editing
other files while making design changes. It is best to have another editor like
`Eclipse` or `VS Code` to work around NetBean's locks. Also note that you should 
not commit any temp or lock files to git. **

When working with NetBeans, I recommend first editing the variable name (under Code tab)
for any new GUI elements added. This will allow all new functions to be named correctly
rather than named `jCheckBox1`. 

Under the Properties tab, uncheck `enabled` so that the element starts disabled until
a ROM is loaded. Check `horizontally resizable` to allow element to match the text size.
Adjust the `text` and `toolTip` by clicking the `...` box to the right.
Ensure `Resource Bundle` is selected, `com.dabomstew.pkrandom.gui.Bundle` is the bundle name.
All tool tips need to start with `<html>` for proper rendering. Use `<br />` for line breaks. 

Under the Code tab, use `actionPerformed` when another element will be enabled/disabled
based on whether your element is selected or not. 

You will also need to edit the `Settings.java` and `RandomizerGUI.java` files to use any new
settings you create. 

## Settings.java
Add a variable to represent your new option. This will likely be a `private boolean`.
Also create 2 functions, 1 to get the value of this variable (most likely a `public boolean`), 
and 1 to set it (most likely a `public Settings`).

Locate the appropriate byte to put your option under. If the byte already has 8 options,
you will need to locate an overflow area, or create one yourself. If you make a new byte,
you will need to update `LENGTH_OF_SETTINGS_DATA` to match the new byte location. This
MUST be the last byte being written or read.

If you have a number, it will need to conform to the binary number system.
For example, one byte can contain a value in the range of -128 to 127. Two bytes (a short)
can contain a value in the range of -32,768 to 32,767. There is a `write2ByteInt` in case
you need a short. If you only need 1 byte, then use `0x80 | (your_value)`.

Reading a data setting requires using `data[the_byte_location]`. For shorts, use `read2ByteInt`.
For bytes, use `data[the_byte_location] & 0x7F`.

## RandomizerGUI.java

Update `restoreStateFromSettings` to retore value to your element.
Update `createSettingsFromState` to save the value of your element to settings.

Update `initialFormState` to set your element's `selected` and `enabled` to false upon ROM
load or form reset. Update `visibility` as needed.

Use `romLoaded` if your element should be active upon ROM load. Use `enableOrDisableSubControls`
if your element should be active based on the state of another element. Remember to set the
state back to default if the dependent state changes.

If not done so already, and if you have an element with an `actionPerformed` event, update
the function to call `this.enableOrDisableSubControls()`.

# Contacting Me
Please only contact me with the following

## Bug Reports
A bug report must contain the following
- Game version used
- Options selected
- Expected outcome
- Erroneous outcome

## Feature Requests
A feature request must contain the following
- What game version you're planning to use it on 
- What you want in this feature
- If it's similar to any existing feature in the program

## Note
I will try to reply to messages, but do not guarantee a response time. 
Additionally, I will decline bugs or features which I feel take too 
much time to finish.

# Forks
Fork as you like, as long as you obey the terms of the included license.

# Pull Requests
If you have fixed a bug, feel free to send in a pull request for me to 
review. Pull requests will be accepted/denied completely at my own
discretion and may not be responded to in a timely manner if I'm busy.

## Pull Requests that will probably be denied:
* Anything that's not a bug fix.
* Code that is blatantly stolen from somewhere else without appropriate credit.

# Purpose
This fork was made to compile the changes from other forks into a single fork. This
repository includes a number of changes, and the resulting fixes that were implemented
for conflicts and oversights. 