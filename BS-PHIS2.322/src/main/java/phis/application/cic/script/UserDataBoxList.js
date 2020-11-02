$package("phis.application.cic.script")

$import("phis.script.SelectList")

phis.application.cic.script.UserDataBoxList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	cfg.disablePagingTbr = false;
	cfg.entryName = this.entryName || "phis.application.emr.schemas.EMR_BQYZ_SJH";
	phis.application.cic.script.UserDataBoxList.superclass.constructor
			.apply(this, [cfg]);
	this.requestData.serviceId = "userDataBoxService";
	this.requestData.serviceAction = "listUserDataByCnd";
	this.on("loadData", this.onLoadData, this);
}

Ext.extend(phis.application.cic.script.UserDataBoxList, phis.script.SelectList, {
	expansion : function(cfg) {
		this.dateFieldks = new Ext.ux.form.Spinner({
					name : 'storeDate',
					value : this.mainApp.serverDate,
					strategy : {
						xtype : "date"
					}
				});
		this.dateFieldks.on("change", this.ksDateChange, this);
		this.dateFieldks.on("spinup", this.ksDateSpin, this);
		this.dateFieldks.on("spindown", this.ksDateSpin, this);
		this.label = new Ext.form.Label({
					text : "-至-"
				});
		this.flabel = new Ext.form.Label({
					text : "开嘱时间："
				});
		this.dateFieldjs = new Ext.ux.form.Spinner({
					name : 'storeDate',
					value : this.mainApp.serverDate,
					strategy : {
						xtype : "date"
					}
				});
		this.dateFieldjs.on("change", this.jsDateChange, this);
		this.dateFieldjs.on("spinup", this.jsDateSpin, this);
		this.dateFieldjs.on("spindown", this.jsDateSpin, this);
		var tbar = cfg.tbar;
		delete cfg.tbar;
		cfg.tbar = [];
		cfg.tbar.push([this.flabel, this.dateFieldks, '-', this.label, '-',
				this.dateFieldjs, tbar]);
	},
	ksDateSpin : function(field) {
		var ksValue=field.getValue();
		var jsValue=this.dateFieldjs.getValue();
		var flag=this.checkBig(ksValue,jsValue);
		if(!flag){
			field.setValue(jsValue);
		}
	},
	jsDateSpin : function(field) {
		var jsValue=field.getValue();
		var ksValue=this.dateFieldks.getValue();
		var flag=this.checkBig(ksValue,jsValue);
		if(!flag){
			field.setValue(ksValue);
		}
	},
	checkBig:function(ksValue,jsValue){
		var ks=Date.parseDate(ksValue,"Y-m-d");
		var js=Date.parseDate(jsValue,"Y-m-d");
		if(ks.getTime()>js.getTime()){
			return false;
		}
		return true;
	},
	ksDateChange : function(field, newValue, oldValue) {
		var flag=this.checkValueByReg(field,newValue);
		this.ksDateSpin(field);
	},
	jsDateChange : function(field, newValue, oldValue) {
		var flag=this.checkValueByReg(field,newValue);
		this.jsDateSpin(field);
	},
	checkValueByReg:function(field,newValue){
		if(/([1-2]\d{3})[\/|\-](0?[1-9]|10|11|12)[\/|\-]([1-2]?[0-9]|0[1-9]|30|31)/.test(newValue)&&newValue.length==10){
			field.clearInvalid();
			return true;
		}
		field.markInvalid("不符合日期格式！") 
		return false;
	},
	doTimeNew : function() {
		if (this.indexId == 14) {
			this.flabel.setText("变更时间：");
		} else {
			this.flabel.setText("开嘱时间：");
		}
		// this.dateFieldks.setValue(new Date().format("Y-m-d"));
		// this.dateFieldjs.setValue(new Date().format("Y-m-d"));
	},
	doCancel : function() {
		this.fireEvent("cancel", this);
	},
	doRefresh : function() {
		var cnd = this.getCnd();
		this.requestData.cnd = cnd;
		this.initCnd = cnd
		this.loadData();
	},
	onLoadData : function() {
		this.clearSelect();
	},
	getCnd : function() {
		var jcnd;
		if (this.ZYH) {
			jcnd = ['eq', ['$', 'a.ZYH'], ['i', this.ZYH]];
		}
		var kssj = new Date();
		if (this.dateFieldks) {
			kssj = this.dateFieldks.getValue() + " 00:00:00";
		} else {
			kssj = kssj.format("Y-m-d") + " 00:00:00";
		}
		var jssj = new Date();
		if (this.dateFieldjs) {
			jssj = this.dateFieldjs.getValue() + " 23:59:59";
		} else {
			jssj = jssj.format("Y-m-d") + " 23:59:59";
		}
		var cnd = [
				'and',
				['ge', ['$', 'a.KSSJ'], ['todate', ['s',kssj], ['s','yyyy-mm-dd hh24:mi:ss']]],
				['le', ['$', 'a.KSSJ'], ['todate', ['s',jssj], ['s','yyyy-mm-dd hh24:mi:ss']]]];
		if (this.indexId == 14) {
			cnd = [
					"or",
					[
							'and',
							['ge', ['$', 'a.KSSJ'],
									['todate', ['s',kssj], ['s','yyyy-mm-dd hh24:mi:ss']]],
							['le', ['$', 'a.KSSJ'],
									['todate', ['s',jssj], ['s','yyyy-mm-dd hh24:mi:ss']]]],
					[
							'and',
							['ge', ['$', 'a.TZSJ'],
									['todate', ['s',kssj], ['s','yyyy-mm-dd hh24:mi:ss']]],
							['le', ['$', 'a.TZSJ'],
									['todate', ['s',jssj], ['s','yyyy-mm-dd hh24:mi:ss']]]]];
		}
		if (jcnd) {
			cnd = ['and', cnd, jcnd]
		}
		return cnd;
	},
	doAppoint : function() {
		var records = this.getSelectedRecords();
		if (records == null || records.length == 0) {
			return
		}
		var data = "";
		for (var i = 0; i < records.length; i++) {
			var str = ""
			var r = records[i];
			var YZMC = r.get("YZMC") || "";
			var YCJL = r.get("YCJL") || "";
			var JLDW = r.get("JLDW") || "";
			var YPYF = r.get("YPYF_text") || "";
			var SYPC = r.get("SYPC_text") || "";
			str = YZMC + "  " + YCJL + JLDW + "  " + YPYF + "  " + SYPC + "  ";
			data += str;
			if (i != (records.length - 1)) {
				data += "\n";
			}
		}
		this.fireEvent("appoint", data, 2);
	}
});