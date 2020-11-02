/**
 * 公共文件
 * 
 * @author : yaozh
 */
$package("chis.script")

$import("chis.script.ChisSimpleListView", "chis.script.BizCommon",
		"chis.script.BizListCommon", "chis.script.util.widgets.MyMessageTip")

chis.script.BizSimpleListView = function(cfg) {
	cfg.buttonIndex = cfg.buttonIndex || 0;
	cfg.showButtonOnTop = cfg.showButtonOnTop || true
	cfg.listServiceId = cfg.listServiceId || "chis.simpleQuery"
	cfg.removeServiceId = cfg.removeServiceId || "chis.simpleRemove"
	cfg.businessType = cfg.businessType; //businessType 为档案随访计划类型编号(planType字典key值)列表工具栏上取开始日期用,详见getStartDate()
	Ext.apply(cfg, chis.script.BizCommon);
	Ext.apply(cfg, chis.script.BizListCommon);
	this.pageSize=25;
	chis.script.BizSimpleListView.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.script.BizSimpleListView, chis.script.ChisSimpleListView , {

	getRemoveRequest : function(r) {
		return {
			pkey : r.id
		};
	},
	onStoreBeforeLoad : function(store, op) {
		var r = this.getSelectedRecord()
		var n = this.store.indexOf(r)
		if (n > -1) {
			this.selectedIndex = n
		}
		if (this.yearField) {
			var year = this.yearField.getValue();
			if (typeof year == "object") {
				year = year.key;
			}
			this.requestData.year = year;
		}
		if (this.checkField) {
			var checkType = this.checkField.getValue()
			if (typeof checkType == "object") {
				checkType = checkType.key;
			}
			this.requestData.checkType = checkType;
		}
	},
	getCndBar : function(items) {
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
		if (this.needOwnerBar) {
			var fields = this.createOwnerBar();
			return [fields, '-', queryBtn]
		} else {
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
						width : this.queryComboBoxWidth || 120
					});
			combox.on("select", this.onCndFieldSelect, this)
			this.cndFldCombox = combox
			var cndField = new Ext.form.TextField({
						width : this.cndFieldWidth || 200,
						selectOnFocus : true,
						name : "dftcndfld"
					})
			this.cndField = cndField
			return [combox, '-', cndField, '-', queryBtn]
		}

	},
	manageBlur : function() {
		if (this.manageField
				&& (this.manageField.getRawValue() == null || this.manageField
						.getRawValue() == "")) {
			this.manageField.setValue();
		}
	},
	createOwnerBar : function() {
		var manageLabel = new Ext.form.Label({
					html : "管辖机构:",
					width : 80

				});
		var manageField = this.createDicField({
					'id' : 'chis.@manageUnit',
					'showWholeText' : 'true',
					'includeParentMinLen' : '6',
					'render' : 'Tree',
					defaultValue : {
						"key" : this.mainApp.deptId,
						"text" : this.mainApp.dept
					},
					'parentKey' : this.mainApp.deptId,
					rootVisible : true,
					width : 120
				});
		manageField.on("blur", this.manageBlur, this);
		this.manageField = manageField;
		var dateLabel1 = new Ext.form.Label({
					html : "&nbsp;建档日期:",
					width : 80
				});
		var startValue = this.getStartDate(this.businessType);
		var dateField1 = new Ext.form.DateField({
					width : this.cndFieldWidth || 120,
					enableKeyEvents : true,
					emptyText : "建档开始日期",
					value : startValue,
					name : "createDate1"
				});
		this.dateField1 = dateField1;
		var dateLabel2 = new Ext.form.Label({
					html : "&nbsp;->&nbsp;",
					width : 30
				});
		var dateField2 = new Ext.form.DateField({
					width : this.cndFieldWidth || 120,
					enableKeyEvents : true,
					emptyText : "建档结束日期",
					value : new Date(),
					name : "createDate2"
				});
		this.dateField2 = dateField2;
		this.dateField1.on("select", this.selectDateField1, this);
		this.dateField2.on("select", this.selectDateField2, this);
		var cnd = this.getOwnerCnd([]);
		if (this.requestData.cnd) {
			cnd = ['and', this.requestData.cnd, cnd]
		}
		this.requestData.cnd = cnd;
		return [manageLabel, manageField, dateLabel1, dateField1, dateLabel2,
				dateField2]
	},
	selectDateField1 : function(field, date) {
		if (date && this.dateField2) {
			this.dateField2.setMinValue(date);
		}
	},
	selectDateField2 : function(field, date) {
		if (date && this.dateField1) {
			this.dateField1.setMaxValue(date);
		}
	},
	getOwnerCnd : function(cnd) {
		if (this.manageField.getValue() != null
				&& this.manageField.getValue() != "") {
			var manageUnit = this.manageField.getValue();
			if (typeof manageUnit != "string") {
				manageUnit = manageUnit.key;
			}
			var cnd1 = ['like', ['$', 'a.manaUnitId'], ['s', manageUnit + "%"]];
			if (cnd.length == 0) {
				cnd = cnd1;
			} else {
				cnd = ['and', cnd1, cnd];
			}
		}
		if (this.dateField1.getValue() != null
				&& this.dateField2.getValue() != null
				&& this.dateField1.getValue() != ""
				&& this.dateField2.getValue() != "") {
			var date1 = this.dateField1.getValue();
			var date2 = this.dateField2.getValue();
			var cnd2 = [
					'and',
					[
							'ge',
							['$', 'a.createDate'],
							['todate',
									['s', date1.format("Y-m-d") + " 00:00:00"],
									['s', 'yyyy-mm-dd HH24:mi:ss']]],
					[
							'le',
							['$', 'a.createDate'],
							['todate',
									['s', date2.format("Y-m-d") + " 23:59:59"],
									['s', 'yyyy-mm-dd HH24:mi:ss']]]];
			if (cnd.length == 0) {
				cnd = cnd2;
			} else {
				cnd = ['and', cnd2, cnd];
			}
		} else if ((this.dateField1.getValue() == null || this.dateField1
				.getValue() == "")
				&& (this.dateField2.getValue() == null || this.dateField2
						.getValue() == "")) {

		} else if (this.dateField1.getValue() == null
				|| this.dateField1.getValue() == "") {
			MyMessageTip.msg("提示", "请选择建档开始日期！", true);
			return;
		} else if (this.dateField2.getValue() == null
				|| this.dateField2.getValue() == "") {
			MyMessageTip.msg("提示", "请选择建档结束日期！", true);
			return;
		}
		return cnd;
	},
	doCndQuery : function(button, e, addNavCnd) {
		if (this.yearField) {
			this.requestData.year = this.yearField.getValue();
		}
		if (this.checkField) {
			this.requestData.checkType = this.checkField.getValue();
		}
		var initCnd = this.initCnd
		var cnd = [];
		this.resetFirstPage()
		if (this.needOwnerBar) {
			cnd = this.getOwnerCnd(cnd);
		} else {
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
				if (addNavCnd) {
					if (initCnd) {
						this.requestData.cnd = ['and', initCnd, this.navCnd];
					} else {
						this.requestData.cnd = this.navCnd;
					}
					this.refresh()
					return
				} else {
					this.refresh()
					return;
				}
			}
			var f = this.cndField
			var v = this.cndField.getValue()
			var rawV = this.cndField.getRawValue();
			var xtype = f.getXType();
			if ((Ext.isEmpty(v) || Ext.isEmpty(rawV))
					&& (xtype !== "MyRadioGroup" && xtype !== "MyCheckboxGroup")) {
				this.queryCnd = null;
				if (addNavCnd) {
					if (initCnd) {
						this.requestData.cnd = ['and', initCnd, this.navCnd];
					} else {
						this.requestData.cnd = this.navCnd;
					}
					this.refresh()
					return
				} else {
					if (initCnd)
						cnd = initCnd
				}
				this.requestData.cnd = cnd.length == 0 ? null : cnd;
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
				if (it.dic.render == "Tree") {
					// var node = this.cndField.selectedNode
					// @@ modified by chinnsii 2010-02-28, add "!node"
					cnd[0] = 'eq'
					// if (!node || !node.isLeaf()) {
					// cnd[0] = 'like'
					// cnd.push(['s', v + '%'])
					// } else {
					cnd.push(['s', v])
					// }
				} else {
					cnd.push(['s', v])
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
						cnd.push(['s', '%' + v + '%'])
						break;
					case "date" :
						// v = v.format("Y-m-d")
						cnd[1] = [
								'$',
								"to_char(" + refAlias + "." + it.id
										+ ",'yyyy-MM-dd')"]
						cnd.push(['s', v])
						break;
					case 'datetime' :
					case 'timestamp' :
						if (it.xtype == "datefield") {
							// v = v.format("Y-m-d")
							cnd[1] = [
									'$',
									"to_char(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
						} else {
							// v = v.format("Y-m-d H:i:s")
							cnd[1] = [
									'$',
									"to_char(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd HH24:mi:ss')"]
							cnd.push(['s', v])
						}

						break;
				}
			}
		}
		this.queryCnd = cnd
		if (initCnd && cnd.length > 0) {
			cnd = ['and', initCnd, cnd]
		} else if (initCnd) {
			cnd = initCnd
		}
		if (addNavCnd) {
			this.requestData.cnd = ['and', cnd, this.navCnd];
			this.refresh()
			return
		}
		this.requestData.cnd = cnd
		this.refresh()
	},
	/**
	 * 控制页面按钮权限
	 */
	resetButtons : function() {
		if (this.showButtonOnTop && this.form.getTopToolbar()) {
			var btns = this.form.getTopToolbar().items;
			if (!btns) {
				return;
			}
			var n = btns.getCount();
			for (var i = 0; i < n; i++) {
				var btn = btns.item(i)
				this.setButtonControl(btn);
			}
		} else {
			var btns = this.form.buttons;
			if (!btns) {
				return;
			}
			for (var i = 0; i < btns.length; i++) {
				var btn = btns[i]
				this.setButtonControl(btn);
			}
		}
	},
	onReady : function() {
		if (this.showBtnOnLevel) {
			var otherTbar = new Ext.Toolbar(this.createButtons("two"));
			this.otherTbar = otherTbar;
			this.grid.getView().showBtnOnLevel = true;
			this.grid.add(otherTbar)
			this.grid.doLayout();
			this.grid.getView().refresh();
		}
		if (this.autoLoadData) {
			this.loadData();
		}
		var el = this.grid.el
		if (!el) {
			return
		}
		var actions = this.actions
		if (!actions) {
			return
		}
		var keyMap = new Ext.KeyMap(el)
		keyMap.stopEvent = true

		// index btns
		var btnAccessKeys = {}
		var keys = []
		if (this.showButtonOnTop) {
			var btns = this.grid.getTopToolbar().items || [];
			var n = btns.getCount()
			for (var i = 0; i < n; i++) {
				var btn = btns.item(i)
				var key = btn.accessKey
				if (key) {
					btnAccessKeys[key] = btn
					keys.push(key)
				}
			}
		} else {
			var btns = this.grid.buttons || []
			for (var i = 0; i < btns.length; i++) {
				var btn = btns[i]
				var key = btn.accessKey
				if (key) {
					btnAccessKeys[key] = btn
					keys.push(key)
				}
			}
		}
		this.btnAccessKeys = btnAccessKeys
		// 屏蔽框架自带的快捷键
		// keyMap.on(keys,this.onAccessKey,this)
		keyMap.on(Ext.EventObject.ENTER, this.onEnterKey, this)

	},
	getYearCheckItems : function() {
		var yearLabel = new Ext.form.Label({
					html : "&nbsp;&nbsp;年度:",
					width : 90
				});
		var yearField = this.createDicField({
					editable : false,
					defaultValue : {
						"key" : new Date().getFullYear(),
						"text" : new Date().getFullYear()
					},
					forceSelection : true,
					width : 60,
					id : "chis.dictionary.years"
				});
		this.yearField = yearField;
		this.yearField.on("select", this.yearFieldSelect, this);
		var checkLabel = new Ext.form.Label({
					html : "&nbsp;&nbsp;体检:",
					width : 80
				});
		var checkField = this.createDicField({
					editable : false,
					defaultValue : {
						"key" : "1",
						"text" : "全部"
					},
					forceSelection : true,
					width : 60,
					id : "chis.dictionary.checkuped"
				});
		this.checkField = checkField;
		this.checkField.on("select", this.checkFieldSelect, this);
		return [yearLabel, yearField, checkLabel, checkField];
	},
	yearFieldSelect : function(combo, record, index) {
		this.requestData.year = this.yearField.getValue();
		this.doCndQuery();
	},
	checkFieldSelect : function(combo, record, index) {
		this.requestData.checkType = this.checkField.getValue();
		this.doCndQuery();
	},
	getWin : function() {
		var win = this.win
		var closeAction = "close"
		if (!this.mainApp || this.closeAction) {
			closeAction = "hide"
		}
		if (!win) {
			win = new Ext.Window({
				title : this.title || this.name,
				width : this.width,
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
				modal : this.modal || false
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
			win.on("close", function() {
						this.fireEvent("close", this)
					}, this)
			win.on("hide", function() {
						this.fireEvent("hide", this)
					}, this)
			this.win = win
		}
		win.maximize() ;
		win.instance = this;
		return win;
	}
})