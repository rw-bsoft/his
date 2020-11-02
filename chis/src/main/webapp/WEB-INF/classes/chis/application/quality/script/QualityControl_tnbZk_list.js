$package("chis.application.quality.script")

$import("chis.script.BizSimpleListView","chis.script.BizTabModule");

chis.application.quality.script.QualityControl_tnbZk_list = function(cfg) {
	chis.application.quality.script.QualityControl_tnbZk_list.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(chis.application.quality.script.QualityControl_tnbZk_list,
		chis.script.BizSimpleListView, {
     onReady: function(){
 	 chis.application.quality.script.QualityControl_tnbZk_list.superclass.onReady.call(this);
         this.actionAdd=[
					{id: 'ckzkjg', name: '查看质控结果'},
					{id: 'ckzksj', name: '查看质控数据'},
					{id: 'lrzksj', name: '录入质控数据'},
					{id: 'xgzksj', name: '修改质控数据'},
					{id: 'sfzkpf', name: '随访质控评分'}
					];
	 } ,doAddYB:function(){
			  	this.fireEvent("addYB", this);
	 },doSomeOne:function(){
	 
	 },addZK:function(data){
	 	var result = util.rmi.miniJsonRequestSync({
					 	serviceId : "chis.qualityControlService", 
						serviceAction : "saveGxyList3", 
						method : "execute",
						body : {
							values :data,
							"entryName":"QUALITY_ZK_SJ"
						  }
				});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				} else {
					var back= result.json;
					if(!back || back["NO"]==null || back["NO"]==""){
						MyMessageTip.msg("提示", "保存出错请检查list3！",true);
					}else{
				       back["visitId"]= data["visitId"];
					  this.fireEvent("addBackNo", back);//返回NO给list3 保存数据
					}
				     this.refresh()
				}
	 },
   doQuery : function(NO) {
   	        this.NO=NO;
			var cnd = [];
		    cnd = ['like',['$','prartNo'],['s',NO]];
		    //类别
			var ZKNO = "1";
			hcnd = ['eq',['$','ZKNO'],['s',ZKNO || '']]
			if(cnd.length > 0){
				cnd = ['and', cnd, hcnd];
			}else{
				cnd = hcnd;
			}
			this.requestData.cnd = cnd
			this.refresh()
    },
	onRowClick:function(grid,index,e){//单击
		this.selectedIndex = index 
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
	},doCkzksj:function(){
	     var r = this.getSelectedRecord();
		if(r){
		  var values={};
		       values["CODERNO"] = r.get("CODERNO");
		        values["empiId"] = r.get("empiId");
		        values["prartNo"] = r.get("prartNo");
		        values["visitId"] = r.get("visitId");
		        values["phrId"] = r.get("phrId");
		this.showWindowM(values,"ckzkjg")
		}
	},doCkzkjg:function(){
		var r = this.getSelectedRecord();
		if(r){
		  var CODERNO = r.get("CODERNO");
		  var result = util.rmi.miniJsonRequestSync({
						 	serviceId : "chis.qualityControlService", 
							serviceAction : "createTnbZk", 
							method : "execute",
							body : {
								CODERNO :CODERNO,
								"entryName":"QUALITY_ZK_TNB"
							  }
					});
			if (result.code > 300) {
				this.processReturnMsg(result.code, result.msg);
				return null;
			} else {
				var back= result.json;
				if(back["CODERNO"]){
					 this.showWinZkDz(back["CODERNO"])
				}else{
				  MyMessageTip.msg("提示", "请先录入质控数据！",true);
				}
			}
		}
	},doLrzksj:function(){
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
	},doSfzkpf:function(){
		   var r = this.getSelectedRecord();
		if(r){
			  var values={};
			      values["CODERNO"] = r.get("CODERNO");
			      values["empiId"] = r.get("empiId");
			      values["prartNo"] = r.get("prartNo");
			      values["visitId"] = r.get("visitId");
			      values["phrId"] = r.get("phrId");
		         var result = util.rmi.miniJsonRequestSync({
					 	serviceId : "chis.qualityControlService",//doInitQualityModel
						serviceAction : "creatTnbZkPf",//getQualityModel  loadHtmlData
						method : "execute",
						body : {
							"values" :values
						  }
						});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				} else {
					//MyMessageTip.msg("提示", "完成评分！",true);
				    this.doCkzkjg();
				}
		}
	},doXgzksj:function(){
		 var r = this.getSelectedRecord();
		if(r){
		  var values={};
		       values["CODERNO"] = r.get("CODERNO");
		        values["empiId"] = r.get("empiId");
		        values["prartNo"] = r.get("prartNo");
		        values["visitId"] = r.get("visitId");
		        values["phrId"] = r.get("phrId");
		this.showWindowM(values,"xgzksj")
		}
	},showWindowM:function(values,type){
		   var module = this.createCombinedModule("lrzksj", this.addModule);
				this.AduitFormWin = module.getWin(); 
				this.AduitFormWin.add(module.initPanel());
				this.AduitFormWin.setPosition(250, 100);
				this.AduitFormWin.show();
				module.doInitLoad(values,type);
	},showWinZkDz:function(CODERNO){
		   var module = this.createCombinedModule("ckzkjg", this.addCkzkbg);
				this.AduitFormWin = module.getWin(); 
				this.AduitFormWin.add(module.initPanel());
				this.AduitFormWin.setPosition(250, 100);
				this.AduitFormWin.show();
				var values={};
				values["CODERNO"] = CODERNO;
			   module.doInitLoad(values);
	},doRemove2:function(){
		 var r = this.getSelectedRecord();
		if(r){
		  var values={};
		       values["CODERNO"] = r.get("CODERNO");
		        values["empiId"] = r.get("empiId");
		        values["prartNo"] = r.get("prartNo");
		        values["visitId"] = r.get("visitId");
		        values["phrId"] = r.get("phrId");
		var result = util.rmi.miniJsonRequestSync({
					 	serviceId : "chis.qualityControlService",//doInitQualityModel
						serviceAction : "deleteGxyZkPf",//getQualityModel  loadHtmlData
						method : "execute",
						body : {
							"values" :values
						  }
						});
				if (result.code > 300) {
					this.processReturnMsg(result.code, result.msg);
					return null;
				} else {
					MyMessageTip.msg("提示", "删除成功！",true);
					 this.doQuery (this.NO);
				}
		}
	}
 })