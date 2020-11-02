$package("phis.application.njjb.script");

$import("phis.script.TableForm","phis.script.Phisinterface");

phis.application.njjb.script.TranToNjjbForm = function(cfg){
	this.entryName="phis.application.njjb.schemas.TRAN_NJJB";
	Ext.apply(this,phis.script.Phisinterface);
	phis.application.njjb.script.TranToNjjbForm.superclass.constructor.apply(this,[cfg]);
}
Ext.extend(phis.application.njjb.script.TranToNjjbForm,phis.script.TableForm,{
	onReady : function() {
		phis.application.njjb.script.TranToNjjbForm.superclass.onReady.call(this);
		var form = this.form.getForm();
		var NJJBYLLB=form.findField("NJJBYLLB");
		if(NJJBYLLB){
			this.NJJBYLLB=NJJBYLLB;
			this.NJJBYLLB.on("select",this.changeyllb,this);
		}
		var YBMC=form.findField("YBMC");
		if(YBMC){
			this.ybmc=YBMC;
		}
	},
	changeyllb:function(){
		this.ybmc.setValue("");
		var v=this.NJJBYLLB.getValue();
		var dic={};
		dic.id="phis.dictionary.ybJbbm";
		var tsbz="";//特殊病种
		if(v==16){
			var arr=this.ybkxx.YBMMBZ.split(",");
			var bz=""
			for(var i=0;i<arr.length;i++){
				bz+="'"+arr[i]+"',"
			}
			dic.filter="['and',['in',['$','item.properties.JBBM'],["+bz.substring(0,bz.length-1)+"]]]";
			if(arr.length==1){
				tsbz=this.ybkxx.YBMMBZ;
			}
		}else if(v==171){
			var arr=this.ybkxx.YBMTBZ.split(",");
			var bz=""
			for(var i=0;i<arr.length;i++){
				bz+="'"+arr[i]+"',"
			}
			dic.filter="['and',['in',['$','item.properties.JBBM'],["+bz.substring(0,bz.length-1)+"]]]";
			if(arr.length==1){
				tsbz=this.ybkxx.YBMTBZ;
			}
		}
		this.ybmc.store.proxy = new util.dictionary.HttpProxy({
							method : "GET",
							url : util.dictionary.SimpleDicFactory.getUrl(dic)
						})
		this.ybmc.store.load();
		if(tsbz.length >2){
			this.ybmc.setValue(tsbz);
			tsbz="";
		}
	},
	createButtons : function() {
		var actions = this.actions;
		var buttons = [];
		if (!actions) {
			return buttons;
		}
		var f1 = 112;
		for (var i = 0; i < actions.length; i++) {
			var action = actions[i];
			var btn = {};
			btn.accessKey = f1 + i;
			btn.cmd = action.id;
			btn.text = action.name + "(F" + (i + 1) + ")";
			btn.iconCls = action.iconCls || action.id;
			btn.script = action.script;
			btn.handler = this.doAction;
			btn.notReadOnly = action.notReadOnly;
			btn.scope = this;
			buttons.push(btn);
		}
		return buttons;
	},
	doSave:function(){
		var form = this.form.getForm();
		var NJJBYLLB=form.findField("NJJBYLLB").getValue();
		if(NJJBYLLB==""){
			return MyMessageTip.msg("提示","请选择医疗类别！", true);
		}
		var YBMC=form.findField("YBMC").getValue();
		if(YBMC && YBMC.length >0){
			var arr=YBMC.split(",")
			if(arr.length>3){
				return MyMessageTip.msg("提示","医保病种最多只能选择三个！", true);
			}
			if(this.data.NJJBYLLB!="51" && arr.length>1){
				return MyMessageTip.msg("提示","除职工计划生育外，其他医疗类别只能选一个病种！", true);
			}
		}
		var njjbbody={};
		njjbbody.USERID=this.mainApp.uid;
		//获取业务周期号
		this.ywzqh="";
		var zqr = phis.script.rmi.miniJsonRequestSync({
			serviceId : "phis.NjjbService",
			serviceAction : "getywzqh",
			body:njjbbody
			});
		if (zqr.code <= 300) {
			this.ywzqh=zqr.json.YWZQH;
		}
		else{
			return Ext.Msg.alert("获取业务周期号失败！请确认是否签到。")
		}
		//获取流水号
		this.NJJBLSH="";
		var getlsh = phis.script.rmi.miniJsonRequestSync({
			serviceId : "phis.NjjbService",
			serviceAction : "getnjjblsh"
			});
		if(getlsh.code<=300){
			this.NJJBLSH=getlsh.json.lsh.LSH
		}else{
			return MyMessageTip.msg("提示", "获取流水号失败！", true);
		}
		this.addPKPHISOBJHtmlElement();
//		this.drinterfaceinit();
		this.ybghxx={};
		this.ybghxx.NJJBLSH=this.NJJBLSH;
		this.ybghxx.YLLB=NJJBYLLB;
		this.ybghxx.RYSJ=this.buildtimestr(new Date());
		this.ybghxx.JBBM=YBMC;
		this.ybghxx.KSDM=this.yyxx.KSDM;
		this.ybghxx.YSBM="";
		this.ybghxx.JBR=this.mainApp.uid;
		this.ybghxx.LXDH=this.yyxx.LXDH;
		this.ybghxx.SHBZKH=this.ybkxx.SHBZKH;
		//进行医保登记
		var str=this.buildstr("2210",this.ywzqh,this.ybghxx);
		var drre=this.drinterfacebusinesshandle(str);
		var arr=drre.split("^");
		if(arr[0]=="0"){
			//登记成功后修改病人性质和医保登记信息
			var tempdata={};
			tempdata.YBMC=YBMC;
			tempdata.NJJBYLLB=NJJBYLLB;
			tempdata.NJJBLSH=this.NJJBLSH;
			tempdata.HYXH=this.yyxx.HYXH;
			var re = phis.script.rmi.miniJsonRequestSync({
					serviceId : "phis.registeredManagementService",
					serviceAction : "saveTurnToYb",
					body:tempdata
				});
			if(re.code>300){
				//取消登记
				var qxxx={};
				qxxx.NJJBLSH=this.NJJBLSH;
				qxxx.JBR=this.ybghxx.JBR;
				var qxs=this.buildstr("2240",this.ywzqh,qxxx);
				var qxre=this.drinterfacebusinesshandle(qxs);
				var qxrr=qxre.split("^");
				if(qxrr[0]=="0"){
					//取消成功
				}else{
					return MyMessageTip.msg("金保返回提示",arr[3], true);
				}
			}
			this.win.hide();
			this.fireEvent("saveTran");
		}else{
			return MyMessageTip.msg("金保返回提示",arr[3], true);
		}
	}
});
