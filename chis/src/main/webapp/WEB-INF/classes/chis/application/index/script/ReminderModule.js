/**
 * 组合模块公共页面，支持 "左右"/"上下" 布局格式的两个模块组合
 * 
 * @author : yaozh
 */
$package("chis.application.index.script")
$import("app.desktop.Module", "chis.script.util.widgets.MyDatePicker")
chis.application.index.script.ReminderModule = function(cfg) {
	chis.application.index.script.ReminderModule.superclass.constructor.apply(this, [cfg])

}
Ext.extend(chis.application.index.script.ReminderModule, app.desktop.Module, {
	initPanel : function() {
		var datefield = new Ext.Panel({
					labelWidth : 100, // label settings here cascade
					// unless
					// overridden
					frame : true,
					bodyStyle : 'padding:20px 10px 20px',
					width : 360,
					defaults : {
						// width : 235,
						height : 177
					},
					defaultType : 'myDatePicker',
					items : [{
								fieldLabel : 'Date',
								name : 'date',
								showToday : false,
								mainApp : this.mainApp,
								listeners : {
									select : this.openWin,
									// 'render': function(dp){
									// //取得DatePicker的DOM节点的第一个子节点
									// var outerTable = dp.el.dom.firstChild;
									// //使用行内样式修改
									// outerTable.style.width = '100%';
									// outerTable.style.height = '100%';
									// },
									scope : this
								}
							}]
				});
		return datefield
	},
	openWin : function(e, t) {
		var initCnd = {}
		var lo = {}
		if (this.mainApp.jobtitleId == 'chis.01' || this.mainApp.jobtitleId == 'chis.05') {
			if (e.json.body[t.getDate()]) {
				lo.moduleId = "A01"  
				lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
				lo.catalogId = "WL" 
				lo.param={date:t};
				this.fireEvent("openWin", lo, true, true, "");
			}
		} else if (this.mainApp.jobtitleId == 'chis.07') {
			if (e.json.body[t.getDate()]) {
				lo.moduleId = "A03"  
				lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
				lo.catalogId = "WL" 
				lo.param={date:t};
				this.fireEvent("openWin", lo, true, true, "");
			}
		} else if (this.mainApp.jobtitleId == 'chis.08') {
			if (e.json.body[t.getDate()]) {
				lo.moduleId = "A02"  
				lo.appId = "chis.application.healthmanage.HEALTHMANAGE"
				lo.catalogId = "WL" 
				lo.param={date:t};
				this.fireEvent("openWin", lo, true, true, "");
			}
		}
	}
})