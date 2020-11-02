

$package("phis.application.reg.script");

/**
 * 项目组套维护module shiwy 2012.05.25
 */
$import("phis.script.SimpleModule");

phis.application.reg.script.DepartmentSchedulingModule = function(cfg) {
	this.registrationDateValue = 0;
	this.dutyCategoryValue = 0;
	phis.application.reg.script.DepartmentSchedulingModule.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.reg.script.DepartmentSchedulingModule,
		phis.script.SimpleModule, {
			initPanel : function() {// 下拉框值
				var registrationDate = [{
							value : "1",
							text : "星期日"
						}, {
							value : "2",
							text : "星期一"
						}, {
							value : "3",
							text : "星期二"
						}, {
							value : "4",
							text : "星期三"
						}, {
							value : "5",
							text : "星期四"
						}, {
							value : "6",
							text : "星期五"
						}, {
							value : "7",
							text : "星期六"
						}];

				var registrationDateStore = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : registrationDate
						});
				var registrationDateCombox = new Ext.form.ComboBox({
							id : "week",
							store : registrationDateStore,
							valueField : "value",
							displayField : "text",
							mode : 'local',
							triggerAction : 'all',
							emptyText : "请选择",
							selectOnFocus : true,
							width : 100,
							allowBlank : false
//							listeners : {
//								select : function(combox, record, index) {
//									this.registrationDateValue = record.get("value");
//								}
//							}
						});
				registrationDateCombox.setValue(new Date().getDay() + 1);
				this.registrationDateValue = new Date().getDay() + 1;
				registrationDateCombox.on("beforeselect", this.onBeforeselect,
						this);
				registrationDateCombox.on("select", this.onSelect, this);
				var dutyCategory = [{
							value : "1",
							text : "上午"
						}, {
							value : "2",
							text : "下午"
						}]
				var dutyCategoryStore = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : dutyCategory
						});
				var dutyCategoryCombox = new Ext.form.ComboBox({
							id : "time",
							store : dutyCategoryStore,
							valueField : "value",
							displayField : "text",
							mode : 'local',
							triggerAction : 'all',
							emptyText : "请选择",
							selectOnFocus : true,
							width : 100,
							allowBlank : false
//							listeners : {
//								select : function(combox, record, index) {
//									this.dutyCategoryValue = record.get("value");
//								}
//							}
						});
				if (new Date().getHours() < 12) {
					dutyCategoryCombox.setValue(1);
					this.dutyCategoryValue = 1;
				} else {
					dutyCategoryCombox.setValue(2);
					this.dutyCategoryValue = 2;
				}
				dutyCategoryCombox
						.on("beforeselect", this.onBeforeselect, this);
				dutyCategoryCombox.on("select", this.onSelect, this);
				var saveBoutton = new Ext.Button({
							xtype : "button",
							text : "保存",
							iconCls : "save",
							scope : this,
							handler : this.doSave
						});
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : ['挂号日期', registrationDateCombox, '值班类别',
									dutyCategoryCombox, saveBoutton],
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'center',
										height : 200,
										width : 470,
										items : this
												.getDepartmentSchedulingList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getDepartmentSchedulingList : function() {
				this.departmentSchedulingList = this.createModule(
						"getDepartmentSchedulingList",
						this.refDepartmentSchedulingList);
				this.departmentSchedulingList.requestData.cnd = this.registrationDateValue
						+ ":" + this.dutyCategoryValue;
				this.departmentSchedulingList.requestData.pageNo = 1;
				this.departmentSchedulingList.requestData.serviceId = "departmentSchedulingService";
				this.departmentSchedulingList.requestData.serviceAction = "getModelDataOperation";
				this.departmentSchedulingList.requestData.schema = "MS_KSPB";

				this.departmentSchedulingGrid = this.departmentSchedulingList
						.initPanel();
				return this.departmentSchedulingGrid;
			},
			onBeforeselect : function(item, record, e) {
				this.beforeswitch();
			},
			onSelect : function(item, record, e) {
				if(item.id=="week"){
					this.registrationDateValue = record.get("value");
				}else if(item.id=="time"){
					this.dutyCategoryValue = record.get("value")
				}
				this.departmentSchedulingList.requestData.cnd = this.registrationDateValue
						+ ":" + this.dutyCategoryValue;
				this.departmentSchedulingList.requestData.pageNo = 1;
				this.departmentSchedulingList.requestData.serviceId = "phis.departmentSchedulingService";
				this.departmentSchedulingList.requestData.serviceAction = "getModelDataOperation";
				this.departmentSchedulingList.requestData.schema = "MS_KSPB";

				this.departmentSchedulingList.loadData();
			},
			doSave : function(item, e) {
				var store = this.departmentSchedulingGrid.getStore();
				var n = store.getCount()
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
//					alert(1)
//					if (r.get("GHRQ") == "0" && r.get("ZBLB") == "0") {
//						alert(2)
						r.set('GHRQ', this.registrationDateValue);
						r.set('ZBLB', this.dutyCategoryValue);
//					}
					if (r.get("GHXE") == null) {
						r.data.GHXE = 0;

					}
					if (r.get("YYXE") == null) {
						r.data.YYXE = 0;
					}
					data.push(r.data)
				}
				this.departmentSchedulingGrid.el.mask("正在保存数据...",
						"x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceSave,
							serviceAction : this.serviceActionSave,
							body : data
						}, function(code, msg, json) {
							this.departmentSchedulingGrid.el.unmask()
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.departmentSchedulingList.loadData();
						}, this)
				this.departmentSchedulingList.store.rejectChanges();
			},
			beforeswitch : function() {
				// 判断grid中是否有修改的数据没有保存
				if (this.departmentSchedulingList.store.getModifiedRecords().length > 0) {
					for (var i = 0; i < this.departmentSchedulingList.store
							.getCount(); i++) {
						if (this.departmentSchedulingList.store.getAt(i)
								.get("KSPB") == 1) {
							if (confirm('科室排班数据已经修改，是否保存?')) {
								// this.needToClose = true;
								return this.doSave();
							} else {
								break;
							}
						}
					}
					this.departmentSchedulingList.store.rejectChanges();
				}
				return true;
			}
		})
