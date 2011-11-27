!define os_hklm 'HKLM "SOFTWARE\Microsoft\Windows NT\CurrentVersion"'
!define var_hklm 'HKLM "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"'
!define cpu_hklm 'HKLM "HARDWARE\Description\System\CentralProcessor\0"'
!define TRACKERFILE "ipTrackerFile.properties"
!define IPTRACKERFILE "$TEMP\${TRACKERFILE}"

!include LogicLib.nsh
!include WordFunc.nsh
!include "x64.nsh"


Function ConfigReadProperties
    !define ConfigReadProperties '!insertmacro ConfigReadPropertiesCall'
 
    !macro ConfigReadPropertiesCall _FILE _ENTRY _RESULT
        Push '${_FILE}'
        Push '${_ENTRY}'
        Call ConfigReadProperties
        Pop ${_RESULT}
    !macroend
 
    Exch $1
    Exch
    Exch $0
    Exch
    Push $2
    Push $3
    Push $4
    ClearErrors
 
    FileOpen $2 $0 r
    IfErrors error
    StrLen $0 $1
    StrCmp $0 0 error
 
    readnext:
    FileRead $2 $3
    IfErrors error
    StrCpy $4 $3 $0
    StrCmp $4 $1 0 readnext
    StrCpy $0 $3 '' $0
    StrCpy $4 $0 1 -1
    StrCmp $4 '$\r' +2
    StrCmp $4 '$\n' 0 close
    StrCpy $0 $0 -1
    goto -4
 
    error:
    SetErrors
    StrCpy $0 ''
 
    close:
    FileClose $2
 
    Pop $4
    Pop $3
    Pop $2
    Pop $1
    Exch $0
FunctionEnd

Function GetRegeditData
    Var /GLOBAL OSNAME
    Var /GLOBAL OSVERSION
    Var /GLOBAL OSLANGUAGE
    Var /GLOBAL CPUNUMBER
    Var /GLOBAL CPUARCHITECTURE
    Var /GLOBAL CPUMODEL
    Var /GLOBAL RAM

    ; Get OS Information
    ReadRegStr $OSNAME ${os_hklm} "ProductName"
    ReadRegStr $OSVERSION ${os_hklm} "CurrentBuildNumber"
    
    Push $0
    System::Alloc "${NSIS_MAX_STRLEN}"
    Pop $0
    System::Call "Kernel32::GetLocaleInfo(i,i,t,i)i(2048,0x2,.r0,${NSIS_MAX_STRLEN})i"
    StrCpy $OSLANGUAGE $0

    ; CPU
    ReadRegStr $CPUNUMBER ${var_hklm} "NUMBER_OF_PROCESSORS"
    ReadRegStr $CPUARCHITECTURE ${var_hklm} "PROCESSOR_ARCHITECTURE"
    ReadRegStr $CPUMODEL ${cpu_hklm} "ProcessorNameString"

    ; RAM
    ${If} ${RunningX64}
        System::Alloc 64
        Pop $1
        System::Call "*$1(i64)"
        System::Call "Kernel32::GlobalMemoryStatusEx(i r1)"
        System::Call "*$1(i.r2, i.r3, l.r4, l.r5, l.r6, l.r7, l.r8, l.r9, l.r10)"
        System::Free $1
    ${Else}
        System::Alloc 32
        Pop $1
        System::Call "Kernel32::GlobalMemoryStatus(i r1)"
        System::Call "*$1(i.r2, i.r3, i.r4, i.r5, i.r6, i.r7, i.r8, i.r9)"
        System::Free $1
    ${EndIf}
    StrCpy $RAM "$4B"
FunctionEnd


Function DriveSpaceUrl
    !define DriveSpaceUrl '!insertmacro DriveSpaceUrlCall'
 
    !macro DriveSpaceUrlCall _DRIVE _OPTIONS _RESULT
        Push '${_DRIVE}'
        Push '${_OPTIONS}'
        Call DriveSpaceUrl
        Pop ${_RESULT}
    !macroend
 
    Exch $1
    Exch
    Exch $0
    Exch
    Push $2
    Push $3
    Push $4
    Push $5
    Push $6
    ClearErrors
 
    StrCpy $2 $0 1 -1
    StrCmp $2 '\' 0 +3
    StrCpy $0 $0 -1
    goto -3
    IfFileExists '$0\NUL' 0 error
 
    StrCpy $5 ''
    StrCpy $6 ''
 
    option:
    StrCpy $2 $1 1
    StrCpy $1 $1 '' 1
    StrCmp $2 ' ' -2
    StrCmp $2 '' default
    StrCmp $2 '/' 0 -4
    StrCpy $3 -1
    IntOp $3 $3 + 1
    StrCpy $2 $1 1 $3
    StrCmp $2 '' +2
    StrCmp $2 '/' 0 -3
    StrCpy $4 $1 $3
    StrCpy $4 $4 '' 2
    StrCpy $2 $4 1 -1
    StrCmp $2 ' ' 0 +3
    StrCpy $4 $4 -1
    goto -3
    StrCpy $2 $1 2
    StrCpy $1 $1 '' $3
 
    StrCmp $2 'D=' 0 unit
    StrCpy $5 $4
    StrCmp $5 '' +4
    StrCmp $5 'T' +3
    StrCmp $5 'O' +2
    StrCmp $5 'F' 0 error
    goto option
 
    unit:
    StrCmp $2 'S=' 0 error
    StrCpy $6 $4
    goto option
 
    default:
    StrCmp $5 '' 0 +2
    StrCpy $5 'T'
    StrCmp $6 '' 0 +3
    StrCpy $6 '1'
    goto getspace
 
    StrCmp $6 'B' 0 +3
    StrCpy $6 1
    goto getspace
    StrCmp $6 'K' 0 +3
    StrCpy $6 1024
    goto getspace
    StrCmp $6 'M' 0 +3
    StrCpy $6 1048576
    goto getspace
    StrCmp $6 'G' 0 error
    StrCpy $6 1073741824
 
    getspace:
    System::Call /NOUNLOAD 'kernel32::GetDiskFreeSpaceExA(t, *l, *l, *l)i(r0,.r2,.r3,.)'
 
    StrCmp $5 T 0 +3
    StrCpy $0 $3
    goto getsize
    StrCmp $5 O 0 +4
    System::Int64Op /NOUNLOAD $3 - $2
    Pop $0
    goto getsize
    StrCmp $5 F 0 +2
    StrCpy $0 $2
 
    getsize:
    System::Int64Op $0 / $6
    Pop $0
    goto end
 
    error:
    SetErrors
    StrCpy $0 ''
 
    end:
    Pop $6
    Pop $5
    Pop $4
    Pop $2
    Pop $1
    Pop $0
    Exch $3
FunctionEnd

Function GetDrivesUrl
    !define GetDrivesUrl '!insertmacro GetDrivesUrl'
    
    !macro GetDrivesUrl
    Push $0
    Push 'HDD'
    GetFunctionAddress $0 'GetDrivesUrlVariable'
    Push '$0'
    Call GetDrivesUrl
    Pop $0
    !macroend
    
    Exch $1
    Exch
    Exch $0
    Exch
    Push $2
    Push $3
    Push $4
    Push $5
    Push $8
    Push $9
    
    System::Alloc 1024
    Pop $2
    
    StrCmp $0 '' 0 typeset
    StrCpy $0 ALL
    goto drivestring
    
    typeset:
    StrCpy $5 -1
    IntOp $5 $5 + 1
    StrCpy $8 $0 1 $5
    StrCmp $8$0 '' enumex
    StrCmp $8 '' +2
    StrCmp $8 '+' 0 -4
    StrCpy $8 $0 $5
    IntOp $5 $5 + 1
    StrCpy $0 $0 '' $5
    
    StrCmp $8 'FDD' 0 +3
    StrCpy $5 2
    goto drivestring
    StrCmp $8 'HDD' 0 +3
    StrCpy $5 3
    goto drivestring
    StrCmp $8 'NET' 0 +3
    StrCpy $5 4
    goto drivestring
    StrCmp $8 'CDROM' 0 +3
    StrCpy $5 5
    goto drivestring
    StrCmp $8 'RAM' 0 typeset
    StrCpy $5 6
    
    drivestring:
    System::Call 'kernel32::GetLogicalDriveStringsA(i,i) i(1024,r2)'
    
    enumok:
    System::Call 'kernel32::lstrlenA(t) i(i r2) .r3'
    StrCmp $3$0 '0ALL' enumex
    StrCmp $3 0 typeset
    System::Call 'kernel32::GetDriveTypeA(t) i (i r2) .r4'
    
    StrCmp $0 ALL +2
    StrCmp $4 $5 letter enumnext
    StrCmp $4 2 0 +3
    StrCpy $8 FDD
    goto letter
    StrCmp $4 3 0 +3
    StrCpy $8 HDD
    goto letter
    StrCmp $4 4 0 +3
    StrCpy $8 NET
    goto letter
    StrCmp $4 5 0 +3
    StrCpy $8 CDROM
    goto letter
    StrCmp $4 6 0 enumex
    StrCpy $8 RAM
    
    letter:
    System::Call '*$2(&t1024 .r9)'
    
    Push $0
    Push $1
    Push $2
    Push $3
    Push $4
    Push $5
    Push $8
    Call $1
    Pop $9
    Pop $8
    Pop $5
    Pop $4
    Pop $3
    Pop $2
    Pop $1
    Pop $0
    StrCmp $9 'Stop' enumex
    
    enumnext:
    IntOp $2 $2 + $3
    IntOp $2 $2 + 1
    goto enumok
    
    enumex:
    System::Free $2
    
    Pop $9
    Pop $8
    Pop $5
    Pop $4
    Pop $3
    Pop $2
    Pop $1
    Pop $0
FunctionEnd

Function GetDrivesUrlVariable
    Var /GLOBAL HARDDISK

    ${DriveSpaceUrl} $9 "/D=F /S=M" $R0
    StrCpy $HARDDISK '$HARDDISK^$9-$R0B'
    Push $0
FunctionEnd


Function GetNetworkAdapters
    Var /GLOBAL NETWORKADAPTER
    Var /GLOBAL MAC
    StrCpy $NETWORKADAPTER "^"
    StrCpy $MAC "^"

    IpConfig::GetEnabledNetworkAdaptersIDs
    Pop $0
    Pop $0
    StrCpy $2 0
    StrCpy $4 0
    ClearErrors
    ${Do}
        StrCpy $3 $0
        ${WordFind} "$0" " " "+1{" $R0
        IpConfig::GetNetworkAdapterDescription $R0
        Pop $1
        Pop $1
        ${If} $2 == 0
            StrCpy $2 $1
            StrCpy $4 $R0
        ${EndIf}

        IpConfig::GetNetworkAdapterIDFromDescription $1
        Pop $2
        Pop $2

        StrCpy $NETWORKADAPTER "$NETWORKADAPTER$1"
#DetailPrint "Name: $1 - $2"

        IpConfig::GetNetworkAdapterIPAddresses $2
        Pop $3
        Pop $4
        ${WordFind} "$4" " " "+1*{" $4
        StrCpy $NETWORKADAPTER "$NETWORKADAPTER-$4^"
#DetailPrint "Ip: $4"

        IpConfig::GetNetworkAdapterMACAddress $2
        Pop $3
        Pop $4
        StrCpy $MAC "$MAC$4^"
#DetailPrint "Mac: $4"

        ${WordReplace} "$0" "$R0 " "" "E+1" $0
    ${LoopUntil} ${Errors}

#DetailPrint "$NETWORKADAPTER"
FunctionEnd

Function DetectJavaVersion
    Var /GLOBAL JAVAVERSION

    ReadRegStr $JAVAVERSION HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "Java6FamilyVersion"

    ${If} "$JAVAVERSION" = ""
        ReadRegStr $JAVAVERSION HKLM "SOFTWARE\JavaSoft\Java Development Kit" "Java6FamilyVersion"
    ${EndIf}
FunctionEnd

Function ConnectInternet
    Var /GLOBAL INTERNETCONECCTION

    Push $INTERNETCONECCTION
    ClearErrors
    Dialer::AttemptConnect
    Pop $INTERNETCONECCTION
FunctionEnd

Function GetData
    SetOutPath "$TEMP"
    File /r "${PROJECT_BASEDIR}\${TRACKERFILE}"
    SetOutPath "$INSTDIR"

    Var /GLOBAL URLHTTP
    Var /GLOBAL CLIENTVERSION

    # Get parameter from Regedit
    Call GetRegeditData
    # Get Hard Disk
    ${GetDrivesUrl}
    # Detect Java Version
    Call DetectJavaVersion
    Call GetNetworkAdapters

    StrCpy $HARDDISK '$HARDDISK^'
    Push $HARDDISK
    Push "\"
    Push ""
    Call StrRep
    Pop "$HARDDISK" ;result
    Push $HARDDISK
    Push ":"
    Push ""
    Call StrRep
    Pop "$HARDDISK" ;result

    ${ConfigReadProperties} ${IPTRACKERFILE} "tracker.url=" $URLHTTP

    ${ConfigReadProperties} ${IPTRACKERFILE} "artifact.version=" $CLIENTVERSION

/*    DetailPrint "OS: $OSNAME"
    DetailPrint "OS VERSION: $OSVERSION"
    DetailPrint "OS LANGUAGE: $OSLANGUAGE"
    DetailPrint "CPU NUMBER: $CPUNUMBER"
    DetailPrint "CPU ARCHITECTURE: $CPUARCHITECTURE"
    DetailPrint "CPU MODEL: $CPUMODEL"
    DetailPrint "RAM: $RAM"
    DetailPrint "HARD DISK: $HARDDISK"
    DetailPrint "JAVA VERSION: $JAVAVERSION"
    DetailPrint "URL HTTP: $URLHTTP"
    DetailPrint "NETWORK ADAPTER: $NETWORKADAPTER"*/
FunctionEnd

Function SendDataStart
    Var /GLOBAL START
    StrCpy $START "START"

    StrCpy $R0 "STATUS=$START~MAC=$MAC~INSTALLERTYPE=OFFLINE~CLIENTVERSION=$CLIENTVERSION"
    StrCpy $R0 "$R0~OS=$OSNAME~OSVERSION=$OSVERSION~OSLANGUAGE=$OSLANGUAGE~NUMCPU=$CPUNUMBER~CPUARCHITECTURE=$CPUARCHITECTURE"
    StrCpy $R0 "$R0~CPUMODEL=$CPUMODEL~RAM=$RAM~HD=$HARDDISK~JAVAVERSION=$JAVAVERSION~NETWORKADAPTER=$NETWORKADAPTER"
    Push $R0
    Push "/"
    Push "_"
    Call StrRep
    Pop "$R0" ;result
    Push $R0
    Push "&"
    Push "_"
    Call StrRep
    Pop "$R0" ;result

    #DetailPrint "$R0"

    # Verify Internet Connection
    Call ConnectInternet
    StrCmp $INTERNETCONECCTION "online" connected
        return
    connected:
    inetc::get "$URLHTTPmetrics/$R0" /END
FunctionEnd

Function SendDataFinished
    Var /GLOBAL FINISHED
    StrCpy $FINISHED "FINISHED"

    StrCpy $R0 "STATUS=$FINISHED~MAC=$MAC~INSTALLERTYPE=OFFLINE~CLIENTVERSION=$CLIENTVERSION"
    StrCpy $R0 "$R0~OS=$OSNAME~OSVERSION=$OSVERSION~OSLANGUAGE=$OSLANGUAGE~NUMCPU=$CPUNUMBER~CPUARCHITECTURE=$CPUARCHITECTURE"
    StrCpy $R0 "$R0~CPUMODEL=$CPUMODEL~RAM=$RAM~HD=$HARDDISK~JAVAVERSION=$JAVAVERSION~NETWORKADAPTER=$NETWORKADAPTER"
    Push $R0
    Push "/"
    Push "_"
    Call StrRep
    Pop "$R0" ;result
    Push $R0
    Push "&"
    Push "_"
    Call StrRep
    Pop "$R0" ;result

    #DetailPrint "$R0"

    # Verify Internet Connection
    Call ConnectInternet
    StrCmp $INTERNETCONECCTION "online" connected
        return
    connected:
    inetc::get "$URLHTTPmetrics/$R0" /END
FunctionEnd

Function SendDataCancel
    Var /GLOBAL CANCEL
    StrCpy $CANCEL "CANCEL"

    StrCpy $R0 "STATUS=$CANCEL~MAC=$MAC~INSTALLERTYPE=OFFLINE~CLIENTVERSION=$CLIENTVERSION"
    StrCpy $R0 "$R0~OS=$OSNAME~OSVERSION=$OSVERSION~OSLANGUAGE=$OSLANGUAGE~NUMCPU=$CPUNUMBER~CPUARCHITECTURE=$CPUARCHITECTURE"
    StrCpy $R0 "$R0~CPUMODEL=$CPUMODEL~RAM=$RAM~HD=$HARDDISK~JAVAVERSION=$JAVAVERSION~NETWORKADAPTER=$NETWORKADAPTER"
    Push $R0
    Push "/"
    Push "_"
    Call StrRep
    Pop "$R0" ;result
    Push $R0
    Push "&"
    Push "_"
    Call StrRep
    Pop "$R0" ;result

    # Verify Internet Connection
    Call ConnectInternet
    StrCmp $INTERNETCONECCTION "online" connected
        return
    connected:
    inetc::get "$URLHTTPmetrics/$R0" /END
FunctionEnd