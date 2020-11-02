$package("phis.application.sup.script");
$import("phis.script.SimpleModule");

phis.application.sup.script.StorageConfirmeModule = function(cfg) {
	this.width = 1024;
	this.height = 500;
	phis.application.sup.script.StorageConfirmeModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.sup.script.StorageConfirmeModule,
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
										title : '',
										region : 'north',
										width : 960,
										height : 89,
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
			doAction : function(item, e) {
				var cmd = item.cmd
				var script = item.script
				cmd = cmd.charAt(0).toUpperCase() + cmd.substr(1)
				if (script) {
					$require(script, [function() {
								eval(script + '.do' + cmd
										+ '.apply(this,[item,e])')
							}, this])
				} else {
					var action = this["do" + cmd]
					if (action) {
						action.apply(this, [item, e])
					}
				}
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				return this.list.initPanel();
			},
			doCommit : function() {
				var DJXH = this.DJXH;
				if(this.form.form.getForm().findField("QRBZ").getValue() == 1){
					return;
				}
				if(this.list.store.getCount() <1){
					MyMessageTip.msg("提示", "没有明细不能确认!", true);
					return ;
				}
				// 盘点状态
				if (this.mainApp['phis'].treasuryPdzt == 1) {
					Ext.Msg.alert("提示", "库房正处于盘点状态，无法进行操作");
					return;
				}
				// 流转方式
				var LZFS = this.form.form.getForm().findField("LZFS").getValue();
				if(!LZFS){
					MyMessageTip.msg("提示", "流转方式不能为空!", true);
					return ;
				}
				// 科室代码
				var KSDM = this.form.form.getForm().findField("KSDM").getValue();
				if(!KSDM){
					MyMessageTip.msg("提示", "科室代码不能为空!", true);
					return ;
				}
				// 盘点状态
				if (this.mainApp['phis'].treasuryPdzt == 1) {
			     	Ext.Msg.alert("提示", "库房正处于盘点状态，无法进行操作");
					return;
				}
				// 判断确认标志
				if(this.form.form.getForm().findField("QRBZ").getValue() != 0){
					Ext.Msg.alert("提示", "不在确认入库状态!");
					return;
				}
				
				var count  = this.list.store.getCount();
				var bodys = [];
				for(var i = 0 ;i< count;i++){
					var body = {};
					var r = this.list.store.getAt(i);
					body["DJXH"]= r.get("DJXH");
					body["JLXH"]= r.get("JLXH");
					body["ZBLB"]= this.ZBLB;
					if(this.KFXH){
						body["KFXH"]=this.KFXH;
					}else{ // 退回单据没有kfxh
						body["KFXH"]=this.mainApp['phis'].treasuryId;
					}
					body["CKRQ"]=this.CKRQ;
					body["CJXH"]= r.get("CJXH");
					body["KCXH"]= r.get("KCXH");
					body["WZXH"]= r.get("WZXH");
					body["SCRQ"]= r.get("SCRQ");
					body["SXRQ"]= r.get("SXRQ");
					body["WZPH"]= r.get("WZPH");
					body["WZSL"]= r.get("WZSL");
					body["WZJG"]= r.get("WZJG");
					body["WZJE"]= r.get("WZJE");
                    body["LSJG"]= r.get("LSJG");
                    body["LSJE"]= r.get("LSJE");
					body["MJPH"]= r.get("MJPH");
					body["GLFS"]= r.get("GLFS");
					body["ZBXH"]= r.get("ZBXH");
					body["LZFS"]= LZFS;
					body["KSDM"]= KSDM;
					body["WZMC"]= r.get("WZMC");
					bodys[i] = body;
				}
				var ret = phis.script.rmi.miniJsonRequestSync({
						serviceId : "storageConfirmeService",
						serviceAction : "saveCommit",
						body : bodys
					 });
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doSave);
					return;
				}
				MyMessageTip.msg("提示", "确认成功", true);
				this.fireEvent("save", this);
				this.doCancel();
				
			},
			doCancel : function() {
				this.fireEvent("winClose", this);
			}
			
		});