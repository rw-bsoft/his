﻿/**
	 * 公共查询表单页面
	 * 
	 * @author : yaozh
	 */
$package("chis.application.mov.script.util")
$import("chis.script.BizTableFormView")
chis.application.mov.script.util.QueryForm = function(cfg) {
	cfg.actions = [{
				id : "select",
				name : "查询",
				iconCls : "common_query"
			}, {
				id : "reset",
				name : "重置",
				iconCls : "common_reset"
			}, {
				id : "cancel",
				name : "取消",
				iconCls : "common_cancel"
			}];
	chis.application.mov.script.util.QueryForm.superclass.constructor.apply(
			this, [cfg]);
	this.colCount = this.colCount || 2;
	this.on("addfield", this.onAddField, this)
};
Ext.extend(chis.application.mov.script.util.QueryForm,
		chis.script.BizTableFormView, {
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
				this.schema = schema;
				var ac = util.Accredit;
				var defaultWidth = this.fldDefaultWidth || 200
				var items = schema.items
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
							cellspacing : '2'
						}
					},
					items : []
				}
				if (!this.autoFieldWidth) {
					var forceViewWidth = (defaultWidth + (this.labelWidth || 60))
							* colCount
					table.layoutConfig.forceWidth = forceViewWidth
				}
				var size = items.length
				for (var i = 0; i < size; i++) {
					var it = items[i]
					if ((it.display == 0 || it.display == 1 || it.hidden == true)
							|| !ac.canRead(it.acValue)) {
						continue;
					}

					if ("form" == this.mainApp.exContext.areaGridShowType) {

						if (it.id == "regionCode") {
							var _ctr = this;
							var ff = new Ext.form.TriggerField({
								name : it.id,
								onTriggerClick : function() {
									_ctr.onRegionCodeClick()
								}, // 单击事件
								triggerClass : 'x-form-search-trigger', // 按钮样式
								// readOnly : true, //只读
								// disabled : true,
								fieldLabel : "<font  >网格地址:<font>",
								"width" : 180
									// 121 两个地方调用
								});
							this.ff = ff;
							// this.ff.allowBlank = false;
							// this.ff.invalidText = "必填字段";
							this.ff.regex = /(^\S+)/;
							this.ff.regexText = "前面不能有空格字符";
							table.items.push(ff)
							continue;

						}
						if (it.id == "homeAddress") {
							var _ctr = this;
							var ff = new Ext.form.TriggerField({
								name : it.id,
								onTriggerClick : function() {
									_ctr.onRegionCodeClick()
								}, // 单击事件
								triggerClass : 'x-form-search-trigger', // 按钮样式
								// readOnly : true, //只读
								// disabled : true,
								fieldLabel : "<font  >户籍地址:<font>",
								"width" : 180
									// 121 两个地方调用
								});
							this.ff = ff;
							// this.ff.allowBlank = false;
							// this.ff.invalidText = "必填字段";
							this.ff.regex = /(^\S+)/;
							this.ff.regexText = "前面不能有空格字符";
							table.items.push(ff)
							continue;

						}

					}
					var f = this.createField(it)
					f.index = i;
					if ("form" == this.mainApp.exContext.areaGridShowType) {
						f.anchor = it.anchor || "90%"
					} else {
						f.anchor = it.anchor || "100%"
					}
					delete f.width

					f.colspan = parseInt(it.colspan)
					f.rowspan = parseInt(it.rowspan)

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
					autoWidth : true,
					autoHeight : true,
					floating : false
				}
				if (this.isCombined) {
					cfg.frame = true
					cfg.shadow = false
					cfg.width = this.width
					cfg.height = this.height
				} else {
					cfg.autoWidth = true
					cfg.autoHeight = true
				}
				this.changeCfg(cfg);
				this.initBars(cfg);
				Ext.apply(table, cfg)
				this.form = new Ext.FormPanel(table)
				this.form.on("afterrender", this.onReady, this)

				this.schema = schema;
				this.setKeyReadOnly(true)
				if (!this.isCombined) {
					this.addPanelToWin();
				}
				return this.form
			},

			onRegionCodeClick : function() {
				if ("update" == this.op) {
					return;
				}
				var m = this.createCombinedModule("wgdz",
						"chis.application.hr.HR/HR/B3410101")
				m.on("qd", this.onQd, this);
				var t = m.initPanel();
				var win = m.getWin();
				win.add(t)
				win.setPosition(400, 150);
				win.show();
				// m.loadData();
			},

			onQd : function(data) {

				// homeAddress
				if ("form" == this.mainApp.exContext.areaGridShowType) {
					var regionCode = this.form.getForm()
							.findField("regionCode");
					if (regionCode) {
						regionCode.setValue(data.regionCode_text);
						this.data.regionCode_text = data.regionCode_text;
						this.data.regionCode = data.regionCode;
					}
					var homeAddress = this.form.getForm()
							.findField("homeAddress");
					if (homeAddress) {
						homeAddress.setValue(data.regionCode_text);
						this.data.homeAddress_text = data.regionCode_text;
						this.data.homeAddress = data.regionCode;
					}

				}
				if ("tree" == this.mainApp.exContext.areaGridShowType) {
					var regionCode = this.form.getForm()
							.findField("regionCode");
					if (regionCode) {
						regionCode.setValue(data.regionCode);
						this.data.regionCode_text = data.regionCode_text;
					}
					var homeAddress = this.form.getForm()
							.findField("homeAddress");
					if (homeAddress) {
						homeAddress.setValue(data.regionCode);
						this.data.homeAddress_text = data.regionCode_text;
					}
				}

			},
			onAddField : function(f, it) {
				if (it.hidden || !it.queryable) {
					return false;
				}
			},

			doSelect : function() {
               //Ext.Msg.alert("1",Ext.encode(this.schema))
				var form = this.form.getForm();
//				if (!this.validate()) {
//					return;
//				}
				if (!this.schema) {
					return;
				}
				var items = this.schema.items;
				this.initCnd = this.initCnd
						|| [
								'like',
								['$', 'a.manaUnitId'],
								[
										'concat',
										['substring',
												['$', '%user.manageUnit.id'],
												0, 9], ['s', '%']]];
				var cnd = ['eq', ['s', '1'], ['s', '1']];
				if (items) {
					var n = items.length;
					for (var i = 0; i < n; i++) {
						var it = items[i];
						var v = "";
						var f = form.findField(it.id);
						if (f) {
							v = f.getValue() || "";
						}
						if (v != null & v != "" & v != undefined) {
							var refAlias = it.refAlias || "a";
							var value = refAlias + "." + it.id;
							var cnds = ['eq', ['$', value]];
							if (it.dic) {
								if (it.dic.render == "Tree") {
									var node = f.selectedNode;
									if (node != null && !node.isLeaf()) {
										cnds[0] = 'like';
										cnds.push(['s', v + '%']);
									} else {
										if ("form" == this.mainApp.exContext.areaGridShowType
												&& (it.id == "homeAddress" || it.id == "regionCode")) {
											if (this.data.regionCode) {
												cnds.push(['s',
														this.data.regionCode]);

											} else {
												cnds.push(['s',
														this.data.homeAddress]);

											}
											this.data = {};
										} else {
											cnds.push(['s', v]);
										}
									}
								} else {
									cnds.push(['s', v]);
								}
							} else {
								switch (it.type) {
									case 'int' :
										cnds.push(['i', v]);
										break;
									case 'double' :
									case 'bigDecimal' :
										cnds.push(['d', v]);
										break;
									case 'string' :
										cnds[0] = 'like';
										cnds.push(['s', v + '%']);
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
				
				cnd = ['and', cnd, this.initCnd];
				this.fireEvent("select", cnd);
			},

			doCancel : function() {

				this.fireEvent("cancel");
			},

			doReset : function() {
				this.doNew();
			}

		})