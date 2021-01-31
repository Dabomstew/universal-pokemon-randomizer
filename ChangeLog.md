# Differences between this Fork and Dabomstew's Original Version
## 1.8.0
 * Converted project into Maven and brought minimum java version to 8.
 * Added an option to randomize the stats and types before or after any evolution randomization is applied. With this, you can get more natural evolutionary lines, or maintain the old method of randomization. 
 * Added an option to prevent random evolutionary lines from converging (2 pokemon evolve into the same thing).
 * Added an option to prevent pokémon from evolving into another with a lower BST.
 * Added an option to forbid lines with split evolutions from being used when randomizing starters.
 * Added an option for randomizing outside of any BST cap.
 * Added an option for fast text.
 * Refactored "Guaranteed Four Moves" to work on a slider of 2-4.
 * Eevee now evolves into different typed pokemon instead of sharing types (when evolutions are randomized).
 * Tweaked moveset randomization to better balance types in movesets.
 * Added an option to shuffle base stats between pokemon.
 * Enabled "No Broken Moves" to be used in non-random builds.
 * Refactored "Modify Levels" to work on non-random trainers.
 * Refactored "Force Fully Evolved" to work on non-random trainers.
 * Added an option to randomize starters with 1 or 2 evolutions.
 * Added an option to balance wild type frequency.
 * Added an option to have unique starter types when randomized.
 * Added an option for evolved pokemon to appear at lower levels.
 * Logs are now in HTML format and contain CSS formatting to enhance readability.

 ## 1.8.1
 * Gen 3 - Wally is now a rival which can carry a starter or team.
 * Rivals and Friends will no longer have duplicate team members. (the only exception is when a team is carried and a pre-evo is used)
 * Rivals and Friends can carry their team in addition to their starter.
 * Pokemon with stats above 190 or BST above 590 are now tagged as `BIG` in logs.
 * Moves with power above 95 are now tagged as `BIG` in the logs.
 * Adjusted how HP is randomized to avoid excessive skewing, leading to more stats in other areas (minimum of 35 instead of 20 and reduced HP growth by 10%).
 * Pokemon types can now be shuffled on a 1-for-1 basis. Does not affect moves.
 * Pokemon types can be randomized for only 1 type (2nd type is preserved).
 * Split evolutions are no longer excluded from inheriting base form types.
 * Eeveelutions are guaranteed to have different types when types are randomized.
 * Base stats can now be shuffled between pokemon. Only applies to similar power levels.
 * Base stats can also be shuffled in order and then between pokemon. Same as above.
 * Starter pokemon can now be limited based on their BST.
 * Starter pokemon can now be limited to just base forms.
 * Updated logs to show old and new trainer teams for comparison.
 * Updated trade logging to increase readability.
 * Added section to show new evolution chains through the 3rd form.
 * Added seed and settings to log (as opposed to just at the end of randomization).

 ## 1.8.2
 * When random retaining types with follow evolutions checked, Pokemon with two types evolving into Pokemon with one type now passes the new type instead of retaining the shared type. Fixes issue where Weepinbell could evolve into Snorlax and not share any types.
 * Added code tests to ensure new features do not break existing features.
 * Pokemon with BST above 490 are now tagged as BIG in the logs when randomizing a Gen 1 game due to there only being 5 stats instead of the usual 6 for Gen 2 onwards.
 * Log generation has been consolidated into one area of the code. This should make future log enhancements easier, as well as improve code readability.
 * Table of Contents added to top of log to jump to section of interest
 * Gen 3 and Gen 4 Feebas can now evolve by happiness when removing impossible evolutions
 * Gen 5 and Gen 6 move updates can be applied independently of one another
 * Black/White Cilan, Chili, Cress now have types that are superior to the player's chosen starter
 * As an additional option to the previous change, players can opt for a defensively resistant opponent. For instance, Magnemite would normally face a Ground type, but this option makes the Gym use an Electric type as Electric resists both Electric and Steel.
 * Modified the following sections of in-game text to match choices
    * Striaton City Gym leaders will use their true types when using type-theming on Gym Leaders, or use generic terms when completely random
   * Nacrene City Gym Guy will use true type and appropriate weakness when using type-theming on Gym Leaders, or use generic terms when completely random
   * Castelia City item guy will use correct starter names when giving type-boosting items. Items will not match the types of the new starters
   * Castelia City cafe guy will use correct types when referring to Striaton City leaders, or use generic terms when completely random
 * Added `Normalize` and `Stall` to list of negative abilities that can be banned