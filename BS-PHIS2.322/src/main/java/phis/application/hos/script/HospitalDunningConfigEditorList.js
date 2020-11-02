$package("phis.application.hos.script")

/**
 * 病人性质维护的自负比例界面list zhangyq 2012.5.25
 */
$import("phis.script.EditorList")

phis.application.hos.script.HospitalDunningConfigEditorList = function(cfg) {
	cfg.disablePagingTbr = true;// 不分页
// cfg.autoLoadData = false;
	cfg.listServiceId = "hospitalDebtDueService";
	this.serviceId = "hospitalDebtDueService";
	this.serverParams = {};
	if(cfg.openby=="nature"){
		this.serverParams.serviceAction = "queryNatureDunningConfig";
	}else if(cfg.openby=="department"){
		this.serverParams.serviceAction = "queryDepartmentDunningConfig";
	}
	this.saveActionId = "saveDunningConfig";
	
	
	phis.application.hos.script.HospitalDunningConfigEditorList.superclass.constructor
			.apply(this, [cfg])
}

Ext.extend(phis.application.hos.script.HospitalDunningConfigEditorList,
		phis.script.EditorList, {
			doSave : function(item, e) {
				var store = this.grid.getStore();
				var n = store.getCount()
				var data = []
				for (var i = 0; i < n; i++) {
					var r = store.getAt(i)
					data.push(r.data)
				}
				this.grid.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
//							schema : this.entryName,
//							method : "execute",
							body : data,
//							brxz : this.brxz
						}, function(code, msg, json) {

							this.grid.el.unmask()
							if (code >= 300) {
								this.processReturnMsg(code, msg);
								return;
							}
							this.refresh();
						}, this)
			},
			expansion : function(cfg) {
				// 底部 统计信息,未完善
				var label = new Ext.form.Label({
					html : "<div id='CKWH' align='center' style='color:blue'>判断是否欠费的公式: 缴款金额 - 自负金额*催款比例 < 最低限额</div>"
				})
				cfg.bbar = [];
				cfg.bbar.push(label);
			},
			doHelp : function(){
				Ext.MessageBox.alert("帮助说明", "&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp1.催款比例：0到1之间的数字。<br/>"+
		"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp2.催款金额：欠费病人每次催款的额度。<br/>"+
		"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp3.冻结金额：当病人欠费多少开始冻结金额。<br/>"+
		"&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp4.最低限额:(缴款金额 - 自负金额*催款比例 < 最低限额)时表示欠费。");
			}
		})