$package("phis.application.hos.script")

$import("phis.script.SimpleModule")

phis.application.hos.script.HospitalDetalisModule = function(cfg) {
	cfg.width = 840;
	cfg.height = 500;
	cfg.modal = true;
	Ext.apply(this, app.modules.common)
	phis.application.hos.script.HospitalDetalisModule.superclass.constructor
			.apply(this, [ cfg ])
}
var radioValue = 1;
Ext
		.extend(
				phis.application.hos.script.HospitalDetalisModule,
				phis.script.SimpleModule,
				{
					initPanel : function() {
						if (this.panel) {
							return this.panel;
						}
						var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : false,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [ {
								layout : "fit",
								border : false,
								split : true,
								region : 'west',
								width : '402',
								items : this.getList()
							}, {
								layout : "fit",
								border : false,
								split : true,
								region : 'center',
//								width : '50%',
								items : this.getDetailsList()
							} ],
							tbar : (this.tbar || [])
									.concat(this.createButton())
						});

						this.panel = panel;
						this.panel.on("afterrender", this.onReady, this)
						return panel;
					},
					 onReady : function() {
//						 this.list.refresh();
					// // this.doWhole();
					// this.on("winShow", this.onWinShow, this);
					// },
					// onWinShow : function() {
					// this.radioGroup.setValue(1);
					// this.form.doNew();
					// this.initFormData(this.data)
					// this.list.requestData.pageNo = 1;
					// if (this.data.JSLX == 10) {
					// this.list.requestData.cnd = ['and',
					// ['eq', ['$', 'a.ZYH'], ['i', this.data.ZYH]],
					// ['eq', ['$', 'a.JSCS'], ['i', this.data.JSCS]]];
					// } else {
					// this.list.requestData.cnd = ['and',
					// ['eq', ['$', 'a.ZYH'], ['i', this.data.ZYH]],
					// ['eq', ['$', 'a.JSCS'], ['i', 0]]];
					// }
					// this.list.refresh();
					 },
					getList : function() {
						var module = this.createModule("refList", this.refList);
						// module.on("loadData", this.initFormData, this);
						this.list = module;
						module.opener = this;
						return module.initPanel();
					},
					getDetailsList : function() {
						var module = this.createModule("refDetailsList",
								this.refDetailsList);
						this.DetailsList = module;
						// var DetailsList = module.initPanel();
						module.opener = this;
						return module.initPanel();
					},
					createButton : function() {
						if (this.op == 'read') {
							return [];
						}
						var actions = this.actions;
						var buttons = [];
						if (!actions) {
							return buttons;
						}
						var f1 = 112;
						for ( var i = 0; i < actions.length; i++) {
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
					doAction : function(item, e) {
						var cmd = item.cmd
						var ref = item.ref
						if (ref) {
							this.loadRemote(ref, item)
							return;
						}
						var script = item.script
						cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
						if (script) {
							$require(script, [
									function() {
										eval(script + '.do' + cmd
												+ '.apply(this,[item,e])')
									}, this ])
						} else {
							var action = this["do" + cmd]
							if (action) {
								action.apply(this, [ item, e ])
							}
						}
					},
					doCancel : function() {
						var win = this.opener.getWin();
						if (win)
							win.hide();
					},
					detaileLoadData : function(data){
						this.DetailsList.loadData(data);
					},
					doPrint : function(){
						this.list.doPrint();
						
					},
					doPrintDetails : function(){
						this.DetailsList.doPrint();
					}
//					doPrint : function() {
//						var module = this.createModule("dayList",
//								this.refDayList);
//						var ZYH = this.data.ZYH;
//						if (ZYH == null) {
//							MyMessageTip.msg("提示", "打印失败：无效的病人信息!", true);
//							return;
//						}
//						var YPLX = radioValue;
//						var BRXM = this.form.form.getForm().findField("BRXM")
//								.getValue();
//						var BRXZ = this.form.form.getForm().findField("BRXZ")
//								.getRawValue();
//						var DAYS = this.form.form.getForm().findField("ZYTS")
//								.getValue();
//						var RYRQ = this.form.form.getForm().findField("RYRQ")
//								.getValue();
//						var ZFHJ = this.form.form.getForm().findField("ZFHJ")
//								.getValue();
//						var ZYHM = this.form.form.getForm().findField("ZYHM")
//								.getValue();
//						var BRKS = this.form.form.getForm().findField("BRKS")
//								.getRawValue();
//						var BRCH = this.form.form.getForm().findField("BRCH")
//								.getValue();
//						var CYRQ = this.form.form.getForm().findField("CYRQ")
//								.getValue();
//						var FYHJ = this.form.form.getForm().findField("FYHJ")
//								.getValue();
//						module.ZYH = ZYH;
//						module.BRXM = encodeURIComponent(BRXM);
//						module.BRXZ = encodeURIComponent(BRXZ);
//						module.DAYS = DAYS;
//						module.RYRQ = RYRQ;
//						module.ZFHJ = ZFHJ;
//						module.ZYHM = ZYHM;
//						module.BRKS = encodeURIComponent(BRKS);
//						module.BRCH = BRCH;
//						module.CYRQ = CYRQ;
//						module.FYHJ = FYHJ;
//						module.YPLX = YPLX;
//						module.JKHJ = this.data.JKHJ;
//						module.NL = this.data.RYNL;
//						module.BRXB = this.data.BRXB
//						module.CZGH = this.data.CZGH;
//						module.JSLX = this.data.JSLX;
//						module.JSCS = this.data.JSCS
//						module.initPanel();
//						module.doPrint();
//					}

				});