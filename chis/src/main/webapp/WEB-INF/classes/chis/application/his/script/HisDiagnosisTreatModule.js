$package("chis.application.his.script")

$import("chis.script.BizModule");
chis.application.his.script.HisDiagnosisTreatModule = function(cfg) {
	chis.application.his.script.HisDiagnosisTreatModule.superclass.constructor
			.apply(this, [cfg]);

},

Ext.extend(chis.application.his.script.HisDiagnosisTreatModule,
		chis.script.BizModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var radio = [];
				var c1 = {
					boxLabel : "医嘱",
					inputValue : "1",
					name : "treat",
					clearCls : true
				};
				radio.push(c1);
				c2 = {
					boxLabel : "费用",
					inputValue : "2",
					name : "treat",
					clearCls : true
				};
				radio.push(c2);
				var radioGroup = new Ext.form.RadioGroup({
							width : 110,
							disabled : false,
							allowBlank : false,
							value : "1",
							items : radio,
							listeners : {
								change : this.radioGroupChange,
								scope : this
							}
						});
				this.radioGroup = radioGroup;
				var checkBox = [];
				config = {
					boxLabel : "长期",
					inputValue : "3",
					name : "treatBox",
					checked : true,
					clearCls : true
				};
				checkBox.push(config);
				config = {
					boxLabel : "临时",
					inputValue : "4",
					name : "treatBox",
					checked : true,
					clearCls : true
				};
				checkBox.push(config);
				config = {
					boxLabel : "药品",
					inputValue : "5",
					name : "treatBox",
					checked : true,
					clearCls : true
				};
				checkBox.push(config);
				config = {
					boxLabel : "项目",
					inputValue : "6",
					name : "treatBox",
					checked : true,
					clearCls : true
				};
				checkBox.push(config);
				var checkBoxGroup = new Ext.form.CheckboxGroup({
							width : 220,
							disabled : false,
							items : checkBox,
							listeners : {
								change : this.checkboxChange,
								scope : this
							}
						});
				this.checkBoxGroup = checkBoxGroup;
				var topButtonBar = [];
				topButtonBar.push(radioGroup);
				// topButtonBar.push('-');
				topButtonBar.push(checkBoxGroup);
				var topButton = this.createButtons();
				if (topButton.length > 0) {
					topButtonBar.push(topButton);
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
							tbar : topButtonBar,
							items : [{
										layout : "card",
										border : false,
										id : "ZLXXLB",
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
			getRecordsList : function() {
				var module = this.createCombinedModule("adviceList",
						this.adviceList);
				module.initCnd = module.requestData.cnd = ['and',
						['eq', ['$', 'a.ZYH'], ['s', this.exContext.ZYH]],
						['eq', ['$', 'b.YZPB'], ['i', 0]]];
				module.autoLoadData = false;
				module.disablePagingTbr = true;
				module.requestData.serviceId = "chis.hospitalDischargeService";
				module.requestData.serviceAction = "hospitalCostDetalsQueryYZGS";
				module.listServiceId = "chis.hospitalDischargeService";
				module.serverParams = {
					serviceAction : "hospitalCostDetalsQueryYZGS"
				}
				this.list1 = module;
				var grid1 = module.initPanel();
				grid1.setHeight(240);
				grid1.setWidth(730);
				this.grid1 = grid1;
				
				var module = this.createCombinedModule("chargeList",
						this.chargeList);
				module.initCnd = module.requestData.cnd = ['and',
						['eq', ['$', 'a.ZYH'], ['s', this.exContext.ZYH]],
						['eq', ['$', 'b.YZPB'], ['i', 0]]];
				module.autoLoadData = false;
				module.disablePagingTbr = true;
				module.requestData.serviceId = "chis.hospitalDischargeService";
				module.requestData.serviceAction = "hospitalCostDetalsQuery";
				module.listServiceId = "chis.hospitalDischargeService";
				module.serverParams = {
					serviceAction : "hospitalCostDetalsQuery"
				}
				this.list2 = module;
				var grid2 = module.initPanel();
				grid1.setHeight(240);
				grid1.setWidth(730);
				this.grid2 = grid2;
				if (this.exContext.ZYH) {
					this.list1.loadData();
					this.list2.loadData();
				}
				return [grid1, grid2];
			},
			radioGroupChange : function(field, newValue, oldValue) {
				if (newValue == null) {
					return;
				}
				if (!this.exContext.ZYH) {
					return;
				}
				if (newValue.inputValue == "2") {
					Ext.getCmp('ZLXXLB').layout.setActiveItem(1);
					this.list2.loadData();
				}
				if (newValue.inputValue == "1") {
					Ext.getCmp('ZLXXLB').layout.setActiveItem(0);
					this.list1.loadData();
				}
			},
			checkboxChange : function(field, newValue, oldValue) {
				if (!this.exContext.ZYH) {
					return;
				}
				var values = field.getValue();
				var valueStr = "";
				for (var i = 0; i < values.length; i++) {
					var val = values[i];
					valueStr += val.inputValue + ",";
				}
				var cnd = [];
				var initCnd = ['and',
						['eq', ['$', 'a.ZYH'], ['s', this.exContext.ZYH]],
						['eq', ['$', 'b.YZPB'], ['i', 0]]];
				cnd = initCnd;
				if (valueStr.indexOf("3") != -1 && valueStr.indexOf("4") != -1) {
					var cnd34 = ['or', ['eq', ['$', 'b.LSYZ'], ['i', 0]],
							['eq', ['$', 'b.LSYZ'], ['i', 1]]];
					cnd.push(cnd34);
				} else if (valueStr.indexOf("3") != -1) {
					var cnd3 = ['eq', ['$', 'b.LSYZ'], ['i', 0]];
					cnd.push(cnd3);
				} else if (valueStr.indexOf("4") != -1) {
					var cnd4 = ['eq', ['$', 'b.LSYZ'], ['i', 1]];
					cnd.push(cnd4);
				}
				if (valueStr.indexOf("5") != -1 && valueStr.indexOf("6") != -1) {
					var cnd56 = ['ge', ['$', 'b.YPLX'], ['i', 0]];
					cnd.push(cnd56);
				} else if (valueStr.indexOf("5") != -1) {
					var cnd5 = ['gt', ['$', 'b.YPLX'], ['i', 0]];
					cnd.push(cnd5);
				} else if (valueStr.indexOf("6") != -1) {
					var cnd6 = ['eq', ['$', 'b.YPLX'], ['i', 0]];
					cnd.push(cnd6);
				}
				this.list1.initCnd = this.list1.requestData.cnd = cnd;
				this.list1.loadData();
				this.list2.initCnd = this.list2.requestData.cnd = cnd;
				this.list2.loadData();
			},
			createButtons : function() {
				var actions = this.actions;
				var buttons = []
				if (!actions) {
					return buttons
				}
				var f1 = 112
				for (var i = 0; i < actions.length; i++) {
					var action = actions[i];
					if (action.hide) {
						continue
					}
					var btn = {
						accessKey : f1 + i + this.buttonIndex,
						text : action.name + "(F" + (i + 1 + this.buttonIndex)
								+ ")",
						ref : action.ref,
						target : action.target,
						cmd : action.delegate || action.id,
						iconCls : action.iconCls || action.id,
						enableToggle : (action.toggle),
						script : action.script,
						handler : this.doAction,
						prop : {},
						scope : this
					}
					Ext.apply(btn.prop, action);
					Ext.apply(btn.prop, action.properties);
					buttons.push(btn)
				}
				return buttons

			}
		});