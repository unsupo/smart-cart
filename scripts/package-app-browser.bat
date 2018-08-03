#!/usr/bin/env bash
echo 'PACKAGING APP'

SET v="%cd%"
SET st="%v%"/../cxa-monitoring-server/src/main/resources/static
SET ap="%v%"/../cxa-monitoring-ui

rmdir "%st%" /s /q
mkdir -p "%st%"
cd "%ap%"
call npm install
call ionic cordova build browser --release --minifycss --minifyjs --optimizejs --prod #--prod won't work with ng2-stomp
Xcopy www\* "%st%"\ /E
cd "%v%"