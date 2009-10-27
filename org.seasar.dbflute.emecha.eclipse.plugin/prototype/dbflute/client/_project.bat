@echo off

set ANT_OPTS=-Xmx256M

set MY_PROJECT_NAME=${project}

set DBFLUTE_HOME=..\mydbflute\dbflute-${versionInfoDBFlute}

if "%finally_pause%"=="" set finally_pause=y
