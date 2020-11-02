$package("chis.script")
$import("chis.script.CardReader")
chis.script.ICCardField = Ext.extend(Ext.form.TextField, {
	initComponent : function(){
        chis.script.ICCardField.superclass.initComponent.call(this);
		this.cardAgent =  chis.script.CardReader
		this.cardAgent.init()
	},
	initEvents : function(){
		chis.script.ICCardField.superclass.initEvents.call(this)
		this.el.on("keyup",this.onFldKeyUp,this)
	},
	onFldKeyUp : function(e){
        if(e.getKey() == Ext.EventObject.SPACE){
        	var ret = this.cardAgent.readCardInfo()
        	if(ret.code == 0){
        		this.setValue(ret.cardId)
        		this.cardInfo = ret
        	}
        	else{
        		this.setRawValue(ret.msg)
        		var ctx = this
        		setTimeout(function(){
        			ctx.setRawValue("")
        		},1000)
        	}
        }
        this.fireEvent('keyup', this, e);
    }
});
Ext.reg('iccardfield', chis.script.ICCardField);