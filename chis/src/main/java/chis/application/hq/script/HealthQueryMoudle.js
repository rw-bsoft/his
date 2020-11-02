$package("chis.application.hq.script")

$import("chis.script.BizSimpleListView", "util.rmi.jsonRequest")

chis.application.hq.script.HealthQueryMoudle = function(cfg) {
	cfg.initCnd = cfg.cnds || ['and',['like',['$','a.manaUnitId'],['concat',['$','%user.manageUnit.id'],['s','%']]]];
	cfg.aotuLoadData = false;
	this.aotuLoadData = false;
	this.needOwnerBar = true;
	this.enableCnd = false;
	chis.application.hq.script.HealthQueryMoudle.superclass.constructor.apply(this,
			[cfg]);
}

Ext.extend(chis.application.hq.script.HealthQueryMoudle, chis.script.BizSimpleListView, {
	loadData : function() {//加是否有高危档案--高血压档案管理
		this.requestData.serviceId = "chis.hqQueryService";
		this.requestData.serviceAction = "queryHealthCheckPeople";
		this.requestData.initDataId = this.initDataId
		this.requestData.FC_Id = this.FC_Id;
		return chis.application.hq.script.HealthQueryMoudle.superclass.loadData
				.call(this)
	}
	,createOwnerBar : function() {
		var createUnitLabel = new Ext.form.Label({
					html : "体检机构:",
					width : 80

				});
		var createUnitField = this.createDicField({
					'id' : 'chis.@manageUnit',
					'showWholeText' : 'true',
					'includeParentMinLen' : '6',
					'render' : 'Tree',
					defaultValue : {
						"key" : this.mainApp.deptId,
						"text" : this.mainApp.dept
					},
					'parentKey' : this.mainApp.deptId,
					rootVisible : true,
					width : 120
				});
		createUnitField.on("blur", this.createUnitBlur, this);
		this.createUnitField = createUnitField;
		var dateLabel1 = new Ext.form.Label({
					html : "&nbsp;体检日期:",
					width : 80
				});
		var curDate = Date.parseDate(this.mainApp.serverDate,'Y-m-d');
		var startDateValue = this.getStartDate(this.businessType);
		var dateField1 = new Ext.form.DateField({
					width : this.cndFieldWidth || 120,
					enableKeyEvents : true,
					emptyText : "开始日期",
					value : startDateValue,
					name : "planDate1"
				});
		this.dateField1 = dateField1;
		var dateLabel2 = new Ext.form.Label({
					html : "&nbsp;->&nbsp;",
					width : 30
				});
		var dateField2 = new Ext.form.DateField({
					width : this.cndFieldWidth || 120,
					enableKeyEvents : true,
					emptyText : "结束日期",
					value : curDate,
					name : "planDate2"
				});
		this.dateField2 = dateField2;
		this.dateField1.on("select", this.selectDateField1, this);
		this.dateField2.on("select", this.selectDateField2, this);
		var cnd = this.getOwnerCnd([]);
		if (this.requestData.cnd) {
			cnd = ['and', this.requestData.cnd, cnd]
		}
		this.requestData.cnd = cnd;
		return [createUnitLabel, createUnitField, dateLabel1, dateField1,
				dateLabel2, dateField2]
	}
	,createUnitBlur : function() {
		if (this.createUnitField && (this.createUnitField.getRawValue() == null 
			|| this.createUnitField.getRawValue() == "")) {
			this.createUnitField.setValue();
		}
	}
	,getOwnerCnd : function(cnd) {
		if (this.dateField1.getValue() != null && this.dateField2.getValue() != null
				&& this.dateField1.getValue() != ""&& this.dateField2.getValue() != "") {
			var date1 = this.dateField1.getValue();
			var date2 = this.dateField2.getValue();
			var cnd2 = ['and',['ge',['$', 'a.checkDate'],['todate',
									['s', date1.format("Y-m-d") + " 00:00:00"],
									['s', 'yyyy-mm-dd HH24:mi:ss']]],
							  ['le',['$', 'a.checkDate'],['todate',
									['s', date2.format("Y-m-d") + " 23:59:59"],
									['s', 'yyyy-mm-dd HH24:mi:ss']]]];
			if (cnd.length == 0) {
				cnd = cnd2;
			} else {
				cnd = ['and', cnd2, cnd];
			}
		} else if ((this.dateField1.getValue() == null || this.dateField1
				.getValue() == "")
				&& (this.dateField2.getValue() == null || this.dateField2
						.getValue() == "")) {

		} else if (this.dateField1.getValue() == null
				|| this.dateField1.getValue() == "") {
			MyMessageTip.msg("提示", "请选择体检开始日期！", true);
			return;
		} else if (this.dateField2.getValue() == null
				|| this.dateField2.getValue() == "") {
			MyMessageTip.msg("提示", "请选择体检结束日期！", true);
			return;
		}
		return cnd;
	},
	doModifyStatus : function(){
		var r = this.getSelectedRecord();
		if (r == null) {
			return;
		}
		var m = this.getHealthStatusModifyForm(r);
		m.opener = this;
		var win = m.getWin();
		win.setPosition(250, 100);
		win.show();
		var formData = this.castListDataToForm(r.data, this.schema);
		m.initFormData(formData);
	},
	getHealthStatusModifyForm : function(r) {
		var m = this.midiModules["healthStatusModifyForm"];
		if (!m) {
			var cfg = {};
			cfg.mainApp=this.mainApp;
			var moduleCfg = this.mainApp.taskManager.loadModuleCfg(this.healthStatusModifyForm);
			Ext.apply(cfg, moduleCfg.json.body);
			Ext.apply(cfg, moduleCfg.json.body.properties);
			var cls = cfg.script;
			$import(cls);
			m = eval("new " + cls + "(cfg)");
			m.on("save", this.refresh, this);
			m.on("close", this.active, this);
			this.midiModules["healthStatusModifyForm"] = m;
		}else {
			m.initDataId = r.EMPIID;
		}
		return m;
	},
	castListDataToForm : function(data, schema) {
		var formData = {};
		var items = schema.items;
		var n = items.length;
		for (var i = 0; i < n; i++) {
			var it = items[i];
			var key = it.id;
			if (it.dic) {
				var dicData = {
					"key" : data[key],
					"text" : data[key + "_text"]
				};
				formData[key] = dicData;
			} else {
				formData[key] = data[key];
			}
		}
		Ext.applyIf(formData, data);
		return formData;
	}
});