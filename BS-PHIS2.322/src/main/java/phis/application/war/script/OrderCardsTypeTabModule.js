$package("phis.application.war.script")

$import("phis.script.TabModule")

phis.application.war.script.OrderCardsTypeTabModule = function(cfg) {
	this.parentModule = null;//获得List页面 选中医嘱病人的住院号
	this.ActiveCardKey = 2;//当前激活考片类型
	phis.application.war.script.OrderCardsTypeTabModule.superclass.constructor.apply(this,
			[cfg])
}
Ext.extend(phis.application.war.script.OrderCardsTypeTabModule, phis.script.TabModule, {
			onTabChange : function(tabPanel, newTab, curTab) {
				SelectedRecords = this.parentModule.getArr_ZYH();
				var typeValue = this.parentModule.typeValue;//获得当前是哪种卡片：固定卡片,执行单
				var orderTypeValue = this.parentModule.orderTypeValue;//获得医嘱类别：0：长期，1：临时
				var flag = false;
				if(newTab.exCfg.id == "mouthCard"){
					this.ActiveCardKey = 2;//口服卡
					if(!this.midiModules["mouthCard"]){
						flag = true;
					}else{
						this.sendToCKData(SelectedRecords,typeValue,orderTypeValue);
					}
				}else if(newTab.exCfg.id == "injectionCard"){
					this.ActiveCardKey = 3;//注射卡
					if(!this.midiModules["injectionCard"]){
						flag = true;
					}else{
						this.sendToCKData(SelectedRecords,typeValue,orderTypeValue);
					}
				}else if(newTab.exCfg.id == "stillDripCard"){
					this.ActiveCardKey = 4;//静滴卡
					if(!this.midiModules["stillDripCard"]){
						flag = true;
					}else{
						this.sendToCKData(SelectedRecords,typeValue,orderTypeValue);
					}
				}else if(newTab.exCfg.id == "transfusionPatrolCard"){
                    this.ActiveCardKey = 5;//输液巡视卡
                    if(!this.midiModules["transfusionPatrolCard"]){
                        flag = true;
                    }else{
                        this.sendToCKData(SelectedRecords,typeValue,orderTypeValue);
                    }
                }else{
					this.ActiveCardKey = 2;	//默认口服卡
					if(!this.midiModules["mouthCard"]){
						flag = true;
					}else{
						this.sendToCKData(SelectedRecords,typeValue,orderTypeValue);
					}
				}

				if (newTab.__inited) {
					this.fireEvent("tabchange", tabPanel, newTab, curTab);
					return;
				}
				var exCfg = newTab.exCfg
				var cfg = {
					showButtonOnTop : true,
					autoLoadSchema : false,
					isCombined : true
				}
				Ext.apply(cfg, exCfg);
				var ref = exCfg.ref
				if (ref) {
					var body = this.loadModuleCfg(ref);
					Ext.apply(cfg, body)
				}
				var cls = cfg.script
				if (!cls) {
					return;
				}

				if (!this.fireEvent("beforeload", cfg)) {
					return;
				}
				$require(cls, [function() {
							var m = eval("new " + cls + "(cfg)");
							m.setMainApp(this.mainApp);
							if (this.exContext) {
								m.exContext = this.exContext;
							}
							m.opener = this;
							this.midiModules[newTab.id] = m;
							var p = m.initPanel();
							m.on("save", this.onSuperRefresh, this)
							newTab.add(p);
							newTab.__inited = true
							this.tab.doLayout();
							this.fireEvent("tabchange", tabPanel, newTab,
									curTab);
							
						    if(flag){
						    	this.sendToCKData(SelectedRecords,typeValue,orderTypeValue);
						    }
						}, this])

			},
			sendToCKData:function(arr_zyh,typeValue,orderTypeValue){
//				if(arr_zyh.length != 0){
					if(this.ActiveCardKey == 2){
						this.midiModules["mouthCard"].doActivePanel(arr_zyh,this.ActiveCardKey,typeValue,orderTypeValue);//口服卡
					}else if(this.ActiveCardKey == 3){
						this.midiModules["injectionCard"].doActivePanel(arr_zyh,this.ActiveCardKey,typeValue,orderTypeValue);//注射卡
					}else if(this.ActiveCardKey == 4){
						this.midiModules["stillDripCard"].doActivePanel(arr_zyh,this.ActiveCardKey,typeValue,orderTypeValue);//静滴卡
					}else if(this.ActiveCardKey == 5){
                        this.midiModules["transfusionPatrolCard"].doActivePanel(arr_zyh,this.ActiveCardKey,typeValue,orderTypeValue);//输液巡视卡
                    }
//				}
			}
})