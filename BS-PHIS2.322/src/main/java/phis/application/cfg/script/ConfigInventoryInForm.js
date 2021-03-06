$package("phis.application.cfg.script")

$import("phis.script.TableForm", "phis.script.util.DateUtil")

phis.application.cfg.script.ConfigInventoryInForm = function(cfg) {
	cfg.showButtonOnTop = false;
	cfg.colCount = 5;
	phis.application.cfg.script.ConfigInventoryInForm.superclass.constructor
			.apply(this, [ cfg ])
	this.wzxh = 0;
	this.cjxh = 0;
	this.zblb = 0;
}
Ext
		.extend(
				phis.application.cfg.script.ConfigInventoryInForm,
				phis.script.TableForm,
				{
					getRemoteDicReader : function() {
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
						},  {
							name : 'ZBLB'
						}, {
							name : 'WZJG'
						}, {
							name : 'LSJG'
						} ]);
					},
					createRemoteDicField : function(it) {
						var mds_reader = this.getRemoteDicReader();
						// store远程url
						var remoteUrl = 'Supplies'
						var remoteTpl = '<td width="18px" style="background-color:#deecfd">{#}.</td><td width="200px">{WZMC}</td><td width="60px">{WZGG}</td><td width="40px">{WZDW}</td><td width="200px">{CJMC}</td>';
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
							onSelect : function(record) { // override default
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
						remoteField.on("beforequery", function(qe) {
							this.comboJsonData.query = qe.query;
							return true;
						}, this);
						remoteField.isSearchField = true;
						this.remoteDic = remoteField;
						return remoteField
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
						this.operater.list.wzjg = r.get("WZJG");
						this.operater.list.lsjg = r.get("LSJG");
						this.operater.list.doCreate();
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
							serviceAction : "backfillingInventoryIn",
							jlxh : jlxh
						});
						var values = r.json.body;
						var form = this.form.getForm();
						form.findField("WZMC").setValue(values.WZMC);
						form.findField("WZGG").setValue(values.WZGG);
						form.findField("WZDW").setValue(values.WZDW);
						form.findField("KFXH").setValue(values.KFXH);
						form.findField("CJMC").setValue(values.CJMC);
						this.wzxh = values.WZXH;
						this.cjxh = values.CJXH;
						this.zblb = values.ZBLB;
					}
				})