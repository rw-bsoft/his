$package("phis.application.war.script");
$import("phis.script.SimpleList");

phis.application.war.script.DoctorReviewList = function(cfg) {
	if(this.serverParams){
		this.serverParams.serviceAction=cfg.serviceAction;
	}else{
		this.serverParams={serviceAction:cfg.serviceAction};
	}
	phis.application.war.script.DoctorReviewList.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.war.script.DoctorReviewList,
		phis.script.SimpleList,{ 
	
		/**
		 * 单病人按钮操作
		 */
		 doDbr : function(){
		 	var select = this.getSelectedRecord();
		 	if(!select){
		 		MyMessageTip.msg("提示", "请先选择数据后再操作!", true);
				return;
		 	}
		 	var r = phis.script.rmi.miniJsonRequestSync({
				serviceId : this.listServiceId,
				serviceAction : "saveDBR",
				body : select.data
			});
			if (r.code > 300) {
				this.processReturnMsg(r.code, r.msg);
				return;
			} else {
				if(r.json.body){
					MyMessageTip.msg("提示", r.json.body, true);
				}
				this.refresh();
			}
		 },
		 /**
		  * 全部按钮操作
		  */
		 doQb : function(){
		 	var r = phis.script.rmi.miniJsonRequestSync({
				serviceId : this.listServiceId,
				serviceAction : "saveQB"
			});
			if (r.code > 300) {
				this.processReturnMsg(r.code, r.msg);
				return;
			} else {
				if(r.json.body){
					MyMessageTip.msg("提示", r.json.body, true);
				}
				this.refresh();
			}
		 },
		 doSx : function(){
		 	this.refresh();
		 },
		 doCndQuery : function(button, e, addNavCnd) { // ** modified by
				// yzh ,
				// 2010-06-09 **
				var initCnd = this.initCnd
				var itid = this.cndFldCombox.getValue()
				var items = this.schema.items
				var it
				for (var i = 0; i < items.length; i++) {
					if (items[i].id == itid) {
						it = items[i]
						break
					}
				}
				if (!it) {
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						return;
					}
				}
				this.resetFirstPage()
				var v = this.cndField.getValue()
				var rawV = this.cndField.getRawValue();
				if (v == null || v == "" || rawV == null || rawV == "") {
					var cnd = []
					this.queryCnd = null;
					if (addNavCnd) {
						if (initCnd) {
							this.requestData.cnd = ['and', initCnd, this.navCnd];
						} else {
							this.requestData.cnd = this.navCnd;
						}
						this.refresh()
						return
					} else {
						if (initCnd)
							cnd = initCnd
					}
					this.requestData.cnd = cnd.length == 0 ? null : cnd;
					this.refresh()
					return
				}
				// 替换'，解决拼sql语句查询的时候报错
				v = v.replace(/'/g, "''")
				var refAlias = it.refAlias || "a"
				var cnd = ['eq', ['$', refAlias + "." + it.id]]
				if (it.dic) {
					if (it.dic.render == "Tree") {
						// var node = this.cndField.selectedNode
						// @@ modified by chinnsii 2010-02-28, add "!node"
						cnd[0] = 'eq'
						// if (!node || !node.isLeaf()) {
						// cnd[0] = 'like'
						// cnd.push(['s', v + '%'])
						// } else {
						cnd.push(['s', v])
						// }
					} else {
						cnd.push(['s', v])
					}
				} else {
					switch (it.type) {
						case 'int' :
							cnd.push(['i', v])
							break;
						case 'double' :
						case 'bigDecimal' :
							cnd.push(['d', v])
							break;
						case 'string' :
							// add by liyl 07.25 解决拼音码查询大小写问题
							if (it.id == "PYDM" || it.id == "WBDM") {
								v = v.toUpperCase();
							}
							cnd[0] = 'like'
							cnd.push(['s', '%' + v])
							break;
						case "date" :
							v = v.format("Y-m-d")
							cnd[1] = [
									'$',
									"str(" + refAlias + "." + it.id
											+ ",'yyyy-MM-dd')"]
							cnd.push(['s', v])
							break;
					}
				}
				this.queryCnd = cnd
				if (initCnd) {
					cnd = ['and', initCnd, cnd]
				}
				if (addNavCnd) {
					this.requestData.cnd = ['and', cnd, this.navCnd];
					this.refresh()
					return
				}
				this.requestData.cnd = cnd
				this.refresh()
			}
});