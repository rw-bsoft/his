//妻子健康询问模板
$package("chis.application.hc.script");

$import("chis.script.BizTableFormView","chis.script.util.helper.Helper");

chis.application.hc.script.MProgestationaskForm = function(cfg) {
	cfg.colCount = 3;
	cfg.labelWidth = 90;
	cfg.fldDefaultWidth = 200;
	cfg.autoLoadData = true;
	cfg.isAutoScroll=true;
	cfg.autoFieldWidth = false;
	cfg.autoLoadSchema = true;
	cfg.showButtonOnTop = true;
	chis.application.hc.script.MProgestationaskForm.superclass.constructor
			.apply(this, [cfg]);
	this.saveServiceId = "chis.healthCheckService";
	this.saveAction = "saveMProgestationaskrecord";
	this.entryName = "chis.application.hc.schemas.hc_m_progestationask";
	this.on("save", this.onSave, this);
};

Ext.extend(chis.application.hc.script.MProgestationaskForm,
		chis.script.BizTableFormView, {
			doNew : function() {
				this.initDataId = this.exContext.ids["hc_m_progestationask.phrId"];
				chis.application.hc.script.MProgestationaskForm.superclass.doNew
						.call(this);
				var phrIdField = this.form.getForm().findField("phrId");
				if (!phrIdField || !phrIdField.getValue()) {
					phrIdField.setValue(this.exContext.ids.phrId);
					phrIdField.setDisabled(true);
				}
			},
		doSave: function(){
		if(this.saving){
			return
		}
		var values = this.getFormData();
		if(!values){
			return;
		}
		Ext.apply(this.data,values);
		this.saveToServer(values)
		},
	
		getSaveRequest:function(savaData){
			if(!savaData.empiId){
				savaData.empiId=this.exContext.ids["empiId"];
			}
		return savaData;
		},
		onSave : function(entryName, op, json, data) {
				this.refreshEhrTopIcon();
				this.fireEvent("save", entryName, op, json, data);
			},
			initFormData : function(data) {
				this.exContext.control = data["hc_m_progestationask_actions"];
				if (this.op == "create") {
					this.initDataId = null;
				}
				chis.application.hc.script.MProgestationaskForm.superclass.initFormData
						.call(this, data);
				var phrIdField = this.form.getForm().findField("phrId");
				if (!phrIdField || !phrIdField.getValue()) {
					phrIdField.setValue(this.exContext.ids.phrId);
				}
			},
			onReady : function() {
				chis.application.hc.script.MProgestationaskForm.superclass.onReady
						.call(this);
				this.nowDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				var form = this.form.getForm();
				var qzsfzh = form.findField("QZSFZH");//丈夫身份证号
				qzsfzh.on("blur", this.onqzsfzhChange, this);
				
			},
			onqzsfzhChange:function(field){
				if (!field.validate()) {
					return;
				}
				var sfzh = field.getValue();
				if (!sfzh) {
					return;
				}
				
				var qzsfzh=this.checkIdcard(sfzh)
				if(sfzh!=qzsfzh){
					Ext.Msg.alert("友情提醒：","身份证号输入错误，如最后为位X，请大写！");
				}
			},
			checkIdcard : function(pId) {
				var arrVerifyCode = [1, 0, "X", 9, 8, 7, 6, 5, 4, 3, 2];
				var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
				var Checker = [1, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1];
				if (pId.length != 15 && pId.length != 18) {
					return "身份证号共有 15 码或18位";
				}
				var Ai = pId.length == 18 ? pId.substring(0, 17) : pId.slice(0,
						6)
						+ "19" + pId.slice(6, 16);
				if (!/^\d+$/.test(Ai)) {
					return "身份证除最后一位外，必须为数字！";
				}
				var yyyy = Ai.slice(6, 10), mm = Ai.slice(10, 12), dd = Ai
						.slice(12, 14);
				var d = new Date(yyyy, mm - 1, dd), year = d.getFullYear(), mon = d
						.getMonth(), day = d.getDate(), now = Date.parseDate(
						this.mainApp.serverDate, "Y-m-d");
				if (year != yyyy || mon + 1 != mm || day != dd || d > now
						|| now.getFullYear() - year > 110
						|| !this.isValidDate(dd, mm, yyyy)) {
					return "身份证输入错误！";
				}
				for (var i = 0, ret = 0; i < 17; i++) {
					ret += Ai.charAt(i) * Wi[i];
				}
				Ai += arrVerifyCode[ret %= 11];
				return pId.length == 18 && pId.toUpperCase() != Ai
						? "身份证输入错误！"
						: Ai;
			},
			isValidDate : function(day, month, year) {
				if (month == 2) {
					var leap = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0));
					if (day > 29 || (day == 29 && !leap)) {
						return false;
					}
				}
				return true;
			}

		});