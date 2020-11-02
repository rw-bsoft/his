/**
 * 药品私用信息-价格
 * 
 * @author : caijy
 */
$package("phis.application.mds.script")
$import("phis.script.EditorList")

phis.application.mds.script.MedicinesManagePriceList = function(cfg) {
	cfg.disablePagingTbr = this.disablePagingTbr = true;
	this.tbar = []
	cfg.autoLoadData = false;
	cfg.remoteUrl="Origin";
	cfg.selectOnFocus = true;
	cfg.remoteTpl='<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{CDMC}</td>';
	phis.application.mds.script.MedicinesManagePriceList.superclass.constructor.apply(this, [cfg])
	this.on("afterCellEdit", this.onAfterCellEdit, this);
	this.on("beforeCellEdit", this.onBeforeCellEdit, this);
}

Ext.extend(phis.application.mds.script.MedicinesManagePriceList,
		phis.script.EditorList, {
			//回车换列
				initPanel : function(sc) {
				var grid = phis.application.mds.script.MedicinesManagePriceList.superclass.initPanel
						.call(this, sc)
				var sm = grid.getSelectionModel();
				var _ctr=this;
				grid.onEditorKey = function(field, e) {
					var sm = this.getSelectionModel();
					var k = e.getKey(), newCell, g = sm.grid, ed = g.activeEditor;
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						if (cell[1] + 3 > count) {
							_ctr.doCreate();
							return;
						}
					}
					this.selModel.onEditorKey(field, e);
				}
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
			getRemoteDicReader : function() {
			return new Ext.data.JsonReader({
							root : 'origins',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'mdssearch'
						}, [{
									name : 'numKey'
								}, {
									name : 'YPCD'
								}, {
									name : 'CDMC'
								}, {
									name : 'PYDM'
								}]);
			},
			// 数据回填
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				Ext.EventObject.stopEvent();// 停止事件
				obj.collapse();
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				var ypcd = record.get("YPCD");
				for (var i = 0; i < griddata.length; i++) {
					if (griddata.itemAt(i).get("YPCD") == ypcd&&i!=row) {
						MyMessageTip.msg("提示", "产地" + record.get("CDMC") + "已经存在", true);
						return;
					}
				}
				var rowItem = griddata.itemAt(row);
				rowItem.set('YPCD', record.get("YPCD"));
				rowItem.set('PYDM', record.get("PYDM"));
				rowItem.set('CDMC', record.get("CDMC"));
				obj.setValue(record.get("PYDM"));
				obj.triggerBlur();
				this.grid.startEditing(row, 3);
			},
			/*
			 * //重写beforeCellEdit实现不是新增的记录不能修改功能 //
			 * 本来用于控制有库存不能修改价格,现在暂时不做控制,以后需要时增加 beforeCellEdit : function(e) {
			 * var f = e.field var record = e.record var op =
			 * record.get("_opStatus") // 不是新增的记录不能编辑 if (op != "create") {
			 * //查询库存,如有则不能修改 //.... return false; } var cm =
			 * this.grid.getColumnModel() var c = cm.config[e.column] var
			 * enditor = cm.getCellEditor(e.column) var it = c.schemaItem var ac =
			 * util.Accredit; if (op == "create") { if
			 * (!ac.canCreate(it.acValue)) { return false } } else { if
			 * (!ac.canUpdate(it.acValue)) { return false } }
			 * 
			 * if (it.dic) { e.value = { key : e.value, text : record.get(f +
			 * "_text") } } else { e.value = e.value || "" } if
			 * (this.fireEvent("beforeCellEdit", it, record, enditor.field,
			 * e.value)) { return true } },
			 */
			getCndBar : function(item) {
				var createButton = new Ext.Button({
							text : '新增',
							iconCls : "create",
							cmd:"create"
						});

				createButton.on("click", this.doCreate, this);
				var cancellationButton = new Ext.Button({
							text : '注销',
							iconCls : "remove",
							cmd:"remove"
						});
				cancellationButton.on("click", this.doCancellation, this);
				this.cancellationButton = cancellationButton;
				var isCancellation = new Ext.form.Checkbox({
							boxLabel : "显示已注销产地"
						})
				this.isCancellation = isCancellation;
				this.isCancellation.on("check", this.doIsCancellation, this)
				return [createButton, cancellationButton, '->', isCancellation];
			},
			// 注销恢复事件
			doCancellation : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				if (r.data.YPCD == null) {
					return;
				}
				if(r.data._opStatus && r.data._opStatus == "create"){
				this.store.remove(r);
				return;
				}

				var tsmsg = "真要注销产地'" + r.data.CDMC + "'吗?";// 数据修改前确认信息
				var zfpb = 1;// 数据修改成功后的作废标志
				var loadMsg = "正在注销数据...";
				if (r.data.ZFPB == 1) {
					tsmsg = "真要恢复产地'" + r.data.CDMC + "'吗?";
					zfpb = 0;
					loadMsg = "正在恢复数据...";
				}
				var record = {};
				record["JGID"] = r.data.JGID;
				record["YPXH"] = r.data.YPXH;
				record["YPCD"] = r.data.YPCD;
				var _ctr = this;
				Ext.Msg.show({
					title : '提示',
					msg : tsmsg,
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							this.mask(loadMsg)
							phis.script.rmi.jsonRequest({
										serviceId : this.serviceId,
										serviceAction : this.removeServiceActionId,
										body : record,
										op : zfpb
									}, function(code, msg, json) {
										this.unmask()
										if (code < 300) {
											this.fireEvent("cancellation",
													this.entryName,
													'cancellation', json,
													r.data)
										} else {
											this.processReturnMsg(code, msg,
													this.doCancellation)
										}
										_ctr.refresh();
									}, this);
						}
					},
					scope : this
				});

			},
			// 是否显示作废记录复选框点击事件
			doIsCancellation : function() {
				// this.isZF用于判断是否作废复选框的当前选中状态
				this.isZF = this.isCancellation.getValue();
				if(this.op=="create"){
				return;
				}
				this.loadData();
			},
			loadData : function() {
				this.clear(); // ** add by yzh , 2010-06-09 **
				var cnd = this.initCnd;
				var icnd =['eq', ['$', 'YPXH'], ['i', this.initDataId]];
				if (cnd != null) {
					cnd = ['and', cnd, icnd];
				} else {
					cnd = icnd;
				}
				if (!this.isZF) {
					cnd = ['and', cnd, ['ne', ['$', 'ZFPB'], ['s', '1']]];
				}
				this.requestData.cnd = cnd;
				if (this.store) {
					if (this.disablePagingTbr) {
						this.store.load()
					} else {
						var pt = this.grid.getBottomToolbar()
						if (this.requestData.pageNo == 1) {
							pt.cursor = 0;
						}
						pt.doLoad(pt.cursor)
					}
				}
				// ** add by yzh **
				this.resetButtons();
			},
			onRenderer : function(value, metaData, r) {
				if (r.data.ZFPB == 1) {
					return "<img src='" + ClassLoader.appRootOffsetPath
									+ "resources/phis/resources/images/(00,04).png'/><font color='#ADADAD'>"+ value + "</font>"
				}
				return value;
			},
			// 单击记录时改变按钮字
			onRowClick : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				if (r.data._opStatus && r.data._opStatus == "create") {
					this.cancellationButton.setText("注销");
				} else {
					if (r.data.ZFPB == 1) {
						this.cancellationButton.setText("恢复");
					} else {
						this.cancellationButton.setText("注销");
					}
				}
			},
			onReady : function() {
				phis.application.mds.script.MedicinesManagePriceList.superclass.onReady
						.call(this);
				this.grid.on("mouseover", this.onMouseover, this);
			},
			// 鼠标移入到数据时 如果作废记录 提示
			onMouseover : function(e) {
				var index = this.grid.getView().findRowIndex(e.getTarget());
				if (index >= 0) {
					var record = this.store.getAt(index);
					if (record) {
						if (record.data.ZFPB == 1) {
							var rowEl = Ext.get(e.getTarget());
							rowEl.set({
										qtip : '产地已注销'
									}, false);

						}
					}
				}
			},
			// 编辑完后,用于输入进货价格自动计算零售价格
			onAfterCellEdit : function(it, record, field, v) {
				if (it.id == "JHJG") {
					var pljc = this.getPljc();
					var lsjg = (v * pljc).toFixed(6);
					if(lsjg>999999.9999){
					lsjg=999999.9999;
					}
					record.set("LSJG", lsjg);
				}
				if(it.id=="DJFS"){
				if(v==1||v==2){
				var jsq=this.createModule("jsq", this.refjsq);
				jsq.on("commit", this.onCommit, this);
				this.record=record;
				var win = jsq.getWin();
				win.add(jsq.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					jsq.doNew();
				}
				}else{
				record.set("DJGS", '');
				}
				}
			},
			onCommit:function(d){
			if(!this.record){
			return;
			}
			this.record.set("DJGS",d)
			},
			// 获取批零加成率
			getPljc : function() {
				if (this.pljc) {
					return this.pljc;
				} else {
					var ret = phis.script.rmi.miniJsonRequestSync({
									serviceId : this.serviceId,
									serviceAction : this.queryPljcActionId
								});
					if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.getPljc);
						return ;
				}
					this.pljc=1+ret.json.pljc
					return this.pljc
				}
			},
			// 重写增行方法,增行后默认选中第一个输入框
			doCreate : function(item, e) {
				phis.application.mds.script.MedicinesManagePriceList.superclass.doCreate
						.call(this);
				this.grid.startEditing(this.grid.getStore().getCount() - 1, 1);
			},
			onBeforeCellEdit:function(it,record,field,value){
				if(this.isRead){
				return false;}
				var op = record.get("_opStatus");
				var name=it.id;
				if(name=="PYDM"&&op!="create"){
				return false;
				}
			},
			doRead:function(){//update by caijy at 2015.1.19 for 医生站合理用药信息调阅,界面只读
			var btns = this.grid.getTopToolbar();
			 btns.find("cmd", "create")[0].disable()
			  btns.find("cmd", "remove")[0].disable()
			this.loadData();
			}
		});