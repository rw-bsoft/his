$package("phis.application.yb.sgyb.script")
$import("phis.script.common","phis.script.widgets.MyMessageTip")
phis.application.yb.sgyb.script.MedicareUtils = {
	//************************************
	//方法例子1开始
	//以下方法 适用于医保的参数格式是 A|B|C|D这种格式,其他分隔符雷同
	/**
	 * 将数据按特定的格式拆分,存到map,key是float类型,用于医保返回数据本地转成map方便处理 需要注意的是format和StrData在拆分后要保持位数一致,并且StrData拆分后的数据是数字类型
	 * @param {} format 需要存到map的text的字符串 用|分割  例如 "mc|sj|dz"
	 * @param {} StrData 需要存到map的key的字符串 格式由split参数确定 假设是&  例如 1&2&3
	 * @param {} split StrData需要拆分的格式,例如&
	 * @return {} 返回map 例如{"mc":1,"sj":2,"dz":3}
	 */
	StrToObj_float : function(format, StrData, split) {
		var formatArr = format.split("|");
		var dataArr = StrData.split(split);
		var outData = {};
		for (var i = 0; i < formatArr.length; i++) {
			var reFloat = parseFloat(dataArr[i])
			outData[formatArr[i]] = reFloat;
		}
		return outData;
	},
	/**
	 * 将数据按特定的格式拆分,存到map,用于医保返回数据本地转成map方便处理 需要注意的是format和StrData在拆分后要保持位数一致
	 * @param {} format 需要存到map的text的字符串 用|分割  例如 "mc|sj|dz"
	 * @param {} StrData 需要存到map的key的字符串 格式由split参数确定 假设是&  例如 1&2&3
	 * @param {} split StrData需要拆分的格式,例如&
	 * @return {} 返回map 例如{"mc":1,"sj":2,"dz":3}
	 */
	StrToObj : function(format, StrData, split) {
		var formatArr = format.split("|");
		var dataArr = StrData.split(split);
		var outData = {};
		for (var i = 0; i < formatArr.length; i++) {
			outData[formatArr[i]] = dataArr[i];
		}
		return outData;
	},
	/**
	 * 将map数据转成指定格式的字符串,用于将后台传过来的map数据转成医保需要的格式,需要注意的是format和data在拆分后要保持位数一致
	 * @param {} format 需要存到map的text的字符串 用|分割  例如 "mc|sj|dz"
	 * @param {} data map数据 例如{"mc":1,"sj":2,"dz":3}
	 * @param {} split 医保需要个格式分隔符 例如&
	 * @return {} 返回字符串 1&2&3
	 */
	ObjToStr : function(format, data, split) {
		var formatArr = format.split("|");
		var outBrxx = "";
		for (var i = 0; i < formatArr.length; i++) {
			if (i != 0) {
				outBrxx += split
			}
			outBrxx += data[formatArr[i]];
		}
		return outBrxx;
	},

	/**
	 * 将医保返回的outputStr转成Map类型
	 * 
	 * @param {}
	 *            outputStr 医保返回的
	 * @param {}
	 *            reg 格式 如 个人编号|单位编号 返回map格式数据 如{"个人编号":123,"单位编号":123}
	 */
	outputStrToMap : function(outputStr, reg) {
		var regs = reg.split("|");
		var out = outputStr.split("|");
		var regLen = regs.length;
		var outLen = out.length;
		var body = {};
		for (var i = 0; i < regLen; i++) {
			if (i < outLen) {
				body[regs[i]] = out[i]
			}
		}
		return body;
	},
	/**
	 * 向服务端请求数据，并返回请求结果
	 * 
	 * @param {}
	 *            ffm 方法名
	 * @param {}
	 *            body 请求数据
	 */
	requestServer : function(ffm, body) {
		var ret = phis.script.rmi.miniJsonRequestSync({
					serviceId : "medicareService",
					serviceAction : ffm,
					body : body
				});
		if (ret.code > 300) {
			this.processReturnMsg(ret.code, ret.msg);
			return null;
		}
		return ret;
	},
	//方法例子1结束
	//*****************************************
	//****************************************
	//方法例子2开始
	//以下方法 适用于医保的参数格式xml的格式
	/**
	 * 将Map转成String,简单的将map的key拼成一个String字符串
	 * @param {} initData
	 * @return {}
	 */
	Map2String : function(initData) {
		var reStr = "";
		for (var str in initData) {
			reStr += initData[str];
		}
		return reStr;
	},
	/**
	 * 将map的key截取指定长度拼成字符串
	 * @param {} map 需要频次字符串的数据 例如{a:"123456",b:"abcd",c:"!@#"}
	 * @param {} arr 对应的Map每个key需要截取的长度 例如 [4,3,2]
	 * 结果 1234abc!@
	 */
	Map2String_2 : function(map, arr) {
		var i = 0;
		for (var id in map) {
			i++;
		}
		if (i != arr.length) {
			Ext.Msg.alert("所传的map长度和arr长度不一致!");
			return;
		}
		i = 0;
		var ret = "";
		for (var id in map) {
			ret += this.string2Length(map[id], arr[i]);
			i++;
		}
		return ret;
	},
	/**
	 * 将一个字符串转换成map类型
	 * 注:截取的长度是按字符来的 一个中文算2个长度
	 * @param {}
	 *            data 要转换的字符串
	 * @param {}
	 *            arr1 将要转换的map的key
	 * @param {}
	 *            arr2 当前位子所需长度 如[4,16,5,5,5]表示第一个占4个字符, 第二个占16个字符,第三个占5个字符 例如
	 *            data="abcdefg" arr1=["code1","code2"] arr2=[3,4]
	 *            调用方法后返回值是{"code1":"abc","code2":"defg"}
	 */
	String2Map : function(data, arr1, arr2) {
		var ret = {};
		if (arr1.length != arr2.length) {
			Ext.Msg.alert("arr1和arr2长度不相同,请检查参数");
			return;
		}
		i = 0;
		for (var i = 0; i < arr2.length; i++) {
			var k = arr2[i];
			if (isNaN(k)) {
				Ext.Msg.alert("arr2第" + (i + 1) + "个参数key不是数字,请检查");
				return;
			}
			for (var j = Math.floor(k / 2); j <= k; j++) {
				if (this.realLength(data.substring(0, j)) >= k) {
					ret[arr1[i]] = data.substring(0, j)
					data = data.substring(j)
					break;
				}
			}
		}
		return ret;
	},
	/*
	 * 计算字符串的实际长度,一个中文算2个长度
	 */
	realLength : function(str) {
		str = (str + "").replace(/[^\x00-\xff]/g, "**")
		var realLength = 0, len = str.length, charCode = -1;
		for (var i = 0; i < len; i++) {
			charCode = str.charCodeAt(i);
			if (charCode >= 0 && charCode <= 128)
				realLength += 1;
			else
				realLength += 2;
		}
		return realLength;
	},
	/**
	 * 将字符串补全长度
	 * 
	 * @param {} s 需要补全的字符串 例如 "abc"
	 *            
	 * @param {} l 字符串需要的长度 例如 5
	 *   结果:"abc  "        
	 */
	string2Length : function(s, l) {
		if (isNaN(l)) {
			// Ext.Msg.alert("长度参数非数字,请检查参数");
			return s
		}
		if (this.realLength(s) > l) {
			// Ext.Msg.alert("字符串长度过长");
			return this.string2Length(s.substring(this.realLength(s) - l), l);
			// return s.substring(this.realLength(s)-l);
		}
		if (this.realLength(s) == l) {
			return s;
		}
		var ret = s;
		for (var i = this.realLength(s); i < l; i++) {
			ret += " ";
		}
		return ret;
	},
	/**
	 * 将字符串的左边按指定长度增加0
	 * @param {} s 需要增加的数字 例如  12345
	 * @param {} l 需要的长度 例如 10
	 * @return {} 0000012345
	 */
	leftadd0 : function(s, l) {
		if (isNaN(l)) {
			// Ext.Msg.alert("长度参数非数字,请检查参数");
			return s
		}
		if (this.realLength(s) > l) {
			// Ext.Msg.alert("字符串长度过长");
			return this.leftadd0(s.substring(this.realLength(s) - l), l);
		}
		if (this.realLength(s) == l) {
			return s;
		}
		var ret = s;
		for (var i = this.realLength(s); i < l; i++) {
			ret = "0" + ret;
		}
		return ret;
	},
	// 去掉空格.
	qkg : function(str) {
		var ret = (str + "").replace(/[ ]/g, "")
		return ret;
	},
	// 数字长度补全0,第一个参数是要转换的数字 第二个是长度,第三个是保留小数点几位默认2位
	number2length : function(number, l, bl) {
		var ret = "";
		if (isNaN(l)) {
			Ext.Msg.alert("长度参数非数字,请检查参数");
			return
		}
		number = parseFloat(number).toFixed(bl || 2);
		if (number == 0) {
			for (var i = 0; i < l - 5; i++) {
				ret += "0";
			}
			ret += ".0000";
		} else {
			var length = parseInt(l) - ((number + "").length)
			for (var i = 0; i < length; i++) {
				ret += "0";
			}
			ret += number;
		}
		return ret;
	},
	//json转成xml
	json2xml:function(jsonObj,rootNode){
	if(Object.prototype.toString.call(jsonObj) !== '[object Object]') return;
	var xmldoc=new Array();
	xmldoc.push('');
	if(rootNode) xmldoc.push('<'+rootNode+'>');
	function toXml(jsonObj){
		for(var m in jsonObj){
			if(Object.prototype.toString.call(jsonObj[m]) === '[object Array]'){
				xmldoc.push('<'+m+'>');
				for(var i=0,len=jsonObj[m].length;i<len;i++){
					toXml(jsonObj[m][i]);
				}
				xmldoc.push('</'+m+'>');
			}
			else if(Object.prototype.toString.call(jsonObj[m]) === '[object Object]'){
				xmldoc.push('<'+m+'>');
				toXml(jsonObj[m]);
				xmldoc.push('</'+m+'>');
			}else{
				xmldoc.push('<'+m+'>'+jsonObj[m]+'</'+m+'>');
			}
		}
	}
	toXml(jsonObj);
	if(rootNode) xmldoc.push('');
	return xmldoc.join('');
	},
	/**
	 * 适用于韶关医保从OCX读取返回数据的字符,字符串格式
	 */
	String2Json: function(str){		
		var res = str.split("|");
		var dkxx  ={};	
		for(var i=0; i<res.length; i++){
			item = res[i].split("=");
			if(item.length>1){
				dkxx[item[0]] = item[1].trim();				
			}
		}
		return dkxx;
	}
}