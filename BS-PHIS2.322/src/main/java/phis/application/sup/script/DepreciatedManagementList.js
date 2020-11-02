$package("phis.application.sup.script")

$import("phis.script.EditorList", "phis.script.util.DateUtil")

phis.application.sup.script.DepreciatedManagementList = function(cfg) {
	phis.application.sup.script.DepreciatedManagementList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sup.script.DepreciatedManagementList,
		phis.script.EditorList, {
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
		if (this.mutiSelect) {
			this.sm = new Ext.grid.CheckboxSelectionModel()
		}
		var grid = phis.script.EditorList.superclass.initPanel.call(this, sc)
		grid.on("afteredit", this.afterCellEdit, this)
		grid.on("beforeedit", this.beforeCellEdit, this)
		grid.on("doNewColumn", this.doInsertAfter, this)
		return grid
	},
			getCndBar : function(items) {
				var dat = Date.getServerDateTime().substring(0,
						Date.getServerDateTime().lastIndexOf('-'));
				var filelable = new Ext.form.Label({
							text : "折旧月份:"
						})
				this.zjdate = new Ext.ux.form.Spinner({
							name : 'zjDate',
							value : dat,
							strategy : {
								xtype : "month"
							}
						});
				this.zjdate.on("spinup",this.onRefreshzj,this);
				this.zjdate.on("spindown",this.onRefreshzj,this);
				this.zjdate.on("blur",this.onRefreshzj,this);
				return [filelable, this.zjdate];
			},
			onRefreshzj:function(){
				var count = this.store.getCount();
				var sign=0;
				 for (var i = 0; i < count; i++) {
					 if(this.store.getAt(i).data.GZL!=0){
						 sign=1;
						 Ext.Msg
							.confirm(
									"请确认",
									"工作量已录入数据,切换月份会刷新页面,是否刷新页面?",
									function(btn) {// 先提示是否删除
									if (btn == 'yes') {
										this.loadData();
									}else{
										this.zjdate.setValue(new Date().format("Y-m"));
									}
							}, this);
							break;
					 }
				 }
				 if(sign==0){
					 this.loadData();
				 }
			},
			loadData : function() {
				if (new Date(this.zjdate.getValue()) == "Invalid Date") {
					MyMessageTip.msg("提示", "日期格式不正确!", true);
				} else {
					var years = this.zjdate.getValue().substring(0,
							this.zjdate.getValue().indexOf("-"));
					var months = this.zjdate.getValue().substring(this.zjdate
							.getValue().indexOf("-")
							+ 1);
					var yearmonth = years + months;
					this.clear();
					recordIds = [];
					this.requestData.serviceId = "phis.depreciatedManagementService";
					this.requestData.serviceAction = "queryZJXX";
					this.requestData.cwyf = yearmonth;
					if (this.store) {
						if (this.disablePagingTbr) {
							this.store.load()
						} else {
							var pt = this.grid.getBottomToolbar()
							if (this.requestData.pageNo == 1) {
								pt.cursor = 0;
							}
							pt.doLoad(pt.cursor)
						}
					}
					this.resetButtons();
				}
			},
			doRefreshWin : function() {
				this.loadData();
			},
			doCommit : function() {
				if (new Date(this.zjdate.getValue()) == "Invalid Date") {
					MyMessageTip.msg("提示", "日期格式不正确!", true);
				} else {
					var count = this.store.getCount();
					if(count==0){
						MyMessageTip.msg("提示", "该月没有产生折旧的记录!", true);
						return
					}
					var years = this.zjdate.getValue().substring(0,
							this.zjdate.getValue().indexOf("-"));
					var months = this.zjdate.getValue().substring(this.zjdate
							.getValue().indexOf("-")
							+ 1);
					var yearmonth = years + months;
					var count = this.store.getCount();
					var body = [];
					for (var i = 0; i < count; i++) {
						if(this.store.getAt(i).data.ZJFF==0){
							MyMessageTip.msg("提示", "请先维护折旧方法", true);
							return;
						}
						body.push(this.store.getAt(i).data);
					}
					var rjson = phis.script.rmi.miniJsonRequestSync({
								serviceId : "depreciatedManagementService",
								serviceAction : "saveZJXX",
								body : body,
								cwyf : yearmonth
							});
					if (rjson.code > 300) {
						this.processReturnMsg(rjson.code, rjson.msg)
						return
					} else {
						MyMessageTip.msg("提示", "折旧成功!", true);
						this.loadData();
					}
				}
			},
			doCanclecommit : function() {
				if (new Date(this.zjdate.getValue()) == "Invalid Date") {
					MyMessageTip.msg("提示", "日期格式不正确!", true);
				} else {
					var years = this.zjdate.getValue().substring(0,
							this.zjdate.getValue().indexOf("-"));
					var months = this.zjdate.getValue().substring(this.zjdate
							.getValue().indexOf("-")
							+ 1);
					var yearmonth = years + months;
					var byzjjson = phis.script.rmi.miniJsonRequestSync({
						serviceId : "depreciatedManagementService",
						serviceAction : "queryBYZJXX",
						cwyf : yearmonth
					});
					if(byzjjson.code > 300){
						this.processReturnMsg(byzjjson.code, byzjjson.msg);
					}else{
						Ext.Msg
						.confirm(
								"请确认",
								"是否取消折旧?",
								function(btn) {// 先提示是否删除
									if (btn == 'yes') {
										var count = this.store.getCount();
										var body = [];
										for (var i = 0; i < count; i++) {
											body.push(this.store.getAt(i).data);
										}
										var rjson = phis.script.rmi.miniJsonRequestSync({
											serviceId : "depreciatedManagementService",
											serviceAction : "updateZJXX",
											body : body,
											cwyf : yearmonth
										});
										if (rjson.code > 300) {
											this.processReturnMsg(rjson.code, rjson.msg)
											return
										} else {
											MyMessageTip.msg("提示", "取消折旧成功!", true);
											this.loadData();
										}
									}
								}, this);
						return;
					}
			}
			}
		})