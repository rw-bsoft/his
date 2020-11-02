$package("phis.application.war.script")

$import("phis.script.TableForm"/* , "util.widgets.ModuleQueryField" */,
		"phis.script.util.DateUtil")

phis.application.war.script.MedicalRecordsQueryNotReviewForm = function(cfg) {
	cfg.colCount = 5;
	cfg.remoteUrl = 'MedicalDiagnosis';
	cfg.remoteTpl = '<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{MSZD}</td></td>';
	phis.application.war.script.MedicalRecordsQueryNotReviewForm.superclass.constructor
			.apply(this, [cfg]);
	this.fldDefaultWidth = 80;
	this.labelWidth = 60;
	this.on("doNew", this.afterDoNew, this);
}

Ext.extend(phis.application.war.script.MedicalRecordsQueryNotReviewForm,
		phis.script.TableForm, {
			onReady : function() {
				phis.application.war.script.MedicalRecordsQueryNotReviewForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var BLLB = form.findField("BLLB");
				BLLB.on("select", function(f) {
							var value = f.getValue();
							var MBBH = form.findField("MBBH");
							if (value == "2000001") {
								MBBH.disable();
							} else {
								MBBH.enable();
							}
						}, this);
			},
			doCancel : function() {
				this.fireEvent("cancel", this)
			},
			doAddCondition : function() {
				var cList = this.createModule("conditionList",
						this.conditionList);
				cList.on("listDblClick", this.onListDblClick, this);
				var c_win = cList.getWin();
				c_win.add(cList.initPanel());
				c_win.setHeight(550);
				c_win.setWidth(240);
				c_win.setPosition(2000, 100);
				this.c_win = c_win;
				c_win.show();
			},
			onListDblClick : function(record, schema) {
				var data = record.data;
				var formData = this.castListDataToForm(data, schema);
				this.doNew();
				this.initFormData(formData, schema);
			},
			initFormData : function(data, schema) {
				Ext.apply(this.data, data)
				this.initDataId = this.data[schema.pkey]
				var form = this.form.getForm()
				var items = schema.items
				var n = items.length;
				var XTSJBOX = form.findField("XTSJBOX");
				var JLSJBOX = form.findField("JLSJBOX");
				var WCSJBOX = form.findField("WCSJBOX");
				if (data.XTSJKS || data.XTSJJS) {
					data.XTSJKS = data.XTSJKS || "";
					data.XTSJJS = data.XTSJJS || "";
					XTSJBOX.setValue(true);
				} else {
					XTSJBOX.setValue(false);
				}
				if (data.JLSJKS || data.JLSJJS) {
					data.JLSJKS = data.JLSJKS || "";
					data.JLSJJS = data.JLSJJS || "";
					JLSJBOX.setValue(true);
				} else {
					JLSJBOX.setValue(false);
				}
				if (data.WCSJKS || data.WCSJJS) {
					data.WCSJKS = data.WCSJKS || "";
					data.WCSJJS = data.WCSJJS || "";
					WCSJBOX.setValue(true);
				} else {
					WCSJBOX.setValue(false);
				}
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						var v = data[it.id];
						if (v != undefined) {
							if (f.getXType() == "checkbox") {
								var setValue = "";
								if (it.checkValue
										&& it.checkValue.indexOf(",") > -1) {
									var c = it.checkValue.split(",");
									checkValue = c[0];
									unCheckValue = c[1];
									if (v == checkValue) {
										setValue = true;
									} else if (v == unCheckValue) {
										setValue = false;
									}
								}
								if (setValue == "") {
									if (v == 1) {
										setValue = true;
									} else {
										setValue = false;
									}
								}
								f.setValue(setValue);
							} else {
								if (it.dic && v !== "" && v === 0) {// add by
									// yangl
									// 解决字典类型值为0(int)时无法设置的BUG
									v = "0";
								}
								f.setValue(v)
								if (it.dic && v != "0" && f.getValue() != v) {
									f.counter = 1;
									this.setValueAgain(f, v, it);
								}

							}
						}
						if (it.update == "false") {
							f.disable();
						}
					}
					this.setKeyReadOnly(true)
					this.focusFieldAfter(-1, 800)
				}
				var MBBH = form.findField("MBBH");
				MBBH.setValue(data.MBMC);
				if (data.MBBH) {
					this.MBBH = data.MBBH;
				}
			},
			/**
			 * 将list数据转换为form数据
			 * 
			 * @param {}
			 *            data
			 * @return {}
			 */
			castListDataToForm : function(data, schema) {
				var formData = {};
				var items = schema.items;
				var n = items.length;
				for (var i = 0; i < n; i++) {
					var it = items[i];
					var key = it.id;
					if (it.dic) {
						var dicData = {
							"key" : data[key],
							"text" : data[key + "_text"]
						};
						formData[key] = dicData;
					} else {
						formData[key] = data[key];
					}
				}
				Ext.applyIf(formData, data)
				return formData;
			},
			doSaveCondition : function() {
				var formData = this.getFormData();
				formData = this.getKSJSTime(formData);
				var module = this.createModule("conditionForm",
						this.conditionForm);
				module.initData = formData;
				module.doNew();
				var n_win = module.getWin();
				n_win.setWidth(250);
				n_win.setHeight(100);
				n_win.show();
			},
			doSelect : function() {
				var ac = util.Accredit;
				var form = this.form.getForm();
				if (!this.validate()) {
					return;
				}
				if (!this.schema) {
					return;
				}
				var items = this.schema.items;
				var cnd = ['eq', ['$', 'SYBZ'], ['s', '0']];
				if (items) {
					var n = items.length;
					for (var i = 0; i < n; i++) {
						var it = items[i];
						if ((it.display == 0 || it.display == 1)
								|| !ac.canRead(it.acValue)) {
							continue;
						}
						if (it.xtype == "combination") {
							continue;
						}
						var v = "";
						var f = form.findField(it.id);
						if (f) {
							v = f.getValue() || "";
							// if (f.getValue() == 0 && it.id == "BLZT") {
							// v = "0";
							// }
						}
						if (v != null & v != "" & v != undefined) {
							var refAlias = it.refAlias || "a";
							var value = refAlias + "." + it.id;
							if (it.id == "MBBH") {
								if (v) {
									var cnds = ['eq', ['$', value],
											['s', this.MBBH]];
									cnd = ['and', cnd, cnds];
								}
								continue;
							}
							if (it.id == "BLZT") {
								if (v && v == "5") {
									var cnds = ['ne', ['$', value], ['s', '9']];
									cnd = ['and', cnd, cnds];
									continue;
								} else if (!v) {
									continue;
								}
							}
							var cnds = ['eq', ['$', value]];
							if (it.dic) {
								if (it.dic.render == "Tree") {
									var node = f.selectedNode;
									if (node != null && !node.isLeaf()) {
										cnds[0] = 'like';
										cnds.push(['s', v + '%']);
									} else {
										cnds.push(['s', v]);
									}
								} else {
									cnds.push(['s', v]);
								}
							} else {
								switch (it.type) {
									case 'long' :
										cnds.push(['s', v]);
										break;
									case 'int' :
										cnds.push(['i', v]);
										break;
									case 'double' :
									case 'bigDecimal' :
										cnds.push(['d', v]);
										break;
									case 'string' :
										cnds[0] = 'like';
										if (it.id == "BRZD") {
											cnds.push(['s', '%' + v + '%']);
										} else {
											cnds.push(['s', v + '%']);
										}
										break;
									case "date" :
										v = v.format("Y-m-d");
										cnds[1] = [
												'$',
												"str(" + value
														+ ",'yyyy-MM-dd')"];
										cnds.push(['s', v]);
										break;
								}
							}
							cnd = ['and', cnd, cnds];
						}
					}
				}
				cnd = this.addCndByCombination(cnd, form);
				this.fireEvent("select", cnd);
				this.focusFieldAfter(-1, 800)
			},

			createRemoteDicField : function(it) {
				var mds_reader = this.getRemoteDicReader();
				// store远程url
				var mdsstore = new Ext.data.Store({
							url : ClassLoader.appRootOffsetPath
									+ this.remoteUrl + '.search?ZXLB=1',
							reader : mds_reader
						});

				this.remoteDicStore = mdsstore;
				Ext.apply(this.remoteDicStore.baseParams, this.queryParams);
				var resultTpl = new Ext.XTemplate(
						'<tpl for=".">',
						'<div class="search-item">',
						'<table cellpadding="0" cellspacing="0" border="0" class="search-item-table">',
						'<tr>' + this.remoteTpl + '<tr>', '</table>', '</div>',
						'</tpl>');
				var _ctx = this;
				var remoteField = new Ext.form.ComboBox({
							// id : "YPMC",
							name : it.id,
							index : it.index,
							fieldLabel : it.alias,
							enableKeyEvents : it.enableKeyEvents,
							listWidth : 270,
							store : mdsstore,
							selectOnFocus : true,
							typeAhead : false,
							loadingText : '搜索中...',
							pageSize : 10,
							hideTrigger : true,
							minListWidth : this.minListWidth || 280,
							tpl : resultTpl,
							minChars : 1,
							lazyInit : false,
							boxMaxWidth : 120,
							itemSelector : 'div.search-item',
							onSelect : function(record) { // override default
								// onSelect
								// to do
								this.bySelect = true;
								_ctx.setBackInfo(this, record);
							}
						});
				remoteField.on("focus", function() {
							remoteField.innerList.setStyle('overflow-y',
									'hidden');
						}, this);
				remoteField.on("keyup", function(obj, e) {// 实现数字键导航
							var key = e.getKey();
							if ((key >= 48 && key <= 57)
									|| (key >= 96 && key <= 105)) {
								var searchTypeValue = _ctx.cookie
										.getCookie(_ctx.mainApp.uid
												+ "_searchType");
								if (searchTypeValue != 'BHDM') {
									if (obj.isExpanded()) {
										if (key == 48 || key == 96)
											key = key + 10;
										key = key < 59 ? key - 49 : key - 97;
										var record = this.getStore().getAt(key);
										obj.bySelect = true;
										_ctx.setBackInfo(obj, record);
									}
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
				remoteField.isSearchField = true;
				remoteField.on("beforequery", function() {
							return this.beforeSearchQuery();
						}, this);
				this.remoteDic = remoteField;
				return remoteField
			},
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'disease',
							totalProperty : 'count',
							id : 'mdssearch_a'
						}, [{
									name : 'numKey'
								}, {
									name : 'JBXH'
								}, {
									name : 'MSZD'

								}, {
									name : 'JBBM'
								}]);
			},
			beforeSearchQuery : function() {
				return true;
			},
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中，继承后实现具体功能
				obj.collapse();
				obj.setValue(record.get("MSZD"));
			},
			getKSJSTime : function(formData) {
				var form = this.form.getForm();
				var XTSJBOX = form.findField("XTSJBOX");
				if (XTSJBOX.getValue()) {
					var XTSJKS = form.findField("XTSJKS");
					var XTSJJS = form.findField("XTSJJS");
					var kssj = XTSJKS.getValue();
					var jssj = XTSJJS.getValue();
					formData.XTSJKS = kssj;
					formData.XTSJJS = jssj;
				}
				var JLSJBOX = form.findField("JLSJBOX");
				if (JLSJBOX.getValue()) {
					var JLSJKS = form.findField("JLSJKS");
					var JLSJJS = form.findField("JLSJJS");
					var kssj = JLSJKS.getValue();
					var jssj = JLSJJS.getValue();
					formData.JLSJKS = kssj;
					formData.JLSJJS = jssj;
				}
				var WCSJBOX = form.findField("WCSJBOX");
				if (WCSJBOX.getValue()) {
					var WCSJKS = form.findField("WCSJKS");
					var WCSJJS = form.findField("WCSJJS");
					var kssj = WCSJKS.getValue();
					var jssj = WCSJJS.getValue();
					formData.WCSJKS = kssj;
					formData.WCSJJS = jssj;
				}
				var MBBH = form.findField("MBBH");
				if (MBBH.getValue()) {
					var mbmc = MBBH.getRawValue();
					formData.MBMC = mbmc;
					formData.MBBH = this.MBBH;
				}
				return formData;
			},

			addCndByCombination : function(cnd, form) {
				var XTSJBOX = form.findField("XTSJBOX");
				if (XTSJBOX.getValue()) {
					var XTSJKS = form.findField("XTSJKS");
					var XTSJJS = form.findField("XTSJJS");
					var kssj = XTSJKS.getValue();
					var jssj = XTSJJS.getValue();
					var c = this.getCnd(kssj, jssj, "XTSJ");
					cnd = ['and', cnd, c];
				}
				var JLSJBOX = form.findField("JLSJBOX");
				if (JLSJBOX.getValue()) {
					var JLSJKS = form.findField("JLSJKS");
					var JLSJJS = form.findField("JLSJJS");
					var kssj = JLSJKS.getValue();
					var jssj = JLSJJS.getValue();
					var c = this.getCnd(kssj, jssj, "JLSJ");
					cnd = ['and', cnd, c];
				}
				var WCSJBOX = form.findField("WCSJBOX");
				if (WCSJBOX.getValue()) {
					var WCSJKS = form.findField("WCSJKS");
					var WCSJJS = form.findField("WCSJJS");
					var kssj = WCSJKS.getValue();
					var jssj = WCSJJS.getValue();
					var c = this.getCnd(kssj, jssj, "WCSJ");
					cnd = ['and', cnd, c];
				}
				return cnd;
			},
			getCnd : function(kssj, jssj, fieldName) {
				if (kssj) {
					kssj = kssj.format("Y-m-d");
					kssj = kssj + " 00:00:00";
				} else {
					kssj = new Date();
					kssj = kssj.format("Y-m-d") + " 00:00:00";
				}
				if (jssj) {
					jssj = jssj.format("Y-m-d");
					jssj = jssj + " 23:59:59";
				} else {
					jssj = new Date();
					jssj = jssj.format("Y-m-d") + " 23:59:59";
				}
				var cnd = [
						'and',
						[
								'ge',
								['$', fieldName],
								['todate', ['s', kssj],
										['s', 'yyyy-mm-dd hh24:mi:ss']]],
						[
								'le',
								['$', fieldName],
								['todate', ['s', jssj],
										['s', 'yyyy-mm-dd hh24:mi:ss']]]];
				return cnd;
			},
			getComFieldId : function() {
				var fields = ["XTSJBOX", "XTSJKS", "XTSJJS", "JLSJBOX",
						"JLSJKS", "JLSJJS", "WCSJBOX", "WCSJKS", "WCSJJS"];
				return fields
			},

			doReset : function() {
				this.doNew();
			},
			afterDoNew : function() {
				var form = this.form.getForm();
				var MBBH = form.findField("MBBH");
				MBBH.setValue();
				this.MBBH = null;
				var fs = this.getComFieldId();
				for (var i = 0; i < fs.length; i++) {
					var fd = fs[i];
					var f = form.findField(fd);
					if (fd.indexOf("BOX") > 0) {
						f.setValue();
						f.enable();
					} else {
						f.setValue();
						f.disable();
					}
				}
			},

			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
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
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var items = schema.items;
				if (!this.fireEvent("changeDic", items)) {
					return
				}
				var colCount = this.colCount;

				var table = {
					layout : 'tableform',
					layoutConfig : {
						columns : colCount,
						tableAttrs : {
							border : 0,
							cellpadding : '2',
							cellspacing : "2"
						}
					},
					items : []
				}
				if (!this.autoFieldWidth) {
					var forceViewWidth = (defaultWidth + (this.labelWidth || 80))
							* (colCount - 1) + 350;
					table.layoutConfig.forceWidth = forceViewWidth
				}
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1)
							|| !ac.canRead(it.acValue)) {
						continue;
					}
					var f;
					if (it.type == "long") {
						it.type = "int";
					}
					if (it.id == "BRZD") {
						f = this.createRemoteDicField(it);
					} else if (it.xtype == "combination") {
						it.index = i;
						f = this.createCombinationField(it);
						table.items.push(f);
						continue;
					} else {
						f = this.createField(it)
					}
					f.labelSeparator = ":"
					f.index = i;
					f.anchor = it.anchor || "75%"
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)
					f.boxMaxWidth = 120
					if (!this.fireEvent("addfield", f, it)) {
						continue;
					}
					table.items.push(f)
				}

				var cfg = {
					buttonAlign : 'center',
					labelAlign : this.labelAlign || "left",
					labelWidth : this.labelWidth || 80,
					frame : true,
					shadow : false,
					border : false,
					collapsible : false,
					autoWidth : false,
					// autoHeight : true,
					autoScroll : true,
					floating : false
				}

				if (this.isCombined) {
					cfg.frame = true
					cfg.shadow = false
					// cfg.width = this.width
					cfg.height = this.height
				} else {
					// cfg.autoWidth = true
					cfg.autoHeight = true
				}
				if (this.disAutoHeight) {
					delete cfg.autoHeight;
				}
				this.initBars(cfg);
				Ext.apply(table, cfg)
				this.expansion(table);// add by yangl
				this.form = new Ext.FormPanel(table)
				this.form.on("afterrender", this.onReady, this)

				this.schema = schema;
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				var MBBH = this.form.getForm().findField("MBBH");
				MBBH.on("lookup", this.onQueryClick, this);
				return this.form
			},
			createCombinationField : function(it) {
				var checkbox = new Ext.form.Checkbox({
							xtype : "checkbox",
							id : it.id + "BOX",
							name : it.id + "BOX",
							hideLabel : true
						});
				checkbox.on("check", this.onBoxCheck, this);
				var label1 = new Ext.form.Label({
							xtype : "label",
							html : it.alias + ":",
							width : 60
						})
				var dateField1 = new Ext.form.DateField({
							xtype : "datefield",
							id : it.id + "KS",
							name : it.id + "KS",
							emptyText : "请选择日期",
							boxMinWidth : 80,
							format : 'Y-m-d'
						});
				dateField1.disable();
				var label2 = new Ext.form.Label({
							xtype : "label",
							text : "->",
							width : 20
						})
				var dateField2 = new Ext.form.DateField({
							xtype : "datefield",
							id : it.id + "JS",
							name : it.id + "JS",
							emptyText : "请选择日期",
							boxMinWidth : 80,
							format : 'Y-m-d'
						});
				dateField2.disable();
				var field = new Ext.form.CompositeField({
							xtype : 'compositefield',
							name : it.id,
							anchor : '-20',
							hideLabel : true,
							index : it.index,
							boxMinWidth : 330,
							items : [checkbox, label1, dateField1, label2,
									dateField2]
						});
				return field;
			},
			onBoxCheck : function(box, flag) {
				var form = this.form.getForm();;
				if (box.id == "XTSJBOX") {
					var XTSJKS = form.findField("XTSJKS");
					var XTSJJS = form.findField("XTSJJS");
					if (flag) {
						XTSJKS.setValue();
						XTSJKS.enable();
						XTSJJS.setValue();
						XTSJJS.enable();
					} else {
						XTSJKS.setValue();
						XTSJKS.disable();
						XTSJJS.setValue();
						XTSJJS.disable();
					}
				}
				if (box.id == "JLSJBOX") {
					var JLSJKS = form.findField("JLSJKS");
					var JLSJJS = form.findField("JLSJJS");
					if (flag) {
						JLSJKS.setValue();
						JLSJKS.enable();
						JLSJJS.setValue();
						JLSJJS.enable();
					} else {
						JLSJKS.setValue();
						JLSJKS.disable();
						JLSJJS.setValue();
						JLSJJS.disable();
					}
				}
				if (box.id == "WCSJBOX") {
					var WCSJKS = form.findField("WCSJKS");
					var WCSJJS = form.findField("WCSJJS");
					if (flag) {
						WCSJKS.setValue();
						WCSJKS.enable();
						WCSJJS.setValue();
						WCSJJS.enable();
					} else {
						WCSJKS.setValue();
						WCSJKS.disable();
						WCSJJS.setValue();
						WCSJJS.disable();
					}
				}
			},

			onQueryClick : function() {
				var form = this.form.getForm();
				var f = form.findField("BLLB");
				var value;
				if (f) {
					value = f.getValue();
				}
				if (!value) {
					Ext.Msg.alert("提示", "未选择病历类别，请选择病历类别！");
					return;
				}
				this.showEMRMode(value);

			},
			loadModuleCfg : function(id) {
				var result = phis.script.rmi.miniJsonRequestSync({
							url : 'app/loadModule',
							id : id
						})
				if (result.code != 200) {
					if (result.msg == "NotLogon") {
						this.mainApp.logon(this.loadModuleCfg, this, [id])
					}
					return null;
				}
				var m = result.json.body
				Ext.apply(m, m.properties)
				return m
			},
			showEMRMode : function(value) {
				var moduleCfg = this.loadModuleCfg(this.NotReviewEMRMode);
				var cfg = {
					showButtonOnTop : true,
					border : false,
					frame : false,
					autoLoadSchema : false,
					isCombined : true,
					exContext : {}
				};
				Ext.apply(cfg, moduleCfg);
				var cls = moduleCfg.script;
				if (!cls) {
					return;
				}
				$import(cls);
				var tplModule = eval("new " + cls + "(cfg)");
				tplModule.setMainApp(this.mainApp);
				tplModule.on("loadTemplate", this.onLoadTemplate, this);
				tplModule.BLLB = value;
				tplModule.opener = this;
				var em_win = tplModule.getWin();
				em_win.setWidth(900);
				em_win.setHeight(450);
				this.em_win = em_win;
				this.em_win.show();
			},

			onLoadTemplate : function(r) {
				var MBBH = r.get("MBBH");
				var MBMC = r.get("MBMC");
				var form = this.form.getForm();
				var f = form.findField("MBBH");
				f.setValue(MBMC);
				this.MBBH = MBBH;
			}
		});