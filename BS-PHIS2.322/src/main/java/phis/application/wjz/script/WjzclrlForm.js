$package("phis.application.wjz.script");

$import("phis.script.TableForm");

phis.application.wjz.script.WjzclrlForm = function(cfg) {
	cfg.colCount = 1;
	cfg.showWinOnly = true;
	phis.application.wjz.script.WjzclrlForm.superclass.constructor.apply(this,
			[cfg]);
};

Ext.extend(phis.application.wjz.script.WjzclrlForm, phis.script.TableForm, {
			doCyy:function(){
				var list=this.createModule("cyyList",this.refCyyList);
				list.on("qr",this.onQr,this)
				var win=list.getWin();
				win.add(list.initPanel());
				win.show();
				win.center();
				if(!win.hidden){
				list.clearSelect();
				}
			},
			onQr:function(cyy){
			var cznr=this.form.getForm().findField("CZNR")
			if(cznr.getValue()!=null&&cznr.getValue().length>0){
			cznr.setValue(cznr.getValue()+","+cyy);
			}else{
			cznr.setValue(cyy);
			}
			},
			doQr : function() {
				var cznr=this.form.getForm().findField("CZNR").getValue()
				if(cznr==null||cznr==""){
				Ext.Msg.alert("提示","请先输入危机值处理的简短描述");
				return
				}
				this.form.el.mask("正在保存数据...", "x-mask-loading");
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : "wjzManageService",
							serviceAction : "saveWjzcz",
							body:{"SQDH":this.wjzxh,"CZNR":cznr,"JC":this.isjc}
						});
				this.form.el.unmask();
				if (ret.code > 300) {
							this.processReturnMsg(ret.code, ret.msg);
							return ;
				}
				this.opener.btnClick("close")
				var d = new Ext.util.DelayedTask(function(){  
              	 this.opener.doWjztx();
            	},this);  
           		 d.delay(1000);  
				this.doCancel();
			},
			doCancel : function() {
				this.getWin().hide();
			},
			getWin : function() {
				var win = this.win
				var closeAction = this.closeAction || "hide"
				if (!this.mainApp || this.closeAction == true) {
					closeAction = "hide"
				}
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.opener.wjzCheck.BRXM+"         "+this.opener.wjzCheck.SQDH,
						width : this.width,
						iconCls : 'refresh',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : closeAction,
						constrainHeader : true,
						constrain : true,
						minimizable : false,
						maximizable : false,
						shadow : false,
						modal : this.modal || false,
						items : this.initPanel()
							// add by huangpf.
						})
					var renderToEl = this.getRenderToEl()
					if (renderToEl) {
						win.render(renderToEl)
					}
					win.on("show", function() {
								this.fireEvent("winShow")
							}, this)
					win.on("add", function() {
								this.win.doLayout()
							}, this)
					win.on("beforeclose", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("beforehide", function() {
								return this.fireEvent("beforeclose", this);
							}, this);
					win.on("close", function() {
								this.fireEvent("close", this)
							}, this)
					win.on("hide", function() { // ** add by yzh 2010-06-24 **
								this.fireEvent("hide", this)
							}, this)
					this.win = win
				}
				this.doNew();
				this.wjzxh=this.opener.wjzCheck.SQDH;
				this.isjc=this.opener.wjzCheck.JC;
				return win;
			}
		});