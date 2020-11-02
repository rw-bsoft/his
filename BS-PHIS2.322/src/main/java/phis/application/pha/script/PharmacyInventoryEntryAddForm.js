$package("phis.application.pha.script")

$import("phis.script.TableForm")

phis.application.pha.script.PharmacyInventoryEntryAddForm = function(cfg) {
	cfg.remoteUrl="Medicines";
	cfg.remoteTpl='<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="70px">{LSJG}</td><td width="70px">{JHJG}</td>';
	cfg.queryParams={"tag":"pd"};
	phis.application.pha.script.PharmacyInventoryEntryAddForm.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.pha.script.PharmacyInventoryEntryAddForm,
		phis.script.TableForm, {
			getRemoteDicReader : function() {
				return new Ext.data.JsonReader({
							root : 'mds',
							// 类里面总数的参数名
							totalProperty : 'count',
							id : 'mdssearch'
						}, [{
									name : 'numKey'
								}, {
									name : 'YPCD'
								}, {
									name : 'YPXH'
								}, {
									name : 'CDMC'
								}, {
									name : 'LSJG'
								}, {
									name : 'JHJG'
								}, {
									name : 'YPMC'
								}, {
									name : 'YFGG'
								}, {
									name : 'YFDW'
								}, {
									name : 'YFBZ'
								}, {
									name : 'PFJG'
								}, {
									name : 'ZXBZ'
								}, {
									name : 'YPGG'
								}, {
									name : 'YPDW'
								}
								]);
			},
			// 数据回填
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				obj.collapse();
				var form=this.form.getForm();
				form.findField("YPMC").setValue(record.get("YPMC"));
				form.findField("YPGG").setValue(record.get("YPGG"));
				form.findField("YFDW").setValue(record.get("YFDW"));
				form.findField("CDMC").setValue(record.get("CDMC"));
				form.findField("JHJG").setValue(record.get("JHJG"));
				form.findField("LSJG").setValue(record.get("LSJG"));
				Ext.apply(this.data, record.data);
				this.data["YKBZ"]=record.get("ZXBZ");
				this.data["YKDW"]=record.get("YPDW");
				this.remoteDic.lastQuery = "";
			},
			//关闭
			doClose:function(){
			this.fireEvent("close",this)
			},
			//确定
			doQd:function(){
			var body=this.getFormData();
			body["PDDH"]=this.pddh;
			var ret = phis.script.rmi.miniJsonRequestSync({
							serviceId : this.serviceId,
							serviceAction : this.savaActionid,
							body:body
						});
				if (ret.code > 300) {
					this.processReturnMsg(ret.code, ret.msg);
					return ;
				}
			this.fireEvent("save",this);
			this.doNew();
			}
			
		})