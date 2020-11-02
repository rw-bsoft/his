/**
 * 药房发药
 * 
 * @author caijy
 */
$package("phis.application.ivc.script");

$import("phis.script.SimpleModule");

phis.application.ivc.script.ClinicModule = function(cfg) {
	this.exContext = {};
	phis.application.ivc.script.ClinicModule.superclass.constructor.apply(this,
			[cfg]);
	this.on('doSave', this.doSave, this);
	/**
	 * 监听快捷键 shortcutKeyFunc common.js有默认实现类
	 * 如有特殊需求要重写，需要重新定义监听的方法名称，否则会被common中的默认方法覆盖
	 */
	this.on("shortcutKey", this.shortcutKeyFunc, this);
}
Ext.extend(phis.application.ivc.script.ClinicModule, phis.script.SimpleModule,
		{
			initPanel : function() {
				var panel = new Ext.Panel({
							border : false,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										region : 'west',
										width : 240,
										collapsible : true,
										collapsed : true,
										items : this.getList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getDModule()
									}]
						});
				this.panel = panel;
				return panel;
			},
			doNew : function() {
			},
			getList : function() {
				this.cfList = this.createModule("cfList", this.refList);
				this.cfList.on("brselect", this.onbrselect, this)
				this.cfList.on("brCanclebrselect", this.onbrCanclebrselect,
						this)
				// this.cfList.on("brChoose", this.onbrChoose, this);
				return this.cfList.initPanel();
			},
			onbrselect : function(record) {
				var field = this.cfDetaiModule.formModule.form.getForm()
						.findField("MZGL");
				field.setValue();
				this.cfDetaiModule.BLLX = {
					"BLLX" : record.data.BLLX
				};
				this.cfDetaiModule.formModule.doJZKHChange(record.data);
			},
			onbrCanclebrselect : function() {
				this.cfDetaiModule.doQx();
			},
			// onbrChoose : function(){},
			getDModule : function() {
				this.cfDetaiModule = this.createModule("cfDetaiModule",
						this.refModule);
				this.cfDetaiModule.opener = this;
				return this.cfDetaiModule.initPanel();
			},
			onCfSelect : function(record) {
				var cfsb = record.data.CFSB;
				var cflx = record.data.CFLX;
				if (!this.cfDetaiModule) {
					return;
				}
				this.cfDetaiModule.showDetail(cfsb, cflx);
			},
			onCfCancleSelect : function() {
				var lastIndex = this.midiModules['cfList'].grid
						.getSelectionModel().lastActive;
				this.midiModules['cfList'].selectRow(lastIndex);
			},
			// 当没发药记录时调用,清空明细
			onNoRecord : function() {
				this.cfDetaiModule.doNew();
			}
		});