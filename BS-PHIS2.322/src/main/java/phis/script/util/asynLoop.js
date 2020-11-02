/**
 * 异步处理
 * 
 * @param {}
 *            funcs
 * @param {}
 *            arrays
 * @param {}
 *            scope
 * @param {}
 *            callback
 * @return {}
 */
$package("phis.script.util")

asynLoop = function(funcs, arrays, scope, callback) {
	funcs = funcs || [];
	var index = 0, counter = 0, loopTimes=0;
	scope = scope || this;
	arrays = arrays;
	
	var args = [];
	var _this = this;
	var reset = function() {
		index = 0;
		counter = 0;
	}
	var r = {
		/**
		 * 开始异步循环
		 */
		start : function() {
			if (!funcs || funcs.length == 0)
				return;
			index = 0;
			if (_this.r.iterator) {
				args = _this.r.iterator(arrays, counter);
				counter++;
			} else {
				args = arrays;
			}
			if (args) {
				_this.r.next();
			} else {
				if (callback) {
					Ext.callback(callback, scope);
				}
				reset();
				Ext.callback(_this.r.over, scope);
			}
		},
		next : function() {
			var func = funcs[index];
			index++;
			if (func) {
				Ext.callback(func, scope || this, args)
			} else {
				if (_this.r.iterator) {
					_this.r.start();
				}else {
					Ext.callback(_this.r.over, scope);
				}
				return;
			}
		},
		/**
		 * 单次循环成功
		 */
		success : function(msg) {
			if (msg) {
				MyMessageTip.msg("提示", msg, true);
			}
			_this.r.start();
		},
		/**
		 * 单次循环失败
		 */
		fail : function(msg) {
			if (msg) {
				MyMessageTip.msg("提示", msg, true);
			}
			_this.r.start();
		},
		/**
		 * 异步循环结束
		 */
		over : function() {

		},
		/**
		 * 终止循环
		 */
		stop : function() {
			reset();
		}
	}
	_this.r = r;
	return r;

}