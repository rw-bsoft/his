$package("phis.application.twr.script");

$import("phis.script.TableForm");

phis.application.twr.script.DRHospitalHZDJForm = function(cfg) {
	cfg.showButtonOnTop = true;
	this.colCount = cfg.colCount = 4;
	phis.application.twr.script.DRHospitalHZDJForm.superclass.constructor
			.apply(this, [ cfg ]);
}
Ext.extend(phis.application.twr.script.DRHospitalHZDJForm,
		phis.script.TableForm, {
			loadData : function() {
				var form = this.form.getForm();
				var brinfo = phis.script.rmi.miniJsonRequestSync({
					serviceId : "referralService",
					serviceAction : "queryBrInfo",
					"EMPIID" : this.exContext.ids.empiId
				});
				var br = brinfo.json.body;
				if (form) {
					if (br.MZHM) {
						this.mzhm = br.MZHM;
					}
					if (br.BRXM) {
						form.findField("BRXM").setValue(br.BRXM);
					}
					if (br.ZDXH) {
						this.zzzd = br.ZDXH;
					}
					if (br.ZDMC) {
						form.findField("MSZD").setValue(br.ZDMC);
					}
					if (br.BRXB) {
						this.brxb = br.BRXB;
						if (br.BRXB == 0) {
							form.findField("BRXB").setValue("未知的性别");
						} else if (br.BRXB == 1) {
							form.findField("BRXB").setValue("男");
						} else if (br.BRXB == 2) {
							form.findField("BRXB").setValue("女");
						} else if (br.BRXB == 9) {
							form.findField("BRXB").setValue("未说明的性别");
						}
					}
					if (br.AGE) {
						form.findField("NL").setValue(br.AGE);
					}
					if (br.SFZH) {
						form.findField("SFZH").setValue(br.SFZH);
					}
					if (br.LXDH) {
						form.findField("LXDH").setValue(br.LXDH);
					}
					if (br.LXDZ) {
						form.findField("LXDZ").setValue(br.LXDZ);
					}
					if (br.CSNY) {
						this.csny = br.CSNY;
					}
				}
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
					root : 'disease',
					totalProperty : 'count',
					id : 'mdssearch_a'
				}, [ {
					name : 'numKey'
				}, {
					name : 'JBXH'
				}, {
					name : 'MSZD'

				}, {
					name : 'JBBM'

				}, {
					name : 'JBPB'

				}, {
					name : 'JBPB_text'

				} ]);
			},
			createRemoteDicField : function(it) {
				var mds_reader = this.getRemoteDicReader();
				// store远程url
				var remoteUrl = 'MedicalDiagnosisZdlr'
				var remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{MSZD}</td></td>';
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
				form.findField("MSZD").setValue(r.get("MSZD"));
				this.zzzd = r.get("JBXH");
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
			doSubmit(){
				var empiId=this.exContext.ids.empiId
				var mzhm=this.mzhm;
				var zdxh=this.zzzd;
				var form = this.form.getForm();
				var zcjg=form.findField("ZCJG").getValue();
				var jgdh=form.findField("JGDH").getValue();
				var zcys=form.findField("ZCYS").getValue();
				var ysdh=form.findField("YSDH").getValue();
				var zljg=form.findField("ZLJG").getValue();
				var zlgc=form.findField("ZLGC").getValue();
				var xybzlfahyj=form.findField("XYBZLFAHYJ").getValue();
				var clqkxx=form.findField("CLQKXX").getValue();
				this.form.el.mask("正在保存数据...", "x-mask-loading");
				phis.script.rmi.jsonRequest({
				     serviceId : "referralService",
				     serviceAction : "saveHZDJ",
					 body : {
						 "EMPIID":empiId+"",
						 "MZHM":mzhm+"",
						 "ZCJG":zcjg+"",
						 "JGDH":jgdh+"",
						 "ZCYS":zcys+"",
						 "YSDH":ysdh+"",
						 "ZDXH":zdxh+"",
						 "ZLJG":zljg+"",
						 "ZLGC":zlgc+"",
						 "XYBZLFAHYJ":xybzlfahyj+"",
						 "CLQKXX":clqkxx+""
					}
				 }, function(code, msg, json) {
				 this.form.el.unmask()
				 if (code > 300) {
				 MyMessageTip.msg("提示", msg, true)
				 return
				 }else{
				 MyMessageTip.msg("提示", "保存成功!", true)
				 }
				}, this)
			}
		})