$package("phis.application.war.script");

$import("phis.script.TableForm", "util.Accredit",
		"org.ext.ux.layout.TableFormLayout")
/**
 * 会诊申请
 * 
 * @param {}
 *            cfg
 */
phis.application.war.script.ConsultationApplyForm_New = function(cfg) {

	cfg.width = 900;
	cfg.modal = true;
	phis.application.war.script.ConsultationApplyForm_New.superclass.constructor.apply(
			this, [cfg]);
	// this.on("winShow", this.onWinShow, this);
};

Ext.extend(phis.application.war.script.ConsultationApplyForm_New,
		phis.script.SimpleForm, {
			initFormDoNew : function() {
				var btns = this.form.getTopToolbar().items;
				if (btns) {
					var btns = btns.items;
					var btn = btns[0];
					btn.enable();
				}
				var form = this.form.getForm();
				var data = this.exContext.empiData;

				form.findField("BRXM").setValue(data.BRXM);
				form.findField("BRXB").setValue(data.BRXB_text);
				form.findField("BRYL").setValue(data.AGE);
				form.findField("BRCH").setValue(data.BRCH);// 病人床号
				var SQKS = {
					"key" : data.BRKS,
					"text" : data.BRKS_text
				};
				var SQYS = {
					"key" : data.ZSYS,
					"text" : data.ZSYS_text
				};
				form.findField("SQKS").setValue(SQKS);// 申请科室
				form.findField("SQYS").setValue(SQYS);// 申请医生
				form.findField("HZSJ").setValue(new Date());
				form.findField("ZYHM").setValue(data.ZYHM);// 住院号码
				form.findField("SQXH").disable();
			},

			onReady : function() {
				phis.application.war.script.ConsultationApplyForm_New.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var yqdx = form.findField("YQDX");

				// 根据所选择的换后科室获取相应病区.
				yqdx.on("select", function(combo, r, idx) {

//							var c = phis.script.rmi.miniJsonRequestSync({
//										serviceId : "sqService",
//										serviceAction : "queryBh",
//										body : {
//											yqdx : form.findField("YQDX")
//													.getValue()
//										}
//									})
//							if (c.json.body.length > 0) {
								form.findField("GH")
										.setValue(this.mainApp.userId);
//							}
						}, this)
			},
			initPanel : function(sc) {
				if (this.form) {
					if (!this.isCombined) {
						this.addPanelToWin();
					}
					return this.form;
				}

				this.form = new Ext.FormPanel({
					labelWidth : 80, // label settings here cascade
					frame : true,
					width : 1024,
					autoHeight : true,
					items : [{
						xtype : "label",
						html : "<br/><div style='font-size:25px;font-weight:bold;text-align:center;letter-spacing:20px' >会诊申请单</div><br/>"
					}, {
						xtype : '',
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 3,
							tableAttrs : {
								align : 'right',
								border : 0,
								cellpadding : '0',
								cellspacing : '0'
							}
						},
						defaultType : 'textfield',
						items : this.getItems('LB')
					}, {
						xtype : 'fieldset',
						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 3,
							tableAttrs : {
								border : 0,
								cellpadding : '0',
								cellspacing : '0'
							}
						},
						defaultType : 'textfield',
						items : this.getItems('part1')
					}, {
						xtype : 'fieldset',

						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 4,
							tableAttrs : {
								border : 0,
								cellpadding : '2',
								cellspacing : "2"
							}
						},
						defaultType : 'textfield',
						items : this.getItems('part2')
					}, {
						xtype : 'fieldset',

						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 4,
							tableAttrs : {
								border : 0,
								cellpadding : '2',
								cellspacing : "2"
							}
						},
						defaultType : 'textfield',
						items : this.getItems('part3')
					}, {
						xtype : 'fieldset',

						autoHeight : true,
						layout : 'tableform',
						layoutConfig : {
							columns : 4,
							tableAttrs : {
								border : 0,
								cellpadding : '2',
								cellspacing : "2"
							}
						},
						defaultType : 'textfield',
						items : this.getItems('OBJ')
					}/*
						 * , { xtype : 'fieldset',
						 * 
						 * autoHeight : true, layout : 'tableform', layoutConfig : {
						 * columns : 3, tableAttrs : { border : 0, cellpadding :
						 * '3', cellspacing : "3" } }, defaultType :
						 * 'textfield', items : this.getItems('LAST') }
						 */],
					tbar : (this.tbar || []).concat(this.createButton())
				});

				if (!this.isCombined) {
					this.addPanelToWin();
				}
				this.form.on("afterrender", this.onReady, this);
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
				// this.form.on("afterrender", this.onReady, this)
				return this.form
			},

			initButton : function() {
				var btns = this.form.getTopToolbar();
				var transform = btns.find("cmd", "sure");// 转换
				var canceled = btns.find("cmd", "cancel2");// 注销

				if (this.cmd == "update") {
					transform[0].show();
					canceled[0].show();

				}
			},

			createButton : function() {
				if (this.op == 'read') {
					return [];
				}
				var actions = [{
							"id" : "sure",
							"name" : "确认",
							iconCls : "save"

						}, {
							"id" : "cancel2",
							"name" : "关闭",
							"iconCls" : "common_cancel"
						}];
				var buttons = [];
				if (!actions) {
					return buttons;
				}
				var f1 = 112;
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					var btn = {};
					btn.accessKey = f1 + i;
					btn.cmd = action.id;
					btn.text = action.name + "(F" + (i + 1) + ")";
					btn.iconCls = action.iconCls || action.id;
					btn.script = action.script;
					btn.handler = this.doAction;
					btn.notReadOnly = action.notReadOnly;
					btn.scope = this;
					buttons.push(btn);
				}
				return buttons;
			},

			getItems : function(para) {
				var ac = util.Accredit;
				var MyItems = [];
				var schema = null;
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
				if (!this.initDataId && !this.initDataBody) {
					return;
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
							serviceId : "sqService",
							serviceAction : "queryDetail",
							pkey : this.initDataId,
							body : this.initDataBody
						}, function(code, msg, json) {

							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return
							}
							if (json.body) {
								this.doNew()
								this.initFormData(json.body)
								/*
								 * if (json.body.CSNY) { form.findField("BRNL")
								 * .setValue(json.body.BRNL) }
								 */
								this.fireEvent("loadData", this.entryName,
										json.body);
							}
							if (this.op == 'create') {
								this.op = "update"
							}
						}, this)// jsonRequest
			},
			initFormData : function(data) {
				var tjbz = data.TJBZ.key;
				var jsbz = data.JSBZ.key;
				var btns = this.form.getTopToolbar().items;
				if (btns) {
					var btns = btns.items;
					var btn = btns[0];
					btn.enable();
					if (tjbz == 1 && jsbz == 1) {
						MyMessageTip.msg("提示", "该会诊已经提交且结束,只允许查看不允许编辑", true);
						btn.disable();
					} else {
						if (tjbz == 1) {
							MyMessageTip.msg("提示", "该会诊已经提交,只允许查看不允许编辑", true);
							btn.disable();
						}
						if (jsbz == 1) {
							MyMessageTip.msg("提示", "该会诊已经结束,只允许查看不允许编辑", true);
							btn.disable();
						}
					}
				}
				

				Ext.apply(this.data, data)
				this.initDataId = this.data[this.schema.pkey]
				var form = this.form.getForm()
				var items = this.schema.items
				var n = items.length
				for (var i = 0; i < n; i++) {
					var it = items[i]
					var f = form.findField(it.id)
					if (f) {
						if (this.cmd == "update") {
							if (it.update == 'false') {
								f.disable();
							}
						} else {
							if (it.readOnly != 'true') {
								f.disable();
							}
						}
						var v = data[it.id]
						if (v != undefined) {
							f.setValue(v);
							if (it.dic && v != "0" && f.getValue() != v) {
								f.counter = 1;
								this.setValueAgain(f, v, it);
							}
						}
					}
				}
				this.setKeyReadOnly(true)
				this.focusFieldAfter(-1, 800)
				var form = this.form.getForm();
				var data = this.exContext.empiData;
				// alert(Ext.encode(data))
				form.findField("BRXM").setValue(data.BRXM);
				form.findField("BRXB").setValue(data.BRXB_text);
				form.findField("BRYL").setValue(data.AGE);
				form.findField("BRCH").setValue(data.BRCH);// 病人床号
				form.findField("SQKS").setValue(data.BRKS);// 申请科室
				form.findField("SQYS").setValue(data.ZSYS);// 申请医生
				form.findField("HZSJ").setValue(new Date());
				form.findField("ZYHM").setValue(data.ZYHM);// 住院号码

			},
			doSure : function(saveData) {
				var form = this.form.getForm();
				var ryrq = this.exContext.empiData.RYRQ;// 入院日期

				var val = form.findField("HZSJ").getValue();

				var hzmd = form.findField("HZMD").getValue();// 会诊目的

				var zlqk = form.findField("BQZL").getValue();

				if (val == "" || val == null) {
					Ext.Msg.alert("提示","会诊日期不能为空");
					return;
				}
		
				if (hzmd == '' || hzmd == null) {
					Ext.Msg.alert("提示","会诊目的不能为空");
					return;
				}

				if (zlqk == '' || zlqk == null) {
					Ext.Msg.alert("提示","治疗情况不能为空");
					return;
				}

				var sqxh = form.findField("SQXH").getValue();
				var body = {};
				var serverAct;
				if (sqxh != '' && sqxh != null) {
					serverAct = 'updateSq';
					body = {
						"sqxh" : form.findField("SQXH").getValue(),
						"zyh" : this.exContext.empiData.ZYH,
						"sqks" : this.exContext.empiData.BRKS,
						"sqys" : this.exContext.empiData.ZSYS,
						// 病人病区
						"brbq" : this.exContext.empiData.BRBQ,
						// 病人床号
						"brch" : this.exContext.empiData.BRCH,
						"sqsj" : form.findField("SQSJ").getValue(),
						"hzmd" : hzmd,
						"hzsj" : form.findField("HZSJ").getRawValue(),
						"yqdx" : form.findField("YQDX").getValue(),
						"bqzl" : zlqk,
						"jjbz" : form.findField("JJBZ").getValue(),
						"ryrq" : ryrq
					}
				} else {
					serverAct = 'saveorupdate';

					body = {
						"zyh" : this.exContext.empiData.ZYH,
						"sqks" : this.exContext.empiData.BRKS,
						"sqys" : this.exContext.empiData.ZSYS,
						// 病人病区
						"brbq" : this.exContext.empiData.BRBQ,
						// 病人床号
						"brch" : this.exContext.empiData.BRCH,
						"sqsj" : form.findField("SQSJ").getValue(),
						"hzmd" : hzmd,
						"hzsj" : form.findField("HZSJ").getRawValue(),
						"yqdx" : form.findField("YQDX").getValue(),
						"bqzl" : zlqk,
						"jjbz" : form.findField("JJBZ").getValue(),
						"ryrq" : ryrq
					}
				}
				/** 保存会诊新增记录* */
				var rr = phis.script.rmi.miniJsonRequestSync({
							serviceId : "sqService",
							serviceAction : serverAct,
							body : body
						});
				if (rr.code > 300) {
					this.processReturnMsg(rr.code, rr.msg);
					return
				} else {
					form.el.unmask();
					if (rr.json.overtime < 0) {
						MyMessageTip.msg("提示", "会诊日期不能小于入院日期!", true);
						//Ext.Msg.alert("会诊日期不能小于入院日期!");
						return;
					}
					if (sqxh != null && sqxh != '')
						MyMessageTip.msg("提示", "会诊记录修改成功！", true);
					else
						MyMessageTip.msg("提示", "会诊记录添加成功！", true);
					var win = this.getWin();
					if (win)
						win.hide();
					this.opener.store.load();
					// console.debug();
				}
			},
			doCancel2 : function() {
				var win = this.getWin();
				if (win)
					win.hide();
			}
		});
