$package("phis.application.fsb.script");

$import("phis.script.SelectList", "phis.script.rmi.miniJsonRequestSync",
		"phis.script.util.DateUtil", "phis.script.widgets.DatetimeField");

phis.application.fsb.script.FsbNursePlanImportList = function(cfg) {
	phis.application.fsb.script.FsbNursePlanImportList.superclass.constructor
			.apply(this, [cfg]);
}

Ext.extend(phis.application.fsb.script.FsbNursePlanImportList,
		phis.script.SelectList, {
			expansion : function(cfg) {
				var items = [];
				items.push(new Ext.form.Label({
							text : "计划日期"
						}))
				this.beginDate = new Ext.form.DateField({
							name : 'beginDate',
							value : Date.getServerDate(),
							width : 100,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '开始时间：'
						});
				this.endDate = new Ext.form.DateField({
							name : 'endDate',
							value : Date.getServerDate(),
							width : 100,
							altFormats : 'Y-m-d',
							format : 'Y-m-d',
							emptyText : '结束时间'
						});
				items.push(this.beginDate);
				items.push(new Ext.form.Label({
							text : "至"
						}))
				items.push(this.endDate)
				items.push('-');
				items.push(new Ext.form.Label({
							text : "计划护士："
						}))
				this.docDic = util.dictionary.SimpleDicFactory.createDic({
							id : "phis.dictionary.doctor_cfqx",
							filter:"['eq',['$','item.properties.ORGANIZCODE'],['$','%user.manageUnit.ref']]",
							width : 80,
							autoLoad : true
						});
				this.docDic.setEditable(false);
				this.docDic.store.on("load", this.docDicLoad, this);
				items.push(this.docDic)
				var tbar = cfg.tbar;
				cfg.tbar = items;
				cfg.tbar.push(tbar);
			},
			docDicLoad : function(store) {
				var r = new Ext.data.Record({
							key : "-1",
							text : "全部"
						})
				store.insert(0, [r]);
				this.docDic.setValue((this.mainApp.uid || "-1"))
				this.doQuery();
			},
			onWinShow : function() {
				if (this.docDic.getValue()) {
					this.doQuery();
				}
			},
			doQuery : function() {
				var exCnd = null;
				// 计划护士
				var ysdm = this.docDic.getValue();
				if (ysdm != '-1') {
					if (exCnd) {
						exCnd = ['and', exCnd,
								['eq', ['$', 'a.HLHS'], ['s', ysdm]]];
					} else {
						exCnd = ['eq', ['$', 'a.HLHS'], ['s', ysdm]];
					}
				}
				// 计划时间
				var datefrom = this.beginDate.getRawValue();
				var dateTo = this.endDate.getRawValue();
				if (datefrom != null && dateTo != null && datefrom != ""
						&& dateTo != "" && datefrom > dateTo) {
					Ext.MessageBox.alert("提示", "计划开始时间不能大于结束时间");
					return;
				}
				var timeCnd = null;
				if (datefrom != null && datefrom != ""
						&& (dateTo == null || dateTo == "")) {
					timeCnd = ['ge',
							['tochar', ['$', 'a.KSRQ'], ['s', 'yyyy-mm-dd']],
							['s', datefrom]];
				} else if (dateTo != null && dateTo != ""
						&& (datefrom == null || datefrom == "")) {
					timeCnd = ['le',
							['tochar', ['$', 'a.KSRQ'], ['s', 'yyyy-mm-dd']],
							['s', dateTo]];
				} else if (dateTo != null && dateTo != "" && datefrom != null
						&& datefrom != "") {
					timeCnd = [
							'and',
							[
									'ge',
									['tochar', ['$', 'a.KSRQ'],
											['s', 'yyyy-mm-dd']],
									['s', datefrom]],
							[
									'le',
									['tochar', ['$', 'a.KSRQ'],
											['s', 'yyyy-mm-dd']], ['s', dateTo]]];
				}
				if (exCnd) {
					exCnd = ['and', exCnd, timeCnd];
				} else {
					exCnd = timeCnd;
				}
				var initCnd = this.initCnd
						|| ['eq', ['$', 'a.JGID'], ['s', this.mainApp.deptId]]
				var cnd;
				if (initCnd) {
					cnd = initCnd
				}
				if (exCnd && cnd) {
					cnd = ['and', exCnd, cnd]
				}
				this.requestData.cnd = cnd
				this.refresh()
			},
			doImport : function() {
				var records = this.getSelectedRecords()
				if (!records || records.length < 1) {
					return;
				}
				var hlcs = "";
				for (var i = 0; i < records.length; i++) {
					hlcs += records[i].get("HLCS")
				}
				this.opener.form.getForm().findField("HLCS")
						.setValue(hlcs);
				this.getWin().hide();
			},
			doClose : function() {
				this.win.hide();
			}
		});