$package("chis.application.rel.script")

$import("chis.script.BizSelectListView")

chis.application.rel.script.RelevanceManageList = function(cfg) {
	chis.application.rel.script.RelevanceManageList.superclass.constructor
			.apply(this, [cfg]);
	this.disablePagingTbr = true;
	// this.cndFieldWidth = 110;
	// this.on("loadData", onLoadData, this);
}
Ext.extend(chis.application.rel.script.RelevanceManageList,
		chis.script.BizSelectListView, {
			loadData : function(combo, node) {
				this.clearSelect();
				var manageUnitId = this.manageUnitId;
				if (!manageUnitId) {
					if (node && node.attributes['key']) {
						manageUnitId = node.attributes["manageUnit"];
						this.manageUnitId = manageUnitId;
					} else {
						return;
					}
				}
				var value;
				if (this.cndField.getValue()) {
					value = this.cndField.getValue().key;
					if (!value) {
						value = this.cndField.getValue();
					}
				} else {
					if (this.mainApp.jobtitleId != "gp.101") {
						return;
					}
					value = this.mainApp.uid;
				}
				this.requestData.body = {
					fda : value,
					manageUnitId : manageUnitId
				};
				chis.application.rel.script.RelevanceManageList.superclass.loadData
						.call(this);
			},

			onClick : function() {
				this.loadData();
			},

			onStoreLoadData : function(store, records, ops) {
				app.modules.list.SelectListView.superclass.onStoreLoadData
						.call(this, store, records, ops)
				var selRecords = []
				this.store.each(function(r) {
							if (r.data.selected) {
								selRecords.push(r)
							}
						}, this);
				this.grid.getSelectionModel().selectRecords(selRecords)

			},

			doSave : function() {
				var records = this.getSelectedRecords();
				var saveRecords = [];
				for (var i = 0; i < records.length; i++) {
					var r = records[i];
					saveRecords.push({
								fd : r.data.fd,
								fd_text : r.data.fd_text
							});
				}
				var fda;
				if (this.cndField.getValue()) {
					fda = this.cndField.getValue().key;
					if (!fda) {
						fda = this.cndField.getValue();
					}
				} else {
					fda = this.mainApp.uid;
				}
				this.mask();
				util.rmi.jsonRequest({
							serviceId : "chis.relevanceManageService",
							method : "execute",
							serviceAction : "saveRelevanceManageDoctor",
							body : {
								fda : fda,
								records : saveRecords
							}
						}, function(code, msg, json) {
							this.unmask();
							if (code > 300) {
								this.processReturnMsg(code, msg,
										this.saveToServer, [{
													fda : fda,
													records : saveRecords
												}]);
								return
							}
							this.clearSelect();
							this.loadData();
							Ext.Msg.alert("提示", "保存成功。");
							this.fireEvent("save");
						}, this);
			},

			onLoadData : function() {

			},

			// doAdd : function() {
			// var form = this.createSimpleModule("relForm",
			// "chis.application.rel.REL/REL/REL01_1_1");
			// this.form = form;
			// form.doNew();
			// var value;
			// if (this.cndField.getValue()) {
			// value = this.cndField.getValue();
			// } else {
			// value = this.mainApp.uid;
			// }
			// form.on("save", this.refresh, this);
			// form.getWin().show();
			// var n = this.store.getCount();
			// if (n > 0) {
			// form.queryId = value;
			// form.loadData();
			// } else {
			// this.onNoData();
			// }
			// },
			//
			// onNoData : function() {
			// var value = this.cndField.getValue();
			// if (value) {
			// var rawValue = this.cndField.getRawValue();
			// var jobId = this.mainApp.jobId;
			// if (jobId!="chis.system") {
			// this.form.form.getForm().findField("fda").setValue({
			// key : value,
			// text : rawValue
			// });
			// }
			// this.form.initDataId = null;
			// }
			// },

			getCndBar : function(items) {
				var cndField = this.createDicField();
				this.cndField = cndField
				if (this.mainApp.jobtitleId == "chis.101") {
					cndField.disable();
				} else {
					cndField.enable();
				}
				var queryBtn = new Ext.Toolbar.Button({
							iconCls : "query",
							text : "查询"
						})
				this.queryBtn = queryBtn;
				queryBtn.on("click", this.onClick, this);
				return ['助理医生：', cndField, queryBtn, '-']
			},

			createDicField : function() {
				var defaultValue = null;
				if (this.mainApp.jobtitleId == "gp.101") {
					defaultValue = {
						key : this.mainApp.uid,
						text : this.mainApp.logonName
					}
				}
				var dic = {
					id : "gp.dictionary.user101",
					defaultValue : defaultValue,
					width : 200,
					parentKey : "%user.manageUnit.id",
					onlySelectLeaf : "true"
				}
				var cls = "util.dictionary.TreeDicFactory";
				$import(cls)
				var factory = eval("(" + cls + ")")
				var field = factory.createDic(dic)
				field.on("select", this.loadData, this);
				return field
			}
		})