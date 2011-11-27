Function ClearFilesInstallation
    Delete "$INSTDIR\${KLITE_FILE_NAME}"
    Delete "$TEMP\ipTrackerFile.properties"
    Delete "$TEMP\clientSettings.properties"
FunctionEnd

Function IncludeFiles
    SetOutPath $INSTDIR
    SetOverwrite on
    File /r "${PROJECT_BUILD_DIR}\*.zip"
    File /r "${THIRDPARTY_FOLDER}\${KLITE_FILE_NAME}"
FunctionEnd

Function WriteRegister
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayName "$(^Name)"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayVersion "${VERSION}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" Publisher "${COMPANY}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" URLInfoAbout "${URL}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayIcon $INSTDIR\uninstall.exe
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" UninstallString $INSTDIR\uninstall.exe
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoModify 1
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoRepair 1
FunctionEnd

Function PermissionFolder
    ReadRegStr $R0 HKLM "${REGKEY}" Path
    AccessControl::GrantOnFile "$INSTDIR" "(BU)" "GenericRead + GenericWrite"
    AccessControl::GrantOnFile "$INSTDIR\DB" "(BU)" "GenericRead + GenericWrite"
    AccessControl::GrantOnFile "$INSTDIR\Cfg" "(BU)" "GenericRead + GenericWrite"
    AccessControl::GrantOnFile "${PROFILEALL}" "(BU)" "GenericRead + GenericWrite"
    AccessControl::GrantOnFile "${PROFILEALL}\DB" "(BU)" "GenericRead + GenericWrite"
    AccessControl::GrantOnFile "${PROFILEALL}\Cfg" "(BU)" "GenericRead + GenericWrite"
    AccessControl::GrantOnFile "$R0" "(BU)" "GenericRead + GenericWrite"
    AccessControl::GrantOnFile "$R0\DB" "(BU)" "GenericRead + GenericWrite"
    AccessControl::GrantOnFile "$R0\Cfg" "(BU)" "GenericRead + GenericWrite"
    AccessControl::GrantOnRegKey HKLM "${REGKEY}" "(BU)" "FullAccess"
    AccessControl::GrantOnRegKey HKLM "${REGKEY}" "(S-1-5-32-545)" "FullAccess"
FunctionEnd

Function EliminaReg
    DeleteRegKey HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
    DeleteRegValue HKLM "${REGKEY}" Path
    DeleteRegKey /IfEmpty HKLM "${REGKEY}\Components"
    DeleteRegKey /IfEmpty HKLM "${REGKEY}"
    RmDir /r "$%APPDATA%\Microsoft\Windows\Start Menu\Programs\ALL"
    Delete "$%USERPROFILE%\Desktop\ALL.lnk"
    Delete "$%USERPROFILE%\Desktop\Uninstall.lnk"

    ReadRegStr $R0 HKLM "${REGKEY}" Path
    Delete "${PROFILEALL}\DB\all_all.properties"
    Delete "${PROFILEALL}\DB\all_all.script"
    Delete "${PROFILEALL}\Cfg\phexCorePrefs.properties"
    Delete "${PROFILEALL}\Cfg\phexCorePrefs.properties.bak"
    Delete "${PROFILEALL}\Cfg\phexdownload.xml"
    Delete "${PROFILEALL}\Cfg\phexdownload.xml.bak"
    Delete "${PROFILEALL}\*.exe"
    Delete "${PROFILEALL}\*.xml"
    RMDir /r "${PROFILEALL}\System"
    RMDir /r "${PROFILEALL}\icons"
    ReadRegStr $R0 HKLM "${REGKEY}" Path
    Delete "$R0\DB\all_all.properties"
    Delete "$R0\DB\all_all.script"
    Delete "$R0\Cfg\phexCorePrefs.properties"
    Delete "$R0\Cfg\phexCorePrefs.properties.bak"
    Delete "$R0\Cfg\phexdownload.xml"
    Delete "$R0\Cfg\phexdownload.xml.bak"
    Delete "$R0\*.exe"
    Delete "$R0\*.xml"
    RMDir /r "$R0\System"
    RMDir /r "$R0\icons"
    Delete "$INSTDIR\DB\all_all.properties"
    Delete "$INSTDIR\DB\all_all.script"
    Delete "$INSTDIR\Cfg\phexCorePrefs.properties"
    Delete "$INSTDIR\Cfg\phexCorePrefs.properties.bak"
    Delete "$INSTDIR\Cfg\phexdownload.xml"
    Delete "$INSTDIR\Cfg\phexdownload.xml.bak"
    Delete "$INSTDIR\*.xml"
    Delete "$INSTDIR\uninstall.exe"
    RMDir /r "$INSTDIR\System"
    RMDir /r "$INSTDIR\icons"
FunctionEnd

Function StrRep
      Exch $R4 ; $R4 = Replacement String
      Exch
      Exch $R3 ; $R3 = String to replace (needle)
      Exch 2
      Exch $R1 ; $R1 = String to do replacement in (haystack)
      Push $R2 ; Replaced haystack
      Push $R5 ; Len (needle)
      Push $R6 ; len (haystack)
      Push $R7 ; Scratch reg
      StrCpy $R2 ""
      StrLen $R5 $R3
      StrLen $R6 $R1
    loop:
      StrCpy $R7 $R1 $R5
      StrCmp $R7 $R3 found
      StrCpy $R7 $R1 1 ; - optimization can be removed if U know len needle=1
      StrCpy $R2 "$R2$R7"
      StrCpy $R1 $R1 $R6 1
      StrCmp $R1 "" done loop
    found:
      StrCpy $R2 "$R2$R4"
      StrCpy $R1 $R1 $R6 $R5
      StrCmp $R1 "" done loop
    done:
      StrCpy $R3 $R2
      Pop $R7
      Pop $R6
      Pop $R5
      Pop $R2
      Pop $R1
      Pop $R4
      Exch $R3
FunctionEnd

Function UserAbort
    Delete "$TEMP\ipTrackerFile.properties"
    Call SendDataCancel
FunctionEnd