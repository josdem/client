;OutFile target\prueba.exe

Name ALL
SetCompressor lzma

!define KLITE_FILE_NAME K-Lite_Codec_Pack_604_Full.exe
!define THIRDPARTY_FOLDER "c:\ci\installer"
!define env_hklm 'HKLM "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"'
!define FILEEXEOLD "AllDotComClient.exe"
!define REGKEY "SOFTWARE\$(^Name)"
!define VERSION alfa
!define COMPANY ALL.COM
!define URL http://www.all.com
!define PROFILEALL "$%USERPROFILE%\ALL"
!define ALL_MEM "128m"
!define ALL_MAX_MEM "1024m"
!define CLASSPATH '"$INSTDIR"\System\Jar;"$INSTDIR"\System\Jar\*;"$INSTDIR"\System'
!define STARTUP_ARGS '-Xmx${ALL_MAX_MEM} -Xms${ALL_MEM} -Djava.library.path="$INSTDIR\System\Lib" -Djna.library.path="$INSTDIR\System\Lib"'
!define MAIN_CLASS "com.all.login.Client"

!define MULTIUSER_EXECUTIONLEVEL Admin
!define MULTIUSER_MUI
!define MULTIUSER_INSTALLMODE_DEFAULT_REGISTRY_KEY "${REGKEY}"
!define MULTIUSER_INSTALLMODE_DEFAULT_REGISTRY_VALUENAME MultiUserInstallMode
!define MULTIUSER_INSTALLMODE_COMMANDLINE
!define MULTIUSER_INSTALLMODE_INSTDIR_REGISTRY_KEY "${REGKEY}"
!define MULTIUSER_INSTALLMODE_INSTDIR_REGISTRY_VALUE "Path"
!define MULTIUSER_INSTALLMODE_INSTDIR_REGISTRY_VALUENAME MultiUserInstallMode
!define MULTIUSER_INIT_TEXT_ADMINREQUIRED

!define MUI_ICON "${PROJECT_BASEDIR}\src\main\os\windows\installer\icons\all.ico"
!define MUI_UNICON "${PROJECT_BASEDIR}\src\main\os\windows\installer\icons\uninstall.ico"
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_UNFINISHPAGE_NOAUTOCLOSE
!define MUI_STARTMENUPAGE_REGISTRY_ROOT HKLM
!define MUI_STARTMENUPAGE_NODISABLE
!define MUI_STARTMENUPAGE_REGISTRY_KEY ${REGKEY}
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME StartMenuGroup
!define MUI_STARTMENUPAGE_DEFAULTFOLDER "$INSTDIR"
!define MUI_ABORTWARNING
!define MUI_HEADERIMAGE
!define MUI_HEADERIMAGE_BITMAP "${PROJECT_BASEDIR}\src\main\os\windows\installer\images\all.bmp" ; optional
!define MUI_WELCOMEFINISHPAGE_BITMAP "${PROJECT_BASEDIR}\src\main\os\windows\installer\images\installer.bmp" ; optional
!define MUI_CUSTOMFUNCTION_ABORT UserAbort

!include Sections.nsh
!include MUI2.nsh
!include LogicLib.nsh
!include ZipDLL.nsh
!include WinMessages.nsh
!include MultiUser.nsh
!include "target\project.nsh"
!include "SendPostInstaller.nsh"
!include "CreateI18n.nsh"
!include FileFunc.nsh
!include "DetectJava.nsh"
!include "UtilInstaller.nsh"

!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE ${PROJECT_BASEDIR}\src\main\os\windows\installer\License.txt
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_PAGE_FINISH
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!insertmacro MUI_LANGUAGE English
!insertmacro MUI_LANGUAGE Spanish

InstallDir "$PROGRAMFILES\ALL"
CRCCheck on
XPStyle on
ShowInstDetails show
VIProductVersion 0.0.0.0
VIAddVersionKey /LANG=${LANG_ENGLISH} ProductName ALL
VIAddVersionKey /LANG=${LANG_ENGLISH} ProductVersion "${VERSION}"
VIAddVersionKey /LANG=${LANG_ENGLISH} CompanyName "${COMPANY}"
VIAddVersionKey /LANG=${LANG_ENGLISH} CompanyWebsite "${URL}"
VIAddVersionKey /LANG=${LANG_ENGLISH} FileVersion "${VERSION}"
VIAddVersionKey /LANG=${LANG_ENGLISH} FileDescription ""
VIAddVersionKey /LANG=${LANG_ENGLISH} LegalCopyright ""
InstallDirRegKey HKLM "${REGKEY}" Path
ShowUninstDetails show

Var StartMenuGroup

Section -Main SEC0000
    MessageBox MB_OK $(^CloseAll) 
    ReadRegStr $R0 HKLM "${REGKEY}" Path

    Call PermissionFolder
    Call EliminaReg

    SetDetailsView hide
    SetShellVarContext all

    Call IncludeFiles

    WriteRegStr HKLM "${REGKEY}\Components" Main 1
SectionEnd

Section -post SEC0001
    WriteRegStr HKLM "${REGKEY}" Path $INSTDIR
    SetOutPath $INSTDIR

    CALL DetectJava

    #in this moment we have to unzip the file
    FindFirst $0 $1 "$INSTDIR\*.zip"
    ZipDLL::extractall $INSTDIR\$1 $INSTDIR "<ALL>"
    FindClose $0
    Delete $INSTDIR\*.zip

    #here we are going to install k-lite
    ExecWait "$INSTDIR\${KLITE_FILE_NAME} /VERYSILENT /SUPPRESSMSGBOXES /NORESTART /SP-"

    WriteUninstaller $INSTDIR\uninstall.exe
    CreateShortCut $DESKTOP\ALL.lnk $INSTDIR\${FILEEXEOLD} "" "$INSTDIR\icons\all.ico"
    SetOutPath $SMPROGRAMS\$StartMenuGroup

    #create directory in start menu group
    CreateDirectory $SMPROGRAMS\$StartMenuGroup

	CreateShortCut $SMPROGRAMS\$StartMenuGroup\ALL.lnk $INSTDIR\${FILEEXEOLD} "" "$INSTDIR\icons\all.ico"
	CreateShortCut $SMPROGRAMS\$StartMenuGroup\Uninstall.lnk $INSTDIR\uninstall.exe "" "$INSTDIR\icons\all.ico"

    Call WriteRegister

    Call ClearFilesInstallation

    Call CreateI18n
    Call SendDataFinished
SectionEnd

Function .onInit
    InitPluginsDir
    !insertmacro MUI_LANGDLL_DISPLAY
    !insertmacro MULTIUSER_INIT
    StrCpy $StartMenuGroup ALL
    Call GetData
    Call SendDataStart
FunctionEnd

# Macro for selecting uninstaller sections
!macro SELECT_UNSECTION SECTION_NAME UNSECTION_ID
    Push $R0
    ReadRegStr $R0 HKLM "${REGKEY}\Components" "${SECTION_NAME}"
    StrCmp $R0 1 0 next${UNSECTION_ID}
    !insertmacro SelectSection "${UNSECTION_ID}"
    GoTo done${UNSECTION_ID}
    next${UNSECTION_ID}:
        !insertmacro UnselectSection "${UNSECTION_ID}"
    done${UNSECTION_ID}:
    Pop $R0
!macroend

# Uninstaller sections
Section /o -un.Main UNSEC0000
    RmDir /r /REBOOTOK $INSTDIR
    DeleteRegValue HKLM "${REGKEY}\Components" Main
SectionEnd

Section -un.post UNSEC0001
    DeleteRegKey HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
    Delete /REBOOTOK "$SMPROGRAMS\$StartMenuGroup\$(^UninstallLink).lnk"
    Delete /REBOOTOK $INSTDIR\uninstall.exe
    Delete /REBOOTOK $DESKTOP\all.lnk
    Delete /REBOOTOK $DESKTOP\uninstall.lnk
    DeleteRegValue HKLM "${REGKEY}" Path
    DeleteRegKey /IfEmpty HKLM "${REGKEY}\Components"
    DeleteRegKey /IfEmpty HKLM "${REGKEY}"
    RmDir /r $SMPROGRAMS\$StartMenuGroup
    RmDir /r $INSTDIR
    Delete "$DESKTOP\ALL.lnk"
    Delete "$DESKTOP\Uninstall.lnk"
    Delete "$SMPROGRAMS\$StartMenuGroup\ALL.lnk"
    Delete "$SMPROGRAMS\$StartMenuGroup\Uninstall.lnk"
SectionEnd

Function un.onInit
    ReadRegStr $INSTDIR HKLM "${REGKEY}" Path
    StrCpy $StartMenuGroup ALL
    !insertmacro SELECT_UNSECTION Main ${UNSEC0000}
    !insertmacro MULTIUSER_UNINIT
FunctionEnd

# Installer Language Strings
LangString ^UninstallLink ${LANG_ENGLISH} "Uninstall $(^Name)"
LangString ^CloseAll ${LANG_ENGLISH} "If ALL.com application is open when updating, remember to restart it to apply last changes."
LangString ^CloseAll ${LANG_SPANISH} "Si la aplicación de ALL.com está abierta al actualizar, recuerda reiniciarla para aplicar los cambios."
LangString ^I18MSG ${LANG_ENGLISH} "default.language=en_US"
LangString ^I18MSG ${LANG_SPANISH} "default.language=es_MX"