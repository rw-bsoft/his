/**
 * 初始账册界面
 * 
 * @author caijy
 */
$package("phis.application.sto.script");

$import("phis.script.SimpleModule");

phis.application.sto.script.StorehouseInitialBooksModule = function(cfg) {
	cfg.width = 1024;
	cfg.height = 550;
	// cfg.listIsUpdate=true;
	cfg.exContext = {};
	cfg.showButtonOnTop = false;
	// cfg.modal = true;
	phis.application.sto.script.StorehouseInitialBooksModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.sto.script.StorehouseInitialBooksModule,
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
										height : 100,
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
							// buttons : [{
							// xtype : "button",
							// text : "确定",
							// handler : this.doSave,
							// scope : this
							// }, {
							// xtype : "button",
							// text : "退出",
							// handler : function() {
							// this.getWin().hide()
							// },
							// scope : this
							// }],
							tbar : (this.tbar || []).concat(this
									.createButtons())
						});
				this.panel = panel;
				return panel;
			},
			getForm : function() {
				this.form = this.createModule("form", this.refForm);
				this.form.on("getParameter", this.onGetParameter, this);
				this.form.on("kcslChange", this.onKcslChange, this);
				this.form.on("query", this.onQuery, this);
				this.form.on("jgChange", this.onJgChange, this);
				return this.form.initPanel();
			},
			getList : function() {
				this.list = this.createModule("list", this.refList);
				this.list.on("create", this.onCreate, this);
				this.list.on("getTopKcsl", this.onGetTopKcsl, this);
				this.list.on("jgjs", this.onJgjs, this);
				this.list.on("jsjg", this.doJsJg, this);
				return this.list.initPanel();
			},
			doCancel : function() {
				this.fireEvent("winClose", this);
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
			// 获取是否需要控制价格
			getSfkz : function() {
				if (this.sfkz == undefined) {
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.queryControlPricesServiceAction
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg, this.getSfkz);
						return null;
					}
					this.sfkz = ret.json.sfkz;
				}
				return this.sfkz;
			},
			// 打开页面加载数据
			load : function(initDataBody) {
				this.form.initDataBody = initDataBody
				this.form.loadData();
				this.list.doNew();
				this.list.requestData.cnd = [
						'and',
						[
								'and',
								['eq', ['$', 'a.YPXH'],
										['l', initDataBody.YPXH]],
								['eq', ['$', 'a.JGID'],
										['s', initDataBody.JGID]]],
						['eq', ['$', 'a.YPCD'], ['l', initDataBody.YPCD]]];
				this.list.loadData();
			},
			// list增行获取数据
			onCreate : function() {
				var data = this.form.getCreateData();
				this.list.createData = data;
				this.list.topKcsl = data.KCSL;
			},
			// list的价格计算
			onJgjs : function(value, it, record, originalValue) {
				var lsjg = record.get("LSJG");
				var jhjg = record.get("JHJG");
				if (it.id == "JHJG") {
					var lsjg = Math.round(value * this.getPljc() * 10000)
							/ 10000;
				}
				var sfkz = this.getSfkz();
				if (sfkz > 0) {
					var gyjj = this.form.data["GYJJ"];
					var gylj = this.form.data["GYLJ"];
					if (lsjg > gylj || jhjg > gyjj) {
						MyMessageTip.msg("提示", "价格超过公共价格!", true);
						record.set(it.id, originalValue);
						return;
					}
				}
				if (it.id == "JHJG") {
					record.set("LSJG", lsjg);
					record.set("LSJE", Math.round(lsjg
								* record.get("KCSL") * 100)
								/ 100);
				}
				this.list.doJs(record);
				this.doJsJg(this.list.getJes());
			},
			// list变动form金额计算
			doJsJg : function(body) {
				this.form.form.getForm().findField("LSJE").setValue(body.lsje);
				this.form.form.getForm().findField("JHJE").setValue(body.jhje);
			},
			// 获取批零加成率
			getPljc : function() {
				if (!this.pljc) {
					var ret = phis.script.rmi.miniJsonRequestSync({
								serviceId : this.serviceId,
								serviceAction : this.queryPljcActionId
							});
					if (ret.code > 300) {
						this.processReturnMsg(ret.code, ret.msg, this.getPljc);
						return;
					}
					this.pljc = 1 + ret.json.pljc
				}
				return this.pljc
			},
			// form设置是否控制价格和批零加成
			onGetParameter : function() {
				this.form.sfkz = this.getSfkz();
				this.form.pljc = this.getPljc();
			},
			// form库存数量修改后
			onKcslChange : function() {
				var topKcsl = this.form.form.getForm().findField("KCSL")
						.getValue();
				var kcsl = this.list.getKcls();
				if (topKcsl < kcsl) {
					MyMessageTip.msg("提示", "账册库存数小于实物库存数!", true);
					this.form.form.getForm().findField("KCSL").setValue(kcsl);
					return;
				} else if (topKcsl > kcsl) {
					var count=this.list.store.getCount();
					if(count>0){
						var data=this.list.store.getAt(count-1);
						var kcsl=data.get("KCSL")+(topKcsl-kcsl);
						data.set("KCSL",kcsl);
						data.set("LSJE",Math.round(kcsl*data.get("LSJG")*100)/100);
						data.set("JHJE",Math.round(kcsl*data.get("JHJG")*100)/100);
						this.list.fireEvent("jsjg", this.list.getJes());
					}else{
					this.list.doCreate();}
				}
			},
			doCommit : function() {
				var body={};
				body["cdxx"]=this.form.getFormData();
				if(body["cdxx"]==null){return;}
				var topKcsl=body["cdxx"].KCSL;
				var count=this.list.store.getCount();
				var kcmx=[];
				var kcsl=0
				for(var i=0;i<count;i++){
				if(this.list.store.getAt(i).data.KCSL!=0&&this.list.store.getAt(i).data.KCSL!=undefined&&this.list.store.getAt(i).data.KCSL!=""){
				if(this.list.store.getAt(i).data.LSJG==""||this.list.store.getAt(i).data.LSJG==undefined||this.list.store.getAt(i).data.JHJG==""||this.list.store.getAt(i).data.JHJG==undefined){
				this.list.store.getAt(i).data.LSJG=0
				//MyMessageTip.msg("提示", "第"+(i+1)+"行价格为空", true);
				//return;
				}
				if(this.list.store.getAt(i).data.JHJG==""||this.list.store.getAt(i).data.JHJG==undefined){
				this.list.store.getAt(i).data.JHJG=0
				//MyMessageTip.msg("提示", "第"+(i+1)+"行价格为空", true);
				//return;
				}
				if(this.list.store.getAt(i).data.LSJG<0||this.list.store.getAt(i).data.JHJG<0){
				MyMessageTip.msg("提示", "第"+(i+1)+"行价格小于0!", true);
				return;
				}
				//以下是用于判断是否有相同批号 效期和库存性质的记录. 以后有好办法再改代码
				var ypxq=this.list.store.getAt(i).data.YPXQ;
				var ypph=this.list.store.getAt(i).data.YPPH;
				var type=this.list.store.getAt(i).data.TYPE;
				if(ypxq==null||ypxq==undefined){
				ypxq="";
				}
				if(ypph==null||ypph==undefined){
				ypph="";
				}
				if(type==null||type==undefined){
				type=0;
				}
				for(var j=i+1;j<count;j++){
				var ypxq_j=this.list.store.getAt(j).data.YPXQ;
				var ypph_j=this.list.store.getAt(j).data.YPPH;
				var type_j=this.list.store.getAt(j).data.TYPE;
				if(ypxq_j==null||ypxq_j==undefined){
				ypxq_j="";
				}
				if(ypph_j==null||ypph_j==undefined){
				ypph_j="";
				}
				if(type_j==null||type_j==undefined){
				type_j=0;
				}
				if(ypxq==ypxq_j&&ypph==ypph_j&&type==type_j){
				MyMessageTip.msg("提示", "有相同库存性质,批号,效期的药!", true);
				return;
				}
				}
				kcmx.push(this.list.store.getAt(i).data);
				kcsl+=parseFloat(this.list.store.getAt(i).data.KCSL);
				}
				}
				if(topKcsl!=kcsl){
				MyMessageTip.msg("提示", "实物库存数与账册库存数不匹配!", true);
				return;
				}
				body["kcmx"]=kcmx;
				var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.saveActionId,
							body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg, this.doCommit);
					return;
				}
				this.fireEvent("save",this);
				this.doNew();
			},
			doNew : function() {
				this.form.doNew();
				this.list.doNew();
			},
			onQuery:function(initDataBody){
			this.load(initDataBody);
			},
			onGetTopKcsl:function(){
			return this.form.form.getForm().findField("KCSL")
						.getValue();
			},
			//form价格变动的时候 判断List记录条数是否是一条,一条则修改明细金额,否则不动
			onJgChange:function(jhjg,lsjg){
			if(this.list.store.getCount()==1){
				var record=this.list.store.getAt(0);
				record.set("LSJG",lsjg);
				record.set("JHJG",jhjg);
				this.list.doJs(record);
				this.doJsJg(this.list.getJes());
			}
			}
		});