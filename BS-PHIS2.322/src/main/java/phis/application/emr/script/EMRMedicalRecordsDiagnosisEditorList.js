$package("phis.application.emr.script")

$import("phis.script.EditorList", "util.dictionary.SimpleDicFactory")

phis.application.emr.script.EMRMedicalRecordsDiagnosisEditorList = function(cfg) {
	cfg.disablePagingTbr = true;// 不分页
	// cfg.autoLoadData = false;
	cfg.selectOnFocus = true;
	cfg.sortable = false;
	cfg.remoteUrl = 'MedicalDiagnosis';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="268px">{MSZD}</td></td>';
	cfg.BWremoteUrl = 'DiseaseSite';
	cfg.BWremoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="268px">{SYMC}</td></td>';
	phis.application.emr.script.EMRMedicalRecordsDiagnosisEditorList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.emr.script.EMRMedicalRecordsDiagnosisEditorList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				var grid = phis.application.emr.script.EMRMedicalRecordsDiagnosisEditorList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				sm.onEditorKey = function(field, e) {
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (k == e.ENTER) {
						e.stopEvent();
						if (!ed) {
							ed = g.lastActiveEditor;
						}
						ed.completeEdit();
						if (e.shiftKey) {
							newCell = g.walkCells(ed.row, ed.col - 1, -1,
									sm.acceptsNav, sm);
						} else {
							newCell = g.walkCells(ed.row, ed.col + 1, 1,
									sm.acceptsNav, sm);
						}

					} else if (k == e.TAB) {
						e.stopEvent();
						ed.completeEdit();
						if (e.shiftKey) {
							newCell = g.walkCells(ed.row, ed.col - 1, -1,
									sm.acceptsNav, sm);
						} else {
							newCell = g.walkCells(ed.row, ed.col + 1, 1,
									sm.acceptsNav, sm);
						}
					} else if (k == e.ESC) {
						ed.cancelEdit();
					}
					if (newCell) {
						r = newCell[0];
						c = newCell[1];
						this.select(r, c);
						if (g.isEditor && !g.editing) {
							ae = g.activeEditor;
							if (ae && ae.field.triggerBlur) {
								ae.field.triggerBlur();
							}
							g.startEditing(r, c);
						}
					}

				};
				return grid
			},
			beforeCellEdit : function(e) {
				var f = e.field
				var record = e.record
				var op = record.get("_opStatus")
				var cm = this.grid.getColumnModel()
				var c = cm.config[e.column]
				var enditor = cm.getCellEditor(e.column, e.row)
				if (!this.exContext.SXQX) {
					return false;
				}
				var r = this.getSelectedRecord()
				if ((e.column == 9 || e.column == 10) && r.get("ZDLB") != '51') {
					this.grid.startEditing(e.row + 1, 3);
					return false;
				}
				if (e.column == 4) {
					this.remoteDic.lastQuery = "";
					if (this.remoteDicStore.baseParams) {
						this.remoteDicStore.baseParams.ZXLB = record
								.get("ZXLB");
					} else {
						this.remoteDicStore.baseParams = {};
						this.remoteDicStore.baseParams.ZXLB = record
								.get("ZXLB");
					}
				} else if (e.column == 6) {
					if (record.get("ZXLB")) {
						if (record.get("JBXH")) {
							this.remoteDic.lastQuery = "";
							if (this.BWremoteDicStore.baseParams) {
								this.BWremoteDicStore.baseParams.JBXH = record
										.get("JBXH");
								this.BWremoteDicStore.baseParams.ZXLB = record
										.get("ZXLB");
							} else {
								this.BWremoteDicStore.baseParams = {};
								this.BWremoteDicStore.baseParams.JBXH = record
										.get("JBXH");
								this.BWremoteDicStore.baseParams.ZXLB = record
										.get("ZXLB");
							}
						} else {
							return false;
						}
					} else {
						return false;
					}
				}
				var it = c.schemaItem
				var ac = util.Accredit;
				if (op == "create") {
					if (!ac.canCreate(it.acValue)) {
						return false
					}
				} else {
					if (!ac.canUpdate(it.acValue)) {
						return false
					}
				}
				// add by yangl 回写未修改下的中文名称
				e.value = e.value || ""
				return this.fireEvent("beforeCellEdit", it, record,
						enditor.field, e.value)
			},
			onReady : function() {
				this.requestData.serviceId = "phis.emrMedicalRecordsService";
				this.requestData.serviceAction = "zYZDJLLoad";
				/**************modify by lizhi at 修改诊断*******************/
				this.requestData.rycnd = [
						'and',
						['eq', ['$', 'JZXH'],
								['i', this.exContext.empiData.ZYH]],
						['eq', ['$', 'ZDLB'], ['i', 22]]];
				/**************modify by lizhi at 修改诊断*******************/	
				this.requestData.mzcnd = [
						'and',
						['eq', ['$', 'JZXH'],
								['i', this.exContext.empiData.ZYH]],
						['eq', ['$', 'ZDLB'], ['i', 11]]];
				this.requestData.blcnd = [
						'and',
						['eq', ['$', 'JZXH'],
								['i', this.exContext.empiData.ZYH]],
						['eq', ['$', 'ZDLB'], ['i', 44]]];
				this.requestData.sscnd = [
						'and',
						['eq', ['$', 'JZXH'],
								['i', this.exContext.empiData.ZYH]],
						['eq', ['$', 'ZDLB'], ['i', 45]]];
				this.requestData.cycnd = [
						'and',
						['eq', ['$', 'JZXH'],
								['i', this.exContext.empiData.ZYH]],
						['eq', ['$', 'ZDLB'], ['i', 51]]];
				this.requestData.schema = this.getMySchema(this.entryName).items;
				var store = this.grid.getStore();
				var cm = this.grid.getColumnModel()
				var ZXLB = cm.getCellEditor(3)
				ZXLB.field.on("change", function() {
							var r = store.getAt(ZXLB.row);
							r.set("MSZD", "");
							r.set("JBXH", "");
							r.set("JBBM", "");
							r.set("JBBW", "");
							r.set("FJBS", "");
							r.set("JBBW_FJBS", "");
							if (ZXLB.row != 1 && ZXLB.row != 2 && ZXLB.row != 3) {
								this.setTopJBMC();
							}
						}, this);
				phis.application.emr.script.EMRMedicalRecordsDiagnosisEditorList.superclass.onReady
						.call(this);
			},
			setTopJBMC : function() {
				var store = this.grid.getStore();
				var n = store.getCount()
				var JBMC = "";
				var jbcs = 0;
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (i != 1 && i != 2 && i != 3) {
						if (r.get("JBBM")) {
							jbcs++;
							JBMC += jbcs + "." + r.get("MSZD");
						}
					}
				}
				this.opener.opener.exContext.empiData.JBMC = JBMC;
				var tpl = this.opener.opener.getNewTopTemplate()
				this.opener.opener.topPanel.body.update(tpl
						.apply(this.opener.opener.exContext.empiData))
				this.opener.opener.attachTopLnkEnvents()
			},
			// setTopJBMC : function(){
			//				
			// },
			onRenderer : function(value, metaData, r) {
				var ZZBZ = r.get("ZZBZ");
				if (ZZBZ == 1) {
					// var src = (ZZBZ == 1) ? "(00,00)" : "(00,04)";
// return "<img src='images/(00,00).png'/>";
					return "<img src='" + ClassLoader.appRootOffsetPath
					+ "resources/phis/resources/images/yes.png'/>";
				}
			},
			doAddCYZD : function() {
				this.doInsert();
			},
			doInsert : function(item, e) {
				var store = this.grid.getStore();
				var row = store.getCount()
				var storeData = store.data;
				var upRowItem = storeData.itemAt(row - 1);
				if (upRowItem.get("JBBM") == null
						|| upRowItem.get("JBBM") == ""
						|| upRowItem.get("JBBM") == 0) {
					return
				}
				if (row > 0) {
					if (!upRowItem.data.ZDYS) {
						MyMessageTip.msg("提示", '诊断医生不能为空!', true);
						return;
					}
					if (!upRowItem.data.ZDRQ) {
						MyMessageTip.msg("提示,", '诊断日期不能为空!', true);
						return;
					}
					if (!upRowItem.data.RYBQDM) {
						MyMessageTip.msg("提示", '出院诊断的【入院病情诊断】不能为空!', true);
						return
					}
					if (!upRowItem.data.CYZGDM) {
						MyMessageTip.msg("提示", '出院诊断的【出院转归情况】不能为空!', true);
						return
					}
				}
				if (store.getCount() > 26) {
					MyMessageTip.msg("提示", '出院诊断不能超过23条!', true, 5);
					return;
				}
				var o = this.getStoreFields(this.schema.items)
				var Record = Ext.data.Record.create(o.fields)
				var items = this.schema.items
				var factory = util.dictionary.DictionaryLoader
				var data = {
					'_opStatus' : 'create'
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					var v = null
					if (it.defaultValue) {
						v = it.defaultValue
						data[it.id] = v
						var dic = it.dic
						if (dic) {
							data[it.id] = v.key;
							var o = factory.load(dic)
							if (o) {
								var di = o.wraper[v.key];
								if (di) {
									data[it.id + "_text"] = di.text
								}
							}
						}
					}
					if (it.type && it.type == "int") {
						data[it.id] = (data[it.id] == "0" || data[it.id] == "" || data[it.id] == undefined)
								? 0
								: parseInt(data[it.id]);
					}

				}
				var r = new Record(data)
				store.insert(row, [r])
				this.grid.getView().refresh()// 刷新行号
				var rowItem = storeData.itemAt(row);
				rowItem.set("ZXLB", upRowItem.get("ZXLB"));
				rowItem.set("ZXLB_text", upRowItem.get("ZXLB_text"));
				rowItem.set("ZDYS", upRowItem.get("ZDYS"));
				rowItem.set("ZDYS_text", upRowItem.get("ZDYS_text"));
				this.grid.startEditing(row, 4);

			},
			getMySchema : function(entryName) {
				var schema = "";
				if (this.opener.schema) {
					schema = this.opener.schema[entryName]
				}
				if (!schema) {
					var re = util.schema.loadSync(entryName)
					if (re.code == 200) {
						schema = re.schema;
						if (this.opener.schema) {
							this.opener.schema[entryName] = schema;
						} else {
							this.opener.schema = {}
							this.opener.schema[entryName] = schema;
						}
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				return schema;
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'disease',
							totalProperty : 'count',
							id : 'mdssearch_a'
						}, [{
									name : 'numKey'
								}, {
									name : 'JBXH'
								}, {
									name : 'MSZD'

								}, {
									name : 'JBBM'
								}]);
			},
			getBWRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'disease',
							totalProperty : 'count',
							id : 'mdssearch_a'
						}, [{
									name : 'numKey'
								}, {
									name : 'JBBW'
								}, {
									name : 'SYMC'

								}]);
			},
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				if (!obj.getValue())
					return
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var rowItem = griddata.itemAt(row);
				obj.collapse();
				rowItem.set('JBXH', record.get("JBXH"));
				rowItem.set('MSZD', record.get("MSZD"));
				rowItem.set('JBBM', record.get("JBBM"));
				rowItem.set('JBMC', record.get("MSZD"));
				rowItem.set('JBBW', "");
				rowItem.set('FJBS', "");
				rowItem.set('JBBW_FJBS', "");
				rowItem.modified['MSZD'] = record.get("MSZD");
				obj.setValue(record.get("MSZD"));
				obj.triggerBlur();
				this.grid.startEditing(row, 6);
				if (row != 1 && row != 2 && row != 3) {
					this.setTopJBMC();
				}
			},
			setBWBackInfo : function(obj, record) {
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var rowItem = griddata.itemAt(row);
				obj.collapse();
				rowItem.set('MSZD', rowItem.get("JBMC") + "("
								+ record.get("SYMC") + ")");
				if(rowItem.get("ZXLB")==1){
					rowItem.set('JBBW', record.get("JBBW"));
				}else{
					rowItem.set('FJBS', record.get("JBBW"));
				}
				rowItem.set('JBBW_FJBS', record.get("SYMC"));
				rowItem.modified['JBBW_FJBS'] = record.get("SYMC");
				obj.setValue(record.get("SYMC"));
				this.grid.startEditing(row, 9);
				if (row != 1 && row != 2 && row != 3) {
					this.setTopJBMC();
				}
			},
			doInsertAfter : function(item, e, newGroup) {// 当前记录后插入一条记录
				var r = this.getSelectedRecord()
				if (!r.data.ZDYS) {
					MyMessageTip.msg("提示", '诊断医生不能为空!', true);
					return;
				}
				if (!r.data.ZDRQ) {
					MyMessageTip.msg("提示,", '诊断日期不能为空!', true);
					return;
				}
				if (r.data.ZDLB == 51) {
					if (!r.data.RYBQDM) {
						MyMessageTip.msg("提示", '出院诊断的【入院病情诊断】不能为空!', true);
						return
					}
					if (!r.data.CYZGDM) {
						MyMessageTip.msg("提示", '出院诊断的【出院转归情况】不能为空!', true);
						return
					}
				}
				var store = this.grid.getStore();
				var maxIndex = store.getCount();
				var n = this.store.indexOf(r)
				if (n >= (maxIndex - 1)) {
					this.doInsert();
				} else {
					this.grid.startEditing(n + 1, 4);
				}
			},
			afterCellEdit : function(e) {
				var f = e.field
				var v = e.value
				var record = e.record
				var cm = this.grid.getColumnModel()
				var enditor = cm.getCellEditor(e.column, e.row)
				var c = cm.config[e.column]
				var it = c.schemaItem
				var field = enditor.field
				if (it.dic) {
					record.set(f + "_text", field.getRawValue())
				}
				if (it.type == "date") {
					if (v) {
						var dt = new Date(v)
						v = dt.format('Y-m-d')
						record.set(f, v)
					}
				}
				if (it.codeType)
					record.set(f, v.toUpperCase())
				if (this.CodeFieldSet) {
					var bField = {};
					for (var i = 0; i < this.CodeFieldSet.length; i++) {
						var CodeField = this.CodeFieldSet[i];
						var target = CodeField[0];
						var codeType = CodeField[1];
						var srcField = CodeField[2];
						if (it.id == target) {
							if (!bField.codeType)
								bField.codeType = [];
							bField.codeType.push(codeType);
							if (!bField.srcField)
								bField.srcField = [];
							bField.srcField.push(srcField);
						}
					}
					if (bField.srcField) {
						var body = {};
						body.codeType = bField.codeType;
						body.value = v;
						var ret = phis.script.rmi.miniJsonRequestSync({
									serviceId : "codeGeneratorService",
									serviceAction : "getCode",
									body : body
								});
						if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg,
									this.saveToServer, [saveData]);
							return;
						}
						for (var i = 0; i < bField.codeType.length; i++) {
							record
									.set(bField.srcField[i],
											eval('ret.json.body.'
													+ bField.codeType[i]));
						}
					}
				}
				// add by yangl 回写未修改下的中文名称
				// if (field.isSearchField) {
				// var patrn = /^[a-zA-Z0-9]+$/;
				// if ((v.trim() == "" || patrn.exec(v))
				// && this.beforeSearchQuery()) {
				// record.set(f, record.modified[f]);
				// } else {
				// record.modified[f] = v;
				// }
				// }
				if (field.isSearchField) {// 远程查询字段不允许修改
					if (!field.bySelect) {
						if (record.get(f)) {
							if (f == 'MSZD') {
								record.set("MSZD",r.modified['MSZD'] + "(" + record.get("JBBW_FJBS") + ")")
							} else {
								record.set(f, r.modified[f])
							}
						} else {
							if (f == 'MSZD') {
								record.set("JBXH", "");
								record.set("JBBM", "");
								record.set("JBBW", "");
								record.set("JBBW_FJBS", "");
								record.set("FJBS", "");
								if (e.row != 1 && e.row != 2 && e.row != 3) {
									this.setTopJBMC();
								}
							} else if (f == 'JBBW_FJBS') {
								record.set(f, "")
								record.set("JBBW", "")
								record.set("FJBS", "")
								record.set("MSZD", r.modified['MSZD'])
								if (e.row != 1 && e.row != 2 && e.row != 3) {
									this.setTopJBMC();
								}
							}
						}
					}
					field.bySelect = false;
				}
				this.fireEvent("afterCellEdit", it, record, field, v)
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				if (records.length == 0) {
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				var n = store.getCount()

				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (!r.modified) {
						r.modified = {};
					}
					r.modified['MSZD'] = r.get('MSZD');
				}
			},
			doMoveUp : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg('警告', '请选中需要上移的【出院诊断】记录!', true);
					return;
				}
				var index = this.grid.getSelectionModel().getSelectedCell()[0];// 当前选中行号
				if (index < 4) {
					MyMessageTip.msg('警告', '非【出院诊断】不能上下移!', true);
					return;
				}
				if (index == 4) {
					MyMessageTip.msg('警告', '主诊断不能上下移!', true);
					return;
				}
				var store = this.grid.getStore();
				if (index == 5) {
					MyMessageTip.msg('警告', '当前【出院诊断】记录已是顶级节点!', true);
					return;
				}
				store.remove(record);
				store.insert(index - 1, record);
				this.grid.getView().refresh();
				this.grid.getSelectionModel().select(index - 1, 1);
			},
			doMoveDown : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg('警告', '请选中需要下移的【出院诊断】记录', true);
					return;
				}
				var index = this.grid.getSelectionModel().getSelectedCell()[0];// 当前选中行号
				if (index < 4) {
					MyMessageTip.msg('警告', '非【出院诊断】不能上下移!', true);
					return;
				}
				if (index == 4) {
					MyMessageTip.msg('警告', '主诊断不能上下移!', true);
					return;
				}
				var store = this.grid.getStore();
				var count = store.getCount();
				if (index == count - 1) {
					MyMessageTip.msg('警告', '当前【出院诊断】记录已是底级节点', true);
					return;
				}
				store.remove(record);
				store.insert(index + 1, record);
				this.grid.getView().refresh();
				this.grid.getSelectionModel().select(index + 1, 1);
			},
			onDblClick : function() {
				var index = this.grid.getSelectionModel().getSelectedCell()[0];// 当前选中行号
				if (index <= 4) {
					return;
				}
				var record = this.getSelectedRecord();
				var store = this.grid.getStore();
				var storeData = store.data;
				var upRowItem = storeData.itemAt(4);
				upRowItem.set("ZZBZ", 0);
				record.set("ZZBZ", 1);
				store.remove(record);
				store.insert(4, record);
				this.grid.getView().refresh();
				this.grid.getSelectionModel().select(4, 1);
			},
			doAddCommon : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					MyMessageTip.msg('警告', '请选中需要添加为常用诊断的记录!', true);
					return;
				}
				if ((!record.data.MSZD) || (!record.data.JBBM)) {
					MyMessageTip.msg('警告', '当前所选中的行没有诊断!', true);
					return;
				}
				var res = phis.script.rmi.miniJsonRequestSync({
							serviceId : "emrMedicalRecordsService",
							serviceAction : "saveCommon",
							body : record.data
						});
				var code = res.code;
				var msg = res.msg;
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return;
				}
				MyMessageTip.msg('提示', '增加常用诊断成功!', true);
			},
			backFill : function(record) {
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg('警告', '请先选中需要诊断的记录!', true);
					return;
				}
				r.set("ZXLB", record.get("ZXLB"));
				r.set("ZXLB_text", record.get("ZXLB_text"));
				r.set("JBBW_FJBS", record.get("FJBS_text"));
				if (record.get("ZXLB") == 1) {
					r.set("JBBW", record.get("FJBS"));
					r.set("FJBS", "");
				} else if (record.get("ZXLB") == 2) {
					r.set("JBBW", "");
					r.set("FJBS", record.get("FJBS"));
				}
				r.set("JBXH", record.get("JBBS"));
				r.set("JBBM", record.get("FJMC"));
				r.set("MSZD", record.get("MSZD"));
				var index = this.grid.getSelectionModel().getSelectedCell()[0];// 当前选中行号
				if (index != 1 && index != 2 && index != 3) {
					this.setTopJBMC();
				}
				r.set("JBMC", record.get("JBMC"));
			},
			createRemoteDicField : function(it) {
				if (it.id == 'MSZD') {
					var mds_reader = this.getRemoteDicReader();
					// store远程url
					var url = ClassLoader.serverAppUrl || "";
					this.comboJsonData = {
						serviceId : "phis.searchService",
						serviceAction : "loadDicData",
						method : "execute",
						className : this.remoteUrl
						// ,pageSize : this.pageSize || 25,
						// pageNo : 1
					}
					var proxy = new Ext.data.HttpProxy({
						url : url + '*.jsonRequest',
						method : 'POST',
						jsonData : this.comboJsonData
					});
					var mdsstore = new Ext.data.Store({
						proxy : proxy,
						reader : mds_reader
					});
					proxy.on("loadexception", function(proxy, o, response, arg, e) {
						if (response.status == 200) {
							var json = eval("(" + response.responseText + ")")
							if (json) {
								var code = json["code"]
								var msg = json["msg"]
								this.processReturnMsg(code, msg, this.refresh)
							}
						} else {
							this.processReturnMsg(404, 'ConnectionError',
									this.refresh)
						}
					}, this)
					this.remoteDicStore = mdsstore;
					Ext.apply(this.remoteDicStore.baseParams, this.queryParams);
					var resultTpl = new Ext.XTemplate(
							'<tpl for=".">',
							'<div class="search-item">',
							'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
							'<tr>' + this.remoteTpl + '<tr>', '</table>',
							'</div>', '</tpl>');
					var _ctx = this;
					var remoteField = new Ext.form.ComboBox({
								// id : "YPMC",
								width : 280,
								store : mdsstore,
								selectOnFocus : true,
								typeAhead : false,
								loadingText : '搜索中...',
								pageSize : 10,
								hideTrigger : true,
								minListWidth : this.minListWidth || 280,
								tpl : resultTpl,
								minChars : 2,
								enableKeyEvents : true,
								lazyInit : false,
								itemSelector : 'div.search-item',
								onSelect : function(record) { // override
									// default
									// onSelect to
									// do
									this.bySelect = true;
									_ctx.setBackInfo(this, record);
								}
							});
					remoteField.on("focus", function() {
								remoteField.innerList.setStyle('overflow-y',
										'hidden');
							}, this);
					remoteField.on("keyup", function(obj, e) {// 实现数字键导航
								obj.lastQuery = "";
								var key = e.getKey();
								if ((key >= 48 && key <= 57)
										|| (key >= 96 && key <= 105)) {
									var searchTypeValue = _ctx.cookie
											.getCookie(_ctx.mainApp.uid
													+ "_searchType");
									if (searchTypeValue != 'BHDM') {
										if (obj.isExpanded()) {
											if (key == 48 || key == 96)
												key = key + 10;
											key = key < 59 ? key - 49 : key
													- 97;
											var record = this.getStore()
													.getAt(key);
											obj.bySelect = true;
											_ctx.setBackInfo(obj, record);
										}
									}
								}
								// 支持翻页
								if (key == 37) {
									obj.pageTb.movePrevious();
								} else if (key == 39) {
									obj.pageTb.moveNext();
								}
								// 删除事件 8
								if (key == 8) {
									if (obj.getValue().trim().length == 0) {
										if (obj.isExpanded()) {
											obj.collapse();
										}
									}
								}
							})
					if (remoteField.store) {
						remoteField.store.load = function(options) {
							Ext.apply(_ctx.comboJsonData, options.params);
							Ext.apply(_ctx.comboJsonData, mdsstore.baseParams);
							options = Ext.apply({}, options);
							this.storeOptions(options);
							if (this.sortInfo && this.remoteSort) {
								var pn = this.paramNames;
								options.params = Ext.apply({}, options.params);
								options.params[pn.sort] = this.sortInfo.field;
								options.params[pn.dir] = this.sortInfo.direction;
							}
							try {
								return this.execute('read', null, options); // <--
																			// null
								// represents
								// rs. No rs for
								// load actions.
							} catch (e) {
								this.handleException(e);
								return false;
							}
						}
					}
					remoteField.isSearchField = true;
					remoteField.on("beforequery", function(qe) {
							this.comboJsonData.query = qe.query;
								// 设置下拉框的分页信息
								// remoteField.pageTb.changePage(0);
								return this.beforeSearchQuery();
							}, this);
					// remoteField.store.on("load",function(store){
					// if(store.getCount() == 1) {
					// this.setBackInfo(remoteField,store.getAt(0));
					// }
					// },this);
					this.remoteDic = remoteField;
					return remoteField
				} else if (it.id == 'JBBW_FJBS') {
					var mds_reader = this.getBWRemoteDicReader();
					// store远程url
					var url = ClassLoader.serverAppUrl || "";
					this.BWcomboJsonData = {
						serviceId : "phis.searchService",
						serviceAction : "loadDicData",
						method : "execute",
						className : this.BWremoteUrl
						// ,pageSize : this.pageSize || 25,
						// pageNo : 1
					}
					var proxy = new Ext.data.HttpProxy({
						url : url + '*.jsonRequest',
						method : 'POST',
						jsonData : this.BWcomboJsonData
					});
					var mdsstore = new Ext.data.Store({
						proxy : proxy,
						reader : mds_reader
					});
					proxy.on("loadexception", function(proxy, o, response, arg, e) {
						if (response.status == 200) {
							var json = eval("(" + response.responseText + ")")
							if (json) {
								var code = json["code"]
								var msg = json["msg"]
								this.processReturnMsg(code, msg, this.refresh)
							}
						} else {
							this.processReturnMsg(404, 'ConnectionError',
									this.refresh)
						}
					}, this)
					this.BWremoteDicStore = mdsstore;
					Ext.apply(this.BWremoteDicStore.baseParams,
							this.queryParams);
					var resultTpl = new Ext.XTemplate(
							'<tpl for=".">',
							'<div class="search-item">',
							'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
							'<tr>' + this.BWremoteTpl + '<tr>', '</table>',
							'</div>', '</tpl>');
					var _ctx = this;
					var remoteField = new Ext.form.ComboBox({
								// id : "YPMC",
								width : 280,
								store : mdsstore,
								selectOnFocus : true,
								typeAhead : false,
								loadingText : '搜索中...',
								pageSize : 10,
								hideTrigger : true,
								minListWidth : this.minListWidth || 280,
								tpl : resultTpl,
								minChars : 2,
								enableKeyEvents : true,
								lazyInit : false,
								itemSelector : 'div.search-item',
								onSelect : function(record) { // override
									// default
									// onSelect to
									// do
									this.bySelect = true;
									_ctx.setBWBackInfo(this, record);
								}
							});
					remoteField.on("focus", function() {
								remoteField.innerList.setStyle('overflow-y',
										'hidden');
							}, this);
					remoteField.on("keyup", function(obj, e) {// 实现数字键导航
								var key = e.getKey();
								if ((key >= 48 && key <= 57)
										|| (key >= 96 && key <= 105)) {
									var searchTypeValue = _ctx.cookie
											.getCookie(_ctx.mainApp.uid
													+ "_searchType");
									if (searchTypeValue != 'BHDM') {
										if (obj.isExpanded()) {
											if (key == 48 || key == 96)
												key = key + 10;
											key = key < 59 ? key - 49 : key
													- 97;
											var record = this.getStore()
													.getAt(key);
											obj.bySelect = true;
											_ctx.setBWBackInfo(obj, record);
										}
									}
								}
								// 支持翻页
								if (key == 37) {
									obj.pageTb.movePrevious();
								} else if (key == 39) {
									obj.pageTb.moveNext();
								}
								// 删除事件 8
								if (key == 8) {
									if (obj.getValue().trim().length == 0) {
										if (obj.isExpanded()) {
											obj.collapse();
										}
									}
								}
							})
					if (remoteField.store) {
						remoteField.store.load = function(options) {
							Ext.apply(_ctx.BWcomboJsonData, options.params);
							Ext.apply(_ctx.BWcomboJsonData, mdsstore.baseParams);
							options = Ext.apply({}, options);
							this.storeOptions(options);
							if (this.sortInfo && this.remoteSort) {
								var pn = this.paramNames;
								options.params = Ext.apply({}, options.params);
								options.params[pn.sort] = this.sortInfo.field;
								options.params[pn.dir] = this.sortInfo.direction;
							}
							try {
								return this.execute('read', null, options); // <--
																			// null
								// represents
								// rs. No rs for
								// load actions.
							} catch (e) {
								this.handleException(e);
								return false;
							}
						}
					}
					remoteField.isSearchField = true;
					remoteField.on("beforequery", function(qe) {
						this.BWcomboJsonData.query = qe.query;
								// 设置下拉框的分页信息
								// remoteField.pageTb.changePage(0);
								return this.beforeSearchQuery();
							}, this);
					// remoteField.store.on("load",function(store){
					// if(store.getCount() == 1) {
					// this.setBackInfo(remoteField,store.getAt(0));
					// }
					// },this);
					remoteField.doQuery = function(q, forceAll) {
						q = Ext.isEmpty(q) ? '' : q;
						var qe = {
							query : q,
							forceAll : forceAll,
							combo : this,
							cancel : false
						};
						if (this.fireEvent('beforequery', qe) === false
								|| qe.cancel) {
							return false;
						}
						q = qe.query;
						forceAll = qe.forceAll;
						if (forceAll === true || (q.length >= this.minChars)) {
							if (!this.lastQuery || this.lastQuery !== q) {
								this.lastQuery = q;
								if (this.mode == 'local') {
									this.selectedIndex = -1;
									if (forceAll) {
										this.store.clearFilter();
									} else {
										this.store.filter(this.displayField, q);
									}
									this.onLoad();
								} else {
									this.store.baseParams[this.queryParam] = q;
									this.store.load({
												params : this.getParams(q)
											});
									this.expand();
								}
							} else {
								this.selectedIndex = -1;
								this.onLoad();
							}
						}
					}
					this.remoteDic = remoteField;
					return remoteField
				}
			},
			doSave : function() {
				var store = this.grid.getStore();
				var n = store.getCount()
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.data.JBXH && r.data.JBBM && r.data.MSZD) {
						if (!r.data.ZXLB) {
							MyMessageTip.msg("提示", '【中西类别】不能为空!', true);
							return [];
						}
						if (!r.data.ZDYS) {
							MyMessageTip.msg("提示", '【诊断医生】不能为空!', true);
							return [];
						}
						if (!r.data.ZDRQ) {
							MyMessageTip.msg("提示", '【诊断日期】不能为空!', true);
							return [];
						}
						if (r.data.ZDLB == 51) {
							if (!r.data.RYBQDM) {
								MyMessageTip.msg("提示", '出院诊断的【入院病情诊断】不能为空!',
										true);
								return [];
							}
							if (!r.data.CYZGDM) {
								MyMessageTip.msg("提示", '出院诊断的【出院转归情况】不能为空!',
										true);
								return [];
							}
							if (!this.ZZMC) {
								this.ZZMC = r.data.MSZD;
							}
						}
						if (!r.data.JBBW) {
							r.data.JBBW = 0;
						}
						if (!r.data.FJBS) {
							r.data.FJBS = 0;
						}
						if (!r.data.RYBQDM) {
							r.data.RYBQDM = 0;
						}
						if (!r.data.CYZGDM) {
							r.data.CYZGDM = 0;
						}

						data.push(r.data)
					}
				}
				this.saving = true
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "emrMedicalRecordsService",
							serviceAction : "saveZYZDJL",
							ZYH : this.exContext.empiData.ZYH,
							data : data,
							BRID : this.exContext.empiData.BRID
						}, function(code, msg, json) {
							this.grid.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.doSave);
								return
							}
							this.grid.store.commitChanges();
							this.refresh();
							MyMessageTip.msg("提示", "保存成功！", true);
						}, this);
			},
			// 刷新,只有点刷新或保存按钮才会清除缓存数据
			doSx : function() {
				this.onReady();
			}
		})