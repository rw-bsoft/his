$package("phis.application.fsb.script")

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")

phis.application.fsb.script.FsbCostAccountingForm = function(cfg) {
	phis.application.fsb.script.FsbCostAccountingForm.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.fsb.script.FsbCostAccountingForm,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}

				this.form = new Ext.FormPanel({
					labelWidth : 57, // label settings here cascade
					// unless overridden
					frame : true,
					autoScroll : true,
					// bodyStyle : 'padding:5px 5px 0',
					// autoHeight : true,
					// tbar : this.createButtons(),
					items : [{
						xtype : "label",
						html : "<br/><div style='font-size:20px;font-weight:bold;text-align:center;letter-spacing:10px' >费用记账联</div><br/>"
					}, {
						xtype : "label",
						html : "<div style='font-weight:bolder;text-align:center' >"
								+ this.mainApp.dept + "</div><br/>"
					}, {
						xtype : 'fieldset',
						title : '病人信息',
						// collapsible : true,
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 2,
							tableAttrs : {
								border : 0,
								cellpadding : '2',
								cellspacing : "2"
							}
						},
						defaultType : 'textfield',
						items : this.getItems('BRXX')
					}, {
						xtype : 'fieldset',
						title : '费用信息',
						// collapsible : true,
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 2,
							tableAttrs : {
								border : 0,
								cellpadding : '2',
								cellspacing : "2"
							}
						},
						defaultType : 'textfield',
						items : this.getItems('FYXX')
					}, {
						xtype : "label",
						html : "<div style='align:right;float:right;visibility: hidden'>记账</div><div style='align:right;float:right;color:red'>"
								+ this.mainApp.uname
								+ "</div><div style='align:right;float:right'>记账：</div>"

					}],
					tbar : (this.tbar || []).concat(this.createButtons())
				});
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				this.form.on("afterrender", this.onReady, this)
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
				return this.form
			},

			getItems : function(para) {
				var ac = util.Accredit;
				var MyItems = [];
				var schema = null;
				var re = util.schema.loadSync(this.entryName)
				if (re.code == 200) {
					schema = re.schema;
				} else {
					this.processReturnMsg(re.code, re.msg, this.initPanel)
					return;
				}
				var items = schema.items
				for (var i = 0; i < items.length; i++) {
					var it = items[i]
					if (!it.layout || it.layout != para) {
						continue;
					}
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						// alert(it.acValue);
						continue;
					}
					var f = this.createField(it)
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "100%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					MyItems.push(f);
				}
				return MyItems;
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mds',
							totalProperty : 'count',
							id : 'mdssearch'
						}, [{
									name : 'FYXH'
								}, {
									name : 'FYMC'
								}, {
									name : 'BFGG'
								}, {
									name : 'FYDW'
								}, {
									name : 'FYDJ'
								}, {
									name : 'CDMC'
								}, {
									name : 'FYGB'
								}, {
									name : 'KSMC'
								}, {
									name : 'FYKS'
								}, {
									name : 'BFBZ'
								}, {
									name : 'ZXBZ'
								}, {
									name : 'TYPE'
								}, {
									name : 'YPCD'
								}, {
									name : 'isZT'
								}]);
			},
			createRemoteDicField : function(it) {
				var this_ = this;
				var mds_reader = this.getRemoteDicReader();
				var url = ClassLoader.serverAppUrl || "";
				this.remoteUrl = 'ChargeDetailsAll';
				this.comboJsonData = {
					serviceId : "phis.searchService",
					serviceAction : "loadDicData",
					method : "execute",
					className : this.remoteUrl
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
				// store远程url
				proxy.on("loadexception", function(proxy, o, response, arg, e) {
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
				Ext.apply(this.remoteDicStore.baseParams, this.queryParams);
				var remoteTpl = '<td width="18px" style="background-color:#deecfd">{#}.</td><td width="150px">{FYMC}</td><td width="60px">{BFGG}</td><td width="70px">{FYDJ}</td><td width="30px">{FYDW}</td><td width="100px">{CDMC}</td>';
				var resultTpl = new Ext.XTemplate(
						'<tpl for=".">',
						'<div class="search-item">',
						'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
						'<tr>' + remoteTpl + '<tr>', '</table>', '</div>',
						'</tpl>');
				var _ctx = this;
				var remoteField = new Ext.form.ComboBox({
							name : it.id,
							fieldLabel : it.alias,
							width : 500,
							listWidth : 470,
							store : mdsstore,
							selectOnFocus : true,
							typeAhead : false,
							loadingText : '搜索中...',
							pageSize : 10,
							hideTrigger : true,
							// minListWidth : 400,
							tpl : resultTpl,
							minChars : 2,
							enableKeyEvents : true,
							lazyInit : false,
							itemSelector : 'div.search-item',
							onSelect : function(record) { // override default
								this.fymx_Info = mdsstore;
								this.selectedRecord = record;
								_ctx.setBackInfo(this, record);
							}
						});
				remoteField.on("focus", function() {
							remoteField.innerList.setStyle('overflow-y',
									'hidden');
						}, this);
				remoteField.on("keydown", function(obj, e) {// 实现数字键导航
							var key = e.getKey();
							if (key == e.ENTER) {
								if (!obj.isExpanded()) {
									// 是否是字母
									var patrn = /^[a-zA-Z.]+$/;
									if (patrn.exec(obj.getValue())) {
										// 防止查询不出数据或者按回车速度过快导致上次输入结果直接被调入
										obj.getStore().removeAll();
										obj.lastQuery = "";
										if (obj.doQuery(obj.getValue(), true) !== false) {
											e.stopEvent();
											return;
										}
									}
								}
								_ctx.focusFieldAfter(obj.index);
								return;
							}
							if ((key >= 48 && key <= 57)
									|| (key >= 96 && key <= 105)) {
								if (obj.isExpanded()) {
									if (key == 48 || key == 96)
										key = key + 10;
									key = key < 59 ? key - 49 : key - 97;
									var record = this.getStore().getAt(key);
									_ctx.setBackInfo(obj, record);
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
						Ext.apply(_ctx.comboJsonData, mdsstore.baseParams);
						options = Ext.apply({}, options);
						this.storeOptions(options);
						if (this.sortInfo && this.remoteSort) {
							var pn = this.paramNames;
							options.params = Ext.apply({}, options.params);
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
							if (!this.opener.FYLR) {
								MyMessageTip.msg("提示", "请先调入病人信息!", true)
								return false;
							}
							this.comboJsonData.query = qe.query;
							return true;
						}, this);
				remoteField.postBlur = function() {
					Ext.form.ComboBox.superclass.postBlur.call(this);
					this.collapse();
					this.inKeyMode = false;
					if (this_.opener.FYLR.FYMC) {
						this.setValue(this_.opener.FYLR.FYMC)
					}
				}
				remoteField.isSearchField = true;
				this.remoteDic = remoteField;
				remoteField.disabled = true
				return remoteField
			},
			setBackInfo : function(obj, r) {
				this.opener.flag = 1;
				obj.collapse();
				if (this.querying) {
					return;
				}
				this.data = this.opener.FYLR;
				Ext.apply(this.data, r.data);
				this.querying = true
				this.form.el.mask("正在查询数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "fsbCostProcessingService",
							serviceAction : "queryCost",
							body : this.data
						}, function(code, msg, json) {
							this.form.el.unmask()
							if (code > 300) {
								this.processReturnMsg(code, msg);
								return
							}
							this.querying = false
							if (this.data.isZT == 1) {
								// 项目组套
								if(json.Errmsg){
									Ext.Msg.alert("提示",json.Errmsg);
									return;
								}
								this.opener.XMXX = json;
								this.opener.FYLR.fyList = json.fyList;
								var form = this.form.getForm();
								obj.setValue(r.get("FYMC"));
								form.findField("FYDJ").setValue(this.opener
										.number_TO_stringFormat(
												json.total + "", 4));
								this.opener.FYLR.fyList.FYDJ = this.opener
								.number_TO_stringFormat(
										json.total + "", 4);
								if (json.total != 0) {
									form.findField("FYDJ").setDisabled(true);
								}
								if (json.total == 0) {
									form.findField("FYDJ").setDisabled(false);
								}
								if (json.fyList[0].FYKS) {
									form.findField("ZXKS")
											.setValue(json.fyList[0].FYKS);
								}
//								this.opener.FYLR.FYKS = form.findField("BRKS")
//										.getValue();
								form.findField("ZFBL").setValue("");
								if (!form.findField("FYRQ").getValue()) {
									form.findField("FYRQ").setValue(new Date());
								}
								form.findField("FYSL").setDisabled(false);
								form.findField("FYSL").focus(true, 200);
								form.findField("ZXKS").setDisabled(false);
								if (!form.findField("YSGH").getValue()) {
									form.findField("YSGH").setDisabled(false);
								}
								form.findField("FYRQ").setDisabled(false);
								this.opener.FYLR_BACKUP = this.opener
										.clone(this.opener.FYLR);
								this.opener.FYLR_BACKUP.ZXKS = form
										.findField("ZXKS").getValue();
								this.opener.FYLR_BACKUP.YSGH = form
										.findField("YSGH").getValue();
							} else {
								this.opener.XMXX = json.body;
								this.opener.FYLR.FYXH = this.data.FYXH
								if (this.data.FYGB) {
									this.opener.FYLR.FYXM = this.data.FYGB
								} else {
									this.opener.FYLR.FYXM = json.body.FYGB
								}
								if (this.data.TYPE) {
									this.opener.FYLR.YPLX = this.data.TYPE
									this.opener.FYLR.TYPE = this.data.TYPE
								} else {
									this.opener.FYLR.YPLX = 0;
									this.opener.FYLR.TYPE = 0;
								}
								if (this.data.YPCD) {
									this.opener.FYLR.YPCD = this.data.YPCD
								}
								this.opener.FYLR.FYDJ = this.data.FYDJ
								// if (this.data.FYKS) {
								// this.opener.FYLR.FYKS = this.data.FYKS
								// }
								this.opener.FYLR.ZFBL = json.body.ZFBL
								var form = this.form.getForm();
								var fymc = r.get("FYMC")
										+ (r.get("BFGG")
												? "/" + r.get("BFGG")
												: "")
										+ (r.get("FYDW")
												? "/" + r.get("FYDW")
												: "")
								obj.setValue(fymc);
								this.opener.FYLR.FYMC = fymc;
								form.findField("FYDJ").setValue(this.opener
										.number_TO_stringFormat(r.get("FYDJ")
														+ "", 4));
								if (r.get("FYDJ") != 0) {
									form.findField("FYDJ").setDisabled(true);
								}
								if (r.get("FYDJ") == 0) {
									form.findField("FYDJ").setDisabled(false);
								}
								if (r.get("FYKS")) {
									form.findField("ZXKS").setValue(r
											.get("FYKS"));
								}
								form.findField("ZFBL").setValue(this.opener
										.number_TO_stringFormat(json.body.ZFBL
														+ "", 3));
								if (!form.findField("FYRQ").getValue()) {
									form.findField("FYRQ").setValue(new Date());
								}
								form.findField("FYSL").setDisabled(false);
								form.findField("FYSL").focus(true, 200);
								form.findField("ZXKS").setDisabled(false);
								if (!form.findField("YSGH").getValue()) {
									form.findField("YSGH").setDisabled(false);
								}
								form.findField("FYRQ").setDisabled(false);
								this.opener.FYLR_BACKUP = this.opener
										.clone(this.opener.FYLR);
								this.opener.FYLR_BACKUP.ZXKS = form
										.findField("ZXKS").getValue();
								this.opener.FYLR_BACKUP.YSGH = form
										.findField("YSGH").getValue();
							}
						}, this)// jsonRequest
			},
			// doReSet : function() {
			// this.fireEvent("reSet", this);
			// },
			doSave : function() {
				this.opener.doVerification();
			},
			doReSet : function() {
				this.doNew();
				// 清空缓存信息
				this.opener.FYLR = null;
				this.opener.XMXX = null;
				this.data = null;
				this.fireEvent("reSet", this.form)
			},
			createField : function(it) {
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				// alert(this.defaultHeight || it.height)
				var cfg = {
					name : it.id,
					fieldLabel : it.alias,
					xtype : it.xtype || "textfield",
					vtype : it.vtype,
					width : defaultWidth,
					height : this.defaultHeight || it.height,
					value : it.defaultValue,
					validator : function(str) {
						if (it.id == "FYSL"
								&& (isNaN(str) || str > 99999999.99 || str == 0)) {
							return '该输入项应为0至99999999.99之间的值,不包含0 ！';
						}
						if (it.id == "FYDJ"
								&& (isNaN(str) || str > 999999.9999 || str <= 0)) {
							return '该输入项应为0至999999.9999之间的值, 不包含0 ！';
						}
						return true;
					},
					enableKeyEvents : it.enableKeyEvents,
					validationEvent : it.validationEvent
				}
				if (it.xtype == "checkbox") {
					cfg.fieldLabel = null;
					cfg.boxLabel = it.alias;
				}

				if (it.xtype == "uxspinner") {
					cfg.strategy = {}
					cfg.strategy.xtype = it.spin_xtype;
					// alert(Ext.encode(it));
				}
				cfg.listeners = {
					specialkey : this.onFieldSpecialkey,
					// add by liyl 2012-06-17 去掉输入字符串首位空格
					blur : function(e) {
						if (typeof(e.getValue()) == 'string') {
							e.setValue(e.getValue().trim())
						}
					},
					scope : this
				}

				if (it.inputType) {
					cfg.inputType = it.inputType
				}
				if (it.editable) {
					cfg.editable = true
				}
				if (it['not-null'] && !it.fixed) {
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
				if (it.emptyText) {
					cfg.emptyText = it.emptyText;
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
				if (this.hideTrigger && cfg.disabled == true) {
					cfg.hideTrigger = true;
					cfg.emptyText = null;
				}
				// add by yangl,modify simple code Generation methods
				if (it.codeType) {
					if (!this.CodeFieldSet)
						this.CodeFieldSet = [];
					this.CodeFieldSet.push([it.target, it.codeType, it.id]);
				}
				if (it.properties && it.properties.mode == "remote") {
					// 默认实现药品搜索，若要实现其他搜索，重写createRemoteDicField和setMedicInfo方法
					return this.createRemoteDicField(it);
				} else if (it.dic) {
					// add by lyl, check treecheck length
					if (it.dic.render == "TreeCheck") {
						if (it.length) {
							cfg.maxLength = it.length;
						}
					}

					it.dic.src = this.entryName + "." + it.id
					it.dic.defaultValue = it.defaultValue
					it.dic.width = defaultWidth
					if (it.dic.fields) {
						if (typeof(it.dic.fields) == 'string') {
							var fieldsArray = it.dic.fields.split(",")
							it.dic.fields = fieldsArray;
						}
					}
					var combox = this.createDicField(it.dic)
					Ext.apply(combox, cfg)
					combox.on("specialkey", this.onFieldSpecialkey, this)
					return combox;
				}

				if (it.length) {
					cfg.maxLength = it.length;
				}
				if (it.xtype) {
					return cfg;
				}
				switch (it.type) {
					case 'int' :
					case 'double' :
					case 'bigDecimal' :
						cfg.xtype = "numberfield"
						cfg.style = "color:#00AA00;font-weight:bold;text-align:right";
						if (it.type == 'int') {
							cfg.decimalPrecision = 0;
							cfg.allowDecimals = false
						} else {
							cfg.decimalPrecision = it.precision || 2;
						}
						if (it.minValue) {
							cfg.minValue = it.minValue;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						break;
					case 'date' :
						cfg.xtype = 'datefield';
						cfg.emptyText = "请选择日期";
						cfg.format = 'Y-m-d';
						if (it.minValue) {
							cfg.minValue = it.minValue;
						}
						if (it.maxValue) {
							cfg.maxValue = it.maxValue;
						}
						break;
					case 'text' :
						cfg.xtype = "htmleditor"
						cfg.enableSourceEdit = false
						cfg.enableLinks = false
						cfg.width = it.width || 700
						cfg.height = it.height || 300
						if (this.plugins)
							this.getPlugins(cfg);
						break;
					case 'label' :
						cfg.id = it.id;
						cfg.xtype = "label"
						cfg.width = it.width || 700
						cfg.height = it.height || 300
						// cfg.html = "<span style='color:red'>测试</span>"
						break;
				}
				return cfg;
			}
		});