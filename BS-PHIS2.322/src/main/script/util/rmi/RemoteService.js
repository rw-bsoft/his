$package("util.rmi");
$import(
	"util.rmi.miniJsonRequestAsync",
	"util.rmi.miniJsonRequestSync"
);
util.rmi.RemoteService = {
	getInvoker:function(beanName,method,callback,scope,relogonFunc,relogonArgs,actionId){
		if(typeof callback == "function"){
			return (function(){
				var parameters = [];
				for(var i = 0; i < arguments.length; i ++){
					parameters[i] = arguments[i]
				}
				util.rmi.miniJsonRequestAsync({
						actionId:actionId,
						serviceId:beanName,
						method:method,
						body:parameters
				},callback,scope,relogonFunc,relogonArgs);
			})
		}
		else{
			return(function(){
				var parameters = [];
				for(var i = 0; i < arguments.length; i ++){
					parameters[i] = arguments[i]
				}
				return rmi.miniJsonRequestSync({
						actionId:actionId,
						serviceId:beanName,
						method:method,
						body:parameters
				},relogonFunc,relogonArgs,scope);
			})
		}
	}
		
};