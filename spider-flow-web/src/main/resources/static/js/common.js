function getQueryString(name) {
    var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return unescape(r[2]);
    }
    return null;
}

function isEmojiCharacter(substring) {
    let iconRule = /[\uD83C|\uD83D|\uD83E][\uDC00-\uDFFF][\u200D|\uFE0F]|[\uD83C|\uD83D|\uD83E][\uDC00-\uDFFF]|[0-9|*|#]\uFE0F\u20E3|[0-9|#]\u20E3|[\u203C-\u3299]\uFE0F\u200D|[\u203C-\u3299]\uFE0F|[\u2122-\u2B55]|\u303D|[\A9|\AE]\u3030|\uA9|\uAE|\u3030/ig;
    if (iconRule.test(substring)) {
        return true;
    }

    for (var i = 0; i < substring.length; i++) {
        var hs = substring.charCodeAt(i);
        if (0xd800 <= hs && hs <= 0xdbff) {
            if (substring.length > 1) {
                var ls = substring.charCodeAt(i + 1);
                var uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                if (0x1d000 <= uc && uc <= 0x1f77f) {
                    return true;
                }
            }
        } else if (substring.length > 1) {
            var ls = substring.charCodeAt(i + 1);
            if (ls == 0x20e3) {
                return true;
            }
        } else {
            if (0x2100 <= hs && hs <= 0x27ff) {
                return true;
            } else if (0x2B05 <= hs && hs <= 0x2b07) {
                return true;
            } else if (0x2934 <= hs && hs <= 0x2935) {
                return true;
            } else if (0x3297 <= hs && hs <= 0x3299) {
                return true;
            } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d || hs == 0x3030 ||
                hs == 0x2b55 || hs == 0x2b1c || hs == 0x2b1b || hs == 0x2b50) {
                return true;
            }
        }
    }
}

Date.prototype.format = function (b) {
    var a = this;
    var c = {
        "M+": a.getMonth() + 1,
        "d+": a.getDate(),
        "h+": a.getHours(),
        "m+": a.getMinutes(),
        "s+": a.getSeconds(),
        "q+": Math.floor((a.getMonth() + 3) / 3),
        S: a.getMilliseconds()
    };
    /(y+)/.test(b) && (b = b.replace(RegExp.$1, (a.getFullYear() + "").substr(4 - RegExp.$1.length)));
    for (var d in c) new RegExp("(" + d + ")").test(b) && (b = b.replace(RegExp.$1, 1 == RegExp.$1.length ? c[d] : ("00" + c[d]).substr(("" + c[d]).length)));
    return b
}
var sf = {};
sf.ajax = function (options) {
    var loading;
    var loadingInterval;
    var closeClear = function () {
        layui.layer.close(loading);
        clearInterval(loadingInterval);
    }
    var beginTime = +new Date();
    var url = options.url;
    var type = options.type;
    var data = options.data;
    var success = options.success;
    var error = options.error;
    $.ajax({
        url: url,
        type: type,
        data: data,
        success: function (result) {
            closeClear();
            success && success(result);
        },
        error: function (errorInfo) {
            closeClear();
            error && error(errorInfo);
        },
        beforeSend: function () {
            loadingInterval = setInterval(function () {
                var endTime = +new Date();
                if ((endTime - beginTime) > 500) {
                    loading = layui.layer.load(1, {
                        shade: [0.1, '#fff']
                    });
                    clearInterval(loadingInterval);
                }
            }, 100);
        }
    })
}