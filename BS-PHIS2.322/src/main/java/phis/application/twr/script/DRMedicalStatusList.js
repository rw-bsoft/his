$package("phis.application.twr.script")

$import("phis.script.SelectList")

phis.application.twr.script.DRMedicalStatusList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.selectOnFocus = true;
	cfg.showRowNumber = false;
	phis.application.twr.script.DRMedicalStatusList.superclass.constructor.apply(this,
			[cfg])
}

/**
 * author: chzhxiang date: 2013.8.11 discription: 体检仪器
 */
Ext.extend(phis.application.twr.script.DRMedicalStatusList, phis.script.SelectList, {
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
										var rec = {
										"referralDate" : this.data.referralDate,
										"hospitalCode" : this.data.hospitalCode,
										"hospital" : this.data.hospital,
										"classifyCode" : this.data.classifyCode,// 分类
										"classifyName" : this.data.classifyName,
										"directionCode" : this.data.directionCode,// 检查方向代码
										"directionName" : this.data.directionName,
										"partCode" : this.data.partCode,// 检查部位代码
										"partName" : this.data.partName,
										"itemCode" : this.data.itemCode,
										"itemName" : this.data.itemName,
										"medicalCode" : json.body[i].JIANCHASBDM,
										"medical" : json.body[i].JIANCHASBMC,
										"JIANCHASBDD" : json.body[i].JIANCHASBDD,
										"YUYUEKSSJ" : json.body[i].YUYUEKSSJ,
										"YUYUEJSSJ" : json.body[i].YUYUEJSSJ,
										"YUYUEH" : json.body[i].YUYUEH,
										"medicalStatus" : json.body[i].YUYUEZT
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
	/** 复选框取消选择 */
	rowdeSelect : function(e, rowIndex, record) {
		var selectedCount = this.grid.getSelectionModel().getSelections().length;
		if (selectedCount == 0) {
			this.fireEvent("rowdeSelect", record.data);
		}
	},
	doDelMedSelected : function(data) {
		/** 取消所有已选择项后再删除 */
		this.grid.getSelectionModel().clearSelections();
		this.store.removeAll();
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