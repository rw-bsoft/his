$package("phis.application.cic.script")
$import("phis.script.SimpleList", "phis.script.rmi.jsonRequest")

phis.application.cic.script.UserFulExpressionsList = function(cfg) {
	cfg.showRowNumber = false;
	cfg.disablePagingTbr = true;
	cfg.autoLoadData = false;
	cfg.autoLoadSchema = false;
	phis.application.cic.script.UserFulExpressionsList.superclass.constructor.apply(
			this, [cfg]);
	this.on("winShow", this.onWinShow, this);
}
Ext.extend(phis.application.cic.script.UserFulExpressionsList,
		phis.script.SimpleList, {
			expansion : function(cfg) {
				cfg.emr_parent = this.emr_parent;
				var bbar = {
					id : "radioGroup",
					xtype : 'radiogroup',
					items : [{
								boxLabel : '所有',
								name : 'stpType',
								inputValue : 3,
								checked : true
							}, {
								boxLabel : '个人',
								name : 'stpType',
								inputValue : 1
							}, {
								boxLabel : '科室',
								name : 'stpType',
								inputValue : 0
							}, {
								boxLabel : '公用',
								name : 'stpType',
								inputValue : 2
							}]
				};
				cfg.bbar = bbar;
			},
			bbarChange : function(field, newValue, oldValue) {
				var nv = newValue.inputValue;
				if (nv == 0) {
					this.requestData.stpType = "0";
				} else if (nv == 1) {
					this.requestData.stpType = "1";
				} else if (nv == 2) {
					this.requestData.stpType = "2";
				} else if (nv == 3) {
					this.requestData.stpType = "all";
				}
				this.loadData();
			},
			onWinShow : function() {
				this.requestData.serviceId = "phis.userFulExpressionsService";
				this.requestData.serviceAction = "listUserFulExpressions";
				this.requestData.ygdm = this.mainApp.uid;
				if (this.KSDM) {
					this.requestData.ksdm = this.KSDM;
				}
				this.requestData.stpType = "all";
				this.loadData();
				var bbar = this.grid.getBottomToolbar();
				bbar.on("change", this.bbarChange, this);
				var radioGroup = Ext.getCmp("radioGroup");
				radioGroup.setValue(3);
			},
			onChangeBack : function() {
				alert('111111');
			},
			onDblClick : function() {
				if (!this.hasDeal) {
					this.hasDeal = true;
					var r = this.getSelectedRecord();
					if (r == null) {
						this.hasDeal = false;
						return;
					}
					var s = this.emr.FunActiveXInterface("BsCurrentPos", '0',
							'');
					var startIndex = this.emr.StrReturnData;
					// if (startIndex == null || startIndex < 1) {
					// Ext.Msg.alert("提示", "请选择正确的插入位置!")
					// return;
					// }

					var txtData = this.getTxtOrXmlData(r.id, "txt");
					var xmlData = this.getTxtOrXmlData(r.id, "xml");
					if (txtData == null) {
						Ext.Msg.alert("提示", "不能引用常用语，原因是选定的常用语内容为空!不继续操作!")
						this.hasDeal = false;
						return;
					}
					if (xmlData == null) {
						xmlData = '';
					}
					var s = this.emr.FunActiveXInterface("BsPasteSelectXmlRec",
							txtData, xmlData);

					s = this.emr.FunActiveXInterface("BsCurrentPos", '0', '');
					var endIndex = this.emr.StrReturnData;

					s = this.emr.FunActiveXInterface("BsCmdSelect", startIndex,
							endIndex);
					this.hasDeal = false;
				}
			},

			getTxtOrXmlData : function(PTID, type) {
				var data = null;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "userFulExpressionsService",
							serviceAction : "getTxtOrXmlData",
							PTID : PTID,
							type : type
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return
				}
				if (r.json.body) {
					data = r.json.body;
				}
				return data;
			},

			clickImgToLogout : function(grid, rowIndex, colIndex) {
				this.store = grid.getStore();
				var record = this.store.getAt(rowIndex);
				if (record == null) {
					return;
				}
				if (grid.emr_parent) {
					grid.emr_parent.hideEmr();
				}
				Ext.Msg.show({
					title : '确认注销常用语[' + record.get("PTNAME") + ']',
					msg : '注销操作将无法恢复，是否继续?',
					modal : true,
					width : 300,
					buttons : Ext.MessageBox.OKCANCEL,
					multiline : false,
					fn : function(btn, text) {
						if (btn == "ok") {
							phis.script.rmi.jsonRequest({
										serviceId : "userFulExpressionsService",
										serviceAction : "removeUserFul",
										pkey : record.id
									}, function(code, msg, json) {
										if (code < 300) {
											this.store.remove(record);
										} else {
											Ext.MessageBox.alert("错误", msg)
										}
										if (grid.emr_parent) {
											grid.emr_parent.showEmr();
										}
									}, this)
						} else {
							if (grid.emr_parent) {
								grid.emr_parent.showEmr();
							}
						}
					},
					scope : this
				})

			},

			getCM : function(items) {
				var cm = []
				var ac = util.Accredit;
				var expands = []
				if (this.showRowNumber) {
					cm.push(new Ext.grid.RowNumberer())
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if ((it.display <= 0 || it.display == 2)
							|| !ac.canRead(it.acValue)) {
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
				this.clm = {
					xtype : 'actioncolumn',
					width : 35,
					header : "注销",
					items : [{
								icon : 'images/no.png',
								tooltip : '注销',
								handler : this.clickImgToLogout
							}]
				}
				cm.push(this.clm);
				return cm
			},
			getWin : function() {
				var win = this.win;
				var closeAction = "hide";
				if (!win) {
					win = new Ext.Window({
								id : this.id,
								title : this.title,
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

		})
