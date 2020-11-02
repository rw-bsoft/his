$package("phis.application.fsb.script");

$import("phis.script.SimpleModule");

phis.application.fsb.script.FamilySickBedProjectSubmissionModule = function(cfg) {
	//cfg.yzlx=0;
	phis.application.fsb.script.FamilySickBedProjectSubmissionModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.fsb.script.FamilySickBedProjectSubmissionModule,
		phis.script.SimpleModule, {
			initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
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
										title : '',
										region : 'west',
										width : 230,
										items : this.getLeftList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										items : this.getRightList()
									}],
							tbar : this.createButtons()
						});
				this.panel = panel;
				return panel;
			},
			getLeftList : function() {
				this.leftList = this.createModule("leftList", this.leftRef);
				this.leftList.on("selectRecord",this.onSelectRecord,this);
				return this.leftList.initPanel();
			},
			getRightList : function() {
				this.rightList = this.createModule("rightList", this.rightRef);
				this.rightList.on("loadData",this.onRightLoadData,this)
				return this.rightList.initPanel();
			},
			createDicField : function(dic) {
				var cls = "util.dictionary.";
				if (!dic.render) {
					cls += "Simple";
				} else {
					cls += dic.render
				}
				cls += "DicFactory"
				$import(cls)
				var factory = eval("(" + cls + ")")
				var field = factory.createDic(dic)
				return field
			},
			//刷新
			doRefresh:function(){
			var body={};
//			var yzlx=this.yzlx;
//			if(yzlx!=0){
//			body["YZLX"]=yzlx;
//			}
			if(this.ZYH){
			body["ZYH"]=this.ZYH;
			}
			this.leftList.requestData.body=body;
			this.leftList.loadData();
			},
			//左边病人选中
			onSelectRecord:function(){
			var body={}
			var records=this.leftList.getSelectedRecords();
			this.rightList.clear();
			var length=records.length;
			if(length==0){
			this.rightList.clear();
			return;}
			var zyhs=new Array();
			for(var i=0;i<length;i++){
			var r=records[i];
			zyhs.push(r.get("ZYH"));
			}
			body["ZYHS"]=zyhs;
//			var yzlx=this.yzlx;
//			if(yzlx!=0){
//			body["YZLX"]=yzlx;
//			}
			this.panel.el.mask("正在加载","x-mask-loading")
			this.rightList.requestData.body=body;
			this.rightList.loadData();
			},
			onRightLoadData:function(){
			this.panel.el.unmask();
			},
			//提交
			doTj:function(){
			this.doSave(null,null,1);
			},
			//保存
			doSave:function(item,e,tag){
			var actionid=this.saveActionId;
			var ts="保存成功!";
			if(tag){
			actionid=this.commitActionId;
			ts="提交成功";
			}
			var records=this.rightList.getSelectedRecords();
			if(!records||records.length==0){
			MyMessageTip.msg("提示","请先选择提交数据",false);
			return;
			}
			var body=[];
			var l=records.length;
			for(var i=0;i<l;i++){
				var zxks=records[i].data.ZXKS;
				if(!zxks||zxks==null||zxks==""){
				MyMessageTip.msg("提示","项目["+records[i].data.YZMC+"]未选择执行科室",false);
				return;
				}
			body.push(records[i].data);
			}
			this.panel.el.mask("正在提交...", "x-mask-loading");
			var r = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : actionid,
							body : body
						});
				this.panel.el.unmask();
				if (r.code > 300) {
					this.processReturnMsg(r.code, r.msg, this.doSave);
					return;
				}
			MyMessageTip.msg("提示",ts, true);
			this.fireEvent("save",this)
			this.doRefresh();
			},
			//重写为了关闭按钮在开医嘱那显示 非开医嘱那不显示
			createButtons : function(level) {
		var actions = this.actions
		var buttons = []
		if (!actions) {
			return buttons
		}
		if (this.butRule) {
			var ac = util.Accredit
			if (ac.canCreate(this.butRule)) {
				this.actions.unshift({
							id : "create",
							name : "新建"
						})
			}
		}
		var f1 = 112
		for (var i = 0; i < actions.length; i++) {
			var action = actions[i];
			if(!this.ZYH&&action.id=="cancel"){
			continue
			}
			if (action.properties && action.properties.hide) {
				continue
			}
			level = level || 'one';
			if (action.properties) {
				action.properties.level = action.properties.level || 'one';
			} else {
				action.properties = {};
				action.properties.level = 'one';
			}
			if (action.properties && action.properties.level != level) {
				continue;
			}
			// ** add by yzh **
			var btnFlag;
			if (action.notReadOnly)
				btnFlag = false
			else
				btnFlag = (this.exContext && this.exContext.readOnly) || false

			if (action.properties && action.properties.scale) {
				action.scale = action.properties.scale
			}
			var btn = {
				accessKey : f1 + i,
				text : action.name
						+ (this.noDefaultBtnKey ? "" : "(F" + (i + 1) + ")"),
				ref : action.ref,
				target : action.target,
				cmd : action.delegate || action.id,
				iconCls : action.iconCls || action.id,
				enableToggle : (action.toggle == "true"),
				scale : action.scale || "small",
				// ** add by yzh **
				disabled : btnFlag,
				notReadOnly : action.notReadOnly,

				script : action.script,
				handler : this.doAction,
				scope : this
			}
			buttons.push(btn)
		}
		return buttons

	},
	doCancel:function(){
	this.fireEvent("cancel",this);
	}
		});