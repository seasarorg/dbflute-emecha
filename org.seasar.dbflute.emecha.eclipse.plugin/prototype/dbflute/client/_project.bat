@echo off

set ANT_OPTS=-Xmx256M

set MY_PROJECT_NAME=${project}

set DBFLUTE_HOME=..\mydbflute\dbflute-${versionInfoDBFlute}

if "%pause_at_end%"=="" set pause_at_end=y
