
import * as TYPES from '../constants/ActionTypes';
import * as APIS from '../constants/Urls';
import { Alert } from 'react-native';
import { toastShort } from '../utils/ToastUtil';
import AES  from "crypto-js/aes";
import SHA256 from "crypto-js/sha256";
import MD5  from "crypto-js/md5";
import CryptoJS from "crypto-js";
import Toast from 'antd-mobile/lib/toast';
import {GetKmbAes,getAesString,getDecAesString} from './js/AES';
import {DeviceInfoApp} from '../utils/DevuceInfo';
export function Coaladds(EditCoal){
	return (dispatch) => {

		var data ='{"userId":"'+EditCoal.phone+'","companyId":"'+ditCoal.companyId+'",coalName:"'+EditCoal.coalName+'","totalSulfur":"'+EditCoal.totalSulfur+'","inventory":"'
		+EditCoal.inventory+'","calorificValue":"'+EditCoal.calorificValue+'","grainSize":"'+EditCoal.grainSize+'","volatileValue":"'+EditCoal.volatileValue+
		'","ash":"'+EditCoal.ash+'","totalMoisture":"'+EditCoal.totalMoisture+'","oneQuote":"'+EditCoal.oneQuote+'",'+DeviceInfoApp()+'}';
		var request = new XMLHttpRequest();//创建XMLHttpRequest组建 
		request.open('POST', APIS.MANAGER_CoalProductsAdd +'?'+GetKmbAes(data),true);
		request.timeout = 3000;  //响应时间 3s
		request.setRequestHeader("Content-Type", "application/json")
		request.send();//发送请求   
		request.onreadystatechange = (e) => {
			//异步获取 相当于ajax
			if (request.readyState !== 4) {
				return;
			 }
			if (request.status == 200) {
				   Toast.hide();
				   Toast.success('添加煤种成功', 1);
				  var item = request.responseText;
		    	 item=eval("("+item+")");//转json
				 item=getDecAesString(item.paramer)
				 item=eval("("+item+")");//转json
				  var requests = new XMLHttpRequest();
				  	var b='userId='+EditCoal.phone+',companyId='+EditCoal.companyId;
					var n=getAesString(b);//加密
	            	requests.open('GET', APIS.MANAGER_CoalProductsList +'?K='+n,true);
		            requests.timeout = 8000;  
	             	requests.setRequestHeader("Content-Type", "application/json")
	            	requests.send();
		            requests.onreadystatechange = (e) => {
		            	if (requests.readyState !== 4) {
				        return;
			         }
			          if (requests.status == 200) {
				       var item = requests.responseText;
					    item=eval("("+item+")");//转json
			   	        item=getDecAesString(item.paramer)
			             item=eval("("+item+")");//转json
				        dispatch(CoalBys(item));
			       } else {
					     Toast.hide();
				          Toast.offline('网络连接失败!', 1);
		   	           }
		           };
				  
		        } else {
			       Toast.hide();
				  Toast.offline('网络连接失败!', 1);
			 }
		};

	 }
}

function  CoalBys(Coal){
	return {
		type: TYPES.Coal_IN,
		Coal
	}
}
function  Coalups(Coal){
	return {
		type: TYPES.CoalUP_IN,
		Coal
	}
}