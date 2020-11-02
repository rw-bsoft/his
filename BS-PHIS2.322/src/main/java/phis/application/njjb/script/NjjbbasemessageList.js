$package("phis.application.njjb.script");

$import("app.modules.list.SimpleListView","phis.script.Phisinterface");

phis.application.njjb.script.NjjbbasemessageList = function(cfg) {
	this.exContext = {};
	Ext.apply(this,phis.script.Phisinterface);
	phis.application.njjb.script.NjjbbasemessageList.superclass.constructor.apply(this,
			[cfg]);
}
Ext.extend(phis.application.njjb.script.NjjbbasemessageList,
		app.modules.list.SimpleListView, {
			//初始化
			doInitialization:function(){
				this.addPKPHISOBJHtmlElement();
				alert(this.drinterfaceinit());
			},
			//签到
			doSign:function(){
				var text=this.dogetdictext("phis.dictionary.NJJB","1");
				if(text==""){
					return;
				}
				this.addPKPHISOBJHtmlElement();
				var time=new Date();
				var timestr=this.buildtimestr(time);
				var ran=Math.floor(Math.random()*9000) + 1000;
				var jylsh=timestr+"-"+text+"-"+ran;
				var uid=this.mainApp.uid+"";
				var str="9100^"+text+"^"+uid.substring(0,8)+"^1^"+jylsh+"^0^0^^";
				var drres=this.drinterfacebusinesshandle(str);
				var arr=drres.split("^");
				if(arr[0]=="0"){
				var body={};
				body.USERID=uid;
				body.JGID=this.mainApp.deptId;
				var canshu=arr[2].split("|")
				body.YWZQH=canshu[0];
				var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "savesign",
						body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.refresh());
				} else {
					this.refresh();
				}
				}else{
					MyMessageTip.msg("东软接口调用失败：", arr[1], true);
				}
			},
			//签退
			doCheckout:function(){
				var text=this.dogetdictext("phis.dictionary.NJJB","1");
				if(text==""){
					return;
				}
				this.addPKPHISOBJHtmlElement();
				var time=new Date();
				var timestr=this.buildtimestr(time);
				var ran=Math.floor(Math.random()*9000) + 1000;
				var jylsh=timestr+"-"+text+"-"+ran;
				var uid=this.mainApp.uid+"";
				var str="9110^"+text+"^"+uid.substring(0,8)+"^1^"+jylsh+"^0^0^^";
				var drres=this.drinterfacebusinesshandle(str);
				var arr=drres.split("^");
				if(arr[0]=="0"){
					var body={};
					body.USERID=uid;
					var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "phis.NjjbService",
						serviceAction : "checkout",
						body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.refresh());
				} else {
					this.refresh();
				}	
				}
			},
			dogetdictext:function(dic,type){
				var dicName = {
            		 id : dic
          		};
				var dic=util.dictionary.DictionaryLoader.load(dicName);
				var di;
				if(type=="1"){
					di = dic.wraper[this.mainApp.deptId];
				}else{
					//待完善
					di = dic.wraper[this.mainApp.deptId];
				} 
				var text=""
				if (di) {
					text = di.text;
				}
				if(text==""){
					alert("未找字典"+dic+"中的配置信息")
					return "";
				}
				return text;
			}
			
		});
