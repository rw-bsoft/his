/*
 * add by liuchunmei 2017-05-23 添加Mc_Base64编码
 * $Id: Mc_Base64.js,v 2.15 2014/04/05 12:58:57 dankogai Exp dankogai $
 *
 *  Licensed under the MIT license.
 *    http://opensource.org/licenses/mit-license
 *
 *  References:
 *    http://en.wikipedia.org/wiki/Mc_Base64
 */
(function (McPass_global) {
    'use strict';
    // existing version for noConflict()
    var _Base64 = McPass_global.Mc_Base64;
    var version = "2.1.9";
    // if node.js, we use Buffer
    var buffer;
    if (typeof module !== 'undefined' && module.exports) {
        try {
            buffer = require('buffer').Buffer;
        } catch (err) { }
    }
    // constants
    var b64chars
        = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
    var b64tab = function (bin) {
        var t = {};
        for (var i = 0, l = bin.length; i < l; i++) t[bin.charAt(i)] = i;
        return t;
    }(b64chars);
    var fromCharCode = String.fromCharCode;
    // encoder stuff
    var cb_utob = function (c) {
        if (c.length < 2) {
            var cc = c.charCodeAt(0);
            return cc < 0x80 ? c
                : cc < 0x800 ? (fromCharCode(0xc0 | (cc >>> 6))
                                + fromCharCode(0x80 | (cc & 0x3f)))
                : (fromCharCode(0xe0 | ((cc >>> 12) & 0x0f))
                   + fromCharCode(0x80 | ((cc >>> 6) & 0x3f))
                   + fromCharCode(0x80 | (cc & 0x3f)));
        } else {
            var cc = 0x10000
                + (c.charCodeAt(0) - 0xD800) * 0x400
                + (c.charCodeAt(1) - 0xDC00);
            return (fromCharCode(0xf0 | ((cc >>> 18) & 0x07))
                    + fromCharCode(0x80 | ((cc >>> 12) & 0x3f))
                    + fromCharCode(0x80 | ((cc >>> 6) & 0x3f))
                    + fromCharCode(0x80 | (cc & 0x3f)));
        }
    };
    var re_utob = /[\uD800-\uDBFF][\uDC00-\uDFFFF]|[^\x00-\x7F]/g;
    var utob = function (u) {
        return u.replace(re_utob, cb_utob);
    };
    var cb_encode = function (ccc) {
        var padlen = [0, 2, 1][ccc.length % 3],
        ord = ccc.charCodeAt(0) << 16
            | ((ccc.length > 1 ? ccc.charCodeAt(1) : 0) << 8)
            | ((ccc.length > 2 ? ccc.charCodeAt(2) : 0)),
        chars = [
            b64chars.charAt(ord >>> 18),
            b64chars.charAt((ord >>> 12) & 63),
            padlen >= 2 ? '=' : b64chars.charAt((ord >>> 6) & 63),
            padlen >= 1 ? '=' : b64chars.charAt(ord & 63)
        ];
        return chars.join('');
    };
    var btoa = McPass_global.btoa ? function (b) {
        return McPass_global.btoa(b);
    } : function (b) {
        return b.replace(/[\s\S]{1,3}/g, cb_encode);
    };
    var _encode = buffer ? function (u) {
        return (u.constructor === buffer.constructor ? u : new buffer(u))
        .toString('Mc_Base64')
    }
    : function (u) { return btoa(utob(u)) }
    ;
    var encode = function (u, urisafe) {
        return !urisafe
            ? _encode(String(u))
            : _encode(String(u)).replace(/[+\/]/g, function (m0) {
                return m0 == '+' ? '-' : '_';
            }).replace(/=/g, '');
    };
    var encodeURI = function (u) { return encode(u, true) };
    // decoder stuff
    var re_btou = new RegExp([
        '[\xC0-\xDF][\x80-\xBF]',
        '[\xE0-\xEF][\x80-\xBF]{2}',
        '[\xF0-\xF7][\x80-\xBF]{3}'
    ].join('|'), 'g');
    var cb_btou = function (cccc) {
        switch (cccc.length) {
            case 4:
                var cp = ((0x07 & cccc.charCodeAt(0)) << 18)
                    | ((0x3f & cccc.charCodeAt(1)) << 12)
                    | ((0x3f & cccc.charCodeAt(2)) << 6)
                    | (0x3f & cccc.charCodeAt(3)),
                offset = cp - 0x10000;
                return (fromCharCode((offset >>> 10) + 0xD800)
                        + fromCharCode((offset & 0x3FF) + 0xDC00));
            case 3:
                return fromCharCode(
                    ((0x0f & cccc.charCodeAt(0)) << 12)
                        | ((0x3f & cccc.charCodeAt(1)) << 6)
                        | (0x3f & cccc.charCodeAt(2))
                );
            default:
                return fromCharCode(
                    ((0x1f & cccc.charCodeAt(0)) << 6)
                        | (0x3f & cccc.charCodeAt(1))
                );
        }
    };
    var btou = function (b) {
        return b.replace(re_btou, cb_btou);
    };
    var cb_decode = function (cccc) {
        var len = cccc.length,
        padlen = len % 4,
        n = (len > 0 ? b64tab[cccc.charAt(0)] << 18 : 0)
            | (len > 1 ? b64tab[cccc.charAt(1)] << 12 : 0)
            | (len > 2 ? b64tab[cccc.charAt(2)] << 6 : 0)
            | (len > 3 ? b64tab[cccc.charAt(3)] : 0),
        chars = [
            fromCharCode(n >>> 16),
            fromCharCode((n >>> 8) & 0xff),
            fromCharCode(n & 0xff)
        ];
        chars.length -= [0, 0, 2, 1][padlen];
        return chars.join('');
    };
    var atob = McPass_global.atob ? function (a) {
        return McPass_global.atob(a);
    } : function (a) {
        return a.replace(/[\s\S]{1,4}/g, cb_decode);
    };
    var _decode = buffer ? function (a) {
        return (a.constructor === buffer.constructor
                ? a : new buffer(a, 'Mc_Base64')).toString();
    }
    : function (a) { return btou(atob(a)) };
    var decode = function (a) {
        return _decode(
            String(a).replace(/[-_]/g, function (m0) { return m0 == '-' ? '+' : '/' })
                .replace(/[^A-Za-z0-9\+\/]/g, '')
        );
    };
    var noConflict = function () {
        var Mc_Base64 = McPass_global.Mc_Base64;
        McPass_global.Mc_Base64 = _Base64;
        return Mc_Base64;
    };
    // export Mc_Base64
    McPass_global.Mc_Base64 = {
        VERSION: version,
        atob: atob,
        btoa: btoa,
        fromBase64: decode,
        toBase64: encode,
        utob: utob,
        encode: encode,
        encodeURI: encodeURI,
        btou: btou,
        decode: decode,
        noConflict: noConflict
    };
    // if ES5 is available, make Mc_Base64.extendString() available
    if (typeof Object.defineProperty === 'function') {
        var noEnum = function (v) {
            return { value: v, enumerable: false, writable: true, configurable: true };
        };
        McPass_global.Mc_Base64.extendString = function () {
            Object.defineProperty(
                String.prototype, 'fromBase64', noEnum(function () {
                    return decode(this)
                }));
            Object.defineProperty(
                String.prototype, 'toBase64', noEnum(function (urisafe) {
                    return encode(this, urisafe)
                }));
            Object.defineProperty(
                String.prototype, 'toBase64URI', noEnum(function () {
                    return encode(this, true)
                }));
        };
    }
    // that's it!
    if (McPass_global['Meteor']) {
        Mc_Base64 = McPass_global.Mc_Base64; // for normal export in Meteor.js
    }
})(this);﻿(function (win, undefined) {
    var McPASS = win.McPASS = function (selector, context) {
        return new McPASS.fn.constructor(selector, context);
    },
    quickExpr = /^(?:[^<]*(<[\w\W]+>)[^>]*$|#([\w\-]+)$)/,
    rclass = /[\n\t]/g;
    if (win.McPASS === undefined) {
        win.McPASS = McPASS;
    };
    McPASS.fn = McPASS.prototype = {
        constructor: function (selector, context) {
            var match, elem;
            context = context || document;
            if (!selector) {
                return this;
            };
            if (selector.nodeType) {
                this[0] = selector;
                return this;
            };
            if (typeof selector === 'string') {
                match = quickExpr.exec(selector);
                if (match && match[2]) {
                    elem = context.getElementById(match[2]);
                    if (elem && elem.parentNode) {
                        this[0] = elem;
                    };
                    return this;
                };
            };
            this[0] = selector;
            return this;
        },
        byId: function (id) {
            return "string" == typeof id ? document.getElementById(id) : id;
        },
        EvalJson: function (json) {
            try {
                //不稳定，有的时候出错
                return eval('(' + json + ')');
                //return (new Function("", "return " + json))();
            }
            catch (e) {
                return null;
            }
        } //end    
    };
    McPASS.fn.constructor.prototype = McPASS.fn;
    return McPASS;
} (window));
McPASS.Debug = function (isDebug, debugInfo, debugDiv) {
    if (isDebug) {
        var div = McPASS.fn.byId(debugDiv);
        if (div != null) {
            div.innerHTML = debugInfo;
        }
        else {
            alert(debugInfo);
        }
    }
};
McPASS.ErrorTip = function () {
    return "由于网络问题数据没能刷新，请重新执行上一步操作！";
};
McPASS.isPC = function () {
    var sUserAgent = navigator.userAgent.toLowerCase();
    var bIsIpad = sUserAgent.match(/ipad/i) == "ipad";
    var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
    var bIsMidp = sUserAgent.match(/midp/i) == "midp";
    var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
    var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
    var bIsAndroid = sUserAgent.match(/android/i) == "android";
    var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
    var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
    if (bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM) {
        return false;
    }
    else {
        return true;
    }
};
McPASS.isIE = function () {
    //if (window.navigator.userAgent.indexOf("MSIE") >= 1) {
    if (!!window.ActiveXObject || "ActiveXObject" in window) { 
        return true;
    }
    else {
        return false;
    }
};
//coding for fix Firefox
if (!McPASS.isIE()) {
    /*insertAdjacentElement for Firefox*/
    if (typeof (HTMLElement) != "undefined" && !window.opera) {
        HTMLElement.prototype.insertAdjacentElement = function (where, parsedNode) {
            switch (where) {
                case "beforeBegin":
                    this.parentNode.insertBefore(parsedNode, this);
                    break;
                case "afterBegin":
                    this.insertBefore(parsedNode, this.firstChild);
                    break;
                case "beforeEnd":
                    this.appendChild(parsedNode);
                    break;
                case "afterEnd":
                    if (this.nextSibling)
                        this.parentNode.insertBefore(parsedNode, this.nextSibling);
                    else
                        this.parentNode.appendChild(parsedNode);
                    break;
            }
        }
    } /*end insertAdjacentElement*/
}

//去除空格
//String.prototype.Trim = function () {
//    return this.replace(/(^\s*)|(\s*$)/g, "");
//} 


//this.StringBuilder = function () {
//    var tmp = new Array();
//    this.Append = function (value) {
//        tmp[tmp.length] = value;
//        return this;
//    }
//    this.Clear = function () {
//        tmp.length = 1;
//    }
//    this.toString = function () {
//        return tmp.join('');
//    }
//}

//测试函数执行时间
//function runTime(fn, args) {
//    var date1 = new Date();
//    fn.apply(this, args);
//    var date2 = new Date();
//    alert(date2 - date1);
//}
//function plus(a, b) {
//    for (var i = 1000000 - 1; i >= 0; i--) {

//    };
//    return a + b;
//}
//runTime(plus, [1, 2])﻿//;
(function(win){
    window.__McAjax = function (win) {
    function McAjax(win) {
        this.win = win;
        this.init();
    };
    McAjax.bindReady = function (fn) {
        if (document.addEventListener) {
            //fn();//Ext.require caused DOMContentLoaded error,execute fn() directly
            /**/
            document.addEventListener("DOMContentLoaded", function () {
                document.removeEventListener("DOMContentLoaded", arguments.callee, false);
                fn();
            }, false);
            /**/
        }
        else if (document.attachEvent) {//for IE 
            if (document.documentElement.doScroll && window.self == window.top) {
                (function () {
                    try {
                        document.documentElement.doScroll("left");
                    } catch (ex) {
                        setTimeout(arguments.callee, 5);
                        return;
                    }
                    fn();
                })();
            }
            else {//maybe late but also for iframes 
                document.attachEvent("onreadystatechange", function () {
                    if (document.readyState === "complete") {
                        document.detachEvent("onreadystatechange", arguments.callee);
                        fn();
                    }
                });
            }
        }
    };
    //init after the document.body is ready
    McAjax.ready = function () {
        win.McAjax = McAjax.initInParent();
    };
    //manual load
    McAjax.prototype.Ready = function () {
        var self = this;
        self.init();
        win.McAjax = McAjax.initInParent();
    }

    //postMessage API is supported
    McAjax.prototype.init = function () {
        var self = this;
        var receiver = function (event) {
            if (event.source != self.win) return;
            (self.onmessage || function () { }).call(self, event.data);
        };
        if (window.addEventListener)//FF
            window.addEventListener('message', receiver, false);
        else if (window.attachEvent)//IE
            window.attachEvent('onmessage', receiver);
    };
    McAjax.prototype.send = function (data, _callback) {
        this.win.postMessage(data, '*');
        this.callback = _callback; //set the callback function
    };
    McAjax.initInParent = function () {
        try {
            this.callback = function () { }; //define the callback function       
            var iframe = document.createElement("iframe");
            iframe.id = "iframe_mc";
            iframe.style.visibility = "hidden";
            iframe.style.height = "0";
            iframe.src = mcWebServiceUrl + "/McPASS.html";
            document.body.insertBefore(iframe, null);
            var frame = document.getElementById('iframe_mc');
            //init after load complete
            if (frame.attachEvent) {
                frame.attachEvent("onreadystatechange", function () {
                    if (frame.readyState === "complete" || frame.readyState == "loaded") {
                        frame.detachEvent("onreadystatechange", arguments.callee);
                        McPASS.McPASSInit();//pass init
                    }
                });
            }
            else {
                frame.addEventListener("load", function () {
                    this.removeEventListener("load", arguments.call, false);
                    McPASS.McPASSInit();//pass init
                }, false);
            }
            return new this(frame.contentWindow);
        }
        catch (ex) {
            alert(ex);
        }
    };

    McAjax.initInIframe = function () {
        return new this(window.parent);
    };
    // in IE6/7, postMessage API is not supported
    if (!window.postMessage && window.attachEvent) {
        //so we have to redefine the init method
        McAjax.prototype.init = function () {
            var isSameOrigin = false;
            // test if the two document is same origin
            try {
                isSameOrigin = !!this.win.location.href;
            } catch (ex) { }
            if (isSameOrigin) {
                this.send = this.sendForSameOrigin;
                this.initForSameOrigin();
                return;
            }
            // different origin case
            // init the message queue, which can guarantee the messages won't be lost
            this.queue = [];
            if (window.parent == this.win) {
                this.initForParent();
            } else {
                this.initForFrame();
            }
        };
        McAjax.prototype.initForSameOrigin = function () {
            var self = this;
            document.attachEvent('ondataavailable', function (event) {
                if (!event.eventType ||
                event.eventType !== 'message' ||
                event.eventSource != self.win)
                    return;
                (self.onmessage || function () { }).call(self, event.eventData);
            });
        };
        McAjax.prototype.sendForSameOrigin = function (data) {
            var self = this;
            setTimeout(function () {
                var event = self.win.document.createEventObject();
                event.eventType = 'message';
                event.eventSource = window;
                event.eventData = data;
                self.win.document.fireEvent('ondataavailable', event);
            });
        };
        // create two iframe in iframe page
        McAjax.prototype.initForParent = function () {
            var fragment = document.createDocumentFragment();
            var style = 'width:1px;height:1px;position:absolute;left:-999px;top:-999px;';
            var senderFrame = document.createElement('iframe');
            senderFrame.style.cssText = style;
            fragment.appendChild(senderFrame);
            var receiverFrame = document.createElement('iframe');
            receiverFrame.style.cssText = style;
            fragment.appendChild(receiverFrame);
            document.body.insertBefore(fragment, document.body.firstChild);
            this.senderWin = senderFrame.contentWindow;
            this.receiverWin = receiverFrame.contentWindow;
            this.startReceive();
        };
        // parent page wait the messenger iframe is ready
        McAjax.prototype.initForFrame = function () {
            this.senderWin = null;
            this.receiverWin = null;
            var self = this;
            this.timerId = setInterval(function () {
                self.waitForFrame();
            }, 50);
        };
        // parent page polling the messenger iframe
        // when all is ready, start trying to receive message
        McAjax.prototype.waitForFrame = function () {
            var senderWin;
            var receiverWin;
            try {
                senderWin = this.win[1];
                receiverWin = this.win[0];
            } catch (ex) { }
            if (!senderWin || !receiverWin) return;
            clearInterval(this.timerId);
            this.senderWin = senderWin;
            this.receiverWin = receiverWin;
            if (this.queue.length)
                this.flush();
            this.startReceive();
        };
        // polling the messenger iframe's window.name
        McAjax.prototype.startReceive = function () {
            var self = this;
            this.timerId = setInterval(function () {
                self.tryReceive();
            }, 50);
        };
        McAjax.prototype.tryReceive = function () {
            try {
                // If we can access name, we have already got the data.
                this.receiverWin.name;
                return;
            } catch (ex) { }
            // if the name property can not be accessed, try to change the messenger iframe's location to 'about blank'
            this.receiverWin.location.replace('about:blank');
            // We have to delay receiving to avoid access-denied error.
            var self = this;
            setTimeout(function () {
                self.receive();
            }, 0);
        };
        // recieve and parse the data, call the listener function
        McAjax.prototype.receive = function () {
            var rawData = null;
            try {
                rawData = this.receiverWin.name;
            } catch (ex) { }
            if (!rawData) return;
            this.receiverWin.name = '';
            var self = this;
            var dataList = rawData.substring(1).split('|');
            for (var i = 0; i < dataList.length; i++) (function () {
                var data = decodeURIComponent(dataList[i]);
                setTimeout(function () {
                    (self.onmessage || function () { }).call(self, data);
                }, 0);
            })();
        };
        //send data via push the data into the message queue
        McAjax.prototype.send = function (data, _callback) {
            this.callback = _callback; //set the callback for IE6/7
            this.queue.push(data);
            if (!this.senderWin) return;
            this.flush();
        };
        McAjax.prototype.flush = function () {
            var dataList = [];
            for (var i = 0; i < this.queue.length; i++)
                dataList[i] = encodeURIComponent(this.queue[i]);
            var encodedData = '|' + dataList.join('|');
            try {
                this.senderWin.name += encodedData;
                this.queue.length = 0;
            } catch (ex) {
                this.senderWin.location.replace('about:blank');
                var self = this;
                setTimeout(function () {
                    self.flush();
                }, 0);
            }
        }; //end
    }
    McAjax.bindReady(McAjax.ready);
}
window.__McAjax(win);
})(window);
//McAjax.send(data);
//McAjax.onmessage = function (result) {}﻿//;
if (mcPrWebServiceUrl != "") {
    (function (win) {
        function McAjaxPR(win) {
            this.win = win;
            this.init();
        };
        McAjaxPR.bindReady = function (fn) {
            if (document.addEventListener)
                document.addEventListener("DOMContentLoaded", function () {
                    document.removeEventListener("DOMContentLoaded", arguments.callee, false);
                    fn();
                }, false);
            else if (document.attachEvent) {//for IE 
                if (document.documentElement.doScroll && window.self == window.top) {
                    (function () {
                        try {
                            document.documentElement.doScroll("left");
                        } catch (ex) {
                            setTimeout(arguments.callee, 5);
                            return;
                        }
                        fn();
                    })();
                }
                else {//maybe late but also for iframes 
                    document.attachEvent("onreadystatechange", function () {
                        if (document.readyState === "complete") {
                            document.detachEvent("onreadystatechange", arguments.callee);
                            fn();
                        }
                    });
                }
            }
        };
        //init after the document.body is ready
        McAjaxPR.ready = function () {
            win.McAjaxPR = McAjaxPR.initInParent();
        };
        //postMessage API is supported
        McAjaxPR.prototype.init = function () {
            var self = this;
            var receiver = function (event) {
                if (event.source != self.win) return;
                (self.onmessage || function () { }).call(self, event.data);
            };
            if (window.addEventListener)//FF
                window.addEventListener('message', receiver, false);
            else if (window.attachEvent)//IE
                window.attachEvent('onmessage', receiver);
        };
        McAjaxPR.prototype.send = function (data, _callback) {
            this.win.postMessage(data, '*');
            this.callback = _callback; //set the callback function
        };
        McAjaxPR.initInParent = function () {
            this.callback = function () { }; //define the callback function       
            var iframe = document.createElement("iframe");
            iframe.id = "iframe_pr";
            iframe.style.visibility = "hidden";
            iframe.style.height = "0";
            iframe.src = mcPrWebServiceUrl + "/McPR.html";
            document.body.insertBefore(iframe, null);
            var frame = document.getElementById('iframe_pr');
            //init after load complete
            //if (frame.attachEvent) {
            //    frame.attachEvent("onreadystatechange", function () {
            //        if (frame.readyState === "complete" || frame.readyState == "loaded") {
            //            frame.detachEvent("onreadystatechange", arguments.callee);
            //            McPASS.McInit();//pr init
            //        }
            //    });
            //}
            //else {
            //    frame.addEventListener("load", function () {
            //        this.removeEventListener("load", arguments.call, false);
            //        McPASS.McInit();//pr init
            //    }, false);
            //}
            return new this(frame.contentWindow);
        };
        McAjaxPR.initInIframe = function () {
            return new this(window.parent);
        };
        // in IE6/7, postMessage API is not supported
        if (!window.postMessage && window.attachEvent) {
            //so we have to redefine the init method
            McAjaxPR.prototype.init = function () {
                var isSameOrigin = false;
                // test if the two document is same origin
                try {
                    isSameOrigin = !!this.win.location.href;
                } catch (ex) { }
                if (isSameOrigin) {
                    this.send = this.sendForSameOrigin;
                    this.initForSameOrigin();
                    return;
                }
                // different origin case
                // init the message queue, which can guarantee the messages won't be lost
                this.queue = [];
                if (window.parent == this.win) {
                    this.initForParent();
                } else {
                    this.initForFrame();
                }
            };
            McAjaxPR.prototype.initForSameOrigin = function () {
                var self = this;
                document.attachEvent('ondataavailable', function (event) {
                    if (!event.eventType ||
                    event.eventType !== 'message' ||
                    event.eventSource != self.win)
                        return;
                    (self.onmessage || function () { }).call(self, event.eventData);
                });
            };
            McAjaxPR.prototype.sendForSameOrigin = function (data) {
                var self = this;
                setTimeout(function () {
                    var event = self.win.document.createEventObject();
                    event.eventType = 'message';
                    event.eventSource = window;
                    event.eventData = data;
                    self.win.document.fireEvent('ondataavailable', event);
                });
            };
            // create two iframe in iframe page
            McAjaxPR.prototype.initForParent = function () {
                var fragment = document.createDocumentFragment();
                var style = 'width:1px;height:1px;position:absolute;left:-999px;top:-999px;';
                var senderFrame = document.createElement('iframe');
                senderFrame.style.cssText = style;
                fragment.appendChild(senderFrame);
                var receiverFrame = document.createElement('iframe');
                receiverFrame.style.cssText = style;
                fragment.appendChild(receiverFrame);
                document.body.insertBefore(fragment, document.body.firstChild);
                this.senderWin = senderFrame.contentWindow;
                this.receiverWin = receiverFrame.contentWindow;
                this.startReceive();
            };
            // parent page wait the messenger iframe is ready
            McAjaxPR.prototype.initForFrame = function () {
                this.senderWin = null;
                this.receiverWin = null;
                var self = this;
                this.timerId = setInterval(function () {
                    self.waitForFrame();
                }, 50);
            };
            // parent page polling the messenger iframe
            // when all is ready, start trying to receive message
            McAjaxPR.prototype.waitForFrame = function () {
                var senderWin;
                var receiverWin;
                try {
                    senderWin = this.win[1];
                    receiverWin = this.win[0];
                } catch (ex) { }
                if (!senderWin || !receiverWin) return;
                clearInterval(this.timerId);
                this.senderWin = senderWin;
                this.receiverWin = receiverWin;
                if (this.queue.length)
                    this.flush();
                this.startReceive();
            };
            // polling the messenger iframe's window.name
            McAjaxPR.prototype.startReceive = function () {
                var self = this;
                this.timerId = setInterval(function () {
                    self.tryReceive();
                }, 50);
            };
            McAjaxPR.prototype.tryReceive = function () {
                try {
                    // If we can access name, we have already got the data.
                    this.receiverWin.name;
                    return;
                } catch (ex) { }
                // if the name property can not be accessed, try to change the messenger iframe's location to 'about blank'
                this.receiverWin.location.replace('about:blank');
                // We have to delay receiving to avoid access-denied error.
                var self = this;
                setTimeout(function () {
                    self.receive();
                }, 0);
            };
            // recieve and parse the data, call the listener function
            McAjaxPR.prototype.receive = function () {
                var rawData = null;
                try {
                    rawData = this.receiverWin.name;
                } catch (ex) { }
                if (!rawData) return;
                this.receiverWin.name = '';
                var self = this;
                var dataList = rawData.substring(1).split('|');
                for (var i = 0; i < dataList.length; i++) (function () {
                    var data = decodeURIComponent(dataList[i]);
                    setTimeout(function () {
                        (self.onmessage || function () { }).call(self, data);
                    }, 0);
                })();
            };
            //send data via push the data into the message queue
            McAjaxPR.prototype.send = function (data, _callback) {
                this.callback = _callback; //set the callback for IE6/7
                this.queue.push(data);
                if (!this.senderWin) return;
                this.flush();
            };
            McAjaxPR.prototype.flush = function () {
                var dataList = [];
                for (var i = 0; i < this.queue.length; i++)
                    dataList[i] = encodeURIComponent(this.queue[i]);
                var encodedData = '|' + dataList.join('|');
                try {
                    this.senderWin.name += encodedData;
                    this.queue.length = 0;
                } catch (ex) {
                    this.senderWin.location.replace('about:blank');
                    var self = this;
                    setTimeout(function () {
                        self.flush();
                    }, 0);
                }
            }; //end
        }
        McAjaxPR.bindReady(McAjaxPR.ready);
    })(window);
};﻿//1.pass 2.pr
function MC_client_request(type) {
    return new McPASS.McAjaxSync(type);
};

McPASS.McAjaxSync = function (type) {
    //pass default
    this.serverUrl = mcWebServiceUrl + "/PASSwebService.asmx";
    if (type == 2) {
        this.serverUrl = mcPrWebServiceUrl + "/WebService/Service.asmx";
    }
    this.params = [];
    this.timeout = 1000;
};

//Initializes the IsAvailable is true
//this global value is used to control to request or not to request to server;
McPASS.McAjaxSync.IsAvailable = true;
McPASS.McAjaxSync.prototype = {
    createXMLHttpRequest: function () {
        var xhr;
        if (window.ActiveXObject) {
            xhr = new ActiveXObject("Microsoft.XMLHTTP");
        }
        else if (window.XMLHttpRequest) {
            xhr = new XMLHttpRequest();
            if (xhr.readyState == null) {
                xhr.readyState = 0;
                xhr.addEventListener("load", function () {
                    xhr.readyState = 4;
                    if (typeof (xhr.onreadystatechange) == "function") {
                        xhr.onreadystatechange();
                    }
                }, false);
            }
        }
        else if (window.XDomainRequest) {
            xhr = new XDomainRequest();
        }
        return xhr;
    }, //end

    loadXMLString: function (txt) {
        try {
            var xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
            xmlDoc.async = false;
            xmlDoc.loadXML(txt);
            return (xmlDoc);
        }
        catch (e) {
            try {
                var parser = new DOMParser();
                var xmlDoc = parser.parseFromString(txt, "text/xml");
                return (xmlDoc);
            }
            catch (e) { return (null); }
        }
    }, //end

    AddParam: function (key, value) {
        if (this.params == null) {
            this.params = [];
        }
        this.params[this.params.length] = { "key": key, "value": value };
    },
    ClearParam: function () {
        this.params.length = 0;
    }, //end

    post: function (methodStr, callback) {
        return this.fetchData(methodStr, "POST", callback);
    }, //end      
    fetchData: function (methodStr, httpMethod, callback) {
        if (mc_pass_init_success != 1) {
            return {
                error: true, value: '[ERROR]:初始化失败', errmore: ''
            };
        }
        if (!McPASS.McAjaxSync.IsAvailable) {
            return {
                error: true, value: '[ERROR]:服务端不可用', errmore: ''
            };
        }
        var xhr = this.createXMLHttpRequest();
        var data;
        if (httpMethod == "POST") {
            var _d_arr = [];
            if (this.params != null && this.params.length > 0) {
                for (var i = 0; i < this.params.length; i++) {
                    _d_arr[_d_arr.length] = this.params[i].key + "=" + this.params[i].value;
                }
            }
            data = _d_arr.join("&");
        }

        if (httpMethod == "POST") {
            try{
                xhr.open("POST", this.serverUrl + "/" + methodStr, false);
                xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");               
                xhr.send(data);
            } catch (e) {
                McPASS.McAjaxSync.IsAvailable = false;
                return {
                    error: true, value: '[ERROR]:服务器异常或网络错误', errmore: '500'
                };
            }           
        }

        if (callback == null) {
            if (xhr.status == 200) {
                var xmldoc = this.loadXMLString(xhr.responseText);
                var rstr = xmldoc.documentElement.text;
                if (rstr == undefined) {
                    rstr = xmldoc.documentElement.textContent;
                }
                return { error: false, value: rstr };
            }
            else {
                McPASS.McAjaxSync.IsAvailable = false;
                return {
                    error: true, value: '[ERROR]:' + xhr.status, errmore: xhr.status
                };
            }
        }
        return "";
    } //end
};﻿McPASS.toMcJSONString = function (object) {
    if (object == null) {
        return;
    }
    var type = typeof object;
    if ('object' == type) {
        if (Array == object.constructor) type = 'array';
        else if (RegExp == object.constructor) type = 'regexp';
        else type = 'object';
    }
    switch (type) {
        case 'undefined':
        case 'unknown':
            return;
            break;
        case 'function':
        case 'boolean':
        case 'regexp':
            return object.toString();
            break;
        case 'number':
            return isFinite(object) ? object.toString() : 'null';
            break;
        case 'string':
            return '"' + object.replace(/(\\|\")/g, "\\$1").replace(/\n|\r|\t/g, function () {
                var a = arguments[0];
                return (a == '\n') ? '\\n' : (a == '\r') ? '\\r' : (a == '\t') ? '\\t' : ""
            }) + '"';
            break;
        case 'object':
            if (object === null) return 'null';
            var results = [];
            for (var property in object) {
                var value = this.toMcJSONString(object[property]);
                if (value !== undefined) results.push(this.toMcJSONString(property) + ':' + value);
            }
            return '{' + results.join(',') + '}';
            break;
        case 'array':
            var results = [];
            for (var i = 0; i < object.length; i++) {
                var value = this.toMcJSONString(object[i]);
                if (value !== undefined) results.push(value);
            }
            return '[' + results.join(',') + ']';
            break;
    }
};

/*demo 
var obj = {
    name: "frank",
    friend: ["LD", "TL", "DX"],
    action: function () {
        alert("go")
    },
    boy: true,
    age: 26,
    reg: /\b([a-z]+) \1\b/gi,
    child: {
        name: "none",
        age: -1
    }
};
var json = McPASS.toMcJSONString(obj);
alert(json);
 */﻿McPASS.SysMenu = {
    DisplayMode: {
        MenuState: 0, //右键菜单时查的ReferenceTip
        Brief: 1,  //简要信息
        SP: 2,     //特殊人群 药物禁忌 性别用药 成人用药
        Inter: 3,  //相互作用
        IV: 4,     //体外配伍
        ADR: 5,    //不良反应
        Level: 6,  //配伍浓度
        PICPRPECHP: 7, //说明书，药物专论，药典，病人用药教育
        Wholetext: 8//药物适应症 肝肾剂量 细菌耐率
    },
    McDetail: {
        Contraind: 1,    //药物禁忌
        Druginter: 2,    //相互作用
        Dosage: 3,       //剂量范围
        Pedtatric: 4,    //儿童用药
        Geriatric: 5,    //老人用药
        Pregnancy: 6,    //妊娠用药
        Lactation: 7,    //哺乳用药
        Indication: 8,   //超适应症
        Drugallergen: 9, //药物过敏
        IV: 10,          //体外配伍
        Adult: 11,       //成人用药
        Sex: 12,         //性别用药
        ADR: 13,         //不良反应
        Route: 14,       //给药途径
        Duplicate: 15,   //重复用药
        DrugAsp: 16,     //药物抗菌
        Druglevel: 17,   //配伍浓度
        Drugfood: 18     //药食作用
    } //end     
};
//通过工作站类型返回查询和审查菜单
McPASS.McGetModuleName = function (_callback) {
    try {
        //初始化失败，pass功能禁用
        if (mc_pass_init_success != 1) {
            if (_callback) {
                _callback();
            }
            return;
        }

        if (MCrefModuleArray.length > 0 || MCrefModuleArray.length > 0) {
            if (_callback) {
                _callback();
            }
            return;
        }
        var _obj = {
            PassClient: MCPASSclient,
            ModuleParam: MCModuleParam
        };
        var obj = {
            PASSFuncName: "Mc_DoModule",
            PASSParam: McPASS.toMcJSONString(_obj)
        };
        var data = McPASS.toMcJSONString(obj);
        McAjax.send(data);
        McAjax.onmessage = function (result) {
            if (result.indexOf("[ERROR]") < 0) {
                MCrefModuleArray = [];
                MCscreenModuleArray = [];
                var res = McPASS.fn.EvalJson(result);
                if (res) {
                    MCscreenModuleArray = res.ScreenModuleList;
                    MCdataVersion = res.DataVersion;
                    MCprojectVersion = res.ProjectVersion;
                    //MChospitalName = res.UserName;
                    MChospitalName = res.RHCName;//2015-11-24刘春梅和刘小留要求 无论那种情况直接取HasRHC
                    MCcanGetScreenDtlRes = res.CanGetScreenDtlRes;//能否获取审查详细信息json串
                    MCrefModuleArray = res.ReferenceModuleList;
                    /*********************说明书+自定义说明书 mcenun.js rightmenu.js**************************/
                    var len = MCrefModuleArray.length;
                    var blsystem = 0;
                    var bluser = 0;
                    var index = 0;
                    for (var j = 0; j < len; j++) {
                        var obj = MCrefModuleArray[j];
                        if (obj.ModuleID == 11) {
                            blsystem = 1;
                        }
                        if (obj.ModuleID == 14) {
                            bluser = 1;
                            index = j;
                        }
                    }
                    //这种情况按系统的进行查询，至于ModuleName只能写死成这样
                    if (!blsystem && bluser) {
                        MCrefModuleArray[index].ModuleID = 11;
                        MCrefModuleArray[index].ModuleName = "药品说明书";
                        MCrefModuleArray[index].IsShowMenu = 1;
                    }
                    /***************************************************************************************************/
                    //if (res.HasRHC) {
                    //    MChospitalName = res.RHCName;
                    //}
                    if (_callback) {
                        _callback();
                    }
                }
                else {
                    McPASS.McGetModuleName(_callback); //try again
                }
            }
                //error,invoke callback
            else {
                if (MCPASSclient.ProductCode == "mKPpCv1n4c4h5") {
                    mc_product_switch.PR = 0;
                }
                else if (MCPASSclient.ProductCode == "mlf35wieQdgHJ478gk06slVGSMrhy9Y2yjfhlkTv1") {
                    mc_product_switch.PASS = 0;
                }
                else {
                }
                if (_callback) {
                    _callback();
                }
            }
        };
    }
    catch (er) {
        alert("错误提示：" + er.ToString());
    }
};

//PASS初始化
McPASS.McPASSInit = function () {
    try {
        if (mc_pass_init_success == 1) {
            return;
        }
        var _obj = {
            PassClient: MCPASSclient
        };
        var obj = {
            PASSFuncName: "Mc_Init",
            PASSParam: McPASS.toMcJSONString(_obj)
        };
        var data = McPASS.toMcJSONString(obj);
        McAjax.send(data);
        McAjax.onmessage = function (result) {
            if (result.indexOf("[ERROR]") < 0) {
                var res = McPASS.fn.EvalJson(result);
                if (res == 1) {
                    mc_pass_init_success =1;//初始化成功
                }
            }
            else if (result == "[ERROR]:timeout") {
                mc_pass_init_success = -99;//超时
            }
            else {
                //初始化遇到core异常
                mc_pass_init_success = -10;
            }
        };
    }
    catch (er) {
    }
};﻿var MC_global_ref_HISdata; //query Data from HIS
var MC_global_ref_PASSdata; //query result from PASS
var MC_global_Screen_HISdata;  //screen Data from HIS
var MC_global_Screen_PASSdata;  //screen result from PASS
var MC_global_Screen_PRstatus;  //screen result from PR , Only for screen
var MC_global_Screen_Count = 0; //screen result counts
var MC_global_Detail_HISdata; //detail Data from HIS
var MC_global_Detail_PASSdata; //detail result from PASS 
var MC_current_Detail_divId = "";
var MC_current_Detail_btnId = "";

var mc_msg_win_zIndex = 100000;
var mc_shade_first_zIndex = 100001;
var mc_screen_win_zIndex = 100002;
var mc_shade_second_zIndex = 100003;
var mc_ref_win_zIndex = 100004;
var mc_shade_color_zIndex = 100005; //color
var mc_alert_win_zIndex = 100006; //alert
var mc_shade_disclaimer_zIndex = 100007; //disclaimer
var mc_disclaimer_win_zIndex = 100008; //disclaimer 

var mc_pass_init_success = 0; //pass init success status(0失败，1成功，-10 core异常，-99超时)

var mc_cancloseWindow = 1; //close screen window
var mc_is_screen_now = 0; //stop screen when screen now 
var mc_is_pr_request = 0;//pr request for ajax webservice swith
var mc_is_fullfill = 1; //是否填写所有理由 0-否 1-无审查结果不需要填 2-系统配置为理由可以不用填 3-已全部填完

var mc_pr_status = 1;//1:through;0 not through 只同步时使用
var mc_pr_statusobj;//获取的状态信息对象， 只同步时使用 add by liuchunmei 2017-04-11
var mc_pass_isreg = false;//pass is reg
var mc_pr_isreg = false;//pr是否注册可用
//his control switch
var mc_product_switch = {
    PASS: (mcWebServiceUrl == "" ? 0 : 1),
    PR: (mcPrWebServiceUrl == "" ? 0 : 1)
};﻿var McDrugsArray = new Array();
var McAllergenArray = new Array();
var McMedCondArray = new Array();
var McOperationArray = new Array();
var McLabArray = new Array();
var McExamArray = new Array();
var MCpatientInfo;
var MCPASSclient;
var MCModuleParam;

var MCrefModuleArray = new Array();
var MCscreenModuleArray = new Array();
var MCdataVersion = "";    //database version
var MCprojectVersion = ""; //core version
var MCcanGetScreenDtlRes = 0;//是否有权限获取审查简要信息json串
var MChospitalName = ""; //医院名称UserName或者区域医疗名称RHCName
var McrefTipArray = new Array(); //菜单哪些模块可用，包括自定义查询的
var McScreenReasonArray = new Array(); //审查，理由必填项

/*理由*/
this.Params_MC_UseReason_In = function () {
    this.Hiscode = "";
    this.Hisname = "";
    this.PatStatus = 1;
    this.InHospNo = "";
    this.VisitCode = "";
    this.PatCode = "";
    this.RecipNo = "";
    this.OrderDeptCode = "";
    this.OrderDoctorCode = "";
    this.OrderStartTime = "";
    this.OrderEndTime = "";
    this.OrderExecuteTime = "";
    this.DrugUniqueCode = "";
    this.DoseUnit = "";
    this.DosePerTime = "";
    this.Frequency = "";
    this.IsTempDrug = 0;
    this.ModuleID = 0;
    this.ModuleName = "";
    this.Slcode = 0;
    this.Severity = "";
    this.Warning = "";
    this.ModuleItems = "USER";
    this.OtherInfo = "";
    this.IsForStatic = 1;
    this.reason = "";
    //    this.Resulttype = "USER";
    //    this.VerifyCode = "";
    //    this.drugindex = "";
};

this.Params_MC_ModuleParam_In = function () {
    this.CheckMode = MC_global_CheckMode;
    this.ClientIP = "";
};
MCModuleParam = new Params_MC_ModuleParam_In();

this.Params_MC_PASSclient_In = function () {
    this.HospID = ""; //医院编码
    this.HospName = ""; //医院名称
    this.UserID = "";
    this.UserName = "";
    this.DeptID = "";
    this.DeptName = "";
    this.IP = "";
    this.PCName = "";
    this.OSInfo = ""; //操作系统
    this.Resolution = ""; //分辨率  
    this.PassVersion = McPASS.isPC() ? "1" : "2";//1pc 2pad
    this.mac = "";//服务器电脑mac地址
    this.otherinfo = "";//其它信息,备用
    this.CheckMode = MC_global_CheckMode;//在病人信息里取了，这里不要了；但是2016-08-22刘春梅说要留起 兼容新JS 旧Core的情况
};
MCPASSclient = new Params_MC_PASSclient_In();

this.Params_MC_queryDrug_In = function () {
    this.ReferenceCode = ""; //用于查询的编码
    this.CodeName = "";       //名称
    this.CodeSource = "USER"; //来源 
    this.IsDrug = 1;         //1-药品 0-具体某一模块对应编码
    this.ReferenceType = 0;  //查询右键菜单是否可用 0，1，2，3   
    this.DisplayMode = 1;    //显示模板 1 -cs ;2 bs调用core不返回说明书图片base64编码（没这样用）   
    this.RefMode = 1;        //1pass 2user        
};
var MC_global_queryDrug = new Params_MC_queryDrug_In();

var McDetailItems = new Array(); //查询的明细
var McDetailItemsScreen = new Array(); //审查的明细
//明细
this.McDetailItems_In = function () {
    this.Delimiter = "";
    this.SubDelimiter = "";
    this.DetailParams = "";
    this.DetailType = 0;
    this.DetailTip = "";
    this.Abstract = "";
    this.LinkText = ""; //用于查更多明细的提示文本
    this.LinkParams = ""; //用于查更多明细的查询参数
};
this.Params_MC_Patient_In = function () {
    this.PatCode = "";   //编号
    this.InHospNo = "";  //门诊号/住院号
    this.VisitCode = ""; //门诊号/住院次数
    this.Name = "";      //姓名
    this.Sex = "";       //性别   
    this.Birthday = "";  //出生日期
    this.HeightCM = "";  //身高（cm）
    this.WeighKG = "";   //体重（kg）   
    this.DeptCode = "";    //科室编码
    this.DeptName = "";    //科室名称
    this.DoctorCode = "";  //主管医生编码
    this.DoctorName = "";  //主管医生姓名
    this.PatStatus = 1;    //状态 0-出院，1-住院（默认），2-门诊，3-急诊
    this.IsLactation = -1; //是否哺乳 -1-无法获取哺乳状态（默认） 0-不是 1-是    
    this.IsPregnancy = -1; //是否妊娠 -1-无法获取妊娠状态（默认） 0-不是 1-是 2  
    this.PregStartDate = ""; //妊娠开始日期，不为空，则计算妊娠期，否则出全期数据
    this.HepDamageDegree = -1; //-1-无法获取肝损害状态（默认） 0-无肝损害  1-存在肝损害，但损害程度不明确 2-轻度 3-中度 4-重度
    this.RenDamageDegree = -1; //-1-无法获取肾损害状态（默认） 0-无肾损害  1-存在肾损害，但损害程度不明确 2-轻度 3-中度 4-重度
    this.UseTime = ""; //审查时间 不提供给HIS使用，如果把这个时间改到开始时间和结束时间之外，那就审查不出问题
    this.CheckMode = MC_global_CheckMode; //审查模式,HIS传入  
    this.IsDoSave = 1; //是否采集   
    this.Age = "";      //年龄
    this.PayClass = ""; //费别
    this.IsTestEtiology = 0; //是否做过病原学检查  0-未做过（默认） 1-做过
    this.InHospDate = "";  //入院日期
    this.OutHospDate = ""; //出院日期    
    this.IDCard = "";    //身份证号
    this.Telephone = ""; //病人联系电话  
    this.Urgent = 0; //是否加急 0-普通（默认） 1-加急
};
this.Params_Mc_Drugs_In = function () {//31
    this.RecipNo = ""; //处方号
    this.Index = "";        //药品序号
    this.OrderNo = 1; 		//医嘱号 如果传的是-1，得给他自动++
    this.DrugSource = "USER";
    this.DrugUniqueCode = ""; //药品唯一码
    this.DrugName = ""; 	//药品名称
    this.DoseUnit = "";   	//给药单位
    this.Form = ""; //剂型
    this.Strength = ""; //规格
    this.CompName = ""; //厂家
    this.RouteSource = "USER";
    this.RouteCode = ""; 	//给药途径编码
    this.RouteName = "";  	//给药途径名称
    this.FreqSource = "USER";
    this.Frequency = ""; 	//用药频次
    this.DosePerTime = ""; 	//单次用量
    this.StartTime = ""; 	//开嘱时间
    this.EndTime = ""; 		//停嘱时间
    this.ExecuteTime = ""; 	//执行时间
    this.DeptCode = ""; 	//开嘱科室编码
    this.DeptName = ""; 	//开嘱科室名称
    this.DoctorCode = ""; 	//开嘱医生编码
    this.DoctorName = ""; 	//开嘱医生姓名 
    this.GroupTag = ""; 	//成组标记
    this.IsTempDrug = 0;    //是否临时用药 0-长期 1-临时
    this.OrderType = 0;     //医嘱类别标记  0-在用（默认），1-已作废，2-已停嘱，3-出院带药
    this.Pharmacists = ""; //审核/调配药师
    this.Pharmacists_ = ""; //核对/发药药师
    this.Num = ""; //药品开出数量
    this.NumUnit = ""; //药品开出数量单位
    this.Cost = "";   //费用
    this.Purpose = 0; //用药目的(1可能预防，2可能治疗，3预防，4治疗，5预防+治疗, 0默认)
    this.OprCode = ""; //手术编号，如果对应多手术，用'，'隔开，表示该药为该编号对应的手术用药
    this.MediTime = ""; //用药时机 （术前、术中、术后）？（0- 未使用 1-  0.5h以内  2- 0.5-2h  3-大于2h）
    this.Remark = "";   // 医嘱备注
    this.driprate = "0";//滴速(120滴/分钟) 
    this.driptime = "0";//滴时间(滴100分钟)
    this.IsOtherRecip = 0; // 历史处方标记 只在门诊生效 add by liuchunmei 2016-12-23 添加
    this.duration = 0; // 用药天数接口 add by liuchunmei 2017-04-25
};
this.Params_Mc_Allergen_In = function () {
    this.Index = "";        //序号  
    this.AllerSource = "USER";
    this.AllerCode = "";    //编码
    this.AllerName = "";    //名称  
    this.AllerSymptom = ""; //过敏症状      
};
this.Params_Mc_MedCond_In = function () {
    this.RecipNo = ""; //处方号
    this.Index = "";        //诊断序号
    this.DisSource = "USER";
    this.DiseaseCode = "";  //诊断编码
    this.DiseaseName = "";  //诊断名称      
    this.DisTimeType = 0; //0-出院诊断（默认） 1-入院诊断 当为门诊病人时，不计算该标记
    this.Ishospinfection = -1; //是否院类继发感染   1-是 0-不是   
    this.starttime = "";//诊断开始时间
    this.endtime = "";  //诊断结束时间
};
this.Params_Mc_Operation_In = function () {
    this.Index = "";        //序号
    this.OprCode = "";      //编码
    this.OprName = "";      //名称
    this.IncisionType = "1"; //切口类型
    this.OprStartDate = ""; //手术开始时间（YYYY-MM-DD HH:MM:SS)
    this.OprEndDate = "";   //手术结束时间 （YYYY-MM-DD HH:MM:SS)   
    this.OprMediTime = -1; //手术用药时机   0- 未使用 1-  0.5h以内  2- 0.5-2h  3-大于2h
    this.OprTreatTime = -1; //手术预防使用抗菌药物疗程  单位（小时）
}; 

this.Params_Mc_Lab_In = function () {
    this.RequestNo = "";    // 检验/检查申请单序号
    this.LabExamCode = "";  // 检验/检查申请项目编码
    this.LabExamName = "";  // 检验/检查申请项目名称
    this.StartDateTime = "";// 检验/检查申请单下达时间
    this.DeptCode = "";     // 申请科室
    this.DeptName = "";     // 申请科室名称
    this.DoctorCode = "";   // 申请医生编码
    this.DoctorName = "";   // 申请医生
};

this.Params_Mc_Exam_In = function () {
    this.RequestNo = "";    // 检验/检查申请单序号
    this.LabExamCode = "";  // 检验/检查申请项目编码
    this.LabExamName = "";  // 检验/检查申请项目名称
    this.StartDateTime = "";// 检验/检查申请单下达时间
    this.DeptCode = "";     // 申请科室
    this.DeptName = "";     // 申请科室名称
    this.DoctorCode = "";   // 申请医生编码
    this.DoctorName = "";   // 申请医生
};

var McDetailItemsScreen = new Array(); //审查的明细
//审查的明细
this.McDetailItems_In = function () {
    this.Delimiter = "";
    this.SubDelimiter = "";
    this.DetailParams = "";
    this.DetailType = 0;
    this.DetailTip = "";
    this.Abstract = "";
    this.LinkText = "";   //用于查更多明细的提示文本
    this.LinkParams = ""; //用于查更多明细的查询参数
};  

/*获取PR中处方状态*/
this.Params_MC_GetStatus_In = function () {   
    this.HospID = "";   //医院编码   
    this.PatCode = "";  //病人编号
    this.InHospNo = ""; //门诊号/住院号
    this.VisitCode = "";//门诊号/住院次数
    this.RecipNo = "";  //处方号
    this.TaskType = 2; //门诊2，住院1   
};
var McGetStatus = new Params_MC_GetStatus_In();﻿McPASS.McClsWindow = function (config) {
    this.config = config;
};
McPASS.McClsWindow.prototype = {
    show: function () {
        mcWindowHeight = mcWindowHeight < 400 ? 400 : mcWindowHeight;
        mcWindowHeight = mcWindowHeight > 700 ? 700 : mcWindowHeight;
        mcWindowWidth = mcWindowWidth < 700 ? 700 : mcWindowWidth;
        mcWindowWidth = mcWindowWidth > 1000 ? 1000 : mcWindowWidth;
        var top = (window.screen.availHeight - 200 - mcWindowHeight) / 2;
        var left = (window.screen.availWidth - 200 - mcWindowWidth) / 2;
        top = top < 100 ? 100 : top;
        left = left < 100 ? 100 : left;
        var scrollLeft = document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft;
        var scrollTop = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop;
        this.config.top = top + scrollTop;
        this.config.left = left + scrollLeft;
        //custom position of let,top
        if (MC_Window_Left > 0) {
            this.config.left = MC_Window_Left;
        }
        if (MC_Window_Top > 0) {
            this.config.top = MC_Window_Top;
        }

        this.config.screen = this.config.screen ? 1 : 0; //是否是审查       
        var mcHeader = ["<table><tr><td class='mcHeader_logo' style='background-image:url(" + mcWebServiceUrl + "/PassImage/passlogo.gif);background-repeat:no-repeat;'></td><td class='mcHeader_Hospital'>", MChospitalName, "</td><td><a class='mcHeader_close' id='mcMsgWindowHead_closer' style='background-image:url(" + mcWebServiceUrl + "/PassImage/mc_close_normal.gif);background-repeat:no-repeat;' onmouseover=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/mc_close_hover.gif)';\" onmouseout=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/mc_close_normal.gif)';\" onclick='McPASS.McCloseWindow(", this.config.screen, ");scroll(0,0);'></a></td></tr></table>"];
        var mcBottom = ["<div class='mcDivWindowBottomVersion'><a style='text-decoration:underline;color:Highlight;_cursor:pointer;cursor:hand;cursor:pointer;' onclick='McPASS.DisclaimerWindow();'>免责声明</a>&nbsp;&nbsp;&nbsp;&nbsp;服务程序版本：", MCprojectVersion, " &nbsp;&nbsp;&nbsp;&nbsp; 数据版本：", MCdataVersion, "</div>",
                        "<div class='mcDivWindowBottomLogo' style='background-image:url(" + mcWebServiceUrl + "/PassImage/mcLogo.jpg);background-repeat:no-repeat;'></div>"
        ];
        //[body=window-header-bottom]-->height-50-30=80  bottom=30-top线(1px)=29px
        var mcWindow = [
                        "<div id='mcDivWindow' class='mcDivWindow' style='width:", mcWindowWidth, "px;height=", mcWindowHeight, "px;overflow:hidden;top:", this.config.top, "px;left:", this.config.left, "px; z-index:", this.config.z, ";' oncontextmenu='return false;' onselectstart='return false;' onselect='return false' oncopy='return false'>",
                           "<div id='mcDivWindowHead' style='width:", mcWindowWidth, "px;background-image:url(" + mcWebServiceUrl + "/PassImage/mcwinheader.gif);background-repeat:repeat-x;height:50px;cursor:move;'>", mcHeader.join(""), "</div>",
                           "<div id='mcDivWindowBody' class='mcDivWindowBody' style='width:", mcWindowWidth, "px;height:", mcWindowHeight - 80, "px;overflow:hidden;'>", this.config.body, "</div>",
                           "<div id='mcDivWindowBottom' class='mcDivWindowBottom' style='width:", mcWindowWidth, "px;'>", mcBottom.join(""), "</div>",
                           "<iframe id='mc_iframe_bg' class='mc_iframe_bg' frameborder='0'></iframe>",
                        "</div>"
        ];
        
        var mcShade = [""];
        if (this.config.shade) {
            mcShade = ["<div id='mcDivShade' class='mcDivShade' style='z-index:", this.config.zShade, ";filter:Alpha(opacity=", this.config.shadeOpacity, ");'><iframe class='mc_iframe_bg' frameborder='0'></iframe></div>"];
        }
        var node = document.createElement("div");
        node.innerHTML = mcWindow.join("") + mcShade.join("");
        // add by lxl 2017-08-14 16:34
        node.setAttribute("x-ms-format-detection", "none");
        // end add
        document.body.insertAdjacentElement("afterBegin", node);
        McPASS.fn.byId("mcDivShade").style.height = parseInt(document.body.scrollHeight) + "px";
        McPASS.fn.byId("mcDivShade").style.width = parseInt(document.body.scrollWidth) + "px";

        McPASS.dragWindow.drag();
    }, //end
    //单药警告
    singleScreenshow: function () {//单药审查窗口       
        var H = 250;
        var W = 450;
        var array = [
          "<div id='mcDivWindowHead' class='mcMsgWindowHead' style='background-image:url(" + mcWebServiceUrl + "/PassImage/mcheader.gif);background-repeat:repeat-x;'><div id='mcDivscreendTitle' class='mcMsgWindowHead_logo' style='padding-left:100px;color:white;font-size:14px;font-weight:bold;background-image:url(" + mcWebServiceUrl + "/PassImage/mcpasslogo.gif);background-repeat:no-repeat;'>&nbsp;</div><a class='mcMsgWindowHead_close' id='mcMsgWindowHead_closer' style='background-image:url(" + mcWebServiceUrl + "/PassImage/McMsg_close_normal.gif);background-repeat:no-repeat;' onmouseover=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/McMsg_close_hover.gif)';\" onmouseout=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/McMsg_close_normal.gif)';\" onclick='McPASS.McCloseWindow(0);'></a></div>",
           "<div id='mcDivWindowBody' class='mcDivWindowBody' style='height:", H - 40, "px;width:", W, "px;'><div class='mc_common_loading' style='background-image:url(" + mcWebServiceUrl + "/PassImage/loading.gif);background-repeat:no-repeat;'></div></div>",
        //"<div id='mc_resizediv' style='position:absolute;right:1px;bottom:1px;cursor:nw-resize;' title='拖动改变窗体大小'>&nbsp;</div>"
           ""
        ];
        var strArray = [
            "<div id='mcDivWindow' oncontextmenu='return false' onselectstart='return false' onselect='return false' oncopy='return false' ",
            "style='-moz-user-select:none;border:solid 1px #c9c9c9;background-color:White;position:absolute;width:", W, "px;height:", H, "px;overflow:hidden;top:",
             this.config.top, "px;left:", this.config.left, "px; z-index:", mc_msg_win_zIndex, ";'>", array.join(''),
             "<iframe class='mc_iframe_bg' frameborder='0'></iframe></div>"
        ];
        var node = document.createElement("div");
        node.innerHTML = strArray.join("");
        document.body.insertAdjacentElement("afterBegin", node);
        McPASS.dragWindow.drag();
    }, //end
    //免责申明
    disclaimerWindow: function () {//免责声明窗口       
        var H = 450;
        var W = 600;
        var top = (window.screen.availHeight - 200 - H) / 2;
        var left = (window.screen.availWidth - 200 - W) / 2;
        top = top < 100 ? 100 : top;
        left = left < 100 ? 100 : left;
        var scrollLeft = document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft;
        var scrollTop = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop;
        this.config.top = top + scrollTop;
        this.config.left = left + scrollLeft;
        var array = [
           "<div id='mcDivWindowHead' style='background-image:url(" + mcWebServiceUrl + "/PassImage/mcwinheader.gif);background-repeat:repeat-x;height:50px;cursor:move;'><div id='mcDivscreendTitle' class='mcHeader_logo' style='padding-left:100px;color:white;font-size:14px;font-weight:bold;background-image:url(" + mcWebServiceUrl + "/PassImage/passlogo.gif);background-repeat:no-repeat;'>&nbsp;</div><a class='mcHeader_close' style='background-image:url(" + mcWebServiceUrl + "/PassImage/mc_close_normal.gif);background-repeat:no-repeat;' onmouseover=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/mc_close_hover.gif)';\" onmouseout=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/mc_close_normal.gif)';\" onclick='McPASS.McCloseWindow(0);'></a></div>",
           "<div id='mcDivWindowBody' class='mcDivWindowBody' style='height:", H - 40, "px;width:", W, "px;'><div class='mc_common_loading' style='background-image:url(" + mcWebServiceUrl + "/PassImage/loading.gif);background-repeat:no-repeat;'></div></div>"
        ];
        var strArray = [
            "<div id='mcDivWindow' oncontextmenu='return false' onselectstart='return false' onselect='return false' oncopy='return false' ",
            "style='-moz-user-select:none;border:solid 1px #c9c9c9;background-color:White;position:absolute;width:", W, "px;height:", H, "px;overflow:hidden;top:",
             this.config.top, "px;left:", this.config.left, "px; z-index:", mc_disclaimer_win_zIndex, ";'>", array.join(''),
             "<iframe class='mc_iframe_bg' frameborder='0'></iframe></div>"
        ];

        var mcShade = ["<div id='mcDivShade' class='mcDivShade' style='z-index:", mc_shade_disclaimer_zIndex, ";filter:Alpha(opacity=0);'><iframe class='mc_iframe_bg' frameborder='0'></iframe></div>"];
        var node = document.createElement("div");
        node.innerHTML = strArray.join("") + mcShade.join("");
        document.body.insertAdjacentElement("afterBegin", node);
        McPASS.fn.byId("mcDivShade").style.height = parseInt(document.body.scrollHeight) + "px";
        McPASS.fn.byId("mcDivShade").style.width = parseInt(document.body.scrollWidth) + "px";
        McPASS.dragWindow.drag();

    }, //end
    //必填理由
    Alertshow: function () {//审查提示必填理由       
        var H = 150;
        var W = this.config.close == 1 ? 350 : 250;

        var top = (window.screen.availHeight - 200 - H) / 2;
        var left = (window.screen.availWidth - 200 - W) / 2;
        top = top < 100 ? 100 : top;
        left = left < 100 ? 100 : left;
        var scrollLeft = document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft;
        var scrollTop = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop;
        this.config.top = top + scrollTop;
        this.config.left = left + scrollLeft;
        var array = [
          "<div id='mcDivWindowHead' class='mcMsgWindowHead' style='background-image:url(" + mcWebServiceUrl + "/PassImage/mcheader.gif);background-repeat:repeat-x;'><div id='mcDivscreendTitle' class='mcMsgWindowHead_logo' style='padding-left:100px;color:white;font-size:14px;font-weight:bold;background-image:url(" + mcWebServiceUrl + "/PassImage/mcpasslogo.gif);background-repeat:no-repeat;'>&nbsp;</div><a class='mcMsgWindowHead_close' id='mcMsgWindowHead_closer' style='background-image:url(" + mcWebServiceUrl + "/PassImage/McMsg_close_normal.gif);background-repeat:no-repeat;' onmouseover=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/McMsg_close_hover.gif)';\" onmouseout=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/McMsg_close_normal.gif)';\" onclick='McPASS.McCloseWindow(0);'></a></div>",
           "<div id='mcDivWindowBody' class='mcDivWindowBody' style='height:", H - 40, "px;width:", W, "px;'><div class='mc_common_loading' style='background-image:url(" + mcWebServiceUrl + "/PassImage/loading.gif);background-repeat:no-repeat;'></div></div>"
        ];
        var strArray = [
            "<div id='mcDivWindow' oncontextmenu='return false' onselectstart='return false' onselect='return false' oncopy='return false' ",
            "style='-moz-user-select:none;border:solid 1px #c9c9c9;background-color:White;position:absolute;width:", W, "px;height:", H, "px;overflow:hidden;top:",
             this.config.top, "px;left:", this.config.left, "px; z-index:", mc_alert_win_zIndex, ";'>", array.join(''),
             "<iframe class='mc_iframe_bg' frameborder='0'></iframe></div>"
        ];

        var mcShade = ["<div id='mcDivShade' class='mcDivShade' style='z-index:", mc_shade_second_zIndex, ";filter:Alpha(opacity=0);'><iframe class='mc_iframe_bg' frameborder='0'></iframe></div>"];
        var node = document.createElement("div");
        node.innerHTML = strArray.join("") + mcShade.join("");
        document.body.insertAdjacentElement("afterBegin", node);
        McPASS.fn.byId("mcDivShade").style.height = parseInt(document.body.scrollHeight) + "px";
        McPASS.fn.byId("mcDivShade").style.width = parseInt(document.body.scrollWidth) + "px";
        McPASS.dragWindow.drag();
    }, //end
    //用药指导单
    PGWindow: function () {//用药指导单窗口       
        mcWindowHeight = mcWindowHeight < 400 ? 400 : mcWindowHeight;
        mcWindowHeight = mcWindowHeight > 700 ? 700 : mcWindowHeight;
        mcWindowWidth = mcWindowWidth < 700 ? 700 : mcWindowWidth;
        mcWindowWidth = mcWindowWidth > 1000 ? 1000 : mcWindowWidth;
        var top = (window.screen.availHeight - 200 - mcWindowHeight) / 2;
        var left = (window.screen.availWidth - 200 - mcWindowWidth) / 2;
        top = top < 100 ? 100 : top;
        left = left < 100 ? 100 : left;
        var scrollLeft = document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft;
        var scrollTop = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop;
        this.config.top = top + scrollTop;
        this.config.left = left + scrollLeft;
        //custom position of let,top
        if (MC_Window_Left > 0) {
            this.config.left = MC_Window_Left;
        }
        if (MC_Window_Top > 0) {
            this.config.top = MC_Window_Top;
        }

        //this.config.screen = this.config.screen ? 1 : 0; //是否是审查       
        var mcHeader = ["<table><tr><td class='mcHeader_logo' style='background-image:url(" + mcWebServiceUrl + "/PassImage/passlogo.gif);background-repeat:no-repeat;'></td><td class='mcHeader_Hospital'>", MChospitalName, "</td><td><a class='mcHeader_close' style='background-image:url(" + mcWebServiceUrl + "/PassImage/mc_close_normal.gif);background-repeat:no-repeat;' onmouseover=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/mc_close_hover.gif)';\" onmouseout=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/mc_close_normal.gif)';\" onclick='McPASS.McCloseWindow(", this.config.screen, ");scroll(0,0);'></a></td></tr></table>"];
        var mcBottom = ["<div class='mcDivWindowBottomVersion'><a style='text-decoration:underline;color:Highlight;_cursor:pointer;cursor:hand;cursor:pointer;' onclick='McPASS.DisclaimerWindow();'>免责声明</a>&nbsp;&nbsp;&nbsp;&nbsp;服务程序版本：", MCprojectVersion, " &nbsp;&nbsp;&nbsp;&nbsp; 数据版本：", MCdataVersion, "</div>",
                        "<div class='mcDivWindowBottomLogo' style='background-image:url(" + mcWebServiceUrl + "/PassImage/mcLogo.jpg);background-repeat:no-repeat;'></div>"
        ];
        //[body=window-header-bottom]-->height-50-30=80  bottom=30-top线(1px)=29px
        var mcWindow = [
                        "<div id='mcDivWindow' class='mcDivWindow' style='width:", mcWindowWidth, "px;height=", mcWindowHeight, "px;overflow:hidden;top:", this.config.top, "px;left:", this.config.left, "px; z-index:", mc_screen_win_zIndex, ";' oncontextmenu='return false;' onselectstart='return false;' onselect='return false' oncopy='return false'>",
                           "<div id='mcDivWindowHead' style='width:", mcWindowWidth, "px;background-image:url(" + mcWebServiceUrl + "/PassImage/mcwinheader.gif);background-repeat:repeat-x;height:50px;cursor:move;'>", mcHeader.join(""), "</div>",
                           "<div id='mcDivWindowBody' class='mcDivWindowBody' style='width:", mcWindowWidth, "px;height:", mcWindowHeight - 80, "px;overflow:hidden;'>", this.config.body, "</div>",
                           "<div id='mcDivWindowBottom' class='mcDivWindowBottom' style='width:", mcWindowWidth, "px;'>", mcBottom.join(""), "</div>",
                           "<iframe id='mc_iframe_bg' class='mc_iframe_bg' frameborder='0'></iframe>",
                        "</div>"
        ];

        var mcShade = ["<div id='mcDivShade' class='mcDivShade' style='z-index:", mc_shade_first_zIndex, ";filter:Alpha(opacity=0);'><iframe class='mc_iframe_bg' frameborder='0'></iframe></div>"];
        var node = document.createElement("div");
        node.innerHTML = mcWindow.join("") + mcShade.join("");
        document.body.insertAdjacentElement("afterBegin", node);
        if (McPASS.fn.byId("mcDivShade")) {
            McPASS.fn.byId("mcDivShade").style.height = parseInt(document.body.scrollHeight) + "px";
            McPASS.fn.byId("mcDivShade").style.width = parseInt(document.body.scrollWidth) + "px";
        }
        McPASS.dragWindow.drag();

    }, //end
    //浮动窗口
    msgshow: function () {
        var H = 250;
        var W = 450;
        var array = [
           "<div id='mcDivWindowHead' class='mcMsgWindowHead' style='background-image:url(" + mcWebServiceUrl + "/PassImage/mcheader.gif);background-repeat:repeat-x;'><div class='mcMsgWindowHead_logo' style='background-image:url(" + mcWebServiceUrl + "/PassImage/mcpasslogo.gif);background-repeat:no-repeat;'>&nbsp;</div><a class='mcMsgWindowHead_close' id='mcMsgWindowHead_closer'  style='background-image:url(" + mcWebServiceUrl + "/PassImage/McMsg_close_normal.gif);background-repeat:no-repeat;' onmouseover=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/McMsg_close_hover.gif)';\" onmouseout=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/McMsg_close_normal.gif)';\" onclick='McPASS.MsgWindow.close(0);'></a></div>",
           "<div id='mcDivWindowBody' class='mcDivWindowBody' style='height:", H - 70, "px;width:", W, "px;'><div class='mc_common_loading' style='background-image:url(" + mcWebServiceUrl + "/PassImage/loading.gif);background-repeat:no-repeat;'></div></div>",
           "<div id='mcDivWindowBodyAttr' class='mcDrugAttribeDiv'></div>", //放图标   
           "<div id='mc_resizediv' style='position:absolute;right:1px;bottom:1px;cursor:nw-resize;' title='拖动改变窗体大小'>&nbsp;</div>"
        ];
        var strArray = [
            "<div id='mcDivWindow' oncontextmenu='return false' onselectstart='return false' onselect='return false' oncopy='return false' ",
            "style='-moz-user-select:none;border:solid 1px #c9c9c9;background-color:White;position:absolute;width:", W, "px;height:", H, "px;overflow:hidden;top:",
             this.config.top, "px;left:", this.config.left, "px; z-index:", mc_msg_win_zIndex, ";'>", array.join(''),
             "<iframe class='mc_iframe_bg' frameborder='0'></iframe></div>"
        ];
        var node = document.createElement("div");
        node.innerHTML = strArray.join("");
        document.body.insertAdjacentElement("afterBegin", node);
        McPASS.dragWindow.drag();
    }, //end
    //推荐剂量窗口
    RecomDoseWindow: function () {
        var H = 300;
        var W = 560;
        this.config.left = window.screen.availWidth - 220 - W;
        this.config.top = window.screen.availHeight - 180 - H;
        var array = [
           "<div id='mcDivWindowHead' class='mcMsgWindowHead' style='background-image:url(" + mcWebServiceUrl + "/PassImage/mcheader.gif);background-repeat:repeat-x;'><div id='mcDivscreendTitle' class='mcMsgWindowHead_logo' style='padding-left:80px;color:white;font-size:14px;font-weight:bold;background-image:url(" + mcWebServiceUrl + "/PassImage/mcpasslogo.gif);background-repeat:no-repeat;'></div><a class='mcMsgWindowHead_close' id='mcMsgWindowHead_closer' style='background-image:url(" + mcWebServiceUrl + "/PassImage/McMsg_close_normal.gif);background-repeat:no-repeat;' onmouseover=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/McMsg_close_hover.gif)';\" onmouseout=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/McMsg_close_normal.gif)';\" onclick='McPASS.McCloseWindow(0);'></a></div>",
           "<div id='mcDivWindowBody' class='mcDivWindowBody' style='height:", H, "px;width:", W, "px;'><div class='mc_common_loading' style='background-image:url(" + mcWebServiceUrl + "/PassImage/loading.gif);background-repeat:no-repeat;'></div></div>"
           //"<div id='mc_resizediv' style='position:absolute;right:1px;bottom:1px;cursor:nw-resize;' title='拖动改变窗体大小'>&nbsp;</div>"
        ];
        var strArray = [
            "<div id='mcDivWindow' oncontextmenu='return false' onselectstart='return false' onselect='return false' oncopy='return false' ",
            "style='-moz-user-select:none;border:solid 1px #c9c9c9;background-color:White;position:absolute;width:", W, "px;height:", H, "px;overflow:hidden;top:",
             this.config.top, "px;left:", this.config.left, "px; z-index:", mc_msg_win_zIndex, ";'>", array.join(''),
             "<iframe class='mc_iframe_bg' frameborder='0'></iframe></div>"
        ];
        var node = document.createElement("div");
        node.innerHTML = strArray.join("");
        document.body.insertAdjacentElement("afterBegin", node);
        McPASS.dragWindow.drag();
    }
    //end
};

//隐藏关闭按钮
McPASS.HideCloseButton = function () {
    var elem = McPASS.fn.byId("mcMsgWindowHead_closer");
    if (elem) {
        elem.style.display = "none";
    }
}

//显示关闭按钮
McPASS.ShowCloseButton = function () {
    var elem = McPASS.fn.byId("mcMsgWindowHead_closer");
    if (elem) {
        elem.style.display = "block";
    }
}

//单纯隐藏显示，供其他地方调用
McPASS.__closeWin = function () {
    var winElem = McPASS.fn.byId("mcDivWindow");
    if (winElem && winElem.parentNode) {
        document.body.removeChild(winElem.parentNode);
    }
    var m = McPASS.fn.byId("mcRightMenu");
    if (m) {
        m.style.display = "none";
    }
}


McPASS.McCloseWindow = function (screen) {

    //当为审查窗口时，存在不关窗口回调的时候，关闭窗口，但不调用His回调函数（PR审查的时候用）
    if (screen && McPASS.McUnCloseWindowCallBack) {
        McPASS.__closeWin();
        McPASS.McUnCloseWindowCallBack();
        return;
    }

    //只需要在审查的时候才需要判断 理由 必填的情况
    if (screen) {
        if (mc_cancloseWindow == 0) {//关闭窗口，判断是否填写所有理由
            McPASS.McGetIsFullFillReason(function (isFullFill) {
                if (isFullFill == 0) {
                    var e = window.event || arguments.callee.caller.arguments[0];
                    var tip = "需要填写理由的问题还未填写完，请填写！";
                    McPASS.AlertWindow(e, tip, 1);
                    return;
                }
                else {
                    McPASS.__closeWin();
                    if (McPASS.Screen_callback && McPASS.Screen_callback != "" && typeof McPASS.Screen_callback === 'function') {
                        McPASS.Screen_callback(McPASS.ScreenHighestSlcode, isFullFill);
                    }
                }
            });
        }
        else {//返回修改，关闭窗口，异步调用回调返回状态
            McPASS.__closeWin();
            if (McPASS.Screen_callback && McPASS.Screen_callback != "" && typeof McPASS.Screen_callback === 'function') {
                McPASS.McGetIsFullFillReason(function (isFullFill) {
                    McPASS.Screen_callback(McPASS.ScreenHighestSlcode, isFullFill);
                });
            }
        }
    }
    else {
        //否则直接关掉
        McPASS.__closeWin();
    }
};

//理由
McPASS.AlertWindow = function (e, _tip, t) {
    var tip = [
                "<div style='padding:10px 1px 1px 20px;float:left;'><img src='" + mcWebServiceUrl + "/PassImage/mcWarning.gif'/></div>",
                "<div style='padding:10px 1px 1px 70px;vertical-align:middle;'>", _tip, "</div>",
                "<br/><div style='text-align:center;'><a onclick='McPASS.McCloseWindow(0);'><img src='" + mcWebServiceUrl + "/PassImage/mcOK.gif' onmouseover=\"this.src='" + mcWebServiceUrl + "/PassImage/mcOKon.gif';\" onmouseout=\"this.src='" + mcWebServiceUrl + "/PassImage/mcOK.gif';\" style='_cursor:pointer;cursor:hand;cursor:pointer;' border='0px'/></a></div>"
    ];
    var win = new McPASS.McClsWindow({ top: e.clientY, left: e.clientX });
    win.Alertshow();
    McPASS.fn.byId("mcDivWindowBody").innerHTML = tip.join('');
};


/*免责声明*/
McPASS.DisclaimerWindow = function () {
    var content = [
                "<div style=\"margin:10px;font-family: 微软雅黑,宋体,Arial;\"><p align=\"center\" style=\"font-size:14px; font-weight:bold;color:Highlight;\">免责声明</p>",
                "<p style=\"text-indent:2em;\">1、合理用药监测系统（PASS，Prescription Automatic Screening System）所包含的信息可以协助医生、药师和其它医疗专业人员处理在药物治疗中遇到的问题。但是，PASS仅供作为咨询信息使用，并不能代替上述医疗专业人员在临床工作中作出治疗判断或决定。</p>",
                "<p style=\"text-indent:2em;\">2、PASS所包含的信息来源于临床和基础研究文献，是由美康医药软件研究开发有限公司对文献进行科学评估，经专家学术委员会审定而成，全部工作经过中国国家药典委员会、国家药品评价中心的共同监制。然而，美康医药软件研究开发有限公司以及中国国家药典委员会和国家药品评价中心对PASS中所包含的所有信息可能导致的后果、信息的正确性、准确性和可靠性（无论是明确证明或是暗示），以及无论用于何种目的，均不承担任何法律责任。</p>",
                "<p style=\"text-indent:2em;\">3、美康公司无意向用户、任何企业或个人推荐或诱导使用或研究PASS系统中以各种形式收载的所有药品，也不推荐或支持任何特定的检验、观点或其他信息。</p>",
                "<table style=\"text-indent:20px; font-size:14px; font-family: 微软雅黑,宋体,Arial; \">",
                "<tr><td colspan=\"2\" align=\"left\">四川美康医药软件研究开发有限公司</td></tr>",
                "<tr><td align=\"left\">售后服务热线： 4006-821-210</td><td align=\"left\">传 真：028-85174760</td></tr>",
                "<tr><td align=\"left\">电子信箱：service@medicom.com.cn</td><td align=\"left\">公司网址：www.medicom.com.cn</td></tr>",
                "</table>",
                "</div>"
    ];

    var win = new McPASS.McClsWindow({});
    win.disclaimerWindow();
    McPASS.fn.byId("mcDivWindowBody").innerHTML = content.join('');
    McPASS.fn.byId("mcDivscreendTitle").innerHTML = "";
};

McPASS.dragWindow = {
    drag: function () {
        var win = McPASS.fn.byId("mcDivWindow");
        var dragBar = McPASS.fn.byId("mcDivWindowHead");
        //for dtd       
        var iWidth = document.documentElement.clientWidth ? document.documentElement.clientWidth : document.body.clientWidth;
        var iHeight = document.documentElement.clientHeight ? document.documentElement.clientHeight : document.body.clientHeight;
        var scrollLeft = document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft;
        var scrollTop = document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop;
        var moveX = 0;
        var moveY = 0;
        var moveTop = 0;
        var moveLeft = 0;
        var moveable = false;
        function getEvent() {
            return window.event || arguments.callee.caller.arguments[0];
        };
        dragBar.onmousedown = function () {
            var evt = getEvent();
            moveable = true;
            moveX = evt.clientX;
            moveY = evt.clientY;
            moveTop = parseInt(win.style.top);
            moveLeft = parseInt(win.style.left);
            //if (document.all) {
            if (McPASS.isIE()) {
                this.setCapture();
            }
            document.onmousemove = function () {
                if (moveable) {
                    var evt = getEvent();
                    var x = moveLeft + evt.clientX - moveX;
                    var y = moveTop + evt.clientY - moveY;
                    if (x > 0 && (x < iWidth) && y > 0 && (y < iHeight)) {
                        win.style.left = x + "px";
                        win.style.top = y + "px";
                    }
                }
            };
            document.onmouseup = function () {
                if (moveable) {
                    moveable = false;
                    if (McPASS.isIE()) {
                        this.releaseCapture();
                    }
                }
            };
        };

        var resizeBar = McPASS.fn.byId("mc_resizediv");
        if (resizeBar) {
            resizeBar.onmousedown = function () {
                var evt = getEvent();
                moveable = true;
                moveX = evt.clientX;
                moveY = evt.clientY;
                moveTop = parseInt(win.style.top);
                moveLeft = parseInt(win.style.left);
                if (McPASS.isIE()) {
                    this.setCapture();
                }
                document.onmousemove = function () {
                    if (moveable) {
                        var evt = getEvent();
                        var h = evt.clientY - moveTop + scrollTop;
                        var w = evt.clientX - moveLeft + scrollLeft;
                        h = h < 250 ? 250 : h;
                        h = h > 300 ? 300 : h;
                        w = w < 450 ? 450 : w;
                        w = w > 600 ? 600 : w;
                        win.style.width = w + "px";
                        win.style.height = h + "px";
                        var body = McPASS.fn.byId("mcDivWindowBody");
                        body.style.height = h - 70 + "px";
                        body.style.width = w + "px";
                        if (McPASS.fn.byId("mcDivWindowBodyAttr") && McPASS.fn.byId("mcDivWindowBodyAttr").style.visibility == "hidden") {
                            body.style.height = h - 30 + "px";
                        }
                    }
                };
                document.onmouseup = function () {
                    if (moveable) {
                        moveable = false;
                        if (McPASS.isIE()) {
                            this.releaseCapture();
                        }
                    }
                };
            };
        }
    }
};﻿McPASS.McRefWindow = function () { };
McPASS.McRefWindow.prototype = {
    menuActiveLeft: 0,
    show: function () {
        mcWindowHeight = mcWindowHeight < 400 ? 400 : mcWindowHeight;
        mcWindowHeight = mcWindowHeight > 700 ? 700 : mcWindowHeight;
        mcWindowWidth = mcWindowWidth < 600 ? 600 : mcWindowWidth;
        mcWindowWidth = mcWindowWidth > 1000 ? 1000 : mcWindowWidth;
        var mcBody = [
            "<div class='mcDivWindowBodyLeft' id='mcrefMenuDiv' style='width:", mcWindowWidth, "px;'>", this.loadMenu(), "</div>",
            "<div class='mcDivWindowBodyLeft' id='mcRefMainLeftDivId' style='width:", mcWindowWidth, "px;height:", mcWindowHeight - 110, "px;'><div class='mcloading'></div></div>" //800要去处边线   
        //bodycontent=mcWindowHeight - 80--->body=bodycontent-menu(30)
            ];
        var win = new McPASS.McClsWindow({ shade: true, z: mc_ref_win_zIndex, body: mcBody.join(""), zShade: mc_shade_second_zIndex, shadeOpacity: 10,screen:0 });
        win.show();
        McPASS.Mcscroll(this.menuActiveLeft); //set the menu in visible area         
    },
    loadMenu: function () {
        var type = MC_global_queryDrug.ReferenceType;
        var len = MCrefModuleArray.length;
        var mcshowItems = 0;
        for (var j = 0; j < len; j++) {
            var isshow = MCrefModuleArray[j].IsShowMenu;
            var state = McrefTipArray[j];
            /***********************************************/
            if (j == 0) {//自定义说明书的情况单独处理
                if (McrefTipArray[1] == 1) {
                    isshow = 1;
                    state = 1;
                }
            } 
            /***********************************************/
            if (isshow > 0 && state > 0) {
                mcshowItems++;
            }
        }
        var html = [];
        html.push("<table style='background-color:white;width:100%;height:30px;border-bottom:1px solid #D4D4D4;' cellpadding='0' cellspacing='0'><tr>");       
        //如果几个选项的宽度和加起来不足矣当前界面的宽度的话，则不要左右点的箭头       
        var isshowLeftright = true;
        if (mcshowItems * 90 < mcWindowWidth) {
            isshowLeftright = false;
        }
        if (isshowLeftright) {
            html.push("<td style='padding-left:5px;'><img onmousedown=\"McPASS.Mcscroll(-100);\" onmouseover=\"McPASS.Mcscroll(-50);\" style=\"_cursor:pointer;cursor:hand;cursor:pointer;\" onmouseout=\"McPASS.Mcscroll(0);this.src='" + mcWebServiceUrl + "/PassImage/mc_left.jpg';\" src=\"" + mcWebServiceUrl + "/PassImage/mc_left.jpg\"/></td>");
        }
        html.push("<td><div id='idScroller' style='width:", mcWindowWidth - 50, "px;overflow:hidden;'>");
        html.push("<table cellpadding='0' cellspacing='0' style='width:" + mcshowItems * 90 + "px;'><tr style='background-color:white;font-size:14px;'>");
        var items = 1; //已经加了多少个items
        for (var i = 0; i < len; i++) {          
            var state = McrefTipArray[i]; //只要不是0就都显示，有0，1，2，3共四种情况 自定义说明书情况特殊 要单独处理
            var isshow = MCrefModuleArray[i].IsShowMenu;
            /***********************************************/
            if (i == 0) {//自定义说明书的情况单独处理
                if (McrefTipArray[1] == 1) {
                    isshow = 1;
                    state = 1;                    
                }
            }
            /***********************************************/
            if (isshow > 0 && state > 0) {
                var id = MCrefModuleArray[i].ModuleID;
                var name = MCrefModuleArray[i].ModuleName;
                var color = "black";
                var className = "";
                var classNameImg = "";
                if (id == type) {
                    className = "mcMenu";
                    classNameImg = "background-image:url(" + mcWebServiceUrl + "/PassImage/mcrefsplit.jpg);";
                    color = "white";
                    this.menuActiveLeft = (items * 90) > mcWindowWidth ? (items * 90 - mcWindowWidth) : 0; //set the menu in visible area  
                }             
                html.push("<td id='" + id + "' class='" + className + "' style='" + classNameImg + "width:90px;text-align:center;'><a style='color:" + color + ";_cursor:pointer;cursor:hand;cursor:pointer;' hidefocus onclick=\"McPASS.McMenuclass(" + id + ");McPASS.Alone = 0;McPASS.McFuncRefChange(" + MCrefModuleArray[i].ModuleID + ");\">" + name + "</a></td>");
                if (items < mcshowItems) {
                    html.push("<td><img src='" + mcWebServiceUrl + "/PassImage/mc_split.jpg' border='0' align='absmiddle'/></td>");
                }
                items++;
            }
        }
        html.push("</tr></table>");
        html.push("</div></td>");
        if (isshowLeftright) {
            html.push("<td style='width:20px;'><img onmousedown=\"McPASS.Mcscroll(100);\" onmouseover=\"McPASS.Mcscroll(50);\" style='_cursor:pointer;cursor:hand;cursor:pointer;' onmouseout=\"McPASS.Mcscroll(0);this.src='" + mcWebServiceUrl + "/PassImage/mc_right.jpg';\" src='" + mcWebServiceUrl + "/PassImage/mc_right.jpg'/></td>");
        }
        html.push("</tr></table>");
        return html.join('');
    }
};
McPASS.McMenuclass = function (id) {
    var obj = document.getElementById(id);
    var parent = obj.parentNode;
    var child = parent.childNodes;
    for (var i = 0; i < child.length; i++) {
        child[i].className = "";
        child[i].style.backgroundImage = "";
        child[i].childNodes[0].style.color = "black";
    }
    obj.className = "mcMenu";
    obj.style.backgroundImage = "url(" + mcWebServiceUrl + "/PassImage/mcrefsplit.jpg)";
    obj.childNodes[0].style.color = "white";
};
McPASS.Mcscroll = function (n) {
    Mcscroll_n = n;
    if (Mcscroll_n == 0) { return; }
    if (McPASS.fn.byId("idScroller")) {
        McPASS.fn.byId("idScroller").scrollLeft += Mcscroll_n;
        setTimeout("McPASS.Mcscroll(Mcscroll_n)", 10);
    }
};﻿McPASS.McScreenWindow = function () { };
McPASS.McScreenWindow.prototype = {
    show: function () {
        mcWindowHeight = mcWindowHeight < 400 ? 400 : mcWindowHeight;
        mcWindowHeight = mcWindowHeight > 700 ? 700 : mcWindowHeight;
        mcWindowWidth = mcWindowWidth < 600 ? 600 : mcWindowWidth;
        mcWindowWidth = mcWindowWidth > 1000 ? 1000 : mcWindowWidth;
        var mcLeft = [
            "<div id='mcDivScreenLeft1ID'><a id='mcScreenPuckerAll' style='_cursor:pointer;cursor:hand;cursor:pointer;background-image:url(" + mcWebServiceUrl + "/PassImage/mc_Pucker_normal.gif);background-repeat:no-repeat;' onmouseover=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/mc_Pucker_hover.gif)';\" onmouseout=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/mc_Pucker_normal.gif)';\" onclick='McPASS.PuckerAll();' hidefocus='true' class='mcScreen_Pucker'></a></div>",
            "<div id='mcDivScreenLeft2ID' style='height:", mcWindowHeight - 115, "px;width:", mcWindowWidth - 0, "px;'></div>"
        ];
        var mcBody = [
            "<div id='mcDivScreenWindowBodyLeft' class='mcDivWindowBodyLeft' style='width:", mcWindowWidth - 0, "px;'>",//11               
                mcLeft.join(""),//上面必须要加height：10px，也必须放在mcLeft这个div的前面，ie8，chrome测试通过
            "</div>"];
        var win = new McPASS.McClsWindow({ shade: true, z: mc_screen_win_zIndex, body: mcBody.join(""), zShade: mc_shade_first_zIndex, shadeOpacity: 10, screen: 1 });
        win.show();
    }
    //end   
};
//---------------------------------------------------------------------------------------------------------------
var mcScreenPuckerAll = false;
McPASS.PuckerAll = function () {
    var count = MC_global_Screen_Count;
    if (count <= 0) {
        return;
    }
    var obj = McPASS.fn.byId("mcScreenPuckerAll");
    if (this.mcScreenPuckerAll) {
        //obj.title = "折叠所有审查结果";       
        obj.className = "mcScreen_Pucker";        
        obj.style.backgroundImage = "url(" + mcWebServiceUrl + "/PassImage/mc_Pucker_normal.gif)";
        obj.onmouseover = function () {
            obj.style.backgroundImage = "url(" + mcWebServiceUrl + "/PassImage/mc_Pucker_hover.gif)";
        };
        obj.onmouseout = function () {
            obj.style.backgroundImage = "url(" + mcWebServiceUrl + "/PassImage/mc_Pucker_normal.gif)";
        };

        this.mcScreenPuckerAll = false;
        for (var i = 1; i <= count; i++) {
            var img = McPASS.fn.byId("mcBtnShowHideDetail_" + i);
            var div = McPASS.fn.byId("mcDivScreenDrugDetail_" + i).style;
            div.display = "";
            div.visibility = "visible";
            img.src = mcWebServiceUrl + "/PassImage/mcunexpand.gif";
            img.title = "折叠";
        }
    }
    else {
        //obj.title = "展开所有审查结果";
        obj.className = "mcScreen_Expand";
        obj.style.backgroundImage = "url(" + mcWebServiceUrl + "/PassImage/mc_Expand_normal.gif)";
        obj.onmouseover = function () {
            obj.style.backgroundImage = "url(" + mcWebServiceUrl + "/PassImage/mc_Expand_hover.gif)";
        };
        obj.onmouseout = function () {
            obj.style.backgroundImage = "url(" + mcWebServiceUrl + "/PassImage/mc_Expand_normal.gif)";
        };
       
        this.mcScreenPuckerAll = true;
        for (var i = 1; i <= count; i++) {
            var img = McPASS.fn.byId("mcBtnShowHideDetail_" + i);
            var div = McPASS.fn.byId("mcDivScreenDrugDetail_" + i).style;
            div.display = "none";
            div.visibility = "hidden";
            img.src = mcWebServiceUrl + "/PassImage/mcexpand.gif";
            img.title = "展开";
        }
    }
};﻿McPASS.McRightMenu = function (isright) { 
    try {
        if (mc_pass_init_success != 1) {
            return;
        }
        document.oncontextmenu = function () {//屏蔽右键
            return false;
        };
        var event = window.event || arguments.callee.caller.arguments[0]; //APP上获取不到event
        if (isright) {
            if (event.button != 2) {//right click
                return false;
            }
        }
        //如果存在，先干掉 
        var m = McPASS.fn.byId("mcRightMenu");
        if (m) {
            m.style.display = "none";
        }
        //先创建一个空的div并设置好显示位置，其中li内容再后面再加载 其实这个地方可以设计一个最小高度，但是也不好看 min-height:30px
        var strMenu = [
            "<div id='mcRightMenu' style='display:none;z-index:100008;position:absolute;width:100px;' oncontextmenu='return false' onselect='return false' oncopy='return false'>",
            "<ul id='mcRightMenuUL'></ul></div>"
            ];
        var node = document.createElement("div");
        node.innerHTML = strMenu.join("");
        document.body.insertAdjacentElement("afterBegin", node);

        /*
        var docElement = document.documentElement, docBody = document.body;      
        var scrollLeft = docElement.scrollLeft ? docElement.scrollLeft : docBody.scrollLeft;
        var scrollTop = docElement.scrollTop ? docElement.scrollTop : docBody.scrollTop;
        if (arguments.length == 2)//传了坐标的参数过来 McRightMenu(200,200)
        {
        x = arguments[0] + scrollLeft;
        y = arguments[1] + scrollTop;
        }
        */

        var doc = [document.documentElement.offsetWidth, document.documentElement.offsetHeight];
        var _doc = [document.documentElement.scrollTop, document.documentElement.scrollLeft];
        var menu = document.getElementById("mcRightMenu");
        menu.style.top = event.clientY + _doc[0] + "px";
        menu.style.left = event.clientX + _doc[1] + "px";
        var maxWidth = maxHeight = 0;
        //最大显示范围
        maxWidth = doc[0] - menu.offsetWidth + _doc[1];
        maxHeight = doc[1] - menu.offsetHeight + _doc[0];
        //防止菜单溢出
        menu.offsetTop > maxHeight && (menu.style.top = maxHeight + "px");
        menu.offsetLeft > maxWidth && (menu.style.left = maxWidth + "px");

        //双击关闭
        menu.ondblclick = function () {           
            menu.style.display = "none";
        };
        //点击其他地方也可关闭菜单
        document.onclick = function () {           
            menu.style.display = "none";
        };
        //IE7下document.onclick没反应，用document.body.onclick
        document.body.onclick = function () {           
            menu.style.display = "none";
        };

        mcLastReferenceCode = ""; //清掉上次右键菜单查询的referencecode这个变量，否则不能重复查
        McPASS.McGetModuleName(McPASS.RightMenuTip);
    }
    catch (e) {
    }
};

McPASS.McMenuActive = function () {
    var mc_menu = document.getElementById("mcRightMenu");
    var mc_ul = mc_menu.getElementsByTagName("ul");
    var mc_li = mc_menu.getElementsByTagName("li");
    mc_menu.style.display = "block";
    set_li_width(mc_ul[0]);
    for (i = 0; i < mc_li.length; i++) {
        mc_li[i].onmouseover = function () {
            this.className += " active";
        };
        mc_li[i].onmouseout = function () {
            this.className = this.className.replace(/s?active/, "");
        };
    };
    function set_li_width(obj) { //取li中最大的宽度, 并赋给同级所有li	
        var maxWidth =  98;
        for (i = 0; i < obj.children.length; i++) {
            var li = obj.children[i];
            var iWidth = li.clientWidth - parseInt(li.currentStyle ? li.currentStyle["paddingLeft"] : getComputedStyle(li, null)["paddingLeft"]) * 2;
            if (iWidth > maxWidth) {
                maxWidth = iWidth;
            }
        }
    };
};

McPASS.McdoMenu = function () {
    try {
        var result = "";
        var len = MCrefModuleArray.length;
        for (var j = 0; j < len; j++) {
            var state = McrefTipArray[j]; //只要不是0就都显示，有0，1，2，3共四种情况
            var obj = MCrefModuleArray[j];
            var isshow = obj.IsShowMenu;
            var name = obj.ModuleName;
            if (isshow > 0) {
                if (state > 0) {
                    result += "<li class='ok'><a style='color: #646464'>" + name + "</a></li>";
                }
                else if (j == 0) {//自定义说明书的情况单独处理
                    if (McrefTipArray[1] == 1) {
                        result += "<li class='ok'><a style='color: #646464'>" + name + "</a></li>";
                    }
                    else {
                        result += "<li><a style='color:#bbb'>" + name + "</a></li>";
                    }
                }
                else {
                    result += "<li><a style='color:#bbb'>" + name + "</a></li>";
                }
            }
        }
        document.getElementById("mcRightMenuUL").innerHTML = result;
        McPASS.McMenuActive(); //设置鼠标移动上去的样式

        //***********************************************************
        //通过id("mcRightMenuUL")来获得各菜单项,并为各菜单项增加响应事件
        var items = document.getElementById("mcRightMenuUL").childNodes;
        for (var i = 0; i < items.length; i++) {//通过mcRightMenuUL的childNodes的index（从0开始）来识别是哪个功能
            var mcfun = function (type) {
                return function () {
                    McPASS.McMenuRef(ToType(type));
                    document.getElementById("mcRightMenu").style.display = "none"; //hide menu                     
                };
            };
            //li属性只有IE支持disabled属性，所以只有通过class来识别
            if (items[i].className == "ok") {
                if (window.addEventListener) {//FF
                    items.item(i).firstChild.addEventListener("click", mcfun(i), false);
                }
                else if (window.attachEvent) {//IE
                    items.item(i).firstChild.attachEvent("onclick", mcfun(i));
                }
            }
        } //

        var ToType = function (n) {
            var r = 0;
            var j = 0;
            var len = MCrefModuleArray.length;
            for (var L = 0; L < len; L++) {
                var obj = MCrefModuleArray[L];
                var show = obj.IsShowMenu;
                var id = obj.ModuleID;
                if (show > 0) {
                    if (n == j) {
                        r = id; break;
                    }
                    j++;
                }
            }
            return r;
        }; //
    }
    catch (e) {
        //alert("error from McdoMenu:" + e.ToString());
    }
};

McPASS.McMenuRef = function (type) {
    MC_global_queryDrug.ReferenceType = type;
    var obj = {
        PassClient: MCPASSclient,
        ReferenceParam: MC_global_queryDrug
    };
    MC_global_ref_HISdata = McPASS.toMcJSONString(obj);
    var win = new McPASS.McRefWindow();
    win.show();
    McPASS.McFuncRefChange(type);
};

//get the ReferenceTip : "20220 20300 02000 00220 000" --0（不可用）；1（只有自定义数据）；2（只有PASS数据）；3（既有自定义又有PASS数据）
McPASS.RightMenuTip = function () {
    try {
        MC_global_queryDrug.ReferenceType = 0;
        var _obj = {
            PassClient: MCPASSclient,
            ReferenceParam: MC_global_queryDrug
        };
        MC_global_ref_HISdata = McPASS.toMcJSONString(_obj);
        var obj = {
            PASSFuncName: "Mc_DoQuery",
            PASSParam: MC_global_ref_HISdata
        };
        var data = McPASS.toMcJSONString(obj);
        McAjax.send(data);
        McAjax.onmessage = function (result) {
            if (result.indexOf("[ERROR]") < 0) {
                McPASS.Debug(IsDebugResult, result, DebugShowDiv_Result); //debug
                var res = McPASS.fn.EvalJson(result);
                if (res) {
                    var tip = res.ReferenceTip;
                    if (tip) {
                        var len = tip.length;
                        McrefTipArray = new Array();
                        for (var i = 0; i < len; i++) {
                            McrefTipArray[McrefTipArray.length] = tip.substr(i, 1);
                        }
                        McPASS.McdoMenu();
                    }
                }
            } 
        };
    }
    catch (e) {
    }
};
//==================================================================================================================================
//单独查询
McPASS.AloneQuery = function (type) {   
    if (mc_pass_init_success != 1) {
        return;
    }
    McPASS.AloneQueryType = type;
    McPASS.Alone = 1;// 1 这样表明是单独查询的   
    McPASS.McGetModuleName(McPASS.McAloneQueryTip);
};
McPASS.McAloneQuery = function (type) {
    MC_global_queryDrug.ReferenceType = type; 
    var obj = {
        PassClient: MCPASSclient,
        ReferenceParam: MC_global_queryDrug
    };
    MC_global_ref_HISdata = McPASS.toMcJSONString(obj);
    var win = new McPASS.McRefWindow();
    win.show();
    McPASS.McFuncRefChange(type);
};
McPASS.McAloneQueryTip = function () {
    try {
        MC_global_queryDrug.ReferenceType = 0;
        var _obj = {
            PassClient: MCPASSclient,
            ReferenceParam: MC_global_queryDrug
        };
        MC_global_ref_HISdata = McPASS.toMcJSONString(_obj);
        var obj = {
            PASSFuncName: "Mc_DoQuery",
            PASSParam: MC_global_ref_HISdata
        };
        var data = McPASS.toMcJSONString(obj);
        McAjax.send(data);
        McAjax.onmessage = function (result) {
            if (result.indexOf("[ERROR]") < 0) {
                McPASS.Debug(IsDebugResult, result, DebugShowDiv_Result); //debug
                var tip = McPASS.fn.EvalJson(result).ReferenceTip;
                var len = tip.length;
                McrefTipArray = new Array();
                for (var i = 0; i < len; i++) {
                    McrefTipArray[McrefTipArray.length] = tip.substr(i, 1);
                }               
                McPASS.McAloneQuery(McPASS.AloneQueryType);
            }
        };
    }
    catch (e) {
        //alert("error from RightMenuTip:" + e.ToString());
    }
};
//查询为了跟cs保持一致特增加的函数
function MDC_DoRefDrug(ptype, x, y) {
    var pass = McPASS.Mc_PassSwitch();
    var pr = McPASS.Mc_PrSwitch();
    if (pass <= 0 && pr <= 0) {
        if (mc_pass_init_success > 0) mc_pass_init_success = -10;
    }
    if (mc_pass_init_success != 1) {
        return;
    }
    var type = ((ptype == undefined || ptype == null || ptype < 0) ? 11 : ptype);
    if (type != 51) {
        McPASS.AloneQuery(type);
    }
    else {
        if (arguments.length == 3)//传了坐标的参数过来
        {
            McPASS.MsgWindow.show(x, y);
        }
        else {
            McPASS.MsgWindow.show();
        }
    }
};﻿McPASS.MsgWindow = {
    popMsg: null, //define the pop Message window
    show: function (x, y) {
        //单药审查，跟浮动窗口要保证唯一只有一个
        if (McPASS.fn.byId("mcDivWindowBody")) {
            McPASS.McCloseWindow();
        }

        //可以不用，再确定，因为下面还是用了this.popMsg 可以直接用个var popmsg代替
        if (this.popMsg) {
            this.close();
        }
       
        if (arguments.length == 2)//传了坐标的参数过来
        {
            this.popMsg = new McPASS.McClsWindow({ top: y, left: x });
        }
        else {
            var e = window.event || arguments.callee.caller.arguments[0];
            this.popMsg = new McPASS.McClsWindow({ top: e.clientY, left: e.clientX });
        }

        //this.popMsg.msgshow();//show the window after get result
        McPASS.McGetModuleName(McPASS.McDoQueryMsg);
    },     
    close: function () {
        if (this.popMsg == null) { return; }
        var pop = McPASS.fn.byId("mcDivWindow");
        if (pop) {
            var node = pop.parentNode;
            var parent = node.parentNode;
            parent.removeChild(node);
        }
        this.popMsg = null;
    }
};
McPASS.McDoQueryMsg = function () {
    MC_global_queryDrug.ReferenceType = 51;    
    McPASS.MCPASSclient_Verify();//Verify
    McPASS.MC_global_queryDrug_Verify();//Verify
    var _obj = {
        PassClient: MCPASSclient,
        ReferenceParam: MC_global_queryDrug
    };
    MC_global_ref_HISdata = McPASS.toMcJSONString(_obj);
    McPASS.Debug(IsDebugDrugInfo, MC_global_ref_HISdata, DebugShowDiv_Drug); //debug
    var obj = {
        PASSFuncName: "Mc_DoQuery",
        PASSParam: MC_global_ref_HISdata
    };
    var data = McPASS.toMcJSONString(obj);
    McAjax.send(data);
    McAjax.onmessage = function (result) {
        if (result.indexOf("[ERROR]") < 0) {
            MC_global_ref_PASSdata = result;
            McPASS.Debug(IsDebugResult, MC_global_ref_PASSdata, DebugShowDiv_Result); //debug
            McPASS.McMsgQueryResult();
        }
    };
};
McPASS.McMsgQueryResult = function () {
    try { 
        var res = McPASS.fn.EvalJson(MC_global_ref_PASSdata);
        var delimiter = res.Delimiter;
        var sub = res.SubDelimiter;
        var obj = res.ReferenceResults;
        if (obj.length > 0) {
            var html = [];
            var Attr = []; //图标
            var node = obj[0];
            if (node.BriefItems.length > 0) {
                html.push([McPASS.McFormatMsgQuery(node.BriefItems, delimiter, sub)].join(''));
            }
            var strDrugAttr = "";
            var detailItems = node.DetailItems;
            if (detailItems.length > 0) {
                var mcDetil = detailItems.split(delimiter);
                for (var j = 0; j < mcDetil.length; j++) {
                    var n = mcDetil[j].split(sub);
                    var key = n[0];
                    var val = n[1];
                    if (key.trim().length > 0) {//一会要去空格一会不去空格，搞不懂      key.trim().length     key.replace(/(^\s*)|(\s*$)/g, "").length                 
                        strDrugAttr += "<span class=\"mcDrugAttribute\"><img src='" + mcWebServiceUrl + "/PassImage/mc" + key + ".gif' title='" + val + "' border='0' align='absmiddle' /></span>";
                    }
                }
            }
            if (strDrugAttr.length > 0) {
                if (html.length > 0) {
                    Attr.push(strDrugAttr);
                }
                else {
                    html.push(strDrugAttr); //如果上面没内容的话，提示信息得移上去
                }
            }
            this.MsgWindow.popMsg.msgshow(); //show the window   
            McPASS.fn.byId("mcDivWindowBody").innerHTML = html.join('');
            McPASS.fn.byId("mcDivWindowBodyAttr").innerHTML = Attr.join('');
            if (Attr.length == 0) {
                McPASS.fn.byId("mcDivWindowBody").style.height = (30 + parseInt(McPASS.fn.byId("mcDivWindowBody").style.height)) + "px";
                McPASS.fn.byId("mcDivWindowBodyAttr").style.visibility = "hidden";
            }
        }
    }
    catch (e) { }
};
McPASS.McFormatMsgQuery = function (parag, delimiter, sub) {
    if (parag.length <= 0) {
        return "";
    }
    var ret = [];
    var paragraph = parag.split(delimiter);
    for (var p = 0; p < paragraph.length; p++) {
        var item = paragraph[p].split(sub);
        //var array = item[1].split("\r\n");
        var array = item[1];
        if (array != "") {
            var btn = "mcBtnMsg" + p;
            var div = "mcDivMsg" + p;
            ret.push("<table class='mcMainText'><tr>");
            ret.push("<td class='mcBriefBigTitle'><img src='" + mcWebServiceUrl + "/PassImage/mclistbreif.gif' align='absmiddle' />&nbsp;" + item[0] + "</td>");
            ret.push("<td style='text-align:right;padding-right:15px;'><img id='", btn, "' src='" + mcWebServiceUrl + "/PassImage/mcunexpand.gif' style='_cursor:pointer;cursor:hand;cursor:pointer;' onclick=\"McPASS.McFoldItemDetailDiv('", btn, "','", div, "')\" title='折叠' boder='0' align='absmiddle'/></td>");
            ret.push("</tr></table>");
            ret.push("<div id='", div, "' class='mcMsgPanel' style='visibility:visible;'>");
            ret.push(array + "</div>");

            //            for (var i = 0; i < array.length; i++) {
            //                var obj = array[i];
            //                if (obj != "") {
            //                                        obj = obj.replace(/●/g, "<font class='mcHotPoint'>&#8226;</font>");
            //                                        var _class = "mcBrief";
            //                                        if (obj.indexOf("【") >= 0) {
            //                                            _class = "mcBriefSmallTitle";
            //                                        }
            //                                        ret.push("<p class='" + _class + "'>" + obj + "</p>");

            //                    
            //                }
            //            }
            //            ret.push("</div>");
        }
    }
    return "<div class='mcMsgContent'>" + ret.join("") + "</div>";
};﻿/*****************************check sync**********************************************************/
McPASS.McGetModuleNameSync = function () {
    try {
        if (MCrefModuleArray.length > 0 || MCrefModuleArray.length > 0) {
            return;
        }
        var _obj = {
            PassClient: MCPASSclient,
            ModuleParam: MCModuleParam
        };
        var check = new MC_client_request();
        check.ClearParam();
        check.AddParam("psJSONStr", McPASS.toMcJSONString(_obj));
        McPASS.McGetModuleNameSyncCallBack(check.post("Mc_DoModule", null, McPASS.McGetModuleNameSyncCallBack)); //同步模式
    }
    catch (e) {
    }
};

//同McGetModuleName
McPASS.McGetModuleNameSyncCallBack = function (result) {
    if (result.value.indexOf("[ERROR]") < 0) {
        MCrefModuleArray = [];
        MCscreenModuleArray = [];
        var res = McPASS.fn.EvalJson(result.value);
        if (res) {
            MCscreenModuleArray = res.ScreenModuleList;
            MCdataVersion = res.DataVersion;
            MCprojectVersion = res.ProjectVersion;
            MChospitalName = res.RHCName;
            MCrefModuleArray = res.ReferenceModuleList;
            MCcanGetScreenDtlRes = res.CanGetScreenDtlRes;//能否获取审查详细信息json串

            /*********************说明书+自定义说明书 mcenun.js rightmenu.js**************************/
            var len = MCrefModuleArray.length;
            var blsystem = 0;
            var bluser = 0;
            var index = 0;
            for (var j = 0; j < len; j++) {
                var obj = MCrefModuleArray[j];
                if (obj.ModuleID == 11) {
                    blsystem = 1;
                }
                if (obj.ModuleID == 14) {
                    bluser = 1;
                    index = j;
                }
            }
            //这种情况按系统的进行查询，至于ModuleName只能写死成这样
            if (!blsystem && bluser) {
                MCrefModuleArray[index].ModuleID = 11;
                MCrefModuleArray[index].ModuleName = "药品说明书";
                MCrefModuleArray[index].IsShowMenu = 1;
            }
        }
    }
};

/*
同步审查，支持IE9+,chrome（低版本IE浏览器若要支持，则需要设置允许跨域）
_isShowWindow 是否显示审查界面(0不显示，1显示)
*/

McPASS.McPASScheckSync = function (_isShowWindow) {
    //return if screen now
    if (mc_is_screen_now == 1) {
        return;
    }
    // add by liuchunmei 2017-04-11
    var pass = McPASS.Mc_PassSwitch();
    var pr = McPASS.Mc_PrSwitch();
    if (pass <= 0 && pr <= 0) {
        if (McPASS.ScreenHighestSlcode != -10) McPASS.ScreenHighestSlcode = -10;
        if (mc_pass_init_success > 0) mc_pass_init_success = -10;
    }
    if (mc_pass_init_success <= 0) return;
    // end add
    mc_is_screen_now = 1; //screen now      
    McPASS.isShowWindow = 1; //whether show screen window
    if (_isShowWindow != undefined) {
        McPASS.isShowWindow = _isShowWindow;
    }
    McPASS.McGetModuleNameSync();//如果只用PR,也得获取这个，因为组织审查界面需要这个，如果只要PR系统审查结果不要PASS界面可以不掉
    MC_global_Screen_Count = 0;

    McPASS.MCPASSclient_Verify();//Verify
    McPASS.MCpatientInfo_Verify();//Verify
    McPASS.McDrugsArray_Verify();//Verify
    McPASS.McMedCondArray_Verify();//Verify
    McPASS.McAllergenArray_Verify();//Verify
    McPASS.McOperationArray_Verify();//Verify
    McPASS.McLabArray_Verify();//Verify
    McPASS.McExamArray_Verify();//Verify

    /*********************************************/
    //新接口，组合方式
    var jsonInfos = McPASS.McScreenJsonAddAPI();
    /*********************************************/
    var o = {
        PassClient: MCPASSclient,
        Patient: MCpatientInfo,
        ScreenAllergenList: { ScreenAllergens: McAllergenArray },
        ScreenMedCondList: { ScreenMedConds: McMedCondArray },
        ScreenOperationList: { ScreenOperations: McOperationArray },
        ScreenDrugList: { ScreenDrugs: McDrugsArray },
        InputJsonInfoList: { InputJsonInfos: jsonInfos }
    };
    MC_global_Screen_HISdata = McPASS.toMcJSONString(o);
    // 重置警示灯
    McPASS.reSetDrugWarnLight(o);

    McPASS.Debug(IsDebugPatient, "病人信息：" + McPASS.toMcJSONString(MCpatientInfo), DebugShowDiv_Patient); //debug
    McPASS.Debug(IsDebugRecipes, "处方：" + McPASS.toMcJSONString(McDrugsArray), DebugShowDiv_Recipes); //debug 
    McPASS.Debug(IsDebugAllergens, "过敏史：" + McPASS.toMcJSONString(McAllergenArray), DebugShowDiv_Allergens); //debug
    McPASS.Debug(IsDebugMedConds, "诊断：" + McPASS.toMcJSONString(McMedCondArray), DebugShowDiv_MedConds); //debug 
    McPASS.Debug(IsDebugOperation, "手术：" + McPASS.toMcJSONString(McOperationArray), DebugShowDiv_Operation); //debug 

    //特殊字符处理
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\%/g, "%25");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\+/g, "%2b");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\s/g, "%20");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/&amp;/g, '%26');
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/&/g, "%26");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/@/g, "%40");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\//g, "%2F");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\?/g, "%3F");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\#/g, "%23");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\$/g, "%24");

    var check = new MC_client_request(1);

    //only pass default
    mc_is_pr_request = 0;//reset
    var ws_function = "Mc_DoScreen";
    //pass,pr
    var pass = McPASS.Mc_PassSwitch();
    var pr = McPASS.Mc_PrSwitch();
    if (pass == 1 && pr == 1) {
        ws_function = "Mc_PASSPR";
        check = new MC_client_request(2);
        mc_is_pr_request = 1;//pr ws
    }
    else if (pass == 0 && pr == 1) {
        ws_function = "Mc_PR";
        check = new MC_client_request(2);
        mc_is_pr_request = 1;//pr ws
    }
    check.ClearParam();
    check.AddParam("psJSONStr", MC_global_Screen_HISdata);
    McPASSCallBackSync(check.post(ws_function, null, McPASSCallBackSync)); //同步模式 
};

function McPASSCallBackSync(callBackResult) {
    if (!callBackResult.error) {
        var result = callBackResult.value;
        if (result == undefined || result == null) return;
        mc_is_screen_now = 0; //screen and analyze screen result done 
        var pass = McPASS.Mc_PassSwitch();
        var pr = McPASS.Mc_PrSwitch();
        MC_global_Screen_PASSdata = null;
        MC_global_Screen_PRstatus = null;
        if (pass == 1 && pr == 0) { // 只有PASS
            if (result.indexOf("[ERROR]") >= 0) {
                mc_product_switch.PASS = 0;
                mc_pass_init_success = -10;
                McPASS.ScreenHighestSlcode = -10;//return -10 when passcore is abnormal 
                if (result == "[ERROR]:timeout") {
                    mc_pass_init_success = -99;
                    McPASS.ScreenHighestSlcode = -99;//return -99 when timeout 
                }
            } else {
                MC_global_Screen_PASSdata = McPASS.fn.EvalJson(result);
            }
        } else if (pass == 1 && pr == 1) {
            var _result = McPASS.fn.EvalJson(result);
            var passstr = "";
            if (_result == undefined || _result == null ||
                _result.PASS == undefined || _result.PASS == null ||
                ((passstr = McPASS.toMcJSONString(_result.PASS)).indexOf("[ERROR]") >= 0)) {
                mc_product_switch.PASS = 0;
                McPASS.ScreenHighestSlcode = -10;//return -10 when passcore is abnormal  
                if (passstr == "[ERROR]:timeout") {
                    McPASS.ScreenHighestSlcode = -99;//return -99 when timeout 
                }
            } else {
                MC_global_Screen_PASSdata = _result.PASS;
            }
            if (_result == undefined || _result == null ||
                _result.PR == undefined || _result.PR == null ||
                McPASS.toMcJSONString(_result.PR).indexOf("[ERROR]") >= 0) {
                mc_product_switch.PR = 0;
            } else {
                MC_global_Screen_PRstatus = _result.PR;
            }
        }
        else if (pass == 0 && pr == 1) {
            if (result.indexOf("[ERROR]") >= 0) {
                mc_product_switch.PR = 0;
                mc_pass_init_success = -10;
                if (result == "[ERROR]:timeout") {
                    mc_pass_init_success = -99;
                }
            } else {
                MC_global_Screen_PRstatus = McPASS.fn.EvalJson(result);
            }
        }
        if (MC_global_Screen_PASSdata != null) {
            McPASS.ScreenHighestSlcode = MC_global_Screen_PASSdata.HighestSlcode; //审查结果最严重级别值 
            if (McPASS.ScreenHighestSlcode > 0) { //审查的时候如果结果小于0则不处理，不返灯
                McPASS.McAnalyzeScreenResult(1); //Analyze the Screen Result 
            }
        }
        McDebug_t2 = McGetCurrentTime();
    }
};
/******************************************************************************************************/

//新接口，组合方式,以后都加这来
McPASS.McScreenJsonAddAPI = function () {
    /*********************************************/
    //新接口，组合方式
    var jsonInfos = [];
    //define jsontype for other API after 4.1.0
    jsonInfos.push({
        type: "jsontype",
        screentype: "1"
    });
    jsonInfos.push({
        type: "prtasktype",
        urgent: MCpatientInfo.Urgent
    });
    for (var p=0; p<McDrugsArray.length; p++) {
        var drug = McDrugsArray[p];
        // alter by liuchunmei 2016-12-23 添加处理历史处方
        if (drug == undefined || drug == null) continue;
        if (drug.IsOtherRecip && drug.IsOtherRecip > 0) {
            //1、将其它处方的药品存入jsonInfos
            jsonInfos.push({
                type: "otherrecipinfo",
                index: drug.Index,
                recipno: drug.RecipNo,
                drugsource: drug.DrugSource,
                druguniquecode: drug.DrugUniqueCode,
                drugname: drug.DrugName,
                doseunit: drug.DoseUnit,
                routesource: drug.RouteSource,
                routecode: drug.RouteCode,
                routename: drug.RouteName
            });
            //2、从药品数组中删除该其它处方的药品
            McDrugsArray.splice(p, 1);
            p--;
        } else {
            jsonInfos.push({
                type: "druginfo",
                index: drug.Index,
                driprate: drug.driprate,
                driptime: drug.driptime,
                duration: drug.duration
            });
        }
    }
    for (var p in McMedCondArray) {
        var dis = McMedCondArray[p];
        jsonInfos.push({
            type: "diseaseinfo",
            index: dis.Index,
            starttime: dis.starttime,
            endtime: dis.endtime
        });
    }
    // 检验申请项
    for (var p in McLabArray) {
        var labexam = McLabArray[p];
        jsonInfos.push({
            type: "labinfo",
            requestno: labexam.Requestno,
            labexamcode: labexam.LabExamCode,
            labexamname: labexam.LabExamName,
            startdatetime: labexam.StartDateTime,
            deptcode: labexam.DeptCode,
            deptname: labexam.DeptName,
            doctorcode: labexam.DoctorCode,
            doctorname: labexam.DoctorName
        });
    }
    // 检查申请项
    for (var p in McExamArray) {
        var labexam = McExamArray[p];
        jsonInfos.push({
            type: "examinfo",
            requestno: labexam.Requestno,
            labexamcode: labexam.LabExamCode,
            labexamname: labexam.LabExamName,
            startdatetime: labexam.StartDateTime,
            deptcode: labexam.DeptCode,
            deptname: labexam.DeptName,
            doctorcode: labexam.DoctorCode,
            doctorname: labexam.DoctorName
        });
    }
    //加历史处方
    return jsonInfos;
    /*********************************************/
};

McPASS.McPASScheck = function (callback, _isShowWindow) {
    var pass = McPASS.Mc_PassSwitch();
    var pr = McPASS.Mc_PrSwitch();
    if (pass <= 0 && pr <= 0) {
        //callback, anyway,we need callback for his invoke 
        if (McPASS.ScreenHighestSlcode != -10) McPASS.ScreenHighestSlcode = -10;
        if (mc_pass_init_success > 0) mc_pass_init_success = -10;
    }
    if (mc_pass_init_success !=1) {        
        callback(mc_pass_init_success, 1);//return status when pass init failed,may passcore is abnormal or ipaddress is error or init timeout
        return;
    }
    //return if screen now
    if (mc_is_screen_now == 1) {
        return;
    }
    mc_is_screen_now = 1; //screen now   
    McPASS.Screen_callback = callback; //set the callback function
    McPASS.isShowWindow = 1; //whether show screen window
    if (_isShowWindow != undefined) {
        McPASS.isShowWindow = _isShowWindow;
    }
    McPASS.McGetModuleName(McPASS.McScreen);
};

//pass screen and pr system screen
McPASS.McScreen = function () {
    var pass = McPASS.Mc_PassSwitch();
    var pr = McPASS.Mc_PrSwitch();

    if (pr == 1 && MCpatientInfo && MCpatientInfo.IsDoSave + '' == '0') { //不采集的时候不审查PR
        pr = 0;
    }

    if (pass <= 0 && pr <= 0) {
        //callback, anyway,we need callback for his invoke 
        if (McPASS.ScreenHighestSlcode != -10) McPASS.ScreenHighestSlcode = -10;
        if (mc_pass_init_success != -10) mc_pass_init_success = -10;
        if (this.callback && this.callback != "" && typeof this.callback === 'function') {
            this.callback(McPASS.ScreenHighestSlcode);
        }
        return;
    }
    McDebug_t1 = McGetCurrentTime();
    MC_global_Screen_Count = 0;

    McPASS.MCPASSclient_Verify();//Verify
    McPASS.MCpatientInfo_Verify();//Verify
    McPASS.McDrugsArray_Verify();//Verify
    McPASS.McMedCondArray_Verify();//Verify
    McPASS.McAllergenArray_Verify();//Verify
    McPASS.McOperationArray_Verify();//Verify
    McPASS.McLabArray_Verify();//Verify
    McPASS.McExamArray_Verify();//Verify

    /*********************************************/
    //新接口，组合方式
    var jsonInfos = McPASS.McScreenJsonAddAPI();
    /*********************************************/
    var o = {
        PassClient: MCPASSclient,
        Patient: MCpatientInfo,
        ScreenAllergenList: { ScreenAllergens: McAllergenArray },
        ScreenMedCondList: { ScreenMedConds: McMedCondArray },
        ScreenOperationList: { ScreenOperations: McOperationArray },
        ScreenDrugList: { ScreenDrugs: McDrugsArray },
        InputJsonInfoList: { InputJsonInfos: jsonInfos }
    };
    McPASS.Debug(IsDebugPatient, "病人信息：" + McPASS.toMcJSONString(MCpatientInfo), DebugShowDiv_Patient); //debug
    McPASS.Debug(IsDebugRecipes, "处方：" + McPASS.toMcJSONString(McDrugsArray), DebugShowDiv_Recipes); //debug 
    McPASS.Debug(IsDebugAllergens, "过敏史：" + McPASS.toMcJSONString(McAllergenArray), DebugShowDiv_Allergens); //debug
    McPASS.Debug(IsDebugMedConds, "诊断：" + McPASS.toMcJSONString(McMedCondArray), DebugShowDiv_MedConds); //debug 
    McPASS.Debug(IsDebugOperation, "手术：" + McPASS.toMcJSONString(McOperationArray), DebugShowDiv_Operation); //debug 
    MC_global_Screen_HISdata = McPASS.toMcJSONString(o);

    McPASS.reSetDrugWarnLight(o);

    //MC_global_Screen_HISdata bs的维护工具，可以直接传入json串进来，然后要审查界面，所以要直接调用，把上面这个对象o剥离出去

    //only pass default
    mc_is_pr_request = 0;//reset
    var obj = {
        PASSFuncName: "Mc_DoScreen",
        PASSParam: MC_global_Screen_HISdata
    };
    var data = McPASS.toMcJSONString(obj);
    //pass,pr
    if (pass == 1 && pr == 1) {
        obj = {
            PASSFuncName: "Mc_PASSPR",
            PASSParam: MC_global_Screen_HISdata
        };
        data = McPASS.toMcJSONString(obj);
        mc_is_pr_request = 1;//pr ws
    }
    else if (pass == 0 && pr == 1) {
        obj = {
            PASSFuncName: "Mc_PR",
            PASSParam: MC_global_Screen_HISdata
        };
        data = McPASS.toMcJSONString(obj);
        mc_is_pr_request = 1;//pr ws
    }
    var ajax = McAjax;
    if (mc_is_pr_request == 1) {
        ajax = McAjaxPR;
    }

    ajax.send(data, McPASS.Screen_callback); //recipecheck should support callback function 
    ajax.onmessage = function (result) {
        mc_is_screen_now = 0; //screen and analyze screen result done
        //var mc_is_core_error = false; 
        //pass,pr switch
        McPASS.Debug(IsDebugResult, "审查结果：" + result, DebugShowDiv_Result); //debug 
        MC_global_Screen_PASSdata = null;
        MC_global_Screen_PRstatus = null;
        MC_current_Detail_divId = "";
        if (pass == 1 && pr == 0) { // 只有PASS
            if (result.indexOf("[ERROR]") >= 0) {
                mc_product_switch.PASS = 0;
                mc_pass_init_success = -10;
                McPASS.ScreenHighestSlcode = -10;//return -10 when passcore is abnormal 
                if (result=="[ERROR]:timeout") {
                    mc_pass_init_success = -99;
                    McPASS.ScreenHighestSlcode = -99;//return -99 when timeout 
                }
            } else {
                MC_global_Screen_PASSdata = McPASS.fn.EvalJson(result);
            }
        } else if (pass == 1 && pr == 1) {
            var _result = McPASS.fn.EvalJson(result);
            var passstr = "";
            if (_result == undefined || _result == null ||
                _result.PASS == undefined || _result.PASS == null ||
                ((passstr = McPASS.toMcJSONString(_result.PASS)).indexOf("[ERROR]") >= 0)) {
                mc_product_switch.PASS = 0;
                McPASS.ScreenHighestSlcode = -10;//return -10 when passcore is abnormal  
                if (passstr == "[ERROR]:timeout") {
                    McPASS.ScreenHighestSlcode = -99;//return -99 when timeout 
                }
            } else {
                MC_global_Screen_PASSdata = _result.PASS;
            }
            if (_result == undefined || _result == null ||
                _result.PR == undefined || _result.PR == null ||
                McPASS.toMcJSONString(_result.PR).indexOf("[ERROR]") >= 0) {
                mc_product_switch.PR = 0;
            } else {
                MC_global_Screen_PRstatus = _result.PR;
            }
        }
        else if (pass == 0 && pr == 1) {
            if (result.indexOf("[ERROR]") >= 0) {
                mc_product_switch.PR = 0;
                mc_pass_init_success = -10;
                if (result == "[ERROR]:timeout") {
                    mc_pass_init_success = -99;
                }
            } else {
                MC_global_Screen_PRstatus = McPASS.fn.EvalJson(result);
            }
        }
        if (MC_global_Screen_PASSdata != null) {
            McPASS.ScreenHighestSlcode = MC_global_Screen_PASSdata.HighestSlcode; //审查结果最严重级别值 
        }


        if (McPASS.ScreenHighestSlcode > 0 || !___mcPrStatusIsPass(MC_global_Screen_PRstatus)) { //审查的时候如果结果小于0则不处理，不返灯
            McPASS.McAnalyzeScreenResult(1); //Analyze the Screen Result
        } else { //没进窗口要回调HIS
            if (this.callback && this.callback != "" && typeof this.callback === 'function') {
                this.callback(McPASS.ScreenHighestSlcode);
            }

            //返蓝灯
            if(MC_global_Screen_PASSdata){                
                var _drugs = MC_global_Screen_PASSdata.ScreenResultDrugs;
                if(_drugs){                   
                    for (var i = 0; i < _drugs.length; i++) {
                        var _drugIndex = _drugs[i].DrugIndex;
                        var _elem = McPASS.fn.byId("McRecipeScreenLight_" + _drugIndex);
                        if (_elem) {
                            _elem.className = "mcScreenLight_0";
                            _elem.style.backgroundImage = "url(" + mcWebServiceUrl + "/PassImage/0.jpg)";
                        }
                    }
                }
            }
        }



        //callback, anyway,we need callback for his invoke 
        // alter by lxl 2017-07-18 因为加入判断是否填写所有理由，回调放到关闭函数调用
        //if (this.callback && this.callback != "" && typeof this.callback === 'function') {
        //    this.callback(McPASS.ScreenHighestSlcode);
        //}
    };
    McDebug_t2 = McGetCurrentTime();
};


function ___mcPrStatusIsPass(prStatus) {
    if (!prStatus) return true;
    for (var i = 0; i < prStatus.length; i++) {
        if (prStatus[0] && prStatus[0].Status < 0) {
            return false;
        }
    }
    return true;
}

var mc_polling_timer = null; //轮询定时器 Handle

//分析审查结果
McPASS.McAnalyzeScreenResult = function (isScreen) {
    var _win = null;
    try {
        //最高级别大于0表示有审查结果 并且是<审查>的时候才显示,筛选的时候已经有了
        if (McPASS.isShowWindow && (McPASS.ScreenHighestSlcode > 0 || MC_global_Screen_PASSdata != null) && isScreen == 1) {
            _win = new McPASS.McScreenWindow();
            _win.show();
        }
        McDetailItemsScreen = [];
        var html = [];
        var Mcdrugs = []; //每个药组织的数据        
        var warn1 = 0, warn2 = 0, warn3 = 0, warn4 = 0;
        var mcindex = 0; //有多少个药有问题
        McScreenReasonArray.length = 0; //虽然刷新会被情况，还是显示给清空下
        
        if (MC_global_Screen_PASSdata == null) { //如果没有PASS审查结果则直接进入PR流程
            setMcGetStatus();
            PrProccess();
        } else {
            var _html = passResultHtml();
            if (!_html) { //如果PASS审查组织的HTML为空也直接进入PR流程
                setMcGetStatus();
                PrProccess();
            } else {
                setPassResultHtml(_html);
                if (isNeedReply()) {
                    McPASS.HideCloseButton(); //有PR提请发药的情况下先隐藏窗口Close按钮
                }
            }
        }

        MC_global_Screen_Count = mcindex;
    }
    catch (e) {
        var _html = "<div class='mcloading' style='height:" + (mcWindowHeight - 115) + "px;'>" + McPASS.ErrorTip() + "</div>";
        setPassResultHtml(_html);
    }


    McDebug_t3 = McGetCurrentTime();
    if (IsDebugExecTime) {
        McDebugShowTime();
    }


    //设置 PASS审查结果 HTML
    function setPassResultHtml(html) {
        var elem = McPASS.fn.byId("mcDivScreenLeft2ID");
        if (elem) {
            elem.innerHTML = html;
        }
    }
    //设置 PR审查结果 HTML
    function setPrResultHtml(html) {
        var elem = McPASS.fn.byId("mcDivScreenWindowBodyLeft");
        if (elem) {
            elem.innerHTML = html;
        }
    }
    //PR审查是否有提请发药
    function isNeedReply() {
        var prStatus = MC_global_Screen_PRstatus;
        if (prStatus && prStatus[0]) {
            var mTaskStatus = prStatus[0];
            if (mTaskStatus.NeedApply == 1) {
                return true;
            }
        }
        return false;
    }
    //PR结果
    var prStatus = MC_global_Screen_PRstatus;

    //当为异步调用，并且 PR  状态未通过的时候，关闭窗口后，进入PR处理流程
    if (!MC_Is_SyncCheck && !___mcPrStatusIsPass(prStatus)) {
        setMcGetStatus();
        McPASS.McUnCloseWindowCallBack = PrProccess; //进止关闭窗口，设置处理函数
    } else if (MC_global_Screen_PASSdata == null) { //如果(没有PR结果或Status> 0)并且也没有PASS结果情况下则直接返回通过
        McPASS.McReturnToModify();
    }

    //判断空字符串
    String.prototype.IsEmpty = function () {
        var re = /^\s*$/gi;
        if (re.test(this)) {
            return true;
        }
        return false;
    }

    //设置获取状态的参数
    function setMcGetStatus() {
        McGetStatus.HospID = MCPASSclient.HospID;
        McGetStatus.PatCode = MCpatientInfo.PatCode;  //病人编号
        McGetStatus.InHospNo = MCpatientInfo.InHospNo; //门诊号/住院号
        McGetStatus.VisitCode = MCpatientInfo.VisitCode;//门诊号/住院次数
        if (MCpatientInfo.PatStatus > 1) {
            McGetStatus.TaskType = 2;
        } else {
            McGetStatus.TaskType = 1;
        }
    }

    var mc_polling_count = 0; //轮询次数
    var currentStatus = null; //当前状态

    //清除定时器
    function clearTimer() {
        if (mc_polling_timer != null) {
            clearTimeout(mc_polling_timer);
        }
    }


    //PR 处理流程
    function PrProccess() {       
        McPASS.McUnCloseWindowCallBack = null;
        if (_win != null) {
            McPASS.McCloseWindow(0);
        }

        //在打开PR窗口之前先检查一次状态，如果已经通过，就不打开窗口直接回调HIS
        McPASS.Mc_PR_GetStatusObj(function (mcTaskStatus) {
            currentStatus = mcTaskStatus;
            MC_global_Screen_PRstatus = [currentStatus];
            if (mcTaskStatus.hasError || mcTaskStatus.Status >= 0) {
                MC_global_Screen_PRstatus = [mcTaskStatus];
                if (McPASS.Screen_callback && typeof McPASS.Screen_callback === 'function') {
                    McPASS.Screen_callback(McPASS.ScreenHighestSlcode);
                }
                return;
            }


            _win = new McPASS.McScreenWindow();
            _win.show();

            McPASS.HideCloseButton(); //轮询之前先隐藏窗口Close按钮

            polling();
        });
    }

   
    //轮询方法
    function polling() {
        mc_polling_timer = null;
        var _func = arguments.callee;

        McPASS.Mc_PR_GetStatusObj(function (mcTaskStatus) {
            mc_polling_count++;
            if (mc_polling_count == 1) {
                var html = prResultHtml(mcTaskStatus);
                setPrResultHtml(html);
            }
            
            currentStatus = mcTaskStatus;

            if (mcTaskStatus.hasError || mcTaskStatus.Status >= 0) {
                clearTimer();
                MC_global_Screen_PRstatus = [currentStatus];
                McPASS.McReturnToModify();
                return;
            }

            if ((mcTaskStatus.Status == -1 &&
               (mcTaskStatus.NeedApply == 0 &&
                mcTaskStatus.NeedAutoIntervene == 0)) ||
                mcTaskStatus.Status == -5 ||
                mcTaskStatus.Status == -6 ||
                mcTaskStatus.Status == -7) {
                resetTimeLeft(mcTaskStatus);
                mc_polling_timer = setTimeout(_func, 1000);
            }
            else
            {
                var html = prResultHtml(mcTaskStatus);
                setPrResultHtml(html);
            }
        });
    }

    //格式化倒计时数字
    function _FormatFloat(time) {
        return Math.ceil(time*100)/100;
    }
    //重新设置倒计时
    function resetTimeLeft(m_taskStatus) {
        var timeLeft = "正在等待药师审核，请稍后";
        if (m_taskStatus.OverTime > 0)
        {
            timeLeft += "，预计等待时间还有" + _FormatFloat( m_taskStatus.OverTime - m_taskStatus.PassTime) + "秒";
        }
        else
        {
            timeLeft += "...";
        }
        var elem = document.getElementById('WaitForScreen');
        if (elem) {
            elem.innerHTML = timeLeft;  
        }
    }
   
    //提请发药
    McPASS.McApplyForReview = function () {
        clearTimer();
        var elem = document.getElementById('mcRadioUrgent');
        var urgent = 0;
        if (elem && elem.checked) {
            urgent = 1;
        }

        if (currentStatus == null) { //提请发药有可能还没有进入轮询，因此先取状态   
            McPASS.Mc_PR_GetStatusObj(function (mcTaskStatus) {
                currentStatus = mcTaskStatus;
                currentStatus.urgent = urgent;
                _prApplyForReview();
            });
        } else {
            currentStatus.urgent = urgent;
            _prApplyForReview();
        }

        function _prApplyForReview() {
            McPASS.Mc_PR_ApplyForReview(currentStatus, function (status) {
                if (status == 1) {
                    McPASS.Mc_PR_GetStatusObj(function (mcTaskStatus) {
                        var html = prResultHtml(mcTaskStatus);
                        setPrResultHtml(html);
                        polling();
                    });
                } else {
                    McPASS.Mc_PR_GetStatusObj(function (mcTaskStatus) {
                        currentStatus = mcTaskStatus;
                        MC_global_Screen_PRstatus = [currentStatus];
                        McPASS.McReturnToModify();
                    });
                }
            });
        }       
    }

    //双签
    McPASS.McCounterSign = function (mcDivReasonRadioD, mcMemoReason) {
        clearTimer();
        var rElem = document.getElementById(mcDivReasonRadioD);
        var tElem = document.getElementById(mcMemoReason);
        var memo = "";
        if (rElem.checked) {
            memo = rElem.value;
        }
        else {
            memo = tElem.value;
        }
        if (memo == null || memo.IsEmpty()) {
            alert('请填写双签理由');
            return;
        }
        currentStatus.Remark = memo;
        McPASS.Mc_PR_CounterSign(currentStatus, function (status) {
            if (status == 1) {
                McPASS.Mc_PR_GetStatusObj(function (mcTaskStatus) {
                    if (mcTaskStatus.Status >= 0) {
                        currentStatus = mcTaskStatus;
                        MC_global_Screen_PRstatus = [currentStatus];
                        McPASS.McReturnToModify();
                    }
                    else {
                        var html = prResultHtml(mcTaskStatus);
                        setPrResultHtml(html);
                        polling();
                    }
                });

            } else {
                McPASS.Mc_PR_GetStatusObj(function (mcTaskStatus) {
                    currentStatus = mcTaskStatus;
                    MC_global_Screen_PRstatus = [currentStatus];
                    McPASS.McReturnToModify();
                });
            }
        });
    }

    //聊天
    McPASS.McShowIM = function () {
        McPASSTalk(null, currentStatus.WorkerId, '', false)
    }

    //切换填写原因
    McPASS.McChangeResaonMemo = function (mcDivReasonRadioD, mcDivReasonRadioO, mcMemoReason) {
        var elem1 = document.getElementById(mcDivReasonRadioD);
        var elem2 = document.getElementById(mcDivReasonRadioO);

        var elem3 = document.getElementById(mcMemoReason);

        if (elem1.checked) {
            elem3.setAttribute("disabled", "disabled");
            return;
        }

        if (elem2.checked) {
            elem3.removeAttribute("disabled");
        }
    }

    //PR审核结果 HTML 字符串
    function prResultHtml(m_taskStatus) {
        var timeLeft = "";
        var strHtml = "";
        if (m_taskStatus.OverTime > 0)
        {
            timeLeft = "，预计等待时间还有" + _FormatFloat(m_taskStatus.OverTime - m_taskStatus.PassTime) + "秒";
        }
        else
        {
            timeLeft = "...";
        }

        if (m_taskStatus.Status == -1 || m_taskStatus.Status == -7)
        {
            if (m_taskStatus.NeedApply == 1)
            {// 需要提请发药
                var urgent = "";
                if (m_taskStatus.Urgent == 0)
                {// 加急模式打开
                    urgent = "<label>\
                        <INPUT id=\"mcRadioUrgent\" hidefocus type=\"checkbox\" class=\"checkbox\">加急\
                        </label>";
                }

                var _arr = [];
                _arr.push("<div class=\"mcPRDivCenter\">");
                _arr.push("    <div class=\"mcPRWait\">");
                _arr.push( "        <img src=\"" + mcWebServiceUrl + "/PassImage/pr_tip.png\" />");
                _arr.push("        <span>请选择您的下一步操作：</span>");
                _arr.push("    </div>");
                _arr.push("</div>");
                _arr.push("<div class=\"mcPRApplyDiv\">");
                _arr.push("    <a id=\"btnReturnToModify\" hidefocus onclick=\"McPASS.McReturnToModify();\">");
                _arr.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/ReturnToModify.gif\" width=\"100\" height=\"30\" border=\"0\" alt=\"返回修改\" />");
                _arr.push( "    </a>");
                _arr.push( "    <a id=\"btnApplyForReview\" hidefocus onclick=\"McPASS.McApplyForReview();\">");
                _arr.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/ApplyForReview.gif\" width=\"100\" height=\"30\" border=\"0\" alt=\"提请发药\" />");
                _arr.push( "    </a>");
                _arr.push(urgent + "</div>");
                strHtml = _arr.join('');
            }
            else if (m_taskStatus.NeedAutoIntervene == 1)

            {// 需要自动干预

                //url(PassImage
                var _reason = '';
                if (m_taskStatus.Reason) {

                    _reason = m_taskStatus.Reason.replace(/url\(PassImage/gi, 'url(' + mcWebServiceUrl + '/PassImage');
                }


                var _arr = [];
                _arr.push("<div class=\"mcPRDivCenter\">");
                _arr.push("    <div class=\"mcPRWarning\">");
                _arr.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/pr_warning.png\" />");
                _arr.push("        <span>系统审核未通过!</span>");
                _arr.push("        <div>");
                _arr.push("           <div class=\"WarningTxt\">" + _reason + "</div>");
                _arr.push("        </div>");
                _arr.push("    </div>");
                _arr.push("    <div class=\"mcPRReason\">");
                _arr.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/pr_info.png\" />");
                _arr.push("        <span>若坚持用药，请填写用药理由：</span>");
                _arr.push("        <div>");
                _arr.push("            <ul>");
                _arr.push("                <li>");
                _arr.push("                    <label>");
                _arr.push("                        <INPUT id=\"mcDivReasonRadioD\" name=\"mcDivReasonRadio\" hidefocus type=\"radio\" onclick=\"McPASS.McChangeResaonMemo('mcDivReasonRadioD', 'mcDivReasonRadioO', 'mcMemoReason')\" value=\"病人病情需要\" checked>病人病情需要");
                _arr.push("                    </label>");
                _arr.push("                </li>");
                _arr.push("                <li>");
                _arr.push("                    <label>");
                _arr.push("                        <INPUT id=\"mcDivReasonRadioO\" name=\"mcDivReasonRadio\" hidefocus type=\"radio\" onclick=\"McPASS.McChangeResaonMemo('mcDivReasonRadioD', 'mcDivReasonRadioO', 'mcMemoReason')\" value=\"其他\">其他");
                _arr.push("                    </label>");
                _arr.push("                </li>");
                _arr.push("            </ul>");
                _arr.push("        </div>");
                _arr.push("        <div>");
                _arr.push("            <textarea id=\"mcMemoReason\" onpropertychange=\"if(value.length>200) value=value.substr(0,200)\" disabled rows=\"6\" class=\"mcTextarea\"></textarea>");
                _arr.push("        </div>");
                _arr.push("    </div>");
                _arr.push("</div>");
                _arr.push("<div class=\"mcPRBottom\">");
                _arr.push("    <a id=\"btnCounterSign\" hidefocus onclick=\"McPASS.McCounterSign('mcDivReasonRadioD', 'mcMemoReason');\">");
                _arr.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/CounterSign.gif\" width=\"100\" height=\"30\" border=\"0\" alt=\"双签发药\" />");
                _arr.push("    </a>");
                _arr.push("    <a id=\"btnReturnToModify\" hidefocus onclick=\"McPASS.McReturnToModify();\">");
                _arr.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/ReturnToModify.gif\" width=\"100\" height=\"30\" border=\"0\" alt=\"返回修改\" />");
                _arr.push("    </a>");
                _arr.push("</div>");
                strHtml = _arr.join('');
            }
            else
            {// 没有配置，需要等待
                var _arr = [];
                _arr.push("<div class=\"mcPRDivCenter\">");
                _arr.push("  <div class=\"mcPRWait\">");
                _arr.push("     <img src=\"" + mcWebServiceUrl + "/PassImage/pr_wait.png\" />");
                _arr.push("    <span id=\"WaitForScreen\">正在等待药师审核，请稍后" + timeLeft + "</span>");
                _arr.push("    </div>");
                _arr.push("</div>");
                _arr.push("<div class=\"mcPRBottom\">");
                _arr.push("    <a id=\"btnReturnToModify\" hidefocus onclick=\"McPASS.McReturnToModify();\">");
                _arr.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/ReturnToModify.gif\" width=\"100\" height=\"30\" border=\"0\" alt=\"返回修改\" />");
                _arr.push("    </a>");
                _arr.push("</div>");
                strHtml = _arr.join('');
            }
        }
        else if (m_taskStatus.Status == -2 || m_taskStatus.Status == -3 || m_taskStatus.Status == -8)
        {// 双签发药
            var _arr = [];
            _arr.push("<div class=\"mcPRDivCenter\">");
            _arr.push("    <div class=\"mcPRWarning\">");
            _arr.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/pr_warning.png\" />");
            _arr.push("       <span>药师审核未通过!</span>");
            _arr.push("      <img class=\"message\" src=\"" + mcWebServiceUrl + "/PassImage/pr_message.png\" title=\"联系药师\" onclick=\"McPASS.McShowIM()\";/>");
            if (m_taskStatus.Reason != null && !m_taskStatus.Reason.IsEmpty())
            {
                _arr.push("       <div>");
                _arr.push("         <textarea readonly rows=\"6\" class=\"mcTextarea\">" + m_taskStatus.Reason + "</textarea>");
                _arr.push("       </div>");
            }
            if (m_taskStatus.Mobile != null && !m_taskStatus.Mobile.IsEmpty() && m_taskStatus.TelePhone != null && !m_taskStatus.TelePhone.IsEmpty())
            {
                _arr.push("       <div class=\"phone\">");
                _arr.push(((m_taskStatus.WorkerName == null || m_taskStatus.WorkerName.IsEmpty()) ? "" : m_taskStatus.WorkerName) + "药师联系电话：");
                _arr.push(m_taskStatus.Mobile + "，" + m_taskStatus.TelePhone )
                _arr.push("        </div>");
            }
            _arr.push("   </div>");
            if (m_taskStatus.NeedCounterSign > 0) {
                _arr.push("    <div class=\"mcPRReason\">");
                _arr.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/pr_info.png\" />");
                _arr.push("        <span>若坚持用药，请填写用药理由：</span>");
                _arr.push("        <div>");
                _arr.push("            <ul>");
                _arr.push("                <li>");
                _arr.push("                    <label>");
                _arr.push("                        <INPUT id=\"mcDivReasonRadioD\" name=\"mcDivReasonRadio\" hidefocus type=\"radio\" onclick=\"McPASS.McChangeResaonMemo('mcDivReasonRadioD', 'mcDivReasonRadioO', 'mcMemoReason')\" value=\"病人病情需要\" checked>病人病情需要");
                _arr.push("                    </label>");
                _arr.push("                </li>");
                _arr.push("                <li>");
                _arr.push("                    <label>");
                _arr.push("                        <INPUT id=\"mcDivReasonRadioO\" name=\"mcDivReasonRadio\" hidefocus type=\"radio\" onclick=\"McPASS.McChangeResaonMemo('mcDivReasonRadioD', 'mcDivReasonRadioO', 'mcMemoReason')\" value=\"其他\">其他");
                _arr.push("                    </label>");
                _arr.push("                </li>");
                _arr.push("            </ul>");
                _arr.push("        </div>");
                _arr.push("        <div>");
                _arr.push("            <textarea id=\"mcMemoReason\" onpropertychange=\"if(value.length>200) value=value.substr(0,200)\" oninput=\"if(value.length>200) value=value.substr(0,200)\" disabled rows=\"6\" class=\"mcTextarea\"></textarea>");
                _arr.push("        </div>");
                _arr.push("    </div>");
            }
            _arr.push("</div>");
            _arr.push("<div class=\"mcPRBottom\">");
            if (m_taskStatus.NeedCounterSign > 0)
            {             
                _arr.push("   <a id=\"btnCounterSign\" hidefocus onclick=\"McPASS.McCounterSign('mcDivReasonRadioD', 'mcMemoReason');\">");
                _arr.push("       <img src=\"" + mcWebServiceUrl + "/PassImage/CounterSign.gif\" width=\"100\" height=\"30\" border=\"0\" alt=\"双签发药\" />");
                _arr.push("   </a>");
            }
            _arr.push("    <a id=\"btnReturnToModify\" hidefocus onclick=\"McPASS.McReturnToModify();\">");
            _arr.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/ReturnToModify.gif\" width=\"100\" height=\"30\" border=\"0\" alt=\"返回修改\" />");
            _arr.push("    </a>");
            _arr.push("</div>");
            strHtml = _arr.join('');
        }
        else if (m_taskStatus.Status == -5)
        {// 点击提请发药，需要等待，不能返回  
            var _arr = [];
            _arr.push("<div class=\"mcPRDivCenter\">");
            _arr.push("    <div class=\"mcPRWait\">");
            _arr.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/pr_wait.png\" />");
            _arr.push("        <span id=\"WaitForScreen\">正在等待药师审核，请稍后" + timeLeft + "</span>");
            _arr.push( "    </div>");
            _arr.push("</div>");
            strHtml = _arr.join('');
        }
        else if (m_taskStatus.Status == -6)
        {// 点击双签复核，需要等待，不能返回
            var _arr = [];
            _arr.push("<div class=\"mcPRDivCenter\">");
            _arr.push("    <div class=\"mcPRWait\">");
            _arr.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/pr_wait.png\" />");
            _arr.push("        <span id=\"WaitForScreen\">正在等待药师复核，请稍后" + timeLeft + "</span>");
            _arr.push("        <img class=\"message\" src=\"" + mcWebServiceUrl + "/PassImage/pr_message.png\" title=\"联系药师\" onclick=\"McPASS.McShowIM();\"/>");
            if (m_taskStatus.Mobile != null && !m_taskStatus.Mobile.IsEmpty() && m_taskStatus.TelePhone != null && !m_taskStatus.TelePhone.IsEmpty()) {
                _arr.push("        <div class=\"phone\">");
                _arr.push("            " + ((m_taskStatus.WorkerName == null || m_taskStatus.WorkerName.IsEmpty()) ? "" : m_taskStatus.WorkerName) + "药师联系电话：");
                _arr.push(m_taskStatus.Mobile + "，" + m_taskStatus.TelePhone);
                _arr.push("        </div>");
            }
            _arr.push("    </div>");
            _arr.push("    <div class=\"mcPRReason\">");
            _arr.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/pr_info.png\" />");
            _arr.push("        <span>双签理由：</span>");
            _arr.push("        <div>");
            _arr.push("            <textarea readonly rows=\"6\" class=\"mcTextarea\">" + m_taskStatus.Remark + "</textarea>");
            _arr.push("        </div>");
            _arr.push("    </div>");
            _arr.push("</div>");
            strHtml = _arr.join('');
        }


        return strHtml;
    }

    //审查结果 HTML 字符串
    function passResultHtml() {

        var drug = MC_global_Screen_PASSdata.ScreenResultDrugs;
        var mustReason = false;
        //必须填写理由 alter by lcm 2017-07-10 理由必填标记由结果链表最外层的 MustReason 对象返回
        if (MC_global_Screen_PASSdata.MustUseReason == 1) {
            mustReason = true;
        }

        for (var i = 0; i < drug.length; i++) {
            var warntyperesult = drug[i];
            // alter by liuchunmei 2016-12-21 警示界面改造，添加非药结果
            if (warntyperesult == undefined ||
                warntyperesult == null ||
                warntyperesult.ScreenResults.Count <= 0) {
                continue;
            }
            var drugName = warntyperesult.DrugName;
            var strRouteName = "";
            var druguniquecode = "";
            var slcode = warntyperesult.Slcode;
            var drugindex = warntyperesult.DrugIndex;
            var isold = 0;
            if (warntyperesult.WarnType == undefined || warntyperesult.WarnType == null || warntyperesult.WarnType <= 1) {
                // 无结果类型节点 或 标识为药品结果     
                if (warntyperesult.WarnType == undefined || warntyperesult.WarnType == null) {
                    isold = 1;
                    if (McDrugsArray != undefined || McDrugsArray != null) {
                        for (var m = 0; m < McDrugsArray.length; m++) {
                            var screendrug = McDrugsArray[m];
                            if (screendrug == null) continue;
                            if (screendrug.Index == drugindex) {
                                druguniquecode = screendrug.DrugUniqueCode;
                                strRouteName = screendrug.RouteName;
                                break;
                            }
                        }
                    }
                } else {
                    druguniquecode = warntyperesult.DrugUniqueCode;
                    strRouteName = warntyperesult.RouteName;
                }
                drugName = "<span class='mcSeqNo'>" + (i + 1) + ".</span><a class='mcLinkHeader' hidefocus                  onclick=\"McPASS.McFuncGetQueryHisData('" + druguniquecode + "','" + drugName + "');McPASS.McRightMenu();\">" + drugName + "</a>";
                if (strRouteName != null && strRouteName != "") {
                    strRouteName = "(" + strRouteName + ")";
                }
                if (slcode <= 0) {
                    //****************************************************************************************//
                    //如果审查结果没问题的也给一个初始化的灯 表示没问题 刘哥暂时还不确定是不是5表示蓝灯 2013-10-17
                    var mc_light = McPASS.fn.byId("McRecipeScreenLight_" + drugindex);
                    if (mc_light) {//if light div is ready
                        mc_light.className = "mcScreenLight_0";
                        mc_light.style.backgroundImage = "url(" + mcWebServiceUrl + "/PassImage/0.jpg)";
                    }
                    //****************************************************************************************//
                    continue; //continue
                }
            }
            else {// 非药结果
                drugName = "<span class='mcSeqNo'>" + (i + 1) + ".</span><a class='mcLinkHeader'>" + drugName + "</a>";
                if (slcode <= 0) {
                    continue;
                }
            }
            Mcdrugs = []; //每个循环后都初始化下，否则会在原有的基础上增加值 
            //*************************************处理明细*********************************************************************************
            var detail = warntyperesult.ScreenResults; //该药的审查明细
            for (var j = 0; j < detail.length; j++) {
                var mc_Slcode = detail[j].Slcode;
                if (mc_Slcode <= 0) {
                    continue;
                }
                // add by lxl 2017-10-24 增加新问题过滤逻辑
                var mc_IsNewWarning = detail[j].IsNewWarning;
                if (mc_IsNewWarning == 0) {
                    var mc_light = McPASS.fn.byId("McRecipeScreenLight_" + drugindex); //mcindex
                    if (mc_light) {//if light div is ready
                        mc_light.className = "mcScreenLight_" + mc_IsNewWarning;
                        mc_light.style.backgroundImage = "url(" + mcWebServiceUrl + "/PassImage/" + mc_IsNewWarning + ".jpg)";
                    }
                    continue;
                }
                // end add
                var moduleName = detail[j].ModuleName;

                if (mc_Slcode == 1) { warn1++; } else if (mc_Slcode == 2) { warn2++; } else if (mc_Slcode == 3) { warn3++; } else if (mc_Slcode == 4) { warn4++; } //累计警示级别数 

                var detailType = detail[j].DetailType;
                var detailParams = detail[j].DetailParams;
                var abstract = detail[j].Abstract;
                var tipAttach = detail[j].TipAttach;
                var moduleItems = detail[j].ModuleItems; // PASS USER
                var moduleId = detail[j].ModuleID; // ModuleID
                var detailReason = detail[j].DetailReason; //理由
                //在查看详细的按钮事件上传入参数（单条审查结果的对象，便于生成详细信息） 
                var linkText = detail[j].LinkText;
                var linkParams = detail[j].LinkParams; //不为空表明有

                var hasDetail = true; //有详细信息
                if ((detailType == 0 || detailParams == "") && abstract == "") {
                    hasDetail = false;
                }
                var hasReason = false; //要填理由
                if (detailReason != "") {
                    hasReason = true;
                }

                var dItem = new McDetailItems_In();
                dItem.DetailParams = detailParams;
                dItem.DetailType = detailType;
                dItem.Abstract = abstract;
                dItem.DetailTip = tipAttach;
                dItem.LinkText = linkText;
                dItem.LinkParams = linkParams;
                McDetailItemsScreen[McDetailItemsScreen.length] = dItem;

                var mc_n = McDetailItemsScreen.length - 1; //n
                var mcDetailrowDiv = "mcDivItemDetailRowMore_" + mcindex + "_" + j;
                var mcDetailrowBtn = "mcBtnItemDetailRowMore_" + mcindex + "_" + j;
                var mcReasonrowDiv = "mcDivItemReasonRowMore_" + mcindex + "_" + j;
                var mcReasonrowBtn = "mcBtnItemReasonRowMore_" + mcindex + "_" + j;
                var mc_detailIsShow = "";
                var mc_reasonIsShow = "";
                var mc_detailDBclick = ""; //理由不支持双击关闭，详细信息支持双击关闭
                // alter by lcm 2017-07-10

                if (hasReason) {
                    var txt = "理由";
                    if (mustReason) {
                        mc_cancloseWindow = 0;
                        // alter by liuchunmei 2016-12-23 将index改为i更准确。定义个二维数组来存储，下次清的时候通过传入的i和j来清理
                        McScreenReasonArray[McScreenReasonArray.length] = [i, j];
                    }
                    //只需要把当前这个j传过去，那边自己找 alter by liuchunmei 2016-12-23 将index改为i更准确
                    mc_reasonIsShow = "<a id='" + mcReasonrowBtn + "' class='mcBtnShowDetail' hidefocus onclick=\"McPASS.McFoldResonRowDiv('"
                        + mcReasonrowBtn + "','" + mcReasonrowDiv + "','" + i + "','" + j + "','" + isold + "' )\">" + txt + "</a>";
                }
                if (hasDetail) {//有详细信息的情况
                    mc_detailDBclick = " ondblclick=\"McPASS.McFoldItemDetailRowMoreDiv('" + mcDetailrowBtn + "','" + mcDetailrowDiv + "',1);\"";
                    mc_detailIsShow = "<a id='" + mcDetailrowBtn + "' class='mcBtnShowDetail' hidefocus onclick=\"McPASS.McFoldItemDetailRowMoreDiv('"
                    + mcDetailrowBtn + "','" + mcDetailrowDiv + "','" + mc_n + "',1)\">详细</a>";
                }
                // end alter by lcm 2017-07-10
                var mcModuleName = moduleName;
                var mcUserLabel = "";
                if (moduleItems.indexOf("CUSTOMRULE") >= 0) {//自由自定义
                    mcUserLabel = "<img src='" + mcWebServiceUrl + "/PassImage/mcCustom.gif' title='自由自定义审查结果' align='absMiddle'/>";
                } else if (moduleItems.indexOf("USER") >= 0) {//USERPeriOpr2 屏蔽标记,,还有多个标记USERPeriOpr0.5之类的
                    mcUserLabel = "<img src='" + mcWebServiceUrl + "/PassImage/mcUSER.gif' title='用户自定义审查结果' align='absMiddle'/>";
                }
                var mc_warning = detail[j].Warning;
                if (mc_warning == "") {
                    mc_warning = "&nbsp;";
                }
                var mcDetaildiv = [
                    "<div align='left' class='mcDivDetailRow' style='visibility:visible;float:none;'>",
                        "<div align='left' class='mcDivDetailRowHead' style='visibility:visible;float:none;'>",
                                "<table class='mcTable' cellpadding='0' cellspacing='0'><tr valign='baseline'>",
                                "<td width='40px' _width='40px' align='left'>&nbsp;</td>",
                                "<td width='5px' _width='5px' align='left'><img src='" + mcWebServiceUrl + "/PassImage/mcpoint.gif'/></td>",
                                "<td width='85px' _width='85px' align='left'> <span class='mcTextSeverity", mc_Slcode, "'>", detail[j].Severity, "</span></td>",
                                "<td width='12px' _width='12px' align ='left'>", mcUserLabel, "</td>",
                                "<td width='85px' _width='85px' align='left'>", mcModuleName, "</td>",
                                "<td align='left' style='word-break:break-all;padding-left:10px;'>", mc_warning, "</td>",
                                "<td width='40px' _width='40px' align='right' style='padding-right:0px;'>", mc_reasonIsShow, "</td>",
                                "<td width='40px' _width='40px' align='right' style='padding-right:10px;'>", mc_detailIsShow, "</td>",
                                "</tr></table>",
                        "</div>",
                        "<div id='", mcReasonrowDiv, "' align='left' class='mcDivDetailRowMore' style='display:none;visibility:hidden;float:none;padding-left:80px;' ", "></div>",
                        "<div id='", mcDetailrowDiv, "' align='left' class='mcDivDetailRowMore' style='display:none;visibility:hidden;float:none;padding-left:80px;' ", mc_detailDBclick, "></div>",
                    "</div>"];
                Mcdrugs.push(mcDetaildiv.join("")); //把该药的问题信息挂在该药下
            } //end for-detail.length
            //*****************************************************************End Detail*****************************************************
            //药这一层,一个一个的药一行一行的依次组织起    
            if (Mcdrugs.join("") != "") //只有该药有详细情况才加这一层                
            {
                mcindex++; //审查结果有多少条药的信息
                var mcBtnShowHideDetail = "mcBtnShowHideDetail_" + mcindex;
                var mcDivScreenDrugDetail = "mcDivScreenDrugDetail_" + mcindex;

                var mcScreendiv = [
                    "<div class='mcdivPanel' onmouseout=\"this.style.backgroundColor=''\" onmouseover=\"this.style.backgroundColor='#efedf5'\" style='visibility:visible;float:none;'>", //装所有内容的div  _width:99%;
                        "<div class='mcDivPanelHead' ondblclick=\"McPASS.McFoldItemDetailDiv('", mcBtnShowHideDetail, "', '", mcDivScreenDrugDetail, "')\" style='visibility:visible;float:none;'>",
                            "<table class='mcTable' cellpadding='0' cellspacing='0'><tr>",
                                "<td align='center' width='52px' rowspan='2' valign='top'><img src='" + mcWebServiceUrl + "/PassImage/", slcode, ".gif' align=\"absMiddle\" /></td>", //align='absmiddle' 
                                "<td align='left' valign='middle' style='white-space:normal;word-break:break-all;'>", drugName, "<span class='mcRouteHeader'>", strRouteName, "</span></td>",
                                "<td align='right' width='56px' style='padding-right:18px;'><img id='", mcBtnShowHideDetail, "' src='" + mcWebServiceUrl + "/PassImage/mcunexpand.gif' style='_cursor:pointer;cursor:hand;cursor:pointer;' title='折叠' onclick=\"McPASS.McFoldItemDetailDiv('", mcBtnShowHideDetail, "', '", mcDivScreenDrugDetail, "');\"/></td>",
                            "</tr></table>",
                        "</div>",
                         "<div id='", mcDivScreenDrugDetail, "' align='left' class='mcDivPanelBody' style='visibility:visible;float:none;'>", Mcdrugs.join(""), "</div>",
                    "</div>"];
                html.push(mcScreendiv.join(""));
                //****************************************************************************************//
                //reset the screen light div classname
                var mc_light = McPASS.fn.byId("McRecipeScreenLight_" + drugindex); //mcindex
                if (mc_light) {//if light div is ready
                    mc_light.className = "mcScreenLight_" + slcode;
                    mc_light.style.backgroundImage = "url(" + mcWebServiceUrl + "/PassImage/" + slcode + ".jpg)";
                }
                /****************************************************************************************/
            } //end if   
        } //end for drug
        if (isScreen) {
            if (html.join("") == "") {
                return "";
            }
        }
        var _html = [];
        _html.push("<div id='mcScreenMainDiv' align='left' class='mcScreenBodyDiv' style='visibility:visible;float:none;background:url(" + mcWebServiceUrl + "/PassImage/mcbackground.gif) left repeat-y;'>");  //灯旁边有一根线 
        _html.push(html.join(""));
        _html.push("</div>");


        //返回修改HTML
        var retModifyHtml = "\r\n\
                   <a id=\"btnReturnToModify\" hidefocus onclick=\"McPASS.McReturnToModify();\">\
                        <img src=\"" + mcWebServiceUrl + "/PassImage/ReturnToModify.gif\" width=\"100\" height=\"30\" border=\"0\" alt=\"返回修改\" />\
                   </a>";

        var hasReturnToModify = false;
        if (mustReason) {
            hasReturnToModify = true;
            _html.push("<div class=\"mcPRBottom\">\r\n");
            _html.push(retModifyHtml);
            _html.push("</div>");
        }


        //PR结果
        var prStatus = MC_global_Screen_PRstatus;
        //在PASS审查结果页面上显示 PR，返回修改，提请发药等内容
        if (prStatus && prStatus[0]) {
            var mTaskStatus = prStatus[0];
            var strHtml = "";
            if (mTaskStatus.NeedApply == 1)
            {
                var urgent = "";
                if (mTaskStatus.Urgent == 0)
                {// 加急模式打开
                    urgent = "    <label>\
                           <INPUT id=\"mcRadioUrgent\" hidefocus type=\"checkbox\" class=\"checkbox\">加急\
                        </label>";
                }
                _html.push("\r\n");
                _html.push("<div class=\"mcPRBottom\">\r\n");
                _html.push(urgent);
                _html.push("<a id=\"btnApplyForReview\" hidefocus onclick=\"McPASS.McApplyForReview();\">\r\n");
                _html.push("        <img src=\"" + mcWebServiceUrl + "/PassImage/ApplyForReview.gif\" width=\"100\" height=\"30\" border=\"0\" alt=\"提请发药\" />\r\n");
                _html.push("    </a>\r\n");
                _html.push(retModifyHtml);
                _html.push("</div>");
            }
            else if (mTaskStatus.Status == -1 || mTaskStatus.Status == -7)
            {
                _html.push("<div class=\"mcPRBottom\">\r\n");
                _html.push(retModifyHtml);
                _html.push("</div>");
            }
        }

        return _html.join('');
    }
};

// 返回修改直接关闭窗口
McPASS.McReturnToModify = function () {
    if(mc_polling_timer != null) {
        clearTimeout(mc_polling_timer);
    }
    McPASS.McUnCloseWindowCallBack = null;
    mc_cancloseWindow = 1;
    McPASS.McCloseWindow(1);
    mc_cancloseWindow = 0;
};

//Set query parameters when click the drug name of the screen result window
McPASS.McFuncGetQueryHisData = function (pDrugUniqueCode, pDrugName) {
    var druguniquecode = (pDrugUniqueCode == null ? "" : pDrugUniqueCode);
    var drugname = (pDrugName == null ? "" : pDrugName);
    var drug = new Params_MC_queryDrug_In();
    drug.ReferenceCode = druguniquecode; //这两个该弄成一样
    drug.CodeName = drugname;
    MC_global_queryDrug = drug;
};
//-------------- 效率测试  记录时间--------------- 
var McDebug_t1;
var McDebug_t2;
var McDebug_t3;
function McDebugShowTime() {
    alert("审查加载数据用时：" + (McDebug_t2 - McDebug_t1) + "毫秒；" + "组织界面用时：" + (McDebug_t3 - McDebug_t2) + "毫秒");
}
function McGetCurrentTime() {
    var t = new Date();
    return (t.getSeconds() * 1000 + t.getMilliseconds());
}

/*
**为了保持跟cs一致，所以特此封装一个函数
_callback   回调函数（如果传了则调用该回调函数）
_isShowWindow （是否显示审查结果界面）0-不显示界面 1-显示界面
*/
function MDC_DoCheck(_callback, _isShowWindow) {
    if (MC_Is_SyncCheck == true) {
        McPASS.McPASScheckSync(_isShowWindow);
    }
    else {
        McPASS.McPASScheck(_callback, _isShowWindow);
    }
}

// add by lxl 2017-07-17 增加判断是否所有问题都填写了用药理由接口
function MDC_IsFullFillReason() {
    // 1.没有审查结果，则不用填理由
    if (MC_global_Screen_PASSdata.ScreenResultDrugs.length <= 0 || MC_global_Screen_PASSdata.HighestSlcode <= 0) {
        return 1;
    }
    // 2.系统配置为理由可以不用填
    if (MC_global_Screen_PASSdata.MustUseReason != 1) {
        return 2;
    }
    // 3.是否已经全部填写完
    McPASS.McGetIsFullFillReasonSync();
    return mc_is_fullfill;
}

//get slcode by drugIndex
function MDC_getWarningCode(drugIndex) {
    var slcode = 0;
    var result = MC_global_Screen_PASSdata.ScreenResultDrugs;
    for (var i = 0; i < result.length; i++) {
        var obj = result[i];
        var index = obj.DrugIndex;
        if (drugIndex == index) {
            slcode = obj.IsNewWarning == 0 ? 0 : obj.Slcode;
            break;
        }
    }
    return slcode;
};

/* 描述：审查之后调用该函数，获取一条药品医嘱的审查结果提示窗口。 */
function MDC_ShowWarningHint(pcIndex) {
    if (MC_global_Screen_PASSdata == null) {
        return;
    }
    var res = MC_global_Screen_PASSdata.ScreenResultDrugs;
    for (var i = 0; i < res.length; i++) {
        var drugIndex = res[i].DrugIndex;
        var drugName = res[i].DrugName;
        var screenResults = res[i].ScreenResults;
        if (pcIndex == drugIndex) {
            if (McPASS.fn.byId("mcDivWindowBody")) {
                McPASS.McCloseWindow();
            }
            var html = [];
            html.push("<div class='mcDivDrugAttribe' style='margin:3px;float:none;width:98%;overflow:hidden;overflow-y:auto;'>"); //100%出不来滚动条 
            var _slcode = 0; //如果蓝灯，则不提示
            for (var j = 0; j < screenResults.length; j++) {
                var obj = screenResults[j];
                var moduleName = obj.ModuleName;
                var severity = obj.Severity;
                var warning = obj.Warning;
                var slcode = obj.Slcode;
                _slcode = slcode;

                //处理自定义的情况                
                if (obj.ModuleItems.indexOf("CUSTOMRULE") >= 0) {//USERPeriOpr2 屏蔽标记,,还有多个标记USERPeriOpr0.5之类的
                    mcUserLabel = "<img src='" + mcWebServiceUrl + "/PassImage/mcCustom.gif' title='自由自定义审查结果' align='absMiddle'/>";
                } else if (obj.ModuleItems.indexOf("USER") >= 0) {//USERPeriOpr2 屏蔽标记,,还有多个标记USERPeriOpr0.5之类的
                    mcUserLabel = "<img src='" + mcWebServiceUrl + "/PassImage/mcUSER.gif' title='用户自定义审查结果' align='absMiddle'/>";
                }
                //*width (ie7) _width(ie6)
                html.push("<div align='left' class='mcDivPanel' style='margin-top:5px;visibility:visible;*width:98%;_width:98%;'>"); //float:none;
                html.push("<img src='" + mcWebServiceUrl + "/PassImage/", slcode, ".jpg' border='0' align='center' style='margin-top:-2px;'/>&nbsp;");
                html.push("<span class='mcTextSeverity", slcode, "'>", severity, "</span>&nbsp;&nbsp;&nbsp;");
                html.push("<span class='mcWarningTitle'>", moduleName, "</span><br>");
                html.push("<p class='mcWarningMain'>&nbsp;", warning, "</p>");
                html.push("</div>");
            }
            html.push("</div>");
            if (slcode > 0 && (res[i].IsNewWarning == 1 || res[i].IsNewWarning == undefined)) {
                var e = window.event || arguments.callee.caller.arguments[0];
                var win = new McPASS.McClsWindow({ top: e.clientY, left: e.clientX });
                win.singleScreenshow();
                McPASS.fn.byId("mcDivWindowBody").innerHTML = html.join('');
                McPASS.fn.byId("mcDivscreendTitle").innerHTML = drugName + "-审查结果";
            }
        }
    }
};
//预留函数，用途：当医生审查后有审查问题，然后就删了某个药，这时候直接保存了，并不再审查，导致我们系统里有存该次审查结果，
//医生希望能把这次审查结果给删掉，做法就是在删除药的时候把这些信息传过来，把库里该次审查结果信息全删掉
function MDC_DelScreenDrug(pcPatCode, pcVisitCode, pcIndex, pcOrderType, piPatStatus) {
    return 1;
};

//add 2016-03-29
/* 描述：审查之后调用该函数，获取一条药品医嘱的[简要警示信息]。 */
function MDC_GetResultDetail(pcIndex) {
    if (MC_global_Screen_PASSdata == null) {
        return null;
    }
    //权限控制
    if (MCcanGetScreenDtlRes < 1) {
        return null;
    }
    var json = new Object(); //返回的json串
    json.List = [];
    var res = MC_global_Screen_PASSdata.ScreenResultDrugs;
    for (var i = 0; i < res.length; i++) {
        var drug = res[i];
        var drugIndex = drug.DrugIndex;
        var druguniquecode = "";
        //alter by liuchunmei 2016-12-21 注释 因为警示界面调整
        if (drug.WarnType == undefined || drug.WarnType == null) {
            var hisDrug = null; //McDrugsArray MC_global_Screen_HISdata
            for (var k = 0; k < McDrugsArray.length; k++) {
                hisDrug = McDrugsArray[k];
                if (hisDrug == null) continue;
                if (hisDrug.Index == drugIndex) {
                    druguniquecode = hisDrug.DrugUniqueCode;
                    break;
                }
            }
        } else {
            druguniquecode = drug.DrugUniqueCode;
        }
        if (drug && drug.Slcode > 0 && (drugIndex == pcIndex || pcIndex == "")) {
            var _list = {
                DrugUniqueCode: druguniquecode, //end alter by liuchunmei 2016-12-21 注释 因为警示界面调整
                DrugName: drug.DrugName,
                Results: []
            };
            var screen = res[i].ScreenResults;
            for (var j = 0; j < screen.length; j++) {
                var obj = screen[j];
                if (obj) {
                    _list.Results.push({
                        OrderIndex: drugIndex,//OrderIndex DrugIndex
                        Slcode: obj.Slcode,
                        ModuleName: obj.ModuleName,
                        ModuleItems: obj.ModuleItems,
                        Severity: obj.Severity,
                        Warning: obj.Warning
                    });//push into Results
                }
            }
            json.List.push(_list);//push into json
        }
    }
    return McPASS.toMcJSONString(json);
};

// fixbug his嵌套警示灯时，药品从审查不通过 到 通过时，界面等没有刷新
McPASS.reSetDrugWarnLight = function (jsonObj) {
    var drugs = null;
    if (jsonObj) {
        try {
            drugs = jsonObj["ScreenDrugList"]["ScreenDrugs"];
        }
        catch (e) {

        }
    }
    else {
        if (MC_global_Screen_HISdata) {
            try {
                var json = McPASS.fn.EvalJson(MC_global_Screen_HISdata);
                drugs = json["ScreenDrugList"]["ScreenDrugs"];
            }
            catch (e) {

            }
        }
    }
    if (drugs) {
        for (var i = 0; i < drugs.length; ++i) {

            var mc_light = McPASS.fn.byId("McRecipeScreenLight_" + drugs[i].Index);
            if (mc_light) {//if light div is ready
                mc_light.className = "mcScreenLight_null";
                mc_light.style.backgroundImage = "";
            }
        }
    }
};﻿/*
查看详细 
btnID 查询点击按钮的id
divID 显示详细信息的div id 
n     McDetailItems[n]下标  
screen 0(查询)；1(审查)；审查跟查询得分开，如果在审查界面点查询会改变McDetailItems值）
*/

McPASS.McFoldItemDetailRowMoreDiv = function (btnID, divID, n, screen) {
    var btn = McPASS.fn.byId(btnID);
    var div = McPASS.fn.byId(divID);
    if (btn && div) {
        if (div.style.visibility.toLowerCase() == "visible") {
            div.style.display = "none";
            div.style.visibility = "hidden";
            if (btn.innerText == "关闭") { btn.innerText = "详细"; btn.innerHTML = "详细"; btn.style.color = "#9c4612"; } //else { btn.style.color = "#808080"; }
        }
        else {
            //close the other detail div
            if (MC_current_Detail_divId.length > 0) {
                var _div = McPASS.fn.byId(MC_current_Detail_divId);
                var _btn = McPASS.fn.byId(MC_current_Detail_btnId);
                if (_btn != null && _div != null) {
                    _div.style.display = "none";
                    _div.style.visibility = "hidden"; //_btn.style.color = "#9c4612";
                    if (_btn.innerText == "关闭") { _btn.innerText = "详细"; _btn.innerHTML = "详细"; } //else { _btn.style.color = "#808080"; }
                }
            } //end if           

            if (n == undefined) {
                n = 0;
            }
            //查询没传值过来
            if (screen == undefined) {
                screen = 0;
            }
            if (screen) {
                var type = McDetailItemsScreen[n].DetailType; //审查对象不一样
            }
            else {
                var type = McDetailItems[n].DetailType;
            }
            div.style.display = "";
            div.style.visibility = "visible";
            btn.style.color = "#9c4612";
            //体外配伍的查询不改变， 体外配伍审查还是要变
            if (type != McPASS.SysMenu.McDetail.IV || screen > 0) {
                btn.innerText = "关闭";
                btn.innerHTML = "关闭"; //firefox                
            }
            //IV的特殊情况处理
            if (btn.innerText == "详细" && type == McPASS.SysMenu.McDetail.IV) {
                btn.innerText = "关闭";
                btn.innerHTML = "关闭"; //firefox  
            }

            //if (McPASS.fn.byId(divID).innerHTML.length <= 0) {//查询具体详细信息结果(如果已经查询过 则不用再请求服务端)
            McPASS.McDodetail(btnID, divID, n, screen); //查询明细
            //}

            //div.scrollIntoView(); //会导致嵌套的界面乱跳
            /*设置当前详细信息的DIV*/
            MC_current_Detail_divId = divID;
            MC_current_Detail_btnId = btnID;
        }
    }
};  //End

var mcDetailParms = {
    btnId: null,
    divId: null,
    Index: 0
};

McPASS.McDodetail = function (btnId, divId, n, screen) {
    McPASS.McDetailContent(btnId, divId, "", n, screen);
    if (n == undefined) {
        n = 0;
    }
    if (screen) {
        var params = McDetailItemsScreen[n].DetailParams;
        var Abstract = McDetailItemsScreen[n].Abstract;
        var type = McDetailItemsScreen[n].DetailType;
    }
    else {
        var params = McDetailItems[n].DetailParams;
        var Abstract = McDetailItems[n].Abstract;
        var type = McDetailItems[n].DetailType;
    }
    if (params <= 0) {
        // Add by liuchunmei 2016-12-28 添加详细信息tip
        var tip = McDetailItemsScreen[n].DetailTip;
        if (tip != "") {
            Abstract = tip + "<br/>" + Abstract; //加上tip
        }
        // end add
        McPASS.McDetailContent(btnId, divId, McPASS.McFormatParagraph(Abstract), n, screen);
    }
    else {
        mcDetailParms = {
            btnId: btnId,
            divId: divId,
            Index: n
        };
        var _obj = {
            DetailParams: {
                DetailParams: params, //以字符的形式传
                DetailType: type
            },
            PassClient: MCPASSclient
        };
        MC_global_Detail_HISdata = McPASS.toMcJSONString(_obj);
        var obj = {
            PASSFuncName: "Mc_DoDetail",
            PASSParam: MC_global_Detail_HISdata
        };
        var data = McPASS.toMcJSONString(obj);
        McAjax.send(data);
        McAjax.onmessage = function (result) {
            if (result.indexOf("[ERROR]") < 0) {
                MC_global_Detail_PASSdata = result;
                McPASS.McAnalyzeDetailResult(mcDetailParms, screen, n);
            }
            else {
                McPASS.McDetailContent(mcDetailParms.btnId, mcDetailParms.divId, McPASS.ErrorTip(), n, screen)
            }
        };
    }
};
McPASS.McAnalyzeDetailResult = function (detailParms, screen, n) {
    try {
        var body = "";
        var obj = McPASS.fn.EvalJson(MC_global_Detail_PASSdata);
        var Abstract = obj.Abstract;
        var title = obj.Articletitle;
        if (title != "") {
            body = "<p class='mcDetailTitle' style='text-align:center;margin:2px;font-weight:bold;'>" + title + "</p>";
        }
        if (screen) {
            var _detailItems = McDetailItemsScreen;
        }
        else {
            var _detailItems = McDetailItems;
        }
        if (_detailItems != null) {
            var tip = null;
            if (detailParms.Index != null) {//有的情况加上DetailTip
                tip = _detailItems[detailParms.Index].DetailTip;
                if (tip != "") {
                    Abstract = tip + "<br/>" + Abstract; //加上tip
                }
            }
        }
        var content = body + McPASS.McFormatParagraph(Abstract);
        if (content == "") {
            content = "暂无相关资料!";
        }

        McPASS.McDetailContent(detailParms.btnId, detailParms.divId, content, n, screen);
    }
    catch (e) {
        McPASS.McDetailContent(detailParms.btnId, detailParms.divId, McPASS.ErrorTip(), n, screen)
    }
};

McPASS.McDetailContent = function (btnId, divId, content, n, screen) {
    var body = "<div class='mc_common_loading' style='background-image:url(" + mcWebServiceUrl + "/PassImage/loading.gif);background-repeat:no-repeat;'></div>";
    if (content != "") {
        body = content;
    }
    //查询跟审查调用不同的样式  
    var divtype = "<div>";
    if (screen == undefined) {
        screen = 0;
    }
    if (screen == 0) {
        divtype = "<div style='height:30px;background-image:url(" + mcWebServiceUrl + "/PassImage/mcdetailtitle_ref.gif);background-repeat:repeat-x;'>";
    }
    else if (screen == 1) {       
        divtype = "<div style='height:30px;background-image:url(" + mcWebServiceUrl + "/PassImage/mcdetailtitle_screen.gif);background-repeat:repeat-x;'>";
        var linkText = McDetailItemsScreen[n].LinkText;
        var linkParams = McDetailItemsScreen[n].LinkParams;
        var type = McDetailItemsScreen[n].DetailType;

        body += "<div style='text-align:right;margin-right:20px;'><a class='mcBtnShowDetail' style='float:right;font-weight:bolder;' onclick=\"McPASS.McDoLinkdetail('" + linkParams + "'," + type + "," + n + ",'" + divId + "')\">" + linkText + "</a></div><br/>"
             + "<div id='mcDivItemDetailRowMore_" + linkParams + "_" + n + "' style='width:95%;visibility:hidden;'></div>"; //不设置95%的话IE6下面哺乳审查看第二级详细就把滚动条撑到右边去了
    }     
    var html = [
        "<div class='mcDetail_div'>", divtype,
           "<a class='detail_header_close' style='background-image:url(" + mcWebServiceUrl + "/PassImage/McDetail_close_Normal.gif);background-repeat:no-repeat;' onmouseover=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/McDetail_close_hover.gif)';\" onmouseout=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/McDetail_close_Normal.gif)';\" onclick=\"McPASS.McFoldItemDetailRowMoreDiv('", btnId, "','", divId, "')\"></a></div>",
           "<div id='mcDivItemDetailRowMoreBody", divId, "' class='mcDivDetailRowMoreBody' style='height:auto;overflow:hidden;overflow-y:auto;margin-left:10px;'>", body, "</div>",
        "</div>"
        ];

    McPASS.fn.byId(divId).innerHTML = html.join('');
    //----------------------------------------------------
    var div = McPASS.fn.byId(divId).style;
    if (div) {
        if (div.visibility.toLowerCase() == "visible") {
            div.display = "none";
            div.visibility = "hidden";
        }
        else {
            div.display = "";
            div.visibility = "visible";
        }
    }

    //放在后面，等div渲染后才能找到该div
    if (content != "") {
        var clientHeight = McPASS.fn.byId("mcDivItemDetailRowMoreBody" + divId).clientHeight; //必须要开始设置成auto，才能获取到值，否则一直获取的都是设置的150       
        if (clientHeight < 150) {
            McPASS.fn.byId("mcDivItemDetailRowMoreBody" + divId).style.height = "auto";
        }
        else {
            McPASS.fn.byId("mcDivItemDetailRowMoreBody" + divId).style.height = "150px";
        }
        //ie6获取clientHeight=0，所以这样设置，，，（如果可以固定div高度为150就不用这样费事了）
        if (content.length > 300) {
            McPASS.fn.byId("mcDivItemDetailRowMoreBody" + divId).style.height = "150px";
            //ie6 360这个地方 有的不会自动出滚动条，实在不行 把overflow-y:visible写到样式里面去，给改变他的class来处理  
        }
    }
    //----------------------------------------------------
};

//相同药理分类药品信息
McPASS.McDoLinkdetail = function (params, type, n, divId) {
    var div = McPASS.fn.byId("mcDivItemDetailRowMore_" + params + "_" + n);
    if (div) {
        //如果审查有《相同药理分类药品信息》则默认给定下高度，否则明细框会一直撑开
        McPASS.fn.byId("mcDivItemDetailRowMoreBody" + divId).style.height = "150px";    
        if (div.style.visibility.toLowerCase() == "visible") {
            div.style.display = "none";
            div.style.visibility = "hidden";
        }
        else {
            div.style.display = "";
            div.style.visibility = "visible";
            //查服务端
            var _obj = {
                PassClient: MCPASSclient,
                DetailParams: {
                    DetailParams: params, //以字符的形式传
                    DetailType: type
                }
            };
            MC_global_Detail_HISdata = McPASS.toMcJSONString(_obj);
            var obj = {
                PASSFuncName: "Mc_DoDetail",
                PASSParam: MC_global_Detail_HISdata
            };
            var data = McPASS.toMcJSONString(obj);
            McAjax.send(data);
            McAjax.onmessage = function (result) {
                if (result.indexOf("[ERROR]") < 0) {
                    MC_global_Detail_PASSdata = result;
                    McPASS.McAnalyzeLinkDetailResult(params, n);
                }
                else {
                    McPASS.fn.byId("mcDivItemDetailRowMore_" + params + "_" + n).innerHTML = McPASS.ErrorTip();
                }
            };
        }
    }
};
McPASS.McAnalyzeLinkDetailResult = function (params, n) {
    try {
        var body = "";
        var res = McPASS.fn.EvalJson(MC_global_Detail_PASSdata);
        var delimiter = res.Delimiter;
        var sub = res.SubDelimiter;
        var obj = res.DetailResults;
        var html = [];
        var every = [];
        html.push(["<table class='mcMainText' border='0' cellspacing='0' cellpadding='0' style='margin-left:10px;'>"].join(''));
        var node = null;

        for (var i = 0; i < obj.length; i++) {
            node = obj[i];
            var rets = [];
            var arrays = node.DetailDesc.split(delimiter);
            if (i > 0) {
                arrays = node.Record.split(delimiter);
            }
            for (var m = 0; m < arrays.length; m++) {
                var line = arrays[m];
                var arrayval = line.split(sub);
                rets.push(arrayval);
            }
            var arrayTitle = node.Record.split(delimiter);
            var len = arrayTitle.length;
            //var pwidth = Math.round(10000 / len) / 100; //Math.round(num*100)/100;这样处理以满足2位小数  之前是8900现在改成10000，因为之前上面没设置95%
            var pwidth = Math.round(8000 / (len - 1)) / 100;
            if (i == 0) {
                html.push(["<tr><td colspan='", len, "'><b>", rets[0][1], "</b>", rets[1][1], "</td></tr><tr>"].join(''));
                for (var j = 0; j < len; j++) {
                    if (j < len - 1) {
                        html.push(["<td class='mcTdTitle' width='" + pwidth + "%' align='center'><b>", arrayTitle[j], "</b></td>"].join(''));
                    }
                    else {
                        html.push(["<td class='mcTdTitle' width='20%' align='center' style='border-right:solid 1px #D4D6E2'><b>", arrayTitle[j], "</b>&nbsp;&nbsp;</td>"].join(''));
                        html.push(["<td width='1px'>&nbsp;&nbsp;</td>"].join(''));
                    }
                }
                html.push(["</tr>"].join(''));
            }
            else {
                every.push(["<tr>"].join(''));
                for (var k = 0; k < len; k++) {
                    if (k < len - 1) {
                        every.push(["<td class='mcTdItem' width='" + pwidth + "%' align='center'>&nbsp;", rets[k][1], "</td>"].join(''));
                    }
                    else {
                        every.push(["<td class='mcTdItem' width='20%' align='center' style='border-right:solid 1px #D4D6E2'>&nbsp;", rets[k][1], "</td>"].join(''));
                        every.push(["<td width='1px'>&nbsp;&nbsp;</td>"].join(''));
                    }
                }
                every.push(["</tr>"].join(''));
            }
        }
        html.push([every.join(''), "</table><br/>"].join(''));
        //向该div里面追加内容
        McPASS.fn.byId("mcDivItemDetailRowMore_" + params + "_" + n).innerHTML = html.join("");
    }
    catch (e) {
        McPASS.fn.byId("mcDivItemDetailRowMore_" + params + "_" + n).innerHTML = McPASS.ErrorTip();
    }
};﻿var mcLastReferenceCode = ""; //上一次查询的ReferenceCode
var mcLastReferenceType = ""; //上一次查询的ReferenceType
var mcstrMoreList = ""; //说明书缓存的更多文章列表,包括点后更改状态
var mcstrManualUser = ""; //说明书自定义内容

//处理图片名 ReferenceCode 由于查询更多说明书时的ReferenceCode跟第一次查的不一样，而查询相互作用时的ReferenceCode跟第一次查的一样，所以得还回来
var mc_really_code = "";
var mc_really_type = "";

McPASS.McFuncRefChange = function (type, mode) {

    //自定义和系统的第一篇都要显示出来   
    //如果还是点的相同的按钮，则不再查询，把按钮给灰掉的效果,如果用户单独写按钮调用的话，则允许
   
    if (!McPASS.Alone) {
        if (type == mcLastReferenceType && MC_global_queryDrug.ReferenceCode == mcLastReferenceCode) {
            return;
        }
    }

    //换新药之后，得重新设置，否则图片显示不出来，图片的名字需要用到这2个变量
    mc_really_code = "";
    mc_really_type = "";

    //因为现在只有说明书，所以可以不考虑这个问题，，如果有其他的比如药典就要判断type了，但是自定义和非自定义的type不一样
    if (MC_global_queryDrug.ReferenceCode != mcLastReferenceCode || parseInt(type / 10) != parseInt(mcLastReferenceType / 10)) {
        mcstrMoreList = "";
        mcstrManualUser = "";
    }
    if (mcstrManualUser != "") {
        var div = McPASS.fn.byId("mcrefContentDiv");
        if (div) {
            McPASS.fn.byId("mcrefContentDiv").innerHTML = mcstrManualUser;
            return;
        }
    }
    //mc_is_manual_pass = null; ////更多PASS文章列表标记
    MC_global_queryDrug.IsDrug = 1;
    MC_global_queryDrug.ReferenceType = type;
    MC_global_queryDrug.RefMode = mode || 1;

    mcLastReferenceCode = MC_global_queryDrug.ReferenceCode;
    mcLastReferenceType = MC_global_queryDrug.ReferenceType;

    var mc_loading = "<div class='mcloading' style='height:" + (mcWindowHeight - 110) + "px;background-image:url(" + mcWebServiceUrl + "/PassImage/loading.gif);background-repeat:no-repeat;'></div>";
    if (MC_global_queryDrug.RefMode == 1) {//相当于清掉之前查询的内容，但是如果是先查询的是USER，再查询PASS，得保存USER的数据
        McPASS.fn.byId("mcRefMainLeftDivId").innerHTML = mc_loading;
    }
    if (McPASS.fn.byId("Mc_pass_Div") == null) {//如果USER数据已经有了，那PASS数据只需要加就行 
        McPASS.fn.byId("mcRefMainLeftDivId").innerHTML = mc_loading;
    }
    McPASS.McFuncCallQuery();
};

McPASS.McFuncCallQuery = function () {
    try {
        McPASS.MCPASSclient_Verify();//Verify
        McPASS.MC_global_queryDrug_Verify();//Verify
        var _obj = {
            PassClient: MCPASSclient,
            ReferenceParam: MC_global_queryDrug
        };
        MC_global_ref_HISdata = McPASS.toMcJSONString(_obj);
        McPASS.Debug(IsDebugDrugInfo, MC_global_ref_HISdata, DebugShowDiv_Drug); //debug  
        var obj = {
            PASSFuncName: "Mc_DoQuery",
            PASSParam: MC_global_ref_HISdata
        };
        var data = McPASS.toMcJSONString(obj);
        McAjax.send(data);
        McAjax.onmessage = function (result) {
            if (result.indexOf("[ERROR]") < 0) {
                MC_global_ref_PASSdata = result;
                McPASS.Debug(IsDebugResult, MC_global_ref_PASSdata, DebugShowDiv_Drug); //debug                
                var res = McPASS.fn.EvalJson(MC_global_ref_PASSdata);
                if (res == null) {
                    McPASS.Mc_Empty();
                    return;
                }
                var obj = res.ReferenceResults;
                if (typeof obj != 'undefined') {
                    McPASS.Mc_QueryHTML(res);
                }
                else {
                    McPASS.Mc_Empty();
                }
            }
            //            else {
            //                McPASS.fn.byId("mcRefMainLeftDivId").innerHTML = "服务器返回错误:" + result;
            //设置个变量就在这给干掉 让pass失效
            //            }
        };
    }
    catch (e) { }
};


//组织查询结果
McPASS.Mc_QueryHTML = function (res) {
    var mcContent = "";
    if (res != undefined && res != null && res != "") {
        MC_global_ref_PASSdata = McPASS.toMcJSONString(res); //转化成字符串，后面每个模块进去要这个全局变量来组织
        //content
        var displayMode = res.DisplayMode;
        var mode = McPASS.SysMenu.DisplayMode;
        switch (displayMode) {
            //其他查询模块现在全部屏蔽       
            //case mode.SP: mcContent = McPASS.Mc_SP(); break;       
            //case mode.Inter: mcContent = McPASS.Mc_Inter(); break;       
            //case mode.IV: mcContent = McPASS.Mc_IV(); break;       
            //case mode.ADR: mcContent = McPASS.Mc_ADR(); break;       
            //case mode.Level: mcContent = McPASS.Mc_Level(); break;        
            //case mode.Wholetext: mcContent = McPASS.Mc_Indication(); break;  
            case mode.PICPRPECHP: McPASS.Mc_Manual(); return;
            default: mcContent = ""; break;
        }
    } 
    //end content
    if (mcContent == "") {
        //McPASS.Mc_Empty(); // mark by liuchunmei 2017-2-16 返回empty的错误提示有误
        MC_global_ref_PASSdata = "";
        McPASS.Mc_Manual();
        return;
    }
    var html = [];
    html.push("<div id='mcreferBodyDiv' style='height:", mcWindowHeight - 138, "px;overflow-y:auto;'>"); //下面正文内容 -135     
    html.push("<div align='left' class='mcScreenBodyDiv' style='background:url(" + mcWebServiceUrl + "/PassImage/mcbackground.gif) left repeat-y;'>");
    //这个地方不知如何能设置100%，在ie6下兼容不了dtd和!dtd的情况，总会支出去一点   
    html.push("<div id='Mc_user_Div' style='overflow:hidden;_width:98%;'></div>"); //user iv自定义信息虽然是显示在这里面了，但是样式还是有点问题，并没把div扩高  

    var mcshow = McPASS.Mc_RefShow();
    var refmode = MC_global_queryDrug.RefMode;
    if (mcshow == 3 && refmode == 1) {//ALL  
        html.push("<div style='margin:1px;'><a id='Mc_pass_click' onclick=\"McPASS.McFoldItemDetailDiv('Mc_pass_click','Mc_pass_Div');McPASS.Alone=0;McPASS.McFuncRefChange(" + MC_global_queryDrug.ReferenceType + ",2);\">");
        html.push("<img src='" + mcWebServiceUrl + "/PassImage/mclinkPASSNormal.gif' onmouseout=\"this.src='" + mcWebServiceUrl + "/PassImage/mclinkPASSNormal.gif';\" onmouseover=\"this.src='" + mcWebServiceUrl + "/PassImage/mclinkPASSActive.gif';\" align='right' style='_cursor:pointer;cursor:hand;cursor:pointer;' title='PASS信息' /></a></div>");
    }
    html.push("<div id='Mc_pass_Div'></div>"); //pass  
    html.push("</div>"); //end 线
    html.push("</div>"); //end mcreferBodyDiv

    if (McPASS.fn.byId("Mc_pass_Div") == null || refmode == 1) {//pass的得重新指定标题，否则会变成自定义的标题了  RefMode   //1pass 2user
        McPASS.fn.byId("mcRefMainLeftDivId").innerHTML = html.join("");
    }
    if (refmode == 1) {
        McPASS.fn.byId("Mc_user_Div").innerHTML = mcContent;
        McPASS.fn.byId("Mc_pass_Div").innerHTML = "";
    }
    else if (refmode == 2) {
        McPASS.fn.byId("Mc_pass_Div").innerHTML = mcTitle + mcContent;
    }
};


/*返回查询结果显示情况：
0都没有  1只有USER 2只有PASS  3都有
*/
McPASS.Mc_RefShow = function () {
    var show = 0; //0 1 2 3
    var len = MCrefModuleArray.length;
    var type = MC_global_queryDrug.ReferenceType;
    for (var i = 0; i < len; i++) {
        var id = MCrefModuleArray[i].ModuleID;
        if (id == type) {
            show = McrefTipArray[i];
            break;
        }
    }
    return show;
};
/**********************************************
标题-换行-内容 这种格式的解析，如：
【标题】
1.内容内容内容
2.内容内容内容
***********************************************/
McPASS.McFormatParagraph = function (parag) {
    return parag;
    //下面全作废
    /*
    if (parag.length <= 0) { return ""; }
    parag = parag.replace(/\n\r/g, "\r\n");
    var array = parag.split("\r\n");
    var ret = [];
    for (var i = 0; i < array.length; i++) {
    var obj = array[i];
    if (obj.indexOf("【") >= 0) {
    if (i == 0) {
    //处理特殊字符 空格，否则缩进不一样
    obj = "<p class='mcDetailTitle'>" + array[0].substring(obj.indexOf("【"), array[0].indexOf("】") + 1).replace(/ /g, "&nbsp;") + "</p>";
    }
    else {
    obj = ("<p class='mcDetailTitle'>" + obj.replace(/ /g, "&nbsp;") + "</p>");
    }
    }
    else {
    //obj.replace(/\s+/g, "&nbsp;");//多个空格的处理
    //obj = ("<p class='mcDetailText'>" + obj.replace(/ /g, "&nbsp;") + "</p>");
    //obj = ("<p class='mcDetailText'>" + obj + "</p>"); //由于数据内容正文前面有空格，测试认为是bug，跟刘哥商量后决定处理掉空格，改于2014-02-08  
    //obj = ("<p class='mcDetailText'>" + obj.replace(/(^\s*)/g, "").replace(/ /g, "&nbsp;") + "</p>"); // 去掉左空格 然后再转化其他空格为nbsp
    obj = ("<p class='mcDetailText'>" + obj.replace(/ /g, "&nbsp;") + "</p>"); // 不能去掉左空格 然后再转化其他空格为nbsp 2014-10-13           
    }
    ret.push(obj);
    }
    return ret.join("");
    */
};
McPASS.Mc_Empty = function () {
    var ref = MC_global_queryDrug.ReferenceType;
    var title = "";
    var len = MCrefModuleArray.length;
    for (var i = 0; i < len; i++) {
        var id = MCrefModuleArray[i].ModuleID;
        var name = MCrefModuleArray[i].ModuleName;
        if (id == ref) {
            title = "【" + name + "】";
            break;
        }
    }
    var div = McPASS.fn.byId("mcRefMainLeftDivId");
    if (div) div.innerHTML = "<div class='mcloading' style='height:" + (mcWindowHeight - 115) + "px;'>" + McPASS.ErrorTip() + "</div>"; //暂无该药品" + title + "相关查询数据
};
McPASS.McFoldItemDetailDiv = function (imgId, divId) {
    var img = McPASS.fn.byId(imgId);
    var div = McPASS.fn.byId(divId).style;
    if (img && div) {
        if (div.visibility.toLowerCase() == "visible") {
            div.display = "none";
            div.visibility = "hidden";
            img.src = mcWebServiceUrl + "/PassImage/mcexpand.gif";
            img.title = "展开";
        }
        else {
            div.display = "";
            div.visibility = "visible";
            img.src = mcWebServiceUrl + "/PassImage/mcunexpand.gif";
            img.title = "折叠";
        }
    }
};
/*用药指导单
 * add by yaozhao 2016-12-26 17:16
*/
McPASS.QueryPG = function (type) {
    try {
        var _obj = {
            PassClient: MCPASSclient,
            Patient: MCpatientInfo,
            ScreenMedCondList: { ScreenMedConds: McMedCondArray },
            ScreenDrugList: { ScreenDrugs: McDrugsArray }
        };
        MC_global_ref_HISdata = McPASS.toMcJSONString(_obj);
        var obj = {
            PASSFuncName: "Mc_DoQueryPG",
            PASSParam: MC_global_ref_HISdata
        };
        var data = McPASS.toMcJSONString(obj);
        McAjax.send(data);
        McAjax.onmessage = function (result) {
            if (result.indexOf("[ERROR]") < 0) {
                MC_global_ref_PASSdata = result;
                McPASS.Debug(IsDebugResult, MC_global_ref_PASSdata, DebugShowDiv_Drug); //debug                
                var res = McPASS.fn.EvalJson(MC_global_ref_PASSdata);
                if (res == null) {
                    McPASS.Mc_Empty();
                    return;
                }
                var win = new McPASS.McClsWindow({});
                win.PGWindow();
                var content = res.ReferenceTip;
                if (typeof content != 'undefined') {
                    if (content != "") {
                        content = content.replace(new RegExp("src=\"PassImage/", 'g'), "src=\"" + mcWebServiceUrl + "/PassImage/");
                        content = content.replace(new RegExp("PassImage/mcpgline.gif", 'g'), mcWebServiceUrl + "/PassImage/mcpgline.gif");
                        content = content.replace(new RegExp("McPASS.McShowPrintPG"), "McPASS.McPrintOpenPGWindow");
                        McPASS.fn.byId("mcDivWindowBody").innerHTML = content;
                    }
                    else {
                        McPASS.fn.byId("mcDivWindowBody").innerHTML = "<div class='mcloading'>对不起，您当前使用的数据库版本暂无用药指导单资料！</div>";
                    }
                }
                else {
                    McPASS.Mc_Empty();
                }
            }
        };
    }
    catch (e) {
        //alert("error from RightMenuTip:" + e.ToString());
    }    
};

/*
 * add by liuchunmei 2017-05-23 添加医生端查询平台调用接口 
*/
McPASS.OpenRefPlatform = function (type) {
    try {
        var dateNow = new Date();
        var year = dateNow.getFullYear().toString();
        var month = (dateNow.getMonth() + 1).toString();
        if (dateNow.getMonth() + 1 < 10) month = "0" + month;
        var day = dateNow.getDate().toString();
        if (dateNow.getDate() < 10) day = "0" + day;
        var hours = dateNow.getHours().toString();
        if (dateNow.getHours() < 10) hours = "0" + hours;
        var minutes = dateNow.getMinutes().toString();
        if (dateNow.getMinutes() < 10) minutes = "0" + minutes;
        var seconds = dateNow.getSeconds().toString();
        if (dateNow.getSeconds() < 10) seconds = "0" + seconds;
        var timestamp = year + "-" + month + "-" + day + " " + hours + ":" + minutes + ":" + seconds;
        var _obj = {
            hiscode: MCPASSclient.HospID,
            doctorcode: MCPASSclient.UserID,
            timestamp: timestamp
        };
        var jsonstr = McPASS.toMcJSONString(_obj);
        jsonstr = Mc_Base64.encode(jsonstr);
        var url = RefPlatformURL + jsonstr;
        window.open(url);
    }
    catch (e) {
        alert("error from OpenRefPlatform:" + e.ToString());
    }
};

/*推荐剂量 警示结果
 * add by lcm 2017-11-06 19:34 
*/
function MDC_ShowRecomDosWarn(pstrScreenResultString) {    
    if (!pstrScreenResultString || pstrScreenResultString == "") {
        return;
    }
    var results = McPASS.fn.EvalJson(pstrScreenResultString);
    if (!results || !results.ScreenResultDrugs) {
        return;
    }
    var res = results.ScreenResultDrugs;
    if (!res || res.length <= 0 || !res[0] || !res[0].ScreenResults || res[0].ScreenResults.length <= 0)
    {
        return;
    }
    if (McPASS.fn.byId("mcDivWindowBody")) {
        McPASS.McCloseWindow();
    }
    var drugName = res[0].DrugName;
    var screenResults = res[0].ScreenResults;
    var html = [];
    html.push("<div style='margin:5px;float:none;width:98%;overflow:hidden;overflow-y:auto;'>"); //100%出不来滚动条 
    var _slcode = 0; //如果蓝灯，则不提示
    for (var j = 0; j < screenResults.length; j++) {
        var obj = screenResults[j];
        var moduleName = obj.ModuleName;
        var severity = obj.Severity;
        var warning = obj.Warning;
        var slcode = obj.Slcode;
        _slcode = slcode;

        //处理自定义的情况                
        if (obj.ModuleItems.indexOf("CUSTOMRULE") >= 0) {//USERPeriOpr2 屏蔽标记,,还有多个标记USERPeriOpr0.5之类的
            mcUserLabel = "<img src='" + mcWebServiceUrl + "/PassImage/mcCustom.gif' title='自由自定义审查结果' align='absMiddle'/>";
        } else if (obj.ModuleItems.indexOf("USER") >= 0) {//USERPeriOpr2 屏蔽标记,,还有多个标记USERPeriOpr0.5之类的
            mcUserLabel = "<img src='" + mcWebServiceUrl + "/PassImage/mcUSER.gif' title='用户自定义审查结果' align='absMiddle'/>";
        }
        //*width (ie7) _width(ie6)
        html.push("<div align='left' class='mcDivPanel' style='margin-top:5px;visibility:visible;*width:98%;_width:98%;'>"); //float:none;
        html.push("<img src='" + mcWebServiceUrl + "/PassImage/", slcode, ".jpg' border='0' align='center' style='margin-top:-2px;'/>&nbsp;");
        html.push("<span class='mcTextSeverity", slcode, "'>", severity, "</span>&nbsp;&nbsp;&nbsp;");
        html.push("<span class='mcWarningTitle'>", moduleName, "</span><br>");
        html.push("<p class='mcWarningMain'>&nbsp;", warning, "</p>");
        html.push("</div>");
    }
    html.push("</div>");
    //if (_slcode > 0 && res[i].IsNewWarning == 1) {
        var win = new McPASS.McClsWindow({});
        win.RecomDoseWindow();
        var content = html.join('');
        var strHtml = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" width=\"100%\" height=\"100%\">"
        strHtml += "  <tr valign=\"top\" height=\"40px;\"><td class=\"mcDrugTitle\" style=\"border-bottom:none;\">" + drugName + "</td></tr>";
        strHtml += "  <tr valign=\"top\">";
        strHtml += "    <td>";
        strHtml += "      <div align=\"left\" class=\"mcBodyDiv\" style=\"height:220px;visibility:visible; float:none; \">";
        strHtml += content;
        strHtml += "      </div></td></tr></table>";

        McPASS.fn.byId("mcDivWindowBody").innerHTML = strHtml;
        McPASS.fn.byId("mcDivscreendTitle").innerHTML = "禁用警示";
    //}
};

/*推荐剂量
 * add by lxl 2017-08-09 18:03
*/
McPASS.QueryRecomDose = function (type) {
    try {
        /*********************************************/
        //新接口，组合方式
        var jsonInfos = McPASS.McScreenJsonAddAPI();
        /*********************************************/

        var _obj = {
            PassClient: MCPASSclient,
            Patient: MCpatientInfo,
            ScreenAllergenList: { ScreenAllergens: McAllergenArray },
            ScreenMedCondList: { ScreenMedConds: McMedCondArray },
            ScreenDrugList: { ScreenDrugs: McDrugsArray },
            InputJsonInfoList: { InputJsonInfos: jsonInfos }
        };
        MC_global_ref_HISdata = McPASS.toMcJSONString(_obj);
        var obj = {
            PASSFuncName: "Mc_DoQueryRecomDose",
            PASSParam: MC_global_ref_HISdata
        };
        var data = McPASS.toMcJSONString(obj);
        McAjax.send(data);
        McAjax.onmessage = function (result) {
            if (result && result.indexOf("[ERROR]") < 0) {
                MC_global_ref_PASSdata = result;
                McPASS.Debug(IsDebugResult, MC_global_ref_PASSdata, DebugShowDiv_Drug); //debug                
                var res = McPASS.fn.EvalJson(MC_global_ref_PASSdata);
                if (res == null) {
                    return;
                }
                if (McPASS.fn.byId("mcDivWindowBody")) {
                    McPASS.McCloseWindow();
                }                
                if (res.ScreenResultString && res.ScreenResultString != '') {// 有部分模块的黑灯警示结果
                    MDC_ShowRecomDosWarn(res.ScreenResultString);
                } else {
                    if (!res.ReferenceTip || res.ReferenceTip == "") {
                        return;
                    }
                    var win = new McPASS.McClsWindow({});
                    win.RecomDoseWindow();
                    var drugName = res.Title;
                    var tip = res.ReferenceTip;

                    var strHtml = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" width=\"100%\" height=\"100%\">"
                    strHtml += "  <tr valign=\"top\" height=\"40px;\"><td class=\"mcDrugTitle\" style=\"border-bottom:none;\">" + drugName + "</td></tr>";
                    strHtml += "  <tr valign=\"top\">";
                    strHtml += "    <td>";
                    strHtml += "      <div align=\"left\" class=\"mcBodyDiv\" style=\"height:210px;visibility:visible; float:none; \">";
                    strHtml += tip;
                    strHtml += "      </div></td></tr></table>";

                    McPASS.fn.byId("mcDivWindowBody").innerHTML = strHtml;
                    McPASS.fn.byId("mcDivscreendTitle").innerHTML = "推荐剂量";
                }
                //setTimeout('McPASS.McCloseWindow(0)', MC_Close_Time*1000);
            }
        };
    }
    catch (e) {
        //alert("error from RightMenuTip:" + e.ToString());
    }
};﻿// add by liuchunmei 2016-12-23 添加传入参数，告知是否是旧版（<201703)的审查结果
McPASS.McFoldResonRowDiv = function (btnID, divID, i, j, isold) {
    var btn = McPASS.fn.byId(btnID);
    var div = McPASS.fn.byId(divID);
    if (btn && div) {
        if (div.style.visibility.toLowerCase() == "visible") {
            div.style.display = "none";
            div.style.visibility = "hidden";
        }
        else {
            div.style.display = "";
            div.style.visibility = "visible";
            btn.style.color = "#9c4612";
            if (MC_current_Detail_divId != divID) {//通过id来判断是否 显示 和隐藏
                var _div = McPASS.fn.byId(MC_current_Detail_divId);
                var _btn = McPASS.fn.byId(MC_current_Detail_btnId);
                if (_btn != null && _div != null) {
                    _div.style.display = "none";
                    _div.style.visibility = "hidden";
                    //加上这一行可以处理点“理由”改变不了“详细信息”字的问题
                    if (_btn.innerText == "关闭") { _btn.innerText = "详细"; _btn.innerHTML = "详细"; }
                }
            }
            if (McPASS.fn.byId(divID).innerHTML.length <= 0) {
                McPASS.McDoGetReason(i, j, function (content) {
                    McPASS.McResonContent(btnID, divID, i, j, content, isold);
                });
            }
            //div.scrollIntoView(); //会导致嵌套的界面乱跳
            MC_current_Detail_divId = divID;
            MC_current_Detail_btnId = btnID;
        }
    }
};

// 刷新使用理由
McPASS.McResonContent = function (btnId, divId, i, j, content, isold) {
    var html = [
        "<div class='mcDetail_div'>",
           "<div style='height:30px;background-image:url(" + mcWebServiceUrl + "/PassImage/mcdetailtitle_reason.gif);background-repeat:repeat-x;'>",
           "<a class='detail_header_close' hidefocus style='background-image:url(" + mcWebServiceUrl + "/PassImage/McDetail_close_Normal.gif);background-repeat:no-repeat;' onmouseover=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/McDetail_close_hover.gif)';\" onmouseout=\"this.style.backgroundImage='url(" + mcWebServiceUrl + "/PassImage/McDetail_close_Normal.gif)';\" onclick=\"McPASS.McFoldItemDetailRowMoreDiv('", btnId, "','", divId, "')\"></a></div>",
           "<div style='height:150px;overflow:hidden;overflow-y:auto;'>",
           "<div><textarea id='mcWriteUserReson", i, j, "' maxlength='500' style='height:110px;width:99%;border:1px solid #ccc;'>", content, "</textarea>",
           "<div style='float:right;padding:3px;'><img src='" + mcWebServiceUrl + "/PassImage/mcOK.gif' onmouseover=\"this.src='" + mcWebServiceUrl + "/PassImage/mcOKon.gif';\" onmouseout=\"this.src='" + mcWebServiceUrl + "/PassImage/mcOK.gif';\" style='_cursor:pointer;cursor:hand;cursor:pointer;' title='保存理由' onclick=\"McPASS.Mcdoreson('", btnId, "','", divId, "','", i, "','", j, "','", isold, "' )\" /></div>",

           "</div>",
        "</div>"];
    McPASS.fn.byId(divId).innerHTML = html.join('');
};

// alter by liuchunmei 2016-12-22 全面调整理由实现方式
McPASS.Mcdoreson = function (btnID, divID, i, j, isold) {
    if (isold > 0) {
        McPASS.McdoresonOld(btnID, divID, i, j);
    } else {
        McPASS.McDoSaveReason(btnID, divID, i, j);
    }
};

// 兼容用药理由
McPASS.McdoresonOld = function (btnID, divID, i, j) {
    var mcreason = McPASS.fn.byId("mcWriteUserReson" + i + j).innerText;
    if (mcreason == "") {
        mcreason = McPASS.fn.byId("mcWriteUserReson" + i + j).value;
    }
    if (typeof mcreason === "undefined") {
        mcreason = McPASS.fn.byId("mcWriteUserReson" + i + j).value; //For firefox             
    }
    if (typeof mcreason === "undefined") {
        mcreason = McPASS.fn.byId("mcWriteUserReson" + i + j).textContent; //For firefox        
    }
    mcreason = mcreason.replace(/^\s+|\s+$/g, "");//trim
    var btn = McPASS.fn.byId(btnID);
    var div = McPASS.fn.byId(divID);

    //没填写理由，还是得关闭
    //    if (mcreason == "") {
    //        if (div && btn) {
    //            if (div.style.visibility.toLowerCase() == "visible") {
    //                div.style.display = "none";
    //                div.style.visibility = "hidden";
    //                btn.style.color = "#808080"; //重置颜色
    //            }
    //        }
    //        return;
    //    }

    //理由
    var useReson = new Params_MC_UseReason_In();
    useReson.Hiscode = MCPASSclient.HospID;
    useReson.Hisname = MCPASSclient.HospName;

    useReson.PatStatus = MCpatientInfo.PatStatus;
    useReson.InHospNo = MCpatientInfo.InHospNo;
    useReson.VisitCode = MCpatientInfo.VisitCode;
    useReson.PatCode = MCpatientInfo.PatCode;
    useReson.reason = mcreason; //reason
   
    //alter by liuchunmei 2016-12-23 将index改为i后，获取结果的方式变动 先要根据i获取结果节点
    var drugreslst = MC_global_Screen_PASSdata.ScreenResultDrugs;
    if (drugreslst == undefined || drugreslst == null) return;
    var drugres = drugreslst[i];
    if (drugres == undefined || drugres == null) return;
    var detail = drugres.ScreenResults[j];
    if (detail == undefined || detail == null) return;

    var _must = 0; //是否必须要填写 
    useReson.ModuleID = detail.ModuleID;
    useReson.ModuleName = detail.ModuleName;
    useReson.Slcode = detail.Slcode;
    useReson.Severity = detail.Severity;
    useReson.Warning = detail.Warning;
    useReson.ModuleItems = detail.ModuleItems;
    useReson.OtherInfo = detail.OtherInfo;
    _must = detail.DetailReason; //赋值，如果是1则不运行关闭

    for (var m = 0; m < McDrugsArray.length; m++) {
        if (McDrugsArray[m].Index == drugres.DrugIndex) {
            useReson.RecipNo = McDrugsArray[m].RecipNo;
            useReson.OrderDeptCode = McDrugsArray[m].DeptCode;
            useReson.OrderDoctorCode = McDrugsArray[m].DoctorCode;
            useReson.OrderStartTime = McDrugsArray[m].StartTime;
            useReson.OrderEndTime = McDrugsArray[m].EndTime;
            useReson.OrderExecuteTime = McDrugsArray[m].ExecuteTime;
            useReson.DrugUniqueCode = McDrugsArray[m].DrugUniqueCode;
            useReson.DoseUnit = McDrugsArray[m].DoseUnit;
            useReson.DosePerTime = McDrugsArray[m].DosePerTime;
            useReson.Frequency = McDrugsArray[m].Frequency;
            useReson.IsTempDrug = McDrugsArray[m].IsTempDrug;
            break;
        }
    }

    //必填的情况就必须填写
    if (mc_cancloseWindow == 0 && _must == "1") {
        if (mcreason == "") {
            var e = window.event || arguments.callee.caller.arguments[0];
            var tip = "带‘*’号的项，为必填项！";
            McPASS.AlertWindow(e, tip, 0);
            return;
        }
    }

    var o = {
        Execute: { "sExeClass": "DRUGUSEREASON", "sConStr": "" },
        ExecuteDrugUseReason: { ExecuteDrugUseReason: [useReson] }
    };
    var reson = McPASS.toMcJSONString(o);
    var obj = {
        PASSFuncName: "Mc_DoReason",
        PASSParam: reson
    };
    var data = McPASS.toMcJSONString(obj);
    McAjax.send(data);
    McAjax.onmessage = function (result) {
        if (result == "EXECUTE_OK") {
            //可能有多个，只有当其他的都已经填写完后才可以关闭  
            var len = McScreenReasonArray.length;
            for (var t = 0; t < len; t++) {
                var drug = McScreenReasonArray[t];
                if (drug) {
                    var index = drug[0];
                    var k = drug[1];
                    if (index == i && j == k) {
                        McScreenReasonArray[t] = null;
                        break;
                    }
                }
            }
            var counts = 0;
            for (var kk = 0; kk < len; kk++) {
                var _drug = McScreenReasonArray[kk];
                if (_drug) {
                    counts++;
                }
            }
            mc_cancloseWindow = 0;
            if (counts == 0) {
                mc_cancloseWindow = 1; //can close window 
            }
            //**********************end**********************************
            if (btn && div) {
                if (div.style.visibility.toLowerCase() == "visible") {
                    div.style.display = "none";
                    div.style.visibility = "hidden";
                    btn.innerHTML = "查看理由";
                    if (mcreason == "") {//如果是把理由删了，并保存后就要把显示的字还原回去
                        btn.innerHTML = "理由";
                    }
                }
            }
        }
        else {
            alert("提交理由到服务器失败!");
        }
    };
};

// add by liuchunmei 2017-07-12 根据传入的下标 获取用药理由键值
McPASS.McGetReasonKey = function (i, j) {
    var drugreslst = MC_global_Screen_PASSdata.ScreenResultDrugs;
    if (!drugreslst) return null;
    var drugres = drugreslst[i];
    if (!drugres) return null;
    var detail = drugres.ScreenResults[j];
    if (!detail) return null;
    if (!detail.DetailReason) return null;

    var objDetailReason = {
        MHisCode: 0,
        ModuleID: 0,
        CaseID: "",
        ReasonKey: "",
        Reason: ""
    };
    var DetailReason_array = detail.DetailReason.split("\r\n");
    for (var h = 0; h < DetailReason_array.length; h++) {
        var arrayItem = DetailReason_array[h].split("=");
        if (arrayItem == null || arrayItem.length < 2) continue;
        var key = arrayItem[0];
        var value = arrayItem[1];
        if (key == "MHisCode") {
            objDetailReason.MHisCode = parseInt(value, 10);
        } else if (key == "CaseID") {
            objDetailReason.CaseID = value;
        } else if (key == "ReasonKey") {
            objDetailReason.ReasonKey = value;
        } else if (key == "ModuleID") {
            objDetailReason.ModuleID = parseInt(value, 10);
        }
    }
    return objDetailReason;
};

// add by liuchunmei 2017-07-12 获取用药理由
McPASS.McDoGetReason = function (i, j, callback) {
    var objDetailReason = McPASS.McGetReasonKey(i, j);
    if (objDetailReason == null) return "";
    var useReason = {
        MHisCode: objDetailReason.MHisCode,
        CaseID: objDetailReason.CaseID,
        ModuleID: objDetailReason.ModuleID,
        ReasonKey: objDetailReason.ReasonKey,
        Reason: ""
    };
    var o = {
        Execute: { "sExeClass": "USEREASON", "sConStr": "GET" },
        ExecuteDrugUseReason: { ExecuteDrugUseReason: [] },
        ExecuteUseReason: { "ExecuteUseReason": [useReason] }
    };
    var reason = McPASS.toMcJSONString(o);
    var obj = {
        PASSFuncName: "Mc_DoReason",
        PASSParam: reason
    };
    var data = McPASS.toMcJSONString(obj);
    McAjax.send(data);
    McAjax.onmessage = function (result) {
        if (result.indexOf("[ERROR]") < 0) {
            var obj = McPASS.fn.EvalJson(result);
            callback(obj.Reason);
        }
        else {
            callback(McPASS.ErrorTip());
        }
    };
};

// add by lxl 2017-07-17 组织判断必填理由的时候传入的串
McPASS.McGetAllUseReasonKey = function () {
    if (MC_global_Screen_PASSdata.MustUseReason == 0) return null;
    var drugreslst = MC_global_Screen_PASSdata.ScreenResultDrugs;
    if (!drugreslst) return null;
    var objExecuteUseReason = [];
    for (var i = 0; i < drugreslst.length; i++) {
        var drugres = drugreslst[i];
        if (!drugres) continue;
        for (var j = 0; j < drugres.ScreenResults.length; j++) {
            var detail = drugres.ScreenResults[j];
            if (!detail) continue;
            var objDetailReason = {
                MHisCode: 0,
                ModuleID: 0,
                CaseID: "",
                ReasonKey: "",
                Reason: ""
            };
            var DetailReason_array = detail.DetailReason.split("\r\n");
            for (var h = 0; h < DetailReason_array.length; h++) {
                var arrayItem = DetailReason_array[h].split("=");
                if (arrayItem == null || arrayItem.length < 2) continue;
                var key = arrayItem[0];
                var value = arrayItem[1];
                if (key == "MHisCode") {
                    objDetailReason.MHisCode = parseInt(value, 10);
                } else if (key == "CaseID") {
                    objDetailReason.CaseID = value;
                } else if (key == "ReasonKey") {
                    objDetailReason.ReasonKey = value;
                } else if (key == "ModuleID") {
                    objDetailReason.ModuleID = parseInt(value, 10);
                }
            }
            objExecuteUseReason.push(objDetailReason);
        }
    }
    var objExecuteSql = {
        Execute: { "sExeClass": "USEREASON", "sConStr": "GETALL" },
        ExecuteDrugUseReason: { ExecuteDrugUseReason: [] },
        ExecuteUseReason: { "ExecuteUseReason": objExecuteUseReason }
    };
    return objExecuteSql;
};

// add by lxl 2017-07-17 判断必填理由的时候是否填写所有理由
McPASS.McGetIsFullFillReason = function (callback) {
    // 1.没有审查结果，则不用填理由
    if (MC_global_Screen_PASSdata.ScreenResultDrugs.length <= 0 || MC_global_Screen_PASSdata.HighestSlcode <= 0) {
        callback(1);
        return;
    }
    // 2.系统配置为理由可以不用填
    if (MC_global_Screen_PASSdata.MustUseReason != 1) {
        callback(2);
        return;
    }
    // 3.是否已经全部填写完
    var objExecuteSql = McPASS.McGetAllUseReasonKey();
    if (objExecuteSql == null) {
        callback(1);
        return;
    }
    var reason = McPASS.toMcJSONString(objExecuteSql);
    var obj = {
        PASSFuncName: "Mc_DoReason",
        PASSParam: reason
    };
    var data = McPASS.toMcJSONString(obj);
    McAjax.send(data);
    McAjax.onmessage = function (result) {
        if (result.indexOf("[ERROR]") < 0) {
            var obj = McPASS.fn.EvalJson(result);
            if (obj.Reason == "NOTFULLFILL") {
                callback(0);
            }
            else {
                callback(3);
            }
        }
        else {
            callback(1);
        }
    };
};

// add by lxl 2017-07-18 同步判断必填理由的时候是否填写所有理由
McPASS.McGetIsFullFillReasonSync = function () {
    var get = new MC_client_request(1);//2:pr
    get.ClearParam();
    get.AddParam("psJSONStr", McPASS.toMcJSONString(McPASS.McGetAllUseReasonKey()));
    McGetIsFullFillReasonSyncCallBack(get.post("Mc_DoReason", null, McGetIsFullFillReasonSyncCallBack)); //同步模式
};

function McGetIsFullFillReasonSyncCallBack(callBackResult) {
    if (!callBackResult.error) {
        var result = callBackResult.value;
        if (result.indexOf("[ERROR]") < 0) {
            var obj = McPASS.fn.EvalJson(result);
            if (obj.Reason == "NOTFULLFILL") {
                mc_is_fullfill = 0;
            }
            else {
                mc_is_fullfill = 3;
            }
        }
        else {
            mc_is_fullfill = 1;
        }
    }
};

// add by liuchunmei 2016-12-23 组织新的理由方式
McPASS.McDoSaveReason = function (btnID, divID, i, j) {
    var mcreason = McPASS.fn.byId("mcWriteUserReson" + i + j).innerText;
    if (mcreason == "") {
        mcreason = McPASS.fn.byId("mcWriteUserReson" + i + j).value;
    }
    if (typeof mcreason === "undefined") {
        mcreason = McPASS.fn.byId("mcWriteUserReson" + i + j).value; //For firefox             
    }
    if (typeof mcreason === "undefined") {
        mcreason = McPASS.fn.byId("mcWriteUserReson" + i + j).textContent; //For firefox        
    }
    mcreason = mcreason.replace(/^\s+|\s+$/g, "");//trim
    var btn = McPASS.fn.byId(btnID);
    var div = McPASS.fn.byId(divID);    

    //理由
    var objDetailReason = McPASS.McGetReasonKey(i, j);
    if (objDetailReason == null) return;
    var useReason = {
        MHisCode: objDetailReason.MHisCode,
        ModuleID: objDetailReason.ModuleID,
        CaseID: objDetailReason.CaseID,
        ReasonKey: objDetailReason.ReasonKey,
        Reason: mcreason //reason
    };

    var o = {
        Execute: { "sExeClass": "USEREASON", "sConStr": "" },
        ExecuteDrugUseReason: { ExecuteDrugUseReason: [] },
        ExecuteUseReason: { "ExecuteUseReason": [useReason] }
    };
    var reason = McPASS.toMcJSONString(o);
    var obj = {
        PASSFuncName: "Mc_DoReason",
        PASSParam: reason
    };
    var data = McPASS.toMcJSONString(obj);
    McAjax.send(data);
    McAjax.onmessage = function (result) {
        if (result == "EXECUTE_OK") {
            if (div.style.visibility.toLowerCase() == "visible") {
                div.style.display = "none";
                div.style.visibility = "hidden";
            }
        }
        else {
            alert("提交理由到服务器失败!");
        }
    };
};﻿/*
药物专论 21  【没图片     只有一篇】
用药教育 31  【可能有多篇         】
中国药典 41  【可能有多篇   有彩图】
药品说明书 11【可能有多篇  有自定义】
*/

McPASS.Mc_Manual = function () {
    try {
        //为了显示出来说明书 图片，所以得取出来mc_really_type和mc_really_code
        if (mc_really_code == "" || mc_really_type == "") {
            mc_really_code = MC_global_queryDrug.ReferenceCode;
            mc_really_type = MC_global_queryDrug.ReferenceType;
        }
        //Add 2016-07-07 医院既没买说明书，也没买自定义说明书的情况
        //文字再定
        var moduledesc = "";
        if (mc_really_type == 11) {
            moduledesc = "药品说明书";
        } else if (mc_really_type == 24) {
            moduledesc = "中药材专论";
        }
        if (MC_global_ref_PASSdata == "") {
            McPASS.fn.byId("mcDivWindowBody").innerHTML = "<div class='mcloading'>抱歉，暂未查询到“" + MC_global_queryDrug.CodeName + "”的" + moduledesc + "！请与美康客服中心联系，热线电话：4006-821-210</div>";
            return;
        }

        var res = McPASS.fn.EvalJson(MC_global_ref_PASSdata);
        var delimiter = res.Delimiter;
        var sub = res.SubDelimiter;

        //表明没说明书
        if (res.ReferenceResults.length == 0) { 
            McPASS.fn.byId("mcDivWindowBody").innerHTML = "<div class='mcloading'>抱歉，暂未查询到“" + MC_global_queryDrug.CodeName + "”的" + moduledesc + "！请与美康客服中心联系，热线电话：4006-821-210</div>";
            return;
        }        

        var parag = res.ReferenceResults[0].BriefItems;
        var resMode = res.ResultMode; //PASS or USER 
        var chp = res.ReferenceResults[0].DetailItems;
        var otherItems = res.ReferenceResults[0].OtherItems.split(delimiter); //存图片的名字和base64码，只需要图片名字
        if (otherItems != "") {
            for (var i = 0; i < otherItems.length; i++) {
                var item = otherItems[i].split(sub)[0];
                var name = mc_really_type + "_" + mc_really_code + "_" + item; //图片文件名组合 

                //直接去掉这些特殊字符，但是webservice里面也要去掉
                name = name.replace(/\%/g, "");
                name = name.replace(/\+/g, ""); //cs处理了+
                name = name.replace(/\//g, ""); //cs处理了/
                name = name.replace(/&amp;/g, ''); //有的是这种情况，看上去是&
                name = name.replace(/&/g, '');
                name = name.replace(/\s+/g, '');
                name = name.replace(/'/g, '');
                name = name.replace(/#/g, '');
                name = name.replace(/!/g, '');
                name = name.replace(/:/g, '');
                name = encodeURIComponent(name);//进行编码，否则的ie6下有的时候图片出不来
                var url = mcWebServiceUrl + "/Images.ashx?id=" + name;
                // edit by yaozhao 2016-12-23 
                var pattern = new RegExp(item, 'g');
                parag = parag.replace(pattern, url);
                //
                //parag = parag.replace(/&quot;/g, "\""); 
                parag = parag.replace("&quot;" + url + "&quot;", "\"" + url + "\""); //为了防止文章正文内容有双引号的情况，只处理图片的双引号问题   
            }
        }
       

        //替换说明书返回的table片段中图片     
        parag = parag.replace(new RegExp("src='PassImage/", 'g'), "src='" + mcWebServiceUrl + "/PassImage/");
        parag = parag.replace(new RegExp("src=\"PassImage/", 'g'), "src=\"" + mcWebServiceUrl + "/PassImage/");       
        //处理勘误
        parag = parag.replace(new RegExp("class=\"mcErrataBodyTitle\"", 'g'), "class=\"mcErrataBodyTitle\" style='background-image:url(" + mcWebServiceUrl + "/PassImage/mckanwu_title.gif);'");

        if (mc_really_type != MC_global_queryDrug.ReferenceType || mc_really_code == "") {
            mc_really_code = MC_global_queryDrug.ReferenceCode; //为了查询其他如相互作用之类的得把code改过来
        }          
        var html = [];
        if (parag != "") {
            html.push("<div id='mcrefContentDiv' style='height:", mcWindowHeight - 110, "px;overflow:hidden;'>", parag, "</div>"); //服务端返回的table,正文的内容             
            McPASS.fn.byId("mcRefMainLeftDivId").innerHTML = html.join("");
            var _content = McPASS.fn.byId("mcManualPara").innerHTML;
            var _content_new = "<div id='mcidScroller' style='text-align:left;float:left;vertical-align:top;width:125px;overflow:hidden;'>" + _content + "</div>";
            McPASS.fn.byId("mcManualPara").innerHTML = _content_new;

            var body = McPASS.fn.byId("body_main_html").style;
            body.width = (mcWindowWidth - 125) + "px"; //计算122 
            body.height = (mcWindowHeight - 142) + "px"; //142

            var body_left = McPASS.fn.byId("body_left").style;
            body_left.height = (mcWindowHeight - 185) + "px"; //生成的td右边应该弄成<td id="body_right" >这样方便设置就不用设置body_main_html的高度
            

            McPASS.fn.byId("mcidScroller").style.height = (mcWindowHeight - 187) + "px"; //left scroller height     187       
            var count_N = 1;
            //自定义说明书的时候可能chp为空
            if (chp.length > 0) {
                var _count = chp.split(delimiter);
                if (_count.length > 1) { //如果没有更多说明书的情况现在只返回了一个值 
                    count_N = chp.split(delimiter)[2].split(sub)[1];
                }
                else {
                    count_N = chp.split(delimiter)[0].split(sub)[1];
                }
                McPASS.fn.byId("ParaPosition").style.height = 37 * count_N + "px"; //mcParaLink a的样式中28+4+5=37
            }
            /* 高度不够的情况下要隐藏*/
            if ((37 * count_N) < (mcWindowHeight - 142)) {
                McPASS.fn.byId("btUp").style.display = "none";
                McPASS.fn.byId("btDown").style.display = "none";
            }
            else {
                McPASS.fn.byId("btUp").style.display = "";
                McPASS.fn.byId("btDown").style.display = "";
            }
            /**/
        }
        else {
            MC_global_queryDrug.ReferenceType = parseInt(MC_global_queryDrug.ReferenceType / 10) * 10 + 1; //处理下type
            McPASS.Mc_Empty();
        }
    }
    catch (e) {
        McPASS.fn.byId("mcDivWindowBody").innerHTML = "<div class='mcloading'>" + McPASS.ErrorTip() + "</div>";
        return ;
    }
}; 

//获取其他文章列表
McPASS.ManualsearchList = function (param, type, refmode) {
    //缓存自定义说明书内容
    if (mcstrManualUser == "") {
        mcstrManualUser = McPASS.fn.byId("mcrefContentDiv").innerHTML;
    }
    //其他文章列表要缓存 要改变已经点过的显示颜色
    if (mcstrMoreList != "") {//如果已经缓存了，则直接取缓存数据，不重新取再组织了
        McPASS.fn.byId("mcrefContentDiv").innerHTML = mcstrMoreList;

       // var body = McPASS.fn.byId("body_main_html").style;
      //  body.height = (mcWindowHeight - 142) + "px";

        return;
    }
    var mc_temp_query = MC_global_queryDrug.ReferenceCode; //temp   
    var _type = MC_global_queryDrug.ReferenceType;
    mc_really_code = param;
    mc_really_type = _type;
    var name = MC_global_queryDrug.CodeName;
    MC_global_queryDrug.CodeName = refmode; //改用这个  

    MC_global_queryDrug.ReferenceType = parseInt(type);
    MC_global_queryDrug.ReferenceCode = param;
    MC_global_queryDrug.IsDrug = 0;
    var _obj = {
        PassClient: MCPASSclient,
        ReferenceParam: MC_global_queryDrug
    };
    MC_global_ref_HISdata = McPASS.toMcJSONString(_obj);
    var obj = {
        PASSFuncName: "Mc_DoQuery",
        PASSParam: MC_global_ref_HISdata
    };
    var data = McPASS.toMcJSONString(obj);
    McAjax.send(data);
    McAjax.onmessage = function (result) {
        if (result.indexOf("[ERROR]") < 0) {
            {
                var res = McPASS.fn.EvalJson(result);
                if (res.ReferenceResults.length > 0) {
                    var items = res.ReferenceResults[0].BriefItems;
                    //替换说明书返回的table片段中图片     
                    items = items.replace(new RegExp("src='PassImage/", 'g'), "src='" + mcWebServiceUrl + "/PassImage/");
                    items = items.replace(new RegExp("src=\"PassImage/", 'g'), "src=\"" + mcWebServiceUrl + "/PassImage/");

                    McPASS.fn.byId("mcrefContentDiv").innerHTML = items;
                    //处理高度
                    var body = McPASS.fn.byId("body_main_html").style;
                    body.height = (mcWindowHeight - 142) + "px";
                    mcstrMoreList = McPASS.fn.byId("mcrefContentDiv").innerHTML; //缓存 处理高度后的
                } 
            }
            MC_global_queryDrug.ReferenceCode = mc_temp_query;
            MC_global_queryDrug.ReferenceType = mc_really_type;
            MC_global_queryDrug.CodeName = name;
        }
    }
};

//文章列表，再查其他文章
McPASS.McRefQuery = function (refcount, param, type, refcommand, btnId, divId) {
    //点更多PASS显示列表后，再点具体某一篇说明书的时候 改样式，然后再取该div的修改后的样式的内容 再缓存，，下次如果有就直接显示        
    var _div = McPASS.fn.byId(divId);
    if (_div) {
        _div.className = 'mcMoreListV';
        mcstrMoreList = McPASS.fn.byId("mcrefContentDiv").innerHTML;
    }
    //查询的时候用加载效果 避免卡起的时候N次点击列表导致错误
    McPASS.fn.byId("mcRefMainLeftDivId").innerHTML = "<div class='mc_common_loading' style='background-image:url(" + mcWebServiceUrl + "/PassImage/loading.gif);background-repeat:no-repeat;'></div>";

    mc_really_code = param;
    mc_really_type = type;
    var code = MC_global_queryDrug.ReferenceCode;
    var name = MC_global_queryDrug.CodeName;
    var _type = MC_global_queryDrug.ReferenceType;

    MC_global_queryDrug.CodeName = refcount; //改用这个
    MC_global_queryDrug.ReferenceCode = param;
    MC_global_queryDrug.ReferenceType = parseInt(type);
    MC_global_queryDrug.IsDrug = 0;
    McPASS.McFuncCallQuery();
    MC_global_queryDrug.ReferenceCode = code; //如果不做还原，再去查相互作用之类的code就不对了
    MC_global_queryDrug.ReferenceType = _type;
    MC_global_queryDrug.CodeName = name;
};
McPASS.McFormatManualParagraph = function (parag) {
    if (parag.length <= 0) { return ""; }
    parag = parag.replace(/\n\r/g, "\r\n");
    var array = parag.split("\r\n");
    var ret = [];
    for (var i = 0; i < array.length; i++) {
        var line = array[i];
        line = ("<span class='mcManualparag'>" + line + "</span>");
        ret.push(line + "<br>");
    }
    return ret.join("");
};
var mcscrollManual = function (n) {
    scrollManual_t = n;
    if (scrollManual_t == 0) { return; }
    McPASS.fn.byId("mcidScroller").scrollTop += scrollManual_t;
    setTimeout("mcscrollManual(scrollManual_t)", 10);
};﻿McPASS.Verify = function (obj, _default) {
    return (obj == "" || obj == undefined || obj == null) ? _default : obj;
};
McPASS.MCPASSclient_Verify = function () {
    MCPASSclient.HospID = McPASS.Verify(MCPASSclient.HospID, "");
    MCPASSclient.UserID = McPASS.Verify(MCPASSclient.UserID, "");
    MCPASSclient.UserName = McPASS.Verify(MCPASSclient.UserName, "");
    MCPASSclient.DeptID = McPASS.Verify(MCPASSclient.DeptID, "");
    MCPASSclient.DeptName = McPASS.Verify(MCPASSclient.DeptName, "");
    MCPASSclient.CheckMode = McPASS.Verify(MCPASSclient.CheckMode, MC_global_CheckMode);
};
McPASS.MC_global_queryDrug_Verify = function () {
    MC_global_queryDrug.ReferenceCode = McPASS.Verify(MC_global_queryDrug.ReferenceCode, "");
    MC_global_queryDrug.CodeName = McPASS.Verify(MC_global_queryDrug.CodeName, "");
};
McPASS.MCpatientInfo_Verify = function () {
    MCpatientInfo.PatCode = McPASS.Verify(MCpatientInfo.PatCode, "");
    MCpatientInfo.InHospNo = McPASS.Verify(MCpatientInfo.InHospNo, "");
    MCpatientInfo.VisitCode = McPASS.Verify(MCpatientInfo.VisitCode, "");
    MCpatientInfo.Name = McPASS.Verify(MCpatientInfo.Name, "");
    MCpatientInfo.Sex = McPASS.Verify(MCpatientInfo.Sex, "");
    MCpatientInfo.Birthday = McPASS.Verify(MCpatientInfo.Birthday, "");
    MCpatientInfo.HeightCM = McPASS.Verify(MCpatientInfo.HeightCM, "");
    MCpatientInfo.WeighKG = McPASS.Verify(MCpatientInfo.WeighKG, "");
    MCpatientInfo.DeptCode = McPASS.Verify(MCpatientInfo.DeptCode, "");
    MCpatientInfo.DeptName = McPASS.Verify(MCpatientInfo.DeptName, "");
    MCpatientInfo.DoctorCode = McPASS.Verify(MCpatientInfo.DoctorCode, "");
    MCpatientInfo.DoctorName = McPASS.Verify(MCpatientInfo.DoctorName, "");
    MCpatientInfo.PatStatus = McPASS.Verify(MCpatientInfo.PatStatus, 1);
    MCpatientInfo.IsLactation = McPASS.Verify(MCpatientInfo.IsLactation, -1);
    MCpatientInfo.IsPregnancy = McPASS.Verify(MCpatientInfo.IsPregnancy, -1);
    MCpatientInfo.PregStartDate = McPASS.Verify(MCpatientInfo.PregStartDate, "");
    MCpatientInfo.HepDamageDegree = McPASS.Verify(MCpatientInfo.HepDamageDegree, -1);
    MCpatientInfo.RenDamageDegree =McPASS.Verify(MCpatientInfo.RenDamageDegree, -1);
    MCpatientInfo.UseTime = McPASS.Verify(MCpatientInfo.UseTime, "");
    MCpatientInfo.CheckMode = McPASS.Verify(MCpatientInfo.CheckMode, MC_global_CheckMode);
    MCpatientInfo.IsDoSave = McPASS.Verify(MCpatientInfo.IsDoSave, 0);
};
McPASS.McDrugsArray_Verify = function () {
    for (var i = 0; i < McDrugsArray.length; i++) {
        McDrugsArray[i].Index = McPASS.Verify(McDrugsArray[i].Index, "");
        McDrugsArray[i].OrderNo = McPASS.Verify(McDrugsArray[i].OrderNo, "");
        McDrugsArray[i].DrugUniqueCode = McPASS.Verify(McDrugsArray[i].DrugUniqueCode, "");
        McDrugsArray[i].DrugName = McPASS.Verify(McDrugsArray[i].DrugName, "");
        McDrugsArray[i].DosePerTime = McPASS.Verify(McDrugsArray[i].DosePerTime, "");
        McDrugsArray[i].DoseUnit = McPASS.Verify(McDrugsArray[i].DoseUnit, "");
        McDrugsArray[i].Frequency = McPASS.Verify(McDrugsArray[i].Frequency, "");
        McDrugsArray[i].RouteCode = McPASS.Verify(McDrugsArray[i].RouteCode, "");
        McDrugsArray[i].RouteName = McPASS.Verify(McDrugsArray[i].RouteName, "");
        McDrugsArray[i].StartTime = McPASS.Verify(McDrugsArray[i].StartTime, "");
        McDrugsArray[i].EndTime = McPASS.Verify(McDrugsArray[i].EndTime, "");
        McDrugsArray[i].ExecuteTime = McPASS.Verify(McDrugsArray[i].ExecuteTime, "");
        McDrugsArray[i].GroupTag = McPASS.Verify(McDrugsArray[i].GroupTag, "");
        McDrugsArray[i].IsTempDrug = McPASS.Verify(McDrugsArray[i].IsTempDrug, 0);
        McDrugsArray[i].OrderType = McPASS.Verify(McDrugsArray[i].OrderType, 0);
        McDrugsArray[i].DeptCode = McPASS.Verify(McDrugsArray[i].DeptCode, "");
        McDrugsArray[i].DeptName = McPASS.Verify(McDrugsArray[i].DeptName, "");
        McDrugsArray[i].DoctorCode = McPASS.Verify(McDrugsArray[i].DoctorCode, "");
        McDrugsArray[i].DoctorName = McPASS.Verify(McDrugsArray[i].DoctorName, "");
        McDrugsArray[i].RecipNo = McPASS.Verify(McDrugsArray[i].RecipNo, "");
        McDrugsArray[i].Num = McPASS.Verify(McDrugsArray[i].Num, "");
        McDrugsArray[i].NumUnit = McPASS.Verify(McDrugsArray[i].NumUnit, "");
        McDrugsArray[i].Purpose = McPASS.Verify(McDrugsArray[i].Purpose, 0);
        McDrugsArray[i].OprCode = McPASS.Verify(McDrugsArray[i].OprCode, "");
        McDrugsArray[i].MediTime = McPASS.Verify(McDrugsArray[i].MediTime, "");
        McDrugsArray[i].Remark = McPASS.Verify(McDrugsArray[i].Remark, "");
        McDrugsArray[i].driprate = McPASS.Verify(McDrugsArray[i].driprate, "");
        McDrugsArray[i].driptime = McPASS.Verify(McDrugsArray[i].driptime, "");
        McDrugsArray[i].IsOtherRecip = McPASS.Verify(McDrugsArray[i].IsOtherRecip, 0);
        McDrugsArray[i].duration = McPASS.Verify(McDrugsArray[i].duration, 0);

    }
};
McPASS.McMedCondArray_Verify = function () {
    for (var i = 0; i < McMedCondArray.length; i++) {
        McMedCondArray[i].Index = McPASS.Verify(McMedCondArray[i].Index, "");
        McMedCondArray[i].DiseaseCode = McPASS.Verify(McMedCondArray[i].DiseaseCode, "");
        McMedCondArray[i].DiseaseName = McPASS.Verify(McMedCondArray[i].DiseaseName, "");
        McMedCondArray[i].RecipNo = McPASS.Verify(McMedCondArray[i].RecipNo, "");
    }
};
McPASS.McAllergenArray_Verify = function () {
    for (var i = 0; i < McAllergenArray.length; i++) {
        McAllergenArray[i].Index = McPASS.Verify(McAllergenArray[i].Index, "");
        McAllergenArray[i].AllerCode = McPASS.Verify(McAllergenArray[i].AllerCode, "");
        McAllergenArray[i].AllerName = McPASS.Verify(McAllergenArray[i].AllerName, "");
        McAllergenArray[i].AllerSymptom = McPASS.Verify(McAllergenArray[i].AllerSymptom, "");
    }
};
McPASS.McOperationArray_Verify = function () {
    for (var i = 0; i < McOperationArray.length; i++) {
        McOperationArray[i].Index = McPASS.Verify(McOperationArray[i].Index, "");
        McOperationArray[i].OprCode = McPASS.Verify(McOperationArray[i].OprCode, "");
        McOperationArray[i].OprName = McPASS.Verify(McOperationArray[i].OprName, "");
        McOperationArray[i].IncisionType = McPASS.Verify(McOperationArray[i].IncisionType, "");
        McOperationArray[i].OprStartDate = McPASS.Verify(McOperationArray[i].OprStartDate, "");
        McOperationArray[i].OprEndDate = McPASS.Verify(McOperationArray[i].OprEndDate, "");
    }
};
McPASS.McLabArray_Verify = function () {
    for (var i = 0; i < McLabArray.length; i++) {
        McLabArray[i].RequestNo = McPASS.Verify(McLabArray[i].RequestNo, "");
        McLabArray[i].LabExamCode = McPASS.Verify(McLabArray[i].LabExamCode, "");
        McLabArray[i].LabExamName = McPASS.Verify(McLabArray[i].LabExamName, "");
        McLabArray[i].StartDateTime = McPASS.Verify(McLabArray[i].StartDateTime, "");
        McLabArray[i].DeptCode = McPASS.Verify(McLabArray[i].DeptCode, "");
        McLabArray[i].DeptName = McPASS.Verify(McLabArray[i].DeptName, "");
        McLabArray[i].DoctorCode = McPASS.Verify(McLabArray[i].DoctorCode, "");
        McLabArray[i].DoctorName = McPASS.Verify(McLabArray[i].DoctorName, "");
    }
};
McPASS.McExamArray_Verify = function () {
    for (var i = 0; i < McExamArray.length; i++) {
        McExamArray[i].RequestNo = McPASS.Verify(McExamArray[i].RequestNo, "");
        McExamArray[i].LabExamCode = McPASS.Verify(McExamArray[i].LabExamCode, "");
        McExamArray[i].LabExamName = McPASS.Verify(McExamArray[i].LabExamName, "");
        McExamArray[i].StartDateTime = McPASS.Verify(McExamArray[i].StartDateTime, "");
        McExamArray[i].DeptCode = McPASS.Verify(McExamArray[i].DeptCode, "");
        McExamArray[i].DeptName = McPASS.Verify(McExamArray[i].DeptName, "");
        McExamArray[i].DoctorCode = McPASS.Verify(McExamArray[i].DoctorCode, "");
        McExamArray[i].DoctorName = McPASS.Verify(McExamArray[i].DoctorName, "");
    }
};﻿//同步模式
McPASS.Mc_PR_RegSync = function () {
    var get = new MC_client_request(2);
    get.ClearParam();
    get.AddParam("psJSONStr", null);
    Mc_PR_RegSyncCallBack(get.post("Mc_GetProductRegistInfo", null, Mc_PR_RegSyncCallBack)); //同步模式
    return mc_pr_isreg;
};
function Mc_PR_RegSyncCallBack(result) {
    if (!result.error) {
        if (result.value.indexOf("[ERROR]") < 0) {
            if (result.value == 1) {
                mc_pr_isreg = true;
            }
        }
    }
};

McPASS.Mc_PASS_RegSync = function () {
    var get = new MC_client_request(1);
    get.ClearParam();
    get.AddParam("psJSONStr", null);
    Mc_PASS_RegSyncCallBack(get.post("Mc_GetProductRegistInfo", null, Mc_PASS_RegSyncCallBack)); //同步模式
    return mc_pass_isreg;
};
function Mc_PASS_RegSyncCallBack(result) {
    if (!result.error) {
        if (result.value.indexOf("[ERROR]") < 0) {
            if (result.value == 1) {
                mc_pass_isreg = true;
            }
        }
    }
}; 
/*******************************************************************************************************/
McPASS.Mc_PassSwitch = function () {
    var result = 1;
    if (mc_product_switch.PASS == 0) {
        return 0;
    }
    if (mcWebServiceUrl == "") {
        return 0;
    }
    //同步,异步太麻烦了，经跟李哥讨论同意不处理
    //if (MC_Is_SyncCheck) {
    //    var pass = McPASS.Mc_PASS_RegSync();
    //    if (pass) {
    //        result = 1;
    //    }
    //    else {
    //        result = 0;
    //    }
    //}
    return result;
};

McPASS.Mc_PrSwitch = function () {
    var result = 1;
    if (mc_product_switch.PR == 0) {
        return 0;
    }
    if (mcPrWebServiceUrl == "") {
        return 0;
    }
    //sync
    //if (MC_Is_SyncCheck) {
    //    var pr = McPASS.Mc_PR_RegSync();
    //    if (pr) {
    //        result = 1;
    //    }
    //    else {
    //        result = 0;
    //    }
    //}
    return result;
};﻿//define same name as cs dll
function MDC_GetSysPrStatus(StatusNo, showdetail) {
    return McPASS.McPrSysStatus(StatusNo, showdetail);
};

McPASS.McPrSysStatus = function (StatusNo, showdetail) {
    var isReturnObj = false;
    if (showdetail && (showdetail == "detail" || showdetail == "1")) {
        isReturnObj = true;
    }
    var obj = null;
    if (mc_product_switch.PR <= 0) {
        if (isReturnObj) {
            obj = {Status : 1, Reason : "PR功能未启用，自动通过。"};
            return obj;
        }
        return 1;
    }
    if (MC_global_Screen_PRstatus === undefined) {
        if (isReturnObj) {
            obj = {Status : 1, Reason : "审查未获取到PR状态对象。"};
            return obj;
        }
        return 1;
    }
    if (StatusNo == undefined || StatusNo == null) {
        if (isReturnObj) {
            obj = {Status : 1, Reason : "输入的处方号或医嘱唯一号为空。"};
            return obj;
        }
        return 1;
    }
    obj = null;
    for (i = 0; i < MC_global_Screen_PRstatus.length; i++) {
        obj = MC_global_Screen_PRstatus[i];
        if (!obj.Status || obj.Status >= 0) {
            obj.Status = 1;
        } else {
            obj.Status = 0;
        }
        if (obj == undefined || obj == null) continue;
        if (obj.StatusNo == undefined || obj.StatusNo == null) continue;
        if (obj.StatusNo != StatusNo) continue;
        break;
    }    
    if (isReturnObj) {
        return obj;
    }
    if (!!obj && !isNaN(obj.Status) && obj.Status <= 0) {
        return 0;
    }
    return 1;
};

// add by liuchunmei 2017-04-13 校验传入值，成功则阻断，否则不阻断
McPASS.FormatGetStatus = function () {
    // 如果传入条件不合法，则不阻断
    if (mcPrWebServiceUrl == "" || !McGetStatus) {
        return false;
    }
    // 异常校验输入值
    try {
        if (!McGetStatus.HospID) {
            McGetStatus.HospID = "";
        }
        if (!McGetStatus.PatCode) {
            McGetStatus.PatCode = "";
        }
        if (!McGetStatus.InHospNo) {
            McGetStatus.InHospNo = "";
        }
        if (!McGetStatus.VisitCode) {
            McGetStatus.VisitCode = "";
        }
        if (!McGetStatus.RecipNo) {
            McGetStatus.RecipNo = "";
        }
        if (!McGetStatus.TaskType) {
            McGetStatus.TaskType = 2; // default be 2
        } else if (McGetStatus.TaskType == 0) {
            McGetStatus.TaskType = 1;
        } else if (McGetStatus.TaskType == 3) {
            McGetStatus.TaskType = 2;
        } else if (McGetStatus.TaskType != 1 && McGetStatus.TaskType != 2) {
            McGetStatus.TaskType = 2; // default be 2
        }
    } catch (e) {
        return false;
    }
    return true;
};
/*******************************************************************************************************/
/*
单独获取pr状态
同步模式


//收入是json串，返回的是    
///           -8 双签复核药师再次不通过 
///           -7 医生修改后系统审核不通过   
///           -6 请求双签状态
///           -5 提请发药状态
///           -4 默认状态
///           -3 药师再次不通过
///           -2 药师首次不通过
///           -1 系统预判不通过
///            0 系统审查通过
///            1 药师首次审查通过
///            2 医生双签通过
///            3 医生修改后药师通过
///            4 干预关闭后自动通过
///            5 未分配科室权限通过
///            6 无人获取超时通过
///            7 无人上班自动通过
///            8 科室无人上班自动通过
///            9 双签复核通过
///           10 系统干预后通过
///           11 医生修改后系统通过
///           12 药师离开自动通过
///           13 医生修改后超时通过
///           14 双签复核超时通过
///           15 药师未审核超时通过
///           16 医生修改后系统关闭后通过
///           17 双签审核系统关闭后通过
///           18 医生修改后药师离开后通过
///           19 双签复核药师离开后通过
///           20 系统审查问题经药师确认后通过
*/

//the same name as cs
function MDC_PR_GetStatusSync() {
    if (!McPASS.FormatGetStatus()) return 1; // 如果输入不合法，则返回通过
    // 调用具体的方法
    McPASS.Mc_PR_GetStatusSync();
};
McPASS.Mc_PR_GetStatusSync = function () {
    var get = new MC_client_request(2);//2:pr
    get.ClearParam();
    get.AddParam("psJSONStr", McPASS.toMcJSONString(McGetStatus));
    Mc_PR_GetStatusSyncCallBack(get.post("Mc_GetStatus", null, Mc_PR_GetStatusSyncCallBack)); //同步模式
};
function Mc_PR_GetStatusSyncCallBack(callBackResult) {
    if (!callBackResult.error) {
        var result = callBackResult.value;
        if (result.indexOf("[ERROR]") < 0) {
            mc_pr_statusobj = McPASS.fn.EvalJson(result); // add by liuchunmei 2017-04-11 记录状态信息
            mc_pr_status = 1;
            if (mc_pr_statusobj.Status == undefined || mc_pr_statusobj.Status == null || mc_pr_statusobj.Status < 0) {
                mc_pr_status = 0;
            } 
        }
    }
};
/*******************************************************************************************************/
//the same name as cs
function MDC_PR_GetStatus(callback) {
    // 如果输入不合法，则返回通过
    if (!McPASS.FormatGetStatus()) {
        //callback, anyway,we need callback for his invoke
        if (callback && callback != "" && typeof callback === 'function') {
            callback(1);
        }
        return;
    }
    // 调用具体的方法
    McPASS.Mc_PR_GetStatus(callback);
};

//PR通用接口执行方法
McPASS.MC_PR_Perform = function (funcName, status, callback) {
    var obj = {
        PASSFuncName: funcName,
        PASSParam: McPASS.toMcJSONString(status)
    };
    var data = McPASS.toMcJSONString(obj);

    McAjaxPR.send(data, callback); //recipecheck should support callback function
    McAjaxPR.onmessage = function (result) {
        McPASS.Debug(IsDebugResult, obj.PASSFuncName + "：" + result, DebugShowDiv_Result); //debug 
        callback(result);
    };
};


//提请发药 设置Status中的 Urgent 1加急 0 不加急
McPASS.Mc_PR_ApplyForReview = function (status, callback) {
    McPASS.MC_PR_Perform("Mc_ApplyForReview", status, function (result) {
        callback(result);
    });
}

//双签 设置Status中的 Remark
McPASS.Mc_PR_CounterSign = function (status, callback) {
    McPASS.MC_PR_Perform("Mc_CounterSign", status, function (result) {
        callback(result);
    });
}

//预处理结果
function __preProcessStatusResult(result) {
    if (result.indexOf("[ERROR]") != -1) {
        return { hasError: true };
    }
    return McPASS.fn.EvalJson(result);
}


//取得任务状态 返回原始JSON对象
McPASS.Mc_PR_GetStatusObj = function (callback) {
    McPASS.MC_PR_Perform("Mc_GetStatus", McGetStatus, function (result) {
        callback(__preProcessStatusResult(result));
    });
}

//取得任务状态
McPASS.Mc_PR_GetStatus = function (callback) {
    McPASS.MC_PR_Perform("Mc_GetStatus", McGetStatus, function (result) {
        mc_is_screen_now = 0; //screen and analyze screen result done 
        if (result.indexOf("[ERROR]") < 0) {
            var _result = McPASS.fn.EvalJson(result);
            // _result 里还有超时时间，超时理由等，需要再讨论处理方式
            //callback, anyway,we need callback for his invoke
            if (callback && callback != "" && typeof callback === 'function') {
                callback(_result.Status < 0 ? 0 : 1, _result);
            }
        }
    });
}﻿//显示编辑用药指导单打印界面
McPASS.McShowPrintPG = function () {
    var btnCancel = McPASS.fn.byId('btnCancel').style;
    if (btnCancel) {
        btnCancel.display = "";
    }
    var cks = document.getElementsByName('ck');
    for (var i = 0; i < cks.length; i++) {
        cks[i].style.visibility = 'visible';
    }
    var btnPrint = McPASS.fn.byId('btnPrint');
    if (btnPrint) {
        btnPrint.onclick = function () {
            McPASS.McPrintPG();
        };
    }
};
//隐藏编辑用药指导单打印界面
McPASS.McHidePrintPG = function () {
    var btnCancel = McPASS.fn.byId('btnCancel').style;
    if (btnCancel) {
        btnCancel.display = "none";
    }
    var cks = document.getElementsByName('ck');
    for (var i = 0; i < cks.length; i++) {
        cks[i].style.visibility = 'hidden';
        cks[i].checked = false;
    }
    var btnPrint = McPASS.fn.byId('btnPrint');
    if (btnPrint) {
        btnPrint.onclick = function () {
            McPASS.McShowPrintPG();
        };
    }
};
//选择名称自动全选内容
McPASS.McChangeAll = function (cbid, divid) {
    var checkboxobj = McPASS.fn.byId(cbid);
    var divobj = McPASS.fn.byId(divid);
    if (checkboxobj && divobj) {
        var cks = divobj.getElementsByTagName('input');
        for (var i = 0; i < cks.length; i++) {
            cks[i].checked = checkboxobj.checked;
        }
    }
};
//选择内容自动选择名称
McPASS.McChangeHeader = function (cbid, divid) {
    var checkboxobj = McPASS.fn.byId(cbid);
    var divobj = McPASS.fn.byId(divid);
    var checked = false;
    if (checkboxobj && divobj) {
        var cks = divobj.getElementsByTagName('input');
        for (var i = 0; i < cks.length; i++) {
            if (cks[i].checked) {
                checked = true;
                break;
            }
        }
        checkboxobj.checked = checked;
    }
};
Date.prototype.Format = function (format) {
    var o = {
        "M+": this.getMonth() + 1, //month 
        "d+": this.getDate(), //day 
        "h+": this.getHours(), //hour 
        "m+": this.getMinutes(), //minute 
        "s+": this.getSeconds(), //second 
        "q+": Math.floor((this.getMonth() + 3) / 3), //quarter 
        "S": this.getMilliseconds() //millisecond 
    };

    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }

    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
};
//打印用药指导单
McPASS.McPrintPG = function () {
    var checked = false;

    var linenotice = McPASS.fn.byId('linenotice');
    var divnotice = McPASS.fn.byId('pgnotice');
    var lineinter = McPASS.fn.byId('lineinter');
    var divinter = McPASS.fn.byId('pginter');

    var cks = document.getElementsByName('ck');
    var lbs = document.getElementsByTagName('label');
    if (cks && lbs) {
        for (var i = 0; i < cks.length; i++) {
            if (cks[i].checked) {
                checked = true; 
                lbs[i].className = "mcPrint";
                if (cks[i].id.indexOf("Notice") > 0 &&
                    linenotice.className.indexOf("mcNoPrint") > 0) {
                    linenotice.className = "mcPGLine mcPrint";
                    divnotice.className = "mcPGNotice mcPrint";
                }
                if (cks[i].id.indexOf("Inter") > 0 &&
                    lineinter.className.indexOf("mcNoPrint") > 0) {
                    lineinter.className = "mcPGLine mcPrint";
                    divinter.className = "mcPGInter mcPrint";
                }
            }
            else {
                lbs[i].className = "mcNoPrint";
            }
        }
    }
    if (checked) {
        var divmain = McPASS.fn.byId('maindiv').style;
        if (divmain) {            
            divmain.overflowY = "hidden";
            divmain.height = "auto";
        }
        var pgcontentdiv = McPASS.fn.byId('pgcontent').style;
        if (pgcontentdiv) {            
            pgcontentdiv.overflowY = "hidden";
            pgcontentdiv.height = "auto";
        }
        
        var printtime = McPASS.fn.byId('printtime');
        if (printtime) {
            printtime.style.display = "block";
            printtime.innerText = new Date().Format("yyyy-MM-dd hh:mm:ss");
        }

        if (!+[1, ] && wb && wb.execwb) {            
            wb.execwb(7, 1);
        }
        else
        {
            window.print();
        }
        
        //if (divmain) {
        //    divmain.height = "100%";
        //    divmain.overflowY = "auto";
        //}
        if (pgcontentdiv) {
            pgcontentdiv.height = "100%";
            pgcontentdiv.overflowY = "auto";
        }
        if (printtime) {
            printtime.style.display = "none";
            printtime.innerText = "";
        }
        

        if (linenotice) {
            linenotice.className = "mcPGLine mcNoPrint";
        }
        if (divnotice) {
            divnotice.className = "mcPGNotice mcNoPrint";
        }
        if (lineinter) {
            lineinter.className = "mcPGLine mcNoPrint";
        }
        if (divinter) {
            divinter.className = "mcPGInter mcNoPrint";
        }        
    }
    else {
        alert("请至少选择一项打印！");
    }
    return false;
};

McPASS.McPrintOpenPGWindow = function () {
    if (document.getElementById("mcDivWindowBody")) {
        var printData = document.getElementById("mcDivWindowBody").innerHTML;
        printData = printData.replace(new RegExp("McPASS.McPrintOpenPGWindow"), "McPASS.McShowPrintPG");
        var html = [];
        html.push("<html>");
        html.push("<head>");
        html.push("<title>用药指导单打印</title>");
        html.push("<link type=\"text/css\" rel=\"Stylesheet\" href=" + mcWebServiceUrl + "/PassJs/McCssAll.css />");
        html.push("<script type=\"text/javascript\" src=" + mcWebServiceUrl + "/PassJs/McConfig.js></script>");
        html.push("<script type=\"text/javascript\" src=" + mcWebServiceUrl + "/PassJs/McJsAll.js></script>");
        html.push("</head>");
        html.push("<body style=\"overflow-y:hidden;\">");
        html.push(printData);
        html.push("</body>");
        html.push("</html>");

        var newWindow = window.open("", "_blank");//打印窗口要换成页面的url
        if (newWindow) {
            newWindow.document.write(html.join(''));
            newWindow.document.close();
        }
    }
    return false;
};/*
 * 外部调用接口
 * PASS综合命令函数
   * @param[in] _cmdtype  命令类型
   *   1、审查：命令格式为"SCREEN_是否弹窗_是否采集"：
   *       1011：查查弹结果窗体 采集
   *       1010：审查弹结果窗体 不采集
   *       1001：审查不弹结果窗体 采集
   *       1000：审查不弹结果窗体 不采集
   *   2、查询：模块编号：
   *       11：药品说明书 弹出结果窗体
   *       51：浮动窗口 弹出结果窗体
   *       34：用药指导单 弹出结果窗体 
   *       211：推荐剂量 弹出结果窗体  
   *   3、查询平台：模块编号：
   *       5001：查询平台 弹出浏览器
 * */

function MDC_DoPassCommand(_cmdtype, _callback) {
    switch (_cmdtype) {
        case 1011:
        case 1010:
            MDC_DoCheck(_callback, 1);
            break;
        case 1001:
        case 1000:
            MDC_DoCheck(_callback, 0);
            break;
        case 11:
        case 51:
        case 24:
            MDC_DoRefDrug(parseInt(_cmdtype));
            break;
        case 34:
            McPASS.McGetModuleName(McPASS.QueryPG);
            break;
        case 211:
            McPASS.McGetModuleName(McPASS.QueryRecomDose);
            break;
        // add by liuchunmei 2017-05-23 添加医生端查询平台调用接口
        case 5001:
            McPASS.McGetModuleName(McPASS.OpenRefPlatform);
            break;
         // end add by liuchunmei 2017-05-23 添加医生端查询平台调用接口       
        case "11_":
        case "51_":
        case "34_":
            break;
        default:
            alert("无效的命令类型!");
    }
};

/*
* b/s 维护工具调用
*/
function MDC_DoCheckByJson(_json, _callback, _isShowWindow) {
    if (MC_Is_SyncCheck == true) {
        McPASS.McScreenSyncByJson(_json, _isShowWindow);
    }
    else {
        McPASS.McScreenAsyncByJson(_json, _callback, _isShowWindow);
    }
};

McPASS.McScreenSyncByJson = function (_json, _isShowWindow) {
    //return if screen now
    if (mc_is_screen_now == 1) {
        return;
    }
    mc_is_screen_now = 1; //screen now      
    McPASS.isShowWindow = 1; //whether show screen window
    if (_isShowWindow != undefined) {
        McPASS.isShowWindow = _isShowWindow;
    }
    McPASS.McGetModuleNameSync();//如果只用PR,也得获取这个，因为组织审查界面需要这个，如果只要PR系统审查结果不要PASS界面可以不掉
    MC_global_Screen_Count = 0;
    
    if (!_json) return;
    
    MC_global_Screen_HISdata = _json;
    //特殊字符处理
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\%/g, "%25");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\+/g, "%2b");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\s/g, "%20");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/&amp;/g, '%26');
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/&/g, "%26");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/@/g, "%40");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\//g, "%2F");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\?/g, "%3F");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\#/g, "%23");
    MC_global_Screen_HISdata = MC_global_Screen_HISdata.replace(/\$/g, "%24");

    var checkHandler = new MC_client_request(1);
    checkHandler.ClearParam();
    checkHandler.AddParam("psJSONStr", MC_global_Screen_HISdata);
    McScreenSyncHandler(checkHandler.post("Mc_DoScreen", null, McScreenSyncHandler)); //同步模式 
};

function McScreenSyncHandler(result) {
    if (!result.error) {
        mc_is_screen_now = 0; //screen and analyze screen result done 
        if (result.value.indexOf("[ERROR]") < 0) {
            McPASS.Debug(IsDebugResult, "审查结果：" + result.value, DebugShowDiv_Result); //debug 

            MC_global_Screen_PASSdata = McPASS.fn.EvalJson(result.value);
            if (MC_global_Screen_PASSdata != null) {
                McPASS.ScreenHighestSlcode = MC_global_Screen_PASSdata.HighestSlcode; //审查结果最严重级别值  
                if (McPASS.ScreenHighestSlcode > 0) { //审查的时候如果结果小于0则不处理，不返灯
                    McPASS.McAnalyzeScreenResult(1); //Analyze the Screen Result 
                }
            }
            McDebug_t2 = McGetCurrentTime();
        }
    }
};


McPASS.McScreenAsyncByJson = function (_json, callback, _isShowWindow) {
    if (mc_pass_init_success !=1) {        
        callback(mc_pass_init_success);//return status when pass init failed,may passcore is abnormal or ipaddress is error or init timeout
        return;
    }
    //return if screen now
    if (mc_is_screen_now == 1) {
        return;
    }
    MC_global_Screen_HISdata = _json;
    mc_is_screen_now = 1; //screen now   
    McPASS.Screen_callback = callback; //set the callback function
    McPASS.isShowWindow = 1; //whether show screen window
    if (_isShowWindow != undefined) {
        McPASS.isShowWindow = _isShowWindow;
    }
    McPASS.McGetModuleName(McPASS.McScreenAsyncHandler);
};

McPASS.McScreenAsyncHandler = function () {
    McDebug_t1 = McGetCurrentTime();
    MC_global_Screen_Count = 0;

    if (!MC_global_Screen_HISdata) return;
    
    var obj = {
        PASSFuncName: "Mc_DoScreen",
        PASSParam: MC_global_Screen_HISdata
    };
    var data = McPASS.toMcJSONString(obj);    
    var ajax = McAjax;

    ajax.send(data, McPASS.Screen_callback); //recipecheck should support callback function 
    ajax.onmessage = function (result) {
        mc_is_screen_now = 0; //screen and analyze screen result done
        //var mc_is_core_error = false;
        if (result.indexOf("[ERROR]") < 0) {
            McPASS.Debug(IsDebugResult, "审查结果：" + result, DebugShowDiv_Result); //debug            
            MC_global_Screen_PASSdata = McPASS.fn.EvalJson(result);
            
            if (MC_global_Screen_PASSdata != null) {
                McPASS.ScreenHighestSlcode = MC_global_Screen_PASSdata.HighestSlcode; //审查结果最严重级别值  
                if (McPASS.ScreenHighestSlcode > 0) { //审查的时候如果结果小于0则不处理，不返灯
                    McPASS.McAnalyzeScreenResult(1); //Analyze the Screen Result 
                }
            }
        } 
        else if (result=="[ERROR]:timeout")
        {
            mc_pass_init_success = -99;
            McPASS.ScreenHighestSlcode = -99;//return -99 when timeout  
        }
        else {
            mc_pass_init_success = -10;
            McPASS.ScreenHighestSlcode = -10;//return -10 when passcore is abnormal  
        } 
        //callback, anyway,we need callback for his invoke 
        if (this.callback && this.callback != "" && typeof this.callback === 'function') {
            this.callback(McPASS.ScreenHighestSlcode);
        } 
    };
    McDebug_t2 = McGetCurrentTime();
};