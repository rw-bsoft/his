$package("phis.application.yb.sgyb.script")
$import("phis.script.TableForm", "phis.application.yb.script.PHISActiveXUtils",
		"phis.application.yb.script.MedicareCommonMethod")
 
phis.application.yb.sgyb.script.ReadeCardform = function(cfg) {
	cfg.showButtonOnTop = true;
	Ext.apply(this, phis.application.yb.script.MedicareCommonMethod);
	phis.application.yb.sgyb.script.ReadeCardform.superclass.constructor.apply(this,[cfg])
} 

Ext.extend(phis.application.yb.sgyb.script.ReadeCardform, phis.script.TableForm, {
	// 每次打开界面的时候调用donew清空医保卡信息
	doNew : function() {
		this.ybkxx = null;
		phis.application.yb.sgyb.script.ReadeCardform.superclass.doNew.call(this);
	},
	// 点击读卡按钮功能
	doDk : function() {
		this.doNew();			
		$PhisActiveXObjectUtils.initHtmlElement();
		var o = $PhisActiveXObjectUtils.getObject();
		// 调用控件读卡的方法
		// 将读出来的医保卡信息转成Map形式,以下代码仅供参考根据实际需要实现
		var reg = "";
		reg = o.ReadFile("mzghsk");
		var res = reg.split("|");
		var dkxx  ={};	
		for(var i=0; i<res.length; i++){
			//var s = String( res[i]);
			var item =res[i].split("=");
			if(item.length>1){
				dkxx[item[0]] = item[1];				
			}
		}
		this.form.getForm().findField("BRXM").setValue(dkxx.xming0); 
		this.form.getForm().findField("BRXB").setValue(dkxx.xbie00); 
		this.form.getForm().findField("BRNL").setValue(dkxx.brnl00); 
		this.form.getForm().findField("ICKH").setValue(dkxx.cardno); 
		this.form.getForm().findField("SBKH").setValue(dkxx.id0000); 
		this.form.getForm().findField("DWMC").setValue(dkxx.dwmc00); 
		this.form.getForm().findField("ICKZT").setValue(dkxx.icztmc); 
		this.form.getForm().findField("GZZT").setValue(dkxx.gzztmc); 
		this.form.getForm().findField("DQMC").setValue(dkxx.dqmc00); 
		this.form.getForm().findField("FZXMC").setValue(dkxx.fzxmc0); 
		this.form.getForm().findField("ZHYE").setValue(dkxx.grzhye); 
		var ybkxx = {};
		ybkxx["BRXM"] = dkxx.xming0;
		ybkxx["BRXB"] = dkxx.xbie00;
		ybkxx["BRNL"] = dkxx.brnl00;
		ybkxx["ICKH"] = dkxx.cardno;
		ybkxx["SBKH"] = dkxx.id0000;
		ybkxx["DWMC"] = dkxx.dwmc00;
		ybkxx["ICKZT"] = dkxx.icztmc;
		ybkxx["GZZT"] = dkxx.gzztmc;
		ybkxx["DQMC"] = dkxx.dqmc00;
		ybkxx["FZXMC"] = dkxx.fzxmc0;
		ybkxx["ZHYE"] = parseFloat(dkxx.grzhye);

		this.ybkxx = ybkxx;
		var body = {};
		// 将读卡读出来用于表示这张卡唯一的字段放到body里面去用于后台查询有没这张卡的记录
		body["ICKH"] = dkxx.cardno;		
		body["BRID"] = this.opener.ybbhxx.BRID;
		var ret=this.requestServer("queryYBKXX",body);
		if(ret==null){
			alert("医保卡信息为空!");
			return;
		}else{
			if (ret.json.body.code == 1) {// 数据库已经存在该卡记录				    
					Ext.apply(this.ybkxx, ret.json.body);  // 将MZXH等信息放到医保卡缓存中
				} else {
					// 数据库里面没有存该卡数据,保存到YB_YBKXX表里
					//this.showModule();
					this.saveYbkxx(this.opener.ybbhxx)
				}
		}
	},
	// 从phis.application.reg.script.RegistrationManageModule拿过来的打开新增病人的方法.以RegistrationManageModule变动为准
	showModule : function() {
		var m = this.midiModules["healthRecordModule"];
		if (!m) {
			$import("phis.application.pix.script.EMPIInfoModule");
			m = new phis.application.pix.script.EMPIInfoModule({
						entryName : "phis.application.pix.schemas.MPI_DemographicInfo",
						title : "个人基本信息查询",
						height : 450,
						modal : true,
						mainApp : this.mainApp
					});
			m.on("onEmpiReturn", this.saveYbkxx, this);
			this.midiModules["healthRecordModule"] = m;
		}
		var win = m.getWin();
		win.show();
	},
	// 新增病人后关联医保卡
	saveYbkxx : function(data) {
		//this.ybkxx["empiId"]=data.empiId;
		this.ybkxx["empiId"]=data.EMPIID;
		var ret=this.requestServer("saveYBKXX",this.ybkxx);
		if(ret==null){
		return;}
		Ext.apply(this.YBKXX,ret.json.body);
	},
	//确认
	doQd:function(){
		this.fireEvent("qr",this.ybkxx);				
		var win = this.getWin();
		if (win)
			win.hide();		
	},
	//取消
	doQx:function(){
		alert("取消操作");
	},
})