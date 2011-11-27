#!/bin/bash

buildClassPath() {
        jar_dir=$1
        if [ $# -ne 1 ]; then
                echo "Jar directory must be specified."
                exit 1
        fi
        class_path=
        c=1
        for i in `ls $jar_dir/*.jar`
        do
                if [ "$c" -eq "1" ]; then
                        class_path=${i}
                        c=2
                else
                        class_path=${class_path}:${i}
                fi
        done
        echo $class_path
        #return $class_path
}

dir=`pwd`/System/Jar
lib_path=`pwd`/System/Lib
lib_browser=`pwd`/System/mozilla
lib=$lib_path":"$lib_browser
CP=`buildClassPath $dir`

#used to find the i18n.properties file
CP=.:./System:$CP

/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Commands/java -Xmx1024m -Xms128m -Djava.library.path=$lib -Djna.library.path=$lib -d32 -cp $CP com.all.login.Client