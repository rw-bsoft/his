$package("phis.application.fsb.script")

$import("phis.script.EditorList")

phis.application.fsb.script.JCProjectEditorList = function(cfg) {
	// cf.requestData.cnd
	cfg.listServiceId = "medicalTechnicalSectionService";
	cfg.autoLoadData = false;
	cfg.listWidth = 270;
	cfg.disablePagingTbr = true;
	cfg.remoteUrl = 'MedicalTechnology';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{FYMC}</td><td width="50px">{FYDW}</td>';
	phis.application.fsb.script.JCProjectEditorList.superclass.constructor.apply(this,
			[cfg])

}
Ext.extend(phis.application.fsb.script.JCProjectEditorList, phis.script.EditorList, {
	doInsertAfter : function(i,e){
		this.doCreate(i,e);
	},
	initPanel : function(sc) {
		var grid = phis.application.fsb.script.JCProjectEditorList.superclass.initPanel
				.call(this, sc)
				// 重写grid的onEditorKey事件
		grid.onEditorKey = function(field, e) {
			if (e.getKey() == e.ENTER && !e.shiftKey) {
				var sm = this.getSelectionModel();
				var cell = sm.getSelectedCell();
				var count = this.colModel.getColumnCount()
				if (cell[1] + 3 >= count) {// 实现倒数第二格单元格回车新增行操作
					this.fireEvent("doNewColumn");
					return;
				}
			}
			this.selModel.onEditorKey(field, e);
		}
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
					newCell = g.walkCells(ed.row, ed.col + 1, 1, sm.acceptsNav,
							sm);
				}

			} else if (k == e.TAB) {
				e.stopEvent();
				ed.completeEdit();
				if (e.shiftKey) {
					newCell = g.walkCells(ed.row, ed.col - 1, -1,
							sm.acceptsNav, sm);
				} else {
					newCell = g.walkCells(ed.row, ed.col + 1, 1, sm.acceptsNav,
							sm);
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
		this.on("afterCellEdit", this.afterGridEdit, this);
		return grid
	},
	afterGridEdit : function(it, record, field, v) {
		if (it.id == "YLSL" || it.id == "YLDJ") {
			if(record.get("YLSL")){
				var v = parseFloat(record.get("YLDJ") * record.get("YLSL"))
						.toFixed(2);
				record.set("HJJE", v);
				this.setCountInfo();
			}
		}
	},
	expansion : function(cfg) {
		// 底部 统计信息,未完善
		var label = new Ext.form.Label({
			html : "<div id='YJZX_HJJE' align='center' style='color:blue'>统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "合计金额：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
					+ "自负金额：0.00&nbsp;&nbsp;￥</div>"
		})
		cfg.bbar = [];
		cfg.bbar.push(label);
	},
	setCountInfo : function() {
		var totalMoney = 0;
		var selfMoney = 0;
		for (var i = 0; i < this.store.getCount(); i++) {
			var r = this.store.getAt(i);
			var ylsl = parseFloat(r.get("YLSL"));
			var yldj = parseFloat(r.get("YLDJ"));
			var zfbl = parseFloat(r.get("ZFBL"));
			totalMoney += parseFloat(ylsl * yldj);
			selfMoney += parseFloat(ylsl * yldj * zfbl);
			if (isNaN(totalMoney)) {
				totalMoney = 0;
			}
			if (isNaN(selfMoney)) {
				selfMoney = 0;
			}
		}

		document.getElementById("YJZX_HJJE").innerHTML = "统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "合计金额："
				+ parseFloat(totalMoney).toFixed(2)
				+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
				+ "自负金额："
				+ parseFloat(selfMoney).toFixed(2) + "&nbsp;&nbsp;￥";
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
		// this.getYjzh();
		this.setCountInfo();
	},
	doNew : function() {
		this.clear();
		this.setCountInfo();
		// this.doCreate();
	},
	// beforeCellEdit : function(e) {
	//				
	// },
	doCreate : function(item, e) {
		var store = this.grid.getStore();
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
					var o = factory.load(dic)
					if (o) {
						var di = o.wraper[v]
						if (di) {
							data[it.id + "_text"] = di.text
						}
					}
				}
			}
		}
		var r = new Record(data)
		var insertIndex = this.grid.getStore().data.length;
		store.insert(insertIndex, [r]);
		this.grid.getView().refresh()// 刷新行号
		this.grid.startEditing(insertIndex, 1);
	},
	doFirstCreate : function(item, e) {
		var store = this.grid.getStore();
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
					var o = factory.load(dic)
					if (o) {
						var di = o.wraper[v]
						if (di) {
							data[it.id + "_text"] = di.text
						}
					}
				}
			}
		}
		var r = new Record(data)
		var insertIndex = this.grid.getStore().data.length;
		store.insert(insertIndex, [r]);
		this.grid.getView().refresh()// 刷新行号
// this.grid.startEditing(insertIndex, 1);
		this.fireEvent("focusZXYS");
	},
	StartEditing : function(){
		var insertIndex = this.grid.getStore().data.length;
		this.grid.startEditing(insertIndex-1, 1);
	},
	onReady : function() {

	},
	getEditorListData : function() {
		var store = this.grid.getStore();
		var n = store.getCount()
		var data = []
		for (var i = 0; i < n; i++) {
			var r = store.getAt(i)
			// alert(Ext.encode(records[i].data))
			if(r.data.FYMC&&(!r.data.YLSL || r.data.YLSL == 0)){
				MyMessageTip.msg("提示", "数量不能为空或0!", true);
				return ;
			}
			if (r.data.FYDW&&r.data.FYMC) {
				data.push(r.data);
			}
		}
		return data;
	},
	getRemoteDicReader : function() {
		return new Ext.data.JsonReader({
					root : 'disease',
					totalProperty : 'count',
					id : 'mdssearch_a'
				}, [{
							name : 'numKey'
						}, {
							name : 'FYMC'
						}, {
							name : 'FYDW'
						}, {
							name : 'PYDM'
						}, {
							name : 'FYXH'
						}, {
							name : 'FYGB'
						}, {
							name : 'FYKS'
						}, {
							name : 'FYDJ'
						}, {
							name : 'BMFL'
						}, {
							name : 'TCBZ'
						}]);
	},
	setBackInfo : function(obj, record) {
		record.data.BRXZ = this.opener.formModule.data.BRXZ.key
		// 将选中的记录设置到行数据中
		if (!obj.getValue())
			return
		// this.form.el.mask("正在查询数据...", "x-mask-loading")
		phis.script.rmi.jsonRequest({
					serviceId : "hospitalCostProcessingService",
					serviceAction : "queryCost",
					body : record.data
				}, function(code, msg, json) {
					// this.form.el.unmask()
					if (code > 300) {
						this.processReturnMsg(code, msg);
						return
					}
					var cell = this.grid.getSelectionModel().getSelectedCell();
					var row = cell[0];
					var col = cell[1];
					var griddata = this.grid.store.data;
					var rowItem = griddata.itemAt(row);
					obj.collapse();
					rowItem.set('FYMC', record.get("FYMC"));
					rowItem.set('FYDW', record.get("FYDW"));
					rowItem.set('YLXH', record.get("FYXH"));
					rowItem.set('FYGB', record.get("FYGB"));
					rowItem.set('FYKS', record.get("FYKS"));
					rowItem.set('YLDJ', record.get("FYDJ"));
					rowItem.set('BMFL', record.get("BMFL"));
					rowItem.set('ZFBL', json.body.ZFBL);
					// rowItem.modified['MSZD'] = record.get("MSZD");
					obj.setValue(record.get("FYMC"));
					obj.triggerBlur();
					this.grid.startEditing(row, 4);
				}, this);
	}
});