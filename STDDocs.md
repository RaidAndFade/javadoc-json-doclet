# How to get JSON Docs for all of java's Standard Library

(Notice that this tutorial is not noob friendly, This is simply because do not think noobs will ever need/want to handle over 5000 json files, if you have this insane desire, I can only assume you are crazy, like me.)

Run this magical script that does everything for you

[Manual instructions](#doing-it-manually)

## Magic Scripts

### Linux Magic Script

The magic script is [stdlib-javadoc.sh](stdlib-javadoc.sh), It tries its best to do everything automatically. 

If it cannot complete, you need to do it manually.

### Windows Magic Script

I'm on OSX right now and don't have access to a windows machine, do it manually and PR a magic script if you feel generous.

Make sure if you PR, your magic script must automatically find the JDK's src.zip since that is the entire premise of the magic script.

## Doing it manually

Ok so basically, these are the steps

1. Find your JDK folder, (however method you like, it depends on OS. you can typically find it with `java -XshowSettings:properties -version`)
2. Find the `src.zip` file, this is your new friend
3. Extract the `src.zip` file somewhere accessible
4. Run `javadoc -doclet com.raidandfade.JsonDoclet.Main -docletpath <PATH TO JAVADOC-JSON DOCLET> -subpackages . -sourcepath <PATH TO EXTRACTED SRC.ZIP DIRECTORY>`, replacing the two variables in that string

Note: if you get up to step 3, you can name your extracted folder to "javadoc" and run the magic script.