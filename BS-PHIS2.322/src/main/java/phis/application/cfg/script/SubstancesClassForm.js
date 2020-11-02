$package("phis.application.cfg.script")

$import( "phis.script.TableForm")

phis.application.cfg.script.SubstancesClassForm = function(cfg) {
	cfg.colCount = 2;
	//cfg.labelWidth = 60;
	cfg.width = 400;
	phis.application.cfg.script.SubstancesClassForm.superclass.constructor.apply(this, [cfg])
}
Ext.extend(phis.application.cfg.script.SubstancesClassForm, phis.script.TableForm, {
			onReady : function() {
				phis.application.cfg.script.SubstancesClassForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				var f = form.findField("ZDXH");
				if(f){
					f.store.on("load", this.fillRkfs, this);
				}
			},
			doNew : function() {
				phis.application.cfg.script.SubstancesClassForm.superclass.doNew
						.call(this);
			    if(this.num == 1){
						var btns =  this.form.getTopToolbar().items;
						if (!btns) {
							return;
						}
						var n = btns.getCount();
						for (var i = 0; i < n; i++) {
							var btn = btns.item(i);
							if(i == 0){
							  btn.disable();
							}else{
							  btn.enable();
							}
						}
					}else{
						var btns =  this.form.getTopToolbar().items;
						if (!btns) {
							return;
						}
						var n = btns.getCount();
						for (var i = 0; i < n; i++) {
							var btn = btns.item(i);
							  btn.enable();
						}
					}
				this.num = 0;
				this.fillRkfs();
			},
			// 新增页面打开药品类别值带过来
			fillRkfs : function() {
				if(this.oper){
					this.op = this.oper;
				}
				if(!this.ZDXH)
				    return;
				if (this.ZDXH && this.oper == "create") {
					var form = this.form.getForm();
					if(form){
						form.findField("SJFL").setValue(this.FLBM);
					}
				}
				if(this.ZDXH && this.oper == "update"){
					var form = this.form.getForm();
					if(form){
						var FLBM = (this.FLBM+"").substring((this.SJFL+"").length,this.FLBM.length);
						form.findField("FLBM").setValue(FLBM);
						form.findField("SJFL").setValue(this.SJFL);
						form.findField("FLMC").setValue(this.FLMC);
					}
				}
			},
        saveToServer : function(saveData) {
        	   if(this.oper){
        	   	  this.op = this.oper;
        	   }
				if (!this.fireEvent("beforeSave", this.entryName, this.op, saveData)) {
					return;
				}
				if(isNaN(saveData.FLBM)){
				    MyMessageTip.msg("提示", "分类编码只能输入数字！", true);
				    return false;
				}
				var gzcd = this.GZCD;
				if(gzcd != (saveData.FLBM+"").length){
					MyMessageTip.msg("提示", "规则长度是:"+gzcd, true);
					return;
				}
				var FLBM = saveData.SJFL+""+ saveData.FLBM+"";
				var LBXH = this.mainApp['phis'].treasuryLbxh;
				
				var data = {"LBXH":LBXH,"FLMC":saveData.FLMC,"FLBM":FLBM};
					if(this.ZDXH){
				      data = {"ZDXH": this.ZDXH,"LBXH":LBXH,"FLMC":saveData.FLMC,"FLBM":FLBM,"SJFL":saveData.SJFL};
					}
				this.saving = true
				this.form.el.mask("正在保存数据...", "x-mask-loading")
				phis.script.rmi.jsonRequest({
							serviceId : "configSubstancesClassService",
							serviceAction :"saveSubstancesClassTree",
							op : this.op,
							schema : this.entryName,
							body : data
						}, function(code, msg, json) {
							this.form.el.unmask()
							this.saving = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.saveToServer,[saveData]);
								return
							}
							var loadNode = this.tree.getSelectionModel().getSelectedNode().parentNode;
							/*if(this.op == "update"){
								loadNode = loadNode.parentNode;
							}*/
							this.tree.getLoader().load(loadNode);
							loadNode.expand();
							
							Ext.apply(this.data, saveData);
							if (json.body) {
								this.initFormData(json.body)
								this.fireEvent("save", this.entryName, this.op, json,
										this.data)
							}
							this.op = "update"
						}, this)
			}
});