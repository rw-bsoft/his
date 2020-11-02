$package("chis.application.hr.script")

$import("chis.script.BizSimpleListView")

chis.application.hr.script.AreaGridList = function(cfg) {
	var r = this.getSystem();
	this.rr = r;
	this.areaGridListId = "";// 用来区别同个页面多个网格地址调用
	this.initCnd = cfg.cnds || ["eq", ["$", "a.parentCode"], ["s", r.city.key]];
	this.sign = 0;
	cfg.autoLoadData = true;
	cfg.showRowNumber = true;
	this.pageSize = 10;
	cfg.showBtnOnLevel = true;
	this.sign = 0;// 
	chis.application.hr.script.AreaGridList.superclass.constructor.apply(this,
			[cfg]);
	this.on("winShow", this.onWinShow, this)
}

Ext.extend(chis.application.hr.script.AreaGridList,
		chis.script.BizSimpleListView, {
			initPanel : function(sc) {

				if (this.grid) {
					if (!this.isCombined) {
						this.fireEvent("beforeAddToWin", this.grid)
						this.addPanelToWin();
					}
					return this.grid;
				}
				var schema = sc
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg, this.initPanel)
						return;
					}
				}
				this.schema = schema;
				this.isCompositeKey = schema.compositeKey;
				var items = schema.items
				if (!items) {
					return;
				}
				this.store = this.getStore(items)

				// if (this.mutiSelect) {
				// this.sm = new Ext.grid.CheckboxSelectionModel()
				// }
				this.cm = new Ext.grid.ColumnModel(this.getCM(items))
				var cfg = {
					border : false,
					store : this.store,
					cm : this.cm,
					height : this.height,
					loadMask : {
						msg : '正在加载数据...',
						msgCls : 'x-mask-loading'
					},
					buttonAlign : 'center',
					clicksToEdit : 1,
					frame : true,
					plugins : this.rowExpander,
					viewConfig : {
						// forceFit : true,
						enableRowBody : this.enableRowBody,
						getRowClass : this.getRowClass
						// style : {
						// overflow : 'auto',
						// overflowX : 'hidden'
						// }

					}
				}
				if (this.sm) {
					cfg.sm = this.sm
				}
				if (this.viewConfig) {
					Ext.apply(cfg.viewConfig, this.viewConfig)
				}
				if (this.group) {
					cfg.view = new Ext.grid.GroupingView({
								// forceFit : true,
								showGroupName : true,
								enableNoGroups : false,
								hideGroupedColumn : true,
								enableGroupingMenu : false,
								columnsText : "表格字段",
								groupByText : "使用当前字段进行分组",
								showGroupsText : "表格分组",
								groupTextTpl : this.groupTextTpl

							});
				}
				if (this.gridDDGroup) {
					cfg.ddGroup = this.gridDDGroup;
					cfg.enableDragDrop = true
				}
				if (this.summaryable) {
					$import("phis.script.ux.GridSummary");
					var summary = new org.ext.ux.grid.GridSummary();
					cfg.plugins = [summary]
					this.summary = summary;
				}

				var cndbars = this.getCndBar(items)

				if (!this.disablePagingTbr) {
					cfg.bbar = this.getPagingToolbar(this.store)
				} else {
					cfg.bbar = this.bbar
				}
				if (!this.showButtonOnPT) {
					if (this.showButtonOnTop) {
						cfg.tbar = (cndbars.concat(this.tbar || []))
								.concat(this.createButtons())
					} else {
						cfg.tbar = cndbars.concat(this.tbar || [])
						cfg.buttons = this.createButtons()
					}
				}
				// this.expansion(cfg);// add by yangl
				this.grid = new this.gridCreator(cfg)
				// this.grid.getTopToolbar().enableOverflow = true
				this.grid.on("afterrender", this.onReady, this)
				this.grid.on("contextmenu", function(e) {
							e.stopEvent()
						})
				this.grid.on("rowcontextmenu", this.onContextMenu, this)
				this.grid.on("rowdblclick", this.onDblClick, this)
				this.grid.on("rowclick", this.onRowClick, this)
				this.grid.on("keydown", function(e) {

							if (e.getKey() == e.PAGEDOWN) {
								e.stopEvent()
								this.pagingToolbar.nextPage()
								return
							}
							if (e.getKey() == e.PAGEUP) {
								e.stopEvent()
								this.pagingToolbar.prevPage()
								return
							}
						}, this)

				if (!this.isCombined) {
					this.fireEvent("beforeAddToWin", this.grid)
					this.addPanelToWin();
				}

				return this.grid
			},
			// 搜索条件
			onWinShow : function() {

				if (this.cndFldCombox) {
					this.cndFldCombox.setValue("pyCode");
					//

				}
				this.changeRecord1();// 每次打开，初始化数据
				this.cndField.setValue("");// 每次打开，清空输入框的内容
			},
			onReady : function() {

				chis.application.hr.script.AreaGridList.superclass.onReady
						.call(this)

				var otherTbar = new Ext.Toolbar(this.createSorterButton());
				this.otherTbar = otherTbar;
				this.grid.getView().showBtnOnLevel = true;
				this.grid.add(otherTbar)
				this.grid.doLayout();
				this.grid.getView().refresh();

			},
			// 创建显示当前选择的层级网格地址的按钮
			createSorterButton : function() {

				var button = []
				var config1 = {
					id : this.rr.city.key,
					text : "<u><font color=blue>" + this.rr.city.text
							+ "</font></u>",
					sortData : {
						regionCode : 0,
						sign : this.rr.city.key
					}
				}
				for (var i = 0; i < 14; i++) {
					var config = {
						id : "button" + i,
						text : '',
						sortData : {
							regionCode : 0,
							sign : i
						}
					}
					var _ctr = this
					Ext.applyIf(config, {
								listeners : {
									click : function(button, e) {

										_ctr.changeRecord(button);
									}
								},
								// iconCls : 'sort', 这个是可以放置图标的地方
								reorderable : true
							});
					Ext.applyIf(config1, {
								listeners : {
									click : function(button, e) {
										_ctr.changeRecord1(button);
									}
								},
								// iconCls : 'sort', 上面文字前面的一小块空地
								reorderable : true
							});
					button.push(config1)
					button.push(config)

				}

				return button;
			},
			// 按钮点击后
			changeRecord : function(button) {
				// this.sign=0;//2014.9.26改动

				if (button.sortData.regionCode == 0) {
					return;
				}
				var bt1 = button.getId()
				var tt = parseInt(bt1.substr(6, bt1.length - 1)) + 1;
				this.sign = tt;// 2014.9.26改动
				for (var i = tt; i < 14; i++) {// 当前按钮之后的按钮数据清空,用于点击点击按钮再选择数据
					var bt = this.otherTbar.findById("button" + i)

					bt.setText("");
					bt.sortData.regionCode = 0
				}

				this.initCnd = ["eq", ["$", "a.parentCode"],
						["s", button.sortData.regionCode]]
				this.requestData.cnd = ["eq", ["$", "a.parentCode"],
						["s", button.sortData.regionCode]];
				// this.sign = button.sortData.sign + 1;

				this.refresh();
			},
			// 按钮：第一按钮触发的事件
			changeRecord1 : function(button) {

				this.sign = 0;

				if (this.mainApp.deptId == 0) {

					return;
				}
				for (var i = this.sign; i < 14; i++) {// 当前按钮之后的按钮数据清空,用于点击点击按钮再选择数据

					var bt = this.otherTbar.findById("button" + i)
					bt.setText("");
					bt.sortData.regionCode = 0
				}

				this.sign++;

				this.initCnd = ["eq", ["$", "a.parentCode"],
						["s", this.rr.city.key]]
				this.requestData.cnd = ["eq", ["$", "a.parentCode"],
						["s", this.rr.city.key]];
				this.sign = 0;

				this.refresh();
			},

			// 数据双击
			onDblClick : function() {

				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var b = this.otherTbar.findById("button" + this.sign)
				var t = b["id"];
				var tt = t.substr(6, t.length - 1)

				if (tt % 2 == 0) {

					this.otherTbar.findById("button" + (this.sign))
							.setText(">>");// addCls//<font
					// color="red">CE</font>‘

					this.otherTbar.findById("button" + (this.sign)).disable();
					this.sign++;
					b = this.otherTbar.findById("button" + this.sign)

				}
				b.enable();// 默认为可用
				if (r.get("isBottom") == "y") {
					// 如果是最后节点,赋值全局变量
					this.regionCode = r.get("regionCode");
					this.regionCode_text = r.get("regionName")

					b.setText(r.get("regionName"));
					b.disable();// 如果是最后节点，按钮就不可用，显示灰色

//					this.doQd();
						if (!this.regionCode || this.regionCode == 0) {

					alert("未选择有效的网格地址");
					return;
				}
				var body = {};

				body["regionCode"] = this.regionCode;
				body["regionCode_text"] = this.regionCode_text;
				body["areaGridListId"] = this.areaGridListId;

				this.fireEvent("qd", body);
				this.doClose();
					// 关闭窗口的时候，情况按钮
					for (var i = 0; i < 14; i++) {

						var bt = this.otherTbar.findById("button" + i)
						bt.setText("");
						bt.sortData.regionCode = 0
					}

				} else {
					this.regionCode = 0;
					this.regionCode_text = "";
					if (!(tt % 2 == 1)) {

						b.setText("<u><font color=blue>" + r.get("regionName")
								+ "</font></u>");
					}
				}
				b.sortData.regionCode = r.get("regionCode")

				// for (var i = this.sign+1; i < 14; i++) {//
				// 当前按钮之后的按钮数据清空,用于点击点击按钮再选择数据
				// var bt = this.otherTbar.findById("button" + i)
				// bt.setText("");
				// bt.sortData.regionCode = 0
				// }
				this.sign++;

				this.initCnd = ["eq", ["$", "a.parentCode"],
						["s", r.get("regionCode")]];
				this.requestData.cnd = ["eq", ["$", "a.parentCode"],
						["s", r.get("regionCode")]];

				this.refresh();

			},
			// 确定
			doQd : function() {
				var r = this.getSelectedRecord();
				if (r == null) {
					return;
				}
				var b = this.otherTbar.findById("button" + this.sign)
				var t = b["id"];
				var tt = t.substr(6, t.length - 1)

				if (tt % 2 == 0) {

					this.otherTbar.findById("button" + (this.sign))
							.setText(">>");// addCls//<font
					// color="red">CE</font>‘

					this.otherTbar.findById("button" + (this.sign)).disable();
					this.sign++;
					b = this.otherTbar.findById("button" + this.sign)

				}
				b.enable();// 
				if (r.get("isBottom") != "y") {
					this.regionCode = 0;
					this.regionCode_text = "";
					if (!(tt % 2 == 1)) {

						b.setText("<u><font color=blue>" + r.get("regionName")
								+ "</font></u>");
					}

					alert("未选择有效的网格地址");
					return;

				} else {
					this.regionCode = r.get("regionCode");
					this.regionCode_text = r.get("regionName")

					b.setText(r.get("regionName"));
					b.disable();// 如果是最后节点，按钮就不可用，显示灰色
					for (var i = 0; i < 14; i++) {

						var bt = this.otherTbar.findById("button" + i)
						bt.setText("");
						bt.sortData.regionCode = 0
					}

				}
				if (!this.regionCode || this.regionCode == 0) {

					alert("未选择有效的网格地址");
					return;
				}
				var body = {};

				body["regionCode"] = this.regionCode;
				body["regionCode_text"] = this.regionCode_text;
				body["areaGridListId"] = this.areaGridListId;

				this.fireEvent("qd", body);
				this.doClose();
			},
			// 关闭
			doClose : function() {
				this.getWin().hide();
			},
			getWin : function() {
				var win = this.win
				var closeAction = this.closeAction || "hide"
				if (!this.mainApp || this.closeAction == true) {
					closeAction = "hide"
				}
				if (!win) {
					win = new Ext.Window({

						id : this.id,
						title : this.title || this.name,
						width : 601,// this.width620
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : true,
						autoScroll : true,
						height : 394
							// maximized : false

							// modal : this.modal || false （true的时候父窗口為灰色）
							// add by huangpf.
						})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("beforeclose", function() {

								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				return win;
			},
			getSystem : function() {
				var result = util.rmi.miniJsonRequestSync({
							serviceId : "chis.systemCommonManageService",
							serviceAction : "getConfig",
							method : "execute"
						})
				if (result.code != 200) {
					Ext.Msg.alert("提示", msg);
					return null;
				}
				return result.json.body;
			},
			getCndBar : function(items) {
				var _cfg = this;
				var fields = [];
				if (!this.enableCnd) {
					return []
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!(it.queryable)) {
						continue
					}
					fields.push({
								// change "i" to "it.id"
								value : it.id,
								text : it.alias
							})
				}
				if (fields.length == 0) {
					return fields;
				}
				var store = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
				var combox = new Ext.form.ComboBox({
					store : store,
					valueField : "value",
					displayField : "text",
					mode : 'local',
					triggerAction : 'all',
					emptyText : '选择查询字段',
					selectOnFocus : true,
					editable : false,
					width : this.queryComboBoxWidth || 120,
					hidden : true
						// 隐藏选择的条件
					});
				combox.on("select", this.onCndFieldSelect, this)
				this.cndFldCombox = combox
				var cndField = new Ext.form.TextField({
							width : this.cndFieldWidth || 200,
							selectOnFocus : true,
							name : "dftcndfld",
							emptyText : '拼音检索码',
							listeners : {
								specialkey : function(field, e) {
									if (e.getKey() == Ext.EventObject.ENTER) { // 触发回车事件
										e.stopEvent();
										_cfg.doCndQuery();
									}
								}
							}
						})
				this.cndField = cndField
				var queryBtn = new Ext.Toolbar.SplitButton({
							iconCls : "query",
							menu : new Ext.menu.Menu({
										items : {
											text : "高级查询",
											iconCls : "common_query",
											handler : this.doAdvancedQuery,
											scope : this
										}
									})
						})
				this.queryBtn = queryBtn;
				queryBtn.on("click", this.doCndQuery, this);
				// return [combox,'-',cndField,'-',queryBtn]
				return [combox, cndField, '-', queryBtn]
			},
			doCndQuery : function() {
//				var r = this.getSelectedRecord();
//				if(!r){
//				return;
//				}
				var initCnd =  this.initCnd
				var itid = this.cndFldCombox.getValue()
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				if (!it) {
					return;
				}
				this.resetFirstPage()
				var f = this.cndField;
				var v = f.getValue()
				var rawV = f.getRawValue();
				var xtype = f.getXType();
				if ((Ext.isEmpty(v) || Ext.isEmpty(rawV))
						&& (xtype !== "MyRadioGroup" && xtype !== "MyCheckboxGroup")) {
					this.queryCnd = null;
					this.requestData.cnd = initCnd
					this.refresh()
					return
				}
				if (f.getXType() == "datefield") {
					v = v.format("Y-m-d")
				}
				if (f.getXType() == "datetimefield") {
					v = v.format("Y-m-d H:i:s")
				}
				// 替换'，解决拼sql语句查询的时候报错
				v = v.replace(/'/g, "''")
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					var expType = this.getCndType(it.type)
					if (it.dic.render == "Tree") {
						var node = this.cndField.selectedNode;
						if (!node || node.isLeaf()) {
							cnd.push([expType, v]);
						} else {
							cnd[0] = 'like'
							cnd.push([expType, v + '%'])
						}
					} else {
						cnd.push([expType,v])
					}
				} else {
					switch (it.type) {
						case 'int' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							cnd[0] = 'like'
							cnd.push(['s', '%'+v + '%'])//模糊查询
							break;
						case "date" :
							if (v.format) {
								v = v.format("Y-m-d")
							}
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
						case 'datetime' :
						case 'timestamp' :
							if (it.xtype == "datefield") {
								v = v.format("Y-m-d")
								cnd[1] = [
										'$',
										"str(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd')"]
								cnd.push(['s', v])
							} else {
								v = v.format("Y-m-d H:i:s")
								cnd[1] = [
										'$',
										"str(" + refAlias + "." + it.id
												+ ",'yyyy-MM-dd HH:mm:ss')"]
								cnd.push(['s', v])
							}
							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				
				this.requestData.cnd = cnd
				this.refresh()
			}


		});