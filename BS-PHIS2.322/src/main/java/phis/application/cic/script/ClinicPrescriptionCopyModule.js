$package("phis.application.cic.script");

$import("phis.script.SimpleModule","phis.script.widgets.MyMessageTip", 
		"phis.script.util.DateUtil");
/**
 * 门诊处方复制
 * @param cfg
 */
phis.application.cic.script.ClinicPrescriptionCopyModule = function(cfg) {
	cfg.width = 1024;
	cfg.height = 768;
//	this.serviceId = "clinicCopyService";
	phis.application.cic.script.ClinicPrescriptionCopyModule.superclass.constructor
			.apply(this, [cfg]);
},

Ext.extend(phis.application.cic.script.ClinicPrescriptionCopyModule,
		phis.script.SimpleModule, {
	initPanel : function() {
		if(this.panel){
			return this.panel;
		}
		var tbar = [];
		var actions = this.actions;
		// 遍历Applications.xml中配置的Tab页，并将其添加到centerTBar中
		for (var i = 0; i < actions.length; i++) {
			var ac = actions[i];
			var btn = {};
			btn.id = ac.id;
			btn.text = ac.name, btn.iconCls = ac.iconCls || ac.id;
			// 添加doAction点击事件,调用doAction方法
			btn.handler = this.doAction;
			btn.name = ac.id;
			btn.scope = this;
			tbar.push(btn);
		}
		var p1 = new Ext.Panel({
			frame : false,
			layout : 'border',
			tbar : tbar,
			items : [{
						layout : "fit",
						split : true,
						title : '门诊处方',
						collapsible : false,
						height : 250,
						region : 'north',
						width : 150,
						items : [this.getNList()]
					}, {
						layout : "fit",
						split : true,
						collapsible : false,
						title : '处方明细',
						region : 'center',
						width : 500,
						items : [this.getCList()]
					}]
		});
		var panel = new Ext.Panel({
			frame : false,
			layout : 'border',
			items : [{
						layout : "fit",
						split : true,
						title : '就诊记录',
						collapsible : false,
						height : 250,
						region : 'west',
						width : 330,
						items : [this.getWList()]
					},{
						layout : "fit",
						split : true,
						collapsible : false,
						title : '',
						region : 'center',
						width : 500,
						items : [p1]
					}]
		});
		this.panel = panel;
		return this.panel;
	},
	getWList : function(){
		var module = this.createModule("refPrescriptionJL", this.refPrescriptionJL);
		module.opener = this;
		return module.initPanel(this.brid);
	},
	getNList : function(){
		var module = this.createModule("refPrescriptionCF01", this.refPrescriptionCF01);
		module.opener = this;
		return module.initPanel(this.brid,this.clinicType);
	},
	getCList : function(){
		var module = this.createModule("refPrescriptionCF02", this.refPrescriptionCF02);
		module.opener = this;
		return module.initPanel(this.brid);
	},
	/**
	 * 清空处方01和02列表中的数据
	 */
	clearCFData : function(){
		 var cf01 = this.midiModules['refPrescriptionCF01'];
		 cf01.clear();
		 var cf02 = this.midiModules['refPrescriptionCF02'];
		 cf02.clear();
	},
	/**
	 * 显示处方01
	 * @param jzxh
	 */
	showCF01Data : function(jzxh){
		this.clearCFData();
		var module = this.midiModules['refPrescriptionCF01'];
		module.reloadData(jzxh, this.brid, this.clinicType);
	},
	/**
	 * 显示处方02列表
	 * @param cfsb
	 */
	showCF02Data : function(cfsb){
		 var module = this.midiModules['refPrescriptionCF02'];
		 module.reloadData(cfsb, this.brid);
	},
	initData : function(brid, brxz, clinicType,tsypBs){
		this.brid = brid;
		this.brxz = brxz;
		this.clinicType = clinicType;
		if(tsypBs==null){
			this.forstNull=true;//第一条药品为空
		}else{
			this.forstNull=false;//第一条药品不为空
			tsypBs=parseInt(tsypBs);
			if(tsypBs==1 || tsypBs==7 || tsypBs==8){
				this.tscfPb=true;
			}else{
				this.tscfPb=false;
			}
			this.tsypBs=tsypBs;
		}
		
	},
	doAction : function(item, e){
		if(item.id == 'confirm'){//确认按钮
			this.confirm();
		}else if(item.id = 'refresh'){//刷新按钮
			this.reloadPrescription();
		}
	},
	confirm : function(){
		var cf01 = this.midiModules['refPrescriptionCF01'].getSelectData();
		var cf02 = this.midiModules['refPrescriptionCF02'].getAllData();
		if(cf01.length == 0){
			MyMessageTip.msg("提示", "未选择拷贝处方!", true);
		}
		var cf01Json =[];
		var cf02Json = [];
		//处方01选中列表放入cf01Json中
		for (var i = 0; i < cf01.length; i++) {
			cf01Json.push(cf01[i].json);
		}
		//处方02选中列表放入cf02Json中
		var cf02json ;
		for (var i = 0; i < cf02.length; i++) {
			if(i==0){
				 var tsypKey=cf02[i].json.TSYP;
				 tsypKey=parseInt(tsypKey);
				 this.tsypKey=tsypKey;
				 
			}
			cf02json = cf02[i].json;
			cf02json.SFJG = 0;//修改1425bug，对复制的过来的处方将其审方结果置为0
			cf02Json.push(cf02json);
		}
		if(!this.forstNull){
			if (!this.tscfPb) {
				if (this.tsypKey == 1 || this.tsypKey == 7 || this.tsypKey == 8) {
					MyMessageTip.msg("提示",
							'该处方已开具普通药品，特殊药品不能和普通药品开具同一张处方上!', true);
					return ;
				}
			}
			if (this.tscfPb) {
				if (this.tsypKey == 1 || this.tsypKey == 7 || this.tsypKey == 8) {
	                   if(this.tsypBs == 1 && this.tsypKey != 1){
	                	   MyMessageTip.msg("提示",
									'该处方已开具麻醉特殊药品，非麻醉特殊药品不能开具在该处方上!', true);
							return false;
	                   }else if(this.tsypBs == 7 && this.tsypKey != 7){
	                	   MyMessageTip.msg("提示",
	  								'该处方已开具“精一”特殊药品，非“精一”特殊药品不能开具在该处方上!', true);
	  						return false;
	                   }else if(this.tsypBs == 8 && this.tsypKey != 8){
	                	   MyMessageTip.msg("提示",
	 								'该处方已开具“精二”特殊药品，非“精二”特殊药品不能开具在该处方上!', true);
	 						return false;
	                   }
				} else {
					MyMessageTip.msg("提示",
							'该处方已开具特殊药品，普通药品不能和特殊药品开具同一张处方上!', true);
					return false;
				}
			}
		}
		
		var body = {};
		body.BRXZ = this.brxz;
		body.cf01List = cf01Json;
		body.cf02List = cf02Json;
		var result = phis.script.rmi.miniJsonRequestSync({
			serviceId : this.serviceId,
			serviceAction : this.serviceAction,
			body : body
		});
		if(result.code == 200){
			this.opener.perscriptionCopy(result.json);
		}else{
			this.processReturnMsg(code, msg);
		}
	},
	/**
	 * 重新加载处方
	 */
	reloadPrescription : function(){
		var module1 = this.midiModules['refPrescriptionJL'];
		module1.initPanel(this.brid);
		module1.refresh();
//		var module2 = this.midiModules['refPrescriptionCF01'];
//		module2.refresh();
	}
	
});