/*!
 * Ext JS Library 3.4.0
 * Copyright(c) 2006-2011 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
$package("phis.script.widgets")

phis.script.widgets.ModuleQueryField = Ext.extend(Ext.form.TriggerField, {
    initComponent : function(){
        phis.script.widgets.ModuleQueryField.superclass.initComponent.call(this);
    },

    validationEvent:false,
    validateOnBlur:false,
    triggerClass:'x-form-search-trigger',
    hideTrigger1:true,
    width:180,

    onTriggerClick : function(e){
		this.fireEvent("queryClick",e);
    },
    
    setValue : function(v) {
    	if(!v){
    		v="";
    	}
		var needFireSelect = false;
		var text = v;
		if (typeof v == "object") {
			text = v[this.displayField]
			v = v[this.valueField]
		}
		if(text){
    		 text= text.replace('&lt','<');
    	}
		//还原被转换的字符
		this.lastSelectionText = text;
		if (this.hiddenField) {
			this.hiddenField.value = v;
		}
        phis.script.widgets.ModuleQueryField.superclass.setValue.call(this, text);
		this.value = v;
		if (needFireSelect) {
			this.fireEvent("select", this, r); // 也是选中了，级联产生的问题
		}
	},
	getValue : function(){
        if(this.valueField){
            return Ext.isDefined(this.value) ? this.value : '';
        }else{
            return phis.script.widgets.ModuleQueryField.superclass.getValue.call(this);
        }
    }
});
Ext.reg('moduleQuery', phis.script.widgets.ModuleQueryField);