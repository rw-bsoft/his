$package("phis.application.emr.script");

$import("phis.script.SimpleList");
phis.application.emr.script.EMRDicInformationList = function(cfg) {
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	cfg.listServiceId = "emrManageService";
	cfg.serverParams = {
		serviceAction : "listDicInformation"
	};
	phis.application.emr.script.EMRDicInformationList.superclass.constructor.apply(this,
			[cfg]);
	this.on("winShow", this.onWinShow, this);
},

Ext.extend(phis.application.emr.script.EMRDicInformationList, phis.script.SimpleList,
		{
			onWinShow : function() {
				var showFieldId;
				if (this.paraB) {
					showFieldId = this.paraB.toString().split("|")[2];
				}
				if (!showFieldId) {
					return;
				}
				this.cndField.setValue();
				this.requestData.schema = this.entryName;
				this.requestData.fieldId = showFieldId;
				this.requestData.cndValue=null;
				this.loadData()
			},
			doSave : function() {
				var r = this.getSelectedRecord()
				if (r == null) {
					return
				}
				var showFieldId;
				if (this.paraB) {
					showFieldId = this.paraB.toString().split("|")[2];
				}
				var fieldValue = r.get(showFieldId);
				var startIndex;
				if (this.paraA) {
					startIndex = this.paraA.toString().split("|")[2];
				}
				this.fireEvent("appoint", fieldValue, startIndex);
				if (this.win) {
					this.win.hide();
				}
			},
			onDblClick : function() {
				this.doSave();
			},
			replaceCmData : function() {
				var schema;
				if (!schema) {
					var re = util.schema.loadSync(this.entryName)
					if (re.code == 200) {
						schema = re.schema;
					} else {
						this.processReturnMsg(re.code, re.msg,
								this.replaceCmData)
						return;
					}
				}
				this.schema = schema;
				this.isCompositeKey = schema.isCompositeKey;
				var items = schema.items
				if (!items) {
					return;
				}
				this.store = this.getStore(items);
				this.cm = new Ext.grid.ColumnModel(this.getCM(items))
				this.grid.reconfigure(this.store, this.cm);
			},
			getCM : function(items) {
				var cm = []
				var ac = util.Accredit;
				var expands = []
				if (this.showRowNumber) {
					cm.push(new Ext.grid.RowNumberer())
				}
				if (this.mutiSelect) {
					cm.push(this.sm);
				}
				var showFieldName;
				var showFieldId;
				if (this.paraB) {
					showFieldName = this.paraB.toString().split("|")[1];
					showFieldId = this.paraB.toString().split("|")[2];
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display <= 0 || it.display == 2)
							|| !ac.canRead(it.acValue)) {
						continue
					}
					if (showFieldId && showFieldId != it.id) {
						continue
					} else if (showFieldId) {
						it.alias = "";
						it.width = 350;
					} else {
						continue
					}
					if (it.expand) {
						var expand = {
							id : it.dic ? it.id + "_text" : it.id,
							alias : it.alias,
							xtype : it.xtype
						}
						expands.push(expand)
						continue
					}
					if (!this.fireEvent("onGetCM", it)) { // **
						// fire一个事件，在此处可以进行其他判断，比如跳过某个字段
						continue;
					}
					var width = parseInt(it.width || 80)
					// if(width < 80){width = 80}
					var c = {
						header : it.alias,
						width : width,
						sortable : this.sortable,// add by yangl 增加是否启用排序参数
						dataIndex : it.dic ? it.id + "_text" : it.id
					}
					if (!this.isCompositeKey && it.pkey == "true") {
						c.id = it.id
					}
					if (it.summaryType) {
						c.summaryType = it.summaryType;
					}
					switch (it.type) {
						case 'int' :
						case 'double' :
						case 'bigDecimal' :
							if (!it.dic) {
								c.css = "color:#00AA00;font-weight:bold;"
								c.align = "right"
								if (it.type == 'double'
										|| it.type == 'bigDecimal') {
									c.precision = it.precision;
									c.nullToValue = it.nullToValue;
									c.renderer = function(value, metaData, r,
											row, col, store) {
										if (value == null && this.nullToValue) {
											value = parseFloat(this.nullToValue)
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											try {
												r.set(this.id, retValue);
											} catch (e) {
												// 防止新增行报错
											}
											return retValue;
										}
										if (value != null) {
											value = parseFloat(value);
											var retValue = this.precision
													? value
															.toFixed(this.precision)
													: value;
											return retValue;
										}
									}
								}
							}
							break
						case 'timestamp' :
							// c.renderer = Ext.util.Format.dateRenderer('Y-m-d
							// HH:m:s')
					}
					if (it.renderer) {
						var func
						func = eval("this." + it.renderer)
						if (typeof func == 'function') {
							c.renderer = func
						}
					}
					if (it.summaryType) {
						c.summaryType = it.summaryType;
						if (it.summaryRenderer) {
							var func = eval("this." + it.summaryRenderer)
							if (typeof func == 'function') {
								c.summaryRenderer = func
							}
						}
					}
					if (this.fireEvent("addfield", c, it)) {
						cm.push(c)
					}
				}
				if (expands.length > 0) {
					this.rowExpander = this.getExpander(expands)
					cm = [this.rowExpander].concat(cm)
					this.array.push(this.rowExpander)// add by taoy
				}
				return cm
			},
			getCndBar : function(items) {
				if (!this.enableCnd) {
					return []
				}
				var cndField = new Ext.form.TextField({
							fieldLabel :"拼音码:",
							width : 150,
							selectOnFocus : true,
							name : "dftcndfld"
						})
				this.cndField = cndField
				var queryBtn = new Ext.Button({
							text : '查询',
							iconCls : "query",
							notReadOnly : true,
							width : 60
						})
				this.queryBtn = queryBtn
				queryBtn.on("click", this.doCndQuery, this);
				return [cndField, '-', queryBtn, '-']
			},
			doCndQuery : function(button, e, addNavCnd) { // ** modified by
				var v = this.cndField.getValue()
				var rawV = this.cndField.getRawValue();
				if (v == null || v == "" || rawV == null || rawV == "") {
					this.refresh()
					return
				}
				this.requestData.cndValue = v
				this.refresh()
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.name,
								width : this.width,
								iconCls : 'icon-grid',
								shim : true,
								layout : "fit",
								animCollapse : true,
								constrain : true,
								resizable : false,
								closeAction : closeAction,
								constrainHeader : true,
								minimizable : true,
								maximizable : true,
								shadow : false,
								modal : this.modal || true,
								items : this.initPanel()
							});
					win.on("show", function() {
								this.fireEvent("winShow");
							}, this);
					win.on("beforeshow", function() {
								this.fireEvent("beforeWinShow");
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this);
							}, this);
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("hide", function() {
								this.fireEvent("close", this);
							}, this);
					var renderToEl = this.getRenderToEl();
					if (renderToEl) {
						win.render(renderToEl);
					}
					this.win = win;
				}
				return win;
			}
		});