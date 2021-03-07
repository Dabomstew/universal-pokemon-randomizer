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

Another option is to build in a docker container. You can build by running
`docker run -it --rm -v "$PWD":/usr/src/mymaven -v "$HOME/.m2":/root/.m2 -w /usr/src/mymaven maven:3.6-jdk-8 mvn clean package`.
This volume maps the current directory and the local maven repository into a docker container
and performs a maven build there. Make sure your current directory is the same location as
`pom.xml` for this command to work. Note that this may make your `target` directory
root-owned and prevent local executions from saving config files. You can run
`sudo chown 1000:1000 target` to return ownership to your user. You can also
delete the target folder, which should prompt for elevated permissions before
allowing the folder to be deleted. The folder is rebuilt during `mvn compile`. 

This is subject to change. Make sure you're paying attention to which version of Java
(currently this project is set to use Java 8) and Maven (3.6) you're using.

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
* Code that is blatantly stolen from somewhere else without appropriate credit.

PRs will likely be given feedback to address concerns like unit tests,
functionality, and code consistency. Approval is required prior to
a merge being granted so follow the feedback!

# Purpose
This fork was made to compile the changes from other forks into a single fork. This
repository includes a number of changes, and the resulting fixes that were implemented
for conflicts and oversights. 
