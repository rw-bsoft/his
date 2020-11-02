/**
 * 网格地址批量修改表单
 * 
 * @author : chenhb
 */
$package("chis.application.ag.script")
$import("app.desktop.Module")

chis.application.ag.script.AreaGridBatchModel = function(cfg){
	this.width = 500
	this.height = 170
	chis.application.ag.script.AreaGridBatchModel.superclass.constructor.apply(this, [cfg])
}

Ext.extend(chis.application.ag.script.AreaGridBatchModel,app.desktop.Module,{
	initPanel:function(){
		var areaGrid = this.createDicField({id:"chis.dictionary.areaGrid",checkModel:"childCascade",name:"regionCode",render:"TreeCheck",rootVisible:"true",parentKey:"%user.regionCode",alias:"网格地址"})
		var  areaGridTree  = areaGrid.tree;
//		areaGridTree.on("checkchange",this.onAreaGridCheckChange,this);
//		areaGridTree.on("expandnode",this.onAreaGridExpaned,this);
		var u1 = this.createDicField({id:"chis.dictionary.user01",name:"manaDoctor",render:"TreeCheck",parentKey:"%user.manageUnit.id",onlyLeafCheckable:"true",rootVisible:"true",alias:"责任医生"})
		var u2 = this.createDicField({id:"chis.dictionary.user02",name:"cdhDoctor",render:"TreeCheck",parentKey:"%user.manageUnit.id",onlyLeafCheckable:"true",rootVisible:"true",alias:"儿保医生"})
		var u3 = this.createDicField({id:"chis.dictionary.user03",name:"mhcDoctor",render:"TreeCheck",parentKey:"%user.manageUnit.id",onlyLeafCheckable:"true",rootVisible:"true",alias:"妇保医生"})
		var panel = new Ext.FormPanel({
			title:"批量查询",
			labelWidth:80,
			height:this.height,
			frame:true,
			items:[areaGrid,u1,u2,u3],
			buttonAlign:"right",
			buttons:[{
				text:"开始查询",
				iconCls:"archiveMove_commit",
				handler:this.doCndQuery,
				scope:this
			}]
		})
		this.panel = panel
		var u1_f = this.createDicField({id:"chis.dictionary.user01",name:"manaDoctor_f",render:"Tree",parentKey:"%user.manageUnit.id",onlySelectLeaf:"true",rootVisible:"true",alias:"责任医生"})
		var u2_f = this.createDicField({id:"chis.dictionary.user02",name:"cdhDoctor_f",render:"Tree",parentKey:"%user.manageUnit.id",onlySelectLeaf:"true",rootVisible:"true",alias:"儿保医生"})
		var u3_f = this.createDicField({id:"chis.dictionary.user03",name:"mhcDoctor_f",render:"Tree",parentKey:"%user.manageUnit.id",onlySelectLeaf:"true",rootVisible:"true",alias:"妇保医生"})
		var form = new Ext.FormPanel({
			disabled:true,
			title:"对查询结果批量修改",
			frame:true,
			width:500,
			height:160,
			items:[u1_f,u2_f,u3_f],
			buttonAlign:"right",
			buttons:[{
				text:"开始批量修改",
				iconCls:"archiveMove_commit",
				handler:this.doBatchUpdateData,
				scope:this
			}]
		})
		this.form = form
		var tabpanel = new Ext.TabPanel({
			items:[panel,form],
			activeTab:0
		})
		this.tabpanel = tabpanel
		return tabpanel
	},
	
	onAreaGridCheckChange:function(node,checked){
		if(checked){
			this.setChildStatus(node,true);
			this.setParentStatus(node,true);
		}else{
			this.setChildStatus(node,false);
			this.setParentStatus(node,false);
		}
	},
	setChildStatus:function(node,check){
		if(node.isExpanded()==false){
			return ;
		}
		var children = node.childNodes;
		for(var i=0;i<children.length;i++){
			var c =children[i];
			c.getUI().check(check);
			if(c.isExpanded()){
				this.setChildStatus(c,check);
			}
		}
	},
	setParentStatus:function(node,check){
		var parentNode = node.parentNode;
		if(!parentNode){
			return ;
		}
		
		if(check){//如果true 需要判断所有父节点的子节点是否全部选中,
			var children = parentNode.childNodes;
			for(var i=0;i<children.length ;i++){
				var c = children[i];
				if(c.getUI().isChecked()==false){
					return ;
				}
			}
			parentNode.getUI().check(true);
			this.setParentStatus(parentNode,true);
		}else{//父节点全部取消
			parentNode.getUI().check(false);
			this.setParentStatus(parentNode,false);
		}
	},
	onAreaGridExpaned:function(node){
		if(node.getUI().isChecked()==false){
			return ;
		}
		
		var nodes = node.childNodes;
		for (var i = 0; i < nodes.length; i++) {
			nodes[i].getUI().check(true);
			this.setChildStatus(nodes[i],true)
		}
	},
	doCndQuery:function(){
		var form = this.panel.getForm()
		var regionCode = form.findField("regionCode")
		var manaDoctor = form.findField("manaDoctor")
		var cdhDoctor = form.findField("cdhDoctor")
		var mhcDoctor = form.findField("mhcDoctor")
		if(!regionCode.getValue()  && !manaDoctor.getValue()
			&& !cdhDoctor.getValue() && !mhcDoctor.getValue()){
			return;
		}
		var cnd = ['and',['eq',['i',1],['i',1]]]
		this.addCnd(regionCode,cnd,true)
//		this.addCnd(manaDoctor,cnd)
//		this.addCnd(cdhDoctor,cnd)
//		this.addCnd(mhcDoctor,cnd)
		this.list.resetFirstPage()
		this.list.requestData.manaDoctor = manaDoctor.getValue()
		this.list.requestData.cdhDoctor = cdhDoctor.getValue()
		this.list.requestData.mhcDoctor = mhcDoctor.getValue()
		this.list.requestData.cnd = cnd
		this.form.setDisabled(false)
		this.list.refresh()	
	},
	doBatchUpdateData:function(){
		var form = this.form.getForm()
		if(!form.findField("manaDoctor_f").getValue()
			&& !form.findField("cdhDoctor_f").getValue() && !form.findField("mhcDoctor_f").getValue()){
			return;
		}
		this.win.hide()
		Ext.MessageBox.show({
			title:"消息",
			msg:"正在批量修改...请稍后...",
			width:280,
			progress:true,
			wait:true,
			waitConfig:{
				interval:200
			}					
		})
		util.rmi.jsonRequest({
				serviceId:"chis.agService",
				serviceAction:"batchUpdate",
				method:"execute",
				manaDoctor:form.findField("manaDoctor_f").getValue(),
				cdhDoctor:form.findField("cdhDoctor_f").getValue(),
				mhcDoctor:form.findField("mhcDoctor_f").getValue(),
				cnd:this.list.requestData.cnd
			},function(code,msg,json){
				Ext.MessageBox.hide()
				if(code < 300){
					this.list.refresh()
				}else{
					alert(msg)
				}
			},this)
	},
	addCnd:function(field,cnd,blur){
		var l = blur?"%":""
		var sign = blur?"like":"eq"
		if(field && field.getValue()){
			var v = field.getValue().split(",")
			if(v.length == 1){
				cnd.push([sign,['$',field.name],['s',v[0]+l]])
			}else{
				var d = ['or']
				for(var i=0;i<v.length;i++){
					if(v[i]){
						d.push([sign,['$',field.name],['s',v[i]+l]])
					}
				}
				cnd.push(d)
			}
		}
	},
	createDicField:function(dic){
		var cls = "util.dictionary.";
		if(!dic.render){
			cls += "Simple";
		}
		else{
			cls += dic.render
		}
		cls += "DicFactory"
		$import(cls)
		var factory = eval("(" + cls + ")")
		dic.onlySelectLeaf  = dic.onlySelectLeaf || false
		dic.width  = dic.width || 360
		var field = factory.createDic(dic)
		field.fieldLabel = dic.alias || "请选择"
		field.name = dic.name
		return field
	},
	getWin:function(){
		var win = new Ext.Window({
				title:this.title||"批量修改",
				width:this.width,
				constrain:true,
				closeAction:"hide",
				layout:"fit",
				items:this.initPanel()
			})
			var renderToEl = this.getRenderToEl()
			if (renderToEl) {
				win.render(renderToEl)
			}
			win.on("add", function() {
				this.win.doLayout()
			}, this)
			this.win = win
			win.instance = this;
		return win
	}
})