$package('phis.application.cic.script');
$import('phis.script.SimpleModule','phis.application.cic.script.IframeModule',"phis.script.widgets.MyMessageTip");
phis.application.cic.script.InjuryReportCard=function(cfg){
	phis.application.cic.script.InjuryReportCard.superclass.constructor.call(this,cfg);	
}
Ext.extend(phis.application.cic.script.InjuryReportCard,phis.script.SimpleModule,{
	initPanel:function(){
		var res = phis.script.rmi.miniJsonRequestSync({
			serviceId : 'injuryReportCardService',
			serviceAction : 'getParam',
			empiid:this.empiid	
		});
		if(res.code==200){
			var module=new phis.application.cic.script.IframeModule({
				params:res.json.body,
				url:'http://10.98.72.8:8083/SHJCServer/shjc/bgk_toAdd'
			})
			module.params.shzd=this.jbxh;
			console.log(this.jbmc)
			var panel = new Ext.Panel({
	            layout: 'fit',
	            width: this.width,
	            height: this.height,
	            //autoHeight:true,
	            items: module.initPanel(),
	            autoScroll: false,
				buttonAlign:'center',
				buttons:[{
					text:'确认',
					handler:function(){
						this.verifyReport(res.json.body);
					},
					scope:this,
					iconCls:'save'
				}/*,{
					text:'取消/删除诊断',
					handler:function(){
						this.getWin().hide();
						this.fireEvent('cancel');
					},
					scope:this,
					iconCls:'common_cancel'
				}*/]
	        });
			
			return panel;
		}
		return '获取个人编码错误，请检查该病人是否有健康档案';
	},
	verifyReport:function(obj){
		var res = phis.script.rmi.miniJsonRequestSync({
			serviceId : 'injuryReportCardService',
			serviceAction : 'verifyReport',
			bkdh:obj.kpbh,
			yybh:obj.yybh
		});
		if(res.code==200){
			this.showResult(res.json.status);
		}else{
			this.showResult('');
		}
	},
	showResult:function(status){
		if(status==0){
			Ext.Msg.alert('提示','报卡未提交成功，请点击报卡页面内的保存按钮重试');
		}else{
			if(status==-1){
				Ext.Msg.alert('提示','40数据库连接异常，请联系管理员',this.getWin().hide);
			}else if(status==1){
				this.getWin().hide();
			}else{
				Ext.Msg.alert('提示','系统异常，请联系管理员',this.getWin().hide);
			}
		}
	},
	getWin : function() {
		var win = this.win;
		var closeAction = "hide";
		if (!win) {
			win = new Ext.Window({
						id : this.id,
						title : this.title || this.name,
						width : this.width,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						constrain : true,
						resizable : false,
						closeAction : closeAction,
						constrainHeader : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || true,
						items : this.initPanel(),
						closable:false
					});
			win.on("show", function() {
						this.fireEvent("winShow");
					}, this);
			win.on("beforeshow", function() {
						this.fireEvent("beforeWinShow");
					}, this);
			win.on("close", function() {
						this.fireEvent("close", this);
					}, this);
			win.on("beforeclose", function() {
						return this.fireEvent("beforeclose", this);
					}, this);
			win.on("beforehide", function() {
						return this.fireEvent("beforeclose", this);
					}, this);
			win.on("hide", function() {
						this.fireEvent("close", this);
					}, this);
			var renderToEl = this.getRenderToEl();
			if (renderToEl) {
				win.render(renderToEl);
			}
			this.win = win;
		}
		win.instance = this;
		return win;
	}
});
phis.application.cic.script.InjuryReportCard.can=function(empiid,shzd){
	var res = phis.script.rmi.miniJsonRequestSync({
		serviceId : 'injuryReportCardService',
		serviceAction : 'detectWhetherExists',
		empiid:empiid,
		shzd:shzd
	});
	if(res.code==200){
		if(!res.json.body){
			return true;
		}
		console.log(res.json.body.result)
		if(res.json.body.result==false){
			MyMessageTip.msg("提示", res.json.body.message, true);
		}
		return res.json.body.result;
	}
	return true;
}
