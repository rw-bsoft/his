$package("chis.application.quality.script")

$import("chis.script.BizSimpleListView") 

chis.application.quality.script.QualityControl_tnbYf_list = function(cfg) {
	this.autoLoadData = true
	this.xmxhId="";
	chis.application.quality.script.QualityControl_tnbYf_list.superclass.constructor.apply(
			this, [cfg]);
	// this.disablePagingTbr = true
}
Ext.extend(chis.application.quality.script.QualityControl_tnbYf_list,
	chis.script.BizSimpleListView, {
     onReady: function(){
		chis.application.quality.script.QualityControl_tnbYf_list.superclass.onReady.call(this);
         this.actionAdd=[
					{id: 'zkbg', name: '质控报告'}
					];
		},addYf:function(data){
			var values=data
			values["GXJG"]=this.mainApp.deptId;
			values["XMLB"]="TNBSF";
		   var result = util.rmi.miniJsonRequestSync({
				 	serviceId : "chis.qualityControlService", 
					serviceAction : "saveTnbListYf", 
					method : "execute",
					body : {
						values :values,
						"entryName":"QUALITY_ZK_ZQ"
					//	op:"create"
					  }
			});
			if (result.code > 300) {
				this.processReturnMsg(result.code, result.msg);
				return null;
			} else {
				var back= result.json;
				if(!back || back["NO"]==null || back["NO"]==""){
					MyMessageTip.msg("提示", "保存出错请检查list2！",true);
				}else{
			       back["visitId"]= data["visitId"];
				  this.fireEvent("addBackNo", back);//返回NO给list3 保存数据
				}
			     this.refresh()
			}
		},
		doQuery : function(values) {
			var cnd = [];
			var manaUtil =values["manaUtil"];
			if(manaUtil){
				cnd = ['like',['$','MANAUNITID'],['s',manaUtil]];
			}
			//类别
			var ZKLB = values["ZKLB"];
			if(ZKLB){
				hcnd = ['eq',['$','ZKLB'],['s',ZKLB || '']]
				if(cnd.length > 0){
					cnd = ['and', cnd, hcnd];
				}else{
					cnd = hcnd;
				}
			}
			//级别
			var ZKJB = values["ZKJB"];
			if(ZKJB){
				hcnd = ['eq',['$','ZKJB'],['s',ZKJB || '']]
				if(cnd.length > 0){
					cnd = ['and', cnd, hcnd];
				}else{
					cnd = hcnd;
				}
			}
			//项目类别
			var XMLB = "TNBSF";
			if(XMLB){
				hcnd = ['eq',['$','XMLB'],['s',XMLB || '']]
				if(cnd.length > 0){
					cnd = ['and', cnd, hcnd];
				}else{
					cnd = hcnd;
				}
			}
			this.requestData.cnd = cnd
			this.refresh();
		},
		onRowClick:function(grid,index,e){//单击
			this.selectedIndex = index
			 //拿list2 NO返回给list3查询 
			var r = this.getSelectedRecord();
			if (r) {
				var NO = r.get("NO");
		    	this.fireEvent("onread2", NO);//返回NO给list3 保存数据
			}
	   },
		onContextMenu:function(grid,rowIndex,e){
			if(e){
				e.stopEvent()
			}
			if(this.disableContextMenu){
				return
			}
			this.grid.getSelectionModel().selectRow(rowIndex)
			var cmenu = this.midiMenus['gridContextMenu']
			if(!cmenu){
				var items = [];
				var actions = this.actionAdd
				if(!actions){
					return;
				}
				for(var i = 0; i < actions.length; i ++){
					var action = actions[i];
					var it = {}
					it.cmd = action.id
					it.ref = action.ref
					it.iconCls = action.iconCls || action.id
					it.script = action.script
					it.text = action.name
					it.handler = this.doAction
					it.scope = this
					items.push(it)
				}
				cmenu = new Ext.menu.Menu({items:items})
				this.midiMenus['gridContextMenu'] = cmenu
			}
			// @@ to set menuItem disable or enable according to buttons of
			// toptoolbar.
			var toolBar = this.grid.getTopToolbar();
			if (toolBar) {
				for (var i = 0; i < this.actionAdd.length; i++) {
					var btn = toolBar.find("cmd", this.actionAdd[i].id);
					if (!btn || btn.length == 0) {
						continue;
					}
					if (btn[0].disabled) {
						cmenu.items.itemAt(i).disable();
					} else {
						cmenu.items.itemAt(i).enable();
					}
				}
			}
			cmenu.showAt([e.getPageX()+5,e.getPageY()+5])
		},
		onDblClick:function(grid,index,e){
			var actions = this.actions
			if(!actions){
				return;
			}
			this.selectedIndex = index
			var item = {};
			for(var i = 0; i < actions.length; i ++){
				var action = actions[i]
				var cmd = action.id
				if(cmd == "update" || cmd == "read"){
					item.text = action.name
					item.cmd = action.id
					item.ref = action.ref
					item.script = action.script
					if(cmd == "update" || cmd == "read"){
						break
					}
				}
			}
			if(item.cmd){
				this.doAction(item,e)
			}
		},doZkbg:function(){
			alert("质控报告")
			   var r = this.getSelectedRecord();
				if(r){
				  var values={};
				       values["CODERNO"] = r.get("CODERNO");
				        values["empiId"] = r.get("empiId");
				        values["prartNo"] = r.get("prartNo");
				        values["visitId"] = r.get("visitId");
				        values["phrId"] = r.get("phrId");
				this.showWindowM(values,"lrzksj")
				}
		}
	 })