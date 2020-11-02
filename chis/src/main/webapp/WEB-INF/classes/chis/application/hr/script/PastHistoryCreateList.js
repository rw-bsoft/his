$package("chis.application.hr.script")

$import("chis.script.BizSimpleListView")

chis.application.hr.script.PastHistoryCreateList = function(cfg) {
	cfg.actions = [{
				id : "save",
				name : "增加",
				iconCls : "healthDoc_addMember"
			}, {
				id : "delete",
				name : "删除",
				iconCls : "remove"

			}, {
				id : "saveAll",
				name : "保存",
				iconCls : "save"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}, {
				id : "selectAll",
				name : "无病史全选",
				iconCls : "healthDoc_addAll"
			}];
	chis.application.hr.script.PastHistoryCreateList.superclass.constructor.apply(this, [cfg]);
}

Ext.extend(chis.application.hr.script.PastHistoryCreateList, chis.script.BizSimpleListView, {
	doSave : function(item, e) {
		var saveData = {};
		this.fireEvent("beforeSave", saveData);
		var recDis = saveData["diseaseText"];
		var recHisCode = saveData["pastHisTypeCode"];
		var recHisText = saveData["pastHisTypeCode_text"];
		var diseaseCode = saveData["diseaseCode"];
		if (!diseaseCode) {
			return;
		}
		var message = this.checkDis(recHisCode, diseaseCode);
		this.fireEvent("chekExists", message, recHisCode, diseaseCode);
		if (message && message.length > 0) {
			var pastMess = "";
			var disText = [];
			var pastId = [];
			for (var i = 0; i < message.length; i++) {
				var mess = message[i];
				pastId.push(mess.get("pastHistoryId"));
				disText.push(mess.get("diseaseText"));
				pastMess += mess.get("diseaseText")
				if (i != message.length - 1) {
					pastMess += ",";
				}
			}
			Ext.Msg.show({
				title : '确认增加记录[' + recDis + ']',
				msg : '已经存在[' + recHisText + ":" + pastMess
						+ ']的记录，数据保存后将同时删除已经存在的记录，是否继续?',
				modal : true,
				width : 300,
				buttons : Ext.MessageBox.OKCANCEL,
				multiline : false,
				fn : function(btn, text) {
					if (btn == "ok") {
						for (var j = 0; j < disText.length; j++) {
							var dit = disText[j];
							var index = this.store.findBy(function(record, id) {
										var disText = record.get("diseaseText");
										var pastType = record
												.get("pastHisTypeCode");
										if (disText == dit
												&& recHisCode == pastType) {
											return true;
										}
									}, this)
							var record = this.store.getAt(index);
							if (record) {
								this.store.remove(record);
							}
						}
						for (var k = 0; k < pastId.length; k++) {
							this.delPast.push(pastId[k]);
						}
						saveData["delPastId"] = pastId;
						this.addList(recDis, recHisCode, diseaseCode, saveData);
					}
				},
				scope : this
			})
		} else {
			this.addList(recDis, recHisCode, diseaseCode, saveData);
		}
	},

	addList : function(recDis, recHisCode, diseaseCode, saveData) {
		var lastCode = diseaseCode.substring(diseaseCode.length - 2,
				diseaseCode.length);
		if (lastCode == "99" || lastCode == "98") {
			this.storeAddItem(saveData);
		} else {
			var result = this.chekHasThis(recDis, recHisCode);
			if (result) {
				Ext.Msg.alert("提示信息", "该记录已经存在,无法重复添加!");
			} else {
				if (this.fireEvent("checkHasThis", recDis, recHisCode)) {
					Ext.Msg.alert("提示信息", "该记录已经存在,无法重复添加!");
				} else {
					this.storeAddItem(saveData);
				}
			}
		}
	},

	chekHasThis : function(recDis, recHisCode) {
		for (var i = 0; i < this.store.getCount(); i++) {
			var storeItem = this.store.getAt(i);
			var disText = storeItem.get('diseaseText');
			var disCode = storeItem.get('diseaseCode');
			var typeCode = storeItem.get('pastHisTypeCode');
			if (disText == recDis && typeCode == recHisCode) {
				return true;
				break;
			}
			if (i == this.store.getCount() - 1) {
				return false;
			}
		}
	},

	checkDis : function(recHisCode, diseaseCode) {
		var saveData = [];
		for (var i = 0; i < this.store.getCount(); i++) {
			var storeItem = this.store.getAt(i);
			var disText = storeItem.get('diseaseText');
			var disCode = storeItem.get('diseaseCode');
			var typeCode = storeItem.get('pastHisTypeCode');
			if (recHisCode == typeCode) {
				var nullDisCode = typeCode + "01";
				if ((diseaseCode != nullDisCode && disCode == nullDisCode)
						|| (diseaseCode == nullDisCode && disCode != nullDisCode)) {
					saveData.push(storeItem);
				}
			}
		}
		return saveData;
	},

	storeAddItem : function(saveData) {
		var items = this.schema.items;
		var r = {};
		for (var i = 0; i < items.length; i++) {
			var it = items[i];
			if (it.dic) {
				r[it.id + "_text"] = saveData[it.id + "_text"];
			}
			r[it.id] = saveData[it.id];
		}
		if (r.diseaseText == null) {
			return;
		}
		r.delPastId = saveData["delPastId"];
		var record = new Ext.data.Record(r);
		this.store.add(record);
		this.grid.getSelectionModel().selectLastRow();
		this.fireEvent("afterSave");
	},

	doModify : function(item, e) {
		var record = this.grid.getSelectionModel().getSelected();
		this.fireEvent("beforeModify", record.data)
	},

	doDelete : function(item, e) {
		var r = this.grid.getSelectionModel().getSelected();
		if (r == null) {
			return;
		}
		Ext.Msg.show({
					title : '确认删除记录[' + r.get("diseaseText") + ']',
					msg : '删除操作将无法恢复，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							var delPastId = r.get("delPastId") || [];
							var delLength = delPastId.length;
							if (delLength > 0) {
								for (var i = 0; i < delLength; i++) {
									var pastId = delPastId[i];
									this.delPast.remove(pastId);
								}
							}
							this.store.remove(r);
						}
					},
					scope : this
				})
	},

	onDblClick : function(grid, index, e) {
		var item = {
			id : "modify",
			name : "修改"
		};
		this.doModify(item);
	},

	doCancel : function(item, e) {
		if (this.store.getCount() > 0)
			Ext.Msg.show({
						title : '确认取消操作',
						msg : '取消操作将失去所有未保存数据,且无法恢复，是否继续?',
						modal : false,
						width : 300,
						buttons : Ext.MessageBox.OKCANCEL,
						multiline : false,
						fn : function(btn, text) {
							if (btn == "ok") {
								this.store.removeAll();
								this.fireEvent("cancel");
							}
						},
						scope : this
					})
		else {
			this.fireEvent("cancel");
		}
	},

	doSaveAll : function(item, e) {
		var records = [];
		for (var i = 0; i < this.store.getCount(); i++) {
			var storeItem = this.store.getAt(i);
			records.push(storeItem.data);
		}
		if (records.length < 1) {
			return;
		}
		this.grid.el.mask("正在保存数据...", "x-mask-loading");
		util.rmi.jsonRequest({
					serviceId : this.saveServiceId,
					serviceAction : this.saveAction || "simpleSave",
					method:"execute",
					op : "create",
					schema : this.entryName,
					body : {
						"empiId" : this.empiId,
						"record" : records,
						"delPastId" : this.delPast
					}
				}, function(code, msg, json) {
					this.grid.el.unmask()
					this.saving = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.saveToServer,
								[records]);
						return;
					}
					this.store.removeAll();
					this.fireEvent("save", this.entryName, "create", json,
							records);
				}, this)
	},

	doSelectAll : function(item, e) {
		var saveData = [];
		var selectData = [{
					diseaseText : "无药物过敏史",
					pastHisTypeCode : "01",
					diseaseCode : "0101",
					pastHisTypeCode_text : "药物过敏史"
				}, {
					diseaseText : "无疾病史",
					pastHisTypeCode : "02",
					diseaseCode : "0201",
					pastHisTypeCode_text : "疾病史"
				}, {
					diseaseText : "无手术史",
					pastHisTypeCode : "03",
					diseaseCode : "0301",
					pastHisTypeCode_text : "手术史"
				}, {
					diseaseText : "无输血情况",
					pastHisTypeCode : "04",
					diseaseCode : "0401",
					pastHisTypeCode_text : "输血情况"
				}, {
					diseaseText : "无家族遗传病史",
					pastHisTypeCode : "05",
					diseaseCode : "0501",
					pastHisTypeCode_text : "家族遗传病史"
				}, {
					diseaseText : "无外伤",
					pastHisTypeCode : "06",
					diseaseCode : "0601",
					pastHisTypeCode_text : "外伤"
				}, {
					diseaseText : "无父亲疾病史",
					pastHisTypeCode : "07",
					diseaseCode : "0701",
					pastHisTypeCode_text : "家族疾病史-父亲"
				}, {
					diseaseText : "无母亲疾病史",
					pastHisTypeCode : "08",
					diseaseCode : "0801",
					pastHisTypeCode_text : "家族疾病史-母亲"
				}, {
					diseaseText : "无兄弟姐妹疾病史",
					pastHisTypeCode : "09",
					diseaseCode : "0901",
					pastHisTypeCode_text : "家族疾病史-兄弟姐妹"
				}, {
					diseaseText : "无子女疾病史",
					pastHisTypeCode : "10",
					diseaseCode : "1001",
					pastHisTypeCode_text : "家族疾病史-子女"
				}, {
					diseaseText : "无残疾",
					pastHisTypeCode : "11",
					diseaseCode : "1101",
					pastHisTypeCode_text : "残疾状况"
				}, {
					diseaseText : "无暴露史",
					pastHisTypeCode : "12",
					diseaseCode : "1201",
					pastHisTypeCode_text : "职业暴露史"
				}];
		for (var i = 0; i < selectData.length; i++) {
			var diseaseText = selectData[i].diseaseText;
			var pastHisTypeCode = selectData[i].pastHisTypeCode;
			var diseaseCode = selectData[i].diseaseCode;
			var pastHisTypeCode_text = selectData[i].pastHisTypeCode_text;

			if (pastHisTypeCode == "10" && this.maritalStatusCode == "10") {
				continue;
			}
			saveData["diseaseText"] = diseaseText;
			saveData["pastHisTypeCode"] = pastHisTypeCode;
			saveData["diseaseCode"] = diseaseCode;
			saveData["pastHisTypeCode_text"] = pastHisTypeCode_text;
			saveData["empiId"] = this.empiId;
			saveData["recordUser"] = this.mainApp.userId;
			saveData["recordDate"] = this.mainApp.serverDate;
			saveData["recordUnit"] = this.mainApp.deptId;

			if (this.checkRecordInvalid(pastHisTypeCode)) {
				continue;
			} else {
				this.storeAddItem(saveData);
			}
		}
	},

	checkRecordInvalid : function(pastHisTypeCode) {
		var index = this.store.find("pastHisTypeCode", pastHisTypeCode);
		if (index >= 0) {
			return true;
		} else {
			return this.fireEvent("chekTypeExists", pastHisTypeCode);
		}
	}
});