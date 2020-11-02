(function(global){
	var ua = navigator.userAgent.toLowerCase();
	check = function(r){               
		return r.test(ua);  
	};
	version = function (is, regex) {
		var m;
		return (is && (m = regex.exec(ua))) ? parseFloat(m[1]) : 0;
	}
	var isOpera = check(/opera/);
	var isWebKit = check(/webkit/);
	var isChrome = check(/\bchrome\b/);
	var isSafari = !isChrome && check(/safari/);
	var isIE = !isOpera && check(/msie/);
	var isIE6 = isIE && check(/msie 6/);
	var isGecko = !isWebKit && check(/gecko/);
	var isFirefox = check(/\bfirefox/);
	var id = "phisActiveXObject"
	var sybId = "ocxObj";
	var xsybId = "xocxObj";
	
	var hzyb2014Id ="WebClientSiInterface";
	var codebase = "PHIS.cab";
	var CardActive = "CardActive"
	var smkOcx = "smkOcx"
	var bjq="bjqobj";
	var fsoid = "fsoObj";
	var html=null
	var sybhtml=null;
	var hzyb2014html = null;
	var cardhtml = null;
	var smlhtml = null;
	var bjqhtml = null;
	var fsohtml = null;
	var sykt2017id = "sykt2017Obj"
	if(isIE){
		html = "<OBJECT ProgID='PHIS.MedicalInsurance' classid='clsid:E28F7E48-2C08-4E42-8860-15681CA3FCD5' width='100' height='100' id='" + id + "' name='" +id+ "' codebase='" + codebase + "'></OBJECT>"
		sybhtml = "<OBJECT id='"+sybId+"' name='"+sybId+"' style='display:none' classid='CLSID:D4B0A700-B1C0-44AC-B5D1-962EAE3B91FB' codeBase='BargaingApplyV2_01050.ocx'></OBJECT>";
		hzyb2014html = "<OBJECT id='"+hzyb2014Id+"' name='"+hzyb2014Id+"' style='display:none' classid='CLSID:188DAF2C-27D8-4C03-9E35-A0A71EC4A77F'></OBJECT>";
		cardhtml = '<OBJECT ID="'+CardActive+'" CLASSID="CLSID:CBA5D514-3544-4E87-80D2-F1582FA87841" style="border:0px;width:0px;height:0px;" CODEBASE="LB.CAB#version=1,0,0,0"> </OBJECT>'
		smlhtml = '<OBJECT ID="' +smkOcx+ '" CLASSID="CLSID:E691A607-D1D2-48A7-A0EC-09511883A445" style="border:0px;width:0px;height:0px;" CODEBASE="MisPosOCX.ocx#version=1,0,0,0"> </OBJECT>'
		bjqhtml = '<OBJECT ID="' +bjq+ '" CLASSID="CLSID:F76DFBBF-BED6-4039-AD6F-7B01AB128BF0" style="border:0px;width:0px;height:0px;" > </OBJECT>'
		fsohtml='<object id="'+fsoid+'" TYPE="application/x-itst-activex" ALIGN="baseline" BORDER="0" WIDTH="0" HEIGHT="0" clsid="{0D43FE01-F093-11CF-8940-00A0C9054228}" ></object>'
		sykt2017html = "<OBJECT id='"+sykt2017id+"' name='"+sykt2017id+"' style='display:none' classid='CLSID:518F75A7-50BD-4970-9F7D-E25CDFF3725C' codeBase='YKTMedInterfaceOcx.ocx'></OBJECT>";
	}
	else {
		html = '<object id="' +id+ '" TYPE="application/x-itst-activex" ALIGN="baseline" BORDER="0" WIDTH="0" HEIGHT="0" clsid="{E28F7E48-2C08-4E42-8860-15681CA3FCD5}" codeBaseURL="' + codebase + '"></object>';
		sybhtml = '<object id="'+sybId+'" TYPE="application/x-itst-activex" ALIGN="baseline" BORDER="0" WIDTH="0" HEIGHT="0" clsid="{D4B0A700-B1C0-44AC-B5D1-962EAE3B91FB}" codeBaseURL="BargaingApplyV2_01050.ocx"></object>';
		hzyb2014html = '<object id="'+hzyb2014Id+'" TYPE="application/x-itst-activex" ALIGN="baseline" BORDER="0" WIDTH="0" HEIGHT="0" clsid="{CLSID:188DAF2C-27D8-4C03-9E35-A0A71EC4A77F}" ></object>';
		cardhtml = '<object id="' +CardActive+ '" TYPE="application/x-itst-activex" ALIGN="baseline" BORDER="0" WIDTH="0" HEIGHT="0" clsid="{CBA5D514-3544-4E87-80D2-F1582FA87841}" codeBaseURL="' + codebase + '"></object>'
		smlhtml = '<object id="' +smkOcx+ '" TYPE="application/x-itst-activex" ALIGN="baseline" BORDER="0" WIDTH="0" HEIGHT="0" clsid="{E691A607-D1D2-48A7-A0EC-09511883A445}" ></object>'
		bjqhtml='<object id="' +bjq+ '" TYPE="application/x-itst-activex" ALIGN="baseline" BORDER="0" WIDTH="0" HEIGHT="0" clsid="{F76DFBBF-BED6-4039-AD6F-7B01AB128BF0}" ></object>'
		fsohtml='<object id="'+fsoid+'" TYPE="application/x-itst-activex" ALIGN="baseline" BORDER="0" WIDTH="0" HEIGHT="0" clsid="{0D43FE01-F093-11CF-8940-00A0C9054228}" ></object>'
		
		xsybhtml = '<object id="'+xsybId+'" TYPE="application/x-itst-activex" ALIGN="baseline" BORDER="0" WIDTH="0" HEIGHT="0" clsid="{D04FEAEB-1A1B-4CFA-BDF8-FA73A5F0DDF8}" ></object>';
		sykt2017html= '<object id="'+sykt2017id+'" TYPE="application/x-itst-activex" ALIGN="baseline" BORDER="0" WIDTH="0" HEIGHT="0" clsid="{518F75A7-50BD-4970-9F7D-E25CDFF3725C}" ></object>';
	}
	global.$PhisActiveXObjectUtils = {
		initHtmlElement:function(){
//			if(!this.initHtmlElement){
			if(!this.ele){
			this.ele = document.createElement("div")
				this.ele.innerHTML = html;
			document.body.appendChild(this.ele);
			}
//			this.initHtmlElement = true;
//			}
		},
		getObject:function(){
			if (window.document[id]) {
				return window.document[id];
			}
			if (isIE) {
				if (document.embeds && document.embeds[id])
				  return document.embeds[id]; 
			} 
			else {
				return document.getElementById(id);
			}
		},
		initXSYBHtmlElement:function(){
			if(!this.initXSYBHtml){
				var ele = document.createElement("div")
					ele.setAttribute("width","0px")
					ele.setAttribute("height","0px")
					ele.innerHTML = xsybhtml;
				document.body.appendChild(ele);
				this.initXSYBHtml = true;
			}
		},
		getXSybObject:function(){
			if (window.document[xsybId]) {
				return window.document[xsybId];
			}
			if (isIE) {
				if (document.embeds && document.embeds[xsybId])
				  return document.embeds[xsybId]; 
			} 
			else {
				return document.getElementById(xsybId);
			}
		},
		initSYBHtmlElement:function(){
			if(!this.initSYBHtml){
				var ele = document.createElement("div")
					ele.setAttribute("width","0px")
					ele.setAttribute("height","0px")
					ele.innerHTML = sybhtml;
				document.body.appendChild(ele);
				this.initSYBHtml = true;
			}
		},
		getSybObject:function(){
			if (window.document[sybId]) {
				return window.document[sybId];
			}
			if (isIE) {
				if (document.embeds && document.embeds[sybId])
				  return document.embeds[sybId]; 
			} 
			else {
				return document.getElementById(sybId);
			}
		},
		initSYKT2017HtmlElement:function(){
			if(!this.initSYBHtml){
				var ele = document.createElement("div")
					ele.setAttribute("width","0px")
					ele.setAttribute("height","0px")
					ele.innerHTML = sykt2017html;
				document.body.appendChild(ele);
				this.initSYKT2017Html = true;
			}
		},
		getSykt2017Object:function(){
			if (window.document[sykt2017id]) {
				return window.document[sykt2017id];
			}
			if (isIE) {
				if (document.embeds && document.embeds[sykt2017id])
				  return document.embeds[sykt2017id]; 
			} 
			else {
				return document.getElementById(sykt2017id);
			}
		},
		sykt2017UserBargaingApply : function(jym,yybm,data1,data2){
			this.initSYKT2017HtmlElement();
			var obj = this.getSykt2017Object();
			if(!this.sykt2017Init){
				var reInitCord = obj.f_Init("",yybm);
				if(reInitCord<0){
					obj.reCord = reInitCord;
					obj.ErrMsg = obj.OutData;
					return obj;
				}
				this.sykt2017Init = true;
			}
			var reCord =null;
			if(parseInt(jym)>3000&&parseInt(jym)<4000 ){
				reCord = obj.f_DataTransApply(jym,yybm,data1,data2)
			}else{
				console.log(jym+","+yybm+","+data1+","+data2)
				 reCord = obj.f_UserApply(jym,yybm,data1,data2)
			}
			obj.reCord = reCord;
			if(reCord<0){
				obj.ErrMsg = obj.OutData.split("$$")[1].split("~")[1];
				obj.RetData=obj.OutData;
				return obj;
			}
			obj.reCord = obj.OutData.split("$$")[1].split("~")[0]
			if(obj.reCord<0){
				obj.ErrMsg = obj.OutData.split("$$")[1].split("~")[1];
			}
			obj.RetData=obj.OutData;
			return obj;
		},
		initHZYB2014HtmlElement:function(){
			if(!this.initHZYB2014Html){
				var ele = document.createElement("div")
					ele.setAttribute("width","0px")
					ele.setAttribute("height","0px")
					ele.innerHTML = hzyb2014html;
				document.body.appendChild(ele);
				this.initHZYB2014Html = true;
			}
		},
		getHZYB2014Object:function(){
			if (window.document[hzyb2014Id]) {
				return window.document[hzyb2014Id];
			}
			if (isIE) {
				if (document.embeds && document.embeds[hzyb2014Id])
				  return document.embeds[hzyb2014Id]; 
			} 
			else {
				return document.getElementById(hzyb2014Id);
			}
		},
		szybUserBargaingApply : function(jym,no,data1,data2){
			this.initSYBHtmlElement();
			var obj = this.getSybObject();
			if(obj==null){
				this.initSYBHtml = false;
				this.initSYBHtmlElement();
				obj = this.getSybObject();
			}
			if(!this.UserBargaingInit){
				var reInitCord = obj.UserBargaingInit("","");
				if(reInitCord<0){
					obj.reCord = reInitCord;
					if(!obj.ErrMsg){
						obj.ErrMsg = obj.RetData.split("$$")[1].split("~")[1];
					}
					return obj;
				}
				this.UserBargaingInit = true;
			}
			var reCord = obj.UserBargaingApply(jym,no,data1,data2)
			obj.reCord = reCord;
			if(reCord<0){
				if(!obj.ErrMsg){
					obj.ErrMsg = obj.RetData.split("$$")[1].split("~")[1];
				}
			}
			return obj;
		},
		syktUserBargaingApply : function(jym,no,data1,data2){
			this.initSYBHtmlElement();
			var obj = this.getSybObject();
			if(!this.syktInit){
				var reInitCord = obj.UserBargaingInit3("",data2);
				if(reInitCord<0){
					obj.reCord = reInitCord;
					if(!obj.ErrMsg){
						obj.ErrMsg = obj.RetData.split("$$")[1].split("~")[1];
					}
					return obj;
				}
				this.syktInit = true;
			}
			var reCord = obj.UserBargaingApply3(jym,no,data1,data2)
			obj.reCord = reCord;
			if(reCord<0){
				if(!obj.ErrMsg){
					obj.ErrMsg = obj.RetData.split("$$")[1].split("~")[1];
				}
			}
			return obj;
		},
		/**余杭医保(全局的初始化对象返回值yhybInit,各方法调用时都会使用)*/
		getYHYBObject : function(){
			$PhisActiveXObjectUtils.initHtmlElement();
			var yhobj = $PhisActiveXObjectUtils.getObject();
			//是否已初始化
			if(!this.yhybInit){
				//获取余杭医保配置
				var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : "medicareYHService",
								serviceAction : "getYHServer"
							});
				if(ret.code > 300){
					this.processReturnMsg(ret.code, ret.msg);
					return;
				}
				
				var yhip = eval(ret.json).YHYBSERVERIP;
				var yhport = eval(ret.json).YHYBSERVERPORT;
				var yhservlet = eval(ret.json).YHYBSERVERSERVLET;
				
				//应用服务器IP地址,应用服务器端口号,Servlet为应用服务器入口Servlet的名称
				this.yhybInit = yhobj.Hy_newinterfacewithinit(yhip, yhport, yhservlet);
				if(!this.yhybInit || this.yhybInit < 0){
					alert("余杭医保接口初始化失败!");
					return;
				}
			}
			//是否已登录
			if(!this.yhybLogin){
				var res_l = yhobj.Hy_start(this.yhybInit,0);//登录
				yhobj.Hy_putcol(this.yhybInit,"staff_id","staff1")
				yhobj.Hy_putcol(this.yhybInit,"staff_pwd","staff_pwd")
				var res = yhobj.Hy_run(this.yhybInit);
				if(res < 0){
					var err = yhobj.Hy_getmessage(this.yhybInit);
					MyMessageTip.msg("提示", "登录到余杭医保中心失败!"+res+":"+err, true);
					return;
				}else{
					this.yhybLogin = true;
					MyMessageTip.msg("提示", "成功登录到余杭医保中心!", false);
				}
			}
			yhobj.yhybInit = this.yhybInit;
			return yhobj;
		}
	},
	global.$ActiveXObjectUtils = {
			
			
			readCard : function ()
			{
				var spobj = this.getObject();
				if(!(spobj.ReadCard) || !(typeof(spobj.ReadCard) == "function")){
					alert('医保控件加载失败,请重启浏览器后再试！');
					return;
				}
	            var rs = spobj.ReadCard();
	            return rs;
			},
			
			initHtmlElement:function(){
//				if(isIE){
//					alert('aa')
//					var f = document.createElement("form");
//					document.body.appendChild(f);
//					f.name = 'form1';
//					var ele = document.createElement("div")
//					ele.setAttribute("width","0px")
//					ele.setAttribute("height","0px")
//					ele.innerHTML = html;
//					f.appendChild(ele);
//				}else{
//					alert('ff')
					var ele = document.createElement("div")
					ele.setAttribute("width","0px")
					ele.setAttribute("height","0px")
					ele.innerHTML = cardhtml;
					document.body.appendChild(ele);
//				}
			},
			getObject:function(){
				if(isIE){
					return document.getElementById(CardActive);
				}
				if (window.document[CardActive]) {
					return window.document[CardActive];
				}
				if (isIE) {
					if (document.embeds && document.embeds[CardActive]){
					  return document.embeds[CardActive]; 
					}else{
						return document.getElementById(CardActive);
					}
				} 
				else {
					return document.getElementById(CardActive);
				}
			},
			
			/**
		 	 * 读卡,返回对象
		 	 */
		 	readCardToObject : function(){
		 		var rs = this.readCard();
		 		try {
			 		var body = {};
			 		body['CardID'] = rs.substring(0,32);//卡的识别码
			 		body['CardSN'] = rs.substring(32,41);//卡号
			 		body['CityID'] = rs.substring(41,47);//所在城市代码
			 		body['PID'] = rs.substring(47,652);//公民身份号码
			 		body['Name'] = rs.substring(65,95);//姓名
			 		body['Sex'] = rs.substring(95,96);//性别
			 		body['BirthDate'] = rs.substring(96,106);//出生日期
			 		body['MICode'] = rs.substring(106,112);//医疗保险统筹区编码
	//		 		body['MITransID'] = rs.substring(112, 127);//交易流水号
	//		 		body['MIMAC'] = rs.substring(127);//社保卡认证码
			 		//以下两位与市民卡公司沟通写死
			 		body['MITransID'] = '000000000000000';//交易流水号
			 		body['MIMAC'] = '00000000';//社保卡认证码
			 		return body;
		 		}catch (e) {
		 			return
		 		}
		 	}
		}
	global.$SMKFeeActiveXObjectUtils = {
			/**
			 * 初始化
			 * 
			 * @return 0+"00"为成功
			 */
			init : function()
			{
				var sRet = this.MisPosHandle(1000,0,"");
				return this.toArray(sRet);
			  //0#00
			},
			/**
			 * 卸载
			 * 
			 * @return 0+"00"为成功
			 */
			exit : function()
			{
				var sRet = this.MisPosHandle(2000,0,"");
				return this.toArray(sRet);
			  //0#00
			},
			/**
			 * 签到
			 * 
			 * @return 0+"00"为成功
			 */
			signIn : function()
			{
			
				var sRet = this.MisPosHandle(6215,0,"");
				return this.toArray(sRet);
			  //0#00
			},
			/**
			 * 消费
			 * @param amt ： 交易金额
			 * 
			 * @return 应答参数，0+"00"为成功 +商户编号(12字节)+ 终端编号(8字节)+ 账户类型(4字节)+ 交易类型(4字节)+ 物理卡号(10字节)+ 
			 * 					交易日期（4字节）+交易时间(6字节)+ 交易参考号（12字节）+批次号(6字节)+POS机流水号、凭证号（6字节）+金额(12字节)+
			 * 					卡面号凹码(8字节)+卡号(9字节)+账户余额(12字节)+小票余额打印限额（12字节）
			 */
			consume : function(amt,src){
				if(src == null) {
					src = '';
				}
				amt = amt * 100;//以分为单位，所以需要乘以100.查询余额也是以分为单位
				var sRet = this.MisPosHandle(81105,amt,src);
				return this.toArray(sRet);
			},
			/**
			 * 查询余额
			 * @return 应答码 0+(2个字节)+返回金额(12个字节)+卡面号(9字节)
			 */
			queryBalance : function ()
			{
				var sRet = this.MisPosHandle(81025,0,"");
				return this.toArray(sRet);
			},
			/**
			 * 消费撤销
			 * @param amt ： 交易金额
			 * @param src ：交易流水号(6个字节)
			 * @param BatNo : 原交易批次号
			 * @param PosSeq : 原交易POS机流水号
			 * @param PosID : 原交易终端编号
			 * 
			 * @return 应答参数，0+“00”为成功 +商户编号(12字节)+ 终端编号(8字节)+ 账户类型(4字节)+ 交易类型(4字节)+ 物理卡号(10字节)+
			 * 				交易日期（4字节）+交易时间(6字节)+ 交易参考号（12字节）+批次号(6字节)+POS机流水号、凭证号（6字节）+金额(12字节)+
			 * 				卡面号凹码(8字节)+ 卡号(9字节)+ 账户余额(12字节)+小票余额打印限额（12字节）
			 */
			txnCancel : function(amt, src, BatNo, PosSeq, PosID){
				amt = amt * 100;//（以分为单位）
				var sRet = this.MisPosHandle2(86105,amt,src, BatNo, PosSeq, PosID);
				return this.toArray(sRet);
				
			},
			
			MisPosHandle : function (loprType,amt,src)
			{
				 var smkOcx = this.getObject();
				 if(smkOcx==null){
					this.smkinithtml = false;
					this.initHtmlElement();
					smkOcx = this.getObject();
				}
				 var sRet = smkOcx.MisPosHandle(loprType,amt,src);
//				 try{
//					 if('81105'==loprType){
//						 phis.script.rmi.jsonRequest({
//							serviceId : "clinicChargesProcessingService",
//							serviceAction : "saveSmkjslog",
//							body : loprType+"@"+amt+'@'+sRet
//						}, function(code, msg, json) {
//						}, this)
//					 }
//				 }catch(e){}
				 return sRet;
			},
			/**
			 * 暂时在消费撤销的时候调用
			 */
			MisPosHandle2 : function (loprType,amt,src,BatNo, PosSeq, PosID)
			{
				 var smkOcx = this.getObject();
				 if(smkOcx==null){
						this.smkinithtml = false;
						this.initHtmlElement();
						smkOcx = this.getObject();
					}
				 var sRet = smkOcx.MisPosHandle2(loprType,amt,src,BatNo, PosSeq, PosID);
				 return sRet;
			},
			/**
			 * 转换成数组
			 */
			toArray : function (msg)
			{
				//alert(msg);
				return msg.split("#");
			},
			/**
			 * 初始化页面元素
			 *       在页面上定义一个div
			 */
			initHtmlElement:function(){
				if(!this.smkinithtml){
					var ele = document.createElement("div")
						ele.setAttribute("width","0px")
						ele.setAttribute("height","0px")
						ele.innerHTML = smlhtml;
					document.body.appendChild(ele);
					this.smkinithtml = true;
				}
			},
			/**
			 * 获取OBJECT元素
			 * 		即OCX
			 */
			getObject:function(){
				if (window.document[smkOcx]) {
					return window.document[smkOcx];
				}
				if (isIE) {
					if (document.embeds && document.embeds[smkOcx]){
					  return document.embeds[smkOcx]; 
					}else{
						return document.getElementById(smkOcx);
					}
				} 
				else {
					return document.getElementById(smkOcx);
				}
			}
		}
		
	global.$BJQActiveXObjectUtils = {
			dsbdll : function (port, outstring)
			{
				try{
					var bjqobject = this.getObject();
					if(!bjqobject){
						this.initBJQHtml = false;
						this.initHtmlElement();
						bjqobject = this.getObject();
					}
		            bjqobject.SendInfo(port, outstring);
				}catch(e){}
			},
			
			initHtmlElement:function(){
				if(!this.initBJQHtml){
					var ele = document.createElement("div")
						ele.setAttribute("width","0px")
						ele.setAttribute("height","0px")
						ele.innerHTML = bjqhtml;
					document.body.appendChild(ele);
					this.initBJQHtml = true;
				}
			},
			getObject:function(){
				if (window.document[bjq]) {
					return window.document[bjq];
				}
				if (isIE) {
					if (document.embeds && document.embeds[bjq])
					  return document.embeds[bjq]; 
				} 
				else {
					return document.getElementById(bjq);
				}
			}
		}
	
	global.$FSActiveXObjectUtils = {
			initHtmlElement:function(){
				if(!this.initFSHtml){
					var ele = document.createElement("div")
						ele.setAttribute("width","0px")
						ele.setAttribute("height","0px")
						ele.innerHTML = fsohtml;
					document.body.appendChild(ele);
					this.initFSHtml = true;
				}
			},
			getObject:function(){
				if (window.document[fsoid]) {
					return window.document[fsoid];
				}
				if (isIE) {
					if (document.embeds && document.embeds[fsoid])
					  return document.embeds[fsoid]; 
				} 
				else {
					return document.getElementById(fsoid);
				}
			}
		}
})(this)