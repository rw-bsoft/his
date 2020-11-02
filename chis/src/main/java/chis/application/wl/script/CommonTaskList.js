/**
 * 列举近期未完成的任务，慢病及老年人，精神病随访任务列表页面
 * 
 * @author : yaozh
 */
$package("chis.application.wl.script");
$import("chis.script.BizSimpleListView", "chis.script.EHRView");
chis.application.wl.script.CommonTaskList = function(cfg) {
	debugger;
	this.initCnd = [ 'and' , 
	['like', ['$','c.manaUnitId'], ['concat',['s',cfg.mainApp.deptId],['s','%']]],
	['or',['eq', ['$','a.businessType'],['s','1']],['eq', ['$','a.businessType'],['s','2']]]];
	
	//this.initCnd = ['like', ['$','c.manaDoctorId'], ['concat',['s',cfg.mainApp.fds],['s','%']]]
// [
//				'and'
//				[
//						'or',
//							['eq', ['$', 'cast(a.businessType as int)'],
//									['i', 10]],
//							// ['eq', ['$', 'cast(a.businessType as int)'],
//							// ['i', 14]],
//							['lt', ['$', 'cast(a.businessType as int)'],
//									['i', 5]]],
//				['like', ['$', 'c.manaUnitId'], ['s', cfg.mainApp.deptId]]];
	chis.application.wl.script.CommonTaskList.superclass.constructor.apply(
			this, [cfg]);
};

Ext.extend(chis.application.wl.script.CommonTaskList,
		chis.script.BizSimpleListView, {

			initPanel : function(sc) {
				if (this.grid) {
					if (!this.isCombined) {
						this.fireEvent("beforeAddToWin", this.grid)
						this.addPanelToWin();
					}
					return this.grid;
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

				var items = schema.items
				if (!items) {
					return;
				}

				this.store = this.getStore(items)
				this.cm = new Ext.grid.ColumnModel(this.getCM(items))

				var cfg = {
					border : false,
					store : this.store,
					cm : this.cm,
					height : this.height,
					loadMask : {
						msg : '正在加载数据...',
						msgCls : 'x-mask-loading'
					},
					buttonAlign : 'center',
					clicksToEdit : true,
					frame : true,
					plugins : this.rowExpander,
					// stripeRows : true,
					view : this.getGroupingView()
				}

				if (this.gridDDGroup) {
					cfg.ddGroup = this.gridDDGroup;
					cfg.enableDragDrop = true
				}
				var cndbars = this.getCndBar(items)
				if (!this.disablePagingTbr) {
					cfg.bbar = this.getPagingToolbar(this.store)
				} else {
					cfg.bbar = this.bbar
				}
				if (!this.showButtonOnPT) {
					if (this.showButtonOnTop) {
						cfg.tbar = (cndbars.concat(this.tbar || []))
								.concat(this.createButtons())
					} else {
						cfg.tbar = cndbars.concat(this.tbar || [])
						cfg.buttons = this.createButtons()
					}
				}

				this.grid = new this.gridCreator(cfg)
				this.schema = schema;
				this.grid.on("afterrender", this.onReady, this)
				this.grid.on("contextmenu", function(e) {
							e.stopEvent()
						})
				this.grid.on("rowcontextmenu", this.onContextMenu, this)
				this.grid.on("rowdblclick", this.onDblClick, this)
				this.grid.on("rowclick", this.onRowClick, this)
				this.grid.on("keydown", function(e) {
							if (e.getKey() == e.PAGEDOWN) {
								e.stopEvent()
								this.pagingToolbar.nextPage()
								return
							}
							if (e.getKey() == e.PAGEUP) {
								e.stopEvent()
								this.pagingToolbar.prevPage()
								return
							}
						}, this)

				if (!this.isCombined) {
					this.fireEvent("beforeAddToWin", this.grid)
					this.addPanelToWin();
				}
				return this.grid;
			},

			getGroupingView : function() {
				return new Ext.grid.GroupingView({
					showGroupName : true,
					enableNoGroups : false,
					hideGroupedColumn : true,
					enableGroupingMenu : false,
					columnsText : "表格字段",
					groupByText : "使用当前字段进行分组",
					showGroupsText : "表格分组",
					groupTextTpl : "姓名：{[values.rs[0].data.personName]}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "性别：{[values.rs[0].data.sexCode_text]}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "联系电话：{[values.rs[0].data.mobileNumber]}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "地址：{[values.rs[0].data.regionCode_text]}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
							+ "(共{[values.rs.length]}项)",
					getRowClass : this.getRowClass
				});
			},

			getStore : function(items) {
				var o = this.getStoreFields(items);
				var reader = new Ext.data.JsonReader({
							root : "body",
							totalProperty : "totalCount",
							id : o.pkey,
							fields : o.fields
						});
				var url = ClassLoader.serverAppUrl || "";
				var proxy = new Ext.data.HttpProxy({
							url : url + "*.jsonRequest",
							method : "post",
							jsonData : this.requestData
						});
				proxy.on("loadexception", function(proxy, o, response, arg, e) {
							if (response.status == 200) {
								var json = eval("(" + response.responseText
										+ ")");
								if (json) {
									var code = json["x-response-code"];
									var msg = json["x-response-msg"];
									this.processReturnMsg(code, msg,
											this.refresh);
								}
							} else {
								this.processReturnMsg(404, "ConnectionError",
										this.refresh);
							}
						}, this);
				var store = new Ext.data.GroupingStore({
							reader : reader,
							proxy : proxy,
							sortInfo : {
								field : "planDate",
								direction : "ASC"
							},
							groupField : "idCard"
						});
				store.on("beforeload", this.onStoreBeforeLoad, this);
				return store;
			},

			getCndBar : function(items) {
				var fields = [];
				if (!this.enableCnd) {
					return [];
				}
				for (var i = 0; i < items.length; i++) {
					var it = items[i];
					if (!it.queryable || it.queryable == "false") {
						continue;
					};
					fields.push({
								value : it.id,
								text : it.alias
							});
				}

				if (fields.length == 0) {
					return [];
				}
				var store = new Ext.data.JsonStore({
							fields : ['value', 'text'],
							data : fields
						});
				var combox = new Ext.form.ComboBox({
							store : store,
							valueField : "value",
							displayField : "text",
							mode : 'local',
							editable : false,
							triggerAction : 'all',
							emptyText : '选择查询字段',
							selectOnFocus : true,
							width : 100
						});
				combox.on("select", this.onCndFieldSelect, this);
				this.cndFldCombox = combox;

				var cndField = new Ext.form.TextField({
							width : 150,
							selectOnFocus : true,
							name : "dftcndfld"
						});
				this.cndField = cndField;

				var workListDate = null;
				if (this.param)
					workListDate = this.param.date;
				var cfg = {
					width : 120,
					xtype : 'datefield',
					emptyText : "请选择日期",
					enableKeyEvents : true,
					selectOnFocus : true,
					value : workListDate || this.mainApp.serverDate
				};
				var fromDateField = new Ext.form.DateField(cfg);
				this.fromDateField = fromDateField;
				this.fromDateField.on("select", this.onFromDateFieldChanged,
						this);
				this.fromDateField
						.on("blur", this.onFromDateFieldChanged, this);
				this.fromDateField.on("keyup", this.onFromDateFieldChanged,
						this);
				this.fromDateField.on("specialkey", this.onQueryFieldEnter,
						this)

				var cfg = {
					width : 120,
					xtype : 'datefield',
					emptyText : "请选择日期",
					enableKeyEvents : true,
					selectOnFocus : true,
					value : workListDate || this.mainApp.serverDate
				};
				var toDateField = new Ext.form.DateField(cfg);
				this.toDateField = toDateField;
				this.toDateField.on("select", this.onToDateFieldChanged, this);
				this.toDateField.on("blur", this.onToDateFieldChanged, this);
				this.toDateField.on("keyup", this.onToDateFieldChanged, this);
				this.toDateField.on("specialkey", this.onQueryFieldEnter, this)

				var queryBtn = new Ext.Toolbar.SplitButton({
							text : '',
							iconCls : "query",
							menu : new Ext.menu.Menu({
										items : {
											text : "高级查询",
											iconCls : "common_query",
											handler : this.doAdvancedQuery,
											scope : this
										}
									})
						});
				this.queryBtn = queryBtn;
				queryBtn.on("click", this.doCndQuery, this);
				var fromDateValue = this.fromDateField.getValue();
				var toDateValue = this.toDateField.getValue();
				if (fromDateValue) {
					this.requestData.fromDate = fromDateValue.format("Y-m-d");
				}
				if (toDateValue) {
					this.requestData.toDate = toDateValue.format("Y-m-d");
				}
				return [combox, '-', cndField, '-', '从', fromDateField, '-',
						'到', toDateField, '-', queryBtn];
			},

			doCndQuery : function(button, e, addNavCnd) {
				var fromDateValue = this.fromDateField.getValue();
				var toDateValue = this.toDateField.getValue();
				if (fromDateValue) {
					this.requestData.fromDate = fromDateValue.format("Y-m-d");
				}
				if (toDateValue) {
					this.requestData.toDate = toDateValue.format("Y-m-d");
				}
				if (fromDateValue && toDateValue && fromDateValue > toDateValue) {
					Ext.Msg.alert("提示", "起始日期不应在终止日期后");
					return;
				}
				chis.application.wl.script.CommonTaskList.superclass.doCndQuery
						.call(this, null, null, true)
			},

			getPagingToolbar : function(store) {
				var cfg = {
					pageSize : 25,
					store : store,
					requestData : this.requestData,
					displayInfo : true,
					emptyMsg : "无相关记录"
				}

				var values = [{
							key : 0,
							text : "所有任务"
						}, {
							key : 1,
							text : "正常任务"
						}, // @@ 除去紧急和过期任务，包括到期和未到期的任务。
						{
							key : 2,
							text : "紧急任务"
						}, // @@ 剩余天数在设定值以内的任务，将近过期的。
						{
							key : 3,
							text : "过期任务"
						}, // @@ 已超过最晚执行日期的任务。
						{
							key : 4,
							text : "可执行任务"
						}, // @@ 在最早执行日期和最晚执行日期间的任务。
						{
							key : 5,
							text : "未到期任务"
						}, // @@ 未到执行日期的任务。
						{
							key : 6,
							text : "当天任务"
						}];
				var store = new Ext.data.JsonStore({
							fields : ["key", "text"],
							data : values
						});
				var comb = new Ext.form.ComboBox({
							valueField : "key",
							displayField : "text",
							editable : false,
							selectOnFocus : true,
							mode : "local",
							triggerAction : "all",
							emptyText : "请选择",
							width : 200,
							store : store
						});
				comb.on("select", this.radioChanged, this);
				this.comb = comb;
				if (this.param) {
					comb.setValue("6");
				} else {
					comb.setValue("0");
				}
				comb.setWidth(80);
				cfg.items = ["任务类别：", comb];
				var pagingToolbar = new util.widgets.MyPagingToolbar(cfg);
				this.pagingToolbar = pagingToolbar;
				return pagingToolbar;
			},
			
			loadData : function(){
				var taskType = this.comb.getValue();
				this.requestData.taskType = taskType;
				this.requestData.pageNo = 1;
				chis.application.wl.script.CommonTaskList.superclass.loadData.call(this);
			},

			radioChanged : function(r) {
				var taskType = r.getValue();
				this.requestData.taskType = taskType;
				this.requestData.pageNo = 1;
				this.refresh();
			},

			onDblClick : function() {
				this.doVisit();
			},

			doVisit : function() {
				var record = this.getSelectedRecord();
				if (!record) {
					return;
				}
				this.mask("正在加载，请稍候...");
				var empiId = record.get("empiId");
				var recordId = record.get("recordId");
				var planId = record.get("planId");
				var module = this.midiModules["VisitView_EHRView" + "_"
						+ this.getModuleName(record)];
				if (!module) {
					module = new chis.script.EHRView({
								empiId : empiId,
								closeNav : true,
								mainApp : this.mainApp,
								initModules : this.getInitModules(record)
							});
					if (this.getIdsIdName) {
						module.exContext.ids[this.getIdsIdName()] = recordId;
					}
					if (this.getArgsIdName) {
						module.exContext.args[this.getArgsIdName()] = recordId;
					}
					module.exContext.args.selectedPlanId = planId;
					this.midiModules["VisitView_EHRView" + "_"
							+ this.getModuleName(record)] = module;
					module.on("save", this.refresh, this);
				} else {
					module.exContext.ids = {};
					module.exContext.ids.empiId = empiId;
					module.exContext.args.selectedPlanId = planId;
					if (this.getIdsIdName) {
						module.exContext.ids[this.getIdsIdName()] = recordId;
					}
					if (this.getArgsIdName) {
						module.exContext.args[this.getArgsIdName()] = recordId;
					}
					module.refresh();
				}
				module.getWin().show();
				this.unmask();
			},

			getInitModules : function(record) {
				var businessType = record.get("businessType");
				switch (businessType) {
					case "1" :
						return ["C_03"];
					case "2" :
						return ["D_03"];
					case "3" :
						return [];
					case "4" :
						return ["B_06"];
					case "10" :
						return ["P_02"];
					case "11" :
						return ["C_05"];
					case "14" :
						return ["R_02"];
				}
			},

			getModuleName : function(record) {
				return record.get("businessType");
			},

			onReady : function(store, records, ops) {
				this.store.on("datachanged", this.setRowBackground, this)
				chis.application.wl.script.CommonTaskList.superclass.onReady
						.call(this, store, records, ops);
			},

			setRowBackground : function() {
				var girdcount = 0;
				this.store.each(function(r) {
					var remainDays = r.get("remainDays");
					if (remainDays < 0) {
						this.grid.getView().getRow(girdcount).style.backgroundColor = '#ffbeba';
					}
					if (remainDays <= 3 && remainDays >= 0) {
						this.grid.getView().getRow(girdcount).style.backgroundColor = '#ffff80';
					}
					girdcount += 1;
				}, this);
			},

			onFromDateFieldChanged : function(combo) {
				this.toDateField.setMinValue(combo.getValue());
				this.toDateField.validate();
				// if (combo.getValue() > this.toDateField.getValue()) {
				// Ext.Msg.alert("提示", "起始日期不应在终止日期后");
				// }
			},

			onToDateFieldChanged : function(combo) {
				this.fromDateField.setMaxValue(combo.getValue());
				this.fromDateField.validate();
				// if (combo.getValue() < this.fromDateField.getValue()) {
				// Ext.Msg.alert("提示", "终止日期不应在起始日期前");
				// }
			}
		});