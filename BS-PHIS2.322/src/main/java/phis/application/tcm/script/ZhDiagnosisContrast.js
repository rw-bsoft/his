$package("phis.application.tcm.script")

$import("phis.script.SimpleModule")
phis.application.tcm.script.ZhDiagnosisContrast = function(cfg) {
	phis.application.tcm.script.ZhDiagnosisContrast.superclass.constructor.apply(
			this, [cfg]);
}

/***
 * 【中医馆】中医证候对照
 */
Ext.extend(phis.application.tcm.script.ZhDiagnosisContrast,
		phis.script.SimpleModule, {
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : false,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : "中医证候编码HIS",
										region : 'west',
										width : "28%",
										items : this.getHISList(),
										tbar : this.getHISTbar()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "中医证候编码TCM",
										region : 'center',
										items : this.getTCMList(),
										tbar : this.getTCMTbar()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : "已对照证候编码",
										region : 'east',
										width : "35%",
										items : this.getDZList()
									}]
						});
				this.panel = panel;
				return panel;
			},
			getHISList : function() {
				this.HISList = this.createModule("HISList",
						this.refHISList);
				this.refHISList.opener = this;
				this.HISGrid = this.HISList.initPanel();
				return this.HISGrid;

			},
			getTCMList : function() {
				this.TCMList = this.createModule("TCMList",
						this.refTCMList);
				this.TCMList.opener = this;
				this.TCMGrid = this.TCMList.initPanel();
				return this.TCMGrid;

			},
			getDZList : function() {
				this.DZList = this.createModule("DZList",
						this.refDZList);
				this.DZList.opener = this;
				this.DZGrid = this.DZList.initPanel();
				return this.DZGrid;
			},
			getHISTbar : function() {
				var tbar = [];
				tbar.push(new Ext.form.Label({
							text : "拼音代码"
						}));
				tbar.push(new Ext.form.TextField({
							id : "pydmhis",
							width : 100
						}));
				tbar.push({
							xtype : "button",
							text : "查询",
							iconCls : "query",
							scope : this.HISList,
							handler : this.HISList.refresh
						});
				return tbar;
			},
			getTCMTbar : function() {
				var tbar = [];
				tbar.push(new Ext.form.Label({
							text : "拼音代码"
						}));
				tbar.push(new Ext.form.TextField({
							id : "pydmtcm",
							width : 100
						}));
				tbar.push({
							xtype : "button",
							text : "查询",
							iconCls : "query",
							scope : this.TCMList,
							handler : this.TCMList.refresh
						});
				return tbar;
			}
		});