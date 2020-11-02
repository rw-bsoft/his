﻿$package("phis.script");
phis.script.Phisinterface={
	// 页面加入对象
	addPKPHISOBJHtmlElement:function(){
		var ua=navigator.userAgent.toLowerCase();
		check=function(r){
			return r.test(ua);
		};
		version=function(is,regex){
			var m;
			return (is&&(m=regex.exec(ua)))?parseFloat(m[1]):0;
		}
		var isOpera=check(/opera/);
		var isWebKit=check(/webkit/);
		var isChrome=check(/\bchrome\b/);
		var isSafari=!isChrome&&check(/safari/);
		var isIE=!isOpera&&check(/msie/);
		var isIE6=isIE&&check(/msie 6/);
		var isGecko=!isWebKit&&check(/gecko/);
		var isFirefox=check(/\bfirefox/);
		var html=""
		this.isIE=isIE;
		if(isIE){
			html='<OBJECT id="PKPHISOBJ" name="PKPHISOBJ" width="0px" height="0px" CLASSID="CLSID:C393D515-609F-41B1-AB66-736358D547EE"></OBJECT>';
		}else{
			html='<object id="PKPHISOBJ" ALIGN="baseline" type="application/x-itst-activex" align="baseline" style="border:0px;width:0px;height:0px;" clsid="{C393D515-609F-41B1-AB66-736358D547EE}"></object>';
		}
		var node=document.getElementById("PKPHISOBJ");
		if(node){
			node.parentNode.removeChild(node);
		}
		var ele=document.createElement("div");
		ele.setAttribute("width","0px");
		ele.setAttribute("height","0px");
		ele.innerHTML=html;
		document.body.appendChild(ele);
	},
	// 东软初始化方法
	drinterfaceinit:function(){
		var active=document.getElementById("PKPHISOBJ");
		return active.NJJBINIT();
	},
	// 东软业务方法
	drinterfacebusinesshandle:function(str){
		var active=document.getElementById("PKPHISOBJ");
		active.NJJBINIT();
		return active.NJJBBUSINESSHANDLE(str);
	},
	StrToObj:function(format,StrData,split){
		var formatArr=format.split("|");
		var dataArr=StrData.split(split);
		var outData={};
		for(var i=0;i<formatArr.length;i++){
			outData[formatArr[i]]=dataArr[i];
		}
		return outData;
	},
	buildjylsh:function(){
		var text=this.dogetdictext("phis.dictionary.NJJB","1");
		if(text==""){
			return;
		}
		var time=new Date();
		var timestr=this.buildtimestr(time);
		var ran=Math.floor(Math.random()*9000)+1000;
		var jylsh=timestr+"-"+text+"-"+ran;
		return jylsh;
	},
	// 构建参数字符串
	buildstr_zy:function(ywh,ywzqh,data){
		var text=this.dogetdictext("phis.dictionary.NJJB","1");
		if(text==""){
			return;
		}
		var time=new Date();
		var timestr=this.buildtimestr(time);
		var ran=Math.floor(Math.random()*9000)+1000;
		var jylsh=timestr+"-"+text+"-"+ran;
		if(data.JYLSH){
			jylsh=data.JYLSH;
		}

		var uid=this.mainApp.uid+"";
		var str=ywh+"^"+text+"^"+uid.substring(0,8)+"^"+ywzqh+"^"+jylsh+"^0^0^";
		return str;
	},
	// 构建参数字符串
	buildstr:function(ywh,ywzqh,data){
		var text=this.dogetdictext("phis.dictionary.NJJB","1");
		if(text==""){
			return;
		}
		var time=new Date();
		var timestr=this.buildtimestr(time);
		var ran=Math.floor(Math.random()*9000)+1000;
		var jylsh=timestr+"-"+text+"-"+ran;
		var uid=this.mainApp.uid+"";
		if(data.JYLSH){
			jylsh=data.JYLSH;
		}
		var str=str=ywh+"^"+text+"^"+uid.substring(0,8)+"^"+ywzqh+"^"+jylsh+"^0^0^";
		if(ywh=="2210"){// 登记 data.type = 2 入院登记
			if(!data.type&&!(data.type==2)){
				data.RYSJ=this.buildtimestr(new Date());
				data.BQMC="";
				data.CWH="";
				data.ZYH="";
			}
			if(!data.ZSESHBZKH){
				data.ZSESHBZKH="";
			}
			str+=data.NJJBLSH+"|"+data.YLLB+"|"+data.RYSJ+"|"+data.JBBM+"|"+data.BQMC+"|"+data.KSDM+"|"+data.CWH+"|"+data.YSBM
					+"|"+data.JBR+"|"+data.LXDH+"|"+data.SHBZKH+"|"+data.ZYH+"|"+data.ZSESHBZKH+"||||";
		}else if(ywh=="2240"){// 取消挂号
			str+=data.NJJBLSH+"|"+data.JBR+"|";
		}else if(ywh=="2320"){// 撤销费用明细
			str+=data.LSH+"|"+data.CFH+"|"+data.CFLSH+"|"+data.JBR+"|"
		}else if(ywh=="2230"){// 修改登记信息
			str+=data.NJJBLSH+"|"+data.YLLB+"|"+data.GHSJ+"|"+data.YBJBBM+"||"+data.KSDM+"||"+data.YSDM+"|"+data.JBR+"|"
					+data.LXDH+"|||||||";
		}else if(ywh=="2310"){// 处方明细上报
			var sfmx=data.sfmx;
			var cfh=data.NJJBLSH+"-00";
			if(data.CFH&&data.CFH!=""){
				cfh=data.CFH;
			}
			for(var i=0;i<sfmx.length;i++){
				var one=sfmx[i];
				if(i==0){
					str+=data.NJJBLSH+"|"
				}else{
					str+="$"+data.NJJBLSH+"|"
				}
				str+=one.XMZL+"|"+cfh+"|"+cfh+"-";
				if(one.limit&&one.limit!=""){
					str+=one.limit+"";
				}else{
					str+=i+"";
				}
				str+="|"+this.buildkfrq(one.KFRQ)+"|"+one.ZBM+"|"+one.DJ+"|"+one.SL+"|"+one.HJJE+"|"+one.CFTS+"|"+one.YSDM+"|"
						+one.KSDM+"|"+data.JBR+"|"+one.JJDW+"|"+data.YBJBBM+"|||||"
			}
		}else if(ywh=="2420"||ywh=="2410"){// 费用预结算、结算
			str+=data.NJJBLSH+"|"+data.DJH+"|"+data.YLLB+"|"+this.buildtimestr(data.JSRQ)+"|"+this.buildtimestr(data.CYRQ)+"|"
					+data.CYYY+"|"+data.CYZDJBBM+"|"+data.YJSLB+"|"+data.ZTJSBZ+"|"+data.JBR+"|"+data.FMRQ+"|"+data.CC+"|"
					+data.TRS+"|"+data.SHBZKH+"|"+data.ZYYYBH+"|"+data.KSDM+"|"+data.YSDM+"|"+data.SFWGHFJS+"|"+data.ZSESHBZKH
					+"|"+data.SSSFCHBZ+"|"
		}else if(ywh=="2430"){// 费用结算撤销
			str+=data.NJJBLSH+"|"+data.DJH+"|"+this.buildtimestr(data.JSCXRQ)+"|"+data.JBR+"|"+data.SFBLCFBZ+"|"+data.BY1+"|"
					+data.BY2+"|"+data.BY3+"|"+data.BY4+"|"+data.BY5+"|"+data.BY6+"|"
		}else if(ywh=="1120"||ywh=="1100"||ywh=="1101"){// 总额对账
			str+=data.DZKSSJ+"|"+data.DZJSSJ
		}else if(ywh=="2421"){
			str+="2410|"+data.LSH
		}
		return str+"^";
	},
	buildkfrq:function(KFRQ){
		if(KFRQ&&KFRQ!="null"){
			return KFRQ
		}else{
			return this.buildtimestr(new Date());
		}
	},
	// 构建时间字符串
	buildtimestr:function(time){
		if(!time&&time==""){
			return "";
		}
		var timestr=time.getFullYear();
		var y=time.getMonth();
		if(y<9){
			y+=1;
			timestr+="0"+y;
		}else{
			y+=1;
			timestr+=""+y;
		}
		var r=time.getDate();
		if(r<10){
			timestr+="0"+r
		}else{
			timestr+=""+r
		}
		var s=time.getHours();
		if(s<10){
			timestr+="0"+s
		}else{
			timestr+=""+s
		}
		var f=time.getMinutes();
		if(f<10){
			timestr+="0"+f
		}else{
			timestr+=""+f
		}
		var m=time.getSeconds();
		if(m<10){
			timestr+="0"+m
		}else{
			timestr+=""+m
		}
		var t=this.mainApp.serverDate.replace("-","");
		t=t.replace("-","");
		if(timestr.substring(0,8)!=t){
			MyMessageTip.msg("重要提示","系统检测到您的电脑时间与服务器时间不符,请修改后进行业务操作！");
		}
		return timestr
	},
	// 获取医院医保编码
	dogetdictext:function(dic,type){
		var dicName={
			id:dic
		};
		var dic=util.dictionary.DictionaryLoader.load(dicName);
		var di;
		if(type=="1"){
			di=dic.wraper[this.mainApp.deptId];
		}else{
			// 待完善
			di=dic.wraper[this.mainApp.deptId];
		}
		var text=""
		if(di){
			text=di.text;
		}
		if(text==""){
			alert("未找字典"+dic+"中的配置信息")
			return "";
		}
		return text;
	},
	getywzqh:function(){
		var zqhbody={};
		zqhbody.USERID=this.mainApp.uid;
		var ret=phis.script.rmi.miniJsonRequestSync({
					serviceId:"phis.NjjbService",
					serviceAction:"getywzqh",
					body:zqhbody
				});
		if(ret.code<=300){
			return ret.json.YWZQH;
		}else{
			MyMessageTip.msg("提示","获取业务周期号失败",true);
			return false;
		}
	},
	readFile:function(path){
		var ct="";
		if(this.isIE){
			try{
				var fso;
				fso=new ActiveXObject("Scripting.FileSystemObject");
				var openf1=fso.OpenTextFile(path);
				ct=openf1.ReadAll();
			}catch(e){
				alert(e)
			}
		}else{// 非IE
			var phis=document.getElementById("PKPHISOBJ");
			ct=phis.readfile(path);
		}
		return ct;
	}
}