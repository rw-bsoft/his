$package("phis.application.phsa.script")

$import("phis.script.SimpleModule", "util.dictionary.TreeDicFactory",
		"org.ext.ux.layout.TableFormLayout", "util.helper.Helper",
		"chis.script.EHRView", "phis.application.war.script.HDRView");
phis.application.phsa.script.PHSAPeopleInfoModule = function(cfg) {
	phis.application.phsa.script.PHSAPeopleInfoModule.superclass.constructor
			.apply(this, [cfg]);

},

Ext.extend(phis.application.phsa.script.PHSAPeopleInfoModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'north',
										height : 80,
										items : this.getQueryForm()
									}, {
										layout : "card",
										id : "BRXXLB",
										border : false,
										split : true,
										activeItem : 0,
										animCollapse : false,
										frame : true,
										region : 'center',
										items : this.getRecordsList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getQueryForm : function() {
				var radio = [];
				var config = {
					boxLabel : "门诊",
					id:"LBLB_BRXX",
					inputValue : "1",
					name : "stack",
					width : 60,
					checked : true,
					clearCls : true,
					listeners : {
						check : this.radioGroupChange,
						scope : this
					}
				};
				// radio.push(config);
				var MZradio = new Ext.form.Radio(config);
				config = {
					boxLabel : "住院",
					inputValue : "2",
					width : 60,
					name : "stack",
					clearCls : true,
					listeners : {
						check : this.radioGroupChange,
						scope : this
					}
				};
				// radio.push(config);
				var ZYradio = new Ext.form.Radio(config);
				// var radioGroup = new Ext.form.RadioGroup({
				// width : 150,
				// disabled : false,
				// allowBlank : false,
				// value : "1",
				// items : radio,
				// id : "LBLB_BRXX",
				// listeners : {
				// change : this.radioGroupChange,
				// scope : this
				// }
				// });
				var BRXBcombox = util.dictionary.SimpleDicFactory.createDic({
							id : "phis.dictionary.gender",
							width : 120
						});
				BRXBcombox.name = 'BRXB_BRXX';
				BRXBcombox.id = 'BRXB_BRXX';
				BRXBcombox.fieldLabel = '性别';
				var BRXZcombox = util.dictionary.SimpleDicFactory.createDic({
							id : 'phis.dictionary.patientProperties',
							width : 120
						})
				BRXZcombox.name = 'BRXZ_BRXX';
				BRXZcombox.id = 'BRXZ_BRXX';
				BRXZcombox.fieldLabel = '病人性质';

				var form = new Ext.FormPanel({
							labelWidth : 60,
							frame : true,
							defaultType : 'textfield',
							layout : 'tableform',
							autoScroll : true,
							defaults : {
								bodyStyle : 'padding-left:3px;padding-bottom:3px;'
							},
							layoutConfig : {
								columns : 6,
								tableAttrs : {
									border : 0
								}
							},
							items : [{
										xtype : 'panel',
										layout : "table",
										items : [MZradio, {
													xtype : "panel",
													width : 60,
													html : "病人姓名:"
												}, new Ext.form.TextField({
															name : "BRXM_BRXX",
															id : "BRXM_BRXX",
															width : 120
														})]
									}, {
										xtype : 'panel',
										layout : "table",
										items : [{
													xtype : "panel",
													width : 70,
													html : "&nbsp;&nbsp;&nbsp;&nbsp;出生日期:"
												}, new Ext.form.DateField({
															name : 'CSNY_BRXX',
															id : "CSNY_BRXX",
															altFormats : 'Y-m-d',
															format : 'Y-m-d',
															emptyText : '出生日期',
															width : 120
														})]
									}, {
										xtype : 'panel',
										layout : "table",
										items : [{
													xtype : "panel",
													width : 60,
													html : "门诊号码:"
												}, new Ext.form.TextField({
															name : "MZHM_BRXX",
															id : "MZHM_BRXX",
															width : 120
														})]
									}, {
										xtype : 'panel',
										layout : "table",
										items : [{
													xtype : "panel",
													width : 60,
													html : "医保卡号:"
												}, new Ext.form.TextField({
															name : "YBKH_BRXX",
															id : "YBKH_BRXX",
															width : 120
														})]
									}, {
										xtype : 'panel',
										layout : "table",
										items : [new Ext.Button({
															iconCls : 'query',
															text : '查询',
															handler : this.doQuery,
															width : 60,
															scope : this
														}), new Ext.Button({
															iconCls : 'create',
															text : '重置',
															width : 60,
															handler : this.doNewQuery,
															scope : this
														})]
									}, {
										xtype : 'panel',
										layout : "table"
									}, {
										xtype : 'panel',
										layout : "table",
										items : [ZYradio, {
													xtype : "panel",
													width : 60,
													html : "病人性别:"
												}, BRXBcombox]
									}, {
										xtype : 'panel',
										layout : "table",
										items : [{
													xtype : "panel",
													width : 70,
													html : "&nbsp;&nbsp;&nbsp;&nbsp;身份证号:"
												}, new Ext.form.TextField({
															name : "SFZH_BRXX",
															id : "SFZH_BRXX",
															width : 120
														})]
									}, {
										xtype : 'panel',
										layout : "table",
										items : [{
													xtype : "panel",
													width : 60,
													html : "病人性质:"
												}, BRXZcombox]
									}, {
										xtype : "panel",
										layout : "table",
										items : [{
													xtype : "panel",
													width : 60,
													html : "病案号码:"
												}, new Ext.form.TextField({
															name : "BAHM_BRXX",
															id : "BAHM_BRXX",
															disabled :true,
															width : 120
														})]
									}, {
										xtype : 'panel',
										layout : "table",
										layoutConfig : {
											rowspan : 2
										},
										items : [new Ext.Button({
													iconCls : 'create',
													text : 'EHRView',
													handler : this.doEHRView,
													width : 120,
													scope : this
												})]
									}]
						});
				this.form = form
				return form
			},
			getRecordsList : function() {
				var module = this.createModule("RecordsList1",
						this.RecordsList1);
				this.list1 = module;
				var grid1 = module.initPanel();
				this.grid1 = grid1;
				this.grid1.on("rowdblclick", this.doEHRView, this);
				var module = this.createModule("RecordsList2",
						this.RecordsList2);
				this.list2 = module;
				var grid2 = module.initPanel();
				this.grid2 = grid2;
				this.grid2.on("rowdblclick", this.doEHRView, this)
				return [grid1, grid2];
			},
			doEHRView : function() {
				var LBLB = this.form.findById("LBLB_BRXX");
				var LBLBValue = LBLB.getGroupValue();
				if (LBLBValue == "1") {
					this.openEMRView();
				} else if (LBLBValue == "2") {
					this.openHDRView();
				}
			},
			doNewQuery : function() {
				var LBLB = this.form.findById("LBLB_BRXX");
				LBLB.setValue(true);
				this.doNew();
			},
			doNew : function() {
				var BRXB = this.form.findById("BRXB_BRXX");
				BRXB.setValue();
				var BRXZ = this.form.findById("BRXZ_BRXX");
				BRXZ.setValue();
				var BAHM = this.form.findById("BAHM_BRXX");
				BAHM.setValue();
				var BRXM = this.form.findById("BRXM_BRXX");
				BRXM.setValue();
				var CSNY = this.form.findById("CSNY_BRXX");
				CSNY.setValue();
				var SFZH = this.form.findById("SFZH_BRXX");
				SFZH.setValue();
				var MZHM = this.form.findById("MZHM_BRXX");
				MZHM.setValue();
				var YBKH = this.form.findById("YBKH_BRXX");
				YBKH.setValue();
			},
			doQuery : function() {
				var cnd = ['and', ['eq', ['s', '1'], ['s', '1']],
						['eq', ['s', '1'], ['s', '1']]];
				var LBLB = this.form.findById("LBLB_BRXX");
				var LBLBValue = LBLB.getGroupValue();
				var field = this.form.findById("BRXB_BRXX");
				var fieldValue = field.getValue();
				if (fieldValue) {
					cnd.push(['eq', ['$', 'a.BRXB'], ['s', fieldValue]]);
				}
				field = this.form.findById("BRXZ_BRXX");
				fieldValue = field.getValue();
				if (fieldValue && fieldValue != -1) {
					cnd.push(['eq', ['$', 'a.BRXZ'], ['s', fieldValue]]);
				}
				field = this.form.findById("BAHM_BRXX");
				fieldValue = field.getValue();
				if (fieldValue) {
					cnd
							.push(['like', ['$', 'a.BAHM'],
									['s', fieldValue + "%"]]);
				}
				field = this.form.findById("BRXM_BRXX");
				fieldValue = field.getValue();
				if (fieldValue) {
					cnd
							.push(['like', ['$', 'a.BRXM'],
									['s', fieldValue + "%"]]);
				}
				field = this.form.findById("CSNY_BRXX");
				fieldValue = field.getValue();
				if (fieldValue) {
					cnd
							.push([
									'eq',
									['$',
											"concat(to_char(a.CSNY,'yyyy-MM-dd'),' 00:00:00')"],
									['date', fieldValue]]);
				}
				field = this.form.findById("SFZH_BRXX");
				fieldValue = field.getValue();
				if (fieldValue) {
					cnd.push(['eq', ['$', 'a.SFZH'], ['s', fieldValue]]);
				}
				field = this.form.findById("MZHM_BRXX");
				fieldValue = field.getValue();
				if (fieldValue) {
					cnd
							.push(['like', ['$', 'a.MZHM'],
									['s', fieldValue + "%"]]);
				}
				field = this.form.findById("YBKH_BRXX");
				fieldValue = field.getValue();
				if (fieldValue) {
					cnd
							.push(['like', ['$', 'a.YBKH'],
									['s', fieldValue + "%"]]);
				}
				if (LBLBValue == "1") {
					this.list1.requestData.BRLB = LBLBValue;
					this.list1.requestData.cnd = cnd;
					this.list1.requestData.pageNo=1;
					this.list1.requestData.pageSize=25;
					this.list1.refresh();
				}
				if (LBLBValue == "2") {
					this.list2.requestData.BRLB = LBLBValue;
					this.list2.requestData.cnd = cnd;
					this.list2.requestData.pageNo=1;
					this.list2.requestData.pageSize=25;
					this.list2.refresh();
				}

			},
			radioGroupChange : function(field) {
				var BAHM = this.form.findById("BAHM_BRXX");
				if (field.getGroupValue() == "2") {
					BAHM.setValue();
					BAHM.enable();
					Ext.getCmp('BRXXLB').layout.setActiveItem(1);
				}
				if (field.getGroupValue() == "1") {
					BAHM.setValue();
					BAHM.disable();
					Ext.getCmp('BRXXLB').layout.setActiveItem(0);
				}
				this.doNew();

			},
			openEMRView : function() {
				var r = this.list1.getSelectedRecord();
				if (!r) {
					return;
				}
				var empiId = r.get("EMPIID");
				var brid = r.get("BRID");
				var BRXZ = r.get("BRXZ");
				var JZXH = r.get("JZXH");
				var initModules = [];
				var flag = "0";
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.PHSAPeopleInfoService",
							serviceAction : "checkHasHealthRecord",
							empiId : empiId
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return
				}
				if (r.json.flag != null) {
					flag = r.json.flag;
				}
				initModules.push("N_01");
				var exContext = {};
				exContext.brxx = r;
				exContext.JZXH = JZXH;
				exContext.alias = this.alias;
				exContext.hasHealthRecord = flag;
				exContext.hisGetEHR = true;
				if (!this.ehrView) {
					var m = new chis.script.EHRView({
								initModules : initModules,
								empiId : empiId,
								closeNav : true,
								activeTab : 0,
								mainApp : this.mainApp
							})
					m.setMainApp(this.mainApp);
					this.ehrView = m;
				}
				Ext.applyIf(this.ehrView.exContext, exContext);
				this.ehrView.initModules = initModules;
				this.ehrView.exContext.ids.empiId = empiId;
				this.ehrView.exContext.ids.brid = brid;
				this.ehrView.actionName = 'EHR_HealthRecord';
				var win = this.ehrView.getWin();
				win.show();
				this.mainApp.locked = true;

			},
			openHDRView : function() {
				var r = this.list2.getSelectedRecord();
				if (!r) {
					return;
				}
				var empiId = r.get("EMPIID");
				var brid = r.get("BRID");
				var BRXZ = r.get("BRXZ");
				var ZYH = r.get("ZYH");
				var initModules = [];
				var flag = "0";
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : "phis.PHSAPeopleInfoService",
							serviceAction : "checkHasHealthRecord",
							empiId : empiId
						});
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.onBeforeSave);
					return
				}
				if (r.json.flag != null) {
					flag = r.json.flag;
				}
				initModules.push("N_02");
				var exContext = {};
				exContext.brxx = r;
				exContext.ZYH = ZYH;
				exContext.alias = this.alias;
				exContext.hasHealthRecord = flag;
				exContext.hisGetEHR = true;
				var m = new chis.script.EHRView({
							initModules : initModules,
							empiId : empiId,
							closeNav : true,
							activeTab : 0,
							mainApp : this.mainApp
						});
				m.exContext.ids.brid = brid;
				Ext.applyIf(m.exContext, exContext);
				m.setMainApp(this.mainApp);
				m.getWin().show()
				m.getWin().maximize()

			}
		});