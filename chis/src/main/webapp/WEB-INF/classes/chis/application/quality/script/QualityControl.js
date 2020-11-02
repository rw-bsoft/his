$package("chis.application.quality.script")

$import("chis.script.BizSimpleListView", "util.rmi.jsonRequest",
		"app.modules.list.SimpleListView","chis.script.BizTabModule") 

chis.application.quality.script.QualityControl = function(cfg) {
	chis.application.quality.script.QualityControl.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(chis.application.quality.script.QualityControl,
		chis.script.BizSimpleListView, {
			doOpen : function(){
			    var module = this.createCombinedModule("addModule", this.addModule);
				this.AduitFormWin = module.getWin();
				module.op="create";
		    	module.on("close",this.closeWin,this);
				this.AduitFormWin.on("show", function() {
					   this.closeW=false;
							module.doLoad( );
						}, this);
				this.AduitFormWin.add(module.initPanel());
				this.AduitFormWin.setPosition(250, 100);
				this.closeW=false;
				this.AduitFormWin.show();
			},doModify : function(){
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请选择需要操作的信息!", true);
					return;
				}
				var initDataId = r.get("ID");
			    var module = this.createCombinedModule("addModule", this.addModule);
				this.AduitFormWin = module.getWin();
				module.op="update";
			 	module.on("close",this.closeWin,this);
				this.AduitFormWin.on("show", function() {
					        this.closeW=false;
					        var r = this.getSelectedRecord();
							module.doLoad(r);
						}, this);
				this.AduitFormWin.add(module.initPanel());
				this.AduitFormWin.setPosition(250, 100);
				this.closeW=false;
				this.AduitFormWin.show();
			},getCndBar2 : function(items){
					this.manaUtil = this.createDicField({
						"src" : "",
						"width" : 120,
						"id" : "chis.@manageUnit",
						"render" : "Tree",
						//"onlySelectLeaf":true,
						//"filter" : "['le',['len',['$','item.key']],['i',9]]",
						"parentKey" : this.mainApp.deptId || {},
						//"defaultValue":this.mainApp.deptId,
						"rootVisible" : "true"
					});
					this.manaUtil.width=120;
					this.manaUtil.tree.expandAll();
					this.manaUtil.tree.on("expandnode", function(node) {
						var key = node.attributes["key"];
						if(key == this.mainApp.deptId){
							this.manaUtil.select(node);
						}
					},this);
					//this.manaUtil.setValue({key:this.mainApp.deptId,text:this.mainApp.dept})
					this.manaUtil.on("specialkey", this.onQueryFieldEnter, this);
					//this.manaUtil.on("blur",this.onComboBoxBlur,this);
					var curDate = Date.parseDate(this.mainApp.serverDate,'Y-m-d');
					var startDateValue = new Date(curDate.getFullYear(),0,1);
					this.startDate = new Ext.form.DateField({
								name : 'startDate',
								value : startDateValue,
								width : 90,
								altFormats : 'Y-m-d',
								format : 'Y-m-d',
								emptyText : '开始日期'
							});
					this.endDate = new Ext.form.DateField({
								name : 'endDate',
								value : curDate,
								width : 90,
								altFormats : 'Y-m-d',
								format : 'Y-m-d',
								emptyText : '结束日期',
								title:'有开始日期，结束日期为空是默认为当天日期'
							});
					var queryBtn = new Ext.Toolbar.SplitButton({
					iconCls : "query",
					style:{marginLeft:'5px'},
					width:50,
					menu : new Ext.menu.Menu({
						items : {
							text : "高级查询",
							iconCls : "common_query",
							handler : this.doAdvancedQuery,
							scope : this
						}
					})
				})
				this.queryBtn = queryBtn;
				queryBtn.on("click",this.doCndQuery,this);
				return [
					{xtype: 'tbtext', text: '调查机构:',width:65,style:{textAlign:'center'}},this.manaUtil,
					{xtype: 'tbtext', text: '调查日期:',width:65,style:{textAlign:'center'}},this.startDate,
					{xtype: 'tbtext', text: '→',width:40,style:{textAlign:'center',fontSize:36,fontWeight:'bold'}},
					this.endDate,queryBtn,'-']
			},closeWin:function(){
   		    	if(this.closeW==false){
 					 this.closeW=true;
 				   this.AduitFormWin.hide();
 				}
			}
		
		})