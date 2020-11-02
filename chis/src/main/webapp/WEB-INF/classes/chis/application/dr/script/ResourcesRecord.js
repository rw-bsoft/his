$package("chis.application.pc.script")

$import("app.desktop.Module")

chis.application.dr.script.ResourcesRecord = function(cfg) {
	cfg = cfg || {}
	cfg.showButtonOnTop = true;
	this.width = 966;
	this.height = 600;
	chis.application.dr.script.ResourcesRecord.superclass.constructor.apply(this, [cfg])
}
Ext.extend(chis.application.dr.script.ResourcesRecord, app.desktop.Module, {
	initPanel: function() {
		var panel = new Ext.Panel({
			frame : false,
			layout : 'fit',
			width : this.width,
			height : this.height,
			autoScroll: true,
			html : '<div >没有数据</div>'
		});
		this.panel = panel;
		this.loadData();
		return panel;
	},
	loadData: function() {
//		this.initPanel();
		util.rmi.jsonRequest({
			method : "execute",
			serviceId : this.properties.loadServiceId,
			action : this.properties.loadAction
		}, function(code, msg, json) {
			if (code > 300) {
				this.processReturnMsg(code, msg);
				return;
			}
			this.panel.body.update(json.body.content);
			for(var i = 0;i < json.body.hospitalCounts;i++){
				var yy = Ext.get("yy" + i);
				if(yy){
					yy.on("click", this.getBusinessTypes, this)
				}
			}
		}, this)
	},
	getBusinessTypes:function(e){
		var hospitalCode = e.getTarget().name;
		this.hospital = e.getTarget().value;
		this.hospitalCode = hospitalCode;
		this.departmentInfo = null;
		this.businessType = null;
		this.businessType_text = null;
		this.departmentCode = null;
		this.department = null;
		this.doctorName = null;
		this.doctorId = null;
		this.timeInterval = null;
		this.recordId = null;
		this.sourceInfo = null;
		this.setApplyInfo();
		util.rmi.jsonRequest({
			method : "execute",
			serviceId : this.properties.loadServiceId,
			action : this.properties.loadAction,
			hospitalCode:hospitalCode
		}, function(code, msg, json) {
			if (code > 300) {
				this.processReturnMsg(code, msg);
				return;
			}
			this.panel.body.update(json.body.content);
			this.hospitalCounts = json.body.hospitalCounts;
			this.businesTypeHospitalListens();
		}, this)
	},
	businesTypeHospitalListens : function() {
		var count = this.businessTypeCounts > this.hospitalCounts
				? this.businessTypeCounts
				: this.hospitalCounts;
		for (var i = 0; i < count; i++) {
			var yy = Ext.get("yy" + i);
			var businessType = Ext.get("businessType" + i);
			if (yy) {
				yy.on("click", this.getBusinessTypes, this)
			}
			if (businessType) {
				businessType.on("click", this.getDepartments, this)
			}
		}

	},
	selectDoctor : function(e) {
		var departmentCode = e.getTarget().name;
		this.departmentCode = departmentCode;
		this.department = e.getTarget().title;
		this.departmentInfo = e.getTarget().value;
		this.doctorName = null;
		this.doctorId = null;
		this.sourceInfo = null;
		this.recordId = null;
		this.timeInterval = null;
		this.setApplyInfo();
		this.doctorId = null;
		this.registerInfo = null;
		util.rmi.jsonRequest({
			method : "execute",
			serviceId : this.properties.loadServiceId,
			action : this.properties.loadAction,
			hospitalCode : this.hospitalCode,
			departmentCode : departmentCode,
			businessType:this.businessType,
			bedSexCode : this.departmentInfo.split(" ")[1]
		}, function(code, msg, json) {
			if (code > 300) {
				this.processReturnMsg(code, msg);
				return;
			}
			this.panel.body.update(json.body.content);
			var doctors = json.body.doctors;
			if (doctors) {
				for (var d = 0; d < doctors.length; d++) {
					var doctorId = doctors[d].doctorId;
					for (var i = 0; i < 15; i++) {
						var doctor = Ext.get(doctorId + i);
						if (doctor) {
							doctor.on("click", this.onGetSource,
									this);
						}
					}
				}
			}
			this.departmentCounts = json.body.departmentCounts;
			this.hospitalCounts = json.body.hospitalCounts;
			this.businesTypeHospitalListens();
			this.departmentListening();
		}, this)
	
	},
	getDepartments:function(e){
		var businessType = e.getTarget().name;
		this.businessType = businessType;
		this.businessType_text = e.getTarget().value;
		this.departmentInfo = null;
		this.departmentCode = null;
		this.department = null;
		this.doctorName = null;
		this.doctorId = null;
		this.recordId = null;
		this.timeInterval = null;
		this.sourceInfo = null;
		this.setApplyInfo();
		util.rmi.jsonRequest({
			method : "execute",
			serviceId : this.properties.loadServiceId,
			action : this.properties.loadAction,
			hospitalCode:this.hospitalCode,
			businessType:this.businessType
		}, function(code, msg, json) {
			if (code > 300) {
				this.processReturnMsg(code, msg);
				return;
			}
			this.panel.body.update(json.body.content);
			this.departmentCounts = json.body.departmentCounts;
			this.hospitalCounts = json.body.hospitalCounts;
			this.businesTypeHospitalListens();
			this.departmentListening();
		}, this)
	},
	cancle:function(){
		if(this.win){
			this.win.hide();
		}
	},
	departmentListening : function() {
		for (var i = 0; i < this.departmentCounts; i++) {
			var ks = Ext.get("ks" + i);
			if (ks) {
				ks.on("click", this.selectDoctor, this)
			}
		}
	},
	onRegister:function(e){
		this.sourceInfo = e.getTarget().title;
		this.sourceRecordId = e.getTarget().value;
		this.setApplyInfo();
	},
	setApplyInfo:function(e){
		var Form = this.parent.topPanel.form.getForm();
		var applyInfo = Form.findField("applyInfo");
		var info = "";
		if(this.hospital){
			info += "医院名称：" + this.hospital;
		}
		if(this.businessType_text){
			info += " , 业务类型：" + this.businessType_text;
		}
		if(this.department){
			info += " , 科室名称：" + this.department;
		}
		if(this.doctorName){
			info += " , 医生姓名：" + this.doctorName;
			info += " , 日期：" + this.workDate;
		}
		if(this.sourceInfo){
			info += " , 选择号源：" + this.sourceInfo;
		}
		var drData = Form.findField("drData");
		var registerInfo = {};
		registerInfo["hospitalCode"] = this.hospitalCode;
		registerInfo["departmentCode"] = this.departmentCode;
		registerInfo["department"] = this.department;
		registerInfo["doctorId"] = this.doctorId;
		registerInfo["sourceInfo"] = this.sourceInfo;
		registerInfo["businessType"] = this.businessType;
		registerInfo["recordId"] = this.recordId;
		registerInfo["departmentInfo"] = this.departmentInfo;
		registerInfo["timeInterval"] = this.timeInterval;
		registerInfo["sourceRecordId"] = this.sourceRecordId
		if (this.workDate) {
			registerInfo["workDate"] = this.workDate.split(" ")[0];
		}
		drData.setValue(registerInfo);
		applyInfo.setValue(info);
	},
	onGetSource:function(e){
		var id = e.getTarget().id;
		var registerInfo = e.getTarget().name;
		var doctorInfo = e.getTarget().title;
		this.doctorId = doctorInfo.split(",")[0];
		this.doctorName = doctorInfo.split(",")[1];
		this.workDate = doctorInfo.split(",")[2];
		this.recordId = registerInfo.split("&")[0];
		this.sourceRecordId = null;
		this.sourceInfo = null;
		this.setApplyInfo();
		this.reqInfo = registerInfo.split("&")[1];
		this.timeInterval = this.reqInfo.split(" ")[4]
		util.rmi.jsonRequest({
			method : "execute",
			serviceId : this.properties.loadServiceId,
			action : this.properties.loadAction,
			reqInfo : this.reqInfo,
			hospitalCode : this.hospitalCode,
			departmentCode : this.departmentCode,
			businessType:this.businessType
		}, function(code, msg, json) {
			if (code > 300) {
				this.processReturnMsg(code, msg);
				return;
			}
			this.panel.body.update(json.body.content);
			var doctors = json.body.doctors;
			if (doctors) {
				for (var d = 0; d < doctors.length; d++) {
					var doctorId = doctors[d].doctorId;
					for (var i = 0; i < 15; i++) {
						var doctor = Ext.get(doctorId + i);
						if (doctor) {
							doctor.on("click", this.onGetSource,
									this);
						}
					}
				}
			}
			var clinicSource = json.body.clinicSource;
			if(clinicSource){
			 for(var i = 0; i < clinicSource.length; i++){
			 	var source = Ext.get("source" + i);
			 	if(source){
			 		source.on("click",this.onRegister,this);
			 	}
			 }
			}
			this.departmentCounts = json.body.departmentCounts;
			this.hospitalCounts = json.body.hospitalCounts;
			this.businesTypeHospitalListens();
			this.departmentListening();
		}, this)
	},
	getWin: function() {
		var win = this.win
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						closeAction: 'hide',
						title : this.title,
						width : this.width,
						height : this.height,
//						autoHeight: true,
//						autoScroll: true,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						constrainHeader : true,
						minimizable : false,
						resizable : false,
						maximizable : false,
						shadow : false,
						modal : true,
						items : this.initPanel()
					})
			this.win = win
		}
		return win;
	},
	getSummary : function() {
		this.loadData();
	},
	setParent : function(parent) {
		this.parent = parent;
	}


});