$package("phis.application.cic.script")

$import("phis.script.EditorList")

phis.application.cic.script.ClinicDisposalEntryList = function(cfg) {
	cfg.disablePagingTbr = true;
	cfg.showButtonOnTop = true;
	cfg.autoLoadData = false;
	cfg.selectOnFocus = true;
	cfg.showRowNumber = true;
	cfg.modal = true;
	cfg.closeAction = 'hide';
	cfg.width = 1024;
	cfg.remoteUrl = 'Clinic';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="60px" style="color:red">{YYZBM}</td><td width="200px">{FYMC}</td><td width="30px">{FYDW}</td><td width="40px">{FYDJ}</td>';
	cfg.minListWidth = 560;
	/* 注释拱墅区版本中有关医保的代码 Ext.apply(this, com.bsoft.phis.yb.YbUtil); */
	phis.application.cic.script.ClinicDisposalEntryList.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.cic.script.ClinicDisposalEntryList,
		phis.script.EditorList, {
			initPanel : function(sc) {
				if (this.grid)
					return this.grid;
				var grid = phis.application.cic.script.ClinicDisposalEntryList.superclass.initPanel
						.call(this, sc)
				grid.on("beforeedit", this.beforeCellEdit, this);
				var sm = grid.getSelectionModel();
				var _ctx = this;
				// 重写grid的onEditorKey事件
				grid.onEditorKey = function(field, e) {
					if (e.getKey() == e.ENTER && !e.shiftKey) {
						var sm = this.getSelectionModel();
						var cell = sm.getSelectedCell();
						var count = this.colModel.getColumnCount()
						var step = 5;
						if (_ctx.opener.openedBy != 'doctorStation') {
							step = 2;
						}
						if (cell[1] + step >= count) {// 实现倒数第二格单元格回车新增行操作
							this.fireEvent("doNewColumn");
							return;
						}
					}
					this.selModel.onEditorKey(field, e);
				}
				// 重写onEditorKey方法，实现Enter键导航功能
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
						// 判断单价是否为0
						if (c == 4) {
							if (field.getValue() != 0) {
								c++;
							}
						}
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
			onReady : function() {
				phis.application.cic.script.ClinicDisposalEntryList.superclass.onReady
						.call(this)
				if (this.opener.openedBy == 'doctorStation') {
					// 隐藏申请医生字段
					this.grid.getColumnModel().setHidden(
							this.grid.getColumnModel().getIndexById('YSDM'),
							true);
				}
				/*var JCBW = this.grid.getColumnModel().getColumnById("JCBWDM").editor;
				// JCBW.on("blur",this.loadDic,this);
				var _ctr = this;
				JCBW.onFocus = function() {
					this.lastQuery = null;
					if (_ctr.isSelect) {
						_ctr.isSelect = false;
						return;
					}
					var store = JCBW.store;
					store.removeAll();
					var rowItem = this.gridEditor.record.data;

					var jclb = rowItem.JCDL;

					var filters;
					if (!jclb || jclb == null || jclb == "") {
						filters = ['eq', ['i', 1], ['i', 2]]
					} else {
						filters = ['eq', ['$', 'item.properties.JCLB'],
								['s', jclb]];
					}

					store.proxy = new Ext.data.HttpProxy({
						method : "GET",
						url : util.dictionary.SimpleDicFactory.getUrl({
							id : "phis.dictionary.yj_jcsq_jcbw",
							filter : filters
						})
					})
					store.reload();

					util.widgets.MyCombox.superclass.onFocus.call(this);
					if (this.store.getCount() == 0) {
						this.store.load() // 异步的有问题
						this.focusLoad = true
					}
				}
				JCBW.select = function(index, scrollIntoView) {
					this.selectedIndex = index;
					_ctr.isSelect = true;
					this.view.select(index);
					if (scrollIntoView !== false) {
						var el = this.view.getNode(index);
						if (el) {
							this.innerList.scrollChildIntoView(el, false);
						}
					}
				}*/

			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='czxx_tjxx_"
							+ this.openedBy
							+ "' align='center' style='color:blue'>统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "合计金额：0.00&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "自负金额：0.00&nbsp;&nbsp;￥</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
					root : 'clinic',
					totalProperty : 'count',
					id : 'disposalSearch'
				}, [{
					name : 'numKey'
				}, {
					name : 'FYXH'
				}, {
					name : 'FYMC'
				}, {
					name : 'FYDW'
				}, {
					name : 'XMLX'
				}, {
					name : 'FYDJ'
				}, {
					name : 'FYGB'
				}, {
					name : 'FYKS'
				}, {
					name : 'FYKS_text'
				}, {
					name : 'JCDL'
				}, {
					name : 'YYZBM'
				}]);
			},
			setBackInfo : function(obj, record) {
				/** 2013-09-15 add by gaof 为拱墅区医保项目，增加(需要审批的药品和费用审批编号录入方法) * */
				record.data.BRID = this.brid;
				record.data.BRXZ = this.brxz;
				record.data.DJLX = 2;
				// add by yangl 增加自动插入诊疗费判断
				var ghgl = 0;
				if (this.opener.exContext.ids.ghgl) {
					ghgl = this.opener.exContext.ids.ghgl
				} else if (this.opener.exContext.ids.ghxh) {// 医生站的写成ghxh，防止对原有流程有影响
					ghgl = this.opener.exContext.ids.ghxh;
				}
				record.data.checkAutoClinic = true;
				record.data.GHGL = ghgl;
				r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "queryIsNeedVerify",
					body : record.data
				});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return;
				} else {
					if (r.json.isNeedVerify == 1) {
						MyMessageTip.msg("提示", r.json.ZWMC
								+ "需要审批，请病人去收费处划价时准备好审批编号", true);
					}
				}

				// 将obj中的记录设置到行数据中?
				/**
				 * 2013-09-09 add by gejj 为拱墅区医保项目，增加1.16.
				 * 医保二乙标志和因公因伤标志病人药品和费用的自负比例*
				 */
				/*
				 * 注释拱墅区版本中有关医保的代码 var json = {}; json.BRID = this.brid;
				 * json.BRXZ = this.brxz; json.al_ypxh = record.data.FYXH;//
				 * 费用序号 json.al_ypcd = 0;// 药品产地 （费用时是空或者0） json.ai_yplx = 0;//
				 * 药品类型（药品：yk_typk的type，费用：0） var rs =
				 * this.requestServer("queryMedicareZFBL", json); var rValue =
				 * rs.json.body; var zfpb = 0; var _ctr = this; if (-1 ==
				 * rValue) {// 返回-1时直接返回，不插入选择项 obj.collapse(); return; } else
				 * if (-1000 == rValue) {// 在-1000的情况下弹出框 obj.collapse();
				 * Ext.MessageBox.show({ title : '提示', msg : '此项目因工因伤使用,是否继续:',
				 * width : 300, buttons : Ext.MessageBox.YESNOCANCEL, fn :
				 * function(btn, text) { if ("yes" == btn) { zfpb = 3; } else if
				 * ("no" == btn) { zfpb = 1; } else { zfpb = -1; obj.collapse();
				 * return; } record.data.ZFPB = zfpb; _ctr.setData(obj, record); } },
				 * this); } else {// rValue 0 -1 zfpb = rValue; record.data.ZFPB =
				 * zfpb; if (r.json.needRemind) {
				 * obj.setValue(record.get("FYMC")); obj.collapse();
				 * Ext.MessageBox.show({ title : '提示', msg :
				 * '该病人已经收取诊疗费，是否继续录入？', width : 300, defBtnFocus : 'no',
				 * buttons : Ext.MessageBox.YESNO, fn : function(btn, text) { if
				 * ("yes" == btn) { record.data.FJLB = 3; _ctr.setData(obj,
				 * record); } else { record.set("FYXH", ""); record.set("FYMC",
				 * ""); record.set("FYMC_text", ""); obj.setValue(""); var cell =
				 * _ctr.grid .getSelectionModel() .getSelectedCell(); var row =
				 * cell[0]; var col = cell[1]; _ctr.grid.startEditing(row, 2); } } },
				 * this); } else { if (r.json.autoClinic) { record.data.FJLB =
				 * 3; } this.setData(obj, record); } }
				 */
				/** 2013-09-09 end add* */
				this.setData(obj, record);

			},
			setData : function(obj, record) {
				var zxks = this.grid.getColumnModel().getColumnById("ZXKS").editor;
				var cell = this.grid.getSelectionModel().getSelectedCell();
				var row = cell[0];
				var col = cell[1];
				var griddata = this.grid.store.data;
				_ctx = this;
				var rowItem = griddata.itemAt(row);
				var store = this.grid.getStore();
				var n = store.getCount()
				// 判断检验检查标志是否启用 2017-3-13
				if (!this.QYJCBZ) {
					var params = this.loadSystemParams({
						"privates" : ['QYJCBZ']
					})
					this.QYJCBZ = params.QYJCBZ;
				}
				if (this.QYJCBZ == "1") {
					// add by zengsm 判断同组中是否与上一条记录的项目类型相同，如果不相同则不让其保存
					if (n > 1) {
						if (store.getAt(n - 2).get("YJZH") == rowItem
								.get("YJZH")) {
							if (store.getAt(n - 2).get("XMLX") != record
									.get("XMLX")) {
								MyMessageTip.msg("提示", "同组辅检项目不一致，请输入相同的检查项目！",
										true);
								return
							} else if (record.get("XMLX") == 11) {// 11是pacs
								if (store.getAt(n - 2).get("JCDL") != record
										.get("JCDL")) {
									MyMessageTip.msg("提示",
											"同组检查大类不一致，请输入相同的检查项目！", true);
									return
								}
							}
						}
					}
				}

				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.get("YJZH") == rowItem.get("YJZH")) {
						if (i != row && r.get("YLXH") == record.get("FYXH")) {
							MyMessageTip.msg("提示", "【" + record.get("FYMC")
									+ "】在该组中已存在，禁止重复录入!", true);
							return;
						}						
						//根据病人性质及收费项目对照情况进行判定提醒
						if((!row || i != row) && this.brxz==2000 && r.get("YYZBM")!=undefined && record.get("YYZBM")!=undefined && r.get("YYZBM").replace(/\s+/g,"").length!=record.get("YYZBM").replace(/\s+/g,"").length){
								MyMessageTip.msg("提示", "医保病人的同一组处置中只能开自费项目或医保项目！", true);
								return;
						}
					}
				}
				obj.collapse();
				// 附加关联
				rowItem.set('YYZBM', record.get("YYZBM"));
				rowItem.set('FJLB', record.get("FJLB"));
				rowItem.set('FJGL', record.get("GHGL"));
				rowItem.set('FYGB', record.get("FYGB"));
				rowItem.set('YLXH', record.get("FYXH"));
				rowItem.set('FYDW', record.get("FYDW"));
				rowItem.set('XMLX', record.get("XMLX"));
				rowItem.set('YLDJ', record.get("FYDJ"));
				rowItem.set('HJJE', record.get("FYDJ") * rowItem.get("YLSL"));// 划价金额等于FYDJ*YLSL
				rowItem.set('ZFPB', record.get("ZFPB"));
				rowItem.set('JCDL', record.get("JCDL"));
				/** 2013-09-10 add by gejj 添加自负判别 * */
				if (rowItem.get("ZXKS") == null || rowItem.get("ZXKS") == "") {
					// 判断当前科室是否是有效的
					if (record.get("FYKS")) {
						var rec = zxks.findRecord("key", record.get("FYKS"))
						var store = this.grid.getStore();
						if (rec) {
							for (var i = 0; i < store.getCount(); i++) {
								var r = store.getAt(i)
								if (r.get("YJZH") == rowItem.get("YJZH")) {
									r.set("ZXKS", rec.get("key"));
									r.set("ZXKS_text", rec.get("text"));
								}
							}
						}
					} else {
						var rec = zxks.findRecord("key",
								this.mainApp['phis'].departmentId);
						if (rec
								&& rec.get("text") == this.mainApp.departmentName) {
							for (var i = 0; i < store.getCount(); i++) {
								var r = store.getAt(i)
								if (r.get("YJZH") == rowItem.get("YJZH")) {
									r.set('ZXKS',
											this.mainApp['phis'].departmentId);
									r.set('ZXKS_text',
											this.mainApp.departmentName);
								}
							}
						}
					}
				}
				// add by liyunt
				var onlyOne = 0;
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					if (r.get("YJZH") == rowItem.get("YJZH")) {
						onlyOne += 1;

						if (record.get("FYKS")) {
							if (i != row && r.get("ZXKS") != record.get("FYKS")) {
								rowItem.set('ZXKS', record.get("FYKS"));
								rowItem.set('ZXKS_text', record
										.get("FYKS_text"));
								rowItem.set("notSycn", true);
								MyMessageTip.msg("提示", "该项目与检查单主项不是同一个执行科室!",
										true);
								return;
							}
						}
					}
				}
				if (onlyOne == 1) { // 新组只有一条的情况下 也要
					if (record.get("FYKS")) {
						rowItem.set('ZXKS', record.get("FYKS"));
						rowItem.set('ZXKS_text', record.get("FYKS_text"));
					}
				}
				obj.setValue(record.get("FYMC"));
				obj.triggerBlur();
				this.setZFBL(this.brxz, rowItem)
				if (rowItem.get("YLDJ") > 0) {
					this.grid.startEditing(row, 5);
				} else {
					this.grid.startEditing(row, 4);
				}
			},
			doInsert : function(item, e, newGroup) {// 当前记录前插入一条记录?
				// this.doInsertAfter(item, e, false)
				var selectdRecord = this.getSelectedRecord();
				var selectRow = this.store.getCount();
				if (selectdRecord) {
					selectRow = this.store.indexOf(selectdRecord);
					this.removeEmptyRecord();
					if (selectdRecord.get("YLXH") == null
							|| selectdRecord.get("YLXH") == ""
							|| selectdRecord.get("YLXH") == 0) {
						selectRow = selectRow;
					} else {
						selectRow = this.store.indexOf(selectdRecord) + 1;
					}
				}
				var row = selectRow;
				var store = this.grid.getStore();
				var r = this.getSelectedRecord()
				if (r) {
					if (r.get("MZXH")) {
						MyMessageTip.msg("提示", "当前处置项目已收费，不能插入同组项目", true)
						return;
					}
				} else {
					if (store.getCount() > 0) {
						var rowItem1 = store.data.itemAt(0);
						if (rowItem1.get("MZXH")) {
							MyMessageTip.msg("提示", "当前处置项目已收费，不能插入同组项目", true)
							return;
						}
					}
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
				var storeData = store.data;
				var maxIndex = store.getCount();
				if (maxIndex == 1) {// 处方的第一条记录自动新组
					var rowItem = storeData.itemAt(maxIndex - 1);
					rowItem.set("YLSL", "1");
					rowItem.set("YJZH", 1);
				} else {
					var upRowItem = storeData.itemAt(row - 1);
					var rowItem = storeData.itemAt(row);
					rowItem.set("YJZH", upRowItem.get("YJZH"));
					rowItem.set("YLSL", upRowItem.get("YLSL"));
					rowItem.set("ZXKS", upRowItem.get("ZXKS"));
					rowItem.set("ZXKS_text", upRowItem.get("ZXKS_text"));
					rowItem.set("YJXH", upRowItem.get("YJXH"));
					rowItem.set("YSDM", upRowItem.get("YSDM"));
					rowItem.set("YSDM_text", upRowItem.get("YSDM_text"));
					rowItem.set("KSDM", upRowItem.get("KSDM"));
					rowItem.set("KSDM_text", upRowItem.get("KSDM_text"));
				}
				this.grid.startEditing(row, 2);
			},
			doInsertAfter : function(item, e, newGroup, stopEditor) {// 当前记录后插入一条记录?
				this.removeEmptyRecord();
				var store = this.grid.getStore();
				var storeData = store.data;
				var maxIndex = store.getCount();
				this.doCreate();
				var rowItem = storeData.itemAt(maxIndex);
				var upRowItem = storeData.itemAt(maxIndex - 1);
				var ypzh = 1;
				if (maxIndex == 0 || newGroup || upRowItem.get("MZXH")) {// 处方的第一条记录或自动新组
					if (maxIndex > 0) {
						ypzh = parseInt(upRowItem.get("YJZH")) + 1;
					}
					rowItem.set("YLSL", "1");
					rowItem.set("YJZH", ypzh);
				} else {
					rowItem.set("YJZH", upRowItem.get("YJZH"));
					rowItem.set("YLSL", 1);// upRowItem.get("YLSL")add by
					// zhouyl
					rowItem.set("ZXKS", upRowItem.get("ZXKS"));
					rowItem.set("YJXH", upRowItem.get("YJXH"));
					rowItem.set("YSDM", upRowItem.get("YSDM"));
					rowItem.set("YSDM_text", upRowItem.get("YSDM_text"));
					rowItem.set("ZXKS_text", upRowItem.get("ZXKS_text"));
					rowItem.set("KSDM", upRowItem.get("KSDM"));
					rowItem.set("KSDM_text", upRowItem.get("KSDM_text"));
				}
				if (!stopEditor) {
					this.grid.startEditing(maxIndex, 2);
				} else {
					this.grid.getSelectionModel().select(maxIndex, 0)
				}
			},
			doNewGroup : function(item, e) {
				this.doInsertAfter(item, e, true);
			},
			YYZBMRenderer : function(value, metaData, r, row, col) {
				if(value==undefined){
					return "";
				}
				return "<font style='color:red;font-weight:bold'>"+value+"</font>";
			},
			removeEmptyRecord : function() {
				var store = this.grid.getStore();
				for (var i = 0; i < store.getCount(); i++) {
					var r = store.getAt(i);
					if (r.get("YLXH") == null || r.get("YLXH") == ""
							|| r.get("YLXH") == 0 || r.get("notSycn")) {
						store.remove(r);
					}
				}
			},
			showColor : function(v, params, data) {
				var YJZH = data.get("YJZH") % 2 + 1;
				switch (YJZH) {
					case 1 :
						params.css = "x-grid-cellbg-1";
						break;
					case 2 :
						params.css = "x-grid-cellbg-2";
						break;
				}
				if (data.get("FPHM")) {
					return "<img src='"
							+ ClassLoader.appRootOffsetPath
							+ "resources/phis/resources/images/rmb.png' style='padding-right:10px' width='16' height='20' title='已收费'/>"
				}
				return "";
			},
			doSave : function(item, e) {
				var ghgl = null;
				if (this.opener.exContext.ids.ghgl) {
					ghgl = this.opener.exContext.ids.ghgl
				}
				this.removeEmptyRecord();
				var store = this.grid.getStore();
				var n = store.getCount()
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					var items = this.schema.items;
					var msg = "保存失败";
					if (r.get("SBXH") == 0)
						r.set("SBXH", null);
					if (r.get("YLXH") == null) {
					} else {
						for (var j = 0; j < items.length; j++) {
							var it = items[j]
							if (it.id == 'SBXH' || it.id == 'YJXH')
								continue;
							if (r.get("XMLX") == null || r.get("XMLX") == ""
									|| r.get("XMLX") == undefined) {
								r.set("XMLX", 0);
							}
							/**
							 * ***********modify by zhaojian 2017-05-17
							 * 解决处置录入模块收费项目（如公卫检验项目）单价为0时无法保存问题*****************
							 */
							/*
							 * if (r.get("YLDJ") == "" || r.get("YLDJ") <= 0) {
							 * MyMessageTip.msg("提示", "医疗单价必须大于0！错误行 " + (i + 1) + "
							 * 。", true); return false; }
							 */
							if (!r.get("YLSL")) {
								MyMessageTip.msg("提示", "医疗数量不能为空！错误行 "
										+ (i + 1) + " 。", true);
								return false;
							}
							// if (!r.get("ZXKS")) {
							// MyMessageTip.msg("提示", "执行科室不能为空！错误行 "
							// + (i + 1) + " 。", true);
							// return;
							// }
							if (this.opener.openedBy == 'doctorStation') {

							} else {
								if (!r.get("YSDM")) {
									MyMessageTip.msg("提示", "申请医生不能为空！错误行 "
											+ (i + 1) + " 。", true);
									return false;
								}
							}
							// if (r.get(it.id) == null || r.get(it.id) == "") {
							// if (it['not-null']) {
							// MyMessageTip.msg("提示", "数据保存失败，本次修改无效！错误行 "
							// + (i + 1) + " 。", true);
							// return;
							// }
							// }
						}
						if (r.get("SBXH") == null) {
							r.data['_opStatus'] = 'create';
						} else {
							r.data['_opStatus'] = 'update';
						}
						data.push(r.data)
					}
				}
				var r = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : "checkProjectMaterials",
					bodys : data,
					ghgl : ghgl,
					mzzy : 0
						// 0是门诊 ，1 是住院

						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg);
					return false;
				}
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				var res = phis.script.rmi.miniJsonRequestSync({
					serviceId : this.serviceId,
					serviceAction : this.serviceActionSave,
					clinicId : this.clinicId,
					brid : this.brid,
					brxm : this.brxm,
					djly : this.djly,
					// 规定病种
					// GDBZ : this.opener.exContext.pdgdbz,
					ghgl : (ghgl ? ghgl : this.opener.exContext.ids.ghxh),
					body : data
				});
				var code = res.code;
				var msg = res.msg;
				var json = res.json;
				this.grid.el.unmask()
				if (code >= 300) {
					this.processReturnMsg(code, msg);
					return false;
				}
				if (this.clinicId == 0) {
					for (var i = 0; i < json.body.length; i++) {
						this.opener.yjxhs.push(json.body[i]);
					}
				}
				if(json.njjbylmsg && json.njjbylmsg.length > 0 ){
					alert("友情提示："+json.njjbylmsg+"如有必要请删除！");
				}
				this.refresh();
				this.fireEvent("doSave", json.body);
				MyMessageTip.msg("提示", "保存成功!", true);
				if (this.needToClose) {
					this.doClose();
					this.needToClose = false;
				}
				return true;
				// }, this)
			},
			doRemove : function() {
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				var count = this.store.getCount();
				var rcount = 0;
				for (var i = 0; i < count; i++) {
					var row = this.store.getAt(i);
					if (row.get("YJZH") == r.get("YJZH")) {
						rcount++;
					}
				}
				if (r == null) {
					return
				}
				if (r.get("DJLY") + "" == "8") {
					MyMessageTip.msg("提示", "当前单据为检验开单不可删除!", true);
					return;
				}
				var date = {};
				if (r.get("SBXH") == null || r.get("SBXH") == 0) {
					this.store.remove(r);
					this.setCountInfo();
					count = this.store.getCount();
					// 移除之后焦点定位
					if (count > 0) {
						cm.select(cell[0] < count ? cell[0] : (count - 1),
								cell[1]);
					} else {
						this.doInsert();
						return;
					}
					var store = this.grid.getStore();
					var l = 0;
					if (cell[0] < count) {
						for (var i = cell[0]; i < count; i++) {
							var r = store.getAt(i)
							if (i == cell[0]) {
								if (i == 0) {
									l = r.get('YJZH');
									r.set('YJZH', 1);
									yjzh = 1;
								} else {
									var r1 = store.getAt(i - 1)
									l = r.get('YJZH') - r1.get('YJZH');
									if (l == 0)
										return;
									r.set('YJZH', (r.get('YJZH') - l + 1));
									yjzh = r.get('YJZH')
								}
							} else {
								r.set('YJZH', (r.get('YJZH') - l + 1));
								yjzh = r.get('YJZH');
							}
						}
					} else {
						var r = store.getAt(count - 1)
						yjzh = r.get('YJZH');
					}
					return;
				}
				if (count == 1 || rcount == 1) {
					Ext.Msg.confirm("请确认", "确认删除项目名称为【" + r.get("FYMC")
							+ "】的处置明细吗？", function(btn) {// 先提示是否删除
								if (btn == 'yes') {
									date["SIGN"] = 1;
									this.grid.el.mask("正在删除数据...",
											"x-mask-loading")
									phis.script.rmi.jsonRequest({
										serviceId : this.serviceId,
										serviceAction : this.serviceActionRemove,
										schemaList : "MS_YJ01",
										schemaDetailsList : "MS_YJ02",
										pkey : r.get("SBXH"),
										body : date
									}, function(code, msg, json) {
										this.grid.el.unmask()
										if (code >= 300) {
											this.processReturnMsg(code, msg);
											if (code != 604) {
												return;
											}
										}
										this.store.remove(r);
										this.setCountInfo();
										// 移除之后焦点定位
										count = this.store.getCount();
										if (count > 0) {
											cm.select(cell[0] < count
													? cell[0]
													: (count - 1), cell[1]);
										}
										this
												.fireEvent("afterRemove",
														json.YJXH);
										this.fireEvent("doSave");
									}, this)

								}
							}, this);
					return;
				} else {
					if (r.get("YJZX") == 1) {// 是医技主项
						Ext.MessageBox.show({
							title : '请确认',
							msg : '该项【' + r.get("FYMC") + '】为医技主项,是否删除同组的处置信息',
							buttons : Ext.MessageBox.YESNOCANCEL,
							fn : this.showResult,
							animEl : 'mb4',
							icon : Ext.MessageBox.QUESTION,
							scope : this
						});

					} else {
						Ext.Msg.confirm("请确认", "确认删除项目名称为【" + r.get("FYMC")
								+ "】的处置明细吗？", function(btn) {// 先提示是否删除
									if (btn == 'yes') {
										date["SIGN"] = 3;
										this.grid.el.mask("正在删除数据...",
												"x-mask-loading")
										phis.script.rmi.jsonRequest({
											serviceId : this.serviceId,
											serviceAction : this.serviceActionRemove,
											schemaList : "MS_YJ01",
											schemaDetailsList : "MS_YJ02",
											pkey : r.get("SBXH"),
											body : date
										}, function(code, msg, json) {
											this.grid.el.unmask()
											if (code >= 300) {
												this
														.processReturnMsg(code,
																msg);
												if (code != 604) {
													return;
												}
											}
											this.store.remove(r);
											this.setCountInfo();
											// 移除之后焦点定位
											count = this.store.getCount();
											if (count > 0) {
												cm.select(cell[0] < count
														? cell[0]
														: (count - 1), cell[1]);
											}
											this.fireEvent("afterRemove",
													this.grid);
										}, this)
									}
								}, this);
						return
					}
				}
			},
			showResult : function(btn) {
				var date = {};
				var cm = this.grid.getSelectionModel();
				var cell = cm.getSelectedCell();
				var r = this.getSelectedRecord()
				var count = this.store.getCount();
				if (btn == 'yes') {
					date["SIGN"] = 1;
					this.grid.el.mask("正在删除数据...", "x-mask-loading")
					phis.script.rmi.jsonRequest({
						serviceId : this.serviceId,
						serviceAction : this.serviceActionRemove,
						schemaList : "MS_YJ01",
						schemaDetailsList : "MS_YJ02",
						pkey : r.get("SBXH"),
						body : date
					}, function(code, msg, json) {
						this.grid.el.unmask()
						if (code >= 300) {
							this.processReturnMsg(code, msg);
							if (code != 604) {
								return;
							}
						}
						this.store.remove(r);
						// 移除之后焦点定位
						var count = this.store.getCount();
						if (count > 0) {
							cm.select(cell[0] < count ? cell[0] : (count - 1),
									cell[1]);
						}
						this.refresh();
						this.fireEvent("afterRemove", json.YJXH);
					}, this);
				} else if (btn == 'no') {
					date["SIGN"] = 2;
					this.grid.el.mask("正在删除数据...", "x-mask-loading")
					phis.script.rmi.jsonRequest({
						serviceId : this.serviceId,
						serviceAction : this.serviceActionRemove,
						schemaList : "MS_YJ01",
						schemaDetailsList : "MS_YJ02",
						pkey : r.get("SBXH"),
						body : date
					}, function(code, msg, json) {
						this.grid.el.unmask()
						if (code >= 300) {
							this.processReturnMsg(code, msg);
							if (code != 604) {
								return;
							}
						}
						this.store.remove(r);
						// 移除之后焦点定位
						var count = this.store.getCount();
						if (count > 0) {
							cm.select(cell[0] < count ? cell[0] : (count - 1),
									cell[1]);
						}
						this.refresh();
						this.fireEvent("afterRemove", this.grid);
					}, this)
					return;
				}
			},
			afterGridEdit : function(it, record, field, v) {
				// 选好医生，自动定位科室并回填
				if (it.id == 'YSDM' && record.get("YSDM")) {
					var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : this.serviceId,
						serviceAction : "getKsxx",
						ygdm : record.get("YSDM")
					});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg,
								this.saveToServer);
						return;
					}
					var ksdm = ret.json.ksdm;
					var ksmc = ret.json.ksmc;
					// record.set("ZXKS", ksdm);
					// record.set("ZXKS_text", ksmc);
					record.set("KSDM", ksdm);
					record.set("KSDM_text", ksmc);
				}

				if (it.id == "YLSL" || it.id == "YLDJ") {
					var v = parseFloat(record.get("YLDJ") * record.get("YLSL"))
							.toFixed(2);
					record.set("HJJE", v);
					this.setCountInfo();
					// add-by-liuxy:it.id=='KSDM' :20131011
				} else if (it.id == "ZXKS" || it.id == 'YSDM'
						|| it.id == 'KSDM') {
					var store = this.grid.getStore();
					var n = store.getCount()
					for (var i = 0; i < n; i++) {
						var r = store.getAt(i)
						if (r.get("YJZH") == record.get("YJZH")) {
							r.set(it.id, record.get(it.id));
							r.set(it.id + '_text', record.get(it.id + "_text"));
							if (it.id == 'YSDM') {// 医生改变，同组的执行科室全部随之改变
								// r.set('ZXKS', record.get('ZXKS'));
								// r.set('ZXKS_text', record.get("ZXKS_text"));
								r.set('KSDM', record.get('KSDM'));
								r.set('KSDM_text', record.get("KSDM_text"));
							}
						}
					}
				}
				// var yjzhSign = record.get("YJZH");
				// for (var i = 0; i < this.store.getCount(); i++) {
				// var row = this.store.getAt(i)
				// if (yjzhSign == row.get('YJZH')) {
				// row.set("ZXKS", record.get("ZXKS"));
				// row.set("ZXKS_text", record.get("ZXKS_text"));
				// }
				// }
			},
			setCountInfo : function() {
				var totalMoney = 0;
				var selfMoney = 0;
				if (this.store) {
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
				}

				document.getElementById("czxx_tjxx_" + this.openedBy).innerHTML = "统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "合计金额："
						+ parseFloat(totalMoney).toFixed(2)
						+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "自负金额："
						+ parseFloat(selfMoney).toFixed(2) + "&nbsp;&nbsp;￥";
			},
			setCountInfo1 : function() {
				var totalMoney1 = 0;
				var selfMoney1 = 0;
				if (this.store) {
					for (var i = 0; i < this.store.getCount(); i++) {
						var r = this.store.getAt(i);
						var hjje = parseFloat(r.get("HJJE") || 0);
						var zfbl = parseFloat(r.get("ZFBL") || 1);
						totalMoney1 += parseFloat(hjje);
						selfMoney1 += parseFloat(hjje * zfbl);
						if (isNaN(totalMoney1)) {
							totalMoney1 = 0;
						}
						if (isNaN(selfMoney1)) {
							selfMoney1 = 0;
						}
					}
				}
				document.getElementById("czxx_tjxx_" + this.openedBy).innerHTML = "统计信息：&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "合计金额："
						+ parseFloat(totalMoney1).toFixed(2)
						+ "&nbsp;&nbsp;￥&nbsp;&nbsp;&nbsp;&nbsp;"
						+ "自负金额："
						+ parseFloat(selfMoney1).toFixed(2) + "&nbsp;&nbsp;￥";
			},
			setZFBL : function(brxz, r) {
				var body = {};
				body["BRXZ"] = brxz;
				body["FYXH"] = r.get("YLXH");
				body["FYGB"] = r.get("FYGB");
				body["TYPE"] = 0;
				phis.script.rmi.jsonRequest({
					serviceId : this.serviceId,
					serviceAction : "getZFBL",
					body : body
				}, function(code, msg, json) {
					if (json.ZFBL.ZFBL != null || json.ZFBL.ZFBL != "") {
						r.set("ZFBL", json.ZFBL.ZFBL);
					} else {
						r.set("ZFBL", 1);
					}
					this.setCountInfo();
				}, this);
			},
			beforeCellEdit : function(e) {
				var f = e.field
				var record = e.record
				var op = record.get("_opStatus")
				var cm = this.grid.getColumnModel()
				var c = cm.config[e.column]
				var enditor = cm.getCellEditor(e.column)
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
				if (record.get("DJLY") + "" == "8") {
					return false;
				}
				// if (it.id == "ZXKS") {
				// return true;
				// }

				if (it.dic) {
					if (e.value) {
						e.value = {
							key : e.value,
							text : record.get(f + "_text")
						}
					}
				} else {
					e.value = e.value || ""
				}
				if (it.id == "YLDJ") {
					var r = this.getSelectedRecord();
					if (r.get("YLDJ") != 0.00)
						return false;
				}
				if (record.get("FPHM")) {
					return false;
				} else {
					return true;
				}
			},
			getYjzh : function() {
				// yjzh = 1;
				yjzh = 0;
				var store = this.grid.getStore();
				var n = store.getCount()
				for (var i = 0; i < n; i++) {
					if (i == 0) {
						yjzh = 1;
						var r = store.getAt(i)
						r.set('YJZH', yjzh);
					} else {
						var r1 = store.getAt(i - 1)
						var r = store.getAt(i)
						if (r1.get('YJXH') == r.get('YJXH')) {
							r.set('YJZH', yjzh);
						} else {
							r.set('YJZH', ++yjzh);
						}
					}
				}
			},
			onStoreLoadData : function(store, records, ops) {
				this.fireEvent("loadData", store) // ** 不管是否有记录，都fire出该事件
				// this.store.rejectChanges();
				if (records.length == 0) {
					this.getYjzh();
					this.store.commitChanges();
					return
				}
				if (!this.selectedIndex || this.selectedIndex >= records.length) {
					this.selectRow(0)
				} else {
					this.selectRow(this.selectedIndex);
					this.selectedIndex = 0;
				}
				this.getYjzh();
				this.setCountInfo1();
				this.store.commitChanges();
				this.opener.loading = false;
			},
			loadData : function() {
				// 添加了phis.
				this.requestData.serviceId = "phis.configLogisticsInventoryControlService";
				this.requestData.serviceAction = "queryYjmx";
				this.clear(); // ** add by yzh , 2010-06-09 **
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
				// this.resetButtons();//取消重置按钮
			},
			doClose : function() {
				if (this.opener.win) {
					this.opener.win.hide();
				} else {
					this.opener.opener.cardWin.hide();
				}
			},
			doPrint : function() {// 处置打印明细 直接传yjxh,因为收费处的jzxh为空
				var module = this.createModule("czprint",
						this.refClinicDisposalPrint)
				module.yjxh = [];
				var store = this.grid.getStore();
				var n = store.getCount();
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					module.yjxh.push(r.get('YJXH'));
				}
				if (this.jzxh) {
					module.jzxh = this.jzxh;
				} else {
					module.jzxh = 0;
				}
				//zhaojian 2017-09-15 处置笺打印增加条形码，这里将门诊号码传给打印窗口
				module.mzhm = this.opener.exContext.empiData.MZHM;
				module.brid = this.brid;// 病人信息部分的打印就传brid过去
				module.initPanel();
				module.doPrint();
			},
			// 查看报告结果
			doBgck : function() {
				if (!this.QYJCBZ) {
					var params = this.loadSystemParams({
						"privates" : ['QYJCBZ']
					})
					this.QYJCBZ = params.QYJCBZ;
				}
				if (this.QYJCBZ != "1") {
					MyMessageTip.msg("提示", "未启用pacs!", true);
					return;
				}
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "未选中有效记录!", true);
					return;
				}
				if (r.get("XMLX") != "11") {
					MyMessageTip.msg("提示", "非检查项目,不能查看结果!", true);
					return;
				}
				if (!r.get("BGSJ") || r.get("BGSJ") == null
						|| r.get("BGSJ") == "") {
					MyMessageTip.msg("提示", "报告未出结果!", true);
					return;
				}
				if (!r.get("SQID") || r.get("SQID") == null
						|| r.get("SQID") == "") {
					MyMessageTip.msg("提示", "没有生成申请单号!", true);
					return;
				}
				this.bgform = this.createModule("bgform", this.bgRef);
				var win = this.bgform.getWin();
				win.add(this.bgform.initPanel());
				win.show()
				win.center()
				if (!win.hidden) {
					this.bgform.loadData(r.get("SQID"));
				}
			}
		});