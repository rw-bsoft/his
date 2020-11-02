/**
 * 处方点评-右边tab
 * 
 * @author caijy
 */
$package("phis.application.pcm.script");

$import("phis.script.TabModule");

phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTab = function(
		cfg) {
	cfg.isNewOpne=1;
	phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTab.superclass.constructor
			.apply(this, [cfg]);
			this.on("tabchange",this.onChange,this)
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsRightModuleLeftTab,
		phis.script.TabModule, {
			loadData : function() {
					var module = this.midiModules[this.tab.activeTab.id];
					if(!module){
					return;}
					module.cyxh = this.cyxh
					module.loadData()
			},
			onChange:function(tabPanel, newTab, curTab){
			this.fireEvent("changeButtonState",this.tab.activeTab.id);
			if(this.isNewOpne==1){
			this.isNewOpne=0;
			return;
			}
			this.loadData();
			},
			//抽样明细点击
			loadDY:function(cfsb,dpbz,cflx){
			this.fireEvent("cymxClick",cfsb,dpbz,cflx);
			},
			doNew:function(){
				this.cyxh=0;
			var module = this.midiModules["cyjg"];
			if(module){
			module.doNew();
			}
			var modules=this.midiModules["dpjg"];
			if(modules){
			modules.doNew();
			}
			},
			clearDY:function(){
			this.fireEvent("noRecord");
			},
			//合理或不合理处理
			doHloBhl:function(tag,wtdms){//tag 1表示合理,2表示不合理.
				if (this.tab.activeTab.id == "cyjg") {
					var module = this.midiModules[this.tab.activeTab.id];
					if(!module){
					return;}
				var dpxh=module.getDpxh();
				if(dpxh==null){
				return;}
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.ServiceId,
							serviceAction : this.serviceAction,
							body : {
								"DPXH" : dpxh,
								"WTDMS" : wtdms,
								"TAG" : tag
							}
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return ;
				}
				var module = this.midiModules["cyjg"];
				if(module){
				var r=module.list.getSelectedRecord();
				var index=module.list.store.find("DPBZ","0",(module.list.store.indexOfId(r.id))+1);
				if(index==-1){
				index=module.list.store.find("DPBZ","0");
				if(index==-1){
				index=module.list.store.indexOfId(r.id);
				}
				}
				module.list.selectedIndex=index;
				}
				this.loadData();
				}
			},
			//判断是否有点评过的记录
			getSfdp:function(){
			if (this.tab.activeTab.id == "cyjg") {
					var module = this.midiModules[this.tab.activeTab.id];
					if(!module){
					return false;}
					return module.getSfdp();
				}
			},
			//是否全部点评
			getSfqbdp:function(){
			if (this.tab.activeTab.id == "cyjg") {
					var module = this.midiModules[this.tab.activeTab.id];
					if(!module){
					return false;}
					return module.getSfqbdp();
				}
			},
			onSuperRefresh : function(entryName, op, json, rec) {
				this.fireEvent("afterLoadData",this);
			}
		});