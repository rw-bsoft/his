$package("phis.application.sup.script");

$import("phis.script.SimpleModule","phis.script.widgets.Spinner","phis.script.widgets.Strategy");

phis.application.sup.script.StorageOfMaterialsListModule = function(cfg) {
	cfg.width = window.screen.width / 2;
	phis.application.sup.script.StorageOfMaterialsListModule.superclass.constructor
			.apply(this, [cfg]);
	this.on('doSave', this.doSave, this);
}
Ext.extend(phis.application.sup.script.StorageOfMaterialsListModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				if (this.mainApp['phis'].treasuryId == null
						|| this.mainApp['phis'].treasuryId == ""
						|| this.mainApp['phis'].treasuryId == undefined) {
					Ext.Msg.alert("提示", "未设置登录库房,请先设置");
					return null;
				}
				if (this.mainApp['phis'].treasuryEjkf != 0) {
					Ext.MessageBox.alert("提示", "该库房不是一级库房!");
					return;
				}
				if (this.mainApp['phis'].treasuryCsbz != 1) {
					Ext.MessageBox.alert("提示", "该库房没有账册初始化!");
					return;
				}
				// 账簿类别
				var zblbItems = this.getZblb();
				this.zblbItems = zblbItems;
				var radioGroup = new Ext.form.RadioGroup({
					height : 20,
					width : 520,
					id : 'StorageOfMaterialsZblb',
					name : 'StorageOfMaterialsZblb', // 后台返回的JSON格式，直接赋值
					value : zblbItems[0].initialConfig.inputValue,
					items : zblbItems,
					listeners : {
						change : function(group, newValue, oldValue) {
							if (this.uCheckInList && this.checkInList) {
								// this.beforeclose();
								var zblbValue = parseInt(newValue.inputValue);
								var addCnd = ['eq', ['$', 'ZBLB'],
										['i', zblbValue]];

								this.uCheckInList.zblb = zblbValue;
								this.uCheckInList.requestData.pageNo = 1;
								this.uCheckInList.requestData.cnd = [
										'and',
										[
												'and',
												['eq', ['$', 'ZBLB'],
														['i', zblbValue]],
												[
														'eq',
														['$', 'KFXH'],
														[
																'i',
																this.mainApp['phis'].treasuryId]]],
										[
												'eq',
												['$', 'DJZT'],
												[
														'i',
														this.uCheckInList.statusRadio
																.getValue().inputValue]]];
								this.uCheckInList.initCnd = [
										'and',
										[
												'and',
												['eq', ['$', 'ZBLB'],
														['i', zblbValue]],
												[
														'eq',
														['$', 'KFXH'],
														[
																'i',
																this.mainApp['phis'].treasuryId]]],
										[
												'eq',
												['$', 'DJZT'],
												[
														'i',
														this.uCheckInList.statusRadio
																.getValue().inputValue]]];;
								this.uCheckInList.loadData();

								this.checkInList.zblb = zblbValue;
								this.checkInList.requestData.pageNo = 1;
								this.checkInList.requestData.cnd = [
										'and',
										[
												'and',
												['eq', ['$', 'ZBLB'],
														['i', zblbValue]],
												[
														'eq',
														['$', 'KFXH'],
														[
																'i',
																this.mainApp['phis'].treasuryId]]],
										['eq', ['$', 'DJZT'], ['i', 2]]];
								this.checkInList.initCnd = [
										'and',
										[
												'and',
												['eq', ['$', 'ZBLB'],
														['i', zblbValue]],
												[
														'eq',
														['$', 'KFXH'],
														[
																'i',
																this.mainApp['phis'].treasuryId]]],
										['eq', ['$', 'DJZT'], ['i', 2]]];;
								this.checkInList.loadData();
							}
						},
						scope : this
					}
				});
				this.radioGroup = radioGroup;
				if (this.panel) {
					return this.panel;
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
				this.schema = schema;
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							tbar : ['', radioGroup],
							items : [{
										layout : "fit",
										ddGroup : "firstGrid",
										border : false,
										split : true,
										region : 'west',
										width : this.width,
										items : this.getUList(),
										selModel : new Ext.grid.RowSelectionModel(
												{
													singleSelect : true
												})
									}, {
										layout : "fit",
										ddGroup : "secondGrid",
										border : false,
										split : true,
										region : 'center',
										items : this.getList(),
										selModel : new Ext.grid.RowSelectionModel(
												{
													singleSelect : true
												})
									}]

						});
				this.panel = panel;
				return panel;
			},
			// 未确定入库界面
			getUList : function() {
				this.uCheckInList = this.createModule(
						"undeterminedCheckInList", this.refUList);
				this.uCheckInList.oper = this;
				this.uCheckInList.on("save", this.onSave, this);
				var zblbValue = this.zblbItems[0].initialConfig.inputValue;
				this.uCheckInList.zblb = this.radioGroup.value;
				this.uCheckInList.requestData.pageNo = 1;
				var addCnd = ['and', ['eq', ['$', 'ZBLB'], ['i', zblbValue]],
						['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]]];
				this.uCheckInList.requestData.cnd = ['and', addCnd,
						['eq', ['$', 'DJZT'], ['i', 0]]];
				this.uCheckInList.initCnd = ['and', addCnd,
						['eq', ['$', 'DJZT'], ['i', 0]]];
				return this.uCheckInList.initPanel();
			},
			// 确定入库界面
			getList : function() {
				this.checkInList = this.createModule("checkInList",
						this.refList);
				this.checkInList.oper = this;
				this.checkInList.on("save", this.onSave, this);
				var zblbValue = this.zblbItems[0].initialConfig.inputValue;
				this.checkInList.zblb = this.radioGroup.value;
				var addCnd = ['and', ['eq', ['$', 'ZBLB'], ['i', zblbValue]],
						['eq', ['$', 'KFXH'], ['i', this.mainApp['phis'].treasuryId]]]
				this.checkInList.requestData.pageNo = 1;
				this.checkInList.requestData.cnd = ['and', addCnd,
						['eq', ['$', 'DJZT'], ['i', 2]]];
				this.checkInList.initCnd = ['and', addCnd,
						['eq', ['$', 'DJZT'], ['i', 2]]];
				return this.checkInList.initPanel();
			},
			afterOpen : function() {
				if (!this.checkInList || !this.uCheckInList) {
					return;
				}
				// 拖动操作
				var firstGrid = this.checkInList.grid;
				var grid = this.uCheckInList;
				var firstGridDropTargetEl = firstGrid.getView().scroller.dom;
				var firstGridDropTarget = new Ext.dd.DropTarget(
						firstGridDropTargetEl, {
							ddGroup : 'firstGridDDGroup',
							notifyDrop : function(ddSource, e, data) {
								var records = ddSource.dragData.selections;
								grid.doCommit();
								return true
							}
						});
			},
			getZblb : function() {
				var kfxh = this.mainApp['phis'].treasuryId;
				var body = {};
				body["KFXH"] = kfxh;
				var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : "getZblbByKfxh",
							body : body
						});
				var kfzblb = [];
				kfzblb = r.json.list;
				var items = [];
				for (var i = 0; i < kfzblb.length; i++) {
					var item = new Ext.form.Radio({
								boxLabel : kfzblb[i][1],
								name : 'StorageOfMaterialsZblb',
								inputValue : kfzblb[i][0]
							})
					items.push(item);
				}
				return items;

			},
			onSave : function() {
				this.checkInList.refresh();
				this.uCheckInList.refresh();
			}
		});