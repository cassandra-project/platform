/**
 * This plugin can enable a cell to cell drag and drop operation within the same grid view.
 *
 * Note that the plugin must be added to the grid view, not to the grid panel. For example, using {@link Ext.panel.Table viewConfig}:
 *
 *      viewConfig: {
 *          plugins: {
 *              ptype: 'celldragdrop',
 *
 *              // Remove text from source cell and replace with value of emptyText.
 *              applyEmptyText: true,
 *
 *              //emptyText: Ext.String.htmlEncode('<<foo>>'),
 *
 *              // Will only allow drops of the same type.
 *              enforceType: true
 *          }
 *      }
 */
Ext.define('Ext.ux.TreeToCellDragDrop', {
    extend: 'Ext.AbstractPlugin',
    alias: 'plugin.treetocelldragdrop',

    uses: ['Ext.view.DragZone'],
	
	/**
     * @cfg {String} dropField
     * Set to the field to be copied from node
     *
     * Defaults to `id`.
     */
    dropField: 'id',
	
	/**
     * @cfg {String} nodeTypeField
     * If enforceType is true the value of this field will be used to compare against column.columnType
     * and if not equal drop operation will fail
	 
     * Defaults to ``.
     */
    nodeTypeField: '',
	
	/**
     * @cfg {number} addToColumnIndex
     * Increase or decrease columnIndex value (needed most offen when plugins like row expander are used)
	 
     * Defaults to 0.
     */
    addToColumnIndex: 0,
	

    /**
     * @cfg {Boolean} enforceType
     * Set to `true` to only allow drops of the same type.
	 * note: nodeType and columnType must be set
     *
     * Defaults to `false`.
     */
    enforceType: false,


    /**
     * @cfg {Boolean} dropBackgroundColor
     * The default background color for when a drop is allowed.
     *
     * Defaults to green.
     */
    dropBackgroundColor: 'green',

    /**
     * @cfg {Boolean} noDropBackgroundColor
     * The default background color for when a drop is not allowed.
     *
     * Defaults to red.
     */
    noDropBackgroundColor: 'red',


    /**
     * @cfg {String} ddGroup
     * A named drag drop group to which this object belongs. If a group is specified, then both the DragZones and
     * DropZone used by this plugin will only interact with other drag drop objects in the same group.
     */
    ddGroup: "ddGlobal",

    /**
     * @cfg {Boolean} enableDrop
     * Set to `false` to disallow the View from accepting drop gestures.
     */
    enableDrop: true,


    /**
     * @cfg {Object/Boolean} containerScroll
     * True to register this container with the Scrollmanager for auto scrolling during drag operations.
     * A {@link Ext.dd.ScrollManager} configuration may also be passed.
     */
    containerScroll: false,

    init: function (view) {
        var me = this;

        view.on('render', me.onViewRender, me, {
            single: true
        });
    },

    destroy: function () {
        var me = this;

        Ext.destroy(me.dragZone, me.dropZone);
    },

    enable: function () {
        var me = this;

        if (me.dragZone) {
            me.dragZone.unlock();
        }
        if (me.dropZone) {
            me.dropZone.unlock();
        }
        me.callParent();
    },

    disable: function () {
        var me = this;

        if (me.dragZone) {
            me.dragZone.lock();
        }
        if (me.dropZone) {
            me.dropZone.lock();
        }
        me.callParent();
    },

    onViewRender: function (view) {
        var me = this,
            scrollEl;
		

        if (me.enableDrop) {
            me.dropZone = new Ext.dd.DropZone(view.el, {
                view: view,
                ddGroup: me.dropGroup || me.ddGroup,
                containerScroll: true,

                getTargetFromEvent: function (e) {
                    var self = this,
                        v = self.view,
                        cell = e.getTarget(v.cellSelector),
                        row, columnIndex;

                    // Ascertain whether the mousemove is within a grid cell.
                    if (cell) {
                        row = v.findItemByChild(cell);
                        columnIndex = cell.cellIndex + me.addToColumnIndex;

                        if (row && Ext.isDefined(columnIndex)) {
                            return {
                                node: cell,
                                record: v.getRecord(row),
                                columnName: self.view.up('grid').columns[columnIndex].dataIndex,
								columnType: self.view.up('grid').columns[columnIndex].columnType
                            };
                        }
                    }
                },

                // On Node enter, see if it is valid for us to drop the field on that type of column.
                onNodeEnter: function (target, dd, e, dragData) {
                    var self = this,
						destType = target.columnType ? target.columnType.toUpperCase() : '',
						sourceType = dragData.records[0].get(me.nodeTypeField) ? dragData.records[0].get(me.nodeTypeField).toUpperCase() : '';

                    delete self.dropOK;

                    // Return if no target node or if over the same cell as the source of the drag.
                    if (!target || target.node === dragData.item.parentNode) {
                        return;
                    }
					
					if (typeof(dragData.records[0].get(me.dropField)).toUpperCase() !== target.record.fields.get(target.columnName).type.type.toUpperCase()) {
						self.dropOK = false;
					}

                    // Check whether the data type of the column being dropped on accepts the
                    // dragged field type. If so, set dropOK flag, and highlight the target node.
                    if (me.enforceType && destType !== sourceType) {

                        self.dropOK = false;

                        if (me.noDropCls) {
                            Ext.fly(target.node).addCls(me.noDropCls);
                        } else {
                            Ext.fly(target.node).applyStyles({
                                backgroundColor: me.noDropBackgroundColor
                            });
                        }

                        return;
                    }

                    self.dropOK = true;

                    if (me.dropCls) {
                        Ext.fly(target.node).addCls(me.dropCls);
                    } else {
                        Ext.fly(target.node).applyStyles({
                            backgroundColor: me.dropBackgroundColor
                        });
                    }
                },

                // Return the class name to add to the drag proxy. This provides a visual indication
                // of drop allowed or not allowed.
                onNodeOver: function (target, dd, e, dragData) {
                    return this.dropOK ? this.dropAllowed : this.dropNotAllowed;
                },

                // Highlight the target node.
                onNodeOut: function (target, dd, e, dragData) {
                    var cls = this.dropOK ? me.dropCls : me.noDropCls;

                    if (cls) {
                        Ext.fly(target.node).removeCls(cls);
                    } else {
                        Ext.fly(target.node).applyStyles({
                            backgroundColor: ''
                        });
                    }
                },

                // Process the drop event if we have previously ascertained that a drop is OK.
                onNodeDrop: function (target, dd, e, dragData) {
                    if (this.dropOK) {
							target.record.set(target.columnName, dragData.records[0].get(me.dropField));
                        return true;
                    }
                },

                onCellDrop: Ext.emptyFn
            });
        }
    }
});