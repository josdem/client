!define JRE_VERSION "1.6"
!define JRE_COMPLETE_VERSION "16020"
!define JRE_URL "http://javadl.sun.com/webapps/download/AutoDL?BundleId=39502"


Function GetJRE
        StrCpy $2 "$TEMP\jre-6u20-windows-i586.exe"
        nsisdl::download /TIMEOUT=30000 ${JRE_URL} $2
        Pop $R0 ;Get the return value
                StrCmp $R0 "success" +3
                MessageBox MB_OK "Download failed: $R0"
                Quit
        ExecWait $2
        Delete $2

        ReadRegStr $3 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "Java6FamilyVersion"
        ReadRegStr $4 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$3" "JavaHome"
        SendMessage ${HWND_BROADCAST} ${WM_WININICHANGE} 0 "STR:Environment" /TIMEOUT=5000
FunctionEnd


Function DetectJava
  ;  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" \
  ;             "CurrentVersion"
  ;  #here we have to validate first of all the version of jre
  ;  #after that we have to review what number of update is
  ;  StrCmp $2 ${JRE_VERSION} done
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "Java6FamilyVersion"
  ReadRegStr $3 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$2" "JavaHome"
  ;; Se reemplazan los caracteres '.' y '_' para ser comparados
  Push $2
  Push "_"
  Push ""
  Call StrRep
  Pop "$2" ;result
  Push $2
  Push "."
  Push ""
  Call StrRep
  Pop "$R0" ;result

  ${If} "$R0" == ""
    ;;;;;;;;; DETECT JDK
    ReadRegStr $4 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "Java6FamilyVersion"
    ReadRegStr $6 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$4" "JavaHome"
    Push $4
    Push "_"
    Push ""
    Call StrRep
    Pop "$4" ;result
    Push $4
    Push "."
    Push ""
    Call StrRep
    Pop "$R1" ;result

    ${If} "$R1" >= "${JRE_COMPLETE_VERSION}"
          SendMessage ${HWND_BROADCAST} ${WM_WININICHANGE} 0 "STR:Environment" /TIMEOUT=5000
        return
    ${EndIf}

    Call GetJRE
  ${ElseIf} "$R0" >= "${JRE_COMPLETE_VERSION}"
    SendMessage ${HWND_BROADCAST} ${WM_WININICHANGE} 0 "STR:Environment" /TIMEOUT=5000
  ${Else}
    Call GetJRE
  ${EndIf}

  return
FunctionEnd
