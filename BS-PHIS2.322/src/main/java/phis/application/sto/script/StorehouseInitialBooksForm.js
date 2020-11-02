$package("phis.application.sto.script")

$import("phis.script.TableForm")

phis.application.sto.script.StorehouseInitialBooksForm = function(cfg) {
	cfg.autoLoadData = false;
	cfg.showButtonOnTop = false;
	cfg.remoteUrl="Medicines";
	cfg.remoteTpl='<td width="12px" style="background-color:#deecfd">{numKey}.</td><td width="100px">{YPMC}</td><td width="70px">{YFGG}</td><td width="20px">{YFDW}</td><td width="80px">{CDMC}</td><td width="70px">{LSJG}</td><td width="70px">{JHJG}</td>';
	cfg.queryParams={"tag":"cszc"};
	phis.application.sto.script.StorehouseInitialBooksForm.superclass.constructor.apply(
			this, [cfg])
}
Ext.extend(phis.application.sto.script.StorehouseInitialBooksForm,
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
									name : 'PFJG'
								}, {
									name : 'PFJE'
								}, {
									name : 'LSJE'
								}, {
									name : 'JHJE'
								}, {
									name : 'JGID'
								}
								, {
									name : 'GYLJ'
								}, {
									name : 'GYJJ'
								}
								]);
			},
			// 数据回填
			setBackInfo : function(obj, record) {
				// 将选中的记录设置到行数据中
				var initDataBody = {};
				initDataBody["YPXH"] = record.get("YPXH");
				initDataBody["JGID"] = record.get("JGID");
				initDataBody["YPCD"] = record.get("YPCD");
				var form=this.form.getForm();
				form.findField("YPMC").setValue(record.get("YPMC"));
				this.data["YPXH"]=record.get("YPXH");
				this.fireEvent("query",initDataBody)
				this.remoteDic.lastQuery = "";
			},
			loadData : function() {
				if (this.loading) {
					return
				}
				if (!this.schema) {
					return
				}
				if (!this.initDataId && !this.initDataBody) {
					return;
				}
				if (!this.fireEvent("beforeLoadData", this.entryName,
						this.initDataId, this.initDataBody)) {
					return
				}
				if (this.form && this.form.el) {
					this.form.el.mask("正在载入数据...", "x-mask-loading")
				}
				var body = {};
				body["ypxh"] = this.initDataBody["YPXH"];
				body["jgid"] = this.initDataBody["JGID"];
				body["ypcd"] = this.initDataBody["YPCD"];
				this.loading = true
				phis.script.rmi.jsonRequest({
							serviceId : this.serviceId,
							serviceAction : this.queryServiceAction,
							body : body
						}, function(code, msg, json) {
							if (this.form && this.form.el) {
								this.form.el.unmask()
							}
							this.loading = false
							if (code > 300) {
								this.processReturnMsg(code, msg, this.loadData)
								return
							}
							if (json.body) {
								this.doNew()
								this.initFormData(json.body)
								this.fireEvent("loadData", this.entryName,
										json.body);
							}
							if (this.op == 'create') {
								this.op = "update"
							}

						}, this)// jsonRequest
			},
			//获取list增行记录
			getCreateData:function(){
			var body={};
			var form=this.form.getForm();
			body["YPXH"]=this.data.YPXH;
			body["YPCD"]=this.data.YPCD;
			body["JGID"]=this.data.JGID;
			body["BZLJ"]=this.data.GYLJ;
			body["JHJG"]=form.findField("JHJG").getValue();
			body["LSJG"]=form.findField("LSJG").getValue();
			body["KCSL"]=form.findField("KCSL").getValue();
			return body;
			},
			onReady : function() {
				phis.application.sto.script.StorehouseInitialBooksForm.superclass.onReady
						.call(this);
				var form = this.form.getForm();
				form.findField("JHJG").on("change", this.onJHJGChange, this);
				form.findField("LSJG").on("change", this.onLSJGChange, this);
				form.findField("KCSL").on("change", this.onKCSLChange, this);
			}
			,
			onJHJGChange:function(){
			this.fireEvent("getParameter",this);//获取是否控制价格,批零加成
			var form=this.form.getForm();
			var jhjg=form.findField("JHJG").getValue();
			var gyjj=this.data.GYJJ;
			var gylj=this.data.GYLJ;
			var lsjg=Math.round(jhjg*this.pljc*10000)/10000;
			if(this.sfkz>0){
			if(jhjg>gyjj||lsjg>gylj){
			MyMessageTip.msg("提示", "价格超过公共价格!", true);
			jhjg>gyjj?form.findField("JHJG").setValue(gyjj):form.findField("JHJG").setValue(jhjg);
			lsjg>gylj?form.findField("LSJG").setValue(gylj):form.findField("LSJG").setValue(lsjg);
			this.fireEvent("jgChange",form.findField("JHJG").getValue(),form.findField("LSJG").getValue());
			return;
			}
			}
			form.findField("LSJG").setValue(lsjg);
			this.fireEvent("jgChange",form.findField("JHJG").getValue(),form.findField("LSJG").getValue());
			},
			onKCSLChange:function(){
			this.fireEvent("kcslChange",this);
			},
			onLSJGChange:function(){
			this.fireEvent("getParameter",this);//获取是否控制价格,批零加成
			var form=this.form.getForm();
			var gylj=this.data.GYLJ;
			var lsjg=form.findField("LSJG").getValue();
			if(this.sfkz>0){
			if(lsjg>gylj){
			lsjg=gylj;
			MyMessageTip.msg("提示", "价格超过公共价格!", true);
			form.findField("LSJG").setValue(gylj);
			}
			}
			this.fireEvent("jgChange",form.findField("JHJG").getValue(),form.findField("LSJG").getValue());
			}
		})