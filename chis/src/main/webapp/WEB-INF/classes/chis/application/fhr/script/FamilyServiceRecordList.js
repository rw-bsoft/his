$package("chis.application.fhr.script")
$import("chis.script.BizSimpleListView",
		"chis.application.fhr.script.SelectTemplatePanel",
		"chis.application.fhr.script.ServiceRecordForm")
chis.application.fhr.script.FamilyServiceRecordList = function(cfg) {
	cfg.aotuLoadData = false;
	cfg.showButtonOnTop = true;
	cfg.queryComboBoxWidth = 90;
	cfg.cndFieldWidth = 160;
	chis.application.fhr.script.FamilyServiceRecordList.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(chis.application.fhr.script.FamilyServiceRecordList,
		chis.script.BizSimpleListView, {
			loadData : function() {
				if (!this.requestData.cnd) {
					this.requestData.cnd = ['eq', ['$', 'empiId'],
							['s', this.empiId]];
				}
				chis.application.fhr.script.FamilyServiceRecordList.superclass.loadData
						.call(this);
			},

			doCndQuery : function() {
				this.initCnd = ['eq', ['$', 'empiId'], ['s', this.empiId]];
				chis.application.fhr.script.FamilyServiceRecordList.superclass.doCndQuery
						.call(this);
			},
			doCreateRecord : function() {
				if (!this.empiId) {
					this.empiId = this.father.getEmpiId();
				}
				if (!this.empiId) {
					Ext.encode("提示", "请选择家庭成员。");
				}
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.familyRecordService",
							serviceAction : "checkUpCreate",
							method : "execute",
							body : {
								"empiId" : this.empiId,
								"familyId" : this.familyId
							}
						});
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return;
				}
				var canCreate = result.json.canCreate;
				if (canCreate == "1") {
					Ext.Msg.alert("提示", "该家庭没有有效的签约记录。");
					return;
				} else if (canCreate == "2") {
					Ext.Msg.alert("提示", "该家庭已解约。");
					return;
				} else if (canCreate == "3") {
					Ext.Msg.alert("提示", "该家庭的签约记录已过期。");
					return;
				} else if (canCreate == "4") {
					Ext.Msg.alert("提示", "该家庭成员还未签约或已解约。");
					return;
				} else if (canCreate == "6") {
					if (!this.exContext.control
							|| !this.exContext.control.update) {
						Ext.Msg.alert("提示", "无新建权限，只有该家庭责任医生或医生助理可以新建。");
						return;
					}
				}
				this.canModify = true;
				var selectTemplate = result.json.selectTemplate;
				var templates = result.json.templates;
				if (selectTemplate) {
					this.openSelectTemplatePanel(templates);
				} else {
					var masterplateId = templates[0].masterplateId;
					this.openCreatePanel(masterplateId);
				}
			},

			openSelectTemplatePanel : function(templates) {
				var selectTemplatePanel = new chis.application.fhr.script.SelectTemplatePanel(
						{
							mainApp : this.mainApp,
							// templates : templates,
							entryName : "chis.application.fhr.schemas.EHR_SelectModule"
						});
				selectTemplatePanel.on("selectTemplate", this.openCreatePanel,
						this);
				var win = selectTemplatePanel.getWin();
				win.show();
			},

			openCreatePanel : function(masterplateId, recordId) {
				var createPanel = new chis.application.fhr.script.ServiceRecordForm(
						{
							mainApp : this.mainApp,
							empiId : this.empiId,
							masterplateId : masterplateId
						});
				createPanel.canModify = this.canModify;
				createPanel.on("save", this.loadData, this);
				var win = createPanel.getWin();
				win.show();
				if (recordId) {
					createPanel.initDataId = recordId;
					createPanel.loadData();
				} else {
					createPanel.initDataId = null;
				}
			},

			doRemove : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.familyRecordService",
							serviceAction : "checkUpCreate",
							method : "execute",
							body : {
								"empiId" : this.empiId,
								"familyId" : this.familyId
							}
						});
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return;
				}
				var canCreate = result.json.canCreate;
				if (canCreate == "6") {
					if (!this.exContext.control
							|| !this.exContext.control.update) {
						Ext.Msg.alert("提示", "无删除权限，只有该家庭责任医生或医生助理可以删除记录。");
						return;
					}
				}
				chis.application.fhr.script.FamilyServiceRecordList.superclass.doRemove
						.call(this);
			},

			doModify : function() {
				var r = this.getSelectedRecord();
				var masterplateId = r.get("masterplateId");
				var recordId = r.get("recordId");
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.familyRecordService",
							serviceAction : "checkUpCreate",
							method : "execute",
							body : {
								"empiId" : this.empiId,
								"familyId" : this.familyId
							}
						});
				if (result.code != 200) {
					this.processReturnMsg(result.code, result.msg);
					return;
				}
				var canCreate = result.json.canCreate;
				this.canModify = true;
				if (canCreate == "6") {
					if (!this.exContext.control
							|| !this.exContext.control.update) {
						this.canModify = false;
					}
				}
				this.openCreatePanel(masterplateId, recordId);
			},

			onDblClick : function() {
				this.doModify();
			}
		});