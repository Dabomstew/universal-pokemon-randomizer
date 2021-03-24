# Developer Guide
This details various areas of the randomizer to enable other developers to contribute. If you discover something new, or make a relevant change, please update this document!

# SettingsTest is failing!
It's probably not an issue with the test, but with your environment. 
There is a known bug where running AssertJ Swing causes the mouse to fail to locate the correct screen position. https://github.com/assertj/assertj-swing/issues/235
Running the tests individually usually works. 
You can also try commenting out the line that says `this.frame.moveTo(new Point(10, 10));` in AbstractUIBase. 
If neither of these works, then try running the test by hand and see if it's actually detecting broken functionality.
If it is, congratulations! You found a bug that you need to fix.

# How do I write a test for SettingsTest?
The controls need to have their `name` property set or else the search mechanism will return `null`. Search utilities are provided in AbstractUIBase for the common `JCheckBox`, `JRadioButton` and `JSlider` objects. Passing a name to those utility functions should return a fixture that enables the automated robot to interact with it.

Once an action has been performed on a UI control, it's best to collect the current settings from the UI and verify everything is in the desired state. This gives granular feedback for what set of procedures lead to the fail state.

Each test should attempt to cover the following cases
* Sanity Check - Initialized state has no selections made (unless it's a default like "unchanged")
* Toggle on - Setting is active and state of `isSelected` is true
* Toggle off - Setting is inactive and state of `isSelected` is false
* Enabled when parent control is selected - Setting is enabled and state of `isSelected` is false (it's available not not chosen yet)
* Disabled when parent control is unselected - Setting is disabled and state of `isSelected` is false (any choice is removed)
* Saving settings is successful - No error when making the settings striing
* Loading settings is successful - Any state saved is restored correctly and no loading errors
If you have a less common radio button or slider, adjust the above criteria to exercise any conditions this could cover.
It may be worthwhile to divide a test case into multiple functions. This is perfectly acceptable.

There are some utility methods included to assist with generic patterns.
* TestCheckboxBasedOnRadioButton - Allows testing when a checkbox is disabled on 1 radio button (default) and enabled on another (trigger). Requires appropriate method references to tie into Settings.

# How do I write a unit test for my function?
There's no one way of doing it. Most of the work is mocking or disabling features that break the regular flow but are not required for proving the function behaves as expected with a particular input.

One thing to consider is if there are any situations where multiple selections create a distinct scenario. For example the `changeMethods` unit test considers whether `removeTradeEvos` is selected since that removes choices from the pool that `changeMethods` uses. This creates a situation where there may be duplicates or an error state when expected values are missing.

The best advice is to follow examples in `LogTest` which encounters the most scenarios where functionality is hit but not needed.

# Adding Options to Interface

This project has a number of files that need to be updated correctly in order to
support any new options. 

Misc tweaks are not applied directly to the GUI, but are instead dynamically created
as part of `initTweaksPanel` in `RandomizerGUI.java`. Please see `Misc Tweaks` section
later in this document.

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
Adjust the `text`, `toolTip`, and `name` by clicking the `...` box to the right.
Ensure `Resource Bundle` is selected, `com.dabomstew.pkrandom.gui.Bundle` is the bundle name.
All tool tips need to start with `<html>` for proper rendering. Use `<br />` for line breaks.
Fill the `name` field the same as the variable. This enables the GUI Tester to
find it by name and makes it consistent with the variable name.

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

## Misc Tweaks
As stated before, Misc Tweak options are dynamically generated based on the values present
in the `allTweaks` list of `MiscTweak.java`. Simply add your option to the list, doubling
the value of the last option present in the list (e.g. if the last option's first argument
is `4096`, yours must be `8192`). This enables the Misc Tweak to be saved to the proper bit and
not interfere with any other setting. 

You will also need to manually add the `toolTipText` and `name` to `Bundle.properties`
as these are required fields in the constructor. The format must be `CodeTweaks.<field>.name`
and `CodeTweaks.<field>.toolTipText` where <field> is the 2nd argument provided in the
Misc Tewak constructor. Remember that you must start the `toolTipText` value with `<html>`.
See other Misc Tweaks for examples.

As the enabling and disabling is handled elsewhere, the only thing left is to add the misc
tweak to the list of options for whichever generation your tweak applies to. Open the
RomHandler, edit the `miscTweaksAvailable` function and add the tweak to the `available`
field. Then update the `applyMiscTweak` function to perform the desired modification.
It is recommended to make a function that does this to enable future enhancements and
easier unit testing.
