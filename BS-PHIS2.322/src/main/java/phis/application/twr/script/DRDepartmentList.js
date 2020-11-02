$package("phis.application.drc.script")

$import("phis.script.SelectList")

phis.application.drc.script.DRDepartmentList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.selectOnFocus = true;
	cfg.showRowNumber = false;
	cfg.mutiSelect=false;
	phis.application.drc.script.DRDepartmentList.superclass.constructor.apply(
			this, [cfg])
}

/**
 * author: chzhxiang date: 2013.8.10 discription: 双向转诊医生列表
 */
Ext.extend(phis.application.drc.script.DRDepartmentList,
		phis.script.SelectList, {
			onReady : function() {
				this.data = {}
			},
			onStoreLoadData : function() {
				// this.fillStore();
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
											"hospitalCode" : this.data.hospitalCode,// 医院编码
											"hospital" : this.data.hospital,
											"departmentCode" : json.body[i].KESHIDM,// 科室代码
											"department" : json.body[i].KESHIMC
											// 科室名称
										};
										records.push(new Ext.data.Record(rec));
									}
									this.store.removeAll();
									this.store.add(records);
									if (records.length == 0) {
										MyMessageTip.msg("提示", "没有排班信息", true);
									}
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
			doDelDepSelected : function(data) {
				/** 取消所有已选择项后再删除 */
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
			}
		});