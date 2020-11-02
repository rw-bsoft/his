$package("phis.application.drc.script")

$import("phis.script.SelectList")

phis.application.drc.script.DRDoctorList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.selectOnFocus = true;
	cfg.showRowNumber = false;
	cfg.mutiSelect=false;
	phis.application.drc.script.DRDoctorList.superclass.constructor.apply(this,
			[cfg])
}

/**
 * author: chzhxiang date: 2013.8.11 discription: 双向转诊医生列表
 */
Ext.extend(phis.application.drc.script.DRDoctorList, phis.script.SelectList, {
	onReady : function() {
		this.data = {}
	},

	onStoreLoadData : function() {
	},
	fillStore : function() {
		if (!this.store) {
			return;
		}
		// 请求DR接口服务。
		phis.script.rmi.jsonRequest({
					serviceId : this.serviceId,
					serviceAction : this.serviceAction,
					body : this.data
				}, function(code, msg, json) {
					if (code < 300) {
						var body = json.body;
						if (body) {
							/* 手动填充数据列表 */
							for (var i = 0; i < json.body.length; i++) {
								if(json.body[i].YISHENGDM == "*"){
										var rec = {
										"hospitalCode" : this.data.hospitalCode,
										"hospital" : this.data.hospital,
										"departmentCode" : this.data.departmentCode,
										"department" : this.data.department,
										"referralDate" : this.data.referralDate,
										"doctorId" : json.body[i].YISHENGDM,
										"doctor" : "普通"
									};
								}else{
									var rec = {
									"hospitalCode" : this.data.hospitalCode,
									"hospital" : this.data.hospital,
									"departmentCode" : this.data.departmentCode,
									"department" : this.data.department,
									"referralDate" : this.data.referralDate,
									"doctorId" : json.body[i].YISHENGDM,
									"doctor" : json.body[i].YISHENGXM
								};
								}
								
								records.push(new Ext.data.Record(rec));
							}
							this.store.removeAll();
							this.store.add(records);
						}
					} else {
						this.processReturnMsg(code, msg)
						this.store.removeAll();
					}
				}, this);

		var records = new Array();
	},
	expansion : function(cfg) {
		cfg.sm.on("rowselect", this.rowSelect, this);
		cfg.sm.on("rowdeselect", this.rowdeSelect, this);
	},
	rowSelect : function(e, rowIndex, record) {
		var r = this.getSelectedRecord()
		var n = this.store.indexOf(r)
		if (n != rowIndex) {
			this.selectRow(rowIndex)

		} else {
			this.fireEvent("rowSelect", r.data);
		}
	},
	/** 复选框取消选择 */
	rowdeSelect : function(e, rowIndex, record) {
		var selectedCount = this.grid.getSelectionModel().getSelections().length;
		if (selectedCount == 0) {
			this.fireEvent("rowdeSelect", record.data);
		}
	},
	doDelDoctorSelected : function(data) {
		/** 取消所有已选择项再删除 */
		this.grid.getSelectionModel().clearSelections();
		this.store.removeAll();
	}
});