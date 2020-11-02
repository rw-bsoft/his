/*
 * ! Ext JS Library 3.3.1 Copyright(c) 2006-2010 Sencha Inc.
 * licensing@sencha.com http://www.sencha.com/license
 */
/**
 * @class Ext.DataView.LabelEditor
 * @extends Ext.Editor
 * 
 */
Ext.DataView.LabelEditor = Ext.extend(Ext.Editor, {
			alignment : "tl-tl",
			hideEl : false,
			cls : "x-small-editor",
			shim : false,
			completeOnEnter : true,
			cancelOnEsc : true,
			labelSelector : 'span.x-editable',

			constructor : function(cfg, field) {
				Ext.DataView.LabelEditor.superclass.constructor.call(this,
						field || new Ext.form.TextField({
									allowBlank : false,
									growMin : 90,
									growMax : 240,
									grow : true,
									selectOnFocus : true
								}), cfg);
			},

			init : function(view) {
				this.view = view;
				view.on('render', this.initEditor, this);
				this.on('complete', this.onSave, this);
			},

			initEditor : function() {
				this.view.on({
							scope : this,
							containerclick : this.doBlur,
							click : this.doBlur
						});
				this.view.getEl().on('mousedown', this.onMouseDown, this, {
							delegate : this.labelSelector
						});
			},

			doBlur : function() {
				if (this.editing) {
					this.field.blur();
				}
			},

			onMouseDown : function(e, target) {
				if (!e.ctrlKey && !e.shiftKey) {
					var item = this.view.findItemFromChild(target);
					e.stopEvent();
					var record = this.view.store.getAt(this.view.indexOf(item));
					this.startEdit(target, record.data[this.dataIndex]);
					this.activeRecord = record;
				} else {
					e.preventDefault();
				}
			},

			onSave : function(ed, value) {
				this.activeRecord.set(this.dataIndex, value);
			}
		});

Ext.DataView.DragSelector = function(cfg) {
	cfg = cfg || {};
	var view, proxy, tracker;
	var rs, bodyRegion, dragRegion = new Ext.lib.Region(0, 0, 0, 0);
	var dragSafe = cfg.dragSafe === true;

	this.init = function(dataView) {
		view = dataView;
		view.on('render', onRender);
	};

	function fillRegions() {
		rs = [];
		view.all.each(function(el) {
					rs[rs.length] = el.getRegion();
				});
		bodyRegion = view.el.getRegion();
	}

	function cancelClick() {
		return false;
	}

	function onBeforeStart(e) {
		return !dragSafe || e.target == view.el.dom;
	}

	function onStart(e) {
		view.on('containerclick', cancelClick, view, {
					single : true
				});
		if (!proxy) {
			proxy = view.el.createChild({
						cls : 'x-view-selector'
					});
		} else {
			if (proxy.dom.parentNode !== view.el.dom) {
				view.el.dom.appendChild(proxy.dom);
			}
			proxy.setDisplayed('block');
		}
		fillRegions();
		view.clearSelections();
	}

	function onDrag(e) {
		var startXY = tracker.startXY;
		var xy = tracker.getXY();

		var x = Math.min(startXY[0], xy[0]);
		var y = Math.min(startXY[1], xy[1]);
		var w = Math.abs(startXY[0] - xy[0]);
		var h = Math.abs(startXY[1] - xy[1]);

		dragRegion.left = x;
		dragRegion.top = y;
		dragRegion.right = x + w;
		dragRegion.bottom = y + h;

		dragRegion.constrainTo(bodyRegion);
		proxy.setRegion(dragRegion);

		for (var i = 0, len = rs.length; i < len; i++) {
			var r = rs[i], sel = dragRegion.intersect(r);
			if (sel && !r.selected) {
				r.selected = true;
				view.select(i, true);
			} else if (!sel && r.selected) {
				r.selected = false;
				view.deselect(i);
			}
		}
	}

	function onEnd(e) {
		if (!Ext.isIE) {
			view.un('containerclick', cancelClick, view);
		}
		if (proxy) {
			proxy.setDisplayed(false);
		}
	}

	function onRender(view) {
		tracker = new Ext.dd.DragTracker({
					onBeforeStart : onBeforeStart,
					onStart : onStart,
					onDrag : onDrag,
					onEnd : onEnd
				});
		tracker.initEl(view.el);
	}
};
ImageDragZone = function(view, config) {
	this.view = view;
	ImageDragZone.superclass.constructor.call(this, this.view.getEl(), config);
};

Ext.extend(ImageDragZone, Ext.dd.DragZone, {
			getDragData : function(e) {
				var target = e.getTarget('.thumb-wrap');
				if (target) {
					var view = this.view;
					if (!view.isSelected(target)) {
						view.onClick(e);
					}
					var selNodes = view.getSelectedNodes();
					var dragData = {
						nodes : selNodes
					};
					if (selNodes.length == 1) {
						dragData.ddel = target.firstChild.firstChild; // the
						// img
						// element
						dragData.single = true;
					} else {
						var div = document.createElement('div'); // create
						// the multi
						// element
						// drag
						// "ghost"
						div.className = 'multi-proxy';
						for (var i = 0, len = selNodes.length; i < len; i++) {
							div.appendChild(selNodes[i].firstChild.firstChild
									.cloneNode(true));
							if ((i + 1) % 3 == 0) {
								div.appendChild(document.createElement('br'));
							}
						}
						dragData.ddel = div;
						dragData.multi = true;
					}
					return dragData;
				}
				return false;
			},

			afterRepair : function() {
				for (var i = 0, len = this.dragData.nodes.length; i < len; i++) {
					Ext.fly(this.dragData.nodes[i]).frame('#8db2e3', 1);
				}
				this.dragging = false;
			},

			// override the default repairXY with one offset for the margins and
			// padding
			getRepairXY : function(e) {
				if (!this.dragData.multi) {
					var xy = Ext.Element.fly(this.dragData.ddel).getXY();
					xy[0] += 3;
					xy[1] += 3;
					return xy;
				}
				return false;
			}
		});
