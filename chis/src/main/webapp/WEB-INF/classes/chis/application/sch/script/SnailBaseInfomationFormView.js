﻿$package("chis.application.sch.script");

$import("chis.script.BizTableFormView");

chis.application.sch.script.SnailBaseInfomationFormView = function(cfg) {
	cfg.colCount = 3;
	chis.application.sch.script.SnailBaseInfomationFormView.superclass.constructor
			.apply(this, [cfg]);
	this.saveServiceId = "chis.snailBaseInfoService";
	this.saveAction = "saveSnailBaseInfo";
};

Ext.extend(chis.application.sch.script.SnailBaseInfomationFormView,
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
										colspan : 2,
										onTriggerClick : function() {
											_ctr.onRegionCodeClick(ff.name)
										}, // 单击事件
										triggerClass : 'x-form-search-trigger', // 按钮样式
										// readOnly : true, //只读
										// disabled : true,
										fieldLabel : "<font  color=red>网格地址(组):<font>",
										"width" : 581

									});
							this.ff = ff;
							this.ff.allowBlank = false;
							this.ff.invalidText = "必填字段";
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
			onRegionCodeClick : function(r) {
				 if ("update" == this.op) {
					return;
				}
				var m = this.createCombinedModule("wgdz",
						"chis.application.hr.HR/HR/B3410101")
				m.on("qd", this.onQd, this);
				m.areaGridListId = r;
				var t = m.initPanel();
				var win = m.getWin();
				win.add(t)
				win.setPosition(400, 150);
				win.show();
				// m.loadData();
			},
			onQd : function(data) {
					this.form.getForm().findField("regionCode")
							.setValue(data.regionCode_text);
					this.data.regionCode_text = data.regionCode_text;
					this.data.regionCode = data.regionCode;

				
			},
			initFormData : function(data) {
				chis.application.sch.script.SnailBaseInfomationFormView.superclass.initFormData
						.call(this, data);

				if ("form" == this.mainApp.exContext.areaGridShowType) {
					if (data.regionCode) {
						if (data.regionCode.key) {

							this.form.getForm().findField("regionCode")
									.setValue(data.regionCode.text);
							this.form.getForm().findField("regionCode")
									.disable();
							this.data.regionCode = data.regionCode.key;
							this.data.regionCode_text = data.regionCode.text;
						}
					}
				 }
				}
					
					
					,
			onReady : function() {
				var form = this.form.getForm();
				var manaUnit = form.findField("regionCode");
				if (manaUnit) {
					manaUnit.on("beforeselect", this.filtermanaUnit, this)
				}
				var snailFind = form.findField("snailFind");
				if (snailFind) {
					snailFind.on("blur", this.snailFindFill, this)
				}
				var snailNegative = form.findField("snailNegative");
				if (snailNegative) {
					snailNegative.on("blur", this.snailNegativeFill, this)
				}
			},
			snailFindFill : function(field) {
				var value = field.getValue();
				var form = this.form.getForm();
				var snailNegative = form.findField("snailNegative");
				if (value != null && value > 0) {
					snailNegative.maxValue = value;
				}
			},
			snailNegativeFill : function(field) {
				var form = this.form.getForm();
				var snailFind = form.findField("snailFind");
				var value = snailFind.getValue();
				if (value != null && value > 0) {
					field.maxValue = value;
				}
			},

			// 判断 是否是组
			// 城市节点定位到小区 g1
			// 农村节点定位到组 g2
			filtermanaUnit : function(comb, node) {
				if (!node) {
					return false;
				}
				var isFamily = node.attributes["isFamily"];
				if (isFamily == "g2") {
					return true;
				} else {
					return false;
				}
			},
			getFormData : function() {
				if (!this.validate()) {
					return
				}
				if (!this.schema) {
					return
				}
				var ac = util.Accredit;
				var values = {};
				var items = this.schema.items;
				Ext.apply(this.data, this.exContext);
				if (items) {
					var form = this.form.getForm();
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						if (this.op == "create" && !ac.canCreate(it.acValue)) {
							continue;
						}
						var v = this.data[it.id] // ** modify by yzh
													// 2010-08-04
						if (v == undefined) {
							v = it.defaultValue
						}
						if (v != null && typeof v == "object") {
							v = v.key
						}
						var f = form.findField(it.id)
						if (f) {
							v = f.getValue()
							// add by huangpf
							if (f.getXType() == "treeField") {
								var rawVal = f.getRawValue();
								if (rawVal == null || rawVal == "")
									v = "";
							}
							if (f.getXType() == "datefield" && v != null
									&& v != "") {
								v = v.format('Y-m-d');
							}
							// end
						}

						if ("form" == this.mainApp.exContext.areaGridShowType) {

							if ("regionCode" == it.id) {
								v = this.data.regionCode;
							}
							
						}

						if (v == null || v === "") {
							if (!(it.pkey)
									&& (it["not-null"] == "1" || it['not-null'] == "true")
									&& !it.ref) {
								Ext.Msg.alert("提示", it.alias + "不能为空");
								return;
							}
						}
						values[it.id] = v;
					}
				}
				
				return values;
			}

		});
