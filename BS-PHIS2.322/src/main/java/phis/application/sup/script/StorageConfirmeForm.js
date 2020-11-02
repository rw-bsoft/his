$package("phis.application.sup.script")

$import("phis.script.TableForm")

phis.application.sup.script.StorageConfirmeForm = function(cfg) {
	phis.application.sup.script.StorageConfirmeForm.superclass.constructor.apply(
			this, [cfg])
	//this.on("loadData", this.onLoadData, this);
}
Ext.extend(phis.application.sup.script.StorageConfirmeForm, phis.script.TableForm,{  
               initFormData : function(data) {
					Ext.apply(this.data, data)
					this.initDataId = this.data[this.schema.pkey]
					var form = this.form.getForm()
					var items = this.schema.items
					var n = items.length
					for (var i = 0; i < n; i++) {
						var it = items[i]
						var f = form.findField(it.id)
						if (f) {
							var v = data[it.id]
							if (v != undefined) {
								if(it.id == 'LZFS'){
									f.setValue("");
								}else{
								    f.setValue(v);
								}
							}
							if (it.update == "false") {
								f.disable();
							}
						}
					}
					var QRBZ = form.findField("QRBZ").getValue();
				    var QRRK = form.findField("QRRK").getValue();
				    if(QRBZ == 1){
				    	form.findField("LZFS").setValue(QRRK);
				    }
				    form.findField("QRRK").hide();
					this.setKeyReadOnly(true)
					this.focusFieldAfter(-1, 800)
			},
			onLoadData : function() {
				    /*var QRBZ =  this.form.getForm().findField("QRBZ").getValue();
				    var QRRK =  this.form.getForm().findField("QRRK").getValue();
				    if(QRBZ == 1){
				    	 this.form.getForm().findField("LZFS").setValue(QRRK);
				         //this.form.getForm().findField("LZFS").disable();
				    }*/
			},
			doIs : function(op) {
				var field = this.form.getForm().findField("LZFS");
			    var filters = "['and',['and',['and',['eq',['$','item.properties.KFXH'],['i',"+ this.mainApp['phis'].treasuryId+ "]]," +
						                           "['eq',['$','item.properties.DJLX'],['s','RK']]]," +
						                           "['eq',['$','item.properties.YWLB'],['i',1]]]," +
						                           "['eq',['$','item.properties.TSBZ'],['i',0]]]";
				if (op > 0) {
					filters = "['and',['and',['and',['eq',['$','item.properties.KFXH'],['i',"+ this.mainApp['phis'].treasuryId+ "]]," +
							                       "['eq',['$','item.properties.DJLX'],['s','RK']]]," +
							                       "['eq',['$','item.properties.YWLB'],['i',-1]]]," +
							                       "['eq',['$','item.properties.TSBZ'],['i',0]]]";
						}
				field.store.removeAll();	
				field.store.proxy = new Ext.data.HttpProxy({
							method : "GET",
							url : util.dictionary.SimpleDicFactory.getUrl({
										id : "phis.dictionary.transfermodes",
										filter : filters
									})
						})
				if (!this.getFormData()) {
					var defaultIndex = 0;
					field.store.on("load", function() {
						if (defaultIndex != null) {
							field.setValue(this.getAt(defaultIndex).get('key'));
							defaultIndex = null;
						}
					})
				}
                field.store.load()
				
			}

		})