Universal Pokemon Randomizer

By Dabomstew, fork by Voliol

Homepage: http://pokehacks.dabomstew.com/randomizer/

Forks used -
* 0xhexrobot - https://github.com/0xhexrobot/universal-pokemon-randomizer.git
* Ajarmar - https://github.com/Ajarmar/universal-pokemon-randomizer-zx.git

**Notice: New binary releases of the randomizer have been indefinitely suspended since 2016. This repository remains available to distribute source code and facilitiate forks.**

# For Program Users
If you're looking to actually *use* the randomizer as opposed to looking at the 
source code, you're reading the wrong readme file. This file shouldn't be 
included in official releases, but if it is, head on over to README.txt instead.
-by Voliol

I would advise you to read this file, especially the "Fair Warning" and 
"Compiling the Program" parts. However, there is also another file detailing the
usage of the program and giving credit to those who originally made it a possibility.
Check it out if you want to.

# Fair Warning
This GitHub repo will have live updates to the randomizer as I program them, but
it won't be at all pretty. Expect lots of spaghetti code and very little use of
best practices. Individual commits may well break randomization, so you might have
to look back in the history a little before you find something usable.

# Compiling the Program
-by Voliol

I do not plan to host any binaries (readymade programs that do not need to be 
compiled before being used) for this fork. This means that if you by chance want 
to try it out you will have to compile it by yourself. You do this by downloading 
a Java interpreter (such as Eclipse https://www.eclipse.org/downloads/),
which you will then have to learn to use. I won't go into details, as they
are sure to change, but it isn't very difficult and there are plenty of
tutorials out there to use. I wish you the best of luck :)

# Forks
Fork as you like, as long as you obey the terms of the included license.

# Pull Requests
If you have fixed a bug or made a cool new feature, feel free to send in a pull
request for me to review. Pull requests will be accepted/denied completely at my
own discretion and may not be responded to in a timely manner if I'm busy.

## Pull Requests that will probably be accepted:
* Well-made new features: they should obviously be free from bugs to your knowledge.
If applicable, they should be universal (major features such as randomizing a new
aspect of the games fall under this, while more minor one-off settings / misc
tweaks can be for only one game or one generation if appropriate)
* Bug fixes

## Pull Requests that will probably be denied:
* Anything that's just pure refactoring, unless you have very good reasoning
for its inclusion. Sorry, but you will need to convince me that it's necessary.
* Code that is blatantly stolen from somewhere else without appropriate credit.


# Purpose
The purpose of this fork is to fix minor issues, but also to rebalance/change some things. As such, you might find that some of the things that are changed are not necessarily _better,_ but rather just different. Regardless, these changes have the goal of making the randomizer more fun to play, so hopefully you (the person reading this) will agree that the changes are good.

Have a look at the [release page](https://github.com/Ajarmar/universal-pokemon-randomizer-zx/releases) for changelogs.

Please note that this fork was made just because I wanted to make the races with my race crew more fun, and I don't want it to be more ambitious than that. Additions/updates will be done at my discretion, so don't contact me to tell me to add things to the randomizer. Thanks!
-by Voliol

I am yet not very familiar with the nature of GitHub, and exactly how 
Pull Requests work. However, if you do create one I'll look into it and see both
how it works and whether I am interested in such a collaboration. Being part of 
creating the ultimate Randomizer I think would be grand, if such a oppurtunity
arises.

## Differences between this Fork and Dabomstew's Original Version
-by Voliol
 - Added an option to randomize the evolutions directly after the base stat updates, before those other things (base stats, types, abilities) that could be set to follow evolutions. With this, you will get more natural evolutionary lines. 
 - Added an option to prevent random evolutionary lines from converging.
 - Added an option to prevent pokémon from evolving into another with a lower BST.
 - Added an option to forbid lines with split evolutions from being used when choosing "Random (basic Pokémon with 2 evolutions)" as the option in starter randomization.
 - The export log contains a section meant to make it compatible with the 
  emerald-randomizer's family pallete swaps. However, it does not fully work 
  as intended, due to issues with the in-game indexes of gen III Pokémon.
