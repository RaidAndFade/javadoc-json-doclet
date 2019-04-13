# if a javasrc directory exists, we skip retreiving it automatically. Simply because fuck you.
if [-f "javasrc"]; then
    echo "A javasrc directory exists, skipping extraction"
else
    # Use a command to find the JRE folder, hoping that this is inside the JDK directory (it usually is)
    #  Then, following the assumption that this is a JDK runtime, we link to jdkfolder/src.zip, which is always tehre
    JPTH=$(java -XshowSettings:properties -version  2>&1 > /dev/null | grep "java.home" | sed s/[\ =]*java.home\ =\ //)/../src.zip
    if [[ $JPTH != *"jdk"* ]]; then
        echo "Could not locate JDK folder automatically. Try running the next command manually."
        exit 1
    fi
    if [-f $FILE]; then
        echo "Found JDK folder but for some reason you did not have a src.zip where it was expected. Try running the command manually"
        exit 1
    fi
    mkdir javasrc
    cd javasrc
    unzip $JPTH
    cd ..
fi
javadoc -doclet com.raidandfade.JsonDoclet.Main -docletpath ./builds/json-jdoc.jar -subpackages . -sourcepath javasrc