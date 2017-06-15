
function NumMath(){
		var num = Math.random()*1000
        num = parseInt(num);
		return num*4
}
export function GetKmbAes(data){
     var s=getAesString(TYPES.mykey,data); 
	 var k=getAesString(TYPES.publickey,TYPES.mykey+NumMath()); 
     return 'data='+s+'&encryptkey='+k+'';
}
export function getAesString(key,data) { //加密
    key = CryptoJS.enc.Utf8.parse(key);
    var encrypted = CryptoJS.AES.encrypt(data, key, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    });
   　var encryptedBase64Str = encrypted.toString();
    return encryptedBase64Str;
}
export function getDecAesString(encrypted) { 
     //解密
     var  key="FC72E7899C804EEC";
     key = CryptoJS.enc.Utf8.parse(key);
     var decrypted = CryptoJS.AES.decrypt(encrypted, key, {  
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    });
    return decrypted.toString(CryptoJS.enc.Utf8);
}