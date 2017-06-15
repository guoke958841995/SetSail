
callbackfunction({"name":"界面初始化"});
callbackview({"name":"您点击了按钮，联网成功"});

//<script src="file:///android_asset/jquery.min.js"></script>
//<script>
//$(function(){
//  var params = new Map();
//  var url = "";
//  var URL = createGetUrlWithParams(url,setRandomAes(params));
//  $.ajax({
//    async: false,
//    type: "GET",
//    dataType: 'jsonp',
//    jsonp: 'callback',
//    jsonpCallback: 'callbackfunction',
//    url: URL,
//    data: "",
//    timeout: 3000,
//    contentType: "application/json;utf-8",
//    success: function(msg) {
//        try{
//           console.log(msg);
//        }catch(e){
//           console.log(e);
//        }
//    }
//  });
//})
//</script>
//
//<script src="./js/AES.js"></script>
//<script src="./js/Map.js"></script>
//<script test="text/javascript">
//     function setRandomAes(map){
//        map = addDeviceType(map);
//        var paramsAES = new Map();
//        //随机生成4的倍数
//        var aesNum = (Math.random() * 100) * 4;
//         //随机生成AES密钥
//         var aesKey = RandomUtil.getRandom(16);
//            // 使用AES算法将商户自己随机生成的AESkey加密
//            var encryptkey = AES.EncryptECB(aesKey+aesNum);
//            paramsAES.put("encryptkey", encryptkey);
//            if (map != null) {
//                var info = JSON.toJSONString(map);
//                //AES加密数据
//                var data = AESUtils.EncryptECB(info,aesKey);
//                paramsAES.put("data", data);
//            }
//        return paramsAES;
//    }
//    function addDeviceType(params){
//        params.put("machineCode", "111111111111111");  //机器码
//        params.put("deviceType", "Android");  //设备类型
//        params.put("deviceBrand", "康邀牌手机");    //设备型号
//        params.put("deviceVersion", "800");    //Android系统版本号
//        params.put("appName", "lmb");    //指定是拉煤宝用户
//        return params;
//    }
//
//   /**
//     * 根据请求地址和请求参数进行拼接成新的请求地址
//     *
//     * @param url    请求服务器地址
//     * @param params 请求参数
//     * @return 拼接过后的地址
//     */
//    function createGetUrlWithParams(url,params){
//        if (params != null) {
//            var stringBuffer = new StringBuffer(url);
//            if (!url.contains("?")) {
//                stringBuffer.append('?');
//            }
//            for (Map.Entry entry : params.entrySet()) {
//                var key = entry.getKey().toString();
//                var value = null;
//                if (entry.getValue() == null) {
//                    value = "";
//                } else {
//                    value = entry.getValue().toString();
//                }
//                stringBuffer.append(key);
//                stringBuffer.append("=");
//                try {
//                    value = URLEncoder.encode(value, DEFAULT_PARAMS_ENCODING);
//                    stringBuffer.append(value);
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                stringBuffer.append('&');
//            }
//            //删除最后一个'&'
//            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
//            url = stringBuffer.toString();
//        }
//        return url;
//    }
//</script>