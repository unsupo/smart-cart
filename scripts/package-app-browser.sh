#!/usr/bin/env bash
#sudo npm install -g ionic cordova
#ionic serve
echo 'PACKAGING APP'

v=`pwd`;
st=$v/../server/src/main/resources/static;
ap=$v/../ui;

rm -rf $st 2>/dev/null;
mkdir -p $st;
cd $ap;
npm install;
node_modules/ionic/bin/ionic cordova build browser --release --minifycss --minifyjs --optimizejs --prod #--prod won't work with ng2-stomp
cp -R www/* $st/
cd $v;