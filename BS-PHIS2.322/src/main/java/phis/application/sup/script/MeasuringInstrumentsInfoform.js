$package("phis.application.sup.script")
$import("phis.script.common", "phis.script.TableForm")

phis.application.sup.script.MeasuringInstrumentsInfoform = function(cfg) {
	phis.application.sup.script.MeasuringInstrumentsInfoform.superclass.constructor
			.apply(this, [cfg])
}
Ext.extend(phis.application.sup.script.MeasuringInstrumentsInfoform,
		phis.script.TableForm, {
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}
				var _layoutConfig = {
					columns : 3,
					tableAttrs : {
						border : 0,
						cellpadding : '2',
						cellspacing : "2"
					}
				};
				this.form = new Ext.FormPanel({
							labelWidth : 80, // label settings here cascade
							// unless overridden
							frame : true,
							// autoScroll : true,
							defaultType : 'fieldset',
							items : [{
										title : '基本信息',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : _layoutConfig,
										defaultType : 'textfield',
										items : this.getItems('JBXX')
									}, {
										title : '计量信息',
										autoHeight : true,
										layout : 'tableform',
										layoutConfig : _layoutConfig,
										defaultType : 'textfield',
										items : this.getItems('JLXX')
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
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
			loadData : function() {
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId, this.initDataBody)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				this.loading = true
				phis.script.rmi.jsonRequest({
					serviceId : "equipmentWeighingManagementService",
					serviceAction : "queryJLXXUPDATE",
					jlxh : this.jlxh,
					action : this.op, // 按钮事件
					module : this._mId
						// 增加module的id
					}, function(code, msg, json) {
					if (this.form && this.form.el) {
						this.form.el.unmask()
					}
					this.loading = false
					if (code > 300) {
						this.processReturnMsg(code, msg, this.loadData)
						return
					}
					if (json.jlxxjson) {
						this.doNew()
						this.initFormData(json.jlxxjson)
						this.fireEvent("loadData", this.entryName,
								json.jlxxjson);
					}
					if (this.op == 'create') {
						this.op = "update"
					}

				}, this)// jsonRequest
			},
			onReady : function() {
				phis.application.sup.script.MeasuringInstrumentsInfoform.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				this.JDZQ = form.findField("JDZQ");
				if (this.JDZQ) {
					this.JDZQ.on("blur", this.onJDZQBlur, this);
				}
			},
			onJDZQBlur : function() {
				var form = this.form.getForm();
				var m = this.JDZQ.getValue();
				var strDate = new Date().format('Y-m-d');
				var now = new Date(strDate.replace(/\-/g, "/"));
				var perMonth = new Date(now.setMonth(now.getMonth() + m));
				var yearmonth = perMonth.format('Y-m');
				yearmonth = yearmonth + "-01";
				this.XCJD = form.findField("XCJD");
				this.XCJD.setValue(yearmonth)
			},
			doClose : function() {
				this.win.hide();
			},
			doSave : function() {
				var body = {};
				body = this.getFormData();
				var r1save = phis.script.rmi.miniJsonRequestSync({
							serviceId : "equipmentWeighingManagementService",
							serviceAction : "updateJLXX",
							body : body,
							op : "update"
						});
				if (r1save.code > 200) {
					MyMessageTip.msg("提示", "修改失败!", true);
				} else {
					MyMessageTip.msg("提示", "修改成功!", true);
					this.doClose();
					this.oper.loadData();
				}
			}
		})