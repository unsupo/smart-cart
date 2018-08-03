"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var Utility = (function () {
    function Utility() {
    }
    //35.192.142.172
    Utility.getHost = function () {
        return this.host;
    };
    Utility.uuid = function () {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    };
    return Utility;
}());
Utility.host = "http://localhost:8981"; //
Utility.user = "user";
Utility.currentPage = "currentPage";
exports.Utility = Utility;
