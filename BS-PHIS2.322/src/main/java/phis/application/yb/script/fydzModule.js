/**
 * 医保药品对照module
 * 
 * @author caijy
 */
$package("phis.application.yb.script");

$import("phis.script.SimpleModule");

phis.application.yb.script.fydzModule = function(cfg) {
	this.updateRecord={}
	phis.application.yb.script.fydzModule.superclass.constructor.apply(
			this, [cfg]);
}
Ext.extend(phis.application.yb.script.fydzModule,
		phis.script.SimpleModule, {
			initPanel : function(sc) {
				if (this.panel) {
					return this.panel;
				}
				var schema = sc
//				if (!schema) {
//					var re = util.schema.loadSync(this.entryName)
//					if (re.code == 200) {
//						schema = re.schema;
//					} else {
//						this.processReturnMsg(re.code, re.msg, this.initPanel)
//						return;
//					}
//				}
				this.schema = schema;
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
										title : '本地费用信息',
										region : 'west',
										width : 600,
										items : this.getLList()
									}, {
										layout : "fit",
										border : false,
										split : true,
										title : '医保费用信息',
										region : 'center',
										items : this.getRList()
									}],
								tbar:this.createButtons()	
						});
				this.panel = panel;
				return panel;
			},
			// 本地费用信息
			getLList : function() {
				this.leftList = this.createModule("leftList", this.refLeftList);
				this.leftList.on("dblClick",this.onLeftDblClick, this)
				return this.leftList.initPanel();
			},
			// 医保费用信息
			getRList : function() {
				this.rightList = this.createModule("rightList",this.refRightList)
				this.rightList.on("dblClick",this.onRightDblClick, this)
				return this.rightList.initPanel();
			},
			//刷新,只有点刷新或保存按钮才会清除缓存数据
			doSx:function(){
			this.updateRecord={};
			this.leftList.loadData();
			this.rightList.loadData();
			},
			//左边list双击,右边刷新(就刷新对照的一条医保信息)
			onLeftDblClick:function(ybfybm){
			this.rightList.ybfybm=ybfybm;
			this.rightList.loadData();
			},
			//右边点击,左边赋值YBYPXH并将修改的数据缓存起来用于保存
			onRightDblClick:function(data){
			var r=this.leftList.getSelectedRecord();
			if(!r){
			return;}
			r.set("YBFYBM",data.YBFYBM);
			r.set("YBFYMC",data.YBFYMC);
			this.updateRecord[r.get("FYXH")]=r.data;
			},
			//将药品对应的医保信息重置,缓存 并不会存到数据库,只有点保存后才会生效
			doCz:function(){
			var r=this.leftList.getSelectedRecord();
			if(!r||!r.get("YBFYBM")||r.get("YBFYBM")==null||r.get("YBFYBM")==""){
			return;}
			r.set("YBFYBM","");
			r.set("YBFYMC","");
			this.updateRecord[r.get("FYXH")]=r.data;
			},
			//保存
			doSave:function(){
			this.panel.el.mask("正在保存...", "x-mask-loading");
			var records=this.getUpdateRecord();
			if(records.length==0){
			MyMessageTip.msg("提示", "没有修改数据",true);
			this.panel.el.unmask();
			return;
			}
			var r = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.saveActionId,
								body : records
							});
			this.panel.el.unmask();
			if (r.code > 300) {
				this.processReturnMsg(r.code, r.msg, this.doSave);
				return;
			} 
			MyMessageTip.msg("提示", "保存成功",true);
			this.doSx();
			},
			//获取修改的数据 放到list中
			getUpdateRecord:function(){
			var records=new Array();
			for(var key in this.updateRecord){
			records.push(this.updateRecord[key])
			}
			return records;
			},
			//左边的费用模糊匹配右边的(可选,也可按实际需求将左边的截取几位匹配)
			doMhpp:function(){
			var r=this.leftList.getSelectedRecord();
			if(!r){
			return;
			}
			this.rightList.FYMC=r.get("FYMC");
			this.rightList.loadData();
			}
			
		});