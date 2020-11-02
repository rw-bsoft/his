

/**
 * 医生排班功能
 * 
 * @author liyl
 */
$package("phis.application.reg.script");

$import("phis.script.SimpleModule");

phis.application.reg.script.RegistrationDoctorPlanModule = function(cfg) {
	this.weekInt = null;
	this.dayInt = null;
	this.weekDate = new Date().format('Y-m-d');
	this.now = new Date();
	// Ext.apply(this, app.modules.common);
	phis.application.reg.script.RegistrationDoctorPlanModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.reg.script.RegistrationDoctorPlanModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				this.now = new Date();
				this.weekDate = new Date().format('Y-m-d');
				if (this.now.getHours() < 12) {
					this.dayInt = 1;
				} else {
					this.dayInt = 2;
				}
				if (this.panel) {
					return this.panel;
				}
				var actions = this.actions
				var bar = [];
				var cfg = {
					xtype : 'buttongroup',
					defaults : {
						scale : 'large'
					}
				};
				var weekBar = [];
				var dateBar = [];
				var n = this.now.getDay();
				for (var i = 0; i < actions.length; i++) {
					var ac = actions[i];
					var start = new Date();
					start.setDate(this.now.getDate() - n + i);
					start = start.dateFormat('m.d');

					var titleWeek = '';
					var titleDay = '';
					if (ac.name != "上午" && ac.name != "下午") {
						titleWeek = ac.name + "(" + start + ")";
					} else {
						titleDay = ac.name;
					}
					var c = {
						text : titleWeek + titleDay,
						exCfg : ac,
						id : ac.id,
						iconCls : ac.iconCls || ac.id,
						enableToggle : true,
						toggleGroup : ac.properties.group,
						allowDepress : false,
						toggleHandler : this.onToggleHandler,
						scope : this
					}; 
					if (ac.properties.group == "week") {
						weekBar.push(c, '-');
					}
					if (ac.properties.group == "date") {
						dateBar.push(c, '-');
					}
				}
				bar.push({
							xtype : "button",
							text : "上周",
							scope : this,
							iconCls : "before",
							handler : this.onTheCircumference
						});
				weekBar.push({
							xtype : "button",
							text : "下周",
							scope : this,
							iconCls : "after",
							handler : this.underZhou
						});
				bar.push(cfg, weekBar, '->',cfg, dateBar)
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							// activeItem : this.activateId,
							defaults : {
								border : false
							},
							tbar : bar,
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										// height : 380,
										// width : '60%',
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'east',
										width : '25%',
										// height : 380,
										items : this.getYSList()
									}]
						});
				this.panel = panel;
				panel.on('afterrender', this.copyYSPB, this);
				return panel;
			},
			copyYSPB : function() {
				var now = new Date();
				var beginDay = now.add(Date.DAY, -now.getDay())
						.format('Y-m-d H:m:s');// 本周第一天（周日）的日期
				var endDay = now.add(Date.DAY, 7 - now.getDay())
						.format('Y-m-d H:m:s');// 下周第一天的日期
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "registrationDoctorPlanService",
							serviceAction : "save_copyYSPB",
							beginDay : beginDay,
							endDay : endDay
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.copyYSPB);
				}
			},
			afterOpen : function() {
				var dat = new Date();
				var n = dat.getDay() + 1;
				var barId = null;
				switch (n) {
					case 1 :
						barId = "sunday";
						break;
					case 2 :
						barId = "monday";
						break;
					case 3 :
						barId = "tuesday";
						break;
					case 4 :
						barId = "wednesday";
						break;
					case 5 :
						barId = "thursday";
						break;
					case 6 :
						barId = "friday";
						break;
					case 7 :
						barId = "saturday";
						break;
				}
				this.panel.getTopToolbar().findById(barId).toggle(true, false);
//				for(var i=0; i <this.panel.getTopToolbar().items.getCount();i++){
//					var obj = this.panel.getTopToolbar().items.item(i);
//					alert(this.panel.getTopToolbar().items.item(i))
					
//					if(this.panel.getTopToolbar().items.item(i).name=="maxCount"){
//						this.panel.getTopToolbar().items.item(i).toggle(true, false);
//					}
//				}
				if (this.schistospmaRecordGrid) {
					var items = this.schistospmaRecordGrid.getTopToolbar().items;
					if (dat.getHours() < 12) {
						items.item(3).toggle(true, false);
					} else {
						items.item(4).toggle(true, false);
					}
				}
				// 拖动操作
				var _ctx = this;
				var firstGrid = this.schistospmaRecordList.grid;
				var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
				var firstGridDropTarget = new Ext.dd.DropTarget(
						firstGridDropTargetEl, {
							ddGroup : 'firstRegDDGroup',
							notifyDrop : function(ddSource, e, data) {
								_ctx.isModify = true;
								var records = ddSource.dragData.selections;
								Ext.each(records, ddSource.grid.store.remove,
										ddSource.grid.store);
								for (var i = 0; i < records.length; i++) {
									var record = records[i];
									_ctx.weekDate = Date.parseDate(
											_ctx.weekDate, 'Y-m-d');
									_ctx.weekDate = _ctx.weekDate
											.format('Y-m-d');
									record.set("GZRQ", _ctx.weekDate);
									record.set("KSDM",
											_ctx.yslist.officeDataCombox
													.getValue());
									record.set("KSDM_text",
											_ctx.yslist.officeDataCombox
													.getRawValue());
									record.set("YSDM", record.get('PERSONID'));
									record.set("YSDM_text", record.get('PERSONNAME'));
									record.set("ZBLB", _ctx.dayInt == 1
													? _ctx.dayInt
													: 2);
									record.set("ZBLB_text", _ctx.dayInt == 1
													? "上午"
													: "下午");
									record.set("JGID", _ctx.mainApp.deptId);
									record.set("GHXE", 0);
									record.set("YGRS", 0);
									record.set("YYRS", 0);
									record.set("YYXE", 0);
									record.set("TGBZ", 0);
									record.set("JZXH", 0);
								}
								firstGrid.store.add(records);
								firstGrid.store.sort('YSDM', 'ASC');
								return true
							}
						});
				// var secondGrid = this.yslist.grid;
				// var secondGridDropTargetEl =
				// secondGrid.getView().scroller.dom;
				// var secondGridDropTarget = new Ext.dd.DropTarget(
				// secondGridDropTargetEl, {
				// ddGroup : 'secondRegDDGroup',
				// notifyDrop : function(ddSource, e, data) {
				// _ctx.isModify = true;
				// var records = ddSource.dragData.selections;
				// Ext.each(records, ddSource.grid.store.remove,
				// ddSource.grid.store);
				// for (var i = 0; i < records.length; i++) {
				// var record = records[i];
				// _ctx.weekDate = Date.parseDate(
				// _ctx.weekDate, 'Y-m-d');
				// _ctx.weekDate = _ctx.weekDate
				// .format('Y-m-d');
				// record.set("PERSONID", record.get('YSDM'));
				// record.set("YGXM", record.get('YSDM_text'));
				// }
				// secondGrid.store.add(records);
				// secondGrid.store.sort('YSDM', 'ASC');
				// return true
				// }
				// });
			},
			getList : function() {
				var module = this.createModule("schistospmaRecordList",
						this.refList);
				this.schistospmaRecordList = module;
				module.opener = this;
				// 左侧list刷新后，调用右侧list刷新
				module.on("loadData", this.afterListLoadData, this)
				var time = this.now.getTime();
				var d = new Date();
				d.setTime(time);
				var nowd = d.format('Y-m-d');
				var nowhours = 0;
				if (d.getHours() < 12) {
					nowhours = 1;
				} else {
					nowhours = 2
				}
				module.requestData.cnd = ['and',
						['eq', ['$', "str(GZRQ,'yyyy-MM-dd')"], ['s', nowd]],
						['eq', ['$', "ZBLB"], ['i', nowhours]]];
				var list = module.initPanel();
				this.schistospmaRecordGrid = list;
				module.loadData();
				module.on("afterRemove", this.onAfterRemove, this);
				// module.on('doSave', this.doSave, this);
				return list;
			},
			getYSList : function() {
				var module = this.createModule("yslist", this.refYsList);
				this.yslist = module;
				var list = module.initPanel();
				this.yslist.getOffice();
				module.on('loadData', this.loadData, this);
				module.on("doctorChoose", this.doctorChoose, this);
				// module.on("doctorCancleChoose", this.doctorCancleChoose,
				// this);
				return list;
			},
			afterListLoadData : function() {
				this.yslist.loadData();
			},
			onAfterRemove : function() {
				if (this.yslist) {
					var ksys = this.yslist.getOffice();
					if (ksys.length == 0) {
						this.yslist.initCnd = ['eq', ['$', 'PERSONID'], ['s', 0]];
						this.yslist.requestData.cnd = ['eq', ['$', 'PERSONID'],
								['s', 0]];
					} else {
						this.yslist.initCnd = ['and',
								['ne', ['$', 'LOGOFF'], ['i', 1]],
								['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
						this.yslist.requestData.cnd = ['and',
								['ne', ['$', 'LOGOFF'], ['i', 1]],
								['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
					}
					this.yslist.loadData();
				}
			},
			doctorChoose : function(grid, record) {
				// 判断是否选中上午或者下午
				Ext.each(record, grid.store.remove, grid.store);
				var lastIndex = grid.getSelectionModel().lastActive;
				this.midiModules['yslist'].selectRow(lastIndex);
				var tmp_record = new Ext.data.Record;
				this.weekDate = Date.parseDate(this.weekDate, 'Y-m-d');
				this.weekDate = this.weekDate.format('Y-m-d');
				var list = this.midiModules["schistospmaRecordList"];
				tmp_record.set("GZRQ", this.weekDate);
				tmp_record.set("KSDM", record.get('KSDM'));
				tmp_record.set("KSDM_text", record.get('KSDM_text'));
				tmp_record.set("YSDM", record.get('PERSONID'));
				tmp_record.set("YSDM_text", record.get('PERSONNAME'));
				tmp_record.set("ZBLB", this.dayInt == 1 ? this.dayInt : 2);
				tmp_record.set("ZBLB_text", this.dayInt == 1 ? "上午" : "下午");
				tmp_record.set("JGID", this.mainApp['phisApp'].deptId);
				tmp_record.set("GHXE", 0);
				tmp_record.set("YGRS", 0);
				tmp_record.set("YYRS", 0);
				tmp_record.set("YYXE", 0);
				tmp_record.set("TGBZ", 0);
				tmp_record.set("JZXH", 0);
				list.grid.getStore().add(tmp_record);
				list.grid.getStore().sort('YSDM', 'ASC');
			},
			loadData : function(store) {
				store.filterBy(function(f_record, id) {
					var count = this.schistospmaRecordList.grid.store
							.getCount();
					var ksdm = this.yslist.officeDataCombox.getValue()
					for (var i = 0; i < count; i++) {
						if (f_record.data.PERSONID == this.schistospmaRecordList.store
								.getAt(i).data.YSDM
								&& ksdm == this.schistospmaRecordList.store
										.getAt(i).data.KSDM)
							return false;
					}
					return true;
				}, this);
			},
			loadModuleCfg : function(id) {
				var result = phis.script.rmi.miniJsonRequestSync({
							serviceId : "moduleConfigLocator",
							id : id
						})
				return result.json.body;
			},
			onToggleHandler : function(button, state) {
				if (state) {
					this.beforeswitch();
					if (button.toggleGroup == "week") {
						week = button.id;
						switch (week) {
							case "sunday" :
								this.weekInt = 1;
								break;
							case "monday" :
								this.weekInt = 2;
								break;
							case "tuesday" :
								this.weekInt = 3;
								break;
							case "wednesday" :
								this.weekInt = 4;
								break;
							case "thursday" :
								this.weekInt = 5;
								break;
							case "friday" :
								this.weekInt = 6;
								break;
							case "saturday" :
								this.weekInt = 7;
								break;

						}
						var list = this.midiModules["schistospmaRecordList"];
						// 调用科室排班list
						var time = this.now.getTime();
						var d = new Date();
						d.setTime(time);
						var day = d.getDay() + 1;
						d = d.add('d', this.weekInt - day);
						var hours = 0;
						this.weekDate = d.format('Y-m-d');
						this.yslist.weekDate = this.weekDate;
						var ksys = this.yslist.getOffice();
						if (ksys.length == 0) {
							this.yslist.initCnd = ['eq', ['$', 'PERSONID'],
									['s', 0]];
							this.yslist.requestData.cnd = ['eq', ['$', 'PERSONID'],
									['s', 0]];
						} else {
							this.yslist.initCnd = ['and',
									['ne', ['$', 'LOGOFF'], ['i', 1]],
									['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
							this.yslist.requestData.cnd = ['and',
									['ne', ['$', 'LOGOFF'], ['i', 1]],
									['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
						}
						if (this.dayInt) {
							hours = this.dayInt;
						} else {
							if (d.getHours() < 12) {
								hours = 1;
							} else {
								hours = 2
							}
						}
						if (list) {
							list.requestData.cnd = [
									'and',
									['eq', ['$', "str(GZRQ,'yyyy-MM-dd')"],
											['s', d.format('Y-m-d')]],
									['eq', ['$', "ZBLB"], ['i', hours]]];

							list.loadData();
						}
					} else {
						var list = this.midiModules["schistospmaRecordList"];
						// if (list.grid.getStore().data.length > 0) {
						// var groups = list.grid.getView().getGroups();
						if (button == "morning") {
							this.dayInt = 1;
							if (list) {
								list.requestData.cnd = [
										'and',
										['eq', ['$', "str(GZRQ,'yyyy-MM-dd')"],
												['s', this.weekDate]],
										['eq', ['$', "ZBLB"], ['i', 1]]];
								list.loadData();
								this.yslist.dayInt = this.dayInt;
								var ksys = this.yslist.getOffice();
								if (ksys.length == 0) {
									this.yslist.initCnd = ['eq', ['$', 'PERSONID'],
											['s', 0]];
									this.yslist.requestData.cnd = ['eq',
											['$', 'PERSONID'], ['s', 0]];
								} else {
									this.yslist.initCnd = ['and',
											['ne', ['$', 'LOGOFF'], ['i', 1]],
											['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
									this.yslist.requestData.cnd = ['and',
											['ne', ['$', 'LOGOFF'], ['i', 1]],
											['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
								}
							}
							/*
							 * list.grid.getView() .toggleGroup(groups[0],
							 * true); list.grid.getView().toggleGroup(groups[1],
							 * false);
							 */
						} else {
							this.dayInt = 2;
							if (list) {
								list.requestData.cnd = [
										'and',
										['eq', ['$', "str(GZRQ,'yyyy-MM-dd')"],
												['s', this.weekDate]],
										['eq', ['$', "ZBLB"], ['i', 2]]];

								list.loadData();
								this.yslist.dayInt = this.dayInt;
								var ksys = this.yslist.getOffice();
								if (ksys.length == 0) {
									this.yslist.initCnd = ['eq', ['$', 'PERSONID'],
											['s', 0]];
									this.yslist.requestData.cnd = ['eq',
											['$', 'PERSONID'], ['s', 0]];
								} else {
									this.yslist.initCnd = ['and',
											['ne', ['$', 'LOGOFF'], ['i', 1]],
											['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
									this.yslist.requestData.cnd = ['and',
											['ne', ['$', 'LOGOFF'], ['i', 1]],
											['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
								}
							}
							/*
							 * list.grid.getView().toggleGroup(groups[0],
							 * false); list.grid.getView()
							 * .toggleGroup(groups[1], true);
							 */
						}
						// }
					}
				} else {
					if (button.toggleGroup == "week") {
						this.weekInt = null;
					} else if (button.toggleGroup == "date") {
						this.dayInt = null;
					}
				}
				// this.yslist.loadData();
				if (this.schistospmaRecordGrid) {
					var items = this.schistospmaRecordGrid.getTopToolbar().items;
					if (this.dayInt == 1) {
						items.item(3).toggle(true, false);
						items.item(4).toggle(false, false);
					} else {
						items.item(4).toggle(true, false);
						items.item(3).toggle(false, false);
					}
				}
			},
			onTheCircumference : function() {
				this.beforeswitch();
				var time = this.now.getTime() - (7 * 24 * 60 * 60 * 1000);
				var dayInts = 0;
				this.now.setTime(time);
				var btns = this.panel.getTopToolbar();
				for (var i = 0; i < this.actions.length; i++) {
					var ac = this.actions[i];
					var start = new Date();
					start.setTime(time);
					var n = this.now.getDay();
					var btn = btns.find("id", ac.id)[0];
					start.setDate(this.now.getDate() - n + i);
					start = start.dateFormat('m.d');
					btn.setText(btn.getText().replace(/\d\d.\d\d/, start));
				}
				if (this.dayInt) {
					dayInts = this.dayInt
				} else {
					if (this.now.getHours() < 12) {
						dayInts = 1;
					} else {
						dayInts = 2
					}
				}
				this.weekDate = Date.parseDate(this.weekDate, 'Y-m-d');
				var changeTime = this.weekDate.getTime()
						- (7 * 24 * 60 * 60 * 1000);
				this.weekDate.setTime(changeTime);
				this.weekDate = this.weekDate.format('Y-m-d');
				this.schistospmaRecordList.requestData.cnd = [
						'and',
						['eq', ['$', "str(GZRQ,'yyyy-MM-dd')"],
								['s', this.weekDate]],
						['eq', ['$', "ZBLB"], ['i', dayInts]]];
				this.schistospmaRecordList.loadData();

				this.yslist.now = this.now;
				var ksys = this.yslist.getOffice();
				if (ksys.length == 0) {
					this.yslist.initCnd = ['eq', ['$', 'PERSONID'], ['s', 0]];
					this.yslist.requestData.cnd = ['eq', ['$', 'PERSONID'],
							['s', 0]];
				} else {
					this.yslist.initCnd = ['and',
							['ne', ['$', 'LOGOFF'], ['i', 1]],
							['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
					this.yslist.requestData.cnd = ['and',
							['ne', ['$', 'LOGOFF'], ['i', 1]],
							['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
				}
				this.yslist.loadData();
			},
			underZhou : function() {
				this.beforeswitch();
				var time = this.now.getTime() + (7 * 24 * 60 * 60 * 1000);
				var dayInts = 0;
				this.now.setTime(time);
				var btns = this.panel.getTopToolbar();
				for (var i = 0; i < this.actions.length; i++) {
					var ac = this.actions[i];
					var start = new Date();
					start.setTime(time);
					var n = this.now.getDay();
					var btn = btns.find("id", ac.id)[0];
					start.setDate(this.now.getDate() - n + i);
					start = start.dateFormat('m.d');
					btn.setText(btn.getText().replace(/\d\d.\d\d/, start));
				}
				if (this.dayInt) {
					dayInts = this.dayInt
				} else {
					if (this.now.getHours() < 12) {
						dayInts = 1;
					} else {
						dayInts = 2
					}
				}
				this.weekDate = Date.parseDate(this.weekDate, 'Y-m-d');
				var changeTime = this.weekDate.getTime()
						+ (7 * 24 * 60 * 60 * 1000);
				this.weekDate.setTime(changeTime);
				this.weekDate = this.weekDate.format('Y-m-d');
				this.schistospmaRecordList.requestData.cnd = [
						'and',
						['eq', ['$', "str(GZRQ,'yyyy-MM-dd')"],
								['s', this.weekDate]],
						['eq', ['$', "ZBLB"], ['i', dayInts]]];
				this.schistospmaRecordList.loadData();
				this.yslist.now = this.now;
				var ksys = this.yslist.getOffice();
				if (ksys.length == 0) {
					this.yslist.initCnd = ['eq', ['$', 'PERSONID'], ['s', 0]];
					this.yslist.requestData.cnd = ['eq', ['$', 'PERSONID'],
							['s', 0]];
				} else {
					this.yslist.initCnd = ['and',
							['ne', ['$', 'LOGOFF'], ['i', 1]],
							['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
					this.yslist.requestData.cnd = ['and',
							['ne', ['$', 'LOGOFF'], ['i', 1]],
							['eq', ['$', 'PRESCRIBERIGHT'], ['s', 1]]];
				}
				this.yslist.loadData();
			},
			beforeswitch : function() {
				// 判断grid中是否有修改的数据没有保存
				if (this.schistospmaRecordList.store.getModifiedRecords().length > 0) {
					for (var i = 0; i < this.schistospmaRecordList.store
							.getCount(); i++) {
						if (this.schistospmaRecordList.store.getAt(i)
								.get("KSDM")) {
							if (confirm('医生排班数据已经修改，是否保存?')) {
								// this.needToClose = true;
								return this.schistospmaRecordList.doSave();
							} else {
								break;
							}
						}
					}
					this.schistospmaRecordList.store.rejectChanges();
				}
				return true;
			}
		});