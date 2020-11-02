$package("phis.application.cfg.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.cfg.script.ConfigassetsForm = function(cfg) {
	cfg.showButtonOnTop = false;
	cfg.colCount = 4;
	phis.application.cfg.script.ConfigassetsForm.superclass.constructor.apply(
			this, [ cfg ])
	this.wzxh = 0;
	this.cjxh = 0;
	this.zblb = 0;
	this.ghdw = 0;
}
Ext
		.extend(
				phis.application.cfg.script.ConfigassetsForm,
				phis.script.TableForm,
				{
					getRemoteDicReader : function(it) {
						return new Ext.data.JsonReader({
							root : 'mds',
							totalProperty : 'count',
							id : 'mdssearch'
						}, [ {
							name : 'WZXH'
						}, {
							name : 'WZMC'
						}, {
							name : 'WZGG'
						}, {
							name : 'WZDW'
						}, {
							name : 'CJXH'
						}, {
							name : 'CJMC'
						}, {
							name : 'KFXH'
						}, {
							name : 'KFMC'
						}, {
							name : 'ZBLB'
						} ]);
					},
					getRemoteDicReaderdw : function(it) {
						return new Ext.data.JsonReader({
							root : 'mds',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'mdssearch'
						}, [ {
							name : 'DWXH'
						}, {
							name : 'DWMC'
						} ]);
					},
					createRemoteDicField : function(it) {
						var mds_reader = this.getRemoteDicReader(it);
						// store远程url
						var remoteUrl = 'Supplies'
						var remoteTpl = '<td width="18px" style="background-color:#deecfd">{#}.</td><td width="200px">{WZMC}</td><td width="60px">{WZGG}</td><td width="40px">{WZDW}</td><td width="200px">{CJMC}</td><td width="40px">{KFMC}</td>';
						// store远程url
						var url = ClassLoader.serverAppUrl || "";
						this.comboJsonData = {
							serviceId : "phis.searchService",
							serviceAction : "loadDicData",
							method : "execute",
							className : remoteUrl
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
						proxy.on("loadexception", function(proxy, o, response,
								arg, e) {
							if (response.status == 200) {
								var json = eval("(" + response.responseText
										+ ")")
								if (json) {
									var code = json["code"]
									var msg = json["msg"]
									this.processReturnMsg(code, msg,
											this.refresh)
								}
							} else {
								this.processReturnMsg(404, 'ConnectionError',
										this.refresh)
							}
						}, this)
						this.remoteDicStore = mdsstore;
						this.queryParams = {
							"tag" : 3
						};
						Ext.apply(this.remoteDicStore.baseParams,
								this.queryParams);
						var resultTpl = new Ext.XTemplate(
								'<tpl for=".">',
								'<div class="search-item">',
								'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
								'<tr>' + remoteTpl + '<tr>', '</table>',
								'</div>', '</tpl>');
						var _ctx = this;
						// update for label颜色变红 by caijy
						var fieldLabel = it.alias;
						if (it['showRed']) {
							fieldLabel = "<span style='color:red'>" + it.alias
									+ "</span>"
						}
						var remoteField = new Ext.form.ComboBox({
							name : it.id,
							fieldLabel : fieldLabel,
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
								// onSelect
								// to do
								_ctx.setBackInfo(this, record);
								this.collapse();
							}
						});
						remoteField.on("focus", function() {
							this.wzxh = 0;
							this.cjxh = 0;
							this.zblb = 0;
							remoteField.innerList.setStyle('overflow-y',
									'hidden');
						}, this);
						remoteField
								.on(
										"keyup",
										function(obj, e) {// 实现数字键导航
											this.wzxh = 0;
											this.cjxh = 0;
											this.zblb = 0;
											var key = e.getKey();
											if (key == e.ENTER
													&& !obj.isExpanded()) {
												// 是否是字母
												if (key == e.ENTER) {
													if (!obj.isExpanded()) {
														// 是否是字母
														var patrn = /^[a-zA-Z.]+$/;
														if (patrn.exec(obj
																.getValue())) {
															// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
															obj
																	.getStore()
																	.removeAll();
															obj.lastQuery = "";
															if (obj
																	.doQuery(
																			obj
																					.getValue(),
																			true) !== false) {
																e.stopEvent();
																return;
															}
														}
													}
													_ctx
															.focusFieldAfter(obj.index);
													return;
												}
												var patrn = /^[a-zA-Z.]+$/;
												if (patrn.exec(obj.getValue())) {
													// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
													obj.getStore().removeAll();
													obj.lastQuery = "";
													if (obj.doQuery(obj
															.getValue(), true) !== false) {
														e.stopEvent();
														return;
													}
												}
											}
											if ((key >= 48 && key <= 57)
													|| (key >= 96 && key <= 105)) {
												if (obj.isExpanded()) {
													if (key == 48 || key == 96)
														key = key + 10;
													key = key < 59 ? key - 49
															: key - 97;
													var record = this
															.getStore().getAt(
																	key);
													_ctx.setBackInfo(obj,
															record);
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
								Ext.apply(_ctx.comboJsonData,
										mdsstore.baseParams);
								options = Ext.apply({}, options);
								this.storeOptions(options);
								if (this.sortInfo && this.remoteSort) {
									var pn = this.paramNames;
									options.params = Ext.apply({},
											options.params);
									options.params[pn.sort] = this.sortInfo.field;
									options.params[pn.dir] = this.sortInfo.direction;
								}
								try {
									return this.execute('read', null, options); // <--
								} catch (e) {
									this.handleException(e);
									return false;
								}
							}
						}
						remoteField.on("beforequery", function(qe) {
							this.comboJsonData.query = qe.query;
							return true;
						}, this);
						remoteField.isSearchField = true;
						this.remoteDic = remoteField;
						return remoteField
					},
					createRemoteDicFielddw : function(it) {
						var mds_readerdw = this.getRemoteDicReaderdw(it);
						// store远程url
						var remoteUrldw = 'SupplyUnits'
						var remoteTpldw = '<td width="12px" style="background-color:#deecfd">{#}.</td><td width="100px">{DWMC}</td>';
						// store远程url
						var urldw = ClassLoader.serverAppUrl || "";
						this.comboJsonDatadw = {
							serviceId : "phis.searchService",
							serviceAction : "loadDicData",
							method : "execute",
							className : remoteUrldw
						// ,pageSize : this.pageSize || 25,
						// pageNo : 1
						}
						var proxydw = new Ext.data.HttpProxy({
							url : urldw + '*.jsonRequest',
							method : 'POST',
							jsonData : this.comboJsonDatadw
						});
						var mdsstore = new Ext.data.Store({
							proxy : proxydw,
							reader : mds_readerdw
						});
						proxydw.on("loadexception", function(proxydw, o,
								response, arg, e) {
							if (response.status == 200) {
								var json = eval("(" + response.responseText
										+ ")")
								if (json) {
									var code = json["code"]
									var msg = json["msg"]
									this.processReturnMsg(code, msg,
											this.refresh)
								}
							} else {
								this.processReturnMsg(404, 'ConnectionError',
										this.refresh)
							}
						}, this)
						this.remoteDicStoredw = mdsstore;
						Ext.apply(this.remoteDicStoredw.baseParams,
								this.queryParams);
						var resultTpldw = new Ext.XTemplate(
								'<tpl for=".">',
								'<div class="search-item">',
								'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
								'<tr>' + remoteTpldw + '<tr>', '</table>',
								'</div>', '</tpl>');
						var _ctx = this;
						// update for label颜色变红 by caijy
						var fieldLabeldw = it.alias;
						if (it['showRed']) {
							fieldLabeldw = "<span style='color:red'>"
									+ it.alias + "</span>"
						}
						var remoteFielddw = new Ext.form.ComboBox({
							name : it.id,
							fieldLabel : fieldLabeldw,
							width : 280,
							store : mdsstore,
							selectOnFocus : true,
							typeAhead : false,
							loadingText : '搜索中...',
							pageSize : 10,
							hideTrigger : true,
							minListWidth : this.minListWidth || 280,
							tpl : resultTpldw,
							minChars : 2,
							enableKeyEvents : true,
							lazyInit : false,
							itemSelector : 'div.search-item',
							onSelect : function(record) { // override
								// default
								// onSelect
								// to do
								_ctx.setBackInfodw(this, record);
								this.collapse();
							}
						});
						remoteFielddw.on("focus", function() {
							this.ghdw = 0;
							remoteFielddw.innerList.setStyle('overflow-y',
									'hidden');
						}, this);
						remoteFielddw
								.on(
										"keyup",
										function(obj, e) {// 实现数字键导航
											this.ghdw = 0;
											var key = e.getKey();
											if (key == e.ENTER
													&& !obj.isExpanded()) {
												// 是否是字母
												if (key == e.ENTER) {
													if (!obj.isExpanded()) {
														// 是否是字母
														var patrn = /^[a-zA-Z.]+$/;
														if (patrn.exec(obj
																.getValue())) {
															// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
															obj
																	.getStore()
																	.removeAll();
															obj.lastQuery = "";
															if (obj
																	.doQuery(
																			obj
																					.getValue(),
																			true) !== false) {
																e.stopEvent();
																return;
															}
														}
													}
													_ctx
															.focusFieldAfter(obj.index);
													return;
												}
												var patrn = /^[a-zA-Z.]+$/;
												if (patrn.exec(obj.getValue())) {
													// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
													obj.getStore().removeAll();
													obj.lastQuery = "";
													if (obj.doQuery(obj
															.getValue(), true) !== false) {
														e.stopEvent();
														return;
													}
												}
											}
											if ((key >= 48 && key <= 57)
													|| (key >= 96 && key <= 105)) {
												if (obj.isExpanded()) {
													if (key == 48 || key == 96)
														key = key + 10;
													key = key < 59 ? key - 49
															: key - 97;
													var record = this
															.getStore().getAt(
																	key);
													_ctx.setBackInfodw(obj,
															record);
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
						if (remoteFielddw.store) {
							remoteFielddw.store.load = function(options) {
								Ext.apply(_ctx.comboJsonDatadw, options.params);
								Ext.apply(_ctx.comboJsonDatadw,
										mdsstore.baseParams);
								options = Ext.apply({}, options);
								this.storeOptions(options);
								if (this.sortInfo && this.remoteSort) {
									var pn = this.paramNames;
									options.params = Ext.apply({},
											options.params);
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
						remoteFielddw.on("beforequery", function(qe) {
							this.comboJsonDatadw.query = qe.query;
							return true;
						}, this);
						remoteFielddw.isSearchField = true;
						this.remoteDic = remoteFielddw;
						return remoteFielddw
					},
					setBackInfo : function(obj, r) {
						var form = this.form.getForm();
						form.findField("WZMC").setValue(r.get("WZMC"));
						form.findField("WZGG").setValue(r.get("WZGG"));
						form.findField("WZDW").setValue(r.get("WZDW"));
						form.findField("KFXH").setValue(r.get("KFXH"));
						form.findField("CJMC").setValue(r.get("CJMC"));
						this.wzxh = r.get("WZXH");
						this.cjxh = r.get("CJXH");
						this.zblb = r.get("ZBLB");
						obj.collapse();
						obj.triggerBlur();
					},
					setBackInfodw : function(obj, r) {
						// 将选中的记录设置到行数据中
						var form = this.form.getForm();
						form.findField("DWMC").setValue(r.get("DWMC"));
						this.ghdw = r.get("DWXH");
						this.operater.list.doCreate();
						obj.collapse();
					},
					createField : function(it) {
						var ac = util.Accredit;
						var defaultWidth = this.fldDefaultWidth || 200
						// alert(defaultWidth)
						var cfg = {
							name : it.id,
							fieldLabel : it.alias,
							xtype : it.xtype || "textfield",
							vtype : it.vtype,
							width : defaultWidth,
							value : it.defaultValue,
							enableKeyEvents : it.enableKeyEvents,
							validationEvent : it.validationEvent
						}
						cfg.listeners = {
							specialkey : this.onFieldSpecialkey,
							scope : this
						}
						if (it.inputType) {
							cfg.inputType = it.inputType
						}
						if (it['not-null']) {
							cfg.allowBlank = false
							cfg.invalidText = "必填字段"
							cfg.fieldLabel = "<span style='color:red'>"
									+ cfg.fieldLabel + "</span>"
						}
						if (it['showRed']) {
							cfg.fieldLabel = "<span style='color:red'>"
									+ cfg.fieldLabel + "</span>"
						}
						if (it.fixed || it.fixed) {
							cfg.disabled = true
						}
						if (it.pkey && it.generator == 'auto') {
							cfg.disabled = true
						}
						if (it.evalOnServer && ac.canRead(it.acValue)) {
							cfg.disabled = true
						}
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
							cfg.disabled = true
						}
						if (this.op == "update" && !ac.canUpdate(it.acValue)) {
							cfg.disabled = true
						}
						if (it.properties && it.properties.mode == "remote") {
							return this.createRemoteDicField(it);
						}
						if (it.properties && it.properties.mode == "remotedw") {
							return this.createRemoteDicFielddw(it);
						} else if (it.dic) {
							if (it.dic.render == "TreeCheck") {
								if (it.length) {
									cfg.maxLength = it.length;
								}
							}

							it.dic.src = this.entryName + "." + it.id
							it.dic.defaultValue = it.defaultValue
							it.dic.width = defaultWidth
							var combox = this.createDicField(it.dic)
							Ext.apply(combox, cfg)
							combox.on("specialkey", this.onFieldSpecialkey,
									this)
							return combox;
						}

						if (it.length) {
							cfg.maxLength = it.length;
						}

						if (it.xtype) {
							return cfg;
						}
						switch (it.type) {
						case 'int':
						case 'double':
						case 'bigDecimal':
							cfg.xtype = "numberfield"
							if (it.type == 'int') {
								cfg.decimalPrecision = 0;
								cfg.allowDecimals = false
							} else {
								cfg.decimalPrecision = it.precision || 2;
							}
							if (it.minValue) {
								cfg.minValue = it.minValue;
							} else {
								cfg.minValue = 0;
							}
							if (it.maxValue) {
								cfg.maxValue = it.maxValue;
							}
							break;
						case 'date':
							cfg.xtype = 'datefield'
							cfg.emptyText = "请选择日期"
							cfg.format = 'Y-m-d'
							break;
						case 'text':
							cfg.xtype = "htmleditor"
							cfg.enableSourceEdit = false
							cfg.enableLinks = false
							cfg.width = 700
							cfg.height = 450
							break;
						}
						return cfg;
					},
					doUpdat : function(jlxh) {
						var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "configInventoryInitialService",
							serviceAction : "backfillingAssetsIn",
							jlxh : jlxh
						});
						var values = r.json.body;
						if (values) {
							var form = this.form.getForm();
							if (form) {
								if (values.WZMC) {
									form.findField("WZMC")
											.setValue(values.WZMC);
								}
								if (values.WZGG) {
									form.findField("WZGG")
											.setValue(values.WZGG);
								}
								if (values.WZDW) {
									form.findField("WZDW")
											.setValue(values.WZDW);
								}
								if (values.KFXH) {
									form.findField("KFXH")
											.setValue(values.KFXH);
								}
								if (values.TZRQ) {
									form.findField("TZRQ")
											.setValue(values.TZRQ);
								}
								if (values.ZCYZ) {
									form.findField("ZCYZ")
											.setValue(values.ZCYZ);
								}
								if (values.WHYZ) {
									form.findField("WHYZ")
											.setValue(values.WHYZ);
								}
								if (values.HBDW) {
									form.findField("HBDW")
											.setValue(values.HBDW);
								}
								if (values.CJMC) {
									form.findField("CJMC")
											.setValue(values.CJMC);
								}
								if (values.DWMC) {
									form.findField("DWMC")
											.setValue(values.DWMC);
								}
								if (values.WZXH) {
									this.wzxh = values.WZXH;
								}
								if (values.CJXH) {
									this.cjxh = values.CJXH;
								}
								if (values.ZBLB) {
									this.zblb = values.ZBLB;
								}
								if (values.GHDW) {
									this.ghdw = values.GHDW
								}

							}
						}
					}
				})