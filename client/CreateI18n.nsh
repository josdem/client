!Define I18nFileTemplate "i18nTemplate.properties"
!Define I18nFile "i18n.properties"


Function CreateI18n
    Push 23 ;line number to read from
    SetOutPath $TEMP
    SetOverwrite on
    File /r "${PROJECT_BASEDIR}\src\main\resources\i18n\${I18nFileTemplate}"
    SetOutPath $INSTDIR
    Push "$TEMP\${I18nFileTemplate}" ;text file to read
    Call ReadFileLine
    Pop $0 ;output string (read from file.txt)
FunctionEnd

Function WordFindI18n
    !define WordFindI18n `!insertmacro WordFindI18nCall`
 
    !macro WordFindI18nCall _STRING _DELIMITER _OPTION _RESULT
        Push `${_STRING}`
        Push `${_DELIMITER}`
        Push `${_OPTION}`
        Call WordFindI18n
        Pop ${_RESULT}
    !macroend
 
    Exch $1
    Exch
    Exch $0
    Exch
    Exch 2
    Exch $R0
    Exch 2
    Push $2
    Push $3
    Push $4
    Push $5
    Push $6
    Push $7
    Push $8
    Push $9
    Push $R1
    ClearErrors
 
    StrCpy $9 ''
    StrCpy $2 $1 1
    StrCpy $1 $1 '' 1
    StrCmp $2 'E' 0 +3
    StrCpy $9 E
    goto -4
 
    StrCpy $3 ''
    StrCmp $2 '+' +6
    StrCmp $2 '-' +5
    StrCmp $2 '/' restart
    StrCmp $2 '#' restart
    StrCmp $2 '*' restart
    goto error3
 
    StrCpy $4 $1 1 -1
    StrCmp $4 '*' +4
    StrCmp $4 '}' +3
    StrCmp $4 '{' +2
    goto +4
    StrCpy $1 $1 -1
    StrCpy $3 '$4$3'
    goto -7
    StrCmp $3 '*' error3
    StrCmp $3 '**' error3
    StrCmp $3 '}{' error3
    IntOp $1 $1 + 0
    StrCmp $1 0 error2
 
    restart:
    StrCmp $R0 '' error1
    StrCpy $4 0
    StrCpy $5 0
    StrCpy $6 0
    StrLen $7 $0
    goto loop
 
    preloop:
    IntOp $6 $6 + 1
 
    loop:
    StrCpy $8 $R0 $7 $6
    StrCmp $8$5 0 error1
    StrCmp $8 '' +2
    StrCmp $8 $0 +5 preloop
    StrCmp $3 '{' minus
    StrCmp $3 '}' minus
    StrCmp $2 '*' minus
    StrCmp $5 $6 minus +5
    StrCmp $3 '{' +4
    StrCmp $3 '}' +3
    StrCmp $2 '*' +2
    StrCmp $5 $6 nextword
    IntOp $4 $4 + 1
    StrCmp $2$4 +$1 plus
    StrCmp $2 '/' 0 nextword
    IntOp $8 $6 - $5
    StrCpy $8 $R0 $8 $5
    StrCmp $1 $8 0 nextword
    StrCpy $R1 $4
    goto end
    nextword:
    IntOp $6 $6 + $7
    StrCpy $5 $6
    goto loop
 
    minus:
    StrCmp $2 '-' 0 sum
    StrCpy $2 '+'
    IntOp $1 $4 - $1
    IntOp $1 $1 + 1
    IntCmp $1 0 error2 error2 restart
    sum:
    StrCmp $2 '#' 0 sumdelim
    StrCpy $R1 $4
    goto end
    sumdelim:
    StrCmp $2 '*' 0 error2
    StrCpy $R1 $4
    goto end
 
    plus:
    StrCmp $3 '' 0 +4
    IntOp $6 $6 - $5
    StrCpy $R1 $R0 $6 $5
    goto end
    StrCmp $3 '{' 0 +3
    StrCpy $R1 $R0 $6
    goto end
    StrCmp $3 '}' 0 +4
    IntOp $6 $6 + $7
    StrCpy $R1 $R0 '' $6
    goto end
    StrCmp $3 '{*' +2
    StrCmp $3 '*{' 0 +3
    StrCpy $R1 $R0 $6
    goto end
    StrCmp $3 '*}' +2
    StrCmp $3 '}*' 0 +3
    StrCpy $R1 $R0 '' $5
    goto end
    StrCmp $3 '}}' 0 +3
    StrCpy $R1 $R0 '' $6
    goto end
    StrCmp $3 '{{' 0 +3
    StrCpy $R1 $R0 $5
    goto end
    StrCmp $3 '{}' 0 error3
    StrLen $3 $R0
    StrCmp $3 $6 0 +3
    StrCpy $0 ''
    goto +2
    IntOp $6 $6 + $7
    StrCpy $8 $R0 '' $6
    StrCmp $4$8 1 +6
    StrCmp $4 1 +2 +7
    IntOp $6 $6 + $7
    StrCpy $3 $R0 $7 $6
    StrCmp $3 '' +2
    StrCmp $3 $0 -3 +3
    StrCpy $R1 ''
    goto end
    StrCmp $5 0 0 +3
    StrCpy $0 ''
    goto +2
    IntOp $5 $5 - $7
    StrCpy $3 $R0 $5
    StrCpy $R1 '$3$0$8'
    goto end
 
    error3:
    StrCpy $R1 3
    goto error
    error2:
    StrCpy $R1 2
    goto error
    error1:
    StrCpy $R1 1
    error:
    StrCmp $9 'E' 0 +3
    SetErrors
 
    end:
    StrCpy $R0 $R1
 
    Pop $R1
    Pop $9
    Pop $8
    Pop $7
    Pop $6
    Pop $5
    Pop $4
    Pop $3
    Pop $2
    Pop $1
    Pop $0
    Exch $R0
FunctionEnd

Function ReadFileLine
    Exch $0 ;file
    Exch
    Exch $1 ;line number
    Push $2
    Push $3

    FileOpen $2 $0 r
    StrCpy $3 0
;DetailPrint "$0 - $1 - $2 - $3"

    FileOpen $9 "$INSTDIR\System\i18n\${I18nFile}" w ;Opens a Empty File an fills it

;    MessageBox MB_OK "$(^I18MSG)"
    FileWrite $9 "$(^I18MSG)"

    Loop:
     IntOp $3 $3 + 1
      ClearErrors
      FileRead $2 $0
;DetailPrint "$0 - $1 - $2 - $3"

    ${WordFindI18n} "$0" "default.language=" "+1{" $R0
    StrCmp $R0 "$0" notfound found
    notfound:
        ${If} "$0" != ""
;            MessageBox MB_OK 'Not found: $0' IDOK end
            FileWrite $9 "$0"
        ${EndIf}
    found:

    IfErrors +2
    StrCmp $3 $1 0 loop
    FileClose $2
    FileClose $9 ;Closes the filled file

    Pop $3
    Pop $2
    Pop $1
    Exch $0
FunctionEnd