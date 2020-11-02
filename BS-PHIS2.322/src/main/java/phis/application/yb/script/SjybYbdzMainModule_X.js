$package("phis.application.yb.script");
$import("phis.script.SimpleModule","phis.script.Phisinterface");

phis.application.yb.script.SjybYbdzMainModule_X = function(cfg) {
	cfg.autoLoadData = false;
	cfg.exContext = {};
	cfg.width = 600;
	Ext.apply(this,phis.script.Phisinterface);
	phis.application.yb.script.SjybYbdzMainModule_X.superclass.constructor.apply(this,[cfg]);
};
Ext.extend(phis.application.yb.script.SjybYbdzMainModule_X,phis.script.SimpleModule,{
	initPanel : function(sc) {
		if (this.panel) {
			return this.panel;
		}
		var schema = sc
		if (!schema) {
			var re = util.schema.loadSync(this.entryName)
			if (re.code == 200) {
				schema = re.schema;
			} else {
				this.processReturnMsg(re.code, re.msg,this.initPanel)
				return;
			}
		}
		this.schema = schema;
		var panel = new Ext.Panel({
			border : false,
			frame : true,
			layout : 'border',
			defaults : {
				border : false
			},
			items : [ {
				layout : "fit",
				border : false,
				split : true,
				title : "",
				region : 'north',
				height : 114,
				items : this.getform()
			}, {
				layout : "fit",
				border : false,
				split : true,
				title : "",
				region : 'center',
				items : this.getlist()

			} ],
			tbar : (this.tbar || []).concat(this.createButtons())
		});
		this.panel = panel;
		return panel;
	},
	afterOpen : function() {
		this.setButtonsState(['dzmx'], true);
		this.getYwzqh()
	},
	// 获取顶部的form
	getform : function() {
		this.form = this.createModule("refsybform", this.refsybform);
		return this.form.initPanel();
	},
	// 获取下边的list
	getlist : function() {
		this.moudle = this.createModule("refsyblist",this.refsyblist);
		return this.moudle.initPanel();
	},
	doDz : function(){
		this.panel.el.mask("正在对账...");
		var form=this.form.form.getForm();
		var DZKSSJ = form.findField("DZKSSJ").getValue();
		if (DZKSSJ) {
			if (DZKSSJ.length != 8) {
				Ext.MessageBox.alert("提示", "开始日期格式不对", function() {
							form.findField("DZKSSJ").focus(false, 100);
						}, this);
				this.panel.el.unmask();
				return;
			}
		}
		var DZJSSJ = form.findField("DZJSSJ").getValue();
		if (DZJSSJ) {
			if (DZJSSJ.length != 8) {
				Ext.MessageBox.alert("提示", "结束日期格式不对", function() {
							form.findField("DZJSSJ").focus(false, 100);
						}, this);
				this.panel.el.unmask();
				return;
			}
		}
		var ret = phis.script.rmi.miniJsonRequestSync({
			serviceId : this.serviceId,
			serviceAction : "queryNjjbDz",
			body : {
				DZJSSJ : DZJSSJ,
				DZKSSJ : DZKSSJ
			}
		});
		if(ret.code > 200){
			MyMessageTip.msg("提示", "获取本地医保对账数据失败，请联系管理员。", true);
			this.panel.el.unmask();
			return;
		}
		var ZXYLFZE = ret.json.ZXYLFZE;
		var ZXTCZFJE = ret.json.ZXTCZFJE;
		var ZXDBJZZF = ret.json.ZXDBJZZF;
		var ZXDBBXZF = ret.json.ZXDBBXZF;
		var ZXMZBZZF = ret.json.ZXMZBZZF;
		var ZXZHZFZE = ret.json.ZXZHZFZE;
		var ZXXJZFZE = ret.json.ZXXJZFZE;
		var data = {};
		data.DZKSSJ = DZKSSJ;
		data.DZJSSJ = DZJSSJ
		//先执行读卡程序
		var body={};
		body.USERID=this.mainApp.uid;
		//获取业务周期号
		var ret = phis.script.rmi.miniJsonRequestSync({
				serviceId : "phis.NjjbService",
				serviceAction : "getywzqh",
				body:body
				});
		if (ret.code <= 300) {
			var ywzqh=ret.json.YWZQH;
			this.addPKPHISOBJHtmlElement();
			this.drinterfaceinit();
			var str=this.buildstr("1120",ywzqh,data);
			var drre=this.drinterfacebusinesshandle(str);
			var arr=drre.split("^");
			if(arr[0]=="0"){
				var data={};
				var canshu=arr[2].split("|")
				data.ZXYLFZE=canshu[0];//中心医疗费总额
				data.ZXTCZFJE=canshu[1];//中心统筹支付金额
				data.ZXDBJZZF=canshu[2];//中心大病救助支付
				data.ZXDBBXZF=canshu[3];//中心大病保险支付
				data.ZXMZBZZF=canshu[4];//中心民政补助支付
				data.ZXZHZFZE=canshu[5];//中心帐户支付总额
				data.ZXXJZFZE=canshu[6];//中心现金支付总额
				
				if(ZXYLFZE != data.ZXYLFZE || ZXTCZFJE!=data.ZXTCZFJE || ZXDBJZZF!=data.ZXDBJZZF || ZXDBBXZF!=data.ZXDBBXZF || ZXMZBZZF!=data.ZXMZBZZF ||
				 ZXZHZFZE!=data.ZXZHZFZE || ZXXJZFZE!=data.ZXXJZFZE){
					form.findField("dzjg").setValue("医院端医疗费总额:"+ZXYLFZE+"中心端医疗费总额:"+data.ZXYLFZE+"医院端统筹支付金额:"+ZXTCZFJE+ "中心端医疗费总额:"+data.ZXTCZFJE
					+"医院端大病救助支付:"+ZXDBJZZF+"中心端大病救助支付:"+data.ZXDBJZZF+"医院端大病保险支付:"+ZXDBBXZF+"中心端大病保险支付:"+data.ZXDBBXZF
					+"医院端民政补助支付:"+ZXMZBZZF+"中心端民政补助支付:"+data.ZXMZBZZF+"医院端帐户支付总额:"+ZXZHZFZE+"中心端帐户支付总额:"+data.ZXZHZFZE
					+"医院现金支付总额:"+ZXXJZFZE+"中心端民政补助支付:"+data.ZXXJZFZE+"        总账对账失败！请进行明细对账!")
					MyMessageTip.msg("提示：","总账对账失败！请进行明细对账!", true);
					this.panel.el.unmask();
					return;	
				} else {
					MyMessageTip.msg("提示：","总账对账成功!", true);
					this.panel.el.unmask();
					return;	
				}
				
			}else{
				MyMessageTip.msg("提示：",arr[3], true);
				this.panel.el.unmask();
				return;	
			}
		} else {
			MyMessageTip.msg("提示：",ret.msg, true);
			this.panel.el.unmask();
			return;
		}	
	},
	doDzmx : function() {
		var form=this.form.form.getForm();
		var DZKSSJ = form.findField("DZKSSJ").getValue();
		var DZJSSJ = form.findField("DZJSSJ").getValue();
		var m = this.midiModules["refSYBDZMxForm"];
		if (!m) {
			m = this.createModule("refSYBDZMxForm",this.refSYBDZMxForm);
			this.midiModules["refSYBDZMxForm"] = m;
		}
		m.DZJSSJ = DZJSSJ
		m.DZKSSJ = DZKSSJ
		var win = m.getWin();
		win.setPosition(250, 100);
		win.add(m.initPanel());
		win.show();
	},
	
    formatDate : function (date) {
        var y=date.getFullYear();
        var m=date.getMonth()+1;
        m=m<10?"0"+m :""+m;
        var d=date.getDate();
        d=d<10?("0"+d):""+d;
        return y+m+d;
    },
	getYwzqh : function(){
		var form=this.form.form.getForm();
		form.findField("DZJSSJ").setValue(this.formatDate(new Date()));
		var Nowdate = new Date();
//		var PreviousMonth = new Date(Nowdate.getFullYear(), Nowdate.getMonth() - 1, Nowdate.getDate());
		var PreviousMonth = new Date();
		form.findField("DZKSSJ").setValue(this.formatDate(PreviousMonth));
	},
	setButtonsState : function(m, enable) {
		var btns;
		var btn;
		if (this.showButtonOnTop) {
			btns = this.panel.getTopToolbar();
		} else {
			btns = this.panel.buttons;
		}
		if (!btns) {
			return;
		}
		if (this.showButtonOnTop) {
			for (var j = 0; j < m.length; j++) {
				if (!isNaN(m[j])) {
					btn = btns.items.item(m[j]);
				} else {
					btn = btns.find("cmd", m[j]);
					btn = btn[0];
				}
				if (btn) {
					(enable) ? btn.enable() : btn.disable();
				}
			}
		} else {
			for (var j = 0; j < m.length; j++) {
				if (!isNaN(m[j])) {
					btn = btns[m[j]];
				} else {
					for (var i = 0; i < this.actions.length; i++) {
						if (this.actions[i].id == m[j]) {
							btn = btns[i];
						}
					}
				}
				if (btn) {
					(enable) ? btn.enable() : btn.disable();
				}
			}
		}
	}
});