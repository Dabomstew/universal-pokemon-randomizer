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
included in official releases, but if it is, head on over to README.txt instead.

I would advise you to read this file, especially the "Fair Warning" and 
"Compiling the Program" parts. However, there is also another file detailing the
usage of the program and giving credit to those who originally made it a possibility.
Check it out if you want to.

# Fair Warning
This GitHub repo should not have any automatic updates as it points to the original
(and now archived) website. If Dabomstew ever does resume updates, there is a good
chance that the functionality in here will break and/or be removed.

# Compiling the Program

I do not plan to host any binaries (readymade programs that do not need to be 
compiled before being used) for this fork. This means that if you by chance want 
to try it out you will have to compile it by yourself. You do this by downloading 
a Java interpreter (such as Eclipse https://www.eclipse.org/downloads/),
which you will then have to learn to use. 

As this is a Maven project, you will need to create a build/run configuration which
accomplishes the equivalent of "mvn clean package", or just run it from the command
line.

This is subject to change. Make sure you're paying attention to which version of Java
(currently this project is set to use Java 8) and Maven you're using.

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

Future work will include more features that I wanted to create, such as shuffling types
and a list of Pokemon/moves with high stats.

## Differences between this Fork and Dabomstew's Original Version
 * Converted project into Maven and brought minimum java version to 8.
 * Added an option to randomize the stats and types before or after any evolution
 randomization is applied. With this, you can get more natural evolutionary lines,
 or maintain the old method of randomization. 
 * Added an option to prevent random evolutionary lines from converging (2 pokemon
 evolve into the same thing).
 * Added an option to prevent pokémon from evolving into another with a lower BST.
 * Added an option to forbid lines with split evolutions from being used when
 randomizing starters.
 * Added an option for randomizing outside of any BST cap.
 * Added an option for fast text.
 * Refactored "Guaranteed Four Moves" to work on a slider of 2-4.
 * Eevee now evolves into different typed pokemon instead of sharing types (when
 evolutions are randomized).
 * Tweaked moveset randomization to better balance types in movesets.
 * Added an option to shuffle base stats between pokemon.
 * Enabled "No Broken Moves" to be used in non-random builds.
 * Refactored "Modify Levels" to work on non-random trainers.
 * Refactored "Force Fully Evolved" to work on non-random trainers.
 * Added an option to randomize starters with 1 or 2 evolutions.
 * Added an option to balance wild type frequency.
 * Added an option to have unique starter types when randomized.
 * Added an option for evolved pokemon to appear at lower levels.
