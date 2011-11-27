to run the com.all.login.Client 

* under windows environment set this as vm arg

-Djava.library.path="${workspace_loc:client/src/main/os/windows/native}"
-Djna.library.path="${workspace_loc:client/src/main/os/windows/native}"
-Xms512m 
-Xmx512m


* under mac osx environment set this as vm arg

-Djava.library.path="${workspace_loc:client/src/main/os/mac/native}:${workspace_loc:client/src/main/os/mac/mozilla}"
-Djna.library.path="${workspace_loc:client/src/main/os/mac/native}:${workspace_loc:client/src/main/os/mac/mozilla}"
-Xms512m 
-Xmx512m
-d32

* under linux environment set this as vm arg

-Djava.library.path="${workspace_loc:client/src/main/os/linux/native}:${workspace_loc:client/src/main/os/linux/mozilla}"
-Djna.library.path="${workspace_loc:client/src/main/os/linux/native}:${workspace_loc:client/src/main/os/linux/mozilla}"
-Xms512m 
-Xmx512m