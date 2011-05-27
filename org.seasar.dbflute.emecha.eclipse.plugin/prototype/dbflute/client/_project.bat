@echo off

set ANT_OPTS=-Xmx512M

set DBFLUTE_HOME=..\mydbflute\dbflute-${versionInfoDBFlute}

set MY_PROJECT_NAME=${project}

set MY_PROPERTIES_PATH=build.properties

if "%pause_at_end%"=="" set pause_at_end=y
