$package("chis.application.quality.script")

$import("chis.script.BizSimpleListView") 

chis.application.quality.script.QualityControl_tnbYsj_list = function(cfg) {
	this.autoLoadData = true
	this.xmxhId="";
	cfg.initCnd="";
	chis.application.quality.script.QualityControl_tnbYsj_list.superclass.constructor.apply(
			this, [cfg]);
	this.height = 270;
	// this.disablePagingTbr = true
}
Ext.extend(chis.application.quality.script.QualityControl_tnbYsj_list,
		chis.script.BizSimpleListView, {
         onReady: function(){
 			chis.application.quality.script.QualityControl_tnbYsj_list.superclass.onReady.call(this);
//				this.initCnd = ["eq", ["$", "XMBS"],
//						["s", this.xmxhId]]
//				this.requestData.cnd = this.initCnd
//				this.doNewSet();
			} 
			,
		  getCndBar : function(items){
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
				
				
				this.ZKZB= this.createDicField({
							"width" :100,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.QualityControl_ZKZB",
							"render" : "Tree",
						 	"selectOnFocus" : true,
							"allowBlank" : "false",
							"defaultValue" : {
								"key" : this.gxsdCfgKey,
								"text" : this.gxsdCfgText
							}
					});
					this.ZKLB= this.createDicField({
							"width" :100,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.QualityControl_ZKLB",
							"render" : "Tree",
						 	"selectOnFocus" : true,
							"allowBlank" : "false",
							"defaultValue" : {
								"key" : this.gxsdCfgKey,
								"text" : this.gxsdCfgText
							}
					});
					this.ZKJB= this.createDicField({
							"width" :100,
							"defaultIndex" : 0,
							"id" : "chis.dictionary.QualityControl_ZKJB",
							"render" : "Tree",
						 	"selectOnFocus" : true,
							"allowBlank" : "false",
							"defaultValue" : {
								"key" : this.gxsdCfgKey,
								"text" : this.gxsdCfgText
							}
					});
				return [
					{xtype: 'tbtext', text: '管辖机构:',width:65,style:{textAlign:'center'}},this.manaUtil,
					{xtype: 'tbtext', text: '组别:',width:65,style:{textAlign:'center'}},this.ZKZB,
					{xtype: 'tbtext', text: '类别:',width:65,style:{textAlign:'center'}},this.ZKLB,
					{xtype: 'tbtext', text: '级别:',width:65,style:{textAlign:'center'}},this.ZKJB,
					{xtype: 'tbtext', text: '调查日期:',width:65,style:{textAlign:'center'}},this.startDate,
					{xtype: 'tbtext', text: '→',width:40,style:{textAlign:'center',fontSize:36,fontWeight:'bold'}},
					this.endDate,queryBtn,'-']
			},
			doCndQuery : function(button, e, addNavCnd) {
				var initCnd = this.initCnd || [];
				if (addNavCnd && this.navCnd) {
					if(this.initCnd){
						if(initCnd.length > 0){
							initCnd = ['and', initCnd, this.navCnd];
						}else{
							initCnd = this.navCnd;
						}
					}
				} 
				//取管辖机构
				var cnd = [];
				var manaUnitId = this.manaUtil.getValue();
				if(manaUnitId){
					cnd = ['like',['$','a.inputUnit'],['s',manaUnitId]];
				}
				//开始、结束日期
				var startDate = this.startDate.getValue();
				var endDate = this.endDate.getValue();
				if(startDate && !endDate){
					endDate = Date.parseDate(this.mainApp.serverDate, "Y-m-d");
				}
				if(startDate && endDate){
					var df = "yyyy-MM-dd";
					var sd = startDate.format("Y-m-d");
					var ed = endDate.format("Y-m-d");
					var dcnd = ['and',['ge',['$',"str(a.visitDate,'yyyy-MM-dd')"],['s',sd]],
												  ['le',['$',"str(a.visitDate,'yyyy-MM-dd')"],['s',ed]]
												  ]
					if(cnd.length > 0){
						cnd = ['and', cnd, dcnd];
					}else{
						cnd = dcnd;
					}
				}
				//组别
				var ZKZB = this.ZKZB.getValue();
				if(ZKZB && ZKZB !='09'){
					hcnd = ['eq',['$','a.diabetesGroup'],['s',ZKZB || '']]
					if(cnd.length > 0){
						cnd = ['and', cnd, hcnd];
					}else{
						cnd = hcnd;
					}
				}
				this.queryCnd = cnd
				this.requestData.cnd = cnd
				var values=this.getValues();
				this.fireEvent("onread", values);//添加第一list查询时间返回查询数据
				this.refresh()
			},doAddYbYz:function(){
			     var values=this.getValues();
			           values["ZKZB"]=this.ZKZB.getValue();
			     if(values && (values["ZKZB"]==null || values["ZKZB"]=="")){
					MyMessageTip.msg("提示", "组别不能为空！",true);
					return
				}
				 if(values && (values["ZKLB"]==null || values["ZKLB"]=="")){
					MyMessageTip.msg("提示", "类别不能为空！",true);
					return
				}
				 if(values && (values["ZKJB"]==null || values["ZKJB"]=="")){
					MyMessageTip.msg("提示", "级别不能为空！",true);
					return
				}
				if(values && (values["ZKJB"]==null || values["ZKJB"]=="")){
					MyMessageTip.msg("提示", "级别不能为空！",true);
					return
				}
				var r = this.getSelectedRecord();
				if (!r) {
					MyMessageTip.msg("提示", "请选择添加的人员!", true);
					return;
				}
			 	var visitId = r.get("visitId");
			          values["visitId"]=visitId;
			     this.fireEvent("addYbYZ", values);
			     this.fireEvent("addYbZK", visitId);
			},getValues:function(){
			      var values={};
			           values["manaUtil"]=this.manaUtil.getValue();
			           values["ZKLB"]=this.ZKLB.getValue();
			           values["ZKJB"]=this.ZKJB.getValue();
			           values["startDate"] = this.startDate.getValue();
				       values["endDate"]= this.endDate.getValue();
				    return values;
			}
		})