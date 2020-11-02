/**
 * 病人变动医嘱查询
 * 
 * @author gaof
 */
$package("phis.application.war.script");

$import("phis.script.SimpleModule", "org.ext.ux.layout.TableFormLayout",
		"app.desktop.Module");

phis.application.war.script.PatientMedicalAdviceQuery = function(cfg) {
	this.exContext = {};
	phis.application.war.script.PatientMedicalAdviceQuery.superclass.constructor.apply(
			this, [cfg]);
}

Ext.extend(phis.application.war.script.PatientMedicalAdviceQuery,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var form = this.createForm();
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							buttonAlign : 'center',
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										height : 78,
										items : form
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getList()
									}]
						});
				this.panel = panel;
				this.panel.on("afterrender", this.onReady, this)
				return panel;
			},
			onReady : function() {
				this.doQuery();
			},
			createForm : function() {
				var FYFSdic = {
					"id" : "phis.dictionary.hairMedicineWay",
					"src" : "ZY_BQYZ.FYFS",
					"width" : 130
				};
				var FYFScombox = util.dictionary.SimpleDicFactory.createDic(FYFSdic)
				FYFScombox.name = 'FYFS';
				FYFScombox.fieldLabel = '发药方式';
				FYFScombox.emptyText = "全部"

				this.YZLXStore = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : [{
										'value' : 1,
										'text' : '当天所有变动医嘱'
									}, {
										'value' : 2,
										'text' : '当天临时医嘱'
									}, {
										'value' : 3,
										'text' : '当天开始长期医嘱'
									}, {
										'value' : 4,
										'text' : '当天结束长期医嘱'
									}]
						});
				var YZLXCombox = new Ext.form.ComboBox({
							name : 'YZLX',
							store : this.YZLXStore,
							valueField : "value",
							displayField : "text",
							mode : 'local',
							triggerAction : 'all',
							emptyText : "所 有 类 型",
							selectOnFocus : true,
							forceSelection : true,
							width : 130
						});
				var ZFPBcheckBox = [{
							boxLabel : '自负药品',
							name : 'ZFPB',
							inputValue : '1',
							xtype : 'checkbox',
							width : 100,
							listeners : {
								// 'check' : this.checkHandler,
								scope : this
							},
							checked : false
						}]
				this.xmlxstore=new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : [{
										'value' : 1,
										'text' : '药品'
									}, {
										'value' : 2,
										'text' : '费用'
									}, {
										'value' : 3,
										'text' : '文字'
									}]
						});
				var xmlxCombox = new Ext.form.ComboBox({
							name : 'XMLX',
							store : this.xmlxstore,
							valueField : "value",
							displayField : "text",
							mode : 'local',
							triggerAction : 'all',
							emptyText : "项目类型",
							selectOnFocus : true,
							forceSelection : true,
							width : 130
						});
						
				var form = new Ext.FormPanel({
							frame : true,
							defaultType : 'textfield',
							layout : 'tableform',
							layoutConfig : {
								columns : 6,
								tableAttrs : {
									border : 0,
									cellpadding : '2',
									cellspacing : "2"
								}
							},
							items : [{
								xtype : 'panel',
								layout : "table",
								items : [{
											xtype : "panel",
											width : 60,
											html : "开始日期:"
										}, new Ext.ux.form.Spinner({
													name : 'KSSJ',
													value : new Date()
															.format('Y-m-d'),
													strategy : {
														xtype : "date"
													}
												})]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [{
											xtype : "panel",
											width : 60,
											html : "医嘱类型:"
										}, YZLXCombox]
							},
							 {
								xtype : 'panel',
								layout : "table",
								items : [{
											xtype : "panel",
											width : 60,
											html : "项目类型:"
										}, xmlxCombox]
							}
//                            , {
//								xtype : 'panel',
//								layout : "table",
//								width : 100,
//								items : [ZFPBcheckBox]
//							}
                            , {
								xtype : 'panel',
								rowspan : "2",
								layout : "table",
								// layoutConfig : {
								// rowspan : 2
								// },
								items : [new Ext.Button({
													iconCls : 'query',
													height : 40,
													width : 70,
													text : '查询',
													handler : this.doQuery,
													scope : this
												}), new Ext.Button({
													iconCls : 'arrow_refresh',
													height : 40,
													width : 70,
													text : '重置',
													handler : this.doNewQuery,
													scope : this
												}), new Ext.Button({
													iconCls : 'print',
													height : 40,
													width : 70,
													text : '打印',
													handler : this.doPrint,
													scope : this
												})]
							},{
								xtype : 'panel'
							}, {
                                xtype : 'panel'
                            }, {
								xtype : 'panel',
								layout : "table",
								items : [{
											xtype : "panel",
											width : 60,
											html : "终止日期:"
										}, new Ext.ux.form.Spinner({
													name : 'TZSJ',
													value : new Date().format('Y-m-d'),
													strategy : {
														xtype : "date"
													}
												})]
							}, {
								xtype : 'panel',
								layout : "table",
								items : [{
											xtype : "panel",
											width : 60,
											html : "发药方式:"
										}, FYFScombox]
							}]
						});
				this.form = form
				return form
			},
			getList : function() {
				var module = this.createModule("List", this.refList);
				var listModule = module.initPanel();
				this.listModule = module;
				module.opener = this;
				return listModule;
			},

			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {eval(script + '.do' +cmd+ '.apply(this,[item,e])')}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			doNewQuery : function() {
				var year = new Date().getFullYear();
				var month = new Date().getMonth() >= 9? (new Date().getMonth() + 1):"0"+(new Date().getMonth() + 1)
				var date = new Date().getDate() >= 10? (new Date().getDate()):"0"+(new Date().getDate())
				var now = year + "-" + month + "-" + date;
				var form = this.form.getForm();
				form.findField("KSSJ").setValue(now);
				form.findField("TZSJ").setValue(now);
				form.findField("YZLX").setValue("");
				form.findField("FYFS").setValue("");
//				form.findField("ZFPB").setValue("");
				form.findField("XMLX").setValue("");
				this.listModule.requestData.cnd=[];
			},
			doQuery : function() {
				var form = this.form.getForm();
				KSSJ = form.findField("KSSJ").getValue();
				if (KSSJ) {
					if (KSSJ.length != 10) {
						Ext.MessageBox.alert("提示", "开始日期格式不对", function() {
									form.findField("KSSJ").focus(false, 100);
								}, this);
						return;
					}
					if (new Date(KSSJ) == 'Invalid Date') {
						Ext.MessageBox.alert("提示", "开始日期格式不对", function() {
									form.findField("KSSJ").focus(false, 100);
								}, this);
						return;
					}
				}
				TZSJ = form.findField("TZSJ").getValue();
				if (TZSJ) {
					if (TZSJ.length != 10) {
						Ext.MessageBox.alert("提示", "终止日期格式不对", function() {
									form.findField("TZSJ").focus(false, 100);
								}, this);
						return;
					}
					if (new Date(TZSJ) == 'Invalid Date') {
						Ext.MessageBox.alert("提示", "终止日期格式不对", function() {
									form.findField("TZSJ").focus(false, 100);
								}, this);
						return;
					}
					TZSJ = TZSJ + ' 23:59:59';
				}

				if (KSSJ != null && TZSJ != null && KSSJ != "" && TZSJ != ""
						&& KSSJ > TZSJ) {
					Ext.MessageBox.alert("提示", "开始日期不能大于终止日期");
					return;
				}
				YZLX = form.findField("YZLX").getValue();
				FYFS = form.findField("FYFS").getValue();
//				ZFPB = form.findField("ZFPB").getValue();
				XMLX = form.findField("XMLX").getValue();

				ZFPBCnd = ['eq', ['$', 'a.ZFPB'], ["i", 1]];
				FYFSCnd = ['eq', ['$', "a.FYFS"], ['s', FYFS]];
				KSSJCnd = ['and',['ge', ['$', "a.KSSJ"], ['todate', ['s',KSSJ], ['s','yyyy-mm-dd']]],
								 ['le', ['$', "a.KSSJ"],['todate', ['s',TZSJ], ['s','yyyy-mm-dd hh24:mi:ss']]]];
				TZSJCnd = ['and',['ge', ['$', "a.TZSJ"], ['todate', ['s',KSSJ], ['s','yyyy-mm-dd']]],
								 ['le', ['$', "a.TZSJ"],['todate', ['s',TZSJ], ['s','yyyy-mm-dd hh24:mi:ss']]]];
				var cnd = [];
				cnd = ['or', KSSJCnd, TZSJCnd];
				cnd = ['and',cnd,['and',['eq', ['$', 'a.SRKS'],["$", '%user.properties.wardId']],
							  			['eq', ['$', 'a.JGID'],['s', this.mainApp['phisApp'].deptId]]]];
				cnd = ['and',cnd,['and', ['ge', ['$', 'a.YPLX'], ["i", 0]],
										 ['eq', ['$', 'a.YZPB'], ['i', 0]]]];

//				if (ZFPB != null && ZFPB != "") {
//					cnd = ['and', cnd, ['eq', ['$', 'a.ZFPB'], ["i", 1]]];
//				}
				if (FYFS != null && FYFS != "") {
					cnd = ['and', cnd, FYFSCnd];
				}
				// 医嘱类型
				if (YZLX != null && YZLX != "") {
					if (YZLX == 1) {

					} else if (YZLX == 2) {
						cnd = ['and', cnd,
								['eq', ['$', 'a.KSSJ'], ["$", 'a.TZSJ']]];
					} else if (YZLX == 3) {
						cnd = ['and', cnd, ['eq', ['$', 'a.TZSJ'], ["$", 'null']]];
					} else if (YZLX == 4) {
						cnd = ['and', cnd,
								['lt', ['$', 'a.KSSJ'], ["$", 'a.TZSJ']]];
					}
				}
				if(XMLX!=null && XMLX!=""){
					if(XMLX==1){
						cnd = ['and', cnd,['gt', ['$', 'a.YPXH'], ["i", 0]]];
						cnd = ['and', cnd,['gt', ['$', 'a.YPCD'], ["i", 0]]];
					}else if (XMLX==2){
						cnd = ['and', cnd,['gt', ['$', 'a.YPXH'], ["i", 0]]];
						cnd = ['and', cnd,['eq', ['$', 'a.YPCD'], ["i", 0]]];
					}else if (XMLX==3){
						cnd = ['and', cnd,['eq', ['$', 'a.YPXH'], ["i", 0]]];
						cnd = ['and', cnd,['eq', ['$', 'a.YPCD'], ["i", 0]]];
					}
				}
				this.listModule.requestData.cnd = cnd;
				this.listModule.refresh();
			},
			doPrint : function() {
			var form = this.form.getForm();
				KSSJ = form.findField("KSSJ").getValue();
				if (KSSJ) {
					if (KSSJ.length != 10) {
						Ext.MessageBox.alert("提示", "开始日期格式不对", function() {
									form.findField("KSSJ").focus(false, 100);
								}, this);
						return;
					}
					if (new Date(KSSJ) == 'Invalid Date') {
						Ext.MessageBox.alert("提示", "开始日期格式不对", function() {
									form.findField("KSSJ").focus(false, 100);
								}, this);
						return;
					}
				}
				TZSJ = form.findField("TZSJ").getValue();
				if (TZSJ) {
					if (TZSJ.length != 10) {
						Ext.MessageBox.alert("提示", "终止日期格式不对", function() {
									form.findField("TZSJ").focus(false, 100);
								}, this);
						return;
					}
					if (new Date(TZSJ) == 'Invalid Date') {
						Ext.MessageBox.alert("提示", "终止日期格式不对", function() {
									form.findField("TZSJ").focus(false, 100);
								}, this);
						return;
					}
				}

				if (KSSJ != null && TZSJ != null && KSSJ != "" && TZSJ != ""
						&& KSSJ > TZSJ) {
					Ext.MessageBox.alert("提示", "开始日期不能大于终止日期");
					return;
				}
				YZLX = form.findField("YZLX").getValue();
				FYFS = form.findField("FYFS").getValue();
				XMLX = form.findField("XMLX").getValue();
				var module = this.createModule("PatientMedicalAdviceQueryPrint",
						this.refPatientMedicalAdviceQueryPrint);
				module.dateFrom=KSSJ;
				module.dateTo=TZSJ;
				debugger;
				module.SRKS= this.mainApp['phis'].wardId;
				module.YZLX=YZLX;
				module.FYFS=FYFS;
				module.XMLX=XMLX;				
				module.initPanel();
				module.doPrint();
			}
		});