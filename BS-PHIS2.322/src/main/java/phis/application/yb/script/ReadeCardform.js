$package("phis.application.yb.script")
$import("phis.script.TableForm", "phis.application.yb.script.PHISActiveXUtils",
		"phis.application.yb.script.MedicareCommonMethod")

phis.application.yb.script.ReadeCardform = function(cfg) {
	cfg.showButtonOnTop = false;
	Ext.apply(this, phis.application.yb.script.MedicareCommonMethod);
	phis.application.yb.script.ReadeCardform.superclass.constructor.apply(this,
			[cfg])
}

Ext.extend(phis.application.yb.script.ReadeCardform, phis.script.TableForm, {
	// 每次打开界面的时候调用donew清空医保卡信息
	doNew : function() {
		this.ybkxx = null;
		phis.application.yb.script.ReadeCardform.superclass.doNew.call(this);
	},
	// 点击读卡按钮功能
	doDk : function() {
		this.doNew();
		$PhisActiveXObjectUtils.initHtmlElement();
		var o = $PhisActiveXObjectUtils.getObject();
		// 调用控件读卡的方法
		// ...
		// 将读出来的医保卡信息转成Map形式,以下代码仅供参考根据实际需要实现
		var reg = "";
		var ybkxx = this.outputStrToMap("", reg);
		this.ybkxx = ybkxx;
		var body = {};
		// 将读卡读出来用于表示这张卡唯一的字段放到body里面去用于后台查询有没这张卡的记录
		// ...
		var ret=this.requestServer("queryYBKXX",body);
		if(ret==null){
		return;
		}else{
		if (ret.json.body.code == 1) {// 数据库已经存在该卡记录
				Ext.apply(this.ybkxx, ret.json.body);// 将MZXH等信息放到医保卡缓存中
			} else {// 数据库里面没有存该卡数据,打开新增病人界面
				this.showModule();
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
		this.ybkxx["empiId"]=data.empiId;
		var ret=this.requestServer("saveYBKXX",this.ybkxx);
		if(ret==null){
		return;}
		Ext.apply(this.YBKXX,ret.json.body);
	},
	//确认
	doQr:function(){
	this.fireEvent("qr",this.ybkxx);
	this.ybkxx=null;//确认后清除缓存
	}
})