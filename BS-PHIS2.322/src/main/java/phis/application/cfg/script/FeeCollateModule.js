/**
 * 费用对照模块
 * 
 * @author gejj
 */
$package("phis.application.cfg.script");
$import("phis.script.SimpleModule", "util.dictionary.DictionaryLoader",
		"util.dictionary.TreeDicFactory");
phis.application.cfg.script.FeeCollateModule = function(cfg) {
	this.exContext = this.exContext || {};
	this.cfg = cfg;
	phis.application.cfg.script.FeeCollateModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.cfg.script.FeeCollateModule,
		phis.script.SimpleModule, {
	initPanel : function() {
			if (this.panel) {
				return this.panel;
			}
			var tbar = [];
			
           
           this.c1 = new Ext.form.Checkbox({  
                name:'invalid',  
                boxLabel:'显示作废'  
            });
            tbar.push(this.c1);
			//获取Applications.xml中配置的按钮(如打印、保存、取消等)
			var actions = this.actions;
			for (var i = 0; i < actions.length; i++) {
				var action = actions[i];
				var btn = {};
				btn.id = action.id;
				btn.accessKey = "F1", btn.cmd = action.id;
				btn.text = action.name, btn.iconCls = action.iconCls
						|| action.id;
				//添加doAction点击事件,调用doAction方法
				btn.handler = this.doAction;
				btn.name = action.id;
				btn.scope = this;
				tbar.push(btn);
			}
			
			var panel = new Ext.Panel({
						border : false,
						frame : true,
						layout : 'border',
						items : [{
									layout : "fit",
									border : false,
									split : false,
									title : '',
									width : 600,
									region : 'center',
									items : this.getList()
								}],
						tbar : tbar
					});
			this.panel = panel;
			return this.panel;
		},
		getList : function(){
			var module = this.createModule("feeList", this.refFeeList);
			if (module) {
				var json = {"grade":1, "invalid":0};
				module.requestData.body = json;
				module.opener = this;
				return module.initPanel();
			}
		},
		doAction : function(item, e){
			if(item.id == "refresh"){//刷新按钮
				this.refreshDates();
			}else if(item.id == "update"){
				this.doUpdate();
			}
		},
		doUpdate : function(){
			var medicList = this.midiModules['feeList'];
			var select = medicList.getSelectedRecord();
			var module = this.createModule("feeYBColl", this.refFYBCollate);
			module.opener = this;
			//监听关闭事件，关闭后调用刷新当前界面  modify by gejj 修改为由保存成功后进行刷新
//			module.on("close", function(){
//				this.refresh();
//			},this);
			//重新设置选择数据
			module.setParameter(select);
			if(!this.ybWin){
				this.ybWin = module.getWin();
				this.ybWin.setHeight(600);
				this.ybWin.setWidth(1024);
			}else{
				//更新Form中的数据
				module.updateFormInfo();
			}
			this.ybWin.show();
		},
		refreshDates : function(){
			var medicList = this.midiModules['feeList']
			var type = 1;//this.comboBox.getValue();
			var json = {"grade":type};
			if(this.c1.checked){
				json["invalid"] = 1;
			}else{
				json["invalid"] = 0;
			}
			medicList.requestData.body = json;
			medicList.refresh();
		}
});