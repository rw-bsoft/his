$package("phis.application.ivc.script");
$import("phis.script.SimpleModule", "phis.script.widgets.MyMessageTip",
		"phis.script.util.DateUtil","util.dictionary.TreeDicFactory","util.helper.Helper");
phis.application.ivc.script.ClinicOutpatientExpensesModule = function(cfg) {
	phis.application.ivc.script.ClinicOutpatientExpensesModule.superclass.constructor.apply(this, [ cfg ]);
	},

Ext.extend(phis.application.ivc.script.ClinicOutpatientExpensesModule,
			phis.script.SimpleModule, {
				initPanel : function() {
								if (this.panel) {
									return this.panel;
								}
								var tbar = [];
								// 添加日历控件
								this.dateField1 = new Ext.form.DateField({
									name : 'storeDate',
									minValue : '1900-01-01',
									width : 100,
									allowBlank : false,
									altFormats : 'Y-m-d',
									value : Date.getServerDate(),
									format : 'Y-m-d'
								});
								// 添加日历控
								this.dateField2 = new Ext.form.DateField({
									name : 'storeDate',
									minValue : '1900-01-01',
									width : 100,
									allowBlank : false,
									altFormats : 'Y-m-d',
									value : Date.getServerDate(),
									format : 'Y-m-d'
								});

								tbar = [ '就诊日期:', this.dateField1, '至',
										this.dateField2 ];
								this.lable = new Ext.form.Label({
									id : 'label',
									text : '就诊科室:'
								});
								var depcyofice = this.createDicField({
									"src" : "",
									"defaultIndex" : "0",
									"width" : 120,
									"id" : "phis.dictionary.department_tree_tj",
									"render" : "Tree",
									"filter" : "['and',['eq',['$','item.properties.ORGANIZCODE'],['s','"+this.mainApp.deptRef+"']],['eq',['$','item.properties.OUTPATIENTCLINIC'],['s','1']]]",
									"parentKey" : this.mainApp.deptRef || {},
									"rootVisible" : "true",
									"editable":false
								});
								depcyofice.tree.on("click", this.onCatalogChanage, this);
								depcyofice.tree.on("beforeexpandnode", this.onExpandNode, this);
								depcyofice.tree.on("beforecollapsenode", this.onCollapseNode, this);
								depcyofice.tree.expandAll()// 展开树
								tbar.push(this.lable);
								tbar.push(depcyofice);
								 var combostore = new Ext.data.ArrayStore({
								 	fields: ['id', 'name'],
								 	data: [["", "不选择"],[this.mainApp.uid, this.mainApp.uname]]
								 });
								 yslable = new Ext.form.Label({
									id : 'ClinicOutyslabel',
									text : '就诊医生:'
								});
								tbar.push(yslable);
								 var depcydoctor = new Ext.form.ComboBox({
								 	width : 100,
								 	fieldLabel: '就诊医生:',
								 	store: combostore,
								 	displayField: 'name',
								 	valueField: 'id',
								 	triggerAction: 'all',
								 	emptyText: '请选择...',
								 	allowBlank: false,
								 	blankText: '请选择',
									editable: false,
								  	mode: 'local'
								 });
								 depcydoctor.on('select', function () {
								 	this.jzys=depcydoctor.getValue();
								 },this)
								tbar.push(depcydoctor);
								
								tbar.push({
									xtype : "button",
									text : "查询",
									iconCls : "query",
									scope : this,
									handler : this.doQuery,
									disabled : false
								});
								var panel = new Ext.Panel({
									border : false,
									width : this.width,
									height : this.height,
									frame : true,
									layout : 'border',
									defaults : {
										border : false
									},
									items : [ {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'north',
										width : 960,
										height : 250,
										items : this.getUpList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 960,
										items : this.getDowList()
									} ],
									tbar : (tbar || []).concat(this
											.createButtons())
								});
								this.panel = panel;
								return panel;
							},
							findChildNodes : function(node, value) {
								var childnodes = node.childNodes;
								for (var i = 0; i < childnodes.length; i++) {
									var nd = childnodes[i];
									value.push(nd.id);
									if (nd.hasChildNodes()) {
										this.findChildNodes(nd, value);
									}
								}
							},
							createDicField : function(dic) {
								var cls = "util.dictionary.";
								if (!dic.render) {
									cls += "Simple";
								} else {
									cls += dic.render
								}
								cls += "DicFactory"
								$import(cls)
								var factory = eval("(" + cls + ")")
								var field = factory.createDic(dic)
								return field
							},
							onCatalogChanage : function(node, e) {
								this.depValue = [];
								var value = [];
								if(!isNaN(node.id)){
									value.push(node.id);
								}
								if (node.hasChildNodes()) {// 有子节点
									// 得到所有子节点
									this.findChildNodes(node, value);
								}
								this.depValue = value;
							},
							onExpandNode : function(node) {
								if (node.getDepth() > 0 && !node.type) {
									node.setIcon(ClassLoader.appRootOffsetPath + "resources/phis/resources/images/open.png");
									// 判断node是否有type
								}
							},
							onCollapseNode : function(node) {
								if (node.getDepth() > 0 && !node.type) {
									node.setIcon(ClassLoader.appRootOffsetPath + "resources/phis/resources/images/close.png");
								}
							},
							getUpList : function() {
								this.uplist = this.createModule("uplist",this.refUplist);
								this.uplist.loadData(this.dateField1.value,this.dateField2.value, 0);
								this.uplist.oper=this;
								this.uplist.on("queryProject", this.onQueryProject,
										this);
								return this.uplist.initPanel();
							},
							getDowList : function() {
								this.dowlist = this.createModule("dowlist",
										this.refDowList);
								return this.dowlist.initPanel();
							},
							doQuery : function() {
								var strdate=new Date().format('Y-m-d');
								var enddate=new Date().format('Y-m-d');
								var ksdm=0;
								var jzys="";
								if(this.dateField1.value){
									strdate=this.dateField1.value;
								}
								if(this.dateField2.value){
									enddate=this.dateField2.value;
								}
								if(this.jzys){
									jzys=this.jzys;
								}
								if(this.depValue){
									ksdm=this.depValue;
									this.uplist.loadData(strdate, enddate, ksdm,jzys);
								}else{
									Ext.MessageBox.alert("提示", "请选择就诊科室");
									return;
								}
							},
							onQueryProject : function() {
								var r = this.uplist.getSelectedRecord()
								if (r == null) {
									this.dowlist.loadData(0,0);
									return
								}
								var jzys="";
								if(this.jzys){
									jzys=this.jzys;
								}
								this.dowlist.loadData(r.get("JZSJ"),r.get("KSDM"),jzys);
							},
							doCancel:function(){
								this.uplist.cancel();
							}
						});