Universal Pokemon Randomizer 1.6.0
by Dabomstew, 2012-14

Homepage: http://pokehacks.dabomstew.com/randomizer/index.php
Source: https://code.google.com/p/universal-pokemon-randomizer/

Contents
--------
1. Introduction
2. Acknowledgements
3. Libraries Used
4. Features
5. How To Use
6. Games/ROMs supported
7. License
8. Known Issues
9. Useful/Interesting Links

Introduction
------------

This program allows you to customize your experience playing the Pokemon games
by randomizing many aspects of them. This means that the Pokemon you get at
the start of the game, the Pokemon you fight in the wild and the Pokemon 
trainers have can all be made completely different from the original game.

Acknowledgements
----------------
Many people have put countless hours of their time into researching the
structures contained within Pokemon games over the years. Without the research
done by these people, this randomizer would not exist, or would have taken a
lot longer to create.

To see the full list of contributions, see 
http://pokehacks.dabomstew.com/randomizer/acks.php

Libraries Used
--------------
  * thenewpoketext by loadingNOW for generation 4 text handling
    http://pokeguide.filb.de/ (source @ https://github.com/magical/ppre )
  * PPTXT by ProjectPokemon for generation 5 text handling
    http://projectpokemon.org/forums/showthread.php?11582-PPTXT-Text-editing-tool
  * Code from ndstool for NDS file extraction/creation (under GPL)
    http://sourceforge.net/p/devkitpro/ndstool/
  * Code from CUE's Nintendo DS Compressors for arm9.bin (de)compressing
    (under GPL)
	http://gbatemp.net/threads/nintendo-ds-gba-compressors.313278/
  * DSDecmp for LZ11 decompression (under MIT)
    http://code.google.com/p/dsdecmp/
 
Features
--------
Below is a list of what exactly can be randomized. You may not understand all
of it if you haven't played Pokemon games much before.
 
  * The Starter Pokemon choices
  * The Wild Pokemon you encounter in grass, caves and other places
  * The Pokemon that Trainers use against you.
  * The base stats which define the potential of each Pokemon
  * The elemental types of each Pokemon
  * The abilities of each Pokemon, in games where Pokemon have abilities
  * The moves that Pokemon learn by gaining levels
  * The contents of each TM which can be taught to Pokemon to give them
    additional moves 
	(HM moves are not changed to make sure you can still beat the game)
  * The ability of each Pokemon to learn each TM or HM move
  * The "static" Pokemon which you either are given, fight on the overworld,
    or are sold.
  * The names of trainers & the classes they belong in
  * The moves that Move Tutors teach, in certain games where they are
    particularly significant.
  * The items that it is possible for Pokemon to hold in the wild, and in
    certain situations the items that Pokemon given to you are holding.
  * The Pokemon that are traded to you by in-game NPCs (as opposed to
    trades with real people)
  * The items you pick up off the ground, from either item balls or
    hidden spots.
  
How To Use
----------
Extract this ZIP file before doing anything else!!!

Make sure you have Java 1.6 or later installed, then run the included JAR file.

In some situations, you will be able to just double-click on the JAR file and
the program will run. If not, execute the following command from your command
line in the directory where you have extracted the program:

java -jar randomizer.jar

From there you can open a ROM (legally acquired), customize what you want to be
randomized, then save the randomized ROM.

Games/ROMs supported
--------------------

Version 1.6.0 supports the following official ROMs:

  * Pokemon Red (any)
  * Pokemon Blue (any)
  * Pokemon Green (J)
  * Pokemon Yellow (any)
  * Pokemon Gold (any except Korean)
  * Pokemon Silver (any except Korean)
  * Pokemon Crystal (any)
  * Pokemon Ruby (any)
  * Pokemon Sapphire (any)
  * Pokemon Emerald (any)
  * Pokemon FireRed (any)
  * Pokemon LeafGreen (any)
  * Pokemon Diamond (any)
  * Pokemon Pearl (any)
  * Pokemon Platinum (any)
  * Pokemon HeartGold (any)
  * Pokemon SoulSilver (any)
  * Pokemon Black (any)
  * Pokemon White (any)
  * Pokemon Black2 (any)
  * Pokemon White2 (any)
  
As you can see, pretty much every game except the Korean releases of Gold
and Silver are supported. This is because these releases were very much
one-offs, and have little to nothing in the way of a ROM hacking community
which would be needed to make it possible to support them.

Whilst pretty much every release of every game is supported to a good level,
this randomizer is still targeted at English games - so users of foreign
language games may still find that the functionality is a bit limited or
small amounts of English text appear ingame where they did not before.

Randomizing ROM hacks of the above games isn't a supported feature yet, but may
still be possible depending on the specifics of the hack itself. In general,
the simpler a hack is, the more likely it will be able to be randomized.

License
-------
This project and the majority of the libraries used are under the GNU GPL v3,
attached as LICENSE.txt.

Source code can be obtained from:
https://code.google.com/p/universal-pokemon-randomizer/source/browse/

Other libraries used are under more liberal licenses, compatible with the GPL.

Known Issues
------------
See https://code.google.com/p/universal-pokemon-randomizer/wiki/KnownIssues

Useful/Interesting Links
------------------------
If you have bugs, suggestions, or other concerns to tell me, contact me at
http://pokehacks.dabomstew.com/randomizer/comments.php