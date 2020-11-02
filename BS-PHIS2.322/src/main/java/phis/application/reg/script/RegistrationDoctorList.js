/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("phis.application.reg.script");

$import("phis.script.SimpleList");

phis.application.reg.script.RegistrationDoctorList = function(cfg) {
	cfg.enableCnd = false;
	cfg.gridDDGroup = "firstRegDDGroup";
	cfg.autoLoadData = false;
	cfg.disablePagingTbr = true;
	phis.application.reg.script.RegistrationDoctorList.superclass.constructor.apply(
			this, [cfg])
}

Ext.extend(phis.application.reg.script.RegistrationDoctorList,
		phis.script.SimpleList, {
			// 加上鼠标移动提示记录是否已作废功能
			onReady : function() {
				phis.application.reg.script.RegistrationDoctorList.superclass.onReady
						.call(this);
				if (this.officeDataCombox.getValue()) {
					this.initCnd = ['and', ['ne', ['$', 'LOGOFF'], ['i', 1]],
							['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
					this.requestData.cnd = ['and',
							['ne', ['$', 'LOGOFF'], ['i', 1]],
							['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
				} else {
					this.initCnd = ['eq', ['$', 'PERSONID'], ['s', 0]];
					this.requestData.cnd = ['eq', ['$', 'PERSONID'], ['s', 0]];
				}
				this.loadData();
			},
			expansion : function(cfg) {
				var labelText = new Ext.form.Label({
							text : "挂号 科 室"
						});
				var officeDataStore = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : []
						});
				this.officeDataStore = officeDataStore;
				var officeDataCombox = new Ext.form.ComboBox({
							store : officeDataStore,
							valueField : "value",
							displayField : "text",
							mode : 'local',
							triggerAction : 'all',
							emptyText : "请选择",
							selectOnFocus : true,
							width : 150,
							allowBlank : false
						});
				this.officeDataCombox = officeDataCombox;
				officeDataCombox.on("select", this.onSelect, this)
				cfg.tbar.push(labelText)
				cfg.tbar.push(['-'])
				cfg.tbar.push(officeDataCombox);
			},
			onSelect : function() {
				this.refresh();
			},
			onDblClick : function(grid, index, e) {
				var lastIndex = grid.getSelectionModel().lastActive;
				var record = grid.store.getAt(lastIndex);
				record.data["KSDM"] = this.officeDataCombox.getValue();
				record.data["KSDM_text"] = this.officeDataCombox.getRawValue();
				if (record) {
					this.fireEvent("doctorChoose", grid, record);
				}
			},
			/*
			 * onESCKey : function() { this.fireEvent("doctorCancleChoose"); },
			 */
			getOffice : function() {
				var dataDate = {};
				if (this.dayInt) {
					dataDate["dayInt"] = this.dayInt;
					if (this.weekDate) {
						var d = Date.parseDate(this.weekDate, 'Y-m-d')
						dataDate["day"] = d.getDay() + 1;
					} else {
						dataDate["day"] = new Date().getDay() + 1;
					}
				} else if (this.weekDate) {
					var d = Date.parseDate(this.weekDate, 'Y-m-d')
					dataDate["day"] = d.getDay() + 1;
					if (this.dayInt) {
						dataDate["dayInt"] = this.dayInt;
					} else {
						if (new Date().getHours() < 12) {
							dataDate["dayInt"] = 1;
						} else {
							dataDate["dayInt"] = 2
						}
					}
				} else if (this.now) {
					var d = this.now
					dataDate["day"] = d.getDay() + 1;
					if (this.dayInt) {
						dataDate["dayInt"] = this.dayInt;
					} else {
						if (new Date().getHours() < 12) {
							dataDate["dayInt"] = 1;
						} else {
							dataDate["dayInt"] = 2
						}
					}
				} else {
					dataDate["day"] = new Date().getDay() + 1;
					if (new Date().getHours() < 12) {
						dataDate["dayInt"] = 1;
					} else {
						dataDate["dayInt"] = 2
					}
				}
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceAction,
							body : dataDate
						});
				var data = r.json.offices;
				this.officeDataStore.loadData(data, false);
				if (data.length > 0) {
					this.officeDataCombox.setValue(data[0].value);
				} else {
					this.officeDataCombox.clearValue();
				}
				return data;
			}
		});