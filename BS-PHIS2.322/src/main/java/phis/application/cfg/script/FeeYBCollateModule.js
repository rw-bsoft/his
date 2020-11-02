/**
 * 药品与医保对照模块
 * 
 * @author gejj
 */
$package("phis.application.cfg.script");
$import("phis.script.SimpleModule", "util.dictionary.DictionaryLoader",
		"phis.script.widgets.MyMessageTip","phis.application.cfg.script.yb.YbUtil");
phis.application.cfg.script.FeeYBCollateModule = function(cfg) {
	this.exContext = this.exContext || {};
	this.cfg = cfg;
	Ext.apply(this,phis.application.cfg.script.YbUtil);
	phis.application.cfg.script.FeeYBCollateModule.superclass.constructor
			.apply(this, [cfg]);
}
Ext.extend(phis.application.cfg.script.FeeYBCollateModule,
		phis.script.SimpleModule, {
			
		initPanel : function() {
			if(this.panel){
				return this.panel;
			}
			var bbar = ['拼音码:'];
			this.textField = new Ext.form.TextField({
				name : 'pym'
			});
			this.textField.on("specialkey", function(text, e) {
			var key = e.getKey();
			if (key == e.ENTER) {
				this.ka03Query();
			}
			}, this);
//			this.textField.on("change", function(){
//				this.ka02Query();
//			},this);
			bbar.push(this.textField);
			//获取Applications.xml中配置的按钮(如打印、保存、取消等)
			var actions = this.actions;
			for (var i = 0; i < actions.length; i++) {
				var action = actions[i];
				var btn = {};
				btn.id = action.id;
				btn.accessKey = "F1", btn.cmd = action.id;
				btn.text = action.name, btn.iconCls = action.iconCls
						|| action.id;
				//添加doAction点击事件,调用doAction方法
				btn.handler = this.doAction;
				btn.name = action.id;
				btn.scope = this;
				bbar.push(btn);
			}
			var panel2 = new Ext.Panel({
						border : true,
						frame : true,
						layout : 'border',
						items : [{
									layout : "fit",
									border : true,
									split : false,
									title : '',
									region : 'center',
									items : this.getCYBCForm()
								},{
									layout : "fit",
									border : true,
									split : false,
									title : '',
									width : 250,
									region : 'east',
									items : this.getCYBCTimeList()
								}]
					});
			var panel = new Ext.Panel({
						border : false,
						frame : true,
						layout : 'border',
						items : [{
									layout : "fit",
									border : true,
									split : false,
									title : '',
									region : 'center',
									items : panel2,
									bbar : bbar
								},{
									layout : "fit",
									border : true,
									split : false,
									title : '',
									region : 'south',
									height : 200,
									items : this.getCYBCList()
								}]
					});
			this.panel = panel;
			return this.panel;
		},
		getCYBCList : function(){
			var module = this.createModule("refCYBCList", this.refCYBCList);
			//根据费用名称模糊查询
			var query = this.par.data.FYMC;
			if(this.par.data.FYMC.length > 5){
				query = query.substring(0,4);//截取4位做模糊查询
			}
			var cnd = ['and',
						['like', ['$', 'AKA091'], ['s', query + '%']]
						];
			module.requestData.cnd = cnd;
			module.on("onDBAK03List",this.onDBAK03List,this);
			if (module) {
				module.opener = this;
				return module.initPanel();
			}
		},
		getCYBCTimeList : function(){
			var module = this.createModule("refCYBCTimeList", this.refCYBCTimeList,
					this.cfg);
			//只使用费用序号
			var cnd = ['and',
						['eq', ['$', 'FYXH'], ['s', this.par.data.FYXH]],
						['eq', ['$', 'YBPB'], ['d', 1]]
						];
			module.requestData.cnd = cnd;
			if (module) {
				module.opener = this;
				return module.initPanel();
			}
		},
		/**
		 * 获取药品医保对照Form
		 * @return {}
		 */
		getCYBCForm : function(){
			var module = this.createModule("CYBCForm", this.refCYBCForm);
			if (module) {
				module.setParameter(this.par);
				module.opener = this;
				var panel = module.initPanel();
				module.setInitParameter();
				return panel;
			}
		},
		/**
		 * KA02列表双击触发方法，更新Form中的信息
		 * @param {} rs
		 */
		onDBAK03List : function(rs){
			var timeList = this.midiModules["refCYBCTimeList"];
			if(timeList.grid.getStore().getCount() <= 0){//时间列表中没有任何数据
				timeList.doCreate();
			}
			var module =  this.midiModules["CYBCForm"];
			if (module) {
				module.setParameter(this.par, rs);
				module.setFormInfo();
			}
		},
		/**
		 * 时间列表选择后，或修改后
		 * @param {} rs
		 */
		setFormTime : function(rs){
			if(rs.data._opStatus == 'create'){
				this.updateFlag = false;
			}else{
				this.updateFlag = true;
				this.commPar = rs.data;//公共参数，在更新时需要主键参数
			}
			var module =  this.midiModules["CYBCForm"];
			if (module) {
				module.setFormTime(rs);
			}
		},
		/**
		 * 设置传入的参数信息
		 */
		setParameter : function(par){
			this.par = par;
			this.commPar = par.data;//公共参数，在更新时需要主键参数
		},
		/**
		 * 更新Form中信息
		 * 		目前在本地药品信息中双击调用
		 */
		updateFormInfo : function(){
			var moduleForm =  this.midiModules["CYBCForm"];
			if (moduleForm) {
				moduleForm.setParameter(this.par);
				moduleForm.setInitParameter();
			}
			var module = this.midiModules["refCYBCTimeList"];
			//只使用费用序号
			var cnd = ['and',
						['eq', ['$', 'FYXH'], ['s', this.par.data.FYXH]],
						['eq', ['$', 'YBPB'], ['d', 1]]
						];
			module.requestData.cnd = cnd;
			module.refresh();
			var module2 = this.midiModules["refCYBCList"];
			delete module2.requestData.cnd;
			//根据费用名称模糊查询
			var query = this.par.data.FYMC;
			if(this.par.data.FYMC.length > 5){
				query = query.substring(0,4);//截取4位做模糊查询
			}
			var cnd2 = ['and',
						['like', ['$', 'AKA091'], ['s', query + '%']]
						];
			module2.requestData.cnd = cnd2;
//			module2.requestData.cnd = ['like', ['$', 'AKA066'], ['s', '%%']];//拼音码
			module2.refresh();
		},
		doAction : function(item, e){
			if(item.id == "close"){//刷新按钮
				this.doClose();
			}else if(item.id == "save"){
				this.doSave();
			}else if(item.id == "create"){
				this.doCreatePYM();
			}
		},
		doCreatePYM : function(){
			var module = this.midiModules["refCYBCList"];
//			var select = module.getSelectedRecord();
//			if(!select){
//				MyMessageTip.msg("提示", "未选择生成拼音码条目!", true);
//				return;
//			}
			var res = this.requestServer('createKA03PYM',null);
			if(res.code == 200){
				MyMessageTip.msg("提示", "生成成功!", true);
				module.refresh();//刷新
			}else{
				MyMessageTip.msg("提示", "生成失败!", true);
			}
		},
		doClose : function(){
			this.win.hide();
		},
		/**
		 * 保存按钮
		 */
		doSave : function(){
			var module =  this.midiModules["CYBCForm"];
			var data = module.getFormData();
			if(this.updateFlag ){//true 表示更新  false 表示新增
				data['U_FLAG'] = 'true';
			}else{
				data['U_FLAG'] = 'false';
			}
			data['FYXH'] = this.commPar.FYXH;
			data['YBPB'] = 1;//医保判别 市医保 为1  省医保 为2
			if (confirm('确定保存数据?')) {
				var res = this.requestServer('saveFYDZ',data)
				if(res.code == 200){
					MyMessageTip.msg("提示", "保存成功!", true);
					this.opener.refreshDates();//刷新费用对照列表
					this.doClose();
				}else{
					MyMessageTip.msg("提示", "保存失败!", true);
				}
			}
			
		},
		ka03Query : function(){
			var module = this.midiModules["refCYBCList"];
			if(this.textField.getValue().toUpperCase()){
				module.requestData.cnd = 
						['like', ['$', 'AKA066'], ['s', '%'+this.textField.getValue().toUpperCase()+'%']];
			}else{
				delete module.requestData.cnd;
			}
			module.refresh();
		}
})