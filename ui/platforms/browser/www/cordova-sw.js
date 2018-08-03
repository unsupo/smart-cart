/**
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
'License'); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
'AS IS' BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

// Note, these will be updated automatically at build time
var CACHE_VERSION = '1513292127910';
var CACHE_LIST = [
    "/assets/fonts/ionicons.eot",
    "/assets/fonts/ionicons.scss",
    "/assets/fonts/ionicons.svg",
    "/assets/fonts/ionicons.ttf",
    "/assets/fonts/ionicons.woff",
    "/assets/fonts/ionicons.woff2",
    "/assets/fonts/noto-sans-bold.ttf",
    "/assets/fonts/noto-sans-bold.woff",
    "/assets/fonts/noto-sans-regular.ttf",
    "/assets/fonts/noto-sans-regular.woff",
    "/assets/fonts/noto-sans.scss",
    "/assets/fonts/roboto-bold.ttf",
    "/assets/fonts/roboto-bold.woff",
    "/assets/fonts/roboto-bold.woff2",
    "/assets/fonts/roboto-light.ttf",
    "/assets/fonts/roboto-light.woff",
    "/assets/fonts/roboto-light.woff2",
    "/assets/fonts/roboto-medium.ttf",
    "/assets/fonts/roboto-medium.woff",
    "/assets/fonts/roboto-medium.woff2",
    "/assets/fonts/roboto-regular.ttf",
    "/assets/fonts/roboto-regular.woff",
    "/assets/fonts/roboto-regular.woff2",
    "/assets/fonts/roboto.scss",
    "/assets/icon/favicon.ico",
    "/assets/login/MangagedMonitoringConsole.png",
    "/build/0.js",
    "/build/main.css",
    "/build/main.js",
    "/build/polyfills.js",
    "/build/sw-toolbox.js",
    "/build/vendor.js",
    "/config.xml",
    "/cordova-sw.js",
    "/cordova.js",
    "/cordova_plugins.js",
    "/favicon.ico",
    "/index.html",
    "/manifest.json",
    "/plugins/cordova-plugin-device/src/browser/DeviceProxy.js",
    "/plugins/cordova-plugin-device/www/device.js",
    "/plugins/cordova-plugin-splashscreen/src/browser/SplashScreenProxy.js",
    "/plugins/cordova-plugin-splashscreen/www/splashscreen.js",
    "/plugins/ionic-plugin-keyboard/www/browser/keyboard.js",
    "/service-worker.js"
];

this.addEventListener('install', function (event) {
    // Perform install steps
    console.log('cordova service worker is installing.');
    event.waitUntil(caches.open(CACHE_VERSION) /* eslint no-undef : 0 */
        .then(function (cache) {
            return cache.addAll(CACHE_LIST);
        }));
});

this.addEventListener('activate', function (event) {
    // Perform activate steps
    console.log('cordova service worker is activated.');
});

this.addEventListener('fetch', function (event) {
    console.log('cordova service worker : fetch : ' + event.request.url);

    event.respondWith(caches.match(event.request).then(function (response) { /* eslint no-undef : 0 */
        // Cache hit? return response else fetch it
        return response || fetch(event.request); /* eslint no-undef : 0 */
    }));
});
