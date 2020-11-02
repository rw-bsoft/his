/**
 * 医保智能提醒
 * 
 */
$package("phis.application.pub.script")
$import("phis.script.widgets.MyMessageTip",
		"phis.application.pub.script.ActiveX")
phis.application.pub.script.MedicareSmartReminder = {
	id:'ControlMedicareSmartReminder',
	clsId:'CF586FD2-E9E5-4114-A44B-05CA3552718E',
	/**
	 * 获得 active 对象
	 * @returns
	 */
	getMedicareSmartReminderObject:function(){
		var medicareSmartReminderObject=this.medicareSmartReminderObject;
		if(!medicareSmartReminderObject){
			medicareSmartReminderObject=phis.application.pub.script.ActiveX.getControl(this.id,this.clsId);
			if(!medicareSmartReminderObject){
				MyMessageTip.msg("错误",'医保前端提醒浏览器插件没有正确安装', true);
			}
			this.medicareSmartReminderObject=medicareSmartReminderObject;
		}
		return medicareSmartReminderObject;
	},
	
	/**
	 * 启动客户端
	 */
	startup:function(){
		try{
			this.getMedicareSmartReminderObject().startup();
		}catch(err){
			MyMessageTip.msg("错误",'医保前端提醒客户端初始化错误:'+err, true);
		}
	},
	
	/**
	 * 关闭客户端
	 */
	shutdown:function(){
		try{
			this.getMedicareSmartReminderObject().shutdown();
		}catch(err){
			MyMessageTip.msg("错误",'医保前端提醒客户端没有正确关闭:'+err, true);
		}
	},
	
	/**
	 * js对象转换成xml
	 * @param obj
	 * @returns {String}
	 */
	toXml:function(obj){
		var xml='';
		var title='<?xml version="1.0" encoding="utf-8" ?>';
		var t1='<YL_ACTIVE_ROOT>';
		var t2='</YL_ACTIVE_ROOT>';
		var t3='<JZLX>100</JZLX>';
		this.getMedicareSmartReminderObject();
		obj.AGENTIP=ControlMedicareSmartReminder.ipaddress();
		obj.AGENTMAC=ControlMedicareSmartReminder.macaddress();
		var xml1=this.object2xml(obj);
		xml=title+t1+xml1+t3+t2;
		console.log(xml);
		return xml;
	},
	object2xml:function(obj){
		var xmlBody='';
		for(var key in obj){
			var val=obj[key];
			if(typeof val=='function'){
				continue;
			}
			if(Ext.isArray(val)){
				xmlBody+=String.format('<{0}>',key);
				for(var i in val){
					var st=this.object2xml(val[i]);
					if(st){
						xmlBody+='<ITEM>';
						xmlBody+=st;
						xmlBody+='</ITEM>';
					}
					
					
					
				}
				xmlBody+=String.format('</{0}>',key);
			}else{
				xmlBody+=String.format('<{0}>{1}</{2}>',key,val,key);
			}
		}
		return xmlBody;
	},
	/**
	 * 接口主方法
	 * @param obj
	 */
	send:function(obj){
		var xml=this.toXml(obj);
		try{
			this.getMedicareSmartReminderObject().doSend(this.toUTF8(xml));
		}catch(err){
			MyMessageTip.msg("错误",'医保前端提醒接口调用错误:'+err, true);
		}
	},
	/**
	 * 获取本机IP地址
	 */
	ipaddress:function(){
		try{
			this.getMedicareSmartReminderObject().ipaddress();
			
		}catch(err){
			MyMessageTip.msg("错误",'获取本机IP地址出错:'+err, true);
		}
	},
	/**
	 * 获取本机MAC地址
	 */
	macaddress:function(){
		try{
			this.getMedicareSmartReminderObject().macaddress();
		}catch(err){
			MyMessageTip.msg("错误",'获取本机MAC地址出错:'+err, true);
		}
	},
	toUTF8 : function (content){
	     var result = content.replace(/[^\u0000-\u00FF]/g
	     			, function ($0) { 
	     				return escape($0).replace(/(%u)(\w{4})/gi, "&#x$2;") 
	     			});
	     return result;
	}
}