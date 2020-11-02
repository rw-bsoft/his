$package("phis.application.hos.script")

/**
 * 处方组套维护module zhangyq 2012.05.25
 */
$import("phis.script.SimpleModule")

phis.application.hos.script.HospitalBedspaceToBedModule = function(cfg) {
	cfg.width = 700
	phis.application.hos.script.HospitalBedspaceToBedModule.superclass.constructor
			.apply(this, [cfg])
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.hos.script.HospitalBedspaceToBedModule,
		phis.script.SimpleModule, {
			onWinShow : function() {
				if (this.form) {
					this.module.initDataId = this.zyh;
					this.module.loadData();
				}
				if (this.list) {
					this.hospitalToBedInformationList.cndField.setValue();
					this.hospitalToBedInformationList.requestData.cnd = null;
					this.hospitalToBedInformationList.requestData.cnds = this.cwks
							+ "#"
							+ this.brch
							+ "#"
							+ this.brxb
							+ "#"
							+ this.brbq;
					this.hospitalToBedInformationList.requestData.pageNo = 1;
					this.hospitalToBedInformationList.requestData.serviceId = "phis.hospitalToBedInfoService";
					this.hospitalToBedInformationList.requestData.serviceAction = "getToBedInfo";
					this.hospitalToBedInformationList.loadData();
				}
			},
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							layout : 'border',
							height : 509,
							tbar : ['', this.initConditionButtons()],
							items : [{
										layout : "fit",
										region : 'center',
										items : this.getList()
									}, {
										layout : "fit",
										region : 'north',
										height : 104,
										items : this.getForm()
									}]
						});
				this.panel = panel
				return panel
			},
			initConditionButtons : function() {
				var items = []
				items.push({
							xtype : "button",
							text : "转床",
							iconCls : "arrow_switch",
							scope : this,
							handler : this.doBedToBed
						})
				items.push({
							xtype : "button",
							text : "关闭",
							iconCls : "common_cancel",
							scope : this,
							handler : this.doClose
						})
				return items
			},
			getForm : function() {
				var module = this.midiModules['hospitalPatientBedsInformationForm'];
				if (!module) {
					module = this.createModule(
							"hospitalPatientBedsInformationForm",
							this.refHospitalPatientBedsInformationForm);
					module.initDataId = this.zyh;
					module.loadData();
					this.module = module;
					var form = module.initPanel();
					this.form = form;
				}
				return form;
			},
			getList : function() {
				var module = this.midiModules['hospitalToBedInformationList'];
				if (!module) {
					module = this.createModule("hospitalToBedInformationList",
							this.refHospitalToBedInformationList);
					module.requestData.cnds = this.cwks + "#" + this.brch + "#"
							+ this.brxb + "#" + this.brbq;
					module.requestData.pageNo = 1;
					module.requestData.serviceId = "phis.hospitalToBedInfoService";
					module.requestData.serviceAction = "getToBedInfo";
					// ['isNull', ['s', 'is'], ['$', 'BRCH']]];
					this.hospitalToBedInformationList = module;
					var list = module.initPanel();
					list.on("rowdblclick", this.onDblClick, this)
					this.list = list;
				}
				return list;
			},
			doBedToBed : function() {
				var r = this.hospitalToBedInformationList.getSelectedRecord();
				if (r.get("ZYHM") > 0) {
					Ext.Msg.confirm("请确认", "确定将病人[" + this.brxm + "]床号["
									+ this.brch + "]与病人[" + r.get("BRXM")
									+ "]床号[" + r.get("BRCH") + "]对调吗？",
							function(btn) {
								if (btn == 'yes') {
									this.doBedToBedService(r);
								}
							}, this);
					return;
				} else {
					Ext.Msg.confirm("请确认", "确定将病人【" + this.brxm + "】从床位【"
									+ this.brch + "】转到【" + r.get("BRCH")
									+ "】吗?", function(btn) {
								if (btn == 'yes') {
									this.doBedToBedService(r);
								}
							}, this);
					return;
				}
			},
			doBedToBedService : function(reco) {
				var data = {
					"al_zyh" : this.zyh,
					"as_cwhm_Old" : this.brch,
					"as_cwhm_New" : reco.get("BRCH"),
					"il_brks" : reco.get("CWKS")
				};
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.serviceActionSave,
							body : data
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return
				} else {
					MyMessageTip.msg("提示", "转床成功!", true);
					this.doClose();
					// add by yangl 转为抛出事件
					this.fireEvent("doSave");
					this.opener.loadData();
				}
			},
			doClose : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			},
			onDblClick : function() {
				this.doBedToBed();
			}
		})
