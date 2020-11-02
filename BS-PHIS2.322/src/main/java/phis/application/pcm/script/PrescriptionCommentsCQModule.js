$package("phis.application.pcm.script");

$import("phis.script.SimpleModule");

phis.application.pcm.script.PrescriptionCommentsCQModule = function(cfg) {
	phis.application.pcm.script.PrescriptionCommentsCQModule.superclass.constructor
			.apply(this, [cfg]);
	
}
Ext.extend(phis.application.pcm.script.PrescriptionCommentsCQModule,
		phis.script.SimpleModule, {
		initPanel : function() {
				if (this.panel) {
					return this.panel;
				}
				var panel = new Ext.Panel({
							border : false,
							width : this.width,
							height : this.height,
							frame : true,
							layout : 'border',
							defaults : {
								border : false
							},
							items : [{
										layout : "fit",
										border : false,
										split : true,
										title : this.title,
										region : 'north',
										width : 960,
										height : 90,
										items : this.getForm()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '',
										region : 'center',
										width : 960,
										items : this.getList()
									}],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				return this.list.initPanel();
			},
			getFormData : function() {
				return this.form.getFormData();
			},
			getWin : function() {
				var win = this.win
				if (!win) {
					win = new Ext.Window({
						id : this.id,
						title : this.title || this.name,
						width : 700,
						height:500,
						iconCls : 'icon-grid',
						shim : true,
						layout : "fit",
						animCollapse : true,
						closeAction : "hide",
						constrainHeader : true,
						constrain : true,
						minimizable : true,
						maximizable : true,
						shadow : false,
						modal : this.modal || false
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
				return win;
			},
			doCommit : function() {
				var values = this.form.getFormData();
				if (!values) {
					return;
				}
				values["DPLX"]=this.dplx;
				if(this.isJX){
				values.isJX=1;
				}else{
				values.isJX=0;
				}
				values.DPLX=this.dplx;
				var count = this.list.store.getCount();
				var ypxhs=[];
				for (var i = 0; i < count; i++) {
				ypxhs.push(this.list.store.getAt(i).data["YPXH"]);
				}
				if(ypxhs.length>0){
				values.ypxhs=ypxhs;
				}
				this.panel.el.mask("正在保存数据...", "x-mask-loading")
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.saveServiceId,
							serviceAction : this.serviceAction,
							body : values
						});
				this.panel.el.unmask()
				if (ret.code > 300) {
					if(ret.code==900){//时间范围内有抽样记录
					Ext.Msg.show({
								title : "提示",
								msg : "时间范围内已经有抽样记录,是否继续抽样?",
								modal : true,
								width : 300,
								buttons : Ext.MessageBox.OKCANCEL,
								multiline : false,
								fn : function(btn, text) {
									if (btn == "ok") {
										this.isJX=true;
										this.doCommit();
									}else{
									this.isJX=false;
									}
								},
								scope : this
							});
					}else{
					this.processReturnMsg(ret.code, ret.msg);
					}
					return;
				}
				MyMessageTip.msg("提示", "保存成功", true);
				this.doClose();
				this.fireEvent("cqcg");
			},
			doClose : function() {
				this.getWin().hide();
			},
			doNew : function() {
			this.form.doNew();
			this.list.clear();
			}
		});