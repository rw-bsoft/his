$package("phis.application.drc.script")

$import("phis.script.SelectList", "util.dictionary.DictionaryLoader")

phis.application.drc.script.DRDoctorScheduleList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.selectOnFocus = true;
	cfg.showRowNumber = false;
	cfg.mutiSelect=false;
	phis.application.drc.script.DRDoctorScheduleList.superclass.constructor.apply(
			this, [cfg])
}

/**
 * author: chzhxiang date: 2013.8.11 discription: 双向转诊排班列表
 */
Ext.extend(phis.application.drc.script.DRDoctorScheduleList,
		phis.script.SelectList, {
	onReady : function() {
		this.data = {}
	},

	fillStore : function() {
		if (!this.store) {
			return;
		}
		if (!this.dic) {
			this.dic = util.dictionary.DictionaryLoader.load({
						id : "phis.dictionary.drSchedule"
					});
		}
		// return
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
									var rec = {
										"hospitalCode" : this.data.hospitalCode,
										"hospital" : this.data.hospital,
										"departmentCode" : this.data.departmentCode,
										"department" : this.data.department,
										"doctorId" : this.data.doctorId,
										"doctor" : this.data.doctor,
										"referralDate" : this.data.referralDate,
										"scheduleCode" : json.body[i].GUAHAOBC,
										"schedule" : this.setSchedule(
												json.body[i].GUAHAOBC,
												this.dic.items),
										"scheduleDate" : json.body[i].JIUZHENSJ,
										"YIZHOUPBID" : json.body[i].YIZHOUPBID,
										"DANGTIANPBID" : json.body[i].DANGTIANPBID,
										"GUAHAOLB" : json.body[i].GUAHAOLB,
										"KESHIDM" : json.body[i].KESHIDM,
										"YISHENGDM" : json.body[i].YISHENGDM,
										"GUAHAOXH" : json.body[i].GUAHAOXH,
										"GUAHAOBC" : json.body[i].GUAHAOBC,
										"RIQI" : json.body[i].RIQI
									};
								records.push(new Ext.data.Record(rec));
							}
							this.store.removeAll();
							this.store.add(records);
						}
					} else {
						this.processReturnMsg(code, msg)
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

	doDelScheSelected : function(data) {
		/** 取消所有已选择项再删除 */
		this.grid.getSelectionModel().clearSelections();
		this.store.removeAll();
	},

	/** 复选框取消选择 */
	rowdeSelect : function(e, rowIndex, record) {
		var selectedCount = this.grid.getSelectionModel()
				.getSelections().length;
		if (selectedCount == 0) {
			this.fireEvent("rowdeSelect", record.data);
		}
	},
	setSchedule : function(value, items) {
		for (var i = 0; i < items.length; i++) {
			if (value == items[i].key) {
				return items[i].text;
			}
			continue;
		}
	}
});